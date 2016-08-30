package com.sds.acube.jstor;

import java.io.*;
import java.util.*;

//import com.sds.acube.jstor.*;

class RegTest
{
	public static void main (String args[])
	{
		int        nRet;
	
		int        nConnID;           // 커넥션 아이디   
		String     sIPAddr;           // 아이피 어드레스
		int        nPortNo;           // 포트 넘버
		int        nNumOfFile;        // 등록 파일 겟수
		String[][] sInfoRegArr;       // 등록 파일 정보
		int        nGUIFlag;          // GUI Interface Flag 

		// For Time Gap
		long	   lSTime;
		long	   lETime;

		nConnID = -1;

		JSTORApi jSTOR = null;

		try
		{
			if (5 != args.length)
			{
				System.out.println ("\nUSAGE : java -Djstor_api_type=(pure_java/ndisc) RegTest [STOR IP] [STOR Port] [SRC Reg. File Path] [Vol ID] [Filter ID]\n");
				return;
			}
			
			// Data Setting
			sIPAddr = args[0];
			nPortNo = Integer.parseInt (args[1]);
			nNumOfFile = 1;
			
			sInfoRegArr = new String[nNumOfFile][4];
			sInfoRegArr[0][0] = new String (args[2]); 
			sInfoRegArr[0][1] = new String (args[3]);   
			sInfoRegArr[0][2] = new String (args[4]);   
			sInfoRegArr[0][3] = null;
			
			nGUIFlag = 0;
			// End Of Data Setting

			// Start Time : Time Stamp
			lSTime = System.currentTimeMillis ();
			//System.setProperty("jstor_api_type","pure_java");
			
			JSTORApiFactory jsFactory = new JSTORApiFactory();
			jSTOR = jsFactory.getInstance();

			System.out.println ("");
			nConnID = jSTOR.JSTOR_Connect (sIPAddr, nPortNo);   
			if (nConnID <0)
			{
				System.out.println ("◎ STORServ 연결실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				return;
			}

			System.out.println ("◎ STORServ 연결성공, nConnID = " + nConnID);

			nRet = jSTOR.JSTOR_FileReg(nConnID, nNumOfFile, sInfoRegArr, nGUIFlag);
			if (nRet <0)
			{
				System.out.println ("◎ FileReg 실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				jSTOR.JSTOR_Rollback (nConnID);
				return;
			}

			System.out.println ("◎ FileReg 성공");

			nRet = jSTOR.JSTOR_Commit (nConnID);
			if (nRet < 0)
			{
				System.out.println ("◎ Commit 실패 : " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
				return;
			}

			System.out.println ("◎ Commit 성공");

			String sTmp[] = new String[nNumOfFile];
			sTmp = jSTOR.JSTOR_getRegFileID();
			for (int i = 0; i < nNumOfFile; i++)
			{
				System.out.println ("◎ Returned Registered File ID = " + sTmp[i]);
			}

			// End Time
			lETime = System.currentTimeMillis ();
			
			// Time Gap Display
			long lGap, lMin, lSec, lMills;
			lGap = lETime - lSTime;
			lMin   = lGap/(60*1000);
			lSec   = (lGap - (lMin*(60*1000)))/1000;
			lMills = lGap - (lSec*1000);
			System.out.println ("");
			System.out.println ("==================================================================================");
			System.out.println ("▶ Total Time Elapsed : " + lMin + " MIN " + lSec + " SEC " + lMills + " MILLS");
			System.out.println ("===================================================================================");
			System.out.println ("");

		} 
		catch (Exception e) 
		{
			System.out.println ("\n◎ EXCEPTION has Occered !!! \n"); 
			e.printStackTrace();
		}
		finally 
		{
			if (null != jSTOR && nConnID >= 0)
			{
				jSTOR.JSTOR_Disconnect(nConnID);
				System.out.println ("◎ 정상적으로 연결을 종료 했습니다.");
			}
		}
	}
}	
	
	
