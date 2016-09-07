package com.sds.rqreport.repository;

import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

import com.sds.rqreport.*;
import com.sds.rqreport.common.*;
import com.sds.rqreport.model.*;
import com.sds.rqreport.util.*;
import com.sds.rqreport.scheduler.SchedulerEnv;
import com.sds.rqreport.service.*;
import com.sds.rqreport.service.queryexecute.*;
import com.sds.rqreport.service.web.*;

import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

class ReportFiles implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		if(name.endsWith(".rqd"))
		{

			return true;
		}
		else
			return false;
	}
}

class Folders implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		File f = new File(dir.getPath() + File.separator + name);
		 return f.isDirectory();
	}
}

public class DocRepository {

	static Logger L = Logger.getLogger("REPOSITORY");
	String rootPath = "";
	File rootFile = null;
	Hashtable docRepository;
	InfoConnector docConnector = null;
	InfoConnector dsListConnector = null;
	InfoConnector sqlConnector = null;
	ArrayList list = null;
	Environment g_env = null;
	RepositoryEnv env = null;
	String userID = "admin";
	RQSyncRepository sync = null;
	RQSyncRepository backupsync = null;
	JDBCHelper jdbc = null;
	ZipOutputStream zipOut;
	ZipInputStream zipIn;
	private RQFileComp oFileComp;

	public DocRepository() throws Exception
	{
		g_env = Environment.getInstance();
		env = Environment.getRepositoryEnv();
		rootPath = env.repositoryRoot;
		docRepository = new Hashtable(100);
		rootFile = new File(rootPath);
		jdbc = new JDBCHelper();
		Class c = Class.forName(env.docConnectorClass);
		docConnector = (InfoConnector)c.newInstance();
		docConnector.setConnection(jdbc);
		list = new ArrayList();

		if(env.docSynchronizerClass != null && env.docSynchronizerClass.length() > 0)
		{
			c = Class.forName(env.docSynchronizerClass);
			sync = (RQSyncRepository)c.newInstance();
			backupsync = (RQSyncRepository)c.newInstance();
			sync.setEnv(env);
			backupsync.setEnv(Environment.getInstance().backUpEnv);
		}

		c = Class.forName(env.docDSConnectorClass);
		dsListConnector = (InfoConnector)c.newInstance();
		dsListConnector.setConnection(jdbc);
		c = Class.forName(env.sqlConnectorClass);
		sqlConnector = (InfoConnector)c.newInstance();
		sqlConnector.setConnection(jdbc);
	}

	// To test code
	public static void main(String args[]) throws IOException, Exception
	{

		DocRepository docRep = new DocRepository();
//		Environment tenv = Environment.getInstance();
//		tenv.load();
//		Properties prop = tenv.getPropertyLoader().getProperties();
//
//		OutputStream f = new FileOutputStream("G:\\REQUBEWeb6.0\\reqube2006\\Prop\\rqreport.properties");
//		prop.store(f, "REQUBE2006 WebServer properties");
//		f.close();


	//	boolean b = docRep.listDocRecursive("/");
	//	boolean b = docRep.listFoldersRecursive("/");
	//	File f = docRep.getFile("/Manual/Tutorial/");
//		System.out.println();
//	//	System.out.println("" + f.getPath());

//		File f = new File("C:\\각부문TOP5.rqd");
//		byte[] data = JDBCHelper.readFile(f);
//		docRep.addDoc(data, "/test/각부문TOP5.rqd", "admin","");
//		docRep.addDoc(data, "/test/각부문TOP6.rqd", "admin","");
//		docRep.addDoc(data, "/test/각부문TOP7.rqd", "admin","");
//		docRep.addDoc(data, "/test/각부문TOP8.rqd", "admin","");
//	//	docRep.makeFolder("/test/");
//		docRep.listAll();
//		ArrayList list = (ArrayList)docRep.getList();
//		DocInfo di = null;
//		for(int i = 0; i < list.size(); ++i)
//		{
//			di = (DocInfo)list.get(i);
//			String line = "";
//			for(int j = 0; j < 8 ;++j)
//			{
//				line += di.getParamString(j) + "|";
//			}
//			System.out.println(line);
//		}
		//docRep.listDoc("/");
//		Vector ret = new Vector(1);
//		int res = docRep.getDocInfo("/test/각부문TOP5.rqd", ret);
//
//		if(res == 0)
//		{
//			RQInfo info = (RQInfo)ret.get(0);
//			System.out.println(info.getParamInt(0) + info.getParamString(1) + info.getParamString(2) + info.getParamString(3));
//		}

//		docRep.delDoc("/test/각부문TOP5.rqd");
//		docRep.delDoc("/test/각부문TOP6.rqd");
//		docRep.delDoc("/test/각부문TOP7.rqd");
//		docRep.delDoc("/test/각부문TOP8.rqd");
//		docRep.makeRepositoryToZip("c:\\test.zip", "C:\\test");
//		docRep.extractZip("C:\\test.zip", "C:\\test2");
//		System.out.println(docRep.getSQL("/TEST_ljg/RQDT.rqd"));


		URL url = new URL("http://70.7.33.81:8080/reqube2006/document/repositoryarchive.jsp?method=downall");
		URLConnection urlC = url.openConnection();
		urlC.setUseCaches(false);

		StringBuffer readData = new StringBuffer();
		StringBuffer encodeData = new StringBuffer();
		String key = "reqube2006-0222";
		byte[] filedata = new byte[10000];
		FileOutputStream fo = new FileOutputStream("C:\\rmzip.zip");
		try {
		//	FileReader f = new FileReader("c:\\repositoryarchive.txt");
		//	BufferedReader  in = new BufferedReader (f);
			InputStreamReader   isr = new InputStreamReader(urlC.getInputStream());
			BufferedReader  in = new BufferedReader (isr);
			char[] data = new char[5000];
			int size;
			int pos = 0, pos2 = 0;
			while((size = in.read(data)) > 0)
			{
				readData.append(data,0,size);
				pos = readData.indexOf("<DATA>");
				pos2 = readData.indexOf("</DATA>" , pos);
				if(pos >= 0 && pos2 > 0)
				{
					byte[] b = com.sds.rqreport.util.Base64Decoder.decodeToBytes(readData.substring(pos + 6, pos2));
					fo.write(b);
					String temp = readData.substring(pos2 + 7);
					readData.setLength(0);
					readData.append(temp);
				}
			}
			L.debug(data);
			in.close();
    		isr.close();
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public RQFileComp getOFileComp() {
		return oFileComp;
	}
	public void setOFileComp(RQFileComp fileComp) {
		oFileComp = fileComp;
	}
	public RQSyncRepository getBackUpSynchronizer()
	{
		return backupsync;
	}
	public int sendDoc(File f, String path)
	{
		if(sync != null)
			return sync.sendDoc(f, path);
		else
			return -1;
	}
	public int remoteDeleteDoc(String path)
	{
		if(sync != null)
			return sync.deleteDoc(path);
		else
			return -1;
	}

	public int remoteDeleteDir(String path)
	{
		if(sync != null)
			return sync.deleteFolder(path);
		else
			return -1;

	}


	public int downloadAllFromServer(String serverUrl)
	{

		StringBuffer readData = new StringBuffer();
		StringBuffer encodeData = new StringBuffer();
		byte[] filedata = new byte[10000];
		try {
			URL url = new URL(serverUrl);
			URLConnection urlC = url.openConnection();
			urlC.setUseCaches(false);
			File tempFile = File.createTempFile("RQR", "ZIPPED");
			FileOutputStream fo = new FileOutputStream(tempFile);
			InputStreamReader   isr = new InputStreamReader(urlC.getInputStream());
			BufferedReader  in = new BufferedReader (isr);
			char[] data = new char[5000];
			int size;
			int pos = 0, pos2 = 0;
			while((size = in.read(data)) > 0)
			{
				readData.append(data,0,size);
				pos = readData.indexOf("<DATA>");
				pos2 = readData.indexOf("</DATA>" , pos);
				if(pos >= 0 && pos2 > 0)
				{
					byte[] b = com.sds.rqreport.util.Base64Decoder.decodeToBytes(readData.substring(pos + 6, pos2));
					fo.write(b);
					String temp = readData.substring(pos2 + 7);
					readData.setLength(0);
					readData.append(temp);
				}
			}
//			System.out.println(data);
			in.close();
    		isr.close();
			fo.close();

			// Extract All
			extractZip(tempFile.getPath(), rootPath);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return -100;
		} catch (IOException e) {

			e.printStackTrace();
			return -101;
		}
		return 0;
	}


	public int extractZip(String filename, String path)
	{
		try {
			zipIn = new ZipInputStream(new FileInputStream(filename));
			ZipEntry zipEn = null;
			byte[] data = null;
			FileOutputStream fo;
			data = new byte[1024];
			while((zipEn = zipIn.getNextEntry()) != null)
			{
				long len = zipEn.getSize();
				String name = java.net.URLDecoder.decode(zipEn.getName(), "UTF-8");
				String entname = path + name;
				int pos = entname.lastIndexOf('/');
				String dirpath = entname.substring(0,pos);
				File dir = new File(dirpath);
				dir.mkdirs();

				fo = new FileOutputStream(entname);
				while((len = zipIn.read(data,0,1024)) > 0)
				{
					fo.write(data,0,(int)len);
				}
				System.out.println("extracted:" + entname);
				L.debug("extracted" + entname);
				fo.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}


	public boolean commitAll()
	{
		return true;
	}
	public boolean listDoc(String path)
	{

//		File f = new File(rootPath + path);
//
//		FilenameFilter reportFiles = new ReportFiles();
//		File[] files = f.listFiles(reportFiles);
//		DocInfo di = null;
//
//		try
//		{
//
//			for(int i = 0; i < files.length; ++i)
//			{
//			  di = new DocInfo();
//			  di.idx = files[i].hashCode();
//			  di.name = files[i].getPath();
//			  docRepository.put(files[i], di);
//			  list.add(di);
//			  System.out.println(path + files[i].getName() + "," + files[i].hashCode());
//			}
//		}catch(Exception e)
//		{
//		  e.printStackTrace();
//		}

		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int pID = getID(path);
			L.debug("after getID()");

			list = getMutiDocInfo(new Integer(pID));

			L.debug("after getID()");

		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return false;
		}
		finally {
			jdbc.close();
		}

		return true;
	}


	public ArrayList getMutiDocInfo(Object condition) {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE, FILE_VERSION from " +  env.docTableName;
		ArrayList doclist = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (condition instanceof Integer)
		{

			qry += " where nPDOCID = " + condition;
			qry += " order by DOC_FG desc , FILE_NM asc ";
			try
			{
				pstmt = jdbc.conn.prepareStatement(qry);

				rs = pstmt.executeQuery();
				DocInfo docInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())
				{
					//System.out.println("MultiDoc rs.next()");
					docInfo = new DocInfo();
					docInfo.idx = rs.getInt(1);
					docInfo.name  = rs.getString(2);
					docInfo.doc_fg  = rs.getString(3).charAt(0);
					docInfo.fullPath  = rs.getString(4);
					docInfo.pdocID = rs.getInt(5);
					docInfo.docDesc = rs.getString(6);
					docInfo.createUserID = rs.getString(7);
				    cal = Calendar.getInstance();
				    d = rs.getString(8);

					docInfo.createDate = docConnector.makeCal(d);
					cal = Calendar.getInstance();
				    d = rs.getString(9);

					docInfo.modDate = docConnector.makeCal(d);
					docInfo.file_version = rs.getString(10);

					doclist.add(docInfo);
				}



			}catch(Exception ex)
			{
				RequbeUtil.do_PrintStackTrace(L, ex);
			}
			finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				}
				catch(SQLException sx)
				{
					RequbeUtil.do_PrintStackTrace(L, sx);
				}
			}
		}
		return doclist;
	}

