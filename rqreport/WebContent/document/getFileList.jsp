<%@ page contentType = "text/plain; charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sds.rqreport.*"%>
<%@ page import="com.sds.rqreport.service.web.*"%>
<%
request.setCharacterEncoding("UTF-8");
String pathis = request.getParameter("pathis");
String filename = request.getParameter("filename");
//String pathis = "/ljg_test/";
//String filename = "RQReport1.rqx";

Environment env = Environment.getInstance();
String backupfolder = env.rqxfile_backup_dir + pathis;
RQHandle oRQHandle = new RQHandle();
RQFileList oRQFileList = new RQFileList();
oRQHandle.setMaxCnt_FileList(filename, backupfolder, oRQFileList);
oRQFileList.setSortingInt();

int cnt = oRQFileList.getFileList().size();

out.print(cnt + "|");
//System.out.print(cnt + "|");

ArrayList arrlist = oRQFileList.getArray();

ArrayList lm_arr = null;
String nowKey = "";
String strModifiedday = "";

for(int i = 0 ; i < cnt ; i++){

	lm_arr = (ArrayList)arrlist.get(i);
	nowKey = (String) lm_arr.get(0);
	out.print(nowKey);
	//System.out.print(nowKey);
	
	out.print(",");
	//System.out.print(",");
	
	strModifiedday = (String) lm_arr.get(1);
	out.print(strModifiedday);
	//System.out.print(strModifiedday);
	
	if(i < cnt-1){
		out.print("\t");
		//System.out.print("\t");
	}
}
%>
