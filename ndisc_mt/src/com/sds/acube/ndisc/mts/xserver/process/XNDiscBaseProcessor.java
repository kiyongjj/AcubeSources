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
package com.sds.acube.ndisc.mts.xserver.process;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.sds.acube.ndisc.common.exception.DaoException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscCipher;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;
import com.sds.acube.ndisc.mts.xserver.util.XNDiscUtils;

/**
 * 기본 Processor로 Processor 구현 시 상속받아 구현함.<br>
 * 기본 storage 처리 공통 함수 구현
 * 
 * @author Takkies
 *
 */
public abstract class XNDiscBaseProcessor {

	/* storage 객체 */
	public StorageIF storage;

	/* logger 객체 */
	public LoggerIF logger;

	/* 송수신 파일 정보들 */
	public StringTokenizer files;

	/* 송수신 파일 개수 */
	public int filecounts;

	/**
	 * Processor 실행 처리
	 * 
	 * @param connection 소켓 connection(Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 {@link INonBlockingConnection} 사용) 
	 * @return 성공이면 true, 실패이면 false
	 * @throws Exception 에러
	 */
	public abstract boolean run(INonBlockingPipeline connection) throws BufferUnderflowException, BufferOverflowException, ClosedChannelException, IOException;

	/**
	 * 	기본적으로 사용하는 생성자
	 * 
	 * @param storage storage 객체
	 * @param logger logger 객체
	 * @param files 송수신 파일 정보들
	 * @param filecounts 송수신 파일 개수
	 */
	public XNDiscBaseProcessor(StorageIF storage, LoggerIF logger, StringTokenizer files, int filecounts) {
		this.storage = storage;
		this.logger = logger;
		this.files = files;
		this.filecounts = filecounts;
	}

	/**
	 * 파일 정보 얻어오기
	 * 
	 * @param nFile NFile 정보 배열
	 * @return  NFile 정보 배열
	 * @throws Exception 에러
	 */
	protected NFile[] getNFileInfo(NFile[] nFile) throws Exception {
		NFile[] retNFile = null;
		try {
			retNFile = new NFile[nFile.length];
			for (int i = 0; i < nFile.length; i++) {
				retNFile[i] = storage.selectNFileFromDB(nFile[i].getId());
				retNFile[i].setStoragePath(storage.getFileMediaPath(nFile[i].getId(), retNFile[i].getCreatedDate()));
			}
		} catch (Exception e) {
			throw e;
		}
		return retNFile;
	}

	/**
	 * 복사, 이동 시 파일정보 얻어오기
	 * 
	 * @param nFile NFile 정보 배열
	 * @return  NFile 정보 배열
	 * @throws Exception 에러
	 */
	protected NFile[] getBasicNFileCpyMovInfo(NFile[] nFile) throws Exception {
		try {
			NFile nfile = null;
			for (int i = 0; i < nFile.length; i++) {
				nfile = storage.selectNFileFromDB(nFile[i].getId());
				nFile[i].setStatType(nfile.getStatType());
				nFile[i].setName(nfile.getName());
				nFile[i].setTmpPath(XNDiscUtils.getTmpPath(nFile[i]));
			}
		} catch (Exception e) {
			throw e;
		}
		return nFile;
	}

