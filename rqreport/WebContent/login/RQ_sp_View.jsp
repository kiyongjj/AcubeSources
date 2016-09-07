<%
/**
 * <p>Title: REQUBE REPORT</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Samsung SDS Co., Ltd.</p>
 * @author : lee jae gyu
 * @version 1.0
 */
%>

<%@ page language="java" contentType="text/html;UTF-8" %>

<%
HttpSession lm_session = request.getSession(false);

if(lm_session.getAttribute("strUserid") == null){
	response.sendRedirect("login_simple.jsp");
	
}else{
	String sessionid = "";
	sessionid = (String)lm_session.getAttribute("strUserid");

	if(!sessionid.equals("admin")) {
		//out.println("Access Error");
		response.sendRedirect("login_simple.jsp");

	}else{
%>

<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script language="javascript">

	function view(){
		var s_runvar = form1.runvar.value;
		if (s_runvar != ""){
			win.location.href="/REQUBE/rqservlet?cmd=view&doc=cycle.rqd&id=admin&pw=admin&scroll=no";
		}else{
			win.location.href="/REQUBE/rqservlet?cmd=view&doc=cycle.rqd&id=admin&pw=admin&scroll=no&runvar=" + s_runvar ;
		}
	}
	
</script>


</head>
<body>

<form  name="form1" method="post">
<table>
<tr>
	<td>document : </td>
	<td>doc=cycle.rqd / iframe</td>
</tr>
<tr>
	<td>runvar : </td>
	<td><input type="text" name="runvar" value=""/></td>
</tr>
<tr>
	<td colspan="4"><input type="button" value="submit" onClick="javascript:view();"/></td>
</tr>
</table>
</form>

	<br><br>
	<iframe name="win" src="about:blank" width= "710" height="400" scrolling="no" gep="0">iframe</iframe><br><br>

</body>
</html>
<%
	}
}
%>