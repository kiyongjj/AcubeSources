package com.sds.rqreport.service.fileservice;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sds.rqreport.util.*;

import org.apache.log4j.Logger;

public class RQFileThread{
	
	private static Logger log = Logger.getLogger("RQFILESVC");
	private RQFileRequest rfreq = null;
	private RQFileResponse rfres = null;
	private volatile Socket sock = null;	
	// client request for only character(byte)
	private volatile BufferedReader br = null;
	// server response for character and binary data(ex. image)
	private volatile BufferedOutputStream bos = null;
	private volatile BufferedInputStream bis = null;
	
	private String remoteAddress = "";
	
	public RQFileThread(RQFileRequest rfreq, RQFileResponse rfres, Socket sock){		

		// flag client protocol //////////////////////////////////////
		// Http 프로클도 가능하게 하려면 밑의 플래그를 true 로 해준다.
		rfres.setBHttpflag(false);
		//////////
		
		this.rfreq = rfreq;
		this.rfres = rfres;
		this.sock = sock;
		this.remoteAddress = "" + this.sock.getRemoteSocketAddress();

	}
	
	public synchronized void _runThread(){			
		byte[] lm_responseHeader = null;
		try{
			// prepare request
			br  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			// prepare response
			bos = new BufferedOutputStream(sock.getOutputStream());
			
			/// prepare client header
			String lm_startline = br.readLine();	
			// for unicode
			//String lm_startline2 = parseIncode(lm_startline);
			int lm_rtn = prepareClientHeader(lm_startline);
			
			if(lm_rtn != 0) throw new RQFileException("Incompatible protocol format Exception");
			
			log.debug(Thread.currentThread().toString() + " " + "[" + getDate() + "] : 1/4. " + remoteAddress + 
							   " # client thread request for : " + rfreq.getStrfileName());
			log.debug(Thread.currentThread().toString() + " " + "[" + getDate() + "] : 2/4. " + sock.getLocalSocketAddress() + 
					   " $ server thread status - " + sock.isConnected() );
			
			String lm_path =  RQFileServerMain.imgrootdir + rfreq.getStrfileName();
			
			// setting response header (fields) //////////////////////////
			File f = new File(lm_path);
			if(!f.exists()) throw new RQFileException("File Not Found Exception");
			
			/////// calculate return file length //////////////////////////////////////////
			String strlen = f.length() + "";
			int nlen = strlen.length();
			String rtnstr = "";
			
			if(nlen != 16){
				int lm_filledzeroCnt = 16 - nlen;
				String zerostr = "";
				for(int i = 0 ; i < lm_filledzeroCnt; i++){
					zerostr += "0";
				}
				rtnstr = zerostr + strlen;
			}
			
			rfres.resprop.put("Content-Length", rtnstr);
			//////////////////////////////////////////////////////////////
	
			// header select
			if(rfres.isBHttpflag() == true){
				lm_responseHeader =  rfres.getSbHttpProto().toString().getBytes();
			}else{
				// client header check!!!!!!! /////////////////////////////////////////
				if(rfres.isBHttpflag() == false){
					if(!rfreq.getStrfirstHeader().startsWith("RQREQUEST")){
						throw new RQFileException("Incompatible protocol Exception"); 
					}
				}
				//////////////////////////////////////////////////////////////////////
				
				// original spec. include header info.
				//lm_responseHeader =  rfres.getSbREQUBEproto().toString().getBytes();
				
				// second spec. excluce header info.
				lm_responseHeader =  rfres.getSbREQUBEproto_se().toString().getBytes();
			}
			bis = new BufferedInputStream(new FileInputStream(lm_path));
						
			byte[] buffer = new byte[1024];
			int readcount = 0;
			
			///////////////// write and flush ////////////////////////////////
			log.debug(Thread.currentThread().toString() + " " + "[" + getDate() + "] : 3/4. " + 
							   remoteAddress + " # client thread write");
			bos.write(lm_responseHeader);
			while( (readcount = bis.read(buffer)) != -1 ){
				bos.write(buffer, 0, readcount );
			}
			bos.flush();
			log.debug(Thread.currentThread().toString() + " " + "[" + getDate() + "] : 4/4. " + 
							   remoteAddress + " # client thread write end #############################");
			/////////////////////////////////////////////////////////////////
		}catch(SocketException se){
			// Connection reset by peer: socket write error
			// user disconnect
			log.debug("####### socket closed by user(client)");
			//se.printStackTrace();
		}catch(Exception e){	
			RequbeUtil.do_PrintStackTrace(log, e);
			//e.printStackTrace();
		}finally{	
			try{if(br != null) br.close();}catch(Exception ex){}
			try{if(bis != null) bis.close();}catch(Exception ex){}	
			try{if(bos != null) bos.close();}catch(Exception ex){}	
			try{if(sock != null) sock.close();}catch(Exception ex){}
		}
	}	
	
	/**
	 * -1 : 일경우 파싱 에러
	 * 0  : 정상 파싱 
	 * @param p_line
	 * @return
	 */
	private int prepareClientHeader(String p_line){
		try{
			// prepare client info.
			rfreq.setStrfirstHeader(p_line);
			// cf) RQREQUEST /sdsTestImage.jpg REQUBE/1.0
			String[] arfHinfo = rfreq.getStrfirstHeader().split(" ", -2);
			rfreq.setStrRequestType(arfHinfo[0]);
			rfreq.setStrfileName(arfHinfo[1]);
		}catch(Exception e){
			return -1;
		}
		return 0;
	}
	
	/**
	 * 현재 날짜 반환 
	 * @return 날짜 반환 형식은 "2008.12.19 10:16:03:468 오전"
	 */
	public static String getDate(){
		//////////////////// connect time info. ///////////////////////
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss:SSS a");
		String dateString = formatter.format(cal.getTime());
		///////////////////////////////////////////////////////////////
		return dateString;
	}
}		