package com.sds.acube.ndisc.mts.process.impl;

import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcFileGet extends DefProcessAdaptor {
   public ProcFileGet(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcFileGet.process() started !!!!");
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

         copyNFile(nFile, false, false);
         filterNFile(nFile, false);
         
         sendReplyMsg(sc, NDConstant.NO_ERROR + NDConstant.DELIM_STR);
         
         sendFileInfo(sc, nFile);
         sendFileExNIO(sc, nFile);
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
      logger.log(LoggerIF.LOG_DEBUG, "ProcFileGet.process() ended !!!!");
      return true;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      try {
         for (int i = 0; i < nFile.length; i++) {
            if (NDConstant.STAT_AUTO.equals(nFile[i].getStatType())) {
               nFile[i].setStatType(selectNFileFromDB(nFile[i].getId()).getStatType());
            }

            if (!NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               nFile[i].setTmpPath(getTmpPath(nFile[i]));
            }
         }
      } catch (Exception e) {
         throw e;
      }

      return nFile;
   }

   protected NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception {
      return storage.aquireStorageInfo(nFile, option);
   }

}