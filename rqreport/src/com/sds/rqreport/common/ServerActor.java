package com.sds.rqreport.common;
import java.io.*;
import java.net.*;

import com.sds.rqreport.service.Timer;
import com.sds.rqreport.service.queryexecute.RQGetDataIf;
import com.sds.rqreport.util.RequbeUtil;

public class ServerActor extends Thread{
	Socket socket = null;
	AbstractRequester rq = null; 
	RQDispatch dispatch;
	public ServerActor(Socket socket, AbstractRequester rq, RQDispatch dispatch) {
	  this.socket = socket;
	  this.rq = rq;
	  this.dispatch = dispatch;
	}
	public void run()
	{
		try {
			
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			/////////////// time check ///////////////////////////////////
			//Timer t1 = new Timer();
			//System.out.println(t1.start(""));

			sendReceive(in,out);
			
			//System.out.println(t1.end(""));
			/////////////////////////////////////////////////////////////
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	protected String read(InputStream is)
	{
		byte[] data = null;
			try {
				byte sizestr[];
				sizestr = new byte[15];
				// Client가 보낼 데이터 크기를 읽기
				is.read(sizestr);
				String ss = new String(sizestr);
				int size = Integer.parseInt(ss.trim());
				byte a[] = new byte[size];
				// Client가 보낼 데이터 읽기
				int offset = 0, len, pos = 0, totalReceived = 0;
				while (totalReceived < size) {
					len = is.read(a, totalReceived, size - totalReceived);
					totalReceived += len;
				}
				data = a;
			} catch (Exception e) {
				data = null;
				//e.printStackTrace();
				return null;
			}
			
			String readString = null;
		
			try {
				readString = new String(data,0,data.length - 1,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				readString = new String(data,0,data.length - 1);
			}
			return readString;
	}
	
	protected boolean write(OutputStream os, String writeString)
	{
		byte[] nullchar = new byte[1];
		byte[] cr = new byte[1];
		nullchar[0] = 0;
		cr[0] = 10;
		
		/////////////// dataset file write for test ///////////////////
		/*
		FileWriter fw = null;
		try{
			fw = new FileWriter("C:/ds.txt");
			fw.write(writeString);
		}catch(IOException e){}
		*/
		//////////////////////////////////////////////////////////////
		 
		try {
			if (os != null) {
				byte[] send = null;
				send = sendArray(writeString); // in case byte[]
				//checkImg(send);
				
				/*
				try{
					send = writeString.getBytes("UTF-8"); // in case BASE64	
				} catch (UnsupportedEncodingException e1) {
					send = writeString.getBytes();o
				}*/
				
				//String nstin = new String(send);
				
				if (send != null) {
					int len  = send.length + 1;
					String s = new String(len + "               ");
					//byte[] bs = s.getBytes();
					byte[] bs = s.getBytes();
					os.write(bs, 0, 15);
					os.write(send);
					os.write(cr);
					os.write(nullchar);
					os.flush();
					os.close();
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
				
	}
	
	public byte[] sendArray(String writeString){
		byte[] send = null;
		
		String lm_data = "";
		String lm_Img  = "";
		byte[] ar_data = null;
		byte[] ar_Img = null;
		
		try {
			int idxbs = writeString.indexOf(RQGetDataIf.BLOB_SEP);
			
			if(idxbs != -1){
				lm_data = writeString.substring(0, idxbs);
				lm_Img  = writeString.substring(idxbs, writeString.length());
				ar_data = lm_data.getBytes("UTF-8");
				// insert IMG length in header
				
				ar_Img  = lm_Img.getBytes("8859_1");
				send = RequbeUtil.merge(ar_data, ar_Img);
				//send = merge(getDSH(ar_data), ar_Img);
				
				////////////////////// TEST CODE      /////////////////////////
				//String testStr = new String(send, "UTF-8");
				//System.out.println("########################## : " + testStr);
				//System.out.println("########################## : " + 
				//				    testStr.indexOf(RQGetDataIf.BLOB_SEP));
				//////////////////////////////////////////////////////////////
				
				//checkImg(send);
			}else{
				lm_data = writeString;
				send = lm_data.getBytes("UTF-8");
			}
			//checkImg(writeString);
			//send = writeString.getBytes("UTF-8");
			//checkImg(send);
		}catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			send = writeString.getBytes();
		}finally{
			lm_data = null; 
			lm_Img = null; 
			ar_data = null; 
			ar_Img = null;
		}
		return send;
	}
	
	/**
	 * get DS with DataSet length
	 * @param p_data
	 * @return
	 */
	public byte[] getDSH(byte[] p_data){
		byte[] lm_data = null;
		byte[] lm_cr  = {0X0D, 0X0A}; // \n (2count)
		byte[] lm_str = {0X0D, 0X0A, 0X49, 0X4D, 0X47, 0X3D, 0X09, 0X0D, 0X0A}; // \nIMG=\t\n  (9count)
		
		int len = p_data.length;   // data length 
		String strLen1 = Integer.toString(len + 11); // length's lengthString ex) 72 + 11 = 83
		String strLen2 = Integer.toString(strLen1.getBytes().length + len + 11) ; // itself 2 + 72 + 11 = 85
		byte[] lm_arrStrLen = strLen2.getBytes(); 	// not need encoding.    ex) 85

		byte[] merged_arr  = RequbeUtil.merge(lm_str, lm_arrStrLen);   // \nIMG=\t\n  (9count)   + 85 
		byte[] merged_arr2 = RequbeUtil.merge(merged_arr, lm_cr);      // \nIMG=\t\n  (9count)   + 85 + \n
		lm_data = RequbeUtil.merge(merged_arr2, p_data);				// \nIMG=\t\n  (9count)   + 85 + \n + DATA ....
		return lm_data;
	}
	
	/**
	 * 이미지 체크 
	 * @param obj
	 * @throws UnsupportedEncodingException
	 */
	public static void checkImg(Object obj) throws UnsupportedEncodingException{
		byte[] send   = null;
		String lm_str = "";
		String filename = "";
		if (obj instanceof byte[]) {
			send = (byte[]) obj;
			lm_str  = new String(send,"8859_1");
			filename = "byteWrite.jpg";
		} else if (obj instanceof String) {
			lm_str  = (String) obj;
			filename = "StringWrite.jpg";
		}
		int lm_flagImg = lm_str.indexOf(RQGetDataIf.BLOB_SEP);
		if(lm_flagImg != -1){
			String lm_Imgdata  = lm_str.substring(lm_flagImg, lm_str.length());
			// check Img //////////////////////////////////////////////////////////////
			try{
				FileOutputStream fw = new FileOutputStream("C:\\" + filename);
				String lm_Img = lm_Imgdata.substring(1, lm_Imgdata.length());
				fw.write(lm_Img.getBytes("8859_1"));
				fw.close();
			}catch(IOException e){}
			///////////////////////////////////////////////////////////////////////////
		}
	}
	
	protected boolean sendReceive(InputStream is, OutputStream os)
	{
		String readstr = read(is);
		if(readstr == null)
			return false;
		int code = rq.request(readstr, dispatch);
		String writestr = rq.getResponse();
		if( writestr == null)
			return false;
		
		boolean ret = write(os,writestr);
		if(writestr.startsWith("-9999"))
		{
			System.exit(-1);
		}
		return ret;
	}
}
