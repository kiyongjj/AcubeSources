<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>REQUBE REPORT</title>
<link href="../css/common.css" rel="stylesheet" type="text/css">
<script language="javascript">
var public_description = new init();
var curObj;
var curObj2;
var ctime ="";

//get current date
var now_date=new Date();
var s_year=now_date.getYear();
if( s_year<1900) s_year=s_year+1900;
var s_month = return0(now_date.getMonth()+1);
var s_day = return0(now_date.getDate());
var dtype = "YYYY/MM/DD";
var strLang = "";
var full_month_name=new Array('January','February','March','April','May','June','July','August','September','October','November','December');
var month_name=new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var day_name=new Array('S','M','T','W','T','F','S');
var year_str="";
var day_str="";
var isSelect = 0;
var calWidth = 180;
var img_path='../img/';

function init() {
   this.put_datetype = put_datetype;
   this.put_curDate = put_curDate;
   this.put_full_month_name= put_full_month_name;
   this.put_month_name= put_month_name;
   this.put_day_name= put_day_name;
   this.put_year_str= put_year_str;
   this.put_day_str= put_day_str;
   this.put_select = put_select;
   this.put_calWidth = put_calWidth;
}

function hover(on) {
   var el = window.event.srcElement;
   if (el && el.nodeName == "TD") {
      if (el.datetitle == '') return;
      if (on) {
         el.style.backgroundColor = '#E1E1E1';
      } else {
         el.style.backgroundColor = '';
      }
   }
}

function choose(y,m) {
   var el = window.event.srcElement;
   if (el && el.nodeName == "TD") {
      if (el.datetitle == '') return;
      return_date(y,m,el.datetitle);
   }
}

function put_datetype(str){
   dtype = str;
}

function put_full_month_name(str){
   full_month_name = str;
}

function put_month_name(str){
   month_name = str;
}

function put_day_name(str){
   day_name = str;
}

function put_year_str(str){
   year_str = str;
}

function put_day_str(str){
   day_str = str;
}

function put_select(str){
   isSelect = str;
}

function put_calWidth(str){
   calWidth = str;
}

function put_curDate(str){
	var y=0,m=0,d=0;
	ctime = "";
	if (str.length == 0 ){
		y = s_year;
		m = s_month;
		d = s_day;
	}else	{
		y = parseInt(str.substring(0,4),10);
		m = parseInt(str.substring(4,6),10);
		d = parseInt(str.substring(6,8),10);
		if (str.length >= 14) ctime = str.substring(8,14);
	}
	show_current(y,m,d);
	return(false);
}

function setLang( lang ) {
	if( lang != null || lang != "" )
		strLang = lang;
	else
		strLang = "E";
}

function return0(str){
	str=""+str;
	if (str.length==1) str="0"+str;
	return str;
}


function dreplace( str , old_char , new_char ){
	if( str == null || str == "" ) return;
	else {
		var fromindex = 0;
		var temp = "";
		for(var i=0 ; i<str.length ; i++) {
			fromindex = i;
			pos = str.indexOf(old_char,fromindex);
			if( pos != -1 ) {
				temp = str.substring(0,pos) + new_char + str.substring(pos+old_char.length);
				str = temp;
				i = pos+new_char.length-1;
			} else break;
		}
		return str;
	}
}

//open calendar
function show_current(y,m,d){
	s_year=y;
	s_month=m;
	s_day=d;
	make_calendar(s_year,s_month,s_day)
}

//processing changed date
function return_date(year_item, month_item, day_item){
	if( year_item < 1900) year_item = 1900 + year_item;
	month_item=return0(month_item);
	day_item=return0(day_item);
	//make_calendar(year_item,month_item,day_item);
	input_date(year_item,month_item,day_item);
}

