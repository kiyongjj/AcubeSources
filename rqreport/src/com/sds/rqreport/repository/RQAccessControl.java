package com.sds.rqreport.repository;

public interface RQAccessControl {
	public boolean accept(Object securitycontext, Object obj);

}
