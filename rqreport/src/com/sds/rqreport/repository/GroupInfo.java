package com.sds.rqreport.repository;

import java.util.Calendar;

import com.sds.rqreport.common.RQInfo;

public class GroupInfo implements RQInfo {

	public String id;
	public String params;


	public String getParamTypes() {
		return null;
	}

	public int getParamInt(int index) {
		return 0;
	}

	public float getParamFloat(int index) {
		return 0;
	}

	public String getParamString(int index) {
		switch(index)
		{
		case 0:
			return id;
		case 1:
			return params;

		}
		return "";

	}

	public Calendar getParamCalendar(int index) {
		return null;
	}

}
