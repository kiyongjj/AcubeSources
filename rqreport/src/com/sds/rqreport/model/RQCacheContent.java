package com.sds.rqreport.model;

public class RQCacheContent {
    
    public RQCacheContent(){}
    
    private String query = "";
    private Object returnStrObject = null;
    private boolean stopStatus = false;
    
    /*
     * setter method to set Parameters.
     */
    public void setQuery(String query){
        this.query = query;
    }
    public void setReturnStrObject(String returnStrObject){
        this.returnStrObject = returnStrObject;
    }
    
    /*
     * getter method to get Parameters.
     */
    public String getQuery(){
        return query;
    }
    public Object getReturnStrObject(){
        return returnStrObject;
    }
    
    public void setStopStatus() {
    	stopStatus = true;
    }
    
    public void resetStopStatus() {
    	stopStatus = false;
    }
    
    public boolean isStopStatus() {
    	return stopStatus;
    }
}

