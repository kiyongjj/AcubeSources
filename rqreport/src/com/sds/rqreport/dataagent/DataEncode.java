package com.sds.rqreport.dataagent;
import java.io.*;
import java.util.*;

import com.sds.rqreport.common.*;
import com.sds.rqreport.service.queryexecute.RQGetDataIf;
import com.sds.rqreport.util.*;

/** Client에 return 값을 보내기 위해서 return 값에 해당하는 값들을 인코딩해서 byte array로 보내기 위한 Class */
public class DataEncode
{
  StringBuffer sendstr;
  OutputStream os = null;
  short result;
  int param;
  String type;
  /** 클라이언트에서 만들어 지는 Array의 형태를 결정한다. */
  int[] arrayLayout = null;

  /** DataEncode Object을 생성한다. */
  public DataEncode()
  {
    param = 1;
	type = "";
  }

  /** Output Stream으로 return vector을 인코딩해서 보낸다.
  @param os 아웃풋 스트림

  */
  public DataEncode(OutputStream os)
  {
    param = 1;
    this.os = os;
  }

  public void setArrayLayout(int[] layout)
  {
 //   System.out.println("layout" + layout);
    arrayLayout = layout;
  }

  /** 결과 값을 받아서 보내는 Byte Array를 만든다.
    @param res 결과 코드
    @param type 결과의 타입
    @param ret 리턴값을 담고 있는 백터
  */
//  public void setResponse(short res,String type,Vector ret)
//  {
//    System.out.println("Make return Data from vector ");
//    addResult(res);
//    addType(type);
//    if(res < 0)  // if error occurred
//    {
//
//       addError((String)ret.get(0));
//    }
//    else                 // when succeeded
//    {
//
//       for(int i = 0; i < type.length();++i)
//       {
//           switch(type.charAt(i))
//           {
//             case 'S':
//             addParam((String)ret.get(i));
//             break;
//
//             case 'I':
//             addParam(((Integer)ret.get(i)).intValue());
//             break;
//
//             case 'F':
//             addParam(((Float)ret.get(i)).floatValue());
//             break;
//
//             case 'D':
//             addParam((Calendar)ret.get(i));
//             break;
//
//             case 'O':
//   //          addParam((RQInfo)ret.get(i));
//             break;
//
//             case 'A':
// //            addParam((Vector)ret.get(i));
//             break;
//			 case 'B':
//			 addParam((byte[])ret.get(i));
//			 break;
//           }
//         }
//       }
//  }
  /** stream으로 보낸다. 만들어진 데이터를 보낸다. 먼저 15바이트의 데이터에 보낼 사이즈가 얼마인지를 보낸다.
  */
  public void write() throws IOException
  {

    if(os != null)
    {
      byte[] send = getBytes();
      if(send != null)
      {
        int len = send.length;
        String s = new String(len + "               ");
        //byte[] bs = s.getBytes();
		byte[] bs = s.getBytes();
        os.write(bs,0,15);
        os.write(send);
        os.close();
      }
    }
  }
  /** 결과값을 데이터에 추가한다.
  @param result 결과 code 0 이상이면 성공 음수 값이면 실패 */
  public void addResult(short result)
  {
    this.result = result;
    sendstr = new StringBuffer("\nRES=\t\n" + result + "\n");
    return;
  }

  public void addType(String type)
  {
	sendstr.insert(0,"\nTYPE=\t\n" + type + "\n");
	return;
  }
  /** 문자열인자를 추가한다.
  @param str 문자열
  */
  public void addParam(String str)
  {
	type += "S";
	/*
	String str1 = str;
	if(str == null)
	{
		 	str1 = "";
	}*/
    sendstr.append("\nParam"+ param++ + "=\t\n");
    
    int n = str.length();
    //StringBuffer sb = new StringBuffer(n + n / 10);
    int idx = 0;
    int from = 0, end = 0;
    while(idx < n)
    {
    	char ch = str.charAt(idx);
    	if(ch == '\\' || ch == '\n' || ch == '\t')
    	{
    		end = idx;
    		sendstr.append(str.substring(from, end));
    		from = end + 1;
    		switch(ch)
    		{
    		case '\\':
    			sendstr.append("\\\\");
    			break;
    		case '\n':
    			sendstr.append("\\n");
    			break;
    		case '\t':
    			sendstr.append("\\t");
    			break;
    		}
    	}
    	idx++;
    }
    sendstr.append(str.substring(from));
//    String temp;
//    temp = replaceAll(str1, "\\","\\\\");
//    temp = replaceAll(temp, "\n","\\n");
//    temp = replaceAll(temp, "\t","\\t");
//    sendstr.append(temp);
    //sendstr.append("\n"); // end of DataSet
  }

