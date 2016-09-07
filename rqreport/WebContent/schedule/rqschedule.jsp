<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.repository.*"%>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<rquser:rqUser action="getUserList"/>
<%
Environment g_env = Environment.getInstance();
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
String scheduleid = request.getParameter("scheduleidx");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>REQUBE REPORT</title>
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=g_env.jsframework%>"></script>
<script language="javascript" src="../setup/HttpRequest.js"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="JavaScript" TYPE="text/javascript">
rqschedule = {}
rqschedule.schedule = function(){
	runvarmap = new Array();
	resultfilemap = new Array();
}
rqschedule.schedule.prototype = {
	MoveUp : function(sel){
	        i = sel.selectedIndex;
	        if (i > 0){
	                this.swap(sel , i , i-1 );
	                sel.options[i-1].selected = true;
	                sel.options[i].selected = false;
	        }
	},
	MoveDown : function(sel){
	        i = sel.selectedIndex;
	        if (i < sel.length-1 && i > -1){
	                this.swap(sel , i+1 , i);
	                sel.options[i+1].selected = true;
	                sel.options[i].selected = false;
	        }
	},
	swap : function(sel,index1, index2){
	        var savedValue = sel.options[index1].value;
	        var savedText = sel.options[index1].text;
	
	        sel.options[index1].value = sel.options[index2].value;
	        sel.options[index1].text = sel.options[index2].text;
	
	        sel.options[index2].value = savedValue;
	        sel.options[index2].text = savedText;
	},
	MoveToTop : function(sel){
	        i = sel.selectedIndex;
	        for ( ; i > 0 ; i--){
	                this.swap(sel , i , i-1);
	                sel.options[i-1].selected = true;
	                sel.options[i].selected = false;
	        }
	},
	MoveToBottom : function(sel){
	        i=sel.selectedIndex;
	        if (i>-1){
	                for (;i<sel.length-1;i++){
	                        this.swap(sel,i+1,i);
	                        sel.options[i+1].selected = true;
	                        sel.options[i].selected = false;
	                }
	        }
	},
	MoveElements : function(direction){
		if(direction == 'toRight'){
			Fromsel = document.getElementById('left_select');
			Tosel = document.getElementById('right_select');
			for (var i=0;i<Fromsel.options.length;i++){
				if (Fromsel.options[i].selected == true){
					//add
			    	var addtext = Fromsel.options[i].text;
			        var addvalue = Fromsel.options[i].value;
			       	//check item
			    	flag = this.checkitem(addtext, Tosel); // text check!!
			    	if(flag == false) continue;
			        Tosel.options[Tosel.options.length] = new Option(addtext, addvalue);
			        Fromsel.options[i].selected = false;
				}
			}
		}else if(direction == 'toLeft'){
			Fromsel = document.getElementById('right_select');
			Tosel = document.getElementById('left_select');
	
			var to_remove_counter=0;
		    for (var i=0 ; i < eval(Fromsel.options.length) ; i++){
	
				if (Fromsel.options[i].selected == true){
			    	Fromsel.options[i].selected=false;
			    	key = Fromsel.options[i].value;
			    	runvarmap[key] = null;
					resultfilemap[key] = null;
	   			    ++to_remove_counter;
				}else{
			        Fromsel.options[i-to_remove_counter].selected=false;
			        Fromsel.options[i-to_remove_counter].text=Fromsel.options[i].text;
			    	Fromsel.options[i-to_remove_counter].value=Fromsel.options[i].value;
		    	}
			}
			var numToLeave=Fromsel.options.length-to_remove_counter;
		    for (i = Fromsel.options.length-1 ; i >= numToLeave ; i--){
				Fromsel.options[i]=null;
			}
			//alert(	eval(Fromsel.options.length));
		}
	},
	SelectAll : function(sel){
		for (var i=0;i<sel.options.length;i++){
			sel.options[i].selected=true;
		}
	},
	//check item
	checkitem : function(addvalue, Tosel){
		var flag = true;
		for(var i=0;i<Tosel.options.length;i++){
			if(addvalue == Tosel.options[i].text){
				flag = false;
			}
		}
		return flag;
	},
	//test function
	addElementToleft_select : function(){
		var leftSelect = document.getElementById("left_select");
		var optEl = document.createElement("option");
		optEl.text = "test";
		optEl.value =  "test";
		//leftSelect.appendChild(optEl);	//mozilla
		leftSelect.add(optEl);	//ie
	},
	showTree : function(){
		var RQTree = document.getElementById("RQTree");
		if(RQTree.style.visibility == "visible"){
			RQTree.style.visibility = "hidden";
		}else if(RQTree.style.visibility == "hidden"){
			RQTree.style.visibility = "visible";
		}
	},
	showMKFolderTree : function(){
		var MKFolderTree = document.getElementById("MKFolderTree");
		if(MKFolderTree.style.visibility == "visible"){
			MKFolderTree.style.visibility = "hidden";
		}else if(MKFolderTree.style.visibility == "hidden"){
			MKFolderTree.style.visibility = "visible";
		}
	},
	startDocList : function(){
		document.getElementById("scheduleid").value = "<%=scheduleid%>";
		var pathis = document.getElementById("pathis");
		var pathis_value = pathis.firstChild.nodeValue;
		var pathis_title = pathis.getAttribute("title");

		var params = "pathis="+encodeURIComponent(pathis_title);
		XHR.sendRequest("getDocList.jsp", params, this.listDocList, 'POST');
	},
	listDocList : function() {
		if (XHR.httpRequest.readyState == 4) {
			if (XHR.httpRequest.status == 200) {
	
				var resultText = XHR.httpRequest.responseText;
				resultText = resultText.replace(/\s/g,"");
				var result = resultText.split('|');
				var count = parseInt(result[0]);
				var keywordList = null;
	
				if (count > 0) {
					docList = result[1].split(",");
					var html = "";
					var leftSelect = document.getElementById("left_select");
					for (var i = 0 ; i < docList.length ; i++) {
						var optEl = document.createElement("option");
						optEl.text = schedulePage.getNameFromFullpath(docList[i]);
						optEl.value = docList[i];
						if(browserName == "Internet Explorer"){
							leftSelect.add(optEl);
						}else{
							leftSelect.appendChild(optEl);
						}
					}
				}
			} else {
				//alert("error: "+httpRequest.status);
			}
		}
	},
	getNameFromFullpath : function(fullpath){
		var filename = "";
		if(fullpath.indexOf("/")> -1){
			filename = fullpath.substring(fullpath.lastIndexOf("/")+1);
		}else{
			filename = fullpath.substring(fullpath.lastIndexOf("\\")+1);
		}
		return filename;
	},
	getPathFromFullpath : function(fullpath){
		var path = "";
		if(fullpath.indexOf("/")> -1){
			path = fullpath.substring(0, fullpath.lastIndexOf("/")+1);
		}else{
			path = fullpath.substring(0, fullpath.lastIndexOf("\\")+1);
		}
		return path;
	},
	delOptElAll : function(){ //delete all item
		var leftSelect = document.getElementById("left_select");
	    if(eval(leftSelect.options.length) > 0){
	     	//for loop leftSelect size
			for(i = 0 ; eval(leftSelect.options.length) ; i++){
	            //select top item
		        var topOp = leftSelect.children(0);
	            //top item delete !!
				leftSelect.removeChild(topOp);
	        }
	   	}
	},
	addLoadEvent : function(func) {
	  	var oldonload = window.onload;
	  	if (typeof window.onload != 'function') {
	    	window.onload = func;
	  	} else {
	    	window.onload = function() {
	      		oldonload();
	      		func();
	    	}
	  	}
	},
	checkDoc : function(){
		var doclist_right = document.getElementById("right_select");
		if(doclist_right.selectedIndex == -1){
			//doclist_right.focus();
			if(doclist_right.length > 0){
				if(confirm("<rqfmt:message strkey='schedule.rqschedule.alert.noselectdocwouldyoutop'/>")){
					doclist_right.options[0].selected = true;
				}
			}
		}
	},
	applyRunvar : function(){
		var doclist_right = document.getElementById("right_select");
		var runvar = document.getElementById("runvar");
		if(doclist_right.selectedIndex == -1){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.firstselectdoc'/>");
			return;
		}
		if(runvar.value == ""){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.noinputvale'/>");
			return;
		}
		var key = "";
		for (var i=0;i<doclist_right.options.length;i++){
			if (doclist_right.options[i].selected == true){
				text = doclist_right.options[i].text;
				key  = doclist_right.options[i].value;
				runvarmap[key] = runvar.value;
				var msgt  = "'" + text;
				    msgt += "' " + "<rqfmt:message strkey='schedule.rqschedule.alert.applydocrunvar'/>" + " '";
				    msgt += runvar.value + "' " + "<rqfmt:message strkey='schedule.rqschedule.alert.applydocrunvar2'/>";
				alert(msgt);
			}
		}
	},
	showRunvar : function(fullpath){
		var key = fullpath;
		var runvar = document.getElementById("runvar");
		runvar.value = runvarmap[key] == null ? "" : runvarmap[key];
	
		var resultfile = document.getElementById("resultfile");
		resultfile.value = resultfilemap[key] == null ? "" : resultfilemap[key];
	},
	//test func
	showRunvarmap : function(){
		for (key in runvarmap) {
			alert(key + " : " + runvarmap[key]);
		}
	},
	checkResultfile : function(){
		var doclist_right = document.getElementById("right_select");
		if(doclist_right.selectedIndex == -1){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.firstselectdoc'/>");
			return;
		}
	},
	//직접 등록
	applyResultfile : function(){
		var doclist_right = document.getElementById("right_select");
		var resultfile = document.getElementById("resultfile");
		if(doclist_right.selectedIndex == -1){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.firstselectdoc'/>");
			return;
		}
		var cnt = 0;
		for (var i=0;i<doclist_right.options.length;i++){
			if (doclist_right.options[i].selected == true){
				++cnt;
			}
		}
		if(cnt > 1){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.noservicemultirqx'/>");
			return;
		}
		var fullpath = "";
		for (var i=0;i<doclist_right.options.length;i++){
			if (doclist_right.options[i].selected == true){
				fullpath = doclist_right.options[i].value; // key = fullpath
				filename = this.getNameFromFullpath(fullpath);
				path = this.getPathFromFullpath(fullpath);
				filename_withoutext = filename.substring(0,filename.indexOf("."));
				//ext = filename.substring(filename.indexOf(".")+1, filename.length);
				resultfilename = path+filename_withoutext+".rqv";
				//alert(resultfilename);
				resultfilemap[fullpath] = resultfilename;
				resultfile.value = resultfilename;
			}
		}
	},
	applyDirectHandle : function(){
		var doclist_right = document.getElementById("right_select");
		var resultfile = document.getElementById("resultfile");
		if(resultfile.value == ""){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.noinputvalue'/>");
			return;
		}
		var cnt = 0;
		for (var i=0;i<doclist_right.options.length;i++){
			if (doclist_right.options[i].selected == true){
				++cnt;
			}
		}
		if(cnt > 1){
			alert("<rqfmt:message strkey='schedule.rqschedule.alert.noservicemultirqx'/>");
			return;
		}
		for (var i=0;i<doclist_right.options.length;i++){
			if (doclist_right.options[i].selected == true){
				fullpath = doclist_right.options[i].value;
				resultfilemap[fullpath] = resultfile.value;
				var msgdoc  = "'"+ doclist_right.options[i].text;
				    msgdoc += "' " + "<rqfmt:message strkey='schedule.rqschedule.alert.saverqx'/>"
				    msgdoc += "\n'" + resultfile.value + "' " + "<rqfmt:message strkey='schedule.rqschedule.alert.saverqx2'/>";
				alert(msgdoc);
			}
		}
	},
	hidden : function(node){
		var parentnode = node.parentNode;
		parentnode.style.visibility = "hidden";
	},
	showinfo : function(fullpath, docname){
		lm_runvar = runvarmap[fullpath] == null ? "<rqfmt:message strkey='schedule.rqschedule.alert.nosetting'/>" : runvarmap[fullpath];
		lm_resultfile = resultfilemap[fullpath] == null ? "<rqfmt:message strkey='schedule.rqschedule.alert.nosetting'/>" : resultfilemap[fullpath];
		var showinfomsg  = "'" + docname + "' " + "<rqfmt:message strkey='schedule.rqschedule.alert.showinfo'/>";
			showinfomsg += "\n\n" + "<rqfmt:message strkey='schedule.rqschedule.alert.showinfo2'/>" + " : " +"'"+ lm_runvar +"'";
			showinfomsg += "\n" + "<rqfmt:message strkey='schedule.rqschedule.alert.showinfo3'/>" + " : " +"'"+lm_resultfile+"'";
		alert(showinfomsg);
	},
	submitfn : function(){
		document.frm.method = "post";
		document.frm.action = "rqschedulehandle.jsp?mode=insertRQV";
		var doclist_right = document.getElementById("right_select");
		if(doclist_right.options.length == 0){alert("<rqfmt:message strkey='schedule.rqschedule.alert.noselectdoc'/>");return;}
		for(var i=0; i < doclist_right.options.length ; i++){
			doclist_right.options[i].selected = true;
		}
		var runvards = "";
		var resultfileds = "";
		for(runvarkey in runvarmap){
			if(runvarmap[runvarkey] == null || runvarmap[runvarkey] == ""){
				runvarmap[runvarkey] = " ";
			}
			runvarset = runvarkey + "\t" + runvarmap[runvarkey] +"";
			runvards += runvarset;
		}
		for(resultfilekey in resultfilemap){
			if(resultfilemap[resultfilekey] == null || resultfilemap[resultfilekey] == ""){
				resultfilemap[resultfilekey] = " ";
			}
			resultfileset = resultfilekey + "\t" + resultfilemap[resultfilekey] +"";   // "" <--- asc code 05
			resultfileds += resultfileset;
		}
		document.frm.runvards.value = runvards;
		document.frm.resultfileds.value = resultfileds;
		document.frm.submit();
	},
	singleEleApply : function(){
		var doclist_right = document.getElementById("right_select");
		var runvar = document.getElementById("runvar");
		text = doclist_right.options[0].text;
		key  = doclist_right.options[0].value;
		runvarmap[key] = runvar.value;
		//alert("'" + text + "' 문서에 실행변수 '" +runvar.value+"' 을 적용 하였습니다.");
	}
}
$(function(){
	XHR = new xhr.Request();
	gfunc = new rqglobalfunc.rqglobal(); 
	schedulePage = new rqschedule.schedule(); 
	//schedulePage.addLoadEvent(schedulePage.startDocList);
	schedulePage.startDocList();
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form name="frm" id="frm">
<table width="100%" border="0" cellspacing="0" cellpadding="0" bordercolor="black">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='schedule.rqschedule.rqxregi'/></td>
				</tr>
				</table>
			</td>
			<td><img src="../img/popup_top_r.gif" width="137" height="50" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td height="3"></td>
</tr>
<tr>
	<td align="center"  bgcolor="#FFFFFF">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="7"></td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td colspan="2">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">1.</span> <rqfmt:message strkey='schedule.rqschedule.exedaytimeregi'/></td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.format'/></td>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td style="width:10px;background-color: white;"></td>
							<td align="left">
								<select id="dformat" name="dformat" style="font-family: verdana;">
									<option selected="selected">yyyyMMdd</option>
									<option>yyMMdd</option>
									<option>yyMM</option>
									<option>MMdd</option>
								</select>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.user'/></td>
							<td>
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td style="width:10px;background-color: white;"></td>
									<td align="left">
										<select size="1" id="username" title="userlist" name="strUserId" id="strUserId" style="width: 80px;font-family: verdana;">
									<%	
										String userid = oUserModel.getUserid();
										ArrayList oUserList = (ArrayList) pageContext.getAttribute("oUserList");
										UserInfo ui = null;
										for(int i = 0; i < oUserList.size(); i++) {
											ui = (UserInfo)oUserList.get(i);
											String user = ui.getParamString(0);
									%>
											<option value="	<%=user%>" <%=userid.equals(user) ? "selected=\"selected\"" : ""%>><%=user%></option>
									<%
										}
									%>
										</select>
									</td>
								</tr>
								</table>
							</td>
							<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.email'/></td>
							<td>
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td style="width:10px;background-color: white;"></td>
									<td align="left">
										<input type="text" size="18" name="mailinglist" id="mailinglist" style="font-family:verdana;"/>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.folder'/></td>
					<td>
						<table border="0" bordercolor="blue" width="100%" align="left">
						<tr>
							<td width="3"></td>
							<td>
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="left">
										<table border="0" cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td width="10" height="24"><IMG SRC="../img/main_folder_l.gif" width="10" height="24" border="0" alt=""></td>
											<td id="pathis" title="/" height="24" class="n_navi" style="color:#EACD32;" style="word-break:break-all">/</td>
											<td width="10" height="24"><IMG SRC="../img/main_folder_r.gif" width="10" height="24" border="0" alt=""></td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>
							<td align="right" width="75">
								<table border="0" cellpadding="0" cellspacing="0" width="75">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:schedulePage.showTree();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.folderselect'/></a></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.doc'/></td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0" align="center">
						<tr height="2px;"><td colspan="3"></td></tr>
						<tr>
							<td class="pop_bar" style="height:1px;"></td>
							<td></td>
							<td class="pop_bar" style="height:1px;"></td>
						</tr>
						<tr height="5px;">
							<td style="background-color:#F5F5F5;"></td>
							<td></td>
							<td style="background-color:#F5F5F5;"></td>
						</tr>
						<tr>
							<td style="background-color:#F5F5F5;"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">1-1.</span> <rqfmt:message strkey='schedule.rqschedule.doclistinfolder'/></td>
							<td>&nbsp;</td>
							<td style="background-color:#F5F5F5;"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">1-2.</span> <rqfmt:message strkey='schedule.rqschedule.selectdoc'/></td>
						</tr>
						<tr height="4px;"><td colspan="3"></td></tr>
						<tr>
						    <td>
						        <select id="left_select" size="6" tabindex="1" style="font-family: verdana;color:black; border-style:none; width:160px;z-index: -1;" multiple="multiple" ondblclick="alert(this.options[selectedIndex].text);">
						        </select>
							</td>
						    <td>
						    	<table cellpadding="0" cellspacing="0" border="0">
						    	<tr>
						    		<td width="2"></td>
						    		<td>
							    		<table cellpadding="0" cellspacing="0" border="0">
							    		<tr>
								    		<td><input tabindex="2" onClick="schedulePage.MoveElements('toRight');" style="width:76;cursor:hand;" type="button" value="→"></td>
								    	</tr>
								    	<tr><td height="4"></td></tr>
								    	<tr>
								    		<td><input tabindex="3" onClick="schedulePage.MoveElements('toLeft');" style="width:76;cursor:hand;" type="button" value="←"></td>
								    	</tr>
							    		</table>
						    		</td>
						    		<td width="2"></td>
						    	</tr>
						    	</table>
						    </td>
						    <td>
							    <select id="right_select" name="doclist" size="6" style="font-family: verdana;color:black; border-style:none; width:160px;z-index: -1;" tabindex="4" onChange="schedulePage.showRunvar(this.options[selectedIndex].value)" ondblclick="schedulePage.showinfo(this.options[selectedIndex].value, this.options[selectedIndex].text);" multiple="multiple">
							    </select>
						    </td>
							<!--td><input type="button" onclick="schedulePage.addElementToleft_select();" value="addElement"></td-->
						</tr>
						</table>

					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">2.</span> <rqfmt:message strkey='schedule.rqschedule.regirunvar'/></td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.runvar'/></td>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td width="3"></td>
							<td align="left">
								<input type="text" id="runvar" name="runvar" size="33" onclick="schedulePage.checkDoc();" onBlur="schedulePage.singleEleApply();" style="font-family: verdana;"/>
							</td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:schedulePage.applyRunvar();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.runvarapply'/></a></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
							<td align="center" background="../img/popup_title_bg.gif" class="pop_title"><span style="font-family:verdana;font-size:18;color:#FEBF52;font-weight:bold;">3.</span> <rqfmt:message strkey='schedule.rqschedule.rqxregi'/></td>
							<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td class="stitle_b" width="100" style="padding-left:3px;background-color:#F5F5F5;"><rqfmt:message strkey='schedule.rqschedule.rqxregistitle'/></td>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td width="3"></td>
							<td align="left">
								<select id="doctype" name="doctype">
									<option selected="selected">rqv</option>
									<option>pdf</option>
									<option>hwp</option>
									<option>xls</option>
									<option>gul</option>
								</select>
								<input type="text" id="resultfile" name="resultfile" size="21" onclick="schedulePage.checkResultfile();"/>
							</td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:schedulePage.showMKFolderTree();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.registerdoc'/></a></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:schedulePage.applyDirectHandle();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.directtype'/></a></td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				<tr>
					<td colspan="2" class="pop_bar" style="height:1px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="height:3px;background-color: white;"></td>
				</tr>
				</table>
			</td>
			<td width="7"></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td align="center" style="height:10px;"></td>
</tr>
<tr>
	<td align="center">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><img src="../img/btn_g_left2.gif"></td>
			<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:schedulePage.submitfn();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.button.confirm'/></a></td>
			<td><img src="../img/btn_g_right.gif"></td>
			<td width="8"></td>
			<td><img src="../img/btn_g_left2.gif"></td>
			<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="javascript:self.close();" class="btn_green"><rqfmt:message strkey='schedule.rqschedule.button.cancel'/></a></td>
			<td><img src="../img/btn_g_right.gif"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<input type="hidden" name="runvards" id="runvards"/>
<input type="hidden" name="resultfileds" id="resultfileds"/>
<input type="hidden" name="scheduleid" id="scheduleid"/>
</form>

<div id="RQTree" style="left: 185px; top: 110px; width: 200px ; height: 200px; visibility:hidden ;position: absolute;z-index: 10;">
	<iframe src="rqTree.jsp?strAction=showtree" width="200" height="190" frameBorder="0" marginHeight="0" marginWidth="0" scrolling="yes" onmouseout="schedulePage.hidden(this);"></iframe>
</div>
<div id="MKFolderTree" style="left: 270px; top: 240px; width: 200px ; height: 200px; visibility:hidden ;position: absolute ;z-index: 10;" >
	<iframe src="rqTree.jsp?strAction=makefoldertree" width="200" height="200" frameBorder="0" marginHeight="0" marginWidth="0" scrolling="yes" onmouseout="schedulePage.hidden(this);"></iframe>
</div>
</body>
</html>