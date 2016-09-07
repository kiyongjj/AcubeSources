<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.sds.rqreport.model.*" %>
<%@ page import="com.sds.rqreport.service.web.*" %>
<%@ page import="com.sds.rqreport.Environment" %>
<%@ page import="com.sds.rqreport.util.*" %>
<%@ taglib uri="/WEB-INF/tld/RQResource.tld" prefix="rqfmt" %>
<%@ taglib uri="/WEB-INF/tld/RQPagePrivilege.tld" prefix="pagePrivilege" %>
<pagePrivilege:checkPagePrivilege action="checkprivilege"  privilege="A"/>
<%
String strAuth = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript">
function logoutCfm(){
	if(confirm("<rqfmt:message strkey='common.logout.alert'/>")){
		document.location.href="../common/sessionPrc.jsp?mode=sessionLogOut";
	}else{
		return;
	}
}

function bluring(){
	if(event.srcElement.tagName=="A"||event.srcElement.tagName=="IMG") document.body.focus();
}
document.onfocusin=bluring;
</script>
<jsp:include flush="true" page="../cs.jsp"/>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="f8f8f8" class="main_bg">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<!--top시작 -->
    	<table width="1004" border="0" cellpadding="0" cellspacing="0" >
        <tr>
        	<td align="left" valign="top" width="465" height="151" class="main_lbg" >
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td style="padding-left:33px;padding-top:35px;"><img src="../img/main_logo.gif" width="290" height="31" border="0" alt=""></td>
				</tr>
				</table>
			</td>
		  	<td height="151"><IMG SRC="../img/main_bimg04.jpg" WIDTH="539" HEIGHT="151" BORDER="0" ALT=""></td>
        </tr>
      	</table>
      	<div class="logoutlayer" style="position: absolute;top: 10px;left: 930px;">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="middle">
				<td><a href="javascript:logoutCfm();" class="logout"><img src="../img/logout_01_n.gif" width="71" height="20" border="0" onMouseOver="this.src='../img/logout_01_on.gif'" onMouseOut="this.src='../img/logout_01_n.gif'"/></a></td>
			</tr>
			</table>
		</div>
      	<!--topP끝 -->
    </td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td width="217" valign="top" class="main_menubgb">
				<!-- menu -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td height="578" class="main_menubg" valign="top">
						<table width="217" height="" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="16"></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../document/document.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.document.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.document.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.document.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="folder"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../usergroup/user.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.usergroup.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="user"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../db/db.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.db.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="data"></a></td>
						</tr>
					<%
						if( strAuth.equals("A")){
					%>
						<tr>
							<td height="45" valign="top"><a href="../environment/environment.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.environment.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="install"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/rqenv.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="environment"></a></td>
						</tr>
						<tr>
							<td height="45" valign="top"><a href="../environment/logman.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.logman.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="logman"></a></td>
						</tr>
						<%
							String isUse_schedule = "false";
							if(oUserModel != null){
								isUse_schedule = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "scheduler");
							}
							if(isUse_schedule.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../schedule/rqscheduletimelist.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.schedule.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="schedule"></a></td>
						</tr>
						<%
							}
							String isUse_statistics = "false";
							if(oUserModel != null){
								isUse_statistics = new RQUser().getPageUse(oUserModel.getPrivilegePage(), "RQStatistics");
							}
							if(isUse_statistics.equals("true")){
						%>
						<tr>
							<td height="45" valign="top"><a href="../environment/docstat.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.statistics.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="statistics"></a></td>
						</tr>
					<%
							}
						}
					%>
				  		</table>
					</td>
				</tr>
				<tr>
					<td height="100%" class="main_menubgb"></td>
				</tr>
			  	</table>
			  	<!-- menu	끝-->
			</td>
			<td width="787" valign="top" class="main_conbg">
				<!-- contents -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop4" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text" ><rqfmt:message strkey='environment.environment.title'/>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:7px;padding-right:10px;" >
						<!-- 본문 시작-->
						<form name="envSetForm" method="POST" action="updateenv.jsp">
						<table width="100%" height="504" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="25">&nbsp;</td>
							<td valign="top">
								<table cellpadding="3" cellspacing="0" border="0" class='border_C'>
								<tr>
									<td bgcolor='#DCDCDC' class='border_F' align="center">
										<span style=" font-size:9pt; font-family:Verdana; color:#000000; text-decoration:none;line-height:26px;">
											<rqfmt:message strkey='environment.environment.environmentvar'/>
										</span>
									</td>
									<td bgcolor='#DCDCDC' class='border_F' align="center">
										<span style=" font-size:9pt; font-family:Verdana; color:#000000; text-decoration:none;line-height:26px;">
											<rqfmt:message strkey='environment.environment.default'/>
										</span>
									</td>
									<td bgcolor='#DCDCDC' class='border_F' align="center">
										<span style=" font-size:9pt; font-family:Verdana; color:#000000; text-decoration:none;line-height:26px;">
											<rqfmt:message strkey='environment.environment.valueforapply'/>
										</span>
									</td>
								</tr>

					<%

						//DocRepository docRep = new DocRepository();
						Environment tenv = Environment.getInstance();
						String path = tenv.getConfigFile();
						String pathorg = path.substring(0, path.length() - 3) + "org";
						//String pathdesc = path.substring(0, path.length() - 10) + "description";

						//Properties prop = tenv.getPropertyLoader().getProperties();
						Properties prop = new Properties();
						Properties proporg = new Properties();

						ResourceBundle propdesc = null;
						if(tenv.rqreport_server_locale.equals("en")){
							propdesc = ResourceBundle.getBundle("description_en", Locale.US);
						}else{
							propdesc = ResourceBundle.getBundle("description_ko", Locale.KOREAN);
						}

						new FileInputStream(path);
						prop.load(new FileInputStream(path));
						proporg.load(new FileInputStream(pathorg));
