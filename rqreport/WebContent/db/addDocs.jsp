<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="com.sds.rqreport.repository.*" %>
<%
RepositoryEnv repEnv = RepositoryEnv.getInstance();
DocRepository oDocRepository = new DocRepository();

File f = new File(repEnv.repositoryRoot);
oDocRepository.addDirSeries(f);

//oDocRepository.addDocSeries(docs);
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<script language="javascript">
alert('SQLinsert done !!');
</script>
<head>
<title>rqreport</title>
</head>
<body>
</body>
</html>
