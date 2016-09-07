package com.sds.rqreport.model.querynode;

import java.util.*;
import java.sql.*;

public class QueryHouse implements QueryNode{

	public static final int RQ_QUERY_UNKNOWN	= -1;
	public static final int RQ_QUERY_SQL 		= 0;
	public static final int RQ_QUERY_PROC		= 1;
	public static final int RQ_QUERY_RFCFUNC	= 2;
	public static final int RQ_QUERY_XML 		= 3;
	public static final int RQ_QUERY_PROCPARAM 	= 100;
	public static final int RQ_QUERY_PROCCURSOR = 101;
	public static final int RQ_QUERY_JCOPARAM 	= 102;
	
	private int nQueryType = RQ_QUERY_SQL;
    private String strSQLStmt = "";
    private int iDBIdx = 0;
    private int iSQLIdx = 0;
    private int iSQLOrder = 0;
    private int iRowCntNow = 0;
    private String[] strBindDataArr = null;
    private int nCol = 0;
    // for stored procedure
	private int nSPType = DatabaseMetaData.procedureResultUnknown;
	private int nParamCount = 0;
	// for SP Parameter 
	// 프로시저는 파라미터마다 DataView(SQLStmt)를 생성하므로 파라미터마다 Query House가 생성된다.
	private String SPName = "";
	private int nParamIdx = 0;
	private int nInOutType = DatabaseMetaData.procedureColumnUnknown;
	//	head info 는 각 쿼리(queryhouse)에서 한번만 찍기 위해 header info check flag 선언.
	private boolean bHIchkflag = false;
    /** 
     *  [
     *      [BindQryIdx, BindColIdx, BindColName],
     *      [BindQryIdx, BindColIdx, BindColName]
     *  ] 
     * */
    private ArrayList oBindSrc = null; 
    private String strFirstNodeIs = "";
    private String strLastNodeIs = "";  //Is Last Node ?
    private int iChildNodeCnt = 0;
    private ArrayList oChildSQLIdxArr = null;
    private int iParentSQLIdx = 0;
    
    //RFC FUNC.
    //RQ_QUERY_RFCFUNC
    private String JCOFncNm = "";
    private String JCOFncGrp = "";
    private String JCOFuncDscpt = "";
    //RQ_QUERY_JCOPARAM
    private String JCOPrmClss = "";  // E, I, T 
    private String JCOPrmNm = "";
    private String JCOPrmTbl = "";
    private String JCOPrmFld = "";
    private String JCOPrmABAPTp = "";  // C
    private String JCOPrmOpt = "";    		// X
    private String JCOPrmDscpt = "";
    private String JCOPrmTp = "";			// CHAR, STRUCTURE, TABLE
    private String RunVarType = "-1";
    private String JCOPrmValueSize = "";
    //RFC FUNC. end 
    
	public QueryHouse() {}

    public int getSQLOrder() {
		return iSQLOrder;
	}
	public void setSQLOrder(int order) {
		iSQLOrder = order;
	}
    public void setQueryType(int nType){
    	this.nQueryType = nType;
    }
    public void setSPName(String SPName){
    	this.SPName = SPName;
    }
    public void setSPType(int nType){
    	this.nSPType = nType;
    }
    public void setParamCount(int nCnt){
    	this.nParamCount = nCnt;
    }
    public void setSQLStmt(String SQLStmt){
        this.strSQLStmt = SQLStmt;
    }
    public void setDBIdx(int DBIdx){
        this.iDBIdx = DBIdx;
    }
    public void setSQLIdx(int SQLIdx){
        this.iSQLIdx = SQLIdx;
    }
    public void setOBindSrc(ArrayList oBindSrc){
        this.oBindSrc = oBindSrc;
    }
    
    public void setIRowCntNow(int iRowCntNow){
        this.iRowCntNow = iRowCntNow;
    }
    public void setFirstNodeIs(String strFirstNodeIs){
        this.strFirstNodeIs = strFirstNodeIs;
    } 
    public void setLastNodeIs(String strLastNodeIs){
        this.strLastNodeIs = strLastNodeIs;
    }   
    public void setIChildNodeCnt(int iChildNodeCnt){
        this.iChildNodeCnt = iChildNodeCnt;
    }
    public void setOChildSQLIdxArr(ArrayList oChildSQLIdxArr){
        this.oChildSQLIdxArr = oChildSQLIdxArr;
    }
    public void setIParentSQLIdx(int iParentSQLIdx){
        this.iParentSQLIdx = iParentSQLIdx;
    }
    public void setParamIndex(int nParamIndex){
    	this.nParamIdx = nParamIndex;
    }
    public void setInOutType(int nInOut){
    	this.nInOutType = nInOut;
    }

    //
    public int getQueryType(){
    	return this.nQueryType;
    }
    public String getSPName(){
    	return this.SPName;
    }
    public int getSPType(){
    	return this.nSPType;
    }
    public int getParamCount(){
    	return this.nParamCount;
    }
    public String getSQLStmt(){
        return this.strSQLStmt;
    }
    public int getDBIdx(){
        return this.iDBIdx;
    }
    public int getSQLIdx(){
        return this.iSQLIdx;
    }
    public ArrayList getOBindSrc(){ 
        return this.oBindSrc;
    }
    
