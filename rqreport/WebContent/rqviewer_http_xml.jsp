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
	
	//---Viewer �Ӽ�(Property) ���� �κ�-----------------------------------------------------------------------------
	//RQViewer.BackColor = 255;
	//RQViewer.ToolBarVisible = false;
	//RQViewer.StatusBarVisible = false;
	//RQViewer.ShowProgressDialog = false;
	//RQViewer.EmptyDataCheckOption = "part";	//"all" or "part" or "Query1|Query2"
	
	//---OpenReport�� ����Ͽ� ��������--------------------------------------------------------------------
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
	
	//---���������� ���� BaseURL ����----------------------------------------------------------------------
	RQViewer.SetBaseURL("<%=basePath%>");	
	
	//---���ຯ�� ����-------------------------------------------------------------------------------------
	oReport.SetRuntimeVariable(runvar);

	oSQL = oReport.GetSQLControl();

	//---CreateConnectionControl �� ����Ͽ� Connection��ü �����-----------------------------------------
	oConnection = RQViewer.CreateConnectionControl();

	//[XML] Web Server�� XML Data����
	oConnObj = oConnection.AddConnection("http.post");	
	
	for (i=0; i<oSQL.GetCount(); i++)
	{
		oConnObj.AddResponseCount(1);
		oConnObj.AddXMLRoot(oSQL.GetXMLRoot(i));
	}
	
	oConnObj.SetPath("<%=basePath%>xmlData.xml");
	
	//---DataSet�� ����------------------------------------------------------------------------------------
	//[XML] DataSet ���� 
	oDataSetCtrl = RQViewer.CreateDataSetControl();
	
	for (i = 0; i< oConnection.GetCount() ;i++)
	{
		oConnObj = oConnection.GetConnection(i);
		for ( j = 0; j < oConnObj.GetCount(); j++)
		{
			oDataSet = oDataSetCtrl.AddDataSet("xml");
			oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j),oConnObj.GetXMLRoot(j));
			//oDataSet.SetBindingOption(2);		// XML Element ������ ���� �ɼ� 
		}
	}

	//---��������------------------------------------------------------------------------------------------
	RQViewer.Run();
}

//[----Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ�-------------------------------------------------
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
//]----Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ�-------------------------------------------------
--> 
</SCRIPT>
<!-- �̺�Ʈ ���� -->
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
<!-- [ Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ� -->
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
</FORM>
</DIV>
<!-- ] Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ� -->
<script language="javaScript" SRC="./setup/rqviewer.js"></script>
</BODY>
</HTML>