//						propdesc.load(new FileInputStream(pathdesc));
						Enumeration lm_enum =  prop.keys();
						ArrayList arr = new ArrayList(20);
						while(lm_enum.hasMoreElements())
						{
							arr.add(lm_enum.nextElement());
						}
//						Object[] sarr = arr.toArray();
						String sarrstr = "rqreport.common.uploadPrc.filelimit,rqreport.engine.thread,rqreport.engine.time_wait,rqreport.engine.timeout" +
												",rqreport.repository.jndi,rqreport.repository.path,rqreport.repository.docTableName" +
												",rqreport.repository.doc_dsTableName,rqreport.repository.sqlTableName,rqreport.repository.userTableName" +
												",rqreport.server.type,rqreport.server.RQcharset,rqreport.server.charset,rqreport.server.SSO";
						String[] sarr = sarrstr.split(",");
//						Arrays.sort(sarr);

						//while(lm_enum.hasMoreElements()){
						for(int i =0 ; i < sarr.length; ++i){
							String color;
							if(i%2 == 0)
							{
								color = "#F4F4F4";
							}
							else
								color = "#E0E0E0";
							//String lm_str  = (String) lm_enum.nextElement();
							String lm_str  = (String)sarr[i] ;

							Encoding enc = new Encoding();
							String lm_serverCharset = enc.getServerCharset();
							String lm_RQCharset = enc.getRQCharset();

							//String desc = new String(propdesc.getString(lm_str).getBytes(lm_serverCharset), lm_RQCharset);
							String desc = propdesc.getString(lm_str);
							if( !lm_str.equals("rqreport.engine.csep") &&
								!lm_str.equals("rqreport.engine.rsep") &&
								!lm_str.equals("rqreport.engine.ssep") &&
								!lm_str.equals("rqreport.rqsession.classpath") &&
								!lm_str.equals("rqreport.engine.cache_size") &&
								!lm_str.equals("rqreport.repository.install_pw") &&
								!lm_str.equals("rqreport.repository.install_id")
								 )
							{
								if(desc != null && desc.length() > 0)
								{
									%>

									<tr valign="center" >
										<td bgcolor='<%=color%>' class='border_F' colspan="3">
											<span style=" font-size:9pt; font-family:Verdana; color:#000000; text-decoration:none;">
												<img src="../img/bullet_desc.gif" width="11" height="13"/> <%= desc %>
											</span>
										</td>
									</tr>

									<%
								}

					%>
								<tr valign="center" >
									<td bgcolor='<%=color%>' class='border_F'>
										<span style=" font-size:9pt; font-family:Verdana; color:#000000; text-decoration:none;">
											<%= lm_str %>
										</span>
									</td>
									<td bgcolor='<%=color%>' class='border_F' style="padding-top:5px;" width="240">
										<%= proporg.get(lm_str)%>
									</td>
									<td bgcolor='<%=color%>' class='border_F' >
										<input type="text" name="<%=lm_str %>" value="<%= prop.get(lm_str)%>" size="<%= 32 %>"  style="border-color:#CCCCCC;border:1px;background-color: <%=color%>"/>
									</td>
								</tr>

					<%
							}else
							{

							}
						}
					%>

								</table>

								<table cellpadding="0" cellspacing="0" border="0" width="730" align="right" >
								<tr><td><img src="../img/1pix.gif" width="1" height="14"/></td></tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" align="right">
										<tr>
											<td>
												<table border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td><img src="../img/btn_g_left2.gif"></td>
													<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="document.envSetForm.submit();return false;" class="btn_green"><rqfmt:message strkey='environment.environment.button.apply'/></a></td>
													<td><img src="../img/btn_g_right.gif"></td>
												</tr>
												</table>
											</td>
											<td>&nbsp;</td>
											<td>
												<table border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td><img src="../img/btn_g_left2.gif"></td>
													<td background="../img/btn_g_bg.gif" class="btn_text_td"><a href="#" onclick="document.location.reload();return false;" class="btn_green"><rqfmt:message strkey='environment.environment.button.reset'/></a></td>
													<td><img src="../img/btn_g_right.gif"></td>
												</tr>
												</table>
											</td>
										</tr>
										</table>
									</td>
								</tr>
								</table>

								 <br><br>

							</td>
							<td width="27">&nbsp;</td>
						</tr>
						</table>
						</form>
						<!-- 본문  끝 -->

					</td>
				</tr>
				</table>
				<!--contents 끝 -->
			</td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="14" class="main_conbgb"></td>
		</tr>
		</table>
	</td>
</tr>
<tr>
	<td>
		<table width="1004" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td height="44" style="padding-left:39px;" valign="bottom"><IMG SRC="../img/copy_2.gif" width="382" height="8" border="0" alt=""></td>
		</tr>
		</table>
	</td>
</tr>
</table>
<br>
<br>
</body>
</html>
