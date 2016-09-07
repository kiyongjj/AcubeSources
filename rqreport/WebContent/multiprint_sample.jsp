<%@ page contentType="text/html;charset=euc-kr"%>
<HTML>
<head>
<title>보고서 일괄출력</title>
<SCRIPT LANGUAGE="JavaScript"> 
function make_innerHTML() {

	document.all.innertxt.innerHTML = "<input type='hidden' name='doc' value='/multiprint/RQReport1.rqx'> ";
	document.all.innertxt.innerHTML += "<input type='hidden' name='runvar' value=''> ";
	document.all.innertxt.innerHTML += "<input type='hidden' name='doc' value='/multiprint/RQCrossTab1.rqx'> ";
	document.all.innertxt.innerHTML += "<input type='hidden' name='runvar' value='test|test|test|test'> ";
	
}

function winPrint() {
	make_innerHTML();
	document.winFile.print_flag.value="true";
	document.winFile.excel_flag.value="false";
	document.winFile.target="prtframe";
	document.winFile.action="/rqreport/rqviewer_bulkprint.jsp";
	document.winFile.submit();
}


function winExcel() {
	make_innerHTML();
	document.winFile.excel_flag.value="true";
	document.winFile.print_flag.value="false";
	document.winFile.target="prtframe";
	document.winFile.action="/rqreport/rqviewer_bulkprint.jsp";
	document.winFile.submit();
	//alert("완료");
}

function winSaveExcel() {
	make_innerHTML();
	// false or show(=true) or save
	document.winFile.excel_flag.value="save"; 
	document.winFile.print_flag.value="false";
	document.winFile.target="prtframe";
	document.winFile.action="/rqreport/rqviewer_bulkprint.jsp";
	document.winFile.submit();
	//alert("완료");
}

function winPDF(action) {
	make_innerHTML();
	// false or show(=true) or save
	document.winFile.excel_flag.value="false"; 
	document.winFile.print_flag.value="false";
	document.winFile.pdf_flag.value=action;
	document.winFile.target="prtframe";
	document.winFile.action="/rqreport/rqviewer_bulkprint.jsp";
	document.winFile.submit();
	//alert("완료");
}

</script>
<body>
<form name="winFile" method="post">
<table border="1" style="font-family:verdana;font-size:12px;" width="300" cellpadding="5" cellspacing="0" bgcolor="#859CB4" bordercolor="#5D6E80" bordercolordark="#9BB7D4" bordercolorlight="#5D6E80">
<tr>
	<td>프린트출력</td>
	<td>
		<a href="javascript:winPrint()" >프린트</a>
	</td>
</tr>
<tr>
	<td>엑셀출력</td>
	<td>
		<a href="javascript:winExcel()" >엑셀출력</a>
	</td>
</tr>
<tr>
	<td>엑셀저장</td>
	<td>
		<a href="javascript:winSaveExcel()" >엑셀저장</a>
	</td>
</tr>
<tr>
	<td>PDF출력</td>
	<td>
		<a href="javascript:winPDF('show')" >PDF출력</a>
	</td>
</tr>
<tr>
	<td>PDF저장</td>
	<td>
		<a href="javascript:winPDF('save')" >PDF저장</a>
	</td>
</tr>
<tr>
	<td>저장 경로</td>
	<td><input type="text" name="saveDir" value="D:\tmp" size="25" style="font-family:verdana;font-size:12px;"></td>
</tr>
<tr>
	<td id=innertxt></td>
</tr>
</table>
<input type=hidden name=print_flag />
<input type=hidden name=excel_flag />		
<input type=hidden name=pdf_flag />
</form>
<iframe src= "" name= "prtframe" width= "1" height= "0" scrolling = "no" frameborder="0"></iframe>
</body>
</html>