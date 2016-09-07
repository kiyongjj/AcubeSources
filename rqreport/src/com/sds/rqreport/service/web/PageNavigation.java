package com.sds.rqreport.service.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.UserModel;
import com.sds.rqreport.repository.DocInfo;
import com.sds.rqreport.util.Encoding;
import com.sds.rqreport.util.RequbeUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class PageNavigation extends TagSupport{

	private static final long serialVersionUID = 1L;
	private String strAction = "";
	private Collection oList = null;
	
	private int iListCount = 10;
	private int iPageCount = 10;
	
	private String strCurrentPage = "1";
	private String strPathIs = "/";
	
	private int iTotalCount = 0;
	private int iTpage=0;
	private int iTpageflag = 1;
	private int iCurrentPageflag = 1;
	private int iPrepage = 0;
	private int iNextPage = 0;
	private int iCurrentPage = 1;
	private int iTpageNamugi = 0;
	private String privilege = "";
	private boolean bIsComplete = true;
	public String m_RQCharset = "";
	public String m_ServerCharset = "";
	private Logger log = Logger.getLogger("RQWEB");
	
	public int doStartTag() throws JspTagException{

		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;
		
		if(strAction.equals("pageNavi")){
			pageNavi();
		}else if(strAction.equals("checkprivilege")){
			checkPagePrivilege();
		}else if(strAction.equals("folderpath")){
			showFolderpath();
		}else if(strAction.equals("getFolderInfo")){
			getFolderInfo();
		}else{
			bIsComplete = false;
		}
		return SKIP_BODY;
	}
	
	public int doEncTag() throws JspTagException{
		
		if(bIsComplete){
			return EVAL_PAGE;
		}else{
			return SKIP_BODY;
		}
	}
	
	public void setAction(String p_strAction){
		strAction = p_strAction;
	}
	public void setList(Collection p_oList){
		oList = p_oList;
	}
	public void setListCount(int p_iListCount){
		this.iListCount = p_iListCount;
	}
	public void setPageCount(int p_iPageCount){
		this.iPageCount = p_iPageCount;
	}
	public void setPrivilege(String p_Privilege){
		this.privilege = p_Privilege;
	}
	public void setStrPathIs(String strPathIs){
		this.strPathIs = strPathIs;
	}
	
	public void checkPagePrivilege(){
		UserModel usermodel = null;
		String strAuth = "";
		HttpSession oSession = ((HttpServletRequest)pageContext.getRequest()).getSession(true);
		JspWriter out = pageContext.getOut();
		try{
			if(!oSession.isNew() || oSession.getAttribute("UM") != null){
				usermodel	=	(UserModel)oSession.getAttribute("UM");
				strAuth = usermodel.getAuth();
				
				int iAuth = 0;
				int iprivilege = 0;
				
				if( privilege.equals("A")){
					iprivilege = 1;
				}else if(privilege.equals("G")){
					iprivilege = 5;
				}else{
					iprivilege = 10;
				}
				
				if(strAuth.equals("A")){
					iAuth = 1;
				}else if(strAuth.equals("G")){
					iAuth = 5;
				}else{
					iAuth = 10;
				}
				if(iprivilege < iAuth){
					oSession.removeAttribute("UM");
					out.println("<script language='javascript'>alert('권한이 없습니다.');location.href='/rqreport/index.jsp';</script>");
				}else{}
			}
		 }catch(Exception e){
			oSession.removeAttribute("UI");
		}
	}
	
	/**
	 * page navigation을 표현한다.
	 *
	 */
	public void pageNavi(){
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		
		if( (pageContext.getRequest()).getParameter("strPathIs") != null && ! (pageContext.getRequest()).getParameter("strPathIs").equals("")){
		   	strPathIs =  (pageContext.getRequest()).getParameter("strPathIs");
		}else{
		    strPathIs = "/";    
		}

		strPathIs = Encoding.chCharset(strPathIs, m_ServerCharset, m_RQCharset);
		
		if(request.getParameter("strCurrentPage") != null && !request.getParameter("strCurrentPage").equals("")) {
			strCurrentPage = request.getParameter("strCurrentPage");
		}else{
			strCurrentPage = "1";
		}
		iCurrentPage = Integer.parseInt(strCurrentPage);
		
		// iTotalCount
		if(oList.size() == 0){
			iTotalCount = 1;
		}else{
			iTotalCount = oList.size();
		}
		
		// iTpage 
		if((iTotalCount%iListCount)==0){
			iTpage = iTotalCount/iListCount;
		} else {
		   	iTpage = (iTotalCount/iListCount)+1;
		}
		if(iCurrentPage > iTpage){
			iCurrentPage = iTpage;
		}
		
		if((iTpage%iPageCount)==0){
			iTpageflag = iTpage/iPageCount;
		} else {
	    	iTpageflag = (iTpage/iPageCount) + 1;
		   	iTpageNamugi = iTpage%iPageCount;
		}
		
		if((iCurrentPage%iPageCount)==0) iCurrentPageflag = iCurrentPage/iPageCount;
			else iCurrentPageflag = (iCurrentPage/iPageCount)+1;
		
		gageNaviImpl();
		
	}
	
	public void gageNaviImpl(){
		try{

			JspWriter out = pageContext.getOut();

			if(iCurrentPageflag==1 && iCurrentPageflag == iTpageflag){
				for(int pageNo = 1; pageNo <= iTpage ; pageNo++){
					out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +pageNo+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>");
					if(iCurrentPage==pageNo) out.print("<span style='color:#F48625;font-size:14px;font-weight:bold;'>");
					out.print(pageNo+"&nbsp;");
					if(iCurrentPage==pageNo) out.print("</span>");
					out.print("</a>");
				}
			}
			
			if(iCurrentPageflag==1 && iCurrentPageflag != iTpageflag){
				
				for(int pageNo = 1; pageNo <= iPageCount ; pageNo++){
					out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +pageNo+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>");
					if(iCurrentPage==pageNo) out.print("<span style='color:#F48625;font-size:14px;font-weight:bold;'>");
					out.print(pageNo + "&nbsp;");
					if(iCurrentPage==pageNo) out.print("</span>");
					out.print("</a>");
				}
				
				iNextPage = iCurrentPageflag*iPageCount+1;
				out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +iNextPage+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>" +
						  "<img src='../img/nav_right2.gif' width='15' height='11' border='0'></a>");
				
			}
			
			if(iCurrentPageflag!=1 && iCurrentPageflag != iTpageflag){
				
				iPrepage = (iCurrentPageflag-1)*iPageCount - (iPageCount-1); 
				out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +iPrepage+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>" +
						  "<img src='../img/nav_left2.gif' width='15' height='11' border='0'></a>&nbsp;");

				for(int pageNo=(iCurrentPageflag-1)*iPageCount+1 ; pageNo <= ( (iCurrentPageflag-1)*iPageCount + iPageCount) ; pageNo++ ){
					out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +pageNo+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>");
					if(iCurrentPage==pageNo) out.print("<span style='color:#F48625;font-size:14px;font-weight:bold;'>");
					out.print(pageNo+"&nbsp;");
					if(iCurrentPage==pageNo) out.print("</span>");
					out.print("</a>");
				}
				
				iNextPage = (iCurrentPageflag+1)*iPageCount - (iPageCount-1); 
				out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +iNextPage+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>" +
				          "<img src='../img/nav_right2.gif' width='15' height='11' border='0'></a>");
				
			}
			
			if(iCurrentPageflag!=1 && iCurrentPageflag == iTpageflag){
				iPrepage = (iCurrentPageflag-1)*iPageCount - (iPageCount-1); 
				out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +iPrepage+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>" +
						  "<img src='../img/nav_left2.gif' width='15' height='11' border='0'></a>&nbsp;");
				
				for(int pageNo=(iCurrentPageflag-1)*iPageCount+1 ; pageNo <= ( (iCurrentPageflag-1)*iPageCount + iTpageNamugi) ; pageNo++ ){
					out.print("<a href="+"\""+"#\"" + " name=\"gopage\" pagevalue=\"" +pageNo+ "\" pathvalue=\"" +strPathIs+ "\" class=tablenav>");
					if(iCurrentPage==pageNo) out.print("<span style='color:#F48625;font-size:14px;font-weight:bold;'>");
					out.print(pageNo+"&nbsp;");
					if(iCurrentPage==pageNo) out.print("</span>");
					out.print("</a>");
				}				
				
			}
			
		}catch(IOException ie){
			RequbeUtil.do_PrintStackTrace(log, ie);
		}
	}
	
	public void showFolderpath(){
		JspWriter out = pageContext.getOut();
		String parentPath = "/";
		try{
			//out.println("strPathIs: " +strPathIs+ "<br>");
			String[] oPaths = strPathIs.split("/");
			//out.println("oPaths length : " + oPaths.length+ "<br>");
			if(oPaths.length != 0){
				StringBuffer sbfGoPath = new StringBuffer();
				for(int p=0; p < oPaths.length;p++){
					if(p == 0){
						//oPaths[p] = "Root";
						oPaths[p] = "";
						sbfGoPath.append("/");
					}else{
						// parentPath
						if(p == (oPaths.length-1)){
							parentPath = sbfGoPath.toString();
						}
						// append path
						sbfGoPath.append(""+oPaths[p]+"/");
					}
		
		
					if(p != 0 && p != 1){ 
						out.println("<span class=\"n_navi\">/</span>");
					}
					String lm_doc = sbfGoPath.toString();
					if(m_RQCharset.equalsIgnoreCase("UTF-8")){
						lm_doc = URLEncoder.encode(lm_doc, "UTF-8");
					}
					out.println("<a href=\"document.jsp?strPathIs=" + lm_doc + "&strCurrentPage=1\" class=\"n_navi\">");
					
					if(oPaths[p].toString().equals("")){
						out.println("/");
					}else{
						out.println(oPaths[p].toString());
					}
					out.println("</a>");	
				}
			}else{
				out.println("<a href=\"document.jsp\" class=\"n_navi\">/</a>");
			}
			pageContext.setAttribute("parentPath", parentPath);
		}catch(IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
	}

	/**
	 * 	tree.root.add(node1);
	 * node1.add(node2);
	 * ...
	 * 	node2.remove(node7);
	 * tree.moveNode( node2, node3);      
	 * tree.copyNode( node2, node4);
	 * @return
	 */
	public boolean getFolderInfo(){
		JspWriter out = pageContext.getOut();
		Iterator lm_it_list = oList.iterator();
		DocInfo lm_di = null;
		String lm_pnodename = "";
		try {
			while(lm_it_list.hasNext()){
				lm_di = (DocInfo) lm_it_list.next();		
				if(lm_di.pdocID == 0){ //root itself
				}else if(lm_di.pdocID == 1){ //first children
					out.println("tree.root.add(" + lm_di.nodename + ");");
				}else{
					lm_pnodename = getNodeName(lm_di.pdocID);
					out.println(lm_pnodename+".add(" + lm_di.nodename + ");");
				}
			}
			out.println(	"tree.expandAll();");
			out.println("tree.repaint();");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
	public String getNodeName(int idx){
		Iterator lm_it_list = oList.iterator();
		String lm_pnodename = "";
		DocInfo lm_di = null;
		while(lm_it_list.hasNext()){
			lm_di = (DocInfo) lm_it_list.next();
			if(lm_di.idx == idx){
				lm_pnodename = lm_di.nodename;
			}
		}
		return lm_pnodename;
	}
	
}
