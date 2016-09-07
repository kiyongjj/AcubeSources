package com.sds.rqreport.service.web;

import java.util.*;

public class RQDocStatGetRC {
	
	private int m_rowcnt = 0; // not applicable to MultiThread
	private HashMap hm = null;
	
    private static RQDocStatGetRC rqDocStatGetRC = null; // RC ? rowcount!
    private RQDocStatGetRC() {                                 
        //System.out.println("New Instance");
    	hm = new HashMap();
    }
    public static RQDocStatGetRC getInstance() {
    	if(rqDocStatGetRC == null){
    		rqDocStatGetRC = new RQDocStatGetRC();
    	}
        return rqDocStatGetRC;
    }
    
    public static void setNullRQDocStatGetRC(){
    	rqDocStatGetRC = null;
    }
    
    public int getM_rowcnt() {
		return m_rowcnt;
	}
	public void setM_rowcnt(int m_rowcnt) {
		this.m_rowcnt = m_rowcnt;
	}
	
	public String getRCinHM(String threadname){
		return (String)hm.get(threadname);
	}
	public void setRCinHM(String threadname, String rowcnt){
		hm.put(threadname, rowcnt);
	}
	public void setNullrowcnt(String threadname){
		hm.remove(threadname);
		//System.out.println("#####################" + hm.size());
	}
	
}
