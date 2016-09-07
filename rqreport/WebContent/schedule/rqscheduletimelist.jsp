<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.scheduler.ScheduleInfo" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQSchedulehandle.tld" prefix="schedulehandle" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<schedulehandle:rqHandler action="schedulTimeList"/>
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
rqschedule = {}
rqschedule.scheduletimelist = function(){
	var browsername = navigator.appName;
	//alert(bname);
	var IE = 'Microsoft Internet Explorer';
	if (browsername==IE) {
		isIE = 1;
	}else {
		isIE = 0;
	}
}
rqschedule.scheduletimelist.prototype = {
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
	openScheduleTime : function(){
		document.getElementById("mode").value = "insertTime";
		windowobj = window.open("","_openScheduleTime","width=392,height=328");
		windowobj.focus();
		document.frm.target = "_openScheduleTime";
		document.frm.method = "post";
		document.frm.action = "rqscheduletime.jsp";
		document.frm.submit();
		document.frm.target = '_self';
		document.frm.action = 'rqscheduletimelist.jsp';
	},
	modifyScheduleTime : function(){
		if(this.check_chkscheduleID() == false){
			this.deselectAll();
			return;
		}
		document.getElementById("mode").value = "modifyTime";
		modifyobj = window.open("","_openScheduleTime","width=392,height=328");
		modifyobj.focus();
		document.frm.target = "_openScheduleTime";
		document.frm.method = "post";
		document.frm.action = "rqscheduletime.jsp";
		document.frm.submit();
		document.frm.target = '_self';
		document.frm.action = 'rqscheduletimelist.jsp';
	},
	check_chkscheduleID : function(){
		var cnt = 0;
		forms = document.frm;
		for(i=0;i<forms.elements.length;i++){
			if (forms.elements[i].name=='chkscheduleID' && forms.elements[i].checked == true) {
				++cnt;
			}
		}
		if(cnt > 1){
			alert("<rqfmt:message strkey='schedule.rqscheduletimelist.alert.modifyoneby'/>");
			return false;
		}else if(cnt == 0){
			alert("<rqfmt:message strkey='schedule.rqscheduletimelist.alert.noseldoc'/>");
			return false;
		}
	},
	showscheduleruninfo : function(scheduleid){
		document.frm.action = "rqschedule_runinfolist.jsp";
		document.frm.method = "post";
		document.frm.scheduleidx.value = scheduleid;
		//alert(document.frm.scheduleidx.value);
		document.frm.submit();
	},
	deselectAll : function(){
		forms = document.frm;
		for( i=0; i < forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkalldoc') {
				forms.elements[i].checked = false;
			}
			if (forms.elements[i].name=='chkscheduleID') {
				forms.elements[i].checked = false;
			}
		}
	},
	reloadpage : function(){
		document.location.reload();
	},
	deleteScheduleTime : function(){
		flag = false;
		forms = document.frm;
		for( i=0 ; i < forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkscheduleID') {
				if (forms.elements[i].checked == true) {
					flag = true;
					break;
				}
			}
		}
		if (flag == false) {
			alert("<rqfmt:message strkey='schedule.rqscheduletimelist.alert.selecttarget'/>");
			return;
		}
		document.frm.action = "rqschedulehandle.jsp?mode=deleteTime";
		document.frm.method = "post";
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
			if (forms.elements[i].name=='chkscheduleID') {
				if (forms.elements[i].checked == false) {
					flag = false;
					break;
				}
			}
		}
	
		if (flag == false) {
			for( i=0 ; i<forms.elements.length ; i++) {
				if (forms.elements[i].name=='chkscheduleID') {
					forms.elements[i].checked = true;
					this.TrColor(forms.elements[i]);
				}
			}
		}
		else {
			for( i=0; i<forms.elements.length; i++) {
				if (forms.elements[i].name=='chkscheduleID') {
					forms.elements[i].checked = false;
					this.TrColor(forms.elements[i]);
				}
			}
		}
	}
}

window.onload = function(){
	scheduletimelistPage = new rqschedule.scheduletimelist();
}


