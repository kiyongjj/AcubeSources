package com.sds.acube.ndisc.admin;

import java.util.ArrayList;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.mts.common.NDCommon;

public class NDiscFileAdmin extends NDiscBaseAdmin{
	
	//lsfile
	public void selectFileList(){			
		ArrayList fileList = null;		
		try{			
			fileList = (ArrayList)storage.selectFileInfoList();
			if(fileList != null){
				showFileInfoList(fileList);
			}
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
		
		return ;
	}	
	
	//File Info List 출력 
	private void showFileInfoList(ArrayList fileList){
		int size = fileList.size();
		NFile file = null;
		System.out.println("=======================================================================================");
		System.out.println("File_ID  File_Name  File_Size  Created_Date  Modified_Date  File_Status  File_Media_Id ");		
		System.out.println("=======================================================================================");
		for(int i=0;i<size;i++){
			file = (NFile)fileList.get(i);
			System.out.println(file.getId() + " " + file.getName() + " " + file.getSize() 
					+ " " + file.getCreatedDate() + " " + file.getModifiedDate() + " " + file.getStatType()
					+ " " + file.getMediaId());	
			if(i < size-1){
				System.out.println("---------------------------------------------------------------------------------------");
			}
		}
		System.out.println("=======================================================================================");
		System.out.println(size + " row selected. ");
		return ;
	}
	
	public void selectFileById(String fileId){
		try{			
			NFile file = storage.selectFileInfo(fileId);
			if(file != null){
				showFileInfo(file);
			}
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}		
	}	

	//File Info 출력
	private void showFileInfo(NFile file){
		System.out.println("======================================================");
		System.out.println("File Infomation");
		System.out.println("======================================================");
		System.out.println("File ID : " + file.getId());	
		System.out.println("File Name : " + file.getName());
		System.out.println("File Size : " + file.getSize());
		System.out.println("File Created Date : " + file.getCreatedDate());
		System.out.println("File Modified Date : " + file.getModifiedDate());
		System.out.println("File Status :" + file.getStatType());
		System.out.println("File Media ID :" + file.getMediaId());
		System.out.println("======================================================");		
		return ;
	}
	
	/*
	 *  파일 등록
	 *  성공 시, file id 반환
	 */
	public String regFile(String host, int port, String regFilePath, int regVolId, String regStatType) {
		String ret = null;		
		NFile[] nFile = null;

		try {
			int numOfFiles = 1;
			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setName(regFilePath);
			nFile[0].setVolumeId(regVolId);
			nFile[0].setStatType(regStatType);
			
			napi.NDisc_Connect(host, port);
			
			String[] arrRet = napi.NDISC_FileReg(nFile);
			if (null == arrRet) {
				System.out.println("NDISC_FileReg() return null");
				ret = null;
			} else {
				ret = arrRet[0];
			}
			
		} catch (FileException e) {
			System.out.println(e.getMessage());
			ret = null;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			ret = null;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			ret = null;
		}
		finally{
			try{        	 
	   		 	napi.NDisc_Disconnect();
	       	 }catch(NetworkException ne){
	       		System.out.println(ne.getMessage());        		
	       	 }
		}

		return ret;
	}
	
	/*
	 * 파일 반환
	 */
	public void getFile(String host, int port, String fileId, String destFilePath){
		try{
			int numOfFiles = 1;
			NFile[] nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);
			nFile[0].setName(destFilePath);
			nFile[0].setStatType("-1"); //AUTO
			
			napi.NDisc_Connect(host, port);
			if(napi.NDISC_FileGet(nFile)){
				System.out.println("getFile() successfully completed, file path : " + destFilePath); 
			}else{
				System.out.println("getFile() failed");
			}
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}	
		finally{
			try{        	 
	   		 	napi.NDisc_Disconnect();
	       	 }catch(NetworkException ne){
	       		System.out.println(ne.getMessage());        		
	       	 }
		}
	}
		
	
	public String getFilePathByFileId(String fileId) {
		String ret = null;
		NFile[] nFile = null;
		
		try {		

			nFile = new NFile[1];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);

			nFile = storage.aquireStorageInfo(nFile,NDCommon.STORAGE_PATH_ACCESS);

			ret = nFile[0].getStoragePath();

		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}
	
	
	public void removeFile(String host, int port, String fileId){
		boolean ret = false;		
		NFile[] nFile = null;
		int numOfFiles = 1;
		try {			

			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);			
		
			napi.NDisc_Connect(host, port);
			ret = napi.NDISC_FileDel(nFile);
			
			if (ret == false) {
				System.out.println("Remove File ID : " + fileId + " failed !!!!");				
			} else {
				System.out.println("Remove File ID : " + fileId + " successfully completed !!!");
			}
		} catch (FileException e) {
			System.out.println(e.getMessage());			
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
		}finally{
			 try{
        		 napi.NDisc_Disconnect();
        	 }catch(NetworkException ne){
        		 System.out.println(ne.getMessage());
        	 }			
		}
		
		return ;
	}
}
