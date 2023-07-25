package org.eclipse.tractusx.productpass.models.catenax;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DtrCache {

    private ConcurrentHashMap<String, List<Dtr>> dtrDataModel;
    private Meta meta;

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

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        private Date createdAt;
        private Date updatedAt;
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
