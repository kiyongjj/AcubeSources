package com.sds.rqreport.service.web;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.GenericServlet;

import org.apache.log4j.Logger;

import com.oreilly.servlet.multipart.*;
import com.oreilly.servlet.multipart.Part;
import com.sds.rqreport.*;
import com.sds.rqreport.model.UserModel;
import com.sds.rqreport.repository.*;
import com.sds.rqreport.util.Encoding;
import com.sds.rqreport.util.RequbeUtil;
import com.sds.rqreport.scheduler.*;

public class RQHandle extends TagSupport{

	private static final long serialVersionUID = 1L;
	private String strAction = "";
	private boolean bIsComplete = false;
	public String m_RQCharset = "";
	public String m_ServerCharset = "";
	private Logger log = Logger.getLogger("RQWEB");

	public int doStartTag() throws JspTagException{

		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;

		if(strAction.equals("delNcrttable")){
            if(delNcrttable()){
                bIsComplete = true;
            }
		}else if(strAction.equals("upLoadPrc")){
			// upviewer & rollback & upload
            if(upLoadPrc()){
                bIsComplete = true;
            }
		}else if(strAction.equals("dbconfigexe")){
            if(dbconfigexe()){
                bIsComplete = true;
            }
		}else if(strAction.equals("viewerStat")){
			if(viewerStat()){
				bIsComplete =true;
			}
		}else if(strAction.equals("rollbackfile")){
			if(rollbackfile()){
				bIsComplete =true;
			}
		}else if(strAction.equals("crtDocStattable")){
			if(crtDocStattable()){
				bIsComplete =true;
			}
        }else{
            bIsComplete = false;
        }
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException{

        if(bIsComplete){
            return EVAL_PAGE;
        }else{
            return SKIP_PAGE;
        }
    }

    public void setAction(String p_strAction){
        strAction = p_strAction;
    }

	private boolean delNcrttable() {
		
		Environment g_env = Environment.getInstance();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response =(HttpServletResponse) pageContext.getResponse();
		HttpSession session = pageContext.getSession();
		JspWriter out = pageContext.getOut();
		RepositoryEnv renv = RepositoryEnv.getInstance();

		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

		String modetype = request.getParameter("modetype");
		String strPathIs = request.getParameter("strPathIs");
		if(strPathIs != null){
			strPathIs = Encoding.chCharset(strPathIs, m_ServerCharset, m_RQCharset);
		}

		if(modetype.equals("del")){

			String[] chkdoc = request.getParameterValues("chkdoc");

			try{

				DocRepository docRep = new DocRepository();

				for(int i=0; i < chkdoc.length ; i++){

					//delete DSList
					int res = docRep.getDSList(Encoding.chCharset(chkdoc[i], m_ServerCharset, m_RQCharset));
					if(res == 0) {
						List dslist = docRep.getList();
						//out.println(dslist.toString());
						int size = dslist.size();
						//out.println(size);
						String[] dsNames = new String[size];
						docRep.setDSList(Encoding.chCharset(chkdoc[i], m_ServerCharset, m_RQCharset), dsNames);
					}

					//out.println(chkdoc[i] + "<br>" );
					//out.println(Encoding.hanToEuc_kr(chkdoc[i]) + "<br>");

					int delDocres = docRep.delDoc(Encoding.chCharset(chkdoc[i], m_ServerCharset, m_RQCharset));
					
					// CDC dev. code ///// with subdir delete
					//int delDocres = docRep.delDocArray(Encoding.chCharset(chkdoc[i], m_ServerCharset, m_RQCharset));

					if(delDocres == -1){
						String msg = (String)rqresource.ht.get("rqhandle.delncrttable.del.error");
						try {
							out.println("<script language='javascript'>alert('" + msg + "');</script>");
						} catch (IOException e) {
							RequbeUtil.do_PrintStackTrace(log, e);
						}
					}

					//docRep.delDoc(Encoding.hanToEuc_kr(chkdoc[i]));
				}

			}catch(UnsupportedEncodingException e){
				RequbeUtil.do_PrintStackTrace(log, e);
			}catch(IOException e){
				RequbeUtil.do_PrintStackTrace(log, e);
			}catch(Exception e){
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			//String strCurrentPage = (String) session.getAttribute("nowPage");
			//response.sendRedirect(request.getHeader("referer") +"?strCurrentPage="+ strCurrentPage);
			try {
				//response.sendRedirect("../document/document.jsp?strPathIs=" + strPathIs + "&strCurrentPage="+ strCurrentPage);
				//session.setAttribute("nowPage", "1");
				//String lm_urlStrPathIs = URLEncoder.encode(strPathIs, "UTF-8");
				if(m_RQCharset.equalsIgnoreCase("UTF-8")){
					strPathIs = URLEncoder.encode(strPathIs, "UTF-8");
				}
					out.println("<script language='javascript'>" +
									//"document.location.href='/rqreport/document/document.jsp?strPathIs="+strPathIs+"&strCurrentPage="+strCurrentPage+ "'"+
//									" 	document.location.href='../document/documentu8.jsp?strPathIs="+strPathIs+"'"+
//									"</script>");
//				}else{
//					out.println("<script language='javascript'>" +
							//"document.location.href='/rqreport/document/document.jsp?strPathIs="+strPathIs+"&strCurrentPage="+strCurrentPage+ "'"+
							" 	document.location.href='../document/document.jsp?strPathIs="+strPathIs+"'"+
							"</script>");
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}

		}else if(modetype.equals("crttable")){
			DocRepository lm_oDocRepository = null;
			String lm_DriverName = "";
			try {
				lm_oDocRepository = new DocRepository();
				lm_DriverName = lm_oDocRepository.getDBMSDriverName();
			} catch (Exception e1) {
				RequbeUtil.do_PrintStackTrace(log, e1);
			}
			if(lm_DriverName.equalsIgnoreCase("SQLServer")){
				//
			}

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
			+  " FILE_VERSION varchar2(100) default '1',"
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
				+ " EMAIL varchar2(50) default '0' ,"
				+ " USER_DESC varchar2(255) default '0' ,"
				+ " GROUPID varchar2(255) default 'DEFAULT' ,"
				+ " AUTH varchar2(10) default 'G' ,"
				+  " PRIMARY KEY  (USERID)"
				+ ")";
			
			String qry4 = "";
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("altibase")){
				qry4 = "CREATE TABLE [name] ("
					+ " NDOCID NUMBER(20) ,"
					+ " SQL CLOB "
					+ ")";
			}else{
				qry4 = "CREATE TABLE [name] ("
					+ " NDOCID NUMBER(20) NOT NULL,"
					+ " SQL CLOB NOT NULL "
					+ ")";	
			}
			
			String scheduleTimetable = "Create Table [name] ("
				+" ScheduleID number(5),"
				+" StartTime DATE,"
				+" StartDate DATE,"
				+" RepeatType NUMBER(5),"
				+" DayOfMonth NUMBER(5),"
				+" DayOfWeek NUMBER(5),"
				+" PeriodOfDay NUMBER(5),"
				+" PeriodOfWeek NUMBER(5),"
				+" PeriodOfMonth NUMBER(5),"
				+" OrdinalOfDayType NUMBER(5),"
				+" DayType NUMBER(5),"
				+" Notification NUMBER(5),"
				+" RepeatFreq NUMBER(5),"
				+" RepeatBoundType NUMBER(5), "
				+" RepeatEndDate DATE, "
				+ "Status NUMBER(5)) ";
			String scheduleRunInfo = "Create Table [name] ( "
			+"ScheduleID NUMBER(5), "
			+"RunInfoID NUMBER(5), "
			+"MailingList VARCHAR2(255), "
			+"UserID VARCHAR2(255), "
			+"Doc VARCHAR2(255), "
			+"Runvar VARCHAR2(4000), "
			+"ResultFile VARCHAR2(255), "
			+"Notification NUMBER(5), "
			+"AttachResult NUMBER(5), "
			+"UserAttach NUMBER(5), "
			+"EmailForm VARCHAR2(255), " 
			+"Dformat VARCHAR2(15) default 'yyyyMMdd')";

			String notification = "Create Table [name] ("
			+"NotificationID NUMBER(5),"
			+"USERID VARCHAR2(255), "
			+"ReceiverType NUMBER(5), "
			+"Email VARCHAR2(255), "
			+"AttachFile VARCHAR2(255))";
			
			String scheduledocdocstatus = "Create Table [name] ("
				+ "Runinfoid VARCHAR2(20), "
				+ "RunDate VARCHAR2(20), "
				+ "Status VARCHAR2(20))";
			
			try {
				RQTableCreator tableCreator = new RQTableCreator();
				Environment env = Environment.getInstance();
				SchedulerEnv scEnv = SchedulerEnv.getInstance(env.schedulerprop);
				int errcnt = 0;
				String msg = "";
				if(tableCreator.makeTable(renv.docTableName, qry1) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.doctable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				if(tableCreator.makeTable(renv.doc_dsTableName, qry2) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.docdstable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				if(tableCreator.makeRootDir(renv.docTableName) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.roordir");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				if(tableCreator.makeTable(renv.doc_userTableName, qry3) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.usertable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				if(tableCreator.makeDefaultUser(renv.doc_userTableName, "DEFAULT") == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.admin");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				if(tableCreator.makeTable(renv.doc_sqlTableName, qry4) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.sqltable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}

				if(tableCreator.makeTable(scEnv.scheduleTimeTableName, scheduleTimetable) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.scheduletime");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}

				if(tableCreator.makeTable(scEnv.scheduleRunInfoTableName, scheduleRunInfo) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.runinfotable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}

				if(tableCreator.makeTable(scEnv.scheduleNotifictionTableName, notification) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.sqltable");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}

				if(tableCreator.makeTable(scEnv.scheduleDocStatus, scheduledocdocstatus) == -1){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.error.scheduledocstatus");
					errcnt++;
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				}
				
				if(errcnt > 0){
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.fail");
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
					out.println("<script language='javascript'>history.back();</script>");
				}else{
					msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttable.success");
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
					out.println("<script language='javascript'>history.back();</script>");
				}

			} catch (Exception e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
			//out.println("<script language='javascript'>alert('create table error object already exist !!!!');</script>");

		}

		return true;
	}

	public boolean upLoadPrc(){

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response =(HttpServletResponse) pageContext.getResponse();
		JspWriter out = pageContext.getOut();

		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

		String mode = request.getParameter("mode");
		String pathIs = request.getParameter("pathIs");

		if(pathIs != null){
			pathIs = Encoding.chCharset(pathIs, m_ServerCharset, m_RQCharset);
		}

		// String path = application.getRealPath("/") + "rpt";
		RepositoryEnv env = RepositoryEnv.getInstance();
		String rootPath = env.repositoryRoot;
		String viewerPath = pageContext.getServletContext().getRealPath("/setup/cab"); //\ucd94\ud6c4 env \uc5d0\uc11c \uac00\uc838\uc62c\uac83

		// String path = application.getRealPath("/") + "rpt";
		Environment tenv = Environment.getInstance();

		int filelimit = tenv.filelimit;
		int iLimit = filelimit * 1024 * 1024 ; //\uc6a9\ub7c9\uc81c\ud55c 50M (fileObj\ub4e4\uc758 \uc804\uccb4 \uc0ac\uc774\uc988\ub97c \uc758\ubbf8)

		if(mode != null && !mode.equals("")){

			File viewerPathDir = new File(viewerPath);
			File[] dirList = viewerPathDir.listFiles();

			if(mode.equals("upviewer")){
				try{
					MultipartParser mp = new MultipartParser(request, iLimit);
					//MultipartParser mp = new MultipartParser(request, iLimit, false, false, m_RQCharset);
				    Part part;
				    String fileName = "";

			      	while ((part = mp.readNextPart()) != null) {
			      		
						FilePart filePart = (FilePart)part;
				       	//fileName = filePart.getFileName();
				       	
						fileName = "RQViewer.cab";
			          	if ( fileName != null ){

		          			fileName = Encoding.chCharset(fileName, m_ServerCharset, m_RQCharset);

		          			String lm_filename = "";
			          		for(int i=0;i<dirList.length;i++){
			          			lm_filename = dirList[i].getName();
								String tmp_ext = lm_filename.substring(lm_filename.lastIndexOf(".")+1, lm_filename.length());
								//out.println(tmp_ext);
								if(tmp_ext.equalsIgnoreCase("OLD")){
									File fileIs = new File(viewerPath +"/"+ dirList[i].getName());
									if(lm_filename.equals(fileName+".OLD")){
										fileIs.delete();
									}
								}
							}
			          		lm_filename = "RQViewer.cab";

							//out.println(dirList[i].getName());
							if(lm_filename.equals(fileName)){

								File sourcefile = new File(viewerPath + "/" + fileName);
								File destfile = new File(viewerPath + "/" + fileName + ".OLD");
								sourcefile.renameTo(destfile);
								//out.println("<script language='javascript'>alert('file already exist !!');history.back();</script>");
							}

							filePart.writeTo(viewerPathDir); //do!!
							//writeTo(viewerPathDir, fileName, filePart); //do!!
			          	}
			        }
					out.println("<script language='javascript'>opener.document.location.reload();self.close();</script>");
				}catch(IOException e){
					RequbeUtil.do_PrintStackTrace(log, e);
				}
				//MultipartRequest multi=new MultipartRequest(request, viewerPath , iLimit, "euc-kr", new DefaultFileRenamePolicy());

				//String modifiedfileName = multi.getFilesystemName("fileobj1");
				//String originalfileName = multi.getOriginalFileName("fileobj1");


				//if(!modifiedfileName.equals(originalfileName)){

				//	File modifiedfileobj = new File(viewerPath+"/"+modifiedfileName);
				//	modifiedfileobj.delete();

				//	//File destfileobj = new File(viewerPath + "/" + "ljg.cab");
				//	//modifiedfileobj.renameTo(destfileobj);
				//	//out.println("<script language='javascript'>alert('file already exist !!');history.back();</script>");
				//}

			}else if(mode.equals("rollback")){

				boolean flag = false;
				String strRbfileName = request.getParameter("strRbfileName");
				String lm_filename = "";
				for(int i=0;i<dirList.length;i++){
					lm_filename = dirList[i].getName();
					if(lm_filename.equals(strRbfileName+".OLD")){
						String tmp_ext = lm_filename.substring(lm_filename.lastIndexOf(".")+1, lm_filename.length());
						if(tmp_ext.equalsIgnoreCase("OLD")){
							flag = true;
						}
					}
				}

				try{

					if(flag == false){
						String msg = (String) rqresource.ht.get("rqhandle.upLoadPrc.rollback.already");
						out.println("<script language='javascript'>alert('" + msg + "');history.back();</script>");
						return false;
					}else{
						if ( strRbfileName != null ){
				        	strRbfileName = Encoding.chCharset(strRbfileName, m_ServerCharset, m_RQCharset);
							File fileIs = new File(viewerPath +"/"+ strRbfileName);
							fileIs.delete();

							File sourcefile = new File(viewerPath +"/"+ strRbfileName + ".OLD");
							File destfile = new File(viewerPath + "/" + strRbfileName);
							sourcefile.renameTo(destfile);
						}
					}
					out.println("<script language='javascript'>alert('rollback execute !!');</script>");
					RequbeUtil.do_SendRedirect(pageContext, "../environment/environment.jsp");
//					if(m_RQCharset.equalsIgnoreCase("UTF-8")){
//						response.sendRedirect("../environment/environmentu8.jsp");
//					}else{
//						response.sendRedirect("../environment/environment.jsp");
//					}
				}catch(IOException e){
					RequbeUtil.do_PrintStackTrace(log, e);
				}

			}

		}else{
			
			Vector vaek = null;
			Hashtable hashTab = null;
			try{
				String lm_uploadPath = rootPath + pathIs;
				String backupfolder = tenv.rqxfile_backup_dir + pathIs;
				File f = new File(lm_uploadPath);
				if(!f.exists()){
					f.mkdirs();
				}
				MultipartParser multiparser = new MultipartParser(request, iLimit, false, false, m_RQCharset);

				DocRepository docRep = new DocRepository();
				vaek = new Vector();
				hashTab = new Hashtable();
				Part part = null;
				String descRes = "";
				String filename = "";
				String path = "";
				File lm_file = null;
				RQFileComp oFileComp = new RQFileComp();
				while ((part = multiparser.readNextPart()) != null) {
					if(part.isParam()) {
						ParamPart paramPart = (ParamPart)part;
						if(part.getName() == null || part.getName().equals("")) continue;
						String name = part.getName();
						if(name.equals("descRes")){
							descRes = paramPart.getStringValue();
						}
					}else if(part.isFile()){
						FilePart filePart = (FilePart)part;
						if(filePart.getFileName() == null || filePart.getFileName().equals("")) continue;
						filename = filePart.getFileName();
						vaek.add(filename);

						path = lm_uploadPath + filename;
						lm_file = new File(path);
						hashTab.put(filename, lm_file); //hashTab(String key, File value);

						// rqxhistory를 사용할경우
						if(tenv.rqxhistory.equalsIgnoreCase("yes")){
							File b_f = new File(backupfolder);
							if(!b_f.exists()){
								b_f.mkdirs();
								// FTP 싱크시 History기능과의 충돌 --> 주석처리.
								//docRep.getBackUpSynchronizer().makeFolders("/");
							}
							filehistoryAct(filename, lm_file, lm_uploadPath, backupfolder);
						}
						filePart.writeTo(lm_file); // write physical area. !

						String servpath = docRep.getName(lm_file);
						docRep.sendDoc(lm_file, servpath);
						oFileComp.setFileComp(filename, filePart, lm_file);
						docRep.setOFileComp(oFileComp); //docRep 내에서 오류가 발생할경우 oFileComp 를 처리하기 위해
					}
				}

				HashMap ohm = new HashMap();
				String[] descResArr = null;
				String[] descResArrPair = null;
				if(descRes != null && !descRes.equals("")){
					descResArr = descRes.split("\\|");
					for(int i = 0 ; i < descResArr.length ; i++){
						descResArrPair = descResArr[i].split("\t");
						ohm.put(descResArrPair[0],  descResArrPair[1] );
					}
				}

				//UserModel UM = (UserModel) pageContext.getAttribute("UM" , PageContext.SESSION_SCOPE);
				HttpSession session = ((HttpServletRequest)pageContext.getRequest()).getSession(true);
				UserModel UM = (UserModel) session.getAttribute("UM");
				String createID = "admin";
				if(UM != null)
					createID = UM.getUserid();

				Enumeration enumer = vaek.elements();
				int res = docRep.addDocLoop(enumer, hashTab, createID, ohm);

				if(res != 0){
					throw new RQFileUploadException(oFileComp, "document upload fail");
				}

				String msg = "";
				if(res == -101){
					msg = (String) rqresource.ht.get("rqhandle.upLoadPrc.docupload.error");
					out.println("<script language='javascript'>alert('" + msg + "');</script>");
				} else if (res == 0){
					msg = (String) rqresource.ht.get("rqhandle.upLoadPrc.docupload.success");
					out.println("<script language='javascript'>alert('"  + msg + "');</script>");
				} else {
					msg = (String) rqresource.ht.get("rqhandle.upLoadPrc.docupload.except");
					out.println("<script language='javascript'>alert('"  + msg + "');</script>");
				}

				// 문서 압축을 사용하며 문서 압축 제한 용량이 document_zip_size 이상일경우만 압축을 한다.
				if(tenv.document_zip_option.equals("yes")){
					int lm_size = Integer.parseInt(tenv.document_zip_size);
					int KB = (int) lm_file.length() / 1024;
					if(KB >= lm_size){
						int lm_level = Integer.parseInt(tenv.document_zip_level);
						RequbeUtil.requbeZip(path, lm_uploadPath, filename, lm_level, log);
					}
				}

				// docRep.commitAll();
				//String fileobjName=multi.getFilesystemName("fileobj");
				//String originalFileobjName = multi.getOriginalFileName("fileobj");

				//byte[] data = JDBCHelper.readFile(file);
				//docRep.addDoc(data, "/testaa.rqd", "admin","");

				out.println("<script language='javascript'>" +
//						"	window.opener.document.location.href = window.opener.document.URL; " +
						"	opener.documentPage.on_reload('4');" +
						"	self.close();" +
						"</script>");
			} catch(Exception e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}finally{
				vaek = null;
				hashTab = null;
			}
		}
		return true;
	}

	/**
	 * 파일 히스토리를 남기기전에 싱크를 맞춰주고 정상일경우 파일 백업을 수행하도록 한다.
	 * @param p_filename
	 * @param p_uploadpath
	 * @param backupfolder
	 */
	public void filehistoryAct(String p_filename, File p_file, String p_uploadpath, String backupfolder){
		RQFileList oRQFileList_before = new RQFileList();
		setMaxCnt_FileList(p_filename, backupfolder, oRQFileList_before);
		filebackup(p_filename, p_file, p_uploadpath, backupfolder, oRQFileList_before);

		RQFileList oRQFileList_after = new RQFileList();
		setMaxCnt_FileList(p_filename, backupfolder, oRQFileList_after);
		fileSync(p_filename, backupfolder, p_file, oRQFileList_after);
	}

	/**
	 * 파일이름과 백업 디렉토리 이름으로 등록된 파일중 최고값을 가져온다.
	 * @param p_filename
	 * @param backupfolder
	 * @return
	 */
	public void setMaxCnt_FileList(String p_filename, String backupfolder, RQFileList oRQFileList){
		int maxNum = 0;
		File f = new File(backupfolder);
		File[] eachfile = f.listFiles();
		if(eachfile == null) return; //없는 폴더일경우 체크
		String lm_filename = "";
		long lm_lastModified = 0;
		String ext = "";
		String filename_without_ext = "";
		int iext = 0;
		for(int i = 0 ; i < eachfile.length ; i++){
			if(!eachfile[i].isFile()) continue;
			lm_filename = eachfile[i].getName();
			lm_lastModified = eachfile[i].lastModified();

			filename_without_ext = lm_filename.substring(0, lm_filename.lastIndexOf("."));
			if(!filename_without_ext.equals(p_filename)) continue;
			ext = lm_filename.substring(lm_filename.lastIndexOf(".") + 1 , lm_filename.length());
			oRQFileList.setFileList(ext, lm_filename, lm_lastModified);
			try{
				iext = Integer.parseInt(ext);
			}catch(NumberFormatException e){ iext = 0;	}
			if(iext > maxNum){
				maxNum = iext;
			}
		}
		oRQFileList.setMaxNum(maxNum);
	}

	/**
	 * step 1. 먼저 환경설정에서 가져온 갯수와 물리적 파일의 갯수를 맞춰주고
	 * 맞지 않을경우 최근에 등록된 순으로 그갯수만큼 만들어준다.
	 * step 2. Repository DB(table)에 등록된 현재 버전과 물리적으로 등록된 파일의 버전을 맞춰준다.
	 * 맞지 않을경우 최근에 등록된 순으로 잘라낸다.
	 * @return
	 */
	public boolean fileSync(String p_filename, String backupfolder, File p_file, RQFileList oRQFileList){
		try{

			//step 1
			Environment tenv = Environment.getInstance();
			int historyCnt = tenv.rqxhistory_count;

			// 6,4,3,2,1 와 같이 이빨이 빠진것들이 있으므로 삭제시 소팅을 하고난뒤
			// 그 순서로 삭제하도록 한다.
			HashMap mapFileList = oRQFileList.getFileList();
			int totalCnt = mapFileList.size();
			if(totalCnt == 0) return true;  // 업로드 뒤에 Sync 작업을 하므로 totalCnt 가 0 경우는 없다.

			int[] fileorder = new int[totalCnt];
			Set keySet = mapFileList.keySet();
			Iterator lm_it = keySet.iterator();

			int x = 0;
			while(lm_it.hasNext()){
				fileorder[x] = Integer.parseInt( (String) lm_it.next() );
				x++;
			}

			//sorting
			int temp = 0;
			for( int i = 0 ; i < fileorder.length ; i++){
				for( int j = i ; j < fileorder.length ; j++){
					if(fileorder[i] < fileorder[j]){
						temp = fileorder[i];
						fileorder[i] = fileorder[j];
						fileorder[j] = temp;
					}
				}
			}

			//System.out.println("+++++++++++++++++++++++++++" + historyCnt);
			File fl = null;
			ArrayList lm_arr = null;
			String lm_Filename = null;
			ArrayList list_for_step2 = new ArrayList();
			for( int i = 0 ; i < fileorder.length ; i++){
				//System.out.println(fileorder[i]);
				//histroyCnt 가 N 일경우 Backup file의 갯수는 rqx 를 제외한 나머지 N-1 만 있어야 한며.
				if(i >= historyCnt - 1){
					//histroyCnt에서 정한 갯수를 제외한 나머지는 모두 삭제
					lm_arr = (ArrayList) mapFileList.get(Integer.toString(fileorder[i]));
					lm_Filename = (String) lm_arr.get(0); //0 : filename , 1 : last modified

					fl = new File(backupfolder +"/"+lm_Filename);
					fl.delete();
				}else{
					//step2를 위해 다시 담는다.(중복삭제(IOException)을 피하기 위해.)
					list_for_step2.add(Integer.toString(fileorder[i]));
				}
			}

			//step 2
			DocRepository docRep = new DocRepository();
			//System.out.println("+++++++++++++++++++++++++" + docRep.getName(lm_file));
			String strRepPath = docRep.getName(p_file);
			int CurrentFileVersion = docRep.getCurrentFileVersion(strRepPath); //파일 업로드는 Repository DB 업데이트 이전에 이루어진다.

			int lm_key = 0;
			for(int k = 0 ;k < list_for_step2.size(); k++){
				lm_key = Integer.parseInt( (String) list_for_step2.get(k) );
				if( lm_key > CurrentFileVersion){
					lm_arr = (ArrayList) mapFileList.get(""+lm_key);
					lm_Filename = (String) lm_arr.get(0); //0 : filename , 1 : last modified
					fl = new File(backupfolder +"/"+lm_Filename);
					fl.delete();
				}
			}

		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
			return false;
		}
		return true;
	}

	/**
	 * 파일을 백업한다.
	 * @param p_filename
	 * @param p_uploadpath
	 * @param backupfolder
	 */
	public void filebackup(String p_filename, File p_file, String p_uploadpath, String backupfolder, RQFileList oRQFileList){
		String lm_filename = "";
		File ori_f = new File(p_uploadpath);
		File[] ori_f_eachfile = ori_f.listFiles();
		DocRepository docRep = null;
		int CurrentFileVersion = 0;
		RQSyncRepository backupsync = null;
		try{
			docRep = new DocRepository();
			String strRepPath = docRep.getName(p_file);
			backupsync = docRep.getBackUpSynchronizer();
			CurrentFileVersion = docRep.getCurrentFileVersion(strRepPath);
		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		//p_filename
		for(int i = 0 ; i < ori_f_eachfile.length ; i++){
			if(!ori_f_eachfile[i].isFile()) continue;
			lm_filename = ori_f_eachfile[i].getName();

			if(lm_filename.equals(p_filename)){
				File sourcefile = new File(p_uploadpath + "/" + lm_filename);
				File destfile   = new File(backupfolder + "/" + lm_filename + "." + CurrentFileVersion); //maxObj[0] == maxCnt
				sourcefile.renameTo(destfile);
				if(backupsync != null)
				{
					backupsync.sendDoc(destfile, lm_filename + "." + CurrentFileVersion);
				}
				if(docRep != null)
				{
				//	docRep.sendDoc(destfile, backupfolder+ "/" + lm_filename + "." + CurrentFileVersion);
				}
			}
		}
	}


	public boolean dbconfigexe(){

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		JspWriter out = pageContext.getOut();

		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

		String lm_change = request.getParameter("change");
		String change = "";
		change = Encoding.chCharset(lm_change, m_ServerCharset, m_RQCharset);
		//		out.println(change);

		//		ex) docRep.setDSList("/RQReport2.rqd",str);
		//		ex)
		//		RQReport1.rqd /RQReport1.rqd 0 jdbc/EReqube
		//		RQReport2.rqd /RQReport2.rqd 0 jdbc/Reqube
		//		RQReport2.rqd /RQReport2.rqd 1 jdbc/EMP

		HashMap hm = new HashMap();
		DocRepository docRep = null;
		try {
			docRep = new DocRepository();
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}

		String[] strElement = change.split("\\|");

		int cnt = 0;
		for(int i=0; i < strElement.length; i++){

			String[] strSubElement = strElement[i].split("\t");
		   	String pivot = strSubElement[1];
		   	String str = "";

		   	ArrayList values = new ArrayList();
		   	ArrayList arr = null;

		   	for(int j=0 ; j < strElement.length ; j++){
		   		String[] lm = strElement[j].split("\t");
		   		if(pivot.equals(lm[1])){
		   			str = pivot;
		   			arr = new ArrayList();
		   			arr.add((String)lm[2]);
		   			arr.add((String)lm[3]);
		   			values.add(arr);
		   		}
		   	}

		   	if(cnt == 0){
		   		hm.put(str,values);
		   	}else{

		   	   	Set set = hm.keySet();
		   		Object[] keyset = set.toArray();

		   		for(int s=0 ; s < keyset.length; s++){
			   		if(!str.equals((String)keyset[s])){
						hm.put(str,values);
			   		}
		   		}
		   	}
		   	cnt++;
		}

		Set set = hm.keySet();
		Object[] keyset = set.toArray();

		for(int i=0; i<keyset.length;i++){
			String docPath = (String)keyset[i];
			//out.println("key : "+key+ " hm.get : "+hm.get(key) + "<br>");

			ArrayList la = (ArrayList) hm.get(docPath);
			Object[] oa = la.toArray();

			String[] dsNames = new String[oa.length];
			int[] orders = new int[oa.length];

			for(int j=0; j<oa.length; j++){
				ArrayList lma = (ArrayList) oa[j];

				dsNames[j] = (String) lma.get(1);
				orders[j] = Integer.parseInt( (String)  lma.get(0) );
			}

			//out.println(key);
			//out.println(dsNames);
			//out.println(orders);
			docRep.setDSList(docPath, dsNames, orders);
		}

		try {
//			out.println("<script language='javascript'>" +
//							"	alert('\ubcc0\uacbd \ub418\uc5c8\uc2b5\ub2c8\ub2e4.');" +
//							"	window.opener.document.location.href = '../document/document.jsp';" +
//							"	self.close();		" +
//							"</script>");
			String msg = (String) rqresource.ht.get("rqhandle.dbconfigexe.dosuccess");
			out.println("<script language='javascript'>" +
							"	alert('" + msg + "');" +
							"	opener.documentPage.on_reload('4');" +
							"	self.close();" +
							"</script>");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		//docRep.setDSList(strElement[1],strElement[2]);
		return true;
	}

	public boolean viewerStat(){

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		JspWriter out = pageContext.getOut();

		String viewerFileName = "RQViewer.cab";
		String strViewerInstall = request.getParameter("strViewerInstall");
		//String viewerPath = pageContext.getServletContext().getRealPath("/setup/cab");
		//String viewerPath = pageContext.getServletContext().getRealPath("/rqreport") + "/setup/cab";
		
		String basePath = pageContext.getServletContext().getRealPath("/");
		String vPath = request.getRequestURI();
		
		if(vPath.startsWith("/rqreport") && basePath.indexOf("rqreport") != -1){
			//rqreport로 시작하는 context를 가지고 있을경우.
			//정상적인 설치
			vPath = "";
		} else {
			//다른 컨텍스에 위치해있을경우.
			vPath = vPath.substring(0, vPath.indexOf("/rqreport") + 9);
		}
		
		String lm_viewerPath = "";
		lm_viewerPath = vPath + "/setup/cab";
		String viewerPath = pageContext.getServletContext().getRealPath(lm_viewerPath);

		File viewerPathDir = new File(viewerPath);
		File[] dirList = viewerPathDir.listFiles();

		// ResourceBundle
		//ResourceBundle rb = new ResourceBundle();

		try{
			String lm_filename = "";
			for(int i=0;i<dirList.length;i++){
				lm_filename = dirList[i].getName();
				String tmp_ext =  lm_filename.substring(lm_filename.lastIndexOf(".")+1, lm_filename.length());
				if( tmp_ext.equalsIgnoreCase("cab") || lm_filename.equals(viewerFileName+".OLD")){
					out.print(lm_filename+ " / ");

					java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyMMdd HH:mm:ss");
					java.util.Date lm_date = new java.util.Date(dirList[i].lastModified());

					out.print("lastModified : " +dateformat.format(lm_date)+ " / ");

					out.print("size : " +dirList[i].length()+ " / ");
					out.print("<br>");
				}
			}

		} catch(IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}

	/**
	 * Repository DB 에 수정날짜와 버전을 업데이트 하고 파일 롤백한다.
	 * 등록된 현재 버전은 삭제 되며, 이전 파일로 롤백되며
	 * 롤백된 파일의 상위버전은 모두 삭제된다.
	 * 삭제가 된뒤에  해준다.
	 * @return
	 */
	public boolean rollbackfile(){

		Environment env = Environment.getInstance();
		RepositoryEnv renv = RepositoryEnv.getInstance();
		if(!env.rqxhistory.equals("yes")) return false;
		String lm_backup_dir = env.rqxfile_backup_dir;
		String lm_repositoryRoot = renv.repositoryRoot;

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String lm_pathIs       = request.getParameter("pathIs");
		lm_pathIs = lm_pathIs != null ? Encoding.chCharset(lm_pathIs, m_ServerCharset, m_RQCharset) : "" ;
		String lm_filename     = request.getParameter("filename");
		lm_filename = lm_filename != null ? Encoding.chCharset(lm_filename, m_ServerCharset, m_RQCharset) : "" ;

		String lm_version      = request.getParameter("version");
		String lm_modiday      = request.getParameter("modiday");

		String lm_rep_fullpath = lm_pathIs + lm_filename;

		try{
			DocRepository lm_oDocRep = new DocRepository();
			//Repository Table Update
			//2007-11-20 18:25
			String[] lm_day = lm_modiday.split(" ");
			String year_month_day = lm_day[0];
			String hour_min = lm_day[1];

			String[] lm_A = year_month_day.split("-");
			String[] lm_B = hour_min.split(":");

			Calendar cal = Calendar.getInstance();
			//cal.set(year, month, date, hour, minute);
			cal.set(Integer.parseInt(lm_A[0]),
					Integer.parseInt(lm_A[1]) - 1,
					Integer.parseInt(lm_A[2]),
					Integer.parseInt(lm_B[0]),
					Integer.parseInt(lm_B[1])   );

			lm_oDocRep.setCurrentFileVersion(lm_pathIs+lm_filename, lm_version, cal );

			//기존 파일 삭제
			File lm_rollbackfile = lm_oDocRep.getFile(lm_rep_fullpath);
			lm_rollbackfile.delete();

			//선택한 파일 복원
			File sourcefile = new File(lm_backup_dir + lm_pathIs + "/" + lm_filename + "." + lm_version);
			File destfile   = new File(lm_repositoryRoot + lm_pathIs + "/" + lm_filename);
			sourcefile.renameTo(destfile);

			//선택한 파일보다 상위버전들은 모두 삭제
			int lm_tmp_version = 0;
			File lm_dir = new File(lm_backup_dir + lm_pathIs);
			File[] lm_files = lm_dir.listFiles();
			String lm_files_name = "";
			String lm_files_name_without_ext = "";
			for(int i = 0 ; i < lm_files.length ; i++ ){
				if(!lm_files[i].isFile()) continue;
				lm_files_name = lm_files[i].getName();
				try{
					lm_files_name_without_ext = lm_files_name.substring(0, lm_files_name.lastIndexOf("."));
					if(lm_filename.equals(lm_files_name_without_ext)){
						lm_tmp_version = Integer.parseInt( lm_files_name.substring(lm_files_name.lastIndexOf(".")+1, lm_files_name.length()) );
						if(lm_tmp_version > Integer.parseInt(lm_version)){
							lm_files[i].delete(); // 상위버전 delete
						}
					}

				}catch(NumberFormatException e){}
			}

		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}

		//HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		JspWriter out = pageContext.getOut();

		try {
			out.println("<script language='javascript'>" +
						"	document.location.href='../document/document.jsp?strPathIs="+lm_pathIs+"'"+
						"</script>");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}

	public void multiupdoc(String createID, String jndiname, String targetFolderRel, String updocpath){

		RepositoryEnv renv = RepositoryEnv.getInstance();
		String rootpath = renv.repositoryRoot;

		try {

			DocRepository rep = new DocRepository();

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
						log.debug(e);
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
					rep.addDocLoop(lm_objnames, hash, createID, ohm); //do !

					//JNDI mapping
					String docPath = rep.getName(targetfile);
					String[] dsNames = new String[1];
					dsNames[0] = jndiname;
					rep.setDSList(docPath, dsNames);

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
					multiupdoc(createID, jndiname, nowFoldernameRel, eachfile.getAbsolutePath());
				}
			}

		}catch(Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}

	}
	
	public boolean crtDocStattable(){
		RQTableCreator tableCreator = new RQTableCreator();
		Environment env = Environment.getInstance();
		RepositoryEnv renv = RepositoryEnv.getInstance();
		JspWriter out = pageContext.getOut();
		
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		int errcnt = 0;
		String qry = "CREATE TABLE [name]" +
				"(" +
				"  RUN_TIME            CHAR(14 BYTE)             NOT NULL," +
				"  FILE_NM             VARCHAR2(255 BYTE)        NOT NULL," +
				"  RUNCNT              NUMBER," +
				"  SERVERTIME_ACCUMUL  NUMBER," +
				"  SERVERTIME_AVE      NUMBER," +
				"  TOTALTIME_ACCUMUL   NUMBER," +
				"  TOTALTIME_AVE       NUMBER," +
				"  MAXTIME             NUMBER," +
				"  MAXTIME_RUNVAR      VARCHAR2(4000 BYTE)," +
				"  MINTIME             NUMBER," +
				"  MINTIME_RUNVAR      VARCHAR2(4000 BYTE)," +
				"  ERROR_CNT           NUMBER" +
				")";
		/*
		String qry = "CREATE TABLE [name]" +
		"(" +
		"  RUN_TIME            CHAR(14)             NOT NULL," +
		"  FILE_NM             VARCHAR(255)        NOT NULL," +
		"  RUNCNT              INTEGER," +
		"  SERVERTIME_ACCUMUL  INTEGER," +
		"  SERVERTIME_AVE      INTEGER," +
		"  TOTALTIME_ACCUMUL   INTEGER," +
		"  TOTALTIME_AVE       INTEGER," +
		"  MAXTIME             INTEGER," +
		"  MAXTIME_RUNVAR      VARCHAR(255)," +
		"  MINTIME             NUMBER," +
		"  MINTIME_RUNVAR      VARCHAR(255)," +
		"  ERROR_CNT           INTEGER" +
		")";
		*/
		
		String msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttableDocstat.error.docstattable");
		try{
			if(tableCreator.makeTable(renv.doc_DocStatTableName, qry) == -1){
				out.println("<script language='javascript'>alert('" + msg + "');</script>");
				errcnt++;
			}
			if(errcnt > 0){
				msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttableDocstat.fail");
				out.println("<script language='javascript'>alert('" + msg + "');</script>");
				out.println("<script language='javascript'>history.back();</script>");
			}else{
				msg = (String)rqresource.ht.get("rqhandle.delncrttable.crttableDocstat.success");
				out.println("<script language='javascript'>alert('" + msg + "');</script>");
				out.println("<script language='javascript'>history.back();</script>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
}

