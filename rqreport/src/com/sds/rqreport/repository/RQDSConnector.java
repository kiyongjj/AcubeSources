package com.sds.rqreport.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.common.*;

public class RQDSConnector implements InfoConnector {

	JDBCHelper jdbc;

	public RQDSConnector()
	{
		jdbc = new JDBCHelper();
	}

	public int writeInfo(RQInfo info) {
		try
		{
			jdbc.connect();

		//	String qry = "Insert into RDoc (nDocID, strDoc, strOwnerID, strDS, strRunvar, strDescription, strParam)";
		//	qry += "values (" + info.getParamInt(0) + ", '"+ info.getParamString(1)+ "', '" + info.getParamString(2) + "', '" +info.getParamString(3) + "', '" + info.getParamString(4) + "', '" + info.getParamString(5) + "', '" + info.getParamString(6) + "')";
		//	jdbc.execute(qry);
			String qry = "Insert into RDataSource (strDSName, strDriver, strConnStr, strID, strPW)";
			qry += "values ( '"+ info.getParamString(0)+ "', '" + info.getParamString(1) + "', '" +info.getParamString(2) + "', '" + info.getParamString(3) + "', '" + info.getParamString(4) + "')";
			jdbc.execute(qry);
			jdbc.close();

		}catch(Exception ex)
		{

		}
		return 0;
	}

	public int deleteInfo(Object key) {
		try {
			jdbc.connect();

			String qry = "Delete from RDataSource";
			qry += " where = '"+ key + "')";
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

	public int updateMultiInfo(Object key, ArrayList infoList) {
		return 0;
	}

	public ArrayList listAll() {
		return null;
	}

	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public RQInfo getInfo(Object key) throws Exception {
		String qry = "Select strDSName, strDriver, strConnStr, strID, strPW from RDataSource where strDSName = '" + key + "'";

		try
		{
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			DataSourceInfo dsInfo = new DataSourceInfo();
			if(rs.next())
			{

				dsInfo.dsName = rs.getString(1);
				dsInfo.dsDriver = rs.getString(2);
				dsInfo.connStr = rs.getString(3);
				dsInfo.id = rs.getString(4);
				dsInfo.pw = rs.getString(5);
			}

			jdbc.close();

		}catch(Exception ex)
		{

		}

		return null;
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
