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
var showToolbar=false;
var showStatusbar=false;
var showToolbarButtonID=0;
var history="";
var historycounter=0;

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

function myFunction() {
	RQViewer.CloseReport();
	RQViewer.BackColor = RGB(255,0,0);
}

function myRefresh()
{
	RQViewer.Refresh();
}

function myStop()
{
	RQViewer.Stop();
}

function myfirstpage()
{
	RQViewer.MoveFirstPage();
	var str = "CurPage : ";
	str += RQViewer.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += RQViewer.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function myprevpage()
{
	RQViewer.MovePrevPage();
	var str = "CurPage : ";
	str += RQViewer.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += RQViewer.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function mynextpage()
{
	RQViewer.MoveNextPage();
	var str = "CurPage : ";
	str += RQViewer.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += RQViewer.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function mylastpage()
{
	RQViewer.MoveLastPage();
	var str = "CurPage : ";
	str += RQViewer.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += RQViewer.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function myZoomIn()
{
	RQViewer.ZoomIn();
}

function myZoomOut()
{
	RQViewer.ZoomOut();
}

function RGB(r, g, b)
{
	return ( r | (g << 8) | ( b<<16) );
}

function myToolbar()
{
	if (showToolbar==true)
	{
		RQViewer.ToolBarVisible = true;
		showToolbar = false;
	}
	else
	{
		RQViewer.ToolBarVisible = false;
		showToolbar = true;
	}
}

function myStatusbar()
{
	if (showStatusbar==true)
	{
		RQViewer.StatusBarVisible = true;
		showStatusbar = false;
	}
	else
	{
		RQViewer.StatusBarVisible = false;
		showStatusbar = true;
	}
}

function sel_item(sel){
 if (sel.options[1].selected)
   showToolbarButtonID = 0;
 else if (sel.options[2].selected)
   showToolbarButtonID = 1;
 else if (sel.options[3].selected)
   showToolbarButtonID = 2;
 else if (sel.options[4].selected)
 	showToolbarButtonID = 3;
 else if (sel.options[5].selected)
 	showToolbarButtonID = 4;
 else if (sel.options[6].selected)
 	showToolbarButtonID = 5;
 else if (sel.options[7].selected)
 	showToolbarButtonID = 6;
 else if (sel.options[8].selected)
 	showToolbarButtonID = 7;
 else if (sel.options[9].selected)
 	showToolbarButtonID = 8;
 else if (sel.options[10].selected)
 	showToolbarButtonID = 9;
 else if (sel.options[11].selected)
 	showToolbarButtonID = 10;
 else if (sel.options[12].selected)
 	showToolbarButtonID = 11;
  else if (sel.options[13].selected)
 	showToolbarButtonID = 12;
 else if (sel.options[14].selected)
 	showToolbarButtonID = 13;
 else if (sel.options[15].selected)
 	showToolbarButtonID = 14;
  else if (sel.options[16].selected)
 	showToolbarButtonID = 15;
 else if (sel.options[17].selected)
 	showToolbarButtonID = 16;
 else if (sel.options[18].selected)
 	showToolbarButtonID = 17;
 else if (sel.options[19].selected)
 	showToolbarButtonID = 18;
 else if (sel.options[20].selected)
 	showToolbarButtonID = 19;	
 else if (sel.options[21].selected)
 	showToolbarButtonID = 20;
 else if (sel.options[22].selected)
 	showToolbarButtonID = 21;	 		 	
 else if (sel.options[23].selected)
 	showToolbarButtonID = 22;	 		 	 	
}

function myHideButton()
{	
	alert(showToolbarButtonID);
	RQViewer.ShowToolBarButton(showToolbarButtonID,false);
}

function myShowButton()
{
	RQViewer.ShowToolBarButton(showToolbarButtonID,true);
}

function sel_color(sel){
 if (sel.options[1].selected)
   RQViewer.BackColor = RGB(255,255,255);
 else if (sel.options[2].selected)
   RQViewer.BackColor = RGB(125,125,125);
 else if (sel.options[3].selected)
   RQViewer.BackColor = RGB(0,0,255);
 else if (sel.options[4].selected)
 	RQViewer.BackColor = RGB(255,0,0);
 else if (sel.options[5].selected)
 	RQViewer.BackColor = RGB(0,255,0);
 else if (sel.options[6].selected)
 	RQViewer.BackColor = RGB(0,0,0);
}

function myExcelButton()
{
	RQViewer.SendToExcel("",true,0,true);
}

function myExcelButton2()
{
	RQViewer.SendToExcelCS("",true,0,0,true);
}

function myPDFButton()
{
	RQViewer.SendToPDF("",true);
}

function myHWPButton()
{
	RQViewer.SendToHWP("",0,0,0,true);
}

function myGULButton()
{
	RQViewer.SendToGUL("",5,0,0,true);
}

function myRTFButton()
{
	RQViewer.SendToRTF("",1,0,0,true);
}

function mySaveButton1()
{
	RQViewer.SaveAsDialog("");
}

function mySaveButton2()
{
	RQViewer.SaveAsDialog("empty");
}

function mySaveButton3()
{
	RQViewer.SaveAsDialog("asdfasdfasdf");
}

function myPrintButton()
{
	RQViewer.PrintDialog("");
}

function updatetext(str)
{
	history += historycounter + ": ";
	history += str;
	document.myform2.txtarea.value = history;
	historycounter++;
}

function myDocBGColor(sel)
{
 if (sel.options[1].selected)
   RQViewer.SetDocBGColor(RGB(0,0,0));
 else if (sel.options[2].selected)
 	 RQViewer.SetDocBGColor(RGB(0,255,0));
 else if (sel.options[3].selected)
   RQViewer.SetDocBGColor(RGB(125,125,125));
 else if (sel.options[4].selected)
   RQViewer.SetDocBGColor(RGB(0,0,255));
 else if (sel.options[5].selected)
 	RQViewer.SetDocBGColor(RGB(255,0,0));
 else
  RQViewer.SetDocBGColor(RGB(255,255,255));
}

-->
</SCRIPT>

<script language="JavaScript" for="RQViewer" event="EmptyResultData()">
	updatetext("NoData\n");
</script>

<script language="JavaScript" for="RQViewer" event="EndRunReport()">
	updatetext("End Run Event\n");
</script>

<script language="JavaScript" for="RQViewer" event="Refresh()">
	updatetext("Refresh Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonRefreshClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("Refresh?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>

<!--
<script language="JavaScript" for="RQViewer" event="ButtonRefreshClickBefore(Cancel)">
	updatetext("Refresh Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonRefreshClickAfter()">
	updatetext("Refresh After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonStopClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("Stop?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>

<!--
<script language="JavaScript" for="RQViewer" event="ButtonStopClickBefore(Cancel)">
	updatetext("Stop Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonStopClickAfter()">
	updatetext("Stop After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonMoveNextPageClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("MoveNextPage?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>

<!--
<script language="JavaScript" for="RQViewer" event="ButtonMoveNextPageClickBefore(Cancel)">
	updatetext("MoveNextPage Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonMoveNextPageClickAfter()">
	updatetext("MoveNextPage After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonMovePrevPageClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("MovePrevPage?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>
<!--
<script language="JavaScript" for="RQViewer" event="ButtonMovePrevPageClickBefore(Cancel)">
	updatetext("MovePrevPage Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonMovePrevPageClickAfter()">
	updatetext("MovePrevPage After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonMoveFirstPageClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("MoveFirstPage?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>

<!--
<script language="JavaScript" for="RQViewer" event="ButtonMoveFirstPageClickBefore(Cancel)">
	updatetext("MoveFirstPage Before Event\n");
</script>
-->

<script language="JavaScript" for="RQViewer" event="ButtonMoveFirstPageClickAfter()">
	updatetext("MoveFirstPage After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonMoveLastPageClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("MoveLastPage?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>
<!--
<script language="JavaScript" for="RQViewer" event="ButtonMoveLastPageClickBefore(Cancel)">
	updatetext("MoveLastPage Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonMoveLastPageClickAfter()">
	updatetext("MoveLastPage After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonZoomOutClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("ZoomOut?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>
<!--
<script language="JavaScript" for="RQViewer" event="ButtonZoomOutClickBefore(Cancel)">
	updatetext("ZoomOut Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonZoomOutClickAfter()">
	updatetext("ZoomOut After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonZoomInClickBefore(Cancel)">
	Dim MyVar
	MyVar = MsgBox("ZoomIn?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>
<!--
<script language="JavaScript" for="RQViewer" event="ButtonZoomInClickBefore(Cancel)">
	updatetext("ZoomIn Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonZoomInClickAfter()">
	updatetext("ZoomIn After Event\n");
</script>

<script language="VBScript" for="RQViewer" event="ButtonSaveAsClickBefore(Cancel)">
	Dim MyVar
	//MyVar = MsgBox("SaveAs?",vbOKCancel)

	if ( MyVar = vbCancel) Then
		Cancel = True
	End iF

</script>

<!--
<script language="JavaScript" for="RQViewer" event="ButtonSaveAsClickBefore(Cancel)">
	updatetext("ResultSave Before Event\n");
</script>
-->
<script language="JavaScript" for="RQViewer" event="ButtonSaveAsClickAfter()">
	updatetext("ResultSave After Event\n");
</script>

<script SRC="./setup/rqviewer_api.js"></script>
<STYLE>

<STYLE>
	DIV { padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px;}
</STYLE>
</HEAD>
<BODY onload="OnRun();">
<script language=JavaScript>
  IE_Viewer_Change(800,400);
</script>
<DIV name=RQ_HyperLinkPost height=0 width=0>
<FORM id=RQ_HyperLink method=post name=RQ_HyperLink>
</FORM>
=======================================================================<BR>
 <form name="myform">
  <input type="button" name="btn1" value="close_report" onclick="myFunction();">
  <input type="button" name="btn2" value="stop"	onclick="myStop();">
  <input type="button" name="btn3" value="refresh" onclick="myRefresh();">
  <input type="button" name="btn4" value="|<-"	onclick="myfirstpage();">
  <input type="button" name="btn5" value="<-"	onclick="myprevpage();">
  <input type="button" name="btn6" value="->"	onclick="mynextpage();">
  <input type="button" name="btn7" value="->|"	onclick="mylastpage();">
  <input type="button" name="btn8" value="ZoomIn"	onclick="myZoomIn();">
  <input type="button" name="btn9" value="ZoomOut"	onclick="myZoomOut();">
  <BR><BR>
  <input type="button" name="btn10" value="Toolbar" onclick="myToolbar();">
  <input type="button" name="btn11" value="Statusbar" onclick="myStatusbar();">

	<select name="myselect" onchange="sel_item(myform.myselect)">
		<option>Button Select
		<option>Save
		<option>----
		<option>Print
		<option>Preview
		<option>----
		<option>Stop
		<option>Refresh
		<option>----
		<option>FirstPage
		<option>PrevPage
		<option>CurrentPage
		<option>NextPage
		<option>LastPage
		<option>----
		<option>ZoomRatio
		<option>----
		<option>ExcelExport
		<option>ExcelExtExport
		<option>PDFExport
		<option>HWPExport
		<option>GULExport
		<option>RTFExport
		<option>Help
	</select>
<INPUT type="button" value="Hidden" onclick="myHideButton();">
<INPUT type="button" value="Show" onclick="myShowButton();">
  	<select name="colorselect" onchange="sel_color(myform.colorselect)">
		<option>BackColor Select
		<option>White
		<option>Gray
		<option>Blue
		<option>Red
		<option>Green
		<option>Black
	</select>
  <BR><BR>
<INPUT type="button" value="Excel" onclick="myExcelButton();">
<INPUT type="button" value="ExcelCS" onclick="myExcelButton2();">
<INPUT type="button" value="PDF" onclick="myPDFButton();">
<INPUT type="button" value="HWP" onclick="myHWPButton();">
<INPUT type="button" value="GUL" onclick="myGULButton();">
<INPUT type="button" value="RTF" onclick="myRTFButton();">
<INPUT type="button" value="Save_none" onclick="mySaveButton1();">
<INPUT type="button" value="Save_empty" onclick="mySaveButton2();">
<INPUT type="button" value="Save_usedocname" onclick="mySaveButton3();">
<INPUT type="button" value="Print" onclick="myPrintButton();">


<select name="bgcolor" onchange="myDocBGColor(myform.bgcolor)">
		<option>DocBGColor Select
		<option>Black
		<option>Green
		<option>Gray
		<option>Blue
		<option>Red
		<option>White
	</select>
  <BR><BR>

 </form>
<form name="myform2">
	<input type="hidden" name="history" value=""/>
<!--	<input type="textarea" name="txtarea" rows="5" cols="50"/>  -->
	<TEXTAREA name="txtarea" rows="5" cols="50"></TEXTAREA>
</form>
=======================================================================<BR>

</DIV>
</BODY>
</HTML>