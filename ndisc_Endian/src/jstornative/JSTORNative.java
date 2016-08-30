/*===================================================================================
 *
 * 작성자 : 조 국 영
 *
 * 클래스 이름 : jstornative/JSTORNative.class
 *
 * 설명 : 
 *
 * 개발 시작일 : 2001/10/01
 *
 * 개발 종료일 : 0000/00/00
 *
 * 수정 내역 : 아래와 같음
 *              - 2002/02/xx : 필터 아이디가 숫자형으로 변환 가능한지 확인 하는 부분.
 *              - 2002/02/22 : 저장서버 서비스 요청 문자열의 크기를 늘림. (너무 클 경우, 처리하는 로직 삽입)
 *              - 2002/03/04 : 스테틱 구문에서 라이브러리 로딩하는 부분에, 익셉션 처리 추가
 *              - 2002/03/04 : 파일 등록/복사시, 리턴받는 아이디 부분에 32자 서브 스트링 처리 부분을 추가 (쓰레기값 방지)
 *			    - 2002/06/12 : 그동안 많은 수정작업이 있었음 (필설로 하기 힘듦)
 *              - 2002/07/xx : Brokerage 파일 서비스 함수 추가
 *			    - 2002/08/13 : Disconnect 시에 Connection ID 검사
 *			    - 2002/12/17 : 각 API 내에서 null string 처리 검사
 *              - 2003/01/10 : 국민건강보험공단 관련해서 오류처리 로직 강화, 파일 등록 함수에서 파일갯수 제한 해제
 *              - 2003/02/03 : Fasoo DRM 적용가능한 API 추가 (DRM 적용 파일반출)
 *
 * 버젼 : ver 7.2
 *
 * Copyright notice : Copyright (C) 2000 by SAMSUNG SDS co.,Ltd. All right reserved.
 *
 ===================================================================================*/

 
package jstornative;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sds.acube.jstor.JSTORApi;

/**
 * C, C++ 기반의 Native Code로 작성된 STORPApi Library와 JAVA간의 연계를 구현
 * @version 8.2
 * @author 조국영 
 */
public class JSTORNative extends JSTORApi
{
	/* Native Library 로딩 */
	static 
	{
		System.out.println ("\n");
		System.out.println ("---------------------------");
		System.out.println ("▶ Company :  SAMSUNG SDS"  );
		System.out.println ("▶ Product Name : JSTOR API");
		System.out.println ("▶ Version : JNI 2003.11.6");
		System.out.println ("---------------------------");
		System.out.println ("\n");

		try
		{
			System.loadLibrary("JSTORApi");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/* 이하 : Native Function -------------------------------------------------------------------------- */
	private native synchronized byte[] NtvJSTOR_Connect       (byte[] szIPAddr, int nPortNo);
	private native synchronized void   NtvJSTOR_Disconnect    (int nConnID);

	private native synchronized byte[] NtvJSTOR_FileReg       (int nConnID, int nNumOfFile, byte[] szInfoReg, int nGUIFlag);
	private native synchronized byte[] NtvJSTOR_FileGet       (int nConnID, int nNumOfFile, byte[] szInfoGet, int nGUIFlag);

	private native synchronized byte[] NtvJSTOR_BrokerFileReg (int nConnID, int nNumOfFile, byte[] szInfoReg, byte[] szInfoBrokerage, int nGUIFlag);
	private native synchronized byte[] NtvJSTOR_BrokerFileGet (int nConnID, int nNumOfFile, byte[] szInfoGet, byte[] szInfoBrokerage, int nGUIFlag);

	private native synchronized byte[] NtvJSTOR_FileDel		  (int nConnID, int nNumOfFile, byte[] szInfoDel);
	private native synchronized byte[] NtvJSTOR_FileRep		  (int nConnID, int nNumOfFile, byte[] szInfoRep, int nGUIFlag);
	private native synchronized byte[] NtvJSTOR_FileCpy		  (int nConnID, int nNumOfFile, byte[] szInfoCpy);
	private native synchronized byte[] NtvJSTOR_FileMov		  (int nConnID, int nNumOfFile, byte[] szInfoMov);
	private native synchronized byte[] NtvJSTOR_FileInfo	  (int nConnID, int nNumOfFile, byte[] szInfoInfo);
	private native synchronized byte[] NtvJSTOR_VolInfo		  (int nConnID);

	private native synchronized byte[] NtvJSTOR_Commit		  (int nConnID);
	private native synchronized byte[] NtvJSTOR_Rollback	  (int nConnID);
	/* 여기까지 ----------------------------------------------------------------------------------------- */

	private int        m_nErrCode;         /* 에러 코드 */
	private String     m_sErrMsg;          /* 에러 메세지 */
	private String[]   m_sRegFileIDArr;    /* 파일 등록이 성공했을 경우, 반환되는 파일 아이디 */
	private String[][] m_sVolInfoArr;      /* 저장서버의 볼륨 정보 */
	private String[][] m_sFileInfoArr;     /* 저장서버의 파일 정보 */
	private String[]   m_sNewCpyFileIDArr; /* 복사 성공한 파일의 New File ID */

	private String[]   m_sOutDrmFilePath;	   /* DRM 파일이 생성된 경로명/파일명(성공시), 에러 메세지 (실패시) */

	private final int    nExceptErr		= -119;  /* Exception Occered */
	private final int    nOverReqErr	= -112; /* Service Request String is Overflowed */
	private final int    nNullPointErr	= -117;
	private final int	 nUnKnownDRMErr = -200;     /* DRM Related Error */

	private final String DELIM_STR = "\t";

	/* ---------------------------------------------------------------------------- */
	// private final int FILE_REG_MAX = 52;   /* 한번에 등록 가능한 최대 파일 갯수 */
	// private final int FILE_GET_MAX = 50;   /* 한번에 반출(Get) 가능한 최대 파일 갯수 */
	// private final int FILE_DEL_MAX = 433;  /* 한번에 삭제 가능한 최대 파일 갯수 */
	// private final int FILE_REP_MAX = 50;   /* 한번에 교체 가능한 최대 파일 갯수 */
	// private final int FILE_CPY_MAX = 66;   /* NewCopyFileID 와 연동해서 생각, 한번에 복사 가능한 최대 파일 갯수 */
	// private final int FILE_MOV_MAX = 155;  /* 한번에 이동 가능한 최대 파일 갯수 */
	// private final int FILE_INF_MAX = 10;   /* getFileInfo 와 연동해서 생각, 한번에 조회 가능한 최대 파일 갯수 */
	/* ---------------------------------------------------------------------------- */

	// 위와 같이 산출된 근거는 다음과 같다.
	// FileGet 기준으로, 한번에 최대 50개의 파일을 반출(Get) 가능하도록 스펙을 정의했을 때,
	// 단위 FileGet 이 요청하는 1개 파일에 대한 정보는 최대 295 byte 가 된다. (여기서 File 의 최대 Full Path 는 256 byte 라고 가정)
	// 따라서, 50*295 = 14750 byte 가 된다.
	// 위 값을 각 서비스에 필요한 최대 단위 파일 정보로 나누면, 각 서비스마다 한번에 서비스 가능한
	// 최대 파일 갯수가 나온다.
	// 참고로, 14750 byte 는 JNI 의 shared library 에서 최대 서비스 가능 Requset 문자열의 사이즈로 Define 된다. 
	// *** 위를 주석으로 막은 이유 : 일단 해당 서비스 API 에서, 최대 서비스 가능한 문자열의 크기를 14750 으로 비교 *** 

	private final int REQUEST_MAX = 14749;    /* 14750 -1 */

	public static void main (String args[])
	{
		System.out.println ("\n");
		System.out.println ("▶ Company :  SAMSUNG SDS");
		System.out.println ("▶ Product Name : JSTOR API");
		System.out.println ("▶ Version : 8.2");
		System.out.println ("\n");
	}

   /** 
    * <pre>
    * STORServ 연결 Api
	* @param String sIPAddr => STORServ IP 또는 Host Name(단, 64자 이상은 넘지 말 것)
    * @param int nPortNo => STORServ Port Number
    * @return int => 연결 성공(Connection ID), 연결 실패(-1)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_Connect (String sIPAddr, int nPortNo)
	{
		int      nRet = 0;
		String   sRet;
	
		if (null == sIPAddr)
		{
			m_nErrCode = nNullPointErr;
			m_sErrMsg = "Unexpected NULL Character is Included";

			return -1;
		}

		sIPAddr = eraseNullCharEx(sIPAddr); 

		sIPAddr += DELIM_STR;

		try
		{
			byte[] szIPAddr = sIPAddr.getBytes("KSC5601");

			sRet = new String (NtvJSTOR_Connect (szIPAddr, nPortNo), "KSC5601");   

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet < 0)
			{
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		} 
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_Connect() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_Connect() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_Connect() !";
			return -1;
		}
		
		return nRet;
	}

   /** 
    * <pre>
    * STORserv 연결 종료 Api
    * @param int nConnID => Connection ID
	* @return void 
	* </pre>
    */
	public void JSTOR_Disconnect (int nConnID)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR may be disconnected from STOR SERVER";
			return;
		}
		// *** End : Connection Check ***
		
		NtvJSTOR_Disconnect (nConnID);
	}

