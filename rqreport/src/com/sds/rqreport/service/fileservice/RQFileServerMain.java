package com.sds.rqreport.service.fileservice;

import java.net.*;				

import com.sds.rqreport.util.*;
import org.apache.log4j.Logger;
			
public class RQFileServerMain {		
	
	private final static int RQdefaultFileport = 10523;
	public static String imgrootdir = "";
	private static Logger log = Logger.getLogger("RQFILESVC");
	
	public static void main(String[] args) {
		
		try{
			ServerSocket serversocket = null;
			
			//// default file server port setting.
			if( args.length > 0){
				serversocket = new ServerSocket(Integer.parseInt( args[0] ) );
				if( args.length > 1){
					imgrootdir	 = args[1];
				}
			}
			else
			{
				serversocket = new ServerSocket(RQdefaultFileport);
			}
			while(true){	
				// log for server info.
				log.debug("[" + RQFileThread.getDate() + "] : " + 
								   "REQUBE File Server wait for client request.");
				// wait for client signal.
				Socket sock = serversocket.accept();
				
				//// Client Thread start.
				// 공용 리소스가 없으므로 새로운 쓰레드가 생성되면 
				// 클라이언트와의 연결에서 받은 정보들을 가지고 바로 처리루틴으로 보내준다.
				RQFileRequest rfreq = new RQFileRequest();
				RQFileResponse rfres = new RQFileResponse();
				final RQFileThread rqft = new RQFileThread(rfreq, rfres, sock);
				// run Thread. (anonymous inner class)
				new Thread(){
					public void run(){ rqft._runThread();}
				}.start();
			}
		}catch(Exception e){		
			RequbeUtil.do_PrintStackTrace(log, e);
			//e.printStackTrace();
		}	
	}		
}
