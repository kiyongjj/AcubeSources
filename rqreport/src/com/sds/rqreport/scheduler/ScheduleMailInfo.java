package com.sds.rqreport.scheduler;

import java.io.Serializable;
import java.util.*;
import java.sql.*;
import com.sds.rqreport.util.*;
import com.sds.rqreport.common.*;

public class ScheduleMailInfo implements RQInfo, Serializable {

  String notificationID;
  int scheduleID;
  String userID;
  int receiverType;
  String email;
  String name;
  String attachFile;
  public ScheduleMailInfo(String notificationID,int scheduleID,String userID,int receiverType,String email,String name,String attachFile)
  {
  	this.notificationID = notificationID;
  	this.scheduleID = scheduleID;
  	this.userID = userID;
  	this.receiverType = receiverType;
  	this.email = email;
  	this.name = name;
  }
  
  public ScheduleMailInfo(ResultSet rs1)
  {
  	try {
			notificationID = rs1.getString(1);
			userID = rs1.getString(2);
			receiverType = rs1.getInt(3);
			email = rs1.getString(4);
			attachFile = rs1.getString(5);
			short res = 0;
//			RQUserAPI userAPI = new RQUserAPI();
//			Vector ret = new Vector();
//			res = userAPI.get(this.userID, ret);
//			if(ErrorCode.isSuccess(res))
//			{
//					UserInfo ui = (UserInfo) ret.get(1);
					//name = ui.name;
//			}
			
	  	try
	  	{
		  	scheduleID = Integer.parseInt(notificationID);
	  	}catch(Exception e)
	  	{
	  		scheduleID = 0;
	  	}
  	}catch(SQLException e)
  	{
  		e.printStackTrace();
  	}
  }
	/* (비Javadoc)
	 * @see com.sds.reqube.repository.RQInfo#getParamTypes()
	 */
	public String getParamTypes() {
				return "ISISS";
	}

	/* (비Javadoc)
	 * @see com.sds.reqube.repository.RQInfo#getParamInt(int)
	 */
	public int getParamInt(int index) {
		int ret = 0;
		switch(index)
		{
			case 0:
			ret = scheduleID;
			break;
			case 2:
			ret = receiverType;
			break;
		}
		return ret;
	}

	/* (비Javadoc)
	 * @see com.sds.reqube.repository.RQInfo#getParamFloat(int)
	 */
	public float getParamFloat(int index) {
		return 0;
	}

	/* (비Javadoc)
	 * @see com.sds.reqube.repository.RQInfo#getParamString(int)
	 */
	public String getParamString(int index) {
		String ret = "";
		switch(index)
		{
		case 1:
		  ret = userID;
		  break;
		case 3:
		  ret = email;
		  break;
		case 4:
		  ret = name;
		  break;
		}
		return ret;
	}

	/* (비Javadoc)
	 * @see com.sds.reqube.repository.RQInfo#getParamCalendar(int)
	 */
	public Calendar getParamCalendar(int index) {

		return null;
	}
	public static Vector getScheduleMails(ResultSet rs1) throws SQLException
	{
	  Vector vec = new Vector(100,100);
	  while(rs1.next())
	  {
			ScheduleMailInfo sm = new ScheduleMailInfo(rs1);
			vec.add(sm);
	  }
	  return vec;
	}
}
