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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

import com.sds.acube.ndisc.mts.util.cipher.jce.KeyFileGenerator;

/**
 * XNDisc 암복호화 처리
 * 
 * @author Takkies
 *
 */
public class XNDiscCipher {

	private static Key key;

	static {
		try {
			key = KeyFileGenerator.readKeyFile(XNDiscConfig.getString(XNDiscConfig.NDISC_CIPHER_KEY));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 암호화
	 * 
	 * @param data 평문 문자열
	 * @return 암호화된 문자열
	 */
	public static String encode(String data) {
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte e[] = cipher.doFinal(data.getBytes("UTF-8"));
			return replaceChar(Base64.encodeBase64String(e));
		} catch (NoSuchAlgorithmException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (NoSuchPaddingException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (InvalidKeyException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (IllegalBlockSizeException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (BadPaddingException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (UnsupportedEncodingException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		}
		return null;
	}

	/**
	 * 복호화
	 * 
	 * @param data 암호화된 문자열
	 * @return 복호화된 문자열
	 */
	public static String decode(String data) {
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte d[] = cipher.doFinal(Base64.decodeBase64(data.getBytes("UTF-8")));
			return replaceChar(new String(d));
		} catch (NoSuchAlgorithmException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (NoSuchPaddingException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (IOException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (InvalidKeyException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (IllegalBlockSizeException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		} catch (BadPaddingException e) {
			System.err.print(XNDiscUtils.printStackTrace(e));
		}
		return null;
	}

	/**
	 * 허용되지 않는 문자열 제거
	 * 
	 * @param string 문자열
	 * @return 허용되지 않은 문자열 제거된 문자열
	 */
	private static String replaceChar(String string) {
		string = string.replace('/', '_');
		string = string.replace('\\', '_');
		return string;
	}

}
