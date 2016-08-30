package com.sds.acube.ndisc.mts.common;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class NDConstant {
   public static final String VERSION = "V5.0";
   
   // DELIM STR
   public static final String DELIM_STR = "|";

   // SERVICE STATUS CODE
   public static final String SERVICE_STAT_INIT = "00000";
   public static final String SERVICE_STAT_FILEREG = "00001";
   public static final String SERVICE_STAT_FILEGET = "00002";
   public static final String SERVICE_STAT_FILEREP = "00003";
   public static final String SERVICE_STAT_FILEDEL = "00004";
   public static final String SERVICE_STAT_FILECPY = "00005";
   public static final String SERVICE_STAT_FILEMOV = "00006";
   public static final String SERVICE_STAT_FILEINFO = "00007";
   public static final String SERVICE_STAT_VOLINFO = "00008";
   public static final String SERVICE_STAT_MKVOLUME = "10001";
   public static final String SERVICE_STAT_RMVOLUME = "10002";
   public static final String SERVICE_STAT_MKMEDIA = "20001";
   public static final String SERVICE_STAT_GETCONF = "30001";
   public static final String SERVICE_STAT_QUIT = "99999";
   public static final String SERVICE_STAT_READY = "90001";

   // STORAGE PATH RETRIEVE CODE
   public static final String STORAGE_PATH_REGIST = "PATH_REGIST";
   public static final String STORAGE_PATH_ACCESS = "PATH_ACCESS";
   
   // SUCCESS / FAIL CODE
   public static final String NO_ERROR = "0";
   public static final String ERROR = "-1";
      
   public static final int INIT_BUFFER_SIZE = 4096;
   public static final int STAT_BUFFER_SIZE = 5;
   public static final int REPLY_BUFFER_SIZE = 4096;
   public static final int FILE_TRANS_BUFFER_SIZE = 4096 * 8;   // 32768 byte (32k)
   
   public static final String NDISC_NA_RESERV = "@@NOT_ASSIGN@@";
   
   public static final String STAT_NONE = "0";
   public static final String STAT_COMP = "1";  
   public static final String STAT_ENC = "2";
   public static final String STAT_COMP_ENC = "3";
   public static final String STAT_AUTO = "-1";

   public static CharsetDecoder decoder = Charset.forName(System.getProperty("file.encoding")).newDecoder();
   
   // TRIAL VERSION'S MAX FILE OPERATION LIMIT
   public static final int MAX_FILE_OPERATION = 10;
   
   public static final int MEDIA_SIZE_UP_FAIL = -9000;
   
   public static final int MEDIA_SIZE_DOWN_FAIL = -9001;
}
