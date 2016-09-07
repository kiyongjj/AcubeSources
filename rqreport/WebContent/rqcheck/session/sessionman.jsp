<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.sds.rqreport.*,java.util.*,com.sds.rqreport.service.session.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String cmd = request.getParameter("cmd");

Properties props = System.getProperties();
Environment env = Environment.getInstance();
String classpath = env.sessionClasspath;
String exepath = props.getProperty("java.home") + "/bin/java";
String sargs = " -classpath " + classpath + " com.sds.reqube.service.session.SessionServerManager -port 7005";
RQSessionServerAdmin ssa = new RQSessionServerAdmin(exepath, sargs);
boolean result = false;
if (cmd == null)
   cmd = "";
if (cmd.equalsIgnoreCase("start"))
{
	result = ssa.startServer();
}
else if (cmd.equalsIgnoreCase("stop"))
{
	result = ssa.stopServer();
}
else if (cmd.equalsIgnoreCase("restart"))
{
	result = ssa.stopServer();
	result = ssa.startServer();
}
int sessioncount = ssa.getSessionCount();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'sessionman.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <%=cmd + " " + result %> <br>
    Session Count : <%=sessioncount %> <br>
    <a href = "/rqreport/session/sessionman.jsp?cmd=start">start</a> <br>
    <a href = "/rqreport/session/sessionman.jsp?cmd=stop">stop</a> <br>
  </body>
</html>
