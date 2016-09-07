<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.scheduler.*" %>
<%@page import="com.sds.rqreport.Environment"%>
<%
/**
 * 서버API함수폴더는 서버단에서 호출하는 함수들을 모아둔 곳이며
 * 간단한 예를 통해 호출방법을 볼수 있다.
 * 서버단의 함수를 밖으로 도출시킨만큼 
 * 사용전에 반드시 보안상 혹은 세션에 관한 처리가 확실하게 보장되어야 한다.
 *
 * 또한 해당 API는 사용 예시만 보여줄뿐
 * 그외의 작업 (보안, 세션)에 대한 추가적인 일을 수행하지 않는다.  
 * 
 * ※ 반드시 필요한 경우에만 사용할것.
 **/

/**
 * RQExportThread 를 이용하여 NT 데몬으로 하여금 서버단에서 
 * pdf, xls ... 의 엑스포트를 하게 한다.
 */
String doc =request.getParameter("doc");
String runvar = request.getParameter("runvar");
Environment g_env = Environment.getInstance();

/////pdf, hwp, xls, gul export for NT Server //////////////////////////////////////////////
/////must be need LocalSchedule Daemon process ////////////////////////////////////////////
// URL pattern : (add svrexport=pdf)
String svrexport = request.getParameter("svrexport") == null ? "" : request.getParameter("svrexport");
if(!svrexport.equals("")){
	if(svrexport.equals("pdf") || svrexport.equals("xls") || 
	   svrexport.equals("gul") || svrexport.equals("hwp") || svrexport.equals("rtf") )
	{
		RQExportThread rqrun = new RQExportThread(doc, runvar, svrexport, "", "", Environment.getInstance().ntcliDmloc);
		//RQExportThread rqrun = new RQExportThread(doc, runvar, svrexport, "ExportDocName.pdf", "");
		//RQExportThread rqrun = new RQExportThread(doc, runvar, svrexport, "", "yyyyMMdd");
		rqrun.start();
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
%>