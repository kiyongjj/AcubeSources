<%@ page contentType="text/html; charset=utf-8" %>
<%@page import="com.sds.rqreport.Environment"%>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%
String umc = session.getAttribute("UM") != null ? "1" : "-1";
//String contextname = request.getContextPath();
String contextname = request.getRequestURI();
contextname = contextname.substring(0, contextname.indexOf("/rqreport")+9);
Environment env = Environment.getInstance();
String lm_sso = env.SSOUse;
String lm_ssoEss = env.SSOEss;
if(lm_sso.equals("")){lm_sso = "no";}
%>
<% if(!lm_sso.equalsIgnoreCase("no")){ %>
<OBJECT ID="EpAdm2 Control" name="EpAdmC" CLASSID="CLSID:C63E3330-049F-4C31-B47E-425C84A5A725" 
	style="size: 0px;height: 0px;"></OBJECT>
<% } %>
<script language="javaScript">
AppName = (navigator.appName);
IE = (AppName == "Microsoft Internet Explorer");
var msg = '';
var umc = '<%=umc%>';

if (IE) {
<%
if(lm_sso.equalsIgnoreCase("yes")){
%>
	var rrtn = "";
	if(EpAdmC != null)
		rrtn = EpAdmC.GetSecureBox();
<%
}
%>	
	if(	<%if(lm_sso.equalsIgnoreCase("yes")){%>rrtn != "" 
		<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> && <%}else{%> || <%}%> 
	   	<%}%> umc == 1) {
		//alert('login sucess !!');
		<%if(lm_sso.equalsIgnoreCase("yes")){%>
			if(rrtn != "" && umc == -1){
				//alert('SSO logon');
				document.location.href="<%=contextname%>/common/sessionPrc.jsp?mode=ssologon";
			}
		<%}%>
	} else {
		if(opener){
			<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> 
			alert("<rqfmt:message strkey='cs.alert.invalidsession.sso'/>");
			<%}else{%>
			alert("<rqfmt:message strkey='cs.alert.invalidsession'/>");
			<%}%> 
			opener.document.location.href='<%=contextname%>/index.jsp';
			self.close();	
		}else{
			<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> 
			alert("<rqfmt:message strkey='cs.alert.invalidsession.sso'/>");
			<%}else{%>
			alert("<rqfmt:message strkey='cs.alert.invalidsession'/>");
			<%}%> 
			document.location.href='<%=contextname%>/index.jsp';
		}
	}
}else{
	if(	<%if(lm_sso.equalsIgnoreCase("yes")){%>rrtn != "" 
		<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> && <%}else{%> || <%}%> 
		<%}%> umc == 1) {
		//alert('login sucess !!');
		<%if(lm_sso.equalsIgnoreCase("yes")){%>
			if(rrtn != "" && umc == -1){
				//alert('SSO logon');
				document.location.href="<%=contextname%>/common/sessionPrc.jsp?mode=ssologon";
			}
		<%}%>
	}else{
		if(opener){
			<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> 
			alert("<rqfmt:message strkey='cs.alert.invalidsession.sso'/>");
			<%}else{%>
			alert("<rqfmt:message strkey='cs.alert.invalidsession'/>");
			<%}%> 
			opener.document.location.href='<%=contextname%>/index.jsp';
			self.close();	
		}else{
			<%if(lm_ssoEss.equalsIgnoreCase("yes")){%> 
			alert("<rqfmt:message strkey='cs.alert.invalidsession.sso'/>");
			<%}else{%>
			alert("<rqfmt:message strkey='cs.alert.invalidsession'/>");
			<%}%>
			document.location.href='<%=contextname%>/index.jsp';
		}
	}
}
</script>

