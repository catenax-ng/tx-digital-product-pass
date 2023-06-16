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

import org.apache.juli.logging.Log;
import org.eclipse.tractusx.productpass.config.ProcessConfig;
import org.eclipse.tractusx.productpass.exceptions.ManagerException;
import org.eclipse.tractusx.productpass.models.negotiation.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.*;

import java.nio.file.Path;
import java.util.Map;

@Component
public class ProcessManager {

    @Autowired
    HttpUtil httpUtil;

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    FileUtil fileUtil;
    private @Autowired ProcessConfig processConfig;

    private final String metaFileName = "meta";
    private final String datasetFileName = "dataset";

    private String getProcessDir(String processId, Boolean absolute){
        String dataDir = fileUtil.getDataDir();
        if(absolute) {
            return Path.of(dataDir, processConfig.getDir(), processId).toAbsolutePath().toString();
        }else{
            return Path.of(dataDir, processConfig.getDir(), processId).toString();
        }
    }
    public String setStatus(String processId, String status){
        try {
            String processDir = this.getProcessDir(processId, false);
            String path = Path.of(processDir, metaFileName + ".json").toAbsolutePath().toString();
            Map<String, Object> statusFile = null;
            if (!fileUtil.pathExists(path)) {
                statusFile = Map.of("id", processId, "status", status, "started", DateTimeUtil.getTimestamp(), "updated", DateTimeUtil.getTimestamp());
            } else {
                statusFile = (Map<String, Object>) jsonUtil.fromJsonFile(path);
                statusFile.put("status", status);
                statusFile.put("updated", DateTimeUtil.getTimestamp());
            }
            return jsonUtil.toJsonFile(path, statusFile, processConfig.getIndent()); // Store the plain JSON
        }catch (Exception e){
            throw new ManagerException(this.getClass().getName(), e, "It was not possible to create/update the status file");
        }
    }
    public String saveDataset(String processId, Dataset dataset){
        this.setStatus(processId, "CREATING");
        String processDir = this.getProcessDir(processId, true);
        String path = Path.of(processDir, datasetFileName + ".json").toAbsolutePath().toString();
        String returnPath = jsonUtil.toJsonFile(path, dataset, processConfig.getIndent()); // Store the plain JSON
        if(returnPath != null) {
            this.setStatus(processId, "CREATED");
        }else{
            this.setStatus(processId, "FAILED");
        }
        return returnPath;
    }

}
