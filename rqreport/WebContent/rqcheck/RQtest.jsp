<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<SCRIPT LANGUAGE="JAVASCRIPT"> 
<!-- 

function sel_item(sel)
{ 
	if (sel.options[1].selected) 
 		location.href = "/rqreport/rqcheck/RQtest.jsp ";
	else if(sel.options[2].selected)
		location.href = "/rqreport/rqcheck/jdbc_version.jsp";
	else if(sel.options[3].selected)
		location.href = "/rqreport/rqcheck/JNDITest.jsp";
	else if(sel.options[4].selected)
		location.href = "/rqreport/rqcheck/RQtest_conv.jsp";
	else if(sel.options[5].selected)
		location.href = "/rqreport/rqcheck/RQtest_resultset_jndi_xml.jsp";
	else if(sel.options[6].selected)
		location.href = "/rqreport/rqcheck/session/sessionObj.jsp";
	else if(sel.options[7].selected)
		location.href = "/rqreport/rqcheck/encoding/ansi/ansi1.jsp";
	else if(sel.options[8].selected)
		location.href = "/rqreport/rqcheck/encoding/utf8/utf81.jsp";
}

//-->
</SCRIPT>

<form name="frm1">
	<select name="myselect" onchange="sel_item(frm1.myselect)">
		<option selected="selected">------------------
		<option>/rqreport/rqcheck/RQtest.jsp</option>
		<option>/rqreport/rqcheck/jdbc_version.jsp</option>
		<option>/rqreport/rqcheck/JNDITest.jsp</option>
		<option>/rqreport/rqcheck/RQtest_conv.jsp</option>
		<option>/rqreport/rqcheck/RQtest_resultset_jndi_xml.jsp</option>
		<option>/rqreport/rqcheck/session/sessionObj.jsp</option>
		<option>/rqreport/rqcheck/encoding/ansi.jsp</option>
		<option>/rqreport/rqcheck/encoding/utf8.jsp</option>
	</select>
</form>
