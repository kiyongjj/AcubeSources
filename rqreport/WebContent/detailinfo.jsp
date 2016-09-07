<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQUser.tld" prefix="rquser" %>
<%
	Encoding enc = new Encoding();
	String lm_serverCharset = enc.getServerCharset();
	String lm_RQCharset = enc.getRQCharset();
	
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	String lm_param = request.getParameter("param");
	String param = new String(lm_param.getBytes(lm_serverCharset), lm_RQCharset);
	
	String sql = "select ";
	String column = "";
	String table = "";
	String where = "";
	String doc = "";
	String dbnum = "";
	int count = 0;
	String name, value;
	
	String [] array = param.split("\\|");
	
	for(int i=0; i<array.length; i++)
	{
		System.out.println(array[i]);
		
		if ("detail_column".equals(array[i]))
		{
			i++;
			column = array[i];
		}
		else if ("detail_table".equals(array[i]))
		{
			i++;
			table = array[i];
		}
		else if ("detail_doc".equals(array[i]))
		{
			i++;
			doc = array[i];
		}
		else if ("detail_dbnum".equals(array[i]))
		{
			i++;
			dbnum = array[i];
		}
		else
		{
			name = array[i];
			i++;
			value = array[i];
			if (value.length() == 0)
				continue;
			if (count > 0)
				where += " and ";
			where += name;
			where += "=\'";
			where += value;
			where += "\'";	
			count++;		
		}
	}
	
	sql += column;
	sql += " from ";
	sql += table;
	if (where.length() > 0)
	{
		sql += " where ";
		sql += where;
	}
	
	String url = "./RQDataset.jsp?action=getRs";
	url += "&sql=";
	url += sql;
	url += "&doc=";
	url += doc;
	pageContext.forward(url); 
		
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html;charset=utf-8">	
<TITLE>REQUBE REPORT</TITLE>	
<SCRIPT language="javaScript">
<!--
function OnRun()
{
/*	var strParam = new String("<%=param%>");//alert("<%=param%>");
	var array = strParam.split('|');
	
	var sql = "select ";
	var column = "";
	var table = "";
	var where = "";
	var doc = "";
	var dbnum = 0;
	var count = 0;
	
	for(var i=0; i<array.length; i++)
	{
		if (array[i] == "detail_column")
		{
			i++;
			column = array[i];
		}
		else if (array[i] == "detail_table")
		{
			i++;
			table = array[i];
		}
		else if (array[i] == "detail_doc")
		{
			i++;
			doc = array[i];
		}
		else if (array[i] == "detail_dbnum")
		{
			i++;
			dbnum = array[i];
		}
		else
		{
			if (count > 0)
				where += " and ";
			where += array[i];
			where += "=\'";
			i++;
			where += array[i];
			where += "\'";	
			count++;		
		}
	}
	
	sql += column;
	sql += " from ";
	sql += table;
	sql += " where ";
	sql += where;
*/
	alert("<%=sql%>");
}
-->
</SCRIPT>
</HEAD>
<BODY topmargin="0" leftmargin="0" onload="OnRun()">
<%=param%>
</BODY>
</HTML>