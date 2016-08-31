package com.sds.acube.jstor;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasoo.adk.packager.FasooPackagerJNI;
import com.fasoo.adk.packager.WorkPackager;

public class FasooPackager
{

   static boolean __JSTOR_FSD_DEBUG__ = false;

   static
   {
      if ("true".equals(System.getProperty("drm.debug")))
      {
         __JSTOR_FSD_DEBUG__ = true;
      }

      System.setProperty("acube.fasoo.pack.version", "3.1.16");
   }

   public static void main(String args[])
   {
      System.out.println("\n");
      System.out.println("▶ Company :  SAMSUNG SDS");
      System.out.println("▶ Product Name : Fasoo Packager API");
      System.out.println("▶ Version : 3.1.16");
      System.out.println("▶ Fasoo : fasoo-jni-1.1.16.jar or Above Version is needed");
      System.out.println("\n");
   }

   public synchronized int makePackage(int nNumOfFile, String[][] sEssentialMetaData, String[][] sAdditionalMetaData,
         String[] sRetErrMsg, String[] sOutDrmFilePath)
   {
      int nRet;
      String sRet;

      fsdjava.FasooPackagerJNI fspjni = new fsdjava.FasooPackagerJNI();

      for (int i = 0; i < nNumOfFile; i++)
      {
         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed ADK Home Dir [" + i + "] -> ["
                  + sEssentialMetaData[i][0] + "]");

         // SET EssentialMetaData
         nRet = fspjni.SetHomeDir(sEssentialMetaData[i][0]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetHomeDir() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Server ID [" + i + "] -> ["
                  + sEssentialMetaData[i][1] + "]");

         nRet = fspjni.SetServerID(sEssentialMetaData[i][1]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetServerID() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Source File Name[" + i + "] -> ["
                  + sEssentialMetaData[i][2] + "]");

         nRet = fspjni.SetSourceFileName(sEssentialMetaData[i][2]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetSourceFileName() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Dest(Content) File Name[" + i + "] -> ["
                  + sEssentialMetaData[i][3] + "]");

         if (null != sEssentialMetaData[i][3] && 0 != sEssentialMetaData[i][3].length())
         {
            nRet = fspjni.SetContainerFileName(sEssentialMetaData[i][3]);
            if (0 != nRet)
            {
               sRetErrMsg[0] = "SetContainerFileName() : " + fspjni.GetLastErrorStr();
               return nRet;
            }
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider ID [" + i + "] -> ["
                  + sEssentialMetaData[i][4] + "]");

         nRet = fspjni.SetContentProviderID(sEssentialMetaData[i][4]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetContentProviderID() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider Name [" + i + "] -> ["
                  + sEssentialMetaData[i][5] + "]");

         nRet = fspjni.SetContentProviderName(sEssentialMetaData[i][5]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetContentProviderName() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider Description [" + i + "] -> ["
                  + sEssentialMetaData[i][6] + "]");

         nRet = fspjni.SetContentProviderDescription(sEssentialMetaData[i][6]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetContentProviderDescription() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         if (__JSTOR_FSD_DEBUG__)
            System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Title [" + i + "] -> ["
                  + sEssentialMetaData[i][7] + "]");

         nRet = fspjni.SetContentTitle(sEssentialMetaData[i][7]);
         if (0 != nRet)
         {
            sRetErrMsg[0] = "SetContentTitle() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         int nLen = sAdditionalMetaData[0].length;

         if (__JSTOR_FSD_DEBUG__)
            System.out
                  .println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info Length [" + i + "] -> " + nLen);

         // Set AdditionalMetaData
         if (null != sAdditionalMetaData)
         {
            if (nLen > 0 && null != sAdditionalMetaData[i][0] && 0 != sAdditionalMetaData[i][0].length())
            {

               if (__JSTOR_FSD_DEBUG__)
                  System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 1 [" + i + "] -> ["
                        + sAdditionalMetaData[i][0] + "]");

               nRet = fspjni.SetAdditionalInfo1(sAdditionalMetaData[i][0]);
               if (0 != nRet)
               {
                  sRetErrMsg[0] = "SetAdditionalInfo1() : " + fspjni.GetLastErrorStr();
                  return nRet;
               }
            }

            if (nLen > 1 && null != sAdditionalMetaData[i][1] && 0 != sAdditionalMetaData[i][1].length())
            {
               if (__JSTOR_FSD_DEBUG__)
                  System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 2 [" + i + "] -> ["
                        + sAdditionalMetaData[i][1] + "]");

               nRet = fspjni.SetAdditionalInfo2(sAdditionalMetaData[i][1]);
               if (0 != nRet)
               {
                  sRetErrMsg[0] = "SetAdditionalInfo2() : " + fspjni.GetLastErrorStr();
                  return nRet;
               }
            }

            if (nLen > 2 && null != sAdditionalMetaData[i][2] && 0 != sAdditionalMetaData[i][2].length())
            {
               if (__JSTOR_FSD_DEBUG__)
                  System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 3 [" + i + "] -> ["
                        + sAdditionalMetaData[i][2] + "]");

               nRet = fspjni.SetAdditionalInfo3(sAdditionalMetaData[i][2]);
               if (0 != nRet)
               {
                  sRetErrMsg[0] = "SetAdditionalInfo3() : " + fspjni.GetLastErrorStr();
                  return nRet;
               }
            }

            if (nLen > 3 && null != sAdditionalMetaData[i][3] && 0 != sAdditionalMetaData[i][3].length())
            {
               if (__JSTOR_FSD_DEBUG__)
                  System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 4 [" + i + "] -> ["
                        + sAdditionalMetaData[i][3] + "]");

               nRet = fspjni.SetAdditionalInfo4(sAdditionalMetaData[i][3]);
               if (0 != nRet)
               {
                  sRetErrMsg[0] = "SetAdditionalInfo4() : " + fspjni.GetLastErrorStr();
                  return nRet;
               }
            }

            if (nLen > 4 && null != sAdditionalMetaData[i][4] && 0 != sAdditionalMetaData[i][4].length())
            {
               if (__JSTOR_FSD_DEBUG__)
                  System.out.println("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 5 [" + i + "] -> ["
                        + sAdditionalMetaData[i][4] + "]");

               nRet = fspjni.SetAdditionalInfo5(sAdditionalMetaData[i][4]);
               if (0 != nRet)
               {
                  sRetErrMsg[0] = "SetAdditionalInfo5() : " + fspjni.GetLastErrorStr();
                  return nRet;
               }
            }
         }

         nRet = fspjni.Initialize();
         if (0 != nRet)
         {
            sRetErrMsg[0] = "Initialize() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         nRet = fspjni.CreateSecureContainer();
         if (0 != nRet)
         {
            sRetErrMsg[0] = "CreateSecureContainer() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         sRet = fspjni.GetSecureContentID();
         if (null == sRet)
         {
            sRetErrMsg[0] = "GetSecureContentID() : " + fspjni.GetLastErrorStr();
            return nRet;
         }

         sOutDrmFilePath[i] = fspjni.GetSecureContainerFileName();

      }

      return 0;
   }

   public synchronized int[] makePackageEx(int nNumOfFile, String[][] sEssentialMetaData,
         String[][] sAdditionalMetaData, String[] sRetErrMsg, String[] sOutDrmFilePath)
   {

      if (__JSTOR_FSD_DEBUG__)
         System.out.println("[JSTOR_FSD_DEBUG] makePackage() Entered, Requested nNumOfFile = " + nNumOfFile);

      int nRet[] = new int[nNumOfFile];
      // String sRet;

      int nEssenCol = sEssentialMetaData[0].length;
      int nAddCol = sAdditionalMetaData[0].length;

      if (__JSTOR_FSD_DEBUG__)
         System.out.println("[JSTOR_FSD_DEBUG] before FasooPackagerJNI Object create");

      // FasooPackagerJNI fspjni = new FasooPackagerJNI();

      if (__JSTOR_FSD_DEBUG__)
         System.out.println("[JSTOR_FSD_DEBUG] FasooPackagerJNI Object is created");

      for (int i = 0; i < nNumOfFile; i++)
      {
         String[] sErr = new String[1];
         String[] sOut = new String[1];
         String[][] sE = new String[1][nEssenCol];
         String[][] sA = new String[1][nAddCol];

         for (int j = 0; j < nEssenCol; j++)
         {
            sE[0][j] = sEssentialMetaData[i][j];
         }

         for (int j = 0; j < nAddCol; j++)
         {
            sA[0][j] = sAdditionalMetaData[i][j];
         }

         int iRet = makePackage(1, sE, sA, sErr, sOut);
         nRet[i] = (iRet > 0 ? iRet * (-1) : iRet);
         sRetErrMsg[i] = sErr[0];
         sOutDrmFilePath[i] = sOut[0];

         sErr = null;
         sOut = null;
         sE = null;
         sA = null;
      }

      if (__JSTOR_FSD_DEBUG__)
         System.out.println("[JSTOR_FSD_DEBUG] makePackage() will be returned");

      return nRet;
   }

   /**
    * fasoo-jni-1.1.2.jar는 더이상 지원하지 않음 : 2006-01-05 fasoo-jni-1.1.5.jar이상만 지원함
    * 
    * @param nNumOfFile :
    *           DRM을 적용할 파일의 개수
    * @param sEssentialMetaData[][] :
    *           패키징할 파일의 개수만큼 생성, JSTOR_FileGetExDRM JavaDoc참조
    * @param sAdditionalMetaData :
    *           JSTOR_FileGetExDRM JavaDoc참조
    * @param sRetErrMsg
    * @param sOutDrmFilePath
    * @return
    */
   public synchronized int[] makePackage31(int nNumOfFile, String[][] sEssentialMetaData,
         String[][] sAdditionalMetaData, String[] sRetErrMsg, String[] sOutDrmFilePath)
   {
      int arrRet[] = new int[nNumOfFile];
      int nRet = -1;

      for (int i = 0; i < nNumOfFile; i++)
      {
         sRetErrMsg[i] = "UNKNOWN";
      }

      writePackLog("[BEFORE] FasooPackager Class Construction");
      writePackLog("Fasoo getInstance Mode");

      com.fasoo.adk.packager.FasooPackagerInstance packagerWork = com.fasoo.adk.packager.FasooPackagerInstance
            .getInstance(); // 파라메터는 생성할 인스탄스 갯수..

      com.fasoo.adk.packager.FasooPackagerJNI nativeobj = packagerWork.openFasooPackager();
      writePackLog("[ AFTER] FasooPackager Class Construction");

      try
      {

         for (int i = 0; i < nNumOfFile; i++)
         {
            nativeobj.Reset();

            writePackLog("[BEFORE] CreateObject");
            nativeobj.CreateObject();
            writePackLog("[ AFTER] CreateObject");

            writePackLog("[BEFORE] SetHomeDir");
            nRet = nativeobj.SetHomeDir(sEssentialMetaData[i][0]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetHomeDir");

            writePackLog("[BEFORE] SetServerID");
            nRet = nativeobj.SetServerID(sEssentialMetaData[i][1]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetServerID");

            writePackLog("[BEFORE] SetPackagerType");
            nativeobj.SetPackagerType(FasooPackagerJNI.PACKAGE_CMH);
            writePackLog("[ AFTER] SetPackagerType");

            writePackLog("[BEFORE] Initialize");
            nRet = nativeobj.Initialize();
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] Initialize");

            writePackLog("[BEFORE] SetContentProviderID");
            nRet = nativeobj.SetContentProviderID(sEssentialMetaData[i][4]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetContentProviderID");

            writePackLog("[BEFORE] SetContentProviderName");
            nRet = nativeobj.SetContentProviderName(sEssentialMetaData[i][5]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetContentProviderName");

            writePackLog("[BEFORE] SetContentProviderDescription");
            nRet = nativeobj.SetContentProviderDescription(sEssentialMetaData[i][6]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetContentProviderDescription");

            writePackLog("[BEFORE] SetAdditionalInfo");
            // Set AdditionalMetaData
            if (null != sAdditionalMetaData && null != sAdditionalMetaData[i])
            {
               if (null != sAdditionalMetaData[i][0] && 0 != sAdditionalMetaData[i][0].length())
               {
                  nRet = nativeobj.SetAdditionalInfo1(sAdditionalMetaData[i][0]);
                  if (0 != nRet)
                  {
                     arrRet[i] = nRet;
                     continue;
                  }
               }

               if (null != sAdditionalMetaData[i][1] && 0 != sAdditionalMetaData[i][1].length())
               {
                  nRet = nativeobj.SetAdditionalInfo2(sAdditionalMetaData[i][1]);
                  if (0 != nRet)
                  {
                     arrRet[i] = nRet;
                     continue;
                  }
               }

               if (null != sAdditionalMetaData[i][2] && 0 != sAdditionalMetaData[i][2].length())
               {
                  nRet = nativeobj.SetAdditionalInfo3(sAdditionalMetaData[i][2]);
                  if (0 != nRet)
                  {
                     arrRet[i] = nRet;
                     continue;
                  }
               }

               if (null != sAdditionalMetaData[i][3] && 0 != sAdditionalMetaData[i][3].length())
               {
                  nRet = nativeobj.SetAdditionalInfo4(sAdditionalMetaData[i][3]);
                  if (0 != nRet)
                  {
                     arrRet[i] = nRet;
                     continue;
                  }
               }

               if (null != sAdditionalMetaData[i][4] && 0 != sAdditionalMetaData[i][4].length())
               {
                  nRet = nativeobj.SetAdditionalInfo5(sAdditionalMetaData[i][4]);
                  if (0 != nRet)
                  {
                     arrRet[i] = nRet;
                     continue;
                  }
               }
            }
            writePackLog("[ AFTER] SetAdditionalInfo");

            writePackLog("[BEFORE] SetSourceFileName");
            nRet = nativeobj.SetSourceFileName(sEssentialMetaData[i][2]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetSourceFileName");

            // 생성될 DRM 파일명을 지정하지 않았을 경우에는,
            // 원본 파일에 "fsc" 확장자를 붙이도록 한다
            // 단, 이미 생성될 DRM 파일이 존재할 경우에는,
            // "test.doc_1.fsc" 와 같이 fsc 확장자 바로 앞에 "_1" 과 같은
            // 동일파일명에 대한 시퀀스 넘버가 붙게 된다 (ADK Spec)
            // 2005-03-31 add by junhyuk : fsc, fsf는 JSP영역에서 붙여주도록 한다.. 필요한 경우
            boolean bConvert = false;

            System.out.println("before sEssentialMetaData[i][3] : " + sEssentialMetaData[i][3]);
            if (null == sEssentialMetaData[i][3] || 0 == sEssentialMetaData[i][3].length())
            {
               // 해당 이름은 어디까지나 서버상에 남기는 이름으로 실제 이름은 Presentation영역에서 변경요망
               if (sEssentialMetaData[i][2].toLowerCase().endsWith(".pdf"))
               {
                  sEssentialMetaData[i][3] = sEssentialMetaData[i][2] + ".fsf";
               }
               else
               {
                  sEssentialMetaData[i][3] = sEssentialMetaData[i][2] + ".fsc";
               }
               // sEssentialMetaData[i][3] = sEssentialMetaData[i][2];
               bConvert = true;
            }
            String sOutFilePath = sEssentialMetaData[i][3];
            System.out.println("after sEssentialMetaData[i][3] : " + sEssentialMetaData[i][3]);

            // sEssentialMetaData[i][3] = sEssentialMetaData[i][2];

            writePackLog("[BEFORE] SetContainerFileName");
            nRet = nativeobj.SetContainerFileName(sEssentialMetaData[i][3]);
            System.out.println("SetContainerFileName Return : " + nRet);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetContainerFileName");

            // 다시 파라미터를 원래대로 돌린다
            if (bConvert)
            {
               sEssentialMetaData[i][3] = null;
            }

            writePackLog("[BEFORE] SetContentTitle");
            nRet = nativeobj.SetContentTitle(sEssentialMetaData[i][7]);
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] SetContentTitle");

            writePackLog("[BEFORE] CreateSecureContainer");
            nRet = nativeobj.CreateSecureContainer();
            if (0 != nRet)
            {
               arrRet[i] = nRet;
               continue;
            }
            writePackLog("[ AFTER] CreateSecureContainer");

            writePackLog("[BEFORE] GetSecureContentID");
            String sRet = nativeobj.GetSecureContentID();
            if (null == sRet)
            {
               arrRet[i] = -100;
               continue;
            }
            writePackLog("[ AFTER] GetSecureContentID");

            writePackLog("[BEFORE] GetSecureContainerFileName");
            sRet = nativeobj.GetSecureContainerFileName();
            if (null == sRet)
            {
               sRetErrMsg[i] = nativeobj.GetLastErrorStr();
               arrRet[i] = -200;
               continue;
            }
            writePackLog("[ AFTER] GetSecureContainerFileName");

            // sOutDrmFilePath[i] = sRet;
            // 2005-04-06 add by junhyuk : FullPath를 리턴해야함
            sOutDrmFilePath[i] = sOutFilePath;

            writePackLog("[BEFORE] ClearAll");
            nativeobj.ClearAll();
            writePackLog("[ AFTER] ClearAll");

            nativeobj.Reset();
         }

         for (int i = 0; i < arrRet.length; i++)
         {
            int Ret = arrRet[i];
            // 리턴값을 모두 음수로 바꾼다
            arrRet[i] = (Ret > 0 ? -1 * Ret : Ret);
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
         writePackLog("[FASOO EXCEPTION] " + e.getMessage());
      }
      finally
      {
         nativeobj.Reset();
         // 2005-04-09 add by junhyuk
         // getInstance Mode
         packagerWork.closeFasooPackager(nativeobj);
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
         if (__JSTOR_FSD_DEBUG__)
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
         strTraceFileFullPath = strTraceFileDirPath + File.separator + "FasooPackager_Trace_"
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

   // 파수 암호화 패키지 파일 여부 확인
   public synchronized boolean isFasooPackageFile(String filePath)
   {
      WorkPackager oWorkPackager = new WorkPackager();
      return oWorkPackager.IsPackageFile(filePath);
   }

   public synchronized boolean doFasooPacakgeFileExtract(String filePath, String fsdHomeDir, String fsdServerID) throws Exception
   {
      boolean bRet = false;
      String drmOutFile = filePath + ".fasooExtractTmp";
      WorkPackager oWorkPackager = new WorkPackager();

      bRet = oWorkPackager.DoExtract(fsdHomeDir, fsdServerID, filePath, drmOutFile);
      if (bRet)
      {
         File f1 = new File(filePath);
         f1.delete();

         File f2 = new File(drmOutFile);
         f2.renameTo(f1);
      }
      else
      {
         String msg = "Fasoo Drm Extract Error : " + oWorkPackager.getLastErrorNum() + ", " + oWorkPackager.getLastErrorStr();
         writePackLog(msg);
         throw new Exception (msg);
      }

      return bRet;
   }

}
