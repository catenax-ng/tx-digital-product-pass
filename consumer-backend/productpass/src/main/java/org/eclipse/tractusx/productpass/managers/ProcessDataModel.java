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

import org.eclipse.tractusx.productpass.models.manager.Process;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessDataModel {

    public Map<String, Process> dataModel;

    public ProcessDataModel() {
        this.dataModel = new HashMap<>();
    }
    public ProcessDataModel addProcess(Process process){
        this.dataModel.put(process.id, process);
        return this;
    }

    public ProcessDataModel setState(String processId, String state){
        Process process = this.dataModel.getOrDefault(processId, null);
        if(process != null){
            process.state = state;
            this.dataModel.put(processId, process);
        }
        return this;
    }
    public ProcessDataModel startProcess(String processId, Thread thread){
        Process process = this.dataModel.getOrDefault(processId, null);
        if(process != null){
            process.state = "RUNNING";
            process.thread = thread;
            this.dataModel.put(processId, process);
        }
        return this;
    }

    public Process getProcess(String processId){
        return this.dataModel.getOrDefault(processId, null);
    }
    public Map<String, Process> getDataModel() {
        return dataModel;
    }

    public void setDataModel(Map<String, Process> dataModel) {
        this.dataModel = dataModel;
    }
}
