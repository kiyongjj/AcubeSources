<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<%
Environment env = Environment.getInstance();
String chkuser = request.getParameter("chkuser");
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
	}
}
%>
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="javascript" src="../setup/HttpRequest.js"></script>
<script language="JavaScript" type="text/JavaScript">
rquser = {}
rquser.newUser = function(){
	this.checkIDflag = false;
}
rquser.newUser.prototype = {
	submitFn : function(){
		signupform = document.signup;
		if(this.checkIDflag == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.chkid'/>");
			signupform.id.focus();
			return;
		}
		idvalue = signupform.id.value.replace(/\s/g,"");
		pwvalue = signupform.pw.value.replace(/\s/g,"");
		pwcfmvalue = signupform.pwcfm.value.replace(/\s/g,"");
		descvalue = signupform.desc.value.replace(/\s/g,"");

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
			if(this.isUserCharCheck(idvalue) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.userid.chkkorean'/>");
				return;
			}
			//최소길이체크
			if(this.checkLength(idvalue, 5, 20) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.alert.idlength'/>");
				return;
			}
			//특수문자사용불가
			if(this.checkSpChar(idvalue, signupform.id, "<rqfmt:message strkey='usergroup.newuser.userid'/>") == false){
				return;
			}
			//첫글자영문으로시작
	
			if(this.aZCheck(idvalue.charAt(0)) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.useridstartdesc'/>");
				return;
			}
	
			if(this.checkLength(pwvalue, 6, 20) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdlength'/>");
				return;
			}
			//영문숫자 혼용
			if(this.isNumCombi(pwvalue) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdmix'/>");
				return;
			}
			if(this.isStrCombi(pwvalue) == false){
				alert("<rqfmt:message strkey='usergroup.newuser.alert.passwdmix'/>");
				return;
			}
	
			if(this.checkSpChar(pwvalue, signupform.pw, "<rqfmt:message strkey='usergroup.newuser.password'/>") == false){
				return;
			}
			if(this.checkSpChar(pwcfmvalue, signupform.pwcfm, "<rqfmt:message strkey='usergroup.newuser.passwordchk'/>") == false){
				return;
			}
	
			if(this.checkSpChar(descvalue, signupform.desc, "<rqfmt:message strkey='usergroup.newuser.description'/>") == false){
				return;
			}
	
			if(document.signup.pw.value != document.signup.pwcfm.value){
				alert("<rqfmt:message strkey='usergroup.newuser.alert.passwordiden'/>");
				document.signup.pw.focus();
				document.signup.pw.value = "";
				document.signup.pwcfm.value = "";
				return;
			}
	
			if (this.CheckStrLen(signupform.desc, 200, "<rqfmt:message strkey='usergroup.newuser.userdescription'/>") == false) {
	    		signupform.desc.focus();
	    		return;
	        }
	
			document.signup.action = "setUI.jsp";
			document.signup.method = "POST";
			signupform.submit();
		}
	},
	checkSpChar : function(tmp, obj, name){
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
	},
	checkLength : function(str,minLng,maxLng){
		var ckstr = str.length;
	    if (parseInt(ckstr) < parseInt(minLng) || parseInt(ckstr) > parseInt(maxLng)) return false;
	    return true;
	},
	//사용자 입력 확인
	isUserCharCheck : function(input) {
	 	var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_+={};,.";
	    return this.containsCharsOnly(input,chars);
	},
	//첫글짜 영문 체크
	aZCheck : function(input) {
	 	var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    return this.containsCharsOnly(input,chars);
	},
	containsCharsOnly : function(input,chars) {
		for (var inx = 0; inx < input.length; inx++) {
	    	if (chars.indexOf(input.charAt(inx)) == -1)
	        	return false;
	    }
	    return true;
	},
	//영문숫자 혼용
	isNumCombi : function(input) {
	    var chars = "0123456789";
		//return this.containsCharsOnly(input,chars);
		for(var inx = 0 ; inx < input.length ; inx++){
			if(chars.indexOf(input.charAt(inx)) != -1){
				return true;
			}
		}
		return false;
	},
	//영문숫자 혼용
	isStrCombi : function(input) {
	    var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		//return containsCharsOnly(input,chars);
		for(var inx = 0 ; inx < input.length ; inx++){
			if(chars.indexOf(input.charAt(inx)) != -1){
				return true;
			}
		}
		return false;
	},
	CheckStrLen : function(obj, MaxLen, FieldName) {
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
	},
	checkID : function(){
		signupform = document.signup;
		idvalue = signupform.id.value.replace(/\s/g,"");
		if(idvalue ==''){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.insertid'/>");
			signupform.id.focus();
			return;
		}
		//최소길이체크
		if(this.checkLength(idvalue, 5, 20) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.idlength'/>");
			signupform.id.focus();
			return;
		}
		//특수문자사용불가
		if(this.checkSpChar(idvalue, signupform.id, "<rqfmt:message strkey='usergroup.newuser.userid'/>") == false){
			signupform.id.focus();
			return;
		}
		//한글사용불가
		if(this.isUserCharCheck(idvalue) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.userid.chkkorean'/>");
			signupform.id.focus();
			return;
		}
		//첫글자영문으로시작
	
		if(this.aZCheck(idvalue.charAt(0)) == false){
			alert("<rqfmt:message strkey='usergroup.newuser.alert.useridstartdesc'/>");
			signupform.id.focus();
			return;
		}
	
		chkid = idvalue;
		var params = "chkid="+encodeURIComponent(chkid);
		XHR.sendRequest("checkuser.jsp", params, this.checkIDstatus, 'POST');
	},
	checkIDstatus : function() {
		if (XHR.httpRequest.readyState == 4) {
			if (XHR.httpRequest.status == 200) {
				var resultText = XHR.httpRequest.responseText;
		 		resultText = resultText.replace(/\s/g,"");   				// 정규식...
				if(resultText == -1){
					alert("<rqfmt:message strkey='usergroup.newuser.alert.duplid'/>");
					signupform.id.value="";
					signupform.id.focus();
				}else{
					/*
					if( confirm('"'+signupform.id.value + '" 는 사용 가능한  ID 입니다.\n사용 하시겠습니까 ?') ){
						checkIDflag = true;
					}else{
						checkIDflag = false;
						signupform.id.value = "";
					}*/
					alert('"'+signupform.id.value + '" <rqfmt:message strkey="usergroup.newuser.alert.availableid"/>');
					newUserPage.checkIDflag = true;  // "this preserved word" not applied
					signupform.pw.focus();
				}
	
			} else {
				//alert("error: "+httpRequest.status);
			}
		}
	}
}

