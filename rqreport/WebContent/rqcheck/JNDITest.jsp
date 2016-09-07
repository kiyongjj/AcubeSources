<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<jsp:include flush="true" page="RQtest.jsp"/>
<html>
<head></head>
<body>
<form action="JNDITest_getData.jsp">
<table border="1">
<tbody>
<tr>
	<td>JNDI name : </td>
	<td><input type="text" name="jndiname" size="40" value="jdbc/Repository"/></td>
</tr>
<tr>
	<td>SQL string : </td>
	<td><input type="text" name="sqlstr" value="select * From RQDoc"/></td>
</tr>
<tr align="center">
	<td colspan="2"><input type="submit"/></td>
</tr>
</tbody>
</table>
</form>
<br>
<form action="JNDITest_getData.jsp">
<table border="1">
<tbody>
<tr>
	<td>Connection String : </td>
	<td><input type="text" name="constr" size="40" value="jdbc:oracle:thin:@dbserver2:1521:worldav"/></td>
</tr>
<tr>
	<td>user id : </td>
	<td><input type="text" name="userid" value="rqadmin_sun"/></td>
</tr>
<tr>
	<td>user password : </td>
	<td><input type="text" name="userpw" value="easybase"/></td>
</tr>
<tr>
	<td>Class Name : </td>
	<td><input type="text" name="classnm"  size="40" value="oracle.jdbc.driver.OracleDriver"/></td>
</tr>
<tr>
	<td>SQL string : </td>
	<td><input type="text" name="sqlstr" value="select * From RQDoc"/></td>
</tr>
<tr align="center">
	<td colspan="2"><input type="submit"/></td>
</tr>
</tbody>
</table>
</form>
</body>
</html>

 
