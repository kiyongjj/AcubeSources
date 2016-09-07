<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.sds.rqreport.repository.*"%>
<%!
public static int checkRepository(String pathis, String filename){
	int res = 0;
	try {
		DocRepository oDocRepository = new DocRepository();
		res = oDocRepository.getDocID(pathis + filename);
	}catch(Exception e){
		e.printStackTrace();
		return res;
	}
	return res;
}
%>
<% // this page use for search "Repository doc name"
request.setCharacterEncoding("UTF-8");
//RepositoryEnv env = RepositoryEnv.getInstance();
//String rootPath = env.repositoryRoot;
String pathis = request.getParameter("pathis");			// not null
String filename = request.getParameter("filename");		// not null
out.println(checkRepository(pathis, filename));
%>