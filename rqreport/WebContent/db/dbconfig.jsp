<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.util.DBUtil" %>
<%@ page import="com.sds.rqreport.service.queryexecute.RQQueryParse" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="org.jdom.Document" %>
<%@ page import="org.jdom.JDOMException" %>
<%@ page import="org.jdom.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="G"/>
<%!
protected String decryptEncValue(String str){
	if(str != null && str.startsWith("*??*")){
		Decrypter dec = new Decrypter("RQREPORT6**??");
		return dec.decrypt(str.substring(4));
	}else{
		return str;
	}
}
%>
<%
//DBUtil oDbUtil = new DBUtil();
//NamingEnumeration enum1 = oDbUtil.getBindingObj();
%>
<%
Environment environ = Environment.getInstance();
DocRepository docRep = new DocRepository();

final char SSEP = 5;	// Data Set 제일 처음
final char COL_SEP = 9;	//tab
final char ROW_SEP = 10;

String lm_strPathIs = request.getParameter("strPathIs");
String[] lm_chkdoc = request.getParameterValues("chkdoc"); //fullpath

Encoding enc = new Encoding();
String lm_serverCharset = enc.getServerCharset();
String lm_RQCharset = enc.getRQCharset();
String strPathIs = Encoding.chCharset(lm_strPathIs, lm_serverCharset, lm_RQCharset);
String[] lm_chkdoc2 = Encoding.chCharset(lm_chkdoc, lm_serverCharset, lm_RQCharset);

//폴더처리 document.jsp 에서 폴더(script로 막았지만)가 넘어올경우 폴더만 제외 시키고

//나머지로만 스트링배열을 만든다.
Vector vchkdoc = new Vector();
for(int i=0 ; i < lm_chkdoc2.length ; i++){
	String lm_s = lm_chkdoc2[i].substring(lm_chkdoc2[i].lastIndexOf("/") + 1 );
	if(!lm_s.equals("") && lm_s != null){
		vchkdoc.add(lm_chkdoc2[i]);
	}
}
String[] chkdoc = new String[vchkdoc.size()];
for(int i=0 ; i < vchkdoc.size(); i++){
	chkdoc[i] = (String) vchkdoc.get(i);
}

RepositoryEnv env = RepositoryEnv.getInstance();
String rootPath = env.repositoryRoot;

Vector v = new Vector();
HashMap ojndiMap = new HashMap();

boolean flag_XML = false;
boolean flag_TEXT = false;

