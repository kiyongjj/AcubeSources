package jsftpnative;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class JSFTPCommon
{
   // /////////////////////////////////////////////////////////////////////
   // Developer's Define
   // /////////////////////////////////////////////////////////////////////
   protected final int JSFTP_ERR_RET = -1;

   protected final int JSFTP_SUCCESS_RET = 0;

   protected final String SFTP_CMD_QUIT = "QUIT";

   protected final String SFTP_CMD_UPLOAD = "UPLOAD";

   protected final String SFTP_CMD_DOWNLOAD = "DOWNLOAD";

   protected final String SFTP_CMD_GETCONF = "GETCONF";

   protected final String SFTP_CMD_MAKEDIR = "MAKEDIR";

   protected final String SFTP_CMD_MAKEDIREX = "MAKEDIREX";

   protected final String SFTP_CMD_EXISTFILE = "EXISTFILE";

   protected final String SFTP_CMD_DELETEFILE = "DELETEFILE";

   protected final String SFTP_DELIMSTR = "\t";
 
   protected final String SFTP_CANCEL_STR = "0000000001";

   protected final String SFTP_NOCANCEL_STR = "0000000000";

   protected final String SFTPD_SUCCESS_STR = "0000";

   protected final int SFTP_TRANSBUF_SIZE = 4096;

   protected final int SFTP_CHECK_COUNT = 4;

   protected final int SFTP_SINFO_FORMAT_LEN = 10;

   protected final int SFTP_DOWN_DEL = 1;

   protected final int SFTP_DOWN_NO_DEL = 0;

   // /////////////////////////////////////////////////////////////////////

   // /////////////////////////////////////////////////////////////////////
   // ERROR CODE MAPPING
   // /////////////////////////////////////////////////////////////////////
   protected final int LOCAL_ERR_FILE_NUM_MISMATCH = -100;

   protected final int LOCAL_ERR_FILE_NOT_FOUND = -101;

   // 리모트측 에러 : 비정상적으로 에러코드 할당이 안된 상태
   protected final int REMOT_ERR_GENERAL = -200;

   // 예기치 않은 Exception 발생
   protected final int EXCEP_ERR_UNEXPECTED = -999;

   // /////////////////////////////////////////////////////////////////////

   protected static CharsetDecoder m_decoder = Charset.forName(System.getProperty("file.encoding")).newDecoder();

   // 문자열 길이를 알아옴
   protected int getLength(String strData)
   {
      byte[] bData = null;
      int nLength = -1;

      bData = strData.getBytes();

      nLength = bData.length;

      return nLength;
   }

   // "0000000000" 와 같이 10자리의 자료 형태를 가져야 함
   protected String getFormatedServiceCodeLen(int nDataLength)
   {
      String strRet = "";

      String strTmp = Integer.toString(nDataLength);
      int nTmpLen = strTmp.length();

      for (int i = 0; i < (SFTP_SINFO_FORMAT_LEN - nTmpLen); i++)
      {
         strRet += "0";
      }

      strRet += strTmp;

      return strRet;
   }
}
