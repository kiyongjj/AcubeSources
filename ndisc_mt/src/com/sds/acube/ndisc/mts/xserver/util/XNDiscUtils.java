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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.xsocket.DataConverter;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.filter.iface.FilterIF;
import com.sds.acube.ndisc.util.RandomGUID;

/**
 * XNDisc Server 유틸리티 클래스
 * 
 * @author Takkies
 * 
 */
public class XNDiscUtils {

	/* XNDisc Server 배포 버전정보(version.txt) */
	private static String XNDisc_PublishingVersion;

	/* XNDisc Server 배포 날짜(version.txt) */
	private static String XNDisc_PublishingDate;

	
	/* 암호화 필터 객체 */
	protected static FilterIF filterEnc = XNDiscConfig.getFilterEncrypt();

	/* 압축 필터 객체 */
	protected static FilterIF filterComp = XNDiscConfig.getFilterCompress();

	/* OS 명 취득 */
	private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

	/* worker pool size 설정 key */
	public static final String XSOCKET_WORKER_POOL_SIZE = "org.xsocket.connection.server.workerpoolSize";

	/* worker pool minimum size 설정 key */
	public static final String XSOCKET_WORKER_POOL_MIN_SIZE = "org.xsocket.connection.server.workerpoolMinSize";

	/* dispatcher initial count 설정 key */
	public static final String XSOKET_DISPATCHER_INIT_COUNT = "org.xsocket.connection.dispatcher.initialCount";

	/* dispatcher max handle count 설정 key */
	public static final String XSOCKET_DISPATCHER_MAX_HANDLE = "org.xsocket.connection.dispatcher.maxHandles";

	/* dispatcher bypass write allow 설정 key */
	public static final String XSOCKET_DISPATCHER_BYPASSING_WRITE_ALLOWED = "org.xsocket.connection.dispatcher.bypassingWriteAllowed";

	/* dispatcher detach 설정 key */
	public static final String XSOCKET_DISPATCHER_DETACH_HANDLE_ON_NO_OPS = "org.xsocket.connection.dispatcher.detachHandleOnNoOps";

	/* server read buffer direct 설정 key */
	public static final String XSOCKET_SERVER_READ_BUFFER_USEDIRECT = "org.xsocket.connection.server.readbuffer.usedirect";

	/* client read buffer direct 설정 key */
	public static final String XSOCKET_CLIENT_READ_BUFFER_USEDIRECT = "org.xsocket.connection.client.readbuffer.usedirect";

	/* write buffer direct 설정 key */
	public static final String XSOCKET_WRITE_BUFFER_USEDIRECT = "org.xsocket.connection.writebuffer.usedirect";

	/* suppress sync flush warning 설정 key */
	public static final String XSOCKET_SUPPRESS_SYNC_FLUSH_WARNING = "org.xsocket.connection.suppressSyncFlushWarning";

	/* suppress reuse buffer warning 설정 key */
	public static final String XSOCKET_SUPPRESS_REUSE_BUFFER_WARNING = "org.xsocket.connection.suppressReuseBufferWarning";

	/* suppress sync flush completion 설정 key */
	public static final String XSOCKET_SUPPRESS_SYNC_FLUSH_COMPLETION_HANDLER_WARNING = "org.xsocket.connection.suppressSyncFlushCompletionHandlerWarning";

	/* transfer byte buffer max size 설정 key */
	public static final String XSOCKET_TRANFER_MAPPED_BYTEBUFFER_MAX_SIZE = "org.xsocket.connection.transfer.mappedbytebuffer.maxsize";

	/**
	 * 상태값 문자열 변경하기
	 * 
	 * @param status
	 *            상태값
	 * @return 변경된 상태값
	 */
	public static String getStatus(int status) {
		String strstatus = Integer.toString(status);
		return StringUtils.leftPad(strstatus, 5, "0");
	}

