package com.sds.rqreport.scheduler;

import java.io.Serializable;

import com.sds.rqreport.common.RQInfo;

public class ScheduleInfoCL extends ScheduleInfo implements RQInfo, Serializable {

	private static final long serialVersionUID = 1L;
	private String localip;
	private int localipport;
	private String resultFileName;
	private String doctype;
	private ScheduleRunInfo oScheduleRunInfo;
	
	public String getLocalip() {
		return localip;
	}
	public void setLocalip(String localip) {
		this.localip = localip;
	}
	public int getLocalipport() {
		return localipport;
	}
	public void setLocalipport(int localipport) {
		this.localipport = localipport;
	}
	public String getResultFileName() {
		return resultFileName;
	}
	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}
	public String getDoctype() {
		return doctype;
	}
	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	public ScheduleRunInfo getOScheduleRunInfo() {
		return oScheduleRunInfo;
	}
	public void setOScheduleRunInfo(ScheduleRunInfo scheduleRunInfo) {
		oScheduleRunInfo = scheduleRunInfo;
	}
}