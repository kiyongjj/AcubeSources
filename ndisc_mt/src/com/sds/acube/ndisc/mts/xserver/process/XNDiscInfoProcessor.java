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

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.StringTokenizer;

import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * 파일 정보 취득 Processor
 * 
 * @author Takkies
 *
 */
public class XNDiscInfoProcessor extends XNDiscBaseProcessor {

	public XNDiscInfoProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
		super(storage, logger, files, filecounts);
	}

	@Override
	public boolean run(INonBlockingPipeline connection) throws BufferUnderflowException, BufferOverflowException, ClosedChannelException, IOException {
		
		if (connection.getRemotePort() == 0) {
			return true;
		}
		
		if (!connection.isOpen()) {
			return true;
		}
		
		if (files == null || files.countTokens() <= 1) {
			return true;
		}

		connection.setAutoflush(false);
		
		boolean rtn = false;
		String msg = "";
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] file info start !!!!");

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - start");
		NFile[] nFile = null;
		nFile = new NFile[filecounts];

		for (int i = 0; i < filecounts; i++) {
			nFile[i] = new NFile();
			nFile[i].setId(files.nextToken().trim());
		}

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - end");

		try {

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] getNFileInfo start");
			nFile = getNFileInfo(nFile);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] getNFileInfo end");
		} catch (Exception e) {
			msg = e.getMessage();
			rtn = false;
		} finally {
			if (rtn) {
				msg = XNDiscUtils.makeReturnNFileInfoMsg(nFile);
				msg = XNDiscConfig.NO_ERROR.concat(XNDiscConfig.DELIM_STR).concat(msg);
			} else {
				if (msg == null) {
					msg = XNDiscConfig.ERROR.concat(XNDiscConfig.DELIM_STR);
				} else {
					msg = XNDiscConfig.ERROR.concat(XNDiscConfig.DELIM_STR).concat(msg);
				}
			}
			
			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
		}
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] file info end !!!!");
		return rtn;
	}

}
