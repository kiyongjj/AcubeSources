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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.xsocket.DataConverter;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.model.NFile;

/**
 * XNApi 유틸리티
 * 
 * @author Takkies
 * 
 */
public class XNApiUtils {

	/* ByteBuffer에 전송할 전체 사이즈(4096) */
	private static String RCV_BUFFER_STR = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

	/* XNApi 버전정보(version.txt) */
	private static String XNApi_PublishingVersion;

	/* XNApi 변경날짜(version.txt) */
	private static String XNApi_PublishingDate;

	/**
	 * ByteBuffer에 전송할 사이즈를 만들기 위해 padding
	 * 
	 * @param data
	 *            전송할 문자열
	 * @param size
	 *            전송할 사이즈
	 * @return padding 된 문자열
	 */
	public static String getFormatString(String data, int size) {
		if (data.length() > size) {
			data = data.substring(0, size);
		} else {
			data = data.concat(RCV_BUFFER_STR.substring(data.length()));
		}
		return data;
	}

	/**
	 * 파일명 가져오기
	 * 
	 * @param strFilePath
	 *            파일 경로
	 * @return 파일명
	 */
	public static String getNameFormatString(String strFilePath) {
		int nPos = strFilePath.lastIndexOf(File.separator);
		return strFilePath.substring(nPos + 1, strFilePath.length());
	}

	/**
	 * 상태값 문자열 변환하기
	 * 
	 * @param status
	 *            상태값
	 * @return 변환된 상태값
	 */
	public static String getStatus(int status) {
		String strstatus = Integer.toString(status);
		return StringUtils.leftPad(strstatus, 5, "0");
	}

	/**
	 * 수신한 메시지 정보 취득하기
	 * 
	 * @param rcvmsg
	 *            수신한 메시지 문자열
	 * @return 메시지 정보
	 */
	public static String getReplyMessage(String rcvmsg) {
		try {
			StringTokenizer sTK = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
			rcvmsg = sTK.nextToken().trim();
			if (XNApiConfig.ERROR.equals(rcvmsg)) {
				throw new NDiscException(sTK.nextToken().trim());
			}
		} catch (Exception e) {
		}
		return rcvmsg;
	}

	/**
	 * 수신 메시지에서 파일아이디 정보 배열 얻어오기
	 * 
	 * @param rcvmsg
	 *            수신한 메시지 문자열
	 * @param count
	 *            파일 개수
	 * @return 파일 아이디 정보 배열
	 */
	public static String[] getFileIds(String rcvmsg, int count) {
		String fileID[] = null;
		try {
			StringTokenizer sTK = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
			if (XNApiConfig.ERROR.equals(sTK.nextToken())) {
				throw new NDiscException(sTK.nextToken().trim());
			}
			fileID = new String[count];
			for (int i = 0; i < count; i++) {
				fileID[i] = sTK.nextToken().trim();
			}
		} catch (Exception e) {
		}
		return fileID;
	}

	/**
	 * 등록을 위한 파일 정보 세팅 및 생성하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 파일 정보 세팅 및 생성 결과 NFile 정보 배열
	 * @throws Exception
	 */
	public static NFile[] makeRegInfo(NFile[] nFile) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {
				File file = new File(nFile[i].getName());
				if (!file.exists()) {
					throw new FileException("file not found : " + nFile[i].getName());
				}
				nFile[i].setSize((int) file.length());
				if (null == nFile[i].getId() || "".equals(nFile[i].getId())) {
					nFile[i].setId(XNApiConfig.NDISC_NA_RESERV);
				}
				if (null == nFile[i].getCreatedDate() || "".equals(nFile[i].getCreatedDate())) {
					nFile[i].setCreatedDate(XNApiConfig.NDISC_NA_RESERV);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
		}
		return nFile;
	}

	/**
	 * ByteBuffer 에서 데이터 문자열로 변환하여 가져오기
	 * 
	 * @param buffer
	 *            수신된 ByteBuffer
	 * @return 변환된 데이터 문자열
	 */
	public static String getData(ByteBuffer buffer) {
		String encoding = System.getProperty("file.encoding");
		encoding = (encoding == null || encoding.equals("")) ? "UTF-8" : encoding;
		try {
			return DataConverter.toString(buffer, encoding);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 에러 추척하기
	 * 
	 * @param e
	 *            에러 객체
	 * @return 에러 내용
	 */
	public static String printStackTrace(Exception e) {
		StringBuilder str = new StringBuilder();
		str.append(e + "\r\n");
		str.append("-----------------------------------------\r\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			if (trace[i].getLineNumber() == -1)
				continue;
			str.append(trace[i] + "\r\n");
		}
		return str.toString();
	}

	public static String getXNApiVersion() {
		if (XNApi_PublishingVersion == null) {
			readVersionFromFile();
		}
		return XNApi_PublishingVersion;
	}

	public static String getXNApiPublshingDate() {
		if (XNApi_PublishingDate == null) {
			readVersionFromFile();
		}
		return XNApi_PublishingDate;
	}
	
	private static void readVersionFromFile() {
		XNApi_PublishingVersion = "<unknown>";
		XNApi_PublishingDate = "<unknown>";
		InputStreamReader isr = null;
		LineNumberReader lnr = null;
		try {
			isr = new InputStreamReader(XNApiUtils.class.getResourceAsStream("/com/sds/acube/ndisc/xnapi/version.txt"));
			if (isr != null) {
				lnr = new LineNumberReader(isr);
				String line = null;
				do {
					line = lnr.readLine();
					if (line != null) {
						if (line.startsWith("Publishing-Version=")) {
							XNApi_PublishingVersion = line.substring("Publishing-Version=".length(), line.length()).trim();
						} else if (line.startsWith("Publishing-Date=")) {
							XNApi_PublishingDate = line.substring("Publishing-Date=".length(), line.length()).trim();
						}
					}
				} while (line != null);
				lnr.close();
			}
		} catch (IOException ioe) {
			XNApi_PublishingVersion = "<unknown>";
			XNApi_PublishingDate = "<unknown>";
		} finally {
			try {
				if (lnr != null) {
					lnr.close();
				}
				if (isr != null) {
					isr.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

}
