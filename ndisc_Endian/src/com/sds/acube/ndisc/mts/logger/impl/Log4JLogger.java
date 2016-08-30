package com.sds.acube.ndisc.mts.logger.impl;

import org.apache.log4j.Logger;

public class Log4JLogger extends Log4JLoggerAdaptor {

	private Logger logger = null;
	
	public void initLogger() {
		try {
			if (null == logger) {
				logger = Logger.getLogger("com.sds.acube.ndisc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void log(byte level, String msg, Throwable error) {
		switch (level) {
		case LOG_SEVERE:
			logger.fatal(msg, error);
			break;
		case LOG_ERROR:
			logger.error(msg, error);
			break;
		case LOG_WARNING:
			logger.warn(msg, error);
			break;
		case LOG_INFO:
			logger.info(msg, error);
			break;
		case LOG_CONFIG:
			logger.debug(msg, error);
			break;
		case LOG_DEBUG:
			logger.debug(msg, error);
			break;
		case LOG_FINE:
			logger.debug(msg, error);
			break;
		case LOG_FINER:
			logger.debug(msg, error);
			break;
		case LOG_FINEST:
			logger.debug(msg, error);
			break;
		default:
			logger.info(msg, error);
			break;
		}
	}

	public void log(byte level, String msg) {
		switch (level) {
		case LOG_SEVERE:
			logger.fatal(msg);
			break;
		case LOG_ERROR:
			logger.error(msg);
			break;
		case LOG_WARNING:
			logger.warn(msg);
			break;
		case LOG_INFO:
			logger.info(msg);
			break;
		case LOG_CONFIG:
			logger.debug(msg);
			break;
		case LOG_DEBUG:
			logger.debug(msg);
			break;
		case LOG_FINE:
			logger.debug(msg);
			break;
		case LOG_FINER:
			logger.debug(msg);
			break;
		case LOG_FINEST:
			logger.debug(msg);
			break;
		default:
			logger.info(msg);
			break;
		}
	}

	public void info(String msg) {
		logger.info(msg);
	}
}
