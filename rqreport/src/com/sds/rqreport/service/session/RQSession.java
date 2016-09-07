package com.sds.rqreport.service.session;

import java.util.*;

import com.sds.rqreport.util.*;
public class RQSession {
	protected	String id;
	protected boolean bExpired;
	protected long lastAccessed = 0;
	protected long expire = 0;
	protected String values;
	public RQSession()
	{
		lastAccessed = System.currentTimeMillis();
		expire = 1800000;
		values = "";
	}
	
	public RQSession(String strID, long timeout)
	{
		lastAccessed = System.currentTimeMillis();
		expire = timeout; // 30 min
		id = strID;		
		values = "";
	}
	public boolean checkSession()
	{
		if(!bExpired && System.currentTimeMillis() - lastAccessed >= expire)
		{
			bExpired = true;
			values = "";
			return false;
		}
		bExpired = false;
		return true;
	}	
	public String getID()
	{
		return id;
	}
	public String encode(String strVal)
	{
		return Encrypter.encrypt26(strVal);
	}
	public String decode(String strVal)
	{
		return Decrypter.decrypt26(strVal);
	}
	public int removeValue(String strKey)
	{
		if(!checkSession())
		{
			return -101;//ST_EXPIRED;
		}else
		{
			lastAccessed = System.currentTimeMillis();
		}

		String strVal = values;
		String strKeyVal = strKey;
		strKeyVal += "=";
		int pos1 = strVal.indexOf(strKeyVal);
		int pos2 = 0;
		int len = strKeyVal.length();
		int len2 = strVal.length();
		while(pos1 > 0 && strKeyVal.charAt(pos1 - 1) != ';')
		{
			pos1 = strVal.indexOf(strKeyVal, pos1 + len);
		}
	
		if(pos1 < 0)
		{
			return 0; //ST_OK
		}else
		{
			pos2 = strVal.indexOf(";", pos1 + len);
		}
	
		if(pos2 < 0)
		{
			pos2 = len2;
		}

		values = strVal.substring(0,pos1) + strVal.substring(pos2, len2 - pos2);
		return 0; //ST_OK;		
	}
	

	public int getValues(String[] value)
	{
		if(!checkSession())
		{
			value[0] = null;
			return -101;//ST_EXPIRED;
		}
		else
		{
			lastAccessed = System.currentTimeMillis();
		}
		value[0] = values;
		return 0;		
	}
	
	public int setValue(String key, String value)
	{
		if(!checkSession())
		{
			values = "";
			return -101;
		}
		else 
			lastAccessed = System.currentTimeMillis();
		if(values.length() > 1)
		{
			values += ";";
		}
		// To add Value but To do: no duplication
		 
		values += key;
		values += "=";

		String strEnc = encode(value);
		values += strEnc;
		return 0;//ST_OK;
	}

}
