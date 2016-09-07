package com.sds.rqreport.service.web;

import java.io.*;
import java.util.*;

import com.oreilly.servlet.multipart.*;

public class RQFileComp {

	private HashMap hm = new HashMap();
	
	public void setFileComp(String p_key, FilePart p_filePart, File p_file){
		ArrayList lm_arr = new ArrayList();
		lm_arr.add(p_filePart);
		lm_arr.add(p_file);
		
		hm.put(p_key, lm_arr);
	}
	
	public Object getFileCom(String p_key, int p_which){
		Object obj = null;
		ArrayList arr = (ArrayList) hm.get(p_key);
		obj = arr.get(p_which);
		return obj;
	}
	
	public int getHMsize(){
		return hm.size();
	}
	
	public HashMap getHM(){
		return hm;
	}
}
