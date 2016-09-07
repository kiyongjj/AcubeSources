<%@ page language="java" import="java.util.*" pageEncoding="EUC-KR"%>
<%@ page import="com.sds.rqreport.*" %>
<%
	Environment env;
	env = Environment.getInstance();
	if(env.useMySingleLogin)
	{
 %>
<OBJECT ID="EpAdm2 Control" name="EpAdmC" CLASSID="CLSID:C63E3330-049F-4C31-B47E-425C84A5A725"></OBJECT>
<%
	}
%>
