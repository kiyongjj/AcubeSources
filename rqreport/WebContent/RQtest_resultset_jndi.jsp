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
		<option selected="selected">true</option>
		<option>false</option>		
	</select>
	(default : 20)
	</td>
</tr>

<tr>
	<td>action</td>
	<td><input name="action" type="text" value="getRs"  size="35" readonly/></td>
</tr>

<tr>
	<td>doc</td>
		<td><input name="doc" type="text" value="/a_younggi/RQCrossTab1_상세보기_URL.rqx" size="35"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) /a_younggi/RQCrossTab1_상세보기_URL.rqx</td>
</tr>

<tr>
	<td>sql</td>
	<td><input name="sql" type="text" size="35" value="SELECT 사원.주민번호, 주문.주문번호, 주문상세.제품번호 FROM 사원, 주문, 주문상세 WHERE  사원.주민번호 = 주문.사원주민번호 AND 주문.주문번호 = 주문상세.주문번호"/></td>
</tr>
<tr>
	<td></td>
	<td style="font-size: 12px">ex) SELECT 사원.주민번호, 주문.주문번호, 주문상세.제품번호 FROM 사원, 주문, 주문상세 WHERE  사원.주민번호 = 주문.사원주민번호 AND 주문.주문번호 = 주문상세.주문번호</td>
</tr>
</table>
<input type="submit">	
</body>
</html>

            