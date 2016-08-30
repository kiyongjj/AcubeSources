package com.sds.acube.ndisc.mts.util.loader;

import java.lang.reflect.Constructor;

import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;

public class DynamicClassLoader {

   public static Object createInstance(String type) throws Exception {
      Object obj = null;
      if (type != null) {
         Class[] paramType = new Class[] {};
         Constructor con = Class.forName(type).getConstructor(paramType);
         Object[] params = new Object[] {};
         obj = con.newInstance(params);
      }
      return obj;
   }
   
   public static Object createInstance(String type, LoggerIF logger) throws Exception {
      Object obj = null;
      if (type != null) {
         Class[] paramType = new Class[] { LoggerIF.class };
         Constructor con = Class.forName(type).getConstructor(paramType);
         Object[] params = new Object[] { logger };
         obj = con.newInstance(params);
      }
      return obj;
   }

   public static Object createInstance(String type, StorageIF storage) throws Exception {
      Object obj = null;
      if (type != null) {
         Class[] paramType = new Class[] { StorageIF.class };
         Constructor con = Class.forName(type).getConstructor(paramType);
         Object[] params = new Object[] { storage };
         obj = con.newInstance(params);
      }
      return obj;
   }   

   public static Object createInstance(String type, LoggerIF logger, StorageIF storage) throws Exception {
      Object obj = null;
      if (type != null) {
         Class[] paramType = new Class[] { LoggerIF.class, StorageIF.class };
         Constructor con = Class.forName(type).getConstructor(paramType);
         Object[] params = new Object[] { logger, storage };
         obj = con.newInstance(params);
      }
      return obj;
   }
}