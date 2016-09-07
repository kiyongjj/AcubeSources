package com.sds.rqreport.service;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.sds.rqreport.*;
import com.sds.rqreport.model.*;
import com.sds.rqreport.repository.*;
import com.sds.rqreport.service.cache.*;
import com.sds.rqreport.service.queryexecute.DataSplitter;
import com.sds.rqreport.service.web.*;
import com.sds.rqreport.site.ems.*;
import com.sds.rqreport.util.*;

public class RQView extends TagSupport{

    private static final long serialVersionUID = 1L;
    private String strAction = "";
    private String strDriver = "";
    private String strConn = "";
    private String strSid = "";
    private String strSpass = "";
    private String strSql = "";
    private String strXml = "";
    private int iStmtidx;
    private String strKey = "";
    private String strDBInfo = null;
    private String encqry = "";
    private String doc = "";
    private String runvar = "";
    private int dbidx = -1;
    private boolean bIsComplete  =   true;
    private String keystr = "reqube2006-0222";
    protected String resultData = "";
    boolean encryptData = false;
    protected static Hashtable sqlrep = new Hashtable();
    // protected static Hashtable dsrep = new Hashtable(); // 문제의 소지가 있는 캐시(?) 기능 삭제.
    ///////////////// RQDoc Statistic //////////////////
    boolean m_svrError = false;
    ///////////////////////////////////////////////////
    String strJndinameFromEnc = "";
    String strUserNameFromEnc = "";
    
    long startTime = 0; //From doStartTag
    long endTime = 0;  //To out.print(strResultSet)

    private Logger log = Logger.getLogger("RQWEBVIEW");

