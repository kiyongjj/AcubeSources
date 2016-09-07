package com.sds.rqreport.service.queryexecute;

import java.sql.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.RQCacheContent;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.util.RequbeUtil;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * RQGetDataObj 클래스는 jndi 이름과 SQL 두값만 받아서 돌아가는 클래스이며
 * 현재 뷰어에서 상세보기를 실행하였을경우 쓰인다.
 * RQGetDataObj.java
 *
 */
public class RQGetDataObj extends RQGetDataIf{

    private boolean flag = false;
    private String strJndiname = "";
    private String strSQL = "";
  
    Connection con = null;
    PreparedStatement  ptmt = null;
    ResultSet rs = null;
    
    StringBuffer strBf = new StringBuffer();
    String result = "";
    private Logger log = Logger.getLogger("RQQRYEXE");
    
    public RQGetDataObj(String strJndiname, String strSQL){
        this.strJndiname = strJndiname;
        this.strSQL = strSQL;
        this.classType = OBJ_TYPE;
    }
    
    public void setFlag(boolean flag){
        this.flag = flag;
    }
 
    public void connect() {
        try {
        	Environment tenv  = Environment.getInstance();
        	
            Context initContext = new InitialContext();
            NamingEnumeration oNenum = null;
            DataSource ds = null;
            String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
            try{
            	oNenum = initContext.listBindings(bind_str);
            	if(!oNenum.hasMore()){
            		bind_str = "java:comp/env";
            	}
            	Context envContext = (Context)initContext.lookup(bind_str);
            	ds = (DataSource)envContext.lookup(strJndiname);
            }catch(Exception e){
            	ds = (DataSource)initContext.lookup(strJndiname);
            }
            con = ds.getConnection();
            
        }catch (NamingException e){
        	RequbeUtil.do_PrintStackTrace(log, e);
        }catch (SQLException e){
        	RequbeUtil.do_PrintStackTrace(log, e);
        }
    }
    
    public void fetch() {
        
        try {
            ptmt = con.prepareStatement(strSQL);
            
            if(isStopStatus())
            {
            	log.debug("fetch stop 1");
				return ;
            }
            ptmt.execute();
            
            if(isStopStatus())
            {
            	log.debug("fetch stop 2");
				return ;
            }
            
            rs =  ptmt.getResultSet();
            ResultSetMetaData rsMd = rs.getMetaData();
            int lm_columnCount = rsMd.getColumnCount();
            //BLOB, CLOB 만 담아서 결과셋 맨뒤에 붙이기 위한 객체
    		RQHierarchyBindingSource oRQHBindingSource = new RQHierarchyBindingSource();
    		
    		//이 클래스는 현재 상세보기에만 쓰이므로 SSEP는 붙이지 않는다.
            //strBf.append(SSEP);
    		
    		//값을 그냥 뿌리지 않고 HTML로 변환하여 뿌려주도록 한다.
    		strBf.append("<table cellpadding=\"3\" cellspacing=\"1\" border=\"1\" bgcolor=\"#AFCDED\" bordercolor=\"#5D6E80\" bordercolordark=\"#9BB7D4\" bordercolorlight=\"#5D6E80\">");
            while(rs.next() && !flag){
                //strBf.append(RQFetchExecute.objectToString(rsMd, rs, lm_columnCount, null, QueryHouse.RQ_QUERY_SQL, null, DatabaseMetaData.procedureColumnUnknown, 0, oRQHBindingSource, null, null));
            	strBf.append("<tr>");
                for(int i = 1 ; i <= lm_columnCount; i++){
                	strBf.append("<td>");
                	strBf.append(rs.getString(i));
                	strBf.append("</td>");
                }
                strBf.append("</tr>");
                
                try {
                	//Thread.sleep(100);
				} catch (Exception e) {
					RequbeUtil.do_PrintStackTrace(log, e);
				}
                if(isStopStatus())
                {
                	log.debug("fetch stop 3");
                	rs.close();
                	result = strBf.toString();
    				return ;
                }
            }
            strBf.append("</table>");
            result =  strBf.toString();
            
        }catch (SQLException e){
        	RequbeUtil.do_PrintStackTrace(log, e);
        }catch(NullPointerException nu){
        	RequbeUtil.do_PrintStackTrace(log, nu);
        }
        
    }
    
    public void close() {
    	if(rs != null){
        	try{
        		rs.close();
        	}catch(SQLException e){
        		RequbeUtil.do_PrintStackTrace(log, e);
        	}
        }
        if(ptmt != null){
        	try {
                ptmt.close();
            } catch (SQLException e) {
            	RequbeUtil.do_PrintStackTrace(log, e);
            }
        }
        if(con != null){
        	try{
        		con.close();
        	}catch(SQLException e){
        		RequbeUtil.do_PrintStackTrace(log, e);
        	}
        }        
    }
    
    /**
     * Method : Connection connect
     * @param 
     * @return void
     */
    public void connect(String p_strJndiname){

    }
    
    /**
     * DB에 Access 하여 Memory 에 RQcacheContent 형태로 저장한다. 
     */  
    public String getRQResult(){

    	RQCacheContent rqContent = null;
    	
        try {
        	rqContent = new RQCacheContent();
			if(con == null) connect();
			
			if(isStopStatus())
			{
				log.debug("getRQResult stop 1");
				rqContent.setQuery(strSQL);
				rqContent.setReturnStrObject(result);
				rqContent.setStopStatus();
				setRQCacheContent(rqContent);
				return "";
			}
			
			fetch();
			
			if(isStopStatus())
			{
				log.debug("getRQResult stop 2");
				rqContent.setQuery(strSQL);
				rqContent.setReturnStrObject(result);
				rqContent.setStopStatus();
				setRQCacheContent(rqContent);
				return "";
			}
			
			rqContent.setQuery(strSQL);
			rqContent.setReturnStrObject(result);
			setRQCacheContent(rqContent);
		} catch (RuntimeException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		} catch(Exception e1) {
			RequbeUtil.do_PrintStackTrace(log, e1);
		} finally {
			close();
		}
		return "";
    }
    
}
