<%@ page language="java" contentType="text/html;charset=UTF-8" %><%@ page import="java.net.*" %><%@ page import="java.io.*" %><%!
public  void send(HttpURLConnection p_con, String p_writeMsg) throws IOException {
	OutputStream out = p_con.getOutputStream();
	out.write(p_writeMsg.getBytes("UTF-8"));
}

public String read(HttpURLConnection p_con) throws IOException {
	DataInputStream dis = new DataInputStream(p_con.getInputStream());
    int c;
    StringBuffer buf = new StringBuffer();
    while ((c = dis.read()) != -1) {
    	buf.append((char)c);
   	}
    return buf.toString(); // no apply trim to DATASET
}
%><%
/**
 * 이페이지는 업무단에서 사용되어지는 페이지 이며 중계서버를 통해 REQUBE호출시 사용.
 * 이 영역에 업무단 Session에 대한 처리 로직이 들어가야  함.
 * 또한 이파일에 대한 수정 및 다른용도로 사용 하면 안됨.
 **/
////////////// 환경에 맞게 경로 수정 /////////////////////////////////
String strURL = "http://localhost:8080/rqreport/RQDataset.jsp";
/////////////////////////////////////////////////////////////////

//////////////// 이 밑으로는 REQUBE SPEC. 이므로 수정하지 말것 ////////////////////////////////////
String action = request.getParameter("action") == null ? "" : request.getParameter("action");
String doc = request.getParameter("doc") == null ? "" : request.getParameter("doc");
String runvar = request.getParameter("runvar") == null ? "" : request.getParameter("runvar");
String encqry = request.getParameter("encqry") == null ? "" : request.getParameter("encqry");

String args = "action=getRsDB&doc=";
args += doc;
args += "&runvar=";
args += runvar;
args += "&encqry=";
args += encqry;

//debugging point !!!! /////////////////////////////////////////////////
URL url = new URL(strURL);
HttpURLConnection conn = (HttpURLConnection)url.openConnection();
conn.setDoInput(true);
conn.setDoOutput(true);
conn.setUseCaches(false);
conn.setRequestMethod("POST");
conn.setRequestProperty("Connection","Keep-Alive");
conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
send(conn, args);

// debugging point !!!! /////////////////////////////////////////////////
if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
	String result = read(conn);
	out.print(new String(result.getBytes("8859_1"),"UTF-8"));
}else{
	out.print("DataSet Return ERROR");
}
%>