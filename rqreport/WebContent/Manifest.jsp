<%@ page language="java" import="java.io.*" pageEncoding="EUC-KR"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
   
<title>REQUBE REPORT</title>
   
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link href="css/common.css" rel="stylesheet" type="text/css">
</head>
  
<body>
<%
String contextPath = session.getServletContext().getRealPath("/META-INF/");

FileReader fr = new FileReader(contextPath + "/MANIFEST.MF");
BufferedReader br = new BufferedReader(fr);
String line = "";	
while((line = br.readLine()) != null){
	out.println("<span style='font-family:verdana;'>" + line + "</span><br>");
}
%>
</body>
</html>
