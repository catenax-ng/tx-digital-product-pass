/*********************************************************************************
 *
 * Catena-X - Product Passport Consumer Backend
 *
 * Copyright (c) 2022, 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
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

package org.eclipse.tractusx.productpass.listeners;

import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.tractusx.productpass.managers.ProcessDataModel;
import org.eclipse.tractusx.productpass.managers.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utils.HttpUtil;
import utils.LogUtil;

@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AppListener {
    @Autowired
    BuildProperties buildProperties;
    @Autowired
    Environment env;
    @Autowired
    HttpUtil httpUtil;


    @EventListener(ApplicationReadyEvent.class)
    public void onStartUp() {
        String serverStartUpMessage = "\n\n" +
                "************************************************\n" +
                buildProperties.getName()+"\n" +
                "Copyright (c) 2022, 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA\n" +
                "Copyright (c) 2022, 2023: Contributors to the CatenaX (ng) GitHub Organisation.\n" +
                "Version: "+ buildProperties.getVersion()  + "\n\n" +
                 "\n\n-------------> [ SERVER STARTED ] <-------------\n" +
                "Listening to requests...\n\n";

        LogUtil.printMessage(serverStartUpMessage);
        LogUtil.printMessage("[ LOGGING STARTED ] <-----------------------------------------");
        LogUtil.printMessage("Creating log file...");
        try{
        RequestAttributes requestAttributes = RequestContextHolder
                .currentRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest httpRequest = attributes.getRequest();
        if(httpRequest != null){
            httpUtil.setSessionValue(httpRequest, "processDataModel", new ProcessDataModel());
            LogUtil.printMessage("[PROCESS] Ready to start processing requests... ");
        }else{
            LogUtil.printError("[PROCESS] Failed to start process data model... ");
        }}catch (Exception e){
            LogUtil.printException(e, "[PROCESS] Failed to start process data model due to exception... ");
        }

        // Store the process manager in memory
       }

}
