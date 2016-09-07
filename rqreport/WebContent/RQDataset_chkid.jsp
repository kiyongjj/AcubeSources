<%@ page language="java" contentType="text/html;charset=UTF-8" %><%
/**
 * 이페이지는 업무단에서 사용되어지는 페이지 이며 클라이언트에서 REQUBE호출이 가능할때 사용
 * 이 영역에 Session 에 대한 처리 로직이 들어가아함.
 **/
%><%
String action = request.getParameter("action") == null ? "" : request.getParameter("action");
String doc = request.getParameter("doc") == null ? "" : request.getParameter("doc");
String runvar = request.getParameter("runvar") == null ? "" : request.getParameter("runvar");
String encqry = request.getParameter("encqry") == null ? "" : request.getParameter("encqry");

String callUrl = "http://localhost:8080/rqreport/RQDataset.jsp?action=" + action
               + "&doc=" + doc
               + "&runvar=" + runvar 
               + "&encqry=" + encqry;

response.sendRedirect(callUrl);
%>