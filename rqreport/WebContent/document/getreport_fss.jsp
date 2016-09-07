<%@ page language="java" pageEncoding="UTF-8"%><%@ page import="java.io.*" %><%
	response.setHeader("RQREPORT","getreport.jsp");
	String doc = request.getParameter("doc");
	File f = new File( "c:/test/" + doc);
	
	if(f != null && f.exists() && f.isFile())
	{
		byte[] fileData = null;
		FileInputStream fi = null;
		try {
			int size = (int) f.length();
			fileData = new byte[size];
			fi = new FileInputStream(f);
			int len, totalReceived = 0;
			while (totalReceived < size) {
				len = fi.read(fileData, totalReceived, size - totalReceived);
				totalReceived += len;
			}
		}catch (Exception e){
			fileData = null;
		}finally{
			try{
				if (fi != null) {
					fi.close();
				}
			}catch (IOException ex) {
		}
			fi = null;
			f = null;
		}
		PrintStream out2 = new PrintStream(response.getOutputStream());
		response.setContentType("Application/x-rqd");
		response.setContentLength(fileData.length);
		out2.write(fileData, 0, fileData.length);
		//out.print((new String(fileData,"UTF-8")).trim()); 
		return;
	} 
%>