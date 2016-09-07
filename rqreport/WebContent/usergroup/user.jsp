<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.repository.*"%>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQPage.tld" prefix="page" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<rquser:rqUser action="getUserList"/>
<rquser:rqUser action="getOResultArr"/>
<%
Environment env = Environment.getInstance();
String strAuth = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
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
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="JavaScript" type="text/JavaScript">
rquser = {}
rquser.userManage = function(){
	browsername = navigator.appName;
	IE = 'Microsoft Internet Explorer';
	if (browsername==IE) {
		isIE = 1;
	}else {
		isIE = 0;
	}
}
rquser.userManage.prototype = {
	TrColor : function(element) {
		if(element.checked) {
			this.changeBgColor(element);
		}else{
			this.rollbakcBgColor(element);
		}
	},
	changeBgColor : function(element) {
		if (isIE) {
			while (element.tagName!="TR") {
				element = element.parentElement;
				//alert('ljg');
			}
	
		} else {
			while (element.tagName!="TR") {
				element = element.parentNode;
			}
		}
		element.style.backgroundColor = "#EDEDE8";
	},
	rollbakcBgColor : function(element) {
		if (isIE) {
			while (element.tagName!="TR") {
				element = element.parentElement;
			}
		} else {
			while (element.tagName!="TR") {
				element = element.parentNode;
			}
		}
		element.style.backgroundColor = "#FFFFFF";
	},
	mouseOver : function(element){
		if (element.childNodes[0].childNodes[0].checked){
			element.style.backgroundColor = "#EDEDE8";
		}else {
			element.style.backgroundColor = "#EDEDE8";
		}
	},
	mouseOut : function(element,vcolor){
		//alert(vcolor);
		if (!element.childNodes[0].childNodes[0].checked) {
			element.style.backgroundColor = vcolor;
		}else{
			element.style.backgroundColor = "#EDEDE8";
		}
	},
	selectchkalldoc : function() {
		forms = document.frmUserList;
		flag = true;
		for( i=0; i<forms.elements.length; i++) {
			if (forms.elements[i].name=='chkuser') {
				if (forms.elements[i].checked == false) {
					flag = false;
					break;
				}
			}
		}
		if (flag == false) {
			for( i=0 ; i<forms.elements.length ; i++) {
				if (forms.elements[i].name=='chkuser') {
					forms.elements[i].checked = true;
					this.TrColor(forms.elements[i]);
				}
			}
		}
		else {
			for( i=0; i<forms.elements.length; i++) {
				if (forms.elements[i].name=='chkuser') {
					forms.elements[i].checked = false;
					this.TrColor(forms.elements[i]);
				}
			}
		}
	},
	openBrWindow : function(theURL,winName,features) { //v2.0
	  	obj = window.open(theURL,winName,features);
	  	obj.focus();
	},
	handlerUser : function(mode){
		forms = document.frmUserList;
		forms.mode.value = mode;
		forms.action='setUI.jsp';
		flag = false;
		cnt = 0;
		for( i=0 ; i<forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkuser') {
				if (forms.elements[i].checked == true) {
					flag = true;
					break;
				}
			}
		}
		for( i=0 ; i<forms.elements.length ; i++){
			if(forms.elements[i].name=='chkuser'){
				if(forms.elements[i].checked == true){
					cnt++;
				}
			}
		}
		if (flag == false) {
			alert("<rqfmt:message strkey='usergroup.user.selecttarget'/>");
			return;
		}else if(mode == 'modify' && cnt > 1){
			alert("<rqfmt:message strkey='usergroup.user.nomultiselecttarget'/>");
			return;
		}else{
			if(mode == 'del'){
				if ( confirm("<rqfmt:message strkey='usergroup.user.confirm.delete'/>") ) {
					document.frmUserList.submit();
				}
			}else if(mode == 'modify'){
				winobj = window.open("modifyuser.jsp","modifyuser","width=301,height=330");
				winobj.focus();
				forms.target = "modifyuser";
				forms.method = "post";
				forms.action = "modifyuser.jsp";
				forms.submit();
				forms.target = '_self';
				forms.action = 'user.jsp';
			}
		}
	},
	logoutCfm : function(){
		if(confirm("<rqfmt:message strkey='common.logout.alert'/>")){
			document.location.href="../common/sessionPrc.jsp?mode=sessionLogOut";
		}else{
			return;
		}
	},
	go : function(page, fullPath){
		document.frmUserList.strCurrentPage.value = page;
		document.frmUserList.submit();
	},
	bluring : function (){
		if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG") document.body.focus();
	},
	userInit : function(){
		// page Navigation
		$("a").click(function(){ 
			var aindex = "a:eq(" + $("a").index(this) + ")"; 
			var name_value = $(aindex).attr("name");
			if(name_value == "gopage"){
				var page_value = $(aindex).attr("pagevalue");
				var path_value = $(aindex).attr("pathvalue");
				userPage.go(page_value,path_value); // this.go not applied 
			}
		});
	}
}
$(function(){
	userPage = new rquser.userManage();
	userPage.userInit();
});
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
			</table></td>
		  	<td height="151"><img src="../img/main_bimg05.jpg" width="539" height="151" border="0" alt=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><a href="javascript:userPage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
			</tr>
			</table>
		</div>
      	<!--top끝 -->
    </td>
