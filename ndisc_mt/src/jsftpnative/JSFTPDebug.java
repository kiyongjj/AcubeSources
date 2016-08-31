package jsftpnative;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSFTPDebug extends JSFTPCommon
{
   // Console 출력에 대한 동기화
   private static Object _debugLock = new Object();

   private static boolean m_bTraceDebug = false;

   private static int m_nTraceDest;

   private static String m_strTraceFileDirPath;

   protected static int TRACE_CONSOLE = 0;

   protected static int TRACE_FILE = 1;

   protected static int TRACE_BOTH = 2;

   protected static void setDebugMode(boolean bTraceDebug, int nTraceDest, String strTraceFileDirPath)
   {
      m_bTraceDebug = bTraceDebug;
      m_nTraceDest = nTraceDest;
      m_strTraceFileDirPath = strTraceFileDirPath;
   }

   protected static void writeTrace(int nConnID, String strMsg)
   {
      String strThreadName = Thread.currentThread().getName();
      SimpleDateFormat dFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
      String strHeader = null;

      if (0 == nConnID)
      {
         strHeader = "---< JSFTPApi Trace Msg : Before Connect >-------------------------------------------------";
      }
      else
      {
         strHeader = "---< JSFTPApi Trace Msg : Connection ID (Socket Number) = " + nConnID
               + "----------------------------";
      }

      strMsg = strHeader + System.getProperty("line.separator") + "◈ [" + dFormat.format(new Date()) + "] "
            + strThreadName + System.getProperty("line.separator") + strMsg + System.getProperty("line.separator")
            + "-------------------------------------------------------------------------------------------";

      // Trace Msg 를 남길 경우
      if (true == m_bTraceDebug)
      {
         // 화면 출력
         if (TRACE_CONSOLE == m_nTraceDest)
         {
            writeTraceConsole(strMsg);
         }
         // 파일 출력
         else if (TRACE_FILE == m_nTraceDest)
         {
            writeTraceFile(strMsg);
         }
         // 둘 다 출력
         else if (TRACE_BOTH == m_nTraceDest)
         {
            writeTraceConsole(strMsg);
            writeTraceFile(strMsg);
         }
      }
   }

   // Console 출력
   private static void writeTraceConsole(String strMsg)
   {
      synchronized (_debugLock)
      {
         System.out.println(strMsg);
      }
   }

   // 파일 출력
   private static synchronized void writeTraceFile(String strMsg)
   {
      String strTraceFileFullPath = null;
      FileWriter fileTrace = null;

      SimpleDateFormat fFormat = new SimpleDateFormat("yyyyMMdd");

      try
      {
         // 경로가 미지정일 경우 또는 디렉토리 경로가 존재하지 않을 경우
         if ((null == m_strTraceFileDirPath) || (!(new File(m_strTraceFileDirPath)).exists()))
         {
            m_strTraceFileDirPath = System.getProperty("user.dir");
         }

         // 파일명 생성
         strTraceFileFullPath = m_strTraceFileDirPath + File.separator + "JSFTP_TRACE_" + fFormat.format(new Date())
               + ".log";
         // 파일 객체 생성
         fileTrace = new FileWriter(strTraceFileFullPath, true);
         // Msg Formatting
         strMsg = strMsg + System.getProperty("line.separator");
         // Msg Writing
         fileTrace.write(strMsg);
      }
      catch (Exception e)
      {
         // e.printStackTrace ();
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
