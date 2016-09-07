package com.sds.rqreport.scheduler;

import java.sql.*;
import java.util.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.common.*;
import com.sds.rqreport.model.*;
import com.sds.rqreport.util.*;
import org.apache.log4j.*;

public class RQScheduleAPI  {
	static Logger L = Logger.getLogger("SCHEDULER");
	String errorMsg;
	JDBCHelper jdbc;
	final static int RQ_EXCEPTION = -1;
	SchedulerEnv env = null;
	Environment g_env = Environment.getInstance();
	
	public RQScheduleAPI(){
		env = SchedulerEnv.getInstance();
	}

	public String getError() {
		return errorMsg;
	}

	public short deleteSchedules(String idList){
		if(idList.length() < 100){
		
			JDBCHelper jdbc = null;
			try {
				jdbc = new JDBCHelper();
				jdbc.connect();
				String qry2 = "delete from " + env.scheduleRunInfoTableName +" where nScheduleID in (" + idList + ")";
				jdbc.execute(qry2);
				String qry = "delete from " + env.scheduleTimeTableName + " where nScheduleID in (" + idList + ")";
				jdbc.execute(qry);
				String mIdListStr = "'" + RequbeUtil.replaceAll(idList,",","','") + "'";
				String qry3 = "delete from " + env.scheduleNotifictionTableName + " where strNotificationID in (" + mIdListStr +  ")";
				jdbc.execute(qry3);
				return 0;
			}catch (Exception ex){
				errorMsg = ex.toString();
				return -1;
			} finally {
				try {
					if (jdbc != null) {
						jdbc.close();
					}
				} catch (Exception ex) {}
				jdbc = null;
			}
			
		}else{
			int pos = idList.lastIndexOf(",",99);
			if(pos > 0){
				short res = deleteSchedules(idList.substring(0,pos));
				if(res >= 0){
					return deleteSchedules(idList.substring(pos + 1)); 
				}else{
					return res;
				}
			}
		}
		return 0;
	}

	public short addMailingList(int nScheduleID, int nReceiverType, String strID, String strEmail){
		addNotification("" + nScheduleID, strID, nReceiverType, strEmail );
		return 0;
	}
	
	public short addScheduleTime(Calendar startDate, Calendar startTime, int repeatType,
                   				int dayOfMonth, int dayOfWeek, int periodOfDay, int periodOfWeek, int periodOfMonth,
                   				int ordinalOfDayType, int dayType, int notification, int repeatFreq, int repeatBoundType,
                   				Calendar repeatEndDate) {
		JDBCHelper jdbc = null;
		StringBuffer sb = null;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			int schedule = jdbc.getID(env.scheduleTimeTableName, "ScheduleID");
			jdbc.close();
			jdbc.connect();
			String qry = "Insert into " + env.scheduleTimeTableName + " (ScheduleID, StartTime, StartDate, RepeatType, DayOfMonth, DayOfWeek, "
					+ "PeriodOFDay, PeriodOfWeek, PeriodOfMonth, OrdinalOfDayType, DayType, RepeatFreq, RepeatBoundType, RepeatEndDate, Status"
					+ ") values(";
			sb = new StringBuffer(3000);
			sb.append("");
			sb.append(schedule);
			sb.append(", ");
			
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				sb.append("to_date('");
				sb.append(makeDateString2(startTime)); // oracle
				sb.append("', '");
				sb.append("RR/MM/DD HH24:MI:SS"); // oracle
				sb.append("'), ");
				sb.append("to_date('");
				sb.append(makeDateString2(startDate)); // oracle
				sb.append("', '");
				sb.append("RR/MM/DD HH24:MI:SS"); // oracle
				sb.append("'), ");
			}else{
				sb.append("'");
				sb.append(makeDateString3(startTime));
				sb.append("'");
				sb.append(", ");
				sb.append("'");
				sb.append(makeDateString3(startDate));
				sb.append("'");
				sb.append(", ");
			}
			
