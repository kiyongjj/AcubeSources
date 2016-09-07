package com.sds.rqreport.service.web;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.*;
import java.util.*;
import java.text.*;

import com.oreilly.servlet.Base64Decoder;
import com.sds.rqreport.Environment;
import com.sds.rqreport.model.UserModel;
import com.sds.rqreport.repository.*;
import com.sds.rqreport.service.session.*;
import com.sds.rqreport.util.*;

public class RQUser extends TagSupport{

    private static final long serialVersionUID = 1L;
    private String m_strAction = "";
    private String m_strUsertype = "";
    private boolean m_bIsComplete = false;
    private int iParamScope = PageContext.SESSION_SCOPE;
    private PageContext pCFromcs;
    private Iterator iterator;
    private ArrayList oArrUMlist;
    private Logger log = Logger.getLogger("RQWEB");
    public String m_RQCharset = "";
    public String m_ServerCharset = "";

    public static UserInfo userinfo = null;

    public RQUser(){}
    public RQUser(PageContext pCFromcs){
    	this.pCFromcs = pCFromcs;
    }
    /**
     * to  process start custom tag
     */
    public int doStartTag() throws JspTagException{

    	Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;

        if(m_strAction.equals("login")){
            if(loginUser()){
                m_bIsComplete = true;
            }else{
            	m_bIsComplete = true;
            }
        }else if(m_strAction.equals("checksession")){
            if(checkSession()){
                m_bIsComplete = true;
                return EVAL_BODY_INCLUDE;
            }
        }else if(m_strAction.equals("checkmysingle")){
            if(mysingleCheck()){
                m_bIsComplete = true;
            }else{
            	m_bIsComplete = true;
            }
        }else if(m_strAction.equals("checkrqsession")){
            if(rqSessionCheck()){
                m_bIsComplete = true;
            }else{
            	m_bIsComplete = true;
            }
        }else if(m_strAction.equals("loadL")){
            if(loadL()){
                m_bIsComplete = true;
            }else{
            	m_bIsComplete = true;
            }
        }else if(m_strAction.equals("addUser")){
            addUI();
        }else if(m_strAction.equals("getUserList")){
        	getUserList();
        	m_bIsComplete = true;
        }else if(m_strAction.equals("getOResultArr")){
        	getOResultArr();
        	m_bIsComplete = true;
        }else if(m_strAction.equals("del")){
        	deleteUsers();
        	m_bIsComplete = true;
        }else if(m_strAction.equals("modifyuser")){
        	modifyuser();
        	m_bIsComplete = true;
        }else if(m_strAction.equals("ssologon")){
        	ssologon();
        	m_bIsComplete = true;
        }else if(m_strAction.equals("sessionLogOut")){
        	sessionLogOut();
        	m_bIsComplete = true;
        }else{
            m_bIsComplete = false;
        }
        return SKIP_BODY;
    }

	/**
     * to process end custom tag
     */
    public int doEndTag() throws JspTagException{
        if(m_bIsComplete){
            return EVAL_PAGE;
        }else{
            return SKIP_PAGE;
        }
    }

    /**
     * to set bean module
     * @param p_strAction
     */
    public void setAction(String p_strAction){
        m_strAction = p_strAction;
    }
    
    public void setUsertype(String p_strUsertype){
        m_strUsertype = p_strUsertype;
    }

    /**
     * login
     * @return
     */
    public boolean loginUser(){

    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
    	HttpSession session = ((HttpServletRequest)pageContext.getRequest()).getSession(true);

    	MDC.put("userip", request.getRemoteAddr());

        String lm_strUserid = setParameterString("strUserid", pageContext);
        String lm_strUserpw = setParameterString("strUserpw", pageContext);

        String strContextPath = request.getContextPath();
        if(!strContextPath.equals("") && !strContextPath.equals("/")){
            String lm_ContextPath = strContextPath.substring(1);
            String strRequestURI = request.getRequestURI();
            String lm_RequestURI = strRequestURI.substring(1, strRequestURI.length()-1);
            if(lm_ContextPath.equals(lm_RequestURI)){
            	if(session.getAttribute("UM") != null){
            		RequbeUtil.do_SendRedirect(pageContext, "document/document.jsp");
            	}
            }
        }
        /////////// userType 추가 //////////////////////////////////////////////////////
        if(m_strUsertype != null && !m_strUsertype.equals("")){
        	// "A" 를 통해 넘어올경우 admin session을 생성시킨다.
        	if(m_strUsertype.equals("A")){
        		lm_strUserid   = "admin";
        	}
        }else{
        	// null check usertype
        	// default value = "RQA"
        	m_strUsertype = "RQA";
        }
        ///////////////////////////////////////////////////////////////////////////////
        
        if(lm_strUserid == null || lm_strUserid.equals("")){
            return false;
        }else{
        	loginUserAct(session, lm_strUserid, lm_strUserpw);
        }
        return false;
    }

