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
package com.sds.acube.ndisc.mts.xserver.handler;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.xsocket.Execution;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineConnectHandler;
import org.xsocket.connection.multiplexed.IPipelineConnectionTimeoutHandler;
import org.xsocket.connection.multiplexed.IPipelineDataHandler;
import org.xsocket.connection.multiplexed.IPipelineDisconnectHandler;
import org.xsocket.connection.multiplexed.IPipelineIdleTimeoutHandler;

import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.cipher.jce.LicenseChecker;
import com.sds.acube.ndisc.mts.xserver.factory.XNDiscThreadFactory;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscBaseProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscConfProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscCpyProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscDelProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscGetProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscInfoProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscMediaProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscRegProcessor;
import com.sds.acube.ndisc.mts.xserver.process.XNDiscVolumeProcessor;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscStatus;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * Handler replace 기술을 사용하였으나<br>
 * replace 된 Handler에서 OS에 따라 socket connection 을 잃어버리거나<br>
 * socket connection이 merging 되는 현상이 발견<br>
 * 대표 Handler만을 구현하고 socket data 수신 event 발생 시<br>
 * 해당 데이터의 process type에 따른 Processor를 개별구현하고<br>
 * socket connection 의 관리는 대표 Handler에 맡김<br>
 * 
 * @author Takkies
 * 
 */
public class XNDiscHandler implements  IPipelineConnectHandler, IPipelineDataHandler, IPipelineDisconnectHandler, IPipelineConnectionTimeoutHandler, IPipelineIdleTimeoutHandler {

	/* multiplexing socket connection pipeline 동기화 */
	private Set<INonBlockingPipeline> sessions = Collections.synchronizedSet(new HashSet<INonBlockingPipeline>());

	/* storage 객체 */
	private StorageIF storage;

	/* logger 객체 */
	private LoggerIF logger;

	/* 라이센스 invalid 시 최대 실행 수 */
	public static final int MAX_FILE_OPERATION_INVALID_LICENSE = 10;

	/* 라이센스 invalid 시 현재 실행 수 */
	private Integer currrentOperationCount = 0;

	private boolean isInitialized = false;

	private boolean isDestroyed = false;

	/**
	 * XNDisc Server에서 호출되는 기본 생성자
	 * 
	 * @param storage
	 *            storage 객체
	 * @param logger
	 *            logger 객체
	 */
	public XNDiscHandler(StorageIF storage, LoggerIF logger) {
		this.storage = storage;
		this.logger = logger;
	}

	/**
	 * XNApi Client에서 접속 event 발생 시 호출<br>
	 * 접속 정보 출력 및 라이센스 체크(라이센스가 없을 경우 특정 회수(10번)만 테스트해볼 수 있도록 함.)<br>
	 * <br>
	 * Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함.<br>
	 * socket connection pipeline 정보 synchronized
	 */
	public boolean onConnect(INonBlockingPipeline connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] ----------▶ Connect");

		boolean license = licenseCheck();

		if (!license) {
			logger.log(LoggerIF.LOG_INFO, "*** XNDisc Server Lincense is invalid !!!");
			if (getCurrrentOperationCount() < MAX_FILE_OPERATION_INVALID_LICENSE) {
				synchronized (currrentOperationCount) {
					currrentOperationCount++;
					setCurrrentOperationCount(currrentOperationCount);
				}
			}
			if (getCurrrentOperationCount() < MAX_FILE_OPERATION_INVALID_LICENSE) {
				logger.log(LoggerIF.LOG_INFO, String.format("*** The remaining file operation count is %d", (MAX_FILE_OPERATION_INVALID_LICENSE - getCurrrentOperationCount())));
			} else {
				logger.log(LoggerIF.LOG_SEVERE, "\r\n\r\n*** XNDisc Server License exceeded!!!\r\n\r\n");
				String msg = XNDiscConfig.ERROR.concat(XNDiscConfig.DELIM_STR).concat("XNDisc Server License exceeded!!!").concat(XNDiscConfig.DELIM_STR);
				msg = XNDiscUtils.getFormatString(msg, XNDiscConfig.REPLY_BUFFER_SIZE);
				connection.write(msg);
				connection.flush();
				return false;
			}
		}

