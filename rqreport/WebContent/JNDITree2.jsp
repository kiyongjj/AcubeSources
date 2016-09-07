<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="javax.sql.DataSource"%>
<%//@ page import="oracle.jdbc.OracleResultSetMetaData" %>
<%!
void getList(String bind_str, Context ctx){
	try	{
		NamingEnumeration oNenum = ctx.listBindings(bind_str);
		while(oNenum.hasMore())	{
			Binding bind = (Binding)oNenum.next();
			System.out.println(((Binding)bind).getName());
			System.out.println(((Binding)bind).getClassName());
			Object obj = ((Binding)bind).getObject();
			System.out.println(obj);
			if(obj instanceof Context){
				getList("" ,(Context)obj);
			}
		}
	}catch(Exception ex){
		ex.printStackTrace();
	}
}

public  void listContext (Context ctx, String indent, Writer out, String pName) throws NamingException{
	NamingEnumeration list = ctx.listBindings("");

	// System.out.println("Listing Context: "+subctx);
	// NamingEnumeration list = ctx.list(subctx);
	try{
		while (list.hasMore()){
			Binding item = (Binding)list.next();
 			String className = item.getClassName();
	    	String name = item.getName();
	    	Object o = item.getObject();
	    	System.out.println(indent + className + " " + pName + name);
		 	out.write(indent + "==" + pName + name + " --> " + o );
			out.write("<br>");
			out.write("<br>");    
			if (o instanceof DataSource){
				out.write("_____________________________real___________________________<br>");
			}
	    	if (o instanceof javax.naming.Context && !pName.equals(name))
	     		listContext ((Context)o, indent+"+", out, pName + name + "/");
		
	 	}
	 }catch(Exception ex){
	 	ex.printStackTrace();
	 }
}
%>
<%
Connection conn = null;
Statement stmt = null;
ResultSet rs = null;
String user = "administrator";
String password = "jeusadmin";

try{
	//PrintWriter out = response.getWriter();
	
	Hashtable envi = new Hashtable();
	
	envi.put(Context.INITIAL_CONTEXT_FACTORY, "jeus.jndi.JNSContextFactory");	
	if (user != null){
       envi.put(Context.SECURITY_PRINCIPAL, user);
       envi.put(Context.SECURITY_CREDENTIALS,
             password == null ? "" : password);
    }
	
	String bind_str = "java:comp";
   	bind_str = request.getParameter("bindstr");
   	if(bind_str == null){
   		bind_str = "java:comp/env";
   	}
   	
	////for tomcat //////////////////////////////////////////
	//bind_str = "java:comp/env";
	////for weblogic8, jeus 5 ///////////////////////////////////
   	bind_str = "";
	/////////////////////////////////////////////////////////////
	
	//// for tomcat //////////////////////////////////////////
	//envi = null;
	///////////////////////////////////////////////////////////
   	Context initContext = new InitialContext(envi);
   	Hashtable env = initContext.getEnvironment();
	Enumeration keys = 	env.keys();

	while(keys.hasMoreElements()){
		//System.out.println(((org.apache.naming.NamingContext)oNenum.next());
		Object key = keys.nextElement();
		System.out.println(key.toString() + " || " + (env.get(key)).toString());
		//System.out.println();
	} 	
	
   
   	out.write("bind_str : " + bind_str +"<br>");
   	Context ctx = (Context)initContext.lookup(bind_str);
  	// Context ctx = initContext;
   	
  	//////////////////////////////////////////////////////////////////////////
  	//listContext (ctx,"+", out, "/");
  	listContext (ctx,"+", out, "/");
  	///////////////////////////////////////////////////////////////////////////
  	
  	//getList(bind_str, ctx);
  	
	//NamingEnumeration oNenum = ctx.listBindings("");
	//System.out.println(initContext.getNameInNamespace());
	//System.out.println(initContext.INITIAL_CONTEXT_FACTORY);
	//System.out.println(initContext.URL_PKG_PREFIXES);
	//while(oNenum.hasMore()){
		//System.out.println(((org.apache.naming.NamingContext)oNenum.next());
		//Binding bind = (Binding)oNenum.next();
		//System.out.println(((Binding)bind).getName());
		//System.out.println(((Binding)bind).getClassName());
		//System.out.println(((Binding)bind).getObject());
	//} 
	//if(!oNenum.hasMore()){
		//bind_str = "java:comp/env";
	//}
	//Context envContext  = (Context)initContext.lookup(bind_str);
	
	//DataSource ds = (DataSource)envContext.lookup("jdbc/Reqube");  
	//conn = ds.getConnection();

	//stmt = conn.createStatement();
  	//rs= stmt.executeQuery("select * From emp ");
  	
	//ResultSetMetaData rsMd = rs.getMetaData();
	//int lm_columnCount = rsMd.getColumnCount();
	//DatabaseMetaData dbmetadata = con.getMetaData();

	}catch(Exception ex){
		ex.printStackTrace();
	}
%>
