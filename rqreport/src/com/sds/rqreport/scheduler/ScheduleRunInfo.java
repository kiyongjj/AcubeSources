package com.sds.rqreport.scheduler;

import java.util.*;
import java.io.*;
import java.sql.*;
import com.sds.rqreport.Environment;
import com.sds.rqreport.common.*;

public class ScheduleRunInfo implements RQInfo , Serializable {
  public int schedule;
  public String user = "";
  public String doc = "";
  public int docID;
  public String runvar = "";
  public int regist = 0;
  public int resultFolderID;     //결과 저장할 때 등록할 폴더의 ID
  public String resultFileName = "Top5.rqr";  //결과 저장할 때 등록할 파일 이름
  public String mailingList;

  public int notification = 0;
  public int attachResult = 0;
  public int userAttach = 0;
  public String indexfile = "";
  public String emailForm = ""; 
  public String runinfoid = "";
  public String dformat = "";
  public boolean callAPI = false;
  
  public ScheduleRunInfo()
  {
  	
    return;
  }
  public ScheduleRunInfo(int schedule, String mailingList, int docID, String runvar, String user, String doc, int  regist,int resultFolderID, String resultFileName, int notification, int attachResult, int userAttach)
  {
    this.schedule = schedule;
    this.user = user;
    this.doc = doc;
    //this.docID = docID;
    this.runvar = runvar;
    this.regist = regist;
    this.resultFolderID = resultFolderID;
    this.resultFileName = resultFileName;
    this.mailingList = mailingList;
    this.notification = notification;
    this.attachResult = attachResult;
    this.userAttach = userAttach;
  }
	
	public void setIndexFile(String indexFile)
	{
		this.indexfile = indexFile;
	}
	
  static public Vector getScheduleRunInfos(ResultSet rs) throws SQLException
  {
    Vector vec = new Vector(100,100);

		try
		{
		//	RQDocAPI dapi = new RQDocAPI();
	    while(rs.next())
	    {
	      ScheduleRunInfo sri = new ScheduleRunInfo();
	      sri.schedule = rs.getInt(1);
	      sri.mailingList = rs.getString(2);
	      sri.doc = rs.getString(3);
	      sri.runvar = rs.getString(4);
	      sri.user = rs.getString(5);
	      //sri.resultFolderID = rs.getInt(6);
	      sri.resultFileName = rs.getString(6);
	     
	      // 금감원용
//	      sri.indexfile = Environment.indexFile;
	      
	      
	      if(sri.resultFolderID > 0)
	      {
	        sri.regist = 1;
	      }
	      sri.notification = rs.getInt(7);
	      sri.attachResult = rs.getInt(8);
	      sri.userAttach   = rs.getInt(9);
	      sri.emailForm    = rs.getString(10);
	      sri.runinfoid    = rs.getString(11);
	      sri.dformat      = rs.getString(12);
		  	Vector v = new Vector();
//			  short res = dapi.getDoc(sri.docID, v);
//			  if (ErrorCode.isFailure(res)) {
//					v = null;
//					return res;
//			  }
//				sri.doc = (String)((DocumentInfo)v.get(1)).fullName;
	      vec.add(sri);
	      v = null;
	    }
//	    dapi = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    return vec;
  }

  public String getParamTypes()
  {
    return "";
  }

  public int getParamInt(int index)
  {
    return 0;
  }

  /** Field index번째의 float값을 얻는다. */
  public float getParamFloat(int index)
  {
    return 0F;
  }
  /** Field index번째의 String값을 얻는다. */
  public String getParamString(int index)
  {
    return null;
  }
   /** Field index번째의 날짜및 시간값을 얻는다. */
  public Calendar getParamCalendar(int index)
  {
    return null;
  }

}
