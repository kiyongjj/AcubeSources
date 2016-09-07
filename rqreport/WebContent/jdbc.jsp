<%@ page contentType="text/html;charset=euc-kr" %>
<%@ page import="java.sql.*" %>   
<%@ page import="oracle.jdbc.driver.*" %>   

<h3>Oracle JDBC Info</h3>

<%
	try {
			// Load the Oracle JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	
			//  Change the following code line to reflect a valid connection to your
			//  server
	
			Connection conn = DriverManager.getConnection ("jdbc:oracle:thin:@10.100.120.2:1530:fsserp","etisadm","rmadl3"); 

			// Create Oracle DatabaseMetaData object 
			DatabaseMetaData meta = conn.getMetaData ();
	
			// gets driver info:
			out.println("<br>=======================================================");
			out.println("<br>Database Product Name is ... " + meta.getDatabaseProductName());
			out.println("<br>Database Product Version is  " + meta.getDatabaseProductVersion());
			out.println("<br>=======================================================");
			out.println("<br>JDBC Driver Name is ........ " + meta.getDriverName());
			out.println("<br>JDBC Driver Version is ..... " + meta.getDriverVersion());
			out.println("<br>JDBC Driver Major Version is " + meta.getDriverMajorVersion());
			out.println("<br>JDBC Driver Minor Version is " + meta.getDriverMinorVersion());
			out.println("<br>=======================================================");

	} catch(SQLException se) {
		se.printStackTrace();
	} catch(Exception e) {
		e.printStackTrace();
	}
	
%>

