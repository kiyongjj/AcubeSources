<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%//@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%
	RepositoryEnv renv = RepositoryEnv.getInstance();
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
		//action = "getRsDB";
		action = "getRs";
	}
	String xmldata = "";
	if(doc.equals("/searchday_baseday.rqx")){
		xmldata = "<SQL><SQLStmt DBIdx=\"0\" SQLIdx=\"0\" NameIdx=\"1\" QryType=\"0\" " +
			"UIType=\"1\" IsEditSQL=\"1\" Distinct=\"0\" Quotation=\"0\"><SQLData>" +
			"<![CDATA[SELECT RUN_TIME, FILE_NM, RUNCNT, SERVERTIME_AVE/1000 as SERVERTIME_AVE, TOTALTIME_AVE/1000 as TOTALTIME_AVE, MAXTIME/1000 as MAXTIME, MINTIME/1000 as MINTIME " +
			"FROM RQDOCSTAT WHERE RUN_TIME between $[duringstart] and $[duringend] and " +
			"FILE_NM like '%$[file_nm]%']]></SQLData><ConditionInfo/><OrderInfo " +
			"ClmnNm=\"RQADMIN_SUN.RQDOCSTAT.RUN_TIME\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.RUN_TIME\" " +
			"bAscnd=\"1\" bChck=\"0\"/><OrderInfo ClmnNm=\"RQADMIN_SUN.RQDOCSTAT.FILE_NM\" " +
			"EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.FILE_NM\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"RQADMIN_SUN.RQDOCSTAT.RUNCNT\" EditClmnNm=\"RQADMIN_SUN." +
			"RQDOCSTAT.RUNCNT\" bAscnd=\"1\" bChck=\"0\"/><OrderInfo ClmnNm=\"RQADMIN_SUN." +
			"RQDOCSTAT.TOTALTIME_AVE\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.TOTALTIME_AVE\" " +
			"bAscnd=\"1\" bChck=\"0\"/><OrderInfo ClmnNm=\"RQADMIN_SUN.RQDOCSTAT.MAXTIME\" " +
			"EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.MAXTIME\" bAscnd=\"1\" bChck=\"0\"/><OrderInfo " +
			"ClmnNm=\"RQADMIN_SUN.RQDOCSTAT.MINTIME\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.MINTIME\" " +
			"bAscnd=\"1\" bChck=\"0\"/><GroupInfo SchmNm=\"RQADMIN_SUN\" TblNm=\"RQDOCSTAT\" " +
			"ClmnNm=\"RUN_TIME\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.RUN_TIME\" bChck=\"0\"/>" +
			"<GroupInfo SchmNm=\"RQADMIN_SUN\" TblNm=\"RQDOCSTAT\" ClmnNm=\"FILE_NM\" EditClmnNm=" +
			"\"RQADMIN_SUN.RQDOCSTAT.FILE_NM\" bChck=\"0\"/><GroupInfo SchmNm=\"RQADMIN_SUN\" " +
			"TblNm=\"RQDOCSTAT\" ClmnNm=\"RUNCNT\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.RUNCNT\" " +
			"bChck=\"0\"/><GroupInfo SchmNm=\"RQADMIN_SUN\" TblNm=\"RQDOCSTAT\" ClmnNm=\"TOTALTIME_AVE\" " +
			"EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.TOTALTIME_AVE\" bChck=\"0\"/><GroupInfo " +
			"SchmNm=\"RQADMIN_SUN\" TblNm=\"RQDOCSTAT\" ClmnNm=\"MAXTIME\" EditClmnNm=\"RQADMIN_SUN." +
			"RQDOCSTAT.MAXTIME\" bChck=\"0\"/><GroupInfo SchmNm=\"RQADMIN_SUN\" TblNm=\"RQDOCSTAT\" " +
			"ClmnNm=\"MINTIME\" EditClmnNm=\"RQADMIN_SUN.RQDOCSTAT.MINTIME\" bChck=\"0\"/><HavingInfo/>" +
			"</SQLStmt></SQL>";
	}else if(doc.equals("/searchday_basedoc.rqx")){
		xmldata = "<SQL><SQLStmt DBIdx=\"0\" SQLIdx=\"0\" NameIdx=\"1\" QryType=\"0\" " +
			"UIType=\"1\" IsEditSQL=\"1\" Distinct=\"0\" Quotation=\"0\"><SQLData>" +
			"<![CDATA[SELECT RUN_TIME, FILE_NM, RUNCNT, SERVERTIME_AVE/1000 as SERVERTIME_AVE, TOTALTIME_AVE/1000 as TOTALTIME_AVE, MAXTIME/1000 as MAXTIME, MINTIME/1000 as MINTIME " + 
			"FROM RQDOCSTAT WHERE RUN_TIME between $[duringstart] " +
			"and $[duringend] and FILE_NM like '%$[file_nm]%']]></SQLData><ConditionInfo/>" +
			"<OrderInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" bAscnd=\"1\" " +
			"bChck=\"0\"/><OrderInfo ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" bAscnd=\"1\" " +
			"bChck=\"0\"/><OrderInfo ClmnNm=\"MINTIME\" EditClmnNm=\"MINTIME\" bAscnd=\"1\" " +
			"bChck=\"0\"/><GroupInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"MINTIME\" EditClmnNm=\"MINTIME\" bChck=\"0\"/><HavingInfo/>" +
			"</SQLStmt></SQL>";
	}else if(doc.equals("/searchmonth_baseday.rqx")){
		xmldata = "<SQL><SQLStmt DBIdx=\"0\" SQLIdx=\"0\" NameIdx=\"1\" QryType=\"0\" " +
			"UIType=\"1\" IsEditSQL=\"1\" Distinct=\"0\" Quotation=\"0\"><SQLData>" +
			"<![CDATA[SELECT substr(RUN_TIME,0,6) as RUN_TIME, FILE_NM, RUNCNT, SERVERTIME_AVE/1000 as SERVERTIME_AVE, TOTALTIME_AVE/1000 as TOTALTIME_AVE, MAXTIME/1000 as MAXTIME, MINTIME/1000 as MINTIME " +
			"FROM RQDOCSTAT WHERE RUN_TIME between " +
			"$[duringstart] and $[duringend] and FILE_NM like '%$[file_nm]%']]></SQLData>" +
			"<ConditionInfo/><OrderInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" bAscnd=\"1\" " +
			"bChck=\"0\"/><OrderInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bAscnd=\"1\" " +
			"bChck=\"0\"/><OrderInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bAscnd=\"1\" " +
			"bChck=\"0\"/><OrderInfo ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" " +
			"bAscnd=\"1\" bChck=\"0\"/><OrderInfo ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" " +
			"bAscnd=\"1\" bChck=\"0\"/><OrderInfo ClmnNm=\"MINTIME\" EditClmnNm=\"MINTIME\" " +
			"bAscnd=\"1\" bChck=\"0\"/><GroupInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" " +
			"bChck=\"0\"/><GroupInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bChck=\"0\"/><GroupInfo " +
			"ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" bChck=\"0\"/><GroupInfo " +
			"ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" bChck=\"0\"/><GroupInfo ClmnNm=\"MINTIME\" " +
			"EditClmnNm=\"MINTIME\" bChck=\"0\"/><HavingInfo/></SQLStmt></SQL>";
	}else if(doc.equals("/searchmonth_basedoc.rqx")){
		xmldata = "<SQL><SQLStmt DBIdx=\"0\" SQLIdx=\"0\" NameIdx=\"1\" QryType=\"0\" " +
			"UIType=\"1\" IsEditSQL=\"1\" Distinct=\"0\" Quotation=\"0\"><SQLData>" +
			"<![CDATA[SELECT substr(RUN_TIME,0,6) as RUN_TIME, FILE_NM, RUNCNT, SERVERTIME_AVE/1000 as SERVERTIME_AVE, TOTALTIME_AVE/1000 as TOTALTIME_AVE, MAXTIME/1000 as MAXTIME, MINTIME/1000 as MINTIME " +
		    "FROM RQDOCSTAT WHERE RUN_TIME between $[duringstart] and " +
			"$[duringend] and FILE_NM like '%$[file_nm]%']]></SQLData><ConditionInfo/>" +
			"<OrderInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<OrderInfo ClmnNm=\"MINTIME\" EditClmnNm=\"MINTIME\" bAscnd=\"1\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"RUN_TIME\" EditClmnNm=\"RUN_TIME\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"FILE_NM\" EditClmnNm=\"FILE_NM\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"RUNCNT\" EditClmnNm=\"RUNCNT\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"TOTALTIME_AVE\" EditClmnNm=\"TOTALTIME_AVE\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"MAXTIME\" EditClmnNm=\"MAXTIME\" bChck=\"0\"/>" +
			"<GroupInfo ClmnNm=\"MINTIME\" EditClmnNm=\"MINTIME\" bChck=\"0\"/><HavingInfo/></SQLStmt></SQL>";
	}
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<TITLE>REQUBE REPORT</TITLE>
<SCRIPT language="javaScript">
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
	if(window.opener == null){
		RQViewer.ToolBarVisible = false;
		RQViewer.StatusBarVisible = false;
	}
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
			    //oReport = RQViewer.OpenReport("<%=basePath%>document/getreport.jsp?doc=" + docName);
				//oReport = RQViewer.OpenReport("<%=basePath%>rqcheck/rqvdownload/RQdownload.jsp?filename=" + docName);
				oReport = RQViewer.OpenReport("<%=basePath%>environment" + docName);
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
			OConnObj.AddParameter("strXml",document.getElementById("rqxml").xml);
			OConnObj.SetQueryCount(oSQL.GetQueryCount());
			OConnObj.SetPath("<%=basePath%>RQDataset.jsp?jndiname=" + "<%=renv.jndiName%>");
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
	//alert("EndRunReport");
}
function eventRefresh()
{
	//alert("Refresh");
}
</script>
<% } %>

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
<xml id="rqxml"><%=xmldata%></xml>
</BODY>
</HTML>