	boolean listDocRecursive(String path)
	{
//		listDoc(path);
//		File f = new File(rootPath + path);
//		FilenameFilter filter = new Folders();
//		File[] files = f.listFiles(filter);
//		try
//		{
//			for(int i = 0; i < files.length; ++i)
//			{
//
//
//		//	  docRepository.put(files[i].getPath(), di);
//			  listDocRecursive(path + files[i].getName() + "/" );
//			}
//		}catch(Exception e)
//		{
//		  e.printStackTrace();
//		}
//
		return true;
	}

	public List getList()
	{
		return list;
	}

	public String getName(File f)
	{
		if(f == null )
		{
			return null;
		}
		else if(f != null && f.equals(rootFile))
		{
			return "/";
		}
		else
		{
			if(f.isDirectory())
				return getName(f.getParentFile()) + f.getName() + "/";
			else
				return getName(f.getParentFile()) + f.getName();
		}
	}
	public int listAll()
	{
		list = docConnector.listAll();
		return 0;
	}
	boolean listFolders(String path)
	{

		File f = new File(rootPath + path);
		FilenameFilter filter = new Folders();
		File[] files = f.listFiles(filter);
		try
		{
			for(int i = 0; i < files.length; ++i)
			{

			  L.debug(getName(files[i]));

			}
		}catch(Exception e)
		{
			RequbeUtil.do_PrintStackTrace(L, e);
		}
		return true;
	}

	boolean listFoldersRecursive(String path)
	{
		File f = new File(rootPath + path);
		FilenameFilter filter = new Folders();
		File[] files = f.listFiles(filter);
		try
		{
			for(int i = 0; i < files.length; ++i)
			{
			  String folderpath = getName(files[i]);

			  L.debug(folderpath);

			  listFoldersRecursive(folderpath);

			}
		}catch(Exception e)
		{
			RequbeUtil.do_PrintStackTrace(L, e);
		}
		return true;
	}

	/**
	 *
	 * @param path 파일의 이름 ex) /sample/doc.rqd
	 * @return 실제파일 Object
	 */

	public File getFile(String path)
	{


		if(path.startsWith("/"))
		{
			//rootFile.hashCode()
			return getFile(path, rootFile);
		}
		else
		{
			return null;
		}
	}

	public File getFile(String path, File parent)
	{
		if(path.startsWith("/"))
		{
			String name;
			int pos = path.indexOf("/",1);
			if(pos > 0)
			{
				name = path.substring(0,pos);
				if(!RequbeUtil.verifyParam(name, 255, "&^%$#@!*\"'&"))
					return null;
			}
			else
			{
				return new File(parent.getPath() + File.separator + path.substring(1));
			}

			if(name.equals("/"))
				return parent;
			File child = new File(parent.getPath() + File.separator  + name.substring(1));
			return getFile(path.substring(pos),child);
		}
		else
		{
			return parent;
		}
	}

	public int addDoc(byte[] data,String path, String createID, String description)
	{

		File f = getFile(path);
		if(f == null)
			return -101;
		else
		{
			if(!f.getName().endsWith(".rqx"))
				return -102;
		}

		try
		{
			if(f.createNewFile())
			{
				FileOutputStream out = new FileOutputStream(f);
				out.write(data);
				out.close();

				// Synchronize Repository.
				if(sync != null)
				{
					sync.sendDoc(f, path);
				}
			}

			addDocInfo(f, createID, description);

		}catch(Exception ex)
		{
			RequbeUtil.do_PrintStackTrace(L, ex);
		}

		return 0;
	}
	
	// CDC dev. code
	public String getFD (String docid)
	{
		
		DocInfo di = null;
		try
		{
			Integer docId = new Integer(Integer.parseInt(docid));
			di = (DocInfo)getInfo(docId);
			if(di == null)
				return "";
		}
		catch(Exception ex)
		{
			RequbeUtil.do_PrintStackTrace(L, ex);
			return "";
		}
		
		return di.getParamString(2);
	}
	