    public int getIRowCntNow(){ 
        return this.iRowCntNow;
    }
    public String getFirstNodeIs(){ 
        return this.strFirstNodeIs;
    }
    public boolean isFirstNode(){
    	if (this.getFirstNodeIs().equals("true"))
    		return true;
    	else
    		return false;
    }
    public String getLastNodeIs(){ 
        return this.strLastNodeIs;
    }
    public boolean isLastNode(){
    	if (this.getLastNodeIs().equals("true"))
    		return true;
    	else
    		return false;
    }
    public int getIChildNodeCnt(){ 
        return this.iChildNodeCnt;
    }
    public ArrayList getOChildSQLIdxArr(){
        return this.oChildSQLIdxArr;
    }
    public int getIParentSQLIdx(){
        return this.iParentSQLIdx;
    }
    public int getParamIndex(){
    	return this.nParamIdx;
    }
    public int getInOutType(){
    	return this.nInOutType;
    }
   
    public HashMap getSource(){
        return null;
    }

    public String[] getBindDataArr(){
    	return this.strBindDataArr;
    }
    public void allocBindData(int n){
    	if (n > 0)
    	{
    		this.strBindDataArr = new String[n];
        	this.nCol = n;
    	}
    	else
    		this.strBindDataArr = null;
    }
    public String getBindData(int n){
    	if (n >= this.nCol)
    		return null;
    	else
    		return this.strBindDataArr[n];
    }
    public void setBindData(int n, String str){
    	if (n >= this.nCol)
    		return;
    	else
    		this.strBindDataArr[n] = str;
    }

    //RFC FUNC.
    //RQ_QUERY_RFCFUNC
    public String getJCOFncNm(){
    	return this.JCOFncNm;
    }
	public void setJCOFncNm(String JCOFncNm) {
		if ( JCOFncNm == null ) {
			JCOFncNm = "";
		}
		this.JCOFncNm = JCOFncNm;
	}

    public String getJCOFncGrp(){
    	return this.JCOFncGrp;
    }
	public void setJCOFncGrp(String JCOFncGrp) {
		if ( JCOFncGrp == null ) {
			JCOFncGrp = "";
		}
		this.JCOFncGrp = JCOFncGrp;
	}

    public String getJCOFuncDscpt(){
    	return this.JCOFuncDscpt;
    }
	public void setJCOFuncDscpt(String JCOFuncDscpt) {
		if ( JCOFuncDscpt == null ) {
			JCOFuncDscpt = "";
		}
		this.JCOFuncDscpt = JCOFuncDscpt;
	}

    //RQ_QUERY_JCOPARAM
    public String getJCOPrmClss(){
    	return this.JCOPrmClss;
    }
	public void setJCOPrmClss(String JCOPrmClss) {
		if ( JCOPrmClss == null ) {
			JCOPrmClss = "";
		}
		this.JCOPrmClss = JCOPrmClss;
	}

    public String getJCOPrmNm(){
    	return this.JCOPrmNm;
    }
	public void setJCOPrmNm(String JCOPrmNm) {
		if ( JCOPrmNm == null ) {
			JCOPrmNm = "";
		}
		this.JCOPrmNm = JCOPrmNm;
	}

    public String getJCOPrmTbl(){
    	return this.JCOPrmTbl;
    }
	public void setJCOPrmTbl(String JCOPrmTbl) {
		if ( JCOPrmTbl == null ) {
			JCOPrmTbl = "";
		}
		this.JCOPrmTbl = JCOPrmTbl;
	}

    public String getJCOPrmFld(){
    	return this.JCOPrmFld;
    }
	public void setJCOPrmFld(String JCOPrmFld) {
		if ( JCOPrmFld == null ) {
			JCOPrmFld = "";
		}
		this.JCOPrmFld = JCOPrmFld;
	}

    public String getJCOPrmABAPTp(){
    	return this.JCOPrmABAPTp;
    }
	public void setJCOPrmABAPTp(String JCOPrmABAPTp) {
		if ( JCOPrmABAPTp == null ) {
			JCOPrmABAPTp = "";
		}
		this.JCOPrmABAPTp = JCOPrmABAPTp;
	}

    public String getJCOPrmOpt(){
    	return this.JCOPrmOpt;
    }
	public void setJCOPrmOpt(String JCOPrmOpt) {
		if ( JCOPrmOpt == null ) {
			JCOPrmOpt = "";
		}
		this.JCOPrmOpt = JCOPrmOpt;
	}

    public String getJCOPrmDscpt(){
    	return this.JCOPrmDscpt;
    }
	public void setJCOPrmDscpt(String JCOPrmDscpt) {
		if ( JCOPrmDscpt == null ) {
			JCOPrmDscpt = "";
		}
		this.JCOPrmDscpt = JCOPrmDscpt;
	}

    public String getJCOPrmTp(){
    	return this.JCOPrmTp;
    }
	public void setJCOPrmTp(String JCOPrmTp) {
		if ( JCOPrmTp == null ) {
			JCOPrmTp = "";
		}
		this.JCOPrmTp = JCOPrmTp;
	}
	
    public String getRunVarType(){
    	return this.RunVarType;
    }
	public void setRunVarType(String RunVarType) {
		if ( RunVarType == null ) {
			RunVarType = "-1";
		}
		this.RunVarType = RunVarType;
	}
	
    public String getJCOPrmValueSize(){
    	return this.JCOPrmValueSize;
    }
	public void setJCOPrmValueSize(String JCOPrmValueSize) {
		if (JCOPrmValueSize == null ) {
			JCOPrmValueSize = "";
		}
		this.JCOPrmValueSize = JCOPrmValueSize;
	}
    //RFC FUNC. end 
	
	public boolean getHIchkflag(){
		return bHIchkflag;
	}
	public void setHIchkflag(boolean flag){
		this.bHIchkflag = flag;
	}
	
}
