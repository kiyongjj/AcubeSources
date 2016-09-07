package com.sds.rqreport.service;

public class Timer {
	
	private String name;
	private int totalcnt;
	private long startTime;
	private long endTime;
	
	public String start(String name) {
		this.name = name;
		startTime = System.currentTimeMillis();
		return " [ " + this.name +" : "+ startTime + " ] ";
	}
    
	public String end(String name) {
		this.name = name;
		endTime = System.currentTimeMillis();
		return " [ " + this.name +" : "+ endTime + " ] "+ " -- > elapsed time : " + (endTime - startTime) ;
	}

}
