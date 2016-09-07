<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQHandle.tld" prefix="rqhandle" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<%
Environment env = Environment.getInstance();
String strAuth = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
}
String viewerFileName = "RQViewer.cab";
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
rqenv = {}
rqenv.envManage = function(){}
rqenv.envManage.prototype = {
	openBrForFormAct : function(theURL,winName,features) { //v2.0
		//////////// browser compatibility ///////////////////////////////////////////////////////
		if(browserName != "Internet Explorer"){
			alert(browserName + " <rqfmt:message strkey='common.compatibility.incompatible'/>");
			return;
		}
		//////////////////////////////////////////////////////////////////////////////////////////
	  	obj = window.open("",winName,features);
	  	obj.focus();
		document.frm.target = "newViewer" ;
		document.frm.action = theURL;
		document.frm.submit();
	},
	viewerInstall : function(){
		obj = window.open("viewerInstall.html","viewerInstall","width=490, height=400");
		obj.focus();
		document.frm.target = "viewerInstall" ;
		document.frm1.submit();
	},
	rollback : function(){
		if(!confirm("<rqfmt:message strkey='environment.rqenv.alert.rollbackconfirm'/>")){return;}
		document.frm2.submit();
	},
	crtTableFn : function(){
		if(!confirm("<rqfmt:message strkey='environment.rqenv.createdatabase.confirm'/>")){return;}
		document.frm3.submit();
	},
	crtDocStattable : function(){
		if(!confirm("<rqfmt:message strkey='environment.rqenv.createstattable.confirm'/>")){return;}
		document.frm4.submit();
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
	}
	//document.onfocusin=bluring;
}

$(function(){
	gfunc = new rqglobalfunc.rqglobal(); 
	envPage = new rqenv.envManage();
});

