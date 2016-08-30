package com.sds.acube.ndisc.mts.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.dao.config.DaoConfig;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.process.iface.ProcessIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;
import com.sds.acube.ndisc.mts.vo.JobVO;
import com.sds.acube.ndisc.mts.util.cipher.jce.LicenseChecker;

public class NDiscServer extends NDCommon {

   private void initLogger() throws Exception {
      try {
         if (null == logger) {
            logger = (LoggerIF) DynamicClassLoader.createInstance(MTS_LOGGER);
         }
         logger.initLogger();
      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initLogger()", e);
         throw e;
      }
   }

   private void initDAO() throws Exception {
      try {
         daoManager = DaoConfig.getDaomanager();
      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initDAO()", e);
         throw e;
      }
   }

   private void initServer() throws NetworkException {
      try {
    	  logger.info("initServer()");
         // open selector
         selector = Selector.open();

         // create server socket channel
         serverSocketChannel = ServerSocketChannel.open();
         // set non-blocking mode for ndisc server
         serverSocketChannel.configureBlocking(false);
         // get server socket channel
         serverSocket = serverSocketChannel.socket();

         // bind
         InetSocketAddress isa = new InetSocketAddress(HOST, PORT);
         serverSocket.bind(isa);

         // register to selector
         serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
      } catch (IOException e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initServer()", e);
         throw new NetworkException();
      }
   }

   private void initFilter() throws Exception {
      try {
    	  logger.info("initFilter()");
         filterEnc = (FilterIF) DynamicClassLoader.createInstance(FILTER_ENC);
         filterComp = (FilterIF) DynamicClassLoader.createInstance(FILTER_COMP);

      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initFilter()", e);
         throw e;
      }
   }

   private void initStorage() throws Exception {
      try {
    	  logger.info("initStorage()");
    	  storage = (StorageIF) DynamicClassLoader.createInstance(MTS_STORAGE, logger);
      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initStorage()", e);
         throw e;
      }
   }

