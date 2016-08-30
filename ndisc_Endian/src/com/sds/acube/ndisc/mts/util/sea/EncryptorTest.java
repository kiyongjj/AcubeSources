/**
 * 
 */
package com.sds.acube.ndisc.mts.util.sea;

import com.sds.acube.ndisc.mts.util.sea.EncryptorBySEA;
/**
 * @author 윤병준
 *
 */
public class EncryptorTest {

	public static void main(String[] args){
		String encryptedFileID = null;
		String decryptedFileID = null;
		
		try{
			if(args.length != 1){
				System.out.println("usage : EncryptorTest {File ID}");
				System.exit(0);
			}else{
				EncryptorBySEA ebs = new EncryptorBySEA();
				System.out.println("FileID : " + args[0]);
				encryptedFileID = ebs.EncryptFileID(args[0]);
				System.out.println("encryptedFileID(Stored File Name) : " + encryptedFileID);
				decryptedFileID = ebs.DecryptFileID(encryptedFileID);
				System.out.println("decryptedFileID(Original File Id) : " + decryptedFileID);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}	
		
	}	
	
}