</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="f8f8f8" class="main_bg">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<!--top 시작 -->
    	<table width="1004" border="0" cellpadding="0" cellspacing="0" >
        <tr>
        	<td align="left" valign="top" width="465" height="151" class="main_lbg" >
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td style="padding-left:33px;padding-top:35px;"><img src="../img/main_logo.gif" width="290" height="31" border="0" alt=""></td>
				</tr>
				</table>
			</td>
		  	<td height="151"><img src="../img/main_bimg03.jpg" width="539" height="151" border="0" alt=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><a href="javascript:envPage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
			</tr>
			</table>
		</div>
      	<!--top 끝 -->
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
						String isUse_statistics = "false";
						if( strAuth.equals("A")){
					%>
						<tr>
							<td height="45" valign="top"><a href="../environment/environment.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.environment.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="install"></a></td>
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
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop3" style="padding-left:23px;"><IMG SRC="../img/main_condot.gif" WIDTH="23" HEIGHT="20" BORDER="0" ALT="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text"/><rqfmt:message strkey='environment.rqenv.title'/>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;" >
						<table width="750"  border="0" cellspacing="0" cellpadding="0"> <!-- 750 -->
						<tr>
							<td height="14" background="../img/con_up_right.gif"></td>
							<td height="14" background="../img/con_up_bg.gif"></td>
							<td height="14" background="../img/con_up_left.gif"></td>
						</tr>
						<tr>
							<td valign="top" width="14" background="../img/con_middle_left.gif"></td>
							<td valign="top" bgcolor="#FFFFFF">
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>

												<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr height='7'><td></td></tr>
												<tr height='1'><td background='../img/view_list.gif'></td></tr>
												<tr height='1'><td></td></tr>
												<tr>
													<td>

														<table border='0' cellpadding='5' cellspacing='0' width='100%' class='border_C'>
														<tr>
															<td bgcolor='F4F4F4' class='border_F' width="90" align="center"><span style=" font-size:9pt; font-family:Verdana; color:#2E5010; text-decoration:none;"><rqfmt:message strkey='environment.rqenv.viewerinfo'/></span></td>
															<td bgcolor='F4F4F4' class='border_F'>

																<span style=" font-size:9pt; font-family:Verdana; color:#487D1A; text-decoration:none;">
																	<rqhandle:rqHandler action="viewerStat"/>
																 </span>

															</td>
														</tr>
														</table>

													</td>
												</tr>
												<tr height='1'><td></td></tr>
												<tr height='1'><td background='../img/view_list.gif'></td></tr>
												</table>

											</td>
										</tr>
										</table>
									</td>
								</tr>

								<tr>
									<td valign="top">
										<table width="100%"  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td height="15" colspan="2"></td>
										</tr>
										<tr>
											<td width="114"><a href="javascript:envPage.openBrForFormAct('uploadviewer.jsp','newViewer','width=397,height=160')" class="envlist"><img src="../img/main_m_img2.gif"  hspace="14" border="0"></a></td>
											<td valign="top">
												<form name="frm" method="post">
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td valign="top" height="20">
														<img src="../img/db_m_bullet.gif" width="7" height="7" border="0" alt="">
														<a href="javascript:envPage.openBrForFormAct('uploadviewer.jsp','newViewer','width=397,height=160')" class="envlist"> <rqfmt:message strkey='environment.rqenv.upviewerfile'/></a>
													</td>
												</tr>
												<tr>
													<td style="padding-left:12px;">
														<span class="envlistdetail">
															<rqfmt:message strkey='environment.rqenv.upviewerfile.desc1'/><br>
															<rqfmt:message strkey='environment.rqenv.upviewerfile.desc2'/>
														</span>
													</td>
												</tr>
												</table>
												</form>
											</td>
										</tr>
										<tr>
											<td height="12" colspan="2"></td>
										</tr>
										<tr>
											<td><a href="javascript:envPage.viewerInstall();" class="envlist"><img src="../img/main_m_img3.gif" hspace="14" border="0"></a></td>
											<td valign="top">
												<form name="frm1">
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td valign="top" height="20"><img src="../img/db_m_bullet.gif" width="7" height="7" border="0" alt=""><a href="javascript:envPage.viewerInstall();" class="envlist"> <rqfmt:message strkey='environment.rqenv.viewerinstall'/></a></td>
												</tr>
												<tr>
													<td style="padding-left:12px;">
														<span class="envlistdetail"><rqfmt:message strkey='environment.rqenv.viewerinstall.desc1'/></span>
													</td>
												</tr>
												</table>
												</form>
											</td>
										</tr>
										<tr>
											<td height="10" colspan="2"></td>
										</tr>
										<tr>
											<td><a href="javascript:envPage.rollback();" class="envlist"><img src="../img/main_m_img5.gif" hspace="14" border="0"></a></td>
											<td valign="top">
												<form name="frm2" action="../common/uploadPrc.jsp" method="post">
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td valign="top" height="20"><img src="../img/db_m_bullet.gif" width="7" height="7" border="0" alt="">
														<a href="javascript:envPage.rollback();" class="envlist"><rqfmt:message strkey='environment.rqenv.viewerrollback'/></a>
														<input name="strRbfileName" type="hidden" value="<%=viewerFileName%>"/>
														<input name="mode" value="rollback" type="hidden"/>
													</td>
												</tr>
												<tr>
													<td style="padding-left:12px;">
														<span class="envlistdetail"><rqfmt:message strkey='environment.rqenv.viewerrollback.desc1'/></span>
													</td>
												</tr>
												</table>
												</form>
											</td>
										</tr>
										<tr>
											<td height="10" colspan="2"></td>
										</tr>
										<tr>
											<td><a href="javascript:envPage.crtTableFn();" class="envlist"><img src="../img/main_m_img4.gif" hspace="14" border="0"></a></td>
											<td valign="top">
												<form name="frm3" action="../common/handler.jsp" method="post">
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td valign="top" height="20"><img src="../img/db_m_bullet.gif" width="7" height="7" border="0" alt=""><a href="javascript:envPage.crtTableFn();" class="envlist"> <rqfmt:message strkey='environment.rqenv.createdatabase'/></a></td>
												</tr>
												<tr>
													<td style="padding-left:12px;">
														<span class="envlistdetail"><rqfmt:message strkey='environment.rqenv.createdatabase.desc1'/></span>
													</td>
												</tr>
												</table>
												<input type="hidden" name="modetype" value="crttable"/>
												</form>
											</td>
										</tr>
										<tr>
											<td height="10" colspan="2"></td>
										</tr>
									<%
										if(isUse_statistics.equals("true")){
									%>
										<tr>
											<td><a href="javascript:envPage.crtDocStattable();" class="envlist"><img src="../img/main_m_img6.gif" hspace="14" border="0"></a></td>
											<td valign="top">
												<form name="frm4" action="../common/handler.jsp" method="post">
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td valign="top" height="20"><img src="../img/db_m_bullet.gif" width="7" height="7" border="0" alt=""><a href="javascript:envPage.crtDocStattable();" class="envlist"> <rqfmt:message strkey='environment.rqenv.createstattable'/></a></td>
												</tr>
												<tr>
													<td style="padding-left:12px;">
														<span class="envlistdetail"><rqfmt:message strkey='environment.rqenv.createstattable.desc1'/></span>
													</td>
												</tr>
												</table>
												<input type="hidden" name="mode" value="crtDocStattable"/>
												</form>
											</td>
										</tr>
									<%
										}
									%>
										<tr>
											<td height="8" colspan="2"></td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>
							<td valign="top" width="14" background="../img/con_middle_right.gif"></td>
						</tr>
						<tr>
							<td height="14" background="../img/con_bottom_left.gif"></td>
							<td height="14" background="../img/con_bottom_bg.gif"></td>
							<td height="14" background="../img/con_bottom_right.gif"></td>
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
</table><br>
</body>
</html>
