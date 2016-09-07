package com.sds.rqreport.model;

import java.io.Serializable;

public class RQDocStatSV implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String FILE_NM = "";
	private String RUNVAR = "";
	private String CLIENT_IP = "";
	private String SERVERTIME_RESPONSE = "";
	private String SERVERTIME = "";
	private String ROWCNT = "";
	private String ERROR = "";
	
	public String getFILE_NM() {
		return FILE_NM;
	}
	public void setFILE_NM(String file_nm) {
		FILE_NM = file_nm;
	}
	public String getRUNVAR() {
		return RUNVAR;
	}
	public void setRUNVAR(String runvar) {
		RUNVAR = runvar;
	}
	public String getCLIENT_IP() {
		return CLIENT_IP;
	}
	public String getERROR() {
		return ERROR;
	}
	public void setERROR(String error) {
		ERROR = error;
	}
	public void setCLIENT_IP(String client_ip) {
		CLIENT_IP = client_ip;
	}
	public String getSERVERTIME_RESPONSE() {
		return SERVERTIME_RESPONSE;
	}
	public void setSERVERTIME_RESPONSE(String servertime_response) {
		SERVERTIME_RESPONSE = servertime_response;
	}
	public String getSERVERTIME() {
		return SERVERTIME;
	}
	public void setSERVERTIME(String servertime) {
		SERVERTIME = servertime;
	}
	public String getROWCNT() {
		return ROWCNT;
	}
	public void setROWCNT(String rowcnt) {
		ROWCNT = rowcnt;
	}
	

}
