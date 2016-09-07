<%@ page contentType="text/html;charset=EUC-KR"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	/*
	String contextname = request.getRequestURI();
	contextname = contextname.substring(0, contextname.indexOf("/rqviewer.jsp"));
	*/
	String contextname = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+contextname+"/";
	String lm_doc = request.getParameter("doc");
	String doc = new String(lm_doc.getBytes(lm_serverCharset), lm_RQCharset);
	String lm_runvar = request.getParameter("runvar");
	String runvar;
	if(lm_runvar != null){
		runvar = new String(lm_runvar.getBytes(lm_serverCharset), lm_RQCharset);
	}else{
		runvar = new String();
	}
	// RDBMS=getRsDB, SAPJCO=getRsJCO
	String action = request.getParameter("action");
	if(action == null){
		action = "getRsDB";
	} 
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>REQUBE REPORT</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<TITLE>REQUBE REPORT</TITLE>
<SCRIPT language="javaScript">
function OnRun()
{	
	var path = "<%=basePath%>RQDataset.jsp"; 
	var docName = "<%=doc%>";
	var runvar = "<%=runvar%>";
	
	var oRQPlugin = document.getElementById('RQViewerPlugin');    

	oRQPlugin.OpenReport("<%=basePath%>document/getreport.jsp?doc=" + docName);

	//-- BaseURL ---------------------------------------------------------------
	oRQPlugin.SetBaseURL("<%=basePath%>");

	//---SQLExec -------------------------------------------------
	oRQPlugin.SetSQLExecURL("<%=basePath%>RQDataset.jsp",docName);

	//---runvar ----------------------------------------------------------------------
	oRQPlugin.SetRuntimeVariable(runvar);

	if (!oRQPlugin.IsResultFile())
	{
		oRQPlugin.AddConnection("http.post", docName, path);		
	}
	else
	{
		// result file open
		oRQPlugin.AddConnection("local", docName, path);
	}

	oRQPlugin.PrepareDataSet();
	
	oRQPlugin.SetRunMode(4); // RQRUNMODE_IEXPLORER (1), 
	                         // RQRUNMODE_PRESENTER (2), RQRUNMODE_PRESENTER_NM (3)
	                         // RQRUNMODE_PLUGIN (4), RQRUNMODE_PLUGIN_NM (5)
	oRQPlugin.Run();
}

//[----Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분-------------------------------------------------
function RQ_HyperLinkSummit()
{
	RQ_HyperLink.submit();
}

function RQ_SetTarget(val)
{
	RQ_HyperLink.target = val;
}

function RQ_SetAction(val)
{
	RQ_HyperLink.action = val;
}

function RQ_AddValue(name,val)
{
	if(document.getElementById(name)!= null)
	{
		document.getElementById(name).value = val;
	}
	else{
		elem = document.createElement("input");
		elem.type="hidden";
		elem.name=name;
		elem.id=name;
		elem.value=val;
		RQ_HyperLink.appendChild(elem);
	}
}

function RQ_ExcuteHyperLink(url,target,option)
{
/*	var string = option.substring(0,12);
	if (string == "fullscreen=1")
	{
		if (self.screen) {
			option += ",fullscreen=0";
			option += ",width=";
			option += screen.width;
			option += ",height=";
			option += screen.height;
		}
	}
*/
	window.open(url,target,option);
}
//]----Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분-------------------------------------------------
</SCRIPT>
<!-- 이벤트 설정 -->
<script language="JavaScript">
function eventEmptyResultData()
{
	//alert("EmptyResultData");
}
function eventEndRunReport()
{
	//alert("EndRunReport");
}
function eventRefresh()
{
	//alert("Refresh");
}
</script>
<STYLE>
	DIV { padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px;}
</STYLE>
</HEAD>
<BODY leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" onload="OnRun()">
<!-- [ Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분 -->
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
</FORM>
</DIV>
<!-- ] Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분 -->
<script>
document.write("<embed id='RQViewerPlugin' type='Application/RQPlugin' width=100% height=100%>");
</script>
</BODY>
</HTML>
