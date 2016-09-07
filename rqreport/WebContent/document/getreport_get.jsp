<%@ page language="java" pageEncoding="UTF-8"%><%@ page import="java.io.*" %><%@ page import="com.sds.rqreport.repository.*" %><%
	//request.setCharacterEncoding("EUC-KR");
	// For Tomcat 5.x to Convert String
	String doc = request.getParameter("doc");
	//String docname = new String(doc.getBytes("8859_1"), "EUC-KR");
	String docname = new String(doc.getBytes("8859_1"),"UTF-8");
	//PrintStream out2 = new PrintStream(response.getOutputStream());
	//response.setContentType("application");

	DocRepository docRep = new DocRepository();
	File f = docRep.getFile(docname);
	if(f != null && f.exists() && f.isFile())
	{
		byte[] fileData;
		fileData = JDBCHelper.readFile(f);
		
		PrintStream out2 = new PrintStream(response.getOutputStream());
		response.setContentType("Application/x-rqd");
		response.setContentLength(fileData.length);
		out2.write(fileData, 0, fileData.length);
		//out.print((new String(fileData,"UTF-8")).trim()); 
		return;
	} 
%>