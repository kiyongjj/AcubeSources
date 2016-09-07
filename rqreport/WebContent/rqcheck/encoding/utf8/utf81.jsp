<%@ page contentType="text/html; charset=utf-8" %>
<jsp:include flush="true" page="../../RQtest.jsp"/>
<jsp:directive.page import="java.net.*"/>
<%
out.println("charset=UTF-8<br>");
out.println("page Resource : UTF-8 <br>");
out.println("------------------------------<br><br>");
%>
<%
String paramHan = "고요한마음으로";
String paramEng = "peacefulmind";
%>
고요한마음으로 (page Resource)<br>
<%= paramHan + " (JVM에서 받은것)" %><br><br>
<html>
<body>
<a href="utf82.jsp?paramEng=<%=paramEng%>">utf82.jsp?paramEng=peacefulmind</a>(get-method-paramEng)<br><br>
<a href="utf82.jsp?paramHan=<%=paramHan%>">utf82.jsp?paramHan=고요한마음으로</a>(get-method-paramHan)<br><br>

&lt;form&gt;
<form method="post" name="frm" action="utf82.jsp">
<input type="text" value="아침을맞이하는" name="paramHan"/><br>
<a href="javascript:document.frm.submit();">submit</a><br><br>
UTF-8 일경우 Post로 파라메터를 넘길경우<br>
 JVM 케릭터 타입(8859_1) 에서 어플케릭터타입(UTF-8)로 바꿔주면<br>
이상없이 나온다.<br>
</form>
&lt;/form&gt;<br><br>

<%
String urlenc_paramHan = URLEncoder.encode(paramHan , "UTF-8");
out.println( "URLEncoder.encode : " + URLEncoder.encode(paramHan , "UTF-8") +"<br>");
out.println( "URLDecoder.decode : " + URLDecoder.decode(paramHan , "UTF-8") +"<br>");
%>
<a href="utf82.jsp?paramHan=<%=urlenc_paramHan%>">utf82.jsp?paramHan=<%=urlenc_paramHan%></a>(get-method-paramHan)<br><br>

</body>
</html>