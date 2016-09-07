package com.sds.rqreport.dataagent;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.common.*;
import com.sds.rqreport.service.queryexecute.RQGetDataIf;
import com.sds.rqreport.util.RequbeUtil;

public class DARequester extends AbstractRequester {
	private Logger log = Logger.getLogger("RQDARequester");
	int functionID = 0;
	Vector functionArgs = null;
	int argSize = 0;
	/* (non-Javadoc)
	 * @see com.sds.reqube.common.AbstractRequester#makeResultString(int, java.util.Vector)
	 */
	public DARequester()
	{
		
	}
	
	public String makeResultString(int res, Vector rets) {
		DataEncode de = new DataEncode();
		de.addResult((short)res);
		if (res < 0 )
		{
			if(rets.size() > 0)
			{
				de.addError((String)rets.get(0));
			}
			else
				de.addError("Error Occured");
		}
		int size = rets.size();
		for (int i = 0; i < size; ++i){
			Object obj = rets.get(i);
			
			if(obj instanceof String)
			{
				//de.addParam((String)obj);
				checkImgParam(de, obj);
			}else if(obj instanceof Integer)
			{
				de.addParam(((Integer)obj).intValue());
			}else if(obj instanceof AbstractList)
			{
				de.addParam((AbstractList)obj);
			}else if(obj instanceof Calendar)
			{
				de.addParam((Calendar)obj);
			}else if(obj instanceof byte[])
			{
				de.addParam((byte[])obj);
			}else if(obj instanceof Float)
			{
				de.addParam(((Float)obj).floatValue());
			}
			obj = null;
		}
		//log.debug("DATASET" + de.getString()); /// log dataset
		return de.getString();
	}
	
	public void checkImgParam(DataEncode de, Object obj){
		String lm_str  = (String) obj;
		int lm_flagImg = lm_str.indexOf(RQGetDataIf.BLOB_SEP);
		if(lm_flagImg != -1){
			String lm_data = lm_str.substring(0, lm_flagImg);
			String lm_Imgdata  = lm_str.substring(lm_flagImg, lm_str.length());
			// check Img //////////////////////////////////////////////////////////////
			/*
			try{
				FileOutputStream fw = new FileOutputStream("D:\\checkImg.jpg");
				String lm_Img = lm_Imgdata.substring(1, lm_Imgdata.length());
				fw.write(lm_Img.getBytes("8859_1"));
				fw.close();
			}catch(IOException e){}
			*/
			///////////////////////////////////////////////////////////////////////////
			de.addParam(lm_data);
			de.sendstr.append(lm_Imgdata);
		}else{
			de.addParam((String)obj);
		}
		lm_str = null;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.AbstractRequester#getArgumentArray()
	 */
	public Vector getArgumentArray() {
		if(functionID < 1)
		{
			if(!parseString())
			{
				return null;
			}
		}
		return functionArgs;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.AbstractRequester#getFunctionID()
	 */
	public int getFunctionID() {
		if(functionID < 1)
		{
			if(!parseString())
			{
				functionID = -1;
			}
		}
		return functionID;
	}
	
	protected boolean parseString()
	{
		if(iostring == null || iostring.length() < 1)
		{
			
			return false; 
		}
		
		
		DataDecode de = new DataDecode(iostring);
		functionID = (int)de.getRequestType(); 
		functionArgs = de.getArgumentVector(); 
		return true;
	}
}
