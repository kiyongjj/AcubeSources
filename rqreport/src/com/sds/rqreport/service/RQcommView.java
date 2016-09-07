package com.sds.rqreport.service;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;
import com.sds.rqreport.model.*;

public class RQcommView extends TagSupport{
    private static final long serialVersionUID = 1L; 
    private String lm_strAction = "";
    private String lm_strDriver = "";
    private String lm_strConn = "";
    private String lm_strSid = "";
    private String lm_strSpass = "";
    private String lm_strSql = "";
    
    private int lm_iStmtidx;
    //private String lm_strResultSet = "";
    
    private boolean lm_bIsComplete  =   true;

    private Logger L = Logger.getLogger("RQCOMVIEW");
     
    /**
     * to  process start custom tag 
     * 
     * @return int
     */ 
    public int doStartTag() throws JspTagException{
        if(lm_strAction.equals("getRs")){
            getRqview();
            
        //}else if(lm_strAction.equals("login")){//login process and session process
            
        }else if(lm_strAction.equals("getTableInfo")){
            getTableList();   
        }else if(lm_strAction.equals("getColumn")){           
            getColumnInfo();
        }else{
            lm_bIsComplete  =   false;
        }
        return SKIP_BODY;
    }   
    
    /**
     *  to process end custom tag
     * 
     * @return int
     */
    public int doEndTag() throws JspTagException{
        
        HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse res = (HttpServletResponse)pageContext.getResponse();
        try {
            req.setCharacterEncoding("UTF-8");
            res.setContentType("text/html;charset=UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            L.error(e);
            StackTraceElement[] lm_ste = e.getStackTrace();
            //for(int i = 0 ; i < lm_ste.length; i++){
            //    L.error(lm_ste[i].toString());
            //}
        }
        if(lm_bIsComplete){
            return EVAL_PAGE;           
        }else{
            return SKIP_BODY;           
        }
    }  
    
    /**
     *  to set bean module 
     */
    public void setAction(String p_strAction){
        lm_strAction = p_strAction;
    }
    public void setDriver(String p_strDriver){
        lm_strDriver = p_strDriver;
    }
    public void setConn(String p_strConn){
        lm_strConn = p_strConn;
    }
    public void setSid(String p_strSid){
        lm_strSid = p_strSid;
    }
    public void setSpass(String p_strSpass){
        lm_strSpass = p_strSpass;
    }
    public void setSql(String p_strSql){
        lm_strSql = p_strSql;
    }
    public void setStmtidx(int p_iStmtidx){
        lm_iStmtidx = p_iStmtidx;
    }
    
    /**
    * JNDI값이 있을경우 서버에 설정된 JNDI를 사용하며 그렇지 않을경우 Direct Connect를 이용하여 DB에서 
	* 정보를 받아와 StringBuffer에 그 결과 값을 담는다. JNDI를 사용할경우 Cache-Manager를 사용할수 있으며
	* Cache-Manager를 사용할경우 key 값은 Query가 된다. 이전에 CacheContainer에 결과가 있는 Query를 요청할경우 
	* 서버는 직접 DB에 Access 하지 않고 CacheContainer 에 담겨있는 결과를 반환한다.
    * 
    * @return void 
    */
    public void getRqview(){
//   
//        long startTime = 0;
//        long endTime = 0;
//        startTime = System.currentTimeMillis();
//
//        String lm_strResultSet = "";
//        JspWriter out = pageContext.getOut(); 
//
//        RQcommController lm_strRQCommController = new RQcommController();
//        try {
//            //lm_strResultSet = lm_strRQCommController.getRQResultSet(lm_strSql, lm_strIp, lm_strDocName, lm_strRunvar);
//            ArrayList lm_oSqlArray = new ArrayList();
//            String[] lm_strSQL =  (pageContext.getRequest()).getParameter("sql").split(";");
//            for(int i=0; i<lm_strSQL.length;i++){
//                lm_oSqlArray.add(lm_strSQL[i]);
//            }
//            
//            if((pageContext.getRequest()).getParameter("jndiname") ==  null){
//                //use Hashtable (direct connect)
//                lm_strResultSet = lm_strRQCommController.getRQResult(lm_strDriver , lm_strConn, lm_strSid, lm_strSpass, lm_oSqlArray, lm_iStmtidx);
//            }else{
//                //use JNDI , Cachemanager
//                //lm_strResultSet = lm_strRQCommController.getRQResult((pageContext.getRequest()).getParameter("jndiname"),lm_oSqlArray);
//                for (int i=0; i<lm_strSQL.length;i++){
//                    //RQcacheContent lm_strReturn = lm_strRQCommController.getRQResult((pageContext.getRequest()).getParameter("jndiname"), lm_strSQL[i]);
//                    
//                    //get CacheManager Instance with Singleton
//                    CacheManager lm_CacheManager = CacheManager.getInstance();
//                    //set Cache-manager true or false
//                    boolean lm_flag = Boolean.valueOf((pageContext.getRequest()).getParameter("cacheman")).booleanValue();
//                    lm_CacheManager.setUseCache(lm_flag);
//                    L.info("use CacheManager : " + lm_flag);
//                    
//                    RQcacheContent lm_strReturn = lm_CacheManager.getContent((pageContext.getRequest()).getParameter("jndiname"), lm_strSQL[i]);
//                    lm_strResultSet += lm_strReturn.getReturnStrObject();
//                }
//            }
//
//            //lm_strResultSet = new String(lm_strResultSet.getBytes("ISO8859_1"),"UTF-8"); 
//            out.print(lm_strResultSet);
//            endTime = System.currentTimeMillis();
//            L.info("Response Time(millisecond) with browsing : " + (endTime-startTime));
//            //FileWriter fw = new FileWriter("D:/java/Tomcat5.0/webapps/WTF_ljg/WebContent/tmp/ds.txt");
//            //String strConPath  = pageContext.getServletContext().getRealPath("/tmp") + "/ds.txt";
//            //FileWriter fw = new FileWriter(strConPath);
//   
//            //BufferedWriter bw = new BufferedWriter(fw); 
//            //bw.write(lm_strResultSet); 
//            //bw.close(); 
//            
//            
//        } catch (IOException e) {
//            //e.printStackTrace();
//            L.error(e);
//        }
    }
    
    /**
     * DB에서 해당 스키마 정보를 가져온다.
     * @return void
     */
  
    public void getTableList() {
//        String lm_strResultSet = "";
//        JspWriter out = pageContext.getOut(); 
//        
//        String lm_sJndiname = (pageContext).getRequest().getParameter("jndiname");
//        String lm_strOwner = (pageContext.getRequest()).getParameter("strOwner");
//        String[] lm_strOption = (pageContext.getRequest()).getParameterValues("strOption");
//        
//        RQcommController lm_strRQCommController = new RQcommController();
//        lm_strResultSet = lm_strRQCommController.getRQTableInfo(lm_sJndiname, lm_strOwner, lm_strOption);
//
//        try {
//            //String lm_uResultSet = new String(lm_strResultSet.getBytes("ISO8859_1"),"UTF-8"); 
//            out.println(lm_strResultSet);
//        } catch (IOException e) {
//            //e.printStackTrace();
//            L.error(e);
//        }
    }
    
    /**
     * DB에서 해당 테이블의 column 정보를 받아온다.
     * @return void
     */
    public void getColumnInfo() {
//        String lm_strResultSet = "";
//        JspWriter out = pageContext.getOut(); 
//        
//        String lm_sJndiname = (pageContext).getRequest().getParameter("jndiname");
//        String lm_strTableName = (pageContext.getRequest()).getParameter("strTableName");
//        
//        RQcommController lm_strRQCommController = new RQcommController();
//        lm_strResultSet = lm_strRQCommController.getRQGetColumnInfo(lm_sJndiname, lm_strTableName);
//        
//        try {
//            out.println(lm_strResultSet);
//        } catch (IOException e) {
//            L.error(e);
//        }

    }
}