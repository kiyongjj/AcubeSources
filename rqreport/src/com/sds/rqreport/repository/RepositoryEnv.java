package com.sds.rqreport.repository;

public class RepositoryEnv {

	private static RepositoryEnv env = new RepositoryEnv();
	private RepositoryEnv() {
		
	}
	
	public static RepositoryEnv getInstance()
	{
		return env;
	}
	
	public String repositoryRoot = "C:\\Samples";
	public String jdbcDriver = "oracle.jdbc.driver.OracleDriver";//"sun.jdbc.odbc.JdbcOdbcDriver";
	public String connStr = "jdbc:oracle:thin:rqadmin_sun/easybase@70.7.101.214:1521:WORLDAV";//"jdbc:odbc:RQRepositoryDB";
	public String dbid = "";
	public String dbpw = "";
	public String docConnectorClass = "com.sds.rqreport.repository.RQDocConnector";
	public String docDSConnectorClass = "com.sds.rqreport.repository.RQDSListConnector";
	public String docTableName = "RQDoc";
	public String doc_dsTableName = "RQDocDs";
	public String doc_userTableName = "RQUser";
	public String doc_sqlTableName = "RQSQL";
	public String doc_DocStatTableName = "RQDOCSTAT";
	public String docSynchronizerClass = "com.sds.rqreport.repository.FTPSynchronizer";
	public String sqlConnectorClass = "com.sds.rqreport.repository.RQSQLConnector";
	public String jndiName = "";
	public String[] ftpRoot = null;//new String[1];
	public String[] ftpServerList = null;
	public int[] portList = null;
	public String[] userlist = null;
	public String[] passlist = null;
	public String installid = "";
	public String installpw = "";
}
