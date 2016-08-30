package com.sds.acube.ndisc.admin;

import com.sds.acube.ndisc.mts.util.cipher.jce.SymmetryCipher;
import com.sds.acube.ndisc.mts.common.NDCommon;

/**
 *
 * 
 */
public class NDiscAdminUtil {	

	static final String TITLE = "\n[USAGE for ACUBE DM Admin Utility(Ver 1.5)]\n";
	/*
	 * VOLUME
	 */
	static final String USAGE_MKVOL = "NDiscAdminUtil mkvol {Volume Name} {Volume Access Authority : R(Read)C(Create)U(Update)D(Delete)} {Description}";
	static final String USAGE_LSVOL = "NDiscAdminUtil lsvol (Volum Id)";
	static final String USAGE_RMVOL = "NDiscAdminUtil rmvol (Volum Id)";
	static final String USAGE_CHVOL = "NDiscAdminUtil chvol {Volum Id} {Volume Name} {Volume Access Authority : R(Read)C(Create)U(Update)D(Delete)} {Description}";
	
	/*
	 * MEDIA
	 */
	static final String USAGE_MKMEDIA = "NDiscAdminUtil mkmedia {host} {port} {Media Name} {Media Type : 1(HDD) 2(OD) 3(CD) 4(DVD) } {Media Path} {Description} {Max Size} {Volume Id}";
	static final String USAGE_LSMEDIA = "NDiscAdminUtil lsmedia (Media Id)";
	static final String USAGE_RMMEDIA = "NDiscAdminUtil rmmedia {Media Id}";
	static final String USAGE_CHMEDIA = "NDiscAdminUtil chmedia {Media Id} {Media Name} {Media Type : 1(HDD) 2(OD) 3(CD) 4(DVD)} {Media Path} {Description} {Max Size} {Volume Id} ";
	
		
	/*
	 * FILE
	 */
	static final String USAGE_LSFILE = "NDiscAdminUtil lsfile (File Id)";
	static final String USAGE_REGFILE = "NDiscAdminUtil regfile {host} {port} {regist file path} {volume id}";
	static final String USAGE_GETFILE = "NDiscAdminUtil getfile {host} {port} {file id} {destination file path}";
	static final String USAGE_WHFILE = "NDiscAdminUtil whfile {file id}";
	static final String USAGE_RMFILE = "NDiscAdminUtil rmfile {host} {port} {file id}";
	
	/*
	 * ID
	 */
	static final String USAGE_IDENC = "NDiscAdminUtil idenc {id}";
	static final String USAGE_IDDEC = "NDiscAdminUtil iddec {id}";	
	
