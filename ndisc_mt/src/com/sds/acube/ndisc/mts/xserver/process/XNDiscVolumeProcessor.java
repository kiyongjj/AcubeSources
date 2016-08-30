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

import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * 볼륨 생성 Processor
 * 
 * @author Takkies
 *
 */
public class XNDiscVolumeProcessor extends XNDiscBaseProcessor {

	public XNDiscVolumeProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
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
		logger.log(LoggerIF.LOG_INFO, connection.getRemotePort() + " make volume start !!!!");

		logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " set volume start");
		Volume volume = new Volume();
		volume.setName(files.nextToken().trim());
		volume.setAccessable(files.nextToken().trim());
		volume.setCreatedDate(XNDiscUtils.getDate(XNDiscConfig.NDISC_NA_RESERV));
		volume.setDesc(files.nextToken().trim());
		logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " set volume end");

		try {
			logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " insertNewVolumeToDB start");
			storage.insertNewVolumeToDB(volume);
			logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " insertNewVolumeToDB end");
			rtn = true;
		} catch (Exception e) {
			msg = e.getMessage();
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
			logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " sendReplyMsg--- start");
			 
			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
			logger.log(LoggerIF.LOG_DEBUG, connection.getRemotePort() + " sendReplyMsg--- end");
		}
		logger.log(LoggerIF.LOG_INFO, connection.getRemotePort() + " make volume end !!!!");
		return rtn;
	}

}
