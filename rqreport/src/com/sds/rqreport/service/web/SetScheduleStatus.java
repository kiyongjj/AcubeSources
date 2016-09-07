package com.sds.rqreport.service.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sds.rqreport.scheduler.RQScheduleAPI;
import com.sds.rqreport.scheduler.ScheduleExecution;
import com.sds.rqreport.scheduler.ScheduleRunInfo;
import com.sds.rqreport.scheduler.SchedulerEnv;

public class SetScheduleStatus extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger("RQWEB");
	
	/**
	 * Constructor of the object.
	 */
	public SetScheduleStatus() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		String lm_docid = request.getParameter("docid");
		String lm_flag = request.getParameter("flag");
		try {
			RQScheduleAPI rqscheduleapi = new RQScheduleAPI();
			rqscheduleapi.addRunInfoStatus(lm_docid, lm_flag);
			
			// 문서가 에러일경우 Secondary PC 로 포워딩한다.
			SchedulerEnv env = SchedulerEnv.getInstance();
			if(!env.secondarypcexeip.equals("notavailable")){
				log.debug("secondarypcexeip : " + env.secondarypcexeip);
				if(lm_flag.equalsIgnoreCase("error")){
					log.debug("flag : " + lm_flag);	
					if(!request.getRemoteAddr().equals(env.secondarypcexeip)){
						log.debug("sendSecondaryExe");	
						sendSecondaryPC(lm_docid);
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		PrintWriter out = response.getWriter();
//		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
//		out.flush();
//		out.close();
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

		response.setContentType("text/html");
		String lm_docid = request.getParameter("docid");
		String lm_flag = request.getParameter("flag");
		try {
			RQScheduleAPI rqscheduleapi = new RQScheduleAPI();
			rqscheduleapi.addRunInfoStatus(lm_docid, lm_flag);
			
			// 문서가 에러일경우 Secondary PC 로 포워딩한다.
			SchedulerEnv env = SchedulerEnv.getInstance();
			if(!env.secondarypcexeip.equals("notavailable")){
				log.debug("secondarypcexeip : " + env.secondarypcexeip);
				if(lm_flag.equalsIgnoreCase("error")){
					log.debug("flag : " + lm_flag);	
					if(!request.getRemoteAddr().equals(env.secondarypcexeip)){
						log.debug("sendSecondaryExe");	
						sendSecondaryPC(lm_docid);
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		PrintWriter out = response.getWriter();
//		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
//		out.flush();
//		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	/**
	 * 문서 실행 오류시 세컨드 PC가 있을경우 해당PC 로 다시 호출을 보낸다.
	 * @param docid
	 * @param flag
	 */
	public void sendSecondaryPC(String docid){
		
		RQScheduleAPI ScheduleAPI = new RQScheduleAPI();
		ScheduleRunInfo sri = ScheduleAPI.getScheduleRunInfo(docid);
		
		SchedulerEnv env = SchedulerEnv.getInstance();
		String resultFileName = sri.resultFileName; 
		String doctype = resultFileName.substring(resultFileName.lastIndexOf(".") + 1, resultFileName.length());
		
		ScheduleExecution sce = new ScheduleExecution();
		log.debug("ScheduleExecution Instance generate");	
		String secondarypcexeip =  env.secondarypcexeip;
		
		sce.sendInfoSocketServer(secondarypcexeip, 59797, resultFileName, doctype, sri);
	}
}
