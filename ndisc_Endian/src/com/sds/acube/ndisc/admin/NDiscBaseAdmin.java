package com.sds.acube.ndisc.admin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sds.acube.ndisc.napi.NApi;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;

public class NDiscBaseAdmin {
	protected NApi napi = null;
	protected StorageIF storage = null;
	protected static LoggerIF logger = null;
	
	public NDiscBaseAdmin(){
		try{
			napi = new NApi();
			
			logger = (LoggerIF) DynamicClassLoader.createInstance(NDCommon.MTS_LOGGER);
			logger.initLogger();
			
			storage = (StorageIF) DynamicClassLoader.createInstance(NDCommon.MTS_STORAGE, logger);
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	protected String getCreateDate(){
		String currentDate = "";
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		currentDate = dateFormat.format(calendar.getTime());	
		
		return currentDate;
	}
}
