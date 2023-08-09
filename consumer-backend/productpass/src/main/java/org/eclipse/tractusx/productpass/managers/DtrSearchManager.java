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

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.tractusx.productpass.config.DtrConfig;
import org.eclipse.tractusx.productpass.exceptions.DataModelException;
import org.eclipse.tractusx.productpass.models.catenax.Dtr;
import org.eclipse.tractusx.productpass.models.catenax.DtrCache;
import org.eclipse.tractusx.productpass.models.catenax.EdcDiscoveryEndpoint;
import org.eclipse.tractusx.productpass.models.http.responses.IdResponse;
import org.eclipse.tractusx.productpass.models.negotiation.Catalog;
import org.eclipse.tractusx.productpass.models.negotiation.Dataset;
import org.eclipse.tractusx.productpass.models.negotiation.Negotiation;
import org.eclipse.tractusx.productpass.models.negotiation.Offer;
import org.eclipse.tractusx.productpass.services.DataTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DtrSearchManager {
    private DataTransferService dataTransferService;
    private DtrConfig dtrConfig;
    private ProcessManager processManager;
    private FileUtil fileUtil;
    private JsonUtil jsonUtil;
    private DtrCache dtrCache;
    private ConcurrentHashMap<String, Catalog> catalogsCache;
    private final long searchTimeoutSeconds = 10;
    private final long negotiationTimeoutSeconds = 20;
    private final String fileName = "dtrDataModel.json";
    private String dtrDataModelFilePath;
    private boolean enableCache;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    private State state;

    public enum State {
        Created,
        Running,
        Error,
        Finished
    }

    @Autowired
    public DtrSearchManager(FileUtil fileUtil, JsonUtil jsonUtil, DataTransferService dataTransferService, DtrConfig dtrConfig, ProcessManager processManager) {
        this.catalogsCache = new ConcurrentHashMap<>();
        this.dataTransferService = dataTransferService;
        this.processManager = processManager;
        this.dtrConfig = dtrConfig;
        this.state = State.Created;
        this.fileUtil = fileUtil;
        this.jsonUtil = jsonUtil;
        this.enableCache = this.dtrConfig.getEnableCache();
        this.loadDtrCache();
    }

    public Runnable startProcess(List<EdcDiscoveryEndpoint> edcEndpoints, String processId) {
        return new Runnable() {
            @Override
            public void run() {
                state = State.Running;
                if (edcEndpoints == null || processId == null) {
                    return;
                }
                List<EdcDiscoveryEndpoint> edcEndpointsToSearch = null;
                try {
                    edcEndpointsToSearch = (List<EdcDiscoveryEndpoint>) jsonUtil.bindReferenceType(edcEndpoints, new TypeReference<List<EdcDiscoveryEndpoint>>() {
                    });
                } catch (Exception e) {
                    throw new DataModelException(this.getClass().getName(), e, "Could not bind the reference type!");
                }
                try {
                    //Iterate the edcEndpoints
                    edcEndpointsToSearch.parallelStream().forEach(edcEndPoint -> {
                        //Iterate the connectionsURLs for each BPN
                        edcEndPoint.getConnectorEndpoint().parallelStream().forEach(connectionUrl -> {
                            //Search Digital Twin Catalog for each connectionURL with a timeout time
                            Thread asyncThread = ThreadUtil.runThread(searchDigitalTwinCatalogExecutor(connectionUrl), "ProcessDtrDataModel");
                            try {
                                if (!asyncThread.join(Duration.ofSeconds(searchTimeoutSeconds))) {
                                    asyncThread.interrupt();
                                    LogUtil.printWarning("Could not retrieve the Catalog due a timeout for the URL: " + connectionUrl);
                                    return;
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //Get catalog for a specific connectionURL (if exists) in the catalogCache data structure
                            Catalog catalog = catalogsCache.get(connectionUrl);
                            if (catalog == null) {
                                return;
                            }
                            Object contractOffers = catalog.getContractOffers();
                            //Check if contractOffer is an Array or just an Object and if is not null or empty, adds it to the dtrDataModel data structure
                            if (contractOffers == null) {
                                return;
                            }
                            if (contractOffers instanceof LinkedHashMap) {
                                Dataset dataset = (Dataset) jsonUtil.bindObject(contractOffers, Dataset.class);
                                if (dataset != null) {
                                    Thread singleOfferThread = ThreadUtil.runThread(createAndSaveDtr(dataset, edcEndPoint.getBpn(), connectionUrl, processId), "CreateAndSaveDtr");
                                    try {
                                        if (!singleOfferThread.join(Duration.ofSeconds(negotiationTimeoutSeconds))) {
                                            singleOfferThread.interrupt();
                                            LogUtil.printWarning("Could not retrieve the Catalog due a timeout for the URL: " + connectionUrl);
                                            return;
                                        }
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                return;
                            }
                            List<Dataset> contractOfferList = (List<Dataset>) jsonUtil.bindObject(contractOffers, List.class);
                            if (contractOfferList.isEmpty()) {
                                return;
                            }
                            contractOfferList.parallelStream().forEach(dataset -> {
                                Thread multipleOffersThread = ThreadUtil.runThread(createAndSaveDtr(dataset, edcEndPoint.getBpn(), connectionUrl, processId), "CreateAndSaveDtr");
                                try {
                                    if (!multipleOffersThread.join(Duration.ofSeconds(negotiationTimeoutSeconds))) {
                                        multipleOffersThread.interrupt();
                                        LogUtil.printWarning("Could not retrieve the Catalog due a timeout for the URL: " + connectionUrl);
                                    }
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    });
                    state = State.Finished;
                } catch (Exception e) {
                    state = State.Error;
                    throw new DataModelException(this.getClass().getName(), e, "Was not possible to process the DTRs");
                }
            }
        };
    }

    public String createDataModelFile(){ return fileUtil.createFile(this.getDataModelPath()); }

    public String getDataModelPath() {
        return Path.of(this.getDataModelDir(), this.fileName).toAbsolutePath().toString();
    }

    public String getDataModelDir() {
        return fileUtil.getTmpDir();
    }

    private Runnable searchDigitalTwinCatalogExecutor(String connectionUrl) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Catalog catalog = dataTransferService.searchDigitalTwinCatalog(connectionUrl);
                    if (catalog == null) {
                        LogUtil.printWarning("No catalog was found for the URL: " + connectionUrl);
                        return;
                    }
                    catalogsCache.put(connectionUrl, catalog);
                } catch (Exception e) {
                    LogUtil.printWarning("Could not find the catalog for the URL: " + connectionUrl);
                }
            }
        };
    }

    private DtrSearchManager addConnectionToBpnEntry (String bpn, Dtr dtr) {
        if (!(bpn.isEmpty() || bpn.isBlank() || dtr.getEndpoint().isEmpty() || dtr.getEndpoint().isBlank()) ) {
            if (this.getDtrDataModel().contains(bpn)) {
                if (!this.getDtrDataModel().get(bpn).contains(dtr))
                    this.getDtrDataModel().get(bpn).add(dtr);
            } else {
                this.getDtrDataModel().put(bpn, List.of(dtr));
            }
        }
        return this;
    }

    public ConcurrentHashMap<String, List<Dtr>> getDtrDataModel() {
        return this.dtrCache.getDtrDataModel();
    }

    private Runnable createAndSaveDtr(Dataset dataset, String bpn, String connectionUrl, String processId) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Offer offer = dataTransferService.buildOffer(dataset);
                    LogUtil.printMessage("OFFER:" + offer);
                    IdResponse negotiationResponse = dataTransferService.doContractNegotiations(offer, CatenaXUtil.buildDataEndpoint(connectionUrl));
                    if (negotiationResponse == null) {
                        LogUtil.printWarning("Was not possible to do ContractNegotiation for URL: " + connectionUrl);
                        return;
                    }
                    Negotiation negotiation = dataTransferService.seeNegotiation(negotiationResponse.getId());
                    if (negotiation == null) {
                        LogUtil.printWarning("Was not possible to see Negotiation for URL: " + connectionUrl);
                        return;
                    }
                    Dtr dtr = new Dtr(negotiation.getContractAgreementId(), connectionUrl, offer.getAssetId());
                    if (dtrConfig.getTemporaryStorage()) {
                        addConnectionToBpnEntry(bpn, dtr);
                        saveDtrDataModel();
                    }

                    processManager.addSearchStatusDtr(processId, dtr);

                } catch (Exception e) {
                    LogUtil.printWarning("Was not possible to do ContractNegotiation for URL: " + connectionUrl);
                }
            }
        };

    }

    private void saveDtrDataModel() {
        if (!this.dtrCache.isEnabledCache()) {
            return;
        }
        Date updateAt = this.dtrCache.getMeta().getUpdatedAt();
        this.dtrCache.getMeta().setUpdatedAt(new Date());
        String filePath = saveDtrCacheToFile(this.dtrCache);
        if (filePath == null) {
            //Restore updateAt if the save wasn't successful
            this.dtrCache.getMeta().setUpdatedAt(updateAt);
            return;
        }
        if (!this.dtrCache.getDtrDataModel().isEmpty()) {
            LogUtil.printMessage("[DTR DataModel] Saved [" + this.dtrCache.getDtrDataModel().size() + "] assets in DTR data model." );
        }
    }

    private String saveDtrCacheToFile (DtrCache dtrCache) {
        try {
            return jsonUtil.toJsonFile(this.dtrDataModelFilePath, dtrCache, true);
        } catch (Exception e ){
            LogUtil.printException(e, "Wasn't possible to save the Dtr Cache" );
            return null;
        }
    }

    private void loadDtrCache() {
        try {
            if (this.dtrDataModelFilePath == null) {
                createNewDtrCache(enableCache);
                return;
            }
            DtrCache dtrCache = (DtrCache) jsonUtil.fromJsonFileToObject(this.dtrDataModelFilePath, DtrCache.class);
            if (dtrCache == null) {
                createNewDtrCache(enableCache);
                return;
            }
            if (dtrCache.getDtrDataModel() == null) {
                dtrCache.setDtrDataModel(new ConcurrentHashMap<String, List<Dtr>>());
            }
            if (dtrCache.getDtrDataModel().isEmpty()) {
                LogUtil.printMessage("DTR Data Model is empty.");
            } else {
                LogUtil.printMessage("Loaded [" + dtrCache.getDtrDataModel().size() + "] entries from DTR Data Model Json.");
            }
            this.dtrCache = dtrCache;
        } catch (Exception e) {
            LogUtil.printWarning("Was not possible to load Dtr Data Model!");
            createNewDtrCache(enableCache);
        }
    }

    private void createNewDtrCache (boolean enableCache) {
        DtrCache dtrCache = new DtrCache(new ConcurrentHashMap<String, List<Dtr>>());
        dtrCache.setEnabledCache(enableCache);
        Date date = new Date();
        dtrCache.getMeta().setCreatedAt(date);
        dtrCache.getMeta().setUpdatedAt(date);
        dtrCache.getMeta().setDeleteAt(this.dtrConfig.convertCacheLifespanToDate(date));
        if (dtrCache.isEnabledCache()) {
            this.dtrDataModelFilePath = createDataModelFile();
            this.saveDtrCacheToFile(dtrCache);
        }
        this.dtrCache = dtrCache;
        this.scheduleCacheEraseTask();
    }

    private void scheduleCacheEraseTask () {
        Timer timer = new Timer("EraseCache");
        timer.schedule(eraseDtrCache(), this.dtrCache.getMeta().getDeleteAt());
    }

    private void deleteDtrCache() {
        try {
            fileUtil.deleteFile(this.dtrDataModelFilePath);
        } catch (Exception e) {
            LogUtil.printWarning("The Dtr Data Model couldn't be deleted.");
        }
    }

    private TimerTask eraseDtrCache () {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    LogUtil.printWarning("Cache is scheduled to be erased now!");
                    deleteDtrCache();
                    createDataModelFile();
                    createNewDtrCache(enableCache);
                } catch (NullPointerException e) {
                    LogUtil.printWarning("Date was null!");
                }
            }
        };
    }

}
