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

package org.eclipse.tractusx.productpass.config;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix="configuration.dtr")
public class DtrConfig {
    Boolean central;
    String centralUrl;
    String internalDtr;
    DecentralApis decentralApis;
    String assetId;
    Boolean enableCache;
    CacheLifespan cacheLifespan;

    public DtrConfig() {
    }
    public DtrConfig(Boolean central) {
        this.central = central;
    }

    public Boolean getEnableCache() {
        return enableCache;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }
    public CacheLifespan getCacheLifespan() {
        return cacheLifespan;
    }

    public void setCacheLifespan(CacheLifespan cacheLifespan) {
        this.cacheLifespan = cacheLifespan;
    }

    public DecentralApis getDecentralApis() {
        return decentralApis;
    }
    public void setDecentralApis(DecentralApis decentralApis) {
        this.decentralApis = decentralApis;
    }
    public String getInternalDtr() {
        return internalDtr;
    }

    public void setInternalDtr(String internalDtr) {
        this.internalDtr = internalDtr;
    }

    public static class CacheLifespan {
        private Integer duration;
        private String timeunit;

        public CacheLifespan() {
        }

        public CacheLifespan(Integer duration, String timeunit) {
            this.duration = duration;
            this.timeunit = timeunit;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getTimeunit() {
            return timeunit;
        }

        public void setTimeunit(String timeunit) {
            this.timeunit = timeunit;
        }
    }


    public static class DecentralApis{
        String prefix;
        String search;
        String digitalTwin;
        String subModel;

        public DecentralApis(String prefix, String search, String digitalTwin, String subModel) {
            this.prefix = prefix;
            this.search = search;
            this.digitalTwin = digitalTwin;
            this.subModel = subModel;
        }

        public DecentralApis() {
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public String getDigitalTwin() {
            return digitalTwin;
        }

        public void setDigitalTwin(String digitalTwin) {
            this.digitalTwin = digitalTwin;
        }

        public String getSubModel() {
            return subModel;
        }

        public void setSubModel(String subModel) {
            this.subModel = subModel;
        }
    }

    public Boolean getCentral() {
        return central;
    }

    public void setCentral(Boolean central) {
        this.central = central;
    }

    public String getCentralUrl() {
        return centralUrl;
    }

    public void setCentralUrl(String centralUrl) {
        this.centralUrl = centralUrl;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Date convertCacheLifespanToDate (Date date) {
        Date convertedDate = new Date();
        switch (this.cacheLifespan.getTimeunit()) {
            case "SECONDS":
                    convertedDate = DateUtils.addSeconds(date, this.cacheLifespan.duration);
                    break;
            case "MINUTES":
                    convertedDate = DateUtils.addMinutes(date, this.cacheLifespan.duration);
                    break;
            case "DAYS":
                    convertedDate = DateUtils.addDays(date, this.cacheLifespan.duration);
                    break;
            default:
                convertedDate = DateUtils.addHours(date, this.cacheLifespan.duration);

        }
        return convertedDate;
    }
}
