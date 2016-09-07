<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.Environment"%>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<!--rquser:rqLog action="logging"/-->
<%
Environment env = Environment.getInstance();
String dir = env.logrqdirname;
out.println("######################"+dir);
String filename = request.getParameter("file");

InputStream is = null;
InputStreamReader isr = null;
BufferedReader br = null;

File f = new File(dir+"/"+filename);
is = new FileInputStream(dir+"/"+filename);

isr = new InputStreamReader(is); 
br =  new BufferedReader(isr); 

String temp = ""; 
out.println("<body leftmargin=\"0\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" style='font-family:verdana;font-size:11px;'>");
while ((temp = br.readLine()) != null) { 
	
	//temp = temp.replaceAll("DEBUG","<font color='green'>DEBUG</font>");
	//temp = temp.replaceAll("INFO","<font color='green'>INFO</font>");
	//temp = temp.replaceAll("WARN","<font color='blue'>WARN</font>");
	//temp = temp.replaceAll("ERROR","<font color='red'>ERROR</font>");
	//temp = temp.replaceAll("FATAL","<font color='red'>FATAL</font>");
	
	temp = temp.replaceAll("<","&lt;");
	temp = temp.replaceAll(">","&gt;");
	
	out.print(temp +"<br>"); 
}
%>