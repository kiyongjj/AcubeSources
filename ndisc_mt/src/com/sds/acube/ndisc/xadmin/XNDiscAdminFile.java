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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;

/**
 * XNDisc Admin File 정보 보여주기
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminFile extends XNDiscAdminBase {

	public XNDiscAdminFile(boolean printlog, PrintStream out, Logger log) {
		super(printlog, out, log);
	}

	/**
	 * 파일 정보 조회결과 Console에 보여주기
	 * 
	 * @param fileList NFile 정보
	 */
	private void showFileInfo(ArrayList<NFile> fileList) {
		int size = (fileList == null) ? 0 : fileList.size();
		NFile file = null;
		StringBuilder files = new StringBuilder(LINE_SEPERATOR);
		files.append("┌").append(StringUtils.center("", 32, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 65, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 10, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 14, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 14, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 10, "-"));
		files.append("┬");
		files.append(StringUtils.center("", 5, "-"));
		files.append("┐").append(LINE_SEPERATOR);

		files.append("│").append(StringUtils.center("File ID", 32, " "));
		files.append("│");
		files.append(StringUtils.center("File Name", 65, " "));
		files.append("│");
		files.append(StringUtils.center("Size", 10, " "));
		files.append("│");
		files.append(StringUtils.center("Create Date", 14, " "));
		files.append("│");
		files.append(StringUtils.center("Modify Date", 14, " "));
		files.append("│");
		files.append(StringUtils.center("Status", 10, " "));
		files.append("│");
		files.append(StringUtils.center("Media", 5, " "));
		files.append("│").append(LINE_SEPERATOR);

		if (size == 0) {
			int colidx = 0;
			files.append("├").append(StringUtils.center("", 32, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 65, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 10, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 14, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 14, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 10, "-"));
			files.append("┴");
			colidx++;
			files.append(StringUtils.center("", 5, "-"));
			files.append("┤").append(LINE_SEPERATOR);
			files.append("│").append(StringUtils.center(" No Data Found.", PRINT_COLUMN_SIZE + colidx, " ")).append("│").append(LINE_SEPERATOR);
		} else {
			files.append("├").append(StringUtils.center("", 32, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 65, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 10, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 14, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 14, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 10, "-"));
			files.append("┼");
			files.append(StringUtils.center("", 5, "-"));
			files.append("┤").append(LINE_SEPERATOR);
		}

		for (int i = 0; i < size; i++) {
			file = (NFile) fileList.get(i);
			files.append("│").append(StringUtils.center(file.getId(), 32, " "));
			files.append("│");
			files.append(StringUtils.rightPad(getName(file.getName(), 65), 65, " "));
			files.append("│");
			files.append(StringUtils.center(Integer.toString(file.getSize()), 10, " "));
			files.append("│");
			files.append(StringUtils.center((StringUtils.isEmpty(file.getCreatedDate()) ? "N/A" : file.getCreatedDate()), 14, " "));
			files.append("│");
			files.append(StringUtils.center((StringUtils.isEmpty(file.getModifiedDate()) ? "N/A" : file.getModifiedDate()), 14, " "));
			files.append("│");
			files.append(StringUtils.center(file.getStatType(), 10, " "));
			files.append("│");
			files.append(StringUtils.center(Integer.toString(file.getMediaId()), 5, " "));
			files.append("│").append(LINE_SEPERATOR);
			if ((i < size - 1) && (i <= MAX_LIST_SIZE)) {
				files.append("├").append(StringUtils.center("", 32, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 65, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 10, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 14, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 14, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 10, "-"));
				files.append("┼");
				files.append(StringUtils.center("", 5, "-"));
				files.append("┤").append(LINE_SEPERATOR);
			}
			if (i > MAX_LIST_SIZE) {
				break;
			}
		}
		if (size == 0) {
			files.append("└").append(StringUtils.center("", 32, "-"));
			files.append("-");
			files.append(StringUtils.center("", 65, "-"));
			files.append("-");
			files.append(StringUtils.center("", 10, "-"));
			files.append("-");
			files.append(StringUtils.center("", 14, "-"));
			files.append("-");
			files.append(StringUtils.center("", 14, "-"));
			files.append("-");
			files.append(StringUtils.center("", 10, "-"));
			files.append("-");
			files.append(StringUtils.center("", 5, "-"));
			files.append("┘").append(LINE_SEPERATOR);
		} else {
			files.append("└").append(StringUtils.center("", 32, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 65, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 10, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 14, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 14, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 10, "-"));
			files.append("┴");
			files.append(StringUtils.center("", 5, "-"));
			files.append("┘").append(LINE_SEPERATOR);
		}
		files.append(size + " row selected.").append(LINE_SEPERATOR).append(LINE_SEPERATOR);
		if (printlog) {
			log.info(files.toString());
		} else {
			out.print(files.toString());
		}
	}

	/**
	 * 모든 파일 정보 가져와 Console에 보여주기(페이징 하지 않으므로 가급적 사용하지 않아야함)
	 */
	@SuppressWarnings("unchecked")
	public void selectFileList() {
		ArrayList<NFile> fileList = null;
		try {
			fileList = new ArrayList<NFile>();
			fileList = (ArrayList<NFile>) storage.selectFileInfoList();
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showFileInfo(fileList);
	}

	/**
	 * 파일 아이디를 이용하여 파일 정보를 Console에 보여주기
	 * 
	 * @param fileId 파일 아이디
	 */
	public void selectFileById(String fileId) {
		ArrayList<NFile> fileList = null;
		try {
			fileList = new ArrayList<NFile>();
			NFile file = storage.selectFileInfo(fileId);
			if (file != null) {
				fileList.add(file);
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showFileInfo(fileList);
	}

	/**
	 * 파일을 등록하고 등록한 결과를 Console에 보여주기
	 * 
	 * @param host HOST 정보
	 * @param port PORT 정보
	 * @param regFilePath 등록한 파일경로(파일명까지 전체 경로)
	 * @param regVolId 등록할 볼륨 아이디
	 * @param regStatType 등록할 상태 정보
	 */
	public void regFile(String host, int port, String regFilePath, int regVolId, String regStatType) {
		NFile[] nFile = null;
		try {
			int numOfFiles = 1;
			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setName(regFilePath);
			nFile[0].setVolumeId(regVolId);
			nFile[0].setStatType(regStatType);

			xnapi.XNDisc_Connect(host, port);
			String[] arrRet = xnapi.XNDISC_FileReg(nFile);
			if (null == arrRet) {
				logger.log(LoggerIF.LOG_ERROR, "XNDISC_FileReg() return null");
			} else {
				StringBuilder files = new StringBuilder(LINE_SEPERATOR);
				files.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
				files.append("│").append(StringUtils.center("Registered File ID : " + arrRet[0], 100, " ")).append("│").append(LINE_SEPERATOR);
				files.append("└").append(StringUtils.rightPad("", 100, "-") + "┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(files.toString());
				} else {
					out.print(files.toString());
				}
			}
		} catch (FileException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} catch (NetworkException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} catch (NDiscException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} finally {
			try {
				xnapi.XNDisc_Disconnect();
			} catch (NetworkException ne) {
				logger.log(LoggerIF.LOG_ERROR, ne.getMessage());
			} catch (IOException e) {
				logger.log(LoggerIF.LOG_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * 파일 아이디를 이용하여 파일을  다운로드 받고 Console에 보여주기
	 * 
	 * @param host HOST 정보
	 * @param port PORT 정보
	 * @param fileId 파일 아이디
	 * @param destFilePath 다운로드 받을 경로(파일명까지)
	 */
	public void getFile(String host, int port, String fileId, String destFilePath) {
		try {
			int numOfFiles = 1;
			NFile[] nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);
			nFile[0].setName(destFilePath);
			nFile[0].setStatType(XNDiscAdminConfig.STAT_AUTO); // AUTO

			xnapi.XNDisc_Connect(host, port);
			boolean reg = xnapi.XNDISC_FileGet(nFile);
			if (reg) {
				StringBuilder files = new StringBuilder(LINE_SEPERATOR);
				files.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
				files.append("│").append(StringUtils.center("getFile : " + getName(destFilePath, 100), 100, " ")).append("│").append(LINE_SEPERATOR);
				files.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(files.toString());
				} else {
					out.print(files.toString());
				}
			} else {
				logger.log(LoggerIF.LOG_ERROR, "getFile() failed.");
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				xnapi.XNDisc_Disconnect();
			} catch (NetworkException ne) {
				logger.log(LoggerIF.LOG_ERROR, ne.getMessage());
			} catch (IOException e) {
				logger.log(LoggerIF.LOG_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * 파일 아이디를 이용하여 파일 경로 정보 얻어오고 Console에 보여주기
	 * 
	 * @param fileId 파일 아이디
	 */
	public void getFilePathByFileId(String fileId) {
		NFile[] nFile = null;
		try {
			nFile = new NFile[1];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);
			nFile = storage.aquireStorageInfo(nFile, NDCommon.STORAGE_PATH_ACCESS);
			String ret = nFile[0].getStoragePath();
			if (!StringUtils.isEmpty(ret)) {
				StringBuilder files = new StringBuilder(LINE_SEPERATOR);
				files.append("┌").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┐").append(LINE_SEPERATOR);
				files.append("│").append(StringUtils.center("getFilePath : " + ret, PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				files.append("└").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(files.toString());
				} else {
					out.print(files.toString());
				}
			}
		} catch (Exception e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		}
	}

	/**
	 * 파일 삭제하고 삭제한 파일정보 Console에 보여주기
	 * 
	 * @param host HOST 정보
	 * @param port PORT 정보
	 * @param fileId 파일 아이디
	 */
	public void removeFile(String host, int port, String fileId) {
		NFile[] nFile = null;
		int numOfFiles = 1;
		try {
			if (fileId.indexOf(",") >= 0) {
				String fileIds[] = StringUtils.split(fileId, ",");
				if (fileIds != null && fileIds.length > 0) {
					nFile = new NFile[fileIds.length];
					for (int i = 0; i < fileIds.length; i++) {
						nFile[i] = new NFile();
						nFile[i].setId(fileIds[i]);
					}
				}
			} else {
				nFile = new NFile[numOfFiles];
				nFile[0] = new NFile();
				nFile[0].setId(fileId);
			}
			xnapi.XNDisc_Connect(host, port);
			boolean ret = xnapi.XNDISC_FileDel(nFile);
			if (ret) {
				StringBuilder files = new StringBuilder(LINE_SEPERATOR);
				files.append("┌").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┐").append(LINE_SEPERATOR);
				if (fileId.indexOf(",") >= 0) {
					String fileIds[] = StringUtils.split(fileId, ",");
					if (fileIds != null && fileIds.length > 0) {
						for (int i = 0; i < fileIds.length; i++) {
							files.append("│").append(StringUtils.center("removeFile[" + i + "] : " + fileIds[i], PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
						}
					}
				} else {
					files.append("│").append(StringUtils.center("removeFile : " + fileId, PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				}
				files.append("└").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(files.toString());
				} else {
					out.print(files.toString());
				}
			} else {
				logger.log(LoggerIF.LOG_ERROR, "Remove File ID : " + fileId + " failed !!!!");
			}
		} catch (FileException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} catch (NetworkException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} catch (NDiscException e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		} finally {
			try {
				xnapi.XNDisc_Disconnect();
			} catch (NetworkException ne) {
				logger.log(LoggerIF.LOG_ERROR, ne.getMessage());
			} catch (IOException e) {
				logger.log(LoggerIF.LOG_ERROR, e.getMessage());
			}
		}
	}
}
