package com.sds.rqreport.service.queryexecute;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.sds.rqreport.util.RequbeUtil;

/**
 * 서버(웹)에서 실행시 버퍼단위로 데이터셋을 내려주는 역활을 한다. 
 * StringBufferEx.java
 *
 */
public class StringBufferEx
{
	StringBuffer mbuf = null;
	Writer out = null;
	private Logger log = Logger.getLogger("RQQRYEXE");
	
	public StringBufferEx()
	{
		 mbuf = new StringBuffer();
	}
	
	public StringBufferEx(Writer out)
	{
		 this.out = out;
		 if(out != null)
			 mbuf = null; // web
		 else
			 mbuf = new StringBuffer(); // designer
	}	
	
	public StringBuffer append(String str)
	{
		if(mbuf != null)
		{
			return mbuf.append(str);
		}
		else
		{
			try {
				out.write(str);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
		
	}
	
	public StringBuffer append(char[] str)
	{
		if(mbuf != null)
		{
			return mbuf.append(str);
		}
		else
		{
			try {
				out.write(str);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}	
	
	public StringBuffer append(int num)
	{
		if(mbuf != null)
		{
			return mbuf.append(num);
		}
		else
		{
			try {
				out.write("" + num);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}
	
	public StringBuffer append(char ch)
	{
		if(mbuf != null)
		{
			return mbuf.append(ch);
		}
		else
		{
			try {
				out.write(ch);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}
	
	public StringBuffer append(short num)
	{
		if(mbuf != null)
		{
			return mbuf.append(num);
		}
		else
		{
			try {
				out.write(num);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}	
	
	public StringBuffer append(long num)
	{
		if(mbuf != null)
		{
			return mbuf.append(num);
		}
		else
		{
			try {
				out.write("" + num);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}	
	
	public StringBuffer append(float num)
	{
		if(mbuf != null)
		{
			return mbuf.append(num);
		}
		else
		{
			try {
				out.write("" + num);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}
	
	public StringBuffer append(double num)
	{
		if(mbuf != null)
		{
			return mbuf.append(num);
		}
		else
		{
			try {
				out.write("" + num);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			return null;
		}
	}	
	
	public String toString()
	{
		if(mbuf != null)
			return mbuf.toString();
		else
			return "";
	}
	
}