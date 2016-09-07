package com.sds.rqreport.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public abstract class AbstractClientRequester {

	InputStream in = null;
	OutputStream out = null;
	protected SocketConnector sc = null;
	String server = null;
	int port = 0;

	public AbstractClientRequester() {
		super();
	}
	AbstractClientRequester(InputStream in, OutputStream out)
	{
		this.in = in;
		this.out = out;
	}
	public AbstractClientRequester(SocketConnector sc)
	{
		this.sc = sc;
	}
	public AbstractClientRequester(String server,int port)
	{
		this.server = server;
		this.port = port;
		sc = new SocketConnector(server,port);
	}

	public synchronized short callFunction(int functionID, List pArgs, List pRets) throws IOException
	{
		if(sc != null)
		{
			sc.connect();
//			in = sc.getInputStream();
//			out = sc.getOutputStream();
		}

		String[] receive = new String[1];
		try
		{
			short rtn = -1;
			if ( (rtn = sendReceive(makeSendString(functionID, pArgs), receive)) > -1)
			{
				if(receive[0] != null && receive[0].length() > 0)
				{
					short res =  parseReceiveString(receive[0], pRets);
					if(sc != null)
					{
						sc.close();
						sc = null;
					}
					return res;
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("errrrrrrr");
			e.printStackTrace();
		}
		finally
		{
			if(sc != null)
			{
				sc.close();
				sc = null;
			}
		}
		return -100;
	}

	public synchronized short sendReceive(String sendstr, String[] receive) throws IOException
	{
		int sendlen = 0;
		byte[] sendbyte = null;
		String strTmp = "";
		String strTmp1 = "";
		String strTmp2 = "";
		short rtn = -1;
		try
		{
			strTmp2 = sendstr;
			sendbyte = sendstr.getBytes("UTF-8");
		}catch(Exception e)
		{
			strTmp2 = sendstr;
			sendbyte = sendstr.getBytes();
		}
		sendlen = sendbyte.length;
		String sizestr = strTmp1 = (sendlen + 1) + "               ";
		byte[] sizebyte = null;
		try
		{
			strTmp1 = sizestr.substring(0,15);
			sizebyte = sizestr.getBytes("UTF-8");
			sizebyte[14] = '\0';
		}
		catch(Exception e)
		{
			e.printStackTrace();
			strTmp1 = sizestr.substring(0,15);
			sizebyte = sizestr.getBytes();
		}
		////////////////////////////////////////////////////
		strTmp = strTmp1 + strTmp2 + '\0';
		sendbyte = strTmp.getBytes();
		try {
			out = sc.getOutputStream();
			out.write(sendbyte, 0, strTmp.length());
		}
		catch (IOException e1) {
			try {
				Thread.sleep(10);
				if(sc != null)
				{
					sc.close();
					sc = null;
					out = null;
					in = null;
					sc = new SocketConnector(server,port);
					sc.connect();
				}

				if(out == null)
					out = sc.getOutputStream();
				out.write(sendbyte, 0, strTmp.length());
			}
			catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}

		/*
		if(sizebyte != null && sizebyte.length > 15)
		{
			System.out.println(sc + " :(11) " + sizestr);
			out.write(sizebyte,0,15);
		}
		System.out.println(sc + " :(22) " + sizestr);
		// data send
		out.write(sendbyte, 0,sendlen);
		System.out.println(sc + " :(33) " + sendstr);
		byte[] nullchar = new byte[1];
		nullchar[0] = '\0';
		out.write(nullchar);
		System.out.println(sc + " :(44) " + nullchar.toString());
		*/

		// Then read
		byte[] readlen = new byte[15];

		if(in == null)
			in = sc.getInputStream();
		in.read(readlen,0,15);
		String temp = new String(readlen);
		int readsize = Integer.parseInt(temp.trim());

		byte a[] = new byte[readsize];
		 // Client가 보낼 데이터 읽기
		int offset = 0,len, pos = 0, totalReceived = 0;
		try {
			while(totalReceived < readsize)
			{
			   len = in.read(a, totalReceived, readsize - totalReceived);
			   totalReceived += len;
			}
		}
		catch (IOException e2) {
			e2.printStackTrace();
		}
		receive[0] = new String(a,0,a.length - 1,"UTF-8");
		return 0;
	}
	abstract public String makeSendString(int id, List args);


	abstract public short parseReceiveString(String str, List rets);


	public String[] split(String arg, String regex)
	{

		int pos = 0, oldpos = 0;
		int num = 0;
		Vector strarray = new Vector();
		while((pos = arg.indexOf(regex,pos)) > -1){
			strarray.add(arg.substring(oldpos,pos));
			oldpos = ++pos;
		}
		strarray.add(arg.substring(oldpos,arg.length()));
		int size = strarray.size();
		String[] result = new String[size];
		for(int i = 0; i < size; ++i)
		{
			result[i] = (String)strarray.get(i);
		}
		return result;
	}
}
