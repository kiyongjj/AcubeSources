package com.sds.rqreport.common;

import java.net.*;

public abstract class RQServerManager {

	int port;
	RQDispatch dispatch = null;
	AbstractRequester ar = null;
	public RQServerManager() {
		super();
	}
	public void setActor(RQDispatch dispatch)
	{
		this.dispatch = dispatch;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public boolean run()
	{
		ServerSocket ss = null;
		boolean acceptLoop = true;
		Socket s = null;
		ServerActor sa = null;

		try {
			ss = new ServerSocket(port, 10);
		}
	   	catch (Exception e) {
		 ss = null;
		 e.printStackTrace();
		 return false;
	   }

	   while (acceptLoop) {
		 try {
		   s = ss.accept();   // Accept connections
		 }
		 catch (Exception e) {
		   s = null;
		   // L.fatal("A Fatal Error has occurred in accepting socket", e);
		   return false;
		 }
		ar = getRequester();
		 if (ar == null || dispatch == null)
		  return false;

		 sa = new ServerActor(s, ar, dispatch );
		 ar = null;
		 sa.start();
		 sa = null;
	   }
	   return false;
	}

	public abstract AbstractRequester getRequester();
}
