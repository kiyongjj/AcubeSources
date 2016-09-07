var full_month_name = new Array('1월','2월','3월','4월','5월','6월', '7월','8월','9월','10월','11월','12월');
var month_name = new Array('1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월');
var day_name = new Array('Su','Mo','Tu','We','Th','Fr','Sa');
var year_str = "년";
var day_str = "일";

var cur_id = null;
var cur_view = null;
var cur_datetype = null;
var cur_select = null;

var cur_x = 0;
var cur_y = 0;

function show_cal(obj_id,obj_view,datetype,select){
    cur_id = obj_id;
    cur_view = obj_view;
    cur_datetype = datetype;
    cur_select = select;

    try {
        cur_x = window.event.clientX;
        cur_y = window.event.clientY;
    }catch (e){}

    if (document.all.calendar.readyState == 4){
        document.all.calendar.style.width=215;
        document.all.calendar.style.height=222;

        if (cur_y + 10 + parseInt(document.all.calendar.style.height) >
                document.body.clientHeight) {
            document.all.calendar.style.top = document.body.clientHeight -
                    parseInt(document.all.calendar.style.height);
        }else {
            document.all.calendar.style.top = cur_y + 10;
        }

        if (cur_x - 100 + parseInt(document.all.calendar.style.width) >
                document.body.clientWidth) {
            document.all.calendar.style.left = document.body.clientWidth -
                    parseInt(document.all.calendar.style.width);
        }else {
            document.all.calendar.style.left = cur_x - 100;
        }

        document.all.calendar.style.display = 'block';
        document.all.calendar.full_month_name=full_month_name;
        document.all.calendar.month_name=month_name;
        document.all.calendar.day_name=day_name;
        document.all.calendar.year_str=year_str;
        document.all.calendar.day_str=day_str;
        document.all.calendar.datetype = cur_datetype;
        document.all.calendar.select = cur_select;
        document.all.calendar.calWidth = document.all.calendar.style.width;
        document.all.calendar.curDate = obj_id.value;
        showcal = true;
    }else {
        setTimeout("show_cal(cur_id,cur_view,cur_datetype,cur_select)", 100);
    }
}

function set_cal(id,view){
    document.all.calendar.style.display = 'none';
    if (id == "" || view == ""){
        return;
    }
    cur_id.value = id;
    cur_view.value = view;
}

function set_cal_new(id,view){
    document.all.calendar.style.display = 'none';
    if (id == "" || view == ""){
        return;
    }
    cur_id.value = id;
    //cur_view.value = view;
    cur_view.innerHTML = view;
    onListSearch('calendar');
}

function set_cal2(id,view,start_id,start,end_id,end){
    document.all.calendar.style.display = 'none';
    if (id == "" || view == ""){
        return;
    }
    cur_id.value = id;
    cur_view.value = view;

    if (cur_id == start_id){
        if (cur_id.value > end_id.value) {
            end_id.value = cur_id.value;
            end.value = cur_view.value;
        }
    }else if (cur_id == end_id){
        if (cur_id.value < start_id.value) {
            start_id.value = cur_id.value;
            start.value = cur_view.value;
        }
    }
}