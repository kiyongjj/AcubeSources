package com.sds.rqreport.service.web;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.log4j.*;

import com.sds.rqreport.Environment;
import com.sds.rqreport.util.*;

/**
 * 다국어 버전을 위해 서버 Resource 를 담당하는 Class
 * 키값은 .properties 에 있는 키와 일치시킨다.
 * RQResource.java
 *
 */
public class RQResource{

	private static final long serialVersionUID = 1L;
	// resource env
	public static String rqlocale = "ko";
	private Logger log = Logger.getLogger("RQWEB");
	// raw data
	public Properties prop = null;
	// encorded data
	public Hashtable ht = null; 
	
	public String m_RQCharset = "";
	public String m_ServerCharset = "";

	/**
	 * singleton로 구성 
	 */
	private static RQResource rqresource = null;
	private RQResource(){}
	
	public static RQResource getInstance(){
		if(rqresource == null){
			rqresource = new RQResource();
		}
		return rqresource;
	}
	
	/**
	 * locale 를 받아 각 locale에 맞는 Resource를 불러온다.
	 * @param locale ko, en, ch, jp 로 받을수 있으며 
	 *        각각 ko=korean, en=english, ch=china, jp=japan 이다.
	 *        기본값은 ko 이다.
	 * @return
	 */
	public boolean getLocale(){
		
		if(ht != null) return false;
		Environment env = Environment.getInstance();
		env.load();
		
		prop = new Properties(); 
		InputStream ins = null;
		
		try{
			if(env.rqreport_server_locale.equals("ko")){
				URL url = this.getClass().getClassLoader().getResource("RQResource_ko.properties");
				ins = url.openStream();
			}else if(env.rqreport_server_locale.equals("en")){
				URL url = this.getClass().getClassLoader().getResource("RQResource_en.properties");
				ins = url.openStream();
			}else if(env.rqreport_server_locale.equals("ch")){
				// no define
			}else if(env.rqreport_server_locale.equals("jp")){
				// no define
			}
			prop.load(ins); 
			
		}catch(FileNotFoundException fe){
			RequbeUtil.do_PrintStackTrace(log, fe);
		}catch(IOException ioe){
			RequbeUtil.do_PrintStackTrace(log, ioe);
		}finally{
			try {ins.close();} catch (IOException e) {} 
		}
		
		return true;
	}
	
	/**
	 * Resource를 불러온다. 최초 한번만 불러 오도록 하며
	 * 이미 있는경우는 생략한다. (리소스를 다시 불러오기위해서는 서버리스타트 혹은 리디플로이가 필요)
	 * @return
	 */
	public boolean load(){
		if(ht != null) return false;
		Environment env = Environment.getInstance();
		m_RQCharset = env.rqreport_server_RQcharset;
		m_ServerCharset = env.rqreport_server_charset;
		String lm_resource_from = env.resource_from;
		String lm_resource_to   = env.resource_to;
		

		// hashtable initial for encoding data
		ht = new Hashtable();
		
		Set set = prop.keySet();
		Iterator keyit = set.iterator();
		
		String lm_keystr = "";
		String lm_valuestr = "";
		while(keyit.hasNext()){
			lm_keystr = (String) keyit.next();
			lm_valuestr = prop.getProperty(lm_keystr, "");
			// Resource encoding converting
			lm_valuestr = Encoding.chCharset(lm_valuestr, lm_resource_from, lm_resource_to);
			ht.put(lm_keystr, lm_valuestr);
		}
		
		return true;
	}
}
