package com.sds.rqreport.repository;

import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;
import com.sds.rqreport.util.*;

public class DocInfo implements RQInfo {

	public int idx;
	public String name;
	public char doc_fg;
	public String fullPath;
	public int pdocID;
	public String docDesc;
	public String createUserID;
	public Calendar createDate;
	public Calendar modDate;
	public String nodename;
	public String file_version; //for file history


	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamTypes()
	 */
	public String getParamTypes() {
		return "ISISISSDD";
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamInt(int)
	 */
	public int getParamInt(int index) {
		switch(index)
		{
		case 0:
			return idx;
		case 2:
			return doc_fg;
		case 4:
			return pdocID;
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
			return "" + idx;
		case 1:
			return name;
		case 2:
			return "" + doc_fg;
		case 3:
			return fullPath;
		case 5:
			return docDesc;
		case 6:
			return createUserID;
		case 7:
			return RequbeUtil.makeDateString(createDate,"%Y%M%D%H%m%S");
		case 8:
			return RequbeUtil.makeDateString(modDate,"%Y%M%D%H%m%S");
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.common.RQInfo#getParamCalendar(int)
	 */
	public Calendar getParamCalendar(int index) {
		switch(index)
		{
		case 7:
			return createDate;
		case 8:
			return modDate;
		}
		return null;
	}

}
