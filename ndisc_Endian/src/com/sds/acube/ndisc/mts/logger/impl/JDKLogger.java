package com.sds.acube.ndisc.mts.logger.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sds.acube.ndisc.mts.common.NDCommon;

public class JDKLogger extends JDKLoggerAdaptor
{
   private FileHandler fileHandler;
   private Logger logger = Logger.getLogger("com.sds.acube.ndisc.mts.server");

   private Level getLogLevel(byte level)
   {
      Level LVL = null;

      switch (level)
      {
      case LOG_SEVERE:
         LVL = Level.SEVERE;
         break;
      case LOG_ERROR:
         LVL = Level.SEVERE;
         break;         
      case LOG_WARNING:
         LVL = Level.WARNING;
         break;
      case LOG_INFO:
         LVL = Level.INFO;
         break;
      case LOG_CONFIG:
         LVL = Level.CONFIG;
         break;
      case LOG_DEBUG:
         LVL = Level.FINE;
         break;         
      case LOG_FINE:
         LVL = Level.FINE;
         break;
      case LOG_FINER:
         LVL = Level.FINER;
         break;
      case LOG_FINEST:
         LVL = Level.FINEST;
         break;
      default:
         LVL = Level.INFO;
         break;
      }

      return LVL;
   }

   public void initLogger()
   {
      try 
      {
         fileHandler = new FileHandler(NDCommon.NDISC_LOG_DIR + File.separator + "NDiscServer.log");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      logger.addHandler(fileHandler);
      logger.setLevel(Level.ALL);
   }

   public void log(byte level, String msg, Throwable error)
   {
      Level LVL = getLogLevel(level);

      logger.log(LVL, msg, error);
   }

   public void log(byte level, String msg)
   {
      Level LVL = getLogLevel(level);
      logger.log(LVL, msg);
   }

   public void info(String msg)
   {
      logger.info(msg);
   }
}
