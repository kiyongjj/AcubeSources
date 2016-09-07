<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.util.RequbeUtil" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%@ taglib uri="/WEB-INF/tld/RQPage.tld" prefix="page" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<%
String strPathIs = "";
String searchword = "";
String strAuth = "";
String order_flag = request.getParameter("order_flag") == null ? "5":request.getParameter("order_flag");

UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
}
Environment env = Environment.getInstance();
String lm_serverCharset = env.rqreport_server_charset;
String lm_RQCharset = env.rqreport_server_RQcharset;
if(request.getParameter("strPathIs") != null && !request.getParameter("strPathIs").equals("")){
   	strPathIs = request.getParameter("strPathIs");
	strPathIs = Encoding.chCharset(strPathIs, lm_serverCharset, lm_RQCharset);
}else{
	strPathIs = "/";
}

if(request.getParameter("searchword") != null && !request.getParameter("searchword").equals("")){
	searchword = request.getParameter("searchword");
	searchword = Encoding.chCharset(searchword, lm_serverCharset, lm_RQCharset);
}

DocRepository docRep = new DocRepository();
//docRep.listAll();
docRep.listDoc(strPathIs, searchword, order_flag);
ArrayList list = (ArrayList)docRep.getList();
DocInfo di = null;

//paging
String strCurrentPage = "1";
if(request.getParameter("strCurrentPage") != null && !request.getParameter("strCurrentPage").equals("")) {
	strCurrentPage = request.getParameter("strCurrentPage");
}else{
	strCurrentPage = "1";
}


int iCurrentPage = Integer.parseInt(strCurrentPage);

//int iListCount = 10;
int iListCount = env.rqreport_document_list_count;
int iPageCount = 10;
int i = 0,  j = 0;

ArrayList oResultArr = new ArrayList();
Iterator it = list.iterator();
if (iListCount != list.size()){
	while(it.hasNext()){
		i++;
		DocInfo lm_di = (DocInfo)it.next();
		if(  i > (iCurrentPage-1)*iListCount && i <= iCurrentPage*iListCount ){
			j++;
			oResultArr.add(lm_di);
		}
		if(j==iListCount) break;
	}
	if(oResultArr.size() == 0){
		Iterator it_b = list.iterator();
		iCurrentPage = iCurrentPage - 1;
		int k = 0;
		int l = 0;
		while(it_b.hasNext()){
			k++;
			DocInfo lm_di_b = (DocInfo) it_b.next();
			if( k > (iCurrentPage - 1) * iListCount && k <= iCurrentPage*iListCount){
				l++;
				oResultArr.add(lm_di_b);
			}
			if(l==iListCount) break;
		}
	}
}else{
	oResultArr = list;
}

/*file history use*/
String his_flag = env.rqxhistory;