	/**
	 *
	 * @param f File f 를 추가한다.
	 * @return
	 */
	
	public int getID(String fullPath)
	{

		DocInfo di = null;
		try
		{
			di = (DocInfo)getInfo(fullPath);
			if(di == null)
				return -1;
		}
		catch(Exception ex)
		{
			RequbeUtil.do_PrintStackTrace(L, ex);
			return -1;
		}

		return di.getParamInt(0);
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#getInfo(java.lang.Object)
	 */
	public RQInfo getInfo(Object key) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet                    rs = null;

		DatabaseMetaData  dbMeta = jdbc.conn.getMetaData();
		String lm_driverName = dbMeta.getDriverName();

		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE, FILE_VERSION from " +  env.docTableName;
		if(key instanceof String)
		{
			qry += " where FULL_PATH = '" + key + "'";
		}
		else if(key instanceof Integer)
			qry += " where nDocID = " + key;
		L.debug(qry);
		try	{
			pstmt = jdbc.conn.prepareStatement(qry);

			rs = pstmt.executeQuery();

			DocInfo docInfo = new DocInfo();
			String d = null;
			Calendar cal = null;
			if(rs.next())
			{

				docInfo.idx = rs.getInt(1);
				docInfo.name  = rs.getString(2);
				docInfo.doc_fg  = rs.getString(3).charAt(0);
				docInfo.fullPath  = rs.getString(4);
				docInfo.pdocID = rs.getInt(5);
				docInfo.docDesc = rs.getString(6);
				docInfo.createUserID = rs.getString(7);
			    cal = Calendar.getInstance();
			    d = rs.getString(8);

				docInfo.createDate = docConnector.makeCal(d);
				cal = Calendar.getInstance();
			    d = rs.getString(9);

				docInfo.modDate = docConnector.makeCal(d);
				docInfo.file_version = rs.getString(10);

			}
			rs.close();
			pstmt.close();
			return docInfo;
		}catch(SQLException e){
			RequbeUtil.do_PrintStackTrace(L, e);
			fieldAdd();
		}catch(Exception ex){
			RequbeUtil.do_PrintStackTrace(L, ex);
		}finally {
			if(lm_driverName.indexOf("JDBC-ODBC") == -1){
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}

		}
		return null;
	}