    /**
     * real login Action.
     * @param session
     * @param p_strUserid
     * @param p_strUserpw
     */
    private void loginUserAct(HttpSession session, String p_strUserid, String p_strUserpw){

    	String lm_strUserid = p_strUserid;
    	String lm_strUserpw = p_strUserpw;

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	log.debug("userid:" + lm_strUserid);
    	String lm_strResult = "";
    	
    	// usertype 추가에 따른 처리 ///////////////////////////////////////////
    	if(m_strUsertype.equals("A")){
    		lm_strResult = "TRUE";
    	}else{
    		lm_strUserpw = Base64Decoder.decode(lm_strUserpw);
    		lm_strResult = loginCheck(lm_strUserid, lm_strUserpw); 
    	}
        //////////////////////////////////////////////////////////////////////
    	
        if(lm_strResult.equals("TRUE")){
            UserModel lm_oUserModel;
            //HttpSession lm_oSession = ((H ttpServletRequest)pageContext.getRequest()).getSession(true);
            lm_oUserModel = getUserModel(lm_strUserid);
            session.setAttribute("UM",(Object)lm_oUserModel);
            RequbeUtil.do_SendRedirect(pageContext, "document/document.jsp");
        }else{
            JspWriter out = pageContext.getOut();
            try {
            	String msg = (String) rqresource.ht.get("rquser.loginuser.loginfail");
                out.println("<script language='javascript'>" +
                		        "    alert('" + msg + "');" +
                				"</script>");
            } catch (IOException e) {
            	RequbeUtil.do_PrintStackTrace(log, e);
            }
        }
    }

    /**
     * check session
     * @return
     */
    public boolean checkSession(){

        JspWriter out = pageContext.getOut();
        HttpSession session = ((HttpServletRequest)pageContext.getRequest()).getSession(true);

        Object UM = session.getAttribute("UM");
        try{
        	if(UM != null){
                return true;
            }else{
            	out.println("<script language='Javascript'>alert('invalid session!');history.back();</script>");
                return false;
            }
        }catch(Exception e){
        	session.removeAttribute("UM");
        }
        return false;
    }

    /**
     * check session
     * @return
     */
    public void checkSession2(){
    	HttpSession lm_oSession = ((HttpServletRequest)pCFromcs.getRequest()).getSession(true);
        try{
        	if(pCFromcs.getAttribute("UM", iParamScope) != null){
                //UserModel lm_oUserModel    =   (UserModel)lm_oSession.getAttribute("UM");
                //String lm_Userid = lm_oUserModel.getUserid();
        		pCFromcs.setAttribute("UMCondition", "1", iParamScope);
            }else{
            	pCFromcs.setAttribute("UMCondition", "-1", iParamScope);
            }
        }catch(Exception err){
            lm_oSession.removeAttribute("UMCondition");
        }
    }

    /**
     * setting parameter
     * @param p_strParam
     * @param p_oPageContext
     * @return
     */
    public String setParameterString(String p_strParam , PageContext p_oPageContext){
        Map lm_oParamMap = ((HttpServletRequest)p_oPageContext.getRequest()).getParameterMap();
        Object[] lm_eParam = (lm_oParamMap.keySet()).toArray();

        Vector lm_vParam = new Vector(Arrays.asList(lm_eParam));

        if(lm_vParam.size() < 0){
            return null;
        }

        try{
            if(lm_vParam.contains(p_strParam)){
                String[] lm_strParamArray = (String[])lm_oParamMap.get(p_strParam);
                if(lm_strParamArray != null && !lm_strParamArray[0].equals("")){
                    if(lm_strParamArray.length == 1)
                        return Encoding.chCharset(lm_strParamArray[0], m_ServerCharset, m_RQCharset);
                    else
                        return null;
                }else{
                    return null;
                }
            }else{
                return null;
            }

        }catch(Exception e){
        	RequbeUtil.do_PrintStackTrace(log, e);
            return null;
        }
    }

