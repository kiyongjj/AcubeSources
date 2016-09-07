/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : Add XNDisc XNApi
 * </pre>
 */
package com.sds.acube.jstor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.commons.lang.StringUtils;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.xnapi.XNApi;

/**
 * XNDisc XNApi Main
 * 
 * @author Takkies
 * 
 */
public class XNDiscXNApi extends JSTORApi {

	/* XNDisc XNApi 배포 버전정보(version.txt) */
	private static String XNDiscXNApi_PublishingVersion;

	/* XNDisc XNApi 배포날짜(version.txt) */
	private static String XNDiscXNApi_PublishingDate;

	/* XNDisc XNApi 버전 정보 */
	public static String XNAPI_VERSION;

	/* XNApi 객체 */
	private XNApi xnapi = null;

	/* 캐시 사용 여부 */
	private boolean useCache = false;

	/* 에러 코드 */
	private int errorCode;

	/* 에러 메시지 */
	private String errorMsg;

	/* 파일 등록 파일 아이디 배열 정보(파일 등록이 성공했을 경우, 반환되는 파일 아이디) */
	private String m_sRegFileIDArr[];

	/* 파일 복사 파일 아이디 배열 정보(복사 성공한 파일의 신규 파일 아이디) */
	private String m_sNewCpyFileIDArr[];

	/* 파일 처리 에러 코드 */
	private static final int FILE_ERROR = 7000;

	/* 네트워크 에러 코드 */
	private static final int NETWORK_ERROR = 7100;

	/* NDisc 에러 코드 */
	private static final int NDISC_ERROR = 7200;

	/* OS 별 라인피드 */
	private static String LINE_SEPERATOR = System.getProperty("line.separator");

	static {
		XNAPI_VERSION = "ACUBE JSTOR XNDISC XNAPI " + getXNDiscXNApiVersion() + "(" + getXNDiscXNApiPublshingDate() + ")";
		System.setProperty("jstor_api_version", XNAPI_VERSION);
		logger = LogFactory.getLogger("jstorapi");
		StringBuilder smsg = new StringBuilder(LINE_SEPERATOR);
		smsg.append("┌").append(StringUtils.rightPad("", 60, "-")).append("┐").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Company        : SAMSUNG SDS", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Product Name   : JSTOR API for ACUBE", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Version        : " + XNAPI_VERSION, 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("└").append(StringUtils.rightPad("", 60, "-")).append("┘").append(LINE_SEPERATOR);
		if (logger.isInfoEnabled() || logger.isDebugEnabled()) {
			logger.info(smsg.toString());
		} else {
			System.out.println(smsg.toString());
		}
	}

	/**
	 * 생성자(캐시 사용 여부)
	 * 
	 * @param useCache
	 *            캐시 사용 여부
	 */
	public XNDiscXNApi(boolean useCache) {
		System.setProperty("xndisc_xnapi_use_cache", Boolean.toString(useCache));
		this.useCache = useCache;
	}

	/**
	 * 에러코드 및 메시지 설정
	 * 
	 * @param errorCode
	 *            에러코드
	 * @param errorMsg
	 *            에러메시지
	 */
	private void setError(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * 에러코드 얻기
	 */
	@Override
	public int JSTOR_getErrCode() {
		return errorCode;
	}

	/**
	 * 에러메시지 얻기
	 */
	@Override
	public String JSTOR_getErrMsg() {
		return errorMsg;
	}

	/**
	 * 파일등록 파일 아이디 배열 얻기
	 */
	@Override
	public String[] JSTOR_getRegFileID() {
		for (int i = 0; i < m_sRegFileIDArr.length; i++) {
			if (m_sRegFileIDArr[i] != null) {
				m_sRegFileIDArr[i] = m_sRegFileIDArr[i].trim();
			}
		}
		return m_sRegFileIDArr;
	}

	/**
	 * 파일복사 파일 아이디 배열 얻기
	 */
	@Override
	public String[] JSTOR_getNewCpyFileID() {
		for (int i = 0; i < m_sNewCpyFileIDArr.length; i++) {
			if (m_sNewCpyFileIDArr[i] != null) {
				m_sNewCpyFileIDArr[i] = m_sNewCpyFileIDArr[i].trim();
			}
		}
		return m_sNewCpyFileIDArr;
	}

	/**
	 * JSTOR XNDisc Server 접속하기
	 * 
	 * @param sIPAddr
	 *            XNDisc Server IP 정보
	 * @param nPortNo
	 *            XNDisc Server PORT 정보
	 * @return 접속 결과 정보(접속 로컬 PORT 정보)
	 */
	@Override
	public int JSTOR_Connect(String sIPAddr, int nPortNo) {
		System.out.println("Kiyong TEST XNDiscXNApi.java JSTOR_Connect()");
		int nConnId = -1;
		xnapi = new XNApi(useCache);
		try {
			nConnId = xnapi.XNDisc_Connect(sIPAddr, nPortNo);
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_Connect fail : " + e.getMessage());
		}
		return nConnId;
	}

	/**
	 * JSTOR XNDisc Server 접속 종료하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 */
	@Override
	public void JSTOR_Disconnect(int nConnID) {
		try {
			xnapi.XNDisc_Disconnect();
			logger.info("JSTOR Disconnect / ConnID : " + nConnID);
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_Disconnect fail : " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_Disconnect fail : " + e.getMessage());
		}
	}