    /**
     * to  process start custom tag
     *
     * @return int
     */
    public int doStartTag() throws JspTagException{

    	startTime = System.currentTimeMillis();

    	if(encqry != null && encqry.length() > 5)
    	{
		  	Decrypter dec1 = new Decrypter(keystr,0);
		  	QueryStrAnalyzer qryAnalyzer = new QueryStrAnalyzer(dec1.decrypt(encqry));
		  	setAction(qryAnalyzer.getParameter("action"));
			setDriver(qryAnalyzer.getParameter("driver"));
			setConn(qryAnalyzer.getParameter("conn"));
			setSid(qryAnalyzer.getParameter("sid"));
			setSpass(qryAnalyzer.getParameter("spass"));
			setSql(qryAnalyzer.getParameter("sql"));
			setStrXml(qryAnalyzer.getParameter("strXml"));
			int val = 0 ;
			String tmp = qryAnalyzer.getParameter("stmtidx");
			if(tmp != null && tmp.length() > 0)
				val = Integer.parseInt(tmp);
			setStmtidx(val);
			setStrKey(qryAnalyzer.getParameter("strKey"));
			setStrDBInfo(qryAnalyzer.getParameter("strDBInfo"));
			setDoc(qryAnalyzer.getParameter("doc"));
			setRunvar(qryAnalyzer.getParameter("runvar"));
			val = 0;
			tmp = qryAnalyzer.getParameter("dbidx");
			if(tmp != null && tmp.length() > 0)
				val = Integer.parseInt(tmp);
			setDbidx(val);
			
			//encqry 안에 jndiname 에 대한 정보가 있으면 (rqviewer.jsp 안에 jndiname 에 대한 정보가 있으면)
			// 그값을 받아와 strJndinameFromEnc 로 셋팅
			strJndinameFromEnc = qryAnalyzer.getParameter("jndiname");
			
			// EMS Logging Site code
			strUserNameFromEnc = qryAnalyzer.getParameter("userName");
			
		//	setEncqry(qryAnalyzer.getParameter("encqry"));


    	}
        if(strAction.equals("getRs") || strAction.equals("getRsDB") || strAction.equals("getRsJCO")){
            getRqview();
        }else if(strAction.equals("getSQLExec")){
            getRqview();
        }else if(strAction.equals("getrsenc")) {
        	encryptData = true;
        	getRqview();
        }else if(strAction.equals("stopResult")) {
        	stopResult();
        }else if(strAction.equals("getFile")){
        	getFile();
        }else if(strAction.equals("getResult")){
        	//Script Executor

        	try {
				DocRepository docRep = new DocRepository();
				File f = docRep.getFile(doc);
				setRunvar(scriptExe(f,runvar));
			} catch (Exception e1) {
				RequbeUtil.do_PrintStackTrace(log, e1);
			}
        	//
        	getRqview();
        	try {
				DocRepository docapi = new DocRepository();
				File rootdir = docapi.getFile("/");
				File resultfile = File.createTempFile("RQR", "TMP", rootdir);
				File designf = docapi.getFile(doc);

				if(resultData != null){
					if(resultData.length() > 0)	{
						DataSplitter ds = new DataSplitter(resultData);
						ds.setNewlineSize(2);
						ds.getIdxArray();
						ds.splitResultSet();

						ds.makeResultFile(designf, resultfile);
					}
				}else{
					////// DataSet 이 없을경우 RQX 자체가 RQV가 된다. 
					if(designf.exists()){
						FileInputStream fis = new FileInputStream(designf);
						FileOutputStream fos = new FileOutputStream(resultfile);
						byte[] buf = new byte[fis.available()];
						try{
							fis.read(buf);
							fos.write(buf);
						}finally{
							if(fis != null) fis.close();
							if(fos != null) fos.close();
						}
					}
				}
								
				JspWriter out = pageContext.getOut();
				out.write(resultfile.getName());
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			} catch (Exception e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
        }else{

            bIsComplete  =   false;
        }
        return SKIP_BODY;
    }

    public String scriptExe(File doc, String runvar)
    {
    	Environment env = Environment.getInstance();
    	String retRunvar = runvar;
    	if(env.scriptExePath != null && env.scriptExePath.length() > 0)
    	{
    		Process scriptexeProcess = null;
    		try {
				File f = File.createTempFile("RQSCR", "TMP");
				String[] args = null;
				f.delete();
				String[] cmdarray = new String[4];
				String sargs =  " \"" + doc.getPath() + "\" \"" + f.getPath() + "\" \"" + runvar + "\"";
				cmdarray[0] = env.scriptExePath;
				cmdarray[1] = doc.getPath();
				cmdarray[2] = f.getPath();
				cmdarray[3] = runvar;
		    	//scriptexeProcess = Runtime.getRuntime().exec(env.scriptExePath + sargs);
				scriptexeProcess = Runtime.getRuntime().exec(cmdarray);

	            // any error message?
	            StreamDisplay errorDisplay = new
	            	StreamDisplay(scriptexeProcess.getErrorStream(), "ERROR");

	            // any output?
	            StreamDisplay outputDisplay = new
	            	StreamDisplay(scriptexeProcess.getInputStream(), "OUTPUT");

	            // kick them off
	            errorDisplay.start();
	            outputDisplay.start();
	            //scriptexeProcess.
	            long start = System.currentTimeMillis();
	            while(!f.exists())
	            {

//	            	try {
//	            		Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//	            	boolean b = f.canRead();
//	            	if(b)
//	            	{
//	            		break;
//	            	}
	            	if(System.currentTimeMillis() - start > 300000)
	            	{
	            		//Timeout
	            		return retRunvar;
	            	}
	            }
	            int len = (int)f.length();
	            byte[] runvarbyte = new byte[len];
	            FileInputStream fi = new FileInputStream(f);
	            fi.read(runvarbyte);
	            fi.close();
	            retRunvar = new String(runvarbyte,"UTF-8");
	            retRunvar.trim();
	            f.delete();

			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
    	}
    	return retRunvar;
    }

    /**
     *  to process end custom tag
     *
     * @return int
     */
    public int doEndTag() throws JspTagException{

        HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse res = (HttpServletResponse)pageContext.getResponse();

        try{
            req.setCharacterEncoding("UTF-8");
            res.setContentType("text/html;charset=UTF-8");
        }catch (UnsupportedEncodingException e){
        	RequbeUtil.do_PrintStackTrace(log, e);
        }
        if(bIsComplete){
            return EVAL_PAGE;
        }else{
            return SKIP_BODY;
        }
    }

    /**
     *  to set bean module
     */
    public void setAction(String p_strAction){
        strAction = p_strAction;
    }
    public void setDriver(String p_strDriver){
        strDriver = p_strDriver;
    }
    public void setConn(String p_strConn){
        strConn = p_strConn;
    }
    public void setSid(String p_strSid){
        strSid = p_strSid;
    }
    public void setSpass(String p_strSpass){
        strSpass = p_strSpass;
    }
    public void setSql(String p_strSql){
        strSql = p_strSql;
    }
    public void setStrXml(String p_strXml){
        strXml  = p_strXml;
    }
    public void setStmtidx(int p_iStmtidx){
        iStmtidx = p_iStmtidx;
    }
    public void setStrKey(String key) {
    	this.strKey = key.trim();
    }
    public void setStrDBInfo(String strDBInfo) {
    	this.strDBInfo = strDBInfo;
    }
    public void setEncqry(String encqry) {
    	this.encqry = encqry;
    }
    public void setDoc(String doc) {
    	this.doc = doc;
    }
    public void setRunvar(String runvar){
    	this.runvar = runvar;
    }
    public void setDbidx(int dbidx){
    	this.dbidx = dbidx;
    }

    /**
	* DB Method 에 따른 getRqview 호출
	* 
	* 2009.08.xx 에 문서호출 통계 모듈 추가됨 
	*
	* DB에는 
	* 일자별, 			RUN_TIME
	* 문서별, 			FILE_NM 
	* 호출횟수, 			RUNCNT
	* 서버총소요시간, 	SERVERTIME_ACCUMUL
	* 서버평균소요시간,	SERVERTIME_AVE
	* 총소요시간,        TOTALTIME_ACCUMUL
	* 총평균소요시간, 	TOTALTIME_AVE	
	* 최대소요시간, 		MAXTIME			
	* 최대소요조건, 		MAXTIME_RUNVAR
	* 최소소요시간, 		MINTIME
	* 최소소요조건 		MINTIME_RUNVAR
	* 오류횟수 	        ERROR_CNT
	* 
	* 그리고 실행이 될때마다 아래의 내용을 
	* 일자_문서명.his 로 파일로 그 히스토리를 남긴다.
	* 
	* 일자, 
	* 문서명, 
	* 사용자IP, 
	* 조건값, 
	* 호출시간, 
	* 서버응답시간, 
	* 최종응답시간, 
	* 서버소요시간, 
	* 최종소요시간, 
	* 데이터건수, 
	* 오류여부
	* 
	* 단 문서가 정상적으로 실행이 되었을경우에만 남긴다.
	* 정상적 실행 = 클라이언트 실행 + 서버 실행 
	* 둘중 한개라도 에러가 발생하면 통계로그를 남기지 않는다.
	* 
    * @return void
    */
    public void getRqview(){
    	long currenttime = System.currentTimeMillis();
    	String lm_SERVERTIME_RESPONSE = 
    		new RequbeUtil().strStampToDate(currenttime);
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	HttpSession        session = (HttpSession) pageContext.getSession();
    	
    	Environment tenv = Environment.getInstance();
    	Environment.load();
    	
    	String client_ip = request.getRemoteAddr();

    	MDC.put("userip", client_ip);
    	MDC.put("doc", doc);
    	log.debug("action : " + strAction);
    	log.info("Server [START]");
    	log.info("runvar, " + runvar);
    	
        String strResultSet = "";
        JspWriter out = pageContext.getOut();

        // site code (전자 EMS 요청사항임) /////////////////////////////////////////////
    	if(tenv.site_ems_logging.equalsIgnoreCase("RQEMSLog")){
    		RepositoryEnv renv = RepositoryEnv.getInstance();    		
    		RQSiteLogInterface sitelog = new RQEMSLogImpl();
    		RQSiteLogDataInterface data = new  RQEMSLogData();
    		
    		((RQEMSLogData)data).setUserid(strUserNameFromEnc);
    		((RQEMSLogData)data).setDocpath(renv.repositoryRoot + doc);
    		((RQEMSLogData)data).setEventtime(currenttime);
    		
    		sitelog.StartLogging(data);
    	}
    	/////////////////////////////////////////////////////////////////////////////
    	
        try{
        	if(strKey == null || strKey.length() < 2)
        		strKey = "123456";

            String jndiname = (pageContext.getRequest()).getParameter("jndiname");
            if(jndiname != null && jndiname.indexOf("?") != -1){
            	jndiname = jndiname.substring(0, jndiname.length()-1);
            }
            // Get jndi name From doc
//            String docName = (pageContext.getRequest()).getParameter("doc");
//
//
//            if(docName != null && docName.length() > 0)
//            {
//
//            }

            boolean flag = Boolean.valueOf((pageContext.getRequest()).getParameter("cacheman")).booleanValue();
            boolean bResultRun = false;
            RQCacheImpl cache = new RQCacheUseImpl();
            DocRepository docRep = new DocRepository();

        	//String runvar = "aa|성명|bb|부서명|cc|직급|dd|사원";
            if( strAction.equals("getResult"))
            {
            	bResultRun = true;
            }


        	if(strAction.equals("getRsDB") || strAction.equals("getRsJCO") || bResultRun){
    			try{
    				strXml = (String)sqlrep.get(doc);

    				if(strXml == null)
    				{
    					strXml = docRep.getSQL(doc);
    					sqlrep.put(doc, strXml);
    				}
    				log.debug("strXml : " + strXml);
    				strXml = RequbeUtil.replaceCondVarWithValue(strXml, runvar); //실행변수 치환
    				log.debug("replaced strXml : " + strXml);
    			}catch(Exception e){
    				RequbeUtil.do_PrintStackTrace(log, e);
    			}
        	}else if(strAction.equals("getRs")){
        		strXml = RequbeUtil.replaceCondVarWithValue(strXml, runvar);
        	}

            RunInfo runInfo = new RunInfo(strKey, jndiname, flag, strSql, strXml, false,
            								strDriver, strConn, strSid, strSpass, iStmtidx);
            if(!bResultRun)
            {
            	log.info("Server Mode, buffer mode");
            	runInfo.setOut(out);
            }
            else
            {
            	log.info("Server Mode, non-buffer mode");
            	runInfo.setOut(null);
            	runInfo.setStrSql(null);
            }
            
            /*
            List dslist = (List)dsrep.get(doc);
            if(dslist == null)
            {
            	int res = docRep.getDSList(doc);
            	if(res == 0)
            	{
            		dslist = docRep.getList();
            		dsrep.put(doc, dslist);
            	}
            }
			*/
            
            List dslist = null;
        	int res = docRep.getDSList(doc);
        	if(res == 0){ // if success return 0
        		dslist = docRep.getList();
        	}
            
            if(dslist != null)
            {
            	int size = dslist.size();
            	String[] dsNames = null;
            	if(strAction.equals("getSQLExec")){
            		dsNames = new String[1];
            		for(int i = 0; i < size; ++i){
            			if(i == dbidx){
            				dsNames[0] = ((DSListInfo)dslist.get(i)).getParamString(1);
            			}
                	}
            	}else{
            		log.debug("DSSize : " + size);
            		dsNames = new String[size];
                	for(int i = 0; i < size; ++i)
                	{
                		dsNames[i] = ((DSListInfo)dslist.get(i)).getParamString(1);
                		log.debug("DSName : " + dsNames[i]);
                	}
            	}
            	// strJndinameFromEnc 가 존재하면 그 값으로 셋팅
            	if(strJndinameFromEnc != null && !strJndinameFromEnc.equals("")){
            		dsNames = strJndinameFromEnc.split("\\|");
            		
            	}
            	runInfo.setDocDS(dsNames);
            	
            }else{
            	runInfo.setDocDS(null);
            }
            	
            runInfo.setStrDBInfo(strDBInfo); // by ljg
            
            // site code (전자 EMS 요청사항임) ///////////////////////////////////////////
        	if(tenv.site_ems_logging.equalsIgnoreCase("RQEMSLog")){
        		long lm_currenttime = System.currentTimeMillis();
        		RepositoryEnv renv = RepositoryEnv.getInstance();    		
        		RQSiteLogInterface sitelog = new RQEMSLogImpl();
        		RQSiteLogDataInterface data = new  RQEMSLogData();
        		
        		((RQEMSLogData)data).setUserid(strUserNameFromEnc);
        		((RQEMSLogData)data).setDocpath(renv.repositoryRoot + doc);
        		((RQEMSLogData)data).setEventtime(lm_currenttime);
        		
        		sitelog.EngineStartLogging(data);
        	}
        	////////////////////////////////////////////////////////////////////////////
        	
            if(strAction.equals("getRsJCO")){
            	JCOController lm_oJCOController = new JCOController();
            	lm_oJCOController.connInfo = new ConnectionInfo();
            	//Connection 정보를 넘겨 받지 못할경우 JCOProp.properties 파일에서 connInfo를 만들어주도록 한다.
            	if(strDBInfo == null || strDBInfo.equals("")){
            		ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
            		Enumeration enum1 = lm_oRb.getKeys();
            		String lm_tmp = "";
            		String JCOPoolNm = "";

            		//SingDB Connection (Multi DB Connection 은 추후)
            		String[] tmpDs = runInfo.getDocDS();
            		String tmpDsStr  = tmpDs[0];

            		while(enum1.hasMoreElements()){
            			lm_tmp = (String)enum1.nextElement();
            			if(lm_tmp.indexOf("JCOPoolName") != -1){
            				if(tmpDsStr.equals(lm_oRb.getString(lm_tmp))){
            					JCOPoolNm = lm_oRb.getString(lm_tmp);
            				}
            			}
            		}

            		if(JCOPoolNm.equals(tmpDsStr)){
                		lm_oJCOController.connInfo.Client      = lm_oRb.getString(JCOPoolNm + "." + "strClient");
                		lm_oJCOController.connInfo.id          = lm_oRb.getString(JCOPoolNm + "." + "strAccount");
                		lm_oJCOController.connInfo.pw 	       = lm_oRb.getString(JCOPoolNm + "." + "strPassword");
                		lm_oJCOController.connInfo.Language    = lm_oRb.getString(JCOPoolNm + "." + "strLanguage");
                		lm_oJCOController.connInfo.host        = lm_oRb.getString(JCOPoolNm + "." + "strHost");
                		lm_oJCOController.connInfo.SysNumber   = lm_oRb.getString(JCOPoolNm + "." + "strSysNumber");
                		lm_oJCOController.connInfo.Client      = lm_oRb.getString(JCOPoolNm + "." + "strClient");
                		lm_oJCOController.connInfo.jcoConnType = lm_oRb.getString(JCOPoolNm + "." + "jcoConnType");
                		lm_oJCOController.connInfo.key         = lm_oRb.getString(JCOPoolNm + "." + "key");
                		lm_oJCOController.connInfo.poolsize    = lm_oRb.getString(JCOPoolNm + "." + "poolsize");
                		lm_oJCOController.connInfo.r3name      = lm_oRb.getString(JCOPoolNm + "." + "r3name");
                		lm_oJCOController.connInfo.group       = lm_oRb.getString(JCOPoolNm + "." + "group");
            		}
            	}
            	strResultSet = lm_oJCOController.rfcExecuteFunction2(strXml,strDBInfo);
            	//log.debug("JCO DataSet : " + strResultSet);

            }else{
            	strResultSet = cache.setCMger(flag).getResultSet(runInfo);
            }
            
            // site code (전자 EMS 요청사항임) ///////////////////////////////////////////
        	if(tenv.site_ems_logging.equalsIgnoreCase("RQEMSLog")){
        		long lm_currenttime = System.currentTimeMillis();
        		RepositoryEnv renv = RepositoryEnv.getInstance();    		
        		RQSiteLogInterface sitelog = new RQEMSLogImpl();
        		RQSiteLogDataInterface data = new  RQEMSLogData();

        		((RQEMSLogData)data).setUserid(strUserNameFromEnc);
        		((RQEMSLogData)data).setDocpath(renv.repositoryRoot + doc);
        		((RQEMSLogData)data).setEventtime(lm_currenttime);
        		
        		sitelog.EngineEngLogging(data);
        	}
        	////////////////////////////////////////////////////////////////////////////
        	
            //use encript<encrypt></encrypt>
           
            if(tenv.rqreport_dataset_charset_change.equals("yes")){
            	strResultSet = new String(strResultSet.getBytes(tenv.rqreport_dataset_charset_change_from),
            			       tenv.rqreport_dataset_charset_change_to);
            }
            if(!bResultRun)
            {
	            if(tenv.useEncryptData)
	            {
	            	out.print("<encryptData>");
	            	Encrypter enc = new Encrypter(keystr,0);
	            	out.print(enc.encrypt(strResultSet));
	            	out.print("</encryptData>");
	            	out.flush();
	            }
	            else
	            {
	            	out.print(strResultSet);
	            	out.flush();
	            }
            }else
            {
            	this.resultData = strResultSet;
            }
            runInfo = null;
            endTime = System.currentTimeMillis();
            log.info("Server Response Time, " + (endTime-startTime));
            log.info("Server [END]");
            MDC.remove("userip");
        	MDC.remove("doc");

        	// site code (전자 EMS 요청사항임) ///////////////////////////////////////////
        	if(tenv.site_ems_logging.equalsIgnoreCase("RQEMSLog")){
        		RepositoryEnv renv = RepositoryEnv.getInstance();    		
        		RQSiteLogInterface sitelog = new RQEMSLogImpl();
        		RQSiteLogDataInterface data = new  RQEMSLogData();
        		
        		((RQEMSLogData)data).setUserid(strUserNameFromEnc);
        		((RQEMSLogData)data).setDocpath(renv.repositoryRoot + doc);
        		((RQEMSLogData)data).setEventtime(endTime);
        		((RQEMSLogData)data).setRuntime("" + (endTime-startTime));
        		
        		sitelog.FinishLogging(data);
        	}
        	////////////////////////////////////////////////////////////////////////////
        	
        } catch (Exception e) {
        	m_svrError = true;
        	RequbeUtil.do_PrintStackTrace(log, e);
        } finally {
        	if(tenv.useRQStatistics.equals("RQStatistics")){
	        	// 서버에서 오류가 날경우 아래의 작업을 하지 않는다.
	        	if(!m_svrError){
		        	endTime = System.currentTimeMillis();
		            long servertime = endTime - startTime;
		        	///////////////// RQDoc Statistic ////////////////////////////////////////////
		        	RQDocStatGetRC lm_DocStatGetRC = RQDocStatGetRC.getInstance();
		        	RQDocStatSV lm_RQDocStatSV = new RQDocStatSV();
		        	lm_RQDocStatSV.setFILE_NM(doc);
		        	lm_RQDocStatSV.setRUNVAR(runvar);
		        	lm_RQDocStatSV.setCLIENT_IP(client_ip);
		        	lm_RQDocStatSV.setSERVERTIME("" + servertime);
		        	lm_RQDocStatSV.setSERVERTIME_RESPONSE(lm_SERVERTIME_RESPONSE);
		        	lm_RQDocStatSV.setROWCNT("" + lm_DocStatGetRC.getRCinHM(Thread.currentThread().getName()));
		        	
		        	// this RQDocStatSV is resetted after RQX execute.     // see RQDocStat.java
		        	session.setAttribute("RQDocStatSV", lm_RQDocStatSV);
		        	////////////////////////////////////////////////////////////////////////////
		        	
		        	//////////// reset values ///////////////////////
		        	//RQDocStatGetRC.setNullRQDocStatGetRC();
		        	lm_DocStatGetRC.setNullrowcnt(Thread.currentThread().getName());
		        	lm_RQDocStatSV = null;
		        	/////////////////////////////////////////////////
	        	}
	        	m_svrError = false;
        	}
        }
    }

    public void stopResult() {
    	long startTime = 0;
        long endTime = 0;

        startTime = System.currentTimeMillis();

        String strResultSet = "";
        JspWriter out = pageContext.getOut();

        try{
        	if(strKey == null && strKey.length() < 2)
        		strKey = "123456";

            RQCacheImpl cache = new RQCacheUseImpl();
            RunInfo runInfo = new RunInfo(strKey, null, false, null, null, false,
            		null, null, null, null, 0);
            strResultSet = cache.setCMger(false).stopResult(runInfo);

            out.print(strResultSet);
            runInfo = null;

            endTime = System.currentTimeMillis();
            log.info("Server Stop Time, " + (endTime-startTime));

        } catch (IOException e) {
        	RequbeUtil.do_PrintStackTrace(log, e);
        }
    }
    public void getFile()
    {


	//request.setCharacterEncoding("EUC-KR");

	// For Tomcat 5.x to Convert String

    	String docname = doc;
	//PrintStream out2 = new PrintStream(response.getOutputStream());
	//response.setContentType("application");
    JspWriter out = pageContext.getOut();

	Environment env = Environment.getInstance();
	try {
		DocRepository docRep = new DocRepository();
		File f = docRep.getFile(docname);
		if(f != null && f.exists() && f.isFile())
		{

			byte[] fileData;
			fileData = JDBCHelper.readFile(f);
			String fileStr = new String(fileData,"UTF-8");
			Encrypter enc = new Encrypter(keystr);
			out.write("<encryptData>");
			out.write(enc.encrypt(fileStr));
			out.write("</encryptData>");
			//out.print((new String(fileData,"UTF-8")).trim());
			return;

		}
	} catch (UnsupportedEncodingException e) {
		RequbeUtil.do_PrintStackTrace(log, e);
	} catch (IOException e) {
		RequbeUtil.do_PrintStackTrace(log, e);
	} catch(Exception e)
	{
		RequbeUtil.do_PrintStackTrace(log, e);
	}
//	public void setEncqry(String encqry) {
//		this.encqry = encqry;
//        if(encqry != null && encqry.length() > 5)
//        {
//        	Decrypter dec1 = new Decrypter(keystr,0);
//
//        	QueryStrAnalyzer qryAnalyzer = new QueryStrAnalyzer(dec1.decrypt(encqry));
//        	setAction(qryAnalyzer.getParameter("action"));
//        	setDriver(qryAnalyzer.getParameter("driver"));
//        	setConn(qryAnalyzer.getParameter("conn"));
//        	setSid(qryAnalyzer.getParameter("sid"));
//        	setSpass(qryAnalyzer.getParameter("spass"));
//        	setSql(qryAnalyzer.getParameter("sql"));
//        	setStrXml(qryAnalyzer.getParameter("strXml"));
//        	int val = 0 ;
//        	val = Integer.parseInt(qryAnalyzer.getParameter("stmtidx"));
//        	setStmtidx(val);
//        	setStrKey(qryAnalyzer.getParameter("strKey"));
//        	setStrDBInfo(qryAnalyzer.getParameter("strDBInfo"));
//        	setEncqry(qryAnalyzer.getParameter("encqry"));
//
//
//        }
    }

	public void setKeystr(String keystr) {
		this.keystr = keystr;
	}

	public static void refreshInfo()
	{
		sqlrep.clear();
		//dsrep.clear();
	}

}