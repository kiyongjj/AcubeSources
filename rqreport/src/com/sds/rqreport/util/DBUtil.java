package com.sds.rqreport.util;

import java.sql.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;

public class DBUtil {

	private String userName = "";
	private String jdbc_url = "";
	private String jdbc_url_ip = "";
	private String jdbc_url_sid = "";
	private String jdbc_info = "";
	protected static Logger log = Logger.getLogger("DBUTIL");

	public String getUserName(){
		return userName;
	}
	public String getJdbc_url(){
		return jdbc_url;
	}
	public String getJdbc_url_ip(){
		return jdbc_url_ip;
	}
	public String getJdbc_url_sid(){
		return jdbc_url_sid;
	}
	public String getJdbc_info(){
		return jdbc_info;
	}

	public String bind_str = "";
	public String dataSourceName = "";
	public Context context;

	public DBUtil() throws NamingException {
		context = new InitialContext();
	}

	//public HashMap jndiMap = new HashMap();
	public ArrayList jndiList = new ArrayList();


	/**
	 *  dataSource 이름을 받아 서버 JDBC 정보를 가져온다.
	 * @param dataSourceName
	 * @throws Exception
	 */
	public boolean setDatabaseMetaDataObj(String dataSourceName){
		Environment tenv  = Environment.getInstance();
		try{
			Connection con = null;
			DatabaseMetaData dbMeta;

			Context initContext = new InitialContext();
			String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
			NamingEnumeration oNenum = null;
			DataSource ds = null;
			try{
				oNenum = initContext.listBindings(bind_str);
				if(!oNenum.hasMore()){
					bind_str = "java:comp/env";
				}
				Context envContext  = (Context)initContext.lookup(bind_str);
				ds = (DataSource)envContext.lookup(dataSourceName);
			}catch(Exception e){
				ds = (DataSource)initContext.lookup(dataSourceName);
			}

			con = ds.getConnection();
			dbMeta = con.getMetaData();

			userName = dbMeta.getUserName();
			jdbc_url = dbMeta.getURL();
			jdbc_info = dbMeta.getDriverName() + ":" +dbMeta.getDriverVersion();

			String[] ojdbc_url = jdbc_url.split(":");

			if(ojdbc_url.length < 5) {
				jdbc_url_ip = "";
				jdbc_url_sid = "";
			}else if(ojdbc_url.length == 5){
				jdbc_url_ip = ojdbc_url[3].replaceAll("/", " ");
				jdbc_url_ip = jdbc_url_ip.substring(1, jdbc_url_ip.length());
				jdbc_url_sid = "";
			}else{
				jdbc_url_ip = ojdbc_url[3].replaceAll("/", " ");
				jdbc_url_ip = jdbc_url_ip.substring(1, jdbc_url_ip.length());
				if(!ojdbc_url[2].equalsIgnoreCase("sqlserver")){
					jdbc_url_sid = ojdbc_url[5] ;
				}
			}

			//System.out.println(dbMeta.getURL());
//			System.out.println(dbMeta.getDriverVersion());

			con.close();
			return true;
		}catch(Exception e){
			userName = "";
			jdbc_url = "";
			jdbc_info = "";
			jdbc_url_ip = "";
			jdbc_url_sid = "";
			e.printStackTrace();
		}
		return false;
	}

