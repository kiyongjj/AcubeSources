package com.sds.rqreport.service.web;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.util.Decrypter;
import com.sds.rqreport.util.RequbeUtil;

/**
 * 라이센스가 없을경우 생성하고 서버에 등록된 라이센스 정보를 RQLSingleton에 저장한다.
 * 라이센스를 로딩하는 작업을 다른곳(RQUser 이외)에서 사용하기 위해 RQUser 에서 분리됨. 
 * @author 
 *
 */
public class RQLCheckMain {
	
	private Logger log = Logger.getLogger("RQWEB");
	
    /**
     * 라이센스를 로딩하는 시점은 (현재(2007.12) Spec.상) 
     * 1. rqviewer.jsp 와 같이 뷰어포함하는 페이지를 실행할때 (loadL())
     * 2. new_document.jsp에서 문서를 등록할때 (loadL())
     * 그외의 부분이 필요할경우 해당 페이지에 <rquser:rqUser action="loadL"/> 부분만 
     * Javacript 에 넣어주면 된다.
     */
    public void loadLicense(HttpServletRequest request){
    	String strPath_inf = request.getSession().getServletContext().getRealPath("WEB-INF/classes/com/sds/rqreport/service");
    	RQLSingleton osingle = RQLSingleton.getRQLicense(true);
    	
    	String strEncKey = "";
		try {
			FileReader fr = new FileReader(strPath_inf + "/RQSerLi.class");
			BufferedReader br = new BufferedReader(fr);
			String line = "";	
			while((line = br.readLine()) != null){
				strEncKey = line;
			}
		}catch (FileNotFoundException e){
			licenseCreateifNotexist(e, strPath_inf);
			return;
		}catch (IOException e){
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		String strDecStr = "";
		Decrypter dec = new Decrypter(RQLSingleton.KEYSTR);
		if(!strEncKey.startsWith("0")){
			strDecStr = dec.decrypt(strEncKey);
		}else{
			strDecStr = strEncKey.substring(1);
		}
		//log.debug("strDecStr : " + strDecStr);
		osingle.setLicenseStatus(strDecStr);
		boolean lm_status = checkLicense();
		//log.debug("lm_status : " + lm_status);
		//log.debug("osingle.getLeftDays() : " + osingle.getLeftDays());
    }
    
    /**
     * 라이센스를 확인하고 그값을 RQLSingleton에 가지고 있는다.
     * @return 라이센스를 가지고 있는지 확인한다.
     */
    public boolean checkLicense(){
    	boolean isLicense = false;
    	Environment env = Environment.getInstance();
    	Runtime     rtime    = Runtime.getRuntime();
    	String lm_hostname = "";
    	String lm_ip = "";
    	int lm_physicalCPU = 1;
    	int lm_logicalCPU = 1;
    	String lm_version = "0.0";
    	try {
			InetAddress address  = InetAddress.getLocalHost();
			lm_hostname    = address.getHostName();
			lm_ip          = address.getHostAddress();
			lm_logicalCPU  = rtime.availableProcessors();
			lm_version     = env.rqreport_server_version;
		} catch (UnknownHostException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
		RQLSingleton osingle = RQLSingleton.getRQLicense(true);
		isLicense = osingle.checkLicenseStatus(lm_hostname, lm_ip, lm_physicalCPU, lm_logicalCPU, lm_version);
		
    	return isLicense;
    }
    
    /**
     * create license if not exist
     * @param e FileNotFoundException
     * @param strPath_inf path
     */
    public void licenseCreateifNotexist(FileNotFoundException e, String strPath_inf){
    	log.error("license file does not exist :" + e);
		log.debug("license create");
		RQLSingleton osingle = RQLSingleton.getRQLicense(false);
		
		Calendar calendar = Calendar.getInstance();
		String lm_Date = "" + calendar.getTimeInMillis();
		String lm_strKey = "0" + lm_Date;

        try {
        	FileWriter fos = new FileWriter(strPath_inf + "/RQSerLi.class", false);
	        fos.write(lm_strKey);
			fos.close();
		} catch (IOException e1) {
			RequbeUtil.do_PrintStackTrace(log, e1);
		}
		String lm_str = "";
		try{
			FileReader fr = new FileReader(strPath_inf + "/RQSerLi.class");
			BufferedReader br = new BufferedReader(fr);
			String line = "";	
			while((line = br.readLine()) != null){
				lm_str = line;
			}
		}catch(FileNotFoundException fnfe){
			RequbeUtil.do_PrintStackTrace(log, fnfe);
		}catch(IOException ioe){
			RequbeUtil.do_PrintStackTrace(log, ioe);
		}
		//log.debug("strDecStr : " + strDecStr);
		osingle.setLicenseStatus(lm_str.substring(1));
		checkLicense();
    }
}
