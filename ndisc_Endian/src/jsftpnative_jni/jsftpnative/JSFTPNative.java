/*===================================================================================
 *
 * 작   성  자 : 조 국 영
 *
 * 클래스 이름 : JSFTPNative.class
 *
 * 설       명 : 
 *
 * 개발 시작일 : 2001/10/01
 *
 * 개발 종료일 : 0000/00/00
 *
 * 수   정  일 : 0000/00/00(수정자 : )
 *
 * 버       젼 : ver 3.0
 *
 * Copyright notice : Copyright (C) 2000 by SAMSUNG SDS co.,Ltd. All right reserved.
 *
 ===================================================================================*/

package jsftpnative_jni.jsftpnative;

import java.util.StringTokenizer;

/**
 * C, C++ 기반의 Native Code로 작성된 SFTPApi Library와 JAVA간의 연계를 구현
 * 
 * @version 3.0
 * @author 조국영
 */
public class JSFTPNative
{
   /* Native Library 로딩 */
   static
   {
      try
      {
         System.loadLibrary("JSFTPApi");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /*
    * (아래 참고) Shared library 대신 local library를 바로 사용합니다. 즉 static구문의
    * System.loadLibrary("userinfo")가 아닌 System.load("path:/userinfo.dll")을
    * 이용합니다.
    */

   /*
    * 이하 : Native Function
    * ------------------------------------------------------------------------------------------------------
    */
   private native synchronized byte[] NtvJSFTP_Connect(byte[] szIPAddr, int nPortNo);

   private native synchronized byte[] NtvJSFTP_UploadEx(int nConnID, int nNumOfFile, byte[] szPath, int nHowFlag,
         int nEncFlag);

   private native synchronized byte[] NtvJSFTP_DownloadEx(int nConnID, int nNumOfFile, byte[] szPath, int nDelFlag,
         int nHowFlag, int nEncFlag);

   private native synchronized byte[] NtvJSFTP_GetBaseDir(int nConnID, byte[] szServName);

   private native synchronized byte[] NtvJSFTP_MakeDir(int nConnID, byte[] szDirPath);

   private native synchronized byte[] NtvJSFTP_ExistFile(int nConnID, byte[] szFilePath, int nAccessMode);

   private native synchronized void NtvJSFTP_Disconnect(int nConnID);

   /*
    * 여기까지
    * ---------------------------------------------------------------------------------------------------------------------
    */

   private int m_nErrCode;

   private String m_sErrMsg;

   private String m_sBaseDir;

   private final int nExceptErr = -119; /* Exception Occered */

   private final int nOverReqErr = -112; /*
                                           * Service Request String is
                                           * Overflowed
                                           */

   private final String DELIM_STR = "\t";

   private final int REQUEST_MAX = 25599; /* 25600 -1 */

   public static void main(String args[])
   {
      System.out.println("\n");

      System.out.println("▶ Company :  SAMSUNG SDS");

      System.out.println("▶ Product Name : JSFTP API");

      System.out.println("▶ Version : 20030328");

      System.out.println("\n");
   }

   /**
    * <pre>
    *     SFTPD 연결 Api
    *     @param String sIPAddr =&gt; SFTPD IP 또는 Host Name(단, 64자 이상은 넘지 말 것)
    *     @param int nPortNo =&gt; SFTPD Port Number
    *     @return int =&gt; 연결 성공(Connection ID), 연결 실패(음수, Minus)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_Connect(String sIPAddr, int nPortNo)
   {
      int nRet = 0;
      String sRet;

      sIPAddr = eraseNullChar(sIPAddr);

      sIPAddr += DELIM_STR;

      try
      {
         byte[] szIPAddr = sIPAddr.getBytes("KSC5601");

         sRet = new String(NtvJSFTP_Connect(szIPAddr, nPortNo), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_Connect() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_Connect() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_Connect() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 연결 종료 Api
    *     @param int nConnID =&gt; Connection ID
    *     @return void      
    * </pre>
    */
   public void JSFTP_Disconnect(int nConnID)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return;
      }
      // *** End : Connection Check ***

      NtvJSFTP_Disconnect(nConnID);
   }

