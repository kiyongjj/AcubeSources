package com.sds.rqreport.service;

import java.util.Calendar;

import com.sap.mw.jco.*;
import com.sds.rqreport.common.RQInfo;

public class JCOConnection
{
	protected int mKey;
	protected JCO.Pool mPool;
	protected JCO.Repository mRepository;

	public JCOConnection()
	{

	}


	public JCO.Repository getRepository()
	{
		return mRepository;
	}

	public JCO.Pool getPool()
	{
		return mPool;
	}

	public int getKey()
	{
		return mKey;
	}
}

class JCOFunctionInfo implements RQInfo{

	public String name;
	public String groupname;
	public String appl;
	public String host;
	public String stext;

	public JCOFunctionInfo()
	{
	}
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
		String strRet;

		switch( index )
		{
		case 0:
			strRet = name;
			break;
		case 1:
			strRet = groupname;
			break;
		case 2:
			strRet = appl;
			break;
		case 3:
			strRet = host;
			break;
		case 4:
			strRet = stext;
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

class JCOParamInfo implements RQInfo{

	public String paramclass;
	public String parameter;
	public String tabname;
	public String fieldname;
	public String exid;
	public String defaultvalue;
	public String paramtext;
	public String optional;
	public String paramtype;

	public JCOParamInfo()
	{
	}
	public String getParamTypes() {

		return "SSSSSSSSS";
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
			strRet = paramclass;
			break;
		case 1:
			strRet = parameter;
			break;
		case 2:
			strRet = tabname;
			break;
		case 3:
			strRet = fieldname;
			break;
		case 4:
			strRet = exid;
			break;
		case 5:
			strRet = defaultvalue;
			break;
		case 6:
			strRet = paramtext;
			break;
		case 7:
			strRet = optional;
			break;
		case 8:
			strRet = paramtype;
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

class JCOFieldStructure implements RQInfo{

	public String paramname;
	public String fieldname;
	public int type;
	public String typename;
	public String fieldtext;
	public int length;
	public int decimals;

	public JCOFieldStructure()
	{
	}
	public String getParamTypes() {

		return "SSISSII";
	}


	public int getParamInt(int index) {
		int res = -1;
		switch(index)
		{
		case 2:
			res = type;
			break;
		case 5:
			res = length;
			break;
		case 6:
			res = decimals;
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
			strRet = paramname;
			break;
		case 1:
			strRet = fieldname;
			break;
		case 3:
			strRet = typename;
			break;
		case 4:
			strRet = fieldtext;
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

class JCOParamResult implements RQInfo{

	public String paramname;
	public String fieldname;
	public String result;
	public String paramclass;

	public JCOParamResult()
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
			strRet = paramname;
			break;
		case 1:
			strRet = fieldname;
			break;
		case 2:
			strRet = result;
			break;
		case 3:
			strRet = paramclass;
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