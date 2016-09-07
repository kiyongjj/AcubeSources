package com.sds.rqreport.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class RQUploadDocDAO {

	static DataSource ds = null;
	boolean connection = false;
	Connection conn = null;
	Statement stmt = null;
	  
	public boolean connect(String p_jdbcDriver, String p_connStr) throws Exception {
		Class.forName(p_jdbcDriver).newInstance();
		conn = DriverManager.getConnection(p_connStr);		  
		stmt = conn.createStatement();
		return connection;
	}
	
	public void close() {
		// Statement 만  close;
		try {
			if(stmt != null)
				stmt.close();

		} catch (Exception ex) {
			conn = null;
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				conn = null;
			}		
		}
	}
}
