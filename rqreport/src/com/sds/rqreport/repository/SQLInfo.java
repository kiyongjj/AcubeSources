package com.sds.rqreport.repository;

import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class SQLInfo implements RQInfo {
	public int docID;
	public String sql;
	public SQLInfo(int docID, String sql)
	{
		this.docID = docID;
		this.sql = sql;
	}

	public Calendar getParamCalendar(int index) {
		return null;
	}

	public float getParamFloat(int index) {
		return 0;
	}

	public int getParamInt(int index) {
		int ret = -1;
		switch(index)
		{
		case 0:
			ret = docID;
			break;

		case 1:
			ret = -1;
			break;
		}
		return ret;
		
	}

	public String getParamString(int index) {
		String ret = null;
		switch(index)
		{
		case 0:
			ret = "" + docID;
			break;
		case 1:
			ret = sql;
			break;
		}
		return ret;
	}

	public String getParamTypes() {
		return "IS";
	}
}
