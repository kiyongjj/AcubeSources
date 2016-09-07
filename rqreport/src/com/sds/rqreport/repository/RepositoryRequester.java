package com.sds.rqreport.repository;

import java.util.AbstractList;
import java.util.Calendar;
import java.util.Vector;

import com.sds.rqreport.common.*;

public class RepositoryRequester extends AbstractRequester {

	int functionID = 0;
	Vector functionArgs = null;
	int argSize = 0;
	public RepositoryRequester() {
		super();
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
				de.addParam((String)obj);
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
		}

		return de.getString();
	}

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
