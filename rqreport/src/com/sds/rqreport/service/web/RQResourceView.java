package com.sds.rqreport.service.web;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.sds.rqreport.util.RequbeUtil;

/**
 * RQResource 에서 Key 값에 맞는 value를 가져오는 View 단 Class
 * RQResourceView.java
 *
 */
public class RQResourceView extends TagSupport{

	private static final long serialVersionUID = 1L;
	private boolean bIsComplete = false;
	private String strkey = "";
	
	private Logger log = Logger.getLogger("RQWEB");
	
	public int doStartTag() throws JspTagException{

		getMessage();
		bIsComplete = false;

		return SKIP_BODY;
	}
	
	public int doAfterBody(){
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspTagException{
        if(bIsComplete){
            return SKIP_PAGE;
        }else{
            return EVAL_PAGE;
        }
    }
	
	/**
	 * tag lib 의 setting method
	 * @param p_strkey
	 */
    public void setStrkey(String p_strkey){
    	strkey = p_strkey;
    }
    
    /**
     * strkey 로 리소스(hashtable로 저장)에서 값을 반환한다.
     * 리소스를 불러오는 방법은 아래와 같다.
     * 
     * RQResource rqresource = RQResource.getInstance();
	 * rqresource.getLocale();
	 * rqresource.load();
     * @return 
     */
	private String getMessage() {
		JspWriter out = pageContext.getOut();
		String value = "";
		
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		value = (String) rqresource.ht.get(strkey);
		try {
			if(value != null){
				out.print(value);
			}else{
				out.print("");
			}
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return value;
	}
}
