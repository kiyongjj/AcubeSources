<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<jsp:include flush="true" page="RQtest.jsp"/>
<rquser:rqLog action="convStr"/><br>
<input type="button" value="back" onclick="history.back();"/>