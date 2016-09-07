<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/tld/RQHandle.tld" prefix="rqhandle" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<rquser:rqUser action="checksession">
<% String mode = request.getParameter("mode") != null ? request.getParameter("mode") : ""; %>
	<%
		if(mode.equals("rollbackfile")){
	%>
			<rqhandle:rqHandler action="rollbackfile"/>
	<%
		}else if(mode.equals("crtDocStattable")){
	%>
			<rqhandle:rqHandler action="crtDocStattable"/>
	<%
		}else{
	%>
			<rqhandle:rqHandler action="delNcrttable"/>
	<%
		}
	%>
</rquser:rqUser>