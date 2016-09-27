package com.sds.acube.ndisc.mts.common;

import java.net.ServerSocket;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.process.iface.ProcessIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

/**
 * @author ky.cho
 * 
 */
public class NDCommon extends NDConstant {
	public static Selector selector = null;

	public ServerSocketChannel serverSocketChannel = null;

	public ServerSocket serverSocket = null;

	public static LoggerIF logger = null;

	public static StorageIF storage = null;

	public static ProcessIF procInit = null;

	public static ProcessIF procFileReg = null;

	public static ProcessIF procFileGet = null;

	public static ProcessIF procFileRep = null;

	public static ProcessIF procFileDel = null;

	public static ProcessIF procFileCpy = null;

	public static ProcessIF procFileMov = null;

	public static ProcessIF procFileInfo = null;

	public static ProcessIF procVolInfo = null;

	public static ProcessIF procMkVolume = null;

	public static ProcessIF procMkMedia = null;

	public static ProcessIF procGetConf = null;

	public static FilterIF filterEnc = null;

	public static FilterIF filterComp = null;
	
	//public static HashMap room = new HashMap();
	
	public static Map<SocketChannel, JobVO>  room =  Collections.synchronizedMap(new HashMap<SocketChannel, JobVO>());

	public static DaoManager daoManager = null;

	public static final Configuration configuration = ConfigurationManager.getConfiguration();

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// BELOW : LOADED FROM NDiscServer Configurtion
	// //////////////////////////////////////////////////////////////////////////////////////////////

	public static String HOST = null;

	public static int PORT = -1;

	public static String JDBC_DRIVER = null;

	public static String JDBC_URL = null;

	public static String JDBC_USERNAME = null;

	public static String JDBC_PASSWORD = null;

	// LOGGER
	public static String MTS_LOGGER = null;

	// ENCRYPT/DECRYPT FILTER
	public static String FILTER_ENC = null;

	// COMPRESS/DECOMPRESS FILTER
	public static String FILTER_COMP = null;

	// STORAGE
	public static String SCHEMA_TYPE = null;
	public static String MTS_STORAGE = null;
	
	// PROCESS
	public static String PROC_INIT = null;

	public static String PROC_FILEREG = null;

	public static String PROC_FILEGET = null;

	public static String PROC_FILEREP = null;

	public static String PROC_FILEDEL = null;

	public static String PROC_FILECPY = null;

	public static String PROC_FILEMOV = null;

	public static String PROC_FILEINFO = null;

	public static String PROC_VOLINFO = null;

	public static String PROC_MKVOLUME = null;

	public static String PROC_MKMEDIA = null;

	public static String PROC_GETCONF = null;

	// NDISC TMP DIR
	public static String NDISC_TMP_DIR = null;

	// NDISC LOG DIR
	public static String NDISC_LOG_DIR = null;

	// NDISC CIPHER KEY FILE
	public static String NDISC_CIPHER_KEY = null;

	public static String SO_TRANS_TUNE_APPLY = null;
	
	public static int SO_TRANS_SLEEP = -1;
	
	public static int SO_TRANS_CHECK_ITER = -1;
	
	public static int SO_PROCINIT_READ_CNT = -1;

	// CONNECTION FAILOVER
	public static String CON_FAILOVER_APPLY = null;
	public static String CON_FAILOVER_TARGET = null;
	
	public static boolean CON_FAILOVER_STATE = false;

	public static String HOST_FO = null;
	public static int PORT_FO = -1;
	
	//NDISC LICENSE KEY
	public static String LICENSE_KEY = null;
	public static int CURRENT_FILE_OPERATION_CNT = 0;
	
	// STORAGE USAGE
	public static int STOR_STORAGE_USAGE = 99;
	
	public static int WORKER_THREAD_COUNT=0;
	
	public static final String NDISC_TYPE = "ndisc";
	
	public static final String STORSERV_TYPE = "storserv";	
	
