package com.sds.rqreport.repository;

import java.util.*;

import com.sds.rqreport.common.AbstractClientRequester;

public class RSClientRequester extends AbstractClientRequester {

	public RSClientRequester(String server, int port)
	{
		super(server,port);
	}
	public String makeSendString(int id, List args) {
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		Iterator it = args.iterator();
		while(it.hasNext())
		{
			sb.append("\t");
			sb.append(it.next());
		}
		return sb.toString();
	}

	public short parseReceiveString(String str, List rets) {
		StringTokenizer st = new StringTokenizer(str,"\\t");
		if(st.hasMoreTokens())
		{
			Integer.parseInt(st.nextToken());
		}
		else
			return -1;
		while(st.hasMoreTokens())
		{
			rets.add(st.nextToken());
		}
		return 0;
	}

}
