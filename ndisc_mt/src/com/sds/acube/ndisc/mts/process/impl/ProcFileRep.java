package com.sds.acube.ndisc.mts.process.impl;

import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcFileRep extends DefProcessAdaptor {
   public ProcFileRep(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcFileRep.process() started !!!!");
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
      }

      if (true == bRet) {
         bRet = false;
         try {
            receiveFileExNIO(sc, nFile);
            filterNFile(nFile, true);
            copyNFile(nFile, true, false);
            updateNFileToDB(nFile);
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
         }
      }

      setInitStatus(sc, job);
      logger.log(LoggerIF.LOG_DEBUG, "ProcFileRep.process() ended !!!!");
      return true;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      try {
         for (int i = 0; i < nFile.length; i++) {
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
