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
String jndiname = request.getParameter("jndiname");

String constr = request.getParameter("constr");
String userid = request.getParameter("userid");
String userpw = request.getParameter("userpw");
String classnm = request.getParameter("classnm");
String sqlstr = request.getParameter("sqlstr");

Connection conn = null;
Statement stmt = null;
ResultSet rs = null;

try{
	if(jndiname != null && !jndiname.equals("")){
		Context initContext = new InitialContext();
	   	String bind_str = "";
		NamingEnumeration oNenum = null;
		DataSource ds = null;
		try{
                        out.println("11111111111111111");
			oNenum = initContext.listBindings(bind_str);
			if(!oNenum.hasMore()){
				bind_str = "java:comp/env";
			}
			Context envContext  = (Context)initContext.lookup(bind_str);
			ds = (DataSource)envContext.lookup(jndiname);
		}catch(Exception e){
                        out.println("2222222222222221111111");
			ds = (DataSource)initContext.lookup(jndiname);
		}
	   	conn = ds.getConnection();
	}else{
		Class.forName(classnm);
		conn = DriverManager.getConnection(constr, userid, userpw);
	}

	stmt = conn.createStatement();
  	rs= stmt.executeQuery(sqlstr);
  	
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

 
