package com.sds.rqreport.service.cache;

import java.sql.SQLException;
import java.util.*;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.sds.rqreport.*;
import com.sds.rqreport.model.RQCacheContent;
import com.sds.rqreport.service.queryexecute.*;

public class RunCacheContainer {

//	private PoolManager poolMgr = PoolManager.getInstance();
    public static final int RQ_CONTENTCACHE_SIZE = Environment.engine_cachesize;
    public static final int RQ_TIMEOUT = Environment.engine_timeout;
    private int MAX_RESOURCE = Environment.engine_thread;

    private Logger log = Logger.getLogger("MANAGER");
    public static final int RQ_DIRECT_TYPE = 1;
    public static final int RQ_XML_CLIENT_TYPE = 2;
    public static final int RQ_XML_SERVER_TYPE = 3;
    public static final int RQ_SQL_CLIENT_TYPE = 4;
    public static final int RQ_SQL_SERVER_TYPE = 5;
    private int LOOP_SLEEP_TIME = 10;

    private ArrayList runningList = new ArrayList(MAX_RESOURCE);

    public RunCacheContainer(int p_rqContentMapSize){
    }

    public RQCacheContent runRQresult(String strJndiname, String strQuery) {
    	RQCacheContent rqContent = null;
    	String strKey = strJndiname + strQuery;
    	log.debug("No Thread : " + strKey);
    	RQGetDataObj oRQServer = new RQGetDataObj(strJndiname, strQuery);
    	oRQServer.getRQResult();

//        PoolObject pobj = new PoolObject();
//        int LOOP_CNT            = (RQ_TIMEOUT * 1000) / LOOP_SLEEP_TIME;
//        try {
//
//        	L.debug("before runRQresult()...");
//			synchronized(this){
//				pobj.setObject(oRQServer);
//				pobj.setKey(strKey);
//				runningList.add(pobj);
//			}
//			Thread td = new Thread(pobj);
//	       	L.debug("before td.start()...");
//			td.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		while(LOOP_CNT > 0) {
//			try{
//				if ((oRQServer.getRQCacheContent() == null) && (oRQServer.exception_status == 1)){
//                     Thread.sleep(LOOP_SLEEP_TIME);
//				}
//				else break;
//
//				LOOP_CNT--;
//            }catch(InterruptedException ie){
//                L.error(ie);
//            }
//        }

//		if (LOOP_CNT == 0) L.info("running time out");

        rqContent = oRQServer.getRQCacheContent();
//        synchronized(this){
//        	runningList.remove(pobj);
//        }
    	return rqContent;
    }
	public RQCacheContent runRQContent(String strKey, RQGetDataIf oRQServer /*String strJndiname, String strQuery*/) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException{
    	RQCacheContent rqContent = null;
    	oRQServer.getRQResult();

//        PoolObject pobj = new PoolObject();
//        int LOOP_CNT            = (RQ_TIMEOUT * 1000) / LOOP_SLEEP_TIME;
//       try {
//        	L.debug("runRQContent strKey = " + strKey);
//        	L.debug("before synch");
//			synchronized(this){
//				pobj.setObject(oRQServer);
//				pobj.setKey(strKey);
//				runningList.add(pobj);
//			}
//			L.debug("after synch");
//			Thread td = new Thread(pobj);
//			td.start();
//			L.debug("td.start()");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
/*
		try {
//			Thread.sleep(2000);
			Iterator it = runningList.iterator();
			while(it.hasNext())
			{
				PoolObject aa = (PoolObject)it.next();
				if(strKey.compareTo(aa.getKey())== 0) {
					aa.getObject().stopStatus();
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
*/

//        L.debug("RQ_TIMEOUT =[" + RQ_TIMEOUT + "]");
//        L.debug("LOOP_CNT     =[" + LOOP_CNT + "]");

//        while(LOOP_CNT > 0) {
//			try{
//				if ((oRQServer.getRQCacheContent() == null) && (oRQServer.exception_status == 1)){
//                     Thread.sleep(LOOP_SLEEP_TIME);
//				}
//				else break;
//
//				LOOP_CNT--;
//            }catch(InterruptedException ie){
//                L.error(ie);
//            }
//        }

//		if (LOOP_CNT == 0) L.info("running time out");

        rqContent = oRQServer.getRQCacheContent();
//        synchronized(this){
//        	runningList.remove(pobj);
//         }
    	return rqContent;
    }

