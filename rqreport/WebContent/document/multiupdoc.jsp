<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="com.sds.rqreport.service.web.*"%>
<%
	/*
	String createID = "admin";
	String jndiname = "jdbc/Reqube";
	String targetDoc = "/ljg_test/";
	String updocpath_pysical = "D:\\work_myeclipse\\rqreport\\defaultroot\\updoc";
	*/
	String createID = request.getParameter("createID");
	String jndiname = request.getParameter("jndiname");
	String targetDoc = request.getParameter("targetDoc");
	String updocpath_pysical = request.getParameter("updocpath_pysical");
		
	RQHandle ohandle = new RQHandle();
	ohandle.multiupdoc(createID, jndiname, targetDoc, updocpath_pysical);
	
	out.println("processed...");
%>