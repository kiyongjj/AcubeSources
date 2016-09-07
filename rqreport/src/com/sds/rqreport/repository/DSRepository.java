package com.sds.rqreport.repository;

import java.util.List;

public class DSRepository {

	InfoConnector ds;
	public DSRepository() throws Exception
	{
		Class c = Class.forName("com.sds.reqube.repository.RQDSConnector");
		ds = (InfoConnector)c.newInstance();
	}

	public DataSourceInfo GetDS(String name)
	{
		try
		{
			return (DataSourceInfo)ds.getInfo(name);
		}catch(Exception ex)
		{

		}
		return null;
	}

	public int deleteDS(String name)
	{

		return ds.deleteInfo(name);
	}

	public int addDS(DataSourceInfo dsInfo)
	{
		return ds.writeInfo(dsInfo);
	}

	public int getDS(String name, List ret)
	{
		DataSourceInfo dsInfo = null;
		try {
			ret.add((DataSourceInfo)ds.getInfo(name));

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
