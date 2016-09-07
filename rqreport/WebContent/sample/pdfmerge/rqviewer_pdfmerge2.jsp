<%@ page contentType="text/html;charset=EUC-KR"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%
	// this page is designed for cross-browsing 
	// but pdf merge function is not working
	String user_agt = request.getHeader("user-agent");
	String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";

	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	
	String[] lm_doc = request.getParameterValues("doc");
	String[] lm_runvar = request.getParameterValues("runvar");
	
	String[]     doc = new String[lm_doc.length];
	String[]  runvar = new String[lm_doc.length];
	
	for (int i=0 ; i < lm_doc.length ; i++)
	{
		if (lm_doc[i] != null)
		{
			doc[i] = new String(lm_doc[i].getBytes(lm_serverCharset), lm_RQCharset);
		}
		else
			doc[i] = new String();
	}
	
	for (int j=0 ; j < lm_runvar.length ; j++)
	{
		if (lm_runvar[j] != null)
			runvar[j] = new String(lm_runvar[j].getBytes(lm_serverCharset), lm_RQCharset);
		else
			runvar[j] = new String();
	}
	String jndiname = request.getParameter("jndiname");
	String strMergefile = request.getParameter("mergefile");
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=EUC-KR">	
<TITLE>REQUBE REPORT</TITLE>	
<SCRIPT language="javaScript">
<!--
cnt = 0;
mergefiles = "";

function OnRun()
{
	var oReport;
	var oSQL;
	var oConnection;
	var OConnObj;
	var oDataSet;
	var oDataSetCtrl;

	var links = document.getElementsByTagName("input");
	var docCnt = 0;
	for(var l = 0 ; l < links.length ; l ++){
		if(links[l].getAttribute("name") == "docname"){
			docCnt ++;
		}
	}
	
	var docName = "";
	var runvar = "";
	if(docCnt > 1){ 
		// 한개 이상이면 배열로 받고
		docName = document.all.docname[cnt].value;
		runvar  = document.all.runvar[cnt].value;
	}else{
		// 하나면 그값을 그냥 가져온다.
		docName = document.getElementById("docname").value;
		runvar  = document.getElementById("runvar").value;
	}
	document.getElementById("nowdocname").value = docName;

	<% if(viewer_type.equals("ocx")){ %>
	
		//---Viewer 속성(Property) 세팅 부분-----------------------------------------------------------------------------
		//RQViewer.BackColor = 255;
		RQViewer.ToolBarVisible = false;
		RQViewer.StatusBarVisible = false;
		RQViewer.ShowProgressDialog = false;
		//RQViewer.EmptyDataCheckOption = "part";	//"all" or "part" or "Query1|Query2"
		
		//---OpenReport를 사용하여 문서열기--------------------------------------------------------------------
		if (docName == 'null')
		{
			alert("DocName is Null");
			return;
		}
		else
		{
			try
			{
			    oReport = RQViewer.OpenReport("<%=basePath%>document/getreport.jsp?doc=" + docName);
			} catch(err)
			{
				return;
			}			
		}
	
		//---문서연결을 위한 BaseURL 설정----------------------------------------------------------------------
		RQViewer.SetBaseURL("<%=basePath%>");	
		
		//--- SQLExec 설정----------------------------------------------------- 
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
			OConnObj.AddParameter("action","getRsDB");
			OConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
			OConnObj.AddParameter("doc",docName);
			// for jndiname parameter
		<%if(jndiname != null && !jndiname.equals("")){%>
			OConnObj.AddParameter("jndiname","<%=jndiname%>");
		<%}%>
			OConnObj.SetQueryCount(oSQL.GetQueryCount());
			OConnObj.SetPath("<%=basePath%>RQDataset.jsp");
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
--> 
</SCRIPT>
<% if(viewer_type.equals("ocx")){ %>
<!-- 이벤트 설정 -->
<script language="JavaScript" for="RQViewer" event="EmptyResultData">
	//alert("EmptyResultData");
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport">
	var finishflag = document.getElementById("finishflag");
	var docfN = document.getElementById("nowdocname").value;
	
	var docN = docfN.substring(docfN.lastIndexOf("/") + 1);
	var lm_docname= docN.substring(0, docN.lastIndexOf("."));

	var lm_pdffileloc = "C:" + "/" + lm_docname + ".pdf";
	mergefiles += lm_pdffileloc + "|";
		
	RQViewer.SendToPdf(lm_pdffileloc, false); // save pdf file
	RQViewer.CloseReport();
	cnt++;
	
	var finishflag = document.getElementById("finishflag");
	if(finishflag.value != "true"){
		if (cnt < <%=lm_doc.length%>){
			OnRun();
		}else if(cnt == <%=lm_doc.length%>){
			RQViewer.MergePDF("<%=strMergefile%>", mergefiles, false);
		}
	}
		
</script>
<script language="JavaScript" for="RQViewer" event="FinishPrint">
	RQViewer.CloseReport();
	cnt++;
	
	var finishflag = document.getElementById("finishflag");
	if(finishflag.value != "true"){
		if (cnt < <%=lm_doc.length%>)  OnRun();
	}
		
</script>
<script language="JavaScript" for="RQViewer" event="FinishSendToExcel">
	RQViewer.CloseReport();
	cnt++;
	
//	parent.frm.exec_cnt.value = cnt;
	var finishflag = document.getElementById("finishflag");
	if(finishflag.value != "true"){
		if (cnt < <%=lm_doc.length%>)  OnRun();
	}
	
	//alert("EndRunReport");
</script>
<script language="JavaScript" for="RQViewer" event="CancelPrint">
	var finishflag = document.getElementById("finishflag");
	finishflag.value = "true";
	//alert("CancelPrint");
</script>

<script language="JavaScript" for="RQViewer" event="Refresh">
	alert("Refresh");
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
	var finishflag = document.getElementById("finishflag");
	var docfN = document.getElementById("nowdocname").value;
	
	var docN = docfN.substring(docfN.lastIndexOf("/") + 1);
	var lm_docname= docN.substring(0, docN.lastIndexOf("."));

	var lm_pdffileloc = "C:" + "/" + lm_docname + ".pdf";
	mergefiles += lm_pdffileloc + "|";
		
	oRQPlugin.SendToPdf(lm_pdffileloc, false); // save pdf file
	oRQPlugin.CloseReport();
	cnt++;
	
	var finishflag = document.getElementById("finishflag");
	if(finishflag.value != "true"){
		if (cnt < <%=lm_doc.length%>){
			OnRun();
		}else if(cnt == <%=lm_doc.length%>){
			oRQPlugin.MergePDF("<%=strMergefile%>", mergefiles, false);
		}
	}
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
<%
for (int i=0 ; i < lm_doc.length ; i++)
{
%>
<input type="hidden" name="docname" value="<%=doc[i]%>" />
<input type="hidden" name="runvar"  value="<%=runvar[i]%>" />
<%
}
%>
</FORM>
</DIV>
<% if(viewer_type.equals("ocx")){ %>
	<script language="javaScript" src="<%=basePath%>setup/rqviewer.js"></script>
<% }else{ %>
	<script language="javaScript" src="<%=basePath%>setup/rqviewer_mozilla.js"></script>
<% } %>
<input type="hidden" name="finishflag" id="finishflag"/>
<input type="hidden" name="nowdocname" id="nowdocname"/>
</BODY>
</HTML>
