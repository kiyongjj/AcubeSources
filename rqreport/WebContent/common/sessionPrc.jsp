<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%// this page use for logout %>
<% String mode = request.getParameter("mode"); %>
<rquser:rqUser action="<%=mode%>"/>