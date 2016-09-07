<%@ page contentType = "text/plain; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.repository.*"%>
<%
request.setCharacterEncoding("UTF-8");
String pathis = request.getParameter("pathis");			// not null
DocRepository m_docRep = new DocRepository();
m_docRep.getListFD(pathis, "D");

ArrayList list = (ArrayList)m_docRep.getList();
DocInfo lm_di = null;
out.print(list.size() + "|");
for(int i = 0 ; i < list.size() ; i++){
	lm_di =	(DocInfo) list.get(i);
	out.print(lm_di.fullPath);
	if(i < list.size()-1){
		out.print(",");
	}
}
%>
