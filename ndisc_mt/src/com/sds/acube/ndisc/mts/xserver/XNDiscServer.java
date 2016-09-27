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
package com.sds.acube.ndisc.mts.xserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.BlockingConnectionPool;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IServer;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.NonBlockingConnectionPool;
import org.xsocket.connection.Server;

import org.xsocket.connection.multiplexed.MultiplexedConnection;
import org.xsocket.connection.multiplexed.MultiplexedProtocolAdapter;

import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;
import com.sds.acube.ndisc.mts.xserver.factory.XNDiscSSLContextFactory;
import com.sds.acube.ndisc.mts.xserver.factory.XNDiscThreadFactory;
import com.sds.acube.ndisc.mts.xserver.handler.XNDiscHandler;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * XNDisc Server 실행 메인<br>
 * XNApi Client에 대해서만 처리되도록 변경<br>
 * 기존의 NApi는 처리되지 않음에 유의바람<br>
 * <br>
 * 기존의 Single-Thread(Procedural Processor) 의 문제점을 개선<br>
 * Multi-Thread로 동작하게 하여 성능 개선<br>
 * Multiplexing 처리(jdk 1.5 부터는 nio 패키지안에서 멀티플렉싱을 제공)로 성능 개선<br>
 * 새로운 socket connection(channel)마다 새로운 thread를 생성하는 대신<br>
 * channel을 multiplexing하면 하나 이상의 thread가 모든 channel을 처리할 수 있음.<br>
 * <br>
 * -- Multiplexing 적용을 위한 조건<br>
 * ---- 클라이언트와 서버간의 송수신 데이터의 용량이 크지 않은 경우<br>
 * ---- 송수신이 연속적이지 않은 경우<br>
 * ---- 많은 수의 클라이언트 처리를 할 경우<br>
 * <br>
 * 현재는 Multiplexing용으로만 개발되어 있음.<br>
 * 위의 Multiplexing 조건을 벗어난 환경(대용량 전송이거나, 송수신이 연속적일 경우 등)일 경우<br>
 * Multiplexing 이 아닌 일반적인 Multi-Thread 기반으로 구성할 필요가 있음.<br>
 * {@link IServer} 구성 시 {@link MultiplexedProtocolAdapter} 대신 Handler만 넘기면 됨.<br>
 * 또한 XNApi에서 Connection 시 {@link MultiplexedConnection} 대신<br>
 * {@link NonBlockingConnection} 또는 {@link BlockingConnection}을 이용하도록 변경해야함.<br>
 * Multi-Thread 기반일 경우 Pool 사용가능 함.<br>
 * {@link NonBlockingConnectionPool} 또는 {@link BlockingConnectionPool} 을 이용하면 됨.<br>
 * 
 * @author Takkies
 * 
 */
public class XNDiscServer {

	/* XNDisc Server 버전 정보 */
	private static String XNDISC_SERVER_VERSION;

	/* 표준 터미널 Clear Command */
	private static final String CLEAR_TERMINAL_ANSI_CMD = new String(new byte[] { 27, 91, 50, 74, 27, 91, 72 });

	/* XNDisc Server 실행 상태(Windows : LISTENING, UNIX : LISTEN) */
	private static final String XNDISC_LISTEN_STATUS = "LISTEN";

	/* xsocket debugging 용 로그 */
	Logger xsocket_log = Logger.getLogger("org.xsocket.connection");

	/* 서버 객체 */
	private IServer server;

	/* Log 클래스 */
	private LoggerIF logger = null;

	/* Storage 클래스 */
	private StorageIF storage = null;

	// XNDisc Server 의 옵션값 : 성능을 고려하여 적절하게 조절

	/* 최대 Worker Pool 개수(Server 생성 시 초기값) */
	private String SIZE_WORKER_POOL = XNDiscConfig.getString(XNDiscConfig.SIZE_WORKER_POOL, "100");

	/* 기본 Worker Pool 개수(Server 생성시 초기값) */
	private String MIN_SIZE_WORKER_POOL = XNDiscConfig.getString(XNDiscConfig.MIN_SIZE_WORKER_POOL, "4");

