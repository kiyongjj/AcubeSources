package com.sds.rqreport.common;
import java.util.*;

public abstract class AbstractRequester {
	protected String iostring = "";
	protected RQDispatch dispatch = null;
	public String getErrorMessage(int code)
	{
		return "ErrorCode - " + code;
	}
	
	public int callFunction(int functionID,List args, List rets)
	{
		int res = -100;
		if(dispatch != null)
			res = dispatch.callByDispatch(functionID, args, rets);
		return res;		
	}
	public int request(String input, RQDispatch dispatch)
	{
		iostring = input;
		int id = getFunctionID();
		if(id < 1)
			return -1;
		Vector  args = getArgumentArray();
		if(args == null)
			return -1;
		int retSize = 0;
		Vector rets = new Vector();
		this.dispatch = dispatch;
		int res = callFunction(id, args, rets);
		iostring = "";
		iostring = makeResultString(res, rets);
		rets = null;
		args = null;
		return 0;				
	}
	public String getResponse()
	{
		return iostring;
	}
	abstract public String makeResultString(int res, Vector rets);
	abstract public Vector getArgumentArray();
	abstract public int getFunctionID();
	
}