    /**
     * 사용자 로그인체크를 한다. 등록된 사용자 일경우 String 값 "TRUE"를 아닐경우 "FALSE"를 반환한다.
     * @param p_strUserid - 사용자 아이디
     * @param p_strUserpw - 사용자 암호
     * @return "TRUE" or "FALSE" 을 반환.
     */
    public String loginCheck(String p_strUserid, String p_strUserpw){

    	try {
			UserRepository ur = new UserRepository();
			int res = ur.verifyPassword(p_strUserid, p_strUserpw);
			if(res == 0){
				return "TRUE";
			}else{
				return "FALSE";
			}

		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(log, e);
			return "FALSE";
		}
//        if(p_strUserid.equals("admin") && p_strUserpw.equals("admin")){
//            return "TRUE";
//        }else{
//            return "FALSE";
//        }

    }

    /**
     * 사용자 정보를 가져온다.
     * @param p_strUserid - 사용자 아이디
     * @return 사용자 정보를 담고 있는 bean 을 반환.
     */
    public UserModel getUserModel(String p_strUserid){
    	UserModel lm_oUserModel = new UserModel();
        RepositoryEnv env = Environment.getRepositoryEnv();
        String auth = "G";
        // 일반 사용자와 관리자에 대한 권한 설정 
        if(userinfo != null){
        	auth = userinfo.auth;
        }
        // sso 로그인에 대한 권한 설정 
        if(p_strUserid.equals("SSO")){
        	auth = "A";
        }
        // installid 에 대한 권한 설정 
		if(env.installid.equals(p_strUserid)){
	        auth = "A";
		}
		// usertype 추가에 따른 처리 ///////////////////////////////////////////
		if(m_strUsertype.equals("A")){
			auth = "A";
		}
		/////////////////////////////////////////////////////////////////////
        lm_oUserModel.setUserid(p_strUserid);
        lm_oUserModel.setAuth(auth);

        //설정파일에 rqreport.server.extraModule1=RQschedule 이면 rqschedule 권한을 만든다.
        //추후에 이러한 특성을 가진 페이지가 있을경우 이런식으로 추가해준다.
        Environment environ = Environment.getInstance();
        String lm_useSchedule = (environ.useSchedule).trim();
        if(lm_useSchedule.equalsIgnoreCase("RQschedule")){
        	lm_oUserModel.setPrivilegePage("scheduler");
        }
        //문서통계가 추가됨 // 2009.09.03
        String lm_useRQStatistics = (environ.useRQStatistics).trim();
        if(lm_useRQStatistics.equalsIgnoreCase("RQStatistics")){
        	lm_oUserModel.setPrivilegePage("RQStatistics");
        }
        
        return lm_oUserModel;
    }

    public boolean rqSessionCheck()
    {
    	HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    	String sessionid;
		SessionClient sc = new SessionClient(request, null);
		sc.putServer("127.0.0.1");
		sc.putPort(7005);
		sessionid = request.getParameter("rqsessionid");
		if(sessionid != null && sessionid.length() > 0)
		{
			sc.putSessionID(sessionid);
		}
		else
		{
			sc.makeSession(1800000);
		}
		String userid  = sc.getParameter("id");
		if(userid != null)
			return true;
		else
			return false;
    }

    public boolean mysingleCheck()
    {
    	HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    	HttpSession session = request.getSession(true);
    	String strTotalData = "";
    	strTotalData = request.getParameter("totaldata");

    	session.setMaxInactiveInterval(30*60); //sessionTimeout : 30 min

    	if(strTotalData != null){
    		if((String) pageContext.getAttribute("SSO", iParamScope) == null){
    			pageContext.setAttribute("SSO", strTotalData , iParamScope);
    			log.debug("SSO : " + strTotalData );
    		}
    	}

    	if(strTotalData == null || strTotalData.length() < 10)
    	{
    		strTotalData = (String)session.getAttribute("MYSINGLE_TOTALDATA");
    	}
    	else
    	{
    		session.setAttribute("MYSINGLE_TOTALDATA", strTotalData);
    	}

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
  //  	out.println("<<site_connect_ocx2>> Login IP "+request.getRemoteAddr());
 //   	out.println("<BR>");
 //   	out.println("> User Info is [" +userInfo+ "][" +userInfo.length()+ "]");
 //   	out.println("<BR><BR>");
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
//        				out.println("LOGIN ERROR!!!!");
  //  				out.println("<BR>");
    				//break;
        			return false;
    			}
        			if (infoKey.equals("EP_LOGINIP"))		// Get LOGIN IP
        			    trayLoginIP = infoValue.equals("=")?"":infoValue.substring(infoValue.indexOf("=")+1,infoValue.length( ));
        			if (infoKey.equals("EP_LOGINTIMEFORMIS"))	// Get LOGIN Time
        			    trayLoginTime = infoValue.equals("=")?"":infoValue.substring(infoValue.indexOf("=")+1,infoValue.length( ));
    		}
    	}


        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    	formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date beginDate = null;
		try {
			beginDate = formatter.parse(trayLoginTime);
		} catch (ParseException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
			return false;
		}
    	Date endDate = new Date(System.currentTimeMillis());
    	long Diff = (endDate.getTime() - beginDate.getTime()) / (60 * 60 * 1000);

