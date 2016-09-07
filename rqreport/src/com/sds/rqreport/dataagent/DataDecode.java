package com.sds.rqreport.dataagent;
import java.io.*;
import java.util.*;


/** 서버에 요청하는 함수이름과 파라미터를 Decode 하는 클래스<br>
 소켓을 통해 들어오는 데이터는 byte Stream 이므로 byte 값을 제외한 부분은 문자열로 변환 하여 사용한다. */
public class DataDecode
{
  /** 소켓을 통해 받아온 바이트 데이터의 Byte Array */
  byte[] data;
  /** 소켓을 통해 받아온 byte Array를 인코딩 값에 의해 변환된 문자열. */
  String dataString = null;
  /** Character Encoding. */
  String enc="UTF-8";
  /** byte data*/
  byte[] bytedata = null;


  /** InputStream 으로부터 보내진 byte 데이타를 얻는다. 첫 15바이트는 받을 데이터의 사이즈가 들어있다. */
  public DataDecode(InputStream is)
  {
    try
    {
      byte sizestr[];
      sizestr = new byte[15];
      // Client가 보낼 데이터 크기를 읽기
      is.read(sizestr);
      String ss = new String(sizestr);
      int size = Integer.parseInt(ss.trim());
      byte a[] = new byte[size];
      // Client가 보낼 데이터 읽기
      int offset = 0,len, pos = 0, totalReceived = 0;
      while(totalReceived < size)
      {
        len = is.read(a, totalReceived, size - totalReceived);
        totalReceived += len;
      }
      data = a;
     }catch (Exception e)
     {
       data = null;
       e.printStackTrace();
     }

  }

  /** byte stream으로 얻어온 byte array를 통해 DataDecode Class를 생성한다. */
  public DataDecode(byte[] data){
     this.data = data;
  }
  public DataDecode(String data)
  {
  	if(data == null)
  	 return;
  	try {
  		
		this.data = data.getBytes("UTF-8");
	} catch (UnsupportedEncodingException e) {
		this.data = data.getBytes();
		e.printStackTrace();
	}
  }
  /** Character Encoding을 정하는 함수 */
  public void setEncoding(String enc)
  {
     this.enc = enc;
  }

  int findPosition(String str){

    return findPosition(str,0);
  }

