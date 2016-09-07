<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ep.*,java.text.*,java.util.*,com.sds.rqreport.*;"%>
                 

<%
	String strTotalData = request.getParameter("totaldata");

	StringTokenizer token = new StringTokenizer(strTotalData , ";");
	String strNewDataList = token.nextToken();
	String strMD5SecureKey = token.nextToken();
	String strKeyFolder = token.nextToken();	


	Environment env = Environment.getInstance();
	//byte[] baPublicKey = ep.Utils.getPublicKey("c:/eptray/" + strKeyFolder + "/mySingle_key");
	byte[] baPublicKey = ep.Utils.getPublicKey(env.mysinglekey );
	String userInfo = ep.EpTrayUtil.DecryptDataList(new String(baPublicKey),strMD5SecureKey,strNewDataList);

	/***********************************/
	/**		check IP					   **/
	/***********************************/
	out.println("<<site_connect_ocx2>> Login IP "+request.getRemoteAddr());
	out.println("<BR>");
	out.println("> User Info is [" +userInfo+ "][" +userInfo.length()+ "]");
	out.println("<BR><BR>");
	String remoteIP = request.getRemoteAddr();
	String trayLoginIP = "";
	String trayLoginTime = "";

	StringTokenizer st = new StringTokenizer(userInfo, ";"); //in case of DataList, you should delimit semicolon
    	for (;st.hasMoreTokens( );)
    	{
		String info = st.nextToken ( );
    		if ( info != null )
    		{
    			String infoKey = info.substring ( 0, info.indexOf ( "=" ) ).trim ( ); // 
    			String infoValue = info.substring ( info.indexOf ( "=" ), info.length ( ) ).trim ( ); // 
    			if (infoKey.equals("EP_RETURNCODE") && infoValue.equals("=0")){		// Get EP_RETURNCODE
    				out.println("LOGIN ERROR!!!!");
				out.println("<BR>");
				break;
			}
    			if (infoKey.equals("EP_LOGINIP"))		// Get LOGIN IP
    			    trayLoginIP = infoValue.equals("=")?"":infoValue.substring(infoValue.indexOf("=")+1,infoValue.length( ));
    			if (infoKey.equals("EP_LOGINTIMEFORMIS"))	// Get LOGIN Time
    			    trayLoginTime = infoValue.equals("=")?"":infoValue.substring(infoValue.indexOf("=")+1,infoValue.length( ));
		}
	}


        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date beginDate = formatter.parse(trayLoginTime);
	Date endDate = new Date(System.currentTimeMillis());
	long Diff = (endDate.getTime() - beginDate.getTime()) / (60 * 60 * 1000);

	out.println("LoginTime Check !!<BR>");
	out.println("EP_LOGINTIMEFORMIS: " +trayLoginTime + "<BR>");
	out.println("EP_SSO_Date :" + beginDate+ "<BR>");
	out.println("System Time :" + endDate+ "<BR>");
	out.println("Differ (Hours):" + Diff);
	out.println("<BR>");

        if(Diff < 6){
		out.println("LoginTime Check OK !!");
		out.println("<BR><BR>");
	}
	else{
		out.println("LoginTime Check Fail !!");
		out.println("<BR><BR>");
	}


	if(remoteIP.equals(trayLoginIP)){
//		System.out.println("<<site_connect_ocx2>> IP Check OK !!");
		out.println("IP Check OK !!");
		out.println("<BR>");

	}
	else{
//		System.out.println("<<site_connect_ocx2>> IP Check Fail !!");
		out.println("IP Check Fail !!");
		out.println("<BR>");
	}
%>