String contextname = request.getRequestURI();
contextname = contextname.substring(0, contextname.indexOf("/document"));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<style type="text/css">
table.doclisttable tr.marked {background-color: #FFFFAA;}
</style>
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="javascript" src="../setup/HttpRequest.js"></script>
<script language="javascript" src="../setup/tableRowCheckboxToggle.js"></script>
<script language="JavaScript" type="text/JavaScript">
var rqdocument = {}
rqdocument.documentPage = function(){
	filemap = null;
	this.nowNode = null;
	var browsername = navigator.appName;
	IE = "Microsoft Internet Explorer";
	
	if (browsername==IE) {
		isIE = 1;
	}else {
		isIE = 0;
	}
}
rqdocument.documentPage.prototype = {
	TrColor : function(element){
		if(element.checked) {
			this.changeBgColor(element);
		}else{
			this.rollbakcBgColor(element);
		}
	},
	changeBgColor : function(element){
		if (isIE) {
			while (element.tagName!="TR") {
				element = element.parentElement;
				//alert('ljg');
			}
	
		} else {
			while (element.tagName!="TR") {
				element = element.parentNode;
			}
		}
		//element.style.backgroundColor = "#FFFFAA";
	},
	rollbakcBgColor : function(element){
		if(isIE){
			while (element.tagName!="TR") {
				element = element.parentElement;
			}
		}else{
			while (element.tagName!="TR") {
				element = element.parentNode;
			}
		}
		if(element.className == "even_row"){
			element.style.backgroundColor = "#F4F4F4"
		}else{
			element.style.backgroundColor = "#EBEBEB"
		}

	},
	mouseOver : function(element){
		if (element.childNodes[0].childNodes[0].checked){
			element.style.backgroundColor = "#EDEDE8";
		}else {
			element.style.backgroundColor = "#EDEDE8";
		}
	},
	mouseOut : function(element,vcolor){
		//alert(vcolor);
		if (!element.childNodes[0].childNodes[0].checked) {
			element.style.backgroundColor = vcolor;
		}else{
			element.style.backgroundColor = "#EDEDE8";
		}
	},
	openViewer : function(docName,winName,features){ //v2.0
		//enc_docName = encodeURI(docName);
		//var theURL = "/<%=contextname%>/rqviewer.jsp?doc=" + enc_docName;
	  	//window.open(theURL,winName,features);
	
	  	document.frmDirList.doc.value = docName;
	  	document.frmDirList.target = "_blank";
	  	//document.frmDirList.action = "<%=contextname%>/rqviewer_multi_reuse.jsp";
	  	if(browserName == "Internet Explorer"){
	  		document.frmDirList.action = "<%=contextname%>/rqviewer.jsp";
	  	}else{
	  		document.frmDirList.action = "<%=contextname%>/rqviewerapp.jsp";
	  	}
	  	
	  	document.frmDirList.submit();
	  	document.frmDirList.target = '_self';
		document.frmDirList.action = 'document.jsp';
	},
	openFunctionBr : function(theURL, winName, features){
		newf = window.open("",winName,features);
		newf.focus();
		newWnd = document.getElementById("newFolderNDoc");
		newWnd.target = winName;
		newWnd.action = theURL;
		newWnd.submit();	
	},
	viewDoc : function(fullPath){
		var fullPath = fullPath;
		$("#strPathIs_frmDirList").attr("value", fullPath);
		$("#strCurrentPage_frmDirList").attr("value", "1");
		$("#frmDirList").submit();
	},
	setDBid : function(theURL,winName,features){
		features = "width=404,height=490";
		forms = document.frmDirList;
		flag = false;
	
		for( i=0 ; i<forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkdoc') {
				if (forms.elements[i].checked == true) {
	
					lm_fname = forms.elements[i].value;
					var s = lm_fname.lastIndexOf('\/');
					var startidx = s+1;
					//alert(lm_fname.substring(startidx));
	
					if( (lm_fname.substring(startidx)) == ""){
						alert('<rqfmt:message strkey="document.document.alert.databasesettingdoc"/>');
						return;
					}
					flag = true;
					break;
				}
			}
		}
		if (flag == false) {
			alert('<rqfmt:message strkey="document.document.alert.selecttarget"/>');
			return;
		}else{
	
			forms.strPathIs.value = '<%=strPathIs%>';
	
			dbwinobj = window.open("",winName,features);
			dbwinobj.focus();
			document.frmDirList.target = "dbwindowobj";
			document.frmDirList.action = theURL;
			document.frmDirList.submit();
			document.frmDirList.target = '_self';
			document.frmDirList.action = 'document.jsp';
	
		}
	},
	go : function(page, fullPath){
		var fullPath = fullPath;
		$("#frmDirList #strPathIs_frmDirList").attr("value", fullPath);
		$("#frmDirList #strCurrentPage_frmDirList").attr("value", page);
		$("#frmDirList").submit();
	},
	selectchkalldoc : function() {
	
		forms = document.frmDirList;
		flag = true;
	
		for( i=0; i<forms.elements.length; i++) {
			if (forms.elements[i].name=='chkdoc') {
				if (forms.elements[i].checked == false) {
					flag = false;
					break;
				}
			}
		}
	
		if (flag == false) {
			for( i=0 ; i<forms.elements.length ; i++) {
				if (forms.elements[i].name=='chkdoc') {
					forms.elements[i].checked = true;
					//this.TrColor(forms.elements[i]);
				}
			}
		}
		else {
			for( i=0; i<forms.elements.length; i++) {
				if (forms.elements[i].name=='chkdoc') {
					forms.elements[i].checked = false;
					//this.TrColor(forms.elements[i]);
				}
			}
		}
	},
	handlerdoc : function(mode){
	
		forms = document.frmDirList;
		forms.modetype.value = mode;
		forms.strPathIs.value = '<%=strPathIs%>';
		forms.action='../common/handler.jsp';
	
		flag = false;
	
		for( i=0 ; i<forms.elements.length ; i++) {
			if (forms.elements[i].name=='chkdoc') {
				if (forms.elements[i].checked == true) {
					flag = true;
					break;
				}
			}
		}
	
		if (flag == false) {
			alert('<rqfmt:message strkey="document.document.alert.selecttarget"/>');
			return;
		}else {
			if ( confirm('<rqfmt:message strkey="document.document.confirm.delete"/>') ) {
				document.frmDirList.submit();
			}
		}
	},
	searchFn : function(){
		document.shwList.strPathIs.value = '<%=strPathIs%>';
		document.shwList.strCurrentPage.value = '1';
		//alert(document.shwList.searchword.value);
		document.shwList.submit();
	},
	on_reload : function(flag){
		//alert(document.frmDirList.strPathIs.value);
		document.frmDirList.order_flag.value = flag;
		document.frmDirList.action="document.jsp";
		document.frmDirList.target="_self";
		document.frmDirList.submit();
	},
	logoutCfm : function(){
		if(confirm('<rqfmt:message strkey="common.logout.alert"/>')){
			document.location.href="../common/sessionPrc.jsp?mode=sessionLogOut";
		}else{
			return;
		}
	},	
	bluring : function(){
		if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG") document.body.focus();
	},
	getFileList : function(filename, p_node){
		var params = "pathis="+encodeURIComponent("<%=strPathIs%>")+"&filename="+encodeURIComponent(filename);
		//url, params, callback, method
		XHR.sendRequest("getFileList.jsp", params, this.listFileList, "POST");
	},
	listFileList : function() {
		//alert(param);
		if (XHR.httpRequest.readyState == 4) {
			if (XHR.httpRequest.status == 200) {
				filemap = new Array();
				var resultText = XHR.httpRequest.responseText;
				//resultText = resultText.replace(/\s/g,"");
				var result = resultText.split('|');
				var count = parseInt(result[0]);
	
				if(count > 0){
					pairFile = result[1].split("\t");
					for(i = 0 ; i < pairFile.length ; i++){
						pairFile_sub = pairFile[i].split(",");
						var order = pairFile_sub[0];
						var modiday = pairFile_sub[1];
						filemap[order] = modiday;
					}
				}
				documentPage.drawhistory(); // this.drawhistory() (x)
			} else {
				//alert("error: "+httpRequest.status);
			}
		}
	},
	//test func
	showFileMap : function(){
		for (key in filemap) {
			alert(key + " : " + filemap[key]);
		}
	},
	setFileMapnull : function(){
		filemap = null;
	},
	showhistory : function(p_node){
		this.nowNode = p_node; // nowNode remember
		var filename = p_node.getAttribute("filename");
		this.getFileList(filename, p_node); //XHR
		
	},
	drawhistory : function(){
		p_node = this.nowNode;
		var filename = p_node.getAttribute("filename");
		//this.showFileMap(); //--> check point !!
		var pNode = p_node.parentNode;
		var pNode_next = pNode.nextSibling; //underline
		var pNode_next_next = pNode_next.nextSibling; //underline next node
		
		var now_version = p_node.getAttribute("nowversion");
		var showhidden = p_node.getAttribute("showhidden");
		inow_version = eval(now_version);
		
		var img_tag = $(p_node).find("img");
		img_tag.removeAttr("src");
		img_tag.attr("src","../img/hidden_history.gif");
		
		if(showhidden == "show"){
			//alert("show");
			//show 일때 pNode_next_next 는 맨처음 만들어진 것들이다.
			var underline = pNode.nextSibling; //underline
			var previousNodeMade = underline.nextSibling; //previousNodeMade
			var underline_of_previousNodeMade = previousNodeMade.nextSibling; //previousNodeMade
	
			pNode_next.parentNode.removeChild(previousNodeMade); //전에 만들어진 노드
			pNode_next.parentNode.removeChild(underline_of_previousNodeMade); //위 노드의 underline
			
			img_tag.removeAttr("src");
			img_tag.attr("src","../img/show_history.gif");

			p_node.setAttribute("showhidden","hidden");
			return;
		}
	
		var tr1 = document.createElement("tr");
		tr1.setAttribute("height","5");
	
		var td1 = document.createElement("td");
		td1.setAttribute("class","text");
		td1.setAttribute("colSpan","15");
		td1.setAttribute("align","right");
		td1.setAttribute("width","718");
	
		var table1 = document.createElement("table");
		table1.setAttribute("cellpadding","0");
		table1.setAttribute("cellspacing","0");
		table1.setAttribute("width","100%");
		table1.setAttribute("border","0");
	
		var tbody1 = document.createElement("tbody");
		var tr2 = document.createElement("tr");
	
		if(filemap == null || filemap.length == 0){
	
			var td2 = document.createElement("td");
			td2.setAttribute("align","right");
			td2.setAttribute("height","26");
			var td2_text = document.createTextNode('<rqfmt:message strkey="document.document.rollback.norollbackfile"/>');
			td2.appendChild(td2_text);
			tr2.appendChild(td2);
	
		}else{
	
			//td2
			var td2 = document.createElement("td");
			td2.setAttribute("align","right");
			var td2_text = document.createTextNode("<rqfmt:message strkey='document.document.rollback.fileversion'/>" + " : ");
			td2.appendChild(td2_text);
			tr2.appendChild(td2);
	
			//td3
			var td3 = document.createElement("td");
			td3.setAttribute("width","40");
			var sel1 = document.createElement("select");
			sel1.setAttribute("size","1");
			sel1.setAttribute("name",filename + "_history");
			sel1.setAttribute("id",  filename + "_history");
			sel1.style.width = "50px";
			sel1.onchange = function(){ documentPage.sel1_changeOption(this, filename); }; //this ==> select
	
			sel1.style.textAlign = "right";
		  	sel1.style.fontFamily = "verdana";
	
			lm_option = new Option();
			lm_text_option = new Array();
	
			//maxkey
			maxkey = 0;
			for (eachkey in filemap) {
				if(eachkey > maxkey) maxkey = eachkey;
			}
	
			//option create
			//loop
			for (key in filemap) {
				lm_option[key] = document.createElement("option");
				if(key == maxkey){
					lm_option[key].setAttribute("selected","selected");
				}
				lm_option[key].setAttribute("value",filemap[key]);
				lm_text_option[key] = document.createTextNode(key);
				lm_option[key].appendChild(lm_text_option[key]);
				sel1.appendChild(lm_option[key]);
			}
			//loop-end
	
			td3.appendChild(sel1);
			tr2.appendChild(td3);
	
			//td4
			var td4 = document.createElement("td");
			td4.setAttribute("width","56");
			var td4_text = document.createTextNode("<rqfmt:message strkey='document.document.rollback.modifyday'/>" + " : ");
			td4.appendChild(td4_text);
			tr2.appendChild(td4);
	
			//td5
			var td5 = document.createElement("td");
			td5.setAttribute("width","130");
			var input_modi = document.createElement("input");
			input_modi.setAttribute("name",filename + "_modifyday");
			input_modi.setAttribute("id"  ,filename + "_modifyday");
			input_modi.setAttribute("disabled","disabled");
	
			if(filemap.length > 0){
				input_modi.setAttribute("value",filemap[maxkey]);
			}else{
				input_modi.setAttribute("value","");
			}
	
			//input_modi.setAttribute("style","text-align:right;font-family:verdana;");
			input_modi.style.textAlign = "right";
		  	input_modi.style.fontFamily = "verdana";
	
			input_modi.setAttribute("size","16");
			td5.appendChild(input_modi);
			tr2.appendChild(td5);
	
			//td6
			var td6 = document.createElement("td");
			td6.setAttribute("width","50");
			var button_img = document.createElement("img");
			button_img.setAttribute("src","../img/rq_l_modify.jpg");
			button_img.style.cursor = "pointer";
			button_img.onclick = function(){ documentPage.filerollback(filename); };
			td6.appendChild(button_img);
			tr2.appendChild(td6);
		}
		//tr2 combine
		tbody1.appendChild(tr2);
		table1.appendChild(tbody1);
		td1.appendChild(table1);
		tr1.appendChild(td1);
	
		pNode.parentNode.insertBefore(tr1,pNode_next_next);
	
		//underline
		var tr_line = document.createElement("tr");
		var td_line = document.createElement("td");
		td_line.setAttribute("height","1");
		td_line.setAttribute("colSpan","15");
		td_line.setAttribute("bgColor","#E6E6E6");
	
		tr_line.appendChild(td_line);
	
		pNode.parentNode.insertBefore(tr_line,pNode_next_next);
	
		//alert(pNode.parentNode.innerHTML); ---> check point !!!
		//alert(document.body.innerHTML);
	
		p_node.setAttribute("showhidden","show");
	},
	sel1_changeOption : function (sel, filename){
		var modifyday = document.getElementById(filename + "_modifyday");
		//alert(sel.options.length);
		for(i = 0 ; i < sel.options.length ; i++){
			if(sel.options[i].selected == true){
				//alert(sel.options[i].value);
				modifyday.setAttribute("value",sel.options[i].value);
			}
		}
	},
	filerollback : function (filename){
		var sel = document.getElementById(filename + "_history");
		var version = -1;
		var lm_day = "";
		for(i = 0 ; i < sel.options.length ; i++){
			if(sel.options[i].selected == true){
				version = sel.options[i].text;
				lm_day  = sel.options[i].value;
			}
		}
		if(version == -1){
		 	alert("<rqfmt:message strkey='document.document.rollback.confirm.rollback7'/>");
			return;
		}
		lm_day = $.trim(lm_day);
	
		msg =  "<rqfmt:message strkey='document.document.rollback.confirm.rollback'/>" + " (" + version + ") " + "<rqfmt:message strkey='document.document.rollback.confirm.rollback2'/>";
		msg += "\n" + "<rqfmt:message strkey='document.document.rollback.confirm.rollback3'/>" + " (" + version + ") " + "<rqfmt:message strkey='document.document.rollback.confirm.rollback4'/>";
		msg += "\n\n" + "<rqfmt:message strkey='document.document.rollback.confirm.rollback5'/>" + " : " + version;
		msg += "\n" + "<rqfmt:message strkey='document.document.rollback.confirm.rollback6'/>" + " : " + lm_day;
		msg += "\n\n" + "<rqfmt:message strkey='document.document.rollback.confirm.rollback7'/>";
	
		if(!confirm(msg)) return;
	
		document.rollbackfile.mode.value = "rollbackfile";
		document.rollbackfile.filename.value = filename;
		document.rollbackfile.version.value = version;
		document.rollbackfile.modiday.value = lm_day;
	
		document.rollbackfile.method = "post";
		document.rollbackfile.action = "../common/handler.jsp";
		document.rollbackfile.submit();
	},
	documentInit : function(){
		// css //////////////////////////////////////////////////////////////////////////////////
		$("#setDB1, #setDB2, #delbtn1, #delbtn2, #search_nm, #logout").css("cursor","pointer");
		
		$("#doclisttable").find("#doclistrow").each(function(i, el){
			//if(i % 2 == 0){
			//	$(el).css("background-color","#EDEDE8");
			//}
		});
		// event helpers for cross browser /////////////////////////////////////////////////////
		$("#setDB1").click(function(){ 
			documentPage.setDBid('../db/dbconfig.jsp','dbwindowobj',''); 
		});
		$("#setDB2").click(function(){ 
			documentPage.setDBid('../db/dbconfig.jsp','dbwindowobj',''); 
		});
		$("#delbtn1").click(function(){ 
			documentPage.handlerdoc('del'); 
		});
		$("#delbtn2").click(function(){ 
			documentPage.handlerdoc('del'); 
		});
		// search
		$("#search_nm").click(function(){ 
			documentPage.searchFn(); 
		});
		// move up folder
		$("#upFolder").click(function(){ 
			var pPath = $("#upFolder").attr("value");
			documentPage.viewDoc(pPath); 
		});
		// change directory 
		$("a").click(function(){ 
			var aindex = "a:eq(" + $("a").index(this) + ")"; 
			var name_value = $(aindex).attr("name");
			if(name_value == "viewfolder"){
				var path_value = $(aindex).attr("value");
				documentPage.viewDoc(path_value);
			// page Navigation
			}else if(name_value == "gopage"){
				var page_value = $(aindex).attr("pagevalue");
				var path_value = $(aindex).attr("pathvalue");
				documentPage.go(page_value,path_value);
			}
			
		});
		// logout
		$("#logout").click(function(){
			documentPage.logoutCfm();
		});
		// new folder click event and css
		$("#newfolder").css("cursor","pointer").click(function(){
			documentPage.openFunctionBr('new_folder.jsp','newFolder','width=360,height=165');
		});
		// new Document click event and css
		$("#newDocument").css("cursor","pointer").click(function(){
			documentPage.openFunctionBr('new_document.jsp','newDoc','width=397,height=260');
		});
		
	}
}

