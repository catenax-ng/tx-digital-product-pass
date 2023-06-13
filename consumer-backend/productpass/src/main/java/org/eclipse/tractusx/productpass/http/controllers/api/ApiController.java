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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.eclipse.tractusx.productpass.config.PassportConfig;
import org.eclipse.tractusx.productpass.exceptions.ControllerException;
import org.eclipse.tractusx.productpass.models.dtregistry.DigitalTwin;
import org.eclipse.tractusx.productpass.models.dtregistry.SubModel;
import org.eclipse.tractusx.productpass.models.http.Response;
import org.eclipse.tractusx.productpass.models.negotiation.*;
import org.eclipse.tractusx.productpass.models.passports.Passport;
import org.eclipse.tractusx.productpass.models.passports.PassportResponse;
import org.eclipse.tractusx.productpass.models.passports.PassportV3;
import org.eclipse.tractusx.productpass.services.AasService;
import org.eclipse.tractusx.productpass.services.AuthenticationService;
import org.eclipse.tractusx.productpass.services.DataTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "API Controller")
@SecurityRequirement(name = "BearerAuthentication")
public class ApiController {
    private @Autowired HttpServletRequest httpRequest;
    private @Autowired HttpServletResponse httpResponse;
    private @Autowired DataTransferService dataService;
    private @Autowired AasService aasService;
    private @Autowired DataController dataController;
    private @Autowired Environment env;
    private @Autowired AuthenticationService authService;
    private @Autowired PassportConfig passportConfig;
    private @Autowired HttpUtil httpUtil;

    public Dataset getContractOfferByAssetId(String assetId, String providerUrl) throws ControllerException {
        /*
         *   This method receives the assetId and looks up for targets with the same name.
         */
        try {
            Catalog catalog = dataService.getContractOfferCatalog(providerUrl);
            Map<String, Integer> offers = catalog.loadContractOffersMapByAssetId();
            if (!offers.containsKey(assetId)) {
                return null;
            }
            Integer index = offers.get(assetId);
            return catalog.getContractOffers().get(index);
        } catch (Exception e) {
            throw new ControllerException(this.getClass().getName(), e, "It was not possible to get Contract Offer for assetId [" + assetId + "]");
        }
    }
    @RequestMapping(value="/api/*", method = RequestMethod.GET)
    @Hidden         // hide this endpoint from api documentation - swagger-ui
    Response index() throws Exception{
        httpUtil.redirect(httpResponse,"/passport");
        return httpUtil.getResponse("Redirect to UI");
    }

