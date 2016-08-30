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
package com.sds.acube.ndisc.xmigration.db;

import java.sql.Connection;
import java.sql.DriverManager;

import com.sds.acube.ndisc.xmigration.util.XNDiscMigConfig;

/**
 * Migration DB 정보 유틸리티
 * 
 * @author Takkies
 *
 */
public class XNDiscMigDBUtil {

	/**
	 * Migration 처리 DB Connection 생성
	 * 
	 * @return Migration 처리 DB Connection 정보
	 */
	public static XNDiscMigConnDB create() {
		return XNDiscMigConnDB.create(getConnection(false));
	}
	
	/**
	 * Migration 처리 DB Connection 생성
	 * 
	 * @param autocommit DB AUTO COMMIT 여부
	 * @return Migration 처리 DB Connection 정보
	 */
	public static XNDiscMigConnDB create(boolean autocommit) {
		return XNDiscMigConnDB.create(getConnection(autocommit));
	}
	
	/**
	 * Migration 처리 DB Connection 정보 얻어오기
	 * 
	 * @param autocommit DB AUTO COMMIT 여부
	 * @return Migration 처리 DB Connection 정보
	 */
	private static Connection getConnection(boolean autocommit) {
		try {
			String driver =  XNDiscMigConfig.getString("driver");
			String url = XNDiscMigConfig.getString("url");
			String user = XNDiscMigConfig.getString("user");
			String pwd = XNDiscMigConfig.getString("password");
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pwd);
			conn.setAutoCommit(autocommit);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
