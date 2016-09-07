<%@ page language="java" pageEncoding="UTF-8"%><%
/**
 * 이페이지는 업무단에서 사용되어지는 페이지 이며 클라이언트에서 REQUBE호출이 가능할때 사용
 * 이 영역에 업무단 Session에 대한 처리 로직이 들어가야  함.
 **/
%><%
String enc = request.getParameter("enc") == null ? "" : request.getParameter("enc");
String doc = request.getParameter("doc") == null ? "" : request.getParameter("doc");
String getType = request.getParameter("getType") == null ? "" : request.getParameter("getType");

String callUrl = "http://localhost:8080/rqreport/document/getreport.jsp?doc=" + doc
		       + "&enc=" + enc
			   + "&getType=" + getType;
response.sendRedirect(callUrl);
%>