	public void setDatabaseMetaDataObj(DataSource ds){

		Connection con = null;
		try{
			DatabaseMetaData dbMeta;
			con = ds.getConnection();
			dbMeta = con.getMetaData();

			userName = dbMeta.getUserName();
			jdbc_url = dbMeta.getURL();
			jdbc_info = dbMeta.getDriverName() + ":" +dbMeta.getDriverVersion();

			String[] ojdbc_url = jdbc_url.split(":");
			jdbc_url_ip = ojdbc_url[3];
			jdbc_url_sid = ojdbc_url[5];
			jdbc_url_ip = jdbc_url_ip.substring(1, jdbc_url_ip.length());
			con.close();
			con = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con != null){
				try{
					con.close();
				}catch(SQLException e){

				}
			}
		}
	}

	public Context getInitialContext() throws Exception{
		return new InitialContext();
	}

	/**
	 * Binding Object 를 Enumeration 형태로 받아온다.
	 * @return
	 * @throws NamingException
	 */
	public NamingEnumeration getBindingObj() throws NamingException{
		NamingEnumeration oNenum = context.listBindings(bind_str);
		if(!oNenum.hasMore()){
			bind_str = "java:comp/env";
		}
		oNenum = context.listBindings(bind_str);

		return oNenum;
	}

	/**
	 * NamingEnumeration(Binding)을 받아 binding에서 각 jndi name 과 그 URL 을 Map 에 넣는다.
	 * @param p_oNenum
	 * @throws NamingException
	 */
	public void setJndiMap(NamingEnumeration p_oNenum) throws NamingException{
		while(p_oNenum.hasMore()){
			Binding bindObj = (Binding) p_oNenum.next();
			if(bindObj.getObject() instanceof Context){
				NamingEnumeration sub_enum = context.listBindings(bind_str +"/" + bindObj.getName());

				while(sub_enum.hasMore()){
					Binding subBindObj = (Binding) sub_enum.next();
					dataSourceName = bind_str +"/"+ bindObj.getName() +"/"+ subBindObj.getName();
					//	ex) java:comp/env/jdbc/Reqube
					String jndiname = bindObj.getName() + "/" + subBindObj.getName();

					try {
						//setDatabaseMetaDataObj(dataSourceName);
						//String strURL = getJdbc_url(); //jdbc_url
						//ex) jdbc:oracle:thin:@70.7.101.214:1521:WorldAV

						//jndiMap.put(strURL , jndiname);  //(String,String)

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
	public List getServerDSList(Hashtable env, String bindstr)
	{
		try {
			Context initContext = null;
			if(env != null)	{
				initContext = new InitialContext(env);
			}else{
				initContext = new InitialContext();
			}
			// if tomcat bindstr = "java:comp/env";
			log.debug("bindstr : " + bindstr);
			Context ctx = (Context)initContext.lookup(bindstr);
			searchTree(ctx, "+" , "");
			return jndiList;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ctx 아래에 list를 모두 표시한다.
	 * @param ctx
	 * @param indent
	 * @param pName
	 */
	public void searchTree(Context ctx, String indent, String pName){

 		try{
 			NamingEnumeration list = ctx.listBindings("");
 			
 			while (list.hasMore()){
 				
 				try{
		 			Binding item = (Binding)list.next();
		  			String className = item.getClassName();
				    String name = item.getName();
				    Object o = item.getObject();
				    log.debug(indent+className+" "+name);
				    if(o instanceof DataSource || o instanceof Reference || className.indexOf("PropertyReference") != -1){
				    	// Add List
						String jndiname = pName + "/" + name;
						if(jndiname.startsWith("/")){
							jndiname = jndiname.substring(1);
						}
						//log.debug("jndiname(searchTree) : " + jndiname);
						if(setDatabaseMetaDataObj(jndiname)){
							jndiList.add(jndiname);
						}
				    }
				    if(o instanceof javax.naming.Context ){
				    	searchTree ((Context)o,indent+"+",  pName + "/" +name);
				    }
 				}catch(Exception e1){}
 			}
 			
 		}catch(Exception e){
 			e.printStackTrace();
 		}
	}

	/**
	 * 환경변수에서 server.type, username, password 등을 읽어와 각 서버타잎에 따른
	 * jndi list 를 반환한다.
	 * @param env
	 * @param bindstr
	 * @return
	 */
	public List getServerDsListwithEnv(Hashtable env, String bindstr){
		ResourceBundle lm_rb = ResourceBundle.getBundle("rqreport");
		String lm_strServerType = lm_rb.getString("rqreport.server.type");
		int lm_iServerType = Integer.parseInt(lm_strServerType);
		/*
		 * Server Type weblogic=1, tomcat=2, sun-one,jeus=3, oracle AS=4
		 */
		if(lm_iServerType == 1){
			String user = lm_rb.getString("username");
			String password = lm_rb.getString("password");
			String conTextFactory = lm_rb.getString("java.naming.factory.initial");
			Hashtable envi = new Hashtable();
			envi.put(Context.INITIAL_CONTEXT_FACTORY, conTextFactory);
			if (user != null) {
		       envi.put(Context.SECURITY_PRINCIPAL, user);
		       envi.put(Context.SECURITY_CREDENTIALS,
		             password == null ? "" : password);
		      }
		   	try {
		   		Context initContext = new InitialContext(envi);
				env = initContext.getEnvironment();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}else if(lm_iServerType == 2){
			bindstr = "java:comp/env";
		}else if(lm_iServerType == 3){
			env = null;
			bindstr = "";
		}else if(lm_iServerType == 4){
			Hashtable envi = new Hashtable();
			try {
		   		Context initContext = new InitialContext(envi);
				env = initContext.getEnvironment();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			bindstr = "";
		}else if(lm_iServerType == 5){
			bindstr = "java:comp/env";
		}

		/**
		 * dbutil.getServerDSList(env, "") -------------------------> weblogic, jeus5
		 * dbutil.getServerDSList(null, "java:comp/env") -------> tomcat
		 * dbutil.getServerDSList(null, "") -------------------------> sun-one, jeus4
		 **/

		List jndiList = getServerDSList(env, bindstr);
		return jndiList;
	}

}
