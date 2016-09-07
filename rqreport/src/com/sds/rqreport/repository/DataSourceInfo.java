package com.sds.rqreport.repository;

import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class DataSourceInfo implements RQInfo {

	public String dsName;
	public String dsDriver;
	public String connStr;

	public String id;
	public String pw;

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamTypes()
	 */
	public String getParamTypes() {
		return "SSSSS";

	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamInt(int)
	 */
	public int getParamInt(int index) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamFloat(int)
	 */
	public float getParamFloat(int index) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamString(int)
	 */
	public String getParamString(int index) {
		switch(index)
		{
		case 0:
			return dsName;
		case 1:
			return dsDriver;
		case 2:
			return connStr;
		case 3:
			return id;
		case 4:
			return pw;

		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamCalendar(int)
	 */
	public Calendar getParamCalendar(int index) {
		return null;
	}

}
