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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>REQUBE REPORT</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">

<script type="text/javascript">
showToolbar=false;
showStatusbar=false;
showToolbarButtonID=0;
m_history="";
historycounter=0;
function init()
{	
	var path = "<%=basePath%>RQDataset.jsp"; 
	var docName = "<%=doc%>";
	var runvar = "<%=runvar%>";
	var oRQPlugin = document.getElementById('RQViewerPlugin');    

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
	
	oRQPlugin.SetRunmode(4); // RQRUNMODE_IEXPLORER (1), 
	                         // RQRUNMODE_PRESENTER (2), RQRUNMODE_PRESENTER_NM (3)
	                         // RQRUNMODE_PLUGIN (4), RQRUNMODE_PLUGIN_NM (5)
	oRQPlugin.Run();
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
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.CloseReport();
	oRQPlugin.SetBackColor(RGB(255,0,0));
}

function myRefresh()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');
	oRQPlugin.Refresh();
}

function myStop()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.Stop();
}

function myfirstpage()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.MoveFirstPage();
	var str = "CurPage : ";
	str += oRQPlugin.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += oRQPlugin.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function myprevpage()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.MovePrevPage();
	var str = "CurPage : ";
	str += oRQPlugin.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += oRQPlugin.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function mynextpage()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.MoveNextPage();
	var str = "CurPage : ";
	str += oRQPlugin.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += oRQPlugin.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function mylastpage()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.MoveLastPage();
	var str = "CurPage : ";
	str += oRQPlugin.GetCurrentPageNo()+1;
	str += ", TotalPage : ";
	str += oRQPlugin.GetTotalPageNo()+1;
	str += "\n";
	updatetext(str);
}

function myZoomIn()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.ZoomIn();
}

function myZoomOut()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.ZoomOut();
}

function RGB(r, g, b)
{
	return ( r | (g << 8) | ( b<<16) );
}

function myToolbar()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	if (showToolbar==true)
	{
	  oRQPlugin.SetToolBarVisible(true);
		showToolbar = false;
	}
	else
	{
	  oRQPlugin.SetToolBarVisible(false);
		showToolbar = true;
	}
}

function myStatusbar()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	if (showStatusbar==true)
	{
	  oRQPlugin.SetStatusBarVisible(true);
		showStatusbar = false;
	}
	else
	{
	  oRQPlugin.SetStatusBarVisible(false);
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
 	  showToolbarButtonID = 23;	 		 	 	
}

function myHideButton()
{	
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	//alert(showToolbarButtonID);
	oRQPlugin.ShowToolBarButton(showToolbarButtonID,false);
}

function myShowButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	//alert(showToolbarButtonID);
	oRQPlugin.ShowToolBarButton(showToolbarButtonID,true);
}

function sel_color(sel){
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
 if (sel.options[1].selected)
   oRQPlugin.SetBackColor(RGB(255,255,255));
 else if (sel.options[2].selected)
   oRQPlugin.SetBackColor(RGB(125,125,125));
 else if (sel.options[3].selected)
   oRQPlugin.SetBackColor(RGB(0,0,255));
 else if (sel.options[4].selected)
 	oRQPlugin.SetBackColor(RGB(255,0,0));
 else if (sel.options[5].selected)
 	oRQPlugin.SetBackColor(RGB(0,255,0));
 else if (sel.options[6].selected)
 	oRQPlugin.SetBackColor(RGB(0,0,0));
}

function myExcelButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToExcel("",true,0,true);
}

function myExcelButton2()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToExcelCS("",true,0,0,true);
}

function myPDFButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToPDF("",true);
}

function myHWPButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToHWP("",0,0,0,true);
}

function myGULButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToGUL("",5,0,0,true);
}

function myRTFButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SendToRTF("",1,0,0,true);
}

function mySaveButton1()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SaveAsDialog("");
}

function mySaveButton2()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SaveAsDialog("empty");
}

function mySaveButton3()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.SaveAsDialog("asdfasdfasdf");
}

function myPrintButton()
{
	var oRQPlugin = document.getElementById('RQViewerPlugin');  
	oRQPlugin.PrintDialog("");
}

function updatetext(str)
{
	//var oRQPlugin = document.getElementById('RQViewerPlugin'); 
	m_history = historycounter + ": ";
	m_history += str;
	
	var txtarea = document.getElementById('txtarea');
	txtarea.value += m_history;
	historycounter++;
}

function myDocBGColor(sel)
{
var oRQPlugin = document.getElementById('RQViewerPlugin'); 
 if (sel.options[1].selected)
   oRQPlugin.SetDocBGColor(RGB(0,0,0));
 else if (sel.options[2].selected)
 	 oRQPlugin.SetDocBGColor(RGB(0,255,0));
 else if (sel.options[3].selected)
   oRQPlugin.SetDocBGColor(RGB(125,125,125));
 else if (sel.options[4].selected)
   oRQPlugin.SetDocBGColor(RGB(0,0,255));
 else if (sel.options[5].selected)
 	oRQPlugin.SetDocBGColor(RGB(255,0,0));
 else
  oRQPlugin.SetDocBGColor(RGB(255,255,255));
}
</script>
</HEAD>

<BODY leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" onload="init()">
<!--h3> REQUBE Viewer Plugin Test </h3-->

<embed id='RQViewerPlugin' type='Application/RQPlugin' width=800 height=400></embed>

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
<form name="myform2" id="myform2">
	<input type="hidden" name="lm_history" id="lm_history" value=""/>
<!--	<input type="textarea" name="txtarea" rows="5" cols="50"/>  -->
	<TEXTAREA name="txtarea" id="txtarea" rows="5" cols="50"></TEXTAREA>
</form>
=======================================================================<BR>
</BODY>
</HTML>
