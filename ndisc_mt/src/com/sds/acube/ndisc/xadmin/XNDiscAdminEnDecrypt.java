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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.acube.ndisc.mts.xserver.util.XNDiscCipher;

/**
 * XNDisc Admin 암복호화결과 보여주기
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminEnDecrypt extends XNDiscAdminBase {

	public XNDiscAdminEnDecrypt(boolean printlog, PrintStream out, Logger log) {
		super(printlog, out, log);
	}
	
	/**
	 * 암호화한 결과 뿌려주기
	 * 
	 * @param id 암호화할 문자열
	 */
	public void encrypt(String id) {
		StringBuilder encrypt = new StringBuilder(LINE_SEPERATOR);
		encrypt.append("┌").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┐").append(LINE_SEPERATOR);
		encrypt.append("│").append(StringUtils.center("ID Encryption", PRINT_COLUMN_SIZE, " "));
		encrypt.append("│").append(LINE_SEPERATOR);
		encrypt.append("├").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		encrypt.append("│").append(StringUtils.center(id + " => " + XNDiscCipher.encode(id), PRINT_COLUMN_SIZE, " "));
		encrypt.append("│").append(LINE_SEPERATOR);
		encrypt.append("└").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append( "┘").append(LINE_SEPERATOR);
		if (printlog) {
			log.info(encrypt.toString());
		} else {
			out.print(encrypt.toString());
		}
	}
	
	/**
	 * 복호화한 결과 뿌려주기
	 * 
	 * @param id 복호화할 문자열
	 */
	public void decrypt(String id) {
		StringBuilder decrypt = new StringBuilder(LINE_SEPERATOR);
		decrypt.append("┌").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┐").append(LINE_SEPERATOR);
		decrypt.append("│").append(StringUtils.center("ID Decryption", PRINT_COLUMN_SIZE, " "));
		decrypt.append("│").append(LINE_SEPERATOR);
		decrypt.append("├").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		decrypt.append("│").append(StringUtils.center(id + " => " + XNDiscCipher.decode(id), PRINT_COLUMN_SIZE, " "));
		decrypt.append("│").append(LINE_SEPERATOR);
		decrypt.append("└").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append( "┘").append(LINE_SEPERATOR);
		if (printlog) {
			log.info(decrypt.toString());
		} else {
			out.print(decrypt.toString());
		}
	}
}