//output selected date
function input_date(year_item, month_item, day_item){
	//var m_name=new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	if( year_item < 1900) year_item = 1900 + year_item;
	month_item=""+month_item;
	day_item=""+day_item;
	month_item2 = return0(month_item);
	day_item2 = return0(day_item);
	var hour_item = "00";
	var min_item = "00";
	var sec_item = "00";
	if (ctime != ""){
		var hour_item = ctime.substring(0,2);
      	var min_item = ctime.substring(2,4);
      	var sec_item = ctime.substring(4,6);
   	}
	var backupidval = year_item + month_item2 + day_item2 + ctime;
	//realDate = dtype.toUpperCase();
	realDate = dtype;
	if(realDate.indexOf("SS")!=-1) realDate=dreplace(realDate,"SS",sec_item);
	else if(realDate.indexOf("ss")!=-1) realDate=dreplace(realDate,"ss",sec_item);

	if(realDate.indexOf("MIN")!=-1) realDate=dreplace(realDate,"MIN",min_item);
	else if(realDate.indexOf("mm")!=-1) realDate=dreplace(realDate,"mm",min_item);

	if(realDate.indexOf("HH")!=-1) realDate=dreplace(realDate,"HH",hour_item);
	else if(realDate.indexOf("hh")!=-1) realDate=dreplace(realDate,"hh",hour_item);

	if(realDate.indexOf("DD")!=-1) realDate=dreplace(realDate,"DD",day_item2);
	else if(realDate.indexOf("dd")!=-1) realDate=dreplace(realDate,"dd",day_item2);
	else if(realDate.indexOf("D")!=-1) realDate=dreplace(realDate,"D",day_item);
	else if(realDate.indexOf("d")!=-1) realDate=dreplace(realDate,"d",day_item);

	if(realDate.indexOf("MMMM")!=-1) realDate=dreplace(realDate,"MMMM",full_month_name[parseInt(month_item,10)-1]);
	else if(realDate.indexOf("MON")!=-1) realDate=dreplace(realDate,"MON",month_name[parseInt(month_item,10)-1]);
	else if(realDate.indexOf("MMM")!=-1) realDate=dreplace(realDate,"MMM",month_name[parseInt(month_item,10)-1]);
	else if(realDate.indexOf("MM")!=-1) realDate=dreplace(realDate,"MM",month_item2);
	else if(realDate.indexOf("M")!=-1) realDate=dreplace(realDate,"M",month_item);

	if(realDate.indexOf("YYYY")!=-1) realDate=dreplace(realDate,"YYYY",year_item);
	else if(realDate.indexOf("yyyy")!=-1) realDate=dreplace(realDate,"yyyy",year_item);
	else if(realDate.indexOf("YY")!=-1) realDate=dreplace(realDate,"YY",year_item.substring(2));
	else if(realDate.indexOf("yy")!=-1) realDate=dreplace(realDate,"yy",year_item.substring(2));
	var backupval = realDate;
	//check point !!!!!!!!!!
    window.external.raiseEvent(backupidval,backupval);
}

//currently not used
function getCurDateStr(){
	//var day_name=new Array('S','M','T','W','T','F','S');
	var cur_date=new Date();
	var c_year=cur_date.getYear();
	if( c_year<1900) c_year=s_year+1900;
	var c_month = cur_date.getMonth()+1;
	var c_day = cur_date.getDate();
	var c_dayof = day_name[cur_date.getDay()];
	return c_month + '.' + c_day + '(' + c_dayof + ')';
}

function getTimeStr(str){
 	return str.substring(0,2) + ":" + str.substring(2,4);
}

function time_updown(h,m,s){
	var hh = parseInt(ctime.substring(0,2),10);
	var mm = parseInt(ctime.substring(2,4),10);
	var ss = parseInt(ctime.substring(4,6),10);
	hh += h;
	mm += m;
	ss += s;
	if (hh < 0) hh = 23; else if (hh > 23) hh = 0;
	if (mm < 0) mm = 59; else if (mm > 59) mm = 0;
	if (ss < 0) ss = 59; else if (ss > 59) ss = 0;
	ctime = return0(hh) + return0(mm) + return0(ss);
	document.all.id_time.innerText = getTimeStr(ctime);
}