	public static void main(String args[]) {
		NDiscVolumeAdmin volumeAdmin = new NDiscVolumeAdmin();
		NDiscMediaAdmin mediaAdmin = new NDiscMediaAdmin();
		NDiscFileAdmin fileAdmin = new NDiscFileAdmin();
		SymmetryCipher cipher = new SymmetryCipher(NDCommon.NDISC_CIPHER_KEY);

		if (0 == args.length) {	
			
			System.out.println("\n");
			System.out.println(TITLE);
			
			System.out.println("[Utility for VOLUME]");
			System.out.println(USAGE_MKVOL);
			System.out.println(USAGE_LSVOL);
			System.out.println(USAGE_RMVOL);
			System.out.println(USAGE_CHVOL);
			System.out.println("\n");
			
			System.out.println("[Utility for MEDIA]");
			System.out.println(USAGE_MKMEDIA);			
			System.out.println(USAGE_LSMEDIA);			
			System.out.println(USAGE_RMMEDIA);
			System.out.println(USAGE_CHMEDIA);
			System.out.println("\n");
			
			System.out.println("[Utility for FILE]");
			System.out.println(USAGE_LSFILE);
			System.out.println(USAGE_REGFILE);
			System.out.println(USAGE_GETFILE);
			System.out.println(USAGE_WHFILE);
			System.out.println(USAGE_RMFILE);		
			System.out.println("\n");
			
			System.out.println("[Utility for ID]");
			System.out.println(USAGE_IDENC);
			System.out.println(USAGE_IDDEC);
			return;
		}		
		
		String COMMAND = args[0].toLowerCase();		
		
		/*
		 * VOLUME  
		 */
		if ("mkvol".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (4 != args.length) {
				System.out.println("USAGE : " + USAGE_MKVOL);
			} else {				
				String name = args[1].trim();
				String accessAuth = args[2].trim();
				String desc = args[3].trim();	
				
				volumeAdmin.makeVolume(name, accessAuth, desc);
			}
		}
		else if ("lsvol".equals(COMMAND)){ 
			System.out.println("args.length : " + args.length);
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSVOL);
			}else{				
				if(args.length == 2){ // volume Id 가 입력 된 경우 해당 id를 가진 Volume 정보 출력 
					int volumeId = Integer.parseInt(args[1].trim());
					volumeAdmin.selectVolumeById(volumeId);		
				}else{
					volumeAdmin.selectVolumeList();
				}			
			}		
		}
		else if ("rmvol".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (2 != args.length) {
				System.out.println("USAGE : " + USAGE_RMVOL);
			} else {				
				int volumeId = Integer.parseInt(args[1].trim());			
				volumeAdmin.removeVolume(volumeId);
			}			
		}
		else if ("chvol".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (5 != args.length) {
				System.out.println("USAGE : " + USAGE_CHVOL);
			} else {
				int id = Integer.parseInt(args[1].trim());
				String name = args[2].trim();
				String accessAuth = args[3].trim();
				String desc = args[4].trim();				
				
				volumeAdmin.changeVolume(id, name, accessAuth, desc);
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
				String host = args[1].trim();
				int port = Integer.parseInt(args[2].trim());
				String name = args[3].trim();
				int type = Integer.parseInt(args[4].trim());
				String path = args[5].trim();
				String desc = args[6].trim();
				int maxSize = Integer.parseInt(args[7].trim());
				int volumeId = Integer.parseInt(args[8].trim());			
				
				mediaAdmin.makeMedia(host, port, name, type, path, desc, maxSize, volumeId);				
			}
		}		
		else if ("lsmedia".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSMEDIA);
			}else{
				if(args.length == 2){ // medialId 가 입력 된 경우 해당 id를 가진 Media 정보 출력 
					int mediaId = Integer.parseInt(args[1].trim());
					mediaAdmin.selectMediaById(mediaId);
				}else{
					mediaAdmin.selectMediaList();
				}				
			}
		}
		else if ("chmedia".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (args.length != 8){
				System.out.println("USAGE : " + USAGE_CHMEDIA);				
			}else{				 					
				int id = Integer.parseInt(args[1].trim());				
				String name = args[2].trim();
				int type = Integer.parseInt(args[3].trim());
				String path = args[4].trim();
				String desc = args[5].trim();
				int maxSize = Integer.parseInt(args[6].trim());
				int volumeId = Integer.parseInt(args[7].trim());				
				
				mediaAdmin.changeMedia(id, name, type, path, desc, maxSize, volumeId);						
			}
		}
		else if ("rmmedia".equals(COMMAND)){
			System.out.println("args.length : " + args.length);
			if (args.length != 2){
				System.out.println("USAGE : " + USAGE_RMMEDIA);
			}else{						
				int id = Integer.parseInt(args[1].trim());				
				mediaAdmin.removeMedia(id);						
			}		
		}
		
		/*
		 * FILE
		 */		
		else if ("lsfile".equals(COMMAND)){
			if (args.length > 2){
				System.out.println("USAGE : " + USAGE_LSFILE);
			}else{
				if(args.length == 2){ // fileId 가 입력 된 경우 해당 id를 가진 file 정보 출력 
					String fileId = args[1].trim();
					fileAdmin.selectFileById(fileId);
				}else{
					fileAdmin.selectFileList();
				}				
			}			
		}
		else if ("regfile".equals(COMMAND)) {
			if (6 != args.length) {
				System.out.println("USAGE : " + USAGE_REGFILE);
			} else {
				String host = args[1];
				int port = Integer.parseInt(args[2]);
				String regFilePath = args[3];
				int regVolId = Integer.parseInt(args[4]);
				String regStatType = "0";				
				
				System.out.println("registered file id : " + fileAdmin.regFile(host, port, regFilePath, regVolId, regStatType));						
			}
		} 
		else if ("getfile".equals(COMMAND)) { 
			if (5 != args.length) {
				System.out.println("USAGE : " + USAGE_GETFILE);
			} else {
				// System.out.println("get file path : " + admin.getFile(args));
				String host = args[1];
				int port = Integer.parseInt(args[2]);
				String fileId = args[3];
				String destFilePath = args[4];
				
				fileAdmin.getFile(host,port,fileId,destFilePath);			
			}
		}
		else if ("whfile".equals(COMMAND)) {
			if (2 != args.length) {
				System.out.println("USAGE : " + USAGE_WHFILE);
			} else {
				String fileId = args[1];				
				System.out.println("file path : " + fileAdmin.getFilePathByFileId(fileId));
			}
		}
		else if ("rmfile".equals(COMMAND)){
			if (4 != args.length){
				System.out.println("USAGE : " + USAGE_RMFILE);
			}else{
				String host = args[1].trim();
				int port = Integer.parseInt(args[2].trim());
				String fileId = args[3].trim();
				
				fileAdmin.removeFile(host, port, fileId);				
			}		
		}
		
		/*
		 * ID
		 */
		else if ("idenc".equals(COMMAND)){
			if (2 != args.length){
				System.out.println("USAGE : " + USAGE_IDENC);
			}else{
				String id = args[1];
				String encryptedId = null;
				try {
					encryptedId = cipher.encrypt(id);
					System.out.println("Encrypted id : " + encryptedId);
				} catch (Exception e) {
					e.printStackTrace();					
				}				
			}		
		}
		else if ("iddec".equals(COMMAND)){
			if (2 != args.length){
				System.out.println("USAGE : " + USAGE_IDDEC);
			}else{
				String id = args[1];
				String decryptedId = null;
				try {
					decryptedId = cipher.decrypt(id);
					System.out.println("Decrypted id : " + decryptedId);
				} catch (Exception e) {
					e.printStackTrace();					
				}					
			}		
		}			
		
		/*
		 * FAIL
		 */
		else {
			System.out.println("FATAL ERROR : illegal command");
		}		
	}	
}
