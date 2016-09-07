<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.scheduler.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<%
Environment g_env = Environment.getInstance();
String mode = "";
ScheduleInfo si = null;
String schedule = "";
if(request.getParameter("mode") != null){
	mode = request.getParameter("mode").trim();
	if(mode.equals("modifyTime")){
		schedule = request.getParameter("chkscheduleID").trim(); // only one
		ArrayList arr = new ArrayList();
		new RQScheduleAPI().getSchedulingList(Integer.parseInt(request.getParameter("chkscheduleID")),arr);
		//out.println(arr.size());
		Iterator it = arr.iterator();
		if(it.hasNext()){
			si = (ScheduleInfo) it.next();
		}
	}
}
//out.println(mode);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>REQUBE REPORT</title>
<link href="../css/common.css" rel="stylesheet" type="text/css">
<link href="../css/ui.all.css" rel="stylesheet" type="text/css"/>
<style>body {font-size: 63%; font-family:"Dotum", "Tahoma";}</style>
<script language="javascript" src="../setup/<%=g_env.jsframework%>"></script>
<script language="javascript" src="../setup/ui.datepicker.js"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="javascript" src="../setup/calendar.js"></script>
<script language="javascript">
var lm_ampm = "am";
function showToday(){
	document.getElementById("mode").value = "<%=mode%>";
	document.getElementById("schedule").value = "<%=schedule%>"; // have value!! ... in case "modify"
	var todayDate = new Date();
<%
	if(mode.equals("modifyTime") && request.getParameter("mode") != null){
		String FullYear = "";
		String Month = "";
		String Date = "";
		String _hour = "";
		String lm_min = "";
		String lm_sec = "";
		
		String[] strDate_Time = si.getStrStartTime().split(" ");
		if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
			String strDate = strDate_Time[0];
			String[] t_Date = strDate.split("-");
			FullYear = t_Date[0];
			Month = t_Date[1];
			Date = t_Date[2];

			String strTime = strDate_Time[1];
			String[] t_Date2 = strTime.split(":");
			_hour = t_Date2[0];
			lm_min = t_Date2[1];
			lm_sec = t_Date2[2];
			if(lm_sec.startsWith("0")){
				lm_sec = lm_sec.substring(1,lm_sec.length());	
			}
		}else{
			String strDate = strDate_Time[0];
			String[] t_Date = strDate.split("/");
			FullYear = t_Date[0];
			Month = t_Date[1];
			Date = t_Date[2];
			
			String strTime = strDate_Time[1];
			String[] t_Date2 = strTime.split(":");
			_hour = t_Date2[0];
			lm_min = t_Date2[1];
			lm_sec = t_Date2[2];
			if(lm_sec.startsWith("0")){
				lm_sec = lm_sec.substring(1,lm_sec.length());	
			}
		}
%>
	var t_date = "<%=FullYear%>" + "." + "<%=Month%>" + "." + "<%=Date%>";
	var _hour  = <%=_hour%>;
    var lm_min = <%=lm_min%>;
	var lm_sec = <%=lm_sec%>;
<%

	}else{
%>
	var lm_month = todayDate.getMonth()+1;
	var lms_month = "" + lm_month;

	if(lms_month.length <= 1){
		lms_month = "0" + lms_month;
	}

	var t_date = todayDate.getFullYear() + "." + lms_month + "." + todayDate.getDate();
	var _hour = todayDate.getHours();
    var lm_min = todayDate.getMinutes();
	var lm_sec =  todayDate.getSeconds();
<%
	}
%>
	if(_hour > 11){
		if(_hour > 12){
			lm_hour = eval(_hour-12);
		}else if(_hour == 12){
			lm_hour = eval(_hour);
		}
		lm_ampm = "pm";
	}else{
		lm_hour = _hour;
	}
	var lm_startDate = document.getElementById("lm_startDate");
	var ampm = document.getElementById("ampm");
	var hour = document.getElementById("hour");
	var min = document.getElementById("min");
	var sec = document.getElementById("sec");
	lm_startDate.value = t_date;

	document.getElementById("periodOfWeek_"+todayDate.getDay()).checked = "checked";

	ampm.value = lm_ampm;
	hour.value = lm_hour;
	min.value = lm_min;
	sec.value = lm_sec;