  int findPosition(String str,int from){
    byte[] cmpstr = str.getBytes();
    int pos = -1;
    boolean find = false;
    for(int i = from; i < data.length - cmpstr.length + 1; i++)
    {
      int j;
      for(j = 0; j < cmpstr.length; j++)
      {
        if(cmpstr[j] != data[i + j])
          break;
        if(j == cmpstr.length - 1)
          find = true;
      }
      if(find)
      {
        pos = i + cmpstr.length;
        break;
      }
    }
    return pos;
  }
  /** byte array로 부터 encoding 값을 얻는다 */
  public String getEncoding(){
    int pos = findPosition("\nENC=\t\n");
    int pos2 = findPosition("\n",pos);
    return new String(data,pos,pos2-pos-1);
  }
  /** 함수의 argument를 타입을 얻어온다. */
  public String getTypes(){
  	if(dataString == null)
    {
      try
      {
        dataString = new String(data,enc);
      }catch(Exception e)
      {
       // L.error("Error in getTypes",e);
       e.printStackTrace();
        return null;
      }
    }
    String key = "\nT=\t\n";
    int pos1 = dataString.indexOf(key);
    int pos2 = dataString.indexOf("\n", pos1+key.length());
    if(pos1 > -1 && pos2 > pos1)
    {
      return convertString(dataString.substring(pos1 + key.length(), pos2));
    }
    else
      return null;
  }
  /** byte array 값을 얻는다. 함수를 통해 요청하는 byte array는 1개만 올 수 있다.*/
  public byte[] getByteData(){
    // For Debug:
   //
    int pos = findPosition("\nDATA=\t\n");

    if(pos != -1)
    {
      int pos2 = findPosition("\n",pos);
      byte ret[] = new byte[data.length - pos2 ];
      System.arraycopy(data, pos2, ret,0,data.length - pos2 );
      try
      {
        dataString = new String(data,0,pos2, enc);
//        System.out.println(dataString);
      }catch(Exception e)
      {
        //L.error("Error occurred in getByteData",e);
        e.printStackTrace();
        return null;
      }
     // L.debug("ByteDataSize:" + ret.length);
      return ret;
    }
    else
    {
      try
      {
      	dataString = new String(data,enc);
//      	System.out.println(dataString);
      }catch(Exception e)
      {
     //   L.error("Parsing Error",e);
     	e.printStackTrace();
      }
      return null;
    }
  }
  public short getRequestType()
  {
    if(dataString == null)
    {
      try
      {
        bytedata = getByteData();
      }catch(Exception e)
      {
       // L.error("Parsing Error",e);
       e.printStackTrace();
        return 0;
      }
    }
    int pos1 = dataString.indexOf("\nType=\t\n");
    int pos2 = dataString.indexOf("\t\n", pos1+8);
    try
    {

      if(pos1 > -1 && pos2 > pos1 + 8)
      {
        short s = Short.parseShort(dataString.substring(pos1+8,pos2));
    //    L.debug("Request:"+s);
        return s;
      }
      else
        return 0;
    }
    catch(Exception e)
    {
 //     L.error("Parsing Error",e);
      e.printStackTrace();
      return 0;
    }


  }
   /**함수이름을 얻는다. */
  public String getFunctionName()
  {
    if(dataString == null)
    {
      try
      {
        bytedata = getByteData();
      }catch(Exception e)
      {
    //    L.error("Parsing Error",e);
        System.out.println(e);
        return null;
      }
    }
    int pos1 = dataString.indexOf("\nFunction=");
    int pos2 = dataString.indexOf("\t\n", pos1+10);

    if(pos1 > -1 && pos2 > pos1)
    {
      return dataString.substring(pos1+10,pos2);
    }
    else
      return null;
  }
   /** 함수의 index번째 인자에 있는 String값을 얻는다.
   @param index 인자의 순서
   @return 문자열 */
  public String getParamString(int index){
    if(dataString == null)
    {
      try
      {
        //dataString = new String(data,enc);
        bytedata = getByteData();
      }catch(Exception e)
      {
 //       L.error("Parsing Error",e);
 		e.printStackTrace();
        return null;
      }
    }
    String key = "\nParam"+index+"=\t\n";
    int pos1 = dataString.indexOf(key);
    int pos2 = dataString.indexOf("\n", pos1+key.length());
    if(pos1 > -1 && pos2 > pos1)
    {
      return convertString(dataString.substring(pos1 + key.length(), pos2));
    }
    else
      return null;
  }
   /** 함수의 index번째 인자에 있는 Integer 값을 얻는다.
   @param index 인자의 순서
   @return Integer값 */
  public Integer getParamInt(int index){
    if(dataString == null)
    {
      try
      {
        //dataString = new String(data,enc);
        bytedata = getByteData();
      }catch(Exception e)
      {
    //    L.error("Parsing Error",e);
    	e.printStackTrace();
        return null;
      }
    }
    String key = "\nParam"+index+"=\t\n";
    int pos1 = dataString.indexOf(key);
    int pos2 = dataString.indexOf("\n", pos1+key.length());
    if(pos1 > -1 && pos2 > pos1)
    {
      return new Integer(dataString.substring(pos1 + key.length(), pos2));
    }
    else
      return null;
  }
   /** 함수의 index번째 인자에 있는 float 값을 얻는다.
   @param index 인자의 순서
   @return Float값 */
  public Float getParamFloat(int index){
    if(dataString == null)
    {
      try
      {
        //dataString = new String(data,enc);
        bytedata = getByteData();
      }catch(Exception e)
      {
       // L.error("Parsing Error",e);
       e.printStackTrace();
        return null;
      }
    }
    String key = "\nParam"+index+"=\t\n";
    int pos1 = dataString.indexOf(key);
    int pos2 = dataString.indexOf("\n", pos1+key.length());
    if(pos1 > -1 && pos2 > pos1)
    {
      return new Float(dataString.substring(pos1 + key.length(), pos2));
    }
    else
      return null;
  }
  /** 인자로 사용하게될 Argument Vector Array를 만들어 받는다.
  @return Argument Object의 Array
  */
  public Vector getArgumentVector()
  {
    String types = getTypes();
    Vector arg = new Vector();
    for(int i = 0; i < types.length();++i)
    {
      switch(types.charAt(i))
      {
         case 'S':
         arg.add(getParamString(i+1));
         break;
         case 'I':
         arg.add(getParamInt(i+1));
         break;
         case 'F':
         arg.add(getParamFloat(i+1));
         break;
         case 'D':
         arg.add(getParamDate(i+1));
         break;
         case 'A':
//       arg.add(getParamArray(i+1));
         break;
         case 'B':
         arg.add(bytedata);
       }
    }

    return arg;
  }

