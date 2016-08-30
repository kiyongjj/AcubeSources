package com.sds.acube.ndisc.mts.process.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Properties;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;

public class ProcGetConf extends DefProcessAdaptor {
   public ProcGetConf(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }

   public boolean process(Object obj1, Object obj2) {
	  logger.log(LoggerIF.LOG_DEBUG, "ProcGetConf.process() started !!!!");
      boolean bRet = false;
      String strMsg = null;

      SocketChannel sc = (SocketChannel) obj1;
      JobVO job = (JobVO) obj2;

      try {
         receiveStatReady(sc);

         sendReplyMsg(sc, NDConstant.NO_ERROR + NDConstant.DELIM_STR);

         sendConfiguration(sc);

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
      
      logger.log(LoggerIF.LOG_DEBUG, "ProcGetConf.process() ended !!!!");
      return true;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      return null;
   }

   protected NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception {
      return null;
   }

   private void sendConfiguration(SocketChannel sc) throws Exception {
      ObjectOutputStream out = null;
      FileOutputStream ostream = null;
      HashMap hash = null;
      Properties prop = null;
      String file = NDCommon.NDISC_TMP_DIR + File.separator + "config-object.tmp";
      
      try {
         hash = new HashMap();
         prop = new Properties();
         
         String[] categoryNames = NDCommon.configuration.getCategoryNames();
         for (int i = 0; i < categoryNames.length; i++) {
            prop = NDCommon.configuration.getProperties(categoryNames[i]);
            hash.put(categoryNames[i], prop);
         }
        
         ostream = new FileOutputStream(file);
         out = new ObjectOutputStream(ostream);
         out.writeObject(hash);
         
         sendObjectFileSize(sc, file);
         sendObjectFile(sc, file);

      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      } finally {
         if (null != out) {
            out.reset();
            out.close();
         }
         
         new File(file).delete();
         prop = null;
         hash = null;
      }
   }

   private boolean sendObjectFileSize(SocketChannel sc, String file) {
      boolean bRet = false;
      ByteBuffer buffer = null;
      StringBuffer strbuf = null;
      String msg = null;

      try {
         strbuf = new StringBuffer();

         strbuf.append((int) new File(file).length());
         strbuf.append(NDConstant.DELIM_STR);

         msg = getFormatString(strbuf.toString(), NDConstant.REPLY_BUFFER_SIZE);
         buffer = ByteBuffer.allocateDirect(NDConstant.REPLY_BUFFER_SIZE);
         buffer.put(msg.getBytes());

         buffer.flip();

         while (buffer.hasRemaining()) {
            sc.write(buffer);
         }
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         deleteBuffer(buffer);
      }

      return bRet;
   }

   private boolean sendObjectFile(SocketChannel sc, String file) {
      boolean bRet = false;
      FileChannel inChannel = null;

      try {
         inChannel = new FileInputStream(file).getChannel();

         int transfer = 0;
         while (transfer < inChannel.size()) {
            transfer += inChannel.transferTo(transfer, inChannel.size() - transfer, sc);
         }

         inChannel.close();
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         ;
      }

      return bRet;
   }

   // private void sendConfiguration(SocketChannel sc) throws Exception {
   // ObjectOutputStream out = null;
   //
   // try {
   //         
   // out = new ObjectOutputStream(sc.socket().getOutputStream());
   // out.writeObject(NDCommon.configuration);
   // } catch (Exception e) {
   // e.printStackTrace();
   // throw e;
   // } finally {
   // if (null != out) {
   // out.reset();
   // out.close();
   // }
   // }
   // }
}