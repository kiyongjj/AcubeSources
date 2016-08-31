package com.sds.acube.jstor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;
import java.util.StringTokenizer;

import com.sds.acube.cache.CacheConfig;
import com.sds.acube.cache.CacheUtil;
import com.sds.acube.cache.iface.ICache;
import com.sds.acube.ndisc.mts.util.timer.TimeChecker;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sds.acube.jstor.LogFactory;
//import org.apache.log4j.Logger;

import jsftpnative.*;

public class JSTORNative extends JSTORApi {

	// cache manager
	private static ICache cacheService = null;

	private static boolean IsCacheUse = false; // 저장서버 cache 사용여부

	private static String IPADDR = null;

	private static int PORTNO = -1;

	private static boolean CACHE_DETECTED = false;

	private static String[] CLUSTER_INFO = null;

	private static JSFTPNative jSFTP = null;
	
	//private static boolean m_bStorConnected = false;
	private boolean m_bStorConnected = false;
	
	//private static Logger logger = null;

	static {
		IsCacheUse = ("Y".equalsIgnoreCase(System
				.getProperty("jstor_cache_use"))) ? true : false;

		if (IsCacheUse) {
			cacheService = CacheConfig.getService();

			Configuration configuration = ConfigurationManager.getConfiguration();
			CLUSTER_INFO = configuration.getArray("cluster_info", null,"cache");

			jSFTP = new JSFTPNative();
		}

		// 2012.07.25 주석처리(inyong.jang)
		/*String strApiType = System.getProperty("jstor_api_type");
		String strSvrType = System.getProperty("jstor_svr_type");*/
		
		//logger = Logger.getLogger(JSTORNative.class);
		logger = LogFactory.getLogger("jstorapi");
		
		// 2012.07.25 주석처리(inyong.jang)
		/*if (null == strSvrType) {
			strSvrType = "unix";
		}*/

		// 2012.07.25 주석처리(inyong.jang)
		//if ("pure_java".equals(strApiType.toLowerCase())
		//		&& "unix".equals(strSvrType.toLowerCase())) {
		String strVersion = "PureJavaApi-UnixSvr(2009-04-23)";			
		
		logger.info("-------------------------------------------------- \n" +
				"▶ Company :  SAMSUNG SDS \n" + 
				"▶ Product Name : JSTOR API \n" + 
				"▶ Version : " + strVersion + " \n" +					
				"--------------------------------------------------");	

		System.setProperty("jstor_api_version", strVersion);
		//}
	}

	public JSTORNative() {
		/* 기존의 JSTORDebug를 사용하는 형태에서 log4j 의 logger 를 사용하는 형태로 변경
		String strTraceType = null;

		strTraceType = System.getProperty("jstor_trace_type");

		if (null != strTraceType) {
			// TRACE MODE SETTING
			if ("console".equalsIgnoreCase(strTraceType)) {
				JSTORDebug.setDebugMode(true, JSTORDebug.TRACE_CONSOLE, null);
			} else if ("file".equalsIgnoreCase(strTraceType)) {
				JSTORDebug.setDebugMode(true, JSTORDebug.TRACE_FILE, null);
			} else if ("both".equalsIgnoreCase(strTraceType)) {
				JSTORDebug.setDebugMode(true, JSTORDebug.TRACE_BOTH, null);
			}
		}
		*/
	}

	protected void setError(int nErrCode, String sErrMsg) {
		m_nErrCode = nErrCode;

		if (null != sErrMsg)
			sErrMsg = sErrMsg.trim();

		m_sErrMsg = sErrMsg;
	}

	public String[] JSTOR_getRegFileID() {
		for (int i = 0; i < m_sRegFileIDArr.length; i++) {
			if (null != m_sRegFileIDArr[i])
				m_sRegFileIDArr[i] = m_sRegFileIDArr[i].trim();
		}

		return m_sRegFileIDArr;
	}

	public String[] JSTOR_getNewCpyFileID() {
		for (int i = 0; i < m_sNewCpyFileIDArr.length; i++) {
			if (null != m_sNewCpyFileIDArr[i])
				m_sNewCpyFileIDArr[i] = m_sNewCpyFileIDArr[i].trim();
		}

		return m_sNewCpyFileIDArr;
	}

	public String[][] JSTOR_getVolInfo() {
		for (int i = 0; i < m_sVolInfoArr.length; i++) {
			for (int j = 0; j < m_sVolInfoArr[i].length; j++) {
				if (null != m_sVolInfoArr[i][j])
					m_sVolInfoArr[i][j] = m_sVolInfoArr[i][j].trim();
			}
		}

		return m_sVolInfoArr;
	}

	public String[] JSTOR_getOutDrmFilePath() {
		if (null != m_sOutDrmFilePath) {
			for (int i = 0; i < m_sOutDrmFilePath.length; i++) {
				if (null != m_sOutDrmFilePath[i]) {
					m_sOutDrmFilePath[i] = m_sOutDrmFilePath[i].trim();
				}
			}
		}

		return m_sOutDrmFilePath;
	}

	public int JSTOR_getErrCode() {
		return m_nErrCode;
	}
 
	public String JSTOR_getErrMsg() {
		if (null != m_sErrMsg)
			m_sErrMsg = m_sErrMsg.trim();

		return m_sErrMsg;
	}

