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
package com.sds.acube.ndisc.mts.xserver.factory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.sds.acube.ndisc.mts.xserver.util.XNDiscCipher;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * keytool을 이용하여 인증 파일 생성<br>
 * <br>
 * 생성방법<br>
 * 1) Server 용 파일 생성(config폴더에 복사)<br>
 * --- a) keytool -genkey -keystore xndisc-keyfile.jks -alias xndisc-server<br>
 * --- b) keytool -export -keystore xndisc-keyfile.jks -alias xndisc-server -file xndisc-keyfile.cer<br>
 * <br>
 * 2) Client 용 파일 생성(XNapi 실행 디렉토리에 복사)<br>
 * --- 첫번째 방법)<br>
 * ----- 서버용 파일(xndisc-keyfile.jks)를 rename(xnapi-keyfile.jks)하여 XNApi 의 디렉토리에 복사<br>
 * --- 두번째 방법)<br>
 * ----- a) keytool -genkey -keystore xnapi-keyfile.jks -alias xndisc-client<br>
 * ----- b) keytool -export -keystore xnapi-keyfile.jks -alias xndisc-client -file xnapi-keyfile.cer<br>
 * <br>
 * 생성시 비밀번호는 혼동을 방지하기 위해 동일한 비밀번호로 설정함.<br>
 * 만약 비밀번호를 다르게 할 경우 KeyStore와 KeyManager 의 비밀번호를 개별적으로 설정해야함.<br>
 * <br>
 * 
 * @author Takkies
 * 
 */
public final class XNDiscSSLContextFactory {

	/**
	 * 기본생성자를 호출하지 못하도록 처리
	 */
	private XNDiscSSLContextFactory() {
	}

	/**
	 * XNDisc SSL Context 얻어오기
	 * 
	 * @return SSL Context
	 */
	public static SSLContext getSSLContext() {
		try {
			String serverkeyfile = getKeyFile(XNDiscConfig.getString(XNDiscConfig.SSL_SERVER_KEY_FILE, "xndisc-keyfile.jks"));
			String ps = XNDiscCipher.decode(XNDiscConfig.getString(XNDiscConfig.SSL_PASSPHRASE, "hXbkJKL+WK8="));
			char passphrase[] = ps.toCharArray();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(serverkeyfile), passphrase);
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
	 * Key 파일 정보 얻어오기<br>
	 * 지정한 경로에 없을 경우 CLASS PATH상에 있는지 한번 더 확인
	 * 
	 * @param keyfile 키 파일 명
	 * @return 키파일 경로
	 */
	private static String getKeyFile(String keyfile) {
		try {
			File f = new File(keyfile);
			if (!f.exists()) {
				String keyfilename = keyfile;
				if (keyfile.indexOf(File.separator) != -1) {
					keyfilename = keyfile.substring(keyfile.lastIndexOf(File.separator) + 1);
				}
				URL url = XNDiscSSLContextFactory.class.getClassLoader().getResource(keyfilename);
				return url.getPath();
			}
		} catch (Exception e) {
			System.err.println(XNDiscUtils.printStackTrace(e));
		}
		return keyfile;
	}
}
