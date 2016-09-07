package com.sds.rqreport.service.cache;

import java.sql.SQLException;
import java.util.*;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.sds.rqreport.service.queryexecute.*;

public class PoolObject implements Runnable{
	
	private static Random random = new Random();
	private Integer index = null;
	private Logger L = Logger.getLogger("MANAGER");
	private RQGetDataIf obj = null;
	private String strKey = null;
	
	public PoolObject()
	{
		
	}
	public PoolObject(Integer i){
		this.index = i;
	}
	
	public Integer getIndex(){
		return index;
	}

	public void run(){
		switch(((RQGetDataIf)obj).classType) {
			case RQGetDataIf.OBJ_TYPE:
				((RQGetDataObj)obj).getRQResult();
				break;
			case RQGetDataIf.STR_TYPE:
				((RQGetDataStr)obj).getRQResult();
				break;
			case RQGetDataIf.XML_TYPE:

				try {
					((RQGetDataHierarchy)obj).getRQResult();
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
		}
	}
	
	public void setObject(Object obj) {
		this.obj = (RQGetDataIf)obj;
	}
	
	public RQGetDataIf getObject() {
		return obj;
	}
	
	public void Reset() {
		this.obj = null;
		this.strKey = null;
	}
	
	public void setKey(String strKey) {
		this.strKey = strKey;
	}
	
	public String getKey() {
		return strKey;
	}
}