<%
	if(mode.equals("modifyTime") && request.getParameter("mode") != null){
%>
	var perday = document.getElementById("perday");
	var perweek = document.getElementById("perweek");
	var month = document.getElementById("permonth");

	switch(<%=si.repeatType%>){
		case 1 :
			document.getElementById("repeatType_1").checked = "checked";
			document.getElementById("periodOfDay").value = "<%=si.periodOfDay%>";
			perday.style.visibility = "visible";
			perweek.style.visibility = "hidden";
			month.style.visibility = "hidden";
			//resetfn();
			break;
		case 2 :
			document.getElementById("repeatType_2").checked = "checked";
			document.getElementById("periodOfWeek").value = "<%=si.periodOfWeek%>";
			periodOfWeek_nullsetting();

			var periodOfWeek_0 = document.getElementById("periodOfWeek_0");
			var periodOfWeek_1 = document.getElementById("periodOfWeek_1");
			var periodOfWeek_2 = document.getElementById("periodOfWeek_2");
			var periodOfWeek_3 = document.getElementById("periodOfWeek_3");
			var periodOfWeek_4 = document.getElementById("periodOfWeek_4");
			var periodOfWeek_5 = document.getElementById("periodOfWeek_5");
			var periodOfWeek_6 = document.getElementById("periodOfWeek_6");
			var yoilele = [periodOfWeek_6,periodOfWeek_5,periodOfWeek_4,periodOfWeek_3,periodOfWeek_2,periodOfWeek_1,periodOfWeek_0];
			for(var i = 6; i >= 0 ; i--){
				if(Math.pow(2, i) & <%=si.dayOfWeek%>){
					yoilele[i].checked = "checked";
				}
			}

			perday.style.visibility = "hidden";
			perweek.style.visibility = "visible";
			month.style.visibility = "hidden";
			//resetfn();
			break;
		case 3 :
			document.getElementById("repeatType_3").checked = "checked";
			document.getElementById("periodOfMonth").checked = "checked";

			perday.style.visibility = "hidden";
			perweek.style.visibility = "hidden";
			month.style.visibility = "visible";
			//resetfn();
			break;
	}
<%
	}
%>

}

function del_period(para){
	for (i = 0; i < para.length;)
    	if (para.substring(i, i + 1) == '.' )
	    	para = para.substring(0, i) +
	        para.substring(i + 1, para.length);
	 	else
	    	i++;
    return para;
}

function open_cal(in_date,out_date,tag){
	out_date.value = in_date.value;
	switch(tag){
	    case 0 :
	         show_cal(out_date,'','YYYY.MM.DD',1);
	        break;
	    case 1 :
	         show_cal(out_date,out_date,'YYYY.MM.DD',1);
	        break;
	    case 2 :
	        out_date.value = del_period(out_date.value);
	         show_cal(out_date,out_date,'YYYY.MM.DD',1);
	        break;
    }
	out_date.value = '';
}

function showCalendar(obj){
	open_cal(obj, obj, 2);
	beforeNowVal = obj.value;
	timecall = setInterval('checkingCal()',1000);
}

beforeNowVal = "";
function checkingCal(){
	formx = document.staform;
	if(formx.now.value != beforeNowVal){
		formx.SEARCH_MODE.value = "TODAY";
	}
}

function showmenu(object){
	if(object.checked){
		showmenu_switch(eval(object.value));
	}
}

function showmenu_switch(value){
	var perday = document.getElementById("perday");
	var perweek = document.getElementById("perweek");
	var month = document.getElementById("permonth");

	switch(value){
		case 1 :
			perday.style.visibility = "visible";
			perweek.style.visibility = "hidden";
			month.style.visibility = "hidden";
			resetfn();
			break;
		case 2 :
			perday.style.visibility = "hidden";
			perweek.style.visibility = "visible";
			month.style.visibility = "hidden";
			resetfn();
			break;
		case 3 :
			perday.style.visibility = "hidden";
			perweek.style.visibility = "hidden";
			month.style.visibility = "visible";
			resetfn();
			break;
	}
}

