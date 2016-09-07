<%
    String  sEmpNo    = request.getParameter("sEmpNo");
    String  sPaymDAY  = request.getParameter("sPaymDAY"); 
    String  sDiv      = request.getParameter("sDiv"); 
%>    
<html>
<head>
<title> ▒▒  급여 메일 ▒▒ </title>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<script language="javascript">
<!--  
   alert('document.all.gubun.value');
   //var urlw= "http://mg1.fss.or.kr:8133/rqreport/rqviewer.jsp?doc=/miplatform/FssERP/BK/PayListByEmpQry.rqx&runvar=sEmpNo|"+sEmpNo+"|sPaymDAY|"+sPaymDAY+"|sDiv|"+sDiv+"|'" ;
   
   var urlw="http://mg1.fss.or.kr:8133/rqreport/rqviewer.jsp?doc=/miplatform/FssERP/BK/PayListByEmpQry.rqx&runvar=sEmpNo|<%=sEmpNo%>|sPaymDAY|<%=sPaymDAY%>|sDiv|<%=sDiv%>|'";
   window.open(urlw,'salRqv','width=970px,height=695px,top=5,left=20,scrollbars=no,Uncopyhistory=no, center=yes,menubar=no,toolbar=no,resizable=no,channelmode=no', location='no' );
   self.close();
     
-->   
</script>
</head>
<body>
<input type="hidden" name="gubun"     value="1"> 
</body>
</html>