//document.onfocusin=bluring;
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
				<td><a href="javascript:scheduletimelistPage.logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
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
					<td height="56" class="main_conbgtop2" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text"/><rqfmt:message strkey='schedule.rqscheduletimelist.title'/>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;">
						<table width="730"  border="0" cellpadding="0" cellspacing="0">
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
													<td height="2" colspan="4" bgcolor="#A8CC72"></td>
												</tr>
												<tr class="table_title">
													<td class="text" width="10"><input type="checkbox" name="chkalldoc" id="chkalldoc" value="checkbox" onClick="scheduletimelistPage.selectchkalldoc()" onfocus="this.blur()"></td>
													<td class="table_title" height="26" width="80"><rqfmt:message strkey='schedule.rqscheduletimelist.scheduleid'/></td>
													<td class="table_title" height="26" width="190"><rqfmt:message strkey='schedule.rqscheduletimelist.starttime'/></td>
													<td class="table_title" height="26" width="340"><rqfmt:message strkey='schedule.rqscheduletimelist.scheduler'/></td>
												</tr>
												<tr>
													<td height="1" colspan="4" bgcolor="#A8CC72" ></td>
												</tr>
											<%
												ArrayList oTimeList = (ArrayList)pageContext.getAttribute("schedultimeList");
												//out.println(oTimeList.size());

												Iterator it_oTimeList = oTimeList.iterator();
												ScheduleInfo lm_ScheduleInfo = null;
												int lm_iColor = 0;
												while(it_oTimeList.hasNext()){
													 lm_ScheduleInfo = (ScheduleInfo) it_oTimeList.next();
											%>
												<tr bgcolor="<%if(lm_iColor%2 == 0){%>#FFFFFF<%}else{%>#F9F9F9<% }%>">
													<td class="text">
														<input type="checkbox" name="chkscheduleID" value="<%=lm_ScheduleInfo.scheduleID%>" onClick="scheduletimelistPage.TrColor(this)" onfocus="this.blur()">
													</td>
													<td class="text" valign="middle" style="font-family:verdana;cursor:pointer;" onClick="scheduletimelistPage.showscheduleruninfo('<%=lm_ScheduleInfo.scheduleID%>');">
														<schedulehandle:rqHandler action="getScheduleIDFromScheduleInfo" scheduleInfo="<%=lm_ScheduleInfo%>"/>
													</td>
													<td class="text" valign="middle" style="font-family:verdana;cursor:pointer;" onClick="scheduletimelistPage.showscheduleruninfo('<%=lm_ScheduleInfo.scheduleID%>');">
														<schedulehandle:rqHandler action="getStartTimeFromScheduleInfo" scheduleInfo="<%=lm_ScheduleInfo%>"/>
													</td>
													<td class="text" valign="middle" style="font-family:verdana;cursor:pointer;word-break:break-all;text-align:left;" onClick="scheduletimelistPage.showscheduleruninfo('<%=lm_ScheduleInfo.scheduleID%>');">
														<schedulehandle:rqHandler action="getDayOfWeekFromScheduleInfo" scheduleInfo="<%=lm_ScheduleInfo%>"/>
													</td>
												</tr>
												<tr>
													<td height="1" colspan="4" bgcolor="#E6E6E6"></td>
												</tr>
											<%
													++lm_iColor;
												}
											%>
												<tr>
													<td height="1" colspan="4" bgcolor="#A8CC72"></td>
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
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="scheduletimelistPage.deleteScheduleTime();"><rqfmt:message strkey='schedule.rqscheduletimelist.button.delete'/></a></td>
															<td><img src="../img/btn_g_right.gif"></td>
														</tr>
														</table>
													</td>
													<td align="left">
														<table border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td><img src="../img/btn_g_left2.gif"></td>
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="scheduletimelistPage.modifyScheduleTime();"><rqfmt:message strkey='schedule.rqscheduletimelist.button.modify'/></a></td>
															<td><img src="../img/btn_g_right.gif"></td>
														</tr>
														</table>
													</td>
													<td align="right">
														<table border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td><img src="../img/btn_g_left2.gif"></td>
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="scheduletimelistPage.openScheduleTime();"><rqfmt:message strkey='schedule.rqscheduletimelist.button.addsche'/></a></td>
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
</table>
<input type="hidden" name="scheduleidx" id="scheduleidx"/>
<input type="hidden" name="mode" id="mode"/>
</form><br>
</body>
</html>

