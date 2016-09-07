package com.sds.rqreport.service.queryexecute;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.model.RQCacheContent;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.util.RequbeUtil;

public class RQGetDataStr extends RQGetDataIf{

    int idx = 1;
    Hashtable connInfoRep = new Hashtable();
    Hashtable stmtInfoRep = new Hashtable();
    StringBuffer strBf = new StringBuffer();

    String strDriver;
    String strConn;
    String strId;
    String strPw;
    Collection oSQLArray;
    int iStmtidx;
    private Logger log = Logger.getLogger("RQQRYEXE");
    
    public RQGetDataStr(String strDriver, String strConn, String strId, String strPw, Collection oSQLArray, int iStmtidx){

        this.strDriver = strDriver;
        this.strConn = strConn;
        this.strId = strId;
        this.strPw = strPw;
        this.oSQLArray = oSQLArray;
        this.classType = STR_TYPE;
    }

    public void connect(){}

    public void fetch(){}

    public void close(){}

    /**
     * Method : makeConnection 
     * @param 
     * @return int
     */  
    public synchronized int makeConnection(String strDriver, String strConn, String strId, String strPw) throws Exception
    {
        Connection con = connect(strDriver, strConn, strId, strPw);
        log.debug(idx + " connected");
        int iConnId = idx;
        connInfoRep.put("" + idx++, con);
        return iConnId;
    }  
    
    /**
     * Method : connect 
     * @param 
     * @return Connection
     */    
    public static Connection connect(String strDriver, String strConn, String strId, String strPw) throws Exception
    {
        Class.forName(strDriver).newInstance();
        Connection con = DriverManager.getConnection(strConn, strId, strPw);
        return con;
    }
    
    /**
     * Method : queryPrepare 
     * @param 
     * @return int
     */    
    public synchronized int queryPrepare(int iConnId, String strQry, int iStmtidx) throws SQLException
    {
        Connection con = (Connection)connInfoRep.get(""+iConnId);
        PreparedStatement pstmt = queryPrepare(con, strQry);
        stmtInfoRep.put("" + iStmtidx, pstmt);
        return iStmtidx;
    }
    
    /**
     * Method : queryPrepare 
     * @param 
     * @return PreparedStatement
     */    
    public static PreparedStatement queryPrepare(Connection con, String strQry) throws SQLException
    {
        PreparedStatement pstmt = con.prepareStatement(strQry);
        return pstmt;
        
    }

    /**
     * Method : queryExecute 
     * @param 
     * @return boolean
     */
    public boolean queryExecute(int stmtid) throws SQLException
    {
        PreparedStatement pstmt = (PreparedStatement)stmtInfoRep.get(""+stmtid);
        return pstmt.execute();
    }
    
    /**
     * Method : getFetch 
     * @param 
     * @return boolean
     */    
    public String getFetch(int iStmtidx) throws SQLException
    {
        Statement stmt = (Statement)stmtInfoRep.get(""+iStmtidx);
        strBf.delete(0,strBf.length());
        return getFetch(stmt, strBf);
    }
    
    
    /**
     * Method : getFetch
     * @param 
     * @return String
     */  
    public String getFetch(Statement stmt, StringBuffer strBf) throws SQLException
    {
        ResultSet rs =  stmt.getResultSet();
        ResultSetMetaData rsMd = rs.getMetaData();
        int iColumnCount = rsMd.getColumnCount();
        RQHierarchyBindingSource oRQHBindingSource = new RQHierarchyBindingSource();
        
        strBf.append(SSEP);
        while(rs.next()){
            strBf.append(RQFetchExecute.objectToString(rsMd, rs, iColumnCount, null, QueryHouse.RQ_QUERY_SQL, null, DatabaseMetaData.procedureColumnUnknown, 0 , oRQHBindingSource, null, null, false));
            strBf.append(COL_SEP);
        }    
        rs.close();
        return strBf.toString();        
    }
    
    /**
     * Method : endFetch
     * @param 
     * @return void
     */  
    public void endFetch(int stmtidx) throws SQLException
    {
        Statement stmt = (Statement)stmtInfoRep.get(""+stmtidx);
        stmt.close();
    }
    
    /**
     * Method : Direct Connect
     * @param 
     * @return String
     */
    public String getRQResult(){
        
        int idnum=0;
        String result = "";
        int iStmtidxInMap = 0;
        Connection lm_con = null;
        RQCacheContent rqContent = null;

        try {
        	rqContent = new RQCacheContent();
        	idnum = makeConnection(strDriver, strConn, strId, strPw);
        	lm_con = (Connection)connInfoRep.get(Integer.toString(idnum));
        	
			if(isStopStatus())
			{
				log.debug("RQGetDataStr getRQResult stop 1");
				rqContent.setStopStatus();
				rqContent.setReturnStrObject(result);
				setRQCacheContent(rqContent);
				return result;
			}
			
			Iterator itSQL = oSQLArray.iterator();            
			while(itSQL.hasNext()) {
		        iStmtidxInMap =  queryPrepare(idnum, itSQL.next().toString(), iStmtidx);
		        queryExecute(iStmtidxInMap);
		        result += getFetch(iStmtidxInMap);
		        endFetch(iStmtidxInMap++);
		        
		        if(isStopStatus())
				{
					log.debug("RQGetDataStr getRQResult stop 2");
					rqContent.setStopStatus();
					rqContent.setReturnStrObject(result);
					setRQCacheContent(rqContent);
					return result;
				}
			}
			rqContent.setReturnStrObject(result);
			setRQCacheContent(rqContent);
		} catch (RuntimeException re) {
			RequbeUtil.do_PrintStackTrace(log, re);
		} catch (SQLException se) {
			RequbeUtil.do_PrintStackTrace(log, se);
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		} finally {
			try{
			    lm_con.close();
			}catch(SQLException e){
				RequbeUtil.do_PrintStackTrace(log, e);
			}finally{
			    try{
			        if (lm_con != null) lm_con.close();
			    }catch(SQLException e){}
			}
		}
        return result;
    }

}
