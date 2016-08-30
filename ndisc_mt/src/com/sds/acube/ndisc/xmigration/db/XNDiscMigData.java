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

import java.util.HashMap;

/**
 * Migration 정보 저장 객체
 * 
 * @author Takkies
 *
 */
public class XNDiscMigData extends HashMap<String, String> {

	private static final long serialVersionUID = 266118220764452840L;

	public String put(String key, String value) {
		if (key == null) {
			System.err.print("Key of Data is NULL !");
			return (String) super.put(key, value);
		}
		if (value != null)
			super.put(key, value);
		return value;
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public String getString(String key, String nullValue) {
		String value = getString(key);
		return (value == null) ? nullValue : value;
	}

	public String getSubstring(String key, int start, int end, String nullValue) {
		String value = getString(key);
		if (value == null || value.length() < start)
			return nullValue;
		if (value.length() < end)
			value.substring(start);
		return value.substring(start, end);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int nullValue) {
		String value = getString(key);
		if (value == null)
			return nullValue;
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return nullValue;
	}
	
}
