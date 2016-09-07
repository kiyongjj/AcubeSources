package com.sds.rqreport.service.fileservice;

public class RQFileException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public RQFileException(){
	}
	public RQFileException(String reason){
		super(reason);
	}
}
