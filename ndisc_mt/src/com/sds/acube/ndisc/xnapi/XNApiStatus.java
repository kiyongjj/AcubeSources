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

/**
 * XNApi 상태 정보
 * 
 * @author Takkies
 *
 */
public enum XNApiStatus {

	INIT(0, "SERVICE_STAT_INIT"),
	REG(1, "SERVICE_STAT_FILEREG"),
	GET(2, "SERVICE_STAT_FILEGET"),
	REP(3, "SERVICE_STAT_FILEREP"),
	DEL(4, "SERVICE_STAT_FILEDEL"),
	CPY(5, "SERVICE_STAT_FILECPY"),
	MOV(6, "SERVICE_STAT_FILEMOV"),
	FILEINFO(7, "SERVICE_STAT_FILEINFO"),
	VOLINFO(8, "SERVICE_STAT_VOLINFO"),
	MKVOLUME(10001, "SERVICE_STAT_MKVOLUME"),
	RMVOLUME(10002, "SERVICE_STAT_RMVOLUME"),
	MKMEDIA(20001, "SERVICE_STAT_MKMEDIA"),
	GETCONF(30001, "SERVICE_STAT_GETCONF"),
	QUIT(99999, "SERVICE_STAT_QUIT"),
	READY(90001, "SERVICE_STAT_READY");

	private int status;
	private String name;
	XNApiStatus(int status, String name) {
		this.status = status;
		this.name = name;
		
	}
	public int getStatus() {
		return this.status;
	}
	public String getName() {
		return this.name;
	}
	
}



