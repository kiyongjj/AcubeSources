/**
 * 
 */
package com.sds.acube.ndisc.mts.util.sea;

import com.sds.acube.ndisc.mts.util.sea.Sea128;

/**
 * @author 윤병준
 *
 */
public class EncryptorBySEA {	
	
	private char[] keyVal = {
			0xf4, 0xb8, 0xdf, 0xbe, 0xf4, 0xb8, 0xf9, 0xbb,
			0xea, 0xe6, 0xe4, 0xb2, 0xd4, 0xbc, 0xf0, 0xed
			};
	
	private Sea128 sea = new Sea128(); 	
	
	public String EncryptFileID(String lpszFileID){
		char[] chBinData = null;
		String lpszEncryptedID = null;
		sea.SEA128_SetKey(keyVal);

		chBinData = ConvertLiteralIDToBin(lpszFileID);
		
		chBinData = sea.SEA128_Encrypt(chBinData, 2);
		
		lpszEncryptedID = ConvertBinToLiteralID(chBinData);

		return lpszEncryptedID; 	
	}
	
	public String DecryptFileID(String lpszFileID){
		char[] chBinData = null;
		String lpszDecryptedID = null;
		sea.SEA128_SetKey(keyVal);
		
		chBinData = ConvertLiteralIDToBin(lpszFileID);
		
		chBinData = sea.SEA128_Decrypt(chBinData, 2);
		
		lpszDecryptedID = ConvertBinToLiteralID(chBinData);
		
		return lpszDecryptedID;
		
	}
	
	
	private char[] ConvertLiteralIDToBin(String lpszFileID)
	{
		int	i;
		char[] pchBinData = new char[16];

		for (i = 0; i < 16; i++)
		{			
			int temp = Integer.valueOf(lpszFileID.substring(2*i, 2*i+2),16).intValue();
			pchBinData[i] = (char)temp;			
		}

		return pchBinData;
	}	
	
	
	private String ConvertBinToLiteralID(char[] pchBinData){
			
		String pszFileID = null;
		StringBuffer pszFileIDBuffer = new StringBuffer();		
		
		for(int i=0;i<16;i++){
			String temp = Integer.toHexString(pchBinData[i]);
			if(temp.length() < 2){
				temp = "0" + temp;
			}
			pszFileIDBuffer.append(temp);		
		}	
		
		pszFileID = pszFileIDBuffer.toString();
		//System.out.println(pszFileIDBuffer.toString());
		
		return pszFileID;		
	}
}
