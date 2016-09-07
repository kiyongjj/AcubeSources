package com.sds.rqreport.service.web;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.util.*;

public class RQlog extends TagSupport{

	private static final long serialVersionUID = 1L;
	private String strAction = "";
	private boolean bIsComplete = false;
	public String m_RQCharset = "";
	public String m_ServerCharset = "";
	
	//for logfile
	private long startPosition = 0;
	private long endPosition = 0;
	
	Environment env = Environment.getInstance();
	private int buffersize = env.rqreport_server_logbuffersize;
	private int intervalTime = env.rqreport_server_logintervalTime;
	private long loggingLength = (long ) (buffersize * Math.pow(2, 20)); //buffer size
	
	private Logger log = Logger.getLogger("RQWEB");
	
	public int doStartTag() throws JspTagException{
		
		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;
		
		if(strAction.equals("logging")){
            if(logging()){
                bIsComplete = true;
            }
		}else if(strAction.equals("taillogging")){
            if(tailLogging()){
                bIsComplete = false;
            }
            return EVAL_BODY_INCLUDE;
            
		}else if(strAction.equals("getLogTail")){
            if(getLogTail()){
                bIsComplete = true;
            }
		}else if(strAction.equals("downloadlogfile")){
            if(downloadlogfile()){
                bIsComplete = true;
            }
		}else if(strAction.equals("convStr")){
            if(convStr()){
                bIsComplete = true;
            }
		}else if(strAction.equals("sessionObj")){
            if(showssesionSize()){
                bIsComplete = true;
            }
        }else{
            bIsComplete = false;
        }
		return SKIP_BODY;
	}
	
