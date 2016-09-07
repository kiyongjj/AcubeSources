package com.sds.rqreport.repository;

import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class DSListInfo implements RQInfo {

	public int docID = 0;
	public String DSName = "";
	public int order = 0;
	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamTypes()
	 */
	public DSListInfo(int docID, String DSName, int order)
	{
		this.docID = docID;
		this.DSName = DSName;
		this.order = order;
	}

	public DSListInfo()
	{

	}

	public String getParamTypes() {
		return "ISI";
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamInt(int)
	 */
	public int getParamInt(int index) {
		switch(index)
		{
		case 0:
			return docID;
		case 2:
			return order;
		}
		return -1;
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
			return "" + docID;
		case 1:
			return DSName;
		case 2:
			return "" +order;
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
