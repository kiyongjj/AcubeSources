package com.sds.acube.jstor;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSTORDebug
{
   // Console 출력에 대한 동기화
   private static Object _debugLock = new Object();

   private static boolean m_bTraceDebug = false;

   private static int m_nTraceDest;

   private static String m_strTraceFileDirPath;

   /**
    * Console 에 출력
    */
   public static int TRACE_CONSOLE = 0;

   /**
    * 파일에 출력
    */
   public static int TRACE_FILE = 1;

   /**
    * Console 과 파일에 동시 출력
    */
   public static int TRACE_BOTH = 2;

   /**
    * <pre>
    *   Debug Mode 를 설정한다.
    *   @param bTraceDebug : Debug Msg(Log) 를 남길 것인지 여부
    *   @param nTraceMode : Trace Msg 를 남기는 방법 (TRACE_CONSOL, TRACE_FILE, TRACE_BOTH)
    *   @param strTraceFile : TRACE_FILE/TRACE_BOTH 일 경우, 파일이 생성될 디렉토리 경로
    *   미지정(null)일 경우 System.getProperty (&quot;user.dir&quot;) 디렉토리로 자동 설정됨
    *   생성될 메세지(로그) 파일명은 &quot;JSTOR_TRACE_20031028.log&quot; 와 같이 생성됨
    *   @return void
    * </pre>
    */
   public static void setDebugMode(boolean bTraceDebug, int nTraceDest, String strTraceFileDirPath)
   {
      m_bTraceDebug = bTraceDebug;
      m_nTraceDest = nTraceDest;
      m_strTraceFileDirPath = strTraceFileDirPath;
   }

   /**
    * <pre>
    *   Debug Msg 를 출력한다
    *   @param strMsg : 출력할 내용
    *   @return void
    * </pre>
    */
   public static void writeTrace(int nConnID, String strMsg)
   {
      String strThreadName = Thread.currentThread().getName();
      SimpleDateFormat dFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
      String strHeader = null;

      if (0 == nConnID)
      {
         //strHeader = "---< JStorApi Trace Msg : Before Connect >-------------------------------------------------";
      }
      else
      {
         strHeader = "---< JStorApi Trace Msg : Connection ID (Socket Number) = " + nConnID
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
         strTraceFileFullPath = m_strTraceFileDirPath + File.separator + "JSTOR_TRACE_" + fFormat.format(new Date())
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
