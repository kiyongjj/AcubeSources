package com.sds.rqreport.repository;

import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.Environment;
import com.sds.rqreport.common.RQInfo;
import com.sds.rqreport.util.*;

public class RQSQLConnector implements InfoConnector {

	JDBCHelper jdbc;
	RepositoryEnv env;
	Environment tenv = Environment.getInstance();
	
	public RQSQLConnector() {
		super();
		jdbc = new JDBCHelper();
		env = RepositoryEnv.getInstance ();
	}

	public int deleteInfo(Object key) {
		String qry = "Delete from " + env.doc_sqlTableName ;
//		if(key instanceof Integer)
//		{
			qry += " where nDOCID = " + key.toString();
//		}
//		else
//		{
//			qry += " where nDOCID = -1";
//		}

		try {
			jdbc.connect();
			jdbc.execute(qry);
			jdbc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;

	}

	public int deleteMultiInfo(ArrayList keylist) {
		return 0;
	}

	public RQInfo getInfo(Object key) throws Exception {
		String qry = "Select nDocID, SQL from " +  env.doc_sqlTableName;
		ArrayList sqllist = new ArrayList(100);
		qry += " where nDOCID = " + key.toString();

			try
			{
				jdbc.connect();
				ResultSet rs = jdbc.getRs(qry);
				SQLInfo sqlInfo = null;
				String d = null;
				Calendar cal = null;
				if(rs.next())
				{
					sqlInfo = new SQLInfo(0,"");
					sqlInfo.docID = rs.getInt(1);
					Clob c = rs.getClob(2);
					sqlInfo.sql   = c.getSubString((long) 1, (int) c.length());
					return sqlInfo;
				}

			}catch(Exception ex)
			{
				ex.printStackTrace();
			}

		return null;
	}

	public ArrayList getMutiInfo(Object condition) {
		String qry = "Select nDocID, SQL from " +  env.doc_sqlTableName;
		ArrayList sqllist = new ArrayList(100);
		if (condition instanceof Integer)
		{

			qry += " where nDOCID = " + condition;
			qry += " order by SQLID";
			try
			{
				jdbc.connect();
				ResultSet rs = jdbc.getRs(qry);
				SQLInfo sqlInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())
				{
					sqlInfo = new SQLInfo(0,"");
					sqlInfo.docID = rs.getInt(1);
					sqlInfo.sql   = rs.getString(2);
					sqllist.add(sqlInfo);
				}

			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{

		}
		return sqllist;
	}

	public ArrayList listAll() {
		return null;
	}

	public int updateInfo(Object key, RQInfo info) {
		deleteInfo(key);
		writeInfo(info);
		return 0;
	}

	public int updateMultiInfo(Object key, ArrayList infoList) {
		deleteInfo(key);
		int size = infoList.size();
		for (int i = 0; i < size; ++i)
		{
			writeInfo((RQInfo)infoList.get(i));
		}
		return 0;
	}

	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public int writeInfo(RQInfo info) {
		
		String qry = "Insert into " + env.doc_sqlTableName + " (nDOCID, SQL) values ("
		+ info.getParamInt(0)+ ", EMPTY_CLOB())";
		String qry2 = "Select SQL FROM " + env.doc_sqlTableName + " WHERE nDOCID = ? ";

		try {
			jdbc.connect();
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
			jdbc.execute(qry);
			PreparedStatement ps =jdbc.conn.prepareStatement(qry2);
			ps.setInt(1, info.getParamInt(0));
			ResultSet rs =ps.executeQuery();
			if(rs.next()){
				Clob tmpClob  = rs.getClob(1);
				if (tmpClob != null){
					oracle.sql.CLOB oraClob =
					(oracle.sql.CLOB)tmpClob;
					Writer writer =
					oraClob.getCharacterOutputStream();
					char[] bss = info.getParamString(1).toCharArray();
					writer.write(bss);
					writer.flush();
					writer.close();
				}
			}
			RequbeUtil.setTransaction(jdbc.conn, "commit", true);

			jdbc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public boolean closeConnection() {
		return false;
	}

	public void setConnection(JDBCHelper jdbc) {

	}

	public Calendar makeCal(String date) {
		return null;
	}

}
