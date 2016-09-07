package com.sds.rqreport.service.cache;

public class RQCacheUseImpl implements RQCacheImpl {

    public CacheManager setCMger(boolean flag) {
        //get CacheManager Instance with Singleton
        CacheManager lm_CacheManager = CacheManager.getInstance();
        //set Cache-manager true or false
        lm_CacheManager.setUseCache(flag);
        return lm_CacheManager;
    }
}