		try {
			synchronized (sessions) {
				sessions.add(connection);
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_SEVERE, "Fail - XNDiscHandler.connect", ex);
			return false;
		}

		return true;
	}

	/**
	 * socket connection disconnect event 발생 시 호출<br>
	 * <br>
	 * disconnect event는 내부적으로 자동 호출되며 여러번 반복될 수 있음.<br>
	 */
	public boolean onDisconnect(INonBlockingPipeline connection) throws IOException {
		try {
			logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] ----------▶ Disconnect");
			synchronized (sessions) {
				sessions.remove(connection);
			}
			if (connection.isOpen()) {
				connection.close();
			}
		} catch (NullPointerException npe) {
			logger.log(LoggerIF.LOG_SEVERE, "XNDisc Server Disconnect Nullpointerexception occured!!!");
		}
		return true;
	}

	/**
	 * socket connection time out event 발생 시 호출<br>
	 * <br>
	 * Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함.<br>
	 */
	public boolean onConnectionTimeout(INonBlockingPipeline connection) throws IOException {
		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] ----------▶ Connect Timeout");
		return true;
	}

	/**
	 * socket connection idle timeout 설정<br>
	 * <br>
	 * Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함.<br>
	 */

	public boolean onIdleTimeout(INonBlockingPipeline connection) throws IOException {
		logger.log(LoggerIF.LOG_DEBUG, "*** [" + connection.getId() + "] ----------▶ Idle Timeout");
		connection.setIdleTimeoutMillis(30 * 1000); // resets the timeout counter
		return true;
	}

	public void onInit() {
		if (Thread.currentThread().getName().startsWith(XNDiscThreadFactory.PREFIX)) {
			logger.log(LoggerIF.LOG_DEBUG, "[onInit] shouldn't be executed by a worker thread not by " + Thread.currentThread().getName());
		}
		if (Thread.currentThread().getName().startsWith("xDispatcher")) {
			logger.log(LoggerIF.LOG_DEBUG, "[onInit] shouldn't be executed by a disptacher thread not by " + Thread.currentThread().getName());
		}
		if (isInitialized) {
			logger.log(LoggerIF.LOG_DEBUG, "[onInit]  shouldn't be initialized");
		}
		if (isDestroyed) {
			logger.log(LoggerIF.LOG_DEBUG, "[onInit]  shouldn't be destroyed");
		}
		isInitialized = true;
	}

	public void onDestroy() throws IOException {
		if (Thread.currentThread().getName().startsWith(XNDiscThreadFactory.PREFIX)) {
			logger.log(LoggerIF.LOG_DEBUG, "[onDestroy] shouldn't be executed by a worker thread not by " + Thread.currentThread().getName());
		}
		if (Thread.currentThread().getName().startsWith("xDispatcher")) {
			logger.log(LoggerIF.LOG_DEBUG, "[onDestroy] shouldn't be executed by a disptacher thread");
		}
		if (!isInitialized) {
			logger.log(LoggerIF.LOG_DEBUG, "[onDestroy]  should be initialized");
		}
		if (isDestroyed) {
			logger.log(LoggerIF.LOG_DEBUG, "[onDestroy]  shouldn't be isDestroyed");
		}
		isInitialized = false;
		isDestroyed = true;
	}

	/**
	 * XNApi Client 에서 socket connection data send event 발생 시 호출<br>
	 * received data 에서 process type을 찾아내어 해당 Processor를 호출<br>
	 * 기존에 Handler replace 시 socket connection 문제점 처리<br>
	 * <br>
	 * Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함.<br>
	 */
	@Execution(Execution.MULTITHREADED)
	public boolean onData(INonBlockingPipeline connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		boolean rtn = true;
		try {

			synchronized (sessions) {
				/* Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함 */
				Iterator<INonBlockingPipeline> iter = sessions.iterator();

				while (iter.hasNext()) {
					/* Multi-Thread 방식일 경우 {@link INonBlockingConnection} 을 받아야함 */
					INonBlockingPipeline nbconn = (INonBlockingPipeline) iter.next();
					iter.remove();

					if (nbconn.isOpen()) { // connection 이 오픈 되어 있을 경우만 처리

						String msg = "";
						// if (nbconn.available() == 5 || nbconn.available() > XNDiscConfig.REPLY_BUFFER_SIZE) {
						if (nbconn.available() == 5) {
							nbconn.readStringByLength(5);
						}
						try {
							msg = nbconn.readStringByLength(XNDiscConfig.REPLY_BUFFER_SIZE);
						} catch (Exception e) {
							logger.log(LoggerIF.LOG_WARNING, "*** [" + connection.getId() + "] BufferUnderflowException, try re-reading ByteBuffer...");
							ByteBuffer buffer = ByteBuffer.allocateDirect(XNDiscConfig.INIT_BUFFER_SIZE);
							while (buffer.position() < XNDiscConfig.INIT_BUFFER_SIZE) {
								nbconn.read(buffer);
							}
							buffer.flip();
							msg = XNDiscUtils.getData(buffer);
						}

						StringTokenizer files = new StringTokenizer(msg, XNDiscConfig.DELIM_STR);
						String svc_status = files.nextToken().trim();
						int filecounts = Integer.parseInt(files.nextToken().trim());
						int st = Integer.parseInt(svc_status);

						XNDiscBaseProcessor processor = null;
						if (st == XNDiscStatus.REG.getStatus()) {
							processor = new XNDiscRegProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.GET.getStatus()) {
							processor = new XNDiscGetProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.DEL.getStatus()) {
							processor = new XNDiscDelProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.CPY.getStatus()) {
							processor = new XNDiscCpyProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.FILEINFO.getStatus()) {
							processor = new XNDiscInfoProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.MOV.getStatus()) {
							processor = new XNDiscMediaProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.REP.getStatus()) {
							processor = new XNDiscRegProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.MKMEDIA.getStatus()) {
							processor = new XNDiscMediaProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.MKVOLUME.getStatus()) {
							processor = new XNDiscVolumeProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else if (st == XNDiscStatus.GETCONF.getStatus()) {
							processor = new XNDiscConfProcessor(storage, logger, files, filecounts);
							rtn = processor.run(nbconn);
						} else {
							if (st == XNDiscStatus.READY.getStatus()) {
								logger.log(LoggerIF.LOG_INFO, "*** " + XNDiscStatus.READY.getName());
							} else if (st == XNDiscStatus.INIT.getStatus()) {
								logger.log(LoggerIF.LOG_INFO, "*** " + XNDiscStatus.INIT.getName());
							} else if (st == XNDiscStatus.QUIT.getStatus()) {
								logger.log(LoggerIF.LOG_INFO, "*** " + XNDiscStatus.QUIT.getName());
							} else {
								logger.log(LoggerIF.LOG_INFO, "*** Unknown Status !!! [" + st + "]");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * XNDisc Server 라이센스 체크<br>
	 * 라이센스가 없을 경우 특정 회수(10번)만 테스트해볼 수 있도록 함.<br>
	 * 
	 * @return 라이센스 있으면 true, 없으면 false
	 */
	private boolean licenseCheck() {
		LicenseChecker licenseChecker = new LicenseChecker();
		return licenseChecker.IsValidLicense(XNDiscConfig.getString(XNDiscConfig.NDISC_LICENSE_KEY, "xNdIsC-nO-lIcEnSeE-kYeS"));
	}

	public int getCurrrentOperationCount() {
		return currrentOperationCount;
	}

	public void setCurrrentOperationCount(int currrentOperationCount) {
		this.currrentOperationCount = currrentOperationCount;
	}

}
