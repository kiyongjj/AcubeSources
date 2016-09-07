package com.sds.rqreport.site.ems;

import java.sql.*;

import javax.naming.*;
import javax.sql.*;

import com.sds.rqreport.*;
import com.sds.rqreport.repository.*;

/**
 * 커넥션만 JDBCHelper 클래스에서만 관리하고 이외의 모든 자원들
 * 예를들어 stmt, rs 와 같은것들은 그것을 직접 사용한 클래스에서
 * 관리하도록 하자 
 *
 */
public class RQSiteLogJDBCHelper {
	
	DataSource ds = null;
	Connection conn = null;
	Environment tenv  = null;
	RepositoryEnv env = null;
	
	public RQSiteLogJDBCHelper() {
		tenv = Environment.getInstance();
		env  = RepositoryEnv.getInstance();
	}
	
	public void connect() throws Exception {
		if(ds == null){
			Context initContext = new InitialContext();
			String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
			NamingEnumeration oNenum = null;
			try{
				oNenum = initContext.listBindings(bind_str);
				if(!oNenum.hasMore()){
					bind_str = "java:comp/env";
				}
				Context envContext = (Context)initContext.lookup(bind_str);
				ds = (DataSource) envContext.lookup(env.jndiName);
			}catch(NamingException e){
				e.printStackTrace();
			}
		}
		conn = ds.getConnection();	
	}
	
	public void close(){
		if(conn != null) try{ conn.close(); } catch(SQLException e){e.printStackTrace();}
	}
}
