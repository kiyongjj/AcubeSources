package com.sds.rqreport.site.ems;

public interface RQSiteLogInterface {
	
	public int StartLogging(RQSiteLogDataInterface data);
	
	public int EngineStartLogging(RQSiteLogDataInterface data);
	
	public int EngineEngLogging(RQSiteLogDataInterface data);
	
	public int FinishLogging(RQSiteLogDataInterface data);
	
}
