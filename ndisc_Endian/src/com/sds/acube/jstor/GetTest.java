// Test For STOR
// File Get
// 웹어플리케이션 java 런타임 기동시에
// JSTORAPI_20041123.jar 를 classpath 에 추가
// -Djstor_api_type=pure_java -Djstor_svr_type=unix 를 환경변수로 추가

package com.sds.acube.jstor;
import java.io.*;
import java.util.*;

class GetTest
{
	public static void main (String args[])
	{
		int        nRet;
	
		int        nConnID;           // 커넥션 아이디 (접속 성공시 반환 받는 값임)
		String     sIPAddr;           // 저장서버 ip
		int        nPortNo;           // 저장서버 포트
		int        nNumOfFile;        // 가져올 파일 갯수
		String[][] sInfoGetArr;       // 가져올 파일 정보 배열
		int        nGUIFlag;          // GUI Interface Flag (0 으로 하면 됨)

		// For Time Gap
		long	   lSTime;
		long	   lETime;

		JSTORApi jSTOR = null;
		JSTORApiFactory jsFatory = null;

		try
		{
			if (5 != args.length)
			{
				System.out.println ("\nUSAGE : java [-options] GetTestA [STOR IP] [STOR Port] [Getting File ID] [Local Saving File Path] [Reverse Filter ID]\n");

				String strOptions = "[-options] \n" +
									"  \n" +
									"where options include: \n" +
									"	-Djstor_api_type=jni|pure_java \n" +
									"					jni : using JNI type jstor api (default) \n" +
									"					pure_java : using PURE JAVA type jstor api \n" +
									"	-Djstor_svr_type=unix|win32 \n" +
									"					unix : using UNIX platform Storage Server (default) \n" +
									"					win32 : using Win32 platform Storage Server \n" +
									"	-Djstor_trace_type=none|console|file|both \n" +
									"					none : do not trace debug msg (default) \n" +
									"					console : trace debug msg to CONSOLE(terminal) \n" +
									"					file : trace debug msg to FILE(created in current app dir) \n" +
									"					both : trace both CONSOLE & FILE \n" +
									" \n" +
									"ex) -Djstor_api_type=pure_java -Djstor_svr_type=unix -Djstor_trace_type=console \n";
				
				System.out.println (strOptions);

				return;
			}
			
			// Data Setting
			sIPAddr = args[0];
			nPortNo = Integer.parseInt (args[1]);
			nNumOfFile = 1;
			
			sInfoGetArr = new String[nNumOfFile][3];
			sInfoGetArr[0][0] = new String (args[2]);		// * 저장서버 파일 ID
			sInfoGetArr[0][1] = new String (args[3]);   	// * 로컬 파일 경로 (full path)
			sInfoGetArr[0][2] = new String (args[4]);		// * reverse filter id (-1 로 하면 됨)
			
			nGUIFlag = 0;
			// End Of Data Setting

			jsFatory = new JSTORApiFactory ();
			jSTOR = jsFatory.getInstance ();
			
			// Start Time : Time Stamp
			lSTime = System.currentTimeMillis ();

			System.out.println ("");
			nConnID = jSTOR.JSTOR_Connect (sIPAddr, nPortNo);   
			if (nConnID <0)
			{
				System.out.println ("◎ STORServ 연결실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				return;
			}

			System.out.println ("◎ STORServ 연결성공, nConnID = " + nConnID);

			nRet = jSTOR.JSTOR_FileGet(nConnID, nNumOfFile, sInfoGetArr, nGUIFlag);
			if (nRet <0)
			{
				System.out.println ("◎ FileGet 실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				jSTOR.JSTOR_Disconnect (nConnID);
				System.out.println ("◎ 정상적으로 연결을 종료 했습니다.");
				return;
			}

			System.out.println ("◎ FileGet 성공");

			jSTOR.JSTOR_Disconnect(nConnID);
			System.out.println ("◎ 정상적으로 연결을 종료 했습니다.");

			// End Time
			lETime = System.currentTimeMillis ();
			
			// Time Gap Display
			long lGap, lMin, lSec, lMills;
			lGap = lETime - lSTime;
			lMin   = lGap/(60*1000);
			lSec   = (lGap - (lMin*(60*1000)))/1000;
			lMills = lGap - (lSec*1000);
			System.out.println ("");
			System.out.println ("=======================================================================================");
			System.out.println ("▶ Total Time Elapsed(Included Connection & Discnnection) : " + lMin + " MIN " + lSec + " SEC " + lMills + " MILLS");
			System.out.println ("=======================================================================================");
			System.out.println ("");

		} 
		catch (Exception e) 
		{
			System.out.println ("\n◎ EXCEPTION has Occered !!! \n"); 
			e.printStackTrace();
		}
	}
}	
	
	
