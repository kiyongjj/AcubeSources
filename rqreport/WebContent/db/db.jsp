<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<%
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
<script language="JavaScript" type="text/JavaScript">
rqdb = {}
rqdb.dbManage = function(){}
rqdb.dbManage.prototype = {
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
}
window.onload = function(){
	//document.onfocusin=bluring;
	dbPage = new rqdb.dbManage();
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
		  	<td height="151"><IMG SRC="../img/main_bimg02.jpg" width="539" height="151" border="0" alt=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><a href="javascript:dbPage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
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
				<!-- 메뉴 -->
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
							<td height="45" valign="top"><a href="../db/db.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.db.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="data"></a></td>
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
				<!-- 메뉴 끝-->
			</td>
			<td width="787" valign="top" class="main_conbg">
				<!-- contents 시작 -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop2" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text" ><rqfmt:message strkey='db.db.title'/></span>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;">
						<table width="730"  border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr valign="bottom">
									<td height="10" align="right"></td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="2" colspan="5" bgcolor="#A8CC72"></td>
												</tr>
												<tr class="table_title">
													<td class="table_title" height="26"><rqfmt:message strkey='db.db.jndiname'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='db.db.userid'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='db.db.dbserverip'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='db.db.sid'/></td>
													<td class="table_title" height="26" align="left"><rqfmt:message strkey='db.db.databaseinfo'/></td>
												</tr>
												<tr>
													<td height="2" colspan="5" bgcolor="#A8CC72" ></td>
												</tr>
												<tr>
													<td height="1" colspan="5" bgcolor="#E6E6E6"></td>
												</tr>
											<%
												Environment environ = Environment.getInstance();
												int serverType = environ.serverType;

												if(serverType != 9){
													Hashtable env = null;
													String bindstr = "";
													DBUtil lm_oDBUtil = new DBUtil();
													List jndiList = lm_oDBUtil.getServerDsListwithEnv(env, bindstr);
													Iterator it = jndiList.iterator();
													String lm_jndiname = "";
													while(it.hasNext())	{
	 										%>
														<tr  bgcolor="#F9F9F9">
															<td class="text" valign="middle" style="font-family: verdana;">
														<%
															lm_jndiname = (String) it.next();
															out.println(lm_jndiname);
														%>
															</td>
															<td class="text" valign="middle" style="font-family: verdana;">
														<%
															lm_oDBUtil.setDatabaseMetaDataObj(lm_jndiname);
															out.println(	lm_oDBUtil.getUserName());
														%>
															</td>
															<td class="text" valign="middle" style="font-family: verdana;">
														<%=lm_oDBUtil.getJdbc_url_ip()%>
															</td>
															<td class="text" valign="middle" style="font-family: verdana;">
														<%=lm_oDBUtil.getJdbc_url_sid()%>
															</td>
															<td class="text" valign="middle" style="font-family:verdana;word-break:break-all;text-align:left;" width="390">
														<%=lm_oDBUtil.getJdbc_url()%>
															</td>
														</tr>
														<tr>
															<td height="1" colspan="5" bgcolor="#E6E6E6"></td>
														</tr>
														<tr>
															<td height="1" colspan="5" bgcolor="#FFFFFF"></td>
														</tr>
											<%
													}
												}else{

													ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
													Enumeration lm_oenm = lm_oRb.getKeys();
													String lm_tmp = "";
													String lm_strDataSource = "";
													String lm_tmp_datasource = "";

													while(lm_oenm.hasMoreElements()){
														lm_tmp = (String) lm_oenm.nextElement();
														int indx = lm_tmp.lastIndexOf(".");
														if(indx == -1) continue;
														lm_tmp_datasource = lm_tmp.substring(0, indx);

														if(lm_tmp_datasource.equals("rqreport.server.DataSource")){
															lm_strDataSource = lm_oRb.getString(lm_tmp);
															%>
															<tr  bgcolor="#F9F9F9">
																<td class="text" valign="middle" style="font-family: verdana;"><%=lm_strDataSource%></td>
																<td class="text" valign="middle" style="font-family: verdana;" width="120"></td>
																<td class="text" valign="middle" style="font-family: verdana;" width="120"></td>
																<td class="text" valign="middle" style="font-family: verdana;" width="120"></td>
																<td class="text" valign="middle" style="font-family:verdana;word-break:break-all;text-align:left;" width="390"></td>
															</tr>
															<tr>
																<td height="1" colspan="5" bgcolor="#E6E6E6"></td>
															</tr>
															<tr>
																<td height="1" colspan="5" bgcolor="#FFFFFF"></td>
															</tr>
															<%
														}
													}
												}
	 										%>
												<tr>
													<td height="1" colspan="5" bgcolor="#A8CC72"></td>
												</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td height="15"></td>
										</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="33"></td>
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
</table><br>
</body>
</html>
