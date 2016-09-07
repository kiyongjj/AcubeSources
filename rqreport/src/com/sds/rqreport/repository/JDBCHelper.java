package com.sds.rqreport.repository;

import java.sql.*;
import java.io.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.util.RequbeUtil;

class IDGen {
	Hashtable ids;
	Vector UniqueDocID = null;
	Vector UniqueFolderID = null;
	IDGen()
	{
		ids = new Hashtable();
	}
	
	public int getID(String key )
	{
		Integer id = (Integer)ids.get(key);		
		if (id == null)
		  return -1;
		else
		  return id.intValue();
	}
	
	public void clear()
	{
		ids.clear();
	}
	
	public void setID(String key,int val)
	{
		ids.put(key, new Integer(val));
	}
}

public class JDBCHelper { //
  static Logger L = Logger.getLogger("REPOSITORY");
//  static DBConnManager dbmnn;
  static IDGen ids;
  static DataSource ds = null;
//  static {
//	dbmnn = new DBConnManager();
//	ids = new IDGen();
//  }
  
  boolean connection = false;

  Connection conn = null;
  Statement stmt = null;
  
  String repositoryJdbcConnection = "";
  
  Environment env = Environment.getInstance();
  
  public JDBCHelper() {
		connection = false;
  }

  
  public boolean connect()
	throws Exception {
	  Environment tenv  = Environment.getInstance();
	  RepositoryEnv env = RepositoryEnv.getInstance();
	  if(env.jndiName != null && env.jndiName.length() > 0)
	  {
		  if(ds == null)
		  {
       		Context initContext = new InitialContext();
       		String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
       		NamingEnumeration oNenum = null;
       		try{
       			oNenum = initContext.listBindings(bind_str);
       			if(!oNenum.hasMore()){
       				bind_str = "java:comp/env";
       			}
       			Context envContext  = (Context)initContext.lookup(bind_str);
       			ds = (DataSource)envContext.lookup(env.jndiName);
       		}catch(NamingException e){
       			ds = (DataSource)initContext.lookup(env.jndiName);
       		}
		  }
		  	conn = ds.getConnection();
	  }
	  else
	  {
			Class.forName(env.jdbcDriver).newInstance();
			conn = DriverManager.getConnection(env.connStr);		  
	  }
		stmt = conn.createStatement();
		return connection;
	}

  public ResultSet getRs(String strQry)
	  throws Exception {

		String convQry = null;
		convQry = strQry;
		ResultSet rs = null;


		try
		{
			if(stmt == null)
				stmt = conn.createStatement();			
			 rs = stmt.executeQuery(convQry);
			 
		}catch(SQLException ex)
		{
			L.error(ex);
		}
		return rs;
  }

  public int execute(String strQry)
	  throws Exception {
  
   String convQry = null;
   convQry = strQry;
   int ret = -1;
   if(stmt == null)
	   	stmt = conn.createStatement();
		ret = stmt.executeUpdate(convQry);
	return ret;
  }
  
  public void close() {
	// Statement 만  close;
	try {
		  if(stmt != null)
			  stmt.close();

	  } catch (Exception ex) {
		L.debug(ex);
		conn = null;
	}finally
	{
		  if(conn != null)
		  {
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			conn = null;
		  }		
	}
  }


  public boolean writeFile(int docID, byte[] byteData, String path) {
//	File f = null;
//	FileOutputStream fo = null;
//	File f1 = null;
//	FileOutputStream fo1 = null;
//	File f2 = null;
//	try {
//	  String filePath = Environment.repositoryPath + "/" + path;
//	  String filePath1 = null;
//	  L.debug(filePath);
//	  if(Environment.repositoryDocIDNameArraySize > 0)
//		{
//			filePath1 = Environment.repositoryPath + "/" + Environment.repositoryDocIDNameArray.get(0);
//			f2 = new File(filePath1);
//			if(!f2.isDirectory())
//				f2.mkdir();
//			f2 = null;
//			filePath1 = Environment.repositoryPath + "/" + Environment.repositoryDocIDNameArray.get(0) +"/"+path;
//		}
//	  f = new File(filePath);
//	  f.createNewFile();
//	  fo = new FileOutputStream(f);
//	  fo.write(byteData);
//	  if(filePath1 != null)
//	  {
//			f1 = new File(filePath1);
//		  f1.createNewFile();
//		  fo1 = new FileOutputStream(f1);
//		  fo1.write(byteData);
//	  }
//	  return true;
//	}
//	catch (Exception e) {
//	  L.error(e);
//	  return false;
//	}
//	finally {
//	  try {
//		if (fo != null)
//		  fo.close();
//		if ( fo1 != null)
//			fo1.close();
//	  }
//	  catch (Exception ex) {
//		L.error(ex);
//	  }
//	  f = null;
//	  fo = null;
//	  f1 = null;
//	  fo1 = null;
//	  f2 = null;
//	}
	  return true;
  }
  
