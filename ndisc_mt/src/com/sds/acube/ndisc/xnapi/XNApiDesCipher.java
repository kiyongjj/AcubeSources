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

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * XNApi 용 암복호화(DES3 방식)
 * 
 * @author Takkies
 *
 */
public class XNApiDesCipher {

	/**
	 * 복호화하기
	 * 
	 * @param data 복호화할 문자열
	 * @return 복호화한 문자열
	 */
	public static String decrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getKey());
			byte d[] = cipher.doFinal(Base64.decodeBase64(data.getBytes()));
			return new String(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 암호화하기
	 * 
	 * @param data 암호화할 문자열
	 * @return 암호화한 문자열
	 */
	public static String encrypt(String data) {
		try {
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getKey());
			byte e[] = cipher.doFinal(data.getBytes());
			return Base64.encodeBase64String(e);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 암호화 키정보 취득하기<br>
	 * 3DES 방식이므로 24bit로 KEY를 생성<br>
	 * DES 방식일 경우 16bit로 KEY 생성해야함.<br>
	 * 
	 * @return 키정보
	 */
	private static Key getKey() {
		String key = "x0134-ad17s658601j56-q75k2we0des-key".substring(0, 24); // 동일값 처리 위해 고정함.
		try {
			DESedeKeySpec desKeySpec = new DESedeKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			return keyFactory.generateSecret(desKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String args[]) {
		String data = "sds000";
		
		String enc = encrypt(data);
		System.out.println("encrypt : " + enc);
		String dec = decrypt(enc);
		System.out.println("decrypt : " + dec);
	}	
}