	/* Worker Thread Pool 개수(Executors 값) */
	private Integer WORKER_THREAD_POOL = XNDiscConfig.getInt(XNDiscConfig.WORKER_POOL_THREAD_COUNT, 15);

	/* Worker Pool 타입(기본은 Fixed) */
	private String WORKER_POOL_TYPE = XNDiscConfig.getString(XNDiscConfig.WORKER_POOL_TYPE, "F");

	/* 초기 디스패쳐 개수 */
	private String DISPATCHER_INIT_COUNT = XNDiscConfig.getString(XNDiscConfig.DISPATCHER_INIT_COUNT, "3");

	/* 디스패쳐의 최대 핸들러 개수 */
	private String DISPATCHER_MAX_HANDLES = XNDiscConfig.getString(XNDiscConfig.DISPATCHER_MAX_HANDLES, "20");

	/* 버퍼 다이렉트 사용여부(client, server 모두 기본 true) */
	private String READ_BUFFER_USEDIRECT = XNDiscConfig.getString(XNDiscConfig.READ_BUFFER_USEDIRECT, "true");

	/* 오퍼레이션 없을 경우 채널 detach */
	private String DETACH_HANDLE_NO_OPERATION = XNDiscConfig.getString(XNDiscConfig.DETACH_HANDLE_NO_OPERATION, "false");

	/* XNDisc Server IP 정보 */
	private String HOST = XNDiscConfig.getString(XNDiscConfig.HOST, XNDiscConfig.LOCAL_HOST);

	/* XNDisc Server Port 정보 */
	private int PORT = XNDiscConfig.getInt(XNDiscConfig.PORT, XNDiscConfig.LOCAL_PORT);

	/* DB 관련 처리 클래스 정보 */
	private String MTS_STORAGE = XNDiscConfig.getString(XNDiscConfig.STORAGE);

	/* Log 관련 처리 클래스 정보 */
	private String MTS_LOGGER = XNDiscConfig.getString(XNDiscConfig.LOGGER);

	/* 인크립트 필터 클래스 정보 */
	private String FILTER_ENC = XNDiscConfig.getString(XNDiscConfig.ENCRYPT);

	/* 압축 필터 클래스 정보 */
	private String FILTER_COMP = XNDiscConfig.getString(XNDiscConfig.COMPRESS);

	/* XNDisc Server SSL 사용 여부 */
	private boolean USE_SSL = XNDiscConfig.getBoolean(XNDiscConfig.USE_SSL);

	private boolean XSOCKET_DEBUG = XNDiscConfig.getBoolean(XNDiscConfig.XSOCKET_DEBUG);

	/* OS 별 라인피드 */
	private String LINE_SEPERATOR = System.getProperty("line.separator");

	static {
		XNDISC_SERVER_VERSION = "XNDisc " + XNDiscUtils.getXNDiscVersion() + "(" + XNDiscUtils.getXNDiscPublshingDate() + ")";
	}

