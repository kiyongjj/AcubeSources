package com.sds.rqreport.model;

import java.io.Serializable;

public class RQDocStatCL implements Serializable{

	private static final long serialVersionUID = 1L;
	private String RUN_TIME = "";
	private String TOTALTIME = "";
	private String TOTALTIME_RESPONSE = "";
	private String ERROR = "";
	
	public String getRUN_TIME() {
		return RUN_TIME;
	}
	public void setRUN_TIME(String run_time) {
		RUN_TIME = run_time;
	}
	public String getTOTALTIME() {
		return TOTALTIME;
	}
	public void setTOTALTIME(String totaltime) {
		TOTALTIME = totaltime;
	}
	public String getTOTALTIME_RESPONSE() {
		return TOTALTIME_RESPONSE;
	}
	public void setTOTALTIME_RESPONSE(String totaltime_response) {
		TOTALTIME_RESPONSE = totaltime_response;
	}
	public String getERROR() {
		return ERROR;
	}
	public void setERROR(String error) {
		ERROR = error;
	}
}
