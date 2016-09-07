<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String lm_doc = request.getParameter("doc");
	String doc = new String(lm_doc.getBytes(lm_serverCharset), lm_RQCharset);
	String lm_runvar = request.getParameter("runvar");
	String runvar;
	if (lm_runvar != null)
		runvar = new String(lm_runvar.getBytes(lm_serverCharset), lm_RQCharset); 
	else
		runvar = new String();
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">	
<TITLE>REQUBE REPORT</TITLE>	
<SCRIPT language="javaScript">
<!--
function OnRun()
{
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

	//---SQLExec 실행을 위한 BaseURL 설정------------------------------------------------------------------
	RQViewer.SetSQLExecURL("<%=basePath%>RQDataset.jsp",docName);
	
	//---실행변수 설정-------------------------------------------------------------------------------------
	oReport.SetRuntimeVariable(runvar);

	oSQL = oReport.GetSQLControl();

	//---CreateConnectionControl 을 사용하여 Connection개체 만들기-----------------------------------------
	oConnection = RQViewer.CreateConnectionControl();

	//---Connection 개체의 설정----------------------------------------------------------------------------
	if (!oReport.IsResultFile())
	{
		OConnObj = oConnection.AddConnection("http.post");
		OConnObj.AddParameter("action","getRsDB");
		OConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
		OConnObj.AddParameter("doc",docName);
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
	window.open(url,target,option);
}
//]----Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분-------------------------------------------------
--> 
</SCRIPT>
<!-- 이벤트 설정 -->
<script language="JavaScript" for="RQViewer" event="EmptyResultData">
	//alert("EmptyResultData");
	//self.location.href='http://www.reqube.com';
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport">
	//alert("EndRunReport");
</script>

<script language="JavaScript" for="RQViewer" event="Refresh">
	//alert("Refresh");
</script>
<STYLE>
	DIV { padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px;}
</STYLE>
</HEAD>
<BODY topmargin="0" leftmargin="0" onload="OnRun()">
<!-- [ Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분 -->
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
</FORM>
</DIV>
<!-- ] Hyper Link(문서연결)을 위해 반드시 있어야 하는 부분 -->
<script language="javaScript" SRC="./setup/rqviewer.js"></script>
</BODY>
</HTML>
