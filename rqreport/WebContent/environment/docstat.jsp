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
Environment env = Environment.getInstance();
String strAuth = "";
UserModel oUserModel	=	(UserModel) session.getAttribute("UM");
if(session.isNew() || session.getAttribute("UM") == null){
	strAuth = "G";
}else{
	strAuth = oUserModel.getAuth();
}
String user_agt = request.getHeader("user-agent");
String viewer_type  = (user_agt.indexOf("MSIE") != -1) ? "ocx" : "plugin";
%>
<jsp:include flush="true" page="../cs.jsp"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../css/common.css" rel="stylesheet" type="text/css"/>
<link href="../css/ui.all.css" rel="stylesheet" type="text/css"/>
<style>body {font-size: 63%; font-family:"Dotum", "Tahoma";}</style>
<script language="javascript" src="../setup/AC_OETags.js" ></script>
<script language="javascript" src="../setup/<%=env.jsframework%>"></script>
<script language="javascript" src="../setup/ui.datepicker.js"></script>
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

function callJSPrint(){
	print();
}
document.onfocusin=bluring;
</script>
<script language="JavaScript" type="text/javascript">
<!--
// -----------------------------------------------------------------------------
// Globals
// Major version of Flash required
var requiredMajorVersion = 9;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 124;
// -----------------------------------------------------------------------------
// -->
</script>
<script language="javascript">
var docstat = {};
docstat.docstatPage = function(){
	this.viewflag = "flash";
}
docstat.docstatPage.prototype = {
	toggleView : function(){
		if(this.viewflag == "flash"){
			$("#flasharea").hide();
			$("#rqxarea").show();
			$("#togglebtn").val("<rqfmt:message strkey='docstat.main.button.flashview'/>");
			this.viewflag = "rqx";
		}else if(this.viewflag == "rqx"){
			$("#rqxarea").hide();
			$("#flasharea").show();
			$("#togglebtn").val("<rqfmt:message strkey='docstat.main.button.rqxview'/>");
			this.viewflag = "flash";
		}
	}
}

$(function(){
	docstatPage = new docstat.docstatPage();
	<%if(viewer_type.equals("ocx")){%>
	//////// rqx ///////////////////////
	$("#flasharea").hide();
	$("#rqxarea").show();
	$("#togglebtn").hide();
	<%}else{%>
	//////// flash ////////////////////
	$("#flasharea").show();
	$("#rqxarea").hide();
	//$("#togglebtn").show();
	$("#togglebtn").hide();
	<%}%>
	$("#togglebtn").click(function(){ 
		docstatPage.toggleView(); 
	});

	$.datepicker.setDefaults({
    	dateFormat: 'yymmdd'
	});
	$("#duringstart").datepicker({
		defaultDate: new Date(),
		//defaultDate: new Date(2009, 1 - 1, 30),
        showOn: "focus", // focus, button, both
        showAnim: "show", // show, fadeIn, slideDown
        duration: 200
	});
	$("#duringend").datepicker({
		defaultDate: new Date(),
		//defaultDate: new Date(2009, 1 - 1, 30),
        showOn: "focus", // focus, button, both
        showAnim: "show", // show, fadeIn, slideDown
        duration: 200
	});
	$("#duringstart").click(function(){
		if($("#drawrqx").attr("src") != ""){
			$("#drawrqx").attr("src", "");
		}
	});
	$("#duringend").click(function(){
		if($("#drawrqx").attr("src") != ""){
			$("#drawrqx").attr("src", "");
		}
	});
	$("#searchbtn").click(function(){
		var lm_url = "docstat_rqviewer.jsp";
		var lm_doc = "";
		var r_searchday   = document.getElementById("searchday");
		//var r_searchmonth = document.getElementById("searchmonth");
		var r_baseday = document.getElementById("baseday");
		//var r_basedoc = document.getElementById("basedoc");
		if(r_searchday.checked){
			if(r_baseday.checked){
				lm_doc = "searchday_baseday.rqx";
			}else{
				lm_doc = "searchday_basedoc.rqx";
			}
		}else{
			if(r_baseday.checked){
				lm_doc = "searchmonth_baseday.rqx";
			}else{
				lm_doc = "searchmonth_basedoc.rqx";
			}
		}
		var lm_duringstart = $("#duringstart").val() == "" ? "00000000" : $("#duringstart").val();
		var lm_duringend = $("#duringend").val() == "" ? "99999999" : $("#duringend").val();
		var lm_docsearch = $("#docsearch").val() == "" ? "" : $("#docsearch").val();
		
		lm_url += "?doc=/" +lm_doc+ "&runvar=" + "duringstart|" +lm_duringstart+ "|duringend|" +
		          lm_duringend+ "|file_nm|" +lm_docsearch;
        //alert(lm_url);
        var newwindow = document.getElementById("newwindow");
        if(newwindow.checked == true){
			window.open(lm_url,"","menubar=no,toolbar=no,location=no,resizable=yes");
        }else{
			$("#drawrqx").attr("src", lm_url);
        }
	});
	$("#printbtn").click(function(){
		var childocx = drawrqx.document.getElementById("RQViewer");
		if(childocx != null){
			childocx.Print(true, 0, 0, 1); // dialog
			//childocx.Print(false, 0, 0, 1); // no dialog
		}else{
			alert("RQViewer ocx Not loaded");
		}
	});
		
});
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" bgcolor="f8f8f8" class="main_bg">
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
<tr valign="top">
	<td>
		<!--top start -->
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
      	<!--topP end -->
    </td>
