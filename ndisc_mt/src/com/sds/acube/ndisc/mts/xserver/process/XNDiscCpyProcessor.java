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
 * 파일 복사 Processor
 * 
 * @author Takkies
 *
 */
public class XNDiscCpyProcessor extends XNDiscBaseProcessor {

	public XNDiscCpyProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
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
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copy file start !!!!");

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - start");
		NFile[] nFile = null;
		nFile = new NFile[filecounts];

		for (int i = 0; i < filecounts; i++) {
			nFile[i] = new NFile();
			nFile[i].setVolumeId(Integer.parseInt(files.nextToken().trim()));
			nFile[i].setStatType(files.nextToken().trim());
			nFile[i].setId(files.nextToken().trim());
			nFile[i].setCreatedDate(XNDiscUtils.getDate(files.nextToken().trim()));
		}

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - end");

		try {

			String[] arrOriFilter = new String[nFile.length];
			for (int i = 0; i < nFile.length; i++) {
				arrOriFilter[i] = nFile[i].getStatType();
			}

			nFile = getBasicNFileCpyMovInfo(nFile);

			nFile = aquireStorageInfo(nFile, XNDiscConfig.STORAGE_PATH_ACCESS);

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile[1]--- start");
			XNDiscUtils.copyNFile(nFile, false, true);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile[1]--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile[1]--- start");
			XNDiscUtils.filterNFile(nFile, false);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile[1]--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] retrieveNFile--- start");
			nFile = XNDiscUtils.retrieveCpyNFile(nFile, arrOriFilter);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] retrieveNFile--- end");
 
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile[2]--- start");
			XNDiscUtils.filterNFile(nFile, true);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile[2]--- end");

			nFile = aquireStorageInfo(nFile, XNDiscConfig.STORAGE_PATH_REGIST);

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile[2]--- start");
			XNDiscUtils.copyNFile(nFile, true, true);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile[2]--- end");

			storage.registNFileToDB(nFile);

			rtn = true;
		} catch (Exception e) {
			msg = e.getMessage();
			rtn = false;
		} finally {
			if (rtn) {
				msg = XNDiscConfig.NO_ERROR.concat(XNDiscConfig.DELIM_STR);
				for (int i = 0; i < nFile.length; i++) {
					msg = msg.concat(nFile[i].getId()).concat(XNDiscConfig.DELIM_STR);
				}
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
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copy file end !!!!");
		return rtn;
	}

}
