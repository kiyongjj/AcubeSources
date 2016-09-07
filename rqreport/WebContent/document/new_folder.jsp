<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<%
Environment env = Environment.getInstance();
String foldername = "";
String pathIs = request.getParameter("pathIs");
Encoding enc = new Encoding();
String lm_serverCharset = enc.getServerCharset();
String lm_RQCharset = enc.getRQCharset();
pathIs = Encoding.chCharset(pathIs, lm_serverCharset, lm_RQCharset);

int res = 0;
if(request.getParameter("foldername") != null && !request.getParameter("foldername").trim().equals("")){
    foldername = request.getParameter("foldername");
    foldername = Encoding.chCharset(foldername, lm_serverCharset, lm_RQCharset);
    foldername = foldername.trim();

	UserModel UM = (UserModel) session.getAttribute("UM");
	RQControl oRQControl = new RQControl(UM);
	res = oRQControl.RQmakeFoler(pathIs + foldername);
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="JavaScript" type="text/JavaScript">
var rqdocument = {}
rqdocument.newFolderPage = function(){
	this.res = <%=res%>;
}
rqdocument.newFolderPage.prototype = {
	chkfoldername : function(){
		var foldername = "<%=foldername%>";
		if(foldername != ""){
			if(this.res == -1){
				alert('"' + foldername + '" ' + '<rqfmt:message strkey="document.new_folder.alert.foldernamealready"/>');
				self.close();
				return;
			}
			//alert(window.opener.document.frmDirList.order_flag.value);
			//window.opener.document.frmDirList.order_flag.value="4";
			opener.documentPage.on_reload("4"); // already declare in opener 
			//window.opener.document.location.href = window.opener.document.URL;
			self.close();
		}else{
			return;
		}
	},
	makeFolder : function(foldername){
		var tmp = foldername;
		tmp = tmp.replace(/\s/g,"");
	
		if(tmp.length == 0){
			alert("<rqfmt:message strkey='document.new_folder.alert.insertfoldername'/>");
			$("#foldername").focus();
			return;
		}else if( tmp.substring(0,1) == "." ){
			alert("<rqfmt:message strkey='document.new_folder.alert.authfoldername'/>");
			return;
		}else{
	
			//var foldername = foldername;
			if(this.checkSpChar(tmp) == true){
				$("#mkFolder").submit();
			}
		}
	},
	checkSpChar : function(tmp){
		for( i = 0 ; i < tmp.length ; i++){
			charAt = tmp.charAt(i);
			charAtASC = charAt.charCodeAt();
	
			if( charAt == "\\" || charAt == "\/" || charAt == "\:" || charAt == "\?" ||
			    charAt == "\*" || charAt == "\?" || charAt == '\"' || charAt == "\<" ||
			    charAt == "\>" || charAt == "\|" || charAt == "\'" ||
			    charAt == "\~" || charAt == "\!" || charAt == "\@" || charAt == "\#" ||
			    charAt == "\$" || charAt == "\%" || charAt == "\^" || charAt == "\&" ||
			    charAt == "\(" || charAt == "\)" )
			{
				var msga  = "<rqfmt:message strkey='document.new_folder.alert.spchar'/>";
					msga += " \\ \/ \: \* \? \" \< \> \| \' \~ \! \@ \# \$ \% \^ \& \( \) ";
					msga += "<rqfmt:message strkey='document.new_folder.alert.spchar2'/>";
				alert(msga);
				$("#foldername").focus();
				return false;
			}
		}
		return true;
	}
}
$(function(){
	newFolderPage = new rqdocument.newFolderPage(); 
	newFolderPage.chkfoldername();
	$("#foldername").focus();
	$("#foldername").keydown(function(event){
		//alert(event.keyCode);
	  	switch (event.keyCode) {
		    case 13 :
		    	newFolderPage.makeFolder($("#foldername").attr("value"));
		    break;
		}
	});
	$("#mkfolderlink").css("cursor","pointer").click(function(){
		newFolderPage.makeFolder($("#foldername").attr("value"));
	});
	$("#closelink").css("cursor","pointer").click(function(){
		window.close();
	});
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<!--
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
 -->
<form name="mkFolder" id="mkFolder" method="post" class="mkf">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='document.new_folder.title'/></td>
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
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td width="15">&nbsp; </td>
			<td>
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				 <tr>
                <td height="20"></td>
              </tr>
				<tr>
					<td height="30">
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
						<tr>
						  <td class="pop_bar" colspan="3" height="2"></td>
						</tr>
						<tr>
						  <td width="30%" height="35"  class="stitle_b"><rqfmt:message strkey='document.new_folder.foldername'/></td>
						  <td class="input_td">
						  	<input name="foldername" type="text" size="24" maxlength="50" id="foldername"/><input type="text" style="width:0; visibility:hidden;">
						  </td>
						</tr>
						<tr>
						  <td height="1" colspan="4" bgcolor="#75B8C1"></td>
						</tr>
					  </table>
					</td>
				</tr>
				<tr>
					<td height="15"></td>
				</tr>
				<tr>
					<td height="15" align="center">
						<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><span class="btn_green" id="mkfolderlink"><rqfmt:message strkey='document.common.button.ok'/></span></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
							<td width="8"> </td>
							<td>
								<table border="0" align="left" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><span class="btn_green" id="closelink"><rqfmt:message strkey='document.common.button.cancel'/></span></td>
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
			<td width="20"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<input type="hidden" name="pathIs" id="pathIs" value="<%=pathIs%>"/>
</form>
</body>
</html>