   /**
    * <pre>
    *     SFTPD 업로드 서비스 Api
    *     @param int nConnID =&gt; Connection ID
    *     @param int nNumOfFile =&gt; 업로드 서비스 대상 파일의 갯수 
    *     @param String sPathArr[i][0] =&gt; 업로드 대상 파일이 존재하는 Local File Full Path
    *     @param String sPathArr[i][1] =&gt; 업로드 대상 파일이 저장될 Remote File Full Path
    *     @param int nHowFlag =&gt; 파일전송 서비스 모드, Win32 플랫폼에서만 유효, UNIX 에서는 0 로 설정되어 있음
    *     (ex : 0 -&gt; Sync, 1 -&gt; Async)
    *     @param int nEncFlag =&gt; 파일전송 암호화 설정, Win32 플랫폼에서만 유효, UNIX 에서는 0 로 설정되어 있음 
    *     (ex : 0 -&gt; 비암호화, 1 -&gt; 암호화)  
    *     @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_UploadEx(int nConnID, int nNumOfFile, String[][] sPathArr, int nHowFlag, int nEncFlag)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return -1;
      }

      int nRet = 0;
      String sRet;
      String sPathString = "";
      byte[] szPath;

      // sPathArr is null ?
      if (null == sPathArr)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Requested Service File Information Length Is Invalid";
         return -1;
      }

      // Match nNumOfFile with sPathArr Length ?
      if (nNumOfFile != sPathArr.length)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Requested Service File Information Length Is Invalid";
         return -1;
      }

      // *** Start : Verify Inputed Info String Array ***
      for (int index = 0; index < nNumOfFile; index++)
      {
         for (int j = 0; j < 2; j++)
         {
            if (null == sPathArr[index][j])
            {
               m_nErrCode = nExceptErr;
               m_sErrMsg = "Requested Service File Information Length Is Invalid";
               return -1;
            }
            else if (0 == sPathArr[index][j].length())
            {
               m_nErrCode = nExceptErr;
               m_sErrMsg = "Requested Service File Information Length Is Invalid";
               return -1;
            }
         }
      }
      // *** End : Verify Inputed Info String Array *****

      for (int i = 0; i < nNumOfFile; i++)
      {
         for (int j = 0; j < 2; j++)
         {
            sPathArr[i][j] = eraseNullChar(sPathArr[i][j]);

            sPathString += sPathArr[i][j];
            sPathString += DELIM_STR;
         }
      }

      try
      {
         szPath = sPathString.getBytes("KSC5601");

         // 사이즈 검출
         int nServiceSize = szPath.length;
         if (nServiceSize > REQUEST_MAX)
         {
            m_nErrCode = nOverReqErr;
            m_sErrMsg = "Too many Service Request Info Size Error, In JSFTP_UploadEx(), [Current : " + nServiceSize
                  + ", Recommend : Under " + REQUEST_MAX + "]";
            return -1;
         }

         sRet = new String(NtvJSFTP_UploadEx(nConnID, nNumOfFile, szPath, nHowFlag, nEncFlag), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_UploadEx() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_UploadEx() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_UploadEx() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 다운로드 서비스 Api
    *     @param int nConnID =&gt; Connection ID
    *     @param int nNumOfFile =&gt; 다운로드 서비스 대상 파일의 갯수 
    *     @param String sPathArr[i][0] =&gt; 다운로드 대상 파일이 저장될 Local File Full Path
    *     @param String sPathArr[i][1] =&gt; 다운로드 대상 파일이 존재하는 Remote File Full Path
    *     @param int nDelFlag =&gt; 다운로드 후, 원본 파일 삭제 여부 (ex : 0 -&gt; NO, 1 -&gt; YES)
    *     @param int nHowFlag =&gt; 파일전송 서비스 모드, Win32 플랫폼에서만 유효, UNIX 버전에서는 내부적으로 0 로 임의 설정
    *     (ex : 0 -&gt; Sync, 1 -&gt; Async) 
    *     @param int nEncFlag =&gt; 파일전송 암호화 설정, Win32 플랫폼에서만 유효, UNIX 에서는 0 로 설정되어 있음 
    *     (ex : 0 -&gt; 비암호화, 1 -&gt; 암호화)  
    *     @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_DownloadEx(int nConnID, int nNumOfFile, String[][] sPathArr, int nDelFlag, int nHowFlag,
         int nEncFlag)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return -1;
      }

      int nRet = 0;
      String sRet;
      String sPathString = "";
      byte[] szPath;

      // sPathArr is null ?
      if (null == sPathArr)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Requested Service File Information Length Is Invalid";
         return -1;
      }

      // Match nNumOfFile with sPathArr Length ?
      if (nNumOfFile != sPathArr.length)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Requested Service File Information Length Is Invalid";
         return -1;
      }

      // *** Start : Verify Inputed Info String Array ***
      for (int index = 0; index < nNumOfFile; index++)
      {
         for (int j = 0; j < 2; j++)
         {
            if (null == sPathArr[index][j])
            {
               m_nErrCode = nExceptErr;
               m_sErrMsg = "Requested Service File Information Length Is Invalid";
               return -1;

            }

            else if (0 == sPathArr[index][j].length())
            {
               m_nErrCode = nExceptErr;
               m_sErrMsg = "Requested Service File Information Length Is Invalid";
               return -1;
            }
         }
      }
      // *** End : Verify Inputed Info String Array *****

      for (int i = 0; i < nNumOfFile; i++)
      {
         for (int j = 0; j < 2; j++)
         {
            sPathArr[i][j] = eraseNullChar(sPathArr[i][j]);

            sPathString += sPathArr[i][j];
            sPathString += DELIM_STR;
         }
      }

      try
      {
         szPath = sPathString.getBytes("KSC5601");

         // 사이즈 검출
         int nServiceSize = szPath.length;
         if (nServiceSize > REQUEST_MAX)
         {
            m_nErrCode = nOverReqErr;
            m_sErrMsg = "Too many Service Request Info Size Error, In JSFTP_DownloadEx(), [Current : " + nServiceSize
                  + ", Recommend : Under " + REQUEST_MAX + "]";
            return -1;
         }

         sRet = new String(NtvJSFTP_DownloadEx(nConnID, nNumOfFile, szPath, nDelFlag, nHowFlag, nEncFlag), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_DownloadEx() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_DownloadEx() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_DownloadEx() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 와 타 서비스의 연계에 사용되는, 각종 Configuration Value 얻기
    *     @param int nConnID =&gt; Connection ID
    *     @param String sServName =&gt; Value를 얻고자 하는 Configuration Name
    *     @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *     (성공시 JSFTP_getBaseDirValue() 를 호출해서 Value 를 얻을 것)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_GetBaseDir(int nConnID, String sServName)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return -1;
      }

      int nRet = 0;
      String sRet;

      byte[] szServName;

      sServName = eraseNullChar(sServName);

      sServName += DELIM_STR;

      try
      {
         szServName = sServName.getBytes("KSC5601");

         // 사이즈 검출
         int nServiceSize = szServName.length;
         if (nServiceSize > REQUEST_MAX)
         {
            m_nErrCode = nOverReqErr;
            m_sErrMsg = "Too many Service Request Info Size Error, In JSFTP_GetBaseDir(), [Current : " + nServiceSize
                  + ", Recommend : Under " + REQUEST_MAX + "]";
            return -1;
         }

         sRet = new String(NtvJSFTP_GetBaseDir(nConnID, szServName), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

         nRet = Integer.parseInt(sTK.nextToken());

         if (nRet == 0)
         {
            m_sBaseDir = sTK.nextToken();
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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_GetBaseDir() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_GetBaseDir() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_GetBaseDir() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 데몬(서비스)이 설치된 서버 컴퓨터 상에, 디렉토리를 생성.
    *     디렉토리 생성의 주체가 SFTPD 데몬임을 주지할 것.
    *     따라서, 데몬 실행계정이 해당 디렉토리 생성에 유효한 Permission 을 가지고 있는지 확인(UNIX)
    *     생성되는 디렉토리의 Default Access Permission 은 UNIX 의 경우,
    *     755(Owner, Group, Other) 로 설정됨
    *     @param int nConnID =&gt; Connection ID
    *     @param String sDirPath =&gt; 생성하고자하는 디렉토리명
    *     @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_MakeDir(int nConnID, String sDirPath)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return -1;
      }

      int nRet = 0;
      String sRet;

      sDirPath = eraseNullChar(sDirPath);

      sDirPath += DELIM_STR;

      try
      {
         byte[] szDirPath = sDirPath.getBytes("KSC5601");

         // 사이즈 검출
         int nServiceSize = szDirPath.length;
         if (nServiceSize > REQUEST_MAX)
         {
            m_nErrCode = nOverReqErr;
            m_sErrMsg = "Too many Service Request Info Size Error, In JSFTP_MakeDir(), [Current : " + nServiceSize
                  + ", Recommend : Under " + REQUEST_MAX + "]";
            return -1;
         }

         sRet = new String(NtvJSFTP_MakeDir(nConnID, szDirPath), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_MakeDir() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_MakeDir() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_MakeDir() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 데몬(서비스)이 설치된 서버 컴퓨터 상에, 
    *     해당 Access Permission(nAccessMode)을 가진 파일/디렉토리의 존재 유무를 검사
    *     @param int nConnID =&gt; Connection ID
    *     @param String sFilePath =&gt; 검사하고자하는 파일/디렉토리의 Full Path
    *     @param int nAccessMode =&gt; 검사하고자하는 파일/디렉토리가 
    *     SFTP Daemon 실행 계정에 대해 어떤 Access Permission 을 가지고 있는지 확인하는 옵션
    *     Permission 옵션 설정은 UNIX 의 &quot;chmod&quot; 명령어를 참고로 해서, 다음과 같음
    *        0 : 존재 여부 (Permission 무시)
    *        1 : execute
    *        2 : write
    *        4 : read
    *        3 : execute + write
    *        5 : execute + read
    *        6 : write + read
    *        7 : execute + write + read
    *     @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *     함수호출이 실패할 경우, JSFTP_GetErrMsg, JSFTP_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSFTP_ExistFile(int nConnID, String sFilePath, int nAccessMode)
   {
      // *** Start : Connection Check ***
      if (nConnID < 0)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "JSFTP is disconnected from SFTP SERVER(DAEMON)";
         return -1;
      }

      int nRet = 0;
      String sRet;

      sFilePath = eraseNullChar(sFilePath);

      sFilePath += DELIM_STR;

      try
      {
         byte[] szFilePath = sFilePath.getBytes("KSC5601");

         // 사이즈 검출
         int nServiceSize = szFilePath.length;
         if (nServiceSize > REQUEST_MAX)
         {
            m_nErrCode = nOverReqErr;
            m_sErrMsg = "Too many Service Request Info Size Error, In JSFTP_ExistFile(), [Current : " + nServiceSize
                  + ", Recommend : Under " + REQUEST_MAX + "]";
            return -1;
         }

         sRet = new String(NtvJSFTP_ExistFile(nConnID, szFilePath, nAccessMode), "KSC5601");

         StringTokenizer sTK = new StringTokenizer(sRet, DELIM_STR);

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
         m_sErrMsg = "NumberFormatException Occered, In JSFTP_ExistFile() !";
         return -1;
      }
      catch (java.io.IOException ie)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "IOException Occered, In JSFTP_ExistFile() !";
         return -1;
      }
      catch (Exception e)
      {
         m_nErrCode = nExceptErr;
         m_sErrMsg = "Exception Occered, In JSFTP_ExistFile() !";
         return -1;
      }

      return nRet;
   }

   /**
    * <pre>
    *     SFTPD 와 타 서비스의 연계에 사용되는, 각종 Configuration Value 을 반환하는 Api
    *     반드시 JSFTP_GetBaseDir() 함수가 성공한 다음에 사용하여야 함.
    *     @param void
    *     @return String =&gt; Configuration Value
    * </pre>
    */
   public String JSFTP_getBaseDirValue()
   {
      return m_sBaseDir.trim();
   }

   /**
    * <pre>
    *     SFTPD 에러 코드 반환 Api
    *     @param void
    *     @return int =&gt; 관련 에러 코드
    * </pre>
    */
   public int JSFTP_getErrCode()
   {
      return m_nErrCode;
   }

   /**
    * <pre>
    *     SFTPD 에러 메세지 반환 Api
    *     @param void 
    *     @return String =&gt; 관련 에러 메세지
    * </pre>
    */
   public String JSFTP_getErrMsg()
   {
      return m_sErrMsg.trim();
   }

   /**
    * <pre>
    *     문자열 거르기 함수
    *     @param  String =&gt; 거르기 대상  
    *     @return String =&gt; 걸러서 반환
    * </pre>
    */
   private String eraseNullChar(String str)
   {

      String strRet = "";
      String strTmp = "";

      if (str != null)
      {
         for (int i = 0; i < str.length(); i++)
         {
            strTmp = str.substring(i, i + 1);
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