   /** 
    * <pre>
    * STORServ 파일등록 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일등록 서비스 대상 파일의 갯수 
    * @param String sInfoRegArr[i][0] => 등록할 File Path 
    * @param String sInfoRegArr[i][1] => 등록 대상 Volume ID 
    * @param String sInfoRegArr[i][2] => 등록시 적용할 Fileter ID
	* (ex : 0 -> NONE, 1 -> 압축(GZIP 방식), 2 -> 암호화, 3 -> 압축 + 암호화)
    * @param String sInfoRegArr[i][3] => FileCDate(option) 사용하지 않을 시에는 반드시 null 을 대입할 것
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음
	* (ex : 0 -> NO, 1 -> YES) 
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
    public int JSTOR_FileReg (int nConnID, int nNumOfFile, String[][] sInfoRegArr, int nGUIFlag)
	{

		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoRegString = "";
		byte[] szInfoReg;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			for (int col = 0; col < 3; col++)
			{
				if (null == sInfoRegArr[index][col])
				{
					m_nErrCode = nNullPointErr;
					m_sErrMsg = "Unexpected NULL Character is Included";

					return -1;
				}
			}
		}
		// *** End : Verify Inputed Info String Array *****

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sTest = "";   // 유효성 테스트 변수
		int	   nTest;        // 유효성 테스트 변수

		try
		{
			for (int i=0; i<nNumOfFile; i++)
			{
				sTest = eraseNullCharEx(sInfoRegArr[i][1]);
				nTest = Integer.parseInt(sTest);

				sTest = eraseNullCharEx(sInfoRegArr[i][2]);
				nTest = Integer.parseInt(sTest);
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileReg(), Service Request Info Array has invalied Value !";
			return -1;
		}	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		for (int i=0; i<nNumOfFile; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if ((j == 3) && (sInfoRegArr[i][j] == null))
				{
					sInfoRegString+="NOT_USED";
				}
				else
				{
					sInfoRegArr[i][j] = eraseNullCharEx(sInfoRegArr[i][j]); 

					sInfoRegString+=sInfoRegArr[i][j];
				}

				sInfoRegString+=DELIM_STR;
			}
		}

		try
		{
			szInfoReg = sInfoRegString.getBytes("KSC5601");

			int nServiceSize = szInfoReg.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileReg(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}
/*
			if (nNumOfFile > 66)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Files Error, In JSTOR_FileReg(), [Current : " + nNumOfFile + ", Recommend : Under 66]";
				return -1;
			}
*/
			sRet = new String (NtvJSTOR_FileReg (nConnID, nNumOfFile, szInfoReg, nGUIFlag), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet == 0)
			{
				m_sRegFileIDArr = new String[nNumOfFile];
				for (int i = 0; i < nNumOfFile; i++)
				{
					String sSubTmp = "";
					sSubTmp = sTK.nextToken();
 					m_sRegFileIDArr[i] = sSubTmp.substring (0, 32);
				}
			}
			else
			{
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileReg() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileReg() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileReg() !";
			return -1;
		}