</tr>
<tr valign="top">
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
							<td height="45" valign="top"><a href="../environment/rqenv.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>" onMouseOver="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOver.img'/>'" onMouseOut="this.src='../img/<rqfmt:message strkey='leftmenu.rqenv.mouseOut.img'/>'" width="217" height="39" border="0" alt="" name="environment"></a></td>
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
							<td height="45" valign="top"><a href="../environment/docstat.jsp"><img src="../img/<rqfmt:message strkey='leftmenu.statistics.mouseOver.img'/>" width="217" height="39" border="0" alt="" name="statistics"></a></td>
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
			  	<!-- menu end-->
			</td>
			<td width="787" valign="top" class="main_conbg">
				<!-- contents -->
				<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td height="56" class="main_conbgtop4" style="padding-left:23px;"><img src="../img/main_condot.gif" width="23" height="20" border="0" alt="" align="absmiddle">&nbsp;&nbsp;<span class="comment_text"/><rqfmt:message strkey='docstat.title'/>
					</td>
				</tr>
				<tr>
					<td style="padding-left: 10px;padding-right: 55px;">
						<table cellpadding="0" cellspacing="0" width="100%" border="0">
						<tr>
							<td><rqfmt:message strkey='docstat.unit'/> : <span style="font-style: italic;">s (second)</span></td><td align="right"><input type="button" value="rqx로 보기" id="togglebtn"/></td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="100%" valign="top" style="padding-left:7px;padding-right:10px;">
						<!-- subcontents start-->
						<!-- rqxarea -->
						<table cellpadding="3" cellspacing="0" border="0" id="rqxarea" width="725">
						<tr>
							<td>
								<table border="1" cellspacing="0" cellpadding="7" bgcolor="#ADBCCD" bordercolor="#8292A4" bordercolordark="#9BB7D4" bordercolorlight="#8292A4" width="100%">
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td width="68"><rqfmt:message strkey='docstat.ui.searchcon'/> : </td>
											<td width="20"><input type="radio" id="searchday" name="searchcon" value="searchday" checked="checked"/></td>
											<td width="42"><rqfmt:message strkey='docstat.ui.searchday'/></td>
											<td width="20"><input type="radio" id="searchmonth" name="searchcon" value="searchmonth"/></td>
											<td width="245"><rqfmt:message strkey='docstat.ui.searchmonth'/></td>
											<td width="65"><rqfmt:message strkey='docstat.ui.basesearch'/> : </td>
											<td width="20"><input type="radio" id="baseday" name="basesearch" value="baseday" checked="checked"/></td>
											<td width="42"><rqfmt:message strkey='docstat.ui.baseday'/> </td>
											<td width="20"><input type="radio" id="basedoc" name="basesearch" value="basedoc"/></td>
											<td width="40"><rqfmt:message strkey='docstat.ui.basedoc'/> </td>
											<td width="20"><input type="checkbox" id="newwindow"/></td>
											<td><rqfmt:message strkey='docstat.ui.newwindow'/></td>
										</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td width="68"><rqfmt:message strkey='docstat.ui.during'/> : </td>
											<td width="90"><input type="text" name="duringstart" id="duringstart" value="" size="8"/></td>
											<td width="20" style="font-size:14px;font-weight: bold;">~&nbsp;</td>
											<td width="90"><input type="text" name="duringend" id="duringend" value="" size="8"/></td>
											<td width="150"></td>
											<td width="45"><rqfmt:message strkey='docstat.ui.docname'/> : </td>
											<td width="160"><input type="text" name="docsearch" id="docsearch" value="" size="20"/></td>
											<td><input type="button" name="searchbtn" id="searchbtn" value="<rqfmt:message strkey='docstat.ui.searchbtn'/>"/></td>
											<td width="6"></td>
											<td><input type="button" name="printbtn" id="printbtn" value="<rqfmt:message strkey='docstat.ui.printbtn'/>"/></td>
											<td></td>
										</tr>										
										</table>
									</td>
								</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								<iframe src="" name="drawrqx" id="drawrqx" marginheight="0" marginwidth="0" frameborder="0" scrolling="no" width="720" height="355"></iframe>
							</td>
						</tr>
						</table>
						<!-- flasharea -->
						<table cellpadding="0" cellspacing="0" border="0" id="flasharea">
						<tr>
							<td>
								<script language="JavaScript" type="text/javascript">
								<!--
								// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
								var hasProductInstall = DetectFlashVer(6, 0, 65);
								
								// Version check based upon the values defined in globals
								var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
								
								if ( hasProductInstall && !hasRequestedVersion ) {
									// DO NOT MODIFY THE FOLLOWING FOUR LINES
									// Location visited after installation is complete if installation is required
									var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
									var MMredirectURL = window.location;
								    document.title = document.title.slice(0, 47) + " - Flash Player Installation";
								    var MMdoctitle = document.title;
								
									AC_FL_RunContent(
										"src", "playerProductInstall",
										"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
										"width", "730",
										"height", "428",
										"align", "middle",
										"id", "reqube_docstat",
										"quality", "high",
										"bgcolor", "#FFFFFF",
										"name", "reqube_docstat",
										"allowScriptAccess","sameDomain",
										"type", "application/x-shockwave-flash",
										"pluginspage", "http://www.adobe.com/go/getflashplayer"
									);
								} else if (hasRequestedVersion) {
									// if we've detected an acceptable version
									// embed the Flash Content SWF when all tests are passed
									AC_FL_RunContent(
											"src", "reqube_docstat",
											"width", "730",
											"height", "428",
											"align", "middle",
											"id", "reqube_docstat",
											"quality", "high",
											"bgcolor", "#FFFFFF",
											"name", "reqube_docstat",
											"allowScriptAccess","sameDomain",
											"type", "application/x-shockwave-flash",
											"pluginspage", "http://www.adobe.com/go/getflashplayer"
									);
								  } else {  // flash is too old or we can't detect the plugin
								    var alternateContent = 'Alternate HTML content should be placed here. '
								  	+ 'This content requires the Adobe Flash Player. '
								   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
								    document.write(alternateContent);  // insert non-flash content
								  }
								// -->
								</script>
								<noscript>
								  	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
											id="reqube_docstat" width="730" height="428"
											codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
											<param name="movie" value="reqube_docstat.swf" />
											<param name="quality" value="high" />
											<param name="bgcolor" value="#869ca7" />
											<param name="allowScriptAccess" value="sameDomain" />
											<embed src="reqube_docstat.swf" quality="high" bgcolor="#FFFFFF"
												width="730" height="428" name="reqube_docstat" align="middle"
												play="true"
												loop="false"
												quality="high"
												allowScriptAccess="sameDomain"
												type="application/x-shockwave-flash"
												pluginspage="http://www.adobe.com/go/getflashplayer">
											</embed>
									</object>
								</noscript>
							</td>
						</tr>
						</table>
						<!-- subcontents end -->
					</td>
				</tr>
				</table>
				<!--contents end -->
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
