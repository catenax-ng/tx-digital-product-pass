package org.eclipse.tractusx.productpass.models.catenax;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DtrCache {

    @JsonProperty("dtrDataModel")
    private ConcurrentHashMap<String, List<Dtr>> dtrDataModel;
    @JsonProperty("meta")
    private Meta meta;
    @JsonIgnore
    private boolean enabledCache;

    public DtrCache(ConcurrentHashMap<String, List<Dtr>> dtrDataModel) {
        this.dtrDataModel = dtrDataModel;
        this.meta = new Meta();
    }

    public ConcurrentHashMap<String, List<Dtr>> getDtrDataModel() {
        return dtrDataModel;
    }

    public void setDtrDataModel(ConcurrentHashMap<String, List<Dtr>> dtrDataModel) {
        this.dtrDataModel = dtrDataModel;
    }

    public boolean isEnabledCache() {
        return enabledCache;
    }

    public void setEnabledCache(boolean enabledCache) {
        this.enabledCache = enabledCache;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        @JsonProperty("createdAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private Date createdAt;
        @JsonProperty("updatedAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private Date updatedAt;
        @JsonProperty("deleteAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private Date deleteAt;

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public synchronized void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Date getDeleteAt() {
            return deleteAt;
        }

        public void setDeleteAt(Date deleteAt) {
            this.deleteAt = deleteAt;
        }
    }
}
