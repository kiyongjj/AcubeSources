package com.sds.rqreport.service;

import java.sql.*;
import java.util.Calendar;

import com.sds.rqreport.common.*;

public class ColumnInfo implements RQInfo{

	public String name;
	public int data_type;
	public int col_size;
	public int dec_digits;
	public int nullable;
	public int precision;
	public String type_name;
	public String col_typename; // IN, OUT, INOUT
	public int col_type; 		// IN, OUT, INOUT
	public String schema;		// Schema Name
	public String table;		// Table Name

	public ColumnInfo()
	{
	}

	public String getParamTypes() {

		return "SIIIIISSISS";
	}

	public int getParamInt(int index) {

		int res = -1;
		switch(index)
		{
		case 1:
			res = data_type;
			break;
		case 2:
			res = col_size;
			break;
		case 3:
			res = dec_digits;
			break;
		case 4:
			res = nullable;
			break;
		case 5:
			res = precision;
			break;
		case 8:
			res = col_type;
			break;
		}
		return res;

	}

	public float getParamFloat(int index) {

		return 0;
	}

	public String getParamString(int index) {
		String strRet;
		switch( index )
		{
		case 0:
			strRet = name;
			break;
		case 6:
			strRet = type_name;
			break;
		case 7:
			strRet = col_typename;
			break;
		case 9:
			strRet = schema;
			break;
		case 10:
			strRet = table;
			break;
		default:
			strRet = null;
		}
		return strRet;
	}

	public Calendar getParamCalendar(int index) {
		return null;
	}

}