	public int JSTOR_Connect(String sIPAddr, int nPortNo) {

		int nRet = -1;

		if(IsCacheUse)
		{
			IPADDR = sIPAddr;
			PORTNO = nPortNo;
			nRet = 99999;
		}
		else
		{
			try {
				if (null != sIPAddr)
					sIPAddr = sIPAddr.trim();

				m_socket = new Socket(sIPAddr, nPortNo);

				// m_socket.setReceiveBufferSize (4096);

				m_buffSock_In = new BufferedInputStream(m_socket.getInputStream());
				m_buffSock_Out = new BufferedOutputStream(m_socket
						.getOutputStream());

				m_dataSock_In = new DataInputStream(m_socket.getInputStream());
				m_dataSock_Out = new DataOutputStream(m_socket.getOutputStream());

				// 이전 버전에서의 커넥션 아이디 개념을 맞추기 위해,
				// 로컬포트넘버를 커넥션 아이디로 반환함
				// 실제 의미는 없음
				nRet = m_socket.getLocalPort();
				//저장서버와 연결 상태 인지 여부 저장 
				m_bStorConnected = true;
				
				logger.debug("JSTOR_Connect() Success : nRet(nConnID) = " + nRet);
				
			} catch (Exception e) {
				e.printStackTrace();
				
				logger.error("JSTOR_Connect() Fail : " + e.getMessage());
				setError(-999, e.getMessage());
				try {
					m_socket.close();
				} catch (Exception ex) {
					logger.error("JSTOR_Connect() Fail : " + ex.getMessage());
				}
				nRet = JSTOR_ERR_RET;
			}
		}

		return nRet;
	}

