package com.sds.rqreport.service.web;

import com.sds.rqreport.model.*;
import com.sds.rqreport.repository.DocRepository;

public class RQControl {
	
	public static UserModel nowUserModel = null;
	
	public RQControl(){}
	
	public RQControl(UserModel UM){
		RQControl.nowUserModel = UM;
	}
	
	public int RQmakeFoler(String fullpath) throws Exception{
		//fullpath = pathIs+foldername
		int res = 0;
		
		DocRepository docRep = new DocRepository();
		res = docRep.makeFolder(fullpath);
		docRep.commitAll();
		
		RQControl.nowUserModel = null;
		return res;
	}
	
}
