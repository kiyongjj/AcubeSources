/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : 
 * </pre>
 */
package com.sds.acube.ndisc.xmigration.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sds.acube.ndisc.xmigration.util.XNDiscMigConfig;

/**
 * XMigration DB 처리
 * 
 * @author Takkies
 *
 */
public class XNDiscMigConnDB {

	/* logger 객체 */
	private static Logger logger = Logger.getLogger(XNDiscMigConnDB.class);

	/* DB Connection 정보 */
	private Connection mConn;

	/* DB Prepared Statement 정보 */
	private PreparedStatement mPstmt;

	/*DB 처리 쿼리 정보 */
	private StringBuffer mSqlBuffer;

	/* DB 처리 쿼리 파라미터 정보 */
	private HashMap<Integer, String> mParam;

	/* DB 연결 정보 공유 정보 */
	private ArrayList<XNDiscMigConnDB> mSharedList;
	
	/* Migration 테이블 명 */
	private static String MIG_TABLE = XNDiscMigConfig.getString("mig-table", "S2N_MIG");

	/**
	 * 생성자 호출 불가하도록 설정
	 */
	private XNDiscMigConnDB() {
	}

	/**
	 * Migration DB 정보 생성
	 * 
	 * @param conn Connection 정보
	 * @return Migration DB 정보
	 */
	public static XNDiscMigConnDB create(Connection conn) {
		XNDiscMigConnDB qry = new XNDiscMigConnDB();
		qry.mConn = conn;
		qry.mSqlBuffer = new StringBuffer(128);
		qry.mParam = new HashMap<Integer, String>();
		return qry;
	}

	/**
	 * Migration DB 정보 공유하여 사용하기
	 * 
	 * @return Migration DB 정보
	 */
	public XNDiscMigConnDB getShared() {
		XNDiscMigConnDB qry = create(mConn);
		if (mSharedList == null) {
			mSharedList = new ArrayList<XNDiscMigConnDB>();
		}
		mSharedList.add(qry);
		return qry;
	}

	/**
	 * 쿼리문 설정하기
	 * 
	 * @param sql 설정할 쿼리문
	 * @return Migration DB 정보
	 */
	public XNDiscMigConnDB appendSQL(String sql) {
		//if (mPstmt != null) {
		//}
		mSqlBuffer.append(sql);
		return this;
	}

	/**
	 * 쿼리 파라미터 설정하기
	 * 
	 * @param seq 파라미터 인덱스
	 * @param value 파라미터 값
	 */
	public void setParam(int seq, String value) {
		mParam.put(new Integer(seq), (value == null) ? "" : value);
	}

	/**
	 * 초기화하기
	 */
	public void init() {
		if (mPstmt != null) {
			closePreparedStatement();
		}
		clear();
	}

	/**
	 * Migration DB 정보 삭제하기
	 */
	private void clear() {
		mSqlBuffer.delete(0, mSqlBuffer.length());
		mParam.clear();
	}

