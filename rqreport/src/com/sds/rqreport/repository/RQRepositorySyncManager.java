package com.sds.rqreport.repository;

import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.MissingResourceException;

import com.sds.rqreport.common.AbstractRequester;
import com.sds.rqreport.common.RQDispatch;
import com.sds.rqreport.common.RQServerManager;
import com.sds.rqreport.common.ServerActor;
import com.sds.rqreport.service.session.RQSessionManager;
import com.sds.rqreport.service.session.RQSessionRequester;
import com.sds.rqreport.service.session.SessionServerManager;
import com.sds.rqreport.util.PropertyLoader;
import com.sds.rqreport.util.ThreadPool;

public class RQRepositorySyncManager extends RQServerManager {

	public RQRepositorySyncManager() {
	}

	public AbstractRequester getRequester() {
		return null;
	}

	public static String safeGet(String[] args, int idx)
 	{
		if(args.length > idx)
		{
			return args[idx];
		}
		else
			return "";
 	}

	public static void main(String[] args) {
		int port = 7005, queue = 100;

	 	int num = 0;
	 	String cmd = "";
	 	String value = "";
	 	while(num < args.length)
	 	{
	 		if(args[num].startsWith("-"))
	 		{
	 			cmd = args[num];
	 			num++;
	 		}

	 		if(cmd != null && cmd.equalsIgnoreCase("-port"))
	 		{
	 			value = safeGet(args, num);
	 			port = Integer.parseInt(value);
	 			num++;
	 		}else if(cmd != null && cmd.equalsIgnoreCase("-queue"))
	 		{
	 			value = safeGet(args,num);
	 			queue = Integer.parseInt(value);
	 			num++;
	 		}

	 	}





	   ServerSocket ss = null;
	   boolean[] acceptLoop = {true};
	   Socket s = null;
	   ServerActor sa = null;
//	   RQSessionRequester sr = null;
//	   RQSessionManager sm = null;
//	   sm = new RQSessionManager();

	   ThreadPool threadpool = new ThreadPool(10, 10/*Max Size value*/);

	   try {
		 ss = new ServerSocket(port, queue);
		 System.out.println("server started");
		 System.out.println("port:" + port );
		 System.out.println("queue:" + queue );
	   }
	   catch (Exception e) {
		 System.out.println("server failed to start");
		 e.printStackTrace();
		 ss = null;
		// L.fatal("Property Loader or Create ServerSocket Failure.", e);
		 return;
	   }

	   while (acceptLoop[0]) {
		 try {
		   s = ss.accept();   // Accept connections
		   System.out.println("accepted");
		 }
		 catch (Exception e) {
		   s = null;
		   // L.fatal("A Fatal Error has occurred in accepting socket", e);
		   return;
		 }

		try {
			AbstractRequester ar = (AbstractRequester)(new RSRequester());
			RQDispatch disp = (RQDispatch)(new RSAgent());
			sa = new ServerActor(s, ar, (RQDispatch)disp );
			threadpool.execute(sa);
		}
		catch (Exception e2) {
			e2.printStackTrace();
		}
		sa = null; s = null; // for GC
	   }
	   System.out.println("stopped");
	 }

}


