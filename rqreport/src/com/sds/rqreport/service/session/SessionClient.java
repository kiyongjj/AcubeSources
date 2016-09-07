package com.sds.rqreport.service.session;
import java.util.*;
import javax.servlet.http.*;

import com.sds.rqreport.util.*;

public class SessionClient {
	String server;
	int port;
	String sessionID;
	SocketConnector sc = null;
	HttpServletRequest request = null;
	HttpServletResponse response = null;
	public SessionClient()
	{
		server = "127.0.0.1";
		port = 7005;
	
	}
	public void putServer(String server)
	{
		this.server = server;
	}
	public String getServer()
	{
		return server;
	}
	
	public void putPort(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return port;
	}
	
	public SessionClient(HttpServletRequest request, HttpServletResponse response)
	{
		server = "127.0.0.1";
		port = 7005;
		this.request = request;
		this.response = response;
	}
	protected void setSession(String sessionID)
	{
		if(request != null)
		{
			HttpSession session = request.getSession();
			session.setAttribute("RQSESSIONID", sessionID);	
		}
		else 
			return;
	}
	protected void setCookie(String sessionID)
	{
		Cookie[] ck = request.getCookies();
		Cookie findCk = null;
		int find = -1;
		if(ck != null)
		{
			for(int i = 0; i < ck.length; ++i)
			{
				if(ck[i].getName().equals("RQSESSIONID"))
				{
					findCk = ck[i];
					break;
				}
			}
	
		}
		if(find > -1)
		{
			findCk.setValue(sessionID);
		}
		else
		{
			findCk = new Cookie("RQSESSIONID",sessionID);
		}
		response.addCookie(findCk);
	}
	protected String getSessionIDfromSession()
	{
		if(request != null)
		{
			HttpSession session = request.getSession();
			return (String)session.getAttribute("RQSESSIONID");	
		}
		else 
			return null;
	}
	protected String getSessionIDfromCookie()
	{
		if(request == null || response == null)
			return null;
		Cookie[] ck = request.getCookies();
		Cookie findCk = null;
		int find = -1;
		
		if(ck != null)
		{
			for(int i = 0; i < ck.length; ++i)
			{
				if(ck[i].getName().equals("RQSESSIONID"))
				{
					findCk = ck[i];
					break;
				}
			}
	
		}
		
		if(find > -1)
		{
			return	findCk.getValue();
		}
		else
		{
			return null;
		}
		 
	}
		
	public void putSessionID(String sessionID)
	{
		this.sessionID = sessionID;
		if(request == null)
			return;
		//setCookie(sessionID);
		setSession(sessionID);
		
	}

	
	public String getSessionID()
	{
		return sessionID;
	}
		
	public String makeSession()
	{
		return makeSession(1800000);		
	}
	public synchronized String makeSession(long timeout)
	{
		String sessID = getSessionIDfromSession();
		if(sessID != null && sessID.length() > 0)
		{
			sessionID = sessID;
			return sessionID;
		}
//		sc = new SocketConnector(server,port);
		SessionRequester sr = new SessionRequester(server,port);
		Vector args = new Vector();
		args.add("" + timeout);
		Vector rets = new Vector();
		try
		{
			short code = sr.callFunction(1, args, rets);
			if(code == 0)
			{
				sessionID = (String)rets.get(0);
				setSession(sessionID);
				return sessionID;
			}
			else 
				return "";
		}catch(Exception e)
		{
			e.printStackTrace();
			return "Error-ioexception";
		}
		
	}
	
	public synchronized boolean setParameter(String key, String val)
	{
//		sc = new SocketConnector(server,port);
		SessionRequester sr = new SessionRequester(server,port);
		Vector args = new Vector();
		args.add(sessionID);
		args.add(key);
		args.add(val);
		Vector rets = new Vector();
		try
		{
			short code = sr.callFunction(4, args, rets);
			if(code == 0)
			{
				return true;
			}
			else 
				return false;
		}catch(Exception e)
		{
			return false;
		}		

	}
	public synchronized String getParameters()
	{
//		sc = new SocketConnector(server,port);
		SessionRequester sr = new SessionRequester(server,port);
		Vector args = new Vector();
		args.add(sessionID);
		Vector rets = new Vector();
		try
		{
			short code = sr.callFunction(3, args, rets);
			if(code == 0)
			{
				return (String)rets.get(0);
			}
			else 
				return null;
		}catch(Exception e)
		{
			return null;
		}			
	}
	
	public synchronized int getSessionCount()
	{
//		sc = new SocketConnector(server,port);
		SessionRequester sr = new SessionRequester(server,port);
		Vector args = new Vector();
//		args.add(sessionID);
		Vector rets = new Vector();
		try
		{
			short code = sr.callFunction(5, args, rets);
			if(code == 0)
			{
				return Integer.parseInt((String)rets.get(0));
			}
			else 
				return -1;
		}catch(Exception e)
		{
			return -1;
		}			
	}	
	
	protected synchronized void removeFromSession()
	{
		if(request == null)
			return;
		HttpSession session = request.getSession();
		session.removeAttribute("RQSESSIONID");
	}
	
	public synchronized boolean close()
	{
		
//		sc = new SocketConnector(server,port);
		SessionRequester sr = new SessionRequester(server,port);
		Vector args = new Vector();
		args.add(sessionID);
		Vector rets = new Vector();
		try
		{
			short code = sr.callFunction(2, args, rets);
			if(code == 0)
			{
				return true;
			}
			else 
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}		
	}
	
	public String getParameter(String key)
	{
		String values = getParameters();
		if(values == null)
			return null; 
		String vallist[] = RequbeUtil.split(values, ";");
		if(vallist != null && vallist.length > 0)
		{
			for(int i = 0; i < vallist.length;++i)
			{
				if(vallist[i].startsWith(key + "="))
				{
					return decode(vallist[i].substring(key.length() + 1 ));
				}
			}
		}
		return null;
	}
	
	public static int pow(int n, int p) {
	  int ret = 1;
	  for (int i = 0; i < p; ++i)
		ret *= n;
	  return ret;
	}

	public static String decode(String str)
	{
	  int len = str.length();
	  char strBuffer[] = new char[len];
	  int i = 0;
	  int n = 0;
	  int p = 0;
	  while (i < len) {
		boolean end = false;
		char wcs = '\0';
		p = 0;
		while (end == false) {
		  char s = str.charAt(i++);
		  if ('a' <= s && s <= 'z') {
			wcs += pow(26, p++) * (s - 'a');
			end = true;
		  }
		  else
			wcs += pow(26, p++) * (s - 'A');
		}
		strBuffer[n++] = wcs;
	  }
	  strBuffer[n] = '\0';
	  String ret = new String(strBuffer,0,n);
	  return ret;
	}	
}
