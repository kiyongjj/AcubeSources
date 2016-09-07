package com.sds.rqreport.repository;

import java.io.IOException;
import java.net.*;

import com.sds.rqreport.service.session.SessionClient;
import com.sds.rqreport.util.StreamDisplay;

public class RQRepositorySyncAdmin {
	String exepath;
	String sargs;
	int port = 7005;
	static Process syncProcess = null;

	public RQRepositorySyncAdmin(String exepath,String sargs)
	{
		this.exepath = exepath;
		this.sargs = sargs;
	}

	public static void main(String[] args) {

	}

	public static boolean isActive()
	{
		Socket s = new Socket();


		return false;
	}

	public boolean startServer()
	{
		if(syncProcess != null)
		{
			return false;
		}
	    try {
	    	String[] args = null;
	    	syncProcess = Runtime.getRuntime().exec(exepath + sargs);

            // any error message?
            StreamDisplay errorDisplay = new
            	StreamDisplay(syncProcess.getErrorStream(), "ERROR");

            // any output?
            StreamDisplay outputDisplay = new
            	StreamDisplay(syncProcess.getInputStream(), "OUTPUT");

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
		if(syncProcess != null)
		{
			syncProcess.destroy();
			syncProcess = null;
		}
		else
			return false;

		return true;
	}

	static Process getSessionProcess()
	{
		return syncProcess;
	}

	Process execProc()
	{
		return null;
	}


	public void run() {


	}
}
