package com.sds.rqreport.service.fileservice;

import java.util.Hashtable;

/**
 * Client Info class
 * 
 * fields 
 * request-Content : 
 * Host : 
 * Modified : 
 * RQFileResquest.java
 *
 */
public class RQFileRequest {
	
	// Client Header first Line
	private String strfirstHeader = "";
	// reqube type
	private String strRequestType = "";
	// request file name
	private String strfileName = "";
	
	// from parameter 
	public Hashtable reqprop = null;
	
	public RQFileRequest(){
		reqprop = new Hashtable();
	}
	
	// from socket 
	private String threadId = "";
	private String remoteAddress = "";
	private String localport = "";
	
	public String getStrRequestType() {
		return strRequestType;
	}
	public void setStrRequestType(String strRequestType) {
		this.strRequestType = strRequestType;
	}
	public String getStrfileName() {
		return strfileName;
	}
	public void setStrfileName(String strfileName) {
		this.strfileName = strfileName;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public String getLocalport() {
		return localport;
	}
	public void setLocalport(String localport) {
		this.localport = localport;
	}
	public String getStrfirstHeader() {
		return strfirstHeader;
	}
	public void setStrfirstHeader(String strfirstHeader) {
		this.strfirstHeader = strfirstHeader;
	}
}
