<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%@ taglib uri="/WEB-INF/tld/RQPage.tld" prefix="page" %>
<%
String strAction = request.getParameter("strAction");
if(strAction == null) strAction = "showtree";
String strPathIs = "/";
DocRepository docRep = new DocRepository();
docRep.getListFD(strPathIs, "F");
ArrayList list = (ArrayList)docRep.getList();
Iterator it_list = null;
DocInfo m_di = null;
%>
<HTML>
<HEAD>
<TITLE>REQUBE REPORT</TITLE>
<script language="JavaScript" src="../setup/tree.js"></script>  
<script language="JavaScript">
function createNode(){
	var temp = new TreeNode(document.all("name").value);
	temp.put("link", document.all("link").value);
	try{
		tree.selectedNode[0].add(temp);
		tree.repaint();
	}catch(e){
		alert("select parent node.");
	}
} 
        
function deleteNode(){
	try{
		if(tree.selectedNode[0]==tree.root){
			alert("can't delete root node");
			return;
		}
		tree.selectedNode[0].removeFromParent();
		tree.selectedNode = new Array();
		tree.repaint();
	}catch(e){
		alert("select node.");
	}        	
}

function mkTree(){
	var tree = new Tree("RQTree","/");
	tree.iconDir="../img/";
	tree.selectionMode = tree.DISCONTIGUOUS_TREE_SELECTION; 
	tree.treeSelectionListener = function(node, event){
		this.selectNode(node, event);
		//document.all("msg").innerText = tree.selectedNode[0].get("link")+" selected";
	  	//document.location.href = tree.selectedNode[0].get("link");
		var parentpathis = parent.document.getElementById('pathis');
		var parentRQTree = parent.document.getElementById('RQTree');
		var nowPathis = tree.selectedNode[0].get("link");
		var nowPathis_value = nowPathis.substring(0, nowPathis.length-1);
		var nowPathis_title = tree.selectedNode[0].get("link");

		if(nowPathis_title == "/"){
			parentpathis.firstChild.nodeValue = "/";
		}else{
			parentpathis.firstChild.nodeValue = nowPathis_value;
		}
		parentpathis.setAttribute("title",nowPathis_title);
		
	 	parentRQTree.style.visibility = "hidden";
	 	parent.schedulePage.delOptElAll();
	 	parent.schedulePage.startDocList();
	}
	tree.root.put("link","/");
<%
	it_list = list.iterator();
	while(it_list.hasNext()){
		m_di = (DocInfo) it_list.next();
%>
	var <%=m_di.nodename%> = new TreeNode("<%=m_di.name == null ? "/" : m_di.name%>");
	<%=m_di.nodename%>.put("link","<%=m_di.fullPath%>");	
<%		
	}
%>
	<page:linkNodes action="getFolderInfo" list="<%=list%>"/> 
}

function mkFolderTree(){
	var tree = new Tree("RQTree","/");
	tree.iconDir="./img/";
	tree.selectionMode = tree.DISCONTIGUOUS_TREE_SELECTION; 
	tree.treeSelectionListener = function(node, event){
		this.selectNode(node, event);

		var parentMKFolderTree = parent.document.getElementById('MKFolderTree');
		var resultfile = parent.document.getElementById('resultfile');
		var selectedPathis = tree.selectedNode[0].get("link");
		

		var parent_doclist_right = parent.document.getElementById("right_select");
		var parent_resultfile = parent.document.getElementById("resultfile");
		if(parent_doclist_right.selectedIndex == -1){
			alert('문서를 먼저 선택하세요.');
			return;
		}
		var cnt = 0;
		for (var i=0;i<parent_doclist_right.options.length;i++){ 
			if (parent_doclist_right.options[i].selected == true){
				++cnt;
			}
		}
		if(cnt > 1){
			alert('결과문서등록시 다중선택은 지원하지 않습니다.');
			return;
		}
		var fullpath = "";
		var doctype  = parent.document.getElementById("doctype");
		var doctypevalue = doctype.options[doctype.selectedIndex].text;
		
		for (var i=0;i<parent_doclist_right.options.length;i++){ 
			if (parent_doclist_right.options[i].selected == true){
				fullpath = parent_doclist_right.options[i].value; // key = fullpath
				filename = parent.schedulePage.getNameFromFullpath(fullpath);
				 
				//path = parent.getPathFromFullpath(fullpath);
				path =  tree.selectedNode[0].get("link");
				
				filename_withoutext = filename.substring(0,filename.indexOf("."));
				//ext = filename.substring(filename.indexOf(".")+1, filename.length);
				
				resultfilename = path+filename_withoutext+"." + doctypevalue;
				//alert(resultfilename);
				parent.resultfilemap[fullpath] = resultfilename;
				parent_resultfile.value = resultfilename;
				parentMKFolderTree.style.visibility = "hidden";
				alert("'"+ parent_doclist_right.options[i].text +"' 문서의 결과문서를 " + "\n'" + resultfilename + "' 에 저장하였습니다.");
				return;
			}
		}
	}
	tree.root.put("link","/");

<%
	it_list = list.iterator();
	while(it_list.hasNext()){
		m_di = (DocInfo) it_list.next();
%>
	var <%=m_di.nodename%> = new TreeNode("<%=m_di.name == null ? "/" : m_di.name%>");
	<%=m_di.nodename%>.put("link","<%=m_di.fullPath%>");	
<%		
	}
%>
	<page:linkNodes action="getFolderInfo" list="<%=list%>"/> 
}
	
function addLoadEvent(func) {
  	var oldonload = window.onload;
  	if (typeof window.onload != 'function') {
    	window.onload = func;
  	} else {
    	window.onload = function() {
      		oldonload();
      		func();
    	}
  	}
}
<%
	if(strAction.equals("showtree")){
%>
	addLoadEvent(mkTree);
<%
	}else if(strAction.equals("makefoldertree")){
%>
	addLoadEvent(mkFolderTree);
<%
	}
%>
</script>
</head>
<body leftmargin="3" topmargin="3" marginwidth="3" marginheight="3">
<table border="1" style="border: 3px;border-color:#CCCCCC;" width="100%" height="100%" cellpadding="0" cellspacing="0">
<tr valign="top">
	<td>
		<table cellpadding="0" cellspacing="0" style="background-color:#F5F5F5;" width="100%" height="100%">
		<tr valign="top">
			<td></td>
		</tr>
		<tr><td style="background-color: #73B9DD; font-size: 12px;font-family: verdana;font-weight: bold;color: #FFFFFF;padding-left: 6px;" height="22" width="100%">Repository Root : /</td></tr>
		<tr valign="top"><td style="background-color: #499BC5;" height="4" width="100%"></td></tr>
		<tr valign="top"><td height="4" width="100%"></td></tr>
		<tr valign="top"><td></td></tr>
		<tr valign="top">
			<td>
				<table id="RQTree" border="0"  cellpadding="0" cellspacing="0" ondragstart="return false" onselectstart="return false" oncontextmenu="return false" ></table>
			</td>        
		</tr>
		<tr valign="top"><td height="7" width="100%"></td></tr>
		</table>
	</td>
</tr>

<tr>
	<td id="msg" style="visibility:hidden;"></td>
</tr>
</table>    
</body>
</html>