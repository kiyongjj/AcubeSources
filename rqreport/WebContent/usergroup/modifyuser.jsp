<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%@ page import="com.sds.rqreport.model.UserModel" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="UM"/>
<%
String strAuth = "";
UserModel oSUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oSUserModel.getAuth();
}
%>
<%
/**********************
* [CDC] This JSP is developed by jia1.liu@samsung.com
* [User information modify in User Manager]
* 2008.10.08
**********************/
String usergroups = "";
String chkuser = "";
chkuser = request.getParameter("chkuser");
UserInfo oUserInfo = null;
Encoding enc = new Encoding();
String lm_serverCharset = enc.getServerCharset();
String lm_RQCharset = enc.getRQCharset();
if(chkuser != null ){
	if(!chkuser.equals("")){
	    chkuser = Encoding.chCharset(chkuser, lm_serverCharset, lm_RQCharset);
		UserRepository oUserRepository = new UserRepository();
		ArrayList arr = new ArrayList();
		oUserRepository.getUser(chkuser, arr);
		// only one-user get
		oUserInfo = (UserInfo) arr.get(0);
		usergroups = oUserInfo.getParamString(4);
		//System.out.print(usergroups);
	}
}
String strGroupIDs = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
String[] strGroupID = strGroupIDs.split(";");
boolean hasAdminGroup = false;
for (int i = 0 ; i < strGroupID.length ; i ++){
if (strGroupID[i].equals("Administrators")){
hasAdminGroup = true;
}
}
%>
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/HttpRequest.js"></script>
<script language="JavaScript" type="text/JavaScript">
<!--
var checkIDflag = false;

function changeAuth(){
  var userAuth = "G";
  var grouplist = document.getElementById("groups");
  for(var i=0; i<grouplist.options.length; i++)
	  {
	  //grouplist.options[i].selected = true;
	  if (grouplist.options[i].selected == true){
	  var authname = grouplist.options[i].value;
	  var groupauth = authname.substring(0,1);
	  if (groupauth == "A") {
		  userAuth = "A";
		  break;
		  }
	    }
     }
   //alert(userAuth);
   var groupauth = document.getElementById("aut");
   if (userAuth == "A")
   {
   groupauth.options[0].selected = false;
   groupauth.options[1].selected = true;
   }
   else {
    groupauth.options[1].selected = false;
    groupauth.options[0].selected = true;
   }
 }
 
function submitFn(){
	signupform = document.signup;

	idvalue = signupform.id.value.replace(/\s/g,"");
	pwvalue = signupform.pw.value.replace(/\s/g,"")
	pwcfmvalue = signupform.pwcfm.value.replace(/\s/g,"");
	descvalue = signupform.desc.value.replace(/\s/g,"")

	if(idvalue ==""){
		alert("<rqfmt:message strkey='usergroup.newuser.alert.insertid'/>");
		signupform.id.focus();
	}else if(pwvalue == ""){
		alert("<rqfmt:message strkey='usergroup.newuser.alert.insertpasswd'/>");
		signupform.pw.focus();
	}else if(pwcfmvalue == ""){
		alert("<rqfmt:message strkey='usergroup.newuser.alert.repasswd'/>");
		signupform.pwcfm.focus();
	}else if(descvalue == ""){
		alert("<rqfmt:message strkey='usergroup.newuser.alert.insertdesc'/>");
		signupform.desc.focus();
		return;

	}else{
		//한글사용불가
		if(isUserCharCheck(idvalue) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.userid.chkkorean'/>");
			return;
		}
		//최소길이체크
		if(checkLength(idvalue, 5, 20) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.idlength'/>");
			return;
		}
		//특수문자사용불가
		if(checkSpChar(idvalue, signupform.id, "<rqfmt:message strkey='usergroup.newuser.userid'/>") == false){
			return;
		}
		//첫글자영문으로시작

		if(aZCheck(idvalue.charAt(0)) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.useridstartdesc'/>");
			return;
		}

		if(checkLength(pwvalue, 6, 20) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdlength'/>");
			return;
		}
		//영문숫자 혼용
		if(isNumCombi(pwvalue) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdmix'/>");
			return;
		}
		if(isStrCombi(pwvalue) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdmix'/>");
			return;
		}

		if(checkSpChar(pwvalue, signupform.pw, "<rqfmt:message strkey='usergroup.newuser.password'/>") == false){
			return;
		}
		if(checkSpChar(pwcfmvalue, signupform.pwcfm, "<rqfmt:message strkey='usergroup.newuser.passwordchk'/>") == false){
			return;
		}

		if(checkSpChar(descvalue, signupform.desc, "<rqfmt:message strkey='usergroup.newuser.description'/>") == false){
			return;
		}

		if(document.signup.pw.value != document.signup.pwcfm.value){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.passwordiden'/>");
			document.signup.pw.focus();
			document.signup.pw.value = "";
			document.signup.pwcfm.value = "";
			return;
		}

		if (CheckStrLen(signupform.desc, 200, "<rqfmt:message strkey='usergroup.newuser.userdescription'/>") == false) {
    		signupform.desc.focus();
    		return;
        }

		document.signup.action = "setUI.jsp";
		document.signup.method = "POST";
		signupform.submit();
	}
}

