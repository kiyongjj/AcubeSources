package com.sds.rqreport.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class RQGroupConnector implements InfoConnector {

	JDBCHelper jdbc;
	public RQGroupConnector()
	{
		jdbc = new JDBCHelper();
	}

	public int writeInfo(RQInfo info) {
		try
		{
			jdbc.connect();
			String qry = "Insert into RGroup (strID,  strParam)";
			qry += "values ( '"+ info.getParamString(0)+ "', '" + info.getParamString(1) + "')";
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
		String qry = "Select strID, strParam from RGroup where strID = '" + key + "'";

		try
		{
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			GroupInfo groupInfo = new GroupInfo();
			if(rs.next())
			{

				groupInfo.id = rs.getString(1);
				groupInfo.params = rs.getString(2);

			}

			jdbc.close();
			return groupInfo;

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public int deleteInfo(Object key) {
		try {
			jdbc.connect();
			String qry = "Delete from RGroup";
			qry += " where strID = '"+ key + "'";
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
		try {
			jdbc.connect();
			String qry = "Update RGroup set strParam = '" + info.getParamString(1)  + "'";
			qry += " where strID = '"+ key +"'";
			jdbc.execute(qry);
			jdbc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public int updateMultiInfo(Object key, ArrayList infoList) {
		return 0;
	}

	public ArrayList listAll() {
		String qry = "Select strID, strParam from RGroup";

		ArrayList list = null;
		try
		{
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
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

			jdbc.close();

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public ArrayList getMutiInfo(Object condition) {
		return null;
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
