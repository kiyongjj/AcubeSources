<%@ page contentType="text/html;charset=utf-8" %>
<jsp:include flush="true" page="../rqcheck/RQtest.jsp"/>

<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
</head>
<body>
<form name="multiupdoc" method="post" action="multiupdoc.jsp">
<table border="1"  style="font-family: verdana; font-size:11px;">
<tr>
	<td>1. createID</td>
	<td><input name="createID" type="text" value="admin"  size="35"/></td>
</tr>
<tr>
	<td>2. jndiname</td>
	<td><input name="jndiname" type="text" value="jdbc/Reqube"  size="35"/></td>
</tr>
<tr>
	<td>3. targetDoc_logical</td>
	<td>
		<input name="targetDoc" type="text" value="/DEMO/"  size="35"/><br>
		ex1) root : / <br>
		ex2) user folder : /DEMO/<br>
	</td>
</tr>
<tr>
	<td>4. updocpath_pysical(server)</td>
	<td>
		<input name="updocpath_pysical" type="text" value="D:\updoc"  size="35"/><br>
		ex1) server physical path : D\updoc <br>
		ex2) server physical path : /usr/tmp/updoc
	</td>
</tr>
</table>
<br><br>
<input type="submit" value="query submit" style="font-family: verdana;font-size: 11px;"/>	
</form>
</body>
</html>

            