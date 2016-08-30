/**
 * 
 */
package com.sds.acube.ndisc.mts.util.sea;

import com.sds.acube.ndisc.mts.util.sea.EncryptorBySEA;
/**
 * @author 윤병준
 *
 */
public class DecryptorTest {

	public static void main(String[] args){
		String storedFileName = null;
		String fileID = null;
		
		try{
			if(args.length != 1){
				System.out.println("usage : EncryptorTest {File ID}");
				System.exit(0);
			}else{
				EncryptorBySEA ebs = new EncryptorBySEA();
				storedFileName = args[0];
				System.out.println("Stored File Name : " + storedFileName);
				fileID = ebs.DecryptFileID(storedFileName);
				System.out.println("Original File Id : " + fileID);				
			}
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}	
		
	}	
	
}
