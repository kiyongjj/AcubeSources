package com.sds.rqreport.model;

import com.sds.rqreport.util.Encoding;

public class RQSchedulerListModel {

	private String strUserId;
	private String[] doclist;
	private String runvards;
	private String resultfileds;
	
	private String lm_serverCharset;
	private String lm_RQCharset;
	
	//
	private String scheduleid;
	private String mailinglist;
	private String doc;
	private String notification;
	private String attachresult;
	private String userattach;
	private String emailform;
	private int runinfoid;
	
	public int getRuninfoid() {
		return runinfoid;
	}
	public void setRuninfoid(int runinfoid) {
		this.runinfoid = runinfoid;
	}
	public String getAttachresult() {
		return attachresult;
	}
	public void setAttachresult(String attachresult) {
		this.attachresult = attachresult;
	}
	public RQSchedulerListModel() {
		Encoding enc = new Encoding();
		lm_serverCharset = enc.getServerCharset();
		lm_RQCharset = enc.getRQCharset();
	}
	public String getStrUserId() {
		return strUserId;
	}
	public void setStrUserId(String strUserId) {
		if(strUserId != null && strUserId != "")
			strUserId = strUserId.trim();
		this.strUserId = strUserId;
	}
	
	public String[] getDoclist() {
		return doclist;
	}
	public void setDoclist(String[] doclist) {
		this.doclist = doclist;
	}
	
	public String getRunvards() {
		return runvards;
	}
	public void setRunvards(String runvards) {
		this.runvards = runvards;
	}
	public String getResultfileds() {
		return resultfileds;
	}
	public void setResultfileds(String resultfileds) {
		this.resultfileds = resultfileds;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getEmailform() {
		return emailform;
	}
	public void setEmailform(String emailform) {
		this.emailform = emailform;
	}
	public String getMailinglist() {
		return mailinglist;
	}
	public void setMailinglist(String mailinglist) {
		this.mailinglist = mailinglist;
	}
	public String getNotification() {
		return notification;
	}
	public void setNotification(String notification) {
		this.notification = notification;
	}
	public String getScheduleid() {
		return scheduleid;
	}
	public void setScheduleid(String scheduleid) {
		this.scheduleid = scheduleid;
	}
	public String getUserattach() {
		return userattach;
	}
	public void setUserattach(String userattach) {
		this.userattach = userattach;
	}
}
