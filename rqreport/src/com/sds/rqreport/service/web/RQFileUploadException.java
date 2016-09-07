package com.sds.rqreport.service.web;

import java.io.File;
import java.util.*;

import com.sds.rqreport.Environment;

public class RQFileUploadException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	//Exception 이 발생할경우 삭제해야할 FileComponent 를 맴버로 가지고 있는다.
	RQFileComp oFileComp = null;
	public RQFileUploadException(){}
	
	/**
	 * Exception 이 발생할경우 처리해야할 file들과 reason 을 표시
	 * @param reason
	 */
	public RQFileUploadException(RQFileComp p_oFileComp, String reason){
		super(reason);
		oFileComp = p_oFileComp;
		removefiles();
	}
	
	public RQFileUploadException(RQFileComp p_oFileComp){
		oFileComp = p_oFileComp;
		removefiles();
	}
	
	public void removefiles(){
		Environment tenv = Environment.getInstance();
		Set keyset = oFileComp.getHM().keySet();
		if(keyset == null) return;
		if(keyset.size() == 0) return;
		Iterator it = keyset.iterator();
		while(it.hasNext()){
			if(tenv.rqxhistory.equalsIgnoreCase("yes")){
				String key = (String) it.next();
				File file = (File) oFileComp.getFileCom(key, 1);
				file.delete();
			}
		}
	}
}
