package com.sds.rqreport.common;
import java.util.*;
//import DataEncode;


public interface RQInfo
{
  /** Info의 형태가 어떻게 구현 되어 있는가 "SSSSS" 스트링의 인자 5개 S: String I: Integer F:Float D: Date
   n번째 인자를 가져오는 함수들 RQInfo를 통해 판단한다. */
  public String getParamTypes();
  /** Field index번째의 정수값을 얻는다. */
  public int getParamInt(int index);  
  /** Field index번째의 float값을 얻는다. */
  public float getParamFloat(int index);
  /** Field index번째의 String값을 얻는다. */
  public String getParamString(int index);
   /** Field index번째의 날짜및 시간값을 얻는다. */
  public Calendar getParamCalendar(int index);
/*  
  public static String makeArrayString(Vector v)
  {
    RQInfo info = (RQInfo)v.get(0);
    String s = info.getParamTypes();
    StringBuffer ret = new StringBuffer(v.size()+","+s.length());
         
    for(int i = 0; i <  v.size();++i)
    {
      info = (RQInfo)v.get(i);
      int j;
      for(j = 0; j < s.length();++j);
      {
        switch(s.charAt(j))
        {
        case 'S':
          String st = info.getParamString(j);
          ret.append("\tS\t");
          String temp = DataEncode.replaceAll(st, "\\","\\\\");
          temp = DataEncode.replaceAll(temp, "\n","\\n");
          temp = DataEncode.replaceAll(temp, "\t","\\t");
          ret.append(temp);
          break;
        case 'I':
          int integer = info.getParamInt(j);
          ret.append("\tI\t");
          ret.append(integer);
          break;
        case 'F':
          float f = info.getParamFloat(j);
          ret.append("\tF\t");
          ret.append(f);
          break;
        case 'D':
          Calendar cal = info.getParamCalendar(j);
          ret.append("\tD\t");
          ret.append(
          cal.get(Calendar.YEAR) + ","
          + cal.get(Calendar.MONTH) + ","
          + cal.get(Calendar.DAY_OF_MONTH) + ","
          + cal.get(Calendar.HOUR_OF_DAY) + ","
          + cal.get(Calendar.MINUTE) + ","
          + cal.get(Calendar.SECOND));
          break;
        }
  
      }
    }
    return ret.toString();
  }
 */
}
