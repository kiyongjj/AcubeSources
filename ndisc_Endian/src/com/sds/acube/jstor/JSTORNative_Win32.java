package com.sds.acube.jstor;

import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class JSTORNative_Win32 extends JSTORNative {

	static {
		String strVersion = "PureJavaApi-Win32Svr(2008-11-19)";

		System.out.println("\n");
		System.out
				.println("--------------------------------------------------");
		System.out.println("▶ Company :  SAMSUNG SDS");
		System.out.println("▶ Product Name : JSTOR API");
		System.out.println("▶ Version : " + strVersion);
		System.out
				.println("--------------------------------------------------");
		System.out.println("\n");

		System.setProperty("jstor_api_version", strVersion);
	}

	public JSTORNative_Win32() {
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
	} 

	public int JSTOR_FileGet(int nConnID, int nNumOfFile,
			String[][] sInfoGetArr, int nGUIFlag) {
		int nRet = -1;
		// int nFileSize = -1;
		// String[] arrRH = null;
		// int nRecvStatus = -1;
		// int nRecvDataSize = -1;

		// ///////////////////////
		String strRecvMsg = null;
		String strStatus = null;

		try {
			for (int i = 0; i < sInfoGetArr.length; i++) {
				for (int j = 0; j < sInfoGetArr[i].length; j++) {
					if (null != sInfoGetArr[i][j])
						sInfoGetArr[i][j] = sInfoGetArr[i][j].trim();
				}
			}

			for (int i = 0; i < nNumOfFile; i++) {
				File file = new File(sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
				String strParent = file.getParent();
				if (null == strParent) {
					throw new Exception("Wrong File Path : "
							+ sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
				} else {
					File dir = new File(strParent);
					if (!(dir.exists())) {
						System.out
								.println("[JSTOR_FileGet - WARNING] Path's Dir. does not exists, Make Dir. for : "
										+ sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
						if (!(dir.mkdirs())) {
							throw new Exception("Can not make Dir. for : "
									+ sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
						}
					}
				}

				/*
				 * if (!file.createNewFile()) { throw new Exception("Fail to
				 * create new file: " + sInfoGetArr[i][SEQ_GETINFO_FILEPATH]); }
				 */

				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_GET_JSTOR,
						SH_SVCOPT_NONE, null, m_dataSock_Out);

				// 이 부분부터 스트림 형식으로 전환
				send_FileGetReqInfo_Win32(nConnID, 1, sInfoGetArr[i],
						m_buffSock_Out);

				// 전체 수신 문자열 반환
				strRecvMsg = recv_ReplyInfo_Win32(nConnID, m_buffSock_In);

				JSTORDebug.writeTrace(nConnID, "recv_ReplyInfo_Win32() : "
						+ strRecvMsg);

				StringTokenizer sTK = new StringTokenizer(strRecvMsg, "\t");
				strStatus = sTK.nextToken();
				if (!("0000".equals(strStatus))) // 성공이 아니라면 ...
				{
					JSTORDebug.writeTrace(nConnID, "Fail");

					setError(-1 * Integer.parseInt(strStatus), sTK.nextToken());

					nRet = JSTOR_ERR_RET;
					break;
				} else {
					JSTORDebug.writeTrace(nConnID, "Success");

					// 파일 갯수 : must be "1"
					Integer.parseInt(sTK.nextToken());

					// 성공했을 때는 파일을 수신해야 한다
					DownFileEx(nConnID, sInfoGetArr[i][SEQ_GETINFO_FILEPATH],
							Integer.parseInt(sTK.nextToken()), m_buffSock_In);

					JSTORDebug.writeTrace(nConnID, "DownFile(" + i
							+ ") Completed - "
							+ sInfoGetArr[i][SEQ_GETINFO_FILEPATH]);
				}

				nRet = JSTOR_SUCCESS_RET;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSTORDebug.writeTrace(nConnID, "JSTOR_FileGet() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				// m_socket.close();
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

		return nRet;
	}

	// JSTOR_FileGetExDRM
	// DRM 권한을 팩키징시 파일에 포함시키는 방식(소프트 캠프) 즉, 선 권한 입력 방식처리를 위한 함수
	// 파수와 마크애니의 경우, 파일 오픈시점에 권한을 확인하는 후 권한 확인 방식임
	public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile,
			String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
			String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData, Vector vPrivInfo) {

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

		return nRet;
	}

	public int JSTOR_FileReg(int nConnID, int nNumOfFile,
			String[][] sInfoRegArr, int nOption) {
		int nRet = -1;
		Properties propFile = null;
		// String[] arrRH = null;
		// int nRecvStatus = -1;
		// int nRecvDataSize = -1;

		String strRecvMsg = null;
		String strStatus = null;

		try {
			for (int i = 0; i < sInfoRegArr.length; i++) {
				for (int j = 0; j < sInfoRegArr[i].length; j++) {
					if (null != sInfoRegArr[i][j])
						sInfoRegArr[i][j] = sInfoRegArr[i][j].trim();
				}
			}

			if (10 == nOption) {   // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if ("FASOO_V3.1".equalsIgnoreCase(m_drmType)) {   // 파수 DRM 일 경우
					com.sds.acube.jstor.FasooPackager fsp = new FasooPackager();
					String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
					String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
					boolean bRet = false;
					for (int i = 0; i < sInfoRegArr.length; i++) {
						bRet = fsp.isFasooPackageFile(sInfoRegArr[i][0]);
						if (bRet) {
							bRet = fsp.doFasooPacakgeFileExtract(sInfoRegArr[i][0],
									fsdHomeDir, fsdServerID);
						}
					}
//				} else if ("SOFTCAMP".equalsIgnoreCase(m_drmType)) {   // SOFTCAMP DRM 일 경우
				} else if (m_drmType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우
					com.sds.acube.jstor.SoftCampPackager scp = new SoftCampPackager();
					String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
					String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
					String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");

					if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
					{
						scp.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
					}

					boolean bRet = false;
					for (int i = 0; i < sInfoRegArr.length; i++) {
						bRet = scp.doSoftCampFileExtract(sInfoRegArr[i][0], keyDir + File.separator + keyFile, privID);
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

				// send Service Header
				send_ServiceHeader(nConnID, SH_SVCCODE_REG_JSTOR,
						SH_SVCOPT_NONE, propFile, m_dataSock_Out);

				// 이 부분부터 스트림 형식으로 전환
				send_FileRegReqInfo_Win32(nConnID, 1, propFile, sInfoRegArr[i],
						m_buffSock_Out);

				// upload file
				UploadFile(nConnID, propFile, m_buffSock_Out, m_dataSock_Out);

				// 전체 수신 문자열 반환
				strRecvMsg = recv_ReplyInfo_Win32(nConnID, m_buffSock_In);

				JSTORDebug.writeTrace(nConnID, "recv_ReplyInfo_Win32() : "
						+ strRecvMsg);

				StringTokenizer sTK = new StringTokenizer(strRecvMsg, "\t");
				strStatus = sTK.nextToken();
				if (!("0000".equals(strStatus))) // 성공이 아니라면 ...
				{
					JSTORDebug.writeTrace(nConnID, "Fail");

					setError(-1 * Integer.parseInt(strStatus), sTK.nextToken());

					nRet = JSTOR_ERR_RET;
					break;
				} else {
					JSTORDebug.writeTrace(nConnID, "Success");

					// 파일 갯수 : must be "1"
					Integer.parseInt(sTK.nextToken());

					// 성공했을 때는 파일 아이디를 수신해야 한다

					// for (int j = 0; j < nNumOfFile; j++)
					// {
					m_sRegFileIDArr[i] = sTK.nextToken();
					// }
				}

				nRet = JSTOR_SUCCESS_RET;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSTORDebug.writeTrace(nConnID, "JSTOR_FileReg() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				// m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}
		return nRet;
	}

	public int JSTOR_FileRep(int nConnID, int nNumOfFile,
			String[][] sInfoRepArr, int nOption) {
		int nRet = -1;
		Properties propFile = null;
		// String[] arrRH = null;
		// int nRecvStatus = -1;
		// int nRecvDataSize = -1;

		String strRecvMsg = null;
		String strStatus = null;

		try {
			for (int i = 0; i < sInfoRepArr.length; i++) {
				for (int j = 0; j < sInfoRepArr[i].length; j++) {
					if (null != sInfoRepArr[i][j])
						sInfoRepArr[i][j] = sInfoRepArr[i][j].trim();
				}
			}

			if (10 == nOption) {   // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다.
				if ("FASOO_V3.1".equalsIgnoreCase(m_drmType)) {   // 파수 DRM 일 경우
					com.sds.acube.jstor.FasooPackager fsp = new FasooPackager();
					String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
					String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
					boolean bRet = false;
					for (int i = 0; i < sInfoRepArr.length; i++) {
						bRet = fsp.isFasooPackageFile(sInfoRepArr[i][0]);
						if (bRet) {
							bRet = fsp.doFasooPacakgeFileExtract(sInfoRepArr[i][0],
									fsdHomeDir, fsdServerID);
						}
					}
//				} else if ("SOFTCAMP".equalsIgnoreCase(m_drmType)) {   // SOFTCAMP DRM 일 경우
				} else if (m_drmType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우
					com.sds.acube.jstor.SoftCampPackager scp = new SoftCampPackager();
					String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
					String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
					String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");

					if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
					{
						scp.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
					}

					boolean bRet = false;
					for (int i = 0; i < sInfoRepArr.length; i++) {
						bRet = scp.doSoftCampFileExtract(sInfoRepArr[i][0], keyDir + File.separator + keyFile, privID);
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
				send_ServiceHeader(nConnID, SH_SVCCODE_REP_JSTOR,
						SH_SVCOPT_NONE, propFile, m_dataSock_Out);

				// 이 부분부터 스트림 형식으로 전환
				send_FileRepReqInfo_Win32(nConnID, 1, propFile, sInfoRepArr[i],
						m_buffSock_Out);

				// upload file
				UploadFile(nConnID, propFile, m_buffSock_Out, m_dataSock_Out);

				// 전체 수신 문자열 반환
				strRecvMsg = recv_ReplyInfo_Win32(nConnID, m_buffSock_In);

				JSTORDebug.writeTrace(nConnID, "recv_ReplyInfo_Win32() : "
						+ strRecvMsg);

				StringTokenizer sTK = new StringTokenizer(strRecvMsg, "\t");
				strStatus = sTK.nextToken();
				if (!("0000".equals(strStatus))) // 성공이 아니라면 ...
				{
					JSTORDebug.writeTrace(nConnID, "Fail");

					setError(-1 * Integer.parseInt(strStatus), sTK.nextToken());

					nRet = JSTOR_ERR_RET;
					break;
				}

				nRet = JSTOR_SUCCESS_RET;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSTORDebug.writeTrace(nConnID, "JSTOR_FileRep() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				// m_socket.close();
			} catch (Exception ex) {
				;
			}
			nRet = JSTOR_ERR_RET;
		}
		return nRet;
	}

	public int JSTOR_VolInfo(int nConnID) {
		int nRet = -1;
		// String[] arrRH = null;
		// int nRecvStatus = -1;
		// int nRecvDataSize = -1;

		int nNumOfVol = -1;

		String strRecvMsg = null;
		String strStatus = null;

		try {
			// send Service Header
			send_ServiceHeader(nConnID, SH_SVCCODE_VOLINFO_JSTOR,
					SH_SVCOPT_NONE, null, m_dataSock_Out);

			// 전체 수신 문자열 반환
			strRecvMsg = recv_ReplyInfo_Win32(nConnID, m_buffSock_In);

			JSTORDebug.writeTrace(nConnID, "recv_ReplyInfo_Win32() : "
					+ strRecvMsg);

			StringTokenizer sTK = new StringTokenizer(strRecvMsg, "\t");
			strStatus = sTK.nextToken();
			if (!("0000".equals(strStatus))) // 성공이 아니라면 ...
			{
				JSTORDebug.writeTrace(nConnID, "Fail");

				setError(-1 * Integer.parseInt(strStatus), sTK.nextToken());

				nRet = JSTOR_ERR_RET;
			} else {
				JSTORDebug.writeTrace(nConnID, "Success");

				// 서버 볼륨 갯수
				nNumOfVol = Integer.parseInt(sTK.nextToken());
				m_sVolInfoArr = new String[nNumOfVol][SZ_VOL_INFO];

				for (int i = 0; i < nNumOfVol; i++) {
					String sToken = null;

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_ID] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_NAME] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_ENAME] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_TYPE] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_RIGHT] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_CDATE] = (null == sToken ? null
							: sToken.trim());

					sToken = sTK.nextToken();
					m_sVolInfoArr[i][SEQ_VOL_DESC] = (null == sToken ? null
							: sToken.trim());
				}

				nRet = nNumOfVol;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSTORDebug.writeTrace(nConnID, "JSTOR_VolInfo() Fail : "
					+ e.getMessage());
			setError(-999, e.getMessage());
			try {
				// m_socket.close();
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
		int nRet[] = new int[nNumOfFile];

		// FASOO DRM 적용
		// FASOO License Server 2.3 호환
		if ("FASOO".equals(sDRMType)) {
			FasooPackager fsp = new FasooPackager();

			// 에러를 리턴받을 Output Parameter
			String[] sRetErrMsg = new String[nNumOfFile];

			String[] sOutDrmFilePath = new String[nNumOfFile];

			nRet = fsp.makePackageEx(nNumOfFile, sEssentialMetaData,
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
			JSTORDebug.writeTrace(nConnID, "FASOO_V3.1 Section Entered");
			com.sds.acube.jstor.FasooPackager fsp = new com.sds.acube.jstor.FasooPackager();

			String[] sRetErrMsg = new String[nNumOfFile];
			String[] sOutDrmFilePath = new String[nNumOfFile];

			int nIndex = -1;

			JSTORDebug.writeTrace(nConnID,
					"fsp.makePackage31() : nNumOfFile = " + nNumOfFile);

			nRet = fsp.makePackage31(nNumOfFile, sEssentialMetaData,
					sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);
			for (int i = 0; i < nNumOfFile; i++) {
				JSTORDebug.writeTrace(nConnID, "Downloaded Src File[" + i
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

					JSTORDebug.writeTrace(nConnID,
							"Make Drm File Success : DrmFile = "
									+ sOutDrmFilePath[i]);

					File fpDrm = null;
					File fpOut = null;

					// 생성될 DRM 파일 패스명을 지정했을 경우에는,
					// 지정된 파일명으로 DRM 파일을 만든다
					if (null != sEssentialMetaData[i][3]
							&& 0 != sEssentialMetaData[i][3].length()) {
						JSTORDebug.writeTrace(nConnID,
								"Target Drm File Path Assigned : TargetFile = "
										+ sEssentialMetaData[i][3]);

						fpDrm = new File(sOutDrmFileFullPath);
						fpOut = new File(sEssentialMetaData[i][3]);

						boolean bRet = fpDrm.renameTo(fpOut);
						if (bRet) {
							JSTORDebug.writeTrace(nConnID,
									"fpDrm.renameTo(fpOut) Success");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"fpDrm.renameTo(fpOut) Fail");
						}

						if (fpOut.exists()) {
							JSTORDebug.writeTrace(nConnID,
									"Rename To Target Drm File Success");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"Rename To Target Drm File Fail");
						}

						new File(sEssentialMetaData[i][2]).delete();

						if (new File(sEssentialMetaData[i][2]).exists()) {
							JSTORDebug.writeTrace(nConnID,
									"Delete Download Src File Fail");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"Delete Download Src File Success");
						}

						sOutDrmFilePath[i] = sEssentialMetaData[i][3];
					}
					// 생성될 이름을 지정하지 않았을 경우에는,
					// 저장서버로 부터 다운로드 받은 원본파일명(sEssentialMetaData[2])으로 DRM 파일을
					// 만든다
					else {
						JSTORDebug.writeTrace(nConnID,
								"Target Drm File Path Does not Assigned");

						fpDrm = new File(sOutDrmFileFullPath);
						fpOut = new File(sEssentialMetaData[i][2]);

						fpOut.delete();

						if (fpOut.exists()) {
							JSTORDebug.writeTrace(nConnID,
									"Delete Download Src File Fail");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"Delete Download Src File Success");
						}

						boolean bRet = fpDrm.renameTo(fpOut);
						if (bRet) {
							JSTORDebug.writeTrace(nConnID,
									"fpDrm.renameTo(fpOut) Success");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"fpDrm.renameTo(fpOut) Fail");
						}

						if (fpDrm.exists()) {
							JSTORDebug.writeTrace(nConnID,
									"Rename DrmFile Fail");
						} else {
							JSTORDebug.writeTrace(nConnID,
									"Rename DrmFile Success");
						}

						sOutDrmFilePath[i] = sEssentialMetaData[i][2];
					}
				}
				// 이미 DRM 패키징된 파일일 경우
				else if (-26 == nRet[i]) {
					JSTORDebug.writeTrace(nConnID, "Already Maked Drm File");

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
					JSTORDebug.writeTrace(nConnID,
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

		return nRet;
	}

	public int[] JSTOR_EncodeDRMFile(int nConnID, int nNumOfFile,
			String[] sFilePath, String sDRMType, String[][] sEssentialMetaData,
			String[][] sAdditionalMetaData, Vector vPrivInfo) {

		int nRet[] = new int[nNumOfFile];

// 		if ("SOFTCAMP".equals(sDRMType)) {
		if (m_drmType.startsWith("SOFTCAMP")) {   // SOFTCAMP DRM 일 경우)
			SoftCampPackager scsl = new SoftCampPackager();

			if ("SOFTCAMP_V3.1".equalsIgnoreCase(m_drmType))
			{
				scsl.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");
			}

			// 에러를 리턴받을 Output Parameter
			String[] sRetErrMsg = new String[nNumOfFile];

			String[] sOutDrmFilePath = new String[nNumOfFile];

			nRet = scsl.makePackage(nNumOfFile, sEssentialMetaData,
					sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath, vPrivInfo);

			for (int i = 0; i < nNumOfFile; i++) {
				if(nRet[i] < 0)
				{
					setError(nRet[i], nRet[i] + "");
				}
				
				if (null == sOutDrmFilePath[i]) {
					sOutDrmFilePath[i] = sFilePath[i];
				}
			}
			m_sOutDrmFilePath = sOutDrmFilePath;
		}
		else 
		{
			nRet = null;
		}

		return nRet;
	}
}
