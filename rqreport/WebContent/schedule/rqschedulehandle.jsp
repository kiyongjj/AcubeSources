<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/RQSchedulehandle.tld" prefix="schedulehandle" %>
<%
String mode = request.getParameter("mode");
if(mode != null && !mode.equals("")){
	if(mode.equals("insertRQV")){
	%>
		<jsp:useBean id="RQSchedulerListModel" class="com.sds.rqreport.model.RQSchedulerListModel"/>
		<jsp:setProperty name="RQSchedulerListModel" property="*"/>
		<schedulehandle:rqHandler action="createSchedule" listModel="<%=RQSchedulerListModel%>"/>
	<%
	}else if(mode.equals("insertTime")){
	%>
		<jsp:useBean id="RQScheduleTimeModel" class="com.sds.rqreport.model.RQScheduleTimeModel"/>
		<jsp:setProperty name="RQScheduleTimeModel" property="*"/>
		<schedulehandle:rqHandler action="scheduleTime" listModel="<%=RQScheduleTimeModel%>"/>
	<%
	}else if(mode.equals("modifyTime")){
	%>
		<jsp:useBean id="RQScheduleTimeModel_modi" class="com.sds.rqreport.model.RQScheduleTimeModel"/>
		<jsp:setProperty name="RQScheduleTimeModel_modi" property="*"/>
		<schedulehandle:rqHandler action="modifyscheduleTime" listModel="<%=RQScheduleTimeModel_modi%>"/>		
	<%
	}else if(mode.equals("deleteTime")){
	%>
		<schedulehandle:rqHandler action="deleteScheduleTime"/>
	<%
	}else if(mode.equals("deleteRunInfo")){
	%>
		<schedulehandle:rqHandler action="deleteRunInfo"/>
	<%
	}
}
%>
