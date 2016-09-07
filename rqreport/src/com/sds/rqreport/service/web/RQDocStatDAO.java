package com.sds.rqreport.service.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.sql.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.repository.RepositoryEnv;

/**
 * ds와 conn 을 관리하는 DAO 클래스 
 *
 */
public class RQDocStatDAO {
	
	DataSource ds = null;
	Connection conn = null;

	public void connect() throws Exception {
		Environment tenv  = Environment.getInstance();
	  	RepositoryEnv env = RepositoryEnv.getInstance();
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
       		}catch(NamingException e){
       			try{
       				ds = (DataSource)initContext.lookup(env.jndiName);
       			}catch(Exception dse){dse.printStackTrace();}
       		}
		}
		conn = ds.getConnection();
	}
	
	public void close() {
		try{
			if(conn != null){
				conn.close();
		  	}
		}catch (Exception ex) {
			conn = null;
	  	}
	}
	
}