	/**
	 * 파일 아이디 얻어오기
	 * 
	 * @param fileid
	 *            파일 아이디
	 * @return 파일 아이디
	 */
	public static String getFileID(String fileid) {
		String strRet = null;
		try {
			if (XNDiscConfig.NDISC_NA_RESERV.equals(fileid)) {
				strRet = getFileID();
			} else {
				strRet = fileid;
			}
		} catch (Exception e) {
			e.printStackTrace();
			strRet = null;
		}
		return strRet;
	}

	/**
	 * 파일 아이디 얻어오기
	 * 
	 * @return 파일 아이디
	 */
	public static String getFileID() {
		String strRet = null;
		RandomGUID guid = null;
		try {
			guid = new RandomGUID();
			strRet = guid.toString();
		} catch (Exception e) {
			e.printStackTrace();
			strRet = null;
		}
		return strRet;
	}

	/**
	 * 날짜 값 가져오기
	 * 
	 * @param date
	 *            날짜 문자열
	 * @return 날짜 값
	 */
	public static String getDate(String date) {
		String strRet = null;
		try {
			if (XNDiscConfig.NDISC_NA_RESERV.equals(date)) {
				strRet = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			} else {
				strRet = date;
			}
		} catch (Exception e) {
			e.printStackTrace();
			strRet = null;
		}
		return strRet;
	}

