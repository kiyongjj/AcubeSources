package com.sds.acube.jstor;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import MarkAny.MaSaferJava.MadnPackage;

public class MarkAnyPackager
{

   static boolean __JSTOR_MARKANY_DEBUG__ = false;

   static
   {
      if ("true".equals(System.getProperty("drm.debug")))
      {
         __JSTOR_MARKANY_DEBUG__ = true;
      }

      System.setProperty("acube.markany.pack.version", "1.0.0");
   }

   public static void main(String args[])
   {
      System.out.println("\n");
      System.out.println("▶ Company :  SAMSUNG SDS");
      System.out.println("▶ Product Name : MarkAny Packager API");
      System.out.println("▶ Version : 1.0.0");
      System.out.println("\n");
   }

   public synchronized int[] makePackage(int nNumOfFile, String[][] sEssentialMetaData, String[][] sAdditionalMetaData,
         String[] sRetErrMsg, String[] sOutDrmFilePath)
   {
      /*
       * // arrEssentialMetaData int idxOrgFileName = 0; // [필수] 원본파일명(Full
       * Path) int idxEncFileName = 1; // [필수] 암호화된 파일명(Full Path) int idxDdsIp =
       * 2; // [필수] DRM 암호화 서버 IP int idxDdsPort = 3; // [필수-고정값] DRM 암호화 서버
       * PORT(40001로 고정) int idxUserId = 4; // 사용자 ID int idxOwnerId = 5; //
       * 문서등록자 ID int idxCompanyId = 6; // [필수-고정값] Site명 (회사명), 삼성투신운용: SSFUND
       * int idxRealTimeAcl = 7; // [필수] 실시간 권한제어 여부 (1로 고정) int idxServerAddr =
       * 8; // [필수] DRM 로그 서버 IP ex) [DRM Server IP]:40002 int idxExchangePolicy =
       * 9;// [필수] 문서교환정책 (1로 고정) int idxDrmFlag = 10; // [필수] 암호화 여부(1이면 암호화안함,
       * 0이면 암호화)
       *  // arrAdditionalMetaData int idxServerOrigin = 0; // [필수] System ID
       * (ex-ACUBE_KM) int idxFileId = 1; // [필수] DocumentID int idxGrade = 2; //
       * [필수] DB Alias
       */
      int arrRet[] = new int[nNumOfFile];
      String[] arrRetMsg = new String[nNumOfFile];
      // int nRet = -1;

      for (int i = 0; i < nNumOfFile; i++)
      {
         sRetErrMsg[i] = "UNKNOWN";
         arrRetMsg[i] = "UNKNOWN";
         arrRet[i] = 0;
      }

      writePackLog("[BEFORE] MarkAny Packager Class Construction");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DRM Server IP -> [" + sEssentialMetaData[0][2] + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DRM Server Port -> [" + sEssentialMetaData[0][3] + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed CompanyID -> [" + sEssentialMetaData[0][6] + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed RealTime ACL -> [" + sEssentialMetaData[0][7] + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DRM Log Server IP -> [" + sEssentialMetaData[0][8]
            + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DRM Exchange Policy -> [" + sEssentialMetaData[0][9]
            + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DRM Flag -> [" + sEssentialMetaData[0][10] + "]");

      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed System ID -> [" + sAdditionalMetaData[0][0] + "]");
      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Inputed DB Alias -> [" + sAdditionalMetaData[0][2] + "]");

      writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), File Cnt -> [" + nNumOfFile + "]");
      for (int i = 0; i < nNumOfFile; i++)
      {
         writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), File[" + i + "] src : " + sEssentialMetaData[i][0]
               + ", tgt : " + sEssentialMetaData[i][1]);
      }

      try
      {
         MadnPackage pkg = new MadnPackage(System.getProperty("dm.home") + File.separator + "lib" + File.separator
               + "DrmFileExtInfo.dat");
         pkg.iMadnPackage( /* in */nNumOfFile, /* in */sEssentialMetaData, /* in */sAdditionalMetaData, /* out */arrRetMsg);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Packaging Exception : " + e);
      }

      // 리턴값 처리
      for (int i = 0; i < nNumOfFile; i++)
      {
         writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), Return Value[" + i + "] -> " + arrRetMsg[i]);
         // if ( "00000".equals(arrRetMsg[i]) ) // 정상
         if (sEssentialMetaData[i][1].equals(arrRetMsg[i])) // 리턴값이 파일명 경로일 경우 :
                                                            // 정상
         {
            sRetErrMsg[i] = "0";
            arrRet[i] = 0;
            if (sOutDrmFilePath != null && sOutDrmFilePath.length > i)
            {
               sOutDrmFilePath[i] = sEssentialMetaData[i][1];
            }
         }
         else if ("90002".equals(arrRetMsg[i])) // 지원하지 않는 파일포맷
         {
            sRetErrMsg[i] = "-11";
            arrRet[i] = -11;
            if (sOutDrmFilePath != null && sOutDrmFilePath.length > i)
            {
               sOutDrmFilePath[i] = sEssentialMetaData[i][0];
            }
         }
         else
         // 그외는 전부 에러임
         {
            sRetErrMsg[i] = arrRetMsg[i];
            arrRet[i] = Integer.parseInt(arrRetMsg[i]) * -1;
            if (sOutDrmFilePath != null && sOutDrmFilePath.length > i)
            {
               sOutDrmFilePath[i] = sEssentialMetaData[i][0];
            }
         }
         writePackLog("[JSTOR_MARKANY_DEBUG] makePackage(), sRetErrMsg[" + i + "] -> " + sRetErrMsg[i]
               + ", OutDrmPath[" + i + "] -> " + sOutDrmFilePath[i]);
      }

      return arrRet;
   }

   private synchronized void writePackLog(String strMsg)
   {
      SimpleDateFormat dFormat = null;
      StringBuffer sBuff = null;

      try
      {
         // 디버그 옵션 지정일 경우
         if (__JSTOR_MARKANY_DEBUG__)
         {
            dFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
            sBuff = new StringBuffer();

            sBuff.append("TIME : ");
            sBuff.append(dFormat.format(new Date()));
            sBuff.append(" - " + strMsg);

            writeTraceFile(sBuff.toString());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   // 파일 출력
   private synchronized void writeTraceFile(String strMsg)
   {
      String strTraceFileFullPath = null;
      FileWriter fileTrace = null;

      SimpleDateFormat fFormat = new SimpleDateFormat("yyyyMMdd");

      String strTraceFileDirPath = null;

      try
      {
         strTraceFileDirPath = System.getProperty("user.dir");

         // 파일명 생성
         strTraceFileFullPath = strTraceFileDirPath + File.separator + "MarkAnyPackager_Trace_"
               + fFormat.format(new Date()) + ".log";
         // 파일 객체 생성
         fileTrace = new FileWriter(strTraceFileFullPath, true);
         // Msg Formatting
         strMsg = strMsg + System.getProperty("line.separator");
         // Msg Writing
         fileTrace.write(strMsg);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (null != fileTrace)
               fileTrace.close();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }
}
