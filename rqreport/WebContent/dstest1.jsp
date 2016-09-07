<%@ page contentType="text/html;charset=euc-kr" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="javax.rmi.*" %>
<%@ page import="java.rmi.*" %>
<%
        Context ctx = null;
        DataSource ds = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
                ctx = new InitialContext();
                ds = (DataSource) ctx.lookup("jdbc/REQUBE");
                con = ds.getConnection();
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT count(*) FROM dual");

%>
        <H1>DataSource Pool Test</H1>
        <table border="1" cellspacing="1">
        <caption>TEST</caption>
        <tr align=center>
                <td>count</td>
        </tr>
<%
        while (rs.next())
        {
                System.out.println(rs.getInt(1));
%>
        <tr align=center>
                <td><%=rs.getInt(1)%></td>
        </tr>
<%
        } //end of while
%>
        </table>




<%
        } catch(SQLException se){
                out.println("Exception Occured");
                se.printStackTrace();
        } finally {
                try{ if( rs != null ) rs.close(); } catch(Exception e) {}
                try{ if( stmt != null ) stmt.close(); } catch(Exception e) {}
                try{ if( con != null ) con.close(); } catch(Exception e) {}
        }
%>