		return nRet;
	}


   /** 
    * <pre>
    * STORServ 파일등록 서비스 Api (중계(Broker) 전송 방식)
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일등록 서비스 대상 파일의 갯수 
	* @param Vector[] vInfoRegArr => 파일등록 정보
	* vInfoRegArr[i].elementAt(0) => [String] 등록 대상 파일의 Full Path (등록할 파일이 존재하는 컴퓨터에서의 File Full Path)
	* vInfoRegArr[i].elementAt(1) => [Integer] 등록할 대상 저장서버의 Volume ID
	* vInfoRegArr[i].elementAt(2) => [Integer] 등록시 적용할 Filter ID
	* (ex : 0 -> NONE, 1 -> 압축(GZIP 방식), 2 -> 암호화, 3 -> 압축 + 암호화)
	* vInfoRegArr[i].elementAt(3) => [String] 파일 등록시 강제로 설정할 File Create Date
	* (저장서버 DB 에 각 파일의 생성일자(14자리)를 기록하는 필드가 있음, 필요없으면 SKIP)
	* (이 때는 파일이 저장서버에 등록되는 일시가 기본으로 지정됨)
    * @param Vector vInfoBrokerage => Brokerage 정보
	* vInfoBrokerage.elementAt(0) => [String] 등록할 파일이 있는 컴퓨터의 IP 또는 Host Name (SFTPD 가 설치되어 있어야 함)
	* vInfoBrokerage.elementAt(1) => [Integer] 위 컴퓨터의 SFTPD Port
	* vInfoBrokerage.elementAt(2) => [Integer] 파일전송 중개 옵션 
	* (ex : 0 -> 컴퓨터간의 직접 전송, 1 -> Broker 역할을 하는 컴퓨터를 통한 간접 전송)
	* (Broker 역할을 하는 컴퓨터는 "JSTOR JNI Java Class API" 를 호출하는 컴퓨터" 를 말한다)
	* vInfoBrokerage.elementAt(3) => [String] 파일전송 중개시 필요한 임시 디렉토리
	* (ex : 중개 옵션 0 -> 저장서버가 설치된 컴퓨터에 임시 디렉토리를 설정해야 함)
	* (ex : 중개 옵션 1 -> Broker 역할을 하는 컴퓨터에 임시 디렉토리를 설정)
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음 (Deprecated)
	* (ex : 0 -> NO, 1 -> YES) 
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_BrokerFileReg (int nConnID, int nNumOfFile, Vector[] vInfoRegArr, Vector vInfoBrokerage, int nGUIFlag)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;

		String sRet;

		String sInfoRegString = "";
		byte[] szInfoReg;

		String sInfoBrokerage = "";
		byte[] szInfoBrokerage;

		for (int i=0; i<nNumOfFile; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				sInfoRegString += eraseNullCharEx(vInfoRegArr[i].elementAt(j).toString());
				sInfoRegString += DELIM_STR;
			}

			if (vInfoRegArr[i].size() == 4)   // File CDate Is Inputed
			{
				sInfoRegString += eraseNullCharEx(vInfoRegArr[i].elementAt(4-1).toString());
				sInfoRegString += DELIM_STR;
			}
			else   // File CDate Is Skipped
			{
				sInfoRegString += eraseNullCharEx("NOT_USED");
				sInfoRegString += DELIM_STR;
			}

			/*
			for (int j = 0; j < 4; j++)
			{
				if ((j == 3) && ((vInfoRegArr[i].elementAt(j)).toString().equals("")))
				{
					sInfoRegString += eraseNullCharEx("NOT_USED");
				}
				else
				{
					sInfoRegString += eraseNullCharEx(vInfoRegArr[i].elementAt(j).toString());
				}

				sInfoRegString += DELIM_STR;
			}
			*/
		}

		for (int i=0; i<4; i++)
		{
			sInfoBrokerage += eraseNullCharEx(vInfoBrokerage.elementAt(i).toString());
			sInfoBrokerage += DELIM_STR;
		}

		try
		{
			szInfoReg = sInfoRegString.getBytes("KSC5601");
			szInfoBrokerage = sInfoBrokerage.getBytes("KSC5601");

			int nServiceSize = szInfoReg.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_BrokerFileReg(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			if (nNumOfFile > 66)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Files Error, In JSTOR_BrokerFileReg(), [Current : " + nNumOfFile + ", Recommend : Under 66]";
				return -1;
			}

			sRet = new String (NtvJSTOR_BrokerFileReg (nConnID, nNumOfFile, szInfoReg, szInfoBrokerage, nGUIFlag), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet == 0)
			{
				m_sRegFileIDArr = new String[nNumOfFile];
				for (int i=0; i<nNumOfFile; i++)
				{
					String sSubTmp = "";
					sSubTmp = sTK.nextToken();
 					m_sRegFileIDArr[i] = sSubTmp.substring (0, 32);
				}
			}
			else
			{
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_BrokerFileReg() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_BrokerFileReg() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_BrokerFileReg() !";
			return -1;
		}

		return nRet;
	}


   /** 
    * <pre>
    * STORServ 파일반출(가져오기) 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 가져오기 서비스 대상 파일의 갯수 
    * @param String sInfoGetArr[i][0] => 저장서버로 부터 가져올 File ID 
    * @param String sInfoGetArr[i][1] => 가져온 파일이 저장될 File Path 
    * @param String sInfoGetArr[i][2] => 가져올 때 적용할 Fileter ID (등록할 때 적용된 Filter ID 와 동일해야 함)
	* (ex : 0 -> NONE, 1 -> 압축(GZIP 방식), 2 -> 암호화, 3 -> 압축 + 암호화, -1 -> 자동 검출)
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음.
	* (ex : 0 -> NO, 1 -> YES) 
	* @return int => 서비스 성공(0), 서비스 실패(음수 ,Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileGet (int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoGetString = "";
		byte[] szInfoGet;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			for (int col = 0; col < 3; col++)
			{
				if (null == sInfoGetArr[index][col])
				{
					m_nErrCode = nNullPointErr;
					m_sErrMsg = "Unexpected NULL Character is Included";

					return -1;
				}
			}
		}
		// *** End : Verify Inputed Info String Array *****

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sTest = "";   // 유효성 테스트 변수
		int	   nTest;        // 유효성 테스트 변수

		try
		{
			for (int i=0; i<nNumOfFile; i++)
			{
				sTest = eraseNullCharEx(sInfoGetArr[i][2]);
				nTest = Integer.parseInt(sTest);
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileGet(), Service Request Info Array has invalied Value !";
			return -1;
		}	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		for (int i=0; i<nNumOfFile; i++)
		{
			for (int j=0; j<3; j++)
			{
				sInfoGetArr[i][j] = eraseNullCharEx(sInfoGetArr[i][j]); 

				sInfoGetString+=sInfoGetArr[i][j];
				sInfoGetString+=DELIM_STR;
			}
		}

		try
		{
			szInfoGet = sInfoGetString.getBytes("KSC5601");

			int nServiceSize = szInfoGet.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileGet(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			sRet = new String (NtvJSTOR_FileGet (nConnID, nNumOfFile, szInfoGet, nGUIFlag), "KSC5601");   

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileGet() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileGet() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileGet() !";
			return -1;
		}

		return nRet;
	}


   /** 
    * <pre>
    * STORServ 파일반출(가져오기) 서비스 Api 
	* - DRM 적용 확장, 현재는 FASOO DRM 만 지원
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 가져오기 서비스 대상 파일의 갯수 
    * @param String sInfoGetArr[i][0] => 저장서버로 부터 가져올 File ID 
    * @param String sInfoGetArr[i][1] => 가져온 파일이 저장될 File Path 
	* - 일반적으로 DRM 적용시에는, 저장서버로 부터 파일반출이 성공하고 난 후 DRM Packager 의 작업대상 파일(원본)이 됨
    * @param String sInfoGetArr[i][2] => 가져올 때 적용할 Fileter ID 
	* - 등록할 때 적용된 Filter ID 와 동일해야 함
	* - ex) 0 -> NONE, 1 -> 압축(GZIP 방식), 2 -> 암호화, 3 -> 압축 + 암호화, -1 -> 자동 검출
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음
	* - ex) 0 -> NO, 1 -> YES
	* @param int nDRMEnabled => DRM 기능을 활성화 시킬지 여부
	* - ex) 0 -> Disabled, 1 -> Enabled
	* @param String sDRMType => 적용할 DRM 모델
	* - FASOO DRM 적용시 반드시 "FASOO" 를 입력할 것
	* @param String[][] sEssentialMetaData => DRM 적용 대상 파일들의 정보 (필수입력)
	* - sInfoGetArr 에 포함된 파일들 각각에 대해서 설정해 줘야 함
	* @param String sEssentialMetaData[i][0] => Fasoo DRM(FSD) Packager ADK Home Directory
	* @param String sEssentialMetaData[i][1] => FSD 서버 ID, Packager activation 이 완료된 서버에서, Packager 가 사용하는 키 파일들에 대한 정보를 알려주는 용도로 사용
	* - 16진수 문자열 16자리
	* @param String sEssentialMetaData[i][2] => Secure Container 로 만들어질 파일을 지정, Secucure Container 로 생성될 원본 파일에 대한 Full Path 이름을 지정함
	* - 일반적으로 sInfoGetArr[i][1] 에서 설정한 경로를 그대로 사용함
	* @param String sEssentialMetaData[i][3] => 생성될 Secure Container 의 파일이름을 지정, 생성될 Secure Container 의 파일이름에 대한 Full Path 이름을 지정하는데 사용 
	* - nNumOfFile 이 1개 일 경우, "null" 을 입력하면, 원본 파일에 ".fsc" 를 붙인 DRM Secure Container 파일이 생성됨
	* - nNumOfFile 이 2개 이상(복수) 일 경우, 이 값을 "null" 을 지정하면, 첫번째 원본파일명의 끝에 순서대로 "_0.fsc", "_1.fsc" 와 같은 형태로 Secure Container 파일명이 생성됨
	* - 이것은 Fasoo Packager 의 디폴트 규약이므로, 혼동을 방지하려면, 반드시 별도의 파일명을 지정해 줘야 함
	* - 별도로 파일명을 지정할 경우, ".fsc" 확장자는 자동으로 부여됨.
	* - 원본 파일이 write 가 불가능한 매체(CD-ROM 등)에 있는 경우에는 반드시, 이 파라미터를 이용해서 저장될 곳의 위치를 지정해야 함
	* @param String sEssentialMetaData[i][4] => Content 공급자의 ID 를 지정 
	* 암호화될 문서의 공급자 ID 를 내부 메타 정보로 저장해 주는 Parameter
	* @param String sEssentialMetaData[i][5] => Content 공급자의 이름을 지정  
	* - 암호화될 문서의 공급자 이름을 내부 메타 정보로 저장해 주는 Parameter
	* @param String sEssentialMetaData[i][6] => Content 공급자에 대한 부가 정보(Description) 를 지정 
	* -  호화될 문서의 공급자에 대한 부가정보를 내부 메타 정보로로 저장해 주는 Parameter
	* @param String sEssentialMetaData[i][7] => Content 의 제목을 지정함 
	* - 암호화될 Content 의 제목을 내부 메타 정보로 저장해 주는 Parameter
	* @param String[][] sAdditionalMetaData => DRM 파일 패키징시 추가할 정보를 설정함 (선택적 입력)
	* - sEssentialMetaData 에서 설정한 파일들 중 "추가정보" 가 필요한 파일들에 대해서 설정하면 됨.
	* - FASOO DRM 적용시, 추가 정보는 총 5개까지 설정 가능
	* - 추가 정보가 필요치 않은 경우, "null" 입력
	* @return int => 서비스 성공(0), 서비스 실패(음수 ,Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것
	* 이 API 에서는 DRM Secure Container 파일을 만들때, 
	* 중간에 생성된 "저장서버로 부터 가져온 원본파일" 및 최종적으로 생성된 DRM 파일을 삭제하지 않음. (API 채용 Application 영역으로 유보)
	* </pre>
    */
	public int[] JSTOR_FileGetExDRM (int nConnID, 
								  int nNumOfFile, 
								  String[][] sInfoGetArr, 
								  int nGUIFlag, 
								  int nDRMEnabled,
								  String sDRMType,
								  String[][] sEssentialMetaData, 
								  String[][] sAdditionalMetaData)
	{
		int nRet[] = new int[nNumOfFile];

		int iRet = JSTOR_FileGet(nConnID, nNumOfFile, sInfoGetArr, nGUIFlag);
		if (iRet <0)
		{
			return null;
		}

		// DRM 적용
		if (1 == nDRMEnabled)
		{
			// FASOO DRM 적용
			if ("FASOO".equals(sDRMType))
			{
				FasooPackager fsp = new FasooPackager();

				// 에러를 리턴받을 Output Parameter
				String[] sRetErrMsg = new String[nNumOfFile];

				String[] sOutDrmFilePath = new String[nNumOfFile];

				nRet = fsp.makePackageEx(nNumOfFile, sEssentialMetaData, sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);

				for (int i = 0; i < nNumOfFile; i++)
				{
					if (null == sOutDrmFilePath[i])
					{
						sOutDrmFilePath[i] = sInfoGetArr[i][1];
					}
				}
				m_sOutDrmFilePath = sOutDrmFilePath;
			}
			else
			{
				nRet = null;
			}

		}

		return nRet;	
	}

   public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
         String sDRMType, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, Vector vPrivInfo)
   {
      return null;
   }

	/*
	public int JSTOR_FileGetExDRM (int nConnID, 
								  int nNumOfFile, 
								  String[][] sInfoGetArr, 
								  int nGUIFlag, 
								  int nDRMEnabled,
								  String sDRMType,
								  String[][] sEssentialMetaData, 
								  String[][] sAdditionalMetaData)
	{
		int nRet = 0;

		nRet = JSTOR_FileGet(nConnID, nNumOfFile, sInfoGetArr, nGUIFlag);
		if (nRet <0)
		{
			return nRet;
		}

		// DRM 적용
		if (1 == nDRMEnabled)
		{
			// FASOO DRM 적용
			if ("FASOO".equals(sDRMType))
			{
				FasooPackager fsp = new FasooPackager();

				// 에러를 리턴받을 Output Parameter
				String[] sRetErrMsg = new String[1];

				String[] sOutDrmFilePath = new String[nNumOfFile];

				nRet = fsp.makePackage(nNumOfFile, sEssentialMetaData, sAdditionalMetaData, sRetErrMsg, sOutDrmFilePath);
				if (nRet != 0)
				{
					m_nErrCode = (nRet > 0 ? (-1) * nRet : nRet);
					m_sErrMsg = sRetErrMsg[0];
					return m_nErrCode;   // Return, ERROR
				}

				m_sOutDrmFilePath = sOutDrmFilePath;

				nRet = 0;   // Return Value Re Assign, SUCCESS
			}
			else
			{
				m_nErrCode = nUnKnownDRMErr;
				m_sErrMsg = "Not Suppored DRM Vender TYPE, In JSTOR_FileGet() !";
				return -1;
			}
		}

		return nRet;	
	}
	*/

	public String[] JSTOR_getOutDrmFilePath()
	{
		return m_sOutDrmFilePath;
	}


   /** 
    * <pre>
    * STORServ 파일반출(가져오기) 서비스 Api (중계(Broker) 전송 방식)
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일등록 서비스 대상 파일의 갯수 
	* @param Vector[] vInfoGetArr => 파일등록 정보
    * @param String sInfoGetArr[i][0] => 저장서버로 부터 가져올 File ID 
    * @param String sInfoGetArr[i][1] => 가져온 파일이 저장될 File Path (실제 파일이 저장될 컴퓨터에서의 File Full Path)
    * @param String sInfoGetArr[i][2] => 가져올 때 적용할 Fileter ID (등록할 때 적용된 Filter ID 와 동일해야 함)
	* (ex : 0 -> NONE, 1 -> 압축(GZIP 방식), 2 -> 암호화, 3 -> 압축 + 암호화, -1 -> 자동 검출)
    * @param Vector vInfoBrokerage => Brokerage 정보
	* vInfoBrokerage.elementAt(0) => [String] 가져온 파일이 저장될 컴퓨터의 IP 또는 Host Name (SFTPD 가 설치되어 있어야 함)
	* vInfoBrokerage.elementAt(1) => [Integer] 위 컴퓨터의 SFTPD Port
	* vInfoBrokerage.elementAt(2) => [Integer] 파일전송 중개 옵션 
	* (ex : 0 -> 컴퓨터간의 직접 전송, 1 -> Broker 역할을 하는 컴퓨터를 통한 간접 전송)
	* (Broker 역할을 하는 컴퓨터는 "JSTOR JNI Java Class API" 를 호출하는 컴퓨터" 를 말한다)
	* vInfoBrokerage.elementAt(3) => [String] 파일전송 중개시 필요한 임시 디렉토리
	* (ex : 중개 옵션 0 -> 저장서버가 설치된 컴퓨터에 임시 디렉토리를 설정해야 함)
	* (ex : 중개 옵션 1 -> Broker 역할을 하는 컴퓨터에 임시 디렉토리를 설정)
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음 (Deprecated)
	* (ex : 0 -> NO, 1 -> YES) 
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_BrokerFileGet (int nConnID, int nNumOfFile, Vector[] vInfoGetArr, Vector vInfoBrokerage, int nGUIFlag)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;

		String sRet;

		String sInfoGetString = "";
		byte[] szInfoGet;

		String sInfoBrokerage = "";
		byte[] szInfoBrokerage;

		for (int i=0; i<nNumOfFile; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				sInfoGetString += eraseNullCharEx(vInfoGetArr[i].elementAt(j).toString());

				sInfoGetString += DELIM_STR;
			}
		}

		for (int i=0; i<4; i++)
		{
			sInfoBrokerage += eraseNullCharEx(vInfoBrokerage.elementAt(i).toString());
			sInfoBrokerage += DELIM_STR;
		}

		try
		{
			szInfoGet = sInfoGetString.getBytes("KSC5601");
			szInfoBrokerage = sInfoBrokerage.getBytes("KSC5601");

			int nServiceSize = szInfoGet.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_BrokerFileGet(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			sRet = new String (NtvJSTOR_BrokerFileGet (nConnID, nNumOfFile, szInfoGet, szInfoBrokerage, nGUIFlag), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_BrokerFileGet() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_BrokerFileGet() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_BrokerFileGet() !";
			return -1;
		}

		return nRet;
	}


   /** 
    * <pre>
    * STORServ Commit Api
    * @param int nConnID => Connection ID
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_Commit (int nConnID)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = -1;
		String sRet;

		try
		{
	
			sRet = new String (NtvJSTOR_Commit (nConnID), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_Commit() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_Commit() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_Commit() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ Rollback Api
    * @param int nConnID => Connection ID
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_Rollback (int nConnID)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;

		try
		{
			sRet = new String (NtvJSTOR_Rollback (nConnID), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_Rollback() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_Rollback() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_Rollback() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ Volume 정보검색 Api
    * @param int nConnID => Connection ID
	* @return int => 서비스 성공(반환 볼륨 정보 수), 서비스 실패(음수, Minus), 성공시 JSTOR_getVolInfo() 를 호출할 것
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_VolInfo (int nConnID)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;

		try
		{
			sRet = new String (NtvJSTOR_VolInfo (nConnID), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet >= 0)   /* 반환되는 볼륨의 수 */
			{
				m_sVolInfoArr = new String[nRet][7];

				for (int m = 0; m < nRet; m++)
				{
					for (int n = 0; n < 7; n++)
					{
						m_sVolInfoArr[m][n] = sTK.nextToken();
					}
				}
			}
			else   /* 서비스 실패 의 경우 */
			{
				m_nErrCode = Integer.parseInt (sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_VolInfo() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_VolInfo() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_VolInfo() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ 파일 삭제 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 삭제 서비스 대상 파일의 갯수 
    * @param String sInfoDelArr[i] => 삭제할 File ID 
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileDel (int nConnID, int nNumOfFile, String[] sInfoDelArr)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoDelString = "";
		byte[] szInfoDel;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			if (sInfoDelArr[index] == null)
			{
				m_nErrCode = nNullPointErr;
				m_sErrMsg = "Unexpected NULL Character is Included";

				return -1;
			}
		}
		// *** End : Verify Inputed Info String Array *****


		for (int i=0; i<nNumOfFile; i++)
		{
			sInfoDelArr[i] = eraseNullCharEx(sInfoDelArr[i]); 

			sInfoDelString+=sInfoDelArr[i];
			sInfoDelString+=DELIM_STR;
		}

		try
		{
			szInfoDel = sInfoDelString.getBytes("KSC5601");

			int nServiceSize = szInfoDel.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileDel(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			sRet = new String (NtvJSTOR_FileDel (nConnID, nNumOfFile, szInfoDel), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileDel() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileDel() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileDel() !";
			return -1;
		}


		return nRet;
	}

   /** 
    * <pre>
    * STORServ 파일 교체 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 교체 서비스 대상 파일의 갯수 
	* @param String sInfoRepArr[i][0] => 교체될 대상 File ID
	* @param String sInfoRepArr[i][1] => 교체할 File Path
	* @param String sInfoRepArr[i][2] => 교체하면서 적용될 Fileter ID (JSTOR_FileReg 참조)
    * @param int nGUIFlag => GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음.
	* (ex : 0 -> NO, 1 -> YES)
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileRep (int nConnID, int nNumOfFile, String[][] sInfoRepArr, int nGUIFlag)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoRepString = "";
		byte[] szInfoRep;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			for (int col = 0; col < 3; col++)
			{
				if (null == sInfoRepArr[index][col])
				{
					m_nErrCode = nNullPointErr;
					m_sErrMsg = "Unexpected NULL Character is Included";

					return -1;
				}
			}
		}
		// *** End : Verify Inputed Info String Array *****

		String sTest = "";   // 필터 아이디 유효성 테스트 변수
		int	   nTest;        // 필터 아이디 유효성 테스트 변수

		/* 필터 아이디가 실제 숫자로 변환이 가능한지 확인하는 부분 */
		try
		{
			for (int i=0; i<nNumOfFile; i++)
			{
				sTest = eraseNullCharEx(sInfoRepArr[i][2]);
				nTest = Integer.parseInt(sTest);
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileRep(), Inputed Filter ID : [" + sTest + "]";
			return -1;
		}	
		/* 여기까지가, 필터 아이디가 실제 숫자로 변환이 가능한지 확인하는 부분 */
		
		for (int i=0; i<nNumOfFile; i++)
		{
			for (int j=0; j<3; j++)
			{
				sInfoRepArr[i][j] = eraseNullCharEx(sInfoRepArr[i][j]); 

				sInfoRepString += sInfoRepArr[i][j];
				sInfoRepString += DELIM_STR;
			}
		}

		try
		{
			szInfoRep = sInfoRepString.getBytes("KSC5601");

			int nServiceSize = szInfoRep.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileRep(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			sRet = new String (NtvJSTOR_FileRep (nConnID, nNumOfFile, szInfoRep, nGUIFlag), "KSC5601");   

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileRep() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileRep() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileRep() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ 파일 복사 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 복사 서비스 대상 파일의 갯수 
	* @param String sInfoCpyArr[i][0] => 복사될 대상 File ID 
	* @param String	sInfoCpyArr[i][1] => 복사될 대상 Volume ID
	* @param String	sInfoCpyArr[i][2] => 복사되면서 적용될 Filter ID (JSTOR_FileReg 참조)
	* @param String	sInfoCpyArr[i][3] => 복사될 대상 저장서버 구분 
	* (ex : 1 -> 로컬 저장서버, 2 -> 원격 저장서버)
	* @param String sInfoCpyArr[i][4] => 복사될 대상 저장서버 IP
	* @param String sInfoCpyArr[i][5] => 복사될 대상 저장서버 Port 
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileCpy (int nConnID, int nNumOfFile, String[][] sInfoCpyArr)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoCpyString = "";
		
		String sNotUsed = "NOT_USED";

		byte[] szInfoCpy;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			if (sInfoCpyArr[index][0] == null)
			{
				m_nErrCode = nExceptErr;
				m_sErrMsg = "Requested Service File Information Length Is Invalid";
				return -1;
			}
		}
		// *** End : Verify Inputed Info String Array *****

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sTest = "";   // 유효성 테스트 변수
		int	   nTest;        // 유효성 테스트 변수

		try
		{
			for (int i=0; i<nNumOfFile; i++)
			{
				sTest = eraseNullCharEx(sInfoCpyArr[i][1]);
				nTest = Integer.parseInt(sTest);

				sTest = eraseNullCharEx(sInfoCpyArr[i][2]);
				nTest = Integer.parseInt(sTest);

				sTest = eraseNullCharEx(sInfoCpyArr[i][3]);
				nTest = Integer.parseInt(sTest);
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileCpy(), Service Request Info Array has invalied Value !";
			return -1;
		}	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		for (int i = 0; i < nNumOfFile; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				sInfoCpyArr[i][j] = eraseNullCharEx(sInfoCpyArr[i][j]); 

				if (j == 3)
				{
					if (sInfoCpyArr[i][j].equals("1"))
					{
						sInfoCpyString += (eraseNullCharEx(sInfoCpyArr[i][j]) + DELIM_STR + eraseNullCharEx(sNotUsed) + DELIM_STR + eraseNullCharEx(sNotUsed) + DELIM_STR);
						j += 2;
					}
					else
					{
						sInfoCpyString += eraseNullCharEx(sInfoCpyArr[i][j]);
						sInfoCpyString += DELIM_STR;
					}
				}
				else
				{
					sInfoCpyString += eraseNullCharEx(sInfoCpyArr[i][j]);
					sInfoCpyString += DELIM_STR;
				}
			}
		}

		try
		{
			szInfoCpy = sInfoCpyString.getBytes("KSC5601");

			int nServiceSize = szInfoCpy.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileCpy(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			if (nNumOfFile > 66)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Files Error, In JSTOR_FileCpy(), [Current : " + nNumOfFile + ", Recommend : Under 66";
				return -1;
			}

			sRet = new String (NtvJSTOR_FileCpy (nConnID, nNumOfFile, szInfoCpy), "KSC5601");  

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet == 0)
			{
				m_sNewCpyFileIDArr = new String[nNumOfFile];
				for (int i = 0; i < nNumOfFile; i++)
				{
					String sSubTmp = "";
					sSubTmp = sTK.nextToken();
					m_sNewCpyFileIDArr[i] = sSubTmp.substring (0, 32);
				}
			}
			else
			{
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileCpy() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileCpy() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileCpy() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ 파일 이동 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 이동 서비스 대상 파일의 갯수 
	* @param String sInfoCpyArr[i][0] => 이동될 대상 File ID
	* @param String	sInfoCpyArr[i][1] => 이동될 대상 Volume ID
	* @param String	sInfoCpyArr[i][2] => 이동되면서 적용될 Filter ID 
	* @param String	sInfoCpyArr[i][3] => 이동될 대상 저장서버 구분 
	* (ex : 1 -> 로컬 저장서버, 2 -> 원격 저장서버)
	* @param String sInfoCpyArr[i][4] => 이동될 대상 저장서버 IP 
	* @param String sInfoCpyArr[i][5] => 이동될 대상 저장서버 Port 
	* @return int => 서비스 성공(0), 서비스 실패(음수 ,Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileMov (int nConnID, int nNumOfFile, String[][] sInfoMovArr)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoMovString = "";

		String sNotUsed = "NOT_USED";

		byte[] szInfoMov;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			if (sInfoMovArr[index][0] == null)
			{
				m_nErrCode = nExceptErr;
				m_sErrMsg = "Requested Service File Information Length Is Invalid";
				return -1;
			}
		}
		// *** End : Verify Inputed Info String Array *****

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String sTest = "";   // 유효성 테스트 변수
		int	   nTest;        // 유효성 테스트 변수

		try
		{
			for (int i=0; i<nNumOfFile; i++)
			{
				sTest = eraseNullCharEx(sInfoMovArr[i][1]);
				nTest = Integer.parseInt(sTest);

				sTest = eraseNullCharEx(sInfoMovArr[i][2]);
				nTest = Integer.parseInt(sTest);

				sTest = eraseNullCharEx(sInfoMovArr[i][3]);
				nTest = Integer.parseInt(sTest);
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileMov(), Service Request Info Array has invalied Value !";
			return -1;
		}	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		for (int i = 0; i < nNumOfFile; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				sInfoMovArr[i][j] = eraseNullCharEx(sInfoMovArr[i][j]); 

				if (j == 3)
				{
					if (sInfoMovArr[i][j].equals("1"))
					{
						sInfoMovString += (eraseNullCharEx(sInfoMovArr[i][j]) + DELIM_STR + eraseNullCharEx(sNotUsed) + DELIM_STR + eraseNullCharEx(sNotUsed) + DELIM_STR);
						j += 2;
					}
					else
					{
						sInfoMovString += eraseNullCharEx(sInfoMovArr[i][j]);
						sInfoMovString += DELIM_STR;
					}
				}
				else
				{
					sInfoMovString += eraseNullCharEx(sInfoMovArr[i][j]);
					sInfoMovString += DELIM_STR;
				}
			}
		}

		try
		{
			szInfoMov = sInfoMovString.getBytes("KSC5601");

			int nServiceSize = szInfoMov.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileMov(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}
			
			sRet = new String (NtvJSTOR_FileMov (nConnID, nNumOfFile, szInfoMov), "KSC5601");   

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			if (sTK.countTokens() == 1)
			{
				nRet = 0;
			}
			else 
			{
				sTK.nextToken();
				m_nErrCode = Integer.parseInt(sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
				nRet = -1;
			}
		}					
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileMov() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileMov() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileMov() !";
			return -1;
		}

		return nRet;
	}

   /** 
    * <pre>
    * STORServ 파일 정보검색 서비스 Api
    * @param int nConnID => Connection ID
    * @param int nNumOfFile => 파일 정보검색 서비스 대상 파일의 갯수 
    * @param String sInfoInfoArr[i] => 파일 정보검색 대상 File ID
	* @return int => 서비스 성공(0), 서비스 실패(음수, Minus)
	* 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
	* </pre>
    */
	public int JSTOR_FileInfo (int nConnID, int nNumOfFile, String[] sInfoInfoArr)
	{
		// *** Start : Connection Check ***
		if (nConnID < 0)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "JSTOR is disconnected from STOR SERVER";
			return -1;
		}
		// *** End : Connection Check ***

		int    nRet = 0;
		String sRet;
		String sInfoInfoString = "";
		byte[] szInfoInfo;

		// *** Start : Verify Inputed Info String Array ***
		for (int index = 0; index < nNumOfFile; index++)
		{
			if (sInfoInfoArr[index] == null)
			{
				m_nErrCode = nExceptErr;
				m_sErrMsg = "Requested Service File Information Length Is Invalid";
				return -1;
			}
		}
		// *** End : Verify Inputed Info String Array *****

		for (int i=0; i<nNumOfFile; i++)
		{
			sInfoInfoArr[i] = eraseNullCharEx(sInfoInfoArr[i]); 

			sInfoInfoString += sInfoInfoArr[i];
			sInfoInfoString += DELIM_STR;
		}

		try
		{
			szInfoInfo = sInfoInfoString.getBytes("KSC5601");

			int nServiceSize = szInfoInfo.length;
			if (nServiceSize > REQUEST_MAX)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Info Size Error, In JSTOR_FileInfo(), [Current : " + nServiceSize + ", Recommend : Under " + REQUEST_MAX + "]";
				return -1;
			}

			if (nNumOfFile > 10)
			{
				m_nErrCode = nOverReqErr; 
				m_sErrMsg  = "Too many Service Request Files Error, In JSTOR_FileInfo(), [Current : " + nNumOfFile + ", Recommend : Under 10]";
				return -1;
			}
			
			sRet = new String (NtvJSTOR_FileInfo (nConnID, nNumOfFile, szInfoInfo), "KSC5601");   

			StringTokenizer sTK = new StringTokenizer (sRet, DELIM_STR);

			nRet = Integer.parseInt(sTK.nextToken());

			if (nRet == 0) 
			{
				m_sFileInfoArr = new String[nNumOfFile][7];

				for (int m = 0; m < nNumOfFile; m++)
				{
					for (int n = 0; n < 7; n++)
					{
						m_sFileInfoArr[m][n] = sTK.nextToken();
					}
				}
			}
			else   /* 서비스 실패 의 경우 */
			{
				m_nErrCode = Integer.parseInt (sTK.nextToken());
				m_sErrMsg = sTK.nextToken();
			}
		}
		catch (java.lang.NumberFormatException nfe) 
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg  = "NumberFormatException Occered, In JSTOR_FileInfo() !";
			return -1;
		}	
		catch (java.io.IOException ie)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "IOException Occered, In JSTOR_FileInfo() !";
			return -1;
		}
		catch (Exception e)
		{
			m_nErrCode = nExceptErr;
			m_sErrMsg = "Exception Occered, In JSTOR_FileInfo() !";
			return -1;
		}

		return nRet;
	}

   /**
    * <pre>
    * STORServ 파일 정보검색 서비스가 성공했을 경우, 각 파일의 정보를 Return 하는 Api,
	* 반드시 JSTOR_FileInfo() 함수가 성공한 다음에 사용하여야 함.
	* @param void
	* @return String[][] => 2차원 배열의 각 칼럼의 내용은 다음과 같다.
	* String[i][0] : File Name
	* String[i][1] : File Size
	* String[i][2] : File Extension 
	* String[i][3] : File Filter
	* String[i][4] : File Creation Date
	* String[i][5] : File Modification Date
	* String[i][6] : Volume ID 
	* </pre>
	*/
	public String[][] JSTOR_getFileInfo()
	{
		return m_sFileInfoArr;
	}

   /**
    * <pre>
    * STORServ 파일등록 서비스에서 복사 성공한 파일들의, 복사된 후 New File ID 를 Return 하는 Api, 
	* 반드시 JSTOR_FileCpy() 함수가 성공한 다음에 사용하여야 함.
	* @param void
	* @return String[] => File ID 의 배열
	* </pre>
	*/
	public String[] JSTOR_getNewCpyFileID()
	{
		return m_sNewCpyFileIDArr;
	}

   /**
    * <pre>
    * STORServ Volume 정보검색 서비스가 성공했을 경우, 각 볼륨의 정보를 Return 하는 Api,
	* 반드시 JSTOR_VolInfo() 함수가 성공한 다음에 사용하여야 함. JSTOR_VolInfo() 의 리턴값이 볼륨의 갯수가 됨.
	* @param  void
	* @return String[][] => 2차원 배열의 각 칼럼의 내용은 다음과 같다.
	* String[i][0] : Volume ID
	* String[i][1] : Volume Name
	* String[i][2] : Volume English Name, 저장서버 DB 에 해당 정보가 없을 경우, "none" 을 리턴
	* String[i][3] : Volume Type
	* String[i][4] : Volume Access Right
	* String[i][5] : Volume Creation Date
	* String[i][6] : Volume Description, 저장서버에 DB 에 해당 정보가 없을 경우, "none" 을 리턴
	* </pre>
	*/
	public String[][] JSTOR_getVolInfo()
	{
		return m_sVolInfoArr;
	}
	
   /**
    * <pre>
    * STORServ 파일등록 서비스에서 등록이 성공한 파일들의 File ID 를 Return 하는 Api, 
	* 반드시 JSTOR_FileReg() 함수가 성공한 다음에 사용하여야 함.
	* @param void
	* @return String[] => File ID 의 배열
	* </pre>
	*/
	public String[] JSTOR_getRegFileID()
	{
		return m_sRegFileIDArr;
	}

   /** 
    * <pre>
    * STORServ 에러 코드 반환 Api
	* @param void
	* @return int => 관련 에러 코드
	* </pre>
    */
	public int JSTOR_getErrCode()
	{
		return m_nErrCode;
	}

   /** 
    * <pre>
    * STORServ 에러 메세지 반환 Api
	* @param void
	* @return String => 관련 에러 메세지
	* </pre>
    */
	public String JSTOR_getErrMsg()
	{
		return m_sErrMsg.trim();
	}

   /** 
	<pre>
	* 문자열 거르기 함수
   * 2005-12-13 에러가 발생하여 주석처리
	* @param  String => 거르기 대상  
	* @return String => 걸러서 반환
	</pre>   
	*/
	private String eraseNullCharEx(String str)  
	{
       				
		String strRet = "";
		String strBuf = "";
		String strTmp = "";
		
		strBuf = str.trim();
		
		if (str != null) 
		{
			for(int i=0; i<str.length() ; i++) 
			{
				strTmp = str.substring(i, i+1);
				if (" ".equals(strTmp)) 
				{
					strRet = strRet + strTmp;
				} 
				else 
				{	
					strRet = strRet + strTmp.trim();
				}
				
			}
		} 
		else 
		{
			strRet = null;
		}

		return strRet;
	}
   
}


