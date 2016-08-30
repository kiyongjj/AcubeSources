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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.StringTokenizer;

import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * 파일 교체 Processor
 * 
 * @author Takkies
 * 
 */
public class XNDiscRepProcessor extends XNDiscBaseProcessor {

	public XNDiscRepProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
		super(storage, logger, files, filecounts);
	}

	@Override
	public boolean run(INonBlockingPipeline connection) throws BufferUnderflowException, BufferOverflowException, ClosedChannelException, IOException {

		boolean rtn = false;
		String msg = "";
		NFile[] nFile = null;

		try {

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

			logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] replace file start !!!!");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - start");

			nFile = new NFile[filecounts];

			for (int i = 0; i < filecounts; i++) {
				nFile[i] = new NFile();
				nFile[i].setName(files.nextToken().trim());
				nFile[i].setSize(Integer.parseInt(files.nextToken().trim()));
				nFile[i].setStatType(files.nextToken().trim());
				nFile[i].setId(XNDiscUtils.getFileID(files.nextToken().trim()));
				nFile[i].setModifiedDate(XNDiscUtils.getDate(files.nextToken().trim()));
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - end");

			nFile = XNDiscUtils.getBasicNFileInfo(nFile);
			nFile = aquireStorageInfo(nFile, XNDiscConfig.STORAGE_PATH_ACCESS);
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

			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
		}
		if (rtn) {
			rtn = false;

			String statType = null;
			String strFilePath = null;
			FileChannel fc = null;
			FileOutputStream fos = null;
			try {
				for (NFile file : nFile) {
					statType = file.getStatType();
					strFilePath = null;
					if ("0".equals(statType)) {
						strFilePath = file.getStoragePath();
					} else {
						strFilePath = file.getTmpPath();
					}
					fos = new FileOutputStream(strFilePath);
					fc = fos.getChannel();

					int transfer = 0;
					while (transfer < file.getSize()) {
						// performance tunning
						// transfer = (int) (transfer + fc.transferFrom(connection, transfer, file.getSize() - transfer));
						transfer = (int) (transfer + (int) connection.transferTo(fc, file.getSize() - transfer));
					}

					fc.close();
					fos.close();
				}
				logger.log(LoggerIF.LOG_DEBUG, "*** [3] receiveFileExNIO");

				XNDiscUtils.filterNFile(nFile, true);
				logger.log(LoggerIF.LOG_DEBUG, "*** [4] filterNFile");

				XNDiscUtils.copyNFile(nFile, true, false);
				logger.log(LoggerIF.LOG_DEBUG, "*** [5] copyNFile(원본 경로와 복사본의 경로 설정 옵션 : true, 복사 실행 여부 : false)");

				storage.updateNFileToDB(nFile);
				logger.log(LoggerIF.LOG_DEBUG, "*** [6] updateNFileToDB");
				rtn = true;
			} catch (Exception ex) {
				msg = ex.getMessage();
				rtn = false;
			} finally {
				if (rtn) {
					msg = XNDiscConfig.NO_ERROR.concat(XNDiscConfig.DELIM_STR);
					for (NFile file : nFile) {
						msg = msg.concat(file.getId()).concat(XNDiscConfig.DELIM_STR);
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
				logger.log(LoggerIF.LOG_DEBUG, "*** [7] sendReplyMsg(ERROR or NO ERROR)");

				// l.append("*** XNDISC FILE REPLACE END...").append(XNDiscUtils.LINE_SEPERATOR);
				// logger.log(LoggerIF.LOG_INFO, l.toString());
			}
		}
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] replace file end !!!!");
		return rtn;
	}

}
