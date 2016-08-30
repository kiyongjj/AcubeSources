package com.sds.acube.ndisc.migration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sds.acube.jstor.JSTORApi;
import com.sds.acube.jstor.JSTORApiFactory;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.napi.NApi;

public class MigCommon {
   static Logger log = null;

   // tmp-dir info
   String tmpDir = null;

   // stor server api type
   String storApiType = null;

   String storSvrType = null;

   // stor server info
   String storHost = null;

   String storPort = null;

   String storTargetMedia = null;

   // stor jdbc info
   String jdbcDrv = null;

   String jdbcUrl = null;

   String jdbcUser = null;

   String jdbcPswd = null;

   // ndisc server info
   String ndiscHost = null;

   String ndiscPort = null;

   String ndiscTargetVolume = null;

   String ndiscStatus = null;

   // stor operation value
   JSTORApi jSTOR = null;

   // jdbc operation value
   Connection jdbcConn = null;

   Statement jdbcStmt = null;

   // ndisc operation value
   NApi NDApi = null;
   
   public MigCommon() {
      try {
         // init log4j logger
         log = Logger.getLogger("com.sds.acube.ndisc.migration");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected void setConfiguration(String configFile) throws Exception {
      Configuration config = null;

      try {
         config = ConfigurationManager.getConfiguration(configFile);

         tmpDir = config.getProperty("tmp-dir", null);

         storApiType = config.getProperty("type", null, "stor-api");
         storSvrType = config.getProperty("server", null, "stor-api");

         storHost = config.getProperty("host", null, "stor-server");
         storPort = config.getProperty("port", null, "stor-server");
         storTargetMedia = config.getProperty("target-media", null, "stor-server");

         jdbcDrv = config.getProperty("driver", null, "stor-jdbc");
         jdbcUrl = config.getProperty("url", null, "stor-jdbc");
         jdbcUser = config.getProperty("user", null, "stor-jdbc");
         jdbcPswd = config.getProperty("password", null, "stor-jdbc");

         ndiscHost = config.getProperty("host", null, "ndisc-server");
         ndiscPort = config.getProperty("port", null, "ndisc-server");
         ndiscTargetVolume = config.getProperty("target-volume", null, "ndisc-server");
         ndiscStatus = config.getProperty("status", null, "ndisc-server");

         log.info("migration configuration is loaded");
      } catch (Exception e) {
         throw e;
      } finally {
         config = null;
      }
   }

   protected void createJdbcConnection() throws Exception {
      try {
         Class.forName(jdbcDrv);
         jdbcConn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPswd);
         jdbcStmt = jdbcConn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      } catch (Exception e) {
         throw e;
      } finally {
         closeJdbcConnection();
      }
   }

   protected void closeJdbcConnection() {
      try {
         if (null != jdbcStmt) {
            jdbcStmt.close();
            jdbcStmt = null;
         }
         if (null != jdbcConn) {
            jdbcConn.close();
            jdbcConn = null;
         }

      } catch (Exception e) {
         log.warn(e.getMessage(), e);
         e.printStackTrace();
      }
   }

   protected ResultSet getTargetFileList() throws Exception {
      ResultSet rs = null;
      String query = null;
      try {
         query = "select fle_id, fle_name, fle_status, fle_crtdt " + 
                 "from file_tbl " + 
                 "where fle_mdid = " + Integer.parseInt(storTargetMedia) + " " +
                 "and fle_id not in (select mig_fleid from stor2ndisc_tbl)";
                 
         log.debug("getTargetFileList query = " + query);

         rs = jdbcStmt.executeQuery(query);

      } catch (Exception e) {
         rs.close();
         closeJdbcConnection();
      }

      return rs;
   }

   protected void insertMigFileLog(String fileID) {
      String query = null;
      
      try {
         query = "insert into stor2ndisc_tbl values('" + fileID + "')";
         jdbcStmt.execute(query);
      } catch (Exception e) {
         log.warn(e.getMessage(), e);         
         e.printStackTrace();
      }
   }
   
   protected int createStorConnection() throws Exception {
      int storConnID = -1;

      try {
         System.setProperty("jstor_api_type", storApiType);
         System.setProperty("jstor_svr_type", storSvrType);

         JSTORApiFactory jsFactory = new JSTORApiFactory();
         jSTOR = jsFactory.getInstance();

         storConnID = jSTOR.JSTOR_Connect(storHost, Integer.parseInt(storPort));
         if (storConnID < 0) {
            throw new Exception("stor connection fail - " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
         }
      } catch (Exception e) {
         throw e;
      }

      return storConnID;
   }
   
   protected int createNDiscConnection() throws Exception {
      int ndiscConnID = -1;

      try {
         NDApi = new NApi(false);
         ndiscConnID = NDApi.NDisc_Connect(ndiscHost, Integer.parseInt(ndiscPort));
         if (ndiscConnID < 0) {
            throw new Exception("can not connect to ndisc server");
         }
      } catch (Exception e) {
         throw e;
      }

      return ndiscConnID;
   }   

   protected void closetStorConnection(int storConnID) throws Exception {
      if (storConnID > 0) {
         jSTOR.JSTOR_Disconnect(storConnID);
      }
   }
   
   protected void closetNDiscConnection() throws Exception {
         NDApi.NDisc_Disconnect();
   }   

   protected void getStorFile(int storConnID, String fileID, String fileName) throws Exception {
      int result = -1;
      String[][] fileGetInfo = null;

      try {
         fileGetInfo = new String[1][3];

         fileGetInfo[0][0] = fileID;
         fileGetInfo[0][1] = getFilePath(fileName);
         fileGetInfo[0][2] = "-1"; // auto reverse filter id

         result = jSTOR.JSTOR_FileGet(storConnID, 1, fileGetInfo, 0);
         if (result < 0) {
            throw new Exception("stor fileget fail - " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
         }
      } catch (Exception e) {
         throw e;
      }
   }
   
   protected void regNDiscFile(String fileID, String fileName, String fileStatus, String fileCrtDate) throws Exception {
      NFile[] nFile = null;
      String[] result = null;
      
      try {
         nFile = new NFile[1];
         nFile[0].setId(fileID);
         nFile[0].setName(getFilePath(fileName));
         nFile[0].setCreatedDate(fileCrtDate);
        
         if ("R".equalsIgnoreCase(ndiscStatus)) {
            nFile[0].setStatType(fileStatus);
         } else {
            nFile[0].setStatType(ndiscStatus);
         }
         
         result = NDApi.NDISC_FileReg(nFile);
         if (null == result) {
            throw new Exception("ndisc filereg fail - " + fileID);
         }
      } catch (Exception e) {
         throw e;
      }
   }
   
   protected void deleteTmpFile(String fileName) {
      new File(getFilePath(fileName)).delete();
   }
   
   private String getFilePath(String fileName) {
      return tmpDir + File.separator + fileName;
   }
   
}