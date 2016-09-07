package com.sds.rqreport.site.ems;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Interface 규약에 맞게 총 5개의 메서드를 구현해야하며,
 * 각각의 기능은 서버시작, 패치시작, 패치끝, 서버끝, 최종걸린시간을
 * EMS Log 규약에 맞게 로그를 남긴다. (DB에) <EMS 요구사항임>
 * 또한 Only Oracle로만 디자인 되었다. 
 */
public class RQEMSLogImpl implements RQSiteLogInterface{
	
	RQSiteLogJDBCHelper jdbchelper = null;
	ResourceBundle rb = null;
	
	public RQEMSLogImpl() {
		jdbchelper = new RQSiteLogJDBCHelper();
		rb = ResourceBundle.getBundle("site_EMS_log_sql");
	}
	
	/**
	 * logging step 1 (start)
	 */
	public int StartLogging(RQSiteLogDataInterface data) {
		PreparedStatement pstmt = null;
		int rowCnt = 0;
		try{
			jdbchelper.connect();
			String sql = rb.getString("EMS.logging.StartLogging");
			pstmt = jdbchelper.conn.prepareStatement(sql);
			
			pstmt.setString(1, ((RQEMSLogData)data).getUserid());
			pstmt.setString(2, ((RQEMSLogData)data).getDocpath());
			pstmt.setString(3, "start");
			pstmt.setString(4, strStampToDate( ((RQEMSLogData)data).getEventtime() ));
			
			rowCnt = pstmt.executeUpdate();

		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			if(pstmt != null) try{ pstmt.close(); } catch(SQLException e){e.printStackTrace();}
			jdbchelper.close();			
		}
		return rowCnt;
	}
	
	/**
	 * logging step 2 (Engine start)
	 */
	public int EngineStartLogging(RQSiteLogDataInterface data) {
		PreparedStatement pstmt = null;
		int rowCnt = 0;
		try{
			jdbchelper.connect();
			String sql = rb.getString("EMS.logging.EngineStartLogging");
			pstmt = jdbchelper.conn.prepareStatement(sql);
			
			pstmt.setString(1, ((RQEMSLogData)data).getUserid());
			pstmt.setString(2, ((RQEMSLogData)data).getDocpath());
			pstmt.setString(3, "EngineStart");
			pstmt.setString(4, strStampToDate( ((RQEMSLogData)data).getEventtime() ));
			
			rowCnt = pstmt.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			if(pstmt != null) try{ pstmt.close(); } catch(SQLException e){e.printStackTrace();}
			jdbchelper.close();			
		}
		return rowCnt;
	}
	
	/**
	 * logging step 3 (engine end)
	 */
	public int EngineEngLogging(RQSiteLogDataInterface data) {

		PreparedStatement pstmt = null;
		int rowCnt = 0;
		try{
			jdbchelper.connect();
			String sql = rb.getString("EMS.logging.EngineEngLogging");
			pstmt = jdbchelper.conn.prepareStatement(sql);
			
			pstmt.setString(1, ((RQEMSLogData)data).getUserid());
			pstmt.setString(2, ((RQEMSLogData)data).getDocpath());
			pstmt.setString(3, "EngineEnd");
			pstmt.setString(4, strStampToDate( ((RQEMSLogData)data).getEventtime() ));
			
			rowCnt = pstmt.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			if(pstmt != null) try{ pstmt.close(); } catch(SQLException e){e.printStackTrace();}
			jdbchelper.close();			
		}
		return rowCnt;
	}
	
	/**
	 * logging step 4 (finish)
	 */
	public int FinishLogging(RQSiteLogDataInterface data) {
		PreparedStatement pstmt = null;
		int rowCnt = 0;
		try{
			jdbchelper.connect();
			String sql = rb.getString("EMS.logging.FinishLogging");
			pstmt = jdbchelper.conn.prepareStatement(sql);
			
			pstmt.setString(1, ((RQEMSLogData)data).getUserid());
			pstmt.setString(2, ((RQEMSLogData)data).getDocpath());
			pstmt.setString(3, "Finish");
			pstmt.setString(4, strStampToDate( ((RQEMSLogData)data).getEventtime() ));
			pstmt.setString(5, ((RQEMSLogData)data).getRuntime());
			
			rowCnt = pstmt.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			if(pstmt != null) try{ pstmt.close(); } catch(SQLException e){e.printStackTrace();}
			jdbchelper.close();			
		}
		return rowCnt;
	}
	
	public String strStampToDate(long p_time){
		String strRtnDate = "";
		Date lm_date = new Date(p_time);
		// 원하는 규약에 맞게 바꿀것 
		SimpleDateFormat formatter = new SimpleDateFormat (rb.getString("EMS.logging.DateFormat"));
		strRtnDate = formatter.format(lm_date);
		return strRtnDate;
	}
}
