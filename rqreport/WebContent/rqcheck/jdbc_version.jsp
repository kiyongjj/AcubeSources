<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.*" %>
<jsp:include flush="true" page="RQtest.jsp"/>
<html>
<head></head>
<body>
<form action="jdbc_version_getcon.jsp">
<table border="1">
<tbody>
<tr>
	<td>Connection String : </td>
	<td><input type="text" name="constr" size="40" value="jdbc:oracle:thin:@dbserver2:1521:worldav"/></td>
</tr>
<tr>
	<td>user id : </td>
	<td><input type="text" name="userid" value="easybase"/></td>
</tr>
<tr>
	<td>user password : </td>
	<td><input type="text" name="userpw" value="rqdev1"/></td>
</tr>
<tr align="center">
	<td colspan="2"><input type="submit"/></td>
</tr>
</tbody>
</table>
</form>
</body>
</html>