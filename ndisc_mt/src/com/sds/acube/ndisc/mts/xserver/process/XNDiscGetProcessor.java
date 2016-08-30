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
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
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
 * 파일 다운로드(취득) Processor
 * 
 * @author Takkies
 * 
 */
public class XNDiscGetProcessor extends XNDiscBaseProcessor {

	public XNDiscGetProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
		super(storage, logger, files, filecounts);
	}

	@Override
	public boolean run(INonBlockingPipeline connection) throws BufferUnderflowException, BufferOverflowException, ClosedChannelException, IOException {

		boolean rtn = false;
		String msg = "";
		try {
			if (connection.getRemotePort() == 0) {
				return true;
			}

			if (!connection.isOpen()) {
				return true;
			}

			connection.setAutoflush(false);

			logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] get file start !!!!");
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - start");

			NFile[] nFile = new NFile[filecounts];

			for (int i = 0; i < filecounts; i++) {
				nFile[i] = new NFile();
				nFile[i].setId(files.nextToken().trim());
				nFile[i].setStatType(files.nextToken().trim());
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] create NFile - end");

			nFile = getBasicNFileGetInfo(nFile);
			nFile = aquireStorageInfo(nFile, XNDiscConfig.STORAGE_PATH_ACCESS);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "][" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile--- start");
			XNDiscUtils.copyNFile(nFile, false, false);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] copyNFile--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile--- start");
			XNDiscUtils.filterNFile(nFile, false);
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] filterNFile--- end");

			msg = XNDiscConfig.NO_ERROR.concat(XNDiscConfig.DELIM_STR);

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[0]--- start");
			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[0]--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendFileInfo --- start");

			StringBuffer filebuf = new StringBuffer();
			for (NFile nfile : nFile) {
				if (XNDiscConfig.STAT_NONE.equals(nfile.getStatType())) {
					nfile.setSize((int) new File(nfile.getStoragePath()).length());
				} else {
					nfile.setSize((int) new File(nfile.getTmpPath()).length());
				}
				filebuf.append(nfile.getSize()).append(XNDiscConfig.DELIM_STR);
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[1]--- start");
			msg = XNDiscUtils.getFormatString(filebuf.toString(), XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[1]--- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendFileInfo --- end");

			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendFileExNIO --- start");

			String transfertype = XNDiscConfig.getString(XNDiscConfig.TRANSFER_TYPE, "M");
			String filepath = null;
			FileChannel fc = null;// - 2014.04.29
			FileInputStream fis = null;
			MappedByteBuffer mappedbuffer = null; // - 2014.05.19
			ByteBuffer buffer = null;

			for (NFile nfile : nFile) {
				if (XNDiscConfig.STAT_NONE.equals(nfile.getStatType())) {
					filepath = nfile.getStoragePath();
				} else {
					filepath = nfile.getTmpPath();
				}
				fis = new FileInputStream(filepath);

				fc = fis.getChannel();

				/**
				 * 서버에서는 한번에 보내고 클라이언트에서는 분할하여 받도록 구성하는 방법도 고려해볼 만함.<br>
				 * 기존에 서버에서 connection.transferFrom 를 사용하여 내부적으로 tuning된 api 전송방식 대신<br>
				 * MappedByteBuffer를 사용하여 소켓에 직접 write 하는 방식으로 대체하는 것도 가능함.<br>
				 * <br>
				 * 클라이언트에서는 전송받은 socket data를 fully read 한 후 fully write 하도록 구성.<br>
				 * 기존에는 NApi 에서 정보를 받을 때 file channel 의 tranferFrom을 사용하도록 되어 있었으나<br>
				 * 이 부분을 socket data를 fully read 한 후 fully write 하도록 변경함.<br>
				 * <br>
				 * MappedByteBuffer 를 이용한 전송 부분이 문제가 있을 경우 서버에서도 fully read, fully write 방식으로 처리하면 됨.<br>
				 * <br>
				 * 로컬의 개발서버에서 테스트 시 서버, 클라이언트 fully read, fully write 방식은<br>
				 * 순수한 텍스트 파일에 대해서 전송할 경우 대량의 건수로 전송해도 이상이 없으나<br>
				 * 바이너리 파일(이미지 등)일 경우 일부 데이터가 전송되지 않는 현상이 발생하고 있음.<br>
				 * 예를 들면 10건 전송 시 간혹 1~2건의 파일에서 일부 데이터가 유실되고 있으며,<br>
				 * 파일 사이즈는 동일하나 파일을 에디터로 강제로 열어 올바르게 전송된 파일과 비교하면 약간의 데이터 차이가 있음.<br>
				 * <br>
				 * large file 전송 시 fully read, fully write 방식은 파일 사이즈가 커질 수록 read, write interval 에 의한 delay로 일부 속도 저하가 있음.<br>
				 * 그러나 파일 전송의 안정성 측면을 고려하고, large file size 가 특이사항이 없는한 몇 MByte 수준으로 간주했을 경우<br>
				 * 테스트 시와 같이 concurrent user가 크지 않고 active user pattern이 빈번하지 않을 것이므로<br>
				 * 전송속도 지체에 일부 영항를 주나 지대한 영향을 주고 있지 않는 것으로 판단됨.<br>
				 * <br>
				 * 클라이언트를 XNApi를 사용할 경우 fully read, fully write 방식 대신 xsocket api를 사용하도록 권장함.<br>
				 */
				if (transfertype.equals("T")) { // - performance tunning - NApi와 연동 시 특정 OS에서 전송이 일부 안되는 (클라이언트가 전부 받지 못하는)현상
					connection.transferFrom(fc);
					connection.flush();
				} else if (transfertype.equals("M")) { // - Mapped 바이트 버퍼(메모리 버퍼)를 이용한 빠른 파일 복사 전송
					// -- connection.markWritePosition();
					mappedbuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
					// -- connection.resetToWriteMark();
					connection.write(mappedbuffer);
					connection.flush();
					mappedbuffer.clear();
				} else if (transfertype.equals("S")) { // - 2014.05.19 NApi 대응
					long nRemain = fc.size();
					long nAmount = XNDiscConfig.FILE_TRANS_BUFFER_SIZE;
					buffer = ByteBuffer.allocateDirect((int) nAmount);
					while (nRemain > 0) {
						if (nRemain < XNDiscConfig.FILE_TRANS_BUFFER_SIZE) {
							nAmount = nRemain;
							buffer = ByteBuffer.allocateDirect((int) nAmount);
						}
						while (buffer.position() < nAmount) {
							fc.read(buffer);
						}
						buffer.flip();
						while (buffer.hasRemaining()) {
							connection.write(buffer);
							connection.flush();
						}
						buffer.clear();
						nRemain -= nAmount;
					}
				}
				fc.close();// - 2014.04.29
				fis.close();
			}
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendFileExNIO --- end");
			rtn = true;

		} catch (Exception e) {
			logger.log(LoggerIF.LOG_ERROR, XNDiscUtils.printStackTrace(e));
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
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[2]--- start");
			msg = msg.concat(XNDiscConfig.DELIM_STR);
			msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
			connection.write(msg);
			connection.flush();
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] sendReplyMsg[2]--- end");
		}
		logger.log(LoggerIF.LOG_INFO, "*** [" + connection.getId() + "] [" + connection.getRemoteAddress().getHostAddress() + ":" + connection.getRemotePort() + "] get file end !!!!");

		return rtn;
	}

}
