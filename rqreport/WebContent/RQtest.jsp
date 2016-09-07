<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<SCRIPT LANGUAGE="JAVASCRIPT"> 
<!-- 

function sel_item(sel)
{ 
	if (sel.options[1].selected) 
 		location.href = "RQtest.jsp ";
	else if(sel.options[2].selected)
		location.href = "jdbc_version.jsp";
	else if(sel.options[3].selected)
		location.href = "JNDITest.jsp";
	else if(sel.options[4].selected)
		location.href = "RQtest_conv.jsp";
	else if(sel.options[5].selected)
		location.href = "RQtest_resultset_jndi_xml.jsp";
	else if(sel.options[6].selected)
		location.href = "RQtest_resultset_jndi.jsp";
	else if(sel.options[7].selected)
		location.href = "RQtest_resultset.jsp";
	else if(sel.options[8].selected)
		location.href = "RQtest_resultset_SQLExec.jsp";
}

//-->
</SCRIPT>

<form name="frm1">
	<select name="myselect" onchange="sel_item(frm1.myselect)">
		<option selected="selected">------------------
		<option>RQtest.jsp</option>
		<option>jdbc_version.jsp</option>
		<option>JNDITest.jsp</option>
		<option>RQtest_conv.jsp</option>
		<option>RQtest_resultset_jndi_xml.jsp</option>
		<option>RQtest_resultset_jndi.jsp</option>
		<option>RQtest_resultset.jsp</option>
		<option>RQtest_resultset_SQLExec.jsp</option>
	</select>
</form>