	// loaded from config.xml
	static {
		HOST = configuration.getProperty("host", "localhost", "server");
		PORT = configuration.getIntProperty("port", 7404, "server");
		WORKER_THREAD_COUNT = configuration.getIntProperty("worker_thread_count", 1, "server");

		JDBC_DRIVER = configuration.getProperty("DRIVER", null, "JDBC");
		JDBC_URL = configuration.getProperty("URL", null, "JDBC");
		JDBC_USERNAME = configuration.getProperty("USERNAME", "nd_user", "JDBC");
		JDBC_PASSWORD = configuration.getProperty("PASSWORD", "nd000", "JDBC");

		MTS_LOGGER = configuration.getProperty("impl_class_logger", null, "logger");

		FILTER_COMP = configuration.getProperty("impl_class_comp", null, "inner_filter");
		FILTER_ENC = configuration.getProperty("impl_class_enc", null, "inner_filter");

		/**
		 * 2014.09.22
		 * SCHEMA TYPE에 따라 구현 클래스를 지정<br>
		 * 현재 NDISC와 STOR SERVER용 SCHEMA 가 틀림.<br>
		 * 따라서, 해당 구현체를 별도로 구현해야함.<br>
		 * NDISC와 STOR SERVER는 패키지 차원에서 제공해줌.<br>
		 */
		SCHEMA_TYPE = configuration.getProperty("schema_type", NDISC_TYPE, "storage").toLowerCase();
		if (SCHEMA_TYPE.equals(NDISC_TYPE)) {
			MTS_STORAGE = "com.sds.acube.ndisc.mts.storage.impl.DefStorage";	//	configuration.getProperty("impl_class_storage", null, "storage");
		} else if (SCHEMA_TYPE.equals(STORSERV_TYPE)) {
			MTS_STORAGE = "com.sds.acube.ndisc.mts.storage.impl.StorServStorage";	//	configuration.getProperty("impl_class_storage", null, "storage");
		} else {
			MTS_STORAGE = configuration.getProperty("impl_class_storage", null, "storage");
		}		
		

		PROC_INIT = configuration.getProperty("impl_class_init", null, "process");
		PROC_FILEREG = configuration.getProperty("impl_class_filereg", null, "process");
		PROC_FILEGET = configuration.getProperty("impl_class_fileget", null, "process");
		PROC_FILEREP = configuration.getProperty("impl_class_filerep", null, "process");
		PROC_FILEDEL = configuration.getProperty("impl_class_filedel", null, "process");
		PROC_FILECPY = configuration.getProperty("impl_class_filecpy", null, "process");
		PROC_FILEMOV = configuration.getProperty("impl_class_filemov", null, "process");
		PROC_FILEINFO = configuration.getProperty("impl_class_fileinfo", null, "process");
		PROC_VOLINFO = configuration.getProperty("impl_class_volinfo", null, "process");
		PROC_MKVOLUME = configuration.getProperty("impl_class_mkvolume", null, "process");
		PROC_MKMEDIA = configuration.getProperty("impl_class_mkmedia", null, "process");
		PROC_GETCONF = configuration.getProperty("impl_class_getconf", null, "process");

		NDISC_TMP_DIR = configuration.getProperty("tmp_dir", null);

		NDISC_LOG_DIR = configuration.getProperty("log_dir", null);

		NDISC_CIPHER_KEY = configuration.getProperty("cipher_key", null);

		SO_TRANS_TUNE_APPLY = configuration.getProperty("so_trans_tune_apply", "N", "tuning_point");
		SO_TRANS_SLEEP = Integer.parseInt(configuration.getProperty("so_trans_sleep", "1", "tuning_point"));
		SO_TRANS_CHECK_ITER = Integer.parseInt(configuration.getProperty("so_trans_check_iter", "10", "tuning_point"));
		SO_PROCINIT_READ_CNT = Integer.parseInt(configuration.getProperty("so_procinit_read_cnt", "5", "tuning_point"));

		CON_FAILOVER_APPLY = configuration.getProperty("con_failover_apply", "N", "connection_failover");
		CON_FAILOVER_TARGET = configuration.getProperty("con_failover_target", "", "connection_failover");
		
		LICENSE_KEY = configuration.getProperty("license_key", "", "license");
		
		
		
		
	}
}
