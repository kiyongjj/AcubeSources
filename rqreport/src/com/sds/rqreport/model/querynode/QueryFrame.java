package com.sds.rqreport.model.querynode;

import java.util.*;

public class QueryFrame implements QueryNode{
    
    private int iSQLCnt = 0;   // 0 부터 시작
    private ArrayList rootSQLarr = new ArrayList();
    
    public QueryFrame(){}
    
    public int getISQLCnt(){
        return this.iSQLCnt;
    }
    
    public void setISQLCnt(int iSQLCnt) {
        this.iSQLCnt = iSQLCnt;
    }

    public String[] getRootSQLIdx(){
    	int size = rootSQLarr.size();
    	String[] sqlidx = new String[size];
    	for(int i = 0 ; i < size ; i++){
    		sqlidx[i] =  (String)rootSQLarr.get(i) ;
    	}
    	return sqlidx;
    }
    
    public void setRootSQLIdx(String strSQLIdx){
    	rootSQLarr.add(strSQLIdx);
    }
    
    private HashMap frame = new HashMap();
    
    public QueryNode addToframe(String strSQLIdx, QueryNode querynode){ //iSQLIdx Object key 변환 1.5
        frame.put(strSQLIdx, querynode);
        return this;
    }
    
    public HashMap getSource(){
        return frame;
    }
    
}
