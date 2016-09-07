package com.sds.rqreport.repository;

import java.util.*;

import com.sds.rqreport.common.*;
import com.sds.rqreport.repository.*;

public interface InfoConnector {
	public void setConnection(JDBCHelper jdbc);
	public int writeInfo(RQInfo info);
	public com.sds.rqreport.common.RQInfo getInfo(Object key) throws Exception;
	public int deleteInfo(Object key);
	public int deleteMultiInfo(ArrayList keylist);
	public int updateInfo(Object key, RQInfo info);
	public int updateMultiInfo(Object key, ArrayList infoList);
	public ArrayList listAll();
	public int writeAll(ArrayList alllist);
	public ArrayList getMutiInfo(Object condition);
	public boolean closeConnection();
	public Calendar makeCal(String date);
}
