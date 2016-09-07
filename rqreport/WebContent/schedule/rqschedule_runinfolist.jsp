<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.model.RQSchedulerListModel" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQSchedulehandle.tld" prefix="schedulehandle" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<schedulehandle:rqHandler_runinfolist action="schedulRunInfoList" scheduleidx="<%=request.getParameter("scheduleidx")%>"/>
<%
Environment g_env = Environment.getInstance();
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
<script language="javascript" src="../setup/<%=g_env.jsframework%>"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="JavaScript" type="text/JavaScript">
rqschedule = {}
rqschedule.scheduleRunning = function(){
	var browsername = navigator.appName;
	var IE = 'Microsoft Internet Explorer';
	if (browsername==IE) {
		isIE = 1;
	}else {
		isIE = 0;
	}
}
rqschedule.scheduleRunning.prototype = {
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
	startFunc : function(){
		document.getElementById("scheduleidx").value = "<%=request.getParameter("scheduleidx")%>";
	},
	mouseOver :	function(element){
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
	openScheduleRunInfo : function(){
		//////////// browser compatibility ///////////////////////////////////////////////////////
		if(browserName != "Internet Explorer"){
			alert(browserName + " <rqfmt:message strkey='common.compatibility.incompatible'/>");
			return;
		}
		//////////////////////////////////////////////////////////////////////////////////////////
		windowobj = window.open("","_openScheduleRunInfo","width=475,height=474");
		windowobj.focus();
		document.frm.target = "_openScheduleRunInfo";
		document.frm.method = "post";
		document.frm.action = "rqschedule.jsp";
		document.frm.submit();
		document.frm.target = '_self';
		document.frm.action = 'rqschedule_runfinfolist.jsp';
	},
	reloadpage : function(){
		document.frm.method = "post";
		document.frm.action = "rqschedule_runinfolist.jsp";
		document.frm.submit();
	},
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
	selectchkalldoc : function() {
		forms = document.frm;
		flag = true;
		for( i=0; i<forms.elements.length; i++) {
			if (forms.elements[i].name=='chkRuninfoID') {
				if (forms.elements[i].checked == false) {
					flag = false;
					break;
				}
			}
		}
		if (flag == false) {
			for( i=0 ; i<forms.elements.length ; i++) {
				if (forms.elements[i].name=='chkRuninfoID') {
					forms.elements[i].checked = true;
					this.TrColor(forms.elements[i]);
				}
			}
		}
		else {
			for( i=0; i<forms.elements.length; i++) {
				if (forms.elements[i].name=='chkRuninfoID') {
					forms.elements[i].checked = false;
					this.TrColor(forms.elements[i]);
				}
			}
		}
	},
	deleteRunInfo : function(){
		flag = false;
		forms = document.frm;
		for( i=0 ; i < forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkRuninfoID') {
				if (forms.elements[i].checked == true) {
					flag = true;
					break;
				}
			}
		}
		if (flag == false) {
			alert("<rqfmt:message strkey='schedule.rqschedule_runinfolist.seltarget'/>");
			return;
		}
		document.frm.action = "rqschedulehandle.jsp?mode=deleteRunInfo";
		document.frm.method = "post";
		document.frm.submit();
	},
	runInfoStatus : function(runinfoid){
		var url = "rqscheduleruninfostatus.jsp?runinfoid=" + runinfoid;
		statusobj = window.open(url ,"statusobj1","width=301,height=310,scrollbars=yes");
		statusobj.focus();
		
	}
}
$(function(){
	//document.onfocusin=bluring;
	//window.onload=startFunc;
	gfunc = new rqglobalfunc.rqglobal(); 
	scheduleRunningPage = new rqschedule.scheduleRunning();
	scheduleRunningPage.startFunc();
});


