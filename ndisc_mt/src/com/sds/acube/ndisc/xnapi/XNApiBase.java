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
package com.sds.acube.ndisc.xnapi;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xsocket.connection.BlockingConnectionPool;
import org.xsocket.connection.IBlockingConnection;

import com.sds.acube.cache.CacheConfig;
import com.sds.acube.cache.iface.ICache;

/**
 * XNApi 베이스 클래스
 * 
 * @author Takkies
 *
 */
public class XNApiBase {

	/* HOST 정보 */
	protected String HOST;
	
	/* PORT 정보 */
	protected int PORT;

	/* 캐시 사용 여부 */
	protected boolean useCache = false;
	
	/* SSL 사용 여부 */
	protected boolean useSSL = false;
	
	/* 디버그 여부 */
	protected boolean debug = false;
	
	/* 캐시 서비스 객체 */
	protected ICache cacheService;
	
	/* fail over 여부 */
	protected static boolean CON_FAILOVER_STATE;

	/* Multiplexing connection 객체 */
	//protected IMultiplexedConnection mxconnection;
	
	/* Blocking pipeline 객체 */
	//protected IBlockingPipeline connection;
	
	/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */
	protected IBlockingConnection connection;

	/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */
	protected BlockingConnectionPool pool;	
	
	/* XNApi 버전 정보 */
	private static final String XNAPI_VERSION ;
	
	/* logger 객체 */
	protected Logger logger = Logger.getLogger(this.getClass());
	
	/* OS 별 라인 피드 */
	protected static String LINE_SEPERATOR = System.getProperty("line.separator");
	
	static {
		XNAPI_VERSION = "XNApi " + XNApiUtils.getXNApiVersion() + "(" + XNApiUtils.getXNApiPublshingDate() + ")";
		StringBuilder smsg = new StringBuilder(LINE_SEPERATOR);
		smsg.append("┌").append(StringUtils.rightPad("", 60, "-")).append("┐").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Company        : SAMSUNG SDS", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Product Name   : ACUBE XNAPI", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Version        : " + XNAPI_VERSION, 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("└").append(StringUtils.rightPad("", 60, "-")).append("┘").append(LINE_SEPERATOR);
		System.out.println(smsg.toString());
	}

	/**
	 * 캐시 초기화
	 */
	protected void initCache() {
		this.cacheService = CacheConfig.getService();
	}
	
	/**
	 * 캐시 얻어오기
	 * 
	 * @param Id 캐시 아이디
	 * @return 캐시 경로
	 */
	protected String getCache(String Id) {
		String FileCachePath = null;
		try {
			FileCachePath = this.cacheService.get(Id).toString();
			if (FileCachePath == null || !(new File(FileCachePath)).canRead())
				FileCachePath = null;
		} catch (Exception ex) {
			return null;
		}
		return FileCachePath;
	}
	
	protected void destroyConnectionPool() {
		if (this.connection != null) {
			try {
				this.pool.destroy(connection);
			} catch (Exception e) {
			}
		}
	}
	
}