function checkSpChar(tmp, obj, name){
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
			var msgn  = name + "<rqfmt:message strkey='usergroup.newuser.alert.spchar'/>";
				msgn += " \\ \/ \: \* \? \" \< \> \| \' \~ \! \@ \# \$ \% \^ \& \( \) ";
				msgn += "<rqfmt:message strkey='usergroup.newuser.alert.spchar2'/>";
			alert(msgn);
			obj.focus();
			return false;
		}
	}
	return true;
}

function checkLength(str,minLng,maxLng){
	var ckstr = str.length;
    if (parseInt(ckstr) < parseInt(minLng) || parseInt(ckstr) > parseInt(maxLng)) return false;
    return true;
}

//사용자 입력 확인
function isUserCharCheck(input) {
 	var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_+={};,.";
    return containsCharsOnly(input,chars);
}

//첫글짜 영문 체크
function aZCheck(input) {
 	var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return containsCharsOnly(input,chars);
}

function containsCharsOnly(input,chars) {
	for (var inx = 0; inx < input.length; inx++) {
    	if (chars.indexOf(input.charAt(inx)) == -1)
        	return false;
    }
    return true;
}

//영문숫자 혼용
function isNumCombi(input) {
    var chars = "0123456789";
	//return containsCharsOnly(input,chars);
	for(var inx = 0 ; inx < input.length ; inx++){
		if(chars.indexOf(input.charAt(inx)) != -1){
			return true;
		}
	}
	return false;
}
//영문숫자 혼용
function isStrCombi(input) {
    var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	//return containsCharsOnly(input,chars);
	for(var inx = 0 ; inx < input.length ; inx++){
		if(chars.indexOf(input.charAt(inx)) != -1){
			return true;
		}
	}
	return false;
}

function CheckStrLen(obj, MaxLen, FieldName) {
	var i, len=0;
	if (typeof obj == "undefined") {
		return true
	}

	var s = obj.value;
	// String 길이를 구하는 부분..
	for(i=0;i < s.length; i++) (s.charCodeAt(i) > 255)? len+=2:len++;

	// 길이 확인.
	if (MaxLen < len) {
		var msgf = FieldName + "<rqfmt:message strkey='usergroup.newuser.alert.lengthcheck'/>" + " " + MaxLen + "<rqfmt:message strkey='usergroup.newuser.alert.lengthcheck2'/>";
		if (FieldName != "") alert(msgf);
		//alert(len);
		obj.focus();
		return false;
	}
	return true;
}

//-->
</script>
<jsp:include flush="true" page="/cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form name="signup">
<table width="300" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
  		<tr>
    		<td valign="top" width="330" height="50" background="../img/popup_top.gif">
				<table width="200" border="0" cellspacing="0" cellpadding="0">
	  			<tr>
					<td width="43" rowspan="2">&nbsp;</td>
					<td height="16"></td>
	  			</tr>
	  			<tr>
					<td class="popuptitle">
							<rqfmt:message strkey="usergroup.newuser.modiuser"/>
					</td>
	  			</tr>
				</table>
			</td>
			<td background="../img/popup_bg.gif">&nbsp;</td>
  		</tr>
		</table>
	</td>
