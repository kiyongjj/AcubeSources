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
	<td>DB Method</td>
	<td>
		DIRECT CONNECT
	</td>
</tr>
<tr>
	<td>action</td>
	<td><input name="action" type="text" value="getRs"  size="35" readonly/></td>
</tr>

<tr>
	<td>driver</td>
	<td><input name="driver" type="text" value="oracle.jdbc.driver.OracleDriver" size="35"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) oracle.jdbc.driver.OracleDriver</td>
</tr>
<tr>
	<td>conn</td>
	<td><input name="conn" type="text" value="jdbc:oracle:thin:@70.7.101.214:1521:WorldAV" size="35"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) jdbc:oracle:thin:@70.7.101.214:1521:WorldAV</td>
</tr>
<tr>
	<td>sid</td>
	<td><input name="sid" type="text" value="easybase" size="35"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) easybase</td>
</tr>
<tr>
	<td>spass</td>
	<td><input name="spass" type="text" value="rqdev1" size="35"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) rqdev1</td>
</tr>
<tr>
	<td>sql</td>
	<td><input name="sql" type="text" size="35" value="SELECT 사원.주민번호, 주문.주문번호, 주문상세.제품번호 FROM 사원, 주문, 주문상세 WHERE  사원.주민번호 = 주문.사원주민번호 AND 주문.주문번호 = 주문상세.주문번호"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) SELECT 사원.주민번호, 주문.주문번호, 주문상세.제품번호 FROM 사원, 주문, 주문상세 WHERE  사원.주민번호 = 주문.사원주민번호 AND 주문.주문번호 = 주문상세.주문번호</td>
</tr>
<tr>
	<td>stmtidx</td>
	<td><input name="stmtidx" type="text" size="35" value="1"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) 1</td>
</tr>

</table>
<input type="submit">	
</body>
</html>

            