<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<jsp:include flush="true" page="RQtest.jsp"/>

<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
</head>
<body>
<form name="docnrunvar" method="post" action="RQDataset.jsp">
<table>
<tr>
	<td width="120">DB Method</td>
	<td>
		JNDI		
	</td>
</tr>
<tr>
	<td>Cache-Manager</td>
	<td>
	<select name="cacheman">
		<option>true</option>
		<option selected="selected">false</option>		
	</select>
	(default : 20)
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
	<td></td>
	<td style="font-size: 12px">ex) jdbc/Reqube</td>
</tr>
<tr>
	<td>strDBInfo</td>
	<td>
		<textarea name="strDBInfo" rows="7" cols="40"><Database><DBInfo DBIdx="0" IFType="1" DBID="jdbc:oracle:thin:@70.7.120.214:1521:WorldAV - JDBC1" Driver="oracle.jdbc.driver.OracleDriver" ConnectionStr="jdbc:oracle:thin:@70.7.120.214:1521:WorldAV" UserID="easybase" Password="rqdev1"/></Database></textarea>
	</td>
</tr>
<tr>
	<td>strXml</td>
	<td>
		<textarea name="strXml" rows="10" cols="40"><SQL><SQLStmt DBIdx="0" SQLIdx="0" IsEditSQL="0" QryType="0"><SQLData><![CDATA[SELECT EASYBASE.사원.부서명 FROM EASYBASE.사원]]></SQLData><SQLStmt DBIdx="0" SQLIdx="1" IsEditSQL="0" QryType="0"><SQLData><![CDATA[SELECT EASYBASE.사원.성명, EASYBASE.사원.주민번호 FROM EASYBASE.사원 where (EASYBASE.사원.부서명=?)]]></SQLData><BindSrc BindQryIdx="0" BindColIdx="0" BindColName="EASYBASE.사원.부서명"/></SQLStmt></SQLStmt></SQL></textarea>
		<!-- 
			<input name="strXml" type="text" size="80" value='<SQL><SQLStmt DBIdx="0" SQLIdx="0" IsEditSQL="0" QryType="0"><SQLData><![CDATA[SELECT 사원.성명, 사원.사진 from 사원 where rownum < 3]]></SQLData></SQLStmt></SQL>' />
		 -->
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

            