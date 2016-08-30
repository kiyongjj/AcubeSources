package com.sds.acube.jstor;
// File Replace

import java.io.*;
import java.util.*;

class RepTest
{
	public static void main (String args[])
	{

		int        nRet;
		
		int        nConnID;           // 커넥션 아이디   
		String     sIPAddr;           // 아이피 어드레스
		int        nPortNo;           // 포트 넘버
		int        nNumOfFile;        // 등록 파일 겟수
		String[][] sInfoRepArr;       // 등록 교체 정보
		int        nGUIFlag;          // GUI Interface Flag 

		JSTORApi jSTOR = null;
		JSTORApiFactory jsFatory = null;

		try
		{

			if (5 != args.length)
			{
				System.out.println ("\n◎ USAGE : java [-options] RepTest [STOR IP] [STOR Port] [Target Rep. File ID] [SRC Rep. File Path] [Filter ID]\n");

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

			sInfoRepArr = new String[nNumOfFile][3];

			sInfoRepArr[0][0] = new String (args[2]);		// * file id
			sInfoRepArr[0][1] = new String (args[3]);		// * file path to replace
			sInfoRepArr[0][2] = new String (args[4]);		// * filter id (0)

			nGUIFlag = 0;

			jsFatory = new JSTORApiFactory ();
			jSTOR = jsFatory.getInstance ();

			nConnID = jSTOR.JSTOR_Connect (sIPAddr, nPortNo);   
			if (nConnID <0)
			{
				System.out.println ("\nSTORServ 연결실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				return;
			}

			System.out.println ("\nSTORServ 연결성공");

			nRet = jSTOR.JSTOR_FileRep(nConnID, nNumOfFile, sInfoRepArr, nGUIFlag);
			if (nRet <0)
			{
				System.out.println ("\nFileRep 실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				jSTOR.JSTOR_Rollback (nConnID);
				jSTOR.JSTOR_Disconnect (nConnID);
				System.out.println ("\n정상적으로 Rollback 및 연결을 종료 했습니다.");
				return;
			}

			System.out.println ("\nFileRep 성공");

			nRet = jSTOR.JSTOR_Commit (nConnID);
			if (nRet < 0)
			{
				System.out.println ("Commit 실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				jSTOR.JSTOR_Disconnect (nConnID);
				System.out.println ("\n정상적으로 연결을 종료 했습니다.");
				return;
			}

			jSTOR.JSTOR_Disconnect(nConnID);
			System.out.println ("\n정상적으로 연결을 종료 했습니다.");

		} catch (Exception e) {e.printStackTrace();}
	}
}	
	
	
