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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 * XNDisc Admin 환경
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminConfig {

	/* 환경 설정 객체 */
	public static final Configuration configuration = ConfigurationManager.getConfiguration();

	/* 주고 받을 초기 버퍼 사이즈 */
	public static final int INIT_BUFFER_SIZE = 4096;

	/* 주고 받을 발송 버퍼 사이즈 */
	public static final int REPLY_BUFFER_SIZE = 4096;

	/* 송수신 데이터에서 정보를 취득하기 위한 구분자 */
	public static final String DELIM_STR = "|";

	/* 에러 없음(송수신 데이터에 같이 전송) */
	public static final String NO_ERROR = "0";

	/* 에러 발생(송수신 데이터에 같이 전송) */
	public static final String ERROR = "-1";

	/* 등록 시 storage 경로 옵션 */
	public static final String STORAGE_PATH_REGIST = "PATH_REGIST";

	/* 다운로드 등 정보 취득 시 경로 옵션 */
	public static final String STORAGE_PATH_ACCESS = "PATH_ACCESS";

	/* 압축, 암호화 옵션 없음 */
	public static final String STAT_NONE = "0";

	/* 압축 옵션 */
	public static final String STAT_COMP = "1";

	/* 암호화 옵션 */
	public static final String STAT_ENC = "2";

	/* 압축+암호화 옵션 */
	public static final String STAT_COMP_ENC = "3";

	/* 옵션 정보(DRM 등에 사용) */
	public static final String STAT_AUTO = "-1";

	/* 송수신 시 아무 정보도 넣지 않을 경우 처리자 */
	public static final String NDISC_NA_RESERV = "@@NOT_ASSIGN@@";

	/* 환경 설정 파일(config.xml)의 XNDisc Server host property */
	public static final String HOST = "host";

	/* 기본 host 정보 */
	public static final String LOCAL_HOST = "127.0.0.1";

	/* 환경 설정 파일(config.xml)의 XNDisc Server port property */
	public static final String PORT = "port";

	/* 환경 설정 파일(config.xml)의 storage 클래스명 property */
	public static final String STORAGE = "impl_class_storage";

	/* 환경 설정 파일(config.xml)의 logger 클래스명 property */
	public static final String LOGGER = "impl_class_logger";

	/* 환경 설정 파일(config.xml)의 압축 클래스명 property */
	public static final String COMPRESS = "impl_class_comp";

	/* 환경 설정 파일(config.xml)의 암호화 클래스명 property */
	public static final String ENCRYPT = "impl_class_enc";

	/* 환경 설정 파일(config.xml)의 임시 디렉토리 property */
	public static final String TEMP_DIR = "tmp_dir";

	/* XNDisc Server의 암호화 키 property */
	public static final String NDISC_CIPHER_KEY = "cipher_key";

	/* XNDisc Server의 라이센스 키 property */
	public static final String NDISC_LICENSE_KEY = "license_key";
	
	/* 환경 변수 저장 객체 */
	private static HashMap<String, String> config;

	static {
		config = new HashMap<String, String>();
		reset();
	}

	/**
	 * 환경 변수 리셋
	 */
	private static void reset() {
		try {
			if (config != null) {
				config.clear();
			}
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 환경 변수 초기화
	 */
	private static void init() {
		Properties props = null;
		String[] categoryNames = configuration.getCategoryNames();
		for (String catgnm : categoryNames) {
			props = configuration.getProperties(catgnm);
			Enumeration<?> enumeration = props.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				String value = props.getProperty(key);
				config.put(key, replaceValueByVariables(value));
			}
		}
	}

	/**
	 * 환경변수를 variable에 의해서 value값 변경하여 가져오기
	 * 
	 * @param value 변경할 문자열
	 * @return 변경된 문자열
	 */
	@SuppressWarnings("unchecked")
	private static String replaceValueByVariables(String value) {
		HashMap<String, String> variables = configuration.getVariables();
		Iterator<String> iterator = variables.keySet().iterator();
		String key = "";
		String keyval = "";
		String repkeyval = "";
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			keyval = "${".concat(key).concat("}");
			repkeyval = "\\$\\{".concat(key).concat("\\}");
			if (value.indexOf(keyval) != -1) {
				value = value.replaceAll(repkeyval, variables.get(key));
				break;
			}
		}
		return value;
	}

	/**
	 * 환경변수를 배열로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @param category 환경변수 카테고리
	 * @return 환경변수 배열
	 */
	public static String[] getArray(String key, String category) {
		String vals[] = configuration.getArray(key, new String[] {}, category);
		if (vals == null || vals.length < 0) {
			return new String[] {};
		}
		return vals;
	}

	/**
	 * 환경변수 문자열로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @return 환경변수 문자열
	 */
	public static String getString(String key) {
		return getString(key, null);
	}

	/**
	 * 환경변수 문자열로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @param defval 환경변수 기본값(값이 없을 경우)
	 * @return 환경변수 문자열
	 */
	public static String getString(String key, String defval) {
		String val = config.get(key);
		if (StringUtils.isEmpty(val)) {
			return defval;
		} else {
			return val;
		}
	}

	/**
	 * 환경변수 숫자형(INTEGER)으로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @return 환경변수 숫자값(INTEGER)
	 */
	public static int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * 환경변수 숫자형(INTEGER)으로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @param defval 환경변수 기본값(값이 없을 경우)
	 * @return 환경변수 숫자값(INTEGER)
	 */
	public static int getInt(String key, int defval) {
		String val = config.get(key);
		if (StringUtils.isEmpty(val)) {
			return defval;
		} else {
			return Integer.parseInt(val);
		}
	}

	/**
	 * 환경변수 숫자형(LONG)으로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @return 환경변수 숫자값(LONG)
	 */
	public static long getLong(String key) {
		return getLong(key, 0);
	}

	/**
	 * 환경변수 숫자형(LONG)으로 가져오기
	 * 
	 * @param key 환경변수 키값
	 * @param defval 환경변수 기본값(값이 없을 경우)
	 * @return 환경변수 숫자값(LONG)
	 */
	public static long getLong(String key, long defval) {
		String val = config.get(key);
		if (StringUtils.isEmpty(val)) {
			return defval;
		} else {
			return Long.parseLong(val);
		}
	}

	/**
	 * 환경변수 참거짓(Boolean)으로 가져오기<br>
	 * 참이 될수 있는 값(1, true, Y)에 대해서 체크함.
	 * 
	 * @param key 환경변수 키값
	 * @return 환경변수 참거짓값
	 */
	public static boolean getBoolean(String key) {
		String val = getString(key, "0");
		return (val.equals("1") || val.equalsIgnoreCase("true") || val.equalsIgnoreCase("Y"));
	}

}
