/**
 * 
 */
package com.sds.acube.ndisc.mts.util.sea;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.sds.acube.ndisc.mts.common.NDCommon;

/**
 * @author 윤병준
 *
 */
public class EncryptorBySEA {	
	
	/*
	 *      天上天下唯我獨存 (천상천하유아독존)
	 */
	private char[] keyVal = {
			0xf4, 0xb8, 0xdf, 0xbe, 0xf4, 0xb8, 0xf9, 0xbb,
			0xea, 0xe6, 0xe4, 0xb2, 0xd4, 0xbc, 0xf0, 0xed
			};


	/**
	 *      謀事在人成事在天 (모사재인성사재천)
	 *      @since 20160722 파일 암복호화용 (STOR의 파일 암복호화 키값)
	 */
	private char[] keyValForFile = {
			0xd9, 0xc7, 0xde, 0xc0, 0xee, 0xa4, 0xec, 0xd1,
			0xe0, 0xf7, 0xde, 0xc0, 0xee, 0xa4, 0xf4, 0xb8
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

	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @throws Exception
	 * @since 20160722
	 */
	public void encryptFile(String infile, String outfile) throws Exception {
		char[] chBinData = null;
		byte[] buffer = new byte[NDCommon.FILE_TRANS_BUFFER_SIZE];
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			sea.SEA128_SetKey(keyVal);
			
	        fis = new FileInputStream(infile);
	        fos = new FileOutputStream(outfile);
			//System.out.println("Total file size to read (in bytes) : " + fis.available());
	
	        int length;
	        while ((length = fis.read(buffer)) != -1) {	        	
	        	String text1 = new String(buffer, "ISO-8859-1"); 	        	
	        	char[] chars = text1.toCharArray();
	        	
	        	chBinData = sea.SEA128_Encrypt(chars, length / 8);	        	

	        	byte[] bindata = new String(chBinData).getBytes("ISO-8859-1");	
	            fos.write(bindata, 0, length);
	        }
		} finally {
			fis.close();
	        fos.close();
		}
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @throws Exception
	 * @since 20160722
	 */
	public void decryptFile(String infile, String outfile) throws Exception{
		
		char[] chBinData = null;
		byte[] buffer = new byte[NDCommon.FILE_TRANS_BUFFER_SIZE];
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			sea.SEA128_SetKey(keyVal);
			
	        fis = new FileInputStream(infile);
	        fos = new FileOutputStream(outfile);
	
			//System.out.println("Total file size to read (in bytes) : " + fis.available());
	
	        int length;
	        while((length = fis.read(buffer)) != -1) {	        	
	        	String text1 = new String(buffer, "ISO-8859-1"); 	        	
	        	char[] chars = text1.toCharArray();
	        	
	        	chBinData = sea.SEA128_Decrypt(chars, length / 8);
	        	
	            byte[] bindata = new String(chBinData).getBytes("ISO-8859-1");	
	            fos.write(bindata,0,length);
	        }
		} finally {
			fis.close();
	        fos.close();
		}
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
