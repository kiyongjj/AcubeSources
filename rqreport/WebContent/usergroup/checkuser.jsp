<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.sds.rqreport.repository.*"%>
<%
request.setCharacterEncoding("UTF-8");
String chkid = request.getParameter("chkid");

UserRepository ouserRepository = new UserRepository();
ArrayList lm_oArrUMlist = new ArrayList();
ouserRepository.getUsers("DEFAULT", lm_oArrUMlist);
ArrayList oArrUMlist = (ArrayList) lm_oArrUMlist.get(0);

UserInfo ui = null;
String userid = "";
int res = 0;
for(int i = 0; i < oArrUMlist.size(); i++) {
	ui = (UserInfo)oArrUMlist.get(i);
	userid = ui.getParamString(0);
	if(chkid.equals(userid)){
		res = -1;
	}
}
out.println(res);
%>