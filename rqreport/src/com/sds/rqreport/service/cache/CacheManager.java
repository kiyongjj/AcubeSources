package com.sds.rqreport.service.cache;

import org.apache.log4j.Logger;

import com.sds.rqreport.model.RQCacheContent;

import java.sql.SQLException;
import java.util.*;

import javax.naming.NamingException;

public class CacheManager {

    private boolean useCacheManager;
    private Logger L = Logger.getLogger("MANAGER");
    private RunCacheContainer runContainer = null;
    
    //singleton pattern
    private static CacheManager cacheManager = new CacheManager();
    public static CacheManager getInstance(){
        if(cacheManager == null){
            cacheManager = new CacheManager();
        }
        return cacheManager;
    }
    
    private CacheContainer container = null;
    
    //default Constructor
    private CacheManager(){
       int rqContentMapSize;
       rqContentMapSize = CacheContainer.RQ_CONTENTCACHE_SIZE;
       container = new CacheContainer(rqContentMapSize);
       runContainer = new RunCacheContainer(rqContentMapSize);
    }
    
    //container 를 반환한다.
    public CacheContainer getCacheContainer(){
        return container;
    }
    
    public RunCacheContainer getRunCashContainer(){
    	return runContainer;
    }
    
    /**
     * setUseCache
     * @param p_bUseCacheManager 
     */
    public void setUseCache(boolean p_bUseCacheManager){
        this.useCacheManager = p_bUseCacheManager;
    }
  
    /**
     * CacheContainer에 값이 있으면 그값을 가져온다.
     * @param p_strJndiname : JNDI name
     * @param p_strQuery : Query
     * @return RQcacheContent
     */
    public RQCacheContent getContent(String strJndiname, String strQuery){
        if(!useCacheManager){
        	return runContainer.runRQresult(strJndiname, strQuery);
             
        }else{
            RQCacheContent rqContent = container.getRQContentFromCacheContainer(strQuery);
            if (rqContent == null){
                rqContent = runContainer.runRQresult(strJndiname, strQuery);
                if(!rqContent.isStopStatus())
                {
                	System.out.println("stopStatus *******************");
                	container.getRQContentIntoCacheContainer(rqContent);
                }
            }
            return rqContent;
        }
    }
    
    /**
     * CacheManager를 통해 모든 실행되도록 한다.
     * Direct 실행, SQL String 실행, XML 실행 모두 CacheManager 를 통한 실행.
     * jndiname 이 없으면 Direct Connect 실행
     * @param rInfo 실행에 필요한 정보를 담은 Class
     * @return ResultSet 을 반환한다. 호출하는 쪽에서는 getResultSet() 함수만 호출하면 실행된다.
     */
    public String getResultSet(RunInfo rInfo) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException{
    	String strResultSet = "";
    	if(rInfo == null)
    		return "";
    	
    	String[] oSQLArr = null;
    	ArrayList lm_oSqlArray = new ArrayList();
    	if(rInfo.getStrSql() != null)
    	{
    		oSQLArr = rInfo.getStrSql().split(";");
    		rInfo.setSqlArray(oSQLArr);
    	
	        for(int i=0; i<oSQLArr.length;i++){
	            lm_oSqlArray.add(oSQLArr[i]);
	        }
	        rInfo.setSqlArrayList(lm_oSqlArray);
    	}

        lm_oSqlArray = null;
        RQCacheContent rqContent = null;
        
        return runContainer.runRQResultSet(rInfo);
    }
    
    public String stopResult(RunInfo rInfo) {
    	String strResult = "";
    	if(rInfo == null)
    		return strResult;
    	strResult = runContainer.stopResult(rInfo);

    	return strResult;
    }
}
