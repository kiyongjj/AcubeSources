package com.sds.rqreport.service.cache;

import java.util.*;
import org.apache.log4j.Logger;

import com.sds.rqreport.*;
import com.sds.rqreport.model.RQCacheContent;

public class CacheContainer {
    
    private HashMap queryMap;
    private ArrayList rqContentMap;
    private int rqContentMapSize;
    private int totalCnt;
    private int hitCnt;
    
    public static final int RQ_CONTENTCACHE_SIZE = Environment.engine_cachesize;
    private Logger L = Logger.getLogger("MANAGER");

    public double getHitRate(){
        return (double)hitCnt / (double)totalCnt;
    }
    
    public int getRQContentMapSize(){
        return rqContentMapSize;
    }
    
    /**
     *  CacheContainer Constructor
     * @param int p_rqContentMapSize
     */
    public CacheContainer(int p_rqContentMapSize){
        queryMap = new HashMap();
        rqContentMap = new ArrayList(p_rqContentMapSize);
        this.rqContentMapSize = p_rqContentMapSize;
    }
    
    /**
     * HashMap에서 해당하는 RQcacheContent의 존재여부를 확인한다.
     * 있으면 그값을 반환하며 그렇지 않으면 null를 반환.
     * @param String p_strQuery
     * @return RQcacheContent
     */  
    public RQCacheContent getRQContentFromCacheContainer(String p_strQuery){
        totalCnt++;
        
        RQCacheContent rqContent = (RQCacheContent)queryMap.get(p_strQuery);
        if(rqContent != null){
            hitCnt++;
            rqContentMap.remove(rqContent);
            rqContentMap.add(0,rqContent);
        }
        return rqContent;
    }
    
    /**
     * RQcacheContent 를 배열에 저장한다. 
     * @param RQcacheContent p_oRqContent
     * @return void
     */  
    public synchronized void getRQContentIntoCacheContainer(RQCacheContent p_oRqContent){
        String lm_sQuery = p_oRqContent.getQuery();
        RQCacheContent oldRQContent = (RQCacheContent)queryMap.get(lm_sQuery);
        if(oldRQContent != null){
            rqContentMap.remove(oldRQContent);
        }else{

            if(rqContentMapSize == rqContentMap.size()){
                RQCacheContent lastRQContent = (RQCacheContent)rqContentMap.remove(rqContentMapSize-1);
                queryMap.remove(lastRQContent.getQuery());
            }
        }
        queryMap.put(lm_sQuery.trim(), p_oRqContent);
        rqContentMap.add(0,p_oRqContent);     
    }
}
