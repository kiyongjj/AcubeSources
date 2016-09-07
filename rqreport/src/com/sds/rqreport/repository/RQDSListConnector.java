package com.sds.rqreport.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class RQDSListConnector implements InfoConnector {

	JDBCHelper jdbc;
	RepositoryEnv env;

	public RQDSListConnector() {
		super();
		jdbc = new JDBCHelper();
		env = RepositoryEnv.getInstance();
	}

	public int writeInfo(RQInfo info) {
		String qry = "Insert into " + env.doc_dsTableName + " (nDOCID, DS_NM, nORDER) values ("
		+ info.getParamInt(0)+ ", '" +info.getParamString(1)+ "', "+ info.getParamInt(2) +")";


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

	public RQInfo getInfo(Object key) throws Exception {
		return null;
	}

	public int deleteInfo(Object key) {
		String qry = "Delete from " + env.doc_dsTableName ;
		if(key instanceof Integer)
		{
			qry += " where nDOCID = " + key.toString();
		}
		else if(key instanceof String)
		{
			qry += " where DS_NM = '" + key.toString() + "'";
		}

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

	public int updateInfo(Object key, RQInfo info) {

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

	public ArrayList listAll() {
		return null;
	}

	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public ArrayList getMutiInfo(Object condition) {
		String qry = "Select nDocID, DS_NM, nORDER from " +  env.doc_dsTableName;
		ArrayList dslist = new ArrayList(100);
		if (condition instanceof Integer)
		{

			qry += " where nDOCID = " + condition;
			qry += " order by nORDER";
			try
			{
				jdbc.connect();
				ResultSet rs = jdbc.getRs(qry);
				DSListInfo dsListInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())
				{
					dsListInfo = new DSListInfo();
					dsListInfo.docID = rs.getInt(1);
					dsListInfo.DSName  = rs.getString(2);
					dsListInfo.order   = rs.getInt(3);
					dslist.add(dsListInfo);
				}



			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{

		}
		return dslist;
	}

	public boolean closeConnection() {
		this.jdbc = null;
		return false;
	}

	public void setConnection(JDBCHelper jdbc) {
		this.jdbc = jdbc;

	}

	public Calendar makeCal(String date) {
		return null;
	}

}
