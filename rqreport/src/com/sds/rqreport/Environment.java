package com.sds.rqreport;


import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.*;

import com.sds.rqreport.repository.RepositoryEnv;
import com.sds.rqreport.repository.FtpSyncEnv;
import com.sds.rqreport.util.*;


public class Environment {

	private static final String PROPERTIES_NAME = "rqreport.properties";
	private static final String MYSINGLEKEY = "mySingle_key";
	private static Environment env = new Environment();
	//private static RepositoryEnv repEnv = null;
	private static String configfile = "";
	private static long lastRead = 0;
	private Environment()
	{

	}

	public static Environment getInstance()
	{
		return env;
	}

	public static String getConfigFile()
	{
		return configfile;
	}


	public static RepositoryEnv getRepositoryEnv()
	{
		return RepositoryEnv.getInstance();
	}

	private static PropertyLoader pl = null;
//	private static Logger L = Logger.getLogger("Environment");

	//engine Environment variable
	public static char engine_SSEP = 5;
	public static char engine_RSEP = 9;
	public static char engine_CSEP = 10;
	public static int engine_cachesize = 20;
	public static int engine_thread = 20;
	public static int engine_time_wait = 30;
	public static int engine_timeout = 600;
	public static String mysinglekey = "";
	public int filelimit = 50;

	public boolean useMySingleLogin = false;
	public boolean useEncryptData = false;
	public String sessionClasspath = "G:\\REQUBEWeb6.0\\reqube2006\\defaultRoot\\WEB-INF\\classes;G:\\REQUBEWeb6.0\\reqube2006\\lib\\log4j-1.2.8.jar";
//	public String scriptExePath = "G:\\REQUBEWeb6.0\\reqube2006\\lib\\RQSvrScript.exe";
	public String schedulerprop = "";
	public int sessionServerPort = 7005;
	public Process sessionProcess = null;
	public int serverType = 2;
	public String scriptExePath ="";
	
	
	//ResultSet fetchSize
	public int RS_fetchSize = 0;

	//Scheduler properties
	public String useSchedule = "";
	
	//RQStatistics properties
	public String useRQStatistics = "";
	
	//document history properties
	public String rqxhistory = "no";
	public int rqxhistory_count = 10;
	public String rqxfile_backup_dir = "";

	//document list count
	public int rqreport_document_list_count = 10;
	
	// RDBMS name 
	public String rqreport_rdbms_name = "";
	
	// oracle jdbc driver type
	public String rqreport_oracle_jdbcDriver_type = "oraclex86";
	
	// transaction
	public String rqreport_transaction_use = "yes";
	
	//charset properties
	public String rqreport_server_RQcharset = "EUC-KR";
	public String rqreport_server_charset = "8859_1";
	
	//datase_buffer_use
	public String rqreport_dataset_buffer_use = "yes";
	
	//dataset null check
	public String rqreport_dataset_null_check = "no";
	
	//dataset charset change 
	public String rqreport_dataset_charset_change = "no";
	public String rqreport_dataset_charset_change_from ="8859_1";                
	public String rqreport_dataset_charset_change_to = "KSC5601";
	
	// eliminate query comment
	public String rqreport_query_comment_delete = "no";
	
	// Resource encoding converting
	public String resource_from = "8859_1";
	public String resource_to   = "8859_1";
	
	// SSO
	public String SSOUse = "no";
	public String SSOEss = "no";
	
	//log manager properties
	public String rqreport_server_loggingtype = "text";
	public int rqreport_server_logintervalTime = 4000;
	public int rqreport_server_logbuffersize = 1;

	//log4j.properties path
	public String log4jconfigpath = "";
	public String logrqdirname = "";
	public String logrqfilename = "";

	public FtpSyncEnv backUpEnv = new FtpSyncEnv();
	//REQUBE Version
	public String rqreport_server_version = "6.0";
	protected static Logger L = Logger.getLogger("RQENV");

	// REQUBE Locale (default:ko)
	public String rqreport_server_locale="ko";

	// document compression option (zip)
	public String document_zip_option;
	public String document_zip_level;
	public String document_zip_size;

	// document cache option
	public String rqreport_document_cache;
	
