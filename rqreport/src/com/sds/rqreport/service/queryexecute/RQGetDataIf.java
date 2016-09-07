package com.sds.rqreport.service.queryexecute;

import java.sql.SQLException;
import javax.naming.NamingException;

import com.sds.rqreport.model.RQCacheContent;

public abstract class RQGetDataIf {
    
    public static char SSEP = 5;   
    public static char COL_SEP = 9;     
    public static char ROW_SEP = 10;
    public static char BLOB_SEP = 6;
    
    public abstract void connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException;
    public abstract void fetch();
    public abstract void close() throws SQLException;
    
    public abstract String getRQResult() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException;
    
    public static final int OBJ_TYPE = 1;
    public static final int STR_TYPE = 2;
    public static final int XML_TYPE = 3;
    public int classType = 0;

    public static final int stop_status = -1;
    private int status = 0;
    //  1 : normal
    // -1 : exception
    public int exception_status = 1;
    
    private RQCacheContent oRQCacheContent = null;
    
    public RQCacheContent getRQCacheContent() {
    	return oRQCacheContent;
    }
    
    public void setRQCacheContent(RQCacheContent content) {
    	oRQCacheContent = content;
    }
    
    public synchronized int getStatus() {
    	return status;
    }
    
    public synchronized void stopStatus() {
    	this.status = stop_status;
    }
    
    public boolean isStopStatus() {
    	return (this.status == stop_status ? true : false);
    }
}