function periodOfWeek_nullsetting(){
	for(i = 0 ; i < 7 ; i++){
		document.getElementById("periodOfWeek_"+i).checked = null;
	}
}

function resetfn(){
	document.getElementById("periodOfDay").value = "1";
	document.getElementById("periodOfWeek").value = "1";
	document.getElementById("periodOfMonth").checked = null;
	periodOfWeek_nullsetting();

	var todayDate = new Date();
	document.getElementById("periodOfWeek_"+todayDate.getDay()).checked = "checked";
}

function submitfn(){
	document.staform.action = "rqschedulehandle.jsp";
	document.staform.method = "post";
	var lm_startDate = document.getElementById("lm_startDate");
	var startDate = document.getElementById("startDate");
	var startTime = document.getElementById("startTime");

	var str_lm_startDate = lm_startDate.value;
	var rep_str_lm_startDate = replaceAll(str_lm_startDate, ".", ":");

	var hour = document.getElementById("hour");
	var min = document.getElementById("min");
	var sec = document.getElementById("sec");

	if(lm_ampm == "pm"){
		lm_hour = eval(hour.value) + eval(12);
		startDate.value = rep_str_lm_startDate + ":" + lm_hour + ":" + min.value + ":" + sec.value;
	}else{
		startDate.value = rep_str_lm_startDate + ":" + hour.value + ":" + min.value + ":" + sec.value;
	}

	var periodOfWeek_0 = document.getElementById("periodOfWeek_0");
	var periodOfWeek_1 = document.getElementById("periodOfWeek_1");
	var periodOfWeek_2 = document.getElementById("periodOfWeek_2");
	var periodOfWeek_3 = document.getElementById("periodOfWeek_3");
	var periodOfWeek_4 = document.getElementById("periodOfWeek_4");
	var periodOfWeek_5 = document.getElementById("periodOfWeek_5");
	var periodOfWeek_6 = document.getElementById("periodOfWeek_6");
	//
	var dayOfWeekValue = 0;
	if(periodOfWeek_0.checked) dayOfWeekValue += Math.pow(2, 6);
	if(periodOfWeek_1.checked) dayOfWeekValue += Math.pow(2, 5);
	if(periodOfWeek_2.checked) dayOfWeekValue += Math.pow(2, 4);
	if(periodOfWeek_3.checked) dayOfWeekValue += Math.pow(2, 3);
	if(periodOfWeek_4.checked) dayOfWeekValue += Math.pow(2, 2);
	if(periodOfWeek_5.checked) dayOfWeekValue += Math.pow(2, 1);
	if(periodOfWeek_6.checked) dayOfWeekValue += Math.pow(2, 0);
	//alert(dayOfWeekValue);
	var dayOfWeek = document.getElementById("dayOfWeek");
	dayOfWeek.value = dayOfWeekValue;

	document.staform.submit();
}

function replaceAll(strOriginal, strFind, strChange){
	return strOriginal.split(strFind).join(strChange);
}

$(function(){
	showToday();
	gfunc = new rqglobalfunc.rqglobal(); 
	$("#lm_startDate").css({
		"font-family" : "Verdana",
		"font-size" : "10pt",
		"color" : "#666666",
		"border" : "1px solid #a0d1d6",
		"padding-left" : "5px",
		"padding-right" : "10px",
		"height" : "20px" 
	});

	if(document.getElementById("schedule").value == ""){
		$("#repeatType_1").attr("checked","checked");
		var perday = document.getElementById("perday");
		perday.style.visibility = "visible";
	}
	
	if( browserName == "Internet Explorer" && browserVersion == "6.0"){
		$("#lm_startDate").click(function(){
			showCalendar(document.getElementById("staform").lm_startDate);
		});
	}else{
		$.datepicker.setDefaults({
	    	dateFormat: 'yy.mm.dd'
		});
		$("#lm_startDate").datepicker({
			defaultDate: new Date(2009, 1 - 1, 30),
	        showOn: "focus", // focus, button, both
	        showAnim: "show", // show, fadeIn, slideDown
	        duration: 200
		});
	}
});

