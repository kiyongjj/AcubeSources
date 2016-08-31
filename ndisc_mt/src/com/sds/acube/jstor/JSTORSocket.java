package com.sds.acube.jstor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
//import org.apache.log4j.Logger;

public class JSTORSocket extends JSTORHeader {
		
	protected synchronized void send_ServiceHeader(int nConnID,
			int nServiceCode, int nServiceOpt, Properties propFile,
			DataOutputStream dataSock_Out) throws Exception {
		String szVersion = null;
		int nNumOfFile = -1;
		int nSizeOfData = -1;
		String chDelimChar = null;
		try {
			// Common
			// nServiceCode = nServiceCode;
			// nServiceOpt = nServiceOpt;
			szVersion = SH_SVCVER;
			szVersion = makeValidDataStream(szVersion, SH_SVCVER_LEN);
			chDelimChar = SH_SVCDELIMCHAR;
			chDelimChar = makeValidDataStream(chDelimChar, SH_SVCDELIMCHAR_LEN);
			switch (nServiceCode) {
			// 파일 저장
			case SH_SVCCODE_REG: {
				// nNumOfFile
				nNumOfFile = 1;
				// Get File Size
				int nFileSize = Integer.parseInt(propFile
						.getProperty("FILE_SIZE"));
				nSizeOfData = nNumOfFile * SH_SVCSZDATA_REG_CONST + nFileSize;
				break;
			}
				// 파일 저장 (Win32)
			case SH_SVCCODE_REG_JSTOR: {
				// nNumOfFile
				nNumOfFile = 1;
				// Get File Size
				int nFileSize = Integer.parseInt(propFile
						.getProperty("FILE_SIZE"));
				nSizeOfData = nNumOfFile * SH_SVCSZDATA_REG_CONST + nFileSize;
				break;
			}
				// 파일 가져오기
			case SH_SVCCODE_GET: {
				// 고정값
				nNumOfFile = 1;
				// 고정값
				nSizeOfData = SH_SVCSZDATA_GET_CONST;
				break;
			}
				// 파일 가져오기 (Win32)
			case SH_SVCCODE_GET_JSTOR: {
				// 고정값
				nNumOfFile = 1;
				// 고정값
				nSizeOfData = SH_SVCSZDATA_GET_CONST;
				break;
			}
				// 파일 삭제
			case SH_SVCCODE_DEL: {
				// 고정값
				nNumOfFile = 1;
				// 고정값
				nSizeOfData = SH_SVCSZDATA_DEL_CONST;
				break;
			}
				// 파일 교체
			case SH_SVCCODE_REP: {
				// 고정값
				nNumOfFile = 1;
				// Get File Size
				int nFileSize = Integer.parseInt(propFile
						.getProperty("FILE_SIZE"));
				nSizeOfData = nNumOfFile * SH_SVCSZDATA_REP_CONST + nFileSize;
				break;
			}
				// 파일 교체 (Win32)
			case SH_SVCCODE_REP_JSTOR: {
				// 고정값
				nNumOfFile = 1;
				// Get File Size
				int nFileSize = Integer.parseInt(propFile
						.getProperty("FILE_SIZE"));
				nSizeOfData = nNumOfFile * SH_SVCSZDATA_REP_CONST + nFileSize;
				break;
			}
				// 파일 복사
			case SH_SVCCODE_FLECPY: {
				// 고정값
				nNumOfFile = 1;
				// 고정값
				nSizeOfData = SH_SVCSZDATA_CPY_CONST;
				break;
			}
				// 파일 이동
			case SH_SVCCODE_FLEMOV: {
				// 고정값
				nNumOfFile = 1;
				// 고정값
				nSizeOfData = SH_SVCSZDATA_MOV_CONST;
				break;
			}
			case SH_SVCCODE_TRANS: {
				nNumOfFile = 0;
				nSizeOfData = 0;
				break;
			}
			case SH_SVCCODE_VOLINFO: {
				nNumOfFile = 0;
				nSizeOfData = 0;
				break;
			}
			case SH_SVCCODE_VOLINFO_JSTOR: {
				nNumOfFile = 0;
				nSizeOfData = 0;
				break;
			}
				// Disconnect
			case SH_SVCCODE_QUIT: {
				nNumOfFile = 0;
				nSizeOfData = 0;
				break;
			}
			}
			
			logger.debug(
					"ConnID : "+ nConnID + " send_ServiceHeader()" + "\n"
					+ "nServiceCode : " + nServiceCode + "\n"
					+ "nServiceOpt : " + nServiceOpt + "\n" + "szVersion : "
					+ szVersion + "\n" + "nNumOfFile : " + nNumOfFile + "\n"
					+ "nSizeOfData : " + nSizeOfData + "\n" + "chDelimChar : "
					+ chDelimChar
			);
			
			// Send ServiceHeader
			dataSock_Out.writeInt(nServiceCode);
			dataSock_Out.writeInt(nServiceOpt);
			dataSock_Out.writeBytes(szVersion);
			dataSock_Out.writeInt(nNumOfFile);
			dataSock_Out.writeInt(nSizeOfData);
			dataSock_Out.writeBytes(chDelimChar);
			// flush
			dataSock_Out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " send_ServiceHeader() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			throw new Exception(strException);
		}
		//JSTORDebug.writeTrace(nConnID, "send_ServiceHeader() Completed");
		logger.debug("ConnID : " + nConnID + " send_ServiceHeader() Completed");
	}

