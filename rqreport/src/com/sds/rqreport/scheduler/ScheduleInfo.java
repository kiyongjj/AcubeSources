package com.sds.rqreport.scheduler;

import java.util.*;
import java.sql.*;
import java.io.*;
import com.sds.rqreport.util.*;
import com.sds.rqreport.common.*;
import org.apache.log4j.*;

//("Select RSchedule.nScheduleID, RSchedule.nDocID, RSchedule.strRuntimeVar, RSchedule.strUserID, RSchedule.dtStartTime, RSchedule.dtStartDate, RSchedule.nRepeatType,");
//("RSchedule.nDayOfMonth, RSchedule.nDayOfWeek, RSchedule.nPeriodOfDay, nPeriodOfWeek, RSchedule.nPeriodOfMonth, RSchedule.nOrdinalOfDayType, RSchedule.nDayType, ");
//("RSchedule.nNotification, RSchedule.nRepeatFreq, RSchedule.nRepeatBoundType, RSchedule.dtRepeatEndDate, RSchedule.nResultFolderID, RSchedule.strResultFileName, ");
//("RSchedule.strEmailForm, RSchedule.nStatus, RDoc.strDoc,RDoc.nFolderID From RSchedule, RDoc where RDoc.nDocID = RSchedule.nDocID ORDER BY RSchedule.nScheduleID");

public class ScheduleInfo implements RQInfo, Serializable {
  public int scheduleID;
  public int docID;
  public String runVar;
  public String userID;
  public Calendar startTime;
  private String strStartTime;
  public Calendar startDate;
  public int repeatType;
  public int dayOfMonth;
  public int dayOfWeek;
  public int periodOfDay;
  public int periodOfWeek;
  public int periodOfMonth;
  public int ordinalOfDayType;
  public int dayType;
  public int notification;
  public int repeatFreq;
  public int repeatBoundType;
  public Calendar repeatEndDate;
  public int resultFolderID;
  public String resultFileName;
  public String emailForm;
  public int status;
  public String docName;
  public String docFullPath;
  public String userName;
  public String resultFolderName;
  public boolean scheduled = false;



  public ScheduleInfo() {

  }
  public ScheduleInfo(ResultSet rs)
  {
    try
    {
			Calendar cal = Calendar.getInstance();
      this.scheduleID = rs.getInt(1);
      this.docID = rs.getInt(2);
      this.runVar = rs.getString(3);
      this.userID = rs.getString(4);
      java.util.Date d = null;
		  java.sql.Time t = null;
		  t = rs.getTime(5);
      cal.setTime(t);
      this.startTime = cal;
      d= rs.getDate(6);
      cal = Calendar.getInstance();
      cal.setTime(d);
      this.startDate = cal;
      this.repeatType = rs.getInt(7);
      this.dayOfMonth = rs.getInt(8);
      this.dayOfWeek = rs.getInt(9);
      this.periodOfDay = rs.getInt(10);
      this.periodOfWeek = rs.getInt(11);
      this.periodOfMonth = rs.getInt(12);
      this.ordinalOfDayType = rs.getInt(13);
      this.dayType = rs.getInt(14);
      this.notification = rs.getInt(15);
      this.repeatFreq = rs.getInt(16);
      this.repeatBoundType = rs.getInt(17);
      cal = Calendar.getInstance();
      d = rs.getDate(18);
      cal.setTime(d);
      this.repeatEndDate = cal;
      this.resultFolderID = rs.getInt(19);
      this.resultFileName = rs.getString(20);
      this.emailForm = rs.getString(21);
      this.status = rs.getInt(22);
      this.docName = rs.getString(23);
      this.docFullPath = null;
      this.userName = null;
      this.resultFolderName = null;
    }catch(Exception ex)
    {
			ex.printStackTrace();
    }

  }
  public String getParamTypes() {
    return "IISSDDIIIIIIIIIIIDISSISSSS";
  }
  public int getParamInt(int index) {
    int ret = 0;
    switch(index)
    {
	  case 0:
	    ret = scheduleID;
	    break;    	
      case 1:
        ret = docID;
        break;
      case 6:
        ret = repeatType;
        break;
      case 7:
        ret = dayOfMonth;
        break;
      case 8:
        ret = dayOfWeek;
        break;
      case 9:
        ret = periodOfDay;
        break;
      case 10:
        ret = periodOfWeek;
        break;
      case 11:
        ret = periodOfMonth;
        break;
      case 12:
        ret = ordinalOfDayType;
        break;
      case 13:
        ret = dayType;
        break;
      case 14:
        ret = notification;
        break;
      case 15:
        ret = repeatFreq;
        break;
      case 16:
        ret = repeatBoundType;
        break;
      case 18:
        ret = resultFolderID;
        break;
      case 21:
        ret = status;
        break;
    }
    return ret;

  }
  public float getParamFloat(int index) {
    return 0F;
  }
  public String getParamString(int index) {
    String ret = null;
    switch(index)
    {

      case 2:
        ret = runVar;
        break;
      case 3:
        ret = userID;
        break;
      case 19:
        ret = resultFileName;
        break;
      case 20:
        ret = emailForm;
        break;
      case 22:
        ret = docName;
        break;
      case 23:
        ret = docFullPath;
        break;
      case 24:
        ret = userName;
        break;
      case 25:
        ret = resultFolderName;
        break;
    }
    return ret;
  }
  static Vector getSchedules(ResultSet rs)
  {
    ScheduleInfo sch;
//    RQDocAPI docAPI = new RQDocAPI();
//    RQUserAPI userAPI = new RQUserAPI();
//    short res = 0;
    Vector r = new Vector(100);
//    try {
//      while (rs.next()) {
//        sch = new ScheduleInfo(rs);
//        Vector ret = new Vector(2);
//        res = docAPI.getFolder(sch.resultFolderID, ret);
//        if(ErrorCode.isSuccess(res))
//        {
//					FolderInfo fi = (FolderInfo) ret.get(1);
//					sch.resultFolderName = fi.folderName;
//        }
//        
//        ret.removeAllElements();
//        res = docAPI.getDoc(sch.docID, ret);
//        if(ErrorCode.isSuccess(res))
//				{
//      	  DocumentInfo di = (DocumentInfo) ret.get(1);
//      	  sch.docFullPath = di.fullName;
//				}
//        ret.removeAllElements();
//        res = userAPI.get(sch.userID, ret);
//        if(ErrorCode.isSuccess(res))
//				{
//        	UserInfo ui = (UserInfo) ret.get(1);
//        	sch.userName = ui.name;
//				}
//        r.add(sch);
//      }
//
//    }
//    catch (SQLException ex) {
//      return null;
//    }
    return r;
  }

  public Calendar getParamCalendar(int index) {
    Calendar ret = null;
    switch(index)
    {
      case 4:
        ret = startTime;
        break;
      case 5:
        ret = startDate;
        break;
      case 17:
        ret = repeatEndDate;
        break;
    }
    return ret;
  }
  
	public String getStrStartTime() {
		return strStartTime;
	}
	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

}