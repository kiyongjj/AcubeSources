<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<%
String strAuth = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
}
%>
<%
Environment env = Environment.getInstance();

String dir = env.logrqdirname;
String filename = env.logrqfilename;
String filenameWithOutEx = filename.substring(0, filename.indexOf(".") );
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript">
rqenv = {}
rqenv.logManage = function(){}
rqenv.logManage.prototype = {
	viewlog : function(name){
	<%if(env.rqreport_server_loggingtype.equals("buffer")){%>
		logbr.location.href="logbr.jsp?file=" +name;
	<%}else{%>
		logbr.location.href="logTypeText.jsp?file=" +name;
		//logbr.location.href="logTypeText_hardcoding.jsp?file=" +name;
	<%}%>
	},
	reflesh : function(){
		logbr.document.location.reload();
	},
	logoutCfm : function(){
		if(confirm("<rqfmt:message strkey='common.logout.alert'/>")){
			document.location.href="../common/sessionPrc.jsp?mode=sessionLogOut";
		}else{
			return;
		}
	},
	bluring : function(){
		if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG") document.body.focus();
	},
	downLoadFile : function(ele){
		var selectlog = document.getElementById("log");
		var lm_filename = "";
		//alert(selectlog.options.selectedIndex);
		//alert(selectlog.options[selectlog.options.selectedIndex].text);
		document.location.href = "RQdownload.jsp?filename="+selectlog.options[selectlog.options.selectedIndex].text;
	}
}
window.onload = function(){
	logManagePage = new rqenv.logManage();
}
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="f8f8f8" class="main_bg">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<!--top시작 -->
    	<table width="1004" border="0" cellpadding="0" cellspacing="0" >
        <tr>
        	<td align="left" valign="top" width="465" height="151" class="main_lbg" >
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td style="padding-left:33px;padding-top:35px;"><img src="../img/main_logo.gif" width="290" height="31" border="0" alt=""></td>
				</tr>
				</table>
			</td>
		  	<td height="151"><IMG SRC="../img/main_bimg06.jpg" WIDTH="539" HEIGHT="151" BORDER="0" ALT=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><a href="javascript:logManagePage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
			</tr>
			</table>
		</div>
      	<!--topP끝 -->
    </td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td width="217" valign="top" class="main_menubgb">
				<!-- menu -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td height="578" class="main_menubg" valign="top">
						<table width="217" height="" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="16"></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../document/document.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.document.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.document.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.document.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="folder"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../usergroup/user.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="user"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../db/db.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="data"></a></td>
						</tr>
					<%
						if( strAuth.equals("A")){
					%>
						<tr>
							<td height="45" valign="top"><a href="../environment/environment.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="install"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/rqenv.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="environment"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/logman.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.logman.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="logman"></a></td>
						</tr>
						<%
							String isUse_schedule = "false";
							if(oUserModel != null){
								isUse_schedule = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "scheduler");
							}
							if(isUse_schedule.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../schedule/rqscheduletimelist.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="schedule"></a></td>
						</tr>
						<%
							}
							String isUse_statistics = "false";
							if(oUserModel != null){
								isUse_statistics = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "RQStatistics");
							}
							if(isUse_statistics.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../environment/docstat.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="statistics"></a></td>
						</tr>
					<%
							}
						}
					%>
				  		</table>
					</td>
				</tr>
				<tr>
					<td height="100%" class="main_menubgb"></td>
				</tr>
			  	</table>
			  	<!-- menu	끝-->
			</td>
			<td width="787" valign="top" class="main_conbg">
				<!-- contents -->
				<table width="100%" class="main_conbgtop6" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" style="padding-left:23px;" colspan="3"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text" ><rqfmt:message strkey='environment.logman.title'/></span></td>
				</tr>
				<tr>
					<td height="100%" valign="middle" style="padding-left:7px;" width="25%">

						<select name="log" id="log" size="1" style="width:192px;font-family:verdana;" onChange="logManagePage.viewlog(this.options[selectedIndex].text)">
					<%
						if(!dir.equals("")){
							File fs = new File(dir);
							String[] filelist = fs.list();
							for(int i = 0 ; i < filelist.length ;  i++){
								if( filelist[i].startsWith(filenameWithOutEx) ){
					%>
									<option><%=filelist[i] %></option>
					<%
								}
							}
						}else{
					%>
							<option><rqfmt:message strkey='environment.logman.novariable'/></option>
					<%
						}
					%>
						</select>
					</td>
					<td>
						<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><img src="../img/btn_g_left2.gif"></td>
							<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="logManagePage.downLoadFile(this);return false;"><rqfmt:message strkey='environment.logman.filedownload'/></a></td>
							<td><img src="../img/btn_g_right.gif"></td>
						</tr>
						</table>
					</td>
					<td align="right" style="padding-right:15px;">
					<%
						if(env.rqreport_server_loggingtype.equals("buffer")){
					%>
						<table width="415"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title" style="font-family:verdana;" width="400">
								<rqfmt:message strkey='environment.logman.logbuffersize'/> : <%=env.rqreport_server_logbuffersize%> MB, <rqfmt:message strkey='environment.logman.logintervalTime'/> : <%=env.rqreport_server_logintervalTime%> ms(<%=env.rqreport_server_logintervalTime/1000%><rqfmt:message strkey='environment.logman.logchkunit'/>)
							</td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					<%
						}
					%>
					</td>
				</tr>
				<tr><td height="7" colspan="3"></td></tr>
				<tr>
					<td valign="top" style="padding-left:7px;padding-right:15px;" colspan="3">
						<%if(env.rqreport_server_loggingtype.equals("buffer")){%>
							<iframe name="logbr" id="logbr" width="100%" marginheight="3" marginwidth="3" height="500px" src="logbr.jsp?file=<%=filename%>"></iframe>
						<%}else{%>
							<iframe name="logbr" id="logbr" width="100%" marginheight="3" marginwidth="3" height="500px" src="logTypeText.jsp?file=<%=filename%>"></iframe>
							<!--iframe name="logbr" id="logbr" width="100%" marginheight="3" marginwidth="3" height="500px" src="logTypeText_hardcoding.jsp?file=<%=filename%>"></iframe-->
						<%}%>
					</td>
				</tr>
				<tr><td height="7" colspan="3"></td></tr>
				<tr>
					<td colspan="3" align="right" style="padding-right:15px;">
						<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><img src="../img/btn_g_left2.gif"></td>
							<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="logManagePage.reflesh();return false;"><rqfmt:message strkey='environment.logman.button.refresh'/></a></td>
							<td><img src="../img/btn_g_right.gif"></td>
						</tr>
						</table>
					</td>
				</tr>
				</table>
				<!--contents 끝 -->
			</td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="14" class="main_conbgb"></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="44" style="padding-left:39px;" valign="bottom"><IMG SRC="../img/copy_2.gif" width="382" height="8" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<br>
<br>
</body>
</html>
