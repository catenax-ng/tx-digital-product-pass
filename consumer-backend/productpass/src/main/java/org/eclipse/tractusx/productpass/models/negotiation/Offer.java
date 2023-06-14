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

package org.eclipse.tractusx.productpass.models.negotiation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer extends Dataset {

    @JsonProperty("offerId")
    String offerId;

    @JsonProperty("assetId")
    String assetId;

    public Offer(String id, String type, Set policy, List<Distribution> distributions, String assetDescription, String assetId, String offerId, String assetId1) {
        super(id, type, policy, distributions, assetDescription, assetId);
        this.offerId = offerId;
        this.assetId = assetId1;
    }

    public Offer(String id, String type, String offerId, String assetId) {
        super(id, type);
        this.offerId = offerId;
        this.assetId = assetId;
    }

    public Offer(String id, String type, Set policy, List<Distribution> distributions, String assetDescription, String assetId) {
        super(id, type, policy, distributions, assetDescription, assetId);
    }

    public Offer(String id, String type) {
        super(id, type);
    }

    public void open(){
        this.offerId = this.policy.id;
        this.assetId = this.policy.target;
    }
    public void close(){
        this.offerId = null;
        this.assetId = null;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
