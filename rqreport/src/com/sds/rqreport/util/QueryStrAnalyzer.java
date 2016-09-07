package com.sds.rqreport.util;

import com.sds.rqreport.Environment;

public class QueryStrAnalyzer {
	String qry;
	String charSet = "KSC5601";
	
	public QueryStrAnalyzer() {
	  }
	  public QueryStrAnalyzer(String qry)
	  {
		this.qry = qry;
	  }

	  
	  public QueryStrAnalyzer(String qry, String charSet)
	  {

		this.qry = qry;
	    this.charSet = charSet;
	  }

	  public void setCharSet(String charSet)
	  {
	    this.charSet = charSet;
	  }
	  public static int pow(int n, int p) {
	    int ret = 1;
	    for (int i = 0; i < p; ++i)
	      ret *= n;
	    return ret;
	  }

	  public static String decrypt(String str)
	  {
	    int len = str.length();
	    char strBuffer[] = new char[len];
	    int i = 0;
	    int n = 0;
	    int p = 0;
	    while (i < len) {
	      boolean end = false;
	      char wcs = '\0';
	      p = 0;
	      while (end == false) {
	        char s = str.charAt(i++);
	        if ('a' <= s && s <= 'z') {
	          wcs += pow(26, p++) * (s - 'a');
	          end = true;
	        }
	        else
	          wcs += pow(26, p++) * (s - 'A');
	      }
	      strBuffer[n++] = wcs;
	    }
	    strBuffer[n] = '\0';
	    String ret = new String(strBuffer,0,n);
	    return ret;
	  }
	  public static void main(String args[]) {
	    if (args.length > 1)
	    {
	      String s = getParameter(args[0], args[1], "KSC5601");
	      System.out.println(s);
	    }
	    else
	    {
	      if(args.length == 1)
	      {
	        String s = decrypt(args[0]);

	        System.out.println(s);
	      }
	    }
	    return;
	  }
	  
	  public String getParameter(String name)
	  {
	    return getParameter(this.qry,name, this.charSet);
	  }
	  
	  public static String getParameter(String qry, String name, String charSet)
	  {
	    String firstString = name + "=";
	    String retVal = null;
	    if(qry != null)
	    {
	      int firstIndex = -1;
	      do
	      {
	           firstIndex = qry.indexOf(firstString,firstIndex +1);
		      if (firstIndex < 0)
		      {
		        return null;
		      }
		      else if (firstIndex == 0)
		      	break;
	      }while(qry.charAt(firstIndex - 1) != '&' && qry.charAt(firstIndex - 1) != '?');
	      int secondIndex = qry.indexOf("&",firstIndex + firstString.length());
	      if (secondIndex < 0)
	      {
	        if(qry.indexOf("=", firstIndex + firstString.length()) > 0)
	          return null;
	        secondIndex = qry.length();
	      }
	      retVal = qry.substring(firstIndex + firstString.length(), secondIndex);
	    }
	    return urlDecode(retVal, charSet);
	  }
	  public static String urlDecode(String str, String charSet)
	  {
		// PDSS_GHR
		return str;
		/*
	    int length = str.length();
	    StringBuffer sb = new StringBuffer(length);
	    byte ansiStr[]  = new byte[length * 2];
	    int idx1 = 0,idx2 = 0;
	    while(idx1 < length)
	    {
	      char ch = str.charAt(idx1);
	      if (ch != '%')
	      {
	        if(idx2 > 0)
	        {
	          String cov = null;
	          try
	          {
	            cov = new String(ansiStr, 0, idx2, charSet);
	          }catch(Exception e)
	          {
	            cov = "";
	          }
	          sb.append(cov);
	          idx2 = 0;
	        }
	        sb.append(ch);
	      }
	      else
	      {
	        byte byteCh = 0;
	        try
	        {
	          byteCh = Byte.parseByte(str.substring(idx1 + 1, idx1 + 3), 16);
	          idx1 += 2;
	        }catch(Exception e)
	        {
	          byteCh = (byte)'%';
	          //idx2 = 0;
	          idx1++;
	        }
	        ansiStr[idx2++] = byteCh;
	      }
	      idx1++;
	    }
	    return sb.toString();
	    */
	  }
}
