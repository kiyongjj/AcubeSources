package com.sds.rqreport.model;

public class RQActionModel {
    
    public RQActionModel(){}
    
    private String action = "";
    private String driver = "";
    private String conn = "";
    private String sid = "";
    private String spass = "";
    private String sql = "";
    private String strXml = "";
    private int stmtidx;
    private String strKey = "";
    private String strDBInfo = "";
    private String encqry = "";
    private String doc = "";
    private String runvar = "";
    private int dbidx = -1;
    /*
    * setter method to set Parameters.
    */
    public void setAction(String p_action){
        action =   p_action;   
    }
    public void setDriver(String p_driver){
        driver =   p_driver;   
    }
    public void setConn(String p_conn){
        conn =   p_conn;   
    }
    public void setSid(String p_sid){
        sid =   p_sid;   
    }
    public void setSpass(String p_spass){
        spass =   p_spass;   
    }
    public void setSql(String p_sql){
        sql =   p_sql;   
    }
    public void setStrXml(String p_strXml){
        strXml =   p_strXml;   
    }
    public void setStmtidx(int p_stmtidx){
        stmtidx =   p_stmtidx;   
    }
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}
    public void setStrDBInfo(String p_strDBInfo){
    	strDBInfo =   p_strDBInfo;   
    }
	public void setEncqry(String encqry) {
		this.encqry = encqry;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public void setRunvar(String runvar){
		this.runvar = runvar;
	}
	public void setDbidx(int dbidx){
		this.dbidx = dbidx;
	}
    /*
    * getter method to get Parameters.
    */
    public String getAction(){
        return action; 
    }
    public String getDriver(){
        return driver; 
    }
    public String getConn(){
        return conn; 
    }
    public String getSid(){
        return sid; 
    }
    public String getSpass(){
        return spass; 
    }
    public String getSql(){
        return sql; 
    }
    public String getStrXml(){
        return strXml; 
    }
    public int getStmtidx(){
        return stmtidx; 
    }
	public String getStrKey() {
		return strKey;
	}
	public String getStrDBInfo() {
		return strDBInfo;
	}
	public String getEncqry() {
		return encqry;
	}
	public String getDoc() {
		return doc;
	}
	public String getRunvar() {
		return runvar;
	}
	public int getDbidx(){
		return dbidx;
	}
	
}
