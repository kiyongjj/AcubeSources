package com.sds.acube.ndisc.mts.process.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.process.iface.ProcessIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.vo.JobVO;
import com.sds.acube.ndisc.util.RandomGUID;


public abstract class DefProcessAdaptor implements ProcessIF {
   public LoggerIF  logger     = null;

   public StorageIF storage    = null;

   private FilterIF filterEnc  = null;

   private FilterIF filterComp = null;

   public DefProcessAdaptor() {
      ;
   }

   public DefProcessAdaptor(LoggerIF logger) {
      this.logger = logger;
   }

   public DefProcessAdaptor(LoggerIF logger, StorageIF storage) {
      this.logger = logger;
      this.storage = storage;

      // two filters will be used here
      this.filterEnc = NDCommon.filterEnc;
      this.filterComp = NDCommon.filterComp;
   }

   public abstract boolean process(Object obj1, Object obj2);

   protected abstract NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception;

   protected abstract NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception;

   protected void deleteBuffer(ByteBuffer buffer) {
      if (buffer != null) {
         buffer.clear();
         buffer = null;
      }
   }

   protected void addClient(SocketChannel sc, JobVO job) {
      NDCommon.room.put(sc, job);
   }

   protected void removeClient(SocketChannel sc) {
      NDCommon.room.remove(sc);
   }

   protected void modifyClient(SocketChannel sc, JobVO job) {
      removeClient(sc);
      addClient(sc, job);
   }

   protected void filterNFile(NFile[] nFile, boolean bForward) throws Exception {
      try {
         for (int i = 0; i < nFile.length; i++) {
            if (NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               logger.log(LoggerIF.LOG_DEBUG, "statType(filter) none");
               continue;
            } else {
               logger.log(LoggerIF.LOG_DEBUG, "statType(filter) - " + nFile[i].getStatType());
               convertFile(nFile[i].getStatType(), nFile[i].getTmpPath(), bForward);
            }
         }
      } catch (Exception e) {
         throw e;
      }
   }

   protected void convertFile(String statType, String strFilePath, boolean bForward) throws FileException {
      try {
         if (NDConstant.STAT_ENC.equals(statType)) {
            if (true == bForward) {
               filterEnc.filterFileForward(strFilePath);
            } else {
               filterEnc.filterFileReverse(strFilePath);
            }
         } else if (NDConstant.STAT_COMP.equals(statType)) {
            if (true == bForward) {
               filterComp.filterFileForward(strFilePath);
            } else {
               filterComp.filterFileReverse(strFilePath);
            }
         } else if (NDConstant.STAT_COMP_ENC.equals(statType)) {
            if (true == bForward) {
               filterComp.filterFileForward(strFilePath);
               filterEnc.filterFileForward(strFilePath);
            } else {
               filterEnc.filterFileReverse(strFilePath);
               filterComp.filterFileReverse(strFilePath);
            }
         } else {
            ;
         }
      } catch (Exception e) {
         logger.log(LoggerIF.LOG_ERROR, e.getMessage(), e);
         throw new FileException(e.getMessage());
      }
   }