  public int writeDataFile(int type, String path, byte[] byteData)
  {
//	String rootpath = "";
//	switch(type)
//	{
//		case PDF_REPOSITORY:
//		case PDF_REPOSITORY_NOOVER:
//		rootpath = Environment.pdf_repositoryPath;
//		case RESULT_REPOSITORY:
//		case RESULT_REPOSITORY_NOOVER:
//		rootpath = Environment.result_repositoryPath;
//		break;
//	}
//  	
//	File f = null;
//	FileOutputStream fo = null;
//	try {
//	  String filePath = null;
//	  if(rootpath.endsWith("/") || path.startsWith("/") || rootpath.endsWith("\\") || path.startsWith("\\"))
//	  {
//		  filePath = rootpath + path;
//	  }
//	  else
//	  {
//		  filePath = rootpath + "/" + path;
//	  }
//	  L.debug(filePath);
//	  // make directories
//	  int pos = filePath.lastIndexOf("/");
//	  String dir = filePath.substring(0,pos);
//	  File mkdirs = new File(dir);
//	  mkdirs.mkdirs();
//	  
//	  f = new File(filePath);
//	  if(type == RESULT_REPOSITORY_NOOVER || type == PDF_REPOSITORY_NOOVER)
//	  {
//		  if(f.exists())
//		  {
//			  return 1;
//		  }
//	  }
//	  f.createNewFile();
//	  fo = new FileOutputStream(f);
//	  fo.write(byteData);
//	  return 0;
//	}
//	catch (Exception e) {
//	  L.error(e);
//	  return 2;
//	}
//	finally {
//	  try {
//		if (fo != null)
//		  fo.close();
//	  }
//	  catch (Exception ex) {
//		L.error(ex);
//	  }
//	  f = null;
//	  fo = null;
//	}
	  return 0;
  }
  
  public int writeDataFileAppend(int type, String path, byte[] byteData)
  {
//	String rootpath = "";
//	switch(type)
//	{
//		case PDF_REPOSITORY_APPEND:
//		rootpath = Environment.pdf_repositoryPath;
//		case RESULT_REPOSITORY_APPEND:
//		rootpath = Environment.result_repositoryPath;
//		break;
//	}
//  	
//	File f = null;
//	FileOutputStream fo = null;
//	try {
//	  String filePath = null;
//	  if(rootpath.endsWith("/") || path.startsWith("/") || rootpath.endsWith("\\") || path.startsWith("\\"))
//	  {
//		  filePath = rootpath + path;
//	  }
//	  else
//	  {
//		  filePath = rootpath + "/" + path;
//	  }
//	  L.debug(filePath);
//	  // make directories
//	  int pos = filePath.lastIndexOf("/");
//	  String dir = filePath.substring(0,pos);
//	  File mkdirs = new File(dir);
//	  mkdirs.mkdirs();
//	  
//	  fo = new FileOutputStream(filePath,true);
//	  fo.write(byteData);
//	  return 0;
//	}
//	catch (Exception e) {
//	  L.error(e);
//	  return 2;
//	}
//	finally {
//	  try {
//		if (fo != null)
//		  fo.close();
//	  }
//	  catch (Exception ex) {
//		L.error(ex);
//	  }
//	  f = null;
//	  fo = null;
//	}
	return 0;
  }
  public boolean deleteFile(int docID, String path) {
	File f;
	try {
	  f = new File(path);
	  f.delete();
	}
	catch (Exception e) {
	  L.error(e);
	}
	f = null;
	return true;
  }

  public static byte[] readFile(File f) {
	String filePath = null;
	byte data[];
	FileInputStream fi = null;
	try {
	//  f = new File(filePath);
	  int size = (int) f.length();
	  data = new byte[size];
	  fi = new FileInputStream(f);
	  int len, totalReceived = 0;
	  while (totalReceived < size) {
		len = fi.read(data, totalReceived, size - totalReceived);
		totalReceived += len;
	  }
	  return data;
	}
	catch (Exception e) {
	  data = null;
	  L.error("ReadFile Error Occurred!", e);
	  return null;
	}
	finally {
	  try {
		if (fi != null) {
		  fi.close();
		}
	  }
	  catch (IOException ex) {
		L.error(ex);
	  }
	  fi = null;
	  f = null;
	}

  }



  private boolean find(String name, Vector docPaths) {
	int size = docPaths.size();
	String docName = "";
	int pos;
	for (int i = 0; i < size; ++i) {
	  docName = (String) docPaths.get(i);
	  pos = docName.lastIndexOf("/");
	  if(pos > 0)
	  {
		  docName = docName.substring(pos + 1);
	  }
	  if (name.equals(docName)) {
		docPaths.remove(i);
		return true;
	  }
	}
	return false;
  }

  public synchronized int getID(String tableName, String fieldName)
	  throws Exception {
		String key = tableName +"." + fieldName;
		int id = ids.getID(key);
		
		if(id < 0)
		{
			ResultSet rs;
			String qry = "SELECT MAX(" + fieldName + ") from " + tableName;
			rs = getRs(qry);
			if (rs.next()) {
				int ret = rs.getInt(1) + 1;
				rs = null;
				ids.setID(key, ret);
				return ret;
			}
		}
		ids.setID(key, id + 1);
    	return id + 1;
  }
  public boolean rollback()
  {
	  try {
		  RequbeUtil.setTransaction(conn, "rollback", true);
		  return true;
	} catch (SQLException e) {
		L.error(e);
		e.printStackTrace();
	}
	return false;
  }
  
  boolean makeDirs(String rootPath, String dir)
  {
//	  StringTokenizer st = new StringTokenizer(dir,"/\\");
//	  String path = rootPath;
//	  File f;
//	  while (st.hasMoreTokens()) {
//		  String childDir = st.nextToken();
//		  if(childDir != null && childDir.length() > 0)
//		  {
//			  path += path + "/" + childDir;
//			  f = new File(path);
//			  if(!f.exists() || !f.isDirectory())
//			  {
//				 f.mkdirs(); 
//			  }
//		  }
//	  }

	  return true;
  }
}