</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="f8f8f8" class="main_bg">
<form name="frm">
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
				<td><a href="javascript:scheduleRunningPage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
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
						<tr>
							<td height="45" valign="top"><a href="../schedule/rqscheduletimelist.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.schedule.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="schedule"></a></td>
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
					<td height="50" class="main_conbgtop2" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text" >서버에 등록된 문서를 파악할 수 있습니다.</span>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;">
						<table width="730"  border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<table width="730" height="24" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="10" height="24"><IMG SRC="../img/main_folder_l.gif" width="10" height="24" border="0" alt=""></td>
									<td width="710" height="24" class="n_navi" style="color:#EACD32;"><rqfmt:message strkey='schedule.rqschedule_runinfolist.scheduleid'/> : <%=request.getParameter("scheduleidx")%></td>
									<td width="10" height="24"><IMG SRC="../img/main_folder_r.gif" width="10" height="24" border="0" alt=""></td>
								</tr>
								</table>
							</td>
						</tr>
						<tr><td height="10"></td></tr>
						<tr>
							<td>
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="2" colspan="8" bgcolor="#A8CC72"></td>
												</tr>
											<%
												ArrayList oRunInfoList = (ArrayList)pageContext.getAttribute("runInfoList");
												//out.println(oRunInfoList.size());
												Iterator it_runInfoList = oRunInfoList.iterator();
											%>
												<tr class="table_title">
													<td class="table_title" height="26"><input type="checkbox" name="chkalldoc" id="chkalldoc" value="checkbox" onClick="scheduleRunningPage.selectchkalldoc();" onfocus="this.blur()"></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.scheduleidcol'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.user'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.doc'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.runvar'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.rqx'/></td>
													<td class="table_title" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.mailing'/></td>
													<td class="table_title" width="80" height="26"><rqfmt:message strkey='schedule.rqschedule_runinfolist.status'/></td>
													
												</tr>
												<tr>
													<td height="1" colspan="8" bgcolor="#A8CC72" ></td>
												</tr>
											<%
												RQSchedulerListModel lm_oRQSchedulerListModel = null;
												int lm_iColor = 0;
												while(it_runInfoList.hasNext()){
													lm_oRQSchedulerListModel = (RQSchedulerListModel) it_runInfoList.next();
											%>
												<tr bgcolor="<%if(lm_iColor%2 == 0){%>#FFFFFF<%}else{%>#F9F9F9<% }%>">
													<td class="text" width="50" valign="middle">
														<input type="checkbox" name="chkRuninfoID" value="<%=lm_oRQSchedulerListModel.getRuninfoid()%>" onClick="scheduleRunningPage.TrColor(this)" onfocus="this.blur()">
													</td>
													<td class="text" width="80" valign="middle" style="font-family:verdana;word-break:break-all;">
														<%=lm_oRQSchedulerListModel.getScheduleid()%>
													</td>
													<td class="text" width="80" valign="middle" style="font-family:verdana;word-break:break-all;">
														<%=lm_oRQSchedulerListModel.getStrUserId()%>
													</td>
													<td class="text" width="200" valign="middle" style="font-family:verdana;word-break:break-all;">
														<%=lm_oRQSchedulerListModel.getDoc()%>
													</td>
													<td class="text" width="200" valign="middle" style="font-family:verdana;word-break:break-all;">
														<%=lm_oRQSchedulerListModel.getRunvards()%>
													</td>
													<td class="text" width="200" valign="middle" style="font-family:verdana;word-break:break-all;">
														<%=lm_oRQSchedulerListModel.getResultfileds()%>
													</td>
													<td class="text" width="200" valign="middle" style="font-family: verdana;">
														<%
															if(lm_oRQSchedulerListModel.getMailinglist() == null){
																out.println("");
															}else{
																out.println(lm_oRQSchedulerListModel.getMailinglist());
															}
														%>
													</td>
													<td align="center">
														<a href="javascript:scheduleRunningPage.runInfoStatus('<%=lm_oRQSchedulerListModel.getRuninfoid()%>');"><rqfmt:message strkey='schedule.rqschedule_runinfolist.view'/></a>
													</td>
												</tr>
												<tr>
													<td height="1" colspan="8" bgcolor="#E6E6E6"></td>
												</tr>
											<%
													++lm_iColor;
												}
											%>
												<tr>
													<td height="1" colspan="8" bgcolor="#A8CC72"></td>
												</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td height="15"></td>
										</tr>
										<tr>
											<td>
												<table border="0" width="100%">
												<tr>
													<td align="left" width="50">
														<table border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td><img src="../img/btn_g_left2.gif"></td>
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="scheduleRunningPage.deleteRunInfo();"><rqfmt:message strkey='schedule.rqschedule_runinfolist.delete'/></a></td>
															<td><img src="../img/btn_g_right.gif"></td>
														</tr>
														</table>
													</td>
													<td align="right">
														<table border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td><img src="../img/btn_g_left2.gif"></td>
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="scheduleRunningPage.openScheduleRunInfo();"><rqfmt:message strkey='schedule.rqschedule_runinfolist.rqxregis'/></a></td>
															<td><img src="../img/btn_g_right.gif"></td>
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
<input type="hidden" name="scheduleidx" id="scheduleidx"/>
</form>
</body>
</html>

