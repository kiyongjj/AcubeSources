<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.ResultSetMetaData"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="javax.sql.DataSource"%>
<%//@ page import="oracle.jdbc.OracleResultSetMetaData" %>

<jsp:include flush="true" page="RQtest.jsp"/>

<%
Connection conn = null;
Statement stmt = null;
ResultSet rs = null;

try{
	Context initContext = new InitialContext();
   	String bind_str = "";
	NamingEnumeration oNenum = null;
	DataSource ds = null;
	try{
		oNenum = initContext.listBindings(bind_str);
		if(!oNenum.hasMore()){
			bind_str = "java:comp/env";
		}
		Context envContext  = (Context)initContext.lookup(bind_str);
		ds = (DataSource)envContext.lookup("jdbc/REQUBE");
	}catch(Exception e){
		ds = (DataSource)initContext.lookup("jdbc/REQUBE");
	}
   	conn = ds.getConnection();

 	//Direct connect
 	/**
	String dbUrl = "jdbc:oracle:thin:@70.7.101.214:1521:WorldAV";
	String dbID  = "rqadmin_sun";
	String dbPwd = "easybase";

	Class.forName("oracle.jdbc.driver.OracleDriver");
	conn = DriverManager.getConnection(dbUrl, dbID, dbPwd);
	**/
	stmt = conn.createStatement();
  	rs= stmt.executeQuery("select * From RQDoc");
  	
	ResultSetMetaData rsMd = rs.getMetaData();
    int lm_columnCount = rsMd.getColumnCount();
	//DatabaseMetaData dbmetadata = con.getMetaData();
	
    %>
	
<body>
	<table cellpadding="3" cellspacing="1" border="1">


  	<tr>
  	<% for(int i=1;i<=lm_columnCount;i++){%>
  	
		<td>
		<% 
			String lm_tbn = rsMd.getTableName(i);
			out.println(lm_tbn);
		%>
		</td>
		
	<%}%>
  	</tr>

  	<tr>
  	<% for(int i=1;i<=lm_columnCount;i++){%>
  	
		<td>
		<% out.println(rsMd.getColumnName(i));%>
		</td>
		
	<%}%>
  	</tr>
  		    
	<%
  	while (rs.next() ) { 
  	%>
 	<tr>
  	<% for(int i = 1 ; i <= lm_columnCount ; i++){%>
		<td>
		<% out.print(rs.getString(i)); 	%>
		</td>
	<% } %>
  	
  	</tr>
  	
  	<%
	} 
	%>
	
	</table>
</body>
	
<% 
} catch(Exception e) {
    out.println("error : " + e);
} finally {
	if (rs != null) try { rs.close(); } catch(SQLException ex) {}
	if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
	if (conn != null) try { conn.close(); } catch(SQLException ex) {}
}

%>

 
