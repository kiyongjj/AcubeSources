<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%
	String user_agt = request.getHeader("user-agent");
	String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();

	//String contextname = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+contextname+"/";

	String lm_doc = request.getParameter("doc");
	String doc = new String(lm_doc.getBytes(lm_serverCharset), lm_RQCharset);
	String lm_runvar = request.getParameter("runvar");
	String runvar = "";
	if(lm_runvar != null){
		runvar = new String(lm_runvar.getBytes(lm_serverCharset), lm_RQCharset);
	}else{
		runvar = new String();
	}
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<TITLE>REQUBE REPORT</TITLE>
<script language="javaScript">
var browserName = navigator.appName;
// get 
function callrqviewer_get() {
	<% if(viewer_type.equals("plugin")){ %>
		document.location.href="rqviewer.jsp" + "?doc=" + "<%=doc%>" + "&runvar=" + "<%=runvar%>" ;
	<% } %>	
}
// post
function callrqviewer() {
	<% if(viewer_type.equals("plugin")){ %>
		frm = document.getElementById("frm");
		frm.action = "rqviewer.jsp";
		frm.method = "post";
		frm.submit();		
	<% } %>	
}
</script>
</HEAD>
<BODY topmargin="0" leftmargin="0">
<form id="frm" name="frm">
<input type="hidden" name="doc" value="<%=doc%>"/>
<input type="hidden" name="runvar" value="<%=runvar%>"/>
</form>
<script type="text/javascript" src="/rqreport/setup/rqdownloadapp.js"></script>
</BODY>
</HTML>

