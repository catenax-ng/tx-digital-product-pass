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

package org.eclipse.tractusx.productpass.http.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.eclipse.tractusx.productpass.config.PassportConfig;
import org.eclipse.tractusx.productpass.exceptions.ControllerException;
import org.eclipse.tractusx.productpass.models.dtregistry.DigitalTwin;
import org.eclipse.tractusx.productpass.models.dtregistry.SubModel;
import org.eclipse.tractusx.productpass.models.http.Response;
import org.eclipse.tractusx.productpass.models.http.requests.Search;
import org.eclipse.tractusx.productpass.models.negotiation.Catalog;
import org.eclipse.tractusx.productpass.models.negotiation.Dataset;
import org.eclipse.tractusx.productpass.models.negotiation.Offer;
import org.eclipse.tractusx.productpass.models.passports.Passport;
import org.eclipse.tractusx.productpass.models.passports.PassportV3;
import org.eclipse.tractusx.productpass.services.AasService;
import org.eclipse.tractusx.productpass.services.AuthenticationService;
import org.eclipse.tractusx.productpass.services.DataTransferService;
import org.eclipse.tractusx.productpass.services.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/data")
@Tag(name = "Data Controller")
@SecurityRequirement(name = "BearerAuthentication")
public class DataController {
    private @Autowired HttpServletRequest httpRequest;
    private @Autowired HttpServletResponse httpResponse;
    private @Autowired DataTransferService dataService;
    private @Autowired VaultService vaultService;
    private @Autowired AasService aasService;
    private @Autowired AuthenticationService authService;
    private @Autowired PassportConfig passportConfig;
    private @Autowired Environment env;
    @Autowired
    HttpUtil httpUtil;
    private @Autowired JsonUtil jsonUtil;
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @Operation(summary = "Searches for a passport with the following id", responses = {
            @ApiResponse(description = "Default Response Structure", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Content of Data Field in Response", responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Dataset.class)))
    })
    public Response search(@Valid @RequestBody Search searchBody) {
        Response response = httpUtil.getResponse();
        response.status = 500;
        response.statusText = "Internal Server Error";
        if(!authService.isAuthenticated(httpRequest)){
            response = httpUtil.getNotAuthorizedResponse();
            return httpUtil.buildResponse(response, httpResponse);
        }
        try{
            Boolean storeProcess = env.getProperty("configuration.edc.store", Boolean.class,true);
            List<String> mandatoryParams = List.of("id", "version");
            if(!jsonUtil.checkJsonKeys(searchBody, mandatoryParams, ".")){
                response = httpUtil.getBadRequest("One or all the mandatory parameters "+mandatoryParams+" are missing");
                return httpUtil.buildResponse(response, httpResponse);
            };

            List<String> versions = passportConfig.getVersions();
            // Initialize variables
            // Check if version is available
            if (!versions.contains(searchBody.getVersion())) {
                return httpUtil.buildResponse(httpUtil.getForbiddenResponse("This passport version is not available at the moment!"), httpResponse);
            }

            // Start Digital Twin Query
            AasService.DigitalTwinRegistryQueryById digitalTwinRegistry = aasService.new DigitalTwinRegistryQueryById(searchBody);
            Thread digitalTwinRegistryThread = ThreadUtil.runThread(digitalTwinRegistry);

            // Wait for digital twin query
            digitalTwinRegistryThread.join();
            DigitalTwin digitalTwin;
            SubModel subModel;
            String connectorId;
            String connectorAddress;
            try {
                digitalTwin = digitalTwinRegistry.getDigitalTwin();
                subModel = digitalTwinRegistry.getSubModel();
                connectorId = subModel.getIdShort();
                // Get first connectorAddress, a possibility is to check for "EDC" type
                connectorAddress = subModel.getEndpoints().get(0).getProtocolInformation().getEndpointAddress();

            } catch (Exception e) {
                response.message = "Failed to get the submodel from the digital twin registry!";
                response.status = 404;
                response.statusText = "Not Found";
                return httpUtil.buildResponse(response, httpResponse);
            }
            if (connectorId.isEmpty() || connectorAddress.isEmpty()) {
                response.message = "Failed to get connectorId and connectorAddress!";
                response.status = 400;
                response.statusText = "Bad Request";
                response.data = subModel;
                return httpUtil.buildResponse(response, httpResponse);
            }


            try {
                connectorAddress = CatenaXUtil.buildEndpoint(connectorAddress);
            }catch (Exception e) {
                response.message = "Failed to build endpoint url to ["+connectorAddress+"]!";
                response.status = 422;
                response.statusText = "Unprocessable Content";
                return httpUtil.buildResponse(response, httpResponse);
            }
            if (connectorAddress.isEmpty()) {
                response.message = "Failed to parse endpoint ["+connectorAddress+"]!";
                response.status = 422;
                response.statusText = "Unprocessable Content";
                response.data = subModel;
                return httpUtil.buildResponse(response, httpResponse);
            }

            String assetId = String.join("-",digitalTwin.getIdentification(), subModel.getIdentification());

            /*[1]=========================================*/
            // Get catalog with all the contract offers

            LogUtil.printMessage(connectorAddress);
            LogUtil.printMessage(assetId);
            Dataset dataset = null;
            try {
                dataset = dataService.getContractOfferByAssetId(assetId, connectorAddress);
            } catch (ControllerException e) {
                LogUtil.printException(e, "Exception on edc");
                response.message = "The EDC is not reachable, it was not possible to retrieve catalog!";
                response.status = 502;
                response.statusText = "Bad Gateway";
                return httpUtil.buildResponse(response, httpResponse);
            }

            // Check if contract offer was not received
            if (dataset == null) {
                response.message = "Asset Id not found in any contract!";
                response.status = 404;
                response.statusText = "Not Found";
                return httpUtil.buildResponse(response, httpResponse);
            }

            String processId = CrypUtil.getUUID();
            response = null;
            response = httpUtil.getResponse();

            response.data = Map.of(
                    "id", processId,
                    "dataset", dataset
            );

            return httpUtil.buildResponse(response, httpResponse);
    } catch (InterruptedException e) {
        // Restore interrupted state...
        Thread.currentThread().interrupt();
        response.message = e.getMessage();
        return httpUtil.buildResponse(response, httpResponse);
    } catch (Exception e) {
        response.message = e.getMessage();
        return httpUtil.buildResponse(response, httpResponse);
    }



    }

    @RequestMapping(value = "/passport/{transferId}", method = {RequestMethod.GET})
    @Operation(summary = "Returns product passport by transfer process Id", responses = {
            @ApiResponse(description = "Default Response Structure", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Response.class))),
            @ApiResponse(description = "Content of Data Field in Response", responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PassportV3.class)))
    })
    public Response getPassport(@PathVariable("transferId") String transferId, @RequestParam(value="version", required = false, defaultValue = "v3.0.1") String version) {
        // Check if user is Authenticated
        if(!authService.isAuthenticated(httpRequest)){
            Response response = httpUtil.getNotAuthorizedResponse();
            return httpUtil.buildResponse(response, httpResponse);
        }
        Response response = httpUtil.getResponse();
        Passport passport = null;
        if(version.equals("v3.0.1")) { // Currently supporting just version v3
            passport = dataService.getPassportV3(transferId,  env.getProperty("configuration.passport.dataTransfer.endpoint"));
        }else{
            response.message = "Version is not available!";
            response.status = 400;
            response.statusText = "Bad Request";
            response.data = null;
            return httpUtil.buildResponse(response, httpResponse);
        }
        if (passport == null) {
            response.message = "Passport for transfer [" + transferId + "] not found!";
            response.status = 404;
            response.statusText = "Not Found";
            response.data = null;
            return httpUtil.buildResponse(response, httpResponse);
        }
        response.data = passport;
        LogUtil.printMessage("Passport for transfer [" + transferId + "] retrieved successfully!");
        return httpUtil.buildResponse(response, httpResponse);
    }

}
