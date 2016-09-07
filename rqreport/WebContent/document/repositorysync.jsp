<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.sds.rqreport.*,java.util.*,com.sds.rqreport.repository.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String cmd = request.getParameter("cmd");

Properties props = System.getProperties();
Environment env = Environment.getInstance();
String classpath = env.sessionClasspath;
String exepath = props.getProperty("java.home") + "/bin/java";
//String classpath2= props.getProperty("java.classpath");
//System.out.println(classpath2);
String sargs = " -classpath " + classpath + " com.sds.rqreport.repository.RQRepositorySyncManager -port 12255";
RQRepositorySyncAdmin ssa = new RQRepositorySyncAdmin(exepath, sargs);
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
}else if (cmd.equalsIgnoreCase("send"))
{
//	result = ssa.stopServer();
//	result = ssa.startServer();
	String server = request.getParameter("server");
	int port = Integer.parseInt(request.getParameter("port"));
	RepositorySyncClient client = new RepositorySyncClient(server,port);
	client.writeAll(env.getRepositoryEnv().repositoryRoot);
}

%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'repositorysync.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="start page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <a href = "<%=basePath%>document/repositorysync.jsp?cmd=start">start</a> <br>
    <a href = "<%=basePath%>document/repositorysync.jsp?cmd=stop">stop</a> <br>
    <a href = "<%=basePath%>document/repositorysync.jsp?cmd=send&server=127.0.0.1&port=12255">send</a> <br>
  </body>
</html>
