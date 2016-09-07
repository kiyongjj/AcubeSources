package com.sds.rqreport.site.ems;

public class RQEMSLogData implements RQSiteLogDataInterface {
	
	private String primarykey = "";
	private String userid     = "";
	private String docpath    = "";
	private String eventtype  = "";
	private long   eventtime  = 0;
	private String runtime    = "";

	public String getPrimarykey() {
		return primarykey;
	}
	public void setPrimarykey(String primarykey) {
		this.primarykey = primarykey;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDocpath() {
		return docpath;
	}
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
	public String getEventtype() {
		return eventtype;
	}
	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}
	public long getEventtime() {
		return eventtime;
	}
	public void setEventtime(long eventtime) {
		this.eventtime = eventtime;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
}