	/**
	 * 파일 등록하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            등록할 파일 개수
	 * @param sInfoRegArr
	 *            등록할 파일정보 배열<br>
	 *            <code>
	 *    	sInfoRegArr[i][0] => 등록할 파일의 전체 경로(일반적으로 서버의 temporary 경로)
	 * 		sInfoRegArr[i][1] => 등록 대상 볼륨 아이디
	 * 		sInfoRegArr[i][2] => 등록시 적용할 필터 (0 : NONE, 1 : 압축(GZIP 방식), 2 : 암호화,  3 : 압축 + 암호화)
	 * 		sInfoRegArr[i][3] => FileCDate(option) 사용하지 않을 시에는 반드시 null 을 대입할 것
	 * 			  </code>
	 * @param nOption
	 *            GUI Interface 지원 여부(DRM 등), 0이면 미사용, 1이면 사용, 10이면 DRM 사용
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileReg(int nConnID, int nNumOfFile, String[][] sInfoRegArr, int nOption) {
		int ret = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setName(sInfoRegArr[i][0]); // 파일 위치 정보
				nFile[i].setVolumeId(Integer.parseInt(sInfoRegArr[i][1])); // 저장서버 볼륨 ID
				nFile[i].setStatType(sInfoRegArr[i][2]); // "0"
			}
			if (nOption == 10) { // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if (JSTOR_DecodeDRMFile(nFile) != true) {// 복호화 실패 시
					logger.error("JSTOR_DecodeDRMFile() fail");
					return ret;
				}
			}
			m_sRegFileIDArr = xnapi.XNDISC_FileReg(nFile);
			logger.info("JSTOR_FileReg() success");
			ret = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileReg fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileReg fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileReg fail : " + e.getMessage());
		}
		return ret;
	}

	/**
	 * 파일 다운로드 하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            다운로드할 파일 개수
	 * @param sInfoGetArr
	 *            다운로드할 파일 정보 배열 <br>
	 *            <code>
	 *     sInfoGetArr[i][0] => 저장서버로 부터 가져올 파일 아이디
	 *     sInfoGetArr[i][1] => 가져온 파일이 저장될 로컬 파일 경로(파일명 까지 포함된 전체 경로임)
	 *     sInfoGetArr[i][2] => 가져올 때 적용할 Fileter ID (등록할 때 적용된 Filter ID 와 동일해야 함)  (0 : NONE, 1 : 압축(GZIP 방식), 2 : 암호화, 3 : 압축 + 암호화, -1 : 자동 검출)
	 *             </code>
	 * @param nGUIFlag
	 *            GUI Interface 지원 여부(DRM 등), 0이면 미사용, 1이면 사용, 10이면 DRM 사용
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileGet(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag) {
		int nRet = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(sInfoGetArr[i][0]);
				nFile[i].setName(sInfoGetArr[i][1]);
				nFile[i].setStatType(sInfoGetArr[i][2]);
			}
			xnapi.XNDISC_FileGet(nFile);
			logger.info("JSTOR_FileGet() sucess");
			nRet = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileGet fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileGet fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileGet fail : " + e.getMessage());
		}
		return nRet;
	}

	/**
	 * 파일 교체하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            교체할 파일 개수
	 * @param sInfoRepArr
	 *            교체할 파일 정보 배열<br>
	 *            <code>
	 * 		sInfoRepArr[i][0] => 교체될 대상 파일 아이디
	 * 		sInfoRepArr[i][1] => 교체할 파일 경로
	 * 		sInfoRepArr[i][2] => 교체하면서 적용될 필터(0 : NONE, 1 : 압축(GZIP 방식), 2 : 암호화, 3 : 압축 + 암호화, -1 : 자동 검출)
	 *            </code>
	 * @param nOption
	 *            GUI Interface 지원 여부(DRM 등), 0이면 미사용, 1이면 사용, 10이면 DRM 사용
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileRep(int nConnID, int nNumOfFile, String[][] sInfoRepArr, int nOption) {
		int nRet = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(sInfoRepArr[i][0]); // 저장서버 위치 정보
				nFile[i].setName(sInfoRepArr[i][1]); // 파일 위치 정보
				nFile[i].setStatType(sInfoRepArr[i][2]); // 0
			}
			if (nOption == 10) { // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if (JSTOR_DecodeDRMFile(nFile) != true) {// 복호화 실패 시
					logger.error("JSTOR_DecodeDRMFile() fail");
					return nRet;
				}
			}
			xnapi.XNDISC_FileRep(nFile);
			logger.info("JSTOR_FileRep() success");
			nRet = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileRep fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileRep fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileRep fail : " + e.getMessage());
		}
		return nRet;
	}

	/**
	 * 파일 삭제하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            삭제할 파일 개수
	 * @param sInfoDelArr
	 *            삭제할 파일 아이디 정보 배열
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileDel(int nConnID, int nNumOfFile, String[] sInfoDelArr) {
		int nRet = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(sInfoDelArr[i]);
			}
			xnapi.XNDISC_FileDel(nFile);
			logger.info("JSTOR_FileDel() success");
			nRet = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileDel fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileDel fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileDel fail : " + e.getMessage());
		}
		return nRet;
	}

	/**
	 * 파일 복사하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            복사할 파일 개수
	 * @param sInfoCpyArr
	 *            복사할 파일 정보 배열<br>
	 *            <code>
	 * 		sInfoCpyArr[i][0] => 복사될 대상 파일 아이디 
	 * 		sInfoCpyArr[i][1] => 복사될 대상 볼륨 아이디
	 * 		sInfoCpyArr[i][2] => 복사되면서 적용될 필터(0 : NONE, 1 : 압축(GZIP 방식), 2 : 암호화, 3 : 압축 + 암호화, -1 : 자동 검출)
	 * 		sInfoCpyArr[i][3] => 복사될 대상 저장서버 구분(1 : 로컬 저장서버, 2 : 원격 저장서버)
	 * 		sInfoCpyArr[i][4] => 복사될 대상 저장서버 IP
	 * 		sInfoCpyArr[i][5] => 복사될 대상 저장서버 Port
	 *             </code>
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileCpy(int nConnID, int nNumOfFile, String[][] sInfoCpyArr) {
		int nRet = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(sInfoCpyArr[i][0]);
				nFile[i].setVolumeId(Integer.parseInt(sInfoCpyArr[i][1]));
				nFile[i].setStatType(sInfoCpyArr[i][2]);
			}
			m_sNewCpyFileIDArr = xnapi.XNDISC_FileCpy(nFile);
			logger.info("JSTOR_FileCpy() success");
			nRet = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileCpy fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileCpy fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileCpy fail : " + e.getMessage());
		}
		return nRet;
	}

	/**
	 * 파일 이동하기
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 * @param nNumOfFile
	 *            이동할 파일 개수
	 * @param sInfoMoveArr
	 *            이동할 파일 정보 배열<br>
	 *            <code>
	 * 		sInfoCpyArr[i][0] => 이동될 대상 파일 아이디
	 * 		sInfoCpyArr[i][1] => 이동될 대상 볼륨 아이디
	 * 		sInfoCpyArr[i][2] => 이동되면서 적용될 필터(0 : NONE, 1 : 압축(GZIP 방식), 2 : 암호화, 3 : 압축 + 암호화, -1 : 자동 검출) 
	 * 		sInfoCpyArr[i][3] => 이동될 대상 저장서버 구분(1 : 로컬 저장서버, 2 : 원격 저장서버)
	 * 		sInfoCpyArr[i][4] => 이동될 대상 저장서버 IP 
	 * 		sInfoCpyArr[i][5] => 이동될 대상 저장서버 Port
	 * 			   </code>
	 * @return 성공이면 0, 실패이면 -1
	 */
	@Override
	public int JSTOR_FileMov(int nConnID, int nNumOfFile, String[][] sInfoMovArr) {
		int nRet = -1;
		NFile[] nFile = null;
		try {
			nFile = new NFile[nNumOfFile];
			for (int i = 0; i < nNumOfFile; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(sInfoMovArr[i][0]);
				nFile[i].setVolumeId(Integer.parseInt(sInfoMovArr[i][1]));
				nFile[i].setStatType(sInfoMovArr[i][2]);
			}
			xnapi.XNDISC_FileMov(nFile);
			logger.info("JSTOR_FileMov() success");
			nRet = 0;
		} catch (FileException e) {
			e.printStackTrace();
			setError(FILE_ERROR, e.getMessage());
			logger.error("JSTOR_FileMov fail : " + e.getMessage());
		} catch (NetworkException e) {
			e.printStackTrace();
			setError(NETWORK_ERROR, e.getMessage());
			logger.error("JSTOR_FileMov fail : " + e.getMessage());
		} catch (NDiscException e) {
			e.printStackTrace();
			setError(NDISC_ERROR, e.getMessage());
			logger.error("JSTOR_FileMov fail : " + e.getMessage());
		}
		return nRet;
	}

