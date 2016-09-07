package com.sds.rqreport.service.fileservice;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * fields 종류
 * Content-Length : (essential)
 * Content-Type : (optional)
 * Last-Modified : (optional)
 * Date : optional 
 * 
 * RQFileResponse.java
 *
 */
public class RQFileResponse {
	
	private StringBuffer sbHttpProto = null; 
	private StringBuffer sbREQUBEproto = null;
	private boolean bHttpflag = false;
	public Hashtable resprop = null;
	
	public RQFileResponse(){
		sbHttpProto   = new StringBuffer();
		sbREQUBEproto = new StringBuffer();
		resprop       = new Hashtable();
	}
	
	public StringBuffer getSbHttpProto() {
		sbHttpProto.append("HTTP/1.1 200 OK\n").append("\n");
		return sbHttpProto;
	}
	
	// original spec.
	// response 시 resprop 에 값이 있을경우 추가로 붙여준다.
	public StringBuffer getSbREQUBEproto() {
		sbREQUBEproto.append("REQUBE/1.0 200\n");
		if(resprop.size() > 0) {
			Enumeration enumks = resprop.keys();
			while(enumks.hasMoreElements()){
				String lm_key   = (String)enumks.nextElement();
				String lm_value = (String)resprop.get(lm_key);
				sbREQUBEproto.append(lm_key + " : " + lm_value).append("\n");
			}
		}
		sbREQUBEproto.append("\n");
		return sbREQUBEproto;
	}
	
	// alternative spec.
	public StringBuffer getSbREQUBEproto_se(){
		if(resprop.size() > 0) {
			Enumeration enumks = resprop.keys();
			while(enumks.hasMoreElements()){
				String lm_key   = (String)enumks.nextElement();
				String lm_value = (String)resprop.get(lm_key);
				sbREQUBEproto.append(lm_value);
			}
		}
		return sbREQUBEproto;
	}
	
	public boolean isBHttpflag() {
		return bHttpflag;
	}
	public void setBHttpflag(boolean httpflag) {
		bHttpflag = httpflag;
	}
}