  /** 정수인자를 추가한다.
  @param integer 정수
  */
  public void addParam(int integer)
  {
  	type += "I";
    sendstr.append("\nParam"+ param++ + "=\t\n" + integer + "\n");
  }

  /** float 인자를 추가한다.
  @param ft float
  */
  public void addParam(float ft)
  {
  	type += "F";
    sendstr.append("\nParam"+ param++ + "=\t\n" + ft + "\n");
  }

  /** RQinfo에 대한 배열 인자를 추가한다.
  RQinfo를 implement한 인자에 대한 배열을 자동생성한다.
  @param v v는 RQInfo interface를 가지고 있는 class의 객체의 배열이 들어 올 수 있다.
  */
 public void addParam(AbstractList v) {
   type += "A";
   if (v.size() < 1) {
     sendstr.append("\nParam" + param++ +"=\t\n0,0\t\n\n\n");
     return;
   }
   RQInfo info = (RQInfo) v.get(0);
   String s = info.getParamTypes();
   StringBuffer ret = new StringBuffer("\nParam" + param++ +"=\t\n");

   if (arrayLayout == null || arrayLayout.length < 1) {
     ret.append(s.length() + "," + v.size());

     for (int i = 0; i < v.size(); ++i) {
       info = (RQInfo) v.get(i);
       int j;
       for (j = 0; j < s.length(); ++j) {
         switch (s.charAt(j)) {
           case 'S':
             String st = info.getParamString(j);
             if(st == null)
             {
               st = "";
             }
             ret.append("\tS\t");
             String temp = DataEncode.replaceAll(st, "\\", "\\\\");
             temp = DataEncode.replaceAll(temp, "\n", "\\n");
             temp = DataEncode.replaceAll(temp, "\t", "\\t");
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
         } // switch
       } // for j
     } //for i

   }
   else {
     ret.append(arrayLayout.length + "," + v.size());
     for (int i = 0; i < v.size(); ++i) {
       info = (RQInfo) v.get(i);

       for (int j = 0; j < arrayLayout.length; ++j) {

         int idx = arrayLayout[j];
         switch (s.charAt(idx)) {
           case 'S':
             String st = info.getParamString(idx);
             if(st == null)
             {
               st = "";
             }
             ret.append("\tS\t");
             String temp = DataEncode.replaceAll(st, "\\", "\\\\");
             temp = DataEncode.replaceAll(temp, "\n", "\\n");
             temp = DataEncode.replaceAll(temp, "\t", "\\t");
             ret.append(temp);
             break;
           case 'I':
             int integer = info.getParamInt(idx);
             ret.append("\tI\t");
             ret.append(integer);
             break;
           case 'F':
             float f = info.getParamFloat(idx);
             ret.append("\tF\t");
             ret.append(f);
             break;
           case 'D':
             Calendar cal = info.getParamCalendar(idx);
             ret.append("\tD\t");
             ret.append(
                 cal.get(Calendar.YEAR) + ","
                 + (cal.get(Calendar.MONTH) + 1) + ","
                 + cal.get(Calendar.DAY_OF_MONTH) + ","
                 + cal.get(Calendar.HOUR_OF_DAY) + ","
                 + cal.get(Calendar.MINUTE) + ","
                 + cal.get(Calendar.SECOND));
             break;
         } // switch
       } // for j
     } //for i
   }
      ret.append("\t\n\n\n");
  // 	System.out.println(ret.toString());
   sendstr.append(ret.toString());
  }

  /** 날짜 시간에 관한 인자를 추가한다.
  @param cal 시간과 날짜
  */
  public void addParam(Calendar cal)
  {
  	  type += "C";
      String str = cal.get(Calendar.YEAR) + ","
          + cal.get(Calendar.MONTH) + ","
          + cal.get(Calendar.DAY_OF_MONTH) + ","
          + cal.get(Calendar.HOUR_OF_DAY) + ","
          + cal.get(Calendar.MINUTE) + ","
          + cal.get(Calendar.SECOND);
      sendstr.append("\nParam"+ param++ + "=\t\n" + str + "\n");
  }
  
