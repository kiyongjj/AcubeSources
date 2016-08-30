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
package com.sds.acube.ndisc.xadmin;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;
import com.sds.acube.ndisc.xnapi.XNApi;

/**
 * XNDisc Admin 기본
 * 
 * @author Takkies
 * 
 */
public class XNDiscAdminBase {

	/* Console 에 출력할 기본 컬럼 라인 수 */
	protected static final int PRINT_COLUMN_SIZE = 150;

	/* Console 에 리스트형으로 출력할 경우 최대 출력 개수 */
	protected static final int MAX_LIST_SIZE = 11;

	/* XNapi 객체 */
	protected XNApi xnapi = null;

	/* storage 객체 */
	protected StorageIF storage = null;

	/* logger 객체 */
	protected static LoggerIF logger = null;

	/* DB 관련 처리 클래스 정보 */
	private String MTS_STORAGE = XNDiscAdminConfig.getString(XNDiscAdminConfig.STORAGE);

	/* Log 관련 처리 클래스 정보 */
	private String MTS_LOGGER = XNDiscAdminConfig.getString(XNDiscAdminConfig.LOGGER);

	/* 로그를 출력할지 여부 */
	protected boolean printlog;

	/* out 객체 */
	protected PrintStream out;

	/* 로그 객체 */
	protected Logger log = Logger.getLogger(XNDiscAdminBase.class);

	/* OS 별 라인 피드 */
	protected String LINE_SEPERATOR = System.getProperty("line.separator");

	/**
	 * XNDisc Admin 생성자
	 * 
	 * @param printlog
	 *            로그 출력 여부
	 * @param out
	 *            out 객체
	 * @param log
	 *            로그 객체
	 */
	public XNDiscAdminBase(boolean printlog, PrintStream out, Logger log) {
		this.printlog = printlog;
		this.out = out;
		this.log = log;
		xnapi = new XNApi();
		try {
			if (null == logger) {
				logger = (LoggerIF) DynamicClassLoader.createInstance(MTS_LOGGER);
			}
			logger.initLogger();
			storage = (StorageIF) DynamicClassLoader.createInstance(MTS_STORAGE, logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * XNDisc Admin 생성자
	 */
	public XNDiscAdminBase() {
		this(false, System.out, Logger.getLogger(XNDiscAdminBase.class));
	}

	/**
	 * 현재 날짜 가져오기
	 * 
	 * @return 현재 날짜 문자열
	 */
	protected String getCreateDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * Console에 뿌릴 문자열이 컬럼길이 보다 클 경우 truncate
	 * 
	 * @param val
	 *            문자열
	 * @param size
	 *            컬럼 길이
	 * @return truncate 된 문자열
	 */
	protected String getName(String val, int size) {
		if (val.length() > (size - 3)) {
			val = val.substring(0, val.length()).concat("...");
		}
		return val;
	}
}