</tr>
<tr>
	<td>
		<!--left시작 -->
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
							<td height="45" valign="top"><a href="../usergroup/user.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="user"></a></td>
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
							<td height="45" valign="top"><a href="../environment/logman.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="logman"></a></td>
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
				<!--contents 시작 -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop5" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text"/><rqfmt:message strkey='usergroup.user.title'/>
				</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;" >
						<table width="730"  border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>

								<!-- frmUserList -->
								<form name="frmUserList" method="post" action="user.jsp">

								<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="2" colspan="13" bgcolor="#A8CC72"></td>
				  				</tr>
				  				<tr class="table_title">
									<td width="50" height="26" class="stitle">
										<input type="checkbox" name="chkalldoc" value="checkbox" onClick="javascript:userPage.selectchkalldoc()" onfocus="this.blur()">
									</td>
									<td valign="bottom" width="1" class="stitle">
										<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td></td>
										</tr>
										</table>
									</td>
									<td class="table_title"><rqfmt:message strkey='usergroup.user.id'/></td>
									<td valign="bottom" width="1" class="stitle">
										<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td></td>
										</tr>
										</table>
									</td>
									<td class="table_title"><rqfmt:message strkey='usergroup.user.authority'/></td>
									<td valign="bottom" width="1" class="stitle">
										<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td></td>
										</tr>
										</table>
									</td>
									<td class="table_title"><rqfmt:message strkey='usergroup.user.description'/></td>
								</tr>
								<tr>
									<td height="1" colspan="13" bgcolor="#A8CC72"></td>
								</tr>

							<%
								ArrayList oUserList = (ArrayList) pageContext.getAttribute("oUserList");
								ArrayList oResultArr = (ArrayList) pageContext.getAttribute("oResultArr");
								UserInfo ui = null;
								int iColor = 1;
								for(int i = 0; i < oResultArr.size(); i++) {
									ui = (UserInfo)oResultArr.get(i);
									//show only myself if auth "G"
									if(strAuth.equals("G")){
										if(!oUserModel.getUserid().equals(ui.getParamString(0))){
											continue;
										}
									}
							%>
								<tr bgcolor="<%if(iColor%2 == 0){%>#F9F9F9<%}else{%>#FFFFFF<% }%>">
									<td class="text">
										<input type="checkbox" name="chkuser" value="<%=ui.getParamString(0)%>" onClick="userPage.TrColor(this)" onfocus="this.blur()">
									</td>
									<td width="1"></td>
									<td class="text" style="font-family: verdana;">
										<%=ui.getParamString(0) %>
									</td>
									<td width="1"></td>
									<td class="text" style="font-family: verdana;">
										<%
											if(ui.getParamString(5).equals("A")){
										%>
												<rqfmt:message strkey='usergroup.newuser.authority.manager'/>
										<%
											}else{
										%>
												<rqfmt:message strkey='usergroup.newuser.authority.general'/>
										<%
											}
										%>
									</td>
									<td width="1"></td>
									<td class="text" style="font-family: verdana;">
										<%=ui.getParamString(3) %>
									</td>
								</tr>
								<tr>
									<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
								</tr>
							<%
									iColor++;
								}
							%>
								<tr>
									<td height="1" colspan="13" bgcolor="#A8CC72"></td>
								</tr>
								</table>
								<input type="hidden" name="strCurrentPage"/>
								<input type="hidden" name="mode"/>
								</form>
								<!-- frmUserList end -->

							</td>
						</tr>
						<tr>
							<td height="15"></td>
						</tr>
						<tr>
							<td height="15" align="center">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td height="1" bgcolor="F0F0F0"></td>
								</tr>
								<tr>
									<td height="24" align="center" bgcolor="FCFCFC">
										<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<page:pageNavigation action="pageNavi" list="<%=oUserList%>" listCount="<%=10 %>" pageCount="<%=10%>"/>
											</td>
										</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="1" bgcolor="F0F0F0"></td>
								</tr>
								</table>
							</td>
						</tr>
						<tr><td height="14" width="1"></td></tr>
						<tr>
							<td>

								<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
									  	<table align="left" cellspacing="0" cellpadding="0" border="0">
										<tr>
											<!--
										     BEGIN jia1.liu add for user modify 2008.7.17 
										    -->
										    <td width="7"> </td>
										    <td> 
												<table border="0" align="left" cellpadding="0" cellspacing="0">
												<tr>   	
												  	<td><img src="../img/btn_w_left.gif" width="15" height="20"></td>
												  <td background="../img/btn_w_bg.gif" class="btn_text_td"><a href="javascript:userPage.handlerUser('modify');" class="btn_white"><rqfmt:message strkey='usergroup.user.modify'/></a></td>
												  	<td><img src="../img/btn_w_right.gif" width="8" height="20"></td>
												</tr>
											  	</table>
										    </td>
										    <!--
										     END jia1.liu add for user modify 2008.7.17
										    -->
											<td width="7"></td>
											<td>
												<table border="0" align="left" cellpadding="0" cellspacing="0">
												<tr>
													<td><img src="../img/btn_w_left.gif" width="15" height="20"></td>
												  	<td background="../img/btn_w_bg.gif" class="btn_text_td"><a href="javascript:userPage.handlerUser('del');" class="btn_white"><rqfmt:message strkey='usergroup.user.delete'/></a></td>
												  	<td><img src="../img/btn_w_right.gif" width="8" height="20"></td>
												</tr>
											  	</table>
										    </td>
										    
										</tr>
										</table>
									</td>
								 	<td align="right">
								  		<table cellspacing="0" cellpadding="0" border="0">
									  	<tr>
											<td>
												<table border="0" cellspacing="0" cellpadding="0">
												<tr>
											  		<td><img src="../img/btn_g_left_user.gif" width="4" height="20"></td>
											  		<td background="../img/btn_g_bg_user.gif"><img src="../img/icon_user_regis.gif" width="20" height="14" hspace="6"></td>
											  		<td background="../img/btn_g_bg_user.gif" class="btn_text_td"><a href="javascript:userPage.openBrWindow('newuser.jsp','newuser','width=301,height=374')" class="btn_green" style="color: white;"><rqfmt:message strkey='usergroup.user.newuser'/></a></td>
											  		<td><img src="../img/btn_g_right_user.gif" width="8" height="20"></td>
												</tr>
										  		</table>
										  	</td>
									  	</tr>
										</table>
									</td>
								</tr>
							  	</table>

							</td>
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
<BR>
</BODY>
</HTML>
