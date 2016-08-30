package jsftpnative;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class JSFTPNative extends JSFTPSocket
{
   private int m_nErrCode = -1;

   private String m_sErrMsg = null;

   private String m_sBaseDir = null;

   protected SocketChannel m_sc = null;

   static
   {
      String strVersion = "JSFTP API USING J2SE NIO V1.2 (JNI Package Compliance)";

      System.out.println("\n");
      System.out.println("-------------------------------------------------------------------");
      System.out.println("▶ Company :  SAMSUNG SDS");
      System.out.println("▶ Product Name : JSFTP API");
      System.out.println("▶ Version : " + strVersion);
      System.out.println("-------------------------------------------------------------------");
      System.out.println("\n");

      System.setProperty("jsftp_api_version", strVersion);
   }

   public JSFTPNative()
   {
      String strTraceType = null;

      strTraceType = System.getProperty("jsftp_trace_type");

      if (null != strTraceType)
      {
         // TRACE MODE SETTING
         if ("console".equalsIgnoreCase(strTraceType))
         {
            JSFTPDebug.setDebugMode(true, JSFTPDebug.TRACE_CONSOLE, null);
         }
         else if ("file".equalsIgnoreCase(strTraceType))
         {
            JSFTPDebug.setDebugMode(true, JSFTPDebug.TRACE_FILE, null);
         }
         else if ("both".equalsIgnoreCase(strTraceType))
         {
            JSFTPDebug.setDebugMode(true, JSFTPDebug.TRACE_BOTH, null);
         }
      }
   }

   protected void setError(int nErrCode, String sErrMsg)
   {
      m_nErrCode = (nErrCode > 0 ? (-1 * nErrCode) : nErrCode);
      m_sErrMsg = sErrMsg;
   }

   public int JSFTP_getErrCode()
   {
      return m_nErrCode;
   }

   public String JSFTP_getErrMsg()
   {
      if (null != m_sErrMsg)
      {
         m_sErrMsg.trim();
      }

      return m_sErrMsg;
   }

   public String JSFTP_getBaseDirValue()
   {
      if (null != m_sBaseDir)
      {
         m_sBaseDir.trim();
      }

      return m_sBaseDir;
   }

   public int JSFTP_Connect(String sIPAddr, int nPortNo)
   {
      int nRet = -1;

      try
      {
         if (null != sIPAddr)
         {
            sIPAddr = sIPAddr.trim();
         }

         m_sc = SocketChannel.open(new InetSocketAddress(sIPAddr, nPortNo));
         // set blocking nio mode for jsftp
         m_sc.configureBlocking(true);

         nRet = m_sc.socket().getLocalPort();

      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_Connect() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());

         try
         {
            if (null != m_sc)
            {
               m_sc.close();
            }
         }
         catch (Exception ex)
         {
            ;
         }

         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public void JSFTP_Disconnect(int nConnID)
   {
      String strServiceCmd = null;

      try
      {
         // 연결 종료
         strServiceCmd = SFTP_CMD_QUIT;

         // send Service Information (For Disconnect)
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (null != m_sc)
            {
               m_sc.close();
            }
         }
         catch (Exception ex)
         {
            ;
         }
      }
   }

   public int JSFTP_UploadEx(int nConnID, int nNumOfFile, String[][] sPathArr, int nHowFlag, int nEncFlag)
   {
      String strServiceCmd = null;
      String[][] arrUploadFile = null;
      String[] arrResponse = null;

      int nRet = -1;
      boolean bRet = false;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_UploadEx() Strated");

      try
      {
         // nNumOfFile 과 실제 배열갯수를 비교하는 코드가 들어가야 함
         // 일단 맞다고 가정하고...

         // 존재하는 파일인지 여부를 확인하는 코드가 들어가야 함
         // Access 가 가능한지 여부도 체크됨

         // 전송할 파일 정보를 구성함
         arrUploadFile = new String[nNumOfFile][3];
         for (int i = 0; i < nNumOfFile; i++)
         {
            // 로컬 파일
            File file = new File(sPathArr[i][0]);
            int nFileLength = (int) file.length();

            // Send 할 로컬 파일 경로
            arrUploadFile[i][0] = sPathArr[i][0];
            // 로컬파일의 사이즈
            arrUploadFile[i][1] = nFileLength + "";
            // Send 할 파일이 저장될 리모트 경로
            arrUploadFile[i][2] = sPathArr[i][1];
         }

         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_UPLOAD + SFTP_DELIMSTR + nNumOfFile;
         for (int i = 0; i < nNumOfFile; i++)
         {
            // 리모트 파일 경로 + 전송될 파일(로컬) 파일의 사이즈
            strServiceCmd = strServiceCmd + SFTP_DELIMSTR + arrUploadFile[i][2] + SFTP_DELIMSTR + arrUploadFile[i][1];

         }

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         // 실제 파일 전송
         bRet = uploadFile(nConnID, nNumOfFile, arrUploadFile, m_sc);
         if (false == bRet)
         {
            JSFTPDebug.writeTrace(nConnID, "Send_File() Fail");
            throw new Exception();
         }

         // 응답을 받음
         // Send_File() 이 성공이든 실패이든간에 여기서 확인이 가능함
         arrResponse = Recv_Response(nConnID, SFTP_CMD_UPLOAD, m_sc);

         JSFTPDebug.writeTrace(nConnID, "JSFTP_UploadEx() Response : " + arrResponse[0] + " - " + arrResponse[1]);

         // 코드 비교
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            // 성공
            nRet = JSFTP_SUCCESS_RET;
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_UploadEx() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public int JSFTP_DownloadEx(int nConnID, int nNumOfFile, String[][] sPathArr, int nDelFlag, int nHowFlag,
         int nEncFlag)
   {
      String strServiceCmd = null;
      String[][] arrDownFile = null;
      String[] arrResponse = null;

      int nRet = -1;
      boolean bRet = false;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_DownloadEx() Strated");

      try
      {
         if (SFTP_DOWN_DEL != nDelFlag)
         {
            nDelFlag = SFTP_DOWN_NO_DEL;
         }

         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_DOWNLOAD + SFTP_DELIMSTR + nDelFlag + SFTP_DELIMSTR + nNumOfFile;
         for (int i = 0; i < nNumOfFile; i++)
         {
            // 전송받을 원격측 파일
            strServiceCmd = strServiceCmd + SFTP_DELIMSTR + sPathArr[i][1];
         }

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         arrResponse = Recv_Response(nConnID, SFTP_CMD_DOWNLOAD, m_sc);
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            arrDownFile = new String[nNumOfFile][3];

            for (int i = 0, j = 2; j < arrResponse.length; i++, j++)
            {
               // 저장할 로컬 파일
               arrDownFile[i][0] = sPathArr[i][0];
               // 다운로드 받을 원격 파일
               arrDownFile[i][1] = sPathArr[i][1];
               // 다운로드 받을 원격 파일의 사이즈 (리얼파일 사이즈)
               arrDownFile[i][2] = arrResponse[j];
            }

            bRet = downloadFile(nConnID, nNumOfFile, arrDownFile, m_sc);
            if (false == bRet)
            {
               JSFTPDebug.writeTrace(nConnID, "Send_File() Fail");
               throw new Exception();
            }

            nRet = JSFTP_SUCCESS_RET;
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_DownloadEx() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public int JSFTP_MakeDir(int nConnID, String sDirPath)
   {
      String strServiceCmd = null;
      String[] arrResponse = null;

      int nRet = -1;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_MakeDir() Strated");

      try
      {
         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_MAKEDIR + SFTP_DELIMSTR + "1" + SFTP_DELIMSTR + sDirPath;

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         // 응답을 받음
         // Send_File() 이 성공이든 실패이든간에 여기서 확인이 가능함
         arrResponse = Recv_Response(nConnID, SFTP_CMD_MAKEDIR, m_sc);

         JSFTPDebug.writeTrace(nConnID, "JSFTP_MakeDir() Response : " + arrResponse[0] + " - " + arrResponse[1]);

         // 코드 비교
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            // 성공
            nRet = JSFTP_SUCCESS_RET;
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_MakeDir() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public int JSFTP_ExistFile(int nConnID, String sFilePath, int nAccessMode)
   {
      String strServiceCmd = null;
      String[] arrResponse = null;

      int nRet = -1;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_ExistFile() Strated");

      try
      {
         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_EXISTFILE + SFTP_DELIMSTR + "1" + SFTP_DELIMSTR + sFilePath + SFTP_DELIMSTR
               + nAccessMode;

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         // 응답을 받음
         // Send_File() 이 성공이든 실패이든간에 여기서 확인이 가능함
         arrResponse = Recv_Response(nConnID, SFTP_CMD_EXISTFILE, m_sc);

         JSFTPDebug.writeTrace(nConnID, "JSFTP_ExistFile() Response : " + arrResponse[0] + " - " + arrResponse[1]);

         // 코드 비교
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            // 성공
            nRet = JSFTP_SUCCESS_RET;
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_ExistFile() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public int JSFTP_GetBaseDir(int nConnID, String sServName)
   {
      String strServiceCmd = null;
      String[] arrResponse = null;

      int nRet = -1;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_GetBaseDir() Strated");

      try
      {
         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_GETCONF + SFTP_DELIMSTR + "1" + SFTP_DELIMSTR + sServName;

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         // 응답을 받음
         // Send_File() 이 성공이든 실패이든간에 여기서 확인이 가능함
         arrResponse = Recv_Response(nConnID, SFTP_CMD_GETCONF, m_sc);

         // 코드 비교
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            // 성공
            nRet = JSFTP_SUCCESS_RET;
            // BASE DIR 반환
            m_sBaseDir = arrResponse[3];
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_GetBaseDir() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }

   public int JSFTP_DeleteFile(int nConnID, String sFilePath)
   {
      String strServiceCmd = null;
      String[] arrResponse = null;

      int nRet = -1;

      JSFTPDebug.writeTrace(nConnID, "JSFTP_DeleteFile() Strated");

      try
      {
         // 서비스 커맨드 헤더
         strServiceCmd = SFTP_CMD_DELETEFILE + SFTP_DELIMSTR + "1" + SFTP_DELIMSTR + sFilePath;

         // 서비스 커맨드 헤더 전송
         Send_ServiceCmd(nConnID, strServiceCmd, m_sc);

         // 응답을 받음
         // Send_File() 이 성공이든 실패이든간에 여기서 확인이 가능함
         arrResponse = Recv_Response(nConnID, SFTP_CMD_DELETEFILE, m_sc);

         JSFTPDebug.writeTrace(nConnID, "JSFTP_DeleteFile() Response : " + arrResponse[0] + " - " + arrResponse[1]);

         // 코드 비교
         if (SFTPD_SUCCESS_STR.equals(arrResponse[0]))
         {
            // 성공
            nRet = JSFTP_SUCCESS_RET;
         }
         else
         {
            int nErrCode = -1;

            if (null == arrResponse[0] || 0 == arrResponse[0].length())
            {
               nErrCode = REMOT_ERR_GENERAL;
            }
            else
            {
               nErrCode = Integer.parseInt(arrResponse[0]);
               nErrCode = (nErrCode > 0 ? -1 * nErrCode : nErrCode);
            }

            setError(nErrCode, arrResponse[1]);

            nRet = nErrCode;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         JSFTPDebug.writeTrace(0, "JSFTP_DeleteFile() Fail : " + e.getMessage());
         setError(EXCEP_ERR_UNEXPECTED, e.getMessage());
         nRet = EXCEP_ERR_UNEXPECTED;
      }

      return nRet;
   }
}