	/**
	 * 파일 다운로드 시 파일정보 얻어오기
	 * 
	 * @param nFile  NFile 정보 배열
	 * @return  NFile 정보 배열
	 * @throws Exception 에러
	 */
	protected NFile[] getBasicNFileGetInfo(NFile[] nFile) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {
				if (XNDiscConfig.STAT_AUTO.equals(nFile[i].getStatType())) {
					nFile[i].setStatType(storage.selectNFileFromDB(nFile[i].getId()).getStatType());
				}
				if (!XNDiscConfig.STAT_NONE.equals(nFile[i].getStatType())) {
					nFile[i].setTmpPath(XNDiscUtils.getTmpPath(nFile[i]));
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return nFile;
	}

	/**
	 * 스토리지 정보 얻어 파일정보에 설정하기
	 * 
	 * @param nFile NFile 정보 배열
	 * @param option 등록처리인지 등의 옵션 정보
	 * @return  NFile 정보 배열
	 * @throws Exception 에러
	 */
	protected NFile[] aquireStorageInfo(NFile[] nFile, String option) throws Exception {
		String[] storages = null;
		for (int i = 0; i < nFile.length; i++) {
			if (XNDiscConfig.STORAGE_PATH_REGIST.equals(option)) {
				storages = getStorage4Regist(nFile[i]);
			} else if (XNDiscConfig.STORAGE_PATH_ACCESS.equals(option)) {
				storages = getStorage4Access(nFile[i]);
			}
			nFile[i].setStoragePath(storages[0]);
			nFile[i].setMediaId(Integer.parseInt(storages[1]));
		}
		return nFile;
	}

	/**
	 * 등록용 스토리지 정보 얻어오기
	 * 
	 * @param nFile NFile 정보
	 * @return 스토리지 정보 배열
	 * @throws Exception 에러
	 */
	private String[] getStorage4Regist(NFile nFile) throws Exception {
		String[] storages = null;
		String strRet = null;
		try {
			storages = new String[2];
			int volumeID = nFile.getVolumeId();
			Media[] media = storage.selectAvailableMedia(volumeID);
			int nFileSize = nFile.getSize();
			for (Media md : media) {
				long nMediaSize = md.getSize();
				long nMediaMaxSize = md.getMaxSize();
				String mediaPath = md.getPath();
				boolean bExists = false;
				if (new File(mediaPath).exists()) {
					bExists = true;
				} else {
					logger.log(LoggerIF.LOG_WARNING, "No exist media path - media : " + md.getId() + ", " + mediaPath);
					bExists = false;
				}
				if ((nMediaSize + nFileSize) <= nMediaMaxSize && bExists) {
					String strCDate = nFile.getCreatedDate();
					String strYear = strCDate.substring(0, 4);
					String strMonth = strCDate.substring(4, 6);
					String strDay = strCDate.substring(6, 8);
					strRet = md.getPath().concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth).concat(File.separator).concat(strDay);
					new File(strRet).mkdirs();
					strRet = strRet.concat(File.separator).concat(XNDiscCipher.encode(nFile.getId())); // cipher.encrypt(nFile.getId());
					storages[0] = strRet;
					storages[1] = md.getId() + "";
					break;
				}
			}
			if (StringUtils.isEmpty(strRet)) {
				String msg = "No avaliable media exists - volume : " + volumeID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return storages;
	}

	/**
	 * 조회용 스토리지 정보 얻어오기
	 * 
	 * @param nFile NFile 정보
	 * @return 스토리지 정보 배열
	 * @throws Exception 에러
	 */
	private String[] getStorage4Access(NFile nFile) throws Exception {
		String[] storages = null;
		try {
			NFile retFile = storage.selectNFileFromDB(nFile.getId());
			String mediaID = retFile.getMediaId() + "";
			String createDate = retFile.getCreatedDate();
			String filePath = getFileMediaPath(nFile.getId(), createDate);
			storages = new String[2];
			storages[0] = filePath;
			storages[1] = mediaID;
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.log(LoggerIF.LOG_ERROR, msg);
			throw e;
		}
		return storages;
	}

	/**
	 * 미디어 정보 경로 얻어오기
	 * 
	 * @param fileID 파일 아이디
	 * @param createDate 생성일 문자열
	 * @return 파일 경로
	 * @throws Exception 에러
	 */
	private String getFileMediaPath(String fileID, String createDate) throws Exception {
		String filePath = null;
		try {
			String mediaPath = storage.getMediaPathByFile(fileID);
			if (StringUtils.isEmpty(mediaPath)) {
				String msg = "File id does not exist : " + fileID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			String strYear = createDate.substring(0, 4);
			String strMonth = createDate.substring(4, 6);
			String strDay = createDate.substring(6, 8);
			filePath = mediaPath.concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth).concat(File.separator).concat(strDay);
			filePath = filePath.concat(File.separator).concat(XNDiscCipher.encode(fileID));// + cipher.encrypt(fileID);
		} catch (Exception e) {
			throw e;
		}
		return filePath.replace('\\', '/');
	}

}