	public String runRQResultSet(RunInfo rInfo) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException {
		RQCacheContent rqContent = null;
		String strResultSet = "";
		String strKey = null;

		//Set run type
		int nRunType = RQ_DIRECT_TYPE;
		if((rInfo.getJndiName() == null || rInfo.getJndiName() == "") && rInfo.getDocDS() == null )
		{
			nRunType = RQ_DIRECT_TYPE;
			// client type
			if(rInfo.getStrXml() != null && rInfo.getStrXml().length() > 2 && rInfo.getSqlArray() == null)
			{
				nRunType = RQ_XML_CLIENT_TYPE;
			}
			else if(rInfo.getSqlArray() != null && rInfo.getStrXml() == null )
			{
				nRunType = RQ_SQL_CLIENT_TYPE;
			}
			//for SQLExec
			if(rInfo.getDocDS() != null){
				nRunType = RQ_XML_CLIENT_TYPE;
			}
		}
		else
		{
			// server type
			if(rInfo.getStrXml() != null && rInfo.getStrXml().length() > 2)
			{
				nRunType = RQ_XML_SERVER_TYPE;
			}
			else if(rInfo.getSqlArray() != null && rInfo.getDocDS() != null && rInfo.getDocDS().length > 0)
			{
				nRunType = RQ_SQL_SERVER_TYPE;
			}

		}

		String lm_strRunType = "";
		switch (nRunType) {
		case 1:
			lm_strRunType = "RQ_DIRECT_TYPE";
			break;
		case 2:
			lm_strRunType = "RQ_XML_CLIENT_TYPE";
			break;
		case 3:
			lm_strRunType = "RQ_XML_SERVER_TYPE";
			break;
		case 4:
			lm_strRunType = "RQ_SQL_CLIENT_TYPE";
			break;
		case 5:
			lm_strRunType = "RQ_SQL_SERVER_TYPE";
			break;
		default:
			lm_strRunType = "";
			break;
		}
		log.info("RunType, " +nRunType+ ", " +lm_strRunType);
		//System.out.println("RunType : " + lm_strRunType);
		
		if ( nRunType == RQ_DIRECT_TYPE) {
    		//Direct Connect
			strKey = rInfo.getStrKey();
    		RQGetDataStr oRQStrGetData = new RQGetDataStr(rInfo.getStrDriver() , rInfo.getStrConn(), rInfo.getStrSid(),
    				rInfo.getStrSpass(), rInfo.getSqlArrayList(), rInfo.getIStmtidx());
    		rqContent = runRQContent(strKey, oRQStrGetData);
    		strResultSet = (String)rqContent.getReturnStrObject();

    	}
		else if(nRunType == RQ_XML_CLIENT_TYPE)
		{
//			RQGetDataHierarchy oRQServer = new RQGetDataHierarchy(rInfo.getJndiName(), rInfo.isBUseCache(),
//            		rInfo.getStrXml(), rInfo.getStrDBInfo(), rInfo.isBExecutor());

			log.debug("RQ_XML_CLIENT_TYPE before rInfo.getStrKey() ");
			RQGetDataHierarchy oRQServer = new RQGetDataHierarchy(rInfo);
            strKey = rInfo.getStrKey();
			log.debug("RQ_XML_CLIENT_TYPE before runRQContent() ");
            rqContent = runRQContent(strKey, oRQServer);
            strResultSet = (String)rqContent.getReturnStrObject();
		}
		else if(nRunType == RQ_SQL_CLIENT_TYPE)
		{
			for (int i=0; i<rInfo.getSqlArray().length;i++){
            	strKey = rInfo.getStrKey();
                RQGetDataObj oRQServer = new RQGetDataObj(rInfo.getJndiName(), rInfo.getSqlArray()[i]);
                rqContent = runRQContent(strKey, oRQServer);
                strResultSet += rqContent.getReturnStrObject();
                if( rqContent.isStopStatus() ){
                	System.out.println("stopStatus CacheManager getResultSet !!!!!!!");
                	break;
                }
            }
		}
		else if(nRunType == RQ_XML_SERVER_TYPE)
		{
			rInfo.setBExecutor(false);
			RQGetDataHierarchy oRQServer = new RQGetDataHierarchy(rInfo);
            strKey = rInfo.getStrKey();
            rqContent = runRQContent(strKey, oRQServer);
            strResultSet = ((String)rqContent.getReturnStrObject());
		}
		else if(nRunType == RQ_SQL_SERVER_TYPE)
		{
			for (int i=0; i<rInfo.getSqlArray().length;i++){
            	strKey = rInfo.getStrKey();
            	String jndiname = "";
            	if(rInfo.getJndiName() != null){
            		jndiname = rInfo.getJndiName();
            	}else{
            		String[] dsArr = rInfo.getDocDS();
            		if(dsArr != null && dsArr.length > 0){
            			jndiname = dsArr[0];
            		}
            	}
            	RQGetDataObj oRQServer = new RQGetDataObj(jndiname, rInfo.getSqlArray()[i]);

                rqContent = runRQContent(strKey, oRQServer);
                strResultSet += rqContent.getReturnStrObject();
                if( rqContent.isStopStatus() ){
                	System.out.println("stopStatus CacheManager getResultSet !!!!!!!");
                	break;
                }
            }
		}
		/*
    	else {
    		//jndi sql 실행.
//    		use JNDI , Cachemanager
            if(!rInfo.getStrXml().equals("") && rInfo.getStrXml() != null ){
                RQGetDataHierarchy oRQServer = new RQGetDataHierarchy(rInfo.getJndiName(), rInfo.isBUseCache(),
                		rInfo.getStrXml(), rInfo.getStrDBInfo(), false);
                strKey = rInfo.getStrKey();
                rqContent = runRQContent(strKey, oRQServer);
                strResultSet = (String)rqContent.getReturnStrObject();
            }else{
                for (int i=0; i<rInfo.getSqlArray().length;i++){
                	strKey = rInfo.getStrKey();
                    RQGetDataObj oRQServer = new RQGetDataObj(rInfo.getJndiName(), rInfo.getSqlArray()[i]);
                    rqContent = runRQContent(strKey, oRQServer);
                    strResultSet += rqContent.getReturnStrObject();
                    if( rqContent.isStopStatus() ){
                    	System.out.println("stopStatus CacheManager getResultSet !!!!!!!");
                    	break;
                    }
                }
            }
    	}
		*/
		return strResultSet;
	}

	public String stopResult(RunInfo rInfo) {
		String strKey = null;
		boolean bStop = false;
		try {
			strKey = rInfo.getStrKey();

//			Thread.sleep(2000);
			synchronized(this) {
				Iterator it = runningList.iterator();
				System.out.println("runningList Size : " + runningList.size());
//				System.out.println("stopResult strKey = " + rInfo.getStrKey());
				while(it.hasNext())
				{
					PoolObject aa = (PoolObject)it.next();
					if(strKey.compareTo(aa.getKey())== 0) {
						aa.getObject().stopStatus();
						System.out.println("RunCacheContainer StopResult Success OK!!");
						bStop = true;
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bStop)
			return "StopResult OK";
		else
			return "NONE";
	}
}
