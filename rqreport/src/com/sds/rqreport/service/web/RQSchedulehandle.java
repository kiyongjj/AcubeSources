package com.sds.rqreport.service.web;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.*;
import com.sds.rqreport.scheduler.*;
import com.sds.rqreport.util.Encoding;
import com.sds.rqreport.util.RequbeUtil;

public class RQSchedulehandle extends TagSupport{

	private static final long serialVersionUID = 1L;
	private String m_strAction = "";
	private boolean bIsComplete = false;
	private String m_RQCharset = "";
	private String m_ServerCharset = "";
	private RQSchedulerListModel m_oRQSchedulerListModel;
	private RQScheduleTimeModel m_oRQScheduleTimeModel;
	private String m_Scheduleidx;
	private ScheduleInfo m_ScheduleInfo = null;
	private JspWriter out = null;
	String exceptDot = "";
	
	private Logger log = Logger.getLogger("RQWEB");
	
	public int doStartTag() throws JspTagException{
		
		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;
		
		if(m_strAction.equals("createSchedule")){
            if(createSchedule()){
                bIsComplete = true;
            }
		}else if(m_strAction.equals("scheduleTime")){
            if(scheduleTime()){
                bIsComplete = true;
            }
		}else if(m_strAction.equals("modifyscheduleTime")){
            if(modifyscheduleTime()){
                bIsComplete = true;
            }
		}else if(m_strAction.equals("schedulTimeList")){
            if(getSchedulTimeList()){
                bIsComplete = true;
            }
		}else if(m_strAction.equals("schedulRunInfoList")){
            if(getSchedulRunInfoList()){
                bIsComplete = true;
            }
		}else if(m_strAction.equals("deleteScheduleTime")){
            if(deleteScheduleTime()){
                bIsComplete = true;
            } 
		}else if(m_strAction.equals("deleteRunInfo")){
			if(deleteRunInfo()){
				bIsComplete = true;
			}
		}else if(m_strAction.equals("getScheduleIDFromScheduleInfo")){
			if(getScheduleIDFromScheduleInfo(m_ScheduleInfo)){
				bIsComplete = true;
			}
		}else if(m_strAction.equals("getStartTimeFromScheduleInfo")){
			if(getStartTimeFromScheduleInfo(m_ScheduleInfo)){
				bIsComplete = true;
			}
		}else if(m_strAction.equals("getDayOfWeekFromScheduleInfo")){
			if(getDayOfWeekFromScheduleInfo(m_ScheduleInfo)){
				bIsComplete = true;
			}	
		}else if(m_strAction.equals("getStatusFromScheduleInfo")){
			if(getStatusFromScheduleInfo(m_ScheduleInfo)){
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
    public void setListModel(RQSchedulerListModel p_ListModel){
    	m_oRQSchedulerListModel =  p_ListModel;
    }
    public void setListModel(RQScheduleTimeModel p_TimeModel){
    	m_oRQScheduleTimeModel =  p_TimeModel;
    }
    public void setScheduleidx(String p_Scheduleidx){
    	m_Scheduleidx =  p_Scheduleidx;
    }
    public void setScheduleInfo(ScheduleInfo p_ScheduleInfo){
    	m_ScheduleInfo =  p_ScheduleInfo;
    }
    
	private boolean createSchedule() {
		out = pageContext.getOut();
		
		String lm_userid = m_oRQSchedulerListModel.getStrUserId(); 
		String lm_Resultsfileds = m_oRQSchedulerListModel.getResultfileds();
		
		if(lm_Resultsfileds != null) lm_Resultsfileds = Encoding.chCharset(lm_Resultsfileds, m_ServerCharset, m_RQCharset);
		String lm_Runvards = m_oRQSchedulerListModel.getRunvards();
		if(lm_Runvards != null)	lm_Runvards = Encoding.chCharset(lm_Runvards, m_ServerCharset, m_RQCharset);
		String[] doclist = m_oRQSchedulerListModel.getDoclist();
		String[] lm_doclist = null;
		if(doclist != null){
			lm_doclist = new String[doclist.length];
			for(int i = 0 ; i < doclist.length ; i++){
				doclist[i] = Encoding.chCharset(doclist[i], m_ServerCharset, m_RQCharset);
				lm_doclist[i] = doclist[i];
			}
		}
		int totalcnt = doclist.length;
		
		HashMap lm_oRVar = new HashMap();
		HashMap lm_oRFile = new HashMap();
		
		String[] lm_RunvardsArr = null;
		String[] lm_RunvardsArrPair = null;
		if(lm_Runvards != null && !lm_Runvards.equals("")){
			lm_RunvardsArr = lm_Runvards.split("\\");         // "" <--- asc code 05
			for(int i = 0 ; i < lm_RunvardsArr.length ; i++){
				lm_RunvardsArrPair = lm_RunvardsArr[i].split("\t");
				lm_oRVar.put(lm_RunvardsArrPair[0],  lm_RunvardsArrPair[1] );
			}
		}
		
		String[] lm_ResultsfiledsArr = null;
		String[] lm_ResultsfiledsArrPair = null;
		if(lm_Resultsfileds != null && !lm_Resultsfileds.equals("")){
			lm_ResultsfiledsArr = lm_Resultsfileds.split("\\");        // "" <--- asc code 05
			for(int i = 0 ; i < lm_ResultsfiledsArr.length ; i++){
				lm_ResultsfiledsArrPair = lm_ResultsfiledsArr[i].split("\t");
				lm_oRFile.put(lm_ResultsfiledsArrPair[0],  lm_ResultsfiledsArrPair[1] );
			}
		}
		
		RQScheduleAPI lm_oRQScheduleAPI = new RQScheduleAPI();
		int scheduleID = Integer.parseInt(m_oRQSchedulerListModel.getScheduleid());  
		String notiListName = m_oRQSchedulerListModel.getMailinglist();
		if(notiListName == null) notiListName = "";
		
		/////////////////// 실행되는 문서를 DB에 Insert 하는 부분 .. ///////////////////////////////////////////
		//// 파일 포맷을 받아와 DB에 같이 Insert 한다. 
		String dformat = ((HttpServletRequest) pageContext.getRequest()).getParameter("dformat");
		for(int i=0 ; i < totalcnt ; i++){
			lm_oRQScheduleAPI.addRunInfo(scheduleID, 
						notiListName, 
						lm_doclist[i], 
						(String) lm_oRVar.get(lm_doclist[i]), 
						lm_userid, 
						(String) lm_oRFile.get(lm_doclist[i]), 
						notiListName != null && notiListName != "" ? 1 : 0, 
						lm_oRFile.get(lm_doclist[i]) != null && lm_oRFile.get(lm_doclist[i]) != "" ? 1 : 0 , 
						0, 
						"",
						dformat);
		}
		try{
			out.println("<script language='javascript'>                                				   " +
						"  	opener.document.getElementById('scheduleidx').value = '"+scheduleID+"';    " +
					    "	opener.scheduleRunningPage.reloadpage();                                       				   " +
					    "	self.close();                                              				   " +
					    "</script>");
			
		}catch(IOException ioe){
			RequbeUtil.do_PrintStackTrace(log, ioe);
		}
		return true;
	}
	
	public boolean scheduleTime(){
		out = pageContext.getOut();		
		RQScheduleAPI lm_oRQScheduleAPI = new RQScheduleAPI();
		
		Calendar startDate 		= m_oRQScheduleTimeModel.getM_cal_startDate();
		Calendar startTime 		= m_oRQScheduleTimeModel.getM_cal_startDate();
		int repeatType 			= m_oRQScheduleTimeModel.getRepeatType();
		int dayOfMonth 			= m_oRQScheduleTimeModel.getDayOfMonth();
		int dayOfWeek 			= m_oRQScheduleTimeModel.getDayOfWeek();
		int periodOfDay 		= m_oRQScheduleTimeModel.getPeriodOfDay();
		int periodOfWeek 		= m_oRQScheduleTimeModel.getPeriodOfWeek();
		int periodOfMonth 	  	= m_oRQScheduleTimeModel.getPeriodOfMonth();
		int ordinalOfDayType  	= m_oRQScheduleTimeModel.getOrdinalOfDayType();
		int dayType 			= m_oRQScheduleTimeModel.getDayType();
		int notification 		= m_oRQScheduleTimeModel.getNotification();
		int repeatFreq 			= m_oRQScheduleTimeModel.getRepeatFreq();
		int repeatBoundType		= m_oRQScheduleTimeModel.getRepeatBoundType();
		Calendar repeatEndDate 	= m_oRQScheduleTimeModel.getRepeatEndDate();
		
		lm_oRQScheduleAPI.addScheduleTime( startDate, startTime, repeatType, dayOfMonth, 
										  dayOfWeek, periodOfDay, periodOfWeek, periodOfMonth, 
										  ordinalOfDayType, dayType, notification, repeatFreq, 
										  repeatBoundType, repeatEndDate);
		try{
			out.println("<script language='javascript'>opener.scheduletimelistPage.reloadpage();self.close();</script>");
		}catch(IOException ioe){
			RequbeUtil.do_PrintStackTrace(log, ioe);
		}
		return true;
	}

	public boolean modifyscheduleTime(){
		
		out = pageContext.getOut();		
		RQScheduleAPI lm_oRQScheduleAPI = new RQScheduleAPI();
		
		int scheduleid 			= m_oRQScheduleTimeModel.getSchedule();
		Calendar startDate 		= m_oRQScheduleTimeModel.getM_cal_startDate();
		Calendar startTime 		= m_oRQScheduleTimeModel.getM_cal_startDate();
		int repeatType 			= m_oRQScheduleTimeModel.getRepeatType();
		int dayOfMonth 			= m_oRQScheduleTimeModel.getDayOfMonth();
		int dayOfWeek 			= m_oRQScheduleTimeModel.getDayOfWeek();
		int periodOfDay 		= m_oRQScheduleTimeModel.getPeriodOfDay();
		int periodOfWeek 		= m_oRQScheduleTimeModel.getPeriodOfWeek();
		int periodOfMonth 	  	= m_oRQScheduleTimeModel.getPeriodOfMonth();
		int ordinalOfDayType  	= m_oRQScheduleTimeModel.getOrdinalOfDayType();
		int dayType 			= m_oRQScheduleTimeModel.getDayType();
		int notification 		= m_oRQScheduleTimeModel.getNotification();
		int repeatFreq 			= m_oRQScheduleTimeModel.getRepeatFreq();
		int repeatBoundType		= m_oRQScheduleTimeModel.getRepeatBoundType();
		Calendar repeatEndDate 	= m_oRQScheduleTimeModel.getRepeatEndDate();
		
		lm_oRQScheduleAPI.updateScheduleTime(scheduleid, startDate, startTime, repeatType, dayOfMonth, 
										  dayOfWeek, periodOfDay, periodOfWeek, periodOfMonth, 
										  ordinalOfDayType, dayType, notification, repeatFreq, 
										  repeatBoundType, repeatEndDate);
		try{
			out.println("<script language='javascript'>opener.scheduletimelistPage.reloadpage();self.close();</script>");
		}catch(IOException ioe){
			RequbeUtil.do_PrintStackTrace(log, ioe);
		}
		return true;
	}
	
	public boolean getSchedulTimeList(){
  		
		RQScheduleAPI lm_oRScheduleAPI = new RQScheduleAPI();
  		ArrayList arr = new ArrayList();
  		lm_oRScheduleAPI.getSchedulingList(0, arr); // 0 일경우 모든 list
  		pageContext.setAttribute("schedultimeList", arr);
  		
		return true;
	}
	
	public boolean getSchedulRunInfoList(){
		RQScheduleAPI lm_oRScheduleAPI = new RQScheduleAPI();
		ArrayList arr = new ArrayList();
		lm_oRScheduleAPI.getRunInfoList(Integer.parseInt(m_Scheduleidx), arr);
		pageContext.setAttribute("runInfoList", arr);
		return true;
	}
	
	public boolean deleteScheduleTime(){
		out = pageContext.getOut();	
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		String[] lm_arrChkscheduleID = request.getParameterValues("chkscheduleID");
		RQScheduleAPI lm_oRScheduleAPI = new RQScheduleAPI();
		for(int i = 0 ; i < lm_arrChkscheduleID.length ; i ++){
			lm_oRScheduleAPI.delete(Integer.parseInt(lm_arrChkscheduleID[i]));
		}
		try {
			String msg = (String) rqresource.ht.get("RQSchedulehandler.deleteScheduleTime.delete");
			out.println("<script language='javascript'>" +
						"	alert('" + msg + "');" +
						"	document.location.href='../schedule/rqscheduletimelist.jsp';" +
						"</script>");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
	public boolean deleteRunInfo(){
		
		out = pageContext.getOut();	
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		String[] lm_arrchkRuninfoID = request.getParameterValues("chkRuninfoID");
		RQScheduleAPI lm_oRScheduleAPI = new RQScheduleAPI();
		String scheduleidx = request.getParameter("scheduleidx");
		
		for(int i = 0 ; i < lm_arrchkRuninfoID.length ; i ++){
			lm_oRScheduleAPI.deleteRunInfo(Integer.parseInt(lm_arrchkRuninfoID[i]));
		}
		
		try {
			String msg = (String) rqresource.ht.get("RQSchedulehandler.deleteScheduleTime.delete");
			out.println("<script language='javascript'>                                				   " +
					"	alert('" + msg + "');" +
					"	document.location.href='../schedule/rqschedule_runinfolist.jsp?scheduleidx="+scheduleidx+"';" +
				    "</script>");
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
	public boolean getScheduleIDFromScheduleInfo(ScheduleInfo p_ScheduleInfo){
		out = pageContext.getOut();
		try {
			out.println(p_ScheduleInfo.scheduleID);
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
	public boolean getStartTimeFromScheduleInfo(ScheduleInfo p_ScheduleInfo){
		out = pageContext.getOut();
		String startTime = "";
		if(p_ScheduleInfo.getStrStartTime() != null){
			startTime = p_ScheduleInfo.getStrStartTime();
			if(startTime.indexOf(".") != -1){
				exceptDot = startTime.substring(0, startTime.indexOf("."));
			}else{
				exceptDot = startTime;
			}
			try {
				out.println(exceptDot);
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
		}
		return true;
	}
	
	public boolean getDayOfWeekFromScheduleInfo(ScheduleInfo p_ScheduleInfo){
		out = pageContext.getOut();
		
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		StringBuffer strBf = new StringBuffer();
		if(p_ScheduleInfo.getStrStartTime() != null){
			
			String strFrom = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.from");
			String strAt = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.at");
			
			String strRepeatday = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.repeatday");
			String strRepeatweek = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.repeatweek");
			
			String strSat = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.sat");
			String strFri = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.fri");
			String strThu = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.thu");
			String strWed = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.wed");
			String strTue = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.tue");
			String strMon = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.mon");
			String strSun = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.sun");
			String strRepeat = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.repeat");
			
			String strRepeatmonth = (String) rqresource.ht.get("RQSchedulehandler.getDayOfWeekFromScheduleInfo.repeatmonth");
			
			String[] arrST = exceptDot.split(" ");

			switch (arrST.length) {
			case 0:
				break;
			case 1:
				strBf.append(arrST[0]);
				strBf.append(" " + strFrom + " ");
				break;
			default:
				strBf.append(arrST[0]);
				strBf.append(" " + strFrom + " ");
				strBf.append(arrST[1]);
				strBf.append(" " + strAt +" ");
			}
			
			switch (p_ScheduleInfo.repeatType){
				case 0 :
					break;
				case 1 :
					strBf.append(" " + strRepeatday + " ");
					break;
				case 2 :
					strBf.append(" " + strRepeatweek + " ");
					int dow = p_ScheduleInfo.dayOfWeek;
					String[] yoil = {strSat,strFri,strThu,strWed,strTue,strMon,strSun};
					strBf.append("'");
					for(int i = 6; i >= 0 ; i--){
						if(((int)Math.pow(2, i) & dow) > 0 ){
							strBf.append(yoil[i]);
						}
					}
					strBf.append("'");
					strBf.append(" " + strRepeat + " ");
					break;
				case 3 :
					strBf.append(" " + strRepeatmonth + " ");
					break;
			}
			try {
				out.println(strBf.toString());
			} catch (IOException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
		}
		return true;
	}
	
	public boolean getStatusFromScheduleInfo(ScheduleInfo p_ScheduleInfo){
		out = pageContext.getOut();
		RQResource rqresource = RQResource.getInstance();
		rqresource.getLocale();
		rqresource.load();
		
		try{
			String strStandby = (String) rqresource.ht.get("RQSchedulehandler.getStatusFromScheduleInfo.standby");
			String strDone = (String) rqresource.ht.get("RQSchedulehandler.getStatusFromScheduleInfo.done");
			
			if(p_ScheduleInfo.status == 0){
				out.print(strStandby);
			}else{
				out.print(strDone);
			}
		} catch (IOException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		return true;
	}
	
}