	/**
	 * JSTOR commit 하기<br>
	 * 사용하지 않으나 API 수준에서 맞춰줌.<br>
	 * 
	 * @param nConnID
	 *            접속 결과 정보(접속 로컬 PORT 정보)
	 */
	@Override
	public int JSTOR_Commit(int nConnID) {
		return 0;
	}

	/**
	 * JSTOR rollback 하기<br>
	 * 사용하지 않으나 API 수준에서 맞춰줌.<br>
	 * 
	 * @param 접속
	 *            결과 정보(접속 로컬 PORT 정보)
	 */
	@Override
	public int JSTOR_Rollback(int nConnID) {
		return 0;
	}

	/**
	 * 파일 정보 DRM 처리하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 */
	private boolean JSTOR_DecodeDRMFile(NFile[] nFile) {
		boolean ret = false;
		try {
			if (m_drmType.equalsIgnoreCase("FASOO_V3.1")) {// Fasoo DRM 사용
				m_fasooPackager = new FasooPackager();
				String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
				String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
				for (int i = 0; i < nFile.length; i++) {
					ret = m_fasooPackager.isFasooPackageFile(nFile[i].getName());
					if (ret) {
						ret = m_fasooPackager.doFasooPacakgeFileExtract(nFile[i].getName(), fsdHomeDir, fsdServerID);
					}
				}
			} else if (m_drmType.startsWith("SOFTCAMP")) {// SoftCamp DRM 사용
				m_softCampPackager = new SoftCampPackager();
				String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
				String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
				String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");
				if (m_drmType.equalsIgnoreCase("SOFTCAMP_V3.1")) {
					m_softCampPackager.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
				}
				for (int i = 0; i < nFile.length; i++) {
					ret = m_softCampPackager.doSoftCampFileExtract(nFile[i].getName(), keyDir + File.separator + keyFile, privID);
				}
			}
		} catch (Exception e) {
			logger.error("JSTOR_DecodeDRMFile fail : " + e.getMessage());
		}
		return ret;
	}

