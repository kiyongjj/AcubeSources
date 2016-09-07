package com.sds.rqreport.service.session;

import java.util.Vector;

import com.sds.rqreport.common.*;
import com.sds.rqreport.util.*;

public class RQSessionRequester extends AbstractRequester {

	int functionID = 0;
	String[] functionArgs = null;
	int argSize = 0;
	/* (non-Javadoc)
	 * @see com.sds.reqube.service.admin.AbstractRequester#makeResultString(int, java.util.Vector)
	 */
	public RQSessionRequester()
	{
		functionArgs = new String[4];
	}
	
	public String makeResultString(int res, Vector rets) {
		StringBuffer result = new StringBuffer();
		result.append(res);
		if(res < 0)
		{
			result.append("\t");
			result.append(getErrorMessage(res));
			return result.toString();
		}
		if(rets != null )
		{
			int size = rets.size();
			for(int i = 0; i < size; ++i)
			{
				result.append("\t");
				result.append((String)rets.get(i));
			}		
		}
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.service.admin.AbstractRequester#getArgumentArray()
	 */
	public Vector getArgumentArray() {
		if(functionID < 1)
		{
			if(!parseString())
			{
				return null;
			}
		}
		Vector array = null;
		try
		{
			array = new Vector(4);
			for(int i=0; i < argSize; ++i)
			{
				array.add(functionArgs[i]);
			}
		}catch(Exception e)
		{
			return null;
		}
				
		return array;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.service.admin.AbstractRequester#getFunctionID()
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
		String[] args = RequbeUtil.split(iostring,"\t");
		if(args.length - 1 < 0)
			return false;
		try {
			if(args[0] == null || args[0].length() < 1)
			{
				functionID = -1;
				return false;
			}
			functionID = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			functionID = 0;
			return false;
		}
		argSize = args.length - 1;
		for(int i = 1; i < argSize + 1; ++i)
		{
			functionArgs[i - 1] = args[i];
		}
		return true;
			
	}
}