try{
	for(int i=0;i<chkdoc.length;i++){
		//for jndi in db
		Vector vjndi = new Vector();
		String strchkdocFullPathIs = rootPath +chkdoc[i];
		//out.println(i + " : " +strchkdocFullPathIs+ " | ");
		strchkdocFullPathIs = strchkdocFullPathIs.replaceAll("\\\\","/");
		File file = new File(strchkdocFullPathIs);

		RQQueryParse lm_rqqueryparse = new RQQueryParse();
		Document doc = lm_rqqueryparse.parseXML(file);

		Element dbelement = doc.getRootElement().getChild("Database");

		//jndi in db
		docRep.getDSList(chkdoc[i]);
		List list = docRep.getList();

		// vjndi 에 list 갯수만큼 vjndi를 만든다. (정확히 예기하면 list의 order 의 max 수)
		int maxsize = 0;
		int tmp = 0;
		for(int ii =0 ; ii < list.size() ; ii++){
			tmp = ((DSListInfo) (list.get(ii))).getParamInt(2);
			if(maxsize < tmp ){
				maxsize = tmp;
			}
		}
		maxsize += 1;
		//out.println(maxsize);

		int[] valid = new int[list.size()];
		for(int iii = 0 ; iii < list.size() ; iii++){
			valid[iii] = ((DSListInfo) list.get(iii)).getParamInt(2);
		}

		//for(int xx = 0 ; xx < valid.length ; xx++){
		//	out.println("valid:"+valid[xx]);
		//}

		String[] jndi = new String[maxsize];
		DSListInfo lm_DSListInfo = null;

		Iterator it_list = list.iterator();
		for(int j=0;j<maxsize;j++){
			jndi[j] = "";
			for( int iiii = 0 ; iiii < valid.length ; iiii++){
				if( j == valid[iiii] ){
					lm_DSListInfo = (DSListInfo)(it_list.next());
					jndi[j] = lm_DSListInfo.getParamString(1);
				}
			}
			vjndi.addElement(jndi[j]);
			//out.println(chkdoc[i] + " : "+jndi[j]);
		}
		// vjndi 에 list 갯수만큼 vjndi를 만든다. 끝.

		//jndi in db
		Iterator it_vjndi = vjndi.iterator();

	 	if(dbelement.getName().equals("Database")){
			List infolists = dbelement.getChildren("DBInfo");
			Iterator it = infolists.iterator();
			while(it.hasNext()){

				Element infoelement = (Element) it.next();
				String strConn = decryptEncValue(infoelement.getAttributeValue("DBID")).trim();
				String[] spl_strchkdocFullPathIs = strchkdocFullPathIs.split("/");
				String filename = spl_strchkdocFullPathIs[spl_strchkdocFullPathIs.length-1];

				String IFTYPE = infoelement.getAttributeValue("IFType"); //IFType 1 : RDB, 6 : XML, 5: TEXT
				if(IFTYPE.equals("6")){
					flag_XML = true;
				}else if(IFTYPE.equals("5")){
					flag_TEXT = true;
				}

				String s = filename + COL_SEP + strchkdocFullPathIs + COL_SEP + strConn + COL_SEP + chkdoc[i] + COL_SEP + IFTYPE + COL_SEP;
				//jndi in db
				String lm_jndi = "";
				if(it_vjndi.hasNext()) {
					lm_jndi = (String)it_vjndi.next();
					//out.println("###"+lm_jndi + "<br>");
					s = s + lm_jndi;
				}
				//
				v.addElement(s);
				//out.println(s);

				ojndiMap.put(chkdoc[i]+strConn, lm_jndi);

			}
			//out.println("<br>");
		 }
	}

}catch(JDOMException je){
	je.printStackTrace();
}catch(Exception e){
	e.printStackTrace();
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=environ.jsframework%>"></script>
<script language="javascript">
rqdb = {}
rqdb.dbConfig = function(){
	s = new Array();
	tab = "\t";
	cr = "|";
	<%
	for(int i=0;i<v.size();i++){
		out.println("s["+i+"]='"+(String) v.get(i)+"';");
	}
	%>
}
rqdb.dbConfig.prototype = {
	listselect : function(name){
		dbindocselect = window.document.form.dbindocselect;
		dbinsearchange = document.form.dbinsearchange;
		string	= "";
		count	= 0;
		dbindocselect.options.length = count;
		var s2 = "";
		flag = false;
		for( x = 0; x < s.length; x++ ) {
			string = s[x].split( tab ); //tab
			if( string[0] == name ) {
				s2 = string[2];
				dbindocselect.options[count++] = new Option( s2 );
				<%
					/**
				   	* DataSource가 XML 이거나 TEXT일경우 데이터소스 매핑전에 XML, TEXT 맵핑을 자동으로 해준다.
				   	* IFType = 1 : RDBMS
				   	* IFTpye = 5 : TEXT
				   	* IFType = 6 : XML
				   	**/
				%>
				var lm_iftype = string[4];
				var doclist  = document.getElementById("doclist");
				var doctext  = doclist.options[doclist.selectedIndex].text;
				var docvalue = doclist.options[doclist.selectedIndex].value;
				if(lm_iftype == "6"){
					dbindocselect.options[dbindocselect.options.length-1].selected = true;
	
					this.dbinsearchanget("XML", doctext, docvalue);
					dbinsearchange.selectedIndex = 0;
					flag = true;
	
				}else if(lm_iftype == "5"){
					dbindocselect.options[dbindocselect.options.length-1].selected = true;
	
					this.dbinsearchanget("TEXT", doctext, docvalue);
					dbinsearchange.selectedIndex = 1;
					flag = true;
				}
			}
		}
		if(flag == true){
			document.form.dbinser.options.selectedIndex = -1;
			return; <%//DataSource로 XML, TEXT가 있을경우 Reset 하는 과정을 skip 한다.%>
		}
		if(dbindocselect.length == 1){
			dbindocselect.options.selectedIndex = 0;
			this.chkselectedname(s2);
		}else if(dbindocselect.length > 1){
			document.form.dbinser.options.selectedIndex = -1;
			document.form.dbinsearchange.options.selectedIndex = -1;
		}
		//dbindocselect.options.selectedIndex = 1;
	 	dbindocselect.focus();
		//alert(dbindocselect.options.selectedIndex);
	},
	<%
	//ex) s[0] = RQReport2.rqd(tab)                           --> document name
	//		   c:/samples/RQReport2.rqd(tab)                --> physical path
	//           jdbc:oracle:thin:@dbserver2:1521:emp(tab)    --> url (in doc)
	//           /RQReport2.rqd(tab)                          --> path (db)
	//          jdbc/Reqube                                  --> jndi name (server)
	%>
	chkselectedname : function(name){ //name --> url
		docvalue = document.form.doclist.options[document.form.doclist.selectedIndex].value; //path
		dbindocindex = document.form.dbindocselect.selectedIndex;
		searchresult = "";
		optiontextIs = "";
		for( i = 0; i < s.length; i++){
			string = s[i].split(tab);
			//alert('string[2] : ' + string[2]); //parameter로 넘어온것
			//alert('name : ' + name); //문서 parsing 된뒤 넘어온것
			//alert('string[3] : ' +string[3]);
			//alert('docvalue : ' + docvalue);
			if(string[2] == name && string[3] == docvalue){
				//document.form.dbinser.options.selectedIndex = 2;
				//document.form.dbinser.options[2].text = 'jdbc/lee';
				for(j=0; j < document.form.dbinser.options.length ; j++){
					//alert("$.trim(string[5]) : " +	$.trim(string[5])  );
					if($.trim(string[5]) == $.trim(document.form.dbinser.options[j].text)){
						//alert(document.form.dbinser.options[j].text);
						//alert("j : " + j);
						document.form.dbinser.options.selectedIndex = j;
					}
					//dbinsearchange select !!!!!!!!!!
					//alert(docvalue + " : " + dbindocindex);
					//alert(this.chkformhivalue(docvalue, dbindocindex));  //-----> check point !!!!!!!!!!!!!!!!
					searchresult = this.chkformhivalue(docvalue, dbindocindex);
					//get dbinsearchange item
					//alert(document.form.dbinsearchange.length);
					var sel = -1;
					if(searchresult != -1){
						for(kk = 0 ; kk < document.form.dbinsearchange.length; kk++){
							optiontextIs = document.form.dbinsearchange.options[kk].text;
							//alert("searchresult : " +searchresult+ " optiontextIs : " +optiontextIs);
	
							if(searchresult == optiontextIs){
								//alert('equal !!!!!!!!!!!!!!!!!');
								sel = kk;
							}else{
								//alert('not equal !!!!!!!!!!!!!!!!!');
							}
						}
					}
					//alert("sel" +sel);
					document.form.dbinsearchange.options.selectedIndex = sel;
				}
			}
		}
	},
	chkformhivalue : function(docvalue, dbindocindex){
		//returnvalue : -1 --> no search result
		returnvalue = -1;
		hivalue = document.form.change.value;
		//alert("ljg");
		//alert(hivalue);
		if(hivalue != null && hivalue != ""){
			hivalueArray = hivalue.split(cr);
			//alert("hivalueArray.length : " +hivalueArray.length);
			for(x = 0 ; x < hivalueArray.length; x++){
				subhivalueArray = hivalueArray[x].split(tab);
				if(subhivalueArray[1] == docvalue && subhivalueArray[2] == dbindocindex){
					returnvalue = subhivalueArray[3];
				}
			}
		}
		return returnvalue;
	},
	chkmultiOrSingle : function (){
		var dbinsearchange = document.getElementById("dbinsearchange");
		var doclist = document.getElementById("doclist");
		var chkalldoc = document.getElementById("chkalldoc");
		if(document.form.dbindocselect.selectedIndex != -1 || chkalldoc.checked == true){
			for(var i=0;i<doclist.options.length;i++){
				if(doclist.options[i].selected == true){
					var name = dbinsearchange.options[dbinsearchange.selectedIndex].text;
					doctext  = doclist.options[i].text;
					docvalue = doclist.options[i].value; //path
					this.dbinsearchanget(name, doctext, docvalue);
				}
			}
		}else{
			alert("<rqfmt:message strkey='document.dbconfig.alert.nvldocument'/>");
			document.form.dbinsearchange.selectedIndex = -1;
		}
	},
	dbinsearchanget : function(name, p_doctext, p_docvalue){
		var chkalldoc = document.getElementById("chkalldoc");
		if(document.form.dbindocselect.selectedIndex != -1 || chkalldoc.checked == true){
			doctext = p_doctext;
			docvalue = p_docvalue; //path
			// chkalldoc 으로 문서전체를 선택했을경우
			// 선택된 문서내 데이터베이스는 없으므로 0 으로 셋팅해준다.
			if(chkalldoc.checked == true){
				dbindocindex = 0;
			}else{
				dbindocindex = document.form.dbindocselect.selectedIndex;
			}
			hivalue = document.form.change.value;
			//alert("hidden value1 : " +hivalue);
			<%
			//RQReport1.rqd		/RQReport1.rqd	0	jdbc/EMP
			//RQReport2.rqd		/RQReport2.rqd	0	jdbc/Reqube
			%>
			str = doctext +tab+ docvalue +tab+ dbindocindex +tab+ name;
			hivalueArray = hivalue.split(cr);
			flag = 0 ; //exist:1 , not-exist:0
			for(i=0;i < hivalueArray.length;i++){
				subhivalueArray = hivalueArray[i].split(tab);
				//alert("subhivalueArray[1] : " +subhivalueArray[1]);
				//alert("docvalue : " +docvalue);
				//alert("subhivalueArray[2] : " +subhivalueArray[2]);
				//alert("dbindocindex : " +dbindocindex);
				for(j=0;j<hivalueArray.length;j++){
					if(subhivalueArray[1] == docvalue && subhivalueArray[2] == dbindocindex)	{
						flag = 1;
					}
				}
			}//end of for
			if(hivalue == "") {
				//alert('first');
				hivalue = str;
			}else if(flag == 0){
				//alert('add');
				hivalue = hivalue +cr+ str;
			}else if(flag == 1){
				//alert('chiwan')
				//with doctext, dbindocindex exchange name
				hivalue = this.chiwanfn(docvalue, dbindocindex, name);
				//alert("chiwan hivalue : " + hivalue);
			}
			//alert("hidden value1 : " +hivalue);
			document.form.change.value = hivalue;
			//alert(document.form.change.value);  //-----> check point !!!!!!!!!!!!!!!!
		}else{
			alert("<rqfmt:message strkey='document.dbconfig.alert.nvldocument'/>");
			document.form.dbinsearchange.selectedIndex = -1;
		}
		//String[] changejndi = new Array[document.form.doclist.length];
		//changejndi
	},
	chiwanfn : function(docvalue, dbindocindex, name){
		hivalue = document.form.change.value;
		hivalueArray = hivalue.split(cr);
		for(i = 0 ; i < hivalueArray.length; i++){
			<%
			//RQReport1.rqd		/RQReport1.rqd	0	jdbc/EMP
			//RQReport2.rqd		/RQReport2.rqd	0	jdbc/Reqube
			%>
			subhivalueArray = hivalueArray[i].split(tab);
			if(subhivalueArray[1] == docvalue && subhivalueArray[2] == dbindocindex){
				subhivalueArray[3] = name;
			}
			for(j=0;j<subhivalueArray.length;j++){
				if(j == 0){
					sub = subhivalueArray[j];
				}else{
					sub = sub +tab+ subhivalueArray[j]
				}
			}
			//alert("sub : " +sub);
			if(i==0){
				root = sub;
			}else{
				root = root +cr+ sub;
			}
		}
		hivalue = root;
		return hivalue;
	},
	createOption : function(filefullpath){
		var optEl = document.createElement("option");
		optEl.text = filefullpath;
		optEl.value =  filefullpath;
		document.form.dbindocselect.add(optEl);
	},
	submitfn : function(){
		if(document.form.change.value == ""){
			alert("<rqfmt:message strkey='document.dbconfig.alert.nochangeinfo'/>");
			return;
		}
		document.form.action = "execute.jsp";
		//alert(document.form.change.value);
		document.form.submit();
	}, 
	setselectbox : function(){
		document.form.dbinser.selectedIndex = -1
		document.form.dbinsearchange.selectedIndex = -1
	},
	ckhalldocFnc : function(){
		var doclist = document.getElementById("doclist");
		var chkalldoc = document.getElementById("chkalldoc");
		if(chkalldoc.checked == true){
			for(var i=0;i<doclist.options.length;i++){
				doclist.options[i].selected = true;
			}
		}else if(chkalldoc.checked == false){
			for(var i=0;i<doclist.options.length;i++){
				doclist.options[i].selected = false;
			}
		}
	}
}
$(function(){
	dbConfigPage = new rqdb.dbConfig();
	$("#no1,#no2,#no3").css({
		"font-family" : "verdana",
		"font-size" : "16px",
		"color" : "#FEBF52",
		"font-weight" : "bold"
	});
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:dbConfigPage.setselectbox();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="../img/popup_top_l.gif" width="24" height="50" border="0" alt=""></td>
			<td valign="top" width="100%" height="50" background="../img/popup_bg.gif" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="popuptitle_b"><rqfmt:message strkey='document.dbconfig.title'/></td>
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
				<form name="form" id="form" method="post">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td height="3"></td>
				</tr>
				<tr>
					<td>
						<table width="100%" border="0" cellpadding="0" cellspacing="0" >
						<tr valign="top">
							<td>
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td align="center"  bgcolor="#FFFFFF">
										<table width="100%"  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td colspan="3" class="pop_bar"></td>
										</tr>
										<tr>
											<td width="100%" height="22" class="stitle_b" ><span id="no1">1.</span>
												<rqfmt:message strkey='document.dbconfig.document'/>
											</td>
										</tr>
										<tr>
											<td height="5" colspan="3"></td>
										</tr>
										<tr>
											<td colspan="3" align="center">
												<select size="4" multiple="multiple" style='width:376px;height:44;font-family:verdana;' name="doclist" id="doclist" 
													onChange='dbConfigPage.listselect(this.options[selectedIndex].text )'>
											<%
												for(int i=0;i<chkdoc.length;i++){
													String strchkdocFullPathIs = rootPath + chkdoc[i];
													strchkdocFullPathIs = strchkdocFullPathIs.replaceAll("\\\\","/");
													String[] spl_strchkdocFullPathIs = strchkdocFullPathIs.split("/");
													String filename = spl_strchkdocFullPathIs[spl_strchkdocFullPathIs.length-1];
											%>
													<option value="<%=chkdoc[i]%>"><%=filename%></option>
											<%
												}
											%>
												</select>
											</td>
										</tr>
										<tr height="3"><td></td></tr>
										<tr>
											<td align="left">
												<table cellpadding="0" cellspacing="0" border="0">
												<tr valign="baseline">
													<td><input type="checkbox" name="chkalldoc" id="chkalldoc" onclick="dbConfigPage.ckhalldocFnc();"/></td>
													<td>&nbsp;<rqfmt:message strkey='document.dbconfig.documentallselect'/></td>
												</tr>
												</table>
											</td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>

						</tr>
						</table>
					</td>
				</tr>
				<tr height="7"><td></td></tr>
				<tr>
					<td>
						<!-- stored JDNI -->
						<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td align="center"  bgcolor="#FFFFFF">
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td colspan="3" class="pop_bar"></td>
								</tr>
								<tr>
									<td width="100%" height="22" class="stitle_b" style="color: #B6B2B5;">
										<rqfmt:message strkey='document.dbconfig.currentjndi'/>
									</td>
								</tr>
								<tr>
									<td height="5" colspan="3"></td>
								</tr>
								<tr>
									<td colspan="3" align="center">
								<%
									//DBUtil oDBUtil = new DBUtil();
									//oDBUtil.setJndiMap(oDBUtil.getBindingObj());
									//HashMap ojndiMap = oDBUtil.jndiMap;

									Set set = ojndiMap.keySet();
									Object[] keyset = set.toArray();

									//for(int i = 0 ; i < keyset.length ; i++){
									//	out.println(keyset[i] + "<br>") ;
									//	out.println(ojndiMap.get(keyset[i]) + "<br>") ;
									//}
								%>
										<select name="dbinser" id="dbinser" size="1" 
											style="width:376px;height:44 ;font-family:verdana;color: gray;" disabled="disabled">
									<%
										for(int i=0; i< keyset.length; i++){
											String strKeyUrl = (String) keyset[i];
											String strJndiName = (String)ojndiMap.get(strKeyUrl);
									%>
											<option><%=strJndiName%></option>
									<%
										}
									%>
										</select>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr height="8"><td></td></tr>
				<tr>
					<td class="pop_wbar" height="1"></td>
				</tr>
				<tr>
					<td>
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="6" colspan="5"></td>
						</tr>
						<tr>
							<td>
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="4"><img src="../img/popup_title_left.gif" width="7" height="25"></td>
									<td align="center" background="../img/popup_title_bg.gif" class="pop_title">
										<rqfmt:message strkey='document.dbconfig.databasesetting'/>
									</td>
									<td width="8"><img src="../img/popup_title_right.gif" width="8" height="25"></td>
								</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td height="30">
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<table width="100%"  border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td class="pop_bar" height="1"></td>
												</tr>
												<tr>
													<td align="center" width="100%" height="22" align="center" class="stitle_b"><span id="no2">2.</span>
														<rqfmt:message strkey='document.dbconfig.databaseindoc'/>
													</td>
												</tr>
												<tr><td height="3"><img src="../img/1pix.gif" width="1" height="1"/></td></tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table width="100%"  border="0" cellspacing="0" cellpadding="3">
												<tr valign="top">
													<td align="center">
														<select size="3" style='width:380px; font-family:verdana;' id="dbindocselect" name="dbindocselect" 
															onChange="javascript:dbConfigPage.chkselectedname(this.options[selectedIndex].text)">
														</select>
													</td>
												</tr>
												</table>
											</td>
										</tr>
										<tr><td height="4"><img src="../img/1pix.gif" width="1" height="1"/></td></tr>
										<tr>
											<td>
												<table width="100%"  border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td colspan="3" class="pop_bar"></td>
												</tr>
												<tr>
													<td align="center" height="22" class="stitle_b"><span id="no3">3.</span>
														<rqfmt:message strkey='document.dbconfig.jnditosave'/>
													</td>
												</tr>
												<tr><td height="7"><img src="../img/1pix.gif" width="1" height="1"/></td></tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table width="100%"  border="0" cellspacing="0" cellpadding="3">
												<tr valign="top">
													<td valign="top" align="left"  style="padding-left:6px;">

														<table cellpadding="0" cellspacing="0" border="0" align="center">
														<tr>
															<td>
																<select id="dbinsearchange" name="dbinsearchange" size="1" style="width:376px;font-family:verdana;" 
																	onChange="dbConfigPage.chkmultiOrSingle();">
																<%if(flag_XML == true){%><option>XML</option><%}%>
																<%if(flag_TEXT == true){%><option>TEXT</option><%}%>
															<%
																// JCO DataSource 가져오기

																ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
																Enumeration lm_oenm = lm_oRb.getKeys();

																String lm_tmp = "";
																while(lm_oenm.hasMoreElements()){
																	lm_tmp = (String) lm_oenm.nextElement();
																	if(lm_tmp.indexOf("JCOPoolName") != -1){
																		out.println("<option>" + lm_oRb.getString(lm_tmp) + "</option>");
																	}
																}
															%>
															<%
																int serverType = environ.serverType;
																if(serverType != 9){
																	// WAS DataSource 에서 읽어옴
																	Hashtable envt = null;
																	String bindstr = "";
																	DBUtil lm_oDBUtil = new DBUtil();
																	List jndiList = lm_oDBUtil.getServerDsListwithEnv(envt, bindstr);
																	Iterator it = jndiList.iterator();
																	String lm_jndiname = "";
																	while(it.hasNext())	{
					 										%>
																	<option>
																<%
																	lm_jndiname = (String) it.next();
																	out.println(lm_jndiname);
																%>
																	</option>
															<%
																	}
																}else{
																	// 직접입력시 DataSource 가져오기
																	ResourceBundle lm_oRb2 = ResourceBundle.getBundle("rqreport");
																	Enumeration lm_oenm2 = lm_oRb2.getKeys();
																	String lm_tmp2 = "";
																	String lm_strDataSource2 = "";
																	String lm_tmp_datasource2 = "";
																	while(lm_oenm2.hasMoreElements()){
																		lm_tmp2 = (String) lm_oenm2.nextElement();
																		int indx = lm_tmp2.lastIndexOf(".");
																		if(indx == -1) continue;
																		lm_tmp_datasource2 = lm_tmp2.substring(0, indx);
																		if(lm_tmp_datasource2.equals("rqreport.server.DataSource")){
																			lm_strDataSource2 = lm_oRb2.getString(lm_tmp2);
																			%>
																				<option>
																			<%
																				out.println(lm_strDataSource2);
																			%>
																				</option>
																			<%
																		}
																	}
																}
					 										%>
																</select>
															</td>
														</tr>
														</table>
													</td>
												</tr>
												</table>
											</td>
										</tr>
										</table>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="8"></td>
				</tr>
				<tr>
					<td class="pop_wbar" height="1"></td>
				</tr>
				<tr>
					<td height="15"></td>
				</tr>
				<tr>
					<td height="15" align="center">
						<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
								<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="javascript:dbConfigPage.submitfn();" class="btn_green">
										<rqfmt:message strkey='document.common.button.ok'/></a>
									</td>
									<td><img src="../img/btn_g_right.gif"></td>
								</tr>
								</table>
							</td>
							<td width="8"> </td>
							<td>
								<table border="0" align="left" cellpadding="0" cellspacing="0">
								<tr>
									<td><img src="../img/btn_g_left2.gif"></td>
									<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="javascript:window.close();"><rqfmt:message strkey='document.common.button.cancel'/></a></td>
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
				<input type="hidden" name="change" id="change" value=""/><%//return result to execute.jsp %>
				</form>
			</td>
			<td width="15"></td>
		</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>