</tr>
<tr>
    <td>
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
        	<td width="20">&nbsp; </td>
          	<td>
	          	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	            <tr>
	            	<td height="19"></td>
	        	</tr>
				<tr>
	            	<td height="30">
	            		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	                    <tr>
	                    	<td height="3" colspan="2" bgcolor="#A8CC72"></td>
	                    </tr>
	                    <tr>
	                    	<td height="26" class="table_title" style="text-align: left;" nowrap="nowrap"><sup style="color: red;">＊</sup><span class="table_title"><rqfmt:message strkey="usergroup.newuser.userid"/></span></td>
	                      	<td class="input_td">
	                      		<table cellpadding="0" cellspacing="0" border="0">
	                      		<tr>
	                      			<td>
		                      			<input name="id" type="text" style="background-color:#EDEDE8" class="input" size="16" maxlength="30" value="<%if(oUserInfo != null){out.println(oUserInfo.getParamString(0));}else{out.println("");}%>" readonly/>
	                      			</td>

	                      		</tr>


	                      		</table>
	                      	</td>

	                    </tr>
	                    <tr>
	                    	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
	                    <tr>
	                      		<!--
	                      		<td class="table_title" colspan="3" style="text-align: left;">
	                      		change password
	                      		<input type="radio" name="pwchange" value="Y"  />Yes
	                      		<input type="radio" name="pwchange" value="N" checked />No
	                      		-->
	                      		<input type="hidden" name="pwchange" value="N" >

	                     </tr>
	                      		<tr>
	                    	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
	                    <tr>
	                      	<td height="26" class="table_title" style="text-align: left;"><sup style="color: red;">＊</sup><span class="table_title"><rqfmt:message strkey="usergroup.newuser.password"/></span></td>
	                      	<td class="input_td"><input name="pw" maxlength="30" type="password" class="input" size="29" /></td>
	                    </tr>
	                    <tr>
	                    	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
						<tr>
	                    	<td height="26" class="table_title" style="text-align: left;"><sup style="color: red;">＊</sup><span class="table_title"><rqfmt:message strkey="usergroup.newuser.passwordchk"/></span></td>
	                      	<td class="input_td"><input name="pwcfm" maxlength="30" type="password" class="input" size="29" /></td>
	                    </tr>
	                    <tr>
	                      	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
						<tr>
	                      	<td height="26" class="table_title" style="text-align: left;">&nbsp;&nbsp;<span class="table_title"><rqfmt:message strkey="usergroup.newuser.authority"/></span></td>
	                      	<td class="input_td">
	                      		<%
	                      		if (strAuth.equals("A")){
	                      		%>
	                      			<select name="aut" size="1">
	                      			<% if(oUserInfo.getParamString(5).equals("A")){ %>
	                      				<option value="A" selected><rqfmt:message strkey="usergroup.newuser.authority.manager"/></option>
	                      				<option value="G"><rqfmt:message strkey="usergroup.newuser.authority.general"/></option>
	                      			<% }else{ %>
	                      				<option value="A"><rqfmt:message strkey="usergroup.newuser.authority.manager"/></option>
	                      				<option value="G" selected><rqfmt:message strkey="usergroup.newuser.authority.general"/></option>
	                      			<% } %>
	                      			</select>
	                      		<%
	                      		}else{
	                      		%>
	                      			<input type="text" name="authtext" class="input" value="<rqfmt:message strkey="usergroup.newuser.authority.general"/>" disabled="disabled"/>
	                      			<input type="hidden" name="aut" value="<%="G"%>"/>
	                      		<%
	                      		}
	                      		%>
	                      	</td>
	                    </tr>
	                    <tr>
	                      	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
						<tr>
	                      	<td height="72" class="table_title" style="text-align: left;"><sup style="color: red;">＊</sup><span class="table_title"><rqfmt:message strkey="usergroup.newuser.description"/></span></td>
	                      	<td class="input_td">
	                      		<textarea name="desc" cols="28" rows="4" class="input"><%if(oUserInfo != null){out.println(oUserInfo.getParamString(3));}else{out.println("");}%></textarea>
	                      	</td>
	                    </tr>
	                    <tr>
	                      	<td height="1" colspan="3" bgcolor="#A8CC72"></td>
	                    </tr>
	                  	</table>
	       			</td>
				</tr>
				<tr>
	            	<td height="7"></td>
	       		</tr>
				<tr>
	       			<td align="left" style="font-size: 11px; font-family: ??; padding-right: 7px;">
	       				( <font color="red">*</font> <rqfmt:message strkey="usergroup.newuser.requiredfield"/> ) <br>
	       			</td>
	       		</tr>
	            <tr>
	            	<td height="4"></td>
	       		</tr>
	 			<tr>
	            	<td height="15" align="center">
	            		<table cellspacing="0" cellpadding="0" border="0">
	                	<tr>
	                    	<td>
	                    		<table border="0" cellpadding="0" cellspacing="0">
	                          	<tr>
	                            	<td><img src="../img/btn_g_left2.gif"></td>
	                            	<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="javascript:submitFn();" class="btn_green"><rqfmt:message strkey="usergroup.common.button.ok"/></a></td>
	                            	<td><img src="../img/btn_g_right.gif"></td>
	                          	</tr>
	                        	</table>
	                        </td>
	                      	<td width="8"> </td>
	                      	<td>
	                      		<table border="0" align="left" cellpadding="0" cellspacing="0">
	                          	<tr>
	                            	<td><img src="../img/btn_g_left2.gif"></td>
	                            	<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="javascript:window.close();"><rqfmt:message strkey="usergroup.common.button.cancel"/></a></td>
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
        	<td width="20"></td>
		</tr>
    	</table>
	</td>
</tr>
</table>
	<input type="hidden" name="mode" value="modifyuser"/>
</form>
</body>
</html>
