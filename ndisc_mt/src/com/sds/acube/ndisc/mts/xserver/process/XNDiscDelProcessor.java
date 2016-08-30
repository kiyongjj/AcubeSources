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
 * 파일 삭제 Processor
 * 
 * @author Takkies
 *
 */
public class XNDiscDelProcessor extends XNDiscBaseProcessor {

	public XNDiscDelProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
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
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] del file start !!!!");

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - start");

		NFile[] nFile = null;
		nFile = new NFile[filecounts];

		for (int i = 0; i < filecounts; i++) {
			nFile[i] = new NFile();
			nFile[i].setId(files.nextToken().trim());
		}
		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - end");

		try {

			nFile = aquireStorageInfo(nFile, XNDiscConfig.STORAGE_PATH_ACCESS);

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] deleteNFile--- start");
			File file = null;
			for (NFile nfile : nFile) {
				file = new File(nfile.getStoragePath());
				boolean bRet = file.delete();
				logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() +  " media file [" + nfile.getStoragePath() + "] deleted : " + bRet);
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] deleteNFile--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] deleteNFileFromDB--- start");

			storage.deleteNFileFromDB(nFile);

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] deleteNFileFromDB--- end");
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
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] del file end !!!!");
		return rtn;
	}

}