    /**
     * @param id Asset id that identifies the object that has a passport
     * @param idType  Type of asset id, the name of the code in the digital twin registry
     *                Default: "Battery_ID_DMC_Code"
     * @param dtIndex Index from the asset in the digital twin registry
     *                Default: 0
     * @param idShort Id from subModel
     *                Default: 0
     * @return PassportV3
     */
    @RequestMapping(value = "/passport/{version}/{id}", method = {RequestMethod.GET})
    @Operation(summary = "Returns versioned product passport by id", responses = {
        @ApiResponse(description = "Default Response Structure", content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Response.class))),
        @ApiResponse(description = "Content of Data Field in Response", responseCode = "200", content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PassportResponse.class))),
        @ApiResponse(description = "Content of Passport Field in Data Field",useReturnTypeSchema = true, content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PassportV3.class)))
    })
    public Response getPassport(
            @PathVariable("id") String id,
            @PathVariable("version") String version,
            @RequestParam(value = "idType", required = false, defaultValue = "partInstanceId") String idType,
            @RequestParam(value = "idShort", required = false, defaultValue = "batteryPass") String idShort,
            @RequestParam(value = "dtIndex", required = false, defaultValue = "0") Integer dtIndex
    ) {
        // Check if user is Authenticated
        if(!authService.isAuthenticated(httpRequest)){
            Response response = httpUtil.getNotAuthorizedResponse();
            return httpUtil.buildResponse(response, httpResponse);
        }
        // Initialize response
        Response response = httpUtil.getResponse();
        List<String> versions = passportConfig.getVersions();
        try {
            // Configure digital twin registry query and params
            AasService.DigitalTwinRegistryQueryById digitalTwinRegistry = aasService.new DigitalTwinRegistryQueryById(id, idType, dtIndex, idShort);
            Thread digitalTwinRegistryThread = ThreadUtil.runThread(digitalTwinRegistry);

            // Initialize variables
            Offer contractOffer = null;
            // Check if version is available
            if (!versions.contains(version)) {
                response.message = "This passport version is not available at the moment!";
                response.status = 403;
                response.statusText = "Forbidden";
                return httpUtil.buildResponse(response, httpResponse);
            }

            // Wait for thread to close and give a response
            digitalTwinRegistryThread.join();
            DigitalTwin digitalTwin;
            SubModel subModel;
            String connectorId;
            String connectorAddress;
            try {
                digitalTwin = digitalTwinRegistry.getDigitalTwin();
                subModel = digitalTwinRegistry.getSubModel();
                connectorId = subModel.getIdShort();
                // Get first connectorAddress, a posibility is to check for "EDC" type
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
            try {
                contractOffer = this.getContractOfferByAssetId(assetId, connectorAddress);
            } catch (ControllerException e) {
                response.message = "The EDC is not reachable, it was not possible to retrieve catalog!";
                response.status = 502;
                response.statusText = "Bad Gateway";
                return httpUtil.buildResponse(response, httpResponse);
            }

            // Check if contract offer was not received
            if (contractOffer == null) {
                response.message = "Asset Id not found in any contract!";
                response.status = 404;
                response.statusText = "Not Found";
                return httpUtil.buildResponse(response, httpResponse);
            }


            /*[2]=========================================*/
            // Start Negotiation
            Negotiation negotiation;
            try {
                negotiation = dataService.doContractNegotiations(contractOffer, connectorAddress);
            } catch (Exception e) {
                response.message = "Negotiation Id not received, something went wrong" + " [" + e.getMessage() + "]";
                response.status = 400;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }

            if (negotiation.getId() == null) {
                response.message = "Negotiation Id not received, something went wrong";
                response.status = 400;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }

            /*[3]=========================================*/
            // Check for negotiation status
            try {
                negotiation = dataService.getNegotiation(negotiation.getId());
            } catch (Exception e) {
                response.message = "The negotiation for asset id failed!" + " [" + e.getMessage() + "]";
                response.status = 400;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }
            if (negotiation.getState().equals("ERROR")) {
                response.message = "The negotiation for asset id failed!";
                response.status = 400;
                response.data = negotiation;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }


            /*[6]=========================================*/
            // Configure Transfer Request
            TransferRequest transferRequest = new TransferRequest(
                    DataTransferService.generateTransferId(negotiation, connectorId, connectorAddress),
                    connectorId,
                    connectorAddress,
                    negotiation.getContractAgreementId(),
                    assetId,
                    false,
                    "HttpProxy"
            );
            /*[7]=========================================*/
            // Initiate the transfer process
            Transfer transfer = null;
            try {
                transfer = dataService.initiateTransfer(transferRequest);
            } catch (Exception e) {
                response.message = "It was not posible to initiate the transfer process!";
                response.status = 500;
                response.statusText = "Internal Server Error";
                return httpUtil.buildResponse(response, httpResponse);
            }
            if (transfer.getId() == null) {
                response.message = "Transfer Id not received, something went wrong";
                response.status = 400;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }


            /*[8]=========================================*/
            // Check for transfer updates and the status
            try {
                transfer = dataService.getTransfer(transfer.getId());
            } catch (Exception e) {
                response.message = "It was not possible to retrieve the transfer!";
                response.status = 500;
                response.statusText = "Internal Server Error";
                return httpUtil.buildResponse(response, httpResponse);
            }

            // If error return transfer message
            if (transfer.getState().equals("ERROR")) {
                response.data = transfer;
                response.message = "The transfer process failed!";
                response.status = 400;
                response.statusText = "Bad Request";
                return httpUtil.buildResponse(response, httpResponse);
            }

            /*[9]=========================================*/
            // Get passport by versions

            int actualRetries = 1;
            Integer maxRetries = env.getProperty("configuration.maxRetries", Integer.class,5);
            while (actualRetries <= maxRetries) {
                try {
                    response = dataController.getPassport(transferRequest.getId(), version);
                } catch (Exception e) {
                    LogUtil.printError("[" + transferRequest.getId() + "] Waiting 5 seconds and retrying #"+actualRetries+" of "+maxRetries+"... ");
                    Thread.sleep(5000);
                }
                if(response.data!=null){
                    break;
                }
                actualRetries++;
            }

            // Correct Response
            if(response.data != null) {
                Passport passport = (Passport) response.data;
                Map<String, Object> metadata = Map.of(
                        "contractOffer", contractOffer,
                        "negotiation", negotiation,
                        "transferRequest", transferRequest,
                        "transfer", transfer
                );
                response.data = new PassportResponse(metadata, passport);
                return httpUtil.buildResponse(response, httpResponse);
            }

            // Error or Exception response
            if(response.message == null){
                response.message = "Passport for transfer [" + transferRequest.getId() + "] not found in provider!";
                response.status = 404;
                response.statusText = "Not Found";
                LogUtil.printError("["+response.status+" Not Found]: "+response.message);
            }
            return httpUtil.buildResponse(response, httpResponse);

        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            response.message = e.getMessage();
            response.status = 500;
            response.statusText = "Internal Server Error";
            return httpUtil.buildResponse(response, httpResponse);
        } catch (Exception e) {
            response.message = e.getMessage();
            response.status = 500;
            response.statusText = "Internal Server Error";
            return httpUtil.buildResponse(response, httpResponse);
        }

    }

}
