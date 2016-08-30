package com.sds.acube.ndisc.mts.process.impl;

import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcMkVolume extends DefProcessAdaptor {
   public ProcMkVolume(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcMkVolume.process() started !!!!");
      boolean bRet = false;
      String strMsg = null;
      
      SocketChannel sc = (SocketChannel) obj1;      
      JobVO job = (JobVO) obj2;
      Volume volume = null;

      try {
         receiveStatReady(sc); 
         volume = (Volume)job.getObject();
         
         insertNewVolumeToDB(volume);
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
      logger.log(LoggerIF.LOG_DEBUG, "ProcMkVolume.process() ended !!!!");
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
   
   protected void insertNewVolumeToDB(Volume volume) throws Exception {
      storage.insertNewVolumeToDB(volume);
   }
}
