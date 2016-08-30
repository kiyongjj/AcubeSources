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
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * XNApi SSL 생성 클래스 WAS 기동 시 환경설정에 반드시 설정되어 있어야 XNApi 사용이 정상적으로 가능함.<br>
 * 
 * <code>
 * java -Djstor_api_type=xndisc -Dxnapi_ssl=true -Dxnapi_keyfile=xnapi-keyfile.jks -Dxnapi_keypwd=7eau4E0t1U0= -classpath ........
 * </code>
 * 
 * @author Takkies
 * 
 */
public final class XNApiSSLUtils {

	/* XNApi SSL 사용여부 */
	public static final String XNAPI_USE_SSL_PROP = "xnapi_ssl";

	/* XNApi SSL 키 파일 위치(전체 경로 이거나 클래스 패스 상의 파일명) */
	public static final String XNAPI_KEYFILE_PROP = "xnapi_keyfile";

	/* XNApi SSL 암호(keytool 생성시 지정한 암호) */
	public static final String XNAPI_KEYPWD_PROP = "xnapi_keypwd";

	private XNApiSSLUtils() {
	}

	/**
	 * keytool을 이용하여 생성된 파일을 XNApi의 실행 디렉토리에 복사<br>
	 * XNApi는 DesCipher tool을 이용하여 encrypt, decrypt<br>
	 * 서버 실행 시 관련 환경 설정이 필요함.<br>
	 * <br>
	 * jstor_api_type 의 값은 xndisc<br>
	 * xnapi_ssl 의 값은 true<br>
	 * xnapi_keyfile 의 값은 키파일의 전체 경로(xnapi-keyfile.jks) xnapi_keypwd 의 값은 키파일 생성시 지정한 비밀번호, XNApiDesCipher로 암호화한 비밀번호 (7eau4E0t1U0=)
	 * 
	 * @return
	 */
	public static SSLContext getSSLContext() {
		try {
			String clientkeyfile = getKeyFile(System.getProperty(XNAPI_KEYFILE_PROP, "xnapi-keyfile.jks"));
			String ps = XNApiDesCipher.decrypt(System.getProperty(XNAPI_KEYPWD_PROP, "7eau4E0t1U0="));
			char passphrase[] = ps.toCharArray();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(clientkeyfile), passphrase);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, passphrase);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);

			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			return sslcontext;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 키 파일 얻어오기
	 * 
	 * @param keyfile
	 *            키파일명
	 * @return 키파일 전체 경로
	 */
	private static String getKeyFile(String keyfile) {
		try {
			File f = new File(keyfile);
			if (!f.exists()) {
				String keyfilename = keyfile;
				if (keyfile.indexOf(File.separator) != -1) {
					keyfilename = keyfile.substring(keyfile.lastIndexOf(File.separator) + 1);
				}
				URL url = XNApiSSLUtils.class.getClassLoader().getResource(keyfilename);
				return url.getPath();
			}
		} catch (Exception e) {
			System.err.println(XNApiUtils.printStackTrace(e));
		}
		return keyfile;
	}

}
