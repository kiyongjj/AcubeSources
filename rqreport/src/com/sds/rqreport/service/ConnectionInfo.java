package com.sds.rqreport.service;
import java.sql.*;
import java.util.Calendar;

import com.sds.rqreport.common.*;

public class ConnectionInfo {
	public int connidx;
	public String connid;
	public String driver;
	public String connection;
	public String id;
	public String pw;
//	Connection conn;
	public String host;
	public String Language;
	public String Client;
	public String SysNumber;
	public String key;
	public String poolsize;
	public String r3name;
	public String group;
	public String jcoConnType;
}

class TableInfo implements RQInfo{

	public String table_schema;
	public String table_name;
	public String table_type;
	public String table_remark;

	public TableInfo()
	{
	}
	public String getParamTypes() {

		return "SSSS";
	}


	public int getParamInt(int index) {
		return 0;
	}

	public float getParamFloat(int index) {
		return 0;
	}

	public String getParamString(int index) {
		String strRet;

		switch( index )
		{
		case 0:
			strRet = table_schema;
			break;
		case 1:
			strRet = table_name;
			break;
		case 2:
			strRet = table_type;
			break;
		case 3:
			strRet = table_remark;
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



class ProcedureInfo implements RQInfo{

	public String catalog;
	public String name;
	public int returnType; // DatabaseMetaData.procedureNoResult, procedureReturnsResult, procedureResultUnknown

	public ProcedureInfo()
	{
	}

	public String getParamTypes() {

		return "SSI";
	}

	public int getParamInt(int index) {

		int res = -1;
		switch(index)
		{
		case 2:
			res = returnType;
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
			strRet = catalog;
			break;
		case 1:
			strRet = name;
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