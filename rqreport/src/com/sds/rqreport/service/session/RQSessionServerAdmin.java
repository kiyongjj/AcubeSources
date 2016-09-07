package com.sds.rqreport.service.session;

import java.io.*;
import java.util.Vector;
import com.sds.rqreport.util.*;

public class RQSessionServerAdmin implements Runnable {

	String exepath;
	String sargs;
	int port = 7005;
	static Process sessionProcess = null;

	public RQSessionServerAdmin(String exepath,String sargs)
	{
		this.exepath = exepath;
		this.sargs = sargs;
	}

	public static void main(String[] args) {

	}

	public int getSessionCount()
	{
		String sessionid;
		SessionClient sc = new SessionClient(null, null);
		sc.putServer("127.0.0.1");
		sc.putPort(port);
		return sc.getSessionCount();
	}

	public boolean startServer()
	{
		// Check if Session is alive.
		if(getSessionCount() > 0)
		{
			return false;
		}

		if(sessionProcess != null)
		{
			return false;
		}
	    try {
	    	String[] args = null;
	    	sessionProcess = Runtime.getRuntime().exec(exepath + sargs);

            // any error message?
            StreamDisplay errorDisplay = new
            	StreamDisplay(sessionProcess.getErrorStream(), "ERROR");

            // any output?
            StreamDisplay outputDisplay = new
            	StreamDisplay(sessionProcess.getInputStream(), "OUTPUT");

            // kick them off
            errorDisplay.start();
            outputDisplay.start();


//	    	try {
//	    	try {
//	        	sessionProcess.wait(5000L);
//	        }
//	        catch (InterruptedException ex1) {
//	          L.warn("Process Wait() Timout", ex1);
//	        }
	      }
	      catch (IOException ex) {
//	        L.error("Cann't call the Shell.", ex);
	        return false;
	      }
		return true;
	}



	public synchronized boolean  stopServer()
	{
		SessionRequester sr = new SessionRequester("127.0.0.1", port);
		Vector args = new Vector();
		Vector rets = new Vector();
		try
		{
			//Terminate Server
			short code = sr.callFunction(6, args, rets);
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		if(sessionProcess != null)
		{
			sessionProcess.destroy();
			sessionProcess = null;
		}
		else
			return false;

		return true;
	}

	static Process getSessionProcess()
	{
		return sessionProcess;
	}

	Process execProc()
	{
		return null;
	}

	public void run() {
	}
	
}