	/**
	 * 미디어 경로 만들기
	 * 
	 * @param path
	 *            미디어 경로
	 * @throws Exception
	 */
	public static void makeMediaPath(String path) throws Exception {
		try {
			File dir = new File(path);
			dir.mkdirs();
			if (!dir.exists()) {
				throw new Exception("can not create new media directory - " + path);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 파일 정보 세팅하기(PATH 정보)
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 설정된 NFile 정보 배열
	 * @throws Exception
	 */
	public static NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {
				if (!XNDiscConfig.STAT_NONE.equals(nFile[i].getStatType())) {
					nFile[i].setTmpPath(getTmpPath(nFile[i]));
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return nFile;
	}

	/**
	 * 복사할 파일 정보 수정
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @param arrOriFilter
	 *            원 필터 정보 배열
	 * @return NFile 정보 배열
	 * @throws Exception
	 */
	public static NFile[] retrieveCpyNFile(NFile[] nFile, String[] arrOriFilter) throws Exception {
		for (int i = 0; i < nFile.length; i++) {
			nFile[i].setStatType(arrOriFilter[i]);
			nFile[i].setId(getFileID());
		}
		return nFile;
	}

	/**
	 * 이동할 파일 정보 수정
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @param arrOriFilter
	 *            원 필터 정보 배열
	 * @return NFile 정보 배열
	 * @throws Exception
	 */
	public static NFile[] retrieveMovNFile(NFile[] nFile, String[] arrOriFilter) throws Exception {
		for (int i = 0; i < nFile.length; i++) {
			nFile[i].setStatType(arrOriFilter[i]);
		}
		return nFile;
	}

	/**
	 * 해당 파일 정보에서 임시 디렉토리 정보 취득
	 * 
	 * @param nFile
	 *            NFile 정보
	 * @return 임시 디렉토리 문자열
	 */
	public static String getTmpPath(NFile nFile) {
		RandomGUID guid = null;
		String strFileName = null;
		try {
			guid = new RandomGUID();
			if (null == nFile.getName() || 0 == nFile.getName().length()) {
				strFileName = guid.toString();
			} else {
				strFileName = guid.toString().concat(".").concat(getFileExt(nFile.getName()));
			}
			String tmp_dir = XNDiscConfig.getString(XNDiscConfig.TEMP_DIR);
			if (tmp_dir.indexOf("/") >= 0) {
				strFileName = tmp_dir.concat("/").concat(strFileName);
			} else {
				strFileName = tmp_dir.concat(File.separator).concat(strFileName);
			}
		} catch (Exception e) {
			strFileName = null;
		}
		return strFileName;
	}

	/**
	 * 파일 확장자 정보
	 * 
	 * @param strFileName
	 *            파일명
	 * @return 확장자
	 */
	public static String getFileExt(String strFileName) {
		int nPos = strFileName.lastIndexOf(".");
		String strExt = strFileName.substring(nPos + 1, strFileName.length());
		return strExt;
	}

	/**
	 * 송수신 시 필요한 ByteBuffer의 사이즈만큼 garbage 문자열 생성
	 * 
	 * @param data
	 *            원본 문자열
	 * @param size
	 *            ByteBuffer 사이즈
	 * @return padding된 문자열
	 */
	public static String getFormatString(String data, int size) {
		if (data.length() > size) {
			return data.substring(0, size);
		} else {
			return StringUtils.rightPad(data, size, "0");
		}
	}

	/**
	 * 파일 복사하기
	 * 
	 * @param nFile
	 *            복사할 NFile 정보 배열
	 * @param bForwardMedia
	 *            원본 경로와 복사본의 경로 설정 옵션
	 * @param bForce
	 *            복사 실행 여부
	 * @throws Exception
	 */
	public static void copyNFile(NFile[] nFile, boolean bForwardMedia, boolean bForce) throws Exception {
		FileChannel srcChannel = null;
		FileChannel dstChannel = null;
		String srcFile = null;
		String dstFile = null;
		try {
			for (int i = 0; i < nFile.length; i++) {
				if (false == bForce && XNDiscConfig.STAT_NONE.equals(nFile[i].getStatType())) {
					continue;
				} else {
					if (bForwardMedia) {
						srcFile = nFile[i].getTmpPath();
						dstFile = nFile[i].getStoragePath();
					} else {
						srcFile = nFile[i].getStoragePath();
						dstFile = nFile[i].getTmpPath();
					}
					srcChannel = new FileInputStream(srcFile).getChannel();
					dstChannel = new FileOutputStream(dstFile).getChannel();
					dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
					if (bForwardMedia) {
						new File(srcFile).delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != srcChannel && srcChannel.isOpen()) {
				srcChannel.close();
			}
			if (null != dstChannel && dstChannel.isOpen()) {
				dstChannel.close();
			}
		}
	}

	/**
	 * 파일정보에 필터 옵션 설정하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @param bForward
	 *            필터 진행 방향 옵션
	 * @throws Exception
	 */
	public static void filterNFile(NFile[] nFile, boolean bForward) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {
				if (XNDiscConfig.STAT_NONE.equals(nFile[i].getStatType())) {
					continue;
				} else {
					convertFile(nFile[i].getStatType(), nFile[i].getTmpPath(), bForward);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 실제 파일에 필터 설정
	 * 
	 * @param statType
	 *            필터 옵션 정보
	 * @param strFilePath
	 *            필터를 지정할 파일 경로
	 * @param bForward
	 *            필터 방향 설정
	 * @throws FileException
	 */
	public static void convertFile(String statType, String strFilePath, boolean bForward) throws FileException {
		try {
			if (XNDiscConfig.STAT_ENC.equals(statType)) {
				if (true == bForward) {
					filterEnc.filterFileForward(strFilePath);
				} else {
					filterEnc.filterFileReverse(strFilePath);
				}
			} else if (XNDiscConfig.STAT_COMP.equals(statType)) {
				if (true == bForward) {
					filterComp.filterFileForward(strFilePath);
				} else {
					filterComp.filterFileReverse(strFilePath);
				}
			} else if (XNDiscConfig.STAT_COMP_ENC.equals(statType)) {
				if (true == bForward) {
					filterComp.filterFileForward(strFilePath);
					filterEnc.filterFileForward(strFilePath);
				} else {
					filterEnc.filterFileReverse(strFilePath);
					filterComp.filterFileReverse(strFilePath);
				}
			}
		} catch (Exception e) {
			throw new FileException(e.getMessage());
		}
	}

	/**
	 * 송수신을 위해서 파일정보를 문자열로 변경
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 파일정보 문자열
	 */
	public static String makeReturnNFileInfoMsg(NFile[] nFile) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < nFile.length; i++) {
			str.append(nFile[i].getId()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getName()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getSize()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getCreatedDate()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getModifiedDate()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getMediaId()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getStatType()).append(XNDiscConfig.DELIM_STR);
			str.append(nFile[i].getStoragePath()).append(XNDiscConfig.DELIM_STR);
		}
		return str.toString();
	}

	/**
	 * 현재 날짜 가져오기
	 * 
	 * @return 현재 날짜 문자열
	 */
	public static String getStartDate() {
		Date dt = new Date();
		SimpleDateFormat sdt = new SimpleDateFormat("yyyy MM-dd HH:mm:ss");
		return sdt.format(dt);
	}

	/**
	 * 송수신한 ByteBuffer data값을 문자열로 변경하기
	 * 
	 * @param buffer
	 *            송수신한 ByteBuffer 값
	 * @return 변경됨 문자열
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
	 * 에러 로그 추적하기
	 * 
	 * @param e
	 *            Exception 객체
	 * @return 추적된 에러로그 문자열
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

	/**
	 * 윈도우 OS 인지 여부
	 * 
	 * @return 윈도우 OS이면 true, 아니면 false
	 */
	public static boolean isWindows() {
		return (OS_NAME.contains("win"));
	}

	/**
	 * HP-UX OS 인지 여부
	 * 
	 * @return HP-UX OS이면 true, 아니면 false
	 */
	public static boolean isHpUnix() {
		return (OS_NAME.contains("hp-ux"));
	}

	/**
	 * IBM-AIX OS 인지 여부
	 * 
	 * @return IBM-AIX OS이면 true, 아니면 false
	 */
	public static boolean isAix() {
		return (OS_NAME.contains("aix"));
	}

	/**
	 * SUN SOLARIS 인지 여부
	 * 
	 * @return SUN SOLARIS OS 이면 true, 아니면 false
	 */
	public static boolean isSolaris() {
		return (OS_NAME.contains("sunos") || OS_NAME.contains("solaris"));
	}

	/**
	 * UNIX, LINUX 인지 여부
	 * 
	 * @return UNIX, LINUX OS 이면 true, 아니면 false
	 */
	public static boolean isUnix() {
		return (OS_NAME.contains("nix") || OS_NAME.contains("nux"));
	}
	
	/**
	 * XNDisc 배포 버전정보 얻어오기
	 * 
	 * @return XNDisc 배포 버전정보
	 */
	public static String getXNDiscVersion() {
		if (XNDisc_PublishingVersion == null) {
			readVersionFromFile();
		}
		return XNDisc_PublishingVersion;
	}

	/**
	 * XNDisc 배포일
	 * 
	 * @return XNDisc 배포일
	 */
	public static String getXNDiscPublshingDate() {
		if (XNDisc_PublishingDate == null) {
			readVersionFromFile();
		}
		return XNDisc_PublishingDate;
	}
	
	/**
	 * XNDisc 배포정보 읽어오기
	 */
	private static void readVersionFromFile() {
		XNDisc_PublishingVersion = "<unknown>";
		XNDisc_PublishingDate = "<unknown>";
		InputStreamReader isr = null;
		LineNumberReader lnr = null;
		try {
			isr = new InputStreamReader(XNDiscUtils.class.getResourceAsStream("/com/sds/acube/ndisc/mts/xserver/version.txt"));
			if (isr != null) {
				lnr = new LineNumberReader(isr);
				String line = null;
				do {
					line = lnr.readLine();
					if (line != null) {
						if (line.startsWith("Publishing-Version=")) {
							XNDisc_PublishingVersion = line.substring("Publishing-Version=".length(), line.length()).trim();
						} else if (line.startsWith("Publishing-Date=")) {
							XNDisc_PublishingDate = line.substring("Publishing-Date=".length(), line.length()).trim();
						}
					}
				} while (line != null);
				lnr.close();
			}
		} catch (IOException ioe) {
			XNDisc_PublishingVersion = "<unknown>";
			XNDisc_PublishingDate = "<unknown>";
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
