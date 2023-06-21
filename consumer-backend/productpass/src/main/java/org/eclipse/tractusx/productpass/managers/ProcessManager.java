/*********************************************************************************
 *
 * Catena-X - Product Passport Consumer Backend
 *
 * Copyright (c) 2022, 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 * Copyright (c) 2022, 2023 Contributors to the CatenaX (ng) GitHub Organisation.
 *
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the
 * License for the specific language govern in permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.productpass.managers;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.eclipse.tractusx.productpass.config.ProcessConfig;
import org.eclipse.tractusx.productpass.exceptions.ManagerException;
import org.eclipse.tractusx.productpass.models.dtregistry.DigitalTwin;
import org.eclipse.tractusx.productpass.models.manager.History;
import org.eclipse.tractusx.productpass.models.manager.Process;
import org.eclipse.tractusx.productpass.models.manager.Status;
import org.eclipse.tractusx.productpass.models.negotiation.Dataset;
import org.eclipse.tractusx.productpass.models.negotiation.Negotiation;
import org.eclipse.tractusx.productpass.models.negotiation.NegotiationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.*;

import javax.xml.crypto.Data;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessManager {

    private HttpUtil httpUtil;
    private JsonUtil jsonUtil;

    private FileUtil fileUtil;
    private @Autowired ProcessConfig processConfig;

    private final String metaFileName = "meta";
    private final String datasetFileName = "dataset";
    private final String negotiationFileName = "negotiation";
    private final String transferFileName = "transfer";

    private final String processDataModelName = "processDataModel";

    private final String digitalTwinFileName = "digitalTwin";


    @Autowired
    public ProcessManager(HttpUtil httpUtil, JsonUtil jsonUtil, FileUtil fileUtil, ProcessConfig processConfig) {
        this.httpUtil = httpUtil;
        this.jsonUtil = jsonUtil;
        this.fileUtil = fileUtil;
        this.processConfig = processConfig;
    }

    public ProcessDataModel loadDataModel(HttpServletRequest httpRequest) {
        try {
            ProcessDataModel processDataModel = (ProcessDataModel) httpUtil.getSessionValue(httpRequest, this.processDataModelName);
            if (processDataModel == null) {
                processDataModel = new ProcessDataModel();
                this.httpUtil.setSessionValue(httpRequest, "processDataModel", processDataModel);
                LogUtil.printMessage("[PROCESS] Process Data Model created, the server is ready to start processing requests...");
            }
            return processDataModel;
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "Failed to load Process DataModel!");
        }
    }

    public void saveDataModel(HttpServletRequest httpRequest, ProcessDataModel dataModel) {
        try {
            httpUtil.setSessionValue(httpRequest, this.processDataModelName, dataModel);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "Failed to save Process DataModel!");
        }
    }

    public Process getProcess(HttpServletRequest httpRequest, String processId) {
        try {
            // Getting a process
            ProcessDataModel dataModel = this.loadDataModel(httpRequest);
            return dataModel.getProcess(processId);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to get process [" + processId + "]");
        }
    }


    public String generateToken(Process process, String contractId) {
        return CrypUtil.sha256("signToken=[" + process.getCreated() + "|" + process.id + "|" + contractId + "|" + processConfig.getSignToken() + "]"); // Add extra level of security, that just the user that has this token can sign
    }

    public Boolean checkProcess(HttpServletRequest httpRequest, String processId) {
        try {
            // Getting a process
            ProcessDataModel dataModel = this.loadDataModel(httpRequest);
            return dataModel.processExists(processId);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to check if process exists [" + processId + "]");
        }
    }


    public NegotiationRequest startNegotiation(HttpServletRequest httpRequest, String processId) {
        try {
            Dataset dataset = this.loadDataset(processId);
            if (dataset == null) {
                throw new ManagerException(this.getClass().getName(), "Something when wrong when loading the dataset [" + processId + "]");
            }

            return new NegotiationRequest();

        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to start negotiation for [" + processId + "]");
        }


    }

    public void setProcess(HttpServletRequest httpRequest, Process process) {
        try { // Setting and updating a process
            ProcessDataModel dataModel = this.loadDataModel(httpRequest);
            this.saveDataModel(httpRequest, dataModel.addProcess(process));
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to set process [" + process.id + "]");
        }
    }

    public void setProcessState(HttpServletRequest httpRequest, String processId, String processState) {
        try { // Setting and updating a process state
            ProcessDataModel dataModel = (ProcessDataModel) httpUtil.getSessionValue(httpRequest, this.processDataModelName);
            this.saveDataModel(httpRequest, dataModel.setState(processId, processState));
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to set process state [" + processState + "] for process [" + processId + "]");
        }
    }

    private String getProcessDir(String processId, Boolean absolute) {
        String dataDir = fileUtil.getDataDir();
        if (absolute) {
            return Path.of(dataDir, processConfig.getDir(), processId).toAbsolutePath().toString();
        } else {
            return Path.of(dataDir, processConfig.getDir(), processId).toString();
        }
    }

    public Process createProcess(HttpServletRequest httpRequest, String connectorAddress) {
        Process process = new Process(CrypUtil.getUUID(), "CREATED");
        LogUtil.printMessage("Process Created [" + process.id + "], waiting for user to sign or decline...");
        this.setProcess(httpRequest, process); // Add process to session storage
        this.newStatusFile(process.id, connectorAddress); // Set the status from the process in file system logs.
        return process;
    }

    public String newStatusFile(String processId, String connectorAddress){
        try {
            String path = this.getProcessFilePath(processId, this.metaFileName);
            return jsonUtil.toJsonFile(
                    path,
                    new Status(
                        processId,
                        "CREATED",
                        connectorAddress,
                        DateTimeUtil.getTimestamp()
                    ),
                    processConfig.getIndent()); // Store the plain JSON
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to create the status file");
        }
    }

    public String getProcessFilePath(String processId, String filename) {
        String processDir = this.getProcessDir(processId, false);
        return Path.of(processDir, filename + ".json").toAbsolutePath().toString();
    }

    public Status getStatus(String processId) {
        try {
            String path = this.getProcessFilePath(processId, this.metaFileName);
            return (Status) jsonUtil.fromJsonFileToObject(path, Status.class);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to get the status file");
        }
    }

    public String setStatus(String processId, String historyId, History history) {
        try {
            String path = this.getProcessFilePath(processId, this.metaFileName);
            Status statusFile = null;
            if (!fileUtil.pathExists(path)) {
                throw new ManagerException(this.getClass().getName(), "Process file does not exists for id ["+processId+"]!");
            }

            statusFile = (Status) jsonUtil.fromJsonFileToObject(path, Status.class);
            statusFile.setStatus(history.getStatus());
            statusFile.setModified(DateTimeUtil.getTimestamp());
            statusFile.setHistory(historyId, history);
            return jsonUtil.toJsonFile(path, statusFile, processConfig.getIndent()); // Store the plain JSON
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to create/update the status file");
        }
    }
    public String setStatus(String processId, String status) {
        try {
            String path = this.getProcessFilePath(processId, this.metaFileName);
            Status statusFile = null;
            if (!fileUtil.pathExists(path)) {
                throw new ManagerException(this.getClass().getName(), "Process file does not exists for id ["+processId+"]!");
            }
            statusFile = (Status) jsonUtil.fromJsonFileToObject(path, Status.class);
            statusFile.setStatus(status);
            statusFile.setModified(DateTimeUtil.getTimestamp());
            return jsonUtil.toJsonFile(path, statusFile, processConfig.getIndent()); // Store the plain JSON
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to create/update the status file");
        }
    }


    public String setDecline(HttpServletRequest httpRequest, String processId) {

        this.setProcessState(httpRequest, processId, "ABORTED");

        return this.setStatus(processId, "contract-decline", new History(
                processId,
                "DECLINED"
        ));
    }

    public String setSigned(HttpServletRequest httpRequest, String processId, String contractId, Long signedAt) {

        this.setProcessState(httpRequest, processId, "STARTING");

        return this.setStatus(processId, "contract-signed", new History(
                contractId,
                "SIGNED",
                signedAt
        ));
    }

    public Dataset loadDataset(String processId) {
        try {
            String path = this.getProcessFilePath(processId, this.datasetFileName);
            return (Dataset) jsonUtil.fromJsonFileToObject(path, Dataset.class);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to load the dataset for process id [" + processId + "]");
        }
    }

    public String saveProcessPayload(String processId, Object payload, String fileName, Long startedTime, String assetId, String status, String eventKey) {
        try {
            // Define history
            History history = new History(
                    assetId,
                    status,
                    startedTime
            );
            // Set status
            this.setStatus(processId, eventKey, history);
            String path = this.getProcessFilePath(processId, fileName);
            String returnPath = jsonUtil.toJsonFile(path, payload, processConfig.getIndent());
            if (returnPath == null) {
                history.setStatus("FAILED");
                this.setStatus(processId, assetId, history);
            }
            return returnPath;
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to save the payload [" + assetId + "] with eventKey [" + eventKey + "]!");
        }
    }

    public String saveProcessPayload(String processId, Object payload, String fileName, String assetId, String status, String eventKey) {
        try {
            return this.saveProcessPayload(processId, payload, fileName, DateTimeUtil.getTimestamp(), assetId, status, eventKey);
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "Failed to save payload!");
        }
    }

    public String saveDigitalTwin(String processId, DigitalTwin digitalTwin, Long startedTime) {
        try {
            return this.saveProcessPayload(
                    processId,
                    digitalTwin,
                    this.digitalTwinFileName,
                    startedTime,
                    digitalTwin.getIdentification(),
                    "READY",
                    "digital-twin-request");
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to save the digitalTwin!");
        }
    }

    public String saveDataset(String processId, Dataset dataset, Long startedTime) {
        try {
            return this.saveProcessPayload(
                    processId,
                    dataset,
                    this.datasetFileName,
                    startedTime,
                    dataset.getId(),
                    "AVAILABLE",
                    "contract-dataset");
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to save the dataset!");
        }
    }

}
