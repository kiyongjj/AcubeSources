package com.sds.rqreport.service.session;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.MissingResourceException;
//import com.sds.reqube.service.admin.ThreadPoo;

import com.sds.rqreport.Environment;
import com.sds.rqreport.common.*;
import com.sds.rqreport.util.*;

public class SessionServerManager {
		private static final String PROPERTIES_NAME = "session.properties";
		static {
	//	   NDC.push("AdminServerManager");
		 }
	//	 static Logger L = Logger.getLogger("AdminServer");

		public static String safeGet(String[] args, int idx)
	 	{
			if(args.length > idx)
			{
				return args[idx];
			}
			else
				return "";
	 	}

		 public static void main(String args[]) {
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


			try {
				String configfile = System.getProperty(PROPERTIES_NAME);
				if (configfile == null) {

				    // 여기는 시스템 프라퍼티에 값이 설정되어 있지 않을 경우
				    // CLASSPATH에 지정된 디렉토리에서 값을 읽는다.
					URL url = SessionServerManager.class.getClassLoader().getResource(PROPERTIES_NAME);
					if(url != null)
					{
						try {
							configfile = URLDecoder.decode(url.getFile(),"UTF-8");
						} catch (UnsupportedEncodingException e) {
							//e.printStackTrace();
							configfile = url.getFile();
						}
					}
			    }

				if(configfile != null && configfile.length() > 0)
				{
					PropertyLoader pl = new PropertyLoader(configfile);
					port = pl.getInt("reqube.session.port", port);
					queue = pl.getInt("reqube.session.socket_queue", queue);
				}
			} catch (MissingResourceException e1) {
				e1.printStackTrace();
			}


		   ServerSocket ss = null;
		   boolean[] acceptLoop = {true};
		   Socket s = null;
		   ServerActor sa = null;
		   RQSessionRequester sr = null;
		   RQSessionManager sm = null;
		   sm = new RQSessionManager();

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
				sr = new RQSessionRequester();
				sa = new ServerActor(s, (AbstractRequester)sr, (RQDispatch)sm );
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
