<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.sds.rqreport.util.*"%>
<%@page import="com.sds.rqreport.common.*"%>
<%@page import="com.sds.rqreport.scheduler.RQScheduleAPI"%>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%
String runinfoid = request.getParameter("runinfoid");
RQScheduleAPI scheapi = new RQScheduleAPI();
ArrayList arr = scheapi.getListStatus(runinfoid);
Iterator it = arr.iterator();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>REQUBE REPORT</title>
<link href="../css/common.css" rel="stylesheet" type="text/css">
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0" bordercolor="black">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='schedule.rqscheduleruninfostatus.executestatus'/></td>
				</tr>
				</table>
			</td>
			<td><img src="../img/popup_top_r.gif" width="137" height="50" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td height="3"></td>
</tr>
<tr>
	<td align="center"  bgcolor="#FFFFFF">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="7"></td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="60">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title">
								<rqfmt:message strkey='schedule.rqscheduleruninfostatus.docid'/>
							</td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
					<td width="2"></td>
					<td>
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title">
								<rqfmt:message strkey='schedule.rqscheduleruninfostatus.date'/>
							</td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
					<td width="2"></td>
					<td width="80">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title">
								<rqfmt:message strkey='schedule.rqscheduleruninfostatus.status'/>
							</td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="5" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="5" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="5" style="height:3px;background-color: white;"></td>
				</tr>
				</table>
			</td>
			<td width="7"></td>
		</tr>
		<tr>
			<td width="7"></td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<%
			while(it.hasNext()){
				RQScheduleRunInfoStatus lm_runinfost = (RQScheduleRunInfoStatus) it.next();
			%>
				<tr>
					<td width="60"><%=lm_runinfost.getRuninfo()%></td>
					<td><%=RequbeUtil.makeDataformat(lm_runinfost.getRundate())%></td>
					<td width="80"><%=lm_runinfost.getStatus()%></td>
				</tr>
				<tr>
					<td colspan="5" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="5" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="5" style="height:3px;background-color: white;"></td>
				</tr>
			<%
			}
			%>
				</table>
			</td>
			<td width="7"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>