<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.*" %>

<jsp:include flush="true" page="RQtest.jsp"/>

<%
 	StringBuffer sbError = new StringBuffer();
 	DatabaseMetaData dbMetaData = null;
 	Connection conn = null;
%>
<font size="-1"><p>
<%
//DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
try {
	conn = DriverManager.getConnection("jdbc:oracle:thin:@70.7.101.214:1521:worldav", "easybase", "rqdev1");
  	dbMetaData = conn.getMetaData();
%>
<p>
Name of JDBC Driver: <%= dbMetaData.getDriverName() %><br>
Version: <%= dbMetaData.getDriverVersion() %><br>
Major: <%= dbMetaData.getDriverMajorVersion() %><br>
Minor: <%= dbMetaData.getDriverMinorVersion() %><br>
<p>
	Database Name: <%= dbMetaData.getDatabaseProductName() %><br>
	Version: <%= dbMetaData.getDatabaseProductVersion() %><br>
<%
} catch (SQLException e) {
	sbError.append(e.toString());
} finally {
  	if (conn != null) {
		try {
    		conn.close();
   		} catch (SQLException e) {
    		sbError.append(e.toString());
   		}
  	}
}

if (sbError.length() != 0) {
	out.println(sbError.toString());
} else {

%>

<p>No error</font>

<%
}
%>