package com.sds.rqreport.model;

import java.io.Serializable;

public class RQDocStatParam implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String strSearchcon   = "";
	private String strBasesearch  = "";
	private String strDuringstart = "";
	private String strDuringend   = "";
	private String strDocsearch   = ""; 
	
	public String getStrSearchcon() {
		return strSearchcon;
	}
	public void setStrSearchcon(String strSearchcon) {
		this.strSearchcon = strSearchcon;
	}
	public String getStrBasesearch() {
		return strBasesearch;
	}
	public void setStrBasesearch(String strBasesearch) {
		this.strBasesearch = strBasesearch;
	}
	public String getStrDuringstart() {
		return strDuringstart;
	}
	public void setStrDuringstart(String strDuringstart) {
		this.strDuringstart = strDuringstart;
	}
	public String getStrDuringend() {
		return strDuringend;
	}
	public void setStrDuringend(String strDuringend) {
		this.strDuringend = strDuringend;
	}
	public String getStrDocsearch() {
		return strDocsearch;
	}
	public void setStrDocsearch(String strDocsearch) {
		this.strDocsearch = strDocsearch;
	}
}
