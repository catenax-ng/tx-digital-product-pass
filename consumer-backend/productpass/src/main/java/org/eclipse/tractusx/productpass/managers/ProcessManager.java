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
import org.eclipse.tractusx.productpass.models.manager.History;
import org.eclipse.tractusx.productpass.models.manager.Process;
import org.eclipse.tractusx.productpass.models.manager.Status;
import org.eclipse.tractusx.productpass.models.negotiation.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.*;

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
            if(processDataModel == null){
                processDataModel = new ProcessDataModel();
                this.httpUtil.setSessionValue(httpRequest, "processDataModel", processDataModel);
                LogUtil.printMessage("[PROCESS] Process Data Model created, the server is ready to start processing requests...");
            }
            LogUtil.printMessage("DataModel: ["+jsonUtil.toJson(processDataModel.dataModel, true)+"]");
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


    public String generateToken(Process process, String contractId){
        return  CrypUtil.sha256("signToken=[" + process.getCreated() + "|" + process.id + "|" + contractId + "|" + processConfig.getSignToken()+"]"); // Add extra level of security, that just the user that has this token can sign
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

    public Process createProcess(HttpServletRequest httpRequest) {
        Process process = new Process(CrypUtil.getUUID(), "CREATED");
        this.setProcess(httpRequest, process);
        return process;
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
                statusFile = new Status(processId, history.getStatus(), DateTimeUtil.getTimestamp());
                statusFile.setHistory(historyId, history);
            } else {
                statusFile = (Status) jsonUtil.fromJsonFileToObject(path, Status.class);
                statusFile.setStatus(history.getStatus());
                statusFile.setModified(DateTimeUtil.getTimestamp());
                statusFile.setHistory(historyId, history);
            }

            return jsonUtil.toJsonFile(path, statusFile, processConfig.getIndent()); // Store the plain JSON
        } catch (Exception e) {
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to create/update the status file");
        }
    }

    public String setDecline(String processId){
        return this.setStatus(processId,"contract-decline", new History(
                processId,
                "DECLINED"
        ));
    }


    public String saveDataset(String processId, Dataset dataset, Long startedTime) {
        History history = new History(
                dataset.getId(),
                "AVAILABLE",
                startedTime
        ); // Set the history for the Dataset
        String id = "contract-dataset";
        this.setStatus(processId, id, history);
        String path = this.getProcessFilePath(processId, this.datasetFileName);
        String returnPath = jsonUtil.toJsonFile(path, dataset, processConfig.getIndent());
        if (returnPath == null) {
            history.setStatus("FAILED");
            this.setStatus(processId, dataset.getId(), history);
        }
        return returnPath;
    }

}
