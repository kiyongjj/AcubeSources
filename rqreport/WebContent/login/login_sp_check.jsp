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
	
String strUserid = request.getParameter("strUserid");	
String strUserpw = request.getParameter("strUserpw");	

if(strUserid.equals("admin") && strUserpw.equals("admin")){
	
	HttpSession lm_session = request.getSession();
	String sessionid = (String)lm_session.getAttribute("strUserid");

	if(sessionid == null ){
	
		lm_session.setAttribute("strUserid",strUserid);
		//out.println("New Session Create.<br>");
		//out.println("Session ID : " + lm_session.getId());
		response.sendRedirect("RQ_sp_View.jsp");
	
	}else if(!sessionid.equals("admin")){
		
		if(session != null){
			session.invalidate();
		}
		//out.println("already Session exist !!<br>");
		//out.println("Session ID : " + lm_session.getId());
		response.sendRedirect("login_simple.jsp");
	}else{
		response.sendRedirect("RQ_sp_View.jsp");
	}
	

}else{
	out.println("Login Fail");
	
	%>

	<script language="javascript">
		alert('Login Fail');
		location.href="login_simple.jsp";
	</script>

	<%

}

%>
