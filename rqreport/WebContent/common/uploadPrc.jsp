<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/tld/RQHandle.tld" prefix="rqhandle" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%// this page use for upload // new_document.jsp, environment.jsp, uploadviewer.jsp %>
<rquser:rqUser action="checksession">
	<rqhandle:rqHandler action="upLoadPrc"/>
</rquser:rqUser>