  public void addParam(byte[] data)
  {
  	type += "B";
	sendstr.append("\nParam"+ param++ + "=\t\n" + Base64Encoder.encode(data) + "\n");
  }

  /** RQInfo를 implement한 객체에 대하여 모든 Field를 추가한다.
  @param info RQInfo를 implement한 객체
  */
//  public void addParam(RQInfo info)
//  {
//    String paramtypes = info.getParamTypes();
//    int i;
//    for(i = 0;i < paramtypes.length();++i)
//    {
//      switch(paramtypes.charAt(i))
//      {
//        case 'S':
//          String st = info.getParamString(i);
//          addParam(st);
//          break;
//        case 'I':
//          int integer = info.getParamInt(i);
//          addParam(integer);
//          break;
//        case 'F':
//          float f = info.getParamFloat(i);
//          addParam(f);
//          break;
//        case 'D':
//          Calendar cal = info.getParamCalendar(i);
//          addParam(cal);
//          break;
//        }
//      }
//    }
  /** 에러메시지를 추가한다.
  @param err 에러메시지
  */
  public void addError(String err)
  {
    sendstr.append("\nERR=\t\n");
    String temp;
    temp = replaceAll(err, "\\","\\\\");
    temp = replaceAll(temp, "\n","\\n");
    temp = replaceAll(temp, "\t","\\t");
    sendstr.append(temp);
    sendstr.append("\n");
  }

  /** ByteStream으로 보내기 위한 ByteArray를 얻는다.
  @return byte array
  */
  public byte[] getBytes()
  {
    try
    {
      if(sendstr != null)
      {
//		if(Environment.adminserverClient_enc != null && Environment.adminserverClient_enc.length() > 0)
//		{
//			if(Environment.adminserverClient_enc.equalsIgnoreCase("default"))
//			{
//				return sendstr.toString().getBytes();
//			}
//			else
//				return sendstr.toString().getBytes(Environment.adminserverClient_enc);
//		}
//		else
		return sendstr.toString().getBytes("UTF8");
		
  	    
      }
      else
        return null;
    }catch(Exception e)
    {
      return null;
    }
  }
  
  public String getString()
  {
	addType(type);
  	return sendstr.toString();
  }
  
  public String getStringWithLen()
  {
	  addType(type);
	  String lm_strIMG = "\nIMG=\t\n";
	  int ipos = sendstr.toString().indexOf(RQGetDataIf.BLOB_SEP);
	  if(ipos != -1){
		  int insertedStringleng = 8 + Integer.toString(ipos).length(); // \nIMG=\t\n56\n --> 8 + 2
		  lm_strIMG += ( insertedStringleng + ipos + "\n");
	  }else{
		  lm_strIMG += ( "-1" + "\n");
	  }
	  sendstr.insert(0, lm_strIMG);
	  
	  return sendstr.toString();
  }

  // Make return Data from vector

  public static String replaceAll(String src, String from, String to)
  {
//    if(src == null)
//    {
//      src = "";
//    }
//
//    StringBuffer sb = new StringBuffer(src);
//    int pos = 0;
//    int num = 0;
//
//    int dis = to.length() - from.length();
//    while((pos = src.indexOf(from,pos)) > -1){
//
//      if(dis > 0)
//      {
//        sb.replace( pos+ num * dis, pos + ++num * dis,to);
//      }
//      else
//      {
//        sb.replace(pos + (num + 1) * dis, pos + num++ * dis,to);
//      }
//      pos++;
//    }
//    return sb.toString();
	  if(to.equals("\\\\"))
			to = "\\\\\\\\";
	  else if(to.equals("\\n"))
	  {
		  to = "\\\\n";
	  }else if(to.equals("\\t"))
	  {
		  to = "\\\\t";
	  }
	  
	  if(from.equals("\n"))
	  {
		 from = "\\n";
		 
	  }else if(from.equals("\t"))
	  {
		  from = "\\t";
		  
	  }else if(from.equals("\\"))
	  {
		  from = "\\\\";
	  }

  	return src.replaceAll(from,to);
  }

}