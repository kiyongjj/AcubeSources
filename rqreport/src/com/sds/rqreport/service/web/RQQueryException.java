package com.sds.rqreport.service.web;

import java.sql.SQLException;

public class RQQueryException extends SQLException {

	private static final long serialVersionUID = 1L;
	
	public RQQueryException(){
		
	}
	
	public RQQueryException(String msg){
		super(msg);
	}
	
}
