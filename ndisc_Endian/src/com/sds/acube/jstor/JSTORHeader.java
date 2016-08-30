package com.sds.acube.jstor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JSTORHeader
{

   // /////////////////////////////////////////////////////////////////////
   // Developer's Define
   // ////////////////////////////////////////////////////////////////////////
   protected final int JSTOR_ERR_RET = -1;

   protected final int JSTOR_SUCCESS_RET = 0;

   // 파일등록(저장) 정보 배열의 Index
   protected final int SEQ_REGINFO_PATH = 0;

   protected final int SEQ_REGINFO_VOLID = 1;

   protected final int SEQ_REGINFO_FLTID = 2;

   // Added 2003-12-12
   protected final int SEQ_REGINFO_FILECDATE = 3;

   protected final int SEQ_REGINFO_FILEID = 4;

   protected final int SZ_REGINFO = 5;

   // 파일가져오기 정보 배열의 Index
   protected final int SEQ_GETINFO_FILEID = 0;

   protected final int SEQ_GETINFO_FILEPATH = 1;

   protected final int SEQ_GETINFO_FLTID = 2;

   // 파일교체 정보 배열의 Index
   protected final int SEQ_REPINFO_FILEID = 0;

   protected final int SEQ_REPINFO_FILEPATH = 1;

   protected final int SEQ_REPINFO_FLTID = 2;

   // 파일복사 정보 배열의 Index
   protected final int SEQ_CPYINFO_FILEID = 0;

   protected final int SEQ_CPYINFO_VOLID = 1;

   protected final int SEQ_CPYINFO_FLTID = 2;

   protected final int SEQ_CPYINFO_CPYOPT = 3;

   protected final int SEQ_CPYINFO_IPADDR = 4;

   protected final int SEQ_CPYINFO_PORTNO = 5;

   // 파일이동 정보 배열의 Index
   protected final int SEQ_MOVINFO_FILEID = 0;

   protected final int SEQ_MOVINFO_VOLID = 1;

   protected final int SEQ_MOVINFO_FLTID = 2;

   protected final int SEQ_MOVINFO_MOVOPT = 3;

   protected final int SEQ_MOVINFO_IPADDR = 4;

   protected final int SEQ_MOVINFO_PORTNO = 5;

   // recv_ReplyHeader() Return Vector Index
   protected final int SEQ_RECV_RH_STATUS = 0;

   protected final int SEQ_RECV_RH_DATASIZE = 1;

   protected final int SZ_RECV_RH = 2;

   // 볼륨정보 반환 배열의 Index
   protected final int SEQ_VOL_ID = 0;

   protected final int SEQ_VOL_NAME = 1;

   protected final int SEQ_VOL_ENAME = 2;

   protected final int SEQ_VOL_TYPE = 3;

   protected final int SEQ_VOL_RIGHT = 4;

   protected final int SEQ_VOL_CDATE = 5;

   protected final int SEQ_VOL_DESC = 6;

   protected final int SZ_VOL_INFO = 7;

   // ONCE TRANS AMOUNT
   // protected final int JSTOR_TRANS_AMOUNT = 4096 * 64;
	protected final int JSTOR_TRANS_AMOUNT = 4096 * 8;
   
   // If Target File Storage Server = Win32 Platform
   protected final int JSTOR_TRANS_AMOUNT_WIN32 = 4096;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // User Cancel Check Count
   // ////////////////////////////////////////////////////////////////////////
   protected final int JSTOR_CHECK_COUNT = 4;

   protected final int JSTOR_USER_CANCEL = 100;

   protected final int JSTOR_NO_CANCEL = 0;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Header Info
   // - nServiceCode
   // - nServiceOpt
   // - szVersion
   // - nNumOfFile
   // - nSizeOfData
   // - chDelimChar
   // ////////////////////////////////////////////////////////////////////////
   // nService Code
   protected final int SH_SVCCODE_REG = 1100; // 저장

   protected final int SH_SVCCODE_REG_JSTOR = 9100; // 저장 (Win32 Server)

   protected final int SH_SVCCODE_GET = 1200; // 가져오기

   protected final int SH_SVCCODE_GET_JSTOR = 9200; // 가져오기 (Win32 Server)

   protected final int SH_SVCCODE_DEL = 1300; // 삭제

   protected final int SH_SVCCODE_REP = 1400; // 교체

   protected final int SH_SVCCODE_REP_JSTOR = 9400; // 교체 (Win32 Server)

   protected final int SH_SVCCODE_FLEMOV = 1600; // 파일이동

   protected final int SH_SVCCODE_FLECPY = 1700; // 파일복사

   protected final int SH_SVCCODE_TRANS = 100; // 트랜잭션 : 커밋/롤백

   protected final int SH_SVCCODE_VOLINFO = 200; // 볼륨정보

   protected final int SH_SVCCODE_VOLINFO_JSTOR = 2009; // 볼륨정보 (Win32 Server)

   protected final int SH_SVCCODE_QUIT = 0; // Disconnect

   // nService Opt
   protected final int SH_SVCOPT_NONE = 0;

   protected final int SH_SVCOPT_START = 1;

   protected final int SH_SVCOPT_COMMIT = 2;

   protected final int SH_SVCOPT_ROLLBACK = 4;

   // szVersion
   protected final String SH_SVCVER = "4.0";

   protected final int SH_SVCVER_LEN = 12;

   // nNumOfFile : API Input Value

   // nSizeOfData
   protected final int SH_SVCSZDATA_REG_CONST = 152;

   protected final int SH_SVCSZDATA_GET_CONST = 40;

   protected final int SH_SVCSZDATA_DEL_CONST = 33;

   protected final int SH_SVCSZDATA_REP_CONST = 128;

   protected final int SH_SVCSZDATA_CPY_CONST = 104;

   protected final int SH_SVCSZDATA_MOV_CONST = 104;

   // sizeof(INFO_VOL)
   protected final int SH_SIZEOF_INFO_VOL = 112;

   // chDelimChar
   protected final String SH_SVCDELIMCHAR = "\1";

   protected final int SH_SVCDELIMCHAR_LEN = 4;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Reg Info
   // - szFileName
   // - nFileSize
   // - szFileExt
   // - nFilterID
   // - nVolID
   // - szFileID
   // - szFileCDate
   // ////////////////////////////////////////////////////////////////////////
   protected final int SREG_FILENAME_LEN = 84;

   protected final int SREG_FILEEXT_LEN = 8;

   protected final int SREG_FILEID_LEN = 33;

   protected final int SREG_FILECDATE_LEN = 14;

   // ///////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Get Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SGET_FILEID_LEN = 36;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Del Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SDEL_FILEID_LEN = 33;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////6
   // # Service Rep Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SREP_FILEID_LEN = 33;

   protected final int SREP_FILENAME_LEN = 81;

   protected final int SREP_FILEEXT_LEN = 6;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Vol Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SVOL_NAME_LEN = 21;

   protected final int SVOL_EAME_LEN = 22;

   protected final int SVOL_CDATE_LEN = 15;

   protected final int SVOL_DESC_LEN = 41;

   // ////////////////////////////////////////////////////////////////////////
   // # Service Cpy Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SCPY_FILEID_LEN = 36;

   protected final int SCPY_IPADDR_LEN = 52;

   protected final int SCPY_CPYIN_OPT = 1;

   protected final int SCPY_CPYOUT_OPT = 2;

   // ////////////////////////////////////////////////////////////////////////

   // ////////////////////////////////////////////////////////////////////////
   // # Service Mov Info
   // ////////////////////////////////////////////////////////////////////////
   protected final int SMOV_FILEID_LEN = 36;

   protected final int SMOV_IPADDR_LEN = 52;

   protected final int SMOV_MOVIN_OPT = 1;

   protected final int SMOV_MOVOUT_OPT = 2;

   // ////////////////////////////////////////////////////////////////////////
   
   protected static Logger logger = null;  
   

   // 저장서버의 구조체가 인식할 수 있는 포맷의 데이터 스트림을 생성
   protected String makeValidDataStream(String strData, int nLength) throws Exception
   {
      String strRet = null;

      // strRet = strData.trim () + "\0";
      strRet = strData + "\0";

      if (strRet.length() < nLength)
      {
         while (strRet.length() != nLength)
         {
            strRet += " ";
         }
      }

      return strRet;
   }

   // 파일의 속성을 반환
   protected Properties getFileProperties(String strFilePath) throws Exception
   {
      File fp = null;
      Properties propRet = null;

      fp = new File(strFilePath);
      propRet = new Properties();

      propRet.setProperty("FILE_FULL_PATH", strFilePath);

      // Set File Size
      propRet.setProperty("FILE_SIZE", "" + fp.length());

      String strFileName = fp.getName();
      propRet.setProperty("FILE_NAME", strFileName);
      String strFileExt = getFileExt(strFileName);

      propRet.setProperty("FILE_EXT", strFileExt);

      return propRet;
   }

   protected String getFileExt(String strFileName) throws Exception
   {
      String strFileExt = null;
      int nIndex = strFileName.lastIndexOf(".");
      if (-1 == nIndex)
      {
         strFileExt = "";
      }
      else
      {
         strFileExt = strFileName.substring(nIndex + 1, strFileName.length());
         if (5 < strFileExt.length())
            strFileExt = strFileExt.substring(0, 5);
      }
      return strFileExt;
   }

   protected String getValidFileName(String strFileName) throws Exception
   {
      String strRet = null;

      if (!isAlpNum(strFileName))
      {
         strRet = "__FILENAME_STOR_REDEFINED__." + getFileExt(strFileName);
      }
      else
      {
         strRet = strFileName;
      }

      return strRet;
   }

   /**
    * 문자열이 알파벳 또는 숫자인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자열
    * @return 알파벳 또는 숫자인지의 여부에 따라 'true' or 'false'
    */
   protected boolean isAlpNum(String s)
   {
      if (!isAlpha(s) && !isNumber(s))
         return false;
      return true;
   }

   /**
    * 문자열이 알파벳인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자열
    * @return 알파벳인지의 여부에 따라 'true' or 'false'
    */
   protected boolean isAlpha(String s)
   {
      if (s == null)
         return false;

      s = s.trim();
      int len = s.length();
      if (len == 0)
         return false;

      for (int i = 0; i < len; i++)
      {
         if (!isAlpha(s.charAt(i)))
            return false;
      }
      return true;
   }

   /**
    * 문자열이 숫자인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자열
    * @return 숫자인지의 여부에 따라 'true' or 'false'
    */
   protected boolean isNumber(String s)
   {
      if (s == null)
         return false;

      s = s.trim();
      int len = s.length();
      if (len == 0)
         return false;

      for (int i = 0; i < len; i++)
      {
         if (!isNumber(s.charAt(i)))
            return false;
      }
      return true;
   }

   /**
    * 문자 하나가 한글 또는 알파벳인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자열
    * @return 한글또는 알파벳 여부에 따라 'true' or 'false'
    */
   protected boolean isHanAlp(char c)
   {
      if (!isAlpha(c) && !isHangul(c))
         return false;
      return true;
   }

   /**
    * 문자 하나가 숫자인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자
    * @return 숫자인지의 여부에 따라 'true' or 'false'
    */
   protected boolean isNumber(char c)
   {
      if (c < '0' || c > '9')
         return false;
      return true;
   }

   /**
    * 문자 하나가 알파벳인지 검사
    * 
    * @param s
    *           검사 하고자 하는 문자
    * @return 알파벳인지의 여부에 따라 'true' or 'false'
    */
   public boolean isAlpha(char c)
   {
      if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != '_' && c != ' ' && c != '.')
         return false;
      return true;
   }

   /**
    * 문자 하나가 한글인지 검사
    * 
    * @param c
    *           검사 하고자 하는 문자
    * @return 한글 여부에 따라 'true' or 'false'
    */
   public boolean isHangul(char c)
   {
      if (c < 0xAC00 || c > 0xD7A3)
         return false;
      return true;
   }

   protected boolean copyFile(String strSrcFile, String strDstFile)
   {
      int BUFF_SIZE = 4096;
      byte[] buffer = new byte[BUFF_SIZE];

      boolean bRet = false;
      // int i = 0;
      FileInputStream fis = null;
      FileOutputStream fos = null;

      try
      {
         fis = new FileInputStream(new File(strSrcFile));
         fos = new FileOutputStream(new File(strDstFile));

         while (true)
         {
            synchronized (buffer)
            {
               int amountRead = fis.read(buffer);
               if (amountRead == -1)
               {
                  break;
               }

               fos.write(buffer, 0, amountRead);
            }
         }

         bRet = true;
      }
      catch (Exception e)
      {
         bRet = false;
      }
      finally
      {
         try
         {
            fis.close();
            fos.close();
         }
         catch (Exception e)
         {
            bRet = false;
         }
      }

      return bRet;
   }
   

   // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // BELOW : JSTOR API For Win32 Storage Server Section
   // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

   protected final int STOR_CODE_QUIT = 0;

   protected final int STOR_CODE_TRANSACTION = 100;

   protected final int STOR_CODE_VOLINFO = 200;

   protected final int STOR_CODE_FILEREG = 1100;

   protected final int STOR_CODE_FILEGET = 1200;

   protected final int STOR_CODE_FILEDEL = 1300;

   protected final int STOR_CODE_FILEREP = 1400;

   protected final int STOR_CODE_FILEFILTER = 1500;

   protected final int STOR_CODE_FILEMOV = 1600;

   protected final int STOR_CODE_FILECPY = 1700;

   protected final int STOR_CODE_FILEINFO = 1800;

   protected final int STOR_CODE_TIFFEXT = 1900;

   protected final int STOR_CODE_TIFFMRG = 2000;

   protected final int STOR_CODE_TIFFINFO = 2100;

   protected final String STOR_CMDSTR_QUIT = "QUIT";

   protected final String STOR_CMDSTR_TRANSACTION = "TRANSACT";

   protected final String STOR_CMDSTR_VOLINFO = "VOLINFO";

   protected final String STOR_CMDSTR_FILEREG = "FILEREG";

   protected final String STOR_CMDSTR_FILEGET = "FILEGET";

   protected final String STOR_CMDSTR_FILEDEL = "FILEDEL";

   protected final String STOR_CMDSTR_FILEREP = "FILEREP";

   protected final String STOR_CMDSTR_FILEFILTER = "FILEFILTER";

   protected final String STOR_CMDSTR_FILEMOV = "FILEMOV";

   protected final String STOR_CMDSTR_FILECPY = "FILECPY";

   protected final String STOR_CMDSTR_FILEINFO = "FILEINFO";

   protected final String STOR_CMDSTR_TIFFEXT = "TIFFEXT";

   protected final String STOR_CMDSTR_TIFFMRG = "TIFFMRG";

   protected final String STOR_CMDSTR_TIFFINFO = "TIFFINFO";

   public byte[] getFormat10(int data)
   {
      String result = "";
      String tmp = Integer.toString(data);
      int tmplen = tmp.length();

      for (int i = 0; i < (10 - tmplen); i++)
      {
         result += "0";
      }

      result += tmp;
      return result.getBytes();
   }
}