   protected void copyNFile(NFile[] nFile, boolean bForwardMedia, boolean bForce) throws Exception {
      FileChannel srcChannel = null;
      FileChannel dstChannel = null;
      String srcFile = null;
      String dstFile = null;

      try {
         for (int i = 0; i < nFile.length; i++) {
            if (false == bForce && NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               continue;
            } else {
               if (bForwardMedia) {
                  srcFile = nFile[i].getTmpPath();
                  dstFile = nFile[i].getStoragePath();
               } else {
                  srcFile = nFile[i].getStoragePath();
                  dstFile = nFile[i].getTmpPath();
               }

               srcChannel = new FileInputStream(srcFile).getChannel();
               dstChannel = new FileOutputStream(dstFile).getChannel();
               dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

               if (bForwardMedia) {
                  new File(srcFile).delete();
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      } finally {
         if (null != srcChannel && srcChannel.isOpen()) {
            srcChannel.close();
         }
         if (null != dstChannel && dstChannel.isOpen()) {
            dstChannel.close();
         }
      }
   }

   protected void deleteNFile(NFile[] nFile) throws Exception {
      File file = null;

      try {
         for (int i = 0; i < nFile.length; i++) {
            file = new File(nFile[i].getStoragePath());
            boolean bRet = file.delete();

            logger.log(LoggerIF.LOG_DEBUG, "media file [" + nFile[i].getStoragePath() + "] deleted : " + bRet);
         }
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      } finally {
         ;
      }
   }

   // no use currently
   protected void receiveFile(SocketChannel sc, NFile[] nFile) throws Exception {
      ByteBuffer fileBuffer = null;
      FileChannel fc = null;

      String statType = null;
      String strFilePath = null;

      try {
         for (int i = 0; i < nFile.length; i++) {
            statType = nFile[i].getStatType();
            strFilePath = null;
            if (NDConstant.STAT_NONE.equals(statType)) {
               strFilePath = nFile[i].getStoragePath();
            } else {
               strFilePath = nFile[i].getTmpPath();
            }

            fc = (new FileOutputStream(new File(strFilePath), false).getChannel());

            int nRemain = nFile[i].getSize();
            ;
            int nAmount = NDConstant.FILE_TRANS_BUFFER_SIZE;
            fileBuffer = ByteBuffer.allocateDirect(nAmount);

            while (nRemain > 0) {
               if (nRemain < NDConstant.FILE_TRANS_BUFFER_SIZE) {
                  nAmount = nRemain;
                  fileBuffer = ByteBuffer.allocateDirect(nAmount);
               }

               // completely read
               while (fileBuffer.position() < nAmount) {
                  sc.read(fileBuffer);
               }

               fileBuffer.flip();

               // completely write section
               while (fileBuffer.hasRemaining()) {
                  fc.write(fileBuffer);
               }

               nRemain -= nAmount;
               fileBuffer.clear();
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   // no use currently
   protected void receiveFileEx(SocketChannel sc, NFile[] nFile) throws Exception {
      ByteBuffer fileBuffer = null;
      FileChannel fc = null;

      String statType = null;
      String strFilePath = null;

      try {
         for (int i = 0; i < nFile.length; i++) {
            statType = nFile[i].getStatType();
            strFilePath = null;
            if (NDConstant.STAT_NONE.equals(statType)) {
               strFilePath = nFile[i].getStoragePath();
            } else {
               strFilePath = nFile[i].getTmpPath();
            }

            fc = (new FileOutputStream(new File(strFilePath), false).getChannel());

            int size = nFile[i].getSize();
            fileBuffer = ByteBuffer.allocateDirect(size);

            // completely read
            while (fileBuffer.position() < size) {
               sc.read(fileBuffer);
            }

            fileBuffer.flip();

            // completely write section
            while (fileBuffer.hasRemaining()) {
               fc.write(fileBuffer);
            }

            fileBuffer.clear();
            fc.close();
         }

      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   // using nio transfer... function
   protected void receiveFileExNIO(SocketChannel sc, NFile[] nFile)
   throws Exception
   {
	    FileChannel fc = null;
    
        String statType = null;
        String strFilePath = null;
    
        this.logger.info("entered receiveFileExNIO");
        try
        {
          for (int i = 0; i < nFile.length; ++i) {
            statType = nFile[i].getStatType();
            strFilePath = null;
            if ("0".equals(statType))
              strFilePath = nFile[i].getStoragePath();
            else {
              strFilePath = nFile[i].getTmpPath();
            }
    
            fc = new FileOutputStream(strFilePath).getChannel();
    
            if ("Y".equalsIgnoreCase(NDCommon.SO_TRANS_TUNE_APPLY)) {
              this.logger.info("SO_TRANS_TUNE_APPLY - Enabled");
              int cnt = 0;
              int trans = 0; int transA = 0;
              while (trans < nFile[i].getSize()) {
                if (trans == transA) {
                  ++cnt;
                } else {
                  transA = trans;
                  cnt = 0;
                }
    
                if (cnt > NDCommon.SO_TRANS_CHECK_ITER) {
                  throw new Exception(
                    "Abnormal client socket disconnect  - No Response");
               }
    
                Thread.sleep(NDCommon.SO_TRANS_SLEEP);
    
                trans = (int)(trans + fc
                  .transferFrom(sc, trans, nFile[i].getSize() - 
                  trans));
              }
            } else {
              this.logger.info("SO_TRANS_TUNE_APPLY - Disabled");
              int trans = 0;
              while (trans < nFile[i].getSize())
              {
                trans = (int)(trans + fc
                  .transferFrom(sc, trans, nFile[i].getSize() - 
                  trans));
              }
            }
            fc.close();
          }
        }
        catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }

   protected boolean sendFileInfo(SocketChannel sc, NFile[] nFile) {
      boolean bRet = false;
      ByteBuffer buffer = null;
      StringBuffer strbuf = null;
      String msg = null;

      try {
         strbuf = new StringBuffer();

         for (int i = 0; i < nFile.length; i++) {

            if (NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               nFile[i].setSize((int)new File(nFile[i].getStoragePath()).length());
            } else {
               nFile[i].setSize((int)new File(nFile[i].getTmpPath()).length());
            }

            strbuf.append(nFile[i].getSize());
            strbuf.append(NDConstant.DELIM_STR);
         }

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

   // no use currently
   protected boolean sendFile(SocketChannel sc, NFile[] nFile) {
      boolean bRet = false;
      ByteBuffer buffer = null;
      FileChannel inChannel = null;
      String inFile = null;

      try {
         for (int i = 0; i < nFile.length; i++) {

            if (NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               inFile = nFile[i].getStoragePath();
            } else {
               inFile = nFile[i].getTmpPath();
            }

            inChannel = new FileInputStream(inFile).getChannel();

            int nRemain = nFile[i].getSize();
            int nAmount = NDConstant.FILE_TRANS_BUFFER_SIZE;
            buffer = ByteBuffer.allocateDirect(nAmount);

            while (nRemain > 0) {
               if (nRemain < NDConstant.FILE_TRANS_BUFFER_SIZE) {
                  nAmount = nRemain;
                  buffer = ByteBuffer.allocateDirect(nAmount);
               }

               // completely read section
               while (buffer.position() < nAmount) {
                  inChannel.read(buffer);
               }

               buffer.flip();

               // completely write section
               while (buffer.hasRemaining()) {
                  sc.write(buffer);
               }

               nRemain -= nAmount;
               buffer.clear();
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         deleteBuffer(buffer);
      }

      return bRet;
   }

   // no use currently
   protected boolean sendFileEx(SocketChannel sc, NFile[] nFile) {
      boolean bRet = false;
      ByteBuffer buffer = null;
      FileChannel inChannel = null;
      String inFile = null;

      try {
         for (int i = 0; i < nFile.length; i++) {

            if (NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               inFile = nFile[i].getStoragePath();
            } else {
               inFile = nFile[i].getTmpPath();
            }

            inChannel = new FileInputStream(inFile).getChannel();

            int size = nFile[i].getSize();
            buffer = ByteBuffer.allocateDirect(size);

            // completely read section
            while (buffer.position() < size) {
               inChannel.read(buffer);
            }

            buffer.flip();

            // completely write section
            while (buffer.hasRemaining()) {
               sc.write(buffer);
            }

            buffer.clear();
            inChannel.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         deleteBuffer(buffer);
      }

      return bRet;
   }

   // using nio transfer... function
   protected boolean sendFileExNIO(SocketChannel sc, NFile[] nFile) {
      boolean bRet = false;
      FileChannel inChannel = null;
      String inFile = null;

      try {
         for (int i = 0; i < nFile.length; i++) {

            if (NDConstant.STAT_NONE.equals(nFile[i].getStatType())) {
               inFile = nFile[i].getStoragePath();
            } else {
               inFile = nFile[i].getTmpPath();
            }

            inChannel = new FileInputStream(inFile).getChannel();

            int transfer = 0;
            while (transfer < inChannel.size()) {
               transfer += inChannel.transferTo(transfer, inChannel.size() - transfer, sc);
            }

            inChannel.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         ;
      }

      return bRet;
   }

   protected boolean sendReadyMsg(SocketChannel sc) {
      boolean bRet = false;
      ByteBuffer buffer = null;

      try {
         buffer = ByteBuffer.allocateDirect(NDConstant.STAT_BUFFER_SIZE);

         buffer.put(NDConstant.SERVICE_STAT_READY.getBytes());

         buffer.flip();

         sc.write(buffer);

         bRet = true;
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         deleteBuffer(buffer);
      }

      return bRet;
   }

   // Message Reply
   protected boolean sendReplyMsg(SocketChannel sc, String strMsg) {
      boolean bRet = false;
      ByteBuffer buffer = null;
      StringBuffer strbuf = null;
      String msg = null;

      try {
         strbuf = new StringBuffer();
         strbuf.append(strMsg);
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
         buffer.clear();
      }

      return bRet;
   }

   protected String receiveStatReady(SocketChannel sc) {
      String ret = null;
      ByteBuffer buffer = null;

      try {
         buffer = ByteBuffer.allocateDirect(NDConstant.STAT_BUFFER_SIZE);
         sc.read(buffer);
         buffer.flip();

         ret = NDConstant.decoder.decode(buffer).toString();
      } catch (Exception e) {
         e.printStackTrace();
         ret = null;
      }

      return ret;
   }

   protected void setInitStatus(SocketChannel sc, JobVO job) {
      job.setServiceStat(NDConstant.SERVICE_STAT_INIT);
      modifyClient(sc, job);
   }

   protected String getFormatString(String data, int size) {
      String result = "";
      String tmp = data;
      int tmplen = tmp.length();

      for (int i = 0; i < (size - tmplen); i++) {
         result += "0";
      }

      result = tmp + result;

      return result;
   }

   protected String getTmpPath(NFile nFile) {
      RandomGUID guid = null;
      String strFileName = null;

      try {
         guid = new RandomGUID();

         if (null == nFile.getName() || 0 == nFile.getName().length()) {
            strFileName = guid.toString();
         } else {
            strFileName = guid.toString() + "." + getFileExt(nFile.getName());
         }
         strFileName = NDCommon.NDISC_TMP_DIR + File.separator + strFileName;
      } catch (Exception e) {
         strFileName = null;
      } finally {
         guid = null;
      }
      return strFileName;
   }

   protected String getFileExt(String strFileName) {
      String strExt = null;

      int nPos = strFileName.lastIndexOf(".");
      strExt = strFileName.substring(nPos + 1, strFileName.length());
      return strExt;
   }

   protected String getFileID(String strID) {
      String strRet = null;

      try {
         if (NDConstant.NDISC_NA_RESERV.equals(strID)) {
            strRet = getFileID();
         } else {
            strRet = strID;
         }
      } catch (Exception e) {
         e.printStackTrace();
         strRet = null;
      } finally {
         ;
      }

      return strRet;
   }

   protected String getFileID() {
      String strRet = null;
      RandomGUID guid = null;

      try {
         guid = new RandomGUID();
         strRet = guid.toString();
      } catch (Exception e) {
         e.printStackTrace();
         strRet = null;
      } finally {
         guid = null;
      }

      return strRet;
   }

   protected String getDate(String strDate) {
      String strRet = null;

      try {
         if (NDConstant.NDISC_NA_RESERV.equals(strDate)) {
            strRet = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
         } else {
            strRet = strDate;
         }
      } catch (Exception e) {
         e.printStackTrace();
         strRet = null;
      }

      return strRet;
   }

   protected String getFileMediaPath(String fileID, String createDate) throws Exception {
      return storage.getFileMediaPath(fileID, createDate);
   }

   protected NFile selectNFileFromDB(String fileID) throws Exception {
      return storage.selectNFileFromDB(fileID);
   }

   protected void updateNFileToDB(NFile[] nFile) throws Exception {
      storage.updateNFileToDB(nFile);
   }

   protected void registNFileToDB(NFile[] nFile) throws Exception {
      storage.registNFileToDB(nFile);
   }

   protected void deleteNFileFromDB(NFile[] nFile) throws Exception {
      storage.deleteNFileFromDB(nFile);
   }

   protected void insertNewVolumeToDB(Volume volume) throws Exception {
      storage.insertNewVolumeToDB(volume);
   }
}