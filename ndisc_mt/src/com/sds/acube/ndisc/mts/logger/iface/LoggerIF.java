package com.sds.acube.ndisc.mts.logger.iface;

public interface LoggerIF {
   public void initLogger();
   public void log(byte lebel, String msg, Throwable error);
   public void log(byte lebel, String msg);
   public void info(String msg);
   
   // LOG LEVEL
   public final static byte LOG_SEVERE = 0;
   public final static byte LOG_ERROR = 1;
   public final static byte LOG_WARNING = 2;
   public final static byte LOG_INFO = 3;
   public final static byte LOG_CONFIG = 4;
   public final static byte LOG_DEBUG = 5;
   public final static byte LOG_FINE = 6;
   public final static byte LOG_FINER = 7;
   public final static byte LOG_FINEST = 8;
}