	public int doAfterBody(){
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspTagException{
        if(bIsComplete){
            return SKIP_PAGE;
        }else{
            return EVAL_PAGE;
        }
    }
	
    public void setAction(String p_strAction){
        strAction = p_strAction;
    }

	private boolean logging() {
		
		JspWriter out = pageContext.getOut();
		String filename = (pageContext.getRequest()).getParameter("file");

		filename = Encoding.chCharset(filename, m_ServerCharset, m_RQCharset);
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try{
			if(!filename.equals("")){
				
				String dir = env.logrqdirname;
				filename = env.logrqfilename;
				
				// InputStream 가져오는 부분 서버특성상 못가져오는 곳일경우
				// 밑의 주석으로 대체한다.
				/*
				File f = new File(dir+"/"+filename);
				is = new FileInputStream(dir+"/"+filename);
				*/
				URL[] arURL = {new File(dir).toURL()}; 
				URLClassLoader ucl = new URLClassLoader(arURL); 
				is = ucl.getResourceAsStream(filename);
				
				isr = new InputStreamReader(is); 
				br =  new BufferedReader(isr); 
				
				String temp = ""; 
				out.println("<body leftmargin=\"0\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" style='font-family:verdana;font-size:11px;'>");
				while ((temp = br.readLine()) != null) { 
					
					//temp = temp.replaceAll("DEBUG","<font color='green'>DEBUG</font>");
					//temp = temp.replaceAll("INFO","<font color='green'>INFO</font>");
					//temp = temp.replaceAll("WARN","<font color='blue'>WARN</font>");
					//temp = temp.replaceAll("ERROR","<font color='red'>ERROR</font>");
					//temp = temp.replaceAll("FATAL","<font color='red'>FATAL</font>");
					
					temp = temp.replaceAll("<","&lt;");
					temp = temp.replaceAll(">","&gt;");
					
					out.print(temp +"<br>"); 
				}
				out.println("</body>");
			}
		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}finally{
			try {
				is.close();
				isr.close(); 
				br.close();
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
		}
		
		return true;
	}
	
	public boolean tailLogging(){
		JspWriter out = pageContext.getOut();
		String filename = (pageContext.getRequest()).getParameter("file");
		ResourceBundle rb = ResourceBundle.getBundle("log4j");
		Enumeration en = rb.getKeys();
		String key = "";
		String dir = "";
		String loggingContents = "";
		
		while( en.hasMoreElements() ){
			key = (String) en.nextElement();
			if(key.equals("dir")){
				dir = rb.getString("dir");
			}
		}
		try{
			if(!filename.equals("")){
				StringBuffer sb = new StringBuffer();
				RandomAccessFile realFile = new RandomAccessFile(dir + "/" + filename, "r"); 
				long realFileLength = realFile.length(); 
				if(realFileLength < loggingLength){
					startPosition = 0;
				}else{
					startPosition = realFileLength - loggingLength;
				}
				realFile.seek(startPosition);   
				String str = "";
				endPosition = realFileLength;
				while((str = realFile.readLine()) != null){
					str = Encoding.chCharset(str, m_ServerCharset, m_RQCharset);
					str = str.replaceAll("\"", "&quot;");
					str = str.replaceAll("DEBUG,", "<span style='font-weight:bold;color:blue;'>DEBUG,</span>");
					str = str.replaceAll("ERROR,", "<span style='font-weight:bold;color:red;'>ERROR,</span>");
					sb.append(str);
					sb.append("<br>");
					endPosition = realFile.getFilePointer();
					realFile.seek(endPosition);   
				}
				realFile.close();
				loggingContents = sb.toString();
				
				//set HTML code at logdata 
				out.println("<script language='javascript'> " +
							"function getLogging(){" +
							"	document.getElementById('logdata').innerHTML = \"" +loggingContents+ "\";" +
							"   document.body.scrollTop += document.body.scrollHeight; " +
							"	startDocList();"+
							"}" +
							"</script> ");
			}
			pageContext.setAttribute("endPosition", new Long(endPosition));
			pageContext.setAttribute("filename", new String(filename));
			pageContext.setAttribute("loggingLength", new Long(loggingLength));
			
			pageContext.setAttribute("intervalTime", new Integer(intervalTime));
			
		}catch(IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		
		return true;
	}
	
	public boolean getLogTail(){
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		
		response.setHeader("Pragma","No-cache"); 
		response.setDateHeader("Expires", 0); 
		response.setHeader("Cache-Control", "no-cache");
		
		String filename = "";
		if(request.getParameter("filename") == null){
			filename = "";
		}else{
			filename = request.getParameter("filename");
		}
		long endPosition = 0;
		if(request.getParameter("endPosition")==null){
			endPosition = 0;
		}else{
			endPosition = Long.parseLong(request.getParameter("endPosition"));
		}
		String loggingContents = "";
		
		ResourceBundle rb = ResourceBundle.getBundle("log4j");
		Enumeration en = rb.getKeys();
		String key = "";
		String dir = "";
		while( en.hasMoreElements() ){
			key = (String) en.nextElement();
			if(key.equals("dir")){
				dir = rb.getString("dir");
			}
		}
		StringBuffer sb = new StringBuffer();
		try{
			RandomAccessFile realFile = new RandomAccessFile(dir + "/" + filename, "r"); 
			long realFileLength = realFile.length(); 
			long startPosition = 0;
			if(endPosition > 0){
				startPosition = endPosition;
			}else{
				startPosition = realFileLength;
			}
			realFile.seek(startPosition);
			String str = "";
			long nowStartPosition = startPosition;
			while((str = realFile.readLine()) != null){
				str = Encoding.chCharset(str, m_ServerCharset, m_RQCharset);
				str = str.replaceAll("\"", "&quot;");
				str = str.replaceAll("DEBUG,", "<span style='font-weight:bold;color:blue;'>DEBUG,</span>");
				str = str.replaceAll("ERROR,", "<span style='font-weight:bold;color:red;'>ERROR,</span>");
				sb.append(str);
				sb.append("<br>");
				nowStartPosition = realFile.getFilePointer();
				realFile.seek(nowStartPosition);   
			}
			realFile.close(); 
			loggingContents = sb.toString();
			out.print(nowStartPosition+"|"+loggingContents);
			
		}catch(IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
	public boolean convStr(){
		JspWriter out = pageContext.getOut();
		
		String strDBInfo = (pageContext.getRequest()).getParameter("strDBInfo");
		String strXml = (pageContext.getRequest()).getParameter("strXml");
		
		StringBuffer stbDBInfo = new StringBuffer(strDBInfo);
		StringBuffer stbXml = new StringBuffer(strXml);
		
		StringBuffer resultStrDBInfo = new StringBuffer();
		StringBuffer resultStrXml = new StringBuffer();
		
		replaceSelf(stbDBInfo, "\"*??*", "\"", resultStrDBInfo);
		replaceSelf(resultStrDBInfo, "[*??*", "]", resultStrDBInfo);
		
		replaceSelf(stbXml, "\"*??*", "\"", resultStrXml);
		replaceSelf(resultStrXml, "[*??*", "]", resultStrXml);
		
		try {
			
			
			out.println("<textarea name=\"strDBInfo\" rows=\"10\" cols=\"80\" style=\"font-family: verdana; font-size: 11px;\">"+resultStrDBInfo+"</textarea>");
			out.println("<br>");
			out.println("<textarea name=\"strDBInfo\" rows=\"10\" cols=\"80\" style=\"font-family: verdana; font-size: 11px;\">"+resultStrXml+"</textarea>");
			
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return false;
	}
	
	public String convXML(String p_Str){
		StringBuffer stb = new StringBuffer(p_Str);
		StringBuffer rtnsb = new StringBuffer();
		replaceSelf(stb  , "\"*??*", "\"" , rtnsb);
		replaceSelf(rtnsb, "[*??*" , "]"  , rtnsb);
		return rtnsb.toString();
	}
	
	public static void main(String[] args){
		
		//String strOri = "*??*EbSiLjQhQcIiFdUcAcVfIbGjvBjThFjHbTdEdSfDjUcPjQhGeXgGcRhVdxZhUdFeDgCiXdOfyVfKbShThUhEiLeQjLhyNjMfUhHdIhNjNjUhCgjChKbUjXfHht";
		
		StringBuffer stbOri = new StringBuffer("<Database><DBInfo DBIdx=\"0\" IFType=\"1\" DBID=\"*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLhQcRfGeShQjQiwMdJhUcWgUhJhWhhHg\" Driver=\"oracle.jdbc.driver.OracleDriver\" ConnectionStr=\"*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLh\" UserID=\"*??*sQcEdYeTfTbQcFbLjLfWgKfAeBjNfQc\" Password=\"*??*TeUjViIfEbHjJbZbMfzPdMhIfqBjQc\"><![CDATA[*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLhQcRfGeShQjQiwMdJhUcWgUhJhWhhHg]]><![CDATA[*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLh]]><![CDATA[*??*sQcEdYeTfTbQcFbLjLfWgKfAeBjNfQc]]><![CDATA[*??*TeUjViIfEbHjJbZbMfzPdMhIfqBjQc]]></DBInfo><DBInfo DBIdx=\"1\" IFType=\"1\" DBID=\"*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdUdtPgYcfGcDgSfXbGhQiYeFgXcPfkSbGcCbLilKdKhKi\" Driver=\"sun.jdbc.odbc.JdbcOdbcDriver\" ConnectionStr=\"*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdWhJeEeGjNhUguLi\"><![CDATA[*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdUdtPgYcfGcDgSfXbGhQiYeFgXcPfkSbGcCbLilKdKhKi]]><![CDATA[*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdWhJeEeGjNhUguLi]]></DBInfo></Database>");
		//String tmp = replaceSP(strOri, "*??*", "ljgljgljg");

		StringBuffer result = new StringBuffer();
		replaceSelf(stbOri, "\"*??*", "\"", result);
		
		System.out.println(result);
		
//		String strRtn= "<Database><DBInfo DBIdx=\"0\" IFType=\"1\" DBID=\"jdbc:oracle:thin:@dbserver2:1521:WorldAV - JDBC1\" Driver=\"oracle.jdbc.driver.OracleDriver\" ConnectionStr=\"*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLh\" UserID=\"*??*sQcEdYeTfTbQcFbLjLfWgKfAeBjNfQc\" Password=\"*??*TeUjViIfEbHjJbZbMfzPdMhIfqBjQc\"><![CDATA[jdbc:oracle:thin:@dbserver2:1521:WorldAV - JDBC1]]><![CDATA[*??*SiUjgUcTfYgChxNiUeLgFbAiYhSgZgMcJdMhNfTiJcJfJfTgPjAhXhOeDgLfNgPbUhGfBdJgDcGiDfvKdFjAjEjCcZeQhUgLbVjIjEeBhMdSdIcWcTgNfHfGdFdWgEgPhIiYbAjZgNbUdLiSdRbUjCiUhvLh]]><![CDATA[*??*sQcEdYeTfTbQcFbLjLfWgKfAeBjNfQc]]><![CDATA[*??*TeUjViIfEbHjJbZbMfzPdMhIfqBjQc]]></DBInfo><DBInfo DBIdx=\"1\" IFType=\"1\" DBID=\"*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdUdtPgYcfGcDgSfXbGhQiYeFgXcPfkSbGcCbLilKdKhKi\" Driver=\"sun.jdbc.odbc.JdbcOdbcDriver\" ConnectionStr=\"*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdWhJeEeGjNhUguLi\"><![CDATA[*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdUdtPgYcfGcDgSfXbGhQiYeFgXcPfkSbGcCbLilKdKhKi]]><![CDATA[*??*SiUjgUcTfYgChxZcxHevFbOiHdDiRgXdIcQdTbKbSdWbLgPjFcGgLgXfJbBdWhJeEeGjNhUguLi]]></DBInfo></Database>";
//		System.out.println(strRtn.indexOf("\"*??*", 44));
	}
	
	public static void replaceSelf(StringBuffer stb, String strStart, String strEnd, StringBuffer result){
		
		String strRtn = "";
		
		String Ori = stb.toString();
		int startidx = Ori.indexOf(strStart);
		int endidx = Ori.indexOf(strEnd,startidx+1);
		String preConv = Ori.substring(startidx+1, endidx);
		String afterConv = decryptEncValue(preConv);
		
		strRtn = replaceSP(Ori, preConv, afterConv);
		
		if( strRtn.indexOf(strStart, startidx+1) != -1){
			replaceSelf(new StringBuffer(strRtn), strStart, strEnd, result);
		}
		if( strRtn.indexOf(strStart, startidx+1) == -1){
			result.setLength(0);
			result.append(strRtn);
		}
		
	}

	public static String replaceSP(String s, String from, String to) 
	{
		int index = s.indexOf(from); 
		StringBuffer buf = new StringBuffer(); 
		if(index<0) return s;
		buf.append(s.substring(0,index));
		buf.append(to);
		if(index + from.length() < s.length()){
			buf.append(replaceSP(s.substring(index+from.length(), s.length()), from, to));
		}
		return buf.toString();
	}
	
	protected static String decryptEncValue(String str){
		if(str != null && str.startsWith("*??*")){
			Decrypter dec = new Decrypter("RQREPORT6**??");
			return dec.decrypt(str.substring(4));
		}else{
			return str;
		}
	}
	
	public boolean downloadlogfile(){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		String filename = request.getParameter("filename");
		String dnfilename = Encoding.chCharset(filename, m_ServerCharset, m_RQCharset);
		ResourceBundle rb = ResourceBundle.getBundle("log4j");

		Enumeration en = rb.getKeys();
		String key = "";
		String strPath = "";

		while( en.hasMoreElements() ){
			key = (String) en.nextElement();
			if(key.equals("dir")){
				strPath = rb.getString("dir");
			}
		}

		if(request.getHeader("User-Agent").indexOf("MSIE 5.5") > -1) {
		 	response.setHeader("Content-Disposition", "filename="+dnfilename);
		} else {
			response.setHeader("Content-Disposition", "attachment; filename="+dnfilename);
		}

//		String strPath = pageContext.getServletContext().getRealPath("environment/" + filename);

		File f = new File(strPath + "/" + filename);
		response.setContentLength((int)f.length());

		byte buffer[] = new byte[2048];
		BufferedInputStream bi;
		try {
			bi = new BufferedInputStream(new FileInputStream(f));
			BufferedOutputStream bo = new BufferedOutputStream(response.getOutputStream());

			try {
			 	int n=0;
			 	while((n = bi.read(buffer,0,2048)) != -1) {
			  		bo.write(buffer,0,n);
			  	}
			}catch(IOException e){
				RequbeUtil.do_PrintStackTrace(log, e);
			} finally {
			 	bo.close();
			 	bi.close();
			}
		} catch (FileNotFoundException e1) {
			RequbeUtil.do_PrintStackTrace(log, e1);
		} catch(IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}

		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean showssesionSize(){
		HttpSession session = ((HttpServletRequest)pageContext.getRequest()).getSession(true);
		JspWriter out = pageContext.getOut();
		Enumeration en   = session.getAttributeNames();
	    String      name = null;
	    Object      obj  = null;
	    ByteArrayOutputStream bastream = null;
	    ObjectOutputStream objOut = null;
	    int         objSize;
	    int         totalSize = 0;
	    try{
	    	
		    while (en.hasMoreElements()) {
		    	name  = (String)en.nextElement();            
		    	obj   = session.getAttribute(name);
	
	    		bastream  = new ByteArrayOutputStream();
	    		objOut    = new ObjectOutputStream(bastream);
	    		objOut.writeObject(obj);
	    		//objSize
	    		objSize = bastream.size();       
	
		    	log.debug("Session Name : " + name + ", Size : " + objSize + " bytes<br>");
		    	out.println("Session Name : " + name + ", Size : " + objSize + " bytes<br>");
		    	totalSize += objSize;
		    }
	    	out.println("<br><br>-------------------------------------------------<br>");
	    	out.println("Total Session Size : " + totalSize + " bytes");
	    	
	    }catch(IOException e){
	    	objSize = 0;
	    	e.printStackTrace();
	    	return true;
	    }
	    return true;
	  }
	
}
