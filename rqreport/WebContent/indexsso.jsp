<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%
String encusertype = request.getParameter("usertype");

// 업무단 decoding 처리 로직
String decusertype = "encusertype.decoded";
%>
<rquser:rqUser action="login" usertype="<%=decusertype%>" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title> REQUBE REPORT </title>
<meta name="Generator">
<meta name="Author" content="">
<meta name="Keywords" content="">
<meta name="Description" content="">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="setup/rqindex.js"></script>
<script language="javascript">
function submitFn(){
	loginform = document.login;
	if(loginform.strUserid.value ==''){
		alert('<rqfmt:message strkey="index.alert.idcheck"/>');
		loginform.strUserid.focus();
	}else if(loginform.strUserpw.value == ''){
		alert('<rqfmt:message strkey="index.alert.pwcheck"/>');
		loginform.strUserpw.focus();
	}else{
		loginform.action = "index.jsp";
		loginform.submit();
	}
}
</script>
</head>

<body topmargin="0" leftmargin="0" class="login_bg" onload="document.login.strUserid.focus();">
<form name="login" method="post" action="javascript:submitFn();">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0"  class="login_ccbg">
<tr>
	<td align="center" valign="middle">
		<table width="865" height="15" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<!--td align="right"><img src="img/login_ico_help.gif" width="28" height="9" border="0" alt=""></td-->
		</tr>
		</table>
		<table width="897"height="592" border="0" cellpadding="0" cellspacing="0" class="login_bgc">
		<tr>
			<td colspan=3  height="95"></td>
		</tr>
		<tr>
			<td width="377" height="397"><img src="img/login_l_img.jpg" width="378" height="397" border="0" alt=""></td>
			<td width="77" height="397"></td>
			<td width="433" height="397">
				<table width="" height="" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" height="69"><img src="img/login_logo.gif" width="327" height="69" border="0" alt=""></td>
				</tr>
				<tr>
					<td align="left" valign="middle" height="111"  class="maintext">
						<rqfmt:message strkey="index.optimized.msg"/><br><br>
						<rqfmt:message strkey="index.nowbrowser.prefix"/>
						&nbsp;<script>document.write(msg);</script>&nbsp;
						<rqfmt:message strkey="index.nowbrowser.suffix"/>
					</td>
				</tr>
				<tr>
					<td align="left" valign="top" height="24"><IMG SRC="img/login_logintit.gif" WIDTH="90" HEIGHT="11" BORDER="0" ALT=""></td>
				</tr>
				<tr>
					<td align="left" valign="top">
						<table width=""height="" BORDER="0" CELLPADDING="0" CELLSPACING="0">
						<tr>
							<td width="79" height="29" align="left" valign="top"><img src="img/<rqfmt:message strkey='index.id.img'/>" width="79" height="29" border="0" alt="id"></td>
							<td width="168" height="29" align="left" valign="middle"><input name="strUserid" type="text" value="" maxlength="40" style="font-family:verdana;color:#666666;border:1px solid #D4D4D4;background-color:#F8F8F8;" onkeydown="tab()"/></td>
							<td width="80" align="left" valign="top" rowspan=2><input type="image" src="img/login_button.gif" border="0" onFocus="this.blur()"></td>
						</tr>
						<tr>
							<td width="79" height="29" align="left" valign="top"><img src="img/<rqfmt:message strkey='index.password.img'/>" width="79" height="29" border="0" alt="pw"></td>
							<td width="168" height="29" align="left" valign="middle"><input name="strUserpw" type="password" maxlength="40" style="	font-family:verdana;color:#666666;border:1px solid #D4D4D4;background-color:#F8F8F8;" value=''/></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="135"></td>
				</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan=3  height="100"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
</form>
</body>
</html>
