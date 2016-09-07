<%@ page contentType="text/html;charset=EUC-KR"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title> REQUBE REPORT </title>
<script type="text/javascript">
function submitFn(){
	document.rqform.action = "rqviewer_pdfmerge.jsp";
	document.rqform.submit();
}
</script>
</head>
<body>
<form name="rqform" method="post">
Merge할 문서1 <br>
doc: <input type="text" name="doc" value="/merge/RQReport1.rqx"/> , 
runvar: <input type="text" name="runvar" value=""/>

<br><br>

Merge할 문서2 <br>
doc: <input type="text" name="doc" value="/merge/RQReport2.rqx"/> ,
runvar: <input type="text" name="runvar" value="key1|value1|key2|value2"/>

<br><br/>
-------------------------------------------------------------------------------------------
<br><br>

Merge가 된뒤에 문서 이름 : <input type="text" name="mergefile" value="C:/mergefile.pdf"/>

<br/><br/><br/>

<input type="button" name="submitbtn" value="submit" onclick="submitFn();"/>
<br><br>
</form>
</body>
</html>
