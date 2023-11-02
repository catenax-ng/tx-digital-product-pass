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

package org.eclipse.tractusx.productpass.exceptions;

import utils.LogUtil;

/**
 * This class consists exclusively to define methods to handle and log exceptions caused in the Manager classes.
 **/
public class ManagerException extends RuntimeException{

    /**
     * Logs the given manager name and error message.
     * <p>
     * @param   managerName
     *          the {@code String} manager name (e.g: the class name where the exception occurred).
     * @param   errorMessage
     *          the {@code String} error message.
     *
     */
    public ManagerException(String managerName, String errorMessage) {
        super("["+managerName+"] " + errorMessage);
        LogUtil.printException(this, "["+managerName+"] " + errorMessage);
    }

    /**
     * Logs the given manager name, the {@code Exception} object and the error message.
     * <p>
     * @param   managerName
     *          the {@code String} manager name (e.g: the class name where the exception occurred).
     * @param   e
     *          the {@code Exception} object thrown.
     * @param   errorMessage
     *          the {@code String} error message.
     *
     */
    public ManagerException(String managerName, Exception e, String errorMessage) {
        super("["+managerName+"] " + errorMessage+", "+e.getMessage());
        LogUtil.printException(this, "["+managerName+"] " + errorMessage);
    }

}