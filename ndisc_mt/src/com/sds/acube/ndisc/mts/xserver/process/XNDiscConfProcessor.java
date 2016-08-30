/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : 
 * </pre>
 */
package com.sds.acube.ndisc.mts.xserver.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * 환경 정보 취득 Processor
 * 
 * @author Takkies
 *
 */
public class XNDiscConfProcessor extends XNDiscBaseProcessor {

	public XNDiscConfProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
		super(storage, logger, files, filecounts);
	}

	@Override
	public boolean run(INonBlockingPipeline connection)  throws BufferUnderflowException, BufferOverflowException, ClosedChannelException, IOException {
		
		if (connection.getRemotePort() == 0) {
			return true;
		}
		
		if (!connection.isOpen()) {
			return true;
		}
		
		connection.setAutoflush(false);
		
		boolean rtn = false;
		String msg = "";
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] get configuration start !!!!");

		try {

			msg = msg.concat(XNDiscConfig.NO_ERROR).concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();

			String file = "";
			String tempdir = XNDiscConfig.getString(XNDiscConfig.TEMP_DIR);
			if (tempdir.indexOf("/") >= 0) {
				file = tempdir.concat("/").concat("config-object.tmp");
			} else {
				file = tempdir.concat(File.separator).concat("config-object.tmp");
			}

			ObjectOutputStream oos = null;
			FileOutputStream fos = null;
			HashMap<String, Properties> hash = null;
			Properties prop = null;
			try {
				hash = new HashMap<String, Properties>();
				prop = new Properties();
				String[] categoryNames = XNDiscConfig.configuration.getCategoryNames();
				for (String catgnm : categoryNames) {
					prop = XNDiscConfig.configuration.getProperties(catgnm);
					hash.put(catgnm, prop);
				}
				fos = new FileOutputStream(file);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(hash);

				logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendObjectFileSize start");

				msg = Integer.toString(((int) new File(file).length())).concat(XNDiscConfig.DELIM_STR);
				msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
				connection.write(msg);
				connection.flush();
				logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendObjectFileSize end");

			} catch (Exception e) {
				msg = e.getMessage();
				rtn = false;
			} finally {
				if (fos != null) {
					fos.close();
				}
				if (oos != null) {
					oos.close();
				}
			}

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendObjectFile start");
			FileChannel fc = null;

			try {
				fc = new FileInputStream(file).getChannel();
				// performance tunning
				//fc.transferTo(0, fc.size(), connection);
				connection.transferFrom(fc);
				connection.flush();
			} catch (Exception e) {
				msg = e.getMessage();
				rtn = false;
			} finally {
				if (fc != null) {
					fc.close();
				}
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendObjectFile end");
			rtn = true;
		} catch (Exception e) {
			msg = e.getMessage();
			rtn = false;
		} finally {
			if (rtn) {
				msg = XNDiscConfig.NO_ERROR.concat(XNDiscConfig.DELIM_STR);
			} else {
				if (msg == null) {
					msg = XNDiscConfig.ERROR.concat(XNDiscConfig.DELIM_STR);
				} else {
					msg = XNDiscConfig.ERROR.concat(XNDiscConfig.DELIM_STR).concat(msg);
				}
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg--- start");
			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg--- end");
		}
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] get configuration end !!!!");
		return rtn;
	}

}