//    	out.println("LoginTime Check !!<BR>");
//    	out.println("EP_LOGINTIMEFORMIS: " +trayLoginTime + "<BR>");
//    	out.println("EP_SSO_Date :" + beginDate+ "<BR>");
//    	out.println("System Time :" + endDate+ "<BR>");
//    	out.println("Differ (Hours):" + Diff);
//    	out.println("<BR>");

            if(Diff < 6){
//    		out.println("LoginTime Check OK !!");
//    		out.println("<BR><BR>");
    	}
    	else{
//    		out.println("LoginTime Check Fail !!");
//    		out.println("<BR><BR>");
    		return false;
    	}


    	if(remoteIP.equals(trayLoginIP)){
//    		System.out.println("<<site_connect_ocx2>> IP Check OK !!");
//    		out.println("IP Check OK !!");
//    		out.println("<BR>");

    	}
    	else{
//    		System.out.println("<<site_connect_ocx2>> IP Check Fail !!");
//    		out.println("IP Check Fail !!");
//    		out.println("<BR>");
    		return false;
    	}
    	return true;
    }

    public void addUI(){
    	JspWriter out = pageContext.getOut();

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	int res = addUser();
    	String msg = "";
    	if(res != 0){
    		msg = (String) rqresource.ht.get("rquser.addui.adduser.error");
    		try{
    			out.println("<script language='javascript'>" +
    					"alert('" + msg + "');" +
    					"self.close();" +
    					"</script>");
    		}catch(IOException ie) {
    			RequbeUtil.do_PrintStackTrace(log, ie);
    		}
    	}else if(res == 0){
    		msg = (String) rqresource.ht.get("rquser.addui.adduser.success");
    		try{
    			out.println("<script language='javascript'>" +
    					"alert('" + msg + "');" +
    					"opener.document.location.reload();" +
    					"self.close();" +
    					"</script>");
    		}catch(IOException ie) {
    			RequbeUtil.do_PrintStackTrace(log, ie);
    		}
    	}
    }

    public int addUser(){

    	String id = "";
    	String pw = "";
    	String email = "";
    	String desc = "";
    	String auth = "";

    	id = (String)(pageContext.getRequest()).getParameter("id").trim();
    	pw = (String)(pageContext.getRequest()).getParameter("pw").trim();
    	desc = (String)(pageContext.getRequest()).getParameter("desc").trim();
    	auth = (String)(pageContext.getRequest()).getParameter("aut").trim();

		ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
		Enumeration lm_oenm = lm_oRb.getKeys();
		String lm_tmp = "";
		while(lm_oenm.hasMoreElements()){
			lm_tmp = (String) lm_oenm.nextElement();
			if(lm_tmp.equals("rqreport.server.charset")){
				m_ServerCharset = lm_oRb.getString("rqreport.server.charset");
			}
		}

		id = Encoding.chCharset(id, m_ServerCharset, m_RQCharset);
    	pw = Encoding.chCharset(pw, m_ServerCharset, m_RQCharset);
    	desc = Encoding.chCharset(desc, m_ServerCharset, m_RQCharset);

    	int res = 0;
    	UserInfo oUI = new UserInfo();
    	oUI.id = id;
    	oUI.pw = pw;
    	oUI.email = email;
    	oUI.desc = desc;
    	oUI.auth = auth;

    	try{
    		UserRepository userRepository =  new UserRepository();
        	res = userRepository.addUser(oUI);
    	}catch(Exception e){
    		RequbeUtil.do_PrintStackTrace(log, e);
    		res =  -1;
    	}
    	return res;
    }

    public boolean getUserList(){
    	try {
			UserRepository ouserRepository = new UserRepository();
			ArrayList lm_oArrUMlist = new ArrayList();
			ouserRepository.getUsers("DEFAULT", lm_oArrUMlist);
			oArrUMlist = (ArrayList) lm_oArrUMlist.get(0);
			pageContext.setAttribute("oUserList", oArrUMlist);
		} catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(log, e);
			return false;
		}
		return true;
    }

    public void getOResultArr(){

    	int iListCount = 10;

    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	ArrayList oUserList = (ArrayList) pageContext.getAttribute("oUserList");

    	String strCurrentPage = "1";
    	if(request.getParameter("strCurrentPage") != null && !request.getParameter("strCurrentPage").equals("")) {
    		strCurrentPage = request.getParameter("strCurrentPage");
    	}else{
    		strCurrentPage = "1";
    	}


    	int iCurrentPage = Integer.parseInt(strCurrentPage);
    	int m_i = 0,  m_j = 0;

    	ArrayList oResultArr = new ArrayList();
    	Iterator it = oUserList.iterator();
    	if (iListCount != oUserList.size()){

    		while(it.hasNext()){
    			m_i++;
    			UserInfo lm_di = (UserInfo)it.next();
    			if(  m_i > (iCurrentPage-1)*iListCount && m_i <= iCurrentPage*iListCount ){
    				m_j++;
    				oResultArr.add(lm_di);
    			}
    			if(m_j==iListCount) break;
    		}

    		if(oResultArr.size() == 0){
    			Iterator it_b = oUserList.iterator();
    			iCurrentPage = iCurrentPage - 1;
    			int k = 0;
    			int l = 0;
    			while(it_b.hasNext()){
    				k++;
    				DocInfo lm_di_b = (DocInfo) it_b.next();
    				if( k > (iCurrentPage - 1) * iListCount && k <= iCurrentPage*iListCount){
    					l++;
    					oResultArr.add(lm_di_b);
    				}
    				if(l==iListCount) break;
    			}
    		}

    	}else{

    		oResultArr = oUserList;

    	}
    	pageContext.setAttribute("oResultArr", oResultArr);

    }

    public ArrayList getList(){
    	return oArrUMlist;
    }

    public void deleteUsers(){
    	int res = 0;
    	res = delUsers();
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
    	JspWriter out = pageContext.getOut();

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	try {
	    	if(res == 0){
	    		RequbeUtil.do_SendRedirect(pageContext, request.getHeader("referer"));
	    	}else if(res == -1){
	    		String msg = (String) rqresource.ht.get("rquser.deleteusers.error");
	    		out.println("<script language='javascript'>" +
	    				"alert('" + msg + "');" +
	    				"history.back();" +
	    				"</script>");
	    	}
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
    }

    private int delUsers() {
    	int res = 0;
    	String[] chkuser = (pageContext.getRequest()).getParameterValues("chkuser");
    	String isChkuser = "";
    	for(int i = 0 ; i < chkuser.length ; i++ ){
    		try {
    			isChkuser = Encoding.chCharset(chkuser[i], m_ServerCharset, m_RQCharset);
    			UserRepository ouserRepository = new UserRepository();
    			res = ouserRepository.deleteUser(isChkuser);
			} catch (UnsupportedEncodingException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			} catch (Exception e){
				RequbeUtil.do_PrintStackTrace(log, e);
				res = -1;
			}
    	}
    	return res;
	}

    private int modifyuser(){
    	int res = 0;
    	JspWriter out = pageContext.getOut();
    	res = modUser();

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	try{
    		String msg = "";
    		if(res ==0 ){
    			msg = (String) rqresource.ht.get("rquser.modifyuser.success");
    			out.println("<script language='javascript'>" +
    					"alert('" + msg + "');" +
    					"opener.document.location.reload();" +
    					"self.close();" +
    					"</script>");
    		}else if(res != 0){
    			msg = (String) rqresource.ht.get("rquser.modifyuser.error");
    			out.println("<script language='javascript'>" +
    					"alert('" + msg + "');" +
    					"self.close();" +
    					"</script>");
    		}

    	}catch(IOException e){
    		RequbeUtil.do_PrintStackTrace(log, e);
    	}
    	return res;
    }

    private int modUser(){

    	String id = "";
    	String pw = "";
    	//String email = "";
    	String aut = "G";
    	String desc = "";

    	id = (String)(pageContext.getRequest()).getParameter("id").trim();
    	pw = (String)(pageContext.getRequest()).getParameter("pw").trim();
    	aut = (String)(pageContext.getRequest()).getParameter("aut");
    	desc = (String)(pageContext.getRequest()).getParameter("desc").trim();
    	
    	id = Encoding.chCharset(id, m_ServerCharset, m_RQCharset);
    	pw = Encoding.chCharset(pw, m_ServerCharset, m_RQCharset);
    	desc = Encoding.chCharset(desc, m_ServerCharset, m_RQCharset);
    	
    	//String changepw = (String)(pageContext.getRequest()).getParameter("pwchange");
    	
    	int res = 0;
    	UserInfo oUI = new UserInfo();
    	oUI.id = id;
    	oUI.pw = pw;
    	oUI.auth = aut;
    	oUI.desc = desc;

    	try{
    		UserRepository userRepository =  new UserRepository();
        	res = userRepository.updateUser(oUI, "");
    	}catch(Exception e){
    		RequbeUtil.do_PrintStackTrace(log, e);
    		res =  -1;
    	}
    	return res;
    }

    /**
     * SSO (Single Sign On)
     *
     */
    public void ssologon(){
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	String contextPath = request.getContextPath();

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	UserModel lm_oUserModel;
    	// 기존 lm_oUserModel = getUserModel("SSO"); 에서
    	// usertype 추가에 따른 변경.
        lm_oUserModel = getUserModel("SSO");
        HttpSession lm_oSession = ((HttpServletRequest)pageContext.getRequest()).getSession(true);
        lm_oSession.setAttribute("UM",(Object)lm_oUserModel);
        lm_oSession.setAttribute("UMCondition", "1");
        JspWriter out = pageContext.getOut();
        try {
        	String msg = (String) rqresource.ht.get("rquser.ssologon.ssologin");
    		out.println("<script language='javascript'>alert('" + msg + "')</script>");
   			out.println("<script language='javascript'>document.location.href='" + contextPath + "/document/document.jsp';</script>");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}

    	//return res;
    }

    /**
     * session Log Out Process
     *
     */
    public void sessionLogOut(){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		//String lm_LogOut	=	request.getParameter("strLogOut");

		HttpSession lm_oSession = ((HttpServletRequest)pageContext.getRequest()).getSession(true);
		lm_oSession.removeAttribute("UM");
		lm_oSession.removeAttribute("UMCondition");
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

		//RequbeUtil.do_SendRedirect(pageContext, basePath + "index.jsp");
		RequbeUtil.do_SendRedirect(pageContext, "../index.jsp");
    }

    public String getPageUse(ArrayList p_arrPage, String p_pageName){
    	String rtn = "false";
		String lm_strPage = "";
		for(int arP = 0 ; arP < p_arrPage.size() ; arP++){
			lm_strPage = (String) p_arrPage.get(arP);
			if(lm_strPage.equals(p_pageName)){
				rtn = "true";
			}
		}
    	return rtn;
    }

    /**
     * 라이센스를 로딩한다.
     * @return
     */
    public boolean loadL(){
    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    	JspWriter out = pageContext.getOut();
    	RQLCheckMain olcheckmain = new RQLCheckMain();
    	olcheckmain.loadLicense(request);

    	RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();

    	RQLSingleton oRQSingleton = RQLSingleton.getRQLicense(true);
    	int leftday = oRQSingleton.getLeftDays();
    	try{
    		if(!RQLSingleton.isStatus() && leftday >= 0){
        		String msg = (String) rqresource.ht.get("rquser.loadl.licenseleft");
    			String msg2 = (String) rqresource.ht.get("rquser.loadl.licenseleft2");
    			String msg3 = (String) rqresource.ht.get("rquser.loadl.callcenter");
        		out.write("alert(\"" + msg + " '" + (leftday+1) + "' " + msg2 + "\\n" + msg3 + "\")");
        	}else if(!RQLSingleton.isStatus() && leftday < 0){
        		String msg4 = (String) rqresource.ht.get("rquser.loadl.licenseend");
        		String msg5 = (String) rqresource.ht.get("rquser.loadl.callcenter");
        		out.write("alert(\"" + msg4 + "\\n" + msg5 + "\")");
        		out.write("return;");
        	}
    	}catch(IOException e){
    		RequbeUtil.do_PrintStackTrace(log, e);
    	}
    	return true;
    }
}
