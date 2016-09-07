<%@ page contentType="text/html;charset=EUC-KR"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String lm_doc = request.getParameter("doc");
	String doc = new String(lm_doc.getBytes("8859_1"),"KSC5601");
	String lm_runvar = request.getParameter("runvar");
	String runvar;
	if (lm_runvar != null)
		runvar = new String(lm_runvar.getBytes("8859_1"),"KSC5601"); 
	else
		runvar = new String();
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=EUC-KR">	
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
	
	//---실행변수 설정-------------------------------------------------------------------------------------
	oReport.SetRuntimeVariable(runvar);

	oSQL = oReport.GetSQLControl();

	//---CreateConnectionControl 을 사용하여 Connection개체 만들기-----------------------------------------
	oConnection = RQViewer.CreateConnectionControl();

	//[Type 1] TXT
	//---Connection 개체의 설정----------------------------------------------------------------------------
	//[TXT] File or String 연결
	for (i=0; i<oSQL.GetCount(); i++)
	{
		oConnObj = oConnection.AddConnection("local");
		oConnObj.SetEncoding(oSQL.GetEncoding(i));
		oConnObj.SetSeperator(oSQL.GetRowSeperator(i),oSQL.GetColSeperator(i));
		oConnObj.AddResponseFile(oSQL.GetConnectionString(i));
		//oConnObj.AddResponseString(TXT_STRING_DATA);
	}

	//---DataSet을 설정------------------------------------------------------------------------------------
	//[TXT] DataSet 설정 
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
	
	//====================================================================================================
	//[Type 2] XML
	//[XML] File or String 연결
	for (i=0; i<oSQL.GetCount(); i++)
	{
		oConnObj = oConnection.AddConnection("local");
		oConnObj.AddXMLResponseFile(oSQL.GetConnectionString(i),oSQL.GetXMLRoot(i));
		//oConnObj.AddXMLResponseString(XML_STRING_DATA,oSQL.GetXMLRoot(i));
	}
	
	//---DataSet을 설정------------------------------------------------------------------------------------
	//[XML] DataSet 설정 
	oDataSetCtrl = RQViewer.CreateDataSetControl();
	
	for (i = 0; i< oConnection.GetCount() ;i++)
	{
		oConnObj = oConnection.GetConnection(i);
		for ( j = 0; j < oConnObj.GetCount(); j++)
		{
			oDataSet = oDataSetCtrl.AddDataSet("xml");
			oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j),oConnObj.GetXMLRoot(j));
			//oDataSet.SetBindingOption(2);		// XML Element 순서로 연결 옵션 
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
