<%@ page language="java" contentType="text/html;charset=UTF-8" %><%@ page import="org.apache.log4j.*" %>
<%@ taglib uri="/WEB-INF/tld/RQComm.tld" prefix="rqcomm" %>
<rqcomm:reqEncode /><%
	Logger L = Logger.getLogger("RQDATASET");
	L.debug("-- start to get dataset");
 %>
<jsp:useBean id="actionModel" class="com.sds.rqreport.model.RQActionModel"/>
<jsp:setProperty name="actionModel" property="*"/>

<rqcomm:rqComm 
action="<%= actionModel.getAction() %>" 
driver="<%=actionModel.getDriver()%>" 
conn="<%=actionModel.getConn()%>" 
sid="<%=actionModel.getSid()%>" 
spass="<%=actionModel.getSpass()%>" 
sql="<%=actionModel.getSql()%>" 
strXml="<%=actionModel.getStrXml()%>"
stmtidx="<%=actionModel.getStmtidx()%>"
strKey="<%=actionModel.getStrKey()%>"
strDBInfo="<%=actionModel.getStrDBInfo()%>"
encqry="<%=actionModel.getEncqry()%>"
doc="<%=actionModel.getDoc()%>"
runvar="<%=actionModel.getRunvar()%>"
dbidx="<%=actionModel.getDbidx()%>"/><%
	L.debug("-- end to get dataset");
%>