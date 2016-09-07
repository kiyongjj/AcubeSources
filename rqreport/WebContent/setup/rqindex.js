var rqindex = {}
rqindex.index = function(){}
rqindex.index.prototype = {

	submitFn : function(){
		loginform = document.login;
		if($("#strUserid").val() ==''){
			alert(msg_idcheck); // msg_idcheck
			$("#strUserid").focus();
		}else if($("#strUserpw").val() == ''){
			alert(msg_pwcheck); // msg_pwcheck
			$("#strUserpw").focus();
		}else{
			loginform.action = "index.jsp";
			document.getElementById("strUserpw").value = gfunc.base64Encode($("#strUserpw").val());
			loginform.submit();
		}
	},
	inputUrename : function(){
		$("#strUserid").focus(); 
	},
	init : function(){
		if (browserName == "Microsoft Internet Explorer" ) {
			if (navigator.appVersion.indexOf("MSIE") != -1) {
				IEmajorStart = navigator.appVersion.indexOf("MSIE") + 4;
				IEmajorEnd = (IEmajorStart + 5);
				theMajor = navigator.appVersion.substring(IEmajorStart, IEmajorEnd);
				(version = theMajor);  
			} if (isNaN(version)) {
				(version = (version.substring(0, (version.length - 1))));
			}
		}else{
			// ETC
		}
		msg = (browserName + " " + browserVersion);
		$("#bwType").text(msg);
		$("#bwMsgBox").css({"font-family":"verdana"}); // bwMsgBox css
		$("#strUserid").css({"font-family":"verdana","color":"#666666",
						     "border":"1px solid #D4D4D4","background-color":"#F8F8F8"});
		$("#strUserpw").css({"font-family":"verdana","color":"#666666",
							 "border":"1px solid #D4D4D4","background-color":"#F8F8F8"});

	}
}

window.onload = function(){}