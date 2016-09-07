package com.sds.rqreport.service.web;

import java.util.Calendar;

import org.apache.log4j.Logger;

public class RQLSingleton {
	
	public final static String KEYSTR = "RQREPORT";
	public final static String STRDELIMITER = "|";
	
	private static boolean status = false;
	private static String hostname = "";
	private static String ip = "";
	private static int physicalCPU = 1;
	private static int logicalCPU = 1;
	private static String RQversion = "";
	private static String m_date = "";
	private static RQLSingleton oRQLSingleton = new RQLSingleton(); 
	private Logger log = Logger.getLogger("RQWEB");
	private RQLSingleton(){}
	
	/**
	 * 객체를 외부에서 생성하지 못하도록 public static factory method로 구현.
	 * @param flag true : 객체생성없이 계속 사용 , false : 객체생성, 최초 사용시.
	 * @return
	 */
	public static RQLSingleton getRQLicense(boolean flag){
		if(flag){
			if(oRQLSingleton == null){
				oRQLSingleton = new RQLSingleton();
			}			
		}else{
			oRQLSingleton = new RQLSingleton();
		}

		return oRQLSingleton; 
	}
	
	/**
	 * 정식으로 발급 받은 라이센스 일경우 그 값을 전부 가져오고
	 * 그렇지 않는 경우는 trial 날짜만 가져온다.(나머지값은 전부 null, 0 으로 셋팅)
	 * @param strKey
	 */
	public void setLicenseStatus(String strKey){
		String[] arrStr = strKey.split("\\|");
		if(arrStr.length != 1){
			hostname    = arrStr[0];
			ip          = arrStr[1];
			physicalCPU = Integer.parseInt(arrStr[2]);
			logicalCPU  = Integer.parseInt(arrStr[3]);
			RQversion   = arrStr[4].trim();
			m_date  = arrStr[5].trim();
		}else{
			hostname    = "null";
			ip          = "null";
			physicalCPU = 0;
			logicalCPU  = 0;
			RQversion   = "0";
			m_date  = arrStr[0].trim();
		}
	}
	
	/**
	 * 서버에서 정보를 가져와 등록된 라이센스값들과 체크 한다. 전부 일치할경우
	 * 라이센스 status를 true로 반환한다.
	 * @param p_hostname  
	 * @param p_ip
	 * @param p_physicalCPU
	 * @param p_logicalCPU
	 * @param p_version
	 * @return
	 */
	public boolean checkLicenseStatus(String p_hostname, String p_ip, int p_physicalCPU, int p_logicalCPU, String p_version){
		status = false;
		int maxCPU = physicalCPU * logicalCPU;
		int lm_maxCPU = p_physicalCPU * p_logicalCPU;
		if(p_hostname.equals(hostname) && p_ip.equals(ip) && maxCPU >= lm_maxCPU && p_version.equals(RQversion)){
			status = true;
		}
		return status;
	}
	
	/**
	 * 라이센스 남은 날짜를 계산한다.
	 * @return 라이센스 남은 일자를 반환한다. (날짜-1) 을 반환
	 */
	public int getLeftDays(){
		int rtn = 0;
		long lm_date = Long.parseLong(m_date);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lm_date);
		//log.debug("setting time : " + cal.getTime());
		
		cal.add(Calendar.DATE, 30); //trial 
		long trial_date = cal.getTimeInMillis();
		//log.debug("30 day later : " + cal.getTime());
		
		Calendar now_cal = Calendar.getInstance();
		//log.debug("now day : " + now_cal.getTime());
		
		long ndate = now_cal.getTimeInMillis();
		
		long t = trial_date - ndate;
		double ben = 24 * 60 * 60 * 1000;
		rtn = (int) Math.floor(t/ben);
		return rtn; 
	}
	
	public static boolean isStatus() {
		return status;
	}
	
	public static void setStatus(boolean status) {
		RQLSingleton.status = status;
	}
}
