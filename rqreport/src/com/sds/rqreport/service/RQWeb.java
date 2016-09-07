/*
 * Title: REQUBE 2006 Server
 * Description: 
 * Copyright: Copyright (c) 2006
 * Company: Samsung SDS Co., Ltd.
 */

package com.sds.rqreport.service;

import java.sql.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.model.RQCacheContent;

/**
 * RQWeb
 * @version 1.0
 */

public class RQWeb extends RQcommController {
    
    private boolean flag = false;
    private String jndiname = "";
    private String strSQL = "";
    
    private RQCacheContent oRQcacheContent = null;
    
    private Logger L = Logger.getLogger("RQWEB");
    
    public RQWeb(){
    }
    
    public RQWeb(String jndiname, String strSQL){
        this.jndiname = jndiname;
        this.strSQL = strSQL;
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }
    
    public RQCacheContent getRQcacheContent(){
        return oRQcacheContent;
    }
    
    /**
     * DB에 Access 하여 Memory 에 RQcacheContent 형태로 저장한다. 
     */  
    public void getRQResult(){
        
        long startTime = 0;
        long endTime = 0;
        
        startTime = System.currentTimeMillis();

        if(con == null) connect(jndiname);      

        try{
            ptmt = con.prepareStatement(strSQL);
            ptmt.execute();
            
            ResultSet rs =  ptmt.getResultSet();
            ResultSetMetaData rsMd = rs.getMetaData();
            int lm_columnCount = rsMd.getColumnCount();
            
            strBf.append(SSEP);
            while(rs.next() && !flag){
                strBf.append(objectToString(rsMd, rs, lm_columnCount));
                strBf.append(ROW_SEP);
            }    
            result =  strBf.toString();        

        }catch (SQLException e) {
            L.error(e);
        }catch(NullPointerException nu){
            L.error(nu);
        }finally{
            try{
                if(ptmt != null) ptmt.close();
            }catch(SQLException e){}
        }
        endTime = System.currentTimeMillis();

        L.info("DB Fetch time(millisecond) : " + (endTime-startTime));
        
        RQCacheContent rqContent = new RQCacheContent();
        rqContent.setQuery(strSQL);
        rqContent.setReturnStrObject(result);
        close();

        oRQcacheContent = rqContent;
        
    }    

    
}