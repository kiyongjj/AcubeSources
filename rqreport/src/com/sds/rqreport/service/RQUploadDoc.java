package com.sds.rqreport.service;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sds.rqreport.Environment;
import com.sds.rqreport.common.RQInfo;
import com.sds.rqreport.repository.*;
import com.sds.rqreport.service.queryexecute.RQQueryParse;
import com.sds.rqreport.service.web.RQControl;
import com.sds.rqreport.util.Decrypter;
import com.sds.rqreport.util.RequbeUtil;

public class RQUploadDoc {
	
	Environment tenv = Environment.getInstance();
	ResourceBundle m_rb = ResourceBundle.getBundle("rqreport");
	String rootpath = m_rb.getString("rqreport.repository.path");
	RepositoryEnv env = null;
	InfoConnector docConnector = null; 
	
	
	File rootFile = new File(rootpath);
	RQUploadDocDAO jdbc = null;
	
	String createID = "";          	    // arg[0]
	String jndiname = "";		        // arg[1]
	String targetDoc = "";				// arg[2]
	String updocpath_pysical = "";	    // arg[3]
	
	String jdbcDriver = "";				// arg[4]
	String connStr = "";				// arg[5]
	
	public RQUploadDoc() {
		jdbc = new RQUploadDocDAO();
		env = Environment.getRepositoryEnv();
		docConnector = new RQDocConnector();
	}
	