documentPage = new rqdocument.documentPage();
gfunc = new rqglobalfunc.rqglobal();
XHR = new xhr.Request();
/////// The function to execute when the DOM is ready. ///////////////////////
$(function(){
	documentPage.documentInit();
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="F8F8F8" class="main_bg">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<!--top 시작 -->
    	<table width="1004" border="0" cellpadding="0" cellspacing="0" >
        <tr>
        	<td align="left" valign="top" width="465" height="151" class="main_lbg" >
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td style="padding-left:33px;padding-top:35px;"><img src="../img/main_logo.gif" width="290" height="31" border="0" alt=""></td>
				</tr>
				</table>
			</td>
		  	<td height="151"><IMG SRC="../img/main_bimg01.gif" width="539" height="151" border="0" alt=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><span id="logout" rqtype="link"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></span></td>
			</tr>
			</table>
		</div>
      	<!--top 끝 -->
    </td>
</tr>
<tr>
	<td>

		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td width="217" valign="top" class="main_menubgb">
				<!-- menu -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td height="578" class="main_menubg" valign="top">
						<table width="217" height="" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="16"></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../document/document.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.document.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="folder"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../usergroup/user.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="user"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../db/db.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="data"></a></td>
						</tr>
					<%
						if( strAuth.equals("A")){
					%>
						<tr>
							<td height="45" valign="top"><a href="../environment/environment.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="install"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/rqenv.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="environment"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/logman.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="logman"></a></td>
						</tr>
						<%
							String isUse_schedule = "false";
							if(oUserModel != null){
								isUse_schedule = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "scheduler");
							}
							if(isUse_schedule.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../schedule/rqscheduletimelist.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="schedule"></a></td>
						</tr>
						<%
							}
							String isUse_statistics = "false";
							if(oUserModel != null){
								isUse_statistics = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "RQStatistics");
							}
							if(isUse_statistics.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../environment/docstat.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="statistics"></a></td>
						</tr>
					<%
							}
						}
					%>
				  		</table>
					</td>
				</tr>
				<tr>
					<td height="100%" class="main_menubgb"></td>
				</tr>
			  	</table>
			  	<!-- menu	끝-->
			</td>
			<td width="787" valign="top" class="main_conbg">
				<!--contents 시작 -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="middle">&nbsp;&nbsp;<span class="comment_text" ><rqfmt:message strkey="document.document.title"/></span></td>
				</tr>
				<tr>
					<td  height="24" valign="top" style="padding-left:23px;">
						<!-- 네비게이션 시작-->
						<table width="730" height="24" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="10" height="24"><IMG SRC="../img/main_folder_l.gif" width="10" height="24" border="0" alt=""></td>
							<td width="710" height="24" class="n_navi"><rqfmt:message strkey="document.document.foldernavi"/> :
								<page:showPath action="folderpath" strPathIs="<%=strPathIs%>"/>
							</td>
							<td width="10" height="24"><IMG SRC="../img/main_folder_r.gif" width="10" height="24" border="0" alt=""></td>
						</tr>
						</table>
						<!-- 네비게이션 끝-->
					</td>
				</tr>
				<tr>
					<td  height="12"></td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:23px;" >
						<table width="730"  border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr><td height="7"></td></tr>
								<tr>
									<td>
										<table cellspacing="0" cellpadding="0" border="0" width="100%">
										<tr>
											<td>
												<table align="left" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td>
														<table border="0" align="left" cellpadding="0" cellspacing="0">
														<tr>
															<td><span id="setDB1" rqtype="link" class="btn_white"><img src="../img/<rqfmt:message strkey='document.document.img.databasesetting'/>" width="119" height="22" border="0" alt="databasesetting"></span></td>
														</tr>
														</table>
													</td>
													<td width="7"> </td>
													<td>
														<table border="0" align="left" cellpadding="0" cellspacing="0">
														<tr>
															<td><span id="delbtn1" rqtype="link" class="btn_white"><img src="../img/<rqfmt:message strkey='document.document.img.delete'/>" width="47" height="22" border="0" alt="delete"></span></td>
														</tr>
														</table>
													</td>
												</tr>
												</table>
											</td>
											<td align="right" style="padding-right: 7px;">
												<form name="shwList" id="shwList" method="post" action="document.jsp">
												<table cellpadding="0" cellspacing="0" border="0" width="100%">
												<tr>
													<td align="right"><span class="stitle_ser"><img src="../img/bullet_title_01.gif" width="24" height="9"/><rqfmt:message strkey="document.document.namesearch"/> : </span></td>
													<td width="115"><input type="text" name="searchword" id="searchword" size="12" value="<%=searchword%>"/><input type="text" style="width:0; visibility:hidden;"></td>
													<td width="50">
														<table border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td><img src="../img/btn_g_left2.gif"></td>
															<td background="../img/btn_g_bg.gif" class="btn_text_td"><span id="search_nm" rqtype="link" class="btn_green"><rqfmt:message strkey="document.document.find"/></span></td>
															<td><img src="../img/btn_g_right.gif"></td>
														</tr>
														</table>
													</td>
												</tr>
												</table>
												<input type="hidden" name="strPathIs" id="strPathIs_shwList"/>
												<input type="hidden" name="strCurrentPage" id="strCurrentPage_shwList"/>
												</form>
											</td>
										</tr>
										</table>

									</td>
								</tr>
								<tr><td height="14"></td></tr>
								<tr>
									<td>
										<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td align="center">

												<!-- frmDirList -->
												<form name="frmDirList" id="frmDirList" method="post" action="document.jsp">
												<input type="hidden" name="order_flag" id="order_flag" value="<%=order_flag%>">
												<table width="100%" border="0" cellspacing="0" cellpadding="0"  id="doclisttable" class="doclisttable">
								  				<tr>
													<td height="2" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#A8CC72"></td>
								  				</tr>
								  				<tr class="table_title">
													<td width="50" height="26" class="stitle">
														<input type="checkbox" name="chkalldoc" id="chkalldoc" value="checkbox" onClick="documentPage.selectchkalldoc()" onfocus="this.blur()">
													</td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="table_title"><a href="javascript:documentPage.on_reload(1)" class="table_title"><rqfmt:message strkey="document.document.classification"/></a></td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="table_title"><a href="javascript:documentPage.on_reload(2)" class="table_title"><rqfmt:message strkey="document.document.name"/></a></td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="table_title"><rqfmt:message strkey="document.document.databaseid"/></td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
														<td></td>
														</tr>
														</table>
													</td>
													<td class="table_title"><rqfmt:message strkey="document.document.description"/></td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="stitle"><a href="javascript:documentPage.on_reload(3)" class="table_title"><rqfmt:message strkey="document.document.creator"/></a></td>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="stitle"><a href="javascript:documentPage.on_reload(4)" class="table_title"><rqfmt:message strkey="document.document.editdate"/></a></td>
												<%if(his_flag.equalsIgnoreCase("yes")){%>
													<td valign="bottom" width="1" class="stitle">
														<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td></td>
														</tr>
														</table>
													</td>
													<td class="table_title" width="35"><rqfmt:message strkey="document.document.version"/></td>
												<%}%>
												</tr>
												<tr>
													<td height="1" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#A8CC72"></td>
												</tr>
											<%
												String pPath = "";
												if( pageContext.getAttribute("parentPath") != null ){
													pPath = (String) pageContext.getAttribute("parentPath");
												}else{
													pPath = "";
												}
											%>
												<tr style="cursor:pointer;" id="upFolder" value="<%=pPath%>">
													<td class="text"><input type="checkbox" name="dummy" id="dummy" disabled="disabled"/></td>
													<td width="1"></td>
													<td class="text">
														<span style="cursor:pointer;"><img src="../img/up_arrow.gif" width="15" height="19" border="0"/></span>
													</td>
													<td width="1"></td>
													<td class="text">
														<b>..</b>
													</td>
													<td width="1"></td>
													<td class="text"></td>
													<td width="1"></td>
													<td class="text"></td>
													<td width="1"></td>
													<td class="text"></td>
													<td width="1"></td>
													<td class="text"></td>
												<%if(his_flag.equalsIgnoreCase("yes")){%>
													<td width="1"></td>
													<td class="text"></td>
												<%}%>
												</tr>
												<tr>
													<td height="1" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#E6E6E6"></td>
												</tr>
									<%
										if(oResultArr.size() != 0){

											int iColor = 1;
											for(int k = 0; k <oResultArr.size(); k++) {
												di = (DocInfo)oResultArr.get(k);
												String lm_ext = (di.name).substring((di.name).indexOf(".")+1, (di.name).length());

									%>
												<tr id="doclistrow" value="<%=iColor%>" class="<%= iColor % 2 == 0 ? "even_row" : "odd_row"%>">
													<td class="text">

														<input type="checkbox" name="chkdoc" id="chkdoc" value="<%=di.fullPath%>" onfocus="this.blur()">

													</td>
													<td width="1"></td>
													<td class="text">
												<%
													String strFg = "" + di.doc_fg;
													if(strFg.equals("F")){
												%>
														<img src="../img/icon_folder.gif" width="15" height="12">
												<%
													}else if(strFg.equals("D")){
												%>
														<img src="../img/icon_docu.gif" width="12" height="13" border="0" alt="">
												<%
													}
												%>
													</td>
													<td width="1"></td>
													<td class="text" style="word-break:break-all;" width="155">
												<%if(strFg.equals("F")){ %>
														<a href="#" style="cursor:pointer;" class="filename" name="viewfolder" value="<%=di.fullPath%>">
															<%= di.name %>
														</a>
												<%}else{%>
													<%
														if(lm_ext.equalsIgnoreCase("rqx")){
													%>
														<a href="javascript:documentPage.openViewer('<%=di.fullPath%>','','');" name="openViewer" class="filename">
															<%= di.name %>
														</a>
													<%
														}else{
													%>
														<a href="javascript:documentPage.openViewer('<%=di.fullPath%>','','');" name="openViewer" class="filename" style="color:olive;font-style:italic;">
															<%= di.name %>
														</a>
													<%
														}
													%>

												<%}%>
													</td>

													<td width="1"></td>
													<td class="text">
												<%
													int res = docRep.getDSList(di.fullPath);
													if(res == 0){
														List dslist = docRep.getList();
														int size = dslist.size();
														String[] dsNames = new String[size];
														for(int ds = 0; ds < size; ds++) {
															dsNames[ds] = ((DSListInfo)dslist.get(ds)).getParamString(1);
															out.println(dsNames[ds] != null && !dsNames[ds].equals("") && !dsNames[ds].equals("null") ? dsNames[ds] : "" );
															if( ds < size-1 ) out.println(",");
														}
													}
												%>
													</td>
													<td width="1"></td>
													<td class="text" style="font-family:verdana; word-break:break-all;" width="110"><%= di.docDesc != null && !(di.docDesc).equals("") ? di.docDesc : "" %></td>
													<td width="1"></td>
													<td class="text" style="font-family:verdana;"><%= di.createUserID %></td>
													<td width="1"></td>
													<td class="text" style="font-family:verdana;">
												<%
													String strdate = RequbeUtil.makeDateString(di.modDate,"%Y%M%D%H%m%S");
													String new_strdate = strdate.substring(0,4) +"-"+ strdate.substring(4,6) +"-"+ strdate.substring(6,8) +" "+ strdate.substring(8,10) +":"+ strdate.substring(10,12);
													out.println(new_strdate);
												%>
													</td>
											<%
												if(strFg.equals("F")){
													if(his_flag.equalsIgnoreCase("yes")){
											%>
													<td width="1"></td>
													<td width="35"></td>
											<%
													}
											  	}else{
										  			if(his_flag.equalsIgnoreCase("yes")){
											%>
													<td width="1"></td>
													<td nowversion="<%=di.file_version%>" showhidden="hidden" filename="<%=di.name%>" id="showhistorytd" onClick="documentPage.showhistory(this)">
														<table cellpadding="1" cellspacing="1" border="0" width="35" height="20" style="border: solid 1px #B0AC00;">
														<tr>
															<td bgcolor="#FFFFFF" style="font-family:verdana;cursor:pointer;">
																<img id="img_shwhis" src="../img/show_history.gif"/>
																<%=di.file_version%>
															</td>
														</tr>
														</table>
													</td>
											<%
													}
											  	}
											%>
												</tr>
												<tr>
													<td height="1" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#E6E6E6"></td>
												</tr>
									<%
												iColor++;
											}
										}else{
											if(!searchword.equals("") && searchword != null){
												%>
												<tr bgcolor="#F9F9F9">
													<td class="text">
														<input type="checkbox" disabled="disabled">
													</td>
													<td width="1"></td>
													<td class="text">
													</td>
													<td width="1"></td>
													<td class="text" style="word-break:break-all;"></td>
													<td width="1"></td>
													<td class="text"><rqfmt:message strkey="document.document.msg.nodata"/></td>
													<td width="1"></td>
													<td class="text" style="verdana; word-break:break-all;" width="110"></td>
													<td width="1"></td>
													<td class="text"></td>
													<td width="1"></td>
													<td class="text"></td>
												<%if(his_flag.equalsIgnoreCase("yes")){%>
													<td width="1"></td>
													<td class="text"></td>
												<%}%>
												</tr>
												<tr>
													<td height="1" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#E6E6E6"></td>
												</tr>
												<%
											}
										}
									%>
												<tr>
													<td height="1" colspan="<%=his_flag.equalsIgnoreCase("yes") ? "15": "13"%>" bgcolor="#A8CC72"></td>
												</tr>
												</table>
												<input type="hidden" name="strPathIs" id="strPathIs_frmDirList" value="<%=strPathIs%>"/>
												<input type="hidden" name="modetype" id="modetype_frmDirList"/>
												<input type="hidden" name="strCurrentPage" id="strCurrentPage_frmDirList"/>
												<input type="hidden" name="doc" id="doc_frmDirList"/>
												</form>
												<!-- frmDirList end -->

											</td>
										</tr>
										<tr>
											<td height="15"></td>
										</tr>
										<tr>
											<td height="15" align="center">
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td height="1" bgcolor="F0F0F0"></td>
												</tr>
												<tr>
													<td height="24" align="center" bgcolor="FCFCFC">
														<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<!-- page navigation -->
																<page:pageNavigation action="pageNavi" list="<%=list%>" listCount="<%=iListCount %>" pageCount="<%=iPageCount %>"/>
															</td>
														</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td height="1" bgcolor="F0F0F0"></td>
												</tr>
												<tr>
													<td height="15"></td>
												</tr>
												<tr>
													<td height="20">
														<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<table align="left" cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																		<table border="0" align="left" cellpadding="0" cellspacing="0">
																		<tr>
																			<td><span id="setDB2" rqtype="link" class="btn_white"><img src="../img/<rqfmt:message strkey='document.document.img.databasesetting'/>" width="119" height="22" border="0" alt="database setting"></span></td>
																		</tr>
																		</table>
																	</td>
																	<td width="7"> </td>
																	<td>
																		<table border="0" align="left" cellpadding="0" cellspacing="0">
																		<tr>
																			<td><span id="delbtn2" rqtype="link" class="btn_white"><img src="../img/<rqfmt:message strkey='document.document.img.delete'/>" width="47" height="22" border="0" alt="delete"></span></td>
																		</tr>
																		</table>
																	</td>
																</tr>
																</table>
															</td>
															<td align="right">

																<!-- newFolder -->
																<form name="newFolderNDoc" id="newFolderNDoc" method="post">
																<table cellspacing="0" cellpadding="0" border="0">
																<tr>
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><img src="../img/<rqfmt:message strkey='document.document.img.newfolder'/>" width="125" height="30" border="0" alt="new Folder" id="newfolder"></td>
																		</tr>
																		</table>
																	</td>
																	<td width="3"> </td>
																	<td>
																		<table border="0" cellspacing="0" cellpadding="0">
																		<tr>
																			<td><img src="../img/<rqfmt:message strkey='document.document.img.newDocument'/>" width="125" height="30" border="0" alt="new Document" id="newDocument"></td>
																		</tr>
																		</table>
																	</td>
																</tr>
																</table>
																<input type="hidden" name="pathIs" value="<%=strPathIs%>"/>
																</form>
															</td>
														</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td height="19" colspan="3">&nbsp;</td>
												</tr>
												</table>
											</td>
										</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="33"></td>
								</tr>
								</table>
							</td>
							</tr>
						</table>
					</td>
				</tr>
				</table>
				<!--contents 끝 -->
			</td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="14" class="main_conbgb"></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="44" style="padding-left:39px;" valign="bottom"><IMG SRC="../img/copy_2.gif" width="382" height="8" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<form name="rollbackfile" id="rollbackfile">
<input type="hidden" name="pathIs" id="pathIs_rollbackfile" value="<%=strPathIs%>"/>
<input type="hidden" name="mode" id="mode_rollbackfile"/>
<input type="hidden" name="filename" id="filename_rollbackfile"/>
<input type="hidden" name="version" id="version_rollbackfile"/>
<input type="hidden" name="modiday" id="modiday_rollbackfile"/>
</form>
<br>
<br>
</body>
</html>
