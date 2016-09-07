package com.sds.rqreport.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.sds.rqreport.Environment;
import com.sds.rqreport.common.RQInfo;
import com.sds.rqreport.util.RequbeUtil;

public class RQDocConnector implements InfoConnector {

	JDBCHelper jdbc = null;
	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#writeInfo(com.sds.reqube.common.RQInfo)
	 */
	RepositoryEnv env = null;
	Environment tenv = Environment.getInstance();
	
	public RQDocConnector()
	{
		jdbc = new JDBCHelper();
		env = RepositoryEnv.getInstance();
	}

	public int writeInfo(RQInfo info) {
		try
		{
			jdbc.connect();
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
			
			String qry = "Insert into  "  + env.docTableName + "(nDocID, File_NM, DOC_FG, Full_PATH, nPDocID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE)";
			qry += " values (" + info.getParamInt(0) + ", '"+ info.getParamString(1)+ "', '" + info.getParamString(2) + "', '" +info.getParamString(3) + "', " + info.getParamInt(4) + ", '" + info.getParamString(5) + "', '" + info.getParamString(6) + "', '" + info.getParamString(7) + "', '"+ info.getParamString(8) +"')";
			jdbc.execute(qry);

			RequbeUtil.setTransaction(jdbc.conn, "commit", true);

		}catch(Exception ex)
		{
			try {
				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
				ex.printStackTrace();
			return -1;

		}finally
		{
			jdbc.close();
		}
		return 0;

	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#getInfo(java.lang.Object)
	 */
	public RQInfo getInfo(Object key) throws Exception {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE from " +  env.docTableName;
		if(key instanceof String)
		{
			qry += " where FULL_PATH = '" + key + "'";
		}
		else if(key instanceof Integer)
			qry += " where nDocID = " + key;
		try
		{
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			DocInfo docInfo = new DocInfo();
			String d = null;
			Calendar cal = null;
			if(rs.next())
			{

				docInfo.idx = rs.getInt(1);
				docInfo.name  = rs.getString(2);
				docInfo.doc_fg  = rs.getString(3).charAt(0);
				docInfo.fullPath  = rs.getString(4);
				docInfo.pdocID = rs.getInt(5);
				docInfo.docDesc = rs.getString(6);
				docInfo.createUserID = rs.getString(7);
			    cal = Calendar.getInstance();
			    d = rs.getString(8);

				docInfo.createDate = makeCal(d);
				cal = Calendar.getInstance();
			    d = rs.getString(9);

				docInfo.modDate = makeCal(d);

			}
			jdbc.close();
			return docInfo;

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#deleteInfo(java.lang.Object)
	 */
	public int deleteInfo(Object key) {
		try {
			int id = Integer.parseInt(key.toString());
			ArrayList arr = getMutiInfo(new Integer(id));
			if(arr.size() != 0)
			{
				return -100;//  하위에 문서가 존재합니다..
			}
			jdbc.connect();
			String qry = "Delete from " + env.docTableName;
			qry += " where nDocID = "+ key;
			jdbc.execute(qry);
			jdbc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#deleteMultiInfo(java.util.ArrayList)
	 */
	public int deleteMultiInfo(ArrayList keylist) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#updateInfo(java.lang.Object, com.sds.reqube.common.RQInfo)
	 */
	public int updateInfo(Object key, RQInfo info) {
		PreparedStatement pstmt = null;
		int rc = 0;

		try {
//			jdbc.connect();
//			String select_qry      = "select  nDocId from " + env.docTableName + " where nDocID = " + key ;
			String update_qry    = "update " + env.docTableName + " set file_nm = '" + info.getParamString(1) + "'"
			                              + "                                                    , doc_fg = '" + info.getParamString(2) + "'"
			                              + "                                                    , full_path='" + info.getParamString(3) + "'"
			                              + "                                                    , npdocid=" + info.getParamInt(4) +" "
			                              + "                                                    , doc_desc='" + info.getParamString(5) +"'"
			                              + "                                                    , create_user_id='" + info.getParamString(6) + "'"
			                              + "                                                    , MOD_DATE='" + info.getParamString(7)  +"'"
					                      + " where nDocID = "+ key;
//			String Insert_qry     = "insert into " + env.doc_dsTableName + "(nDocId, file_nm, doc_fg, full_path, npdocid, doc_desc,  create_user_id, create_date, mod_date) ";
//			          Insert_qry   +="values(" + info.getParamInt(0) + ", '"+ info.getParamString(1)+ "', '" + info.getParamString(2) + "', '" +info.getParamString(3) + "', " + info.getParamInt(4) + ", '" + info.getParamString(5) + "', '" + info.getParamString(6) + "', '" + info.getParamString(7) + "', '"+ info.getParamString(8) +"')";

//			pstmt = jdbc.conn.prepareStatement(select_qry);

//			ResultSet rs=pstmt.executeQuery();

//			if (rs.next()) {  // update
//				rs.close();
//				pstmt.close();

				pstmt = jdbc.conn.prepareStatement(update_qry);

				rc = pstmt.executeUpdate();
//			} else {
//				rs.close();
//				pstmt.close();
//				pstmt = jdbc.conn.prepareStatement(Insert_qry);

//				rc = pstmt.executeUpdate();
//			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#updateMultiInfo(java.lang.Object, java.util.ArrayList)
	 */
	public int updateMultiInfo(Object key, ArrayList infoList) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#listAll()
	 */
	public ArrayList listAll() {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE from " +  env.docTableName;
		ArrayList doclist = new ArrayList(100);
		qry += " order by DOC_FG";
		try
		{
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			DocInfo docInfo = null;
			String d = null;
			Calendar cal = null;
			while(rs.next())
			{
				docInfo = new DocInfo();
				docInfo.idx = rs.getInt(1);
				docInfo.name  = rs.getString(2);
				docInfo.doc_fg  = rs.getString(3).charAt(0);
				docInfo.fullPath  = rs.getString(4);
				docInfo.pdocID = rs.getInt(5);
				docInfo.docDesc = rs.getString(6);
				docInfo.createUserID = rs.getString(7);
			    cal = Calendar.getInstance();
			    d = rs.getString(8);

				docInfo.createDate = makeCal(d);
				cal = Calendar.getInstance();
			    d = rs.getString(9);

				docInfo.modDate = makeCal(d);
				doclist.add(docInfo);
			}



		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return doclist;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#writeAll(java.util.ArrayList)
	 */
	public int writeAll(ArrayList alllist) {
		return 0;
	}

	public ArrayList getMutiInfo(Object condition) {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE from " +  env.docTableName;
		ArrayList doclist = new ArrayList(100);
		if (condition instanceof Integer)
		{

			qry += " where nPDOCID = " + condition;
			qry += " order by DOC_FG";
			try
			{
				jdbc.connect();
				ResultSet rs = jdbc.getRs(qry);
				DocInfo docInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())
				{
					docInfo = new DocInfo();
					docInfo.idx = rs.getInt(1);
					docInfo.name  = rs.getString(2);
					docInfo.doc_fg  = rs.getString(3).charAt(0);
					docInfo.fullPath  = rs.getString(4);
					docInfo.pdocID = rs.getInt(5);
					docInfo.docDesc = rs.getString(6);
					docInfo.createUserID = rs.getString(7);
				    cal = Calendar.getInstance();
				    d = rs.getString(8);

					docInfo.createDate = makeCal(d);
					cal = Calendar.getInstance();
				    d = rs.getString(9);

					docInfo.modDate = makeCal(d);
					doclist.add(docInfo);
				}



			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{

		}
		return doclist;
	}

	public Calendar makeCal(String date)
	{
		Calendar cal = null;
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(date.substring(0,4)));
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4,6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6,8)));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date.substring(8,10)));
		cal.set(Calendar.MINUTE, Integer.parseInt(date.substring(10,12)));
		cal.set(Calendar.SECOND, Integer.parseInt(date.substring(12,14)));
		return cal;
	}

	public boolean closeConnection() {
		jdbc = null;
		return false;
	}

	public void setConnection(JDBCHelper jdbc) {
		this.jdbc = jdbc;

	}




}
