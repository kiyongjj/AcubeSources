package com.sds.rqreport.util;
import java.util.*;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.*;

public class Encoding {
	
	public static String m_ServerCharset = "8859_1";
	public static String m_RQCharset = "EUC-KR";
	
	private static Logger L= Logger.getLogger("ENCODE");

  public static String chCharset(String str, String charset) {
    String temp = null;
    try {
  
      if (charset == null || charset.equals("")) {
        return str;
      }
	  
      if (str == null) {
        return "";
      }
      else {
		if (charset.equals("encoded"))
		{
		  return Decrypter.decrypt26(str);
		}      	
        temp = new String(str.getBytes("8859_1"), charset);
      }
    }
    catch (Exception e) {
      L.error("encoding to "+charset, e);
    }
    return temp;
  }

  public static String chReCharset(String str, String charset) {
    String temp = null;
    try {
      if (str == null) {
        return "";
      }
      else {
        temp = new String(str.getBytes(charset), "8859_1");
      }
    }
    catch (Exception e) {
      L.error("encoding to 8859_1", e);
    }
    return temp;
  }

  public static String chCharset(String str, String inCharset, String outCharset) {
    String temp = null;
    try {
      if (inCharset.equals("encoded"))
      {
      	return Decrypter.decrypt26(str);
      }
      if (str == null) {
        return "";
      }
      if ( inCharset == null)
        return str;
      if (inCharset.equalsIgnoreCase(outCharset)) {
        return str;
      }
      else {
        temp = new String(str.getBytes(inCharset), outCharset);
        //L.debug(inCharset  + " : " + str + " -> " + outCharset + ":" + temp);
      }
    }
    catch (Exception e) {
      L.error("encoding "+inCharset+" to " + outCharset, e);
    }
    return temp;
  }
  	
  	/**
  	 * inCharset 로 받은 케릭터셋을 outCharset 으로 바꿔 반환한다. 
  	 * @param str
  	 * @param inCharset
  	 * @param outCharset
  	 * @return
  	 * @throws UnsupportedEncodingException
  	 */
  	public static String[] chCharset(String str[], String inCharset, String outCharset) throws UnsupportedEncodingException {
    	String[] lm_returnArray = new String[str.length];
    	for(int i = 0 ; i < str.length; i++){
    		lm_returnArray[i] = new String(str[i].getBytes(inCharset), outCharset);
    	}
    	return lm_returnArray;
  	}
    /**
     * 8859_1 을 EUC-KR로 바꾼다.
     * @param p_oHan 
     * @return 한글로된 String 으로 리턴 
     * @throws UnsupportedEncodingException
     */
    public static String hanToEuc_kr(String p_oHan) throws UnsupportedEncodingException{  
    	return new String(p_oHan.getBytes("8859_1"), "EUC-KR" );
    }
    
    /**
     * 8859_1 을 EUC-KR로 바꿔 배열로 리턴한다.
     * @param p_oHan 배열 
     * @return 한글로된 String 배열로 리턴
     * @throws UnsupportedEncodingException
     */
    public static String[] hanToEuc_kr(String[] p_oHan) throws UnsupportedEncodingException{
    	String[] lm_returnArray = new String[p_oHan.length];
    	for(int i = 0 ; i < p_oHan.length; i++){
    		lm_returnArray[i] = new String(p_oHan[i].getBytes("8859_1"), "EUC-KR");
    	}
    	return lm_returnArray;
    }
    
    /**
     * 8859_1 을 UTF-8 로 바꾼다.
     * @param p_oHan 
     * @return 한글로된 String 으로 리턴 
     * @throws UnsupportedEncodingException
     */
    public static String hanToUTF8(String p_oHan) throws UnsupportedEncodingException{  
    	return new String(p_oHan.getBytes("8859_1"), "UTF-8" );
    }
    
    /**
     * 8859_1 을 UTF-8로 바꿔 배열로 리턴한다.
     * @param p_oHan 배열 
     * @return 한글로된 String 배열로 리턴
     * @throws UnsupportedEncodingException
     */
    public static String[] hanToUTF8(String[] p_oHan) throws UnsupportedEncodingException{
    	String[] lm_returnArray = new String[p_oHan.length];
    	for(int i = 0 ; i < p_oHan.length; i++){
    		lm_returnArray[i] = new String(p_oHan[i].getBytes("8859_1"), "UTF-8");
    	}
    	return lm_returnArray;
    }
    
    public String getServerCharset(){
    	
		ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
		Enumeration lm_oenm = lm_oRb.getKeys();
		String lm_tmp = "";
		while(lm_oenm.hasMoreElements()){
			lm_tmp = (String) lm_oenm.nextElement();
			if(lm_tmp.equals("rqreport.server.charset")){
				m_ServerCharset = lm_oRb.getString("rqreport.server.charset");
			}
		}
    	return Encoding.m_ServerCharset;
    }
    
    public String getRQCharset(){
    	
		ResourceBundle lm_oRb = ResourceBundle.getBundle("rqreport");
		Enumeration lm_oenm = lm_oRb.getKeys();
		String lm_tmp = "";
		while(lm_oenm.hasMoreElements()){
			lm_tmp = (String) lm_oenm.nextElement();
			if(lm_tmp.equals("rqreport.server.RQcharset")){
				m_RQCharset = lm_oRb.getString("rqreport.server.RQcharset");
			}
		}
    	return Encoding.m_RQCharset;
    }
}