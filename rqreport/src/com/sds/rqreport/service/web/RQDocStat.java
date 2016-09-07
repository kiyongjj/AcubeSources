package com.sds.rqreport.service.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.*;

/**
 * 문서통계를 서블릿 JAVA , rqviewer.jsp 호출뒤 XHR(ajax)를 통해 
 * 이 서블릿을 호출하여 클라이언트에서 받은 정보로 
 * RQDOCSTAT 테이블에 인서트 혹은 업데이트함
 *
 */
public class RQDocStat extends HttpServlet {
	// member fields here
	private String act = "";
	/**
	 * Constructor of the object.
	 */
	public RQDocStat() {
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
		// 
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
		/////// reqube server use RQDocStat Module ? 
		Environment env = Environment.getInstance();
		if(!env.useRQStatistics.equals("RQStatistics"))
			return;
		act = request.getParameter("act");
		HttpSession session = request.getSession();
		if(act.equalsIgnoreCase("setCLInfo")){
			String GetRunTimeInfo     = request.getParameter("GetRunTimeInfo");
			String RUN_TIME           = null;
			String TOTALTIME          = null;
			String ERROR              = null;
			String TOTALTIME_RESPONSE = null;
			
			String[] ar = GetRunTimeInfo.split("\\|");
			if(ar != null && ar.length == 3){
				long starttime = getTimeStampFromCalendar(ar[0]);
				long endtime = getTimeStampFromCalendar(ar[1]);
				RUN_TIME = getStringFromTimeStamp(starttime);
				TOTALTIME_RESPONSE = getStringFromTimeStamp(endtime);
				// server spec은 error시 1 성공시 0 (기준이 에러)
				// client spec은  성공시 1 에러시 0   (기준이 성공여부)
				// server spec 으로 바꿔준다. 즉 성공시 0 , 에러시 1 로 바꿔줌.
				if(Integer.parseInt(ar[2]) == 1){
					ERROR = "0";
				}else{
					ERROR = "1";
				}				
				TOTALTIME = "" + (endtime - starttime);
			}
			boolean paramloadfail     = false;

			if(RUN_TIME == null || RUN_TIME.length() <= 1){
				paramloadfail = true;
			}
			if(TOTALTIME_RESPONSE == null || TOTALTIME_RESPONSE.length() <= 1){
				paramloadfail = true;
			}
			if(TOTALTIME == null || TOTALTIME.length() <= 0){
				paramloadfail = true;
			}
			if(ERROR == null){
				paramloadfail = true;
			}
			
			RQDocStatCL lm_RQDocStatCL = null;
			try{
				if(!paramloadfail){
					lm_RQDocStatCL = new RQDocStatCL();
					lm_RQDocStatCL.setRUN_TIME(RUN_TIME);
					lm_RQDocStatCL.setTOTALTIME_RESPONSE(TOTALTIME_RESPONSE);
					lm_RQDocStatCL.setTOTALTIME(TOTALTIME);
					// 서버에서 오류가 나면 서버에서 만들어지는 정보를 만들지 않지만
					// 클라이언트(API를 통해)에서 오류가 발생할경우는 그 오류의 횟수만 update 해준다.
					lm_RQDocStatCL.setERROR(ERROR);

					RQDocStatSV oRQDocStatSV = (RQDocStatSV)session.getAttribute("RQDocStatSV");

					int ierr = Integer.parseInt(ERROR);
					if(ierr == 0 && oRQDocStatSV != null){ // Viewer API를 통해 받은 에러가  0 이라면
						// table update 
						RQDocStatDAOImpl daoimpl = new RQDocStatDAOImpl();
						int applyRowflag = 0;
						applyRowflag = daoimpl.updateDocStatInfoNormal(lm_RQDocStatCL, oRQDocStatSV);
						if(applyRowflag == 0 || applyRowflag == -1){
							daoimpl.insertDocStatInfoNormal(lm_RQDocStatCL, oRQDocStatSV);
						}
						// file write
						//System.out.println("file write ...");
						
					
					//서버에서 에러가 날경우 클라이언트에선 반드시 에러가 나므로 클라이언트 에러만 처리한다.
					}else if(ierr != 0){
						// table update error++
						RQDocStatDAOImpl daoimpl = new RQDocStatDAOImpl();
						int applyRowflag = 0;
						applyRowflag = daoimpl.updateDocStatInfoAbNormal(lm_RQDocStatCL, oRQDocStatSV);
						if(applyRowflag == 0 || applyRowflag == -1){
							daoimpl.insertDocStatInfoAbNormal(lm_RQDocStatCL, oRQDocStatSV);
						}
					}
				}
			}finally{
				session.setAttribute("RQDocStatSV", null);
				lm_RQDocStatCL = null;
			}
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	public long getTimeStampFromCalendar(String cDateFormat){
		//strStart.Format(_T("%4d/%2d/%2d/%d/%d/%d/%d")
		Calendar cal = Calendar.getInstance();
		String[] strcal = cDateFormat.split("/");
		cal.set(Integer.parseInt(strcal[0].trim()), 
				Integer.parseInt(strcal[1].trim()) - 1 ,
				Integer.parseInt(strcal[2].trim()), 
				Integer.parseInt(strcal[3].trim()), 
				Integer.parseInt(strcal[4].trim()), 
				Integer.parseInt(strcal[5].trim()) );
		cal.set(Calendar.MILLISECOND, Integer.parseInt(strcal[6].trim())  );
		return cal.getTimeInMillis();
	}
	
	public String getStringFromTimeStamp(long p_ts){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmssSSS");
		String dateString = formatter.format(new Date(p_ts));
		return dateString;
	}
}
