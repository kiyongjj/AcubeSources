<%@ page contentType="text/html;charset=EUC-KR" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String lm_doc = request.getParameter("doc");
	String doc = new String(lm_doc.getBytes("8859_1"),"KSC5601");
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
	var oReport;
	var oSQL;
	var oConnection;
	var OConnObj;
	var oDataSet;
	var oDataSetCtrl;
	
	if (docName == 'null')
	{
		alert("DocName is Null");
		return;
	}
	else
	{
		try
		{
			oReport = eb1.OpenReport("<%=basePath%>document/getreport.jsp?doc=" + docName);
		} catch(err)
		{
			return;
		}			
	}

	eb1.SetBaseURL("<%=basePath%>");

	oSQL = oReport.GetSQLControl();
	
	oConnection = eb1.CreateConnectionControl();
	
	// XML 파일 1개이고 Query는 여러개인 경우
	OConnObj = oConnection.AddConnection("http.post");	
	
	for (i=0; i<oSQL.GetCount(); i++)
	{
		OConnObj.AddResponseCount(1);	
		OConnObj.AddXMLRoot(oSQL.GetXMLRoot(i));
	}
	
	OConnObj.SetPath("<%=basePath%>sawon.xml");
		
	// DataSet 설정(xml)			
	oDataSetCtrl = eb1.CreateDataSetControl();
	
	for (i = 0; i< oConnection.GetCount() ;i++)
	{
		oConnObj = oConnection.GetConnection(i);
		for ( j = 0; j < oConnObj.GetCount(); j++)
		{
			oDataSet = oDataSetCtrl.SetDataSet(oConnObj.GetResponseIndex(j),"xml");
			oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j),oConnObj.GetXMLRoot(j));
		}
	}
	
	eb1.Run();
}

// Hyper Link
function RQ_HyperLinkSummit()
{
	// var value = document.getElementById("param").value;
	//alert(value);
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
<BODY topmargin="0" leftmargin="0" onload="OnRun();">
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
</FORM>
</DIV>
<script language="javaScript" SRC="./setup/rqviewer.js"></script>
</BODY>
</HTML> 