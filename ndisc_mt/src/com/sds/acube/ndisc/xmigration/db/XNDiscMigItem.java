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
package com.sds.acube.ndisc.xmigration.db;

/**
 * Migration 파일 정보
 * 
 * @author Takkies
 *
 */
public class XNDiscMigItem {
	private String fileId;
	private int fileSize;
	private String fileName;
	private String fileStatus;
	private String fileRegDate;
	private int fileMediaId;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public String getFileRegDate() {
		return fileRegDate;
	}
	public void setFileRegDate(String fileRegDate) {
		this.fileRegDate = fileRegDate;
	}
	public int getFileMediaId() {
		return fileMediaId;
	}
	public void setFileMediaId(int fileMediaId) {
		this.fileMediaId = fileMediaId;
	}
}