	/**
	 * XNDisc XNApi 배포 버전정보 얻어오기
	 * 
	 * @return XNDisc XNApi 버전정보
	 */
	public static String getXNDiscXNApiVersion() {
		if (XNDiscXNApi_PublishingVersion == null) {
			readVersionFromFile();
		}
		return XNDiscXNApi_PublishingVersion;
	}

	/**
	 * XNDisc XNApi 배포일 얻어오기
	 * 
	 * @return XNDisc XNApi 배포일
	 */
	public static String getXNDiscXNApiPublshingDate() {
		if (XNDiscXNApi_PublishingDate == null) {
			readVersionFromFile();
		}
		return XNDiscXNApi_PublishingDate;
	}

	/**
	 * XNDisc XNApi 배포 정보 읽기
	 */
	private static void readVersionFromFile() {
		XNDiscXNApi_PublishingVersion = "<unknown>";
		XNDiscXNApi_PublishingDate = "<unknown>";
		InputStreamReader isr = null;
		LineNumberReader lnr = null;
		try {
			isr = new InputStreamReader(XNDiscXNApi.class.getResourceAsStream("/com/sds/acube/jstor/version.txt"));
			if (isr != null) {
				lnr = new LineNumberReader(isr);
				String line = null;
				do {
					line = lnr.readLine();
					if (line != null) {
						if (line.startsWith("Publishing-Version=")) {
							XNDiscXNApi_PublishingVersion = line.substring("Publishing-Version=".length(), line.length()).trim();
						} else if (line.startsWith("Publishing-Date=")) {
							XNDiscXNApi_PublishingDate = line.substring("Publishing-Date=".length(), line.length()).trim();
						}
					}
				} while (line != null);
				lnr.close();
			}
		} catch (IOException ioe) {
			XNDiscXNApi_PublishingVersion = "<unknown>";
			XNDiscXNApi_PublishingDate = "<unknown>";
		} finally {
			try {
				if (lnr != null) {
					lnr.close();
				}
				if (isr != null) {
					isr.close();
				}
			} catch (IOException ioe) {
			}
		}
	}
}
