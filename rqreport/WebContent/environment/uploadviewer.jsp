<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<jsp:include flush="true" page="/cs.jsp"/>
<%
Environment env = Environment.getInstance();
String pathIs = request.getParameter("pathIs");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="javascript" src="../setup/jquery.filestyle.mini.js"></script>
<script language="JavaScript" type="text/JavaScript">
rqenv = {}
rqenv.uploadviewer = function(){}
rqenv.uploadviewer.prototype = {
	uploadFile : function(){
		
		var fileObj = document.getElementById("fileobj1");
		filename = fileObj.value;
		if(fileObj.value = ""){
			alert("select upload file...");
			document.getElementById("fileobj1").focus();
			return;
		}else{
			if(fileObj.value.indexOf("/")> -1){
				filename = fileObj.value.substring(fileObj.value.lastIndexOf("/")+1);
			}else{
				filename = fileObj.value.substring(fileObj.value.lastIndexOf("\\")+1);
			}
		}
		if(filename != "RQViewer.cab"){
			alert("<rqfmt:message strkey='environment.rqenv.uploadviewer.alert.chkupfile'/>");
			return;
		}else{
			
			document.getElementById("frmlee").submit();
		}
		//return true;
	},
	MM_callJS : function (jsStr) { //v2.0
		return eval(jsStr)
	}
}
$(function(){
	gfunc = new rqglobalfunc.rqglobal(); 
	uploadPage = new rqenv.uploadviewer();
	$("#fileobj1").filestyle({ 
    	image: "../img/btn_search.jpg",
     	imageheight : 25,
     	imagewidth : 82,
     	width : 150
 	});

});
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form name="frmlee" id="frmlee" method="post" action="../common/uploadPrc.jsp?mode=upviewer" enctype="multipart/form-data">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='environment.rqenv.uploadviewer.title'/></td>
				</tr>
				</table>	
			</td>
			<td><img src="../img/popup_top_r.gif" width="137" height="50" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr> 
			<td width="15">&nbsp; </td>
			<td>
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr> 
					<td>
						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr> 
							<td height="20"></td>
						</tr>
						<tr> 
							<td height="30">
								<table width="100%" border="0" cellspacing="0" cellpadding="0" >
								<tr> 
									<td class="pop_bar" colspan="2"></td>
								</tr>
								<tr> 
									<td width="30%" height="35"  class="stitle_b">RQViewer.cab</td>
									<td class="input_td">
										<input type="file" name="fileobj1" id="fileobj1" size="20" onkeydown="event.returnValue=false;"/>
									</td>
								</tr>
								<tr> 
									<td height="2" colspan="2" bgcolor="#75B8C1"></td>
								</tr>
								</table>
							</td>
						</tr>
						<tr> 
							<td height="13"></td>
						</tr>
						<tr> 
							<td height="15" align="center">
								<table cellspacing="0" cellpadding="0" border="0">
								<tr> 
									<td> 
										<table border="0" cellpadding="0" cellspacing="0">
										<tr> 
											<td><img src="../img/btn_g_left2.gif"></td>
											<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="javascript:uploadPage.uploadFile();"><rqfmt:message strkey='environment.common.button.ok'/></a></td>
											<td><img src="../img/btn_g_right.gif"></td>
										</tr>
										</table>
									</td>
									<td width="8"> </td>
									<td> 
										<table border="0" align="left" cellpadding="0" cellspacing="0">
										<tr> 
											<td><img src="../img/btn_g_left2.gif"></td>
											<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="uploadPage.MM_callJS('window.close()')"><rqfmt:message strkey='environment.common.button.cancel'/></a></td>
											<td><img src="../img/btn_g_right.gif"></td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						<tr> 
							<td height="15" align="center">&nbsp;</td>
						</tr>
						</table>
					</td>
					<td width="15"></td>
				</tr>
				</table>
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>
</form>
</body>
</html>


