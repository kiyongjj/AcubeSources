package com.sds.rqreport.service.web;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.TagSupport;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.RQDocStatParam;
import com.sds.rqreport.model.RQDocStatTB;
import com.sds.rqreport.util.Encoding;


public class RQDocStatHandle extends TagSupport{

	private static final long serialVersionUID = 1L;
	private String m_strAction = "";
	private boolean bIsComplete = false;
	private String m_RQCharset = "";
	private String m_ServerCharset = "";
	private JspWriter out = null;
	
	public int doStartTag() throws JspTagException{
		
		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;
		
		if(m_strAction.equals("getDocStatInfo")){
            if(getDocStatInfo()){
                bIsComplete = true;
            }
        }else{
            bIsComplete = false;
        }
		return SKIP_BODY; //to doEndTag()
	}

	public int doEndTag() throws JspTagException{

        if(bIsComplete){
            return EVAL_PAGE;
        }else{
            return SKIP_PAGE;
        }
    }
	
    public void setAction(String p_strAction){
        m_strAction = p_strAction;
    }
    
	private boolean getDocStatInfo() {
		out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		//HttpServletResponse reponse = (HttpServletResponse)pageContext.getResponse();
		
		String strSearchcon = request.getParameter("strSearchcon");     // not null and ANSI //searchday, searchmonth
        String strBasesearch = request.getParameter("strBasesearch");    // not null and ANSI//baseday, basedoc
        String strDuringstart = request.getParameter("strDuringstart"); // ANSI
        String strDuringend = request.getParameter("strDuringend");     // ANSI
        String strDocsearch = request.getParameter("strDocsearch");     // ANSI or KOR 
		strDocsearch = Encoding.chCharset(strDocsearch, m_ServerCharset, m_RQCharset);
		
		RQDocStatParam rqparam = new RQDocStatParam();
		rqparam.setStrSearchcon(strSearchcon); 
		rqparam.setStrBasesearch(strBasesearch);
		rqparam.setStrDuringstart(strDuringstart);
		rqparam.setStrDuringend(strDuringend);
		rqparam.setStrDocsearch(strDocsearch);
		
		RQDocStatDAOImpl daoimpl = new RQDocStatDAOImpl();
		
		ArrayList rslist = daoimpl.selectDocStatInfo(rqparam);
		Iterator iter = rslist.iterator();
		RQDocStatTB tb = null;
		
		StringBuffer rtnxml = new StringBuffer();
		rtnxml.append("<DOCSTAT>");
		if(rslist.size() != 0){
			while(iter.hasNext()){
				tb = (RQDocStatTB) iter.next();
				rtnxml.append("<DOCINFO>")
					  .append("<RUNTIME>" + tb.getRUN_TIME()+ "</RUNTIME>")
					  .append("<FILE_NM>" +tb.getFILE_NM()+ "</FILE_NM>")
					  .append("<RUNCNT>" +tb.getRUNCNT()+ "</RUNCNT>")
					  .append("<SERVERTIME_AVE>" +tb.getSERVERTIME_AVE()/1000.0+ "</SERVERTIME_AVE>")
					  .append("<TOTALTIME_AVE>" +tb.getTOTALTIME_AVE()/1000.0+ "</TOTALTIME_AVE>")
					  .append("<MAXTIME>" +tb.getMAXTIME()/1000.0+ "</MAXTIME>")
					  .append("<MINTIME>" +tb.getMINTIME()/1000.0+ "</MINTIME>")
					  .append("<ERROR_CNT>" +tb.getERROR_CNT()+ "</ERROR_CNT>")
					  .append("</DOCINFO>");
			}
		}else{
			rtnxml.append("<DOCINFO>")
				  .append("<RUNTIME></RUNTIME>")
				  .append("<FILE_NM></FILE_NM>")
				  .append("<RUNCNT></RUNCNT>")
				  .append("<SERVERTIME_AVE></SERVERTIME_AVE>")
				  .append("<TOTALTIME_AVE></TOTALTIME_AVE>")
				  .append("<MAXTIME></MAXTIME>")
				  .append("<MINTIME></MINTIME>")
				  .append("<ERROR_CNT></ERROR_CNT>");
			rtnxml.append("</DOCINFO>");
		}
		rtnxml.append("</DOCSTAT>");
		try {
			out.print(rtnxml.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
}