   private void initProcess() throws Exception {
      try {
    	  logger.info("initProcess()");
    	  // INIT
         procInit = (ProcessIF) DynamicClassLoader.createInstance(PROC_INIT, logger);
         // FILE REG
         procFileReg = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEREG, logger, storage);
         // FILE GET
         procFileGet = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEGET, logger, storage);
         // FILE REP
         procFileRep = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEREP, logger, storage);
         // FILE DEL
         procFileDel = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEDEL, logger, storage);
         // FILE CPY
         procFileCpy = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILECPY, logger, storage);
         // FILE MOV
         procFileMov = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEMOV, logger, storage);
         // FILE INFO
         procFileInfo = (ProcessIF) DynamicClassLoader.createInstance(PROC_FILEINFO, logger, storage);
         // VOL INFO
         procVolInfo = (ProcessIF) DynamicClassLoader.createInstance(PROC_VOLINFO, logger, storage);
         // MAKE VOLUME
         procMkVolume = (ProcessIF) DynamicClassLoader.createInstance(PROC_MKVOLUME, logger, storage);
         // MAKE MEDIA
         procMkMedia = (ProcessIF) DynamicClassLoader.createInstance(PROC_MKMEDIA, logger, storage);
         // GET CONF
         procGetConf = (ProcessIF) DynamicClassLoader.createInstance(PROC_GETCONF, logger, storage);

      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, "Fail - NDiscServer.initProcess()", e);
         throw e;
      }
   }

   private void startServer() {
      logger.info("ACUBE DM Configuration has loaded");
      logger.info("\n" + configuration.toString());
      logger.info("ACUBE DM(" + NDCommon.VERSION + ") is succefully started [BINDING PORT : " + PORT + "]");
      try {
    	 LicenseChecker licenseChecker = new LicenseChecker();
    	 String licenseKey = NDCommon.LICENSE_KEY;
    	 boolean isValidLicense = false;
    	 
    	 if(licenseChecker.IsValidLicense(licenseKey)) {
    		 isValidLicense = true;
    		 logger.info("License key is valid!");
    	 }
    	 else {
    		 logger.info("License key is invalid!");
    	 }
    			 
         while (true) {
            // check event
            selector.select();

            // check SelectedSet, then process event
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
               SelectionKey key = (SelectionKey) it.next();
               if (key.isAcceptable()) {
                  // client connected
                  accept(key);
               } else if (key.isReadable()) {
                  // process for read event
            	   
            	  if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION){
            		  invokeProcess(key);
            		  
            		  if(isValidLicense == false) {
            			  logger.info("The # of remaining file operation is " + (NDConstant.MAX_FILE_OPERATION - NDCommon.CURRENT_FILE_OPERATION_CNT));
            		  }
            			  
            	  }else{            		  
            		  if(licenseChecker.IsValidLicense(licenseKey)){
            			  invokeProcess(key);            			  
            		  }else{            		  
            			  logger.log(LoggerIF.LOG_WARNING, "NDiscServer needs valid license key, You need to get valid license to use NDiscServer...");
            			  return;
            		  }           		  
            	  }
               }

               // remove event
               it.remove();
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         logger.log(LoggerIF.LOG_WARNING, "NDiscServer.startServer()", e);
      }
   }

   private void accept(SelectionKey key) throws NetworkException {
      ServerSocketChannel server = (ServerSocketChannel) key.channel();
      SocketChannel sc;
      try {
         sc = server.accept();
         // registerChannel(selector, sc, SelectionKey.OP_READ |
         // SelectionKey.OP_WRITE);
         registerChannel(selector, sc, SelectionKey.OP_READ);

         logger.info(sc.toString() + " Client is Connected.");
      } catch (ClosedChannelException e) {
         logger.log(LoggerIF.LOG_WARNING, "NDiscServer.accept()", e);
         throw new NetworkException();
      } catch (IOException e) {
         logger.log(LoggerIF.LOG_WARNING, "NDiscServer.accept()", e);
         throw new NetworkException();
      }
   }

   private void registerChannel(Selector selector, SocketChannel sc, int ops) throws NetworkException {

      try {
         if (null == sc) {
            logger.info("Invalid Connection");
            return;
         }
         sc.configureBlocking(false);
         sc.register(selector, ops);

         // add client
         JobVO job = new JobVO();
         job.setServiceStat(NDCommon.SERVICE_STAT_INIT);
         room.put(sc, job);
      } catch (ClosedChannelException e) {
         logger.log(LoggerIF.LOG_WARNING, "NDiscServer.registerChannel()", e);
         throw new NetworkException();
      } catch (IOException e) {
         logger.log(LoggerIF.LOG_WARNING, "NDiscServer.registerChannel()", e);
         throw new NetworkException();
      }
   }

   private void invokeProcess(SelectionKey key) throws Exception {
      boolean bRet = false;
      SocketChannel sc = null;
      JobVO job = null;
      String strServiceStat = null;

      try {
         sc = (SocketChannel) key.channel();
         job = (JobVO) room.get(sc);

         strServiceStat = job.getServiceStat();

         if (NDConstant.SERVICE_STAT_INIT.equals(strServiceStat)) {
            bRet = procInit.process(sc, job);
         } else if (NDConstant.SERVICE_STAT_FILEREG.equals(strServiceStat)) {
            bRet = procFileReg.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILEGET.equals(strServiceStat)) {
            bRet = procFileGet.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILEREP.equals(strServiceStat)) {
            bRet = procFileRep.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILEDEL.equals(strServiceStat)) {
            bRet = procFileDel.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILECPY.equals(strServiceStat)) {
            bRet = procFileCpy.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILEMOV.equals(strServiceStat)) {
            bRet = procFileMov.process(sc, job);
            if(NDCommon.CURRENT_FILE_OPERATION_CNT < NDConstant.MAX_FILE_OPERATION)
            	NDCommon.CURRENT_FILE_OPERATION_CNT++;
         } else if (NDConstant.SERVICE_STAT_FILEINFO.equals(strServiceStat)) {
            bRet = procFileInfo.process(sc, job);
         } else if (NDConstant.SERVICE_STAT_VOLINFO.equals(strServiceStat)) {
            bRet = procVolInfo.process(sc, job);
         } else if (NDConstant.SERVICE_STAT_MKVOLUME.equals(strServiceStat)) {
            bRet = procMkVolume.process(sc, job);
         } else if (NDConstant.SERVICE_STAT_MKMEDIA.equals(strServiceStat)) {
            bRet = procMkMedia.process(sc, job);
         } else if (NDConstant.SERVICE_STAT_GETCONF.equals(strServiceStat)) {
            bRet = procGetConf.process(sc, job);
         } else {
            logger.info("Invalid Service");
         }
      } catch (Exception e) {
         bRet = false;
         e.printStackTrace();
         logger.log(LoggerIF.LOG_SEVERE, sc.toString() + " : Fail - process");
      } finally {
         try {
            if (false == bRet) {
               room.remove(sc);
               sc.close();
               sc = null;
               job = null;
            }
         } catch (Exception exp) {
            exp.printStackTrace();
         }
      }
   }

   // ///////////////////////// Main ////////////////////////////
   public static void main(String[] args) {
      NDiscServer scs = new NDiscServer();
      try {
         scs.initLogger();
         scs.initDAO();
         scs.initFilter();
         scs.initStorage();
         scs.initProcess();
         scs.initServer();
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }

      try {
         scs.startServer();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
