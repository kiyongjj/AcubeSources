/**
 * 
 */
package com.sds.acube.ndisc.mts.storage.impl;

/**
 * @author ¿±∫¥¡ÿ
 *
 */
public class VolumeTest {
	
	public static void main(String[] args){
		
		VolumeTest v = new VolumeTest();
		int result = v.insertNewVolumeToDB(args);
		System.out.println("result = " + result);
		System.exit(0);
	}
	
	
	public int insertNewVolumeToDB(String[] args) {		
		int intAccessable = 0;
						
		char[] charAccessable = args[0].toCharArray();
		for(int i=0;i<charAccessable.length;i++){
			if(String.valueOf(charAccessable[i]).equalsIgnoreCase("R")){
				intAccessable = intAccessable + 8;					
			}
			if(String.valueOf(charAccessable[i]).equalsIgnoreCase("C")){
				intAccessable = intAccessable + 4;
			}
			if(String.valueOf(charAccessable[i]).equalsIgnoreCase("U")){
				intAccessable = intAccessable + 2;
			}
			if(String.valueOf(charAccessable[i]).equalsIgnoreCase("D")){
				intAccessable = intAccessable + 1;
			}			
		}	
		
		return intAccessable;
	}

}
