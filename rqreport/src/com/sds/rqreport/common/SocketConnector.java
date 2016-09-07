package com.sds.rqreport.common;
import java.io.*;
import java.net.*;

public class SocketConnector {
	String server;
	int port;
	Socket s = null;
	public SocketConnector()
	{
	}
	public SocketConnector(String server,int port)
	{
		this.server = server;
		this.port = port; 
	}	
	public synchronized boolean connect()
	{
		try
		{
			if(s != null)
			{
				s.close();
				s = null;
			}
			s = new Socket(server, port);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	public synchronized InputStream getInputStream() throws IOException
	{
		if(s != null)
		{
			return s.getInputStream();
		}
		return null;
	}
	public synchronized OutputStream  getOutputStream() throws IOException
	{
		if(s != null)
		{
			return s.getOutputStream();
		}
		return null;
	}
	
	public synchronized void close()
	{
		try
		{
			if(s != null)
			{
				s.close();
				s = null;
			}
			
		}catch(IOException e)
		{
			e.printStackTrace();
			s = null;
		}		
	}
	
}