	//// TEST Main Function //////////////////////////////////////////////////
	public static void main(String[] args){
		RQUploadDoc ohandle = new RQUploadDoc();
		
		//ohandle.createID = "admin";          	                // arg[0]
		//ohandle.jndiname = "jdbc/Reqube";		                // arg[1]
		//ohandle.targetDoc = "/LJG/";				            // arg[2]
		//ohandle.updocpath_pysical = "D:\\temp\\RQReport1.rqx";	// arg[3]
		//ohandle.jdbcDriver = "oracle.jdbc.driver.OracleDriver"; // arg[4]
		//ohandle.connStr = "jdbc:oracle:thin:rqadmin_sun/easybase@70.7.120.214:1521:WORLDAV"; // arg[5]
		int needItems = 6;
		if(args.length < 6){
			System.out.println("No match Argument (need " +needItems+ " items)");
			System.exit(0);
		}
		for(int i = 0 ; i < needItems ; i ++){
			if(args[i] == null || args[i].equals("")){
				System.out.println(i + " th. Argument has invalid value");
				System.exit(0);
			}
		}
		try{
			ohandle.createID  = args[0];
			ohandle.jndiname  = args[1];
			ohandle.targetDoc = args[2];
			ohandle.updocpath_pysical = args[3];
			ohandle.jdbcDriver = args[4];
			ohandle.connStr = args[5];
			
			ohandle.multiupdoc_file(ohandle.createID, ohandle.jndiname, 
					ohandle.targetDoc, ohandle.updocpath_pysical);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void multiupdoc_file(String createID, String jndiname, String targetFolderRel, String updocpath) throws Exception{

		File ofile = new File(updocpath);
		if(ofile.isFile()){

			// 해당 파일 업로드 (정확히 말하면 서버에서 서버로 옮기는것이므로 Copy 이다.)
			String sourcepath = ofile.getAbsolutePath();
			String targetPath = rootpath + targetFolderRel + ofile.getName();

			FileInputStream      fis = new FileInputStream(sourcepath);
			FileOutputStream     fos = new FileOutputStream(targetPath);

			BufferedInputStream  bis = new BufferedInputStream(fis);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			try {
				byte[] buffer = new byte[512];
				int readcount = 0;
				while((readcount = bis.read(buffer)) != -1){
					bos.write(buffer,0,readcount);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try{
					bis.close();
					bos.close();
					fis.close();
					fos.close();
				}catch(Exception e){}
			}

			File sourcefile = new File(sourcepath);
			File targetfile = new File(targetPath);

			//Copy 파일로 나머지 Repository 관련 작업을 한다.
			Vector v = new Vector();
			v.add(targetfile.getName());
			Enumeration lm_objnames = v.elements();

			Hashtable hash = new Hashtable();
			hash.put(targetfile.getName(), targetfile);

			HashMap ohm = new HashMap();

			//문서 파싱, 문서 등록
			addDocLoop(lm_objnames, hash, createID, ohm); //do !

			//JNDI mapping
			String docPath = getName(targetfile);
			String[] dsNames = new String[1];
			dsNames[0] = jndiname;
			setDSList(docPath, dsNames);
		}

	}
	
	public void multiupdoc_folder(String createID, String jndiname, String targetFolderRel, String updocpath) throws Exception{

		File ofile = new File(updocpath);

		File[] filelist = ofile.listFiles();
		File eachfile = null;
		for(int i = 0 ; i < filelist.length ; i++){
			if(filelist[i].isFile()){
				eachfile = filelist[i];
				// 해당 파일 업로드 (정확히 말하면 서버에서 서버로 옮기는것이므로 Copy 이다.)
				String sourcepath = eachfile.getAbsolutePath();
				String targetPath = rootpath + targetFolderRel + eachfile.getName();

				FileInputStream      fis = new FileInputStream(sourcepath);
				FileOutputStream     fos = new FileOutputStream(targetPath);

				BufferedInputStream  bis = new BufferedInputStream(fis);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				try {
					byte[] buffer = new byte[512];
					int readcount = 0;
					while((readcount = bis.read(buffer)) != -1){
						bos.write(buffer,0,readcount);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					try{
						bis.close();
						bos.close();
						fis.close();
						fos.close();
					}catch(Exception e){}
				}

				File sourcefile = new File(sourcepath);
				File targetfile = new File(targetPath);

				//Copy 파일로 나머지 Repository 관련 작업을 한다.
				Vector v = new Vector();
				v.add(targetfile.getName());
				Enumeration lm_objnames = v.elements();

				Hashtable hash = new Hashtable();
				hash.put(targetfile.getName(), targetfile);

				HashMap ohm = new HashMap();

				//문서 파싱, 문서 등록
				addDocLoop(lm_objnames, hash, createID, ohm); //do !

				//JNDI mapping
				String docPath = getName(targetfile);
				String[] dsNames = new String[1];
				dsNames[0] = jndiname;
				setDSList(docPath, dsNames);

			}else if(filelist[i].isDirectory()
					  && !filelist[i].getName().equals(".")
					  && !filelist[i].getName().equals("..")){

				eachfile = filelist[i];
				// 폴더일경우 폴더를 만들고
				//System.out.println(eachfile.getAbsolutePath());
				String nowFoldernameRel = targetFolderRel + eachfile.getName()+ "/";
				RQControl oRQControl = new RQControl();
				try {
					oRQControl.RQmakeFoler(nowFoldernameRel);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 그 폴더 안으로
				multiupdoc_folder(createID, jndiname, nowFoldernameRel, eachfile.getAbsolutePath());
			}
		}

	}
	
	public int addDocLoop(Enumeration lm_objnames, Hashtable hash, String createID, HashMap ohm){
		try {
		    int succ_cnt = 0;
			jdbc = new RQUploadDocDAO();
			jdbc.connect(jdbcDriver, connStr);
			
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
			e.printStackTrace();
			return -1;
		}finally {
			jdbc.close();
		}
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
			}else if(rt.indexOf("<SQL/>") != -1 || rt.indexOf("<SQL />") != -1 
					|| rt.indexOf("<SQL  />") != -1 || rt.indexOf("<SQL   />") != -1){
				strSQL = "<SQL/>";
			}
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

			int res = setSQL(docName, strSQL); //DBinsert Logic

			if (res < 0)
				return false;
			else
				return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

    }
	
	public int setSQL(String docPath, String sql){
		PreparedStatement pstmt =  null;
		ResultSet               rs       = null;
		DatabaseMetaData  dbMeta = null;
		
		final int iOracle    = 100;
		final int iMySQL     = 101;
		final int iSQLServer = 102;
		final int iJDBC_ODBC = 200;
		final int iAltibase  = 300;
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
			
			if(lm_driverName.indexOf("MySQL") != -1){
				iDBMS = iMySQL;
			}else if(lm_driverName.indexOf("SQLServer") != -1){
				iDBMS = iSQLServer;
			}else if(lm_driverName.indexOf("JDBC-ODBC") != -1){
				iDBMS = iJDBC_ODBC;
			}else if(lm_driverName.indexOf("Altibase") != -1){
				iDBMS = iAltibase;
			}else{
				iDBMS = iOracle;
			}

			switch (iDBMS) {
			case iMySQL:
			case iSQLServer:
			case iJDBC_ODBC:
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
				if(rs.next()){
					java.sql.Clob tmpClob  = rs.getClob(1);
					if (tmpClob != null){

						if (dbMeta instanceof oracle.jdbc.driver.OracleDatabaseMetaData) {
							oracle.sql.CLOB oraClob = (oracle.sql.CLOB)tmpClob;
							BufferedWriter writer = new BufferedWriter(oraClob.getCharacterOutputStream());
							char[] bss = sql.toCharArray();
							writer.write(bss);
							writer.flush();
							writer.close();
						} else {
							weblogic.jdbc.vendor.oracle.OracleThinClob oraClob = (weblogic.jdbc.vendor.oracle.OracleThinClob)tmpClob;
							BufferedWriter writer = new BufferedWriter(oraClob.getCharacterOutputStream());
							char[] bss = sql.toCharArray();
							writer.write(bss);
							writer.flush();
							writer.close();
						}
					}
				}
				pstmt.close();
				
				break;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return - 101;
		} catch (Exception e) {
			e.printStackTrace();
			return - 101;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
			catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		return 0;

	}
	
	public int getID(String fullPath){

		DocInfo di = null;
		try	{
			di = (DocInfo)getInfo(fullPath);
			if(di == null)
				return -1;
		}catch(Exception ex){
			ex.printStackTrace();
			return -1;
		}
		return di.getParamInt(0);
	}
	
	public RQInfo getInfo(Object key) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet                    rs = null;

		DatabaseMetaData  dbMeta = jdbc.conn.getMetaData();
		String lm_driverName = dbMeta.getDriverName();

		String qry = "Select nDocID, FILE_NM , DOC_FG,  FULL_PATH, nPDOCID, DOC_DESC, CREATE_USER_ID, " +
				     "CREATE_DATE, MOD_DATE, FILE_VERSION from " +  env.docTableName;
		if(key instanceof String){
			qry += " where FULL_PATH = '" + key + "'";
		} else if(key instanceof Integer){
			qry += " where nDocID = " + key;
		}
		try	{
			pstmt = jdbc.conn.prepareStatement(qry);
			rs = pstmt.executeQuery();

			DocInfo docInfo = new DocInfo();
			String d = null;
			Calendar cal = null;
			if(rs.next()){
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
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			if(lm_driverName.indexOf("JDBC-ODBC") == -1){
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
		}
		return null;
	}
	
	public void decryptSQL(Element el){
		List l = el.getChildren("SQLStmt");
		if(l.size() < 1){
			return;
		}else{
			Iterator it = l.iterator();
			while(it.hasNext()){
				Element el2 = (Element)it.next();
				decryptSQL(el2);
				Element data = el2.getChild("SQLData");
				String sql = data.getText();
				// 암호화되어 있으면 바꾼다.
				if(sql.startsWith("*??*")){
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
	
	public File getFile(String path){
		if(path.startsWith("/")){
			//rootFile.hashCode()
			return getFile(path, rootFile);
		}else{
			return null;
		}
	}
	
	public File getFile(String path, File parent){
		if(path.startsWith("/")){
			String name;
			int pos = path.indexOf("/",1);
			if(pos > 0){
				name = path.substring(0,pos);
				if(!RequbeUtil.verifyParam(name, 255, "&^%$#@!*\"'&"))
					return null;
			}else{
				return new File(parent.getPath() + File.separator + path.substring(1));
			}

			if(name.equals("/"))
				return parent;
			File child = new File(parent.getPath() + File.separator  + name.substring(1));
			return getFile(path.substring(pos),child);
		}else{
			return parent;
		}
	}
	
	public String getName(File f){
		if(f == null ){
			return null;
		}else if(f != null && f.equals(rootFile)){
			return "/";
		}else{
			if(f.isDirectory())
				return getName(f.getParentFile()) + f.getName() + "/";
			else
				return getName(f.getParentFile()) + f.getName();
		}
	}
	
	public int setDSList(String docPath, String[] dsNames){
		try {
			jdbc = new RQUploadDocDAO();
			jdbc.connect(jdbcDriver, connStr);

			int id = getID(docPath);

			if(dsNames == null || id == 0)
				return -1;

			ArrayList dsList = new ArrayList(dsNames.length);

			for(int i=0; i < dsNames.length; ++i){
				dsList.add(new DSListInfo(id, dsNames[i], i));
			}

			if (updateMultiDSInfo(new Integer(id),dsList) < 0){
				return -1;
			}else{
				return 0;
			}
		}catch (Exception e) {
			return -1;
		}finally{
			jdbc.close();
		}
	}
	
	public int updateMultiDSInfo(Object key, ArrayList infoList) {
		try {
			deleteDSInfo(key);
			int size = infoList.size();
			for (int i = 0; i < size; ++i){
				writeDSInfo((RQInfo)infoList.get(i));
			}
			return 0;
		}catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public int writeDSInfo(RQInfo info){
		PreparedStatement pstmt = null;

		String qry = "Insert into " + env.doc_dsTableName + " (nDOCID, DS_NM, nORDER) values ("
		+ info.getParamInt(0)+ ", '" +info.getParamString(1)+ "', "+ info.getParamInt(2) +")";
		try{
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			return 0;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally {
			try {
				if (pstmt != null) pstmt.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int deleteDSInfo(Object key){
		PreparedStatement pstmt = null;

		String qry = "Delete from " + env.doc_dsTableName ;
		if(key instanceof Integer){
			qry += " where nDOCID = " + key.toString();
		}else if(key instanceof String){
			qry += " where DS_NM = '" + key.toString() + "'";
		}

		try {
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			return 0;

		}catch (Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			try{
				if (pstmt != null) pstmt.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int addDocInfo(File f, String createID, String description){
		int parentID;
		// Check id
		String docname = getName(f);
		int docID = getID(docname);
		int rtn_cd = 0;

		if(docID != 0){
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
		}else{
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
	
	public int InsertInfo(RQInfo info){
		PreparedStatement pstmt = null;
		try{

			String qry = "Insert into  "  + env.docTableName + "(nDocID, File_NM, DOC_FG, Full_PATH, nPDocID, DOC_DESC, CREATE_USER_ID, CREATE_DATE, MOD_DATE)";
			qry += " values (" + info.getParamInt(0) + ", '"+ RequbeUtil.convertForSQL(info.getParamString(1))+ "', '" + info.getParamString(2) + "', '" +RequbeUtil.convertForSQL(info.getParamString(3))
			+ "', " + info.getParamInt(4) + ", '" + RequbeUtil.convertForSQL(info.getParamString(5))
			+ "', '" + RequbeUtil.convertForSQL(info.getParamString(6)) + "', '" + info.getParamString(7) + "', '"+ info.getParamString(8) +"')";
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.executeUpdate();
			pstmt.close();

		}catch(Exception ex){
			ex.printStackTrace();
			//RequbeUtil.do_PrintStackTrace(L, ex);
			return -1;

		}
		return 0;

	}
	
	public int updateInfo(Object key, RQInfo info){
		PreparedStatement pstmt = null;
		int rc = 0;
		try {
			String update_qry    = "update " + env.docTableName + " set file_nm = '" + info.getParamString(1) + "'"
			                              + " , doc_fg = '" + info.getParamString(2) + "'"
			                              + " , full_path='" + info.getParamString(3) + "'"
			                              + " , npdocid=" + info.getParamInt(4) +" "
			                              + " , doc_desc='" + info.getParamString(5) +"'"
			                              + " , create_user_id='" + info.getParamString(6) + "'"
			                              + " , MOD_DATE='" + info.getParamString(7)  +"'"
			                              + " , FILE_VERSION=( select file_version+1 from rqdoc where nDocID = " + key +")"
					                      + " where nDocID = "+ key;

				pstmt = jdbc.conn.prepareStatement(update_qry);
				pstmt.executeUpdate();
				pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}
}