	/**
	 * 서버 정보 초기화 하기
	 * 
	 * @throws Exception
	 *             초기화 에러
	 */
	private void init() throws Exception {

		/* xsocket 디버깅 - FINE 이상일 경우 디버깅용 소켓 정보가 로그에 그대로 쌓이므로 운영 시 주의 */
		if (XSOCKET_DEBUG) {
			xsocket_log.setLevel(Level.ALL);
		} else {
			xsocket_log.setLevel(Level.INFO);
		}
		ConsoleHandler console = new ConsoleHandler();
		if (XSOCKET_DEBUG) {
			console.setLevel(Level.ALL);
		} else {
			console.setLevel(Level.INFO);
		}
		xsocket_log.addHandler(console);
		/* xsocket 디버깅 - FINE 이상일 경우 디버깅용 소켓 정보가 로그에 그대로 쌓이므로 운영 시 주의 */

		initLogger();

		initFilter();

		initStorage();

		InetAddress ia = InetAddress.getByName(HOST);
		if (ia == null) {
			ia = InetAddress.getLocalHost();
		}

		/* 서버 기동 옵션 정보 (기본값 사용) - Receive Buffer Size는 지정하지 않음 */
		Map<String, Object> options = new HashMap<String, Object>();

		// - 2014.04.29 - 기동 옵션 추가 - START - Multiplexing일 경우 대부분 사용불가함.
		// options.put(IConnection.SO_REUSEADDR, true);
		// options.put(IConnection.SO_LINGER, "5"); // 닫을 때 데이터가 존재하는 경우는 지연 합니다 (블록 모드에 구성되어 있는 경우만)
		// options.put(IConnection.SO_KEEPALIVE, true);
		// options.put(IConnection.TCP_NODELAY, true);
		// options.put(IConnection.SO_RCVBUF, XNDiscConfig.INIT_BUFFER_SIZE);
		// options.put(IConnection.SO_SNDBUF, XNDiscConfig.INIT_BUFFER_SIZE);
		// - 2014.04.29 - 기동 옵션 추가 - END - Multiplexing일 경우 대부분 사용불가함.

		/* SERVER의 성능을 고려하여 적절하게 조절 */

		/* 기본 workerpool (자동 크기 조정 작업자 풀)의 최대 스레드 풀 크기. */
		/* 사용자 지정의 workerpool이 설정되지 않은 경우 여기서 지정한 workerpool는 서버에서 사용됨. 기본값은 100. */
		System.setProperty(XNDiscUtils.XSOCKET_WORKER_POOL_MIN_SIZE, SIZE_WORKER_POOL);
		/* 기본 workerpool의 최소 스레드 풀 크기. 기본값은 4 */
		System.setProperty(XNDiscUtils.XSOCKET_WORKER_POOL_MIN_SIZE, MIN_SIZE_WORKER_POOL);
		/* 사용되는 디스패쳐 (NIO Selectors)의 수. 기본적으로 2 개의 디스패처가 사용됨. */
		System.setProperty(XNDiscUtils.XSOKET_DISPATCHER_INIT_COUNT, DISPATCHER_INIT_COUNT);
		/* 디스패처 인스턴스에 연결되는 채널의 최대 수 */
		/* 필요한 경우, 추가적인 디스패처가 자동으로 추가됨. 기본적으로 채널의 수는 무제한. */
		System.setProperty(XNDiscUtils.XSOCKET_DISPATCHER_MAX_HANDLE, DISPATCHER_MAX_HANDLES);
		/* 직접 할당 된 버퍼를 설정하여 들어오는 소켓 데이터를 판독하는 데 사용. 기본값은 false. */
		System.setProperty(XNDiscUtils.XSOCKET_SERVER_READ_BUFFER_USEDIRECT, READ_BUFFER_USEDIRECT);

		// - 2014.04.25 - ByteBuffer Direct 옵션 추가 - START
		System.setProperty(XNDiscUtils.XSOCKET_CLIENT_READ_BUFFER_USEDIRECT, READ_BUFFER_USEDIRECT);
		System.setProperty(XNDiscUtils.XSOCKET_WRITE_BUFFER_USEDIRECT, READ_BUFFER_USEDIRECT);
		// - 2014.04.25 - ByteBuffer Direct 옵션 추가 - END

		// - 2014.04.29 - 기타 옵션 추가 - START
		System.setProperty(XNDiscUtils.XSOCKET_SUPPRESS_SYNC_FLUSH_WARNING, "true");
		System.setProperty(XNDiscUtils.XSOCKET_SUPPRESS_REUSE_BUFFER_WARNING, "true"); // or FlushMode.SYNC (GET 일 경우)
		System.setProperty(XNDiscUtils.XSOCKET_SUPPRESS_SYNC_FLUSH_COMPLETION_HANDLER_WARNING, "true");
		System.setProperty(XNDiscUtils.XSOCKET_DISPATCHER_BYPASSING_WRITE_ALLOWED, "true");
		// - 2014.04.29 - 기타 옵션 추가 - END

		/* 작업(NIO SelectionKey)이 없는 경우에 true 이면 채널이 detach됨. 채널은 자동적으로 다시 연결. 기본값 false. */
		System.setProperty(XNDiscUtils.XSOCKET_DISPATCHER_DETACH_HANDLE_ON_NO_OPS, DETACH_HANDLE_NO_OPERATION);
		/* 버퍼를 읽을 경우 한번에 읽을 수 있는 버퍼 사이즈 설정 기본 (1024 * 16) */
		Integer mapped_bytebuffer_maxsize = 1024 * XNDiscConfig.getInt(XNDiscConfig.TRANSFER_MAPPED_BYTE_BUFFER_MAXSIZE, 16);
		System.setProperty(XNDiscUtils.XSOCKET_TRANFER_MAPPED_BYTEBUFFER_MAX_SIZE, mapped_bytebuffer_maxsize.toString());

		/* 기동할 Sever 선언 - SSL 일 경우 키 파일 정보가 올바르지 않으면 Connection이 되지 않으므로 유의! */
		if (USE_SSL) {
			this.server = new Server(ia, PORT, options, new MultiplexedProtocolAdapter(new XNDiscHandler(storage, logger)), XNDiscSSLContextFactory.getSSLContext(), true);
			// this.server = new Server(ia, PORT, options, new XNDiscHandler(storage, logger), XNDiscSSLContextFactory.getSSLContext(), true);
		} else {
			this.server = new Server(ia, PORT, options, new MultiplexedProtocolAdapter(new XNDiscHandler(storage, logger)), null, false);
			// this.server = new Server(ia, PORT, options, new XNDiscHandler(storage, logger), null, false);
		}

		/* 어떤 ThreadPool을 사용하여도 무방, Cached 일 경우 가용한 서버의 자원을 모두 사용하므로 점유율 상승 가능성 유념. */
		/* SERVER의 성능을 고려하여 적절하게 조절(기본은 Fixed) */
		/* 스레드 풀 크기 조절 : 스레드 풀의 크기는 설정 파일이나 Runtime.availableProcessors() 값에 따라 동적으로 지정되도록 해야 함 */
		/* newFixedThreadPool 의 경우 생성시 지정한 크기로 corePoolSize, maximumPoolSize 가 설정. 시간 제한은 무제한 */
		/* newCachedThreadPool 의 경우 corePoolSize = 0, maximumPoolSize 는 Integer.MAX_VALUE 로 설정. 시간 제한은 1분 */

		if (WORKER_POOL_TYPE.equals("C")) {
			this.server.setWorkerpool(Executors.newCachedThreadPool(new XNDiscThreadFactory(true)));
		} else {
			if (XNDiscConfig.getBoolean(XNDiscConfig.MULTIPLICATION_THREAD_AVAILABLE_PROCESSORS)) {
				WORKER_THREAD_POOL = Runtime.getRuntime().availableProcessors() * WORKER_THREAD_POOL;
			}
			this.server.setWorkerpool(Executors.newFixedThreadPool(WORKER_THREAD_POOL, new XNDiscThreadFactory(true)));
		}
		this.server.setFlushmode(FlushMode.ASYNC);
	}

