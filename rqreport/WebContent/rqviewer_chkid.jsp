<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%//@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%
/**
 * 참조 : 
 * rqviewer.jsp는 REQUBE서버 에서 제공하는 예제 샘플(Sample)페이지 로써
 * 뷰어(ocx) 호출과정을 보여준다.
 * 
 * RQViewer(ocx)를 통한 문서(RQX,RQV)호출을 구현하기 위해서는  
 * 이페이지(rqviewer.jsp)를 참조하여 업무단 JSP(페이지)를 구성하셔야 함.
 * 
 * 또한, 본 페이지는 업무단 세션(로그인)에 대한 로직이 구현되어 있지 않으므로
 * 세션처리를 하려면 반드시 이페이지를 업무단에서 구현하여야 함.
 **/
%>
<%
// URL pattern : 
// rqviewer.jsp?doc=/RQReport1.rqx&runvar=key1|value1|key2|value2
String user_agt = request.getHeader("user-agent");
String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";
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
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<TITLE>REQUBE REPORT</TITLE>
<script language="javascript" src="setup/HttpRequest.js"></script>
<SCRIPT language="javaScript">
var rqviewer = {}
rqviewer.viewerPage = function(){
}
rqviewer.viewerPage.prototype = {
	invokeServ : function(GetRunTimeInfo){
		XHR = new xhr.Request();
		var params = "act=setCLInfo"
			        + "&GetRunTimeInfo="+encodeURIComponent(GetRunTimeInfo);
		XHR.sendRequest("<%=basePath%>RQDocStat.do", params, this.callServlet, "POST");
	},
	callServlet : function(){
		if (XHR.httpRequest.readyState == 4) {
			if (XHR.httpRequest.status == 200) {
				// 
			} else {
				// 
			}
		}
	}
}

var browserName = navigator.appName;
function OnRun() {
	var path = "<%=basePath%>RQDataset.jsp"; 
	var docName = "<%=doc%>";
	var runvar  = "<%=runvar%>";
	var oReport;
	var oSQL;
	var oConnection;
	var OConnObj;
	var oDataSet;
	var oDataSetCtrl;
	
	//---Viewer 속성(Property) 세팅 부분-----------------------------------------------------------------------------
	//RQViewer.BackColor = 255;
	//RQViewer.ToolBarVisible = false;
	//RQViewer.StatusBarVisible = false;
	//RQViewer.ShowProgressDialog = false;
	//RQViewer.EmptyDataCheckOption = "part";	//"all" or "part" or "Query1|Query2"

	//---OpenReport를 사용하여 문서열기--------------------------------------------------------------------
	<% if(viewer_type.equals("ocx")){ %>
		if (docName == 'null')
		{
			alert("DocName is Null");
			return;
		}
		else
		{
			try
			{	
				// Repository Directory use 
			    oReport = RQViewer.OpenReport("<%=basePath%>document/getreport_chkid.jsp?doc=" + docName);
			    //oReport = RQViewer.OpenReport("<%=basePath%>document/getreport_chkid.jsp?getType=rqv&doc=" + docName);

			    // Temp Directory use
				//oReport = RQViewer.OpenReport("<%=basePath%>rqcheck/rqvdownload/RQdownload.jsp?filename=" + docName);
				//oReport = RQViewer.OpenReport("<%=basePath%>rqcheck/rqvdownload/RQReport1.rqv");
			} catch(err)
			{
				return;
			}
		}
		
		//---문서연결을 위한 BaseURL 설정----------------------------------------------------------------------
		RQViewer.SetBaseURL("<%=basePath%>");
	
		//---SQLExec 실행을 위한 BaseURL 설정------------------------------------------------------------------
		RQViewer.SetSQLExecURL("<%=basePath%>RQDataset.jsp",docName);
		
		//---실행변수 설정-------------------------------------------------------------------------------------
		oReport.SetRuntimeVariable(runvar);	
		oSQL = oReport.GetSQLControl();

		// ResponseType : 1 - RQCSV, 2 - XML, 3 - TXT
		// DBIFType : 1 - JDBC, 3 - SAP, 5 - TXT, 6 - XML, 7 - RQCSV
	
		//---CreateConnectionControl 을 사용하여 Connection개체 만들기-----------------------------------------
		oConnection = RQViewer.CreateConnectionControl();
	
		//---Connection 개체의 설정----------------------------------------------------------------------------
		if (!oReport.IsResultFile())
		{
			OConnObj = oConnection.AddConnection("http.post");
			//OConnObj.DisableEncryption();
			OConnObj.AddParameter("action","<%=action%>");
			OConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
			OConnObj.AddParameter("doc",docName);
			OConnObj.SetQueryCount(oSQL.GetQueryCount());
			OConnObj.SetPath("<%=basePath%>RQDataset_chkid.jsp");
		}
		else	// 결과 파일일 경우
		{
			for (i = 0; i<oSQL.GetCount(); i++)
			{
				OConnObj = oConnection.AddConnection("local");
				OConnObj.SetEncoding(oSQL.GetEncoding(i));
				OConnObj.AddResponseFile(oSQL.GetConnectionString(i));
				OConnObj.SetResponseType(oSQL.GetResponseType(i));
				OConnObj.SetSeperator(oSQL.GetRowSeperator(i),oSQL.GetColSeperator(i));
			}
		}
	
		//---DataSet을 설정------------------------------------------------------------------------------------
		oDataSetCtrl = RQViewer.CreateDataSetControl();
	
		for (i = 0; i< oConnection.GetCount() ;i++)
		{
			oConnObj = oConnection.GetConnection(i);
			for ( j = 0; j < oConnObj.GetCount(); j++)
			{
				oDataSet = oDataSetCtrl.AddDataSet("txt");
				oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j));
				oDataSet.SetEncoding(oConnObj.GetEncoding());
				oDataSet.SetSeperator(oConnObj.GetRowSeperator(),oConnObj.GetColSeperator());
				oDataSet.SetDataSetType(oConnObj.GetResponseType());
			}
		}
	
		//---문서실행------------------------------------------------------------------------------------------
		RQViewer.Run();

	<% }else{ %>
		
		oRQPlugin = document.getElementById('RQViewerPlugin');
		oRQPlugin.OpenReport("<%=basePath%>document/getreport_chkid.jsp?doc=" + docName);

		//-- BaseURL ---------------------------------------------------------------
		oRQPlugin.SetBaseURL("<%=basePath%>");
		
		//---SQLExec -------------------------------------------------
		oRQPlugin.SetSQLExecURL("<%=basePath%>RQDataset_chkid.jsp",docName);
	
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

	<% } %>
	
}

