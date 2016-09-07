package com.sds.rqreport.service.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.repository.DocRepository;
import com.sds.rqreport.util.RequbeUtil;

/**
 * 캐시기능을 위해 서버파일 버전체크를 위한 서블릿 
 * RQGetfileversion.java
 *
 */
public class RQGetfileversion extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger("RQWEB");

	/**
	 * Constructor of the object.
	 */
	public RQGetfileversion() {
		super();
	}
	
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put code here
	}
	
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * 문서 버전을 가져온다. 문서 날짜를 yyyyMMddHHmmss 포멧으로 반환하며, 
	 * 문서가 존재하지 않을경우는 -1 를 반환한다. 또한 문서캐시기능 자체를 사용하지 않을경우는
	 * 0 을 반환한다.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Environment env = Environment.getInstance();
		String RQcharset = env.rqreport_server_RQcharset;
		String servercharset = env.rqreport_server_charset;
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		String lm_doc = request.getParameter("doc");
		if(lm_doc == null) return;
		lm_doc = new String(lm_doc.getBytes(servercharset), RQcharset);
		
		String doccacheOption = env.rqreport_document_cache;    
		String lm_rtnString = "-1"; 
		
		try {
			DocRepository rep = new DocRepository();
			// getFile(path)
			File file = rep.getFile(lm_doc);
			
			if(doccacheOption.equals("yes")){
				if(file.lastModified() != 0){
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
					Date lm_date = new Date(file.lastModified());
					lm_rtnString = dateformat.format(lm_date);
				}else{
					// file not found
					lm_rtnString = "-1";
				}
			}else{
				// document cache option is "no"
				lm_rtnString = "0";
			}
			
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}finally{
			out.write(lm_rtnString);
			out.flush();
			out.close();
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String lm_doc = request.getParameter("doc");
		if(lm_doc == null) return;
		String doccacheOption = (Environment.getInstance()).rqreport_document_cache;    
		String lm_rtnString = "-1"; 
		
		try {
			DocRepository rep = new DocRepository();
			// getFile(path)
			File file = rep.getFile(lm_doc); 
			response.setContentType("text/html");
			
			if(doccacheOption.equals("yes")){
				if(file.lastModified() != 0){
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
					Date lm_date = new Date(file.lastModified());
					lm_rtnString = dateformat.format(lm_date);
				}else{
					// file not found
					lm_rtnString = "-1";
				}
			}else{
				// document cache option is "no"
				lm_rtnString = "0";
			}
			
		}catch (Exception e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}finally{
			out.print(lm_rtnString);		
			out.flush();
			out.close();
		}
	}
}
