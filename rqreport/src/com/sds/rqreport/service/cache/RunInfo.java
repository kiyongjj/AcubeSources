package com.sds.rqreport.service.cache;

import java.util.*;
import java.io.*;

public class RunInfo {
	
    private String strAction = null;
    private String strDriver = null;
    private String strConn = null;
    private String strSid = null;
    private String strSpass = null;
    private String strSql = null;
    private String strXml  = null;
    private int iStmtidx = 0;
    private String jndiName = null;
    private String strDBInfo = null;
    private boolean bExecutor = false;
    private boolean bUseCache = false;
    private String strKey = null;
    private String[] docDS = null;
    
    private ArrayList sqlArrayList = null;
    private String[] sqlArray = null;
    private Writer out = null;

    public RunInfo() {}
    
    public RunInfo(String strkey, String jndiname, boolean cacheUseflag, String strSql, 
    		String strXml, boolean buseExecutor, String strDriver, String strConn, 
    		String strSid, String strSpass, int iStmtidx) {
    	this.strKey = strkey;
    	jndiName = jndiname;
    	bUseCache = cacheUseflag;
    	this.strSql = strSql;
    	this.strXml = strXml;
    	this.bExecutor = buseExecutor;
    	this.strDriver = strDriver;
    	this.strConn = strConn;
    	this.strSid = strSid;
    	this.strSpass = strSpass;
    	this.iStmtidx = iStmtidx;
    }
	public boolean isBExecutor() {
		return bExecutor;
	}

	public void setBExecutor(boolean executor) {
		bExecutor = executor;
	}

	public int getIStmtidx() {
		return iStmtidx;
	}

	public void setIStmtidx(int stmtidx) {
		iStmtidx = stmtidx;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public ArrayList getSqlArrayList() {
		return sqlArrayList;
	}

	public void setSqlArrayList(ArrayList sqlArray) {
		this.sqlArrayList = sqlArray;
	}

	public String getStrAction() {
		return strAction;
	}

	public void setStrAction(String strAction) {
		this.strAction = strAction;
	}

	public String getStrConn() {
		return strConn;
	}

	public void setStrConn(String strConn) {
		this.strConn = strConn;
	}

	public String getStrDBInfo() {
		return strDBInfo;
	}

	public void setStrDBInfo(String strDBInfo) {
		this.strDBInfo = strDBInfo;
	}

	public String getStrDriver() {
		return strDriver;
	}

	public void setStrDriver(String strDriver) {
		this.strDriver = strDriver;
	}

	public String getStrSid() {
		return strSid;
	}

	public void setStrSid(String strSid) {
		this.strSid = strSid;
	}

	public String getStrSpass() {
		return strSpass;
	}

	public void setStrSpass(String strSpass) {
		this.strSpass = strSpass;
	}

	public String getStrSql() {
		return strSql;
	}

	public void setStrSql(String strSql) {
		this.strSql = strSql;
	}

	public String getStrXml() {
		return strXml;
	}

	public void setStrXml(String strXml) {
		this.strXml = strXml;
	}

	public String[] getSqlArray() {
		return sqlArray;
	}

	public void setSqlArray(String[] sqlArray) {
		this.sqlArray = sqlArray;
	}

	public boolean isBUseCache() {
		return bUseCache;
	}

	public void setBUseCache(boolean useCache) {
		bUseCache = useCache;
	}

	public String getStrKey() {
		return strKey;
	}

	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}

	public String[] getDocDS() {
		return docDS;
	}

	public void setDocDS(String[] docDS) {
		this.docDS = docDS;
	}

	public Writer getOut() {
		return out;
	}

	public void setOut(Writer out) {
		this.out = out;
	}

	


}
