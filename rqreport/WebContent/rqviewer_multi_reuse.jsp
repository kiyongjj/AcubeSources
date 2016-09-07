<%@ page contentType="text/html;charset=EUC-KR"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
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
	var oConnObj;
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

	// ResponseType : 1 - RQCSV, 2 - XML, 3 - TXT
	// DBIFType : 1 - JDBC, 3 - SAP, 5 - TXT, 6 - XML, 7 - RQCSV

	//---Connection ��ü�� ����----------------------------------------------------------------------------
	//[Type 1] : HTTP(REQUBE ���� ���)�� XML(Web Server�� �ִ� XML ����) ȥ�� ����

	for (i=0; i<oSQL.GetCount(); i++)
	{
		nType = oSQL.GetDBIFType(i);
		switch(nType)
		{
			case 5: // TXT
				oConnObj = oConnection.GetConnectionByID("http.post",oSQL.GetDBID(i));
				oConnObj.SetEncoding(oSQL.GetEncoding(i));
				oConnObj.SetSeperator(oSQL.GetRowSeperator(i),oSQL.GetColSeperator(i));
				oConnObj.AddResponseCount(1);
				oConnObj.SetResponseType(3);
				oConnObj.SetPath("<%=basePath%>sawon_UTF8_TAB.txt");				
			break;
			case 6://XML
				oConnObj = oConnection.GetConnectionByID("http.post",oSQL.GetDBID(i));				
				oConnObj.AddXMLRoot(oSQL.GetXMLRoot(i));				
				oConnObj.AddResponseCount(1);			
				oConnObj.SetPath("<%=basePath%>xmlData.xml");
			break;
			default: // HTTP(REQUBE Server�̿�)
				oConnObj = oConnection.GetConnectionByID("http.post","RQDataSet");	
				oConnObj.AddParameter("action","getRsDB");
				oConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
				oConnObj.AddParameter("doc",docName);
				oConnObj.AddResponseCount(oSQL.GetSQLStmtCount(i));
				oConnObj.SetPath("<%=basePath%>RQDataset.jsp");
			break;
		}
	}

	//[Type 2] : LOCAL(REQUBE ������ ������ �ʴ� XML Local File or String)�� TXT File or String 
/*	
	for (i=0; i<oSQL.GetCount(); i++)
	{
		nType = oSQL.GetDBIFType(i);
		switch(nType)
		{
			case 5: // TXT
				oConnObj = oConnection.AddConnection("local");
				oConnObj.SetEncoding(oSQL.GetEncoding(i));
				oConnObj.SetSeperator(oSQL.GetRowSeperator(i),oSQL.GetColSeperator(i));
				oConnObj.AddResponseFile(oSQL.GetConnectionString(i));	// ��� TXT ���� �н� ������ �־��ش�. 
				//oConnObj.AddResponseString(TXT_STRING_DATA);			// ����� TXT String�� ��� String Object�� �־��ش�.
				oConnObj.SetResponseType(3);				
			break;
			case 6: // XML
				oConnObj = oConnection.AddConnection("local");
				oConnObj.AddXMLResponseFile(oSQL.GetConnectionString(i),oSQL.GetXMLRoot(i));	// XML ���� �н��� XML Root
				//oConnObj.AddXMLResponseString(XML_STRING_DATA,oSQL.GetXMLRoot(i));			// XML String Object �� XML Root
			break;
			default: // HTTP(REQUBE Server�̿�)
				oConnObj = oConnection.GetConnectionByID("http.post","RQDataSet");	
				oConnObj.AddParameter("action","getRsDB");
				oConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
				oConnObj.AddParameter("doc",docName);
				oConnObj.AddResponseCount(oSQL.GetSQLStmtCount(i));
				oConnObj.SetPath("<%=basePath%>RQDataset.jsp");
			break;
		}		
	}
*/
	//---DataSet�� ����------------------------------------------------------------------------------------
	oDataSetCtrl = RQViewer.CreateDataSetControl();
	
	for (i = 0; i< oConnection.GetCount() ;i++)
	{
		oConnObj = oConnection.GetConnection(i);
		nType = oConnObj.GetResponseType();
		switch(nType)
		{
			case 2: // XML
				for ( j = 0; j < oConnObj.GetCount(); j++)
				{
					oDataSet = oDataSetCtrl.SetDataSet(oConnObj.GetResponseIndex(j),"xml");
					oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j),oConnObj.GetXMLRoot(j));
				}
			break;
			default: // TXT
				for ( j = 0; j < oConnObj.GetCount(); j++)
				{
					oDataSet = oDataSetCtrl.SetDataSet(oConnObj.GetResponseIndex(j),"txt");
					oDataSet.SetDataSetFile(oConnObj.GetResponseFile(j));
					oDataSet.SetEncoding(oConnObj.GetEncoding());
					oDataSet.SetSeperator(oConnObj.GetRowSeperator(),oConnObj.GetColSeperator());
					oDataSet.SetDataSetType(nType);
				}
			break;
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
