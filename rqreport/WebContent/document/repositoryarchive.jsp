<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.sds.rqreport.util.*,com.sds.rqreport.*,com.sds.rqreport.repository.*" %>
<%@ page import="java.io.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String method = request.getParameter("method");
String serveridx = request.getParameter("idx");
String targetURL = request.getParameter("target");
String doc = request.getParameter("doc");
String docname = "";
if(doc != null && doc.length() > 0)
{
  docname = new String(doc.getBytes("8859_1"),"KSC5601");
}

int nserveridx;
if(serveridx != null && serveridx.length() > 0)
{
	nserveridx = Integer.parseInt(serveridx);
}

if(method.equalsIgnoreCase("downall"))
{
	Environment env = Environment.getInstance();
	RepositoryEnv renv = env.getRepositoryEnv();
	DocRepository docRep = new DocRepository();
	
	File f = File.createTempFile("RQRPT","TMP");
	docRep.makeRepositoryToZip(f.getPath(),renv.repositoryRoot);
	try
	{
		FileInputStream fi = new FileInputStream(f);
		byte[] data = new byte[10000];
		int size = 0;
		while((size = fi.read(data,0,10000)) > 0)
		{
			out.write("<DATA>");
			out.write(Base64Encoder.encode(data,0,size));
			out.write("</DATA>");
		}
	}catch(Exception ex)
	{
	}
	f.delete();
}
else if(method.equalsIgnoreCase("upall"))
{
	DocRepository docRep = new DocRepository();
	int res = docRep.downloadAllFromServer(targetURL);
	System.out.println("targetURL:" + targetURL);
	if(res == 0)
	{
		out.write("<RESULT>OK</RESULT>");
	}
	else
	{
		out.write("<RESULT>ERROR</RESULT>");
	}
}
else if(method.equalsIgnoreCase("up"))
{
	DocRepository docRep = new DocRepository();
//	int res = docRep.downloadDocFromServer(targetURL);
	int res = 0;
	System.out.println("targetURL:" + targetURL);
	if(res == 0)
	{
		out.write("<RESULT>OK</RESULT>");
	}
	else
	{
		out.write("<RESULT>ERROR</RESULT>");
	}	
}else if(method.equalsIgnoreCase("down"))
{
	
	DocRepository docRep = new DocRepository();
	File f = docRep.getFile(docname);
	if(f != null && f.exists() && f.isFile())
	{
		byte[] fileData;
		fileData = JDBCHelper.readFile(f);
		response.setContentType("Application/x-rqd");
		out.write("<DATA>");
		out.write(Base64Encoder.encode(fileData));
		out.write("</DATA>");
	}

}

%>