function showHourDiv(){
   	document.all.divHour.style.display = "";
   	document.all.divMin.style.display = "none";
}

function showMinuteDiv(){
   	document.all.divHour.style.display = "none";
   	document.all.divMin.style.display = "";
}

function setTime(h,m,s){
	var hh = parseInt(ctime.substring(0,2),10);
	var mm = parseInt(ctime.substring(2,4),10);
   	var ss = parseInt(ctime.substring(4,6),10);
   	if (h >= 0) hh = h;
   	if (m >= 0) mm = m;
   	if (s >= 0) ss = s;
   	ctime = return0(hh) + return0(mm) + return0(ss);
   	document.all.id_time.innerText = getTimeStr(ctime);
   	if (h >= 0)
    	document.all.divHour.style.display = "none";
   	if (m >= 0)
    	document.all.divMin.style.display = "none";
}

//draw calendar UI
function make_calendar(y,m,d){
	var content="";
	var day_num=new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
   	m = parseInt(m, 10);
   	if (m == 0) { 
   		y = y - 1; m = 12; 
   	}else if (m == 13) { y = y + 1; m = 1; }
	if(((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0)) day_num[1]=29;
	var first=new Date(y,m-1, 1);
	firstday    = first.getDay()+1
	DaysInMonth = day_num[m-1]

	if ((m-2) == -1) DaysInBefMonth = day_num[11]
	else DaysInBefMonth = day_num[m-2]

	if (d > DaysInMonth) day = DaysInMonth
	else day = d
   
 	var cur_date = new Date(y,m-1,day);
   	var c_dayof = day_name[cur_date.getDay()];

	content = content + 
    "<FORM NAME=calendar onSubmit='return false;'>" +
	"<TABLE width=215 border=0 cellspacing=1 cellpadding=4 bgcolor='#CCCCCC'>"+
    "<TR><TD bgcolor='#FFFFFF'>" + 
	"<Table width=100% cellspacing=0 cellpadding=0>" +
    "<Tr><Td height=80 valign=top background='" + img_path + "top_bg.gif'>"+
    "<table border=0 width=100% cellspacing=0 cellpadding=0>" ;

   	//close
   	content += "<tr><td class=calda01 height=11 align=right valign=top colspan=3>" +
    "<img src='"+img_path+"close.gif' style='cursor:hand'" +
    " onclick='javascript:window.external.raiseEvent(\"\",\"\");'>" + 
    "</td></tr>" ;
            
   	//year
   	content += "<tr><td class=calda01 height=19 align=center valign=bottom width=80 nowrap>" +
    "<img class=calarrow src='" + img_path + "ico_left2.gif'" + 
    " onClick='make_calendar(" + 
       	(y-1) + "," + m + "," + day + ");' >"+
      	+ y + year_str +
    "<img class=calarrow src='" + img_path+"ico_right2.gif' " + 
    " onClick='make_calendar(" + (y+1) + "," + m + "," + day + ");' >" +
    "</td>" ;
     //month & day
   	if (ctime == "")
    	tmp = " style='padding-bottom:20' rowspan=2";
	else
    	tmp = "";
	content+=
    "<td width=55 class=calto style='text-align:right' nowrap valign=bottom"+tmp+">" +
    " <img class=calarrow src='"+img_path+"ico_left.gif'" + 
    "  onClick='make_calendar(" + y + "," + (m-1) + "," + day + ");'>"
      	   + month_name[m-1] + 
    "<img class=calarrow src='"+img_path+"ico_right.gif'" + 
    "  onClick='make_calendar(" + y + "," + (m+1) + "," + day + ");'></td>" +
    "<td width=55 class=calto nowrap valign=bottom"+tmp+">" + day + day_str + "(" + c_dayof + ")</td></tr>";

  	//time
  	if (ctime == "")
    	content += "<tr><td height=27></td></tr></table>";
  	else{
      	"<tr><td></td>"+
      	"<td height=27 colspan=2 align=center >"+
      	"<table cellpadding=0 cellspacing=0><tr>"+
      	"<td class=calto width=8 valign=top>"+
      	"<img class=calarrow vspace=2 src='"+img_path+"ico_down.gif' "+
      	" onclick='showHourDiv()'>"+
      	"<div id=divHour class=caltimediv style='display:none;top:50;left:110'>";
      	for (i=0;i<12;i++){
			for (j=0;j<24;j+=12)
	        	content += "<span onClick='setTime("+(i+j)+",-1,-1)'>"+
	            	return0(i+j)+"</span>&nbsp;";
		        content += "<br>";
      	}
      	content+="</div></td>"+
      	"<td class=calto width=40 id=id_time valign=bottom>"+getTimeStr(ctime)+
      	"</td>"+
      	"<td class=calto width=8 valign=top>"+
      	"<img class=calarrow vspace=2 src='"+img_path+"ico_down.gif' "+
      	" onclick='showMinuteDiv()'>"+
      	"<div id=divMin class=caltimediv style='display:none;top:50;left:110'>";
      	for (i=0;i<10;i++){
       		for (j=0;j<60;j+=10)
            	content += "<span onClick='setTime(-1,"+(i+j)+",-1)'>"+
                    return0(i+j)+"</span>&nbsp;";
         		content += "<br>";
      		}
      	content+="</div></td>"+
      	"</td></tr></table>"+
      	"</td></tr></table>";
	}

   	//yoil
	content+= "<table width=100% cellspacing=0 cellpadding=0>\n"
	content=content + "<tr >\n"
	content=content + "<td width=10></td>\n"
	content=content + "<td class=calyoil >" + day_name[0] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[1] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[2] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[3] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[4] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[5] + "</td>\n"
	content=content + "<td class=calyoil >" + day_name[6] + "</td>\n"
	content=content + "<td width=10></td>\n"
	content=content + "</tr></table>\n"
	content+="</Td></Tr>\n";

   	//days
	content+= "<Tr><Td bgcolor='#F5F5F5'>\n"+
    "<table width=100% height=100% cellspacing=0 cellpadding=0 "+
    " onmouseover='hover(true)' onmouseout='hover(false)'  onclick='choose(" + y + "," + m + ")'> " + 
    "<tr><td width=10 datetitle=''></td>";
	var column = 0;

	for (i=1 ; i <= (firstday-1);i++){
		content+= "<td align=center class=calgray datetitle=''>"+ 
                        (DaysInBefMonth-(firstday-1)+i) +"</td>\n"
		column =column + 1
	}

	var rows=0;
	for( i=1 ; i<= DaysInMonth;i++){
		if(column ==0) content=content+"<td align=center class = cal03"
		else if(column ==6) content=content+"<td align=center class = cal02"
		else  content=content+"<td align=center class = cal01"
		if(i==day) content=content+" bgcolor='#E8E8E8' style='text-decoration:underline'"
		content = content + " datetitle=" + i + ">" + i;
		column  = column + 1
		content = content + "</td>\n"

		if(column == 7 ){
			content+="<td width=10 datetitle=''></td></tr>"+
                  "<tr><td width=10 datetitle=''></td>\n";
			column = 0;
         rows++;
		}
	}
	rows++;

	if((column > 0) && (column < 7))	{
		for (i=1 ;i <= (7-column) ; i++)
			content+="<td align=center class=calgray datetitle=''>"+i+"</td>\n"
	}

	content+="<td width=10 datetitle=''></td></tr>\n";
  	if (rows <=5) content+="<tr><td class=calgray datetitle=''></td></tr>\n";
	content+="</table>\n";
   	content+="</Td></Tr></Table>";
   	content+="</TD></TR>";
	content+="</TABLE>\n"
	content+="</FORM>\n"
	document.body.innerHTML = content;
}

</script>
</head>
<body style='background-color:#ffffff;margin:0;'>
</body>
</html>
