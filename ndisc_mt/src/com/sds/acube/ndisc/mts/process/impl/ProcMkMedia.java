package com.sds.acube.ndisc.mts.process.impl;

import java.io.File;
import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcMkMedia extends DefProcessAdaptor {
   public ProcMkMedia(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcMkMedia.process() started !!!!");
      boolean bRet = false;
      String strMsg = null;
      
      SocketChannel sc = (SocketChannel) obj1;      
      JobVO job = (JobVO) obj2;
      Media media = null;

      try {
         receiveStatReady(sc); 
         media = (Media)job.getObject();
         makeMediaPath(media.getPath());
         insertNewMediaToDB(media);
         bRet = true;
      } catch (Exception e) {
         strMsg = e.getMessage();
         bRet = false;
      } finally {
         if (true == bRet) {
            strMsg = NDConstant.NO_ERROR + NDConstant.DELIM_STR;
         } else {
            strMsg = NDConstant.ERROR + NDConstant.DELIM_STR + strMsg;
         }

         sendReplyMsg(sc, strMsg);
         setInitStatus(sc, job);         
      }
      logger.log(LoggerIF.LOG_DEBUG, "ProcMkMedia.process() ended !!!!");
      return true;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      try {
         ;
      } catch (Exception e) {
         throw e;
      }

      return nFile;
   }

   protected NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception {
      return null;
   }
   
   protected void insertNewMediaToDB(Media media) throws Exception {
      storage.insertNewMediaToDB(media);
   }
   
   private void makeMediaPath(String path) throws Exception {
      try {
         File dir = new File(path);
         dir.mkdirs();
         
         if (!dir.exists()) {
            throw new Exception("can not create new media directory - " + path);
         }
      } catch (Exception e) {
         logger.log(LoggerIF.LOG_ERROR, "fail to create media path - " + e.getMessage());         
         throw e;
      }
   }
}
