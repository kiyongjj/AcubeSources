<%@ page language="java" pageEncoding="UTF-8"%><%@ page import="java.io.*" %><%@ page import="com.sds.rqreport.util.*" %><%@ page import="com.sds.rqreport.repository.*" %><%@ page import="com.sds.rqreport.Environment" %><%
	response.setHeader("RQREPORT","getreport.jsp");
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	
	RepositoryEnv rep_env = RepositoryEnv.getInstance(); 
	String getType = request.getParameter("getType") == null ? "" : request.getParameter("getType");
	//request.setCharacterEncoding("EUC-KR");
	
	// For Tomcat 5.x to Convert String
	String doc = request.getParameter("doc");
	
	String docname = null;
	if(doc != null)
		docname = new String(doc.getBytes(lm_serverCharset), "UTF-8");
	String encstr = request.getParameter("enc");
	if(encstr != null && encstr.length() > 0)
	{
		String qry = Decrypter.decrypt26(encstr);
		QueryStrAnalyzer qryAnalyzer = new QueryStrAnalyzer(qry);
		docname = qryAnalyzer.getParameter("doc");
	}
	//PrintStream out2 = new PrintStream(response.getOutputStream());
	//response.setContentType("application");
	
	File f = null;
	if(getType.equals("rqv")){
		f = new File(rep_env.repositoryRoot + doc);			
	}else{
		DocRepository docRep = new DocRepository();
		int lmID = docRep.getDocID(docname);
		if( lmID != -1 && lmID != 0){
			f = docRep.getFile(docname);
		}else{
			f = null;
		}
	}
	
	// if compression option "yes"	
	Environment tenv = Environment.getInstance();
	if(tenv.document_zip_option.equals("yes")){
		String rqxfullpath = f.getAbsolutePath();
		String zippath = rqxfullpath.substring(0, rqxfullpath.lastIndexOf("."));
		String zipfullpath = zippath + ".zip";
		//Tests whether the file is a normal file.
		File lm_f = new File(zipfullpath);
		if(lm_f.isFile()){
			f = new File(zipfullpath);
		}
	}
	
	if(f != null && f.exists() && f.isFile())
	{
		byte[] fileData;
		fileData = JDBCHelper.readFile(f);
		
		PrintStream out2 = new PrintStream(response.getOutputStream());
		response.setContentType("Application/x-rqd");
		response.setContentLength(fileData.length);
		out2.write(fileData, 0, fileData.length);
		//out.print((new String(fileData,"UTF-8")).trim()); 
		return;
	} 
%>