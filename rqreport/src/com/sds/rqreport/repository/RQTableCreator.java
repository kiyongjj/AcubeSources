package com.sds.rqreport.repository;

import com.sds.rqreport.util.RequbeUtil;
import java.util.*;

public class RQTableCreator {

	RepositoryEnv env;
	JDBCHelper jdbc;
	public RQTableCreator() {
		env = RepositoryEnv.getInstance();
		jdbc = new JDBCHelper();
	}
	
	public static void main(String args[])
	{
		String qry1 = "CREATE TABLE [name] ("
		+  " nDocID number(20)  NOT NULL,"
		+ " File_NM varchar2(255) default '',"
		+  " DOC_FG CHAR(1) default 'D',"
		+  " Full_PATH varchar2(512) default NULL,"
		+  " nPDocID number(20) default '0',"
		+  " DOC_DESC varchar2(255) default NULL,"
		+ " CREATE_USER_ID varchar2(255) default NULL,"
		+  " CREATE_DATE CHAR(14) default '20000101000000',"
		+  " MOD_DATE CHAR(14) default '20000101000000',"
		+  " PRIMARY KEY  (nDocID)"
		+ ")";
		
		String qry2 = "CREATE TABLE [name] ("
		+ " nDocID NUMBER(20) NOT NULL," 
		+ " DS_NM varchar2(255) default '' NOT NULL ,"
		+ " NORDER number(2) default '0' NOT NULL "
		+ ")";
		
		String qry3 = "CREATE TABLE [name] ("
			+ " USERID varchar2(20) NOT NULL," 
			+ " PW varchar2(50) default '' NOT NULL ,"
			+ " EMAIL varchar2(50) default '0' NOT NULL ,"
			+ " USER_DESC varchar2(255) default '0' NOT NULL ,"
			+ " GROUPID varchar2(255) default 'DEFAULT' ,"
			+  " PRIMARY KEY  (USERID)"
			+ ")";		
		
		String qry4 = "CREATE TABLE [name] ("
			+ " NDOCID NUMBER(20) NOT NULL," 
			+ " SQL CLOB NOT NULL "
			+ ")";
		RQTableCreator tableCreator = new RQTableCreator();
		//tableCreator.makeTable("RQDoc", qry1);
		//tableCreator.makeTable("RQDocDS", qry2);
		//tableCreator.makeRootDir("RQDoc");
		//tableCreator.deleteTable("RQuser");
		//tableCreator.makeTable("RQUser", qry3);
		//tableCreator.makeDefaultUser("RQUser", "DEFAULT");
		tableCreator.makeTable("RQSQL", qry4);
		
	}
	public int makeTable(String name, String qry)
	{
		// To do : convert name
		String qryExe = null;
		//int pos = qry.indexOf("[name]");
		qryExe = qry.replaceAll("\\[name\\]", name);
		return executeQry(qryExe);
	}
	
	public int makeRootDir(String docTableName)
	{
		String qry = "insert into " + docTableName + " (nDOCID, FILE_NM, DOC_FG, FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE) values (1, '','F','/','0','RootDir','admin',";
		Calendar curDate = Calendar.getInstance();
		String date = "'" + RequbeUtil.makeDateString(curDate, "%Y%M%D%H%m%S") + "'";
		qry += date + ", " + date; 
		qry += ")" ;
		return executeQry(qry);
	}
	public int makeDefaultUser(String userTableName, String group)
	{
		String qry = "insert into " + userTableName + " (USERID, PW, EMAIL, USER_DESC, GROUPID, AUTH) values ('admin', '23660a89f7408f89441622e03857e8921f074f8a','admin@easybase.com','default user','" + group + "','A')" ;
		return executeQry(qry);		
	}
		
	private int  executeQry(String ddlStr){
		try{
			jdbc.connect();
			jdbc.execute(ddlStr);
		}catch(Exception ex){
			ex.printStackTrace();
			return -1;
		}finally{
			jdbc.close();
		}
		return 0;
	}
	
	int deleteTable(String name)
	{
		try
		{
			String qry = "Drop Table " + name;
			jdbc.connect();
			jdbc.execute(qry);
			jdbc.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return -1;
		}
		return 0;		
	}
	
	boolean isExistTable(String name)
	{
		return false;
	}
}
