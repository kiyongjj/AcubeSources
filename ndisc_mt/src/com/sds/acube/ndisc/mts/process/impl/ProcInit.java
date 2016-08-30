package com.sds.acube.ndisc.mts.process.impl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;

import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcInit extends DefProcessAdaptor {
   public ProcInit(LoggerIF logger) {
      super(logger);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcInit.process() started !!!!");
      boolean bRet = false;

      SocketChannel sc = (SocketChannel)obj1;
      JobVO job = (JobVO)obj2;
      
      ByteBuffer buffer = null;
      Object object = null;
      try {
         buffer = ByteBuffer.allocateDirect(NDCommon.INIT_BUFFER_SIZE);

         while (buffer.position() < NDConstant.INIT_BUFFER_SIZE) {
            int bytes = sc.read(buffer);
            
            int i = 0;
		    while (bytes <= 0) {
		    	this.logger.info("ProcInit -> process -> sc.read() <= 0 : " + ++i + "st.");
		        bytes = sc.read(buffer);
		 
		        if ((bytes <= 0) && (i == NDCommon.SO_PROCINIT_READ_CNT)) {
		        	throw new NetworkException(
		               "illegal connection detected - connection will be closed");
		       }
		    }
            
            /*
             * if (bytes <= 0) { logger.log(LoggerIF.LOG_DEBUG, sc.toString() + " ProcInit() sc.read() <= 0, bytes = " +
             * bytes); throw new NetworkException("illegal connection detected - connection will be closed"); }
             */
            
            /*
            if (true) {
               System.out.println("############# ndisc 서버 테스트 - connection will be closed");
               throw new NetworkException("########### ndisc 서버 테스트 - connection will be closed");
            }
            */
            
            /*
            if (bytes <= 0) {
               System.out.println("ProcInit() sc.read() <= 0 1st.");
               bytes = sc.read(buffer);
               if (bytes <= 0) {
                  System.out.println("ProcInit() sc.read() <= 0 2st.");
                  bytes = sc.read(buffer);
                  if (bytes <= 0) {
                     System.out.println("ProcInit() sc.read() <= 0 3st.");
                     bytes = sc.read(buffer);
                     if (bytes <= 0) {
                        System.out.println("ProcInit() sc.read() <= 0 4st.");
                        throw new NetworkException("illegal connection detected - connection will be closed");
                     }
                  }
               }
            }
            */
         }

         buffer.flip();
         String strMsg = NDConstant.decoder.decode(buffer).toString();

         // if (0 == strMsg.length()) {
         // throw new NetworkException("illegal connection detected - connection will be closed");
         // }

         StringTokenizer sTK = new StringTokenizer(strMsg, NDCommon.DELIM_STR);
         String strServiceStat = sTK.nextToken().trim();
         int objectCounts = Integer.parseInt(sTK.nextToken().trim());

         if (NDConstant.SERVICE_STAT_QUIT.equals(strServiceStat)) {
            // request for disconnect detected
            bRet = false;
         } else {
            if (NDConstant.SERVICE_STAT_FILEREG.equals(strServiceStat)) {
               object = readFileRegInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILEGET.equals(strServiceStat)) {
               object = readFileGetInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILEREP.equals(strServiceStat)) {
               object = readFileRepInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILEDEL.equals(strServiceStat)) {
               object = readFileDelInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILECPY.equals(strServiceStat)) {
               object = readFileCpyInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILEMOV.equals(strServiceStat)) {
               object = readFileMovInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_FILEINFO.equals(strServiceStat)) {
               object = readFileInformationInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_VOLINFO.equals(strServiceStat)) {
               object = readVolInformationInfo(objectCounts, sTK);
            } else if (NDConstant.SERVICE_STAT_MKMEDIA.equals(strServiceStat)) {
               object = readMakeMediaInfo(sTK);
            } else if (NDConstant.SERVICE_STAT_MKVOLUME.equals(strServiceStat)) {
               object = readMakeVolumeInfo(sTK);
            } else if (NDConstant.SERVICE_STAT_GETCONF.equals(strServiceStat)) {
               object = readGetConfInfo(sTK);
            } else {
               object = null;
            }

            job.setServiceStat(strServiceStat);
            job.setFileCount(objectCounts);
            
            job.setObject(object);
            modifyClient(sc, job);

            bRet = true;
         }
      } catch (Exception e) {
    	  e.printStackTrace();
         bRet = false;
         logger.log(LoggerIF.LOG_ERROR, sc.toString() + " " + e.getMessage());
      } finally {
         deleteBuffer(buffer);
      }
      logger.log(LoggerIF.LOG_DEBUG, "ProcInit.process() ended !!!!");
      return bRet;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      return null;
   }

   protected NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception {
      return null;
   }

   private NFile[] readFileRegInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;
      try {

         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setName(sTK.nextToken().trim());
            nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
            nFile[i].setVolumeId(Integer.parseInt(sTK.nextToken().trim()));
            nFile[i].setStatType(sTK.nextToken().trim());
            nFile[i].setId(getFileID(sTK.nextToken().trim()));
            nFile[i].setCreatedDate(getDate(sTK.nextToken().trim()));
         }

      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileGetInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;

      try {
         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setId(sTK.nextToken().trim());
            nFile[i].setStatType(sTK.nextToken().trim());
         }

      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileRepInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;
      try {

         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setName(sTK.nextToken().trim());
            nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
            nFile[i].setStatType(sTK.nextToken().trim());
            nFile[i].setId(getFileID(sTK.nextToken().trim()));
            nFile[i].setModifiedDate(getDate(sTK.nextToken().trim()));
         }

      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileDelInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;

      try {
         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setId(sTK.nextToken().trim());
         }

      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileCpyInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;
      try {

         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setVolumeId(Integer.parseInt(sTK.nextToken().trim()));
            nFile[i].setStatType(sTK.nextToken().trim());
            nFile[i].setId(sTK.nextToken().trim());
            nFile[i].setCreatedDate(getDate(sTK.nextToken().trim()));
         }
      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileMovInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;
      try {

         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setVolumeId(Integer.parseInt(sTK.nextToken().trim()));
            nFile[i].setStatType(sTK.nextToken().trim());
            nFile[i].setId(sTK.nextToken().trim());
            nFile[i].setCreatedDate(getDate(sTK.nextToken().trim()));
         }
      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readFileInformationInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      NFile[] nFile = null;

      try {
         nFile = new NFile[nNumOfFiles];

         for (int i = 0; i < nNumOfFiles; i++) {
            nFile[i] = new NFile();
            nFile[i].setId(sTK.nextToken().trim());
         }

      } catch (Exception e) {
         e.printStackTrace();
         nFile = null;
      }

      return nFile;
   }

   private NFile[] readVolInformationInfo(int nNumOfFiles, StringTokenizer sTK) throws Exception {
      return null;
   }

   private Media readMakeMediaInfo(StringTokenizer sTK) throws Exception {
      Media media = null;

      try {
         media = new Media();

         media.setName(sTK.nextToken().trim());
         media.setType(Integer.parseInt(sTK.nextToken().trim()));
         media.setPath(sTK.nextToken().trim());
         media.setCreatedDate(getDate(NDConstant.NDISC_NA_RESERV));
         media.setDesc(sTK.nextToken().trim());
         media.setMaxSize(Long.parseLong(sTK.nextToken().trim()));
         media.setSize(0);
         media.setVolumeId(Integer.parseInt(sTK.nextToken().trim()));

      } catch (Exception e) {
         e.printStackTrace();
         media = null;
      }

      return media;
   }

   private Volume readMakeVolumeInfo(StringTokenizer sTK) throws Exception {
      Volume volume = null;

      try {
         volume = new Volume();

         volume.setName(sTK.nextToken().trim());
         volume.setAccessable(sTK.nextToken().trim());
         volume.setCreatedDate(getDate(NDConstant.NDISC_NA_RESERV));
         volume.setDesc(sTK.nextToken().trim());

      } catch (Exception e) {
         e.printStackTrace();
         volume = null;
      }

      return volume;
   }

   private Object readGetConfInfo(StringTokenizer sTK) throws Exception {

      try {
         ; // in this case, no action
      } catch (Exception e) {
         ;
      }

      return null;
   }
}