  /** Return Array layout을 얻는다. */
  int[] getRetArrayLayout()
  {
    int[] layout = null;
    String arrayLayout =  getParamString(99);
    if(arrayLayout == null)
    {
      return null;
    }
    StringTokenizer strTokenizer = new StringTokenizer(arrayLayout,",");
    int size = strTokenizer.countTokens();
    if(size < 1)
    {
      return null;
    }
    else
    {
      layout = new int[size];
    }
    int i = 0;
    while(strTokenizer.hasMoreTokens())
    {
      layout[i] = Integer.parseInt((String)strTokenizer.nextToken());
      i++;
    }
    return layout;

  }
  public Calendar getParamDate(int index)
  {
	if(dataString == null)
	   {
		 try
		 {
		   //dataString = new String(data,enc);
		   bytedata = getByteData();
		 }catch(Exception e)
		 {
		   //L.error("Parsing Error",e);
		   e.printStackTrace(); 
		   return null;
		 }
	   }
	   String key = "\nParam"+index+"=\t\n";
	   int pos1 = dataString.indexOf(key);
	   int pos2 = dataString.indexOf("\n", pos1+key.length());
	   if(pos1 > -1 && pos2 > pos1)
	   {
				return getCalendar(dataString.substring(pos1+key.length(), pos2));
	   }
	   else
		   return null;
		 
  }
  // 스트링을 변환한다. 사용할 수 있는 형태로
  String convertString(String str)
  {
    StringBuffer sb = new StringBuffer(str);
    int i = 0;
    while(i < sb.length()){
      if(sb.charAt(i) == '\\')
      {
        if(sb.charAt(i+1)=='\\')
        {
          sb.replace(i,i+2,"\\");
        }
        else if(sb.charAt(i+1) == 'n')
        {
          sb.replace(i,i+2,"\n");
        }
        else if(sb.charAt(i+1) == 't')
        {
          sb.replace(i,i+2,"\t");
        }
      }
      i++;
    }
    return sb.toString();
  }
  /**
   *  To Convert String to Calendar Object
   * @param date String  [year],[month],[day],[hour],[minute],[second]  
   * @return Calendar object
   */
  public Calendar getCalendar(String date)
  {
  	int idx1 = 0, idx2 = 0;
  	int[] calnum;
  	int i = 0;
  	calnum = new int[6];
  	while (idx2 >= 0)
  	{
  		idx2 = date.indexOf("," , idx1);
  		if(idx2 > 0)
			  calnum[i++] = Integer.parseInt(date.substring(idx1,idx2));
	  	else
			  calnum[i++] = Integer.parseInt(date.substring(idx1));
			idx1 = idx2 + 1;
  	} 
  	Calendar cal = Calendar.getInstance();
  	cal.set(calnum[0],calnum[1] - 1,calnum[2],calnum[3],calnum[4],calnum[5]);
  	return cal;
  }
}




