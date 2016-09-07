<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<%
Environment env = Environment.getInstance();
String pathIs = request.getParameter("pathIs");
Encoding enc = new Encoding();
String lm_serverCharset = enc.getServerCharset();
String lm_RQCharset = enc.getRQCharset();
pathIs = Encoding.chCharset(pathIs, lm_serverCharset, lm_RQCharset);

Environment g_env = Environment.getInstance();
String user_agt = request.getHeader("user-agent");
String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";
String iev = "0";
if(viewer_type.equals("ocx")){
	int startPos = user_agt.indexOf("MSIE") + 4;
	String tmp = user_agt.substring(startPos, user_agt.indexOf(";", startPos) );
	iev = tmp.trim();
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="javascript" src="../setup/rqglobalfunc.js"></script>
<script language="javascript" src="../setup/HttpRequest.js"></script>
<script language="JavaScript" type="text/JavaScript">
var rqdocument = {}
rqdocument.newDocPage = function(){
	scripts = new Array;
	httpRequest = null;

	strDescs = new Object();
	lm_filename = "";
	descRes = "";
}
rqdocument.newDocPage.prototype = {

	addDoc : function(fileObj){
		formObj = document.getElementById("attachForm");
		var filename = "";
	
		if(fileObj.value==""){
			alert("<rqfmt:message strkey='document.new_document.alert.selectfile'/>");
			return;
		}else{
			if(fileObj.value.indexOf("/")> -1){
				filename = fileObj.value.substring(fileObj.value.lastIndexOf("/")+1);
			}else{
				filename = fileObj.value.substring(fileObj.value.lastIndexOf("\\")+1);
			}
		}
		ext = filename.substring(filename.indexOf(".")+1, filename.length);
		ext = ext.toLowerCase();
	
		if(ext != 'rqx' && ext !='rqv'){
			alert("<rqfmt:message strkey='document.new_document.alert.onlyrqx'/>");
			return;
		}
	
		// localfile check and add to Select Box
		this.addOptionToSelectBox(filename, fileObj);
		// server Repository file check
		this.checkRepositoryfile("<%=pathIs%>", filename);
	},
	checkRepositoryfile : function(pathis, filename){
		var params = "pathis="+encodeURIComponent(pathis)+"&"+"filename="+encodeURIComponent(filename);
		XHR.sendRequest("chkrepository.jsp", params, this.showRepositorystatus, 'POST'); // XHR
	},
	showRepositorystatus : function() {
		if (XHR.httpRequest.readyState == 4) {
			if (XHR.httpRequest.status == 200) {
				var resultText = XHR.httpRequest.responseText;
		 		resultText = resultText.replace(/\s/g,""); 
				if(eval(resultText) != 0){
					var msg = "<rqfmt:message strkey='document.new_document.confirm.overwrite1'/>" + "\n" + "<rqfmt:message strkey='document.new_document.confirm.overwrite2'/>";
					if(confirm(msg)){
						return;
					}else{
						i = eval(document.getElementById("doclist").options.length);
						document.getElementById("doclist").options[i-1] = null;
					}
				}
			} else {
				//alert("error: "+httpRequest.status);
			}
		}
	},
	addOptionToSelectBox : function(filename, fileObj){
		//option element create..!!
		var optEl = document.createElement("option");
		optEl.text = filename;
		optEl.value = filename;
		//check file
		if(document.getElementById("doclist").length > 0){

			//checkfilename(filename);
			for(i=0;i<document.getElementById("doclist").length;i++){
				//var selCh = document.getElementById("doclist").children(i).value; //only IE
				var selCh = $("#doclist option").eq(i).attr("value");
				//alert(selCh);
				if(selCh == filename){
					alert("<rqfmt:message strkey='document.new_document.alert.docalready'/>");
					return;
				}
			}
		}
		//add to Element selectbox with created element
		if(browserName == "Internet Explorer"){
			document.getElementById("doclist").add(optEl);    // IE
		}else{
			document.getElementById("doclist").appendChild(optEl); // FF
		}
		//alert(document.attachForm.doclist.length);
		this.attach(fileObj);
	},
	make_array : function(status, display_script) {
	    this.status = status;
	    this.display_script = display_script;
	},
	attach : function(fileObj) {
	    var val = fileObj.value;
	    var idx = fileObj.name.substring('fileobj'.length);
	    fileObj.parentNode.style.display = 'none';
	
	    this.add_item(++idx, val);
	    this.item_list();
	},
	add_item : function(idx, val) {
	    var seq = scripts.length;
	    var display_script = '<span id=display_item'+idx+'>'+val+' <b onclick=remove_item('+seq+') style=cursor:pointer>remove..</b></span><br>';
	    //var file_script = '<span id=file_item'+idx+'><input type=file name=fileobj'+idx+' id=fileobj'+idx+' size=20 onkeydown=event.returnValue=false>&nbsp;<img src=../img/btn_add.gif width=47 height=19 border=0 onClick=addDoc(document.getElementById("fileobj'+idx+'")) style=cursor:hand></span>';
	    
	    if(browserName == "Internet Explorer"){
			var file_script = '<span id=file_item'+idx+'><input type=file name=fileobj'+idx+' id=fileobj'+idx+' size=1 onkeydown=event.returnValue=false onChange=newDocPage.addDoc(document.getElementById("fileobj'+idx+'")) style="filter:alpha(Opacity:0);width:1; cursor: pointer;"></span>';	    	
	    }else{
	    	var file_script = '<span id=file_item'+idx+'><input type=file name=fileobj'+idx+' id=fileobj'+idx+' size=1 onkeydown=event.returnValue=false onChange=newDocPage.addDoc(document.getElementById("fileobj'+idx+'")) style="opacity:0;width:0; cursor: pointer;"></span>';
	    }
	    scripts[seq] = new this.make_array(true, display_script);
	
	    //document.getElementById('file_items').insertAdjacentHTML("afterEnd", file_script); // maybe not working...
	    $("#file_items").append(file_script);
	    
	},
	item_list : function() {
	    var validate_cnt = 0;
	    var display_scripts = '';
	
	    for (var i = 0; i < scripts.length; i++) {
	        if (scripts[i].status){
	            validate_cnt++;
	            display_scripts += '<b>'+validate_cnt+'</b>.'+scripts[i].display_script;
	        }
	    }
	    if (validate_cnt == 0)
	        display_scripts = 'No files..';
	    //document.getElementById('display_items').innerHTML = display_scripts;
	},
	remove_item : function(seq) {
	    //scripts[seq].status = false;
	    document.getElementById('file_item'+(seq+1)).innerHTML = '';
	    this.item_list();
	},
	preprocessing : function() {
	    var idx = scripts.length + 1;
	    document.getElementById('file_item'+idx).innerHTML = '';
	},
	delOptEl : function(){//delete selected item
		if(document.getElementById("doclist").selectedIndex == -1){
			alert("<rqfmt:message strkey='document.document.alert.noselectedfile'/>");
			return;
		}
		//for loop doclist size
		//alert(document.attachForm.doclist.length);
		for(i=0;i < document.getElementById("doclist").length; i++) {
			//if selected ?
			//alert("selectedIndex : "+document.attachForm.doclist.selectedIndex);
	        if (i == document.getElementById("doclist").selectedIndex){
	            //do delete
	      		//alert("delete file order : " + i);
	           	//alert(document.getElementById("doclist").options[i].value);
	           	
	           	strDescs[document.getElementById("doclist").options[i].value] = null;
				document.getElementById("desc").disabled = false;
				
				document.getElementById("desc").value = "";
				document.getElementById("desc").disabled = true;
	
	           	document.getElementById("doclist").options[i] = null;
	           	document.getElementById('file_item'+(i+1)).innerHTML = '';
	    	}
		}
	
		if(document.getElementById("doclist").length == 0){
			document.getElementById("desc").disabled = true;
		}
	
	},
	delOptElAll : function(){//delete all item
	    if(eval(document.getElementById("doclist").options.length)>0){
	     	//for loop doclist size
			for(i=0;eval(document.getElementById("doclist").options.length);i++){
	            //select top item
		        var selCh=document.getElementById("doclist").children(0);
	            //top item delete !!
				document.getElementById("doclist").removeChild(selCh);
	        }
	   	}
	},
	uploadFile : function(){
		//alert(document.attachForm.	doclist.length);
		if(document.getElementById("doclist").length == 0){
			alert("<rqfmt:message strkey='document.new_document.alert.nofileupload'/>");
			//document.getElementById("attachForm").fileobj.focus();
			return false;
		}
	
		desc = document.getElementById("desc").value;
	
		if( this.checkSpChar(desc, document.getElementById("desc")) == false){
			return;
		}
		if( this.CheckStrLen(document.getElementById("desc"), 100, "<rqfmt:message strkey='document.new_document.desc'/>") == false ){
			return;
		}
	
		//strDescs check point !!!!
		//for (key in strDescs) {
		//	alert(strDescs[key]);
		//}
		for(key in strDescs){
			if(strDescs[key] == null || strDescs[key] == ""){
				strDescs[key] = " ";
			}
			set = key + "\t" + strDescs[key] +"|";
			descRes += set;
		}
		document.getElementById("descRes").value = descRes;
		//alert(descRes); //<--check point
		document.getElementById("attachForm").submit();
		//return true;
	},
	checkSpChar : function(tmp, obj){
		for( i = 0 ; i < tmp.length ; i++){
			charAt = tmp.charAt(i);
			charAtASC = charAt.charCodeAt();
	
			if( charAt == "\\" || charAt == "\/" || charAt == "\:" || charAt == "\?" ||
			    charAt == "\*" || charAt == "\?" || charAt == '\"' || charAt == "\<" ||
			    charAt == "\>" || charAt == "\|" || charAt == "\'" ||
			    charAt == "\~" || charAt == "\!" || charAt == "\@" || charAt == "\#" ||
			    charAt == "\$" || charAt == "\%" || charAt == "\^" || charAt == "\&" ||
			    charAt == "\(" || charAt == "\)" )
			{
				var msgc  = "<rqfmt:message strkey='document.new_document.alert.desc'/>";
					msgc += " \\ \/ \: \* \? \" \< \> \| \' \~ \! \@ \# \$ \% \^ \& \( \) ";
					msgc += "<rqfmt:message strkey='document.new_document.alert.desc2'/>";
				alert(msgc);
				obj.focus();
				return false;
			}
		}
		return true;
	},
	CheckStrLen : function(obj, MaxLen, FieldName) {
		var i, len=0;
		if (typeof obj == "undefined") {
			return true
		}
	
		var s = obj.value;
		for(i=0;i < s.length; i++) (s.charCodeAt(i) > 255)? len+=2:len++;
	
		// 길이 확인.
		if (MaxLen < len) {
			var msgf = FieldName + "<rqfmt:message strkey='document.new_document.alert.lengthcheck'/>" + " " + MaxLen + "<rqfmt:message strkey='document.new_document.alert.lengthcheck2'/>";
			if (FieldName != "") alert(msgf);
			//alert(len);
			obj.focus();
			return false;
		}
		return true;
	},
	insertDesc : function(filename){
		//alert(fileObj.value);
		if(strDescs[filename] == null){
			strDescs[filename] = "";
			document.getElementById("desc").value = "";
		}else{
			document.getElementById("desc").value = strDescs[filename];
		}
		lm_filename = filename;
	
		if(document.getElementById("desc").disabled == true){
			document.getElementById("desc").disabled = false;
		}
		document.getElementById("desc").focus();
	
	},
	checkDocDesc : function (){
		if(document.getElementById("doclist").selectedIndex == -1){
			alert("<rqfmt:message strkey='document.new_document.alert.selectdoc'/>");
		}
	},
	descapply : function(){
		desc = document.getElementById("desc").value;
		if( this.checkSpChar(desc, document.getElementById("desc")) == false){
			return;
		}else{
			if(document.getElementById("doclist").selectedIndex == -1){
				alert("<rqfmt:message strkey='document.new_document.alert.selectdoc'/>");
				return;
			}
			strDescs[lm_filename] = document.getElementById("desc").value;
			alert("<rqfmt:message strkey='document.new_document.alert.applyconfirm'/>");
		}
		//alert(lm_filename +" : " +strDescs[lm_filename]);
	},
	MM_callJS : function(jsStr) { //v2.0
		return eval(jsStr)
	}
}
$(function(){
	gfunc = new rqglobalfunc.rqglobal(); 
	newDocPage = new rqdocument.newDocPage();
	XHR = new xhr.Request();
	//$("input:file").css({ 
	//    "image" : "../img/btn_addfile2.jpg",
	//    "opacity": "0.0" ,
	//    "width" : "0",
	//    "cursor": "pointer"
	//});
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='document.new_document.title'/></td>
				</tr>
				</table>
			</td>
			<td><img src="../img/popup_top_r.gif" width="137" height="50" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="15">&nbsp; </td>
			<td>
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td height="20"></td>
						</tr>
						<tr>
							<td height="30">

								<!-- form name="attachForm" method="post" action="../common/handler.jsp" enctype="multipart/form-data" -->
							<%
								String uriPath = pathIs;
								uriPath = URLEncoder.encode(pathIs, lm_RQCharset);
							%>
								<form name="attachForm" id="attachForm" method="post" action="../common/uploadPrc.jsp?pathIs=<%=uriPath%>" enctype="multipart/form-data" class="attform">

								<table width="100%" border="0" cellspacing="0" cellpadding="0" >
								<tr>
									<td class="pop_bar" colspan="3"></td>
								</tr>
								<tr>
									<td class="pop_bar" colspan="3"></td>
								</tr>
								<tr>
									<td width="70" height="60" class="stitle_b" rowspan="2" nowrap="nowrap"><rqfmt:message strkey='document.new_document.doclist'/></td>
									<td class="input_td">
										<select size="4" style='width:175px;font-family:verdana;' name="doclist" id="doclist" onChange="javascript:newDocPage.insertDesc(this.options[selectedIndex].text)"></select>
									</td>
									<td valign="top" style="padding-top:7px;">
										<div id="file_items"></div>
										<span id="file_item1">
											<% 
												if(viewer_type.equals("ocx")){ 
													if(iev.indexOf("8") != -1) {
											%>
													<input type="file" name="fileobj1" id="fileobj1" size="1" onkeydown="event.returnValue=false;" onChange="newDocPage.addDoc(document.getElementById('fileobj1'))" style="filter:alpha(Opacity:1);width:1; cursor:pointer;"/>
												<%  }else{  %>
													<input type="file" name="fileobj1" id="fileobj1" size="1" onkeydown="event.returnValue=false;" onChange="newDocPage.addDoc(document.getElementById('fileobj1'))" style="filter:alpha(Opacity:0);width:0; cursor:pointer;"/>
											<% 		
													}
												}else{ 
											%>
													<input type="file" name="fileobj1" id="fileobj1" size="1" onkeydown="event.returnValue=false;" onChange="newDocPage.addDoc(document.getElementById('fileobj1'))" style="opacity:0;width:0; cursor:pointer;"/>
											<% 	} %>
										</span>
										<%
											String layerLeftpos = "280";
											String applybuttonsize = "90";
											if(viewer_type.equals("ocx") && iev.indexOf("8") != -1){
												layerLeftpos = "262";
												applybuttonsize = "74";
											}
										%>
										<div style="position:absolute; top:78px; left:<%=layerLeftpos%>px; z-index:-2; cursor:pointer;">
											<table cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td><img src="../img/btn_addfile2.jpg"/></td>
											</tr>
											</table>
										</div>
										<table cellpadding="0" cellspacing="0" border="0">
										<tr><td height="7"></td></tr>
										<tr>
											<td style="cursor: pointer;" onclick="newDocPage.delOptEl();"><img src="../img/btn_delfile.jpg"/></td>
										</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" width="120" colspan="3"></td>
								</tr>
								<tr>
									<td height="1" colspan="3" class="pop_bar"></td>
								</tr>
								<tr>
									<td class="stitle_b"><rqfmt:message strkey='document.new_document.description'/></td>
									<td class="input_td" colspan="2">
										<table cellpadding="0" cellspacing="0" border="0">
										<tr><td height="4"></td></tr>
										<tr valign="top">
											<td onClick="newDocPage.checkDocDesc();"><textarea name="desc" id="desc" style='width:155px; height:40px;font-family: dotum,Verdana; font-size: 9pt; color: #666666; border: 1px solid #a0d1d6; padding-left: 10px; padding-right: 10px;' disabled="disabled"></textarea></td>
											<td width="10"></td>
											<td>
												<table border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td><img src="../img/btn_g_left2.gif"></td>
													<td background="../img/btn_g_bg.gif"class="btn_text_td" width="<%=applybuttonsize%>" align="center" style="cursor:hand" onClick="newDocPage.descapply();"><rqfmt:message strkey='document.new_document.apply'/></td>
													<td><img src="../img/btn_g_right.gif"></td>
												</tr>
												</table>
											</td>
										</tr>
										<tr><td height="4"></td></tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="1" colspan="3" class="pop_bar"></td>
								</tr>
								</table>
								<input type="hidden" name="descRes" id="descRes"/>
								</form>
							</td>
						</tr>
						<tr>
							<td height="13"></td>
						</tr>
						<tr>
							<td height="15" align="center">
								<table cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td>
										<table border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td><img src="../img/btn_g_left2.gif"></td>
											<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="javascript:newDocPage.uploadFile();"><rqfmt:message strkey='document.common.button.ok'/></a></td>
											<td><img src="../img/btn_g_right.gif"></td>
										</tr>
										</table>
									</td>
									<td width="8"> </td>
									<td>
										<table border="0" align="left" cellpadding="0" cellspacing="0">
										<tr>
											<td><img src="../img/btn_g_left2.gif"></td>
											<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="newDocPage.MM_callJS('window.close()')"><rqfmt:message strkey='document.common.button.cancel'/></a></td>
											<td><img src="../img/btn_g_right.gif"></td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="15" align="center">&nbsp;</td>
						</tr>
						</table>
					</td>
					<td width="15"></td>
				</tr>
				</table>
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>