</script>
<script for="calendar" event="onscriptletevent(id, view)">
	set_cal(id,view);
    try{
    	pst_set_date();
   	}catch(e){}
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form name="staform" id="staform" method="post">
<input type="hidden" name="now" id="now" value="" >
<table width="100%" border="0" cellspacing="0" cellpadding="0" bordercolor="black">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b">
				<%
					if(mode.equals("modifyTime")){
				%>
					<rqfmt:message strkey='schedule.rqscheduletime.schedulemodify'/>
				<%
					}else{
				%>
					<rqfmt:message strkey='schedule.rqscheduletime.addschedule'/>
				<%
					}
				%>
					</td>
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
				<table width="100%"  border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td colspan="2">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">1.</span> <rqfmt:message strkey='schedule.rqscheduletime.executedaytime'/> <% 	if(mode.equals("")){ out.println("<rqfmt:message strkey='schedule.rqscheduletime.regis'/>"); }else{ out.println("<rqfmt:message strkey='schedule.rqscheduletime.modify'/>"); } %></td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><img src="../img/bullet_title_01.gif" border="0"/><rqfmt:message strkey='schedule.rqscheduletime.day'/></td>
					<td>
						<table cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td style="width:10px;background-color: white;"></td>
							<td>
								<input type="text" name="lm_startDate" id="lm_startDate" value="" />
							</td>
							<td style="width:3px;background-color: white;"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><img src="../img/bullet_title_01.gif" border="0"/><rqfmt:message strkey='schedule.rqscheduletime.time'/></td>
					<td>
						<table border="0" bordercolor="blue">
						<tr>
							<td style="width:3px;background-color: white;"></td>
							<td>
								<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td><select name="ampm" id="ampm" size="1">
										 	<option value="am"><rqfmt:message strkey='schedule.rqscheduletime.am'/></option>
										 	<option value="pm"><rqfmt:message strkey='schedule.rqscheduletime.pm'/></option>
										 </select></td>
									<td width="3"></td>
									<td>
										<select name="hour" id="hour" size="1">
									<%
										for(int i = 0 ; i <= 12; i++){
									%>
										<option value="<%=i%>"><%=i%></option>
									<%
										}
									%>
										 </select>
									</td>
									<td style='font-size:9pt; font-family:"dotum","Verdana"; text-decoration:none;'><rqfmt:message strkey='schedule.rqscheduletime.hour'/></td>
									<td width="3"></td>
									<td>
										<select name="min" id="min" size="1">
									<%
										for(int i = 0 ; i <= 59; i++){
									%>
										<option value="<%=i%>"><%=i%></option>
									<%
										}
									%>
										 </select>
									</td>
									<td style='font-size:9pt; font-family:"dotum","Verdana"; text-decoration:none;'><rqfmt:message strkey='schedule.rqscheduletime.min'/></td>
									<td width="3"></td>
									<td>
										<select name="sec" id="sec" size="1">
									<%
										for(int i = 0 ; i <= 59; i++){
									%>
										<option value="<%=i%>"><%=i%></option>
									<%
										}
									%>
										 </select>
										 <input type="hidden" name="startDate" id="startDate"/>
									</td>
									<td style='font-size:9pt; font-family:"dotum","Verdana"; text-decoration:none;'><rqfmt:message strkey='schedule.rqscheduletime.sec'/></td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				</table>
			</td>
			<td width="7"></td>
		</tr>
		</table>
	</td>
</tr>

