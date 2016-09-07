package com.sds.rqreport.repository;

import java.util.*;

import com.sds.rqreport.common.AbstractRequester;

public class RSRequester extends AbstractRequester {

	Vector args = null;

	public Vector getArgumentArray() {
		return args;
	}

	public int getFunctionID() {
		StringTokenizer st = new StringTokenizer(iostring,"\t");
		int funcnum = -1;
		if(st.hasMoreTokens())
		{
			funcnum = Integer.parseInt((String)st.nextToken());
		}
		this.args = new Vector();
		while(st.hasMoreTokens())
		{
			args.add(st.nextToken());
		}
		return funcnum;
	}

	public String makeResultString(int res, Vector rets) {
		StringBuffer resultstr = new StringBuffer();
		resultstr.append(res);
		int size = rets.size();
		for(int i=0; i < size; ++i)
		{
			resultstr.append("\t");
			resultstr.append((String)rets.get(i));
		}
		return resultstr.toString();
	}

}
