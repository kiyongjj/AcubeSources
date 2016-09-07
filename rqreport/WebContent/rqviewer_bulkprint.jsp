<%@ page contentType="text/html;charset=EUC-KR"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	String print_flag = request.getParameter("print_flag") == null ? "false" : request.getParameter("print_flag");
	String excel_flag = request.getParameter("excel_flag") == null ? "false" : request.getParameter("excel_flag");
	String pdf_flag   = request.getParameter("pdf_flag") == null ? "false" : request.getParameter("pdf_flag");
	
	String saveDir    = "";
	if(request.getParameter("saveDir") == null){
		saveDir = "/";
	}else{
		String lm_saveDir = request.getParameter("saveDir");
		saveDir = lm_saveDir.replaceAll("\\\\", "/");
	}
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
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=EUC-KR">	
<TITLE>REQUBE REPORT</TITLE>	
<SCRIPT language="javaScript">
<!--
cnt = 0;

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
		// �Ѱ� �̻��̸� �迭�� �ް�
		docName = document.all.docname[cnt].value;
		runvar  = document.all.runvar[cnt].value;
	}else{
		// �ϳ��� �װ��� �׳� �����´�.
		docName = document.getElementById("docname").value;
		runvar  = document.getElementById("runvar").value;
	}
	document.getElementById("nowdocname").value = docName;
	
	//---Viewer �Ӽ�(Property) ���� �κ�-----------------------------------------------------------------------------
	//RQViewer.BackColor = 255;
	RQViewer.ToolBarVisible = false;
	RQViewer.StatusBarVisible = false;
	RQViewer.ShowProgressDialog = false;
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
	
	var print_flag = "<%=print_flag%>";
	
	if (print_flag == "true" && cnt == 0)
		//RQViewer.PrintSetupDialog(); //show dialog

	//---���������� ���� BaseURL ����----------------------------------------------------------------------
	RQViewer.SetBaseURL("<%=basePath%>");	
	
	//--- SQLExec ����----------------------------------------------------- 
	RQViewer.SetSQLExecURL("<%=basePath%>RQDataset.jsp",docName);

	//---���ຯ�� ����-------------------------------------------------------------------------------------
	oReport.SetRuntimeVariable(runvar);

	oSQL = oReport.GetSQLControl();

	// ResponseType : 1 - RQCSV, 2 - XML, 3 - TXT
	// DBIFType : 1 - JDBC, 3 - SAP, 5 - TXT, 6 - XML, 7 - RQCSV

	//---CreateConnectionControl �� ����Ͽ� Connection��ü �����-----------------------------------------
	oConnection = RQViewer.CreateConnectionControl();

	//---Connection ��ü�� ����----------------------------------------------------------------------------
	if (!oReport.IsResultFile())
	{
		OConnObj = oConnection.AddConnection("http.post");
		OConnObj.AddParameter("action","getRsDB");
		OConnObj.AddParameter("runvar",oReport.GetRuntimeVariable());
		OConnObj.AddParameter("doc",docName);
		OConnObj.SetQueryCount(oSQL.GetQueryCount());
		OConnObj.SetPath("<%=basePath%>RQDataset.jsp");
	}
	else	// ��� ������ ��� 
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
	
	//---DataSet�� ����------------------------------------------------------------------------------------
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
//]----Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ�-------------------------------------------------
--> 
</SCRIPT>
<!-- �̺�Ʈ ���� -->
<script language="JavaScript" for="RQViewer" event="EmptyResultData">
	//alert("EmptyResultData");
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport">
	var finishflag = document.getElementById("finishflag");
	var docfN = document.getElementById("nowdocname").value;
	
	var docN = docfN.substring(docfN.lastIndexOf("/") + 1);
	var lm_docname= docN.substring(0, docN.lastIndexOf("."));
	
	if(finishflag.value != "true"){
	<%	
		if (print_flag.equals("true")) {
	%>
		  RQViewer.Print(false, 0, 0, 1);
	<%
		}else if(excel_flag.equals("true") || excel_flag.equals("show")){
	%>
		  	RQViewer.SendToExcel("", true, 0, true); // show excel
	<%
		}else if(excel_flag.equals("save")){
	%>
			RQViewer.SendToExcel("<%=saveDir%>" + "/" + lm_docname + ".xls"  , true, 0, false); // save excel file
	<%
		}else if(pdf_flag.equals("show")){
	%>
			alert('show');
		  	RQViewer.SendToPdf("", true); // show pdf
	<%
		}else if(pdf_flag.equals("save")){
	%>	
			alert('save');
			RQViewer.SendToPdf("<%=saveDir%>" + "/" + lm_docname + ".pdf", false); // save pdf file
	<%
		}
	%>
	}
	//alert("EndRunReport");
</script>
<script language="JavaScript" for="RQViewer" event="FinishPrint">
	RQViewer.CloseReport();
	cnt++;
	
	var finishflag = document.getElementById("finishflag");
	if(finishflag.value != "true"){
		if (cnt < <%=lm_doc.length%>)  OnRun();
	}

	//alert("EndRunReport");
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
<STYLE>
	DIV { padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px;}
</STYLE>
</HEAD>
<BODY topmargin="0" leftmargin="0" onload="OnRun()">
<!-- [ Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ� -->
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
<%
for (int i=0 ; i < lm_doc.length ; i++)
{
%>
<input type=hidden name=docname value="<%=doc[i]%>" >
<input type=hidden name=runvar  value="<%=runvar[i]%>" >
<%
}
%>
</FORM>
</DIV>
<!-- ] Hyper Link(��������)�� ���� �ݵ�� �־�� �ϴ� �κ� -->
<script language="javaScript" SRC="<%=path%>/setup/rqviewer.js"></script>
<input type="hidden" name="finishflag"/>
<input type="hidden" name="nowdocname" id="nowdocname"/>
</BODY>
</HTML>
