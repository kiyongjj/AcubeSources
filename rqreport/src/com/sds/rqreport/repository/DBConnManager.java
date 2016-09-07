package com.sds.rqreport.repository;

import java.sql.*;
import java.io.*;
import java.util.*;

import javax.naming.*;
import javax.sql.DataSource;

import org.apache.log4j.*;

import com.sds.rqreport.*;
import com.sds.rqreport.util.RequbeUtil;


public class DBConnManager {
  Vector connArr;
  static Logger L = Logger.getLogger("REPOSITORY");
  int connsize = 0;
  int connectionCount = 2;
  int index = 0;
  
  DBConnManager() {
	// Connection size
	
	connArr = new Vector(15);
	try {
	  for (int i = 0; i < connectionCount; ++i) {
//		L.debug(i + " Connected...");
		connArr.add(connect());
	  }
	}
	catch (Exception e) {
	  L.debug(e);
	}

  }

  public Connection connect() throws Exception {
	//L.debug("ConnectAdd()++");
	Environment tenv  = Environment.getInstance();
	RepositoryEnv env = RepositoryEnv.getInstance();
	if(env.jndiName != null && env.jndiName.length() > 0)
	{
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
   			ds = (DataSource)envContext.lookup(env.jndiName);
   		}catch(NamingException e){
   			ds = (DataSource)initContext.lookup(env.jndiName);
   		}
		
		Connection con = ds.getConnection();		
		return con;
	}
	else
	{
		Class.forName(env.jdbcDriver).newInstance();
		Connection c = DriverManager.getConnection(env.connStr);
		if(c != null){
			RequbeUtil.setTransaction(c, "setAutoCommit", true);
		}
		return c;
	}

  }

  public Connection replaceConnection(Connection c)
  {

	
	if(connArr.remove(c))
	{
		connsize--;
	}
	if (c != null)
	{
		try
		{
			c.close();
		}catch(Exception ex)
		{
		  L.error("replaceconnection close : " + ex);
  			
		}
		c = null;
	}
	Connection newConn = null;
	try
	{
		newConn = connect();
		connsize++;
		connArr.add(newConn);
		return newConn;
	}catch(Exception e)
	{
		return null;
	}

    
  }

  public synchronized Connection getConn() {
    Environment tenv  = Environment.getInstance();
	RepositoryEnv env = RepositoryEnv.getInstance();
	if(env.jndiName != null && env.jndiName.length() > 0)
	{
	        try {
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
		   			ds = (DataSource)envContext.lookup(env.jndiName);
		   		}catch(NamingException e){
		   			ds = (DataSource)initContext.lookup(env.jndiName);
		   		}

		   		Connection con = ds.getConnection();
				return con;
			} catch (NamingException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	connsize++;
	index++;
	if (index >= connArr.size()) {
	  index = 0;
	}
		Connection conn = null;
		if(connArr.size() > 0)
		{
			 conn = (Connection) connArr.get(index);
		}
		else
		{
			return null;
		}
		
	try {
	  if (conn.isReadOnly()) {
		connArr.remove(index);
        L.debug("closed db");
	  }
	  else {
	  	if(connArr.size() < connectionCount)
			{
			  for(int ii = 0; ii < (connectionCount-1); ii++) {
				  try {
					conn = connect();
					connArr.add(conn);
				  }
				  catch (Exception e) {
						L.debug(e);
						try {
						  Thread.sleep(10000);
						}
						catch (Exception ex) {
						  L.debug(ex);
						}
					}
				}
			}
			return conn;
	  }
	}
	catch (SQLException e) {
	  L.debug("closed db" + e);
	  connArr.remove(index);
	}
	return conn;
  }

  public synchronized void  releaseConn() {
	connsize--;
	//L.debug("ConnectRelease--------------------------------:" + connsize);
	return;
  }

}