	/**
	 * FixedThreadPool 일 경우 Console에 해당 내용 Notice 해주기
	 */
	private void getXNDiscNotice() {
		if (WORKER_POOL_TYPE.equals("F") && XNDiscConfig.getBoolean(XNDiscConfig.MULTIPLICATION_THREAD_AVAILABLE_PROCESSORS)) {
			int worker_thread_pool = XNDiscConfig.getInt(XNDiscConfig.WORKER_POOL_THREAD_COUNT, 15);
			StringBuilder msg = new StringBuilder(LINE_SEPERATOR);
			msg.append("┌").append(StringUtils.center(" XNDisc Server Notice !!! ", 90, "-")).append("┐").append(LINE_SEPERATOR);
			msg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
			msg.append("│").append(StringUtils.rightPad("Readjust the number of the Worker Thread Pool,", 90, " ")).append("│").append(LINE_SEPERATOR);
			msg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
			msg.append("│").append(StringUtils.rightPad("Original Thread Pool Size : " + worker_thread_pool + " => Changed Thread Pool Size : " + WORKER_THREAD_POOL, 90, " ")).append("│").append(LINE_SEPERATOR);
			msg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
			msg.append("└").append(StringUtils.rightPad("", 90, "-")).append("┘").append(LINE_SEPERATOR);
			logger.log(LoggerIF.LOG_INFO, msg.toString());
		}
	}

