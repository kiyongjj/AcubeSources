package com.sds.acube.ndisc.explorer.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.napi.NApi;

public class ConfigHandler {

   public static String[][] getLocalCategory() {

      String[][] category = null;
      String[] categoryNames = NDCommon.configuration.getCategoryNames();
      category = new String[categoryNames.length][3];

      for (int i = 0; i < category.length; i++) {
         category[i][0] = "CONF_LOCAL_CATEGORY";
         category[i][1] = categoryNames[i];
         category[i][2] = categoryNames[i];
      }

      return category;
   }

   public static String[][] getLocalProperty(String category) {

      String[][] property = null;
      Properties prop = NDCommon.configuration.getProperties(category);

      property = new String[prop.size()][3];

      Enumeration enumeration = prop.propertyNames();
      for (int i = 0; enumeration.hasMoreElements(); i++) {
         String key = (String) enumeration.nextElement();
         String value = prop.getProperty(key);

         property[i][0] = "CONF_LOCAL_PROPERTY";
         property[i][1] = key;
         property[i][2] = value;
      }

      return property;
   }

   public static String[][] getRemoteCategory() {
      HashMap hash = getRemoteConfiguration();
      String[][] category = new String[hash.size()][3];      
      Iterator iter = hash.keySet().iterator();

      for (int i = 0; iter.hasNext(); i++) {
         String categoryName = (String) iter.next();

         category[i][0] = "CONF_REMOTE_CATEGORY";
         category[i][1] = categoryName;
         category[i][2] = categoryName;
      }

      return category;
   }

   public static String[][] getRemoteProperty(String categoryName) {
      HashMap hash = getRemoteConfiguration();      
      Properties prop = (Properties) hash.get(categoryName);
      String[][] property = new String[prop.size()][3];

      Enumeration enumeration = prop.propertyNames();
      for (int i = 0; enumeration.hasMoreElements(); i++) {
         String key = (String) enumeration.nextElement();
         String value = prop.getProperty(key);

         property[i][0] = "CONF_REMOTE_PROPERTY";
         property[i][1] = key;
         property[i][2] = value;
      }

      return property;
   }

   private static HashMap getRemoteConfiguration() {
      HashMap hash = null;

      NApi napi = new NApi(false);

      try {
         napi.NDisc_Connect(NDCommon.HOST, NDCommon.PORT);

         hash = napi.NDISC_GetServerConfigure();

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            napi.NDisc_Disconnect();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      
      return hash;
   }
}