	public int addDocLoop(Enumeration lm_objnames, Hashtable hash, String createID, HashMap ohm){
		try {
			Environment tenv = Environment.getInstance();
		    int succ_cnt = 0;
			jdbc = new JDBCHelper();
			jdbc.connect();
			
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);

			while(lm_objnames.hasMoreElements()){
				String objname = (String)lm_objnames.nextElement();
				//out.println(objname);
				//File file = multi.getFile(objname);
				File file = (File) hash.get(objname);

				if(file != null){
					String desc = (String) ohm.get( file.getName());
					if(desc == null){
						desc = "";
					}

					if ((addDocInfo(file, createID, desc) ) < 0){
						RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
					}else{
						if(insertSQLDB(getName(file))){
							RequbeUtil.setTransaction(jdbc.conn, "commit", true);
							succ_cnt++;
						}else{
							RequbeUtil.setTransaction(jdbc.conn, "rollback", true);

							if (succ_cnt > 0)
								return -1;
							else
								return -101; //parsing error
						}
					}
					//doc parse 07.03.23
					//jdbc.close();
				}
				//out.println("size : " + file.length() + "Byte" + "<br>");
			}
			return 0;
		}catch( Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}finally {
			jdbc.close();
		}

	}

	public int addDocInfo(File f, String createID, String description)
	{
		int parentID;
		// Check id
		String docname = getName(f);
		int docID = getID(docname);
		int rtn_cd = 0;

		if(docID != 0)
		{
			DocInfo di = new DocInfo();
			di.idx = docID;
			di.name = f.getName();
			di.createUserID = createID;
			di.doc_fg = 'D';
			di.docDesc = description;
			di.fullPath = getName(f);
			di.modDate =  Calendar.getInstance();
			if(di.name.length() > 0)
			{
				parentID = getID(di.fullPath.substring(0,di.fullPath.length() - di.name.length()));
				if(parentID != 0)
				{
					di.pdocID = parentID;
				}
				else
					return -101;

			}
			rtn_cd = updateInfo(new Integer(docID), (RQInfo)di);
			return rtn_cd;
		}
		else
		{
			docID = f.hashCode();
			//String docname = getName(f);
			DocInfo di = new DocInfo();
			di.idx = docID;
			di.name = f.getName();
			di.createUserID = createID;
			di.doc_fg = 'D';
			di.docDesc = description;
			di.fullPath = getName(f);
			di.createDate = Calendar.getInstance();
			di.modDate =  Calendar.getInstance();
			if(di.name.length() > 0)
			{
				String parentPath = di.fullPath.substring(0,di.fullPath.length() - di.name.length());
				parentID = getID(parentPath);
				if(parentID != 0){
					di.pdocID = parentID;
				}else{
					//make parent folder ... Repository 에 해당 Directory 가 존재하지 않을경우
					try{
						DocRepository lm_docR = new DocRepository();
						lm_docR.makeFolder(parentPath);
					}catch (Exception e) {
						e.printStackTrace();
					}
					parentID = (getFile(parentPath).hashCode());
					di.pdocID = parentID;
				}
			}
			rtn_cd = InsertInfo((RQInfo)di);
			return rtn_cd;
		}
	}

	public int InsertInfo(RQInfo info) {
		PreparedStatement pstmt = null;
		try
		{
			String qry = "Insert into  "  + env.docTableName + "(nDocID, File_NM, DOC_FG, Full_PATH, nPDocID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE)";
			qry += " values (" + info.getParamInt(0) + ", '"+ RequbeUtil.convertForSQL(info.getParamString(1))+ "', '" + info.getParamString(2) + "', '" +RequbeUtil.convertForSQL(info.getParamString(3))
			+ "', " + info.getParamInt(4) + ", '" + RequbeUtil.convertForSQL(info.getParamString(5))
			+ "', '" + RequbeUtil.convertForSQL(info.getParamString(6)) + "', '" + info.getParamString(7) + "', '"+ info.getParamString(8) +"')";
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			pstmt.close();

		}catch(Exception ex)
		{
			ex.printStackTrace();
			//RequbeUtil.do_PrintStackTrace(L, ex);
			return -1;

		}
		return 0;

	}

	public int updateInfo(Object key, RQInfo info) {
		PreparedStatement pstmt = null;
		int rc = 0;
		try {
			String update_qry = "update " + env.docTableName + " set file_nm = '" + info.getParamString(1) + "'"
			                  + " , doc_fg = '" + info.getParamString(2) + "'"
			                  + " , full_path='" + info.getParamString(3) + "'"
			                  + " , npdocid=" + info.getParamInt(4) +" "
			                  + " , doc_desc='" + info.getParamString(5) +"'"
			                  + " , create_user_id='" + info.getParamString(6) + "'"
			                  + " , MOD_DATE='" + info.getParamString(7)  +"'";
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				update_qry        += " , FILE_VERSION=( select file_version+1 from rqdoc where nDocID = " + key +")";
			}else{
				update_qry        += " , FILE_VERSION= file_version + 1 ";
			}
			update_qry        += " where nDocID = "+ key;
			
			pstmt = jdbc.conn.prepareStatement(update_qry);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}

		return 0;
	}
	
	// CDC dev. code
	public int delDocArray(String path)
	{
		L.debug("deleteDoc:" + path);
		Environment env = Environment.getInstance();
		String lm_fullpath = env.rqxfile_backup_dir + path;		// Backup Dir path
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			File f = getFile(path);
			L.debug("deleteDocPath:" + f.getPath());
			int docID = 0;
			if(f != null)
			{
				docID = getID(path);
			}
			
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
			
			ArrayList arry = getDirSonIDandSave("" + docID);
			arry.add("" + docID);
			//System.out.print(arry);
			for (int i = 0 ; i < arry.size(); i++){
				String docid = (String)arry.get(i);
				int res = 0;
				if (getFD(docid).equalsIgnoreCase("D") ){
					res = deleteDocAll(docid);
				}else{
				    res = deleteDocOnly(docid);	
				}
				if (res < 0) {
					L.debug("rollback" + docid);
					RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
					return -1;
				}	
			}
			RequbeUtil filealldel = new RequbeUtil();
			filealldel.del(f.getPath());
			f.delete();
			RequbeUtil.setTransaction(jdbc.conn, "commit", true);
			return 0;
		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			jdbc.close();
		}
	}
				

		    
	/**
	 *
	 * @param path File f를  삭제한다.
	 * @return
	 */
	public int delDoc(String path)
	{
		L.debug("deleteDoc:" + path);
		Environment env = Environment.getInstance();
		String lm_fullpath = env.rqxfile_backup_dir + path;		// Backup Dir path
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			File f = getFile(path);
			L.debug("deleteDocPath:" + f.getPath());
			int docID = 0;
			if(f != null)
			{
				//docID = f.hashCode();
				docID = getID(path);
			}
			
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
			
			// CDC dev. code ///////////////////////////////////////
			ArrayList arry = getDirSonIDandSave("" + docID);
			arry.add("" + docID);
			//System.out.print(arry);
			////////////////////////////////////////////////////////

			int res = deleteDocInfo("" + docID);

			if (res < 0) {
				L.debug("rollback1");
				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
				return -1;
			}
			if(f.isDirectory())
			{
				f.delete();
				if(sync != null)
					sync.deleteFolder(path);
				File fback = new File(lm_fullpath);
				fback.delete();
			}
			else
			{
				res = deleteDSInfo(new Integer(docID));

				if (res < 0) {
					L.debug("rollback2");
					RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
					return -1;
				}

				res = deleteSQLInfo(new Integer(docID));

				if(res == 0 )
				{
					//파일 삭제
					f.delete();

					// 압축파일 기능 추가로 인한 해당파일 모두 삭제
					if(env.document_zip_option.equals("yes")){
						String rqxfullpath = f.getAbsolutePath();
						String zippath = rqxfullpath.substring(0, rqxfullpath.lastIndexOf("."));
						String zipfullpath = zippath + ".zip";
						File zf = new File(zipfullpath);
						zf.delete();
					}

					if(sync != null)
						sync.deleteDoc(path);

					// history 기능 추가에 따른 파일이름으로 모두 삭제
					if(env.rqxhistory.equals("yes")){

						String lm_dir = lm_fullpath.substring(0, lm_fullpath.lastIndexOf("/"));
						String lm_fname = lm_fullpath.substring(lm_fullpath.lastIndexOf("/") + 1 , lm_fullpath.length());
						//String lm_name = lm_fname.substring(0, lm_fname.lastIndexOf("."));

						String filename = "";
						String fname = "";
						File lm_f = new File(lm_dir);
						File[] lm_flist = lm_f.listFiles();
						if(lm_flist != null){
							for(int i = 0 ; i < lm_flist.length ; i++){
								if(!lm_flist[i].isFile()) continue;
								filename = lm_flist[i].getName();
								fname = filename.substring(0, filename.lastIndexOf("."));
								if(fname.equals(lm_fname)){
									lm_flist[i].delete();
								}
							}
						}
						//String ori_fullpath = rootPath + path;                  // Repository path
						//File ori_f = new File(ori_fullpath);
						//ori_f.delete();
					}

				}
				else {
					L.debug("rollback3");
					RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
					return -1;
				}
			}
			RequbeUtil.setTransaction(jdbc.conn, "commit", true);
			return 0;
		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			jdbc.close();
		}
	}

	public int deleteDocInfo(Object key) {
		try {
			PreparedStatement pstmt = null;

			int id = Integer.parseInt(key.toString());
			ArrayList arr = getMutiDocInfo(new Integer(id));
			if(arr.size() != 0)
			{
				return -100;//  하위에 문서가 존재합니다..
			}
			String qry = "Delete from " + env.docTableName;
			qry += " where nDocID = "+ key;
			L.debug(qry);
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		return 0;
	}
	
	// CDC DEV. code
	public int deleteDocOnly(String key){
		try {
		Statement stmt = jdbc.conn.createStatement();
		String qry = "Delete from " + env.docTableName + " where nDocID = "+ key;
	    stmt.execute(qry);
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}		
		return 0;
    }
	
	public int deleteDocAll(String key){
		try {
			Statement stmt = jdbc.conn.createStatement();
			String qry1 = "Delete from " + env.docTableName + " where nDocID = "+ key; 
			String qry2 = "Delete from " + env.doc_dsTableName + " where nDocId = " + key;
			String qry3 = "Delete from " + env.doc_sqlTableName + " where nDOCID = " + key;
			stmt.addBatch(qry1);
			stmt.addBatch(qry2);
			stmt.addBatch(qry3);
			int[] deletecounts = stmt.executeBatch();
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}		
		return 0;
			
	}
	
/*
 	public int deleteDSInfo(Object key) {
		try {

			PreparedStatement pstmt = null;
			//String qry1 = "Select nDocId from " + env.docTableName + " where nDocId = " + key;
			String qry2 = "Delete from " + env.doc_dsTableName + " where nDocId = " + key;
			pstmt = jdbc.conn.prepareStatement(qry2);
			pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
*/

	public int deleteSQLInfo(Object key) {
		PreparedStatement pstmt = null;
		try {
			String qry = "Delete from " + env.doc_sqlTableName ;
			qry += " where nDOCID = " + key.toString();

			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			//return -1;
		}finally{
			if(pstmt != null) try{pstmt.close();}catch(SQLException e){RequbeUtil.do_PrintStackTrace(L, e);}
		}
		return 0;

	}

	/**
	 * To make Folder
	 * @param path
	 * @return
	 */

	public int makeFolder(String path)
	{
		int res = 0;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			File f = getFile(path);
			L.debug("make:" + f.getPath());
			if(f != null )
			{
				if(!RequbeUtil.verifyParam(f.getName(),255,"!@~`$%^&*()+|\"':;"))
					return -103;
				f.mkdirs();
			}
			if(!(f.exists() && f.isDirectory()))
			{
				return -104;
			}

			DocInfo di = new DocInfo();
			di.idx = f.hashCode();
			di.name = f.getName();
			di.fullPath = getName(f);
			int pos = di.fullPath.lastIndexOf("/", di.fullPath.length() - 2);
			di.pdocID = getID(di.fullPath.substring(0, pos + 1));
			if(di.pdocID == -1)
			{
				return - 100;
			}
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			di.createDate = c;
			di.modDate = c;
			di.docDesc = "DIR";
			di.doc_fg = 'F';

			if(RQControl.nowUserModel != null){
				UserModel UM = (UserModel) RQControl.nowUserModel;
				di.createUserID = UM.getUserid();
			}else{
				di.createUserID = userID;
			}

			if(f != null )
			{
			//f.mkdir();
			//Synchronize Repository
				res = docConnector.writeInfo((RQInfo) di);
				if( res != 0 )
				{
					f.delete();
				}
				else
				{
					if(sync != null)
						sync.makeFolder(path);
				}
			}

			return res;
		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			jdbc.close();
		}
	}

	public int getDocInfo(String full_path, List ret)
	{
//		File f = getFile(path);
//		if(f != null )
//		{
//			try {
//				DocInfo info = (DocInfo)docConnector.getInfo("" + f.hashCode());
//				ret.add(info);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}


		try {
			DocInfo info = (DocInfo)docConnector.getInfo(full_path);
			ret.add(info);
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}
		return 0;
	}

	public int getDSList(String docPath)
	{
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int id = getID(docPath);
			L.debug("Doc=" + docPath + ", id=" + id);
			ArrayList arr = (ArrayList)getMutiDSInfo(new Integer(id));
			if(arr != null)
				list = arr;
			return 0;
		}
		catch (Exception e) {
			return -1;
		}
		finally {
			jdbc.close();
		}
	}

	public ArrayList getMutiDSInfo(Object condition) {		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String qry = "Select nDocID, DS_NM, nORDER from " +  env.doc_dsTableName;
		ArrayList dslist = new ArrayList(100);

		if (condition instanceof Integer)
		{

			qry += " where nDOCID = " + condition;
			qry += " order by nORDER";
			try
			{
				pstmt = jdbc.conn.prepareStatement(qry);
				rs = pstmt.executeQuery();
				DSListInfo dsListInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())
				{
					dsListInfo = new DSListInfo();
					dsListInfo.docID = rs.getInt(1);
					dsListInfo.DSName  = rs.getString(2);
					dsListInfo.order   = rs.getInt(3);
					dslist.add(dsListInfo);
					L.debug("docID=" + dsListInfo.docID + ", DSName=" + dsListInfo.DSName + ", order=" + dsListInfo.order);
				}

			}catch(Exception ex){
				RequbeUtil.do_PrintStackTrace(L, ex);
			}finally{
				try{
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				}catch(SQLException se){
					RequbeUtil.do_PrintStackTrace(L, se);
				}
			}
		}
		else
		{

		}
		return dslist;
	}

	public String getSQL(String docPath) throws Exception
	{
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int id = getID(docPath);
			RQInfo si = getSQLInfo("" +id);
			return si.getParamString(1);
		}
		catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return null;
		}
		finally {
			jdbc.close();
		}
	}

	/**
	 * DB 에서 SQL 을 가져온다
	 * MySQL 일경우 "SQL" 은 reserved word 이므로 따로 처리 한다.
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public RQInfo getSQLInfo(Object key) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DatabaseMetaData dbMeta = null;

		String qry = "";
		dbMeta = jdbc.conn.getMetaData();
		if( (dbMeta.getDriverName()).indexOf("MySQL") != -1  ){
			qry =  "Select nDocID, SQLDATA from " +  env.doc_sqlTableName;
		}else {
			qry = "Select nDocID, SQL from " +  env.doc_sqlTableName;
		}

		ArrayList sqllist = new ArrayList(100);
		qry += " where nDOCID = " + key.toString();

		L.debug(qry);

		try
		{
			pstmt = jdbc.conn.prepareStatement(qry);
			rs      = pstmt.executeQuery();

			SQLInfo sqlInfo = null;
			String d = null;
			Calendar cal = null;
			if(rs.next())
			{
					sqlInfo = new SQLInfo(0,"");
					sqlInfo.docID = rs.getInt(1);
					String lm_driverNm = dbMeta.getDriverName();
					if(lm_driverNm.indexOf("SQLServer") != -1  ){
						sqlInfo.sql = rs.getString(2);
					}else if( lm_driverNm.indexOf("JDBC-ODBC") != -1  ){
						sqlInfo.sql = rs.getString(2);
					}else{
						Clob c = rs.getClob(2);
						sqlInfo.sql   = c.getSubString((long) 1, (int) c.length());
					}
					return sqlInfo;
			} else
			return null;

		}catch(Exception ex)
		{
			RequbeUtil.do_PrintStackTrace(L, ex);
			return null;
		}
		finally {
			if (rs != null) rs.close();
			if (pstmt != null) pstmt.close();
		}
	}

	public int setDSList(String docPath, String[] dsNames)
	{
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int id = getID(docPath);

			if(dsNames == null || id == 0)
				return -1;

			ArrayList dsList = new ArrayList(dsNames.length);

			for(int i=0; i < dsNames.length; ++i)
			{
				dsList.add(new DSListInfo(id, dsNames[i], i));
			}

			if (updateMultiDSInfo(new Integer(id),dsList) < 0)
				return -1;
			else
				return 0;
		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			jdbc.close();
			RQView.refreshInfo();
		}
	}

	public int updateMultiDSInfo(Object key, ArrayList infoList) {
		try {

			deleteDSInfo(key);

			int size = infoList.size();
			for (int i = 0; i < size; ++i)
			{
				writeDSInfo((RQInfo)infoList.get(i));
			}

			return 0;
		}
		catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#deleteInfo(java.lang.Object)
	 */
	public int deleteDSInfo(Object key) {
		PreparedStatement pstmt = null;

		String qry = "Delete from " + env.doc_dsTableName ;
		if(key instanceof Integer)
		{
			qry += " where nDOCID = " + key.toString();
		}
		else if(key instanceof String)
		{
			qry += " where DS_NM = '" + key.toString() + "'";
		}

		try {
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			return 0;

		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			try {
				if (pstmt != null) pstmt.close();
			}
			catch (SQLException e) {
				RequbeUtil.do_PrintStackTrace(L, e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.reqube.repository.InfoConnector#writeInfo(com.sds.reqube.common.RQInfo)
	 */
	public int writeDSInfo(RQInfo info) {
		PreparedStatement pstmt = null;

		String qry = "Insert into " + env.doc_dsTableName + " (nDOCID, DS_NM, nORDER) values ("
		+ info.getParamInt(0)+ ", '" +info.getParamString(1)+ "', "+ info.getParamInt(2) +")";


		try {
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			return 0;
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			try {
				if (pstmt != null) pstmt.close();
			}
			catch(SQLException e) {
				RequbeUtil.do_PrintStackTrace(L, e);
			}
		}
	}

	/**
	 * 문서내 SQL 을 DB SQLTABLE(env.doc_sqlTableName) 에 각 DBMS 에 맞게 저장한다.
	 * DB가 MySQL 일경우는 따로 체크 하여 Insert 하도록 한다. (MySQL 에서 "SQL" 은 reserved word 이다.)
	 * 처리한다.
	 * @param docPath 문서 경로
	 * @param sql 문새내 SQL
	 * @return
	 */
	public int setSQL(String docPath, String sql)
	{
		Environment tenv = Environment.getInstance();
		PreparedStatement pstmt =  null;
		ResultSet               rs       = null;
		DatabaseMetaData  dbMeta = null;
		
		final int iOracle    = 100;
		final int iMySQL     = 101;
		final int iSQLServer = 102;
		final int iJDBC_ODBC = 200;
		final int iAltibase  = 300;
		final int iInformix  = 400;
		final int iDB2       = 500;
		
		int iDBMS            = 0;
		
		try {
			int id = getID(docPath);

			if(sql == null || id == 0)
				return -1;

			SQLInfo si = new SQLInfo(id, sql);
			String qry = "Delete from " + env.doc_sqlTableName ;
			qry += " where nDOCID = " + id;
			String qry2 = "";
			String qry3 = "";

			dbMeta = jdbc.conn.getMetaData();
			String lm_driverName = 	dbMeta.getDriverName();
			L.debug("driver Name : " + lm_driverName);
			L.debug("meda Info : " + dbMeta);
			
			if(lm_driverName.indexOf("MySQL") != -1){
				iDBMS = iMySQL;
			}else if(lm_driverName.indexOf("SQLServer") != -1){
				iDBMS = iSQLServer;
			}else if(lm_driverName.indexOf("Microsoft") != -1){
				iDBMS = iSQLServer;
			}else if(lm_driverName.indexOf("JDBC-ODBC") != -1){
				iDBMS = iJDBC_ODBC;
			}else if(lm_driverName.indexOf("Altibase") != -1){
				iDBMS = iAltibase;
			}else if(lm_driverName.indexOf("Informix") != -1){
				iDBMS = iInformix;
			}else if(lm_driverName.indexOf("DB2") != -1){
				iDBMS = iDB2;
			}else{
				iDBMS = iOracle;
			}
			
			// Altibase JDBC Driver
			// longvarchar ex) MySQL
			switch (iDBMS) {
			case iMySQL:
			case iSQLServer:
			case iInformix:
			case iJDBC_ODBC:
			case iDB2:
				if(iDBMS == iMySQL){
					qry2 = "Insert into " + env.doc_sqlTableName + " (nDOCID, SQLDATA) values ( ? , ? )";
				}else{
					qry2 = "Insert into " + env.doc_sqlTableName + " (nDOCID, SQL) values ( ? , ? )";
				}
				pstmt = jdbc.conn.prepareStatement(qry);
				pstmt.executeUpdate();
				pstmt.close();

				pstmt = jdbc.conn.prepareStatement(qry2);
				pstmt.setInt(1, id);
				StringReader sr = new StringReader(sql);
				pstmt.setCharacterStream(2, sr, sql.length());

				pstmt.executeUpdate();
				pstmt.close();
				break;
			case iAltibase:
				qry2 = "Insert into " + env.doc_sqlTableName + " (nDOCID, SQL) values (?,?) ";
				pstmt = jdbc.conn.prepareStatement(qry);
				pstmt.executeUpdate();
				pstmt.close();
				
				//LobLocator can not span the transaction XXXXX
				RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
				
				pstmt = jdbc.conn.prepareStatement(qry2);
				pstmt.setInt(1, id);
				StringReader sr_alti = new StringReader(sql);
				pstmt.setCharacterStream(2, sr_alti, sql.length());
				//pstmt.setBinaryStream(2, new ByteArrayInputStream(sql.getBytes()), sql.length());
				pstmt.executeUpdate();
				
				RequbeUtil.setTransaction(jdbc.conn, "commit", true);
				
				pstmt.close();
				
				break;
			default:
				qry2 = "Insert into " + env.doc_sqlTableName + " (nDOCID, SQL) values ("
				+ id+ ", EMPTY_CLOB())";
				qry3 = "Select SQL FROM " + env.doc_sqlTableName + " WHERE nDOCID = ? ";

				pstmt = jdbc.conn.prepareStatement(qry);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt = jdbc.conn.prepareStatement(qry2);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt = jdbc.conn.prepareStatement(qry3);

				pstmt.setInt(1, id);
				rs = pstmt.executeQuery();
//				if(rs.next()){
//					java.sql.Clob tmpClob  = rs.getClob(1);
//					if (tmpClob != null){
//						if(tenv.rqreport_oracle_jdbcDriver_type.equalsIgnoreCase("oraclex86")){
//							oracle.sql.CLOB oraClob = (oracle.sql.CLOB)tmpClob;
//							BufferedWriter writer = new BufferedWriter(oraClob.getCharacterOutputStream());
//							char[] bss = sql.toCharArray();
//							writer.write(bss);
//							writer.flush();
//							writer.close();
//						} else if(tenv.rqreport_oracle_jdbcDriver_type.equalsIgnoreCase("weblogicx86")){
//							handleWeblogicJdbcDriver(tmpClob, sql);
//						}
//					}
//				}		20160905 수정 - by kiyong
				if(rs.next()) {
					StringBuffer sb = new StringBuffer();
					Clob tmpClob = rs.getClob(1);
					if(tmpClob != null) {
						try {
							Reader reader = tmpClob.getCharacterStream();
							char[] buffer = new char[1024];
							int byteRead;
							
							while((byteRead = reader.read(buffer, 0, 1024)) != -1) {
								sb.append(buffer, 0, byteRead);
							}
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				pstmt.close();
				
				break;
			}
			
		} catch (SQLException e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return - 101;
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return - 101;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
			catch (Exception ee) {
				RequbeUtil.do_PrintStackTrace(L, ee);
			}
			RQView.refreshInfo();
		}

		return 0;

	}
	
	public void handleWeblogicJdbcDriver(Clob tmpClob, String sql) throws Exception {
		weblogic.jdbc.vendor.oracle.OracleThinClob oraClob = (weblogic.jdbc.vendor.oracle.OracleThinClob)tmpClob;
		BufferedWriter writer = new BufferedWriter(oraClob.getCharacterOutputStream());
		char[] bss = sql.toCharArray();
		writer.write(bss);
		writer.flush();
		writer.close();
	}
	
	public int delFolder(String path)
	{
		Environment tenv = Environment.getInstance();
		File f = getFile(path);
		int folderID = getID(getName(f));
		L.debug("deletefolder:" + f.getPath());
//		Delete Info
		//docConnector.deleteInfo("" + folderID);
		JDBCHelper jdbc = new JDBCHelper();
		int res = 0;
		try {

			jdbc.connect();
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);
			res = deleteDocInfo("" + folderID);
			if (res < 0) {
				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
				return -1;
			}

			// Delete File
			if(f != null)
			{
				//Synchronize Repository
	
				boolean bres = f.delete();
	//			if(!bres)
	//			{
	//				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
	//				return -1;
	//			}
				if(sync != null)
					sync.deleteFolder(path);
			}
			RequbeUtil.setTransaction(jdbc.conn, "commit", true);
		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
		finally {
			jdbc.close();
		}

		return 0;
	}

	public int makeRepositoryToZip(String filename, String path)
	{

		try {
			zipOut = new ZipOutputStream(new FileOutputStream(filename));
			File f = new File(path);
			zipaddDir(f);
			zipOut.close();
			zipOut = null;
		} catch (FileNotFoundException e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -101;
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return -102;
		}
		return 0;
	}

	  public int zipaddDoc(File f)
	  {
		int len;
		byte[] buf = new byte[4096];
		try {
			FileInputStream fi = new FileInputStream(f);
			if(zipOut != null)
			{
				String name = getName(f);
				System.out.println(name);
				name = new String(java.net.URLEncoder.encode(name, "UTF-8"));
				zipOut.putNextEntry(new ZipEntry(name));
				while ((len = fi.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
                }

			}
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}

		return 1;
	  }

	  public int zipaddDir(File f )
	  {
		File[] names = f.listFiles();
		File temp = null;
		for (int i = 0; i < names.length; ++i)
		{


		  if (names[i].isDirectory() && !(names[i].getName().equals(".") || names[i].getName().equals("..")))
		  {
			//String folder = names[i].getPath().substring(docRootPath.getPath().length());
			//if (folder == null || folder.length() < 1)
			//{
			//  folder = "/";
			//}
			L.debug("dir:" + names[i]);
//			System.out.println("folder:" + folder);

			zipaddDir(names[i]);
		  }
		  else {
				zipaddDoc(names[i]);
		  }

		}
		return names.length;
	  }

	/**
	 * 문서일괄저장
	 * @param f
	 * @return
	 */
    public int addDocSeries(File f){
    	Environment tenv = Environment.getInstance();
    	String docpath =  getName(f);

		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);

			if (insertSQLDB(docpath)){
				RequbeUtil.setTransaction(jdbc.conn, "commit", true);
			}else{
				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
			}
				
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}
		finally {
			jdbc.close();
		}

    	return 1;
    }

    /**
     *  문서일괄저장 문서일경우 addDocSeries(File f) 를 호출한다.
     * @param f
     * @return
     */
    public  int addDirSeries(File f){
    	File[] names = f.listFiles();
    	File temp = null;
	    for (int i = 0; i < names.length; ++i){
	    	if (names[i].isDirectory() && !(names[i].getName().equals(".") || names[i].getName().equals(".."))) {
	    		//System.out.println("dir:" + names[i]);
	    		addDirSeries(names[i]);
	    	}else{
	    		addDocSeries(names[i]);
	        }
	    }
		return names.length;
	}
    public boolean insertSQLDB(String docName){

	   	File f = getFile(docName);
	   	if(f == null)
	   		return false;
	   	try {
			RQQueryParse oQueryParse = new RQQueryParse();
			Document doc = oQueryParse.parseXML(f);
			Element  element = doc.getRootElement().getChild("SQL");
			Document sqldoc = element.getDocument();
			StringWriter sw = new StringWriter();

			XMLOutputter xmlOut = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("EUC-KR");
			xmlOut.setFormat(format);
			xmlOut.output(sqldoc , sw);

			String rt = sw.toString();
			int sqlstrstartlocation = 0;
			int sqlstrendlocation = 0;
			String strSQL = "";
			if( rt.indexOf("<SQL>") != -1 ){
				sqlstrstartlocation = rt.indexOf("<SQL>");
				sqlstrendlocation = rt.indexOf("</SQL>");
				strSQL = rt.substring(sqlstrstartlocation, sqlstrendlocation+6); //6 means "</SQL>"
			}else if(rt.indexOf("<SQL/>") != -1 || rt.indexOf("<SQL />") != -1 || rt.indexOf("<SQL  />") != -1 || rt.indexOf("<SQL   />") != -1){
				strSQL = "<SQL/>";
			}
//
//			int pos2 = -1;
//			if(pos2 > -1)
//			{
				Document doc2 = oQueryParse.parseXML(strSQL);
				List l = doc2.getRootElement().getChildren("SQLStmt");
				Iterator it = l.iterator();
				while(it.hasNext())
				{
					Element el2 = (Element)it.next();
					decryptSQL(el2);
					Element el = el2.getChild("SQLData");
					String sql = el.getText();
					// 암호화되어 있으면 바꾼다.
					if(sql.startsWith("*??*"))
					{
						Decrypter dec = new Decrypter("RQREPORT6**??");
						sql = dec.decrypt(sql.substring(4));
						CDATA cdata = new CDATA(sql.trim());
						el.removeContent();
						el.addContent(cdata);
					}

				}
				sw = new StringWriter();
				xmlOut = new XMLOutputter();
				format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				xmlOut.setFormat(format);
				xmlOut.output(doc2 , sw);
				strSQL = sw.toString();
//			}


//
			int res = setSQL(docName, strSQL); //DBinsert Logic

			if (res < 0)
				return false;
			else
				return true;
		} catch (RuntimeException e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return false;
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			new RQFileUploadException(oFileComp);
			return false;
		}

    }

   public void decryptSQL(Element el)
   {
	   List l = el.getChildren("SQLStmt");
	   if(l.size() < 1)
	   {
		   return;
	   }
	   else
	   {
		   Iterator it = l.iterator();
			while(it.hasNext())
			{
				Element el2 = (Element)it.next();
				decryptSQL(el2);
				Element data = el2.getChild("SQLData");
				String sql = data.getText();
				// 암호화되어 있으면 바꾼다.
				if(sql.startsWith("*??*"))
				{
					Decrypter dec = new Decrypter("RQREPORT6**??");
					sql = dec.decrypt(sql.substring(4));
					CDATA cdata = new CDATA(sql.trim());
					data.removeContent();
					data.addContent(cdata);
				}

			}
			return;
	   }
   }

    // orders 추가로 인한 method 추가
	public int setDSList(String docPath, String[] dsNames, int[] orders){
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int id = getID(docPath);

			if(dsNames == null || id == 0)
				return -1;

			ArrayList dsList = new ArrayList(dsNames.length);

			for(int i=0; i < dsNames.length; ++i){
				dsList.add(new DSListInfo(id, dsNames[i], orders[i]));
			}

			if (updateMultiDS(new Integer(id),dsList) < 0)
				return -1;
			else
				return 0;
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}finally{
			jdbc.close();
			RQView.refreshInfo();
		}
	}

	//	orders 추가로 인한 method 추가
	public int updateMultiDS(Object key, ArrayList infoList){
		try {

			int size = infoList.size();

			for(int j = 0 ; j < size ; j++ ){
				deleteDSInfo(key, ((DSListInfo)infoList.get(j)).getParamInt(2) );
			}

			for (int i = 0; i < size; ++i){
				writeDSInfo((RQInfo)infoList.get(i));
			}

			return 0;
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}
	}

	// orders 추가로 인한 method 추가
	public int deleteDSInfo(Object key, int order) {
		PreparedStatement pstmt = null;

		String qry = "Delete from " + env.doc_dsTableName ;
		if(key instanceof Integer){
			qry += " where nDOCID = " + key.toString();
		}else if(key instanceof String){
			qry += " where DS_NM = '" + key.toString() + "'";
		}
		qry += " and nORDER =" +order+ "";

		try {
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			return 0;

		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return -1;
		}finally{
			try{
				if (pstmt != null) pstmt.close();
			}catch (SQLException e){
				RequbeUtil.do_PrintStackTrace(L, e);
			}
		}
	}

	// 검색으로 인한 method 추가
	public boolean listDoc(String path, String searchword){
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int pID = getID(path);
			list = getMutiDocInfo(new Integer(pID) , searchword);
		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return false;
		}finally{
			jdbc.close();
		}
		return true;
	}

	public boolean listDoc(String path, String searchword, String order_flag){
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int pID = getID(path);
			list = getMutiDocInfo(new Integer(pID) , searchword, order_flag);
		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return false;
		}finally{
			jdbc.close();
		}
		return true;
	}

	// 검색으로 인한 method 추가
	public ArrayList getMutiDocInfo(Object condition, String searchword) {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE from " +  env.docTableName;
		ArrayList doclist = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (condition instanceof Integer){

			qry += " where nPDOCID = " + condition;
			qry += " and FILE_NM like '%"+RequbeUtil.convertForSQL(searchword)+"%' ";
			qry += " order by DOC_FG desc , MOD_DATE desc ";
			try{
				pstmt = jdbc.conn.prepareStatement(qry);

				rs = pstmt.executeQuery();
				DocInfo docInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())	{
					docInfo = new DocInfo();
					docInfo.idx = rs.getInt(1);
					docInfo.name  = rs.getString(2);
					docInfo.doc_fg  = rs.getString(3).charAt(0);
					docInfo.fullPath  = rs.getString(4);
					docInfo.pdocID = rs.getInt(5);
					docInfo.docDesc = rs.getString(6);
					docInfo.createUserID = rs.getString(7);
				    cal = Calendar.getInstance();
				    d = rs.getString(8);

					docInfo.createDate = docConnector.makeCal(d);
					cal = Calendar.getInstance();
				    d = rs.getString(9);

					docInfo.modDate = docConnector.makeCal(d);
					doclist.add(docInfo);
				}
			}catch(Exception ex){
				RequbeUtil.do_PrintStackTrace(L, ex);
			}finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				}catch(SQLException sx){
					RequbeUtil.do_PrintStackTrace(L, sx);
				}
			}
		}
		return doclist;
	}

	public ArrayList getMutiDocInfo(Object condition, String searchword, String order_flag) {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE, FILE_VERSION from " +  env.docTableName;
		ArrayList doclist = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (condition instanceof Integer){

			qry += " where nPDOCID = " + condition;
			qry += " and FILE_NM like '%"+RequbeUtil.convertForSQL(searchword)+"%' ";

			if (order_flag.equals("1"))
				qry += " order by DOC_FG desc , FILE_NM ";
			else if (order_flag.equals("2"))
				qry += " order by FILE_NM ";
			else if (order_flag.equals("3"))
				qry += " order by CREATE_USER_ID ";
			else if (order_flag.equals("4"))
				qry += " order by MOD_DATE desc ";
			else
				qry += " order by DOC_FG desc , MOD_DATE desc ";

			try{
				pstmt = jdbc.conn.prepareStatement(qry);

				rs = pstmt.executeQuery();
				DocInfo docInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())	{
					docInfo = new DocInfo();
					docInfo.idx = rs.getInt(1);
					docInfo.name  = rs.getString(2);
					docInfo.doc_fg  = rs.getString(3).charAt(0);
					docInfo.fullPath  = rs.getString(4);
					docInfo.pdocID = rs.getInt(5);
					docInfo.docDesc = rs.getString(6);
					docInfo.createUserID = rs.getString(7);
				    cal = Calendar.getInstance();
				    d = rs.getString(8);

					docInfo.createDate = docConnector.makeCal(d);
					cal = Calendar.getInstance();
				    d = rs.getString(9);

					docInfo.modDate = docConnector.makeCal(d);
					docInfo.file_version = rs.getString(10);
					doclist.add(docInfo);
				}
			}catch(Exception ex){
				RequbeUtil.do_PrintStackTrace(L, ex);
			}finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				}catch(SQLException sx){
					RequbeUtil.do_PrintStackTrace(L, sx);
				}
			}
		}
		return doclist;
	}

	public int getDocID(String path){
		int ID = 0;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			ID = getID(path);
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
			return ID;
		}finally{
			jdbc.close();
		}
		return ID;
	}

	public boolean getListFD(String path, String F_D){
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();

			int pID = getID(path);
			list = getFolderList(new Integer(pID), F_D );
		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
			return false;
		}finally{
			jdbc.close();
		}
		return true;
	}

	public ArrayList getFolderList(Object condition, String F_D) {
		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE from " +  env.docTableName;
		ArrayList doclist = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		if (condition instanceof Integer){
			qry += " where DOC_FG = " + "'" + F_D + "' ";
			if(F_D.equals("D")){
				qry += " and nPDOCID = " + condition;
			}
			qry += " order by FILE_NM asc ";

			try{
				pstmt = jdbc.conn.prepareStatement(qry);

				rs = pstmt.executeQuery();
				DocInfo docInfo = null;
				String d = null;
				Calendar cal = null;
				while(rs.next())	{
					docInfo = new DocInfo();
					docInfo.idx = rs.getInt(1);
					docInfo.name  = rs.getString(2);
					docInfo.doc_fg  = rs.getString(3).charAt(0);
					docInfo.fullPath  = rs.getString(4);
					docInfo.pdocID = rs.getInt(5);
					docInfo.docDesc = rs.getString(6);
					docInfo.createUserID = rs.getString(7);
				    cal = Calendar.getInstance();
				    d = rs.getString(8);

					docInfo.createDate = docConnector.makeCal(d);
					cal = Calendar.getInstance();
				    d = rs.getString(9);

					docInfo.modDate = docConnector.makeCal(d);
					docInfo.nodename = (docInfo.fullPath).replaceAll("/","_");
					doclist.add(docInfo);
				}
			}catch(Exception ex){
				RequbeUtil.do_PrintStackTrace(L, ex);
			}finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				}catch(SQLException sx){
					RequbeUtil.do_PrintStackTrace(L, sx);
				}
			}
		}
		return doclist;
	}

	public String getDBMSDriverName(){
		Environment tenv = Environment.getInstance();
		String rtn = "";
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			
			RequbeUtil.setTransaction(jdbc.conn, "setAutoCommit", false);

			DatabaseMetaData  dbMeta = jdbc.conn.getMetaData();
			rtn = dbMeta.getDriverName();
			
			RequbeUtil.setTransaction(jdbc.conn, "commit", true);
		} catch (Exception e1) {
			try {
				RequbeUtil.setTransaction(jdbc.conn, "rollback", true);
			} catch (SQLException e) {
				RequbeUtil.do_PrintStackTrace(L, e);
			}
			RequbeUtil.do_PrintStackTrace(L, e1);
		}finally {
			jdbc.close();
		}
		return rtn;
	}

	/**
	 * 등록된 문서의 현재 버전을 가져온다.
	 * @param RepPath
	 * @return
	 */
	public int getCurrentFileVersion(String RepPath){
		int rtn = 0;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			int pID = getID(RepPath);

			rtn = getFileVersion(pID);

		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			jdbc.close();
		}
		return rtn;
	}

	public int getFileVersion(int nDocID) {
		int nowFileversion = 0;
		String qry = "Select FILE_VERSION from " +  env.docTableName;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		qry += " where nDOCID = " + nDocID;
		try	{
			pstmt = jdbc.conn.prepareStatement(qry);
			rs = pstmt.executeQuery();
			while(rs.next()){
				nowFileversion = rs.getInt(1);
			}
		}catch(SQLException ex){
			RequbeUtil.do_PrintStackTrace(L, ex);
		}finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}catch(SQLException se)	{
				se.printStackTrace();
			}
		}
		return nowFileversion;
	}

	/**
	 * 해당 파일의 버전과 수정날짜를 업데이트한다.
	 * @param RepPath Repository 경로
	 * @param version 버전
	 * @param modiday 수정날짜
	 */
	public void setCurrentFileVersion(String RepPath, String version, Calendar modiday){
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			int pID = getID(RepPath);

			setFileVersion(pID, version, modiday);

		}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			jdbc.close();
		}
	}

	public void setFileVersion(int nDocID, String version, Calendar modiday){
		PreparedStatement pstmt = null;
		String strModiday = RequbeUtil.makeDateString(modiday,"%Y%M%D%H%m%S");
		String update_qry = "update " + env.docTableName + " set MOD_DATE = '" + strModiday + "'"
						 + " ,   FILE_VERSION = '" + version  +"'"
					     + " where nDocID = "+ nDocID;
		try	{
			pstmt = jdbc.conn.prepareStatement(update_qry);
			pstmt.executeUpdate();
			pstmt.close();
		}catch(SQLException ex){
			RequbeUtil.do_PrintStackTrace(L, ex);
		}finally {
			try {
				if (pstmt != null) pstmt.close();
			}catch(SQLException se)	{
				se.printStackTrace();
			}
		}
	}

	/**
	 * RQDoc Table에 컬럼 추가에 따른 Func
	 *
	 */
	public void fieldAdd(){
		jdbc = new JDBCHelper();
		PreparedStatement pstmt = null;
		try{
			jdbc.connect();

			// Column check !! 없으면 insert !!!
			/*
			DatabaseMetaData  dbMeta = jdbc.conn.getMetaData();
			String schema = dbMeta.getUserName();
			ResultSet rs = dbMeta.getColumns(null, schema , env.docTableName , null);
			String colname = "";
			while(rs.next()){
				colname = rs.getString(4); // 4 : colum name
				System.out.println(colname);
			}*/

			String update_qry = "alter table " + env.docTableName + " add(FILE_VERSION varchar2(100) default '1')";
			pstmt = jdbc.conn.prepareStatement(update_qry);
			pstmt.executeUpdate();
			pstmt.close();
		}catch(SQLException e){
			RequbeUtil.do_PrintStackTrace(L, e);
		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			try {
				if (pstmt != null) pstmt.close();
			}catch(SQLException se)	{
				RequbeUtil.do_PrintStackTrace(L, se);
			}
			jdbc.close();
		}
	}
	
	// CDC DEV. code
    public ArrayList getDirSonIDandSave(String fatherID){
  		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE, FILE_VERSION from " +  env.docTableName;
		ArrayList doclist = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		qry += " where nPDOCID = " + fatherID;
		qry += " order by DOC_FG desc , FILE_NM asc";
		try{
			pstmt = jdbc.conn.prepareStatement(qry);
			rs = pstmt.executeQuery();
			DocInfo docInfo = null;
			while(rs.next()){
				docInfo = new DocInfo();
				docInfo.idx = rs.getInt(1);
				//docInfo.name  = rs.getString(2);
				//System.out.print(docInfo.name);
				docInfo.doc_fg  = rs.getString(3).charAt(0);
				doclist.add("" + docInfo.idx);
				if (docInfo.doc_fg == 'F' ){
					ArrayList docson = getDirSonIDandSave(""+ docInfo.idx);
					doclist.addAll(docson);
				}
			}
		}catch(Exception ex){
			RequbeUtil.do_PrintStackTrace(L, ex);
		}finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}catch(SQLException sx){
				RequbeUtil.do_PrintStackTrace(L, sx);
			}
		}
		return doclist;
	}
}