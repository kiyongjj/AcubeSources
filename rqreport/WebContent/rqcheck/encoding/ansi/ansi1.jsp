<%@ page contentType="text/html; charset=EUC-KR" %>
<jsp:include flush="true" page="../../RQtest.jsp"/>
<%
out.println("charset=EUC-KR<br>");
out.println("page Resource : ANSI <br>");
out.println("------------------------------<br><br>");
%>
<%
String paramHan = "����Ѹ�������";
String paramEng = "peacefulmind";
%>
<%= paramHan + " (JVM���� ������)" %>
<html>
<body>
<br>
����Ѹ������� (page Resource)<br><br>
<a href="ansi2.jsp?paramEng=<%=paramEng%>">ansi2.jsp?paramEng=peacefulmind</a> (get-method-paramEng)<br><br>
<a href="ansi2.jsp?paramHan=<%=paramHan%>">ansi2.jsp?paramHan=����Ѹ�������</a> (get-method-paramHan)<br><br>

</body>
</html>