	protected synchronized void send_ServiceRegInfo(int nConnID,
			Properties propFile, String[] arrServiceInfo,
			DataOutputStream dataSock_Out) throws Exception {
		String szFileName = null;
		int nFileSize = -1;
		String szFileExt = null;
		int nFilterID = -1;
		int nVolID = -1;
		String szFileID = null;
		String szFileCDate = null;
		try {
			szFileName = propFile.getProperty("FILE_NAME");
			szFileName = getValidFileName(szFileName);
			szFileName = makeValidDataStream(szFileName, SREG_FILENAME_LEN);
			nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));
			szFileExt = propFile.getProperty("FILE_EXT");
			szFileExt = makeValidDataStream(szFileExt, SREG_FILEEXT_LEN);
			nFilterID = Integer.parseInt(arrServiceInfo[SEQ_REGINFO_FLTID]);
			nVolID = Integer.parseInt(arrServiceInfo[SEQ_REGINFO_VOLID]);
			// SZ_REGINFO 만큼의 사이즈라면,
			// file_id 를 외부에서 입력해서 넘겼다는 의미가 됨
			// 덧붙여 파일 생성일시도 입력했는지를 확인
			if (SZ_REGINFO == arrServiceInfo.length) {
				if (null == arrServiceInfo[SEQ_REGINFO_FILEID]) {
					szFileID = "";
				} else {
					szFileID = arrServiceInfo[SEQ_REGINFO_FILEID];
				}
				szFileCDate = arrServiceInfo[SEQ_REGINFO_FILECDATE];
				if (null == szFileCDate || 0 == szFileCDate.length()) {
					szFileCDate = "";
				}
			}
			// (SZ_REGINFO -1) 만큼의 사이즈라면,
			// 파일 생성일시를 입력했는지 확인해야 함
			else if ((SZ_REGINFO - 1) == arrServiceInfo.length) {
				szFileID = "";
				szFileCDate = arrServiceInfo[SEQ_REGINFO_FILECDATE];
				if (null == szFileCDate || 0 == szFileCDate.length()) {
					szFileCDate = "";
				}
			} else {
				szFileID = "";
				szFileCDate = "";
			}
			szFileID = makeValidDataStream(szFileID, SREG_FILEID_LEN);
			if (0 == szFileCDate.length()) {
				szFileCDate = makeValidDataStream(szFileCDate,
						SREG_FILECDATE_LEN);
				// 이 경우, 아래와 같이 반드시 붙여줘야 됨
				szFileCDate += "\0";
			} else {
				szFileCDate = makeValidDataStream(szFileCDate,
						SREG_FILECDATE_LEN);
			}
			// Send ServiceReg Info
			dataSock_Out.writeBytes(szFileName);
			dataSock_Out.writeInt(nFileSize);
			dataSock_Out.writeBytes(szFileExt);
			dataSock_Out.writeInt(nFilterID);
			dataSock_Out.writeInt(nVolID);
			dataSock_Out.writeBytes(szFileID);
			//JSTORDebug.writeTrace(nConnID, "szFileCDate : " + szFileCDate);
			logger.debug("ConnID : " + nConnID + " szFileCDate : " + szFileCDate);
						
