<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.sds.rqreport.Environment" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Enumeration enum1 = request.getParameterNames();
Map paramMap = request.getParameterMap();
Environment tenv = Environment.getInstance();
tenv.load();
tenv.getPropertyLoader().setProperty(paramMap); 

Iterator it = paramMap.keySet().iterator();
String key = null;
String[] value = null;
while(it.hasNext())
{
    key = (String)it.next();
    value = (String[])paramMap.get(key);
    for(int i=0; i< value.length; i++)
    {
        System.out.println(key + " : " + value[i]);
    }
}

//while(enum.hasMoreElements())
//{
//	String name = (String)enum.nextElement();
//	String value = request.getParameter(name);
//	System.out.println(name + "=" + value);
//}

out.println("	<script language='javascript'> 				");
out.println("		alert('반영 되었습니다.');        				");
out.println("		document.location.href='rqenv.jsp';		");
out.println("	</script>												");

%>

