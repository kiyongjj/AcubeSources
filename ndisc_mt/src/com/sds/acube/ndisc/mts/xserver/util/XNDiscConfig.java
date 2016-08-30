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
package com.sds.acube.ndisc.mts.xserver.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;

/**
 * XNDisc 환경 설정 및 세팅값 처리
 * 
 * @author Takkies
 *
 */
public class XNDiscConfig {

	/* 환경 설정 객체 */
	public static final Configuration configuration = ConfigurationManager.getConfiguration();

	/* 주고 받을 초기 버퍼 사이즈 */
	public static final int INIT_BUFFER_SIZE = 4096;

	/* 주고 받을 발송 버퍼 사이즈 */
	public static final int REPLY_BUFFER_SIZE = 4096;
	
	public static final int FILE_TRANS_BUFFER_SIZE = 4096 * 8;   // 32768 byte (32k)

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
	
	/* 기본 port 정보 */
	public static final int LOCAL_PORT = 7404;

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

	/* XNDisc Server 의 Worker Pool Size property */
	public static final String SIZE_WORKER_POOL = "workerpool_size";

	/* XNDisc Server 의 Worker Pool minimum Size property */
	public static final String MIN_SIZE_WORKER_POOL = "workerpool_min_size";

	/* XNDisc Server 의 Worker Pool 쓰레드 개수 property */
	public static final String WORKER_POOL_THREAD_COUNT = "workerpool_thread_count";

	/* XNDisc Server 의 Worker Pool 타입 property */
	public static final String WORKER_POOL_TYPE = "workerpool_type";

	/* XNDisc Server 의 Worker Pool 쓰레드 우선순위 property */
	public static final String WORKER_POOL_PRIORITY = "workpool_thread_priority";

	/* XNDisc Server 의 디스패쳐 초기 개수 property */
	public static final String DISPATCHER_INIT_COUNT = "dispatcher_initial_count";

	/* XNDisc Server 의 디스패쳐 최대 개수 property */
	public static final String DISPATCHER_MAX_HANDLES = "dispatcher_max_handles";

	/* XNDisc Server 의 버퍼 다이렉트 옵션 property */
	public static final String READ_BUFFER_USEDIRECT = "readbuffer_usedirect";

	/* XNDisc Server의 암호화 키 property */
	public static final String NDISC_CIPHER_KEY = "cipher_key";

	/* XNDisc Server의 라이센스 키 property */
	public static final String NDISC_LICENSE_KEY = "license_key";

	/* XNDisc Server fail over 환경설정 카테고리 */
	public static final String NDISC_FAILOVER_CATEGORY = "connection_failover";

	/* XNDisc Server fail over 적용 여부 property */
	public static final String NDISC_FAILOVER_APPLY = "con_failover_apply";

	/* XNDisc Server fail over 타겟 서버 정보(HOST:PORT;HOST1:PORT1...) property */
	public static final String NDISC_FAILOVER_HOSTS = "con_failover_target";
	
	/* FixedThreadPool 옵션일 경우 가용한 Processor의 수에 지정된 쓰레드 수를 곱하여 쓰레드수를 산출 */
	public static final String MULTIPLICATION_THREAD_AVAILABLE_PROCESSORS = "multiplication_thread_available_processors";
	
	/* 액션이 없을 경우 detach 옵션 property */
	public static final String DETACH_HANDLE_NO_OPERATION = "detach_handle_no_operation";
	
	/* 전송 시 전송 바이트 버퍼의 최대 사이즈 지정 property */
	public static final String TRANSFER_MAPPED_BYTE_BUFFER_MAXSIZE = "transfer_mapped_bytebuffer_maxsize";

	/* XNDisc Server SSL 옵션 설정 property */
	public static final String USE_SSL = "use_ssl";
	
	/* XNDisc Server SSL key file 전체 경로(혹은 (클래스 패스상에 있을 경우)파일명) property */
	public static final String SSL_SERVER_KEY_FILE = "ssl_server_key_file";
	
	/* XNDisc Server SSL 암호 property */
	public static final String SSL_PASSPHRASE = "ssl_passphrase";
	
	public static final String XSOCKET_DEBUG = "xsocket_debug";
	
	public static final String TRANSFER_TYPE = "transfer_type";
	
	/* 암호화 필터 객체 */
	private static FilterIF filterEncrypt;

	/* 압축 필터 객체 */
	private static FilterIF filterCompress;

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

	/**
	 * 암호화 필터 생성
	 * 
	 * @return 암호화 필터
	 */
	public static FilterIF getFilterEncrypt() {
		if (filterEncrypt == null) {
			try {
				filterEncrypt = (FilterIF) DynamicClassLoader.createInstance(config.get(ENCRYPT));
			} catch (Exception e) {
			}
		}
		return filterEncrypt;
	}

	/**
	 * 암호화 필터 가져오기
	 * 
	 * @param filterEncrypt 암호화 필터
	 */
	public static void setFilterEncrypt(FilterIF filterEncrypt) {
		XNDiscConfig.filterEncrypt = filterEncrypt;
	}

	/**
	 * 압축 필터 생성
	 * @return 압축 필터
	 */
	public static FilterIF getFilterCompress() {
		if (filterCompress == null) {
			try {
				filterCompress = (FilterIF) DynamicClassLoader.createInstance(config.get(COMPRESS));
			} catch (Exception e) {
			}
		}
		return filterCompress;
	}

	/**
	 * 압축 필터 가져오기
	 * 
	 * @param filterCompress 압축 필터
	 */
	public static void setFilterCompress(FilterIF filterCompress) {
		XNDiscConfig.filterCompress = filterCompress;
	}

}
