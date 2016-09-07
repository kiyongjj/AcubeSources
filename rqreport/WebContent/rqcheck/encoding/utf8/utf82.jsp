<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:include flush="true" page="../../RQtest.jsp"/>
<jsp:directive.page import="java.net.URLDecoder"/>
charset=UTF-8<br>
page Encoding : UTF-8 <br><br>
<%
String paramEng = request.getParameter("paramEng");
String paramHan = request.getParameter("paramHan");
%>
<%
out.println("1. request.getParameter(\"paramEng\"); :" + paramEng + "<br><br>");
out.println("2. request.getParameter(\"paramHan\"); : " + paramHan + "<br><br>");

if(paramHan != null){

	String paramHan_8859_1_to_EUC_KR = new String(paramHan.getBytes("8859_1"), "EUC-KR");
	String paramHan_8859_1_to_UTF_8 = new String(paramHan.getBytes("8859_1"), "UTF-8");
	String paramHan_UTF_8_to_UTF_8 = new String(paramHan.getBytes("UTF-8"), "UTF-8");

	out.println("3. new String(paramHan.getBytes(\"8859_1\"), \"EUC-KR\"); : " + paramHan_8859_1_to_EUC_KR + "<br><br>");
	out.println("4. new String(paramHan.getBytes(\"8859_1\"), \"UTF-8\"); :" + paramHan_8859_1_to_UTF_8 + "<br><br>");
	out.println("5. new String(paramHan.getBytes(\"UTF-8\"), \"UTF-8\"); : " + paramHan_UTF_8_to_UTF_8 + "<br><br>");
	
	out.println( "URLDecoder.decode(paramHan_8859_1_to_UTF_8 , \"UTF-8\")  : " + URLDecoder.decode(paramHan_8859_1_to_UTF_8 , "UTF-8") +"<br>");
}
%>