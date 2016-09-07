<%
/**
 * <p>Title: REQUBE REPORT</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Samsung SDS Co., Ltd.</p>
 * @author : lee jae gyu
 * @version 1.0
 */
%>

<%@ page language="java" contentType="text/html;UTF-8" %>

<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<rquser:rqUser action="login"/>

<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script language="javascript">

	function Login(){
		if (form1.strUserid.value == "") {
			alert('type user ID !!');
			form1.strUserid.focus();
			return;
		}
		if (form1.strUserpw.value == "") {
			alert('type user PASSWORD !!');
			form1.strUserpw.focus();
			return;
		}
		form1.submit();
	}

</script>

</head>
<body>
<form name="form1" method="post" action="login.jsp">
<table border="1" cellpadding="1" cellspacing="3" width="350">
<tr>
	<td width="100">USERID : </td>
	<td width="250"><input type="text" name="strUserid" value=""></td>
</tr>
<tr>
	<td>PASSWORD : </td>
	<td><input type="password" name="strUserpw" value=""></td>
</tr>
<tr>
	<td colspan="2"><input type="button" value="submit" onClick="javascript:Login();"></td>
</tr>
<tr>
	<td colspan="2">admin/admin</td>
</tr>
</table>
</form>
</body>
</html>