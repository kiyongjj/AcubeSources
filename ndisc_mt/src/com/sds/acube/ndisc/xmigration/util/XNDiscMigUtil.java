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
package com.sds.acube.ndisc.xmigration.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

/**
 * 
 * @author Takkies
 *
 */
public class XNDiscMigUtil {

	/* Console 삭제하기 위한 표준 Command */
	private static final String CLEAR_TERMINAL_ANSI_CMD = new String(new byte[] { 27, 91, 50, 74, 27, 91, 72 });
	
	/* OS 별 라인 피드 */
	public static String LINE_SEPERATOR = System.getProperty("line.separator");

	/* XMigration 배포 버전정보(version.txt) */
	private static String XMigration_PublishingVersion;

	/* XMigration 배포 날짜(version.txt) */
	private static String XMigration_PublishingDate;
	
	/**
	 * 문자열 null 처리하기
	 * 
	 * @param val 문자열
	 * @param def null일 경우 기본값 지정
	 * @return null 처리된 문자열
	 */
	public static String getString(String val, String def) {
		if (val == null) {
			return def;
		}
		return val;
	}

	/**
	 * 문자열 null 처리하기
	 * 
	 * @param val 문자열
	 * @return null 처리된 문자열
	 */
	public static String getString(String val) {
		return getString(val, "");
	}

	/**
	 * 물리적인 임시파일 저장위치 가져오기<br>
	 * 동일 파일명으로 인한 overwrite 방지하기 위해 파일아이디를 이용하여 디렉토리 생성<br>
	 * 
	 * @param fileid 저장서버 파일 아이디
	 * @param filename 저장서버 파일명
	 * @return 파일 경로
	 */
	public static String getFilePath(String fileid, String filename) {
		String tmpDir = XNDiscMigConfig.getString("tmp-dir", null);
		tmpDir = (tmpDir.indexOf("/") >= 0) ? tmpDir + "/" + fileid : tmpDir + File.separator + fileid;
		File d = new File(tmpDir);
		if (!d.isDirectory()) {
			d.mkdirs();
		}
		return (tmpDir.indexOf("/") >= 0) ? tmpDir + "/" + filename : tmpDir + File.separator + filename;
	}

	/**
	 * 생성된 임시파일 및 디렉토리 삭제하기
	 * 
	 * @param fileid 파일 아이디
	 */
	public static void deleteTmpFiles(String fileid) {
		String tmpDir = XNDiscMigConfig.getString("tmp-dir", null);
		tmpDir = (tmpDir.indexOf("/") >= 0) ? tmpDir + "/" + fileid : tmpDir + File.separator + fileid;
		File d = new File(tmpDir);
		if (!d.exists()) {
			return;
		}
		File files[] = d.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		d.delete();
	}

	/**
	 * 실제 파일명 가져오기<br>
	 * 간혹 파일명에 디렉토리 경로가 붙는 경우가 있어서 파일명만 가져옴.<br>
	 * 
	 * @param filename 파일명
	 * @return 실제 파일명
	 */
	public static String getRealFileName(String filename) {
		if (filename.indexOf("/") >= 0) {
			return filename.substring(filename.lastIndexOf("/"));
		}
		if (filename.indexOf(File.separator) >= 0) {
			return filename.substring(filename.lastIndexOf(File.separator));
		}
		return filename;
	}

	/**
	 * console 정보 clear 하기
	 */
	public static void clearConsoleOutput() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String ostype = (os.contains("windows")) ? "W" : "U";
			if (ostype.equals("W")) {
				CommandLine cmdLine = CommandLine.parse("cls");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.printf("%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n", new Object[0]);
			} else {
				CommandLine cmdLine = CommandLine.parse("clear");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.print(CLEAR_TERMINAL_ANSI_CMD);
				System.out.flush();
			}
		} catch (IOException e) {
		}
	}
	
	/**
	 * XMigration 배포 버전정보 얻어오기
	 * 
	 * @return XMigration 버전정보
	 */
	public static String getXMigrationVersion() {
		if (XMigration_PublishingVersion == null) {
			readVersionFromFile();
		}
		return XMigration_PublishingVersion;
	}

	/**
	 * XMigration 배포일 얻어오기
	 * 
	 * @return XMigration 배포일
	 */
	public static String getXMigrationPublshingDate() {
		if (XMigration_PublishingDate == null) {
			readVersionFromFile();
		}
		return XMigration_PublishingDate;
	}
	
	/**
	 * XMigration 배포정보 읽어오기
	 */
	private static void readVersionFromFile() {
		XMigration_PublishingVersion = "<unknown>";
		XMigration_PublishingDate = "<unknown>";
		InputStreamReader isr = null;
		LineNumberReader lnr = null;
		try {
			isr = new InputStreamReader(XNDiscMigUtil.class.getResourceAsStream("/com/sds/acube/ndisc/xmigration/version.txt"));
			if (isr != null) {
				lnr = new LineNumberReader(isr);
				String line = null;
				do {
					line = lnr.readLine();
					if (line != null) {
						if (line.startsWith("Publishing-Version=")) {
							XMigration_PublishingVersion = line.substring("Publishing-Version=".length(), line.length()).trim();
						} else if (line.startsWith("Publishing-Date=")) {
							XMigration_PublishingDate = line.substring("Publishing-Date=".length(), line.length()).trim();
						}
					}
				} while (line != null);
				lnr.close();
			}
		} catch (IOException ioe) {
			XMigration_PublishingVersion = "<unknown>";
			XMigration_PublishingDate = "<unknown>";
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
