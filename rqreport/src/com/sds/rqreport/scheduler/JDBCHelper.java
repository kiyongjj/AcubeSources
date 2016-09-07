package com.sds.rqreport.scheduler;

import java.sql.*;
import java.io.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.repository.RepositoryEnv;
import com.sun.jndi.toolkit.dir.DirSearch;

class IDGen {
	Hashtable ids;
	Vector UniqueDocID = null;
	Vector UniqueFolderID = null;
	IDGen(){
		ids = new Hashtable();
	}

	public int getID(String key){
		Integer id = (Integer)ids.get(key);
		if (id == null)
			return -1;
		else
			return id.intValue();
	}

	public void clear(){
		ids.clear();
	}

	public void setID(String key,int val){
		ids.put(key, new Integer(val));
	}
}

public class JDBCHelper { //
	static Logger L = Logger.getLogger("SCHEDULER");
	//  static DBConnManager dbmnn;
	static IDGen ids;
	static {
		//	dbmnn = new DBConnManager();
		ids = new IDGen();
	}
	static DataSource ds = null;

	boolean connection = false;
	boolean connection2 = false;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SchedulerEnv env = null;

	public JDBCHelper() {
		connection = false;
		env = SchedulerEnv.getInstance();
	}

	public boolean connect() throws Exception {
	  	Environment tenv  = Environment.getInstance();
	  	RepositoryEnv env = RepositoryEnv.getInstance();
	  	
	  	if(env.jndiName != null && env.jndiName.length() > 0){
			if(ds == null){
	       		Context initContext = new InitialContext();
	       		String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
	       		NamingEnumeration oNenum = null;
	       		try{
	       			oNenum = initContext.listBindings(bind_str);
	       			if(!oNenum.hasMore()){
	       				bind_str = "java:comp/env";
	       			}
	       			Context envContext  = (Context)initContext.lookup(bind_str);
	       			ds = (DataSource)envContext.lookup(env.jndiName);
	    	  		L.debug("jndi connnect : " + env.jndiName);
	       		}catch(NamingException e){
	       			try{
	       				ds = (DataSource)initContext.lookup(env.jndiName);
	       			}catch(Exception dse){
	       				return connect_direct();
	       			}
	       		}
			}
			conn = ds.getConnection();
	  	}else{	  		
	  		return connect_direct();
	  	}
	  	stmt = conn.createStatement();
		return connection;
	}
  
	public boolean connect_direct() throws Exception{
		
		SchedulerEnv senv = SchedulerEnv.getInstance();
		L.debug("direct connnect");
		Class.forName(senv.scheduleJdbcDriver).newInstance();
		conn = DriverManager.getConnection(senv.scheduleJdbcConnection);
		connection = true;
		
		return connection;
	}

	public ResultSet getRs(String strQry) throws Exception {
		String convQry = null;
		try{
			if(stmt == null){
				stmt = conn.createStatement();
			}
			rs = stmt.executeQuery(strQry);
		}catch(SQLException ex){
			L.error(ex);
			L.error(strQry);
		}finally{
		}
		// stmt = null;
		return rs;
	}

	public int execute(String strQry) throws Exception {
		// Statement stmt = conn.createStatement();
		String convQry = null;
		//Statement stmt = null;

		int ret = 0;
		try {
			if(stmt == null){
				stmt = conn.createStatement();
			}
			ret = stmt.executeUpdate(strQry);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			if(stmt != null)
				stmt.close();
		}
		return ret;
	}

	public void close() {
		try {
			if(stmt != null) {
				stmt.close();
				stmt = null;
			}
			if(rs != null ){
				rs.close();
			}
		  if(conn != null){
			  conn.close();
		  }
	  } catch (Exception ex) {
		  rs = null;
		  conn = null;
		  stmt = null;
	  }
	}

	public boolean deleteFile(int docID, String path) {
		File f;
		try {
			f = new File(path);
			f.delete();
		} catch (Exception e) {
			L.error(e);
		}
		f = null;
		return true;
	}

	public synchronized int getID(String tableName, String fieldName) throws Exception {
		String key = tableName +"." + fieldName;
		int id = ids.getID(key);

		if(id < 0){
			ResultSet rs;
			String qry = "SELECT MAX(" + fieldName + ") from " + tableName;
			rs = getRs(qry);
			if (rs.next()) {
				int ret = rs.getInt(1) + 1;
				rs = null;

				ids.setID(key, ret);
				return ret;
			}else{
				rs = null;
				ids.setID(key, 1);
				return 1;
			}
		}else{
			id++;
			ids.setID(key, id);
			return id;
		}
	}

	public int getUniqueID(String tableName, String fieldName) throws Exception {

		int ret = 0;
		ResultSet rs;
		int id = 0;
		int selectid = 0;
		String strErr = "OverFlow the range : " + fieldName;

		if((ids.UniqueDocID != null) && (ids.UniqueDocID.size() != 0 ) && (tableName.compareToIgnoreCase("RDoc") == 0)) {
			ret = ((Integer)(ids.UniqueDocID.firstElement())).intValue();
			ids.UniqueDocID.remove(new Integer(ret));
			return ret;
		} else if((ids.UniqueFolderID != null) && (ids.UniqueFolderID.size() != 0) && (tableName.compareToIgnoreCase("RFolder") == 0)){
			ret = ((Integer)(ids.UniqueFolderID.firstElement())).intValue();
			ids.UniqueFolderID.remove(new Integer(ret));
			return ret;
		}

		String qry = "SELECT " + fieldName + " from " + tableName + " ORDER BY " + fieldName + " ASC";
		rs = getRs(qry);

		rs = null;
		return ret;
	}
}