			dataSock_Out.writeBytes(szFileCDate);
			// flush
			dataSock_Out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " send_ServiceRegInfo() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			throw new Exception(strException);
		}
		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceRegInfo() for FileReg Completed");*/
		logger.debug("ConnID : " + nConnID + " send_ServiceRegInfo() for FileReg Completed");
	}

	protected synchronized void send_ServiceGetInfo(int nConnID,
			String[] arrServiceInfo, DataOutputStream dataSock_Out)
			throws Exception {
		String szFileID = null;
		int nFilterID = -1;
		try {
			szFileID = arrServiceInfo[SEQ_GETINFO_FILEID];
			szFileID = makeValidDataStream(szFileID, SGET_FILEID_LEN);
			nFilterID = Integer.parseInt(arrServiceInfo[SEQ_GETINFO_FLTID]);
			// Send ServiceGet Info
			dataSock_Out.writeBytes(szFileID);
			dataSock_Out.writeInt(nFilterID);
			// flush
			dataSock_Out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " send_ServiceGetInfo() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			throw new Exception(strException);
		}
		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceGetInfo() for FileGet Completed");*/
		logger.debug("ConnID : " + nConnID + " send_ServiceGetInfo() for FileGet Completed");
	}

	protected synchronized void send_ServiceDelInfo(int nConnID,
			String sServiceInfo, DataOutputStream dataSock_Out)
			throws Exception {
		String szFileID = null;
		try {
			szFileID = sServiceInfo;
			szFileID = makeValidDataStream(szFileID, SDEL_FILEID_LEN);
			// Send ServiceGet Info
			dataSock_Out.writeBytes(szFileID);
			// flush
			dataSock_Out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " send_ServiceDelInfo() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + strException);
			throw new Exception(strException);
		}
		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceDelInfo() for FileGet Completed");*/
		logger.debug("ConnID : " + nConnID + " send_ServiceDelInfo() for FileGet Completed");
	}

	protected synchronized void send_ServiceCpyInfo(int nConnID,
			String[] arrServiceInfo, DataOutputStream dataSock_Out)
			throws Exception {
		String szFileID = null;
		int nVolID = -1;
		int nFltID = -1;
		int nCpyOpt = -1;
		String szIPAddr = null;
		int nPortNo = -1;
		try {
			szFileID = arrServiceInfo[SEQ_CPYINFO_FILEID];
			nVolID = Integer.parseInt(arrServiceInfo[SEQ_CPYINFO_VOLID]);
			nFltID = Integer.parseInt(arrServiceInfo[SEQ_CPYINFO_FLTID]);
			nCpyOpt = Integer.parseInt(arrServiceInfo[SEQ_CPYINFO_CPYOPT]);
			// ////////////////////////////////////////////////////////////
			// 아래와 같이 강제 설정 (동일 저장서버내에서만 동작하도록 함)
			// ////////////////////////////////////////////////////////////
			nCpyOpt = SCPY_CPYIN_OPT;
			// ////////////////////////////////////////////////////////////
			// 동일 저장서버내이므로 의미 없음
			if (null == arrServiceInfo[SEQ_CPYINFO_IPADDR]
					|| 0 == arrServiceInfo[SEQ_CPYINFO_IPADDR].length()) {
				szIPAddr = "";
			} else {
				szIPAddr = arrServiceInfo[SEQ_CPYINFO_IPADDR];
			}
			// 동일 저장서버내이므로 의미 없음
			if (null == arrServiceInfo[SEQ_CPYINFO_PORTNO]
					|| 0 == arrServiceInfo[SEQ_CPYINFO_PORTNO].length()) {
				nPortNo = -1;
			} else {
				/*JSTORDebug.writeTrace(nConnID,
						"send_ServiceCpyInfo(), nPortNo = "
								+ arrServiceInfo[SEQ_CPYINFO_PORTNO]);*/
				
				logger.debug(
						"ConnID : " + nConnID + 
						" send_ServiceCpyInfo(), nPortNo = "
								+ arrServiceInfo[SEQ_CPYINFO_PORTNO]			
				);
				
				nPortNo = Integer.parseInt(arrServiceInfo[SEQ_CPYINFO_PORTNO]);
			}
			/*JSTORDebug.writeTrace(nConnID, "szFileID    : [" + szFileID + "]\n"
					+ "nFltID      : [" + nFltID + "]\n" + "nCpyOpt     : ["
					+ nCpyOpt + "]\n" + "szIPAddr    : [" + szIPAddr + "]\n"
					+ "nPortNo     : [" + nPortNo + "]\n" + "nVolID      : ["
					+ nVolID + "]");*/
			
			logger.debug(
					"ConnID : " + nConnID + " szFileID    : [" + szFileID + "]\n"
					+ "nFltID      : [" + nFltID + "]\n" + "nCpyOpt     : ["
					+ nCpyOpt + "]\n" + "szIPAddr    : [" + szIPAddr + "]\n"
					+ "nPortNo     : [" + nPortNo + "]\n" + "nVolID      : ["
					+ nVolID + "]"			
			);
			
			
			szFileID = makeValidDataStream(szFileID, SCPY_FILEID_LEN);
			szIPAddr = makeValidDataStream(szIPAddr, SCPY_IPADDR_LEN);
			// Send ServiceCpy Info - 순서가 중요함
			dataSock_Out.writeBytes(szFileID);
			dataSock_Out.writeInt(nFltID);
			dataSock_Out.writeInt(nCpyOpt);
			dataSock_Out.writeBytes(szIPAddr);
			dataSock_Out.writeInt(nPortNo);
			dataSock_Out.writeInt(nVolID);
			// flush
			dataSock_Out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " send_ServiceCpyInfo() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			throw new Exception(strException);
		}
		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceCpyInfo() for FileCpy Completed");*/
		logger.debug("ConnID : " + nConnID + " send_ServiceCpyInfo() for FileCpy Completed");		
	}

	protected synchronized void send_ServiceMovInfo(int nConnID,
			String[] arrServiceInfo, DataOutputStream dataSock_Out)
			throws Exception {
		String szFileID = null;
		int nVolID = -1;
		int nFltID = -1;
		int nMovOpt = -1;
		String szIPAddr = null;
		int nPortNo = -1;
		try {
			szFileID = arrServiceInfo[SEQ_MOVINFO_FILEID];
			nVolID = Integer.parseInt(arrServiceInfo[SEQ_MOVINFO_VOLID]);
			nFltID = Integer.parseInt(arrServiceInfo[SEQ_MOVINFO_FLTID]);
			nMovOpt = Integer.parseInt(arrServiceInfo[SEQ_MOVINFO_MOVOPT]);
			// ////////////////////////////////////////////////////////////
			// 아래와 같이 강제 설정 (동일 저장서버내에서만 동작하도록 함)
			// ////////////////////////////////////////////////////////////
			nMovOpt = SMOV_MOVIN_OPT;
			// ////////////////////////////////////////////////////////////
			// 동일 저장서버내이므로 의미 없음
			if (null == arrServiceInfo[SEQ_MOVINFO_IPADDR]
					|| 0 == arrServiceInfo[SEQ_MOVINFO_IPADDR].length()) {
				szIPAddr = "";
			} else {
				szIPAddr = arrServiceInfo[SEQ_MOVINFO_IPADDR];
			}
			// 동일 저장서버내이므로 의미 없음
			if (null == arrServiceInfo[SEQ_MOVINFO_PORTNO]
					|| 0 == arrServiceInfo[SEQ_MOVINFO_PORTNO].length()) {
				nPortNo = -1;
			} else {

				nPortNo = Integer.parseInt(arrServiceInfo[SEQ_MOVINFO_PORTNO]);

			}

			/*JSTORDebug.writeTrace(nConnID, "szFileID    : [" + szFileID + "]\n"
					+

					"nFltID      : [" + nFltID + "]\n" +

					"nMovOpt     : [" + nMovOpt + "]\n" +

					"szIPAddr    : [" + szIPAddr + "]\n" +

					"nPortNo     : [" + nPortNo + "]\n" +

					"nVolID      : [" + nVolID + "]");*/

			logger.debug(
					"ConnID : " +  nConnID +  " szFileID    : [" + szFileID + "]\n"
					+ "nFltID      : [" + nFltID + "]\n" +

					"nMovOpt     : [" + nMovOpt + "]\n" +

					"szIPAddr    : [" + szIPAddr + "]\n" +

					"nPortNo     : [" + nPortNo + "]\n" +

					"nVolID      : [" + nVolID + "]"		
			);
			
			szFileID = makeValidDataStream(szFileID, SMOV_FILEID_LEN);

			szIPAddr = makeValidDataStream(szIPAddr, SMOV_IPADDR_LEN);

			// Send ServiceMov Info - 순서가 중요함

			dataSock_Out.writeBytes(szFileID);

			dataSock_Out.writeInt(nFltID);

			dataSock_Out.writeInt(nMovOpt);

			dataSock_Out.writeBytes(szIPAddr);

			dataSock_Out.writeInt(nPortNo);

			dataSock_Out.writeInt(nVolID);

			// flush

			dataSock_Out.flush();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " send_ServiceMovInfo() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			
			throw new Exception(strException);

		}

		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceMovInfo() for FileMov Completed");*/
		
		logger.debug("ConnID : " + nConnID + " send_ServiceMovInfo() for FileMov Completed");

	}

	protected synchronized void send_ServiceRepInfo(int nConnID,
			Properties propFile, String[] arrServiceInfo,
			DataOutputStream dataSock_Out) throws Exception

	{

		String szFileID = null;

		String szFileName = null;

		String szFileExt = null;

		int nFileSize = -1;

		int nFilterID = -1;

		try

		{

			szFileID = arrServiceInfo[SEQ_REPINFO_FILEID];

			szFileName = propFile.getProperty("FILE_NAME");

			szFileExt = propFile.getProperty("FILE_EXT");

			nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));

			nFilterID = Integer.parseInt(arrServiceInfo[SEQ_REPINFO_FLTID]);

			/*JSTORDebug.writeTrace(nConnID, "send_ServiceRepInfo() : "
					+ szFileID + ", " +

					szFileName + ", " +

					szFileExt + ", " +

					nFileSize + ", " +

					nFilterID);*/
			
			logger.debug(
					"ConnID : " + nConnID +  " send_ServiceRepInfo() : "
					+ szFileID + ", " +

					szFileName + ", " +

					szFileExt + ", " +

					nFileSize + ", " +

					nFilterID				
			);
			
			szFileID = makeValidDataStream(szFileID, SREP_FILEID_LEN);

			szFileName = makeValidDataStream(szFileName, SREP_FILENAME_LEN);

			szFileExt = makeValidDataStream(szFileExt, SREP_FILEEXT_LEN);

			// Send ServiceRep Info

			dataSock_Out.writeBytes(szFileID);

			dataSock_Out.writeBytes(szFileName);

			dataSock_Out.writeBytes(szFileExt);

			dataSock_Out.writeInt(nFileSize);

			dataSock_Out.writeInt(nFilterID);

			// flush

			dataSock_Out.flush();

		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " send_ServiceRepInfo() : " + e.getMessage();

			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			

			throw new Exception(strException);

		}

		/*JSTORDebug.writeTrace(nConnID,
				"send_ServiceRepInfo() for FileRep Completed");*/
		logger.debug("ConnID : " + nConnID + " send_ServiceRepInfo() for FileRep Completed");
		
		

	}

	protected synchronized void send_File(int nConnID, Properties propFile,
			BufferedOutputStream buffSock_Out, DataOutputStream dataSock_Out)
			throws Exception

	{

		FileInputStream fis = null;

		String strFilePath = null;

		int nFileSize = -1;

		int nBytesRead = 0, nTotalSend = 0, nCount = 0;

		try

		{

			strFilePath = propFile.getProperty("FILE_FULL_PATH");
			strFilePath = eraseNullChar(strFilePath);

			nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));

			fis = new FileInputStream(strFilePath);

			while ((nFileSize - nTotalSend) > 0)

			{

				byte[] buffer = new byte[JSTOR_TRANS_AMOUNT];

				nBytesRead = fis.read(buffer);

				buffSock_Out.write(buffer, 0, nBytesRead);

				buffSock_Out.flush();

				nTotalSend += nBytesRead;

				nCount++;

				// User's Cancel Check

				if (((nCount % JSTOR_CHECK_COUNT) == 0 ||

				(nFileSize - nTotalSend) <= 0))

				{

					dataSock_Out.writeInt(JSTOR_NO_CANCEL);

					// dataSock_Out.writeLong (JSTOR_NO_CANCEL);

					dataSock_Out.flush();

				}

			}

		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " send_File() : " + e.getMessage();

			/*JSTORDebug.writeTrace(nConnID, strException);*/
			logger.error("ConnID : " + nConnID + strException);

			throw new Exception(strException);

		}

		finally

		{

			if (null != fis)
				fis.close();

		}

		//TORDebug.writeTrace(nConnID, "send_File() Completed");
		logger.debug("ConnID : " + nConnID + " send_File() Completed");

	}

	protected synchronized void recv_File(int nConnID, String strFilePath,
			int nFileSize, BufferedInputStream buffSock_In,
			DataOutputStream dataSock_Out) throws Exception

	{

		File file = null;

		DataOutputStream fout = null;

		FileOutputStream fout_stream = null;

		BufferedOutputStream fout_buff = null;

		int nPartBytesRead = 0, nBytesRead = 0, nTotalRecv = 0, nCount = 0;

		int RECV_BUFF_SIZE = JSTOR_TRANS_AMOUNT;

		// 아래값이 튜닝성 수치 : 소캣 에러등과 같은 경우에

		// 이 수치를 작게 조정해 주면 해결되는 경우가 있음

		// 크게 조정하면 속도는 개선됨

		// 작게 조정하면 안정성이 개선됨

		// int RECV_PART_BUFF_SIZE = JSTOR_TRANS_AMOUNT / (1024 * 8);
		// int ITER_COUNT = 64;
		int ITER_COUNT = 1024 * 8;
		int RECV_PART_BUFF_SIZE = JSTOR_TRANS_AMOUNT / ITER_COUNT;

		byte[] buff_Recv = null;

		byte[] buff_PartRecv = null;

		try

		{

			file = new File(strFilePath);

			fout_stream = new FileOutputStream(file);

			fout_buff = new BufferedOutputStream(fout_stream);

			fout = new DataOutputStream(fout_buff);

			while ((nFileSize - nTotalRecv) > 0)

			{

				buff_Recv = new byte[RECV_BUFF_SIZE];

				nBytesRead = 0;

				for (int iter1 = 0; iter1 < ITER_COUNT; iter1++)

				{

					if ((JSTOR_TRANS_AMOUNT - nBytesRead) < RECV_PART_BUFF_SIZE)

					{

						buff_PartRecv = new byte[JSTOR_TRANS_AMOUNT
								- nBytesRead];

					}

					else

					{

						buff_PartRecv = new byte[RECV_PART_BUFF_SIZE];

					}

					nPartBytesRead = buffSock_In.read(buff_PartRecv);

					nBytesRead += nPartBytesRead;

					for (int iter2 = 0; iter2 < nPartBytesRead; iter2++)

					{

						buff_Recv[iter1 * RECV_PART_BUFF_SIZE + iter2] = buff_PartRecv[iter2];

					}

					if ((nFileSize - nTotalRecv) <= nBytesRead)

					{

						break;

					}

				}

				fout.write(buff_Recv, 0, nBytesRead);

				fout.flush();

				nTotalRecv += nBytesRead;

				nCount++;

				// User's Cancel Check

				if (((nCount % (JSTOR_CHECK_COUNT)) == 0 ||

				(nFileSize - nTotalRecv) <= 0))

				{

					dataSock_Out.writeInt(JSTOR_NO_CANCEL);

					dataSock_Out.flush();

				}

			}

		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " recv_File() : " + e.getMessage();

			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			

			throw new Exception(strException);

		}

		finally

		{

			if (null != fout_stream)
				fout_stream.close();

			if (null != fout_buff)
				fout_buff.close();

			if (null != fout)
				fout.close();

		}

		//JSTORDebug.writeTrace(nConnID, "recv_File() for FileGet Completed");
		logger.debug("ConnID : " + nConnID + " recv_File() for FileGet Completed");		

	}

	protected synchronized String[] recv_ReplyHeader(int nConnID,
			DataInputStream dataSock_In) throws Exception {
		String[] arrRet = null;
		int nReturnStatus = -1;
		int nNumOfList = -1;
		int nSizeOfData = -1;
		char chDelimChar;
		try {
			nReturnStatus = dataSock_In.readInt();
			nNumOfList = dataSock_In.readInt();
			nSizeOfData = dataSock_In.readInt();
			chDelimChar = (char) dataSock_In.read();
			dataSock_In.read();
			dataSock_In.read();
			dataSock_In.read();
			arrRet = new String[SZ_RECV_RH];
			arrRet[SEQ_RECV_RH_STATUS] = "" + nReturnStatus;
			arrRet[SEQ_RECV_RH_DATASIZE] = "" + nSizeOfData;
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " recv_ReplyHeader() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			
			throw new Exception(strException);
		}
		//JSTORDebug.writeTrace(nConnID, "recv_ReplyHeader() Completed");
		logger.debug("ConnID : " + nConnID + " recv_ReplyHeader() Completed");
		

		// below : no means
		chDelimChar = (char) nNumOfList;
		nNumOfList = chDelimChar;

		return arrRet;
	}

	protected synchronized String recv_ReplyMsg(int nConnID, int nSizeOfData,
			DataInputStream dataSock_In) throws Exception

	{

		String strMsg = null;

		try

		{

			byte[] bBytes = new byte[nSizeOfData];

			dataSock_In.read(bBytes, 0, nSizeOfData);

			strMsg = new String(bBytes);

			/*JSTORDebug.writeTrace(nConnID, "recv_ReplyMsg() - Receved Msg : "
					+ strMsg);*/
			logger.debug("ConnID : " + nConnID +  " recv_ReplyMsg() - Receved Msg : " + strMsg);
		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " recv_ReplyMsg() : " + e.getMessage();

			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);

			throw new Exception(strException);

		}

		return strMsg;

	}

	protected synchronized int recv_ReplyGetInfo(int nConnID,
			DataInputStream dataSock_In) throws Exception

	{

		int nRet = -1;

		try

		{

			byte[] bBytes = new byte[JSTOR_TRANS_AMOUNT];

			// File ID

			dataSock_In.read(bBytes, 0, SGET_FILEID_LEN);

			// FilterID

			int nFilterID = dataSock_In.readInt();

			// nFileSize

			int nFileSize = dataSock_In.readInt();

			nRet = nFileSize;

			/*JSTORDebug.writeTrace(nConnID, "recv_ReplyGetInfo() Completed" +
			"\nFileID : " + new String(bBytes) +
			"\nnFilterID : " + nFilterID +
			"\nnFileSize : " + nFileSize);*/
			
			logger.debug(
					"ConnID : " + nConnID +  " recv_ReplyGetInfo() Completed" +
					"\nFileID : " + new String(bBytes) +
					"\nnFilterID : " + nFilterID +
					"\nnFileSize : " + nFileSize);

		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " recv_ReplyGetInfo() : " + e.getMessage();

			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);

			throw new Exception(strException);

		}

		return nRet;

	}

	protected synchronized String[] recv_ReplyVolInfo(int nConnID,
			DataInputStream dataSock_In) throws Exception

	{

		String[] arrRet = new String[SZ_VOL_INFO];

		try

		{

			byte[] bBytes = null;

			// ///////////////////////////////////////////////////

			arrRet[SEQ_VOL_ID] = "" + dataSock_In.readInt();

			// ///////////////////////////////////////////////////

			// ///////////////////////////////////////////////////

			bBytes = new byte[JSTOR_TRANS_AMOUNT];

			dataSock_In.read(bBytes, 0, SVOL_NAME_LEN);

			arrRet[SEQ_VOL_NAME] = new String(bBytes);

			// ///////////////////////////////////////////////////

			// ///////////////////////////////////////////////////

			bBytes = new byte[JSTOR_TRANS_AMOUNT];

			dataSock_In.read(bBytes, 0, SVOL_EAME_LEN);

			arrRet[SEQ_VOL_ENAME] = new String(bBytes);

			// ///////////////////////////////////////////////////

			// ///////////////////////////////////////////////////

			arrRet[SEQ_VOL_TYPE] = "" + dataSock_In.readInt();

			// ///////////////////////////////////////////////////

			dataSock_In.read();

			// ///////////////////////////////////////////////////

			arrRet[SEQ_VOL_RIGHT] = "" + dataSock_In.readInt();

			// ///////////////////////////////////////////////////

			// ///////////////////////////////////////////////////

			bBytes = new byte[JSTOR_TRANS_AMOUNT];

			dataSock_In.read(bBytes, 0, SVOL_CDATE_LEN);

			arrRet[SEQ_VOL_CDATE] = new String(bBytes);

			// ///////////////////////////////////////////////////

			// ///////////////////////////////////////////////////

			bBytes = new byte[JSTOR_TRANS_AMOUNT];

			dataSock_In.read(bBytes, 0, SVOL_DESC_LEN);

			arrRet[SEQ_VOL_DESC] = new String(bBytes);

			// ///////////////////////////////////////////////////

			/*JSTORDebug.writeTrace(nConnID, "recv_ReplyVolInfo() Completed"
					+ "\n" +
					"Vol ID    = [" + arrRet[SEQ_VOL_ID] + "]\n" +
					"Vol Name  = [" + arrRet[SEQ_VOL_NAME] + "]\n" +
					"Vol EName = [" + arrRet[SEQ_VOL_ENAME] + "]\n" +
					"Vol Type  = [" + arrRet[SEQ_VOL_TYPE] + "]\n" +
					"Vol Right = [" + arrRet[SEQ_VOL_RIGHT] + "]\n" +
					"Vol CDate = [" + arrRet[SEQ_VOL_CDATE] + "]\n" +
					"Vol Desc  = [" + arrRet[SEQ_VOL_DESC] + "]");
				*/
			logger.debug(
					"ConnID : " + nConnID + " recv_ReplyVolInfo() Completed"
					+ "\n" +
					"Vol ID    = [" + arrRet[SEQ_VOL_ID] + "]\n" +
					"Vol Name  = [" + arrRet[SEQ_VOL_NAME] + "]\n" +
					"Vol EName = [" + arrRet[SEQ_VOL_ENAME] + "]\n" +
					"Vol Type  = [" + arrRet[SEQ_VOL_TYPE] + "]\n" +
					"Vol Right = [" + arrRet[SEQ_VOL_RIGHT] + "]\n" +
					"Vol CDate = [" + arrRet[SEQ_VOL_CDATE] + "]\n" +
					"Vol Desc  = [" + arrRet[SEQ_VOL_DESC] + "]"			
			);
			
			
		}

		catch (Exception e)

		{

			e.printStackTrace();

			String strException = " recv_ReplyGetInfo() : " + e.getMessage();

			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);

			throw new Exception(strException);

		}

		return arrRet;

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// JSTOR API For Win32 Storage Server Section
	//
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected void Send(byte[] buffer, int nbyte,
			BufferedOutputStream buffSock_Out) throws Exception {
		for (int i = 0; (i < buffer.length) && (i < nbyte); i++) {
			buffSock_Out.write(buffer[i]);
		}

		buffSock_Out.flush();
	}

	public String Recv(int nDataSize, BufferedInputStream buffSock_In)
			throws Exception {
		byte[] val = new byte[nDataSize];

		for (int i = 0; i < nDataSize; i++) {
			val[i] = (byte) buffSock_In.read();
		}

		return (new String(val));
	}

	protected synchronized void send_FileGetReqInfo_Win32(int nConnID,
			int nNumOfFile, String[] sInfoGetArr, BufferedOutputStream buff_Sock)
			throws Exception {
		String strInfo = "";

		// nNumOfFile
		strInfo += ("" + nNumOfFile);
		// delim str
		strInfo += "\t";

		// 파일 아이디
		strInfo += sInfoGetArr[SEQ_GETINFO_FILEID];
		// delim str
		strInfo += "\t";
		// 필터 아이디
		strInfo += sInfoGetArr[SEQ_GETINFO_FLTID];

		// byte 변환
		byte[] byteInfo = strInfo.getBytes();

		// 전체 문자열 크기를 먼저 보냄 (10 자리 포맷)
		Send(getFormat10(byteInfo.length), 10, buff_Sock);
		// 실제 문자열을 보냄
		Send(byteInfo, byteInfo.length, buff_Sock);
	}

	protected synchronized void send_DisconnectReqInfo_Win32(int nConnID,
			BufferedOutputStream buff_Sock) throws Exception {
		String strInfo = "";

		// service code
		strInfo += "QUIT";

		// byte 변환
		byte[] byteInfo = strInfo.getBytes();

		// 전체 문자열 크기를 먼저 보냄 (10 자리 포맷)
		Send(getFormat10(byteInfo.length), 10, buff_Sock);
		// 실제 문자열을 보냄
		Send(byteInfo, byteInfo.length, buff_Sock);
	}

	protected synchronized String recv_ReplyInfo_Win32(int nConnID,
			BufferedInputStream m_buffSock_In) throws Exception {
		// 10 자리 크기의 수신할 데이터 크기를 먼저 수신한다 (고정값)
		String strDataSize = Recv(10, m_buffSock_In);
		// 수신한 데이터 크기만큼 데이터를 수신한다
		String strMsg = Recv(Integer.parseInt(strDataSize), m_buffSock_In);

		return strMsg;
	}

	protected synchronized void send_FileRegReqInfo_Win32(int nConnID,
			int nNumOfFile, Properties propFile, String[] sInfoRegArr,
			BufferedOutputStream buff_Sock) throws Exception {
		String strFileName = propFile.getProperty("FILE_NAME");
		int nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));
		String strFileExt = propFile.getProperty("FILE_EXT");
		int nFilterID = Integer.parseInt(sInfoRegArr[SEQ_REGINFO_FLTID]);
		int nVolID = Integer.parseInt(sInfoRegArr[SEQ_REGINFO_VOLID]);
		String strFileID = "_N_A_";
		String strFileCDate = "_N_A_";

		// SZ_REGINFO 만큼의 사이즈라면,
		// file_id 를 외부에서 입력해서 넘겼다는 의미가
		if (SZ_REGINFO == sInfoRegArr.length) {
			if (null == sInfoRegArr[SEQ_REGINFO_FILEID]) {
				strFileID = "_N_A_";
			} else {
				strFileID = sInfoRegArr[SEQ_REGINFO_FILEID];
			}
		} else {
			strFileID = "_N_A_";
		}

		String strInfo = "";

		// nNumOfFile
		strInfo += ("" + nNumOfFile);
		// delim str
		strInfo += "\t";

		strInfo += strFileName;
		strInfo += "\t";

		strInfo += ("" + nFileSize);
		strInfo += "\t";

		if (null == strFileExt || 0 == strFileExt.length()) {
			//JSTORDebug.writeTrace(nConnID, "strFileExt is blank");
			logger.debug("ConnID : " + nConnID +  " strFileExt is blank");
			strFileExt = strFileName;
		}

		strInfo += strFileExt;
		strInfo += "\t";

		strInfo += ("" + nFilterID);
		strInfo += "\t";

		strInfo += ("" + nVolID);
		strInfo += "\t";

		strInfo += strFileID;
		strInfo += "\t";

		strInfo += strFileCDate;

		// byte 변환
		byte[] byteInfo = strInfo.getBytes();

		// 전체 문자열 크기를 먼저 보냄 (10 자리 포맷)
		Send(getFormat10(byteInfo.length), 10, buff_Sock);
		// 실제 문자열을 보냄
		Send(byteInfo, byteInfo.length, buff_Sock);
	}

	// Only For Used Win32 FileStorage Server
	public boolean DownFile(int nConnID, String varAthInfoPath, int fileLength,
			BufferedInputStream is) throws Exception {
		File file = null;
		DataOutputStream fout = null;
		FileOutputStream fout_stream = null;
		BufferedOutputStream fout_buff = null;

		try {
			file = new File(varAthInfoPath);
			file.createNewFile();

			fout_stream = new FileOutputStream(file);
			fout_buff = new BufferedOutputStream(fout_stream);
			fout = new DataOutputStream(fout_buff);

			int totaldown = 0;
			int transbuf = JSTOR_TRANS_AMOUNT_WIN32;
			int nCount = 0;

			while ((fileLength - totaldown) > 0) {
				if ((fileLength - totaldown) < JSTOR_TRANS_AMOUNT_WIN32) {
					transbuf = fileLength - totaldown;
				}

				byte[] val = new byte[transbuf];

				val = Down(nConnID, transbuf, is);

				//JSTORDebug.writeTrace(nConnID, new String(val));
				logger.debug("ConnID : " + nConnID +  new String(val));				

				fout.write(val, 0, transbuf);
				fout.flush();

				totaldown += transbuf;

				nCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " DownFile() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			
			throw new Exception(strException);
		} finally {
			if (null != fout_stream)
				fout_stream.close();
			if (null != fout_buff)
				fout_buff.close();
			if (null != fout)
				fout.close();
		}

		//JSTORDebug.writeTrace(nConnID, "DownFile() Completed");
		logger.debug("ConnID : " + nConnID + " DownFile() Completed");

		return true;
	}

	// Only For Used Win32 FileStorage Server
	public boolean DownFileEx(int nConnID, String varAthInfoPath,
			int fileLength, BufferedInputStream is) throws Exception {
		File file = null;
		DataOutputStream fout = null;
		FileOutputStream fout_stream = null;
		BufferedOutputStream fout_buff = null;

		try {
			file = new File(varAthInfoPath);

			fout_stream = new FileOutputStream(file);
			fout_buff = new BufferedOutputStream(fout_stream);
			fout = new DataOutputStream(fout_buff);

			int nBytesRead = 0, nTotalDown = 0, nCount = 0;

			while ((fileLength - nTotalDown) > 0) {
				byte[] buffer = new byte[JSTOR_TRANS_AMOUNT_WIN32];
				nBytesRead = is.read(buffer);

				//JSTORDebug.writeTrace(nConnID, new String(buffer));
				logger.debug("ConnID : " + nConnID +  new String(buffer));

				fout.write(buffer, 0, nBytesRead);
				fout.flush();

				nTotalDown += nBytesRead;

				nCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " DownFileEx() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			
			throw new Exception(strException);
		} finally {
			if (null != fout_stream)
				fout_stream.close();
			if (null != fout_buff)
				fout_buff.close();
			if (null != fout)
				fout.close();
		}

		//JSTORDebug.writeTrace(nConnID, "DownFileEx() Completed");
		logger.debug("ConnID : " + nConnID + " DownFileEx() Completed");		

		return true;
	}

	// Only For Used Win32 FileStorage Server
	protected synchronized void UploadFile(int nConnID, Properties propFile,
			BufferedOutputStream buffSock_Out, DataOutputStream dataSock_Out)
			throws Exception {
		FileInputStream fis = null;
		String strFilePath = null;
		int nFileSize = -1;
		int nBytesRead = 0, nTotalSend = 0, nCount = 0;

		try {
			strFilePath = propFile.getProperty("FILE_FULL_PATH");
			strFilePath = eraseNullChar(strFilePath);
			nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));

			fis = new FileInputStream(strFilePath);

			while ((nFileSize - nTotalSend) > 0) {
				byte[] buffer = new byte[JSTOR_TRANS_AMOUNT_WIN32];

				nBytesRead = fis.read(buffer);

				buffSock_Out.write(buffer, 0, nBytesRead);
				buffSock_Out.flush();

				nTotalSend += nBytesRead;
				nCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String strException = " UploadFile() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			
			throw new Exception(strException);
		} finally {
			if (null != fis)
				fis.close();
		}

		//JSTORDebug.writeTrace(nConnID, "UploadFile() Completed");
		logger.debug("ConnID : " + nConnID + " UploadFile() Completed");
		
	}

	// Only For Used Win32 FileStorage Server
	public byte[] Down(int nConnID, int nbyte, BufferedInputStream is)
			throws Exception {
		byte[] val = new byte[nbyte];

		try {
			for (int i = 0; i < nbyte; i++) {
				val[i] = (byte) is.read();
			}

		} catch (Exception e) {
			e.printStackTrace();
			String strException = " Down() : " + e.getMessage();
			//JSTORDebug.writeTrace(nConnID, strException);
			logger.error("ConnID : " + nConnID + strException);
			throw new Exception(strException);
		}

		//JSTORDebug.writeTrace(nConnID, "Down() Completed");\
		logger.debug("ConnID : " + nConnID + " Down() Completed");
		

		return val;
	}

	protected synchronized void send_FileRepReqInfo_Win32(int nConnID,
			int nNumOfFile, Properties propFile, String[] sInfoRepArr,
			BufferedOutputStream buff_Sock) throws Exception {

		String strFileID = sInfoRepArr[SEQ_REPINFO_FILEID];
		String strFileName = propFile.getProperty("FILE_NAME");
		String strFileExt = propFile.getProperty("FILE_EXT");
		int nFileSize = Integer.parseInt(propFile.getProperty("FILE_SIZE"));
		int nFilterID = Integer.parseInt(sInfoRepArr[SEQ_REPINFO_FLTID]);

		String strInfo = "";

		// nNumOfFile
		strInfo += ("" + nNumOfFile);
		// delim str
		strInfo += "\t";

		strInfo += strFileID;
		strInfo += "\t";

		strInfo += strFileName;
		strInfo += "\t";

		if (null == strFileExt || 0 == strFileExt.length()) {
			//JSTORDebug.writeTrace(nConnID, "strFileExt is blank");
			logger.debug("ConnID : " + nConnID +  " strFileExt is blank");
			
			strFileExt = strFileName;
		}

		strInfo += strFileExt;
		strInfo += "\t";

		strInfo += ("" + nFileSize);
		strInfo += "\t";

		strInfo += ("" + nFilterID);

		// byte 변환
		byte[] byteInfo = strInfo.getBytes();

		// 전체 문자열 크기를 먼저 보냄 (10 자리 포맷)
		Send(getFormat10(byteInfo.length), 10, buff_Sock);
		// 실제 문자열을 보냄
		Send(byteInfo, byteInfo.length, buff_Sock);
	}

	protected String eraseNullChar(String str) {

		String strRet = "";
		String strTmp = "";

		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				strTmp = str.substring(i, i + 1);
				if (" ".equals(strTmp)) {
					strRet = strRet + strTmp;
				} else {
					strRet = strRet + strTmp.trim();
				}
			}
		} else {
			strRet = null;
		}

		return strRet;
	}
}
