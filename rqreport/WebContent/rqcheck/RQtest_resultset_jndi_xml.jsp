<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<jsp:include flush="true" page="RQtest.jsp"/>

<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
</head>
<body>
<form name="docnrunvar" method="post" action="../RQDataset.jsp">
<table>
<tr>
	<td width="120">DB Method</td>
	<td>
		JNDI		
	</td>
</tr>
<tr>
	<td>action</td>
	<td>
		<!-- 
		<input name="action" type="text" value="getRs"  size="35" readonly/>
		-->
	 	<select name="action">
			<option  selected="selected">getRs</option>
			<option>getRsDB</option>
			<option>getSQLExec</option>		
		</select>
	 </td>
</tr>

<tr>
	<td>JNDI name</td>
	<td><input name="jndiname" type="text" value="jdbc/Reqube" size="35"/></td>
</tr>
<tr>
	<td>strXml<br>(decoded str<br>or encoded str)</td>
	<td>
		<textarea name="strXml" rows="10" cols="40">
<SQL>
	<SQLStmt DBIdx="0" SQLIdx="0" NameIdx="1" QryType="0" UIType="1" IsEditSQL="0" Distinct="0" Quotation="0">
		<SQLData><![CDATA[SELECT EASYBASE.사원V.부서명, EASYBASE.사원V.성명 
FROM EASYBASE.사원V]]></SQLData>
		<ConditionInfo/>
		<HavingInfo/>
	</SQLStmt>
</SQL>
		</textarea>
		
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td style="font-size: 12px">ex) 
		<xmp>
		</xmp>
	</td>
</tr>
</table>
<input type="submit">	
</form>
</body>
</html>

            