package com.sds.acube.ndisc.mts.process.impl;

import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcFileDel extends DefProcessAdaptor {
   public ProcFileDel(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcFileDel.process() started !!!!");
      boolean bRet = false;
      String strMsg = null;
      
      SocketChannel sc = (SocketChannel) obj1;      
      JobVO job = (JobVO) obj2;
      NFile[] nFile = null;

      try {
         receiveStatReady(sc); 
         nFile = (NFile[])job.getObject();

         nFile = getBasicNFileInfo(nFile);
         nFile = getStorageInfo(nFile, NDConstant.STORAGE_PATH_ACCESS);

         deleteNFile(nFile);
         deleteNFileFromDB(nFile);
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
      logger.log(LoggerIF.LOG_DEBUG, "ProcFileDel.process() ended !!!!");
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
      return storage.aquireStorageInfo(nFile, option);
   }
}
