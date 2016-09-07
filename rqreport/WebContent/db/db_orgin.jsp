<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript">
<!--
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}
//-->
</script>
</head>
<!-- 링크시점선박스없애기 -->
<script> 
function bluring(){ 
	if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG") document.body.focus(); 
} 
document.onfocusin=bluring; 
</script>
<!-- 링크시점선박스없애기 -->
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
	<td> 
    	<!--top시작 -->
    	<table width="100%" border="0" cellpadding="0" cellspacing="0" background="../img/top_bg.gif">
        <tr> 
        	<td align="left" height="80"><img src="../img/top_left.gif" width="757" height="80"></td>
		  	<td align="right" height="80"><img src="../img/top_right.gif" width="232" height="80"></td>
        </tr>
      	</table>
      	<!--topP끝 -->
    </td>
</tr>
<tr>
	<td>
    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<!-- menu시작 -->
	  		<td width="170" valign="top" bgcolor="f9f9f9">
	  			<table width="100%"  border="0" cellspacing="0" cellpadding="0">
        		<tr>
          			<td height="1" bgcolor="263663"></td>
        		</tr>
        		<tr>
          			<td height="23" bgcolor="56729E"  onMouseOver="this.style.backgroundColor='#4D5C87'" onMouseOut="this.style.backgroundColor=''" style=cursor:hand; >
		  				<table width="100%"  border="0" cellspacing="0" cellpadding="0">
            			<tr>
			              	<td align="right" width="25"><img src="../img/menu_dot_y.gif"></td>
			              	<td width="8"></td>
			              	<td align="left"><a href="../document/document.jsp" class="menuoff">문서관리</a></td>
            			</tr>
          				</table>
		 	 		</td>
        		</tr>
        		<tr>
          			<td height="1" bgcolor="263663"></td>
        		</tr>
        		<tr>
          			<td height="23" bgcolor="56729E" onMouseOver="this.style.backgroundColor='#4D5C87'" onMouseOut="this.style.backgroundColor=''" style=cursor:hand; >
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="right" width="25"><img src="../img/menu_dot.gif"></td>
							<td width="8"></td>
							<td align="left"><a href="../usergroup/user.jsp" class="menuoff">사용자/그룹관리</a></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="1" bgcolor="263663"></td>
				</tr>
				<tr>
					<td height="23" bgcolor="263663">
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="right" width="25"><img src="../img/menu_dot.gif"></td>
							<td width="8"></td>
							<td align="left"><a href="db.jsp" class="menuon">DB관리</a></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="1" bgcolor="263663"></td>
				</tr>
				<tr>
					<td height="23" bgcolor="56729E" onMouseOver="this.style.backgroundColor='#4D5C87'" onMouseOut="this.style.backgroundColor=''" style=cursor:hand;>
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="right" width="25"><img src="../img/menu_dot.gif"></td>
							<td width="8"></td>
							<td align="left"><a href="../environment/environment.jsp" class="menuoff">환경설정</a></td>
						</tr>
						</table>
					</td>
				</tr>
				</table> 
			</td>
			<!-- menu끝-->      
			<td width="1" bgcolor="cecece"></td>
			<td valign="top">
				<!-- contents시작-->
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="25">&nbsp;</td>
					<td>
						<table width="100%"  border="0" cellspacing="0" cellpadding="0">
						<tr valign="bottom"> 
							<td height="23" align="right"></td>
						</tr>
						<tr>
							<td>
								<table width="100%"  border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="10"><img src="../img/title_dot.gif" width="10" height="17"></td>
									<td><span class="title">DB관리</span></td>
								</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="7"></td>
						</tr>
						<tr>
							<td height="1" bgcolor="E6E6E6"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>
								<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr> 
									<td align="center"> 
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr> 
											<td height="2" colspan="13" bgcolor="#A8CC72"></td>
										</tr>
										<tr class="table_title"> 
											<td width="50" height="26" class="stitle">
												<input type="checkbox" name="checkbox" value="checkbox"> 
											</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="table_title">데이터베이스ID</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="table_title">데이터베이스</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="table_title">DBMS 종류</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="table_title">연결방법</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="stitle">DB 사용자</td>
											<td valign="bottom" width="1" class="stitle"> 
												<table width="1" bgcolor="#A8CC72" height="7" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td></td>
												</tr>
												</table>
											</td>
											<td class="stitle">설명</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#A8CC72"></td>
										</tr>
										<tr> 
											<td class="text"><input type="checkbox" name="checkbox2" value="checkbox"></td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text"><img src="../img/icon_db_.gif"  hspace="5">test</td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text">test</td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text">Access</td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text">ODBC</td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text">Not Used</td>
											<td width="1"></td>
											<td bgcolor="#FFFFFF" class="text">샘플로 제공</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr bgcolor="F9F9F9"> 
											<td class="text"><input type="checkbox" name="checkbox22" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<!--
										<tr> 
											<td class="text"><input type="checkbox" name="checkbox23" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr bgcolor="F9F9F9"> 
											<td class="text"><input type="checkbox" name="checkbox24" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr> 
											<td class="text"><input type="checkbox" name="checkbox25" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr bgcolor="F9F9F9"> 
											<td class="text"><input type="checkbox" name="checkbox26" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr> 
											<td class="text"><input type="checkbox" name="checkbox27" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr bgcolor="F9F9F9"> 
											<td class="text"><input type="checkbox" name="checkbox28" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										<tr> 
											<td class="text"><input type="checkbox" name="checkbox29" value="checkbox"></td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
											<td width="1"></td>
											<td valign="middle" bgcolor="#FFFFFF" class="text">&nbsp;</td>
										</tr>
										<tr> 
											<td height="1" colspan="13" bgcolor="#E6E6E6"></td>
										</tr>
										-->
										<tr> 
											<td height="1" colspan="13" bgcolor="#A8CC72"></td>
										</tr>
										</table>
									</td>
								</tr>
								<tr> 
									<td height="15"></td>
								</tr>
								<!--
								<tr> 
									<td height="15" align="center"> 
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td height="2" bgcolor="F0F0F0"></td>
										</tr>
										<tr>
											<td height="33" align="center" bgcolor="FCFCFC"> 
												<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td align="left" width="20"><a href="#"><img src="../img/nav_left2.gif" width="15" height="11" border="0"></a></td>
													<td align="left" width="40"><a href="#"><img src="../img/nav_prev.gif" width="33" height="11" border="0"></a></td>
													<td>
														<a href="#"  class="tablenav">1</a>
														<a href="#"  class="tablenav">2</a>
														<a href="#"  class="tablenav">3</a>
														<a href="#"  class="tablenav">4</a>
														<a href="#"  class="tablenav">5</a>
														<a href="#"  class="tablenav">6</a>
														<a href="#"  class="tablenav">7</a>
														<a href="#"  class="tablenav">8</a>
														<a href="#"  class="tablenav">9</a>
														<a href="#"  class="tablenav">10</a>
													</td>
													<td align="right" width="40"><a href="#"><img src="../img/nav_next.gif" width="32" height="11" border="0"></a></td>			
													<td align="right" width="20"><a href="#"><img src="../img/nav_right2.gif" width="15" height="11" border="0"></a></td>
												</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td height="2" bgcolor="F0F0F0"></td>
										</tr>
										<tr> 
											<td height="15"></td>
										</tr>
										<tr> 
											<td height="20">
												<table width="100%" border="0" cellspacing="0" cellpadding="0">
												<tr> 
													<td>
														<table align="left" cellspacing="0" cellpadding="0" border="0">
														<tr> 
															<td>
																<table border="0" align="left" cellpadding="0" cellspacing="0">
																<tr> 
																	<td><img src="../img/btn_w_left.gif" width="15" height="20"></td>
																	<td background="../img/btn_w_bg.gif" class="btn_text_td"><a href="#" class="btn_white">데이터베이스편집</a></td>
																	<td><img src="../img/btn_w_right.gif" width="8" height="20"></td>
																</tr>
																</table>
															</td>
															<td width="7"> </td>
															<td> 
																<table border="0" align="left" cellpadding="0" cellspacing="0">
																<tr> 
																	<td><img src="../img/btn_w_left.gif" width="15" height="20"></td>
																	<td background="../img/btn_w_bg.gif" class="btn_text_td"><a href="#" class="btn_white">삭제</a></td>
																	<td><img src="../img/btn_w_right.gif" width="8" height="20"></td>
																</tr>
																</table>
															</td>
														</tr>
														</table>
													</td>
													<td align="right">
														<table cellspacing="0" cellpadding="0" border="0">
														<tr> 
															<td> 
																<table border="0" cellspacing="0" cellpadding="0">
																<tr> 
																	<td><img src="../img/btn_g_left.gif" width="4" height="20"></td>
																	<td background="../img/btn_g_bg.gif"><img src="../img/icon_db_regis.gif" hspace="6"></td>
																	<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" class="btn_green" onClick="MM_openBrWindow('newdb.jsp','','width=380,height=400')">새 데이터베이스 등록</a></td>
																	<td><img src="../img/btn_g_right.gif" width="8" height="20"></td>
																</tr>
																</table>
															</td>
														</tr>
														</table>
													</td>
												</tr>
												</table>
											</td>
										</tr>
										<tr> 
											<td height="19" colspan="3">&nbsp;</td>
										</tr>
										</table>
									</td>
								</tr>
								-->
								</table>
							</td>
						</tr>
						<tr>
							<td height="33"></td>
						</tr>
						</table>
					</td>
					<td width="27">&nbsp;</td>
				</tr>
				</table>
				<!-- contents끝-->
			</td>
		</tr>
		</table>
	</td>
</tr>
<tr> 
	<td> 
		<!--bottom시작 -->
		<table width="100%" height="30" border="0" cellspacing="0" cellpadding="0">
		<tr> 
			<td height="1" colspan="4" bgcolor="cecece"></td>
        </tr>
        <tr> 
			<td width="170" height="20"></td>
			<td width="1" bgcolor="cecece"></td>
			<td align="left" valign="bottom"><img src="../img/bottomcopy.gif" hspace="25"></td>
			<td width="24"></td>
        </tr>
        <tr> 
			<td colspan="4">&nbsp;</td>
        </tr>
		</table>
      <!--bottom끝 -->
	</td>
</tr>
</table>

</body>
</html>