	/**
	 * DB 업데이트 실행하기
	 * 
	 * @return 처리 결과 수
	 * @throws SQLException SQL 에러
	 */
	public int queryUpdate() throws SQLException {
		createStatement();
		try {
			return mPstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * DB 조회하여 Collection으로 넘겨주기
	 * 
	 * @return DB 조회 결과 Collection
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public ArrayList<XNDiscMigData> queryList() throws SQLException, IOException {
		ArrayList<XNDiscMigData> list = new ArrayList<XNDiscMigData>();
		createStatement();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			while (rs.next()) {
				list.add(readResultSet(rs, names, types));
			}
			rs.close();
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
		return list;
	}

	/**
	 * DB 조회하여 Migration Data 객체형으로 넘겨주기
	 * 
	 * @return DB 조회 결과 Data
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public XNDiscMigData queryData() throws SQLException, IOException {
		createStatement();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			XNDiscMigData data = (rs.next()) ? readResultSet(rs, names, types) : new XNDiscMigData();
			rs.close();
			return data;
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 *  DB 조회하여 문자형으로 넘겨주기
	 *  
	 * @return DB 조회 결과 문자
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public String queryString() throws SQLException, IOException {
		createStatement();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			XNDiscMigData data = (rs.next()) ? readResultSet(rs, names, types) : new XNDiscMigData();
			rs.close();
			return data.getString(names[0]);
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 * DB 조회하여 숫자형으로 넘겨주기
	 * 
	 * @param nullValue null 일 경우 기본값
	 * @return DB 조회 결과 숫자
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public int queryInt(int nullValue) throws SQLException, IOException {
		createStatement();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			XNDiscMigData data = (rs.next()) ? readResultSet(rs, names, types) : new XNDiscMigData();
			rs.close();
			return data.getInt(names[0].toUpperCase(), nullValue);
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 * DB 조회하여 문자형 Collection 으로 넘겨주기
	 * 
	 * @return DB 조회 결과 문자형 Collection
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public ArrayList<String> queryStringList() throws SQLException, IOException {
		createStatement();
		ArrayList<String> list = new ArrayList<String>();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			XNDiscMigData data;
			while (rs.next()) {
				data = readResultSet(rs, names, types);
				list.add(data.getString(names[0]));
			}
			rs.close();
			return list;
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 * DB 조회하여 문자형 Map 으로 넘겨주기
	 * 
	 * @return DB 조회 결과 문자형 Map
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	public HashMap<String, String> queryMap() throws SQLException, IOException {
		createStatement();
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			ResultSet rs = mPstmt.executeQuery();
			String[] names = getColumnNames(rs);
			String[] types = getColumnTypes(rs);
			XNDiscMigData data;
			while (rs.next()) {
				data = readResultSet(rs, names, types);
				map.put(data.getString(names[0]), data.getString(names[1]));
			}
			rs.close();
			return map;
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 * Prepared Statement 생성하기
	 * 
	 * @throws SQLException SQL 에러
	 */
	private void createStatement() throws SQLException {
		if (mPstmt == null) {
			mPstmt = mConn.prepareStatement(mSqlBuffer.toString());
		}
		int i = 0;
		String value;
		while ((value = (String) mParam.get(new Integer(++i))) != null) {
			mPstmt.setString(i, value);
		}
	}

	/**
	 * 컬럼명 얻어오기
	 * 
	 * @param rs ResultSet 객체
	 * @return 컬럼명 배열
	 * @throws SQLException SQL 에러
	 */
	private String[] getColumnNames(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int i, count = meta.getColumnCount();
		String[] names = new String[count];
		for (i = 0; i < count; i++)
			names[i] = meta.getColumnName(i + 1).toLowerCase();
		return names;
	}

	/**
	 * 컬럼타입 얻어오기
	 * 
	 * @param rs ResultSet 객체
	 * @return 컬럼타입 배열
	 * @throws SQLException SQL 에러
	 */
	private String[] getColumnTypes(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int i, count = meta.getColumnCount();
		String[] names = new String[count];
		for (i = 0; i < count; i++) {
			names[i] = meta.getColumnTypeName(i + 1);
		}
		return names;
	}

	/**
	 * ResultSet 에서 Migration Data에 값 지정하기
	 * 
	 * @param rs ResultSet 객체
	 * @param names 컬럼명 배열
	 * @param types 컬럼타입 배열
	 * @return Migration Data
	 * @throws SQLException SQL 에러
	 * @throws IOException IO 에러
	 */
	private XNDiscMigData readResultSet(ResultSet rs, String[] names, String[] types) throws SQLException, IOException {
		XNDiscMigData data = new XNDiscMigData();
		String value;
		for (int i = 0; i < types.length; i++) {
			if ("CLOB".equals(types[i]) || "LONG".equals(types[i])) {
				value = readReader(rs.getCharacterStream(i + 1));
				if (value != null)
					data.put(names[i].toUpperCase(), value);
			} else if ("BLOB".equals(types[i])) {
				byte[] bytes = readStream(rs.getBinaryStream(i + 1));
				if (bytes != null)
					data.put(names[i].toUpperCase(), new String(bytes, "ISO8859-1"));
			} else {
				value = rs.getString(i + 1);
				if (value != null)
					data.put(names[i].toUpperCase(), value);
			}
		}
		return data;
	}

	/**
	 * CLOB 데이터 읽기
	 * 
	 * @param reader CLOB 를 읽기위한 Reader 객체
	 * @return CLOB 데이터
	 * @throws IOException IO 에러
	 */
	private String readReader(Reader reader) throws IOException {
		if (reader == null) {
			return null;
		}
		char[] chs = new char[1024];
		int len;
		StringBuffer buffer = new StringBuffer();
		while ((len = reader.read(chs, 0, 1024)) > 0) {
			buffer.append(chs, 0, len);
		}
		reader.close();
		return buffer.toString();
	}

	/**
	 * BLOB 데이터 읽기
	 * 
	 * @param in BLOB 를 읽기위한 Stream 객체
	 * @return BLOB 데이터
	 * @throws IOException IO 에러
	 */
	private byte[] readStream(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}
		byte[] bytes = new byte[1024];
		int len;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((len = in.read(bytes, 0, 1024)) > 0) {
			out.write(bytes, 0, len);
		}
		in.close();
		return out.toByteArray();
	}

	/**
	 * 롤백하기
	 */
	public void rollback() {
		try {
			mConn.rollback();
		} catch (SQLException e) {
			logger.error(e);

		}
	}

	/**
	 * 커밋하기
	 * 
	 * @throws SQLException SQL 에러
	 */
	public void commit() throws SQLException {
		mConn.commit();
	}

	/**
	 * Connection 닫기
	 */
	private void closeConnection() {
		try {
			if (mConn != null)
				mConn.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		mConn = null;
	}

	/**
	 * Prepared Statement 닫기
	 */
	private void closePreparedStatement() {
		try {
			if (mPstmt != null)
				mPstmt.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		mPstmt = null;
	}

	/**
	 * 모든 DB 정보 닫기 처리
	 */
	public void close() {
		if (mSharedList != null) {
			XNDiscMigConnDB qry;
			while (mSharedList.size() > 0) {
				qry = (XNDiscMigConnDB) mSharedList.remove(mSharedList.size() - 1);
				qry.closePreparedStatement();
			}
		}
		closePreparedStatement();
		closeConnection();
	}

	/**
	 * 테이블이 존재하는지 여부 체크
	 * 
	 * @param table 체크할 테이블명
	 * @return 존재하면 true, 존재하지 않으면 false
	 * @throws SQLException SQL 에러
	 */
	public boolean hasTable(String table) throws SQLException {
		try {
			DatabaseMetaData dbm = mConn.getMetaData();
			ResultSet rs = dbm.getTables(null, null, table, null);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error(getQueryTrace());
			throw e;
		}
	}

	/**
	 * Migration 대상 조회용 쿼리(페이징 처리)
	 * 
	 * @param start 시작
	 * @param end 끝
	 * @return Migration 대상 조회용 쿼리문
	 */
	public static String getJSTORList(int start, int end) {
		String stor_media = XNDiscMigConfig.getString("stor-media");
		StringBuilder qry = new StringBuilder();
		
		// - 페이징 - START
		qry.append("SELECT * FROM ( \r\n");
		qry.append("SELECT ROWNUM AS RNUM, A.* FROM ( \r\n");
		// - 페이징 - END
		
		qry.append("select fle_id, fle_name, fle_status, fle_crtdt ");
		qry.append("from file_tbl ");
		qry.append("where fle_mdid = " + stor_media + " ");
		qry.append("and fle_id not in (select mig_fileid from ");
		qry.append(MIG_TABLE).append(")");
		
		// - 페이징 - START
		qry.append(") A WHERE ROWNUM <= " + end);
		qry.append(") WHERE RNUM >  " + start);
		// - 페이징 - END
		
		return qry.toString();
	}

	/**
	 * Migration 대상 건수 쿼리
	 * 
	 * @return Migration 대상 건수 쿼리문
	 */
	public static String getJSTORListCount() {
		String stor_media = XNDiscMigConfig.getString("stor-media");
		StringBuilder qry = new StringBuilder();
		
		qry.append("SELECT COUNT(FLE_ID) CNT FROM (\r\n");
		qry.append("select fle_id, fle_name, fle_status, fle_crtdt ");
		qry.append("from FILE_TBL ");
		qry.append("where fle_mdid = " + stor_media + " ");
		qry.append("and fle_id not in (select mig_fileid from ");
		qry.append(MIG_TABLE).append(")");
		qry.append(")\r\n");
		
		return qry.toString();
	}
	
	/**
	 * Migration 대상 테이블 생성 쿼리
	 * 
	 * @return Migration 대상 테이블 생성 쿼리문
	 */
	public static String getMigrationTable() {
		StringBuffer query = new StringBuffer();
		query.append("CREATE TABLE ");
		query.append(MIG_TABLE).append("(\r\n");
		query.append("MIG_FILEID VARCHAR2(150),\r\n");
		query.append("MIG_DT DATE \r\n");
		query.append(")\r\n");
		return query.toString();
	}

	/**
	 * Migration 결과 기록 쿼리
	 * 
	 * @return Migration 결과 기록 쿼리문
	 */
	public static String getMigrationHistory() {
		StringBuffer query = new StringBuffer();
		query.append("insert into ");
		query.append(MIG_TABLE);
		query.append("(MIG_FILEID, MIG_DT) ");
		query.append("values (?, sysdate) ");
		return query.toString();
	}
	
	/**
	 * 쿼리문 트레이스하기
	 * 
	 * @return 트레이스한 쿼리문
	 */
	private String getQueryTrace() {
		String sql = mSqlBuffer.toString();
		StringBuffer buffer = new StringBuffer(64);

		String value;
		int seq = 1, s = 0, e = 0;
		while ((e = sql.indexOf('?', s)) >= 0) {
			buffer.append(sql.substring(s, e));
			value = (String) mParam.get(new Integer(seq++));
			if (value == null)
				value = "#NULL#";
			buffer.append('\'').append(value).append('\'');
			s = e + 1;
		}
		buffer.append(sql.substring(s));
		return buffer.toString();
	}
}