	/**
	 * XNDisc 서버 기동하기
	 * 
	 * @throws Exception
	 *             서버 기동 에러
	 */
	public void start() throws Exception {

		init();

		this.server.setStartUpLogMessage(getXNDiscServerStartMessage());

		try {
			logger.log(LoggerIF.LOG_INFO, this.server.getStartUpLogMessage());

			getXNDiscNotice();

			this.server.run(); // shutdown hook....

			this.server.start();

		} catch (Exception e) {
			throw new RuntimeException("*** XNDisc Server Start fail..." + XNDiscUtils.printStackTrace(e));
		}

	}

	/**
	 * XNDisc 서버 중지하기.
	 * @throws Exception
	 */
	public void stop() throws Exception {
		try {
			this.server.close();
		} catch (Exception e) {
			throw new RuntimeException("*** XNDisc Server Stop fail..." + XNDiscUtils.printStackTrace(e));
		}
	}
	
	/**
	 * 로그 초기화하기
	 * 
	 * @throws Exception
	 *             로그 초기화 에러
	 */
	private void initLogger() throws Exception {
		try {
			if (null == logger) {
				logger = (LoggerIF) DynamicClassLoader.createInstance(MTS_LOGGER);
			}
			logger.initLogger();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(LoggerIF.LOG_SEVERE, "*** Fail - XNDisc Server initLogger() " + XNDiscUtils.printStackTrace(e));
			throw e;
		}
	}

	/**
	 * 필터 초기화하기
	 * 
	 * @throws Exception
	 *             필터 초기화 에러
	 */
	private void initFilter() throws Exception {
		try {
			FilterIF filterEnc = (FilterIF) DynamicClassLoader.createInstance(FILTER_ENC);
			FilterIF filterComp = (FilterIF) DynamicClassLoader.createInstance(FILTER_COMP);
			XNDiscConfig.setFilterEncrypt(filterEnc);
			XNDiscConfig.setFilterCompress(filterComp);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(LoggerIF.LOG_SEVERE, "*** Fail - XNDisc Server initFilter() " + XNDiscUtils.printStackTrace(e));
			throw e;
		}
	}

	/**
	 * 스토리지(DB 처리) 초기화하기
	 * 
	 * @throws Exception
	 *             스토리지 초기화 에러
	 */
	private void initStorage() throws Exception {
		try {
			storage = (StorageIF) DynamicClassLoader.createInstance(MTS_STORAGE, logger);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(LoggerIF.LOG_SEVERE, "*** Fail - XNDisc Server initStorage() " + XNDiscUtils.printStackTrace(e));
			throw e;
		}
	}

