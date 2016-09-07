package com.sds.rqreport.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class RQUserConnector implements InfoConnector {
	JDBCHelper jdbc;
	RepositoryEnv env = null;
	final static String USERID = "USERID";
	final static String PW = "PW";
	final static String EMAIL = "EMAIL";
	final static String DESC = "USER_DESC";
	final static String GROUP = "GROUPID";
	final static String AUTH = "AUTH";

	public RQUserConnector()
	{

		jdbc = new JDBCHelper();
		env = RepositoryEnv.getInstance();
	}
	public int writeInfo(RQInfo info) {

		try
		{
			jdbc.connect();
			String qry = "Insert into " + env.doc_userTableName + " (" + USERID + ", " + PW + ", "+ EMAIL +", " + DESC +  ", " + GROUP +  ", " + AUTH + ")";
			qry += " values ( '"+ info.getParamString(0)+ "', '" + info.getParamString(1) + "', '" +info.getParamString(2) + "', '" + info.getParamString(3) + "', '" + info.getParamString(4) + "', '" + info.getParamString(5) + "')";
			jdbc.execute(qry);
			jdbc.close();

		}catch(Exception ex)
		{
			ex.printStackTrace();
			return -1;
		}
		return 0;
	}

	public RQInfo getInfo(Object key) throws Exception {
		String qry = "Select " + USERID + ", " + PW + ", "+ EMAIL +", " + DESC +  ", " + GROUP + ", " + AUTH + " from "  + env.doc_userTableName +" where " + USERID + " = '" + key + "'";
		ResultSet rs = null;
		try
		{
			jdbc.connect();
			rs = jdbc.getRs(qry);
			UserInfo userInfo = new UserInfo();
			if(rs.next())
			{

				userInfo.id = rs.getString(1);
				userInfo.pw = rs.getString(2);
				userInfo.email = rs.getString(3);
				userInfo.desc = rs.getString(4);
				userInfo.group = rs.getString(5);
				userInfo.auth = rs.getString(6);
			}


			return userInfo;
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(rs != null)
				rs.close();
			jdbc.close();
		}


		return null;
	}

	public int deleteInfo(Object key) {
		try {
			jdbc.connect();
			String qry = "Delete from " + env.doc_userTableName;
			qry += " where " + USERID + " = '"+ key + "'";
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

	public int updateInfo(Object key, RQInfo info) {
		return 0;
	}

	//end jia1.liu 2008.7.17
    public int updateInfoPw(Object key, RQInfo info) {
    	int res = -1;
		UserInfo userInfo = (UserInfo)info;
		String qry = "UPDATE " + env.doc_userTableName + " SET PW = '" + userInfo.pw + "'," + "USER_DESC = '" + userInfo.desc +"'";
		qry += ",AUTH = '" +userInfo.auth + "'" +
				" where USERID = " + "'"+ key + "'";
		try {
		    jdbc.connect();
			res = jdbc.execute(qry);
			jdbc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}			
		if (res == 1)
			return 0;
			else {return -1;}
	}
    
	public int updateMultiInfo(Object key, ArrayList infoList) {
		return 0;
	}

	public ArrayList listAll() {
		String qry = "Select " + USERID + ", " + PW + ", "+ EMAIL +", " + DESC +  ", " + GROUP + " from "  + env.doc_userTableName;
		ArrayList list = null;
		ResultSet rs = null;
		try
		{
			jdbc.connect();
			 rs = jdbc.getRs(qry);
			list = new ArrayList(50);
			while(rs.next())
			{
				UserInfo userInfo = new UserInfo();
				userInfo.id = rs.getString(1);
				userInfo.pw = rs.getString(2);
				userInfo.email = rs.getString(3);
				userInfo.desc = rs.getString(4);
				userInfo.group = rs.getString(5);
				list.add(userInfo);
			}



		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			jdbc.close();
		}
		return list;
	}

	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public ArrayList getMutiInfo(Object condition) {
		String qry = "Select " + USERID + ", " + PW + ", "+ EMAIL +", " + DESC +  ", " + GROUP + ", " + AUTH +  " from "  + env.doc_userTableName + " where "+ GROUP +" = '" + condition + "'";

		ArrayList list = null;
		ResultSet rs = null;
		try
		{
			jdbc.connect();
			rs = jdbc.getRs(qry);
			list = new ArrayList(50);
			while(rs.next())
			{
				UserInfo userInfo = new UserInfo();
				userInfo.id = rs.getString(1);
				userInfo.pw = rs.getString(2);
				userInfo.email = rs.getString(3);
				userInfo.desc = rs.getString(4);
				userInfo.group = rs.getString(5);
				userInfo.auth = rs.getString(6);
				list.add(userInfo);
			}



		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			jdbc.close();
		}
		return list;

	}
	public boolean closeConnection() {
		//jdbc.close();
		return false;
	}
	public void setConnection(JDBCHelper jdbc) {
		this.jdbc = jdbc;

	}
	public Calendar makeCal(String date) {
		return null;
	}

}
