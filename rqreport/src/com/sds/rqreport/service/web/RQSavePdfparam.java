package com.sds.rqreport.service.web;

public class RQSavePdfparam {

	private String doc = "";
	private String id = "";
	private String pw = "";
	private String uid = "";
	private String flag = "";
	private byte[] rawdata = null;
	
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public byte[] getRawdata() {
		return rawdata;
	}
	public void setRawdata(byte[] rawdata) {
		this.rawdata = rawdata;
	}
}
