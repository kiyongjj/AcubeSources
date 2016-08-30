package com.sds.acube.ndisc.napi;

import junit.framework.TestCase;

import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.util.timer.TimeChecker;

public class NApiTest extends TestCase {

   public static void main(String[] args) {
      junit.textui.TestRunner.run(NApiTest.class);
   }

   public NApiTest(String arg0) {
      super(arg0);
   } 

   protected void setUp() throws Exception {
      super.setUp();
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /*
    * public void testMisc() throws Exception { String string =
    * "D:\\ACUBE\\acube31\\dm\\tmp\\6a65060a0000010a4a1fe5ea00000370.ppt%1D";
    * 
    * int pos = string.lastIndexOf("%"); if (pos > 0) { string =
    * string.substring(0, pos); }
    * 
    * System.out.println("STRING = " + string); }
    */

   // public void testMisc() throws Exception {
   // Configuration config =
   // ConfigurationManager.getConfiguration("Y:\\david\\NDISC_DEV\\config\\config.xml");
   //
   // // Cache Service 환경설정 변수
   // String CACHE_CATEGORY = "cache";
   // String CACHE_DIR = "cache_dir";
   // String CAPACITY = "capacity";
   // try {
   //
   // if (config == null) {
   // System.out.println("configuration is null");
   // }
   //
   // System.out.println("cache capacity = " + config.getIntProperty(CAPACITY,
   // 1000, CACHE_CATEGORY));
   // System.out.println("cache dir = " + config.getProperty(CACHE_DIR, null,
   // CACHE_CATEGORY));
   //
   // } catch (Exception e) {
   // e.printStackTrace();
   // }
   // }
   // public void testMisc() throws Exception {
   //
   // try {
   // // String aaa = NDCommon.configuration.toString();
   // String[] category = NDCommon.configuration.getCategoryNames();
   // for (int i = 0; i < category.length; i++) {
   // String[] property = NDCommon.configuration.getPropertyNames(category[i]);
   // for (int j = 0; j < property.length; j++) {
   //           
   // System.out.println(category[i] + " - " + property[j] + " : " +
   // NDCommon.configuration.getVariable(property[j]));
   // }
   // }
   //
   // } catch (Exception e) {
   // e.printStackTrace();
   // }
   // }
   // public void testMisc() throws Exception {
   //
   // try {
   // String[] category = NDCommon.configuration.getCategoryNames();
   //         
   // for (int i = 0; i < category.length; i++) {
   // Properties prop = NDCommon.configuration.getProperties(category[i]);
   //            
   // Enumeration enumeration = prop.propertyNames();
   // while (enumeration.hasMoreElements()) {
   // String name =(String)enumeration.nextElement();
   // System.out.println(category[i] + " - " + name + " : " +
   // prop.getProperty(name));
   // }
   //
   // }
   //
   // } catch (Exception e) {
   // e.printStackTrace();
   // }
   // }
   
   /*
    * public void testFileGet() throws Exception { String HOST = "70.7.105.140";
    * int PORT = 7404; int nNumOfFiles = 1; NFile[] nFile = new
    * NFile[nNumOfFiles]; boolean bRet = false; int connID = -1;
    * 
    * nFile[0] = new NFile();
    * nFile[0].setId("f36d9e414f29d1fde1c067702eb0c8ce");
    * nFile[0].setStatType(NDConstant.STAT_AUTO);
    * nFile[0].setName("D:\\NDISC_TEST\\data\\test_get.ppt");
    * 
    * NApi napi = new NApi(false); try {
    * 
    * connID = napi.NDisc_Connect(HOST, PORT); System.out.println("Connected -
    * ID = " + connID);
    * 
    * TimeChecker.setStartPoint(); bRet = napi.NDISC_FileGet(nFile);
    * System.out.println(TimeChecker.getCurrentInterval()); } catch
    * (FileException e) { e.printStackTrace();
    * System.out.println(e.getMessage()); } catch (NetworkException e) {
    * e.printStackTrace(); System.out.println(e.getMessage()); } catch
    * (NDiscException e) { e.printStackTrace();
    * System.out.println(e.getMessage()); } finally { try {
    * napi.NDisc_Disconnect(); } catch (NetworkException e) {
    * e.printStackTrace(); System.out.println(e.getMessage()); } }
    * 
    * System.out.println("Result : " + bRet); }
    */

   /*
    * public void testGetConf() throws Exception { String HOST = "70.7.103.106";
    * int PORT = 7404;
    * 
    * NApi napi = new NApi(false);
    * 
    * try { int connID = napi.NDisc_Connect(HOST, PORT);
    * System.out.println("Connected - ID = " + connID);
    * 
    * napi.NDISC_GetServerConfigure();
    *  } catch (Exception e) { e.printStackTrace();
    *  } finally { try { napi.NDisc_Disconnect(); } catch (Exception ex) {
    * ex.printStackTrace(); } } }
    */
   
   public void testFileReg() throws Exception {
      String HOST = "70.2.199.85";
      int PORT = 7404;
      int nNumOfFiles = 1;
      NFile[] nFile = new NFile[nNumOfFiles];
      String[] fileID = null;
      int connID = -1;

      nFile[0] = new NFile();
      nFile[0].setName("D:\\NDISC_TEST\\data\\3.dat");
      nFile[0].setVolumeId(101);
      nFile[0].setStatType(NDConstant.STAT_COMP_ENC);

      NApi napi = new NApi(false);

      try {
         TimeChecker.setStartPoint();
         connID = napi.NDisc_Connect(HOST, PORT);
         System.out.println("Connected - ID = " + connID);

         try {
            fileID = napi.NDISC_FileReg(nFile);
         } catch (Exception ee) {
            System.out.println(ee.getMessage());
         }

         if (null == fileID) {
            System.out.println("FileReg : Fail");
         } else {
            for (int j = 0; j < fileID.length; j++) {
               System.out.println("Return ID [" + j + "] = " + fileID[j]);
            }
         }
      } catch (Exception e) {
         e.printStackTrace(); //
         System.out.println(e.getMessage());
      } finally {
         try {
            napi.NDisc_Disconnect();
            System.out.println(TimeChecker.getCurrentInterval());
         } catch (NetworkException e) {
            e.printStackTrace(); //
            System.out.println(e.getMessage());
         }
      }
   }   
}