$(function(){
	newUserPage = new rquser.newUser();
	XHR = new xhr.Request();
	
});
</script>
<jsp:include flush="true" page="../cs.jsp"/>
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
					<%
						if( oUserInfo != null ){
					%>
							<rqfmt:message strkey="usergroup.newuser.modiuser"/>
					<%
						}else{
					%>
							<rqfmt:message strkey="usergroup.newuser.adduser"/>
					<%
						}
					%>
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
		                      			<input name="id" type="text" class="input" size="16" maxlength="30" value="<%if(oUserInfo != null){out.println(oUserInfo.getParamString(0));}else{out.println("");}%>" onChange="javascript:newUserPage.checkIDflag=false"/>
	                      			</td>
	                      			<td width="7"></td>
	                      			<td>
	                      				<table border="0" cellpadding="0" cellspacing="0">
			                          	<tr>
			                            	<td><img src="../img/btn_g_left2.gif"></td>
			                            	<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="javascript:newUserPage.checkID();" class="btn_green"><rqfmt:message strkey="usergroup.newuser.checkid"/></a></td>
			                            	<td><img src="../img/btn_g_right.gif"></td>
			                          	</tr>
			                        	</table>
	                      			</td>
	                      		</tr>
	                      		</table>
	                      	</td>
	                    </tr>
	                    <tr>
	                    	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
	                    <tr>
                     	<td height="26" class="table_title" style="text-align: left;"></td>
	                      	<td style="font-size: 11px; font-family: 돋움;padding-top: 3px;padding-left: 5px; color: gray;"><rqfmt:message strkey="usergroup.newuser.useriddesc"/></td>
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
	                    <td height="26" class="table_title" style="text-align: left;"></td>
	                      	<td style="font-size: 11px; font-family: 돋움;padding-top: 3px;padding-left: 5px; color: gray;"><rqfmt:message strkey="usergroup.newuser.passworddesc"/></td>
	                    </tr>
	                   	<tr>
	                    	<td height="1" colspan="3" bgcolor="#E7E7E7"></td>
	                    </tr>
						<tr>
	                      	<td height="26" class="table_title" style="text-align: left;">&nbsp;&nbsp;<span class="table_title"><rqfmt:message strkey="usergroup.newuser.authority"/></span></td>
	                      	<td class="input_td">
	                      		<select name="aut" size="1">
	                      			<option value="G" selected="selected"><rqfmt:message strkey="usergroup.newuser.authority.general"/></option>
	                      			<option value="A"><rqfmt:message strkey="usergroup.newuser.authority.manager"/></option>
	                      		</select>
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
	       			<td align="left" style="font-size: 11px; font-family: 돋움; padding-right: 7px;">
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
	                            	<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="javascript:newUserPage.submitFn();" class="btn_green"><rqfmt:message strkey="usergroup.common.button.ok"/></a></td>
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
<%
	if(oUserInfo != null){
%>
	<input type="hidden" name="mode" value="modifyuser"/>
<%
	}else{
%>
	<input type="hidden" name="mode" value="addUser"/>
<%
	}
%>
</form>
</body>
</html>
