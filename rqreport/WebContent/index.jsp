<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<rquser:rqUser action="login"/>
<% 
Environment g_env = Environment.getInstance();
String user_agt = request.getHeader("user-agent");
String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";
%>
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
<script language="javascript" src="setup/<%=g_env.jsframework%>"></script>
<script language="javascript" src="setup/rqglobalfunc.js"></script>
<script language="javascript" src="setup/rqindex.js"></script>
<script language="javascript">
gfunc = new rqglobalfunc.rqglobal(); 
index = new rqindex.index();
$(function(){
	msg_idcheck = "<rqfmt:message strkey="index.alert.idcheck"/>";
	msg_pwcheck = "<rqfmt:message strkey="index.alert.pwcheck"/>";
	gfunc.initBase64();
	index.inputUrename();
	index.init();
	
	$("#rqcheck").click(function(){
		//document.location.href="/rqreport/rqcheck/RQtest.jsp";
	});

});
</script>
<script language="javascript">
function documentReflesh(){
	////////////// Applet call this method after viewer install end .////////
	//if(confirm("RQViewer Installed Now\nDocument reflesh ?")){
	//	document.location.reload();
	//}
	////////////////////////////////////////////////////////////////////////
}
</script>
</head>

<body topmargin="0" leftmargin="0" class="login_bg">
<!-- script language="javaScript" SRC="/rqreport/setup/rqdownloader.js"></script -->
<form name="login" method="post" action="javascript:index.submitFn();">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0"  class="login_ccbg">
<tr>
	<td align="center" valign="middle">
		<table width="865" height="15" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td align="right">
				<span id="rqcheck">&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<!--img src="img/login_ico_help.gif" width="28" height="9" border="0" alt=""-->
			</td>
		</tr>
		</table>
		<table width="897"height="592" border="0" cellpadding="0" cellspacing="0" class="login_bgc">
		<tr>
			<td colspan="3"  height="95"></td>
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
					<td align="left" valign="middle" height="111"  class="maintext" id="bwMsgBox">
						<rqfmt:message strkey="index.optimized.msg"/><br><br>
						<rqfmt:message strkey="index.nowbrowser.prefix"/>
						&nbsp;<span id="bwType"></span>&nbsp;
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
							<td width="79" height="29" align="left" valign="top">
								<img src="img/<rqfmt:message strkey='index.id.img'/>" width="79" height="29" border="0" alt="id">
							</td>
							<td width="168" height="29" align="left" valign="middle">
								<input name="strUserid" tabIndex="1" id="strUserid" type="text" value="" maxlength="40"/>
							</td>
							<td width="80" align="left" valign="top" rowspan="2">
								<input type="image" tabIndex="3" src="img/login_button.gif" border="0">
							</td>
						</tr>
						<tr>
							<td width="79" height="29" align="left" valign="top">
								<img src="img/<rqfmt:message strkey='index.password.img'/>" width="79" height="29" border="0" alt="pw">
							</td>
							<td width="168" height="29" align="left" valign="middle">
								<input name="strUserpw" tabIndex="2" id="strUserpw" type="password" maxlength="40" value=''/>
							</td>
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
			<td colspan="3"  height="100"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
</form>
<% if(!viewer_type.equals("ocx")){ %>
<script type="text/javascript" src="/rqreport/setup/rqdownloadapp.js"></script>
<% } %>
</body>
</html>
