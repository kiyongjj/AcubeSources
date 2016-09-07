package com.sds.rqreport.service.web;

import java.util.*;

public class RQRawdataS {
	
	private HashMap rawDList = null;
	private static RQRawdataS singleton = new RQRawdataS();
    private RQRawdataS() {
    	// instance gen.
    	rawDList = new HashMap();
    }
    
    public static RQRawdataS getInstance() {
        return singleton;
    }
    
    public void setDocIntoRawDList(String uid, RQSavePdfparam pdfobj){
    	rawDList.put(uid, pdfobj);
    }
    
    public RQSavePdfparam getDocFromRawDList(String uid){
    	return (RQSavePdfparam) rawDList.get(uid);
    }
    
    public HashMap getList(){
    	return rawDList;
    }
    
    public void setResetRawDList(){
    	rawDList = null;
    }
    
}