function RQ_HyperLinkSummit(){
	RQ_HyperLink.submit();
}

function RQ_SetTarget(val){
	RQ_HyperLink.target = val;
}

function RQ_SetAction(val){
	RQ_HyperLink.action = val;
}

function RQ_AddValue(name,val){
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

function RQ_ExcuteHyperLink(url,target,option){
/*	var string = option.substring(0,12);
	if (string == "fullscreen=1")
	{a
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
</SCRIPT>
<% if(viewer_type.equals("ocx")){ %>
<!-- 이벤트 설정 -->
<script language="JavaScript" for="RQViewer" event="EmptyResultData">
	//alert("EmptyResultData");
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport">
	//RQViewer.SendToExcel("C:/test.xls",true,0,true);
	////// Document Statistics ///////////////////////////
	//viewerPage = new rqviewer.viewerPage();
	//viewerPage.invokeServ(RQViewer.GetRunTimeInfo());
	//////////////////////////////////////////////////////
</script>

<script language="JavaScript" for="RQViewer" event="Refresh">
	//alert("Refresh");
</script>
<% }else{ %>
<!-- 이벤트 설정 -->
<script language="JavaScript">
function eventEmptyResultData()
{
	//alert("EmptyResultData");
}
function eventEndRunReport()
{	
	//////Document Statistics ///////////////////////////
	//viewerPage = new rqviewer.viewerPage();
	//viewerPage.invokeServ(oRQPlugin.GetRunTimeInfo());
	//////////////////////////////////////////////////////
}
function eventRefresh()
{
	//alert("Refresh");
}
</script>
<% } %>
<STYLE>
	DIV { padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px;}
</STYLE>
</HEAD>
<BODY topmargin="0" leftmargin="0" onload="OnRun()">
<!-- [ Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분 -->
<DIV name="RQ_HyperLinkPost" height="0" width="0">
<FORM id="RQ_HyperLink" method="post" name="RQ_HyperLink">
</FORM>
</DIV>
<% if(viewer_type.equals("ocx")){ %>
	<script language="javaScript" src="<%=basePath%>setup/rqviewer.js"></script>
<% }else{ %>
	<script language="javaScript" src="<%=basePath%>setup/rqviewer_mozilla.js"></script>
<% } %>
</BODY>
</HTML>