	private int JSTOR_Connect()
	{
		int nRet = -1;

		try {
			if (null != IPADDR)
				IPADDR = IPADDR.trim();

			m_socket = new Socket(IPADDR, PORTNO);

			// m_socket.setReceiveBufferSize (4096);

			m_buffSock_In = new BufferedInputStream(m_socket.getInputStream());
			m_buffSock_Out = new BufferedOutputStream(m_socket
					.getOutputStream());

			m_dataSock_In = new DataInputStream(m_socket.getInputStream());
			m_dataSock_Out = new DataOutputStream(m_socket.getOutputStream());

			// 이전 버전에서의 커넥션 아이디 개념을 맞추기 위해,
			// 로컬포트넘버를 커넥션 아이디로 반환함
			// 실제 의미는 없음
			nRet = m_socket.getLocalPort();
			
			//저장서버와 연결 상태 인지 여부 저장 
			this.m_bStorConnected = true;
			logger.debug("JSTOR_Connect() Success : nRet(nConnID) = " + nRet);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("JSTOR_Connect() Fail : " + e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public void JSTOR_Disconnect(int nConnID) {

		//if (!(IsCacheUse && CACHE_DETECTED))
		if(m_bStorConnected || nConnID != 99999)
		{
			try {
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_QUIT, SH_SVCOPT_NONE, null,
						m_dataSock_Out);

				if (null != m_socket) {
					try {
						m_buffSock_In.close();
						m_buffSock_Out.close();

						m_dataSock_In.close();
						m_dataSock_Out.close();

						m_socket.close();
						
						//저장서버와의 연결 상태 저장
						m_bStorConnected = false;
						logger.debug("JSTOR_Disconnect() Success");
					} catch (Exception ex) {
						ex.printStackTrace();
						logger.error("ConnID : "+nConnID+" JSTOR_Disconnect() Fail (In Socket Close) :"+ ex.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("JSTOR_Disconnect() Fail : "+e.getMessage());
			}
		}
	}

	public int JSTOR_Commit(int nConnID) {
		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {
			// send Service Header
			send_ServiceHeader(nConnID, SH_SVCCODE_TRANS, SH_SVCOPT_COMMIT,
					null, m_dataSock_Out);

			// recv_ReplyHeader
			arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

			nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
			nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

			// recv_ReplyMsg
			strRecvMsg = recv_ReplyMsg(nConnID, nRecvStatus, m_dataSock_In);

			// 성공일 경우
			if (JSTOR_SUCCESS_RET == nRecvDataSize) {
				nRet = JSTOR_SUCCESS_RET;
				logger.debug("JSTOR_Commit() Success");
			}
			// 실패일 경우, 에러 메세지를 반환
			else {
				setError(-1 * nRecvDataSize, strRecvMsg);
				nRet = JSTOR_ERR_RET;
				logger.debug("JSTOR_Commit() fail : " + JSTOR_ERR_RET);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + " JSTOR_Commit() Fail : " + e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int JSTOR_Rollback(int nConnID) {
		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {
			// send Service Header
			send_ServiceHeader(nConnID, SH_SVCCODE_TRANS, SH_SVCOPT_ROLLBACK,
					null, m_dataSock_Out);

			// recv_ReplyHeader
			arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

			nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
			nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

			// recv_ReplyMsg
			strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize, m_dataSock_In);

			// 성공일 경우
			if (JSTOR_SUCCESS_RET == nRecvStatus) {
				nRet = JSTOR_SUCCESS_RET;
				logger.debug("JSTOR_Rollback() success");
			}
			// 실패일 경우, 에러 메세지를 반환
			else {
				setError(-1 * nRecvStatus, strRecvMsg);
				nRet = JSTOR_ERR_RET;
				logger.debug("JSTOR_Rollback() fail : " + JSTOR_ERR_RET);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + " JSTOR_Rollback() Fail : " + e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int JSTOR_FileReg(int nConnID, int nNumOfFile,
			String[][] sInfoRegArr, int nOption) {

logger.debug("JSTOR_FileReg started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");		
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		Properties propFile = null;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {
			for (int i = 0; i < sInfoRegArr.length; i++) {
				for (int j = 0; j < sInfoRegArr[i].length; j++) {
					if (null != sInfoRegArr[i][j])
						sInfoRegArr[i][j] = sInfoRegArr[i][j].trim();
				}
			}

			if (10 == nOption) {   // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if ("FASOO_V3.1".equalsIgnoreCase(m_drmType)) {   // 파수 DRM 일 경우
					m_fasooPackager = new FasooPackager();
					String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
					String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
					boolean bRet = false;
					for (int i = 0; i < sInfoRegArr.length; i++) {
						bRet = m_fasooPackager.isFasooPackageFile(sInfoRegArr[i][0]);
						if (bRet) {
							bRet = m_fasooPackager.doFasooPacakgeFileExtract(sInfoRegArr[i][0],
									fsdHomeDir, fsdServerID);
						}
					}
//				} else if ("SOFTCAMP".equalsIgnoreCase(m_drmType)) {   // SOFTCAMP DRM 일 경우
				} else if (m_drmType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우
					m_softCampPackager = new SoftCampPackager();
					String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
					String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
					String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");

					if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
					{
						m_softCampPackager.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
					}

					boolean bRet = false;
					for (int i = 0; i < sInfoRegArr.length; i++) {
						
						bRet = m_softCampPackager.doSoftCampFileExtract(sInfoRegArr[i][0], keyDir + File.separator + keyFile, privID);

						if (false == bRet)
						{
							throw new Exception("SCSL EXCEPTION(doSoftCampFileExtract)");
						}
					}
				}
			}

			m_sRegFileIDArr = new String[nNumOfFile];

			for (int i = 0; i < nNumOfFile; i++) {
				// 파일속성 가져오기
				propFile = getFileProperties(sInfoRegArr[i][SEQ_REGINFO_PATH]);

logger.debug("FILE_FULL_PATH : " + propFile.getProperty("FILE_FULL_PATH") + 
			 "FILE_SIZE : " + propFile.getProperty("FILE_SIZE") + 
			 "FILE_NAME : " + propFile.getProperty("FILE_NAME") +
			 "FILE_EXT : " + propFile.getProperty("FILE_EXT")); 
								
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_REG, SH_SVCOPT_NONE,
						propFile, m_dataSock_Out);
				
logger.debug("send_ServiceHeader() called");				

				// send Service Reg Info
				send_ServiceRegInfo(nConnID, propFile, sInfoRegArr[i],
						m_dataSock_Out);
logger.debug("send_ServiceRegInfo() called");

				// send File
				send_File(nConnID, propFile, m_buffSock_Out, m_dataSock_Out);
				
logger.debug("send_File() called");
				
				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);
				
logger.debug("recv_ReplyHeader() called");				
				
				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

logger.debug("nRecvStatus : " + nRecvStatus + " nRecvDataSize : " + nRecvDataSize);				
				// recv_ReplyMsg
				strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
						m_dataSock_In);
				
logger.debug("recv_ReplyMsg() called");
logger.debug("strRecvMsg = " + strRecvMsg);
		
				// 성공일 경우, 파일 아이디를 반환시킴
				if (JSTOR_SUCCESS_RET == nRecvStatus) {
					m_sRegFileIDArr[i] = strRecvMsg;
					nRet = JSTOR_SUCCESS_RET;
				}
				// 실패일 경우, 에러 메세지를 반환
				else {
					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
				}

				// putCache, FileReg
				if (IsCacheUse) {
					TimeChecker.setStartPoint();
					putCache(m_sRegFileIDArr[i],
							sInfoRegArr[i][SEQ_REGINFO_PATH]);
					logger.info("ConnID : "+nConnID+" REG, putCache() : "+ TimeChecker.getCurrentInterval());					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + "JSTOR_FileReg() Fail : " + e.getMessage());			
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}
logger.debug("JSTOR_FileReg Ended >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");			
		return nRet;
	}

	public int JSTOR_FileGet(int nConnID, int nNumOfFile,
			String[][] sInfoGetArr, int nGUIFlag) {

		logger.debug("JSTOR_FileGet() start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		int nRet = -1;
		int nFileSize = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		//boolean bStorConnected = false;
		CACHE_DETECTED = false;

		try {
			for (int i = 0; i < sInfoGetArr.length; i++) {
				for (int j = 0; j < sInfoGetArr[i].length; j++) {
					if (null != sInfoGetArr[i][j])
						sInfoGetArr[i][j] = sInfoGetArr[i][j].trim();
				}
			}	

			String CachePath = null;
			for (int i = 0; i < nNumOfFile; i++) {

				if (IsCacheUse) {
					// cache Manager를 통해 cache에 해당 파일이 있는지 확인하다
					TimeChecker.setStartPoint();
					CachePath = getCache(sInfoGetArr[i][SEQ_GETINFO_FILEID]);
					logger.info("ConnID : " + nConnID + " getCache() : " + TimeChecker.getCurrentInterval());

					// cache에서 찾은경우 저장서버에 연결하여 파일을 가져 오는 작업을 하지 않고 다음 이터레이션으로 넘어간다. continue; 
					if (CachePath != null) {
						logger.info("ConnID : " + nConnID + "\t [Cache Hit] Path : " + CachePath);

						CacheUtil.copyFile(CachePath,
								sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
						logger.info("ConnID : " + nConnID + "GET, copyFile() : " + TimeChecker.getCurrentInterval()); 

						// 함수반환값 전달
						nRet = JSTOR_SUCCESS_RET;

						CACHE_DETECTED = true;
						continue;
					}
					
					logger.debug("m_bStorConnected : " + m_bStorConnected);

					//if (!bStorConnected)
					if(!m_bStorConnected)
					{
						nConnID = JSTOR_Connect();
						if(nConnID < 0)
						{
							return JSTOR_ERR_RET;
						}

						//bStorConnected = true;
					}
				}
				
				//수정 2009.05.08
				//m_bStorConneced 값이 true 이지만, 저장서버와 Connection 이 맺어지지 않은 경우 (nConnID 값이 9999 일 때), 다시 저장서버와 연결한다. 
				if(nConnID == 99999){
					nConnID = JSTOR_Connect();
					if(nConnID < 0)
					{
						return JSTOR_ERR_RET;
					}															
				}
				
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_GET, SH_SVCOPT_NONE,
						null, m_dataSock_Out);

				// send Service Get Info
				send_ServiceGetInfo(nConnID, sInfoGetArr[i], m_dataSock_Out);

				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);
				
				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				logger.info("ConnID : " + nConnID + "after recv_ReplyHeader(), nRecvStatus = "	+ nRecvStatus);

				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);
				logger.info("ConnID : " + nConnID + "after recv_ReplyHeader(), nRecvDataSize = " + nRecvDataSize);

				// 실패일 경우, 에러 메세지를 반환, 서비스 Break
				if (JSTOR_SUCCESS_RET != nRecvStatus) {
					// recv_ReplyMsg
					strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
							m_dataSock_In);

					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
					break;
				}

				logger.info("ConnID : " + nConnID + "CHK POINT => before recv_File()");

				nFileSize = recv_ReplyGetInfo(nConnID, m_dataSock_In);
				logger.info("ConnID : " + nConnID + "call recv_ReplyGetInfo(), nFileSize = " + nFileSize);
				

				// receive File

				logger.info("ConnID : " + nConnID + "recv_File() will be called");
				
				recv_File(nConnID, sInfoGetArr[i][SEQ_GETINFO_FILEPATH],
						nFileSize, m_buffSock_In, m_dataSock_Out);

				// DownFile(sInfoGetArr[i][SEQ_GETINFO_FILEPATH], nFileSize,
				// m_buffSock_In, m_dataSock_Out);

				nRet = JSTOR_SUCCESS_RET;

				if (IsCacheUse) {
					// cache Manager를 통해 cache에 넣는다
					logger.info("ConnID : " + nConnID + "\t [Cache Miss] ID : "
							+ sInfoGetArr[i][SEQ_GETINFO_FILEID]);
					logger.info("ConnID : " + nConnID + "\t [Cache Miss] Path : "
							+ sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);

					putCache(sInfoGetArr[i][SEQ_GETINFO_FILEID],
							sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
					logger.info("ConnID : " + nConnID + " GET, putCache() : "
							+ TimeChecker.getCurrentInterval());
				
				}
				
				
			}
			logger.debug("JSTOR_FileGet() end  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + " JSTOR_FileGet() Fail : "					
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}
		return nRet;
	}

	public int JSTOR_FileRep(int nConnID, int nNumOfFile,
		String[][] sInfoRepArr, int nOption) {
		logger.debug("JSTOR_FileRep() start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		Properties propFile = null;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {
			for (int i = 0; i < sInfoRepArr.length; i++) {
				for (int j = 0; j < sInfoRepArr[i].length; j++) {
					if (null != sInfoRepArr[i][j])
						sInfoRepArr[i][j] = sInfoRepArr[i][j].trim();
				}
			}

			if (10 == nOption) {   // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if ("FASOO_V3.1".equalsIgnoreCase(m_drmType)) {   // 파수 DRM 일 경우
					m_fasooPackager = new FasooPackager();
					String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
					String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
					boolean bRet = false;
					for (int i = 0; i < sInfoRepArr.length; i++) {
						bRet = m_fasooPackager.isFasooPackageFile(sInfoRepArr[i][0]);
						if (bRet) {
							bRet = m_fasooPackager.doFasooPacakgeFileExtract(sInfoRepArr[i][0],
									fsdHomeDir, fsdServerID);
						}
					}
//				} else if ("SOFTCAMP".equalsIgnoreCase(m_drmType)) {   // SOFTCAMP DRM 일 경우
				} else if (m_drmType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우
					m_softCampPackager = new SoftCampPackager();

					String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
					String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
					String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");

					if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
					{
						m_softCampPackager.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
					}

					boolean bRet = false;
					for (int i = 0; i < sInfoRepArr.length; i++) {

						bRet = m_softCampPackager.doSoftCampFileExtract(sInfoRepArr[i][1], keyDir + File.separator + keyFile, privID);

						if (false == bRet)
						{
							throw new Exception("SCSL EXCEPTION(doSoftCampFileExtract)");
						}
					}
				}
			}

			for (int i = 0; i < nNumOfFile; i++) {
				// 파일속성 가져오기
				propFile = getFileProperties(sInfoRepArr[i][SEQ_REPINFO_FILEPATH]);

				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_REP, SH_SVCOPT_NONE,
						propFile, m_dataSock_Out);

				// send Service Rep Info
				send_ServiceRepInfo(nConnID, propFile, sInfoRepArr[i],
						m_dataSock_Out);

				// send File
				send_File(nConnID, propFile, m_buffSock_Out, m_dataSock_Out);

				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

				// 실패일 경우, 에러 메세지를 반환, 서비스 Break
				if (JSTOR_SUCCESS_RET != nRecvStatus) {
					// recv_ReplyMsg
					strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
							m_dataSock_In);

					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
					break;
				}

				nRet = JSTOR_SUCCESS_RET;

				if (IsCacheUse) {
					TimeChecker.setStartPoint();

					// cache에서 object 삭제한다
					removeCache(sInfoRepArr[i][SEQ_REPINFO_FILEID]);
					logger.info("ConnID : " + nConnID + " REP, removeCache() : "
							+ TimeChecker.getCurrentInterval());

					// cache에 object 다시 넣어준다
					putCache(sInfoRepArr[i][SEQ_REPINFO_FILEID],
							sInfoRepArr[i][SEQ_REPINFO_FILEPATH]);
					logger.info("ConnId : " + nConnID + "REP, putCache() : "
							+ TimeChecker.getCurrentInterval());

					// 클러스터에 등록된 정보를 삭제한다
					manageCacheCluster(sInfoRepArr[i][SEQ_REPINFO_FILEID]);
				}
			}
			
			logger.debug("JSTOR_FileRep() end  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + "JSTOR_FileRep() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int JSTOR_FileDel(int nConnID, int nNumOfFile, String[] sInfoDelArr) {
		logger.debug("JSTOR_FileDel() start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {

			for (int i = 0; i < sInfoDelArr.length; i++) {
				if (null != sInfoDelArr[i])
					sInfoDelArr[i] = sInfoDelArr[i].trim();
			}

			for (int i = 0; i < nNumOfFile; i++) {
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_DEL, SH_SVCOPT_NONE,
						null, m_dataSock_Out);

				// send Service Del Info
				send_ServiceDelInfo(nConnID, sInfoDelArr[i], m_dataSock_Out);

				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

				// 실패일 경우, 에러 메세지를 반환, 서비스 Break
				if (JSTOR_SUCCESS_RET != nRecvStatus) {
					// recv_ReplyMsg
					strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
							m_dataSock_In);

					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
					break;
				}

				nRet = JSTOR_SUCCESS_RET;

				if (IsCacheUse) {
					TimeChecker.setStartPoint();

					// cache에서 object 삭제한다
					removeCache(sInfoDelArr[i]);
					logger.info("ConnID : " + nConnID + " DEL, removeCache() : "
							+ TimeChecker.getCurrentInterval());

					// 클러스터에 등록된 정보를 삭제한다
					manageCacheCluster(sInfoDelArr[i]);
				}
			}
			logger.debug("JSTOR_FileDel() end  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + "JSTOR_FileDel() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int JSTOR_VolInfo(int nConnID) {
		logger.debug("JSTOR_VolInfo() start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;
		int nNumOfVol = -1;

		try {
			// send Service Header
			send_ServiceHeader(nConnID, SH_SVCCODE_VOLINFO, SH_SVCOPT_NONE,
					null, m_dataSock_Out);

			// recv_ReplyHeader
			arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

			nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
			logger.info("ConnID : " + nConnID +
					" after recv_ReplyHeader(), nRecvStatus = " + nRecvStatus);
			
			nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);
			logger.info("ConnID : " + nConnID +
					" after recv_ReplyHeader(), nRecvDataSize = "
							+ nRecvDataSize);

			// 실패일 경우, 에러 메세지를 반환, 서비스 Break
			if (JSTOR_SUCCESS_RET != nRecvStatus) {
				// recv_ReplyMsg
				strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
						m_dataSock_In);

				setError(-1 * nRecvStatus, strRecvMsg);
				nRet = JSTOR_ERR_RET;
			} else {
				// 저장서버에서의 볼륨 개수
				nNumOfVol = nRecvDataSize / SH_SIZEOF_INFO_VOL;
				m_sVolInfoArr = new String[nNumOfVol][SZ_VOL_INFO];
				for (int i = 0; i < nNumOfVol; i++) {
					m_sVolInfoArr[i] = recv_ReplyVolInfo(nConnID, m_dataSock_In);
				}

				nRet = nNumOfVol;
			}
			
			logger.debug("JSTOR_VolInfo() end  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + "JSTOR_VolInfo() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile,
			String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
			String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData) {

		logger.debug("JSTOR_FileGetExDRM() for Fasso Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return null;
			}
		}

		for (int i = 0; i < sInfoGetArr.length; i++) {
			for (int j = 0; j < sInfoGetArr[i].length; j++) {
				if (null != sInfoGetArr[i][j])
					sInfoGetArr[i][j] = sInfoGetArr[i][j].trim();
			}
		}

		for (int i = 0; i < sEssentialMetaData.length; i++) {
			for (int j = 0; j < sEssentialMetaData[i].length; j++) {
				if (null != sEssentialMetaData[i][j])
					sEssentialMetaData[i][j] = sEssentialMetaData[i][j].trim();
			}
		}

		for (int i = 0; i < sAdditionalMetaData.length; i++) {
			for (int j = 0; j < sAdditionalMetaData[i].length; j++) {
				if (null != sAdditionalMetaData[i][j])
					sAdditionalMetaData[i][j] = sAdditionalMetaData[i][j]
							.trim();
			}
		}

		int nRet[] = new int[nNumOfFile];
		
		int iRet = JSTOR_FileGet(nConnID, nNumOfFile, sInfoGetArr, nGUIFlag);

		if (iRet < 0) {
			return null;
		}

		// DRM 적용
		if (1 == nDRMEnabled) {
			String[] sFilePath = new String[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				sFilePath[i] = sInfoGetArr[i][1];
			}

			nRet = JSTOR_EncodeDRMFile(nConnID, nNumOfFile, sFilePath,
					sDRMType, sEssentialMetaData, sAdditionalMetaData);
		}
		logger.debug("JSTOR_FileGetExDRM() End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		return nRet;
	}

	// JSTOR_FileGetExDRM
	// DRM 권한을 팩키징시 파일에 포함시키는 방식(소프트 캠프) 즉, 선 권한 입력 방식처리를 위한 함수
	// 파수와 마크애니의 경우, 파일 오픈시점에 권한을 확인하는 후 권한 확인 방식임
	public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile,
			String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
			String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData, Vector vPrivInfo) {

		/* 수정됨 2009.04.20 : 
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return null;
			}
		}
		*/
		logger.debug("JSTOR_FileGetExDRM() for SoftCamp Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		int nRet[] = new int[nNumOfFile];

		// step 1) 저장서버로 부터 다운로드
		int iRet = JSTOR_FileGet(nConnID, nNumOfFile, sInfoGetArr, nGUIFlag);

		if (iRet < 0) {
			return null;
		}

		// DRM 적용
		if (1 == nDRMEnabled) {
			String[] sFilePath = new String[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				sFilePath[i] = sInfoGetArr[i][1];
			}
			
			nRet = JSTOR_EncodeDRMFile(nConnID, nNumOfFile, sFilePath,
					sDRMType, sEssentialMetaData, sAdditionalMetaData, vPrivInfo);
					
		}
		logger.debug("JSTOR_FileGetExDRM() for SoftCamp End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		return nRet;
	}


	public int JSTOR_FileCpy(int nConnID, int nNumOfFile, String[][] sInfoCpyArr) {
		logger.debug("JSTOR_FileCpy() Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {

			for (int i = 0; i < sInfoCpyArr.length; i++) {
				for (int j = 0; j < sInfoCpyArr[i].length; j++) {
					if (null != sInfoCpyArr[i][j])
						sInfoCpyArr[i][j] = sInfoCpyArr[i][j].trim();
				}
			}

			m_sNewCpyFileIDArr = new String[nNumOfFile];

			for (int i = 0; i < nNumOfFile; i++) {
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_FLECPY, SH_SVCOPT_NONE,
						null, m_dataSock_Out);

				// send Service FileCpy Info
				send_ServiceCpyInfo(nConnID, sInfoCpyArr[i], m_dataSock_Out);

				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

				// recv_ReplyMsg
				strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
						m_dataSock_In);

				// 성공일 경우, 파일 아이디를 반환시킴
				if (JSTOR_SUCCESS_RET == nRecvStatus) {
					m_sNewCpyFileIDArr[i] = strRecvMsg;
					nRet = JSTOR_SUCCESS_RET;
				}
				// 실패일 경우, 에러 메세지를 반환
				else {
					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
				}
			}
			
			logger.debug("JSTOR_FileCpy() End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + "JSTOR_FileCpy() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int JSTOR_FileMov(int nConnID, int nNumOfFile, String[][] sInfoMovArr) {
		logger.debug("JSTOR_FileMov() Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(IsCacheUse)
		{
			nConnID = JSTOR_Connect();
			if(nConnID < 0)
			{
				return JSTOR_ERR_RET;
			}
		}

		int nRet = -1;
		String[] arrRH = null;
		int nRecvStatus = -1;
		int nRecvDataSize = -1;
		String strRecvMsg = null;

		try {

			for (int i = 0; i < sInfoMovArr.length; i++) {
				for (int j = 0; j < sInfoMovArr[i].length; j++) {
					if (null != sInfoMovArr[i][j])
						sInfoMovArr[i][j] = sInfoMovArr[i][j].trim();
				}
			}

			m_sNewCpyFileIDArr = new String[nNumOfFile];

			for (int i = 0; i < nNumOfFile; i++) {
				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_FLEMOV, SH_SVCOPT_NONE,
						null, m_dataSock_Out);

				// send Service FileMov Info
				send_ServiceMovInfo(nConnID, sInfoMovArr[i], m_dataSock_Out);

				// recv_ReplyHeader
				arrRH = recv_ReplyHeader(nConnID, m_dataSock_In);

				nRecvStatus = Integer.parseInt(arrRH[SEQ_RECV_RH_STATUS]);
				nRecvDataSize = Integer.parseInt(arrRH[SEQ_RECV_RH_DATASIZE]);

				// 실패일 경우, 에러 메세지를 반환, 서비스 Break
				if (JSTOR_SUCCESS_RET != nRecvStatus) {
					// recv_ReplyMsg
					strRecvMsg = recv_ReplyMsg(nConnID, nRecvDataSize,
							m_dataSock_In);

					setError(-1 * nRecvStatus, strRecvMsg);
					nRet = JSTOR_ERR_RET;
					break;
				}

				nRet = JSTOR_SUCCESS_RET;
			}
			logger.debug("JSTOR_FileMov() End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ConnID : " + nConnID + " JSTOR_FileMov() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}

		return nRet;
	}

	public int[] JSTOR_EncodeDRMFile(int nConnID, int nNumOfFile,
			String[] sFilePath, String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData) {
		logger.debug("JSTOR_EncodeDRMFile() Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		int nRet[] = new int[nNumOfFile];

		// FASOO DRM 적용
		// FASOO License Server 2.3 호환
		if ("FASOO".equals(sDRMType)) {
			m_fasooPackager = new FasooPackager();

			// 에러를 리턴받을 Output Parameter
			String[] sRetErrMsg = new String[nNumOfFile];

			String[] sOutDrmFilePath = new String[nNumOfFile];

			nRet = m_fasooPackager.makePackageEx(nNumOfFile, sEssentialMetaData,
					sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);

			for (int i = 0; i < nNumOfFile; i++) {
				if (null == sOutDrmFilePath[i]) {
					sOutDrmFilePath[i] = sFilePath[i];
				}
			}
			m_sOutDrmFilePath = sOutDrmFilePath;
		}
		// 2004년 7월 : ACUBE 자료관 시스템에 적용하면서 수정된 클래스
		// FASOO License Server 3.1 호환
		else if ("FASOO_V3.1".equals(sDRMType)) {
	
			logger.info("ConnID : " + nConnID + " FASOO_V3.1 Section Entered");
			
			m_fasooPackager = new FasooPackager();

			String[] sRetErrMsg = new String[nNumOfFile];
			String[] sOutDrmFilePath = new String[nNumOfFile];

			int nIndex = -1;

			logger.info("ConnID : " + nConnID +
					"m_fasooPackager.makePackage31() : nNumOfFile = " + nNumOfFile);			

			nRet = m_fasooPackager.makePackage31(nNumOfFile, sEssentialMetaData,
					sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);
			for (int i = 0; i < nNumOfFile; i++) {
				logger.info("ConnID : " + nConnID + " Downloaded Src File[" + i
						+ "] = " + sEssentialMetaData[i][2]);

				if (nRet[i] >= 0) {
					nIndex = sEssentialMetaData[i][2].lastIndexOf(System
							.getProperty("file.separator"));
					// 생성된 DRM 파일의 전체 경로
					// sOutDrmFilePath[i] 는 파일명만 받아야 한다.
					// FASOO API 버전에 따라서, 파일명이 아니라, 전체 경로를 반환하는 경우도 있으므로,
					// 다음과 같이 명확히 파일명만을 분리하도록 한다.
					nIndex = sOutDrmFilePath[i].lastIndexOf(System
							.getProperty("file.separator"));
					sOutDrmFilePath[i] = sOutDrmFilePath[i].substring(
							nIndex + 1, sOutDrmFilePath[i].length());

					// DRM FILE FULL PATH
					String sOutDrmFileFullPath = sEssentialMetaData[i][2]
							.substring(0, nIndex + 1)
							+ sOutDrmFilePath[i];

					logger.info("ConnID : " + nConnID +
							" Make Drm File Success : DrmFile = " 
									+ sOutDrmFilePath[i]);

					File fpDrm = null;
					File fpOut = null;

					// 생성될 DRM 파일 패스명을 지정했을 경우에는,
					// 지정된 파일명으로 DRM 파일을 만든다
					if (null != sEssentialMetaData[i][3]
							&& 0 != sEssentialMetaData[i][3].length()) {
						logger.info("ConnID : " + nConnID +
								"Target Drm File Path Assigned : TargetFile = "
										+ sEssentialMetaData[i][3]);

						fpDrm = new File(sOutDrmFileFullPath);
						fpOut = new File(sEssentialMetaData[i][3]);

						boolean bRet = fpDrm.renameTo(fpOut);
						if (bRet) {
							logger.info("ConnID : " + nConnID + 
									" fpDrm.renameTo(fpOut) Success");
						} else {
							logger.info("ConnID : " + nConnID +
									" fpDrm.renameTo(fpOut) Fail");
						}

						if (fpOut.exists()) {
							logger.info("ConnID : " + nConnID +
									" Rename To Target Drm File Success");
						} else {
							logger.info("ConnID : " + nConnID +
									" Rename To Target Drm File Fail");
						}

						new File(sEssentialMetaData[i][2]).delete();

						if (new File(sEssentialMetaData[i][2]).exists()) {
							logger.info("ConnID : " + nConnID +
									"Delete Download Src File Fail");
						} else {
							logger.info("ConnID : " + nConnID +
									" Delete Download Src File Fail");
						}

						sOutDrmFilePath[i] = sEssentialMetaData[i][3];
					}
					// 생성될 이름을 지정하지 않았을 경우에는,
					// 저장서버로 부터 다운로드 받은 원본파일명(sEssentialMetaData[2])으로 DRM 파일을
					// 만든다
					else {
						logger.info("ConnID : " + nConnID +
								"Target Drm File Path Does not Assigned");

						fpDrm = new File(sOutDrmFileFullPath);
						fpOut = new File(sEssentialMetaData[i][2]);

						fpOut.delete();

						if (fpOut.exists()) {
							logger.info("ConnID : " + nConnID +
									"Delete Download Src File Fail");
						} else {
							logger.info("ConnID : " + nConnID + " Delete Download Src File Success");
						}

						boolean bRet = fpDrm.renameTo(fpOut);
						if (bRet) {
							logger.info("ConnID : " + nConnID + 
									"fpDrm.renameTo(fpOut) Success");
						} else {
							logger.info("ConnID : " + nConnID +
									"fpDrm.renameTo(fpOut) Fail");
						}

						if (fpDrm.exists()) {
							logger.info("ConnID : " + nConnID + 
									"Rename DrmFile Fail");
						} else {
							logger.info("ConnID : " + nConnID + 
									"Rename DrmFile Success");
						}

						sOutDrmFilePath[i] = sEssentialMetaData[i][2];
					}
				}
				// 이미 DRM 패키징된 파일일 경우
				else if (-26 == nRet[i]) {
					logger.info("ConnID : " + nConnID + "Already Maked Drm File");

					nRet[i] = 0;

					String sOutDrmFileFullPath = sEssentialMetaData[i][2];

					File fpDrm = null;
					File fpOut = null;

					// 생성될 DRM 파일 패스명을 지정했을 경우에는,
					// 지정된 파일명으로 DRM 파일을 만든다
					if (null != sEssentialMetaData[i][3]
							&& 0 != sEssentialMetaData[i][3].length()) {
						fpDrm = new File(sOutDrmFileFullPath);
						fpOut = new File(sEssentialMetaData[i][3]);

						fpDrm.renameTo(fpOut);

						new File(sEssentialMetaData[i][2]).delete();

						sOutDrmFilePath[i] = sEssentialMetaData[i][3];
					}
					// 생성될 이름을 지정하지 않았을 경우에는,
					// 저장서버로 부터 다운로드 받은 원본파일명(sEssentialMetaData[2])으로 리턴
					else {
						sOutDrmFilePath[i] = sOutDrmFileFullPath;
					}
				} else {
					logger.info("ConnID : " + nConnID +
							"Make Drm File Fail : nRet = " + nRet[i]);
					sOutDrmFilePath[i] = null;
				}

				if (null != sOutDrmFilePath[i]) {
					nIndex = sOutDrmFilePath[i].lastIndexOf(System
							.getProperty("file.separator"));

					sOutDrmFilePath[i] = sOutDrmFilePath[i].substring(
							nIndex + 1, sOutDrmFilePath[i].length());
				}
			}

			m_sOutDrmFilePath = sOutDrmFilePath;
		}
		// MARKANY DRM 적용 : 2005-05-20
		else if ("MARKANY".equals(sDRMType)) {
			// 에러를 리턴받을 Output Parameter
			String[] sRetErrMsg = new String[nNumOfFile];
			String[] sOutDrmFilePath = new String[nNumOfFile];

			MarkAnyPackager map = new MarkAnyPackager();

			nRet = map.makePackage(nNumOfFile, sEssentialMetaData,
					sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);

			for (int i = 0; i < nNumOfFile; i++) {
				if (null == sOutDrmFilePath[i]) {
					sOutDrmFilePath[i] = sFilePath[i];
				}
			}
			m_sOutDrmFilePath = sOutDrmFilePath;
		}
		// //////////////////////////
		else {
			nRet = null;
		}
		logger.debug("JSTOR_EncodeDRMFile() End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return nRet;
	}


	public int[] JSTOR_EncodeDRMFile(int nConnID, int nNumOfFile,
			String[] sFilePath, String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData, Vector vPrivInfo) {
		
		int nRet[] = new int[nNumOfFile];
		try{
			logger.debug("JSTOR_EncodeDRMFile() for SoftCamp Start  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
	
	// 		if ("SOFTCAMP".equals(sDRMType)) {
			logger.debug("sDRMType : " + sDRMType);
			if (sDRMType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우)
				SoftCampPackager scsl = new SoftCampPackager();
	
			logger.debug("m_drmType : " + m_drmType);
			
				if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
				{
					scsl.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
					logger.debug("m_drmConfig.get(SCSL_PROP_PATH)" + (String) m_drmConfig.get("SCSL_PROP_PATH"));
				}
	
				// 에러를 리턴받을 Output Parameter
				String[] sRetErrMsg = new String[nNumOfFile];
	
				String[] sOutDrmFilePath = new String[nNumOfFile];
	
				nRet = scsl.makePackage(nNumOfFile, sEssentialMetaData,
						sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath, vPrivInfo);
	
				for (int i = 0; i < nNumOfFile; i++) {
					if(nRet[i] < 0)
					{
						if(nRet[i] != -36 || nRet[i] != -81){ //36, 81 에 대해서는 에러로 처리 하지 않음 (국민은행 요구사항 2009.03.03)
							setError(nRet[i], nRet[i] + "");
						}
					}
					
					if (null == sOutDrmFilePath[i]) {
						sOutDrmFilePath[i] = sFilePath[i];
					}
				}
				m_sOutDrmFilePath = sOutDrmFilePath;
				logger.debug("m_sOutDrmFilePath : " + m_sOutDrmFilePath);
			}
			else 
			{
				nRet = null;
				return nRet;
			}
		}catch(Exception ex){
			logger.error("JSTOR_EncodeDRMFile() error has occured : " + ex.getMessage());			
		}	
		
		logger.debug("JSTOR_EncodeDRMFile() for SoftCamp End  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return nRet;
	}

	/**
	 * 해당 파일에 해당하는 cache가 있는지 확인하여 cache 경로를 반환한다
	 * 
	 * @return
	 */
	private String getCache(String Id) {
		String FileCachePath = null;
		try {
logger.debug("getCache(" + Id + ") start!!!!!");			
			FileCachePath = cacheService.get(Id).toString();
logger.debug("FileCachePath : " + FileCachePath);
			if (!(FileCachePath != null && new File(FileCachePath).canRead())) {
				FileCachePath = null;
			}
logger.debug("getCache(" + Id + ") end!!!!!");
		} catch (Exception ex) {
			//logger.error("getCache() fail : " + ex.getMessage());
			return null;
		}

		return FileCachePath;
	}

	/**
	 * 파일 작업이후 해당경로를 cache Manager에게 넣어준다
	 * 
	 * @param Path
	 */
	private void putCache(String Id, String FilePath) {
		try {
			cacheService.putInCache(Id, FilePath);
		} catch (Exception e) {			
			logger.error("putCache() fail : " + e.getMessage());
		}
	}

	/**
	 * 파일 교체, 파일 삭제시 cache Manager가 object를 제거하도록 한다
	 * 
	 * @param Id
	 */
	private void removeCache(String Id) {
		try {
			cacheService.removeFromCache(Id);
		} catch (Exception e) {			
			logger.error("removeCache() fail" + e.getMessage());
		}
	}

	private void manageCacheCluster(String filePath)
	{
		if (null != CLUSTER_INFO && CLUSTER_INFO.length > 0)
		{
			for (int i = 0; i < CLUSTER_INFO.length; i++)
			{
				String info = CLUSTER_INFO[i].trim();
				StringTokenizer sTK = new StringTokenizer(info, ";");
				String host = sTK.nextToken();
				int port    = Integer.parseInt(sTK.nextToken());
				String file  = sTK.nextToken() + "//" + filePath;

				int nConnID = -1;
				int nRet = -1;
				try
				{
					jSFTP = new JSFTPNative();

					nConnID = jSFTP.JSFTP_Connect (host, port);   
					if (nConnID <0)
					{
						throw new Exception("[JSTOR] cluster connect fail - " + host + ", " + port);
					}

					nRet = jSFTP.JSFTP_ExistFile (nConnID, file, 6);
					if (nRet < 0)
					{
						throw new Exception("[JSTOR] cluster check exist file fail - " + host + ", " + port + ", " + file + "(" + jSFTP.JSFTP_getErrMsg() + ")");
					}

					nRet = jSFTP.JSFTP_DeleteFile (nConnID, file);
					if (nRet < 0)
					{
						throw new Exception("[JSTOR] cluster delete file fail - " + host + ", " + port + ", " + file + ", " + jSFTP.JSFTP_getErrMsg() + ")");
					}
				} 
				catch (Exception e) 
				{
					logger.error("manageCacheClustere() Fail: "  +  e.getMessage());
				}
				finally
				{
					if (nConnID > 0)
					{
						jSFTP.JSFTP_Disconnect(nConnID);
					}
				}
			}
		}
	}
}