			sb.append(repeatType);
			sb.append(", ");
			sb.append(dayOfMonth);
			sb.append(", ");
			sb.append(dayOfWeek);
			sb.append(", ");
			sb.append(periodOfDay);
			sb.append(", ");
			sb.append(periodOfWeek);
			sb.append(", ");
			sb.append(periodOfMonth);
			sb.append(", ");
			sb.append(ordinalOfDayType);
			sb.append(", ");
			sb.append(dayType);
			sb.append(", ");
			sb.append(repeatFreq);
			sb.append(", ");
			sb.append(repeatBoundType);
			sb.append(", ");
			
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				sb.append("to_date('");
				sb.append(makeDateString2(repeatEndDate)); // oracle
				sb.append("', '");
				sb.append("RR/MM/DD HH24:MI:SS"); // oracle
				sb.append("'), ");
			}else{
				sb.append("'");
				sb.append(makeDateString3(repeatEndDate));
				sb.append("'");
				sb.append(", ");
			}
			
			sb.append(0);
			sb.append(")");
			qry += sb.toString();
			jdbc.execute(qry);
			// 	jdbc.close();
			//  	jdbc = null;
			return 0;
		}catch (Exception ex){
			errorMsg = ex.toString();
			ex.printStackTrace();
			return -1;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){
			}
			jdbc = null;
			sb = null;
		}
	}

	/**
	 * 5.7 spec. 을 위한 함수
	 * @param nScheduleID
	 * @param strUserID
	 * @return
	 */

	public short updateUserInfo( int nScheduleID, String strUserID){
		return 0;
	}
	
	/**
	 *  5.7 spec 을 위한 함수
	 * @param nScheduleID
	 * @param nDocID
	 * @param strRunvar
	 * @return
	 */
	public short updateDocInfo(int nScheduleID, int nDocID, String strRunvar){
		return 0;
	}

	public short UpdateRepeatInfo(int nScheduleID, int nRepeatType, Calendar dtStartTime, Calendar dtStartDate, int nDayOfMonth, int nDayOfWeek, int nPeriodOfDay, int nPeriodOfWeek, int nPeriodOfMonth, int nOrdinalOfDayType, int nDayType, int nRepeatFreq, int nRepeatBoundType, Calendar dtRepeatEndDate){
		return 0;
	}

	public short addRunInfo(int scheduleID, String notiListName,String doc,String runVar,String userID,String resultFile, int notification, int attachResult, int userAttach, String emailForm, String dformat){
		JDBCHelper jdbc = null;
		StringBuffer sb = null;
		try {

			jdbc = new JDBCHelper();
			jdbc.connect();
			// RUNINFOID 의 MAX값을 가져온다.
			int runInfoID = jdbc.getID(env.scheduleRunInfoTableName, "RunInfoID");
			jdbc.close();
			jdbc.connect();
			String qry = "Insert into " + env.scheduleRunInfoTableName + " (ScheduleID, Doc, RunVar, UserID, ResultFile,"
				+" MailingList, Notification, AttachResult, UserAttach, EmailForm, RunInfoID, dformat"
				+ ") values(";
			sb = new StringBuffer(1000);
			sb.append("");
			sb.append(scheduleID);
			sb.append(", '");
			sb.append(doc);
			sb.append("', '");
			// runvVar single quotation check
			if(runVar != null){
				runVar = runVar.replaceAll("'", "''");	
			}
			sb.append(runVar);
			sb.append("', '");
			sb.append(userID);
			sb.append("', '");
			sb.append(resultFile);
			sb.append("', '");
			sb.append(notiListName);
			sb.append("', ");
			sb.append(notification);
			sb.append(", ");
			sb.append(attachResult);
			sb.append(", ");
			sb.append(userAttach);
			sb.append(",'");
			sb.append(emailForm);
			sb.append("', ");
			sb.append(runInfoID);
			// YYYYMMDD, MMDD, DD 와 같은 데이터포맷 삽입
			sb.append(", '");
			sb.append(dformat);
			sb.append("')");
			qry += sb.toString();
			sb = null;
			jdbc.execute(qry);
      
			if(notiListName != null && !notiListName.equals("")){
				//메일링등록
				addMailingList(runInfoID, 0, userID, notiListName);
			}
			//등록된 문서는 현황파악을 위해 RQSCHEDULEDOCSTATUS에 Insert 해준다.
			addRunInfoStatus(""+runInfoID, "Registered");
			return 0;
		}catch (Exception ex){
			errorMsg = ex.toString();
			ex.printStackTrace();
			return -1;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){
			}
			jdbc = null;
			sb = null;
		}
	}

	public short addNotification(String notiID, String userID, int receiverType, String email){
		JDBCHelper jdbc = null;
		StringBuffer sb = null;
		try {
			int scheduleID;
			jdbc = new JDBCHelper();
			jdbc.connect();
			String qry = "Insert into " + env.scheduleNotifictionTableName +" (NotificationID, userID, ReceiverType, Email"
                   + ") values(";
			sb = new StringBuffer(3000);
			sb.append("'");
			sb.append(notiID);
			sb.append("', '");
			sb.append(userID);
			sb.append("', ");
			sb.append(receiverType);
			sb.append(", '");
			sb.append(email);
			sb.append("')");
			qry += sb.toString();
			sb = null;
			jdbc.execute(qry);
			return 0;
		}catch (Exception ex){
			errorMsg = ex.toString();
			ex.printStackTrace();
			return -1;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){
			}
			jdbc = null;
			sb = null;
		}
	}

	public short delete(int schedule) {
		JDBCHelper jdbc = null;
		try {
			jdbc = new JDBCHelper();
		
			jdbc.connect();
			String qry2 =
				"delete from "+ env.scheduleRunInfoTableName + " where ScheduleID = " + schedule;
			jdbc.execute(qry2);
			jdbc.close();
		
			jdbc.connect();
			String qry =
				"delete from " + env.scheduleTimeTableName + " where ScheduleID = " + schedule;
			jdbc.execute(qry);
			jdbc.close();
		
			//delete_Noti_DocStatus(schedule);
		
			jdbc.close();
		
			jdbc = null;
			return 0;
		}catch (Exception ex) {
			errorMsg = ex.toString();
			return RQ_EXCEPTION;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			} catch (Exception ex) {
			}
			jdbc = null;
		}
	}

	public void delete_Noti_DocStatus(int scheduleid){
		
		//
		//String arrRuninfo = "select runinfoid from " + env.scheduleTimeTableName + " where ScheduleID = " + schedule;
		//jdbc.execute(arrRuninfo);
		
	    JDBCHelper jdbc = null;
		try {
			jdbc.connect();
			String qry3 =
				"delete from " + env.scheduleNotifictionTableName + " where NotificationID = '"
					+ scheduleid
					+ "'";
			jdbc.execute(qry3);
			jdbc.close();
			
			jdbc.connect();
			String qry4 =
				"delete from " + env.scheduleDocStatus + " where NotificationID = '"
					+ scheduleid
					+ "'";
			jdbc.execute(qry4);
			jdbc.close();
			jdbc = null;
		}catch (Exception ex){
	      errorMsg = ex.toString();
	    }finally{
	    	try {
	    		if (jdbc != null) {
	    			jdbc.close();
	    		}
	    	}catch (Exception ex) {}
	    	jdbc = null;
	    }
	}

	public short deleteMaillist(String notiListName){
		JDBCHelper jdbc = null;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			String qry = "delete from " + env.scheduleNotifictionTableName +" where NotificationID = '" + notiListName + "'";
			jdbc.execute(qry);
			return 0;
		}catch (Exception ex) {
			errorMsg = ex.toString();
			return RQ_EXCEPTION;
		} finally {
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){
			}
			jdbc = null;
		}
	}

	public short deleteByUser(String userID) {
		JDBCHelper jdbc = null;
		ResultSet rs;
		StringBuffer ids;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			String qry = "Select ScheduleID from " + env.scheduleRunInfoTableName + " where UserID = '" + userID + "'";
			rs = jdbc.getRs(qry);
			ids = new StringBuffer("(''");
			while (rs.next()) {
				String id = rs.getString("ScheduleID");
				ids.append(",'" + RequbeUtil.convertForSQL(id) + "'");
			}
			ids.append(")");
			String qry2 = "delete from " + env.scheduleRunInfoTableName + " where UserID = '" + userID + "'";
			jdbc.execute(qry2);
			String qry3 = "delete from " + env.scheduleNotifictionTableName + " where strSchedule in " + ids.toString();
			jdbc.execute(qry3);
			return 0;
		}catch (Exception ex){
			errorMsg = ex.toString();
			return RQ_EXCEPTION;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){
			}
			jdbc = null;
			rs = null;
			ids = null;
		}
	}

	//  public short getMailingInfo(String
	public short updateMailingInfo(long nScheduleID, long nNotification, Vector strMailingListArray) {
		try {

		}catch (Exception ex) {

		}
		return 0;
	}



	private short checkID(String id, int type) {
		//    RQUserAPI userAPI = new RQUserAPI();
		//    RQGroupAPI groupAPI = new RQGroupAPI();
		//    Vector ret = new Vector();
		//    short res;
		//    if (type == 0) {
		//
		//      res = userAPI.get(id, ret);
		//      errorMsg = userAPI.getError();
		//
		//    }
		//    else {
		//      res = groupAPI.get(id, ret);
		//      errorMsg = groupAPI.getError();
		//    }
		return 0;
	}

	public short getScheduling(java.util.Date schedulingTime, long interval, Vector ret){
		JDBCHelper jdbc = null;
		StringBuffer sb = null;
		try {
			int size;
			//2. nStatus 가 '종료되지 않은 스케줄' 인 스케줄 정보를 가져온다.
			Calendar curDate, tempCal;
			curDate = Calendar.getInstance();
			tempCal = Calendar.getInstance();
			tempCal.setTimeInMillis(System.currentTimeMillis());
			curDate.set(tempCal.get(Calendar.YEAR),tempCal.get(Calendar.MONTH),tempCal.get(Calendar.DAY_OF_MONTH),0,0,0);
			curDate.add(Calendar.DATE, 1);
			String qry = "Select ScheduleID, RepeatType, StartDate, StartTime, DayOfMonth, DayOfWeek, PeriodOfDay,"
					   + " PeriodOfWeek, PeriodOfMonth, OrdinalOfDayType, DayType, RepeatBoundType, RepeatFreq "
					   + " From " + env.scheduleTimeTableName;
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				qry   += " Where (Status = 0 AND " + makeDateString(curDate) + " > StartDate) ";	
			}else{
				qry   += " Where (Status = 0 AND " + makeDateString(curDate) + " > StartDate) ";
			}
					//      +"Group By strSchedule, nRepeatType, dtStartDate, dtStartTime, nDayOfMonth, nDayOfWeek, nPeriodOfDay, "
					//     +"nPeriodOfWeek, nPeriodOfMonth, nOrdinalOfDayType, nDayType, nRepeatBoundType, nRepeatFreq "
			qry       += "Order By StartTime";
			
			jdbc = new JDBCHelper();
			if(jdbc.connect() == false){
				errorMsg = "Connection Error";
			 	L.error(errorMsg);
				ret.add("11");
				ret.add(new Vector(1));
				ret.add(new Vector(1));
				jdbc = null; 
				return RQ_EXCEPTION;
			} 
			ResultSet rs;
			java.sql.Date startDate;
			Calendar cal;
			Time time;
			Vector schedules = new Vector();
			int intVal;
			rs = jdbc.getRs(qry);
			ScheduleInfo si;
			java.sql.Date date;

			while (rs.next()) {
				si = new ScheduleInfo();
				si.scheduleID = rs.getInt(1);
				si.repeatType = rs.getInt(2);
				date = rs.getDate(3);
				cal = Calendar.getInstance();
				cal.setTime(date);
				// msgCal("si.startDate", cal, cal);
				si.startDate = cal;
				
				if( g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle") ){
					time = rs.getTime(4);
				}else{
					String lm_str    = rs.getString(4);
					String[] arr_Tmp = lm_str.split(" ");
					time = Time.valueOf( arr_Tmp[1] );	
				}
				
				cal = Calendar.getInstance();
				cal.setTime(time);
				//msgCal("si.startTime", cal, cal);
				si.startTime = cal;
				si.dayOfMonth = rs.getInt(5);
				si.dayOfWeek = rs.getInt(6);
				si.periodOfDay = rs.getInt(7);
				si.periodOfWeek = rs.getInt(8);
				si.periodOfMonth = rs.getInt(9);
				si.ordinalOfDayType = rs.getInt(10);
				si.dayType = rs.getInt(11);
				si.repeatBoundType = rs.getInt(12);
				si.repeatFreq = rs.getInt(13);
				schedules.add(si);
			}
			rs.close();
			rs = null;
			size = schedules.size();
			Calendar scheduledCal,scheduledCalDate, scheduledCalTime, startCalDate, startCalTime, startCal, endCal;
			Calendar tempCal1, tempCal2;
			long dateSpan, timeSpan1, timeSpan2, timeSpan3;
			startCal = Calendar.getInstance();
			endCal = Calendar.getInstance();
			scheduledCal = Calendar.getInstance();
			scheduledCalDate = Calendar.getInstance();
			scheduledCalTime = Calendar.getInstance();
			tempCal1 = Calendar.getInstance();
			tempCal2 = Calendar.getInstance();
			tempCal1.set(1970,1,1,23,59,59);
			tempCal2.set(1970,1,1,0,0,0);
			scheduledCal.setTime(schedulingTime);
			scheduledCalDate.set(scheduledCal.get(Calendar.YEAR),scheduledCal.get(Calendar.MONTH),scheduledCal.get(Calendar.DAY_OF_MONTH),0,0,0);
			scheduledCalTime.set(1970,1,1,scheduledCal.get(Calendar.HOUR_OF_DAY), scheduledCal.get(Calendar.MINUTE),scheduledCal.get(Calendar.SECOND));
			//   startCal = si.startDate;
			//  startCalDate.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH),0,0,0);
			//   startCal = si.startTime;
			//   startCalTime.set(1970,1,1,startCal.get(Calendar.HOUR),startCal.get(Calendar.MINUTE),startCal.get(Calendar.SECOND));
			//   dateSpan = scheduledCalDate.getTimeInMillis() - startCalDate.getTimeInMillis();
			//   timeSpan1 = startCalTime.getTimeInMillis() - scheduledCalTime.getTimeInMillis();

			Vector idV = new Vector();
			Vector timeV = new Vector();
			startCal.setTime(schedulingTime);
			endCal.setTime(schedulingTime);
			msgCal("Start Time", startCal, startCal);
			endCal.add(Calendar.MILLISECOND,(int)interval );
			msgCal("End Time", endCal, endCal);      
			int monthdiff = 0;
			for (int i = 0; i < size; ++i){
				si = (ScheduleInfo)schedules.get(i);
				switch (si.repeatType){
					case 0: // 되풀이 되지 않는 스케줄

						scheduledCal.set(si.startDate.get(Calendar.YEAR), si.startDate.get(Calendar.MONTH), si.startDate.get(Calendar.DAY_OF_MONTH),si.startTime.get(Calendar.HOUR_OF_DAY),si.startTime.get(Calendar.MINUTE),si.startTime.get(Calendar.SECOND));
						//	          startCal.setTimeInMillis(schedulingTime.getTime());
						//	          endCal.setTimeInMillis(schedulingTime.getTime() + interval);
						if(!(startCal.after(scheduledCal) || endCal.before(scheduledCal))) {
							msgCal("scheduledCal",scheduledCal,scheduledCal);
							idV.add(new Integer(si.scheduleID));
							timeV.add(scheduledCal.getTime());
						}
						break;
					case 1: // 매 ...일 마다

						scheduledCal.set(startCal.get(Calendar.YEAR),startCal.get(Calendar.MONTH),startCal.get(Calendar.DAY_OF_MONTH), si.startTime.get(Calendar.HOUR_OF_DAY),si.startTime.get(Calendar.MINUTE),si.startTime.get(Calendar.SECOND));
						if( startCal.after(scheduledCal)){
							scheduledCal.add(Calendar.DATE, 1);
						}
						if(!(startCal.after(scheduledCal) || endCal.before(scheduledCal))){
							idV.add(new Integer(si.scheduleID));
							timeV.add(scheduledCal.getTime());
						}
						break;
					case 2: // 매 ...주 마다 ...요일에
						scheduledCal.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH),startCal.get(Calendar.DAY_OF_MONTH), si.startTime.get(Calendar.HOUR_OF_DAY),si.startTime.get(Calendar.MINUTE),si.startTime.get(Calendar.SECOND));
						int ordinal = scheduledCal.get(Calendar.DAY_OF_WEEK);
						//int diff = ordinal - si.ordinalOfDayType;
						//			if(ordinal - si.ordinalOfDayType >= 0)
						//			{
						//				scheduledCal.add(Calendar.DATE, diff);
						//			}
						//			else
						//			{
						//				scheduledCal.add(Calendar.DATE, diff + 7);
						//			}
			
						// Check Day Of Week
						int val = 0x0001 << (7 - ordinal);
						if (!(( val & si.dayOfWeek) > 0)){
							break;
						}
			
						// Time Check
						if(!(startCal.after(scheduledCal) || endCal.before(scheduledCal))){
							idV.add(new Integer(si.scheduleID));
							timeV.add(scheduledCal.getTime());
						}
						break;
					case 3: // ...개월마다 ...일에
						//      if(si.startDate.before(scheduledCal))
						//        break;
						monthdiff = (si.startDate.get(Calendar.YEAR) - scheduledCal.get(Calendar.YEAR)) * 12 + si.startDate.get(Calendar.MONTH) - scheduledCal.get(Calendar.MONTH);
						if (monthdiff % si.periodOfMonth != 0)
							break;
						if (!(startCal.get(Calendar.DAY_OF_MONTH) == scheduledCal.get(Calendar.DAY_OF_MONTH) || endCal.get(Calendar.DAY_OF_MONTH) == scheduledCal.get(Calendar.DAY_OF_MONTH))){
							break;
						}
						scheduledCal.set(startCal.get(Calendar.YEAR),startCal.get(Calendar.MONTH),startCal.get(Calendar.DAY_OF_MONTH), si.startTime.get(Calendar.HOUR_OF_DAY),si.startTime.get(Calendar.MINUTE),si.startTime.get(Calendar.SECOND));
						if( startCal.after(scheduledCal)){
							scheduledCal.add(Calendar.DATE, 1);
						}
						if(!(startCal.after(scheduledCal) || endCal.before(scheduledCal))){
							idV.add(new Integer(si.scheduleID));
							timeV.add(scheduledCal.getTime());
						}		  	
						break;
					case 4: // ...개월마다 ...째 ...요일에
						monthdiff = (si.startDate.get(Calendar.YEAR) - scheduledCal.get(Calendar.YEAR)) * 12 + si.startDate.get(Calendar.MONTH) - scheduledCal.get(Calendar.MONTH);
						if (monthdiff % si.periodOfMonth != 0){
							break;            
						}
						break;
				}
			}
			ret.add("11");
			ret.add(idV);
			ret.add(timeV);
			//3-4. 종료된 스케줄의 nStatus 를 [종료됨]으로 바꿔준다.
			Calendar currentDate = Calendar.getInstance();

			currentDate.set(Calendar.HOUR_OF_DAY,0);
			currentDate.set(Calendar.MINUTE,0);
			currentDate.set(Calendar.SECOND,0);
			currentDate.set(Calendar.MILLISECOND,0);
			jdbc.close();
			jdbc.connect();
			String qry2 = "Update " + env.scheduleTimeTableName + " Set Status=1 Where (RepeatType > 0 AND RepeatBoundType = 3 AND " + makeDateString(currentDate) +" > RepeatEndDate) "
			+ "OR (RepeatType > 0 AND RepeatBoundType = 2 AND RepeatFreq = 0) OR (RepeatType = 0 AND StartDate < " + makeDateString(currentDate) +")";
			jdbc.execute(qry2);

			return 0;
		}catch(Exception ex){
			errorMsg = "Scheduling get Error";
			L.error(errorMsg, ex);
			return RQ_EXCEPTION;

		}finally{
    		if(jdbc != null){
    				try{
    					jdbc.close();
    				}catch(Exception e){}
    		}
    		jdbc = null;
		}
	}

    public short getSchedulingList(int schedulid, ArrayList schedules){
    	JDBCHelper jdbc = null;
		ResultSet rs;
		Calendar cal;
		try {
			String qry = "Select ScheduleID, RepeatType, StartTime, StartDate, DayOfMonth, DayOfWeek, PeriodOfDay,"
				+ " PeriodOfWeek, PeriodOfMonth, OrdinalOfDayType, DayType, RepeatBoundType, RepeatFreq "
				+ " From " + env.scheduleTimeTableName;
			if(schedulid != 0){ 
				qry += " Where ScheduleID = " + schedulid;
			}
			qry += " Order By ScheduleID";

			jdbc = new JDBCHelper();
			jdbc.connect();
			rs = jdbc.getRs(qry);
			ScheduleInfo si;
			
			//java.sql.Date date;
			//String lm_strStartTime = "";
			//8String day, month, year, ampm, hour, minute, second = "";

			while (rs.next()){
				si = new ScheduleInfo();
				si.scheduleID = rs.getInt(1);
				si.repeatType = rs.getInt(2);
				si.setStrStartTime(rs.getString(3));
				/*
				if(date != null && !date.equals("")){
				    cal = Calendar.getInstance();
				    cal.setTime(date);
				    si.startTime = cal;
			        day = Integer.toString(cal.get(Calendar.DATE) ); 
			        month = Integer.toString(cal.get(Calendar.MONTH)); 
			        year = Integer.toString(cal.get(Calendar.YEAR));
			        if( cal.get(Calendar.AM_PM) == 0)
			        	ampm = "오전";
			        else
			        	ampm = "오후";
			        hour = String.valueOf(cal.get(Calendar.HOUR));
			        minute = String.valueOf(cal.get(Calendar.MINUTE));
			        second = String.valueOf(cal.get(Calendar.SECOND));
				    lm_strStartTime =  year + "-" + month + "-" + day + " " + ampm + " " + hour + ":" + minute + ":" + second;
				    si.setStrStartTime(lm_strStartTime);
				}
				*/
			    si.dayOfMonth = rs.getInt(5);
			    si.dayOfWeek = rs.getInt(6);
			    si.periodOfDay = rs.getInt(7);
			    si.periodOfWeek = rs.getInt(8);
			    si.periodOfMonth = rs.getInt(9);
			    si.ordinalOfDayType = rs.getInt(10);
			    si.dayType = rs.getInt(11);
			    si.repeatBoundType = rs.getInt(12);
			    si.repeatFreq = rs.getInt(13);
			    schedules.add(si);
			}
			rs.close();
			return 0;
		}catch(Exception ex){
			L.error("Scheduling get Error", ex);
			return RQ_EXCEPTION;
		}finally{
			if(jdbc != null){
				try{    				
					jdbc.close();
				}catch(Exception e){}
			}
			jdbc = null;
	  	}
    }
  
    public short getScheduleRunInfos(int schedule, Vector ret) {
    	JDBCHelper jdbc = null;
    	try {
    		String qry = "SELECT ScheduleID, MailingList, Doc,"
    			+ " Runvar, UserID,"
    			+ " ResultFile,"
    			+ " Notification, AttachResult, UserAttach, EmailForm, RUNINFOID, DFORMAT "
    			+ " FROM " + env.scheduleRunInfoTableName;
    		if (schedule > 0) {
    			qry += " WHERE ScheduleID = " + schedule ;
    		}
    		jdbc = new JDBCHelper();
    		jdbc.connect();
    		ResultSet rs = jdbc.getRs(qry);
    		Vector v = ScheduleRunInfo.getScheduleRunInfos(rs);
    		ret.add("A");
    		ret.add(v);
    	}catch (Exception ex){
    		errorMsg = "Get schedules Error";
    		L.error(errorMsg, ex);
    		return RQ_EXCEPTION;
    	}finally{
    		try {
    			if (jdbc != null) {
    				jdbc.close();
    			}
    		}catch (Exception ex){}
    		jdbc = null;
    	}
    	return 0;
    }
    
    private void msgCal(String s, Calendar date, Calendar time){
    	L.debug(s + ":" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)+ "-" + date.get(Calendar.DAY_OF_MONTH) +" " +time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" +time.get(Calendar.SECOND));
    	return;
    }
    /**
     *
     * @param scheduleID
     * @param docID
     * @param runvar
     * @param userID
     * @param startDate
     * @param startTime
     * @param repeatType
     * @param notification
     * @param dayOfMonth
     * @param dayOfWeek
     * @param periodOfDay
     * @param periodOfWeek
     * @param periodOfMonth
     * @param ordinalOfDayType
     * @param dayType
     * @param repeatFreq
     * @param repeatBoundType
     * @param repeatEndDate
     * @param resultFolderID
     * @param resultFileName
     * @param emailForm
     * @param mailingListArr
     * @return
     */
    public short updateScheduleTime(int scheduleID, Calendar startDate, Calendar startTime, int repeatType,
          int dayOfMonth, int dayOfWeek, int periodOfDay, int periodOfWeek, int periodOfMonth,
          int ordinalOfDayType, int dayType, int notification, int repeatFreq, int repeatBoundType,
          Calendar repeatEndDate) {

    	//user validate
    	//    short res;
    	//    res = checkID(userID, 0);
    	//    if (res < 0) {
    	//      return res;
    	//    }
    	JDBCHelper jdbc = null;
    	StringBuffer qrySet = new StringBuffer(1000);
    	try {
    		jdbc = new JDBCHelper();
    		jdbc.connect();
    		String qry;
			qrySet.append("Update " + env.scheduleTimeTableName +" set");
			qrySet.append(" StartTime = ");
			
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				qrySet.append("to_date('");
				qrySet.append(makeDateString2(startTime));
				qrySet.append("', 'RR/MM/DD HH24:MI:SS')");
				qrySet.append(", ");
			}else{
				qrySet.append("'");
				qrySet.append(makeDateString3(startTime));
				qrySet.append("'");
				qrySet.append(", ");
			}
			
			qrySet.append(" StartDate = ");
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				qrySet.append("to_date('");
				qrySet.append(makeDateString2(startDate));
				qrySet.append("', 'RR/MM/DD HH24:MI:SS')");
				qrySet.append(", ");
			}else{
				qrySet.append("'");
				qrySet.append(makeDateString3(startDate));
				qrySet.append("'");
				qrySet.append(", ");
			}
			
			qrySet.append(" RepeatType = ");
			qrySet.append(repeatType);
			qrySet.append(", Notification = ");
			qrySet.append(notification);
			qrySet.append(", DayOfMonth = ");
			qrySet.append(dayOfMonth);
			qrySet.append(", DayOfWeek = ");
			qrySet.append(dayOfWeek);
			qrySet.append(", PeriodOfDay = ");
			qrySet.append(periodOfDay);
			qrySet.append(", PeriodOfWeek = ");
			qrySet.append(periodOfWeek);
			qrySet.append(", PeriodOfMonth = ");
			qrySet.append(periodOfMonth);
			qrySet.append(", OrdinalOfDayType = ");
			qrySet.append(ordinalOfDayType);
			qrySet.append(", DayType = ");
			qrySet.append(dayType);
			qrySet.append(", RepeatFreq = ");
			qrySet.append(repeatFreq);
			qrySet.append(", RepeatBoundType = ");
			qrySet.append(repeatBoundType);
			qrySet.append(", RepeatEndDate = ");
			
			if(g_env.rqreport_rdbms_name.equalsIgnoreCase("oracle")){
				qrySet.append("to_date('");
				qrySet.append(makeDateString2(repeatEndDate));
				qrySet.append("', 'RR/MM/DD HH24:MI:SS')");
				qrySet.append(", ");
			}else{
				qrySet.append("'");
				qrySet.append(makeDateString3(repeatEndDate));
				qrySet.append("'");
				qrySet.append(", ");
			}
			
			qrySet.append(" Status = 0 ");
			qrySet.append(" where ScheduleID = ");
			qrySet.append(scheduleID);
			qry = qrySet.toString();
			jdbc.execute(qry);
			return 0;
    	}catch (Exception ex) {
    		errorMsg = ex.toString();
    		ex.printStackTrace();
    		return -1;
    	}finally {
    		try {
    			if (jdbc != null) {
    				jdbc.close();
    			}
    		}catch (Exception ex) {}
    		jdbc = null;
   			qrySet = null;
    	}
	
    	//		qrySet = new StringBuffer(500);
    	//		qrySet.append("Update RScheduleRunInfo set");
    	//		qrySet.append(" nDocID = ");
    	//		qrySet.append(docID);
    	//		qrySet.append(",");
    	//		qrySet.append(" strRunVar = '");
    	//		qrySet.append(runvar);
    	//		qrySet.append("' ,");
    	//		qrySet.append("strUser = '");
    	//		qrySet.append(userID);
    	//		qrySet.append("', nResultFolderID = ");
    	//		qrySet.append(resultFolderID);
    	//		qrySet.append(", strResultFileName = '");
    	//		qrySet.append(resultFileName);
    	//		qrySet.append("', strEmailForm = '");
    	//		qrySet.append(emailForm);
    	//		qrySet.append("', nNotification = ");
    	//		qrySet.append(notification);
    	//		qrySet.append(" where nScheduleID = ");
    	//		qrySet.append(scheduleID);
    	//		qry = qrySet.toString();
    	//		jdbc.execute(qry);
    	//		jdbc.close();
    	//		return updateMailInfo(scheduleID, mailingListArr);
    	//
    	//
    	//	}
    	//    catch (Exception ex) {
    	//      errorMsg = "Schedule Update Error";
		//      L.error(errorMsg, ex);
		//      return RQ_EXCEPTION;
		//    }
		//    finally {
		//      try {
		//        if (jdbc != null) {
		//          jdbc.close();
		//        }
		//      }
		//      catch (Exception ex) {
		//      }
		//      jdbc = null;
		//    }
    }
    
    public short updateMailInfo(int scheduleID, String mailArrString){
		//  	JavaVariantArray jva = new JavaVariantArray(mailArrString);
		//    int uBound = jva.getUBound(0);
		//    String schedule = "" + scheduleID;
		//    short res = deleteMaillist(schedule);
		//    for (int i = 0; i < uBound; ++i)
		//    {
		//    	addMailingList(scheduleID, jva.getInt(i,0),jva.getString(i,1), jva.getString(i,2));
		//    }
    	return 0;
    }
    
	/**
	 *
	 * @param scheduleID 스케줄 ID가 0 이면
	 * @param ret
	 * @return
	 */
    public short getSchedules(int scheduleID, Vector ret) {
		//	try {
		//		String qry =
		//			"SELECT a.ScheduleID, b.Doc, b.Runvar, b.User, a.StartTime, a.StartDate,"
		//				+ " a.RepeatType, a.DayOfMonth, a.DayOfWeek, a.PeriodOfDay, a.PeriodOfWeek, a.PeriodOfMonth, "
		//				+ "a.OrdinalOfDayType, a.DayType, b.Notification, a.RepeatFreq, a.RepeatBoundType, a.RepeatEndDate,"
		//				+ " b.nResultFolderID, b.strResultFileName, b.strEmailForm, a.nStatus, c.strDoc"
		//				+ " FROM RScheduleTime  a, RScheduleRunInfo  b, RDoc c"
		//				+ " WHERE a.ScheduleID=b.ScheduleID And b.Doc=c.Doc order by 1";
		//
		//		if (scheduleID > 0) {
		//			qry += " AND nScheduleID=" + scheduleID;
		//		}
		//		JDBCHelper jdbc = new JDBCHelper();
		//		jdbc.connect();
		//		ResultSet rs = jdbc.getRs(qry);
		//		Vector v = ScheduleInfo.getSchedules(rs);
		//		ret.add("A");
		//		ret.add(v);
		//	} catch (Exception ex) {
		//		errorMsg = "Scheduler is not installed!";
		//		L.error(errorMsg, ex);
		//		return RQ_EXCEPTION;
		//	}
    	return 0;
    }
    
    public short getScheduleTimes(int scheduleID, Vector ret){
	  	try {
	  		String qry = "";
	  		if (scheduleID > 0) {
	  			qry += "AND ScheduleID=" + scheduleID;
	  		}
	  		JDBCHelper jdbc = new JDBCHelper();
	  		jdbc.connect();
	  		ResultSet rs= jdbc.getRs(qry);
	
	  	}catch(Exception ex){
			errorMsg = "Scheduler is not installed!";
			L.error(errorMsg, ex);
			return RQ_EXCEPTION;
	  	}
	  	return 0;
    }
    /**
     *  5.7 을 위한 함수
     * @param scheduleID
     * @param ret
     * @return
     */
	public short getMailingList(int scheduleID, Vector ret){
		try {
			String qry = "SELECT NotificationID, UserID,ReceiverType,Email, AttachFile"
			 		 + " FROM " + env.scheduleNotifictionTableName;
			qry += " WHERE NotificationID = '" + scheduleID + "'";

			JDBCHelper jdbc = new JDBCHelper();
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			Vector mi = ScheduleMailInfo.getScheduleMails(rs);
	  		ret.add("A");
			ret.add(mi);
		}catch (Exception ex){
			errorMsg = "Get ScheduleMailInfo Error";
			L.error(errorMsg, ex);
			return RQ_EXCEPTION;
		}
		return getMailInfo("" + scheduleID, ret);
	}
	
	public short getMailInfo(String notiList, Vector ret){
		JDBCHelper jdbc = null;
		try {
			String qry = "SELECT NotificationID, Email"
				+ " FROM " + env.scheduleNotifictionTableName;
			if (notiList != null && notiList.length() > 0) {
				qry += " WHERE NotificationID = '" + notiList + "'";
			}
			jdbc = new JDBCHelper();
			jdbc.connect();
			ResultSet rs = jdbc.getRs(qry);
			MailInfo mi = new MailInfo();
			mi.maillingList = notiList;
			// Set Message
			mi.subject = "Title: Scheduler Test";
			mi.message = "This is test message";
			mi.setMaillingList(rs);
			ret.add("M");
			ret.add(mi);
		}catch (Exception ex){
			errorMsg = "Get MailInfo Error";
			L.error(errorMsg, ex);
			return RQ_EXCEPTION;
		}finally{
			try {
				if (jdbc != null) {
					jdbc.close();
				}
			}catch (Exception ex){}
			jdbc = null;
		}
		return 0;
	}

	public String makeDateString(Calendar cal) {

		// makedateString
		// select to_date('2004 9 30 9 10 11','YYYY MM DD HH24 MI SS') from dual; for Oracle
		// mysql
		// dateFormat = "'%Y-%M-%D %H:%m:%S'"
		// access "#%Y/%M/%D %H:%m:%S#";
		
		String dateFormat = env.timeFormat;
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%Y", Integer.toString(cal.get(Calendar.YEAR)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%M", Integer.toString(cal.get(Calendar.MONTH) + 1));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%D", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%H", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%m", Integer.toString(cal.get(Calendar.MINUTE)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%S", Integer.toString(cal.get(Calendar.SECOND)));
	    return dateFormat;
	}
	
	/**
	 * oracle
	 * @param cal
	 * @return
	 */
	public String makeDateString2(Calendar cal) {
		
		// makedateString
		// select to_date('2004 9 30 9 10 11','YYYY MM DD HH24 MI SS') from dual; for Oracle
		// mysql
		// dateFormat = "'%Y-%M-%D %H:%m:%S'"
		// access "#%Y/%M/%D %H:%m:%S#";

		String dateFormat = env.timeFormat2;
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%Y", Integer.toString(cal.get(Calendar.YEAR)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%M", Integer.toString(cal.get(Calendar.MONTH) + 1));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%D", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%H", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%m", Integer.toString(cal.get(Calendar.MINUTE)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%S", Integer.toString(cal.get(Calendar.SECOND)));
	    return dateFormat;
	}
	
	/**
	 * mysql
	 * @param cal
	 * @return
	 */
	public String makeDateString3(Calendar cal) {
		
		String dateFormat = env.timeFormat3;
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%Y", Integer.toString(cal.get(Calendar.YEAR)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%M", Integer.toString(cal.get(Calendar.MONTH) + 1));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%D", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%H", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%m", Integer.toString(cal.get(Calendar.MINUTE)));
	    dateFormat = RequbeUtil.replaceAll(dateFormat, "%S", Integer.toString(cal.get(Calendar.SECOND)));
	    return dateFormat;
	}
	
	public short getRunInfoList(int scheduleID, ArrayList runinfolist){
		JDBCHelper jdbc = null;
		ResultSet rs;
		Calendar cal;
		try {
			String qry = "SELECT SCHEDULEID, MAILINGLIST, USERID, DOC, RUNVAR, RESULTFILE, " +
					"NOTIFICATION, ATTACHRESULT, USERATTACH, EMAILFORM, RUNINFOID" +
					" FROM " + env.scheduleRunInfoTableName;
					qry += " WHERE SCHEDULEID = '" + scheduleID + "'";
				
			jdbc = new JDBCHelper();
			jdbc.connect();
			rs = jdbc.getRs(qry);
			RQSchedulerListModel lm_rqSchedulerListmodel;

			while (rs.next()){
				lm_rqSchedulerListmodel = new RQSchedulerListModel();
				lm_rqSchedulerListmodel.setScheduleid(rs.getString(1));
				lm_rqSchedulerListmodel.setMailinglist(rs.getString(2));
				lm_rqSchedulerListmodel.setStrUserId(rs.getString(3));
				lm_rqSchedulerListmodel.setDoc(rs.getString(4));
				lm_rqSchedulerListmodel.setRunvards(rs.getString(5));
				lm_rqSchedulerListmodel.setResultfileds(rs.getString(6));
				lm_rqSchedulerListmodel.setNotification(rs.getString(7));
				lm_rqSchedulerListmodel.setAttachresult(rs.getString(8));
				lm_rqSchedulerListmodel.setUserattach(rs.getString(9));
				lm_rqSchedulerListmodel.setEmailform(rs.getString(10));
				lm_rqSchedulerListmodel.setRuninfoid(rs.getInt(11));
				runinfolist.add(lm_rqSchedulerListmodel);
			}
			rs.close();
			return 0;
		}catch(Exception ex){
			L.error("Scheduling get Error", ex);
			return RQ_EXCEPTION;
		}finally{
			if(jdbc != null){
				try{    				
					jdbc.close();
				}catch(Exception e){}
			}
			jdbc = null;
	  	}
	}
  	
  	public static void main(String[] args){
  		RQScheduleAPI lm_oRScheduleAPI = new RQScheduleAPI();
  		ArrayList arr = new ArrayList();
  		//short s = lm_oRScheduleAPI.getSchedulingList(arr);
  		System.out.println(arr.size());
  		
  	}
  	
    public short deleteRunInfo(int runInfo) {
        JDBCHelper jdbc = null;
    	try {
    		jdbc = new JDBCHelper();
    		jdbc.connect();
    		String qry =
    			"delete from "+ env.scheduleRunInfoTableName + " where RunInfoID = " + runInfo;
    		jdbc.execute(qry);
    		jdbc.close();
    		jdbc = null;
    		return 0;
    	}
        catch (Exception ex) {
          errorMsg = ex.toString();
          return RQ_EXCEPTION;
        }
        finally {
          try {
            if (jdbc != null) {
              jdbc.close();
            }
          }
          catch (Exception ex) {
          }
          jdbc = null;
        }
    }
    
    public void addRunInfoStatus(String docid, String flag){
    	jdbc = new JDBCHelper();
    	PreparedStatement pstmt = null;
    	SchedulerEnv senv = SchedulerEnv.getInstance();
    	try{
    		jdbc.connect();
    		String insert_qry = "Insert into " + senv.scheduleRunInfoStatusTableName + 
    		                    " (RUNINFOID, RUNDATE, STATUS) " +
    		                    "Values " +
    		                    " ('" +docid+ "','" + RequbeUtil.makeDateString(Calendar.getInstance(),"%Y%M%D%H%m%S") + "','" +flag+"')";
    		pstmt = jdbc.conn.prepareStatement(insert_qry);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			try {
				if (pstmt != null) pstmt.close();
			}catch(SQLException se)	{
				se.printStackTrace();
			}
			jdbc.close();
		}
    }
    
    public ArrayList getListStatus(String runinfoid){
    	ArrayList arr = new ArrayList();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	SchedulerEnv senv = SchedulerEnv.getInstance();
    	try{
    		String qry = "SELECT RUNINFOID, RUNDATE, STATUS FROM " + senv.scheduleRunInfoStatusTableName + 
    		                    " WHERE RUNINFOID = '" +runinfoid+ "' ORDER BY RUNDATE ASC";
    		jdbc = new JDBCHelper();
    		jdbc.connect();
    		pstmt = jdbc.conn.prepareStatement(qry);
    		rs = pstmt.executeQuery();
			while(rs.next()){
				RQScheduleRunInfoStatus runinfostatus = new RQScheduleRunInfoStatus();
				runinfostatus.setRuninfo(rs.getString(1)); //runinfo
				runinfostatus.setRundate(rs.getString(2)); //rundate
				runinfostatus.setStatus(rs.getString(3));   //status
				arr.add(runinfostatus);
			}
			
    		pstmt.close();
    	}catch (Exception e) {
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			}catch(SQLException se)	{
				se.printStackTrace();
			}
			jdbc.close();
		}
		return arr;
    }
    
    public ScheduleRunInfo getScheduleRunInfo(String runinfoid){
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	ScheduleRunInfo sri = new ScheduleRunInfo();
        try {

        	String qry = "SELECT ScheduleID, MailingList, Doc,"
                + " Runvar, UserID,"
                + " ResultFile,"
                + " Notification, AttachResult, UserAttach, EmailForm, RUNINFOID, DFORMAT "
                + " FROM " + env.scheduleRunInfoTableName;
            qry += " WHERE RUNINFOID = " + runinfoid ;
            jdbc = new JDBCHelper();
			jdbc.connect();
			pstmt = jdbc.conn.prepareStatement(qry);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				sri.schedule = rs.getInt(1);
				sri.mailingList = rs.getString(2);
				sri.doc = rs.getString(3);
				sri.runvar = rs.getString(4);
				sri.user = rs.getString(5);
				sri.resultFileName = rs.getString(6);
				sri.notification = rs.getInt(7);
				sri.attachResult = rs.getInt(8);
				sri.userAttach   = rs.getInt(9);
				sri.emailForm    = rs.getString(10);
				sri.runinfoid    = rs.getString(11);
				sri.dformat      = rs.getString(12);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return sri;
    }
}