	/**
	 * XNDisc Server 기동 메시지 출력
	 * 
	 * @return 기동 메시지
	 */
	private String getXNDiscServerStartMessage() {
		StringBuilder smsg = new StringBuilder(LINE_SEPERATOR);
		smsg.append("┌").append(StringUtils.rightPad("", 90, "-")).append("┐").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("XNDisc Server (Version : " + XNDISC_SERVER_VERSION + ")", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("Listening on " + this.server.getLocalAddress().getHostName() + "(" + this.server.getLocalAddress().getHostAddress() + "):" + this.server.getLocalPort(), 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("Start on " + XNDiscUtils.getStartDate(), 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("├").append(StringUtils.center(" XNDisc Server Options ", 90, "-")).append("┤").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Use SSL : " + Boolean.toString(USE_SSL), 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Dispatcher Initial Size : " + DISPATCHER_INIT_COUNT, 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Dispatcher maximum Handle Size : " + DISPATCHER_MAX_HANDLES, 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Read Buffer Use Direct : " + READ_BUFFER_USEDIRECT, 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Worker Pool Thread Priority : " + XNDiscConfig.getString(XNDiscConfig.WORKER_POOL_PRIORITY, "NORM"), 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Worker Pool minimun Size : " + MIN_SIZE_WORKER_POOL, 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       Worker Pool maximum Size : " + SIZE_WORKER_POOL, 90, " ")).append("│").append(LINE_SEPERATOR);
		if (WORKER_POOL_TYPE.equals("F")) {
			smsg.append("│").append(StringUtils.rightPad("       Thread Pool Type : Fixed", 90, " ")).append("│").append(LINE_SEPERATOR);
			smsg.append("│").append(StringUtils.rightPad("       Thread Pool Size : " + WORKER_THREAD_POOL, 90, " ")).append("│").append(LINE_SEPERATOR);
		} else if (WORKER_POOL_TYPE.equals("C")) {
			smsg.append("│").append(StringUtils.rightPad("       Thread Pool Type : Cached", 90, " ")).append("│").append(LINE_SEPERATOR);
		}
		smsg.append("│").append(StringUtils.center("", 90, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("├").append(StringUtils.rightPad("", 90, "-")).append("┤").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       OS Architecture   : " + System.getProperty("os.arch"), 90, "")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       OS Name           : " + System.getProperty("os.name"), 90, "")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("       OS Version        : " + System.getProperty("os.version"), 90, "")).append("│").append(LINE_SEPERATOR);
		Runtime runtime = Runtime.getRuntime();
		smsg.append("│").append(StringUtils.rightPad("       Processors        : " + runtime.availableProcessors() + "(s)", 90, "")).append("│").append(LINE_SEPERATOR);
		smsg.append("└").append(StringUtils.rightPad("", 90, "-")).append("┘").append(LINE_SEPERATOR);
		return smsg.toString();
	}

	/**
	 * Console clear 하기
	 */
	private static void clearConsoleOutput() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String ostype = (os.contains("windows")) ? "W" : "U";
			if (ostype.equals("W")) {
				CommandLine cmdLine = CommandLine.parse("cls");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.printf("%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n", new Object[0]);
			} else {
				CommandLine cmdLine = CommandLine.parse("clear");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.print(CLEAR_TERMINAL_ANSI_CMD);
				System.out.flush();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * XNDisc Server 가 기동 중인지 여부 확인
	 * 
	 * @return 기동중이면 true, 아니면 false
	 */
	private static boolean isXNDiscServerAlive() {
		boolean isAlive = false;

		String HOST = XNDiscConfig.getString(XNDiscConfig.HOST, XNDiscConfig.LOCAL_HOST);
		String PORT = XNDiscConfig.getString(XNDiscConfig.PORT);

		Scanner scanner = null;
		ByteArrayOutputStream baos = null;
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String ostype = (os.contains("windows")) ? "W" : "U";
			CommandLine cmdline = new CommandLine("netstat");
			cmdline.addArgument("-an");
			if (ostype.equals("W")) {
				cmdline.addArgument("-p");
				cmdline.addArgument("\"TCP\"");
			} else { // UNIX 계열은 기본적으로 -an만으로 확인하면 되나 범위를 좁히기 위해 추가
				if (XNDiscUtils.isSolaris()) {
					cmdline.addArgument("-P");
					cmdline.addArgument("tcp");
				} else if (XNDiscUtils.isAix()) {
					cmdline.addArgument("-p");
					cmdline.addArgument("TCP");
				}
			}
			DefaultExecutor executor = new DefaultExecutor();
			executor.setExitValue(0);
			baos = new ByteArrayOutputStream();
			PumpStreamHandler sh = new PumpStreamHandler(baos);
			executor.setStreamHandler(sh);
			executor.execute(cmdline);
			String str = baos.toString();
			if (str != null && str.length() > 0) { // 값이 없으면 XNDisc alive 상태아님(XNDisc Server 기동 가능상태)
				scanner = new Scanner(str);
				while (scanner.hasNextLine()) {
					String readline = scanner.nextLine();
					if (readline.contains(HOST) && readline.contains(PORT) && readline.contains(XNDISC_LISTEN_STATUS)) {
						isAlive = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isAlive = false;
		} finally {
			try {
				if (scanner != null) {
					scanner.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isAlive;
	}

	public static void main(String[] args) throws Exception {

		if (XNDiscServer.isXNDiscServerAlive()) {
			XNDiscServer.clearConsoleOutput();
			System.out.println("*** XNDisc Server is already started, this XNDisc Server stop and restart or just use the current XNDisc Server !!!");
			System.exit(-1);
		}

		XNDiscServer.clearConsoleOutput();
		final XNDiscServer xnds = new XNDiscServer();
		try {
			xnds.start();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println("*** XNDisc Server Stopping.");
						xnds.stop();
					} catch (Exception ignored) {
					}
				}
			}));			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
