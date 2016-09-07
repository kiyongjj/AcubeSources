package com.sds.rqreport.repository;

import java.util.Calendar;
import com.sds.rqreport.common.RQInfo;

public class UserInfo implements RQInfo {

	public String id;
	public String pw;
	public String email;
	public String desc = "";
	public String group = "DEFAULT";
	public String auth = "";

	public String getParamTypes() {
		return "SSSSS";
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
			return pw;
		case 2:
			return email;
		case 3:
			return desc;
		case 4:
			return group;
		case 5:
			return auth;
		}
		return "";
	}

	public Calendar getParamCalendar(int index) {
		return null;
	}

}
