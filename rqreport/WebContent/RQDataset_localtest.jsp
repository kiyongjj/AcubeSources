<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.io.*" %>
<%
String dnfilename = request.getParameter("filename");

if(request.getHeader("User-Agent").indexOf("MSIE 5.5") > -1) {
 	response.setHeader("Content-Disposition", "filename="+dnfilename);
} else {
	response.setHeader("Content-Disposition", "attachment; filename="+dnfilename);
}

//String strPath = pageContext.getServletContext().getRealPath("environment/" + filename);

File f = new File("d:/WAS/Tomcat5.0.28/webapps/rqreport/rqcheck/rqvdownload/DS091818223000313734_HTTP");
response.setContentLength((int)f.length());

byte buffer[] = new byte[2048];
BufferedInputStream bi;
try {
	bi = new BufferedInputStream(new FileInputStream(f));
	BufferedOutputStream bo = new BufferedOutputStream(response.getOutputStream());

	try {
	 	int n=0;
	 	while((n = bi.read(buffer,0,2048)) != -1) {
	  		bo.write(buffer,0,n);
	  	}
	}catch(IOException e){
		e.printStackTrace();
	} finally {
	 	bo.close();
	 	bi.close();
	}
} catch (FileNotFoundException e1) {
	e1.printStackTrace();
} catch(IOException e){
	e.printStackTrace();
}
%>