package com.sds.acube.ndisc.mts.process.impl;

import java.nio.channels.SocketChannel;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcFileInfo extends DefProcessAdaptor { 
   public ProcFileInfo(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }
   
   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcFileInfo.process() started !!!!");
      boolean bRet = false;
      String strMsg = null;
      
      SocketChannel sc = (SocketChannel) obj1;      
      JobVO job = (JobVO) obj2;
      NFile[] nFile = null;

      try {
         receiveStatReady(sc); 
         nFile = (NFile[])job.getObject();

         nFile = getBasicNFileInfo(nFile);
         nFile = getStorageInfo(nFile, null);
         nFile = getNFileInfo(nFile);
         bRet = true;
      } catch (Exception e) {
         strMsg = e.getMessage();
         bRet = false;
      } finally {
         if (true == bRet) {
            strMsg = makeReturnNFileInfoMsg(nFile);
            strMsg = NDConstant.NO_ERROR + NDConstant.DELIM_STR + strMsg;
         } else {
            strMsg = NDConstant.ERROR + NDConstant.DELIM_STR + strMsg;
         }

         sendReplyMsg(sc, strMsg);
         setInitStatus(sc, job);         
      }
      logger.log(LoggerIF.LOG_DEBUG, "ProcFileInfo.process() ended !!!!");
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
      try {
         ;
      } catch (Exception e) {
         throw e;
      }

      return nFile;
   }
   
   private NFile[] getNFileInfo(NFile[] nFile) throws Exception {
      NFile[] retNFile = null;
      
      try {
         retNFile = new NFile[nFile.length];
         
         for (int i = 0; i < nFile.length; i++) {
            retNFile[i] = selectNFileFromDB(nFile[i].getId());
            retNFile[i].setStoragePath(storage.getFileMediaPath(nFile[i].getId(), retNFile[i].getCreatedDate()));
         }
      
      } catch (Exception e) {
         throw e;
      }
      
      
      return retNFile;
   }
   
   private String makeReturnNFileInfoMsg(NFile[] nFile) {
      StringBuffer strBuf = new StringBuffer();
      
      for (int i = 0; i < nFile.length; i++) {
         strBuf.append(nFile[i].getId());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getName());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getSize());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getCreatedDate());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getModifiedDate());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getMediaId());
         strBuf.append(NDConstant.DELIM_STR);
         strBuf.append(nFile[i].getStatType());
         strBuf.append(NDConstant.DELIM_STR);         
         strBuf.append(nFile[i].getStoragePath());
         strBuf.append(NDConstant.DELIM_STR);   
      }
      return strBuf.toString();
   }
}
