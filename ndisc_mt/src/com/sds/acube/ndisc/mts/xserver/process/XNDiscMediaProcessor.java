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
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.ibatis.common.jdbc.exception.NestedSQLException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * XNDisc 미디어 생성 Processor
 *  
 * @author Takkies
 *
 */
public class XNDiscMediaProcessor extends XNDiscBaseProcessor {

	public XNDiscMediaProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
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
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] make media start !!!!");

		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] set media start");
		Media media = new Media();

		media.setName(files.nextToken().trim());
		media.setType(Integer.parseInt(files.nextToken().trim()));
		media.setPath(files.nextToken().trim());
		media.setCreatedDate(XNDiscUtils.getDate(XNDiscConfig.NDISC_NA_RESERV));
		media.setDesc(files.nextToken().trim());
		media.setMaxSize(Long.parseLong(files.nextToken().trim()));
		media.setSize(0);
		media.setVolumeId(Integer.parseInt(files.nextToken().trim()));
		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] set media end");
		
		try {
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] makeMediaPath start");
			XNDiscUtils.makeMediaPath(media.getPath());
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] makeMediaPath end");
			
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] insertNewMediaToDB start");
			
			storage.insertNewMediaToDB(media);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] insertNewMediaToDB end");
			rtn = true;
		} catch (Exception e) {
			if (e instanceof SQLException) {
				msg = ((SQLException) e).getSQLState() + " : " + ((SQLException) e).getErrorCode(); 
			} else if (e instanceof NestedSQLException) {
				msg = ((NestedSQLException) e).getSQLState() + " : " + ((NestedSQLException) e).getErrorCode();
			} else {
				msg = e.getMessage();
			}
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

		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] make media end !!!!");
		return rtn;
	}

}
