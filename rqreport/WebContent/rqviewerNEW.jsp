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
<HTML>
<HEAD>

<script language='javascript'>
<!--


var Request = function()
{
    this.getParameter = function( name )
    {
        var rtnval = "";
        var nowAddress = unescape(location.href);
        var parameters = (nowAddress.slice(nowAddress.indexOf("?")+1,nowAddress.length)).split("&");


        for(var i = 0 ; i < parameters.length ; i++)
        {
            var varName = parameters[i].split("=")[0];
            if(varName.toUpperCase() == name.toUpperCase())
            {
                rtnval = parameters[i].split("=")[1];
                break;
            }
        }
        return rtnval;
    }
}


window.onload = function()
{
    var request = new Request();
    if(opener==undefined) window.open('./noname1.html','popup','status=no, top=0, left=0, width='+window.screen.width+', height='+(window.screen.height-100));
    else opener.close();
}


-->
</script>





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
                   <%if(doc.indexOf("rqv") == -1){%>
		    oReport = RQViewer.OpenReport("<%=basePath%>document/getreport.jsp?doc=" + docName);
                   <%}else{%>
                    //document.location.href="http://10.100.120.5:8188/rqreport/index.jsp";
		    oReport = RQViewer.OpenReport("http://10.100.120.5:8188/FssERP/upload" + docName);
                   <%}%>
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
<!-- 이벤트 설정 -->
<script language="JavaScript" for="RQViewer" event="EmptyResultData">
	//alert("EmptyResultData");
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport">
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
<script language="javaScript" SRC="<%=basePath%>setup/rqviewer.js"></script>
</BODY>
</HTML>