	// RQExportCmd.bat file location
	public String ntcliDmloc = "";
	
	// js framework name
	public String jsframework = "";
	//repository Environment variable
//	public String repository_path = null;
//	public String repository_jdbcDriver = null;
//	public String repository_connStr = null;
//	public String repository_dbid = null;
//	public String repository_dbpw = null;
//	public String repository_docConnClass = null;
//	public String repository_docDSConnClass = null;
//	public String repository_docTablename = null;
//	public String repository_docDSTablename = null;
//
	//Environment setting
	
	// site issue (EMS logging)
	public String site_ems_logging = "";
	
	static {
		//Environment tenv = getInstance();
		configfile = System.getProperty(PROPERTIES_NAME);
		if (configfile == null) {

		    // 여기는 시스템 프라퍼티에 값이 설정되어 있지 않을 경우
		    // CLASSPATH에 지정된 디렉토리에서 값을 읽는다.
			URL url = Environment.class.getClassLoader().getResource(PROPERTIES_NAME);
			if(url != null)
			{
				try {
					configfile = URLDecoder.decode(url.getFile(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					//e.printStackTrace();
					configfile = url.getFile();
				}
			}
	    }

		L.debug("Property File Path:" + configfile);
//		L.info("Property File Path:" + configfile);
		pl = new PropertyLoader(configfile);
		// To find My Single Key

		mysinglekey = System.getProperty(MYSINGLEKEY);
		if (mysinglekey == null) {

		    // 여기는 시스템 프라퍼티에 값이 설정되어 있지 않을 경우
		    // CLASSPATH에 지정된 디렉토리에서 값을 읽는다.
			URL url = Environment.class.getClassLoader().getResource(MYSINGLEKEY);
			if(url != null)
			{
				try {
					mysinglekey = URLDecoder.decode(url.getFile(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					//e.printStackTrace();
					mysinglekey = url.getFile();
				}
			}
	    }
//		L.info("My Single Key:" + mysinglekey);
		L.debug("My Single Key:" + mysinglekey);
		load();

		//engine Environment
//		engine_SSEP = (char)pl.getInt("rqreport.engine.ssep");
//		engine_RSEP = (char)pl.getInt("rqreport.engine.rsep");
//		engine_CSEP = (char)pl.getInt("rqreport.engine.csep");
//		engine_cachesize = pl.getInt("rqreport.engine.cache_size");
//		engine_thread = pl.getInt("rqreport.engine.thread");

		//repository Environment
//		tenv.repository_path = pl.getString("rqreport.repository.path", "c:/samples");
//		repository_jdbcDriver = pl.getString("rqreport.repository.jdbcDriver", "oracle.jdbc.driver.OracleDriver;sun.jdbc.odbc.JdbcOdbcDriver");
//		repository_connStr = pl.getString("rqreport.repository.connStr",
//				"jdbc:oracle:thin:rqadmin_sun/easybase@70.7.101.214:1521:WORLDAV;jdbc:odbc:RQRepositoryDB");
//		repository_dbid = pl.getString("rqreport.repository.dbid");
//		repository_dbpw = pl.getString("rqreport.repository.dbpw");
//		repository_docConnClass = pl.getString("rqreport.repository.docConnectorClass","com.sds.reqube.repository.RQDocConnector");
//		repository_docDSConnClass = pl.getString("rqreport.repository.docDSConnectorClass","com.sds.reqube.repository.RQDSListConnector");
//		repository_docTablename = pl.getString("rqreport.repository.docTableName","RQDoc");
//		repository_docDSTablename = pl.getString("rqreport.repository.doc_dsTableName","RQDocDs");

	}

	public static boolean load()
	{
		long modified = 0;
		if(configfile != null && configfile.length() > 0)
			modified = (new File(configfile)).lastModified();
		if(lastRead == 0 || modified != lastRead )
		{

			Environment tenv = getInstance();
			RepositoryEnv repEnv = RepositoryEnv.getInstance();
			tenv.engine_SSEP = (char)pl.getInt("rqreport.engine.ssep", 5);
			tenv.engine_RSEP = (char)pl.getInt("rqreport.engine.rsep", 9);
			tenv.engine_CSEP = (char)pl.getInt("rqreport.engine.csep", 10);
			tenv.engine_cachesize = pl.getInt("rqreport.engine.cache_size", 20);
			tenv.engine_thread = pl.getInt("rqreport.engine.thread", 20);
			tenv.engine_time_wait = pl.getInt("rqreport.engine.time_wait", 600);
			tenv.engine_timeout = pl.getInt("rqreport.engine.timeout", 600);
			tenv.filelimit = pl.getInt("rqreport.common.uploadPrc.filelimit", 60);

			tenv.sessionServerPort = pl.getInt("rqreport.rqsession.port",7005);
			tenv.sessionClasspath = pl.getString("rqreport.rqsession.classpath", "G:\\REQUBEWeb6.0\\reqube2006\\classes;G:\\REQUBEWeb6.0\\reqube2006\\lib\\log4j-1.2.8.jar");
			tenv.useEncryptData = pl.getString("rqreport.security.encryptData","false").equalsIgnoreCase("true");
			tenv.serverType = pl.getInt("rqreport.server.type",2); //weblogic=1, tomcat=2, sun-one,jeus=3

			// Statement FetchSize
			tenv.RS_fetchSize = pl.getInt("rqreport.RS.fetchSize",0);
			tenv.schedulerprop = pl.getString("rqscheduler.properties","");
			tenv.scriptExePath = pl.getString("rqreport.scriptexe","");

			//use Schedule module
			tenv.useSchedule = pl.getString("rqreport.server.extraModule1","no");
			
			//use Statistics module
			tenv.useRQStatistics = pl.getString("rqreport.server.extraModule2","no");
			
			//rqxhistory
			tenv.rqxhistory = pl.getString("rqreport.server.rqxhistory","no");
			tenv.rqxhistory_count = pl.getInt("rqreport.server.rqxhistory.count", 10);
			tenv.rqxfile_backup_dir = pl.getString("rqreport.server.rqxfile.backup.dir","");

			//document list count
			tenv.rqreport_document_list_count = pl.getInt("rqreport.document.list",10);
			
			//RDBMS Name 
			tenv.rqreport_rdbms_name = pl.getString("rqreport.rdbms.name","oracle"); 
				
			//oracle jdbc driver type
			tenv.rqreport_oracle_jdbcDriver_type = pl.getString("rqreport.oracle.jdbcDriver.type", "oraclex86");
			
			//transaction use
			tenv.rqreport_transaction_use = pl.getString("rqreport.transaction.use","yes");
			
			//char set
			tenv.rqreport_server_RQcharset = pl.getString("rqreport.server.RQcharset", "EUC-KR");
			tenv.rqreport_server_charset = pl.getString("rqreport.server.charset","8859_1");
		
			//dataset_buffer_use
			tenv.rqreport_dataset_buffer_use = pl.getString("rqreport.dataset.buffer.use","yes");
			
			//dataset null check
			tenv.rqreport_dataset_null_check = pl.getString("rqreport.dataset.null.check", "no");
			
			//dataset charset change 
			tenv.rqreport_dataset_charset_change = pl.getString("rqreport.dataset.charset.change", "no");
			tenv.rqreport_dataset_charset_change_from = pl.getString("rqreport.dataset.charset.change.from", "8859_1");
			tenv.rqreport_dataset_charset_change_to = pl.getString("rqreport.dataset.charset.change.to", "KSC5601"); 
			
			// delete query comment 
			tenv.rqreport_query_comment_delete = pl.getString("rqreport.query.comment.delete","no");
			
			//resource converting
			tenv.resource_from = pl.getString("resource_from", "8859_1");
			tenv.resource_to   = pl.getString("resource_to"  , "8859_1");
			
			// SSO
			tenv.SSOUse = pl.getString("rqreport.server.SSO", "no");
			tenv.SSOEss   = pl.getString("rqreport.server.SSO.Essential"  , "no");
			
			//log manager
			tenv.rqreport_server_loggingtype = pl.getString("rqreport.server.loggingtype", "text");
			tenv.rqreport_server_logintervalTime = pl.getInt("rqreport.server.logintervalTime", 4000);
			tenv.rqreport_server_logbuffersize = pl.getInt("rqreport.server.logbuffersize", 1);

			//log4j.properties path
			tenv.log4jconfigpath = pl.getString("rqreport.server.log.log4jconfigpath","");
			tenv.logrqdirname = pl.getString("rqreport.server.log.rqdirname","");
			tenv.logrqfilename = pl.getString("rqreport.server.log.rqfilename","");
			
			//REQUBE version
			tenv.rqreport_server_version = pl.getString("rqreport.server.version","6.0");

			// rqreport locale
			tenv.rqreport_server_locale = pl.getString("rqreport.server.locale", "ko");

			// document compression option (zip)
			tenv.document_zip_option = pl.getString("rqreport.zip.option", "no");
			tenv.document_zip_level  = pl.getString("rqreport.zip.level", "6");
			tenv.document_zip_size   = pl.getString("rqreport.zip.size", "500");

			// document cache option
			tenv.rqreport_document_cache = pl.getString("rqreport.document.cache","no");
			
			// RQExportCmd.bat file location
			tenv.ntcliDmloc = pl.getString("rqreport.ntclientDaemon.location", "");
			
			// js framework
			tenv.jsframework = pl.getString("rqreport.jsframework.name","jquery-1.3.2.min.js");
			
			// site issue (EMS logging)
			tenv.site_ems_logging = pl.getString("rqreport.site.issue.EMS.logging", "");
			
			//repository Environment
			repEnv.repositoryRoot = pl.getString("rqreport.repository.path", "c:/samples");
			repEnv.jdbcDriver = pl.getString("rqreport.repository.jdbcDriver", "oracle.jdbc.driver.OracleDriver");
			repEnv.connStr = pl.getString("rqreport.repository.connStr",
					"jdbc:oracle:thin:rqadmin_sun/easybase@70.7.101.214:1521:WORLDAV");
			repEnv.dbid = pl.getString("rqreport.repository.dbid");
			repEnv.dbpw = pl.getString("rqreport.repository.dbpw");
			repEnv.docConnectorClass = pl.getString("rqreport.repository.docConnectorClass","com.sds.rqreport.repository.RQDocConnector");
			repEnv.docDSConnectorClass = pl.getString("rqreport.repository.docDSConnectorClass","com.sds.rqreport.repository.RQDSListConnector");
			repEnv.docTableName = pl.getString("rqreport.repository.docTableName","RQDoc");
			repEnv.doc_dsTableName = pl.getString("rqreport.repository.doc_dsTableName","RQDocDs");
			repEnv.doc_userTableName = pl.getString("rqreport.repository.userTableName","RQUser");
			repEnv.doc_sqlTableName = pl.getString("rqreport.repository.sqlTableName","RQSQL");
			repEnv.doc_DocStatTableName = pl.getString("rqreport.repository.docStatTableName","RQDOCSTAT");
			repEnv.jndiName = pl.getString("rqreport.repository.jndi","");
						if(configfile != null && configfile.length() > 0)
				lastRead = (new File(configfile)).lastModified();
			repEnv.installid = pl.getString("rqreport.repository.install_id","");
			repEnv.installpw = pl.getString("rqreport.repository.install_pw","");
			repEnv.docSynchronizerClass = pl.getString("rqreport.repository.docsyncclass","");
			// Read ftp
			String ipServers = pl.getString("reqube.repository.sync.serveriplist");
			ArrayList serverArr = makeArrayList(ipServers);
			String portServers = pl.getString("reqube.repository.sync.portlist");
			ArrayList portArr = makeArrayList(portServers);
			String ftpRoots = pl.getString("reqube.repository.sync.ftp.rootdirlist");
			ArrayList ftpRootArr = makeArrayList(ftpRoots);
			String ftpids = pl.getString("reqube.repository.sync.ftp.idlist");
			ArrayList ftpidArr = makeArrayList(ftpids);
			String ftppws = pl.getString("reqube.repository.sync.ftp.pwlist");
			ArrayList ftppwArr = makeArrayList(ftppws);
			String backupRoots = pl.getString("reqube.repository.sync.ftp.backupdirlist");
			ArrayList backupRootList = makeArrayList(backupRoots);
			if(serverArr != null && serverArr.size() > 0)
			{
				int otherservercount = serverArr.size();
				repEnv.ftpServerList = new String[otherservercount];
				repEnv.portList = new int[otherservercount];
				repEnv.ftpRoot = new String[otherservercount];
				repEnv.userlist = new String[otherservercount];
				repEnv.passlist = new String[otherservercount];
				tenv.backUpEnv.ftpRoot = new String[otherservercount];

				// fill array
				String rootdir = "/";
				String id = "";
				String pw = "";
				String backupdir = "/backup/";
				int defaultport = 21;
				for(int i = 0; i < otherservercount; ++i)
				{
					repEnv.ftpServerList[i] = (String)serverArr.get(i);

					if( portArr.size() > i)
					{
						defaultport = Integer.parseInt((String)portArr.get(i));
						repEnv.portList[i] = defaultport;
					}
					else
					{
						repEnv.portList[i] = defaultport;
					}

					if( ftpRootArr.size() > i)
					{
						rootdir = (String)ftpRootArr.get(i);
						repEnv.ftpRoot[i] = rootdir;
					}
					else
					{
						repEnv.ftpRoot[i] = rootdir;
					}

					if( ftpidArr.size() > i)
					{
						id = (String)ftpidArr.get(i);
						repEnv.userlist[i] = id;
					}
					else
					{
						repEnv.userlist[i] = id;
					}


					if( ftppwArr.size() > i)
					{
						pw = (String)ftppwArr.get(i);
						repEnv.passlist[i] = pw;
					}
					else
					{
						repEnv.passlist[i] = pw;
					}

					if( backupRootList.size() > i)
					{
						backupdir = (String)backupRootList.get(i);
						tenv.backUpEnv.ftpRoot[i] = backupdir;
					}
					else
					{
						tenv.backUpEnv.ftpRoot[i] = backupdir;
					}

				}
				tenv.backUpEnv.userlist = repEnv.userlist;
				tenv.backUpEnv.passlist = repEnv.passlist;
				tenv.backUpEnv.portList = repEnv.portList;
				tenv.backUpEnv.serverList = repEnv.ftpServerList;
			}

		}
		return true;
	}

	static public ArrayList makeArrayList(String val)
	{
		ArrayList ret = new ArrayList();
		  String tmpName = val;
		  int ndelStr = 0;// = tmpName.indexOf(';');
		  int nend = 0;
		  String tmpStr = "";
		  while((nend=tmpName.indexOf(';',ndelStr)) >= 0)
		  {
			tmpStr = tmpName.substring(ndelStr,nend);
			ret.add(tmpStr);
			ndelStr = nend+1;
		  }
		  tmpStr = tmpName.substring(ndelStr);
		  if(tmpStr.length() > 0)
			  ret.add(tmpStr);
//		  if(ret.isEmpty())
//			  ret.add("127.0.0.1");
		  return ret;
	}
	public static String getDefaultEnc()
	{
		try
		{
			OutputStream outputStream = new FileOutputStream("tempfile");
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			String enc = writer.getEncoding();
			outputStream.close();
			File f = new File("tempfile");
			f.delete();

			return enc;
		}catch(Exception ex)
		{
			return "8859_1";
		}
	}
	public static PropertyLoader getPropertyLoader()
	{
		return pl;
	}
	public static synchronized Process getSessionServerProc()
	{
		env = Environment.getInstance();
		return env.sessionProcess;
	}

	public static synchronized boolean setSessionServerProc(Process p)
	{
		env = Environment.getInstance();
		if(env.sessionProcess == null)
			env.sessionProcess = p;
		else
		{
			return false;
		}
		return true;
	}

	public static synchronized void releaseServerProc()
	{
		env.sessionProcess = null;
	}

	/**
	 * rqreport.properties 에 log4j.properties 파일 경로가 있으면
	 * 그경로에 설정된 log4j.properties 파일을 읽어오기위한 메서드.
	 * @param loggername
	 * @return
	 */
	public static final Logger getLog(String loggername){
		Logger logger = null;

		Environment tenv = getInstance();
		String log4jpath = tenv.log4jconfigpath;

		if(!log4jpath.equals("")){
			PropertyConfigurator.configure(log4jpath);
		}

		logger = Logger.getLogger(loggername);
		return logger;
	}
}
