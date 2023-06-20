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

package org.eclipse.tractusx.productpass.models.manager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import utils.DateTimeUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class History {

    @JsonProperty("name")
    public String name;

    @JsonProperty("status")
    public String status;

    @JsonProperty("started")
    public Long started;

    @JsonProperty("updated")
    public Long updated;
    @JsonProperty("attempts")
    public Integer attempts;

    public History(String name, String status, Integer attempts) {
        this.name = name;
        this.status = status;
        this.started = DateTimeUtil.getTimestamp();
        this.updated = DateTimeUtil.getTimestamp();
        this.attempts = attempts;
    }

    public History(String name, String status, Long started) {
        this.name = name;
        this.status = status;
        this.started = started;
        this.updated = DateTimeUtil.getTimestamp();
    }

    public History(String name, String status, Long started, Integer attempts) {
        this.name = name;
        this.status = status;
        this.started = started;
        this.updated = DateTimeUtil.getTimestamp();
        this.attempts = attempts;
    }

    public History(String name, String status) {
        this.name = name;
        this.status = status;
        this.started = DateTimeUtil.getTimestamp();
        this.updated = DateTimeUtil.getTimestamp();
    }

    public History() {
    }

    public History(String name,String status, Long started, Long updated) {
        this.name = name;
        this.status = status;
        this.started = started;
        this.updated = updated;
    }

    public History(String name,String status, Long started, Long updated, Integer attempts) {
        this.name = name;
        this.status = status;
        this.started = started;
        this.updated = updated;
        this.attempts = attempts;
    }


    public Long getStarted() {
        return started;
    }

    public void setStarted(Long started) {
        this.started = started;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void addAttempt(){
        this.updated = DateTimeUtil.getTimestamp();
        if(this.attempts==null){
            this.attempts=0;
        }
        this.attempts++;
    }

    public void setAttempts(Integer attempts) {
        this.updated = DateTimeUtil.getTimestamp();
        this.attempts = attempts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updated = DateTimeUtil.getTimestamp();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
