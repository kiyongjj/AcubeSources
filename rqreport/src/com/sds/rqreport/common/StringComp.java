package com.sds.rqreport.common;

import java.util.Calendar;

public class StringComp implements RQInfo {

	public String str ="";
	public StringComp(String str)
	{
		this.str = str;
	}
	public String getParamTypes() {
		return "S";
	}

	public int getParamInt(int index) {
		return 0;
	}

	public float getParamFloat(int index) {
		return 0;
	}

	public String getParamString(int index) {
		return str;
	}

	public Calendar getParamCalendar(int index) {
		return null;
	}

}
