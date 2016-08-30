package com.sds.acube.ndisc.mts.util.cipher.jce;

import com.sds.acube.ndisc.mts.common.*;

public class LicenseChecker {
	private SymmetryCipher cipher = null;
	private String licenseString = "";
	public LicenseChecker(){
		try{
		String keyFile = NDCommon.NDISC_CIPHER_KEY;
		cipher = new SymmetryCipher(keyFile);
		
		String serverIp = NDCommon.HOST;
		String port = Integer.toString(NDCommon.PORT);		    
	    
	    licenseString = serverIp + ";" + port;
	    licenseString = cipher.encrypt(licenseString);
	    
		}catch(Exception ex)
		{		
			ex.printStackTrace();			
		}		
	}
	
	public boolean IsValidLicense(String licenseKey){
		boolean bRet = false;
			
		if(licenseKey.equals(licenseString))
		 	return true;
		
		return bRet;
	}
}