<tr>
	<td align="center"  bgcolor="#FFFFFF">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="7"></td>
			<td>
				<table width="100%"  border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td colspan="2">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">2.</span> <rqfmt:message strkey='schedule.rqscheduletime.repeatexecute'/> <% 	if(mode.equals("")){ out.println("<rqfmt:message strkey='schedule.rqscheduletime.regis'/>"); }else{ out.println("<rqfmt:message strkey='schedule.rqscheduletime.modify'/>"); } %></td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="background-color:#F5F5F5;">
						<table border="0" bordercolor="blue" class="text" style="background-color:#F5F5F5;" width="100%">
						<tr>
							<td align="right"><input type="radio" id="repeatType_1" value="1" name="repeatType" onclick="showmenu(this);"></td><td align="left"><rqfmt:message strkey='schedule.rqscheduletime.perday'/><input type="radio" value="0" checked="checked" name="repeatType" id="repeatType" style="visibility:hidden;width:0;height:0;"></td>
						</tr>
						<tr>
							<td align="right"><input type="radio" id="repeatType_2" value="2" name="repeatType" onclick="showmenu(this);"></td><td align="left"><rqfmt:message strkey='schedule.rqscheduletime.perweek'/></td>
						</tr>
						<tr>
							<td align="right"><input type="radio" id="repeatType_3" value="3" name="repeatType" onclick="showmenu(this);"></td><td align="left"><rqfmt:message strkey='schedule.rqscheduletime.permonth'/></td>
						</tr>
						</table>
					</td>
					<td height="100" width="280">
						<table cellspacing="1" cellpadding="3" border="1" height="100%" width="100%" >
						<tr>
							<td>
								<div id="perday" style="visibility: hidden; position: absolute; top:210px;left:121px;width:263px;height:80px;">
								<table class="text" border="0">
								<tr valign="top">
									<td><rqfmt:message strkey='schedule.rqscheduletime.perdayexe'/></td>
									<td><input type="hidden" id="periodOfDay" name="periodOfDay" size="4" value="1"/></td>
									<td></td>
								</tr>
								</table>
								</div>

								<div id="perweek" style="visibility: hidden; position: absolute; top:190px;left:112px;width:268px;height:80px;">
								<table class="text" border="0" width="100%" height="100%">
								<tr>
									<td align="left" style="padding-left:10px;"><rqfmt:message strkey='schedule.rqscheduletime.perweekspecialdayexe'/></td>
									<td align="center"><input type="hidden" size="4" name="periodOfWeek" id="periodOfWeek" value="1"/></td>
									<td align="left"></td>
								</tr>
								<tr>
									<td colspan="3" align="left">
										<table class="text" border="0" width="180">
										<tr>
											<td><input type="checkbox" name="periodOfWeek_0" id="periodOfWeek_0"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.sun'/></td>
											<td><input type="checkbox" name="periodOfWeek_1" id="periodOfWeek_1"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.mon'/></td>
											<td><input type="checkbox" name="periodOfWeek_2" id="periodOfWeek_2"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.tue'/></td>
											<td><input type="checkbox" name="periodOfWeek_3" id="periodOfWeek_3"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.wed'/></td>
										</tr>
										<tr>
											<td><input type="checkbox" name="periodOfWeek_4" id="periodOfWeek_4"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.thu'/></td>
											<td><input type="checkbox" name="periodOfWeek_5" id="periodOfWeek_5"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.fri'/></td>
											<td><input type="checkbox" name="periodOfWeek_6" id="periodOfWeek_6"/></td>
											<td><rqfmt:message strkey='schedule.rqscheduletime.sat'/></td>
										</tr>
										</table>
										<input type="hidden" name="dayOfWeek" id="dayOfWeek"/>
									</td>
								</tr>
								</table>
								</div>

								<div id="permonth" style="visibility: hidden; position: absolute; top:210px;left:121px;width:263px;height:80px;">
								<table class="text">
								<tr>
									<td><rqfmt:message strkey='schedule.rqscheduletime.permonthexe'/></td>
									<td><input type="checkbox" id="periodOfMonth" name="periodOfMonth" value="" style="visibility: hidden;"/></td>
									<td></td>
								</tr>
								</table>
								</div>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:7px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2">
						<table cellpadding="0" cellspacing="0" border="0" align="center">
						<tr>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="submitfn();" class="btn_green">
								<%
									if(mode.equals("modifyTime")){
								%>
									<rqfmt:message strkey='schedule.rqscheduletime.modify'/>
								<%
									}else{
								%>
									<rqfmt:message strkey='schedule.rqscheduletime.regis'/>
								<%
									}
								%>
									</a></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
							<td width="7px"></td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="self.close();" class="btn_green"><rqfmt:message strkey='schedule.rqscheduletime.cancel'/></a></td>
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
			<td width="7"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<input type="hidden" id="schedule" name="schedule"/>
<input type="hidden" id="mode" name="mode"/>
</form>
<object id='calendar' type='text/x-scriptlet' data='calendar.jsp' style='position:absolute;display:none'></object>
</body>
</html>