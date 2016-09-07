<%@ page contentType="text/html; charset=utf-8" %>
<%@ page errorPage="../common/error.jsp" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/> 
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../setup/HttpRequest.js"></script>
<%//로그파일을 초기에 불러오고 기본적인 셋팅을 한뒤 스크립트를 Generate 한다.%>
<rquser:rqLog action="taillogging">
<script language="JavaScript">
endPosition = <%=endPosition%>;
cnt = 0;
function startDocList(){
	var params = "filename="+encodeURIComponent("<%=filename%>")+"&endPosition=" + endPosition;
	//alert(params);
	XHR.sendRequest("getLogTail.jsp", params, getLogTail, 'POST');
	setTimeout("startDocList()", <%=intervalTime%>);
}

function getLogTail() {
	if (XHR.httpRequest.readyState == 4) {
		if (XHR.httpRequest.status == 200) {
			var resultText = XHR.httpRequest.responseText;
			//resultText = resultText.replace(/\s/g,"");
			endPosition = resultText.substring(0,resultText.indexOf("|"));
			endPosition = endPosition.replace(/\s/g,"");
			//alert(":"+endPosition+":");
			//alert("endPositionInGetLog" + endPosition);
			var loggingContents = resultText.substring(resultText.indexOf("|") + 1 , resultText.length);

			var logdata = document.getElementById("logdata");
			if(loggingContents.length > 0){
				if(cnt % 2 == 0){
					logdata.innerHTML += "<span style='color:olive;'>" + loggingContents + "</span>";
				}else{
					logdata.innerHTML += "<span style='color:purple;'>" + loggingContents + "</span>";
				}
				cnt++;
			}
			if(logdata.innerHTML.length > <%=loggingLength%>){
				logdata.innerHTML = logdata.innerHTML.substring(logdata.innerHTML.length - <%=loggingLength%>);
				document.body.scrollTop = document.body.scrollHeight + <%=loggingLength%>;
			}
			if(loggingContents.length > 0){
				document.body.scrollTop = document.body.scrollHeight + <%=loggingLength%>;
			}
			
		} else {
			//alert("error: "+httpRequest.status);
		}
	}
	return false;
}

XHR = new xhr.Request();
//window.onload = startDocList;
window.onload = getLogging;
</script>
</rquser:rqLog>
</head>
<body leftmargin="0" topmargin="0" marginheight="0" marginwidth="0">
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
<tr>
	<td id="logdata" style="font-size:11px;">logdata Area</td>
</tr>
</table>
</body>
</html>