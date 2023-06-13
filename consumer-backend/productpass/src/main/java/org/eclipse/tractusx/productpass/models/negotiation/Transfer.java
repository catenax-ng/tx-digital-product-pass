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
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transfer extends DidDocument{
    @JsonProperty("edc:state")
    String state;
    @JsonProperty("edc:stateTimestamp")
    Long stateTimestamp;

    @JsonProperty("edc:type")
    String edcType;

    @JsonProperty("edc:callbackAddresses")
    List<JsonNode> callbackAddresses;

    @JsonProperty("edc:dataDestination")
    DataDestination dataDestination;


    @JsonProperty("edc:dataRequest")
    DataRequest dataRequest;

    @JsonProperty("edc:receiverHttpEndpoint")
    String receiverHttpEndpoint;

    @JsonProperty("@context")
    JsonNode context;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getStateTimestamp() {
        return stateTimestamp;
    }

    public void setStateTimestamp(Long stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    public String getEdcType() {
        return edcType;
    }

    public void setEdcType(String edcType) {
        this.edcType = edcType;
    }

    public List<JsonNode> getCallbackAddresses() {
        return callbackAddresses;
    }

    public void setCallbackAddresses(List<JsonNode> callbackAddresses) {
        this.callbackAddresses = callbackAddresses;
    }

    public DataDestination getDataDestination() {
        return dataDestination;
    }

    public void setDataDestination(DataDestination dataDestination) {
        this.dataDestination = dataDestination;
    }

    public DataRequest getDataRequest() {
        return dataRequest;
    }

    public void setDataRequest(DataRequest dataRequest) {
        this.dataRequest = dataRequest;
    }

    public String getReceiverHttpEndpoint() {
        return receiverHttpEndpoint;
    }

    public void setReceiverHttpEndpoint(String receiverHttpEndpoint) {
        this.receiverHttpEndpoint = receiverHttpEndpoint;
    }

    public JsonNode getContext() {
        return context;
    }

    public void setContext(JsonNode context) {
        this.context = context;
    }


    static class DataRequest extends DidDocument{
        @JsonProperty("edc:assetId")
        String assetId;
        @JsonProperty("edc:contractId")
        String contractId;

        public String getAssetId() {
            return assetId;
        }

        public void setAssetId(String assetId) {
            this.assetId = assetId;
        }

        public String getContractId() {
            return contractId;
        }

        public void setContractId(String contractId) {
            this.contractId = contractId;
        }
    }

    static class DataDestination {
        @JsonProperty("edc:type")
        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

