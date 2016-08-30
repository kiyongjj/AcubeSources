package com.sds.acube.ndisc.admin;

import java.util.ArrayList;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.cipher.jce.SymmetryCipher;
import com.sds.acube.ndisc.mts.util.loader.DynamicClassLoader;
import com.sds.acube.ndisc.napi.NApi;

/**
 * @author David.Cho
 * 
 */
public class NDiscAdminUtil_old {

	static SymmetryCipher cipher = null;

	static LoggerIF logger = null;

	static {
		try {
			cipher = new SymmetryCipher(NDCommon.NDISC_CIPHER_KEY);
			logger = (LoggerIF) DynamicClassLoader
					.createInstance(NDCommon.MTS_LOGGER);
			logger.initLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static final String TITLE = "\n[USAGE for ACUBE DM Admin Utility(Ver 1.5)]\n";
	/*
	 * VOLUME
	 */
	static final String USAGE_MKVOL = "NDiscAdminUtil mkvol {host} {port} {Volume Name} {Volume Access Authority : R(Read)C(Create)U(Update)D(Delete)} {Description}";
	static final String USAGE_LSVOL = "NDiscAdminUtil lsvol (Volum Id)";
	static final String USAGE_RMVOL = "NDiscAdminUtil rmvol {host} {port} (Volum Id)";
	
	/*
	 * MEDIA
	 */
	static final String USAGE_MKMEDIA = "NDiscAdminUtil mkmedia {host} {port} {Media Name} {Media Type : 1(HDD) 2(OD) 3(CD) 4(DVD) } {Media Path} {Description} {Max Size} {Volume Id}";
	static final String USAGE_LSMEDIA = "NDiscAdminUtil lsmedia (Media Id)";
	
	/*
	 * FILE
	 */
	static final String USAGE_LSFILE = "NDiscAdminUtil lsfile (File Id)";
	static final String USAGE_FILEREG = "NDiscAdminUtil filereg {host} {port} {regist file path} {volume id} {stat type}";
	static final String USAGE_FILEGET = "NDiscAdminUtil fileget {host} {port} {get file id} {get file path} {reverse stat type}";
	static final String USAGE_WHFILE = "NDiscAdminUtil whfile {file id}";
	static final String USAGE_RMFILE = "NDiscAdminUtil rmfile {host} {port} {file id}";
	
	/*
	 * ID
	 */
	static final String USAGE_IDENC = "NDiscAdminUtil idenc {id}";
	static final String USAGE_IDDEC = "NDiscAdminUtil iddec {id}";
	
	public static void main(String args[]) {

		if (0 == args.length) {	
			
			System.out.println("\n");
			System.out.println(TITLE);
			
			System.out.println(USAGE_MKVOL);
			System.out.println(USAGE_LSVOL);
			System.out.println(USAGE_RMVOL);
			
			System.out.println(USAGE_MKMEDIA);			
			System.out.println(USAGE_LSMEDIA);
			
			System.out.println(USAGE_LSFILE);
			System.out.println(USAGE_FILEREG);
			System.out.println(USAGE_FILEGET);
			System.out.println(USAGE_WHFILE);
			System.out.println(USAGE_RMFILE);					
			
			System.out.println("\n");
			return;
		}
		
		NDiscAdminUtil_old admin = new NDiscAdminUtil_old();
		String COMMAND = args[0].toLowerCase();		
		
		/*
		 * VOLUME  
		 */
		if ("mkvol".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (6 != args.length) {
				System.out.println("USAGE : " + USAGE_MKVOL);
			} else {
				admin.makeVolume(args);
			}
		}
		else if ("lsvol".equals(COMMAND)){ 
			System.out.println("args.length : " + args.length);
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSVOL);
			}else{
				admin.selectVolumeList(args);
			}		
		}
		else if ("rmvol".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (6 != args.length) {
				System.out.println("USAGE : " + USAGE_MKVOL);
			} else {
				admin.removeVolume(args);
			}			
		}
		
		/*
		 * MEDIA
		 */
		else if ("mkmedia".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (9 != args.length) {
				System.out.println("USAGE : " + USAGE_MKMEDIA);
			} else {
				admin.makeMedia(args);
			}
		}		
		else if ("lsmedia".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSMEDIA);
			}else{
				admin.selectMediaList(args);
			}
		}
		
		/*
		 * FILE
		 */		
		else if ("lsfile".equals(COMMAND)){
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSFILE);
			}else{
				admin.selectFileList(args);
			}			
		}
		else if ("filereg".equals(COMMAND)) {
			if (6 != args.length) {
				System.out.println("USAGE : " + USAGE_FILEREG);
			} else {
				System.out.println("registered file id : "
						+ admin.regFile(args));
			}
		} 
		else if ("fileget".equals(COMMAND)) { //구현 미 완
			if (6 != args.length) {
				System.out.println("USAGE : " + USAGE_FILEGET);
			} else {
				// System.out.println("get file path : " + admin.getFile(args));
			}
		}
		else if ("whfile".equals(COMMAND)) {
			if (2 != args.length) {
				System.out.println("USAGE : " + USAGE_WHFILE);
			} else {
				System.out.println("file path : "
						+ admin.getFilePathFromNDiscID(args));
			}
		}
		else if ("rmfile".equals(COMMAND)){
			if (4 != args.length){
				System.out.println("USAGE : " + USAGE_RMFILE);
			}else{
				admin.removeFile(args);
			}		
		}
		
		/*
		 * ID
		 */
		else if ("idenc".equals(COMMAND)){
			if (2 != args.length){
				System.out.println("USAGE : " + USAGE_IDENC);
			}else{
				admin.getIdEnc(args);
			}		
		}
		else if ("iddec".equals(COMMAND)){
			if (2 != args.length){
				System.out.println("USAGE : " + USAGE_IDDEC);
			}else{
				admin.getIdDec(args);
			}		
		}			
		
		/*
		 * FAIL
		 */
		else {
			System.out.println("FATAL ERROR : illegal command");
		}		
	}

	/**
	 * Id 를 인코딩해서 반환한다
	 * 
	 * @param Id -
	 *            인코딩할 Id
	 * @return 인코딩된 Id
	 */
	public String getIdEnc(String[] args) {
		String ret = null;
		String id = args[1];

		try {
			ret = cipher.encrypt(id);

		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	/**
	 * Id 를 디코딩해서 반환한다
	 * 
	 * @param Id -
	 *            디코딩할 Id
	 * @return - 디코딩된 Id
	 */
	public String getIdDec(String[] args) {
		String ret = null;
		String id = args[1];

		try {
			ret = cipher.decrypt(id);

		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	
	public String getFilePathFromNDiscID(String[] args) {
		String ret = null;
		String id = args[1];

		StorageIF storage = null;

		NFile[] nFile = null;
		try {

			storage = (StorageIF) DynamicClassLoader.createInstance(
					NDCommon.MTS_STORAGE, logger);

			nFile = new NFile[1];
			nFile[0] = new NFile();
			nFile[0].setId(id);

			nFile = storage.aquireStorageInfo(nFile,
					NDCommon.STORAGE_PATH_ACCESS);

			ret = nFile[0].getStoragePath();

		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	 
	//filereg : 파일을 등록한다. 
	private String regFile(String[] args) {
		String ret = null;

		String host = null;
		int port = -1;
		String regFilePath = null;
		int regVolId = -1;
		String regStatType = null;

		int numOfFiles = -1;

		NFile[] nFile = null;

		try {
			host = args[1];
			port = Integer.parseInt(args[2]);
			regFilePath = args[3];
			regVolId = Integer.parseInt(args[4]);
			regStatType = args[5];

			numOfFiles = 1;

			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();

			nFile[0].setName(regFilePath);
			nFile[0].setVolumeId(regVolId);
			nFile[0].setStatType(regStatType);

			NApi napi = new NApi(host, port);

			String[] arrRet = napi.NDISC_FileRegEx(nFile);
			if (null == arrRet) {
				System.out.println("NDISC_FileRegEx() return null");
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

		return ret;
	}
		
	
	//rmfile : ID를 입력 받아 등록된 파일을 삭제 한다. 
	public void removeFile(String[] args){
		boolean ret = false;
		String host = null;
		int port = -1;
		String fileId = null;
		
		NFile[] nFile = null;
		int numOfFiles = 1;
		NApi napi = null;

		try {
			host = args[1].trim();
			port = Integer.parseInt(args[2].trim());
			fileId = args[3].trim();

			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();
			nFile[0].setId(fileId);
			
			napi = new NApi();
			napi.NDisc_Connect(host, port);
			ret = napi.NDISC_FileDel(nFile);
			
			if (ret == false) {
				System.out.println("Remove File ID : " + fileId + "has been failed !!!!");				
			} else {
				System.out.println("Remove File ID : " + fileId + "successfully completed !!!");
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
	
	
	//mkvol : Volume을 생성한다. 
	public void makeVolume(String[] args){
		NApi napi = null;
		String host = args[1].trim();
		int port = Integer.parseInt(args[2].trim());
		String name = args[3].trim();
		String accessable = args[4].trim();
		String desc = args[5].trim();		
		
		try{		
			napi = new NApi();
			napi.NDisc_Connect(host, port);
	        
	        Volume volume = new Volume();
	        volume.setName(name);
	        volume.setAccessable(accessable);
	        volume.setDesc(desc);
	      	        
	        if(napi.NDISC_MakeVolume(volume)){
	        	System.out.println("makeVolume() successfully completed !!!!");	        	
	        }else{
	        	System.out.println("makeVolume() failed !!!!");
	        }
         }catch(Exception ex){
        	 System.out.println(ex.getMessage());        
        }finally{
        	 try{        		 
        		 napi.NDisc_Disconnect();
        	 }catch(NetworkException ne){
        		 System.out.println(ne.getMessage());
        	 }
         }       
        		
		return ;
	}
	
	//mkmedia : Media를 생성한다. 
	public void makeMedia(String[] args){
		NApi napi = null;
		String host = args[1].trim();
		int port = Integer.parseInt(args[2].trim());
		String name = args[3].trim();
		int type = Integer.parseInt(args[4].trim());
		String path = args[5].trim();
		String desc = args[6].trim();
		int maxSize = Integer.parseInt(args[7].trim());
		int volumeId = Integer.parseInt(args[8].trim());
		
		try{		
			Media media = new Media();
            media.setName(name);
            media.setType(type);
            media.setPath(path);
            media.setDesc(desc);
            media.setMaxSize(maxSize);
            media.setVolumeId(volumeId);
                       
            napi = new NApi();
			napi.NDisc_Connect(host, port);
            if(napi.NDISC_MakeMedia(media)){
            	System.out.println("mkmedia() successfully completed !!!!");
            }else{
            	System.out.println("mkmedia() failed !!!!");
            }		
         }catch(Exception ex){
        	 System.out.println(ex.getMessage());        	 
         }finally{
        	 try{        	 
        		 napi.NDisc_Disconnect();
        	 }catch(NetworkException ne){
        		 System.out.println(ne.getMessage());        		
        	 }
         }        		
		return ;
	}
	

	//===================================================================================================================================
	
	//Volume Info List 출력 
	private void showVolumeInfoList(ArrayList volumelist){
		int size = volumelist.size();
		Volume volume = null;
		System.out.println("======================================================");
		System.out.println("Volume_ID  Volume_Name  Access  Create_Date  Desc ");		
		System.out.println("======================================================");
		
		for(int i=0;i<size;i++){
			volume = (Volume)volumelist.get(i);
			System.out.println(volume.getId() + " " + volume.getName() + " " + volume.getAccessable()
					+ " " + volume.getCreatedDate() + " " + volume.getDesc());
			
			if(i < size-1){
				System.out.println("------------------------------------------------------");
			}
		}
		System.out.println("======================================================");
		System.out.println(size + " rows selected. ");
		return ;
	}

	//Volume Info 출력
	private void showVolumeInfo(Volume volume){
		System.out.println("======================================================");
		System.out.println("Volume Infomation");
		System.out.println("======================================================");
		System.out.println("Volume ID : " + volume.getId());	
		System.out.println("Volume Name : " + volume.getName());
		System.out.println("Volume Accessabe : " + volume.getAccessable());
		System.out.println("Volume Create Date : " + volume.getCreatedDate());
		System.out.println("Volume Description :" + volume.getDesc());
		System.out.println("======================================================");		
		return ;
	}
	
	//lsvol 
	public void selectVolumeList(String args[]){
		StorageIF storage = null;
		int volumeId = 0;
		Volume volume = null;
		ArrayList volumelist = null;
		
		try{
			storage = (StorageIF) DynamicClassLoader.createInstance(
					NDCommon.MTS_STORAGE, logger);
						
			if(args.length == 2){ // volume Id 가 입력 된 경우 해당 id를 가진 Volume 정보 출력 
				volumeId = Integer.parseInt(args[1].trim());
				volume = storage.selectVolumeInfo(volumeId);				
				showVolumeInfo(volume);
			}else{ // volume id 가 입력 되지 않은 경우, 전체 Volume 정보 리스트 출력
				volumelist = (ArrayList)storage.selectVolumeInfoList();
				showVolumeInfoList(volumelist);			
			}		
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
		
		return ;
	}
	
	//===================================================================================================================================
	
	//Media Info List 출력 
	private void showMediaInfoList(ArrayList mediaList){
		int size = mediaList.size();
		Media media = null;
		System.out.println("=============================================================================================");
		System.out.println("Media_ID  Media_Name  Type  Path  Create_Date  Desc  Max_Size  Media_Size  Media_Volume_Id ");		
		System.out.println("=============================================================================================");
		for(int i=0;i<size;i++){
			media = (Media)mediaList.get(i);
			System.out.println(media.getId() + " " + media.getName() + " " + media.getType() 
					+ " " + media.getPath() + " " + media.getCreatedDate() + " " + media.getDesc()
					+ " " + media.getMaxSize() + " " + media.getSize() + " " + media.getVolumeId());	
			if(i < size-1){
				System.out.println("---------------------------------------------------------------------------------------------");
			}
		}
		System.out.println("=============================================================================================");
		System.out.println(size + " row selected. ");
		return ;
	}

	//Media Info 출력
	private void showMediaInfo(Media media){
		System.out.println("======================================================");
		System.out.println("Media Infomation");
		System.out.println("======================================================");
		System.out.println("Media ID : " + media.getId());	
		System.out.println("Media Name : " + media.getName());
		System.out.println("Media Type : " + media.getType());
		System.out.println("Media Path : " + media.getPath());
		System.out.println("Media Create Date : " + media.getCreatedDate());
		System.out.println("Media Description :" + media.getDesc());
		System.out.println("Media Max Size :" + media.getMaxSize());
		System.out.println("Media Size :" + media.getSize());
		System.out.println("Media Volume ID :" + media.getVolumeId());
		System.out.println("======================================================");		
		return ;
	}
	
	
	//lsmedia
	public void selectMediaList(String args[]){
		StorageIF storage = null;
		int mediaId = 0;
		Media media = null;
		ArrayList mediaList = null;
		
		try{
			storage = (StorageIF) DynamicClassLoader.createInstance(
					NDCommon.MTS_STORAGE, logger);
						
			if(args.length == 2){ // medialId 가 입력 된 경우 해당 id를 가진 Media 정보 출력 
				mediaId = Integer.parseInt(args[1].trim());
				media = storage.selectMediaInfo(mediaId);				
				showMediaInfo(media);
			}else{ // medialId id 가 입력 되지 않은 경우, 전체 media 정보 리스트 출력
				mediaList = (ArrayList)storage.selectMediaInfoList();
				showMediaInfoList(mediaList);			
			}		
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
		
		return ;
	}
	
	//===================================================================================================================================
	
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
	
	
	//lsfile
	public void selectFileList(String args[]){
		StorageIF storage = null;
		String fileId = null;
		NFile file = null;
		ArrayList fileList = null;
		
		try{
			storage = (StorageIF) DynamicClassLoader.createInstance(
					NDCommon.MTS_STORAGE, logger);
						
			if(args.length == 2){ // fileId 가 입력 된 경우 해당 id를 가진 file 정보 출력 
				fileId = args[1].trim();
				file = storage.selectFileInfo(fileId);				
				showFileInfo(file);
			}else{ // medialId id 가 입력 되지 않은 경우, 전체 media 정보 리스트 출력
				fileList = (ArrayList)storage.selectFileInfoList();
				showFileInfoList(fileList);			
			}		
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
		
		return ;
	}	
	
	//===================================================================================================================================
	
	//rmvol
	public void removeVolume(String args[]){
		int ret = 1;
		String host = null;
		int port = -1;
		String volumeId = null;
		
		Volume volume = null;
		NApi napi = null;

		try {
			host = args[1].trim();
			port = Integer.parseInt(args[2].trim());
			volumeId = args[3].trim();
		    
			volume = new Volume();
			volume.setId(Integer.parseInt(volumeId));
			
			napi = new NApi();
			napi.NDisc_Connect(host, port);
			ret = napi.NDISC_VolumeDel(volume);
						
			
			if (ret == 0) { // success
				System.out.println("Remove Volume ID : " + volumeId + "successfully completed !!!");				
			} else if (ret == 2) { // fail				
				System.out.println("Remove Volume ID : " + volumeId + "has been failed !!!!");
			} else if (ret == 3){ // Volume has medias
				System.out.println("Remove has been failed. Volume ID : " + volumeId + "has medias. !!!!");
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
	

	/*// 개발 중 (20071029)
	private String getFile(String[] args) {
		String ret = null;
		String host = null;
		int port = -1;
		String getFileID = null;
		String getFilePath = null;
		String getStatType = null;

		int numOfFiles = -1;

		NFile[] nFile = null;

		try {
			host = args[1];
			port = Integer.parseInt(args[2]);
			getFileID = args[3];
			regVolId = Integer.parseInt(args[4]);
			regStatType = args[5];

			numOfFiles = 1;

			nFile = new NFile[numOfFiles];
			nFile[0] = new NFile();

			nFile[0].setName(regFilePath);
			nFile[0].setVolumeId(regVolId);
			nFile[0].setStatType(regStatType);

			NApi napi = new NApi(host, port);

			String[] arrRet = napi.NDISC_FileRegEx(nFile);
			if (null == arrRet) {
				System.out.println("NDISC_FileRegEx() return null");
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

		return ret;
	}*/
	
}
