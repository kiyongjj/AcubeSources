package com.sds.acube.ndisc.mts.logger.impl;

import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;

public abstract class Log4JLoggerAdaptor implements LoggerIF {
   public abstract void initLogger();
   public abstract void log(byte lebel, String msg, Throwable error);
   public abstract void log(byte lebel, String msg);
   public abstract void info(String msg);
}

