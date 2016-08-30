package com.sds.acube.ndisc.migration;

import java.sql.ResultSet;

// Migration From STOR To NDISC
public class Migration extends MigCommon {

public static void main(String[] args) {
      Migration mig = null;
      ResultSet rs = null;
      int storConnID = -1;

      try {
         mig = new Migration();

         String configFile = args[1];
         if (null == args[1]) {
            throw new Exception("can not find configuration file");
         }
         
         // set configuration
         mig.setConfiguration(configFile);
         // create jdbc connection
         mig.createJdbcConnection();
         // connect to stor server
         storConnID = mig.createStorConnection();
         // connect to ndisc server
         mig.createNDiscConnection();

         String fileID = null;
         String fileName = null;
         String fileStatus = null;
         String fileCrtDate = null;
         rs = mig.getTargetFileList();
         while (rs.next()) {
            fileID = rs.getString(1);
            fileName = rs.getString(2);
            fileStatus = rs.getString(3);
            fileCrtDate = rs.getString(4);
            
            try {
               // step 1 : get file from stor server
               try {
                  mig.getStorFile(storConnID, fileID, fileName);
               } catch (Exception e1) {
                  log.error(e1.getMessage(), e1);
                  e1.printStackTrace();
                  continue;
               } 
               
               // step 2 : regist file to ndisc server
               try {
                  mig.regNDiscFile(fileID, fileName, fileStatus, fileCrtDate);
               } catch (Exception e2) {
                  log.error(e2.getMessage(), e2);
                  e2.printStackTrace();
                  continue;
               }
            } catch (Exception e0) {
               ;
            } finally {
               mig.deleteTmpFile(fileName);
            }
            
            mig.insertMigFileLog(fileID);
         }
      } catch (Exception e) {
         log.fatal(e.getMessage(), e);
         e.printStackTrace();
      } finally {
         try {
            mig.closeJdbcConnection();
            mig.closetStorConnection(storConnID);
            mig.closetNDiscConnection();
         } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);            
            ex.printStackTrace();
         }
      }
   }}
