package com.sds.acube.ndisc.admin;

import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.napi.NApi;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;

import java.util.ArrayList;

public class NDiscMediaAdmin extends NDiscBaseAdmin{
	private MediaDAO dao = null;
	
	public NDiscMediaAdmin()
	{
		dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
	}
	
	public void makeMedia(String host, int port, String name, int type, String path, String desc, int maxSize, int volumeId){
		
		try{		
			Media media = new Media();
            media.setName(name);
            media.setType(type);
            media.setPath(path);
            media.setDesc(desc);
            media.setMaxSize(maxSize);
            media.setVolumeId(volumeId);        
            media.setCreatedDate(getCreateDate());
            
            napi = new NApi();
			napi.NDisc_Connect(host, port);
            if(napi.NDISC_MakeMedia(media)){
            	System.out.println("mkmedia() successfully completed !!!!");
            }else{
            	System.out.println("mkmedia() failed !!!!");
            }    
            		
         }catch(Exception ex){
        	 System.out.println(ex.getMessage());        	 
         }
         finally{
        	 try{        	 
        		 napi.NDisc_Disconnect();
        	 }catch(NetworkException ne){
        		 System.out.println(ne.getMessage());        		
        	 }
         }        	
	}
	
	public void selectMediaById(int mediaId){		
		Media media = null;			
		try{
			media = storage.selectMediaInfo(mediaId);				
			showMediaInfo(media);					
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}			
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
	
	public void selectMediaList(){	
		ArrayList mediaList = null;		
		try{
			mediaList = (ArrayList)storage.selectMediaInfoList();				
			showMediaInfoList(mediaList);					
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
		return ;
	}	
	
	
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
	
	/*
	 * 미디어 정보 수정
	 */
	public void changeMedia(int id, String name, int type, String path, String desc, int maxSize, int volumeId){
		try{			
			Media media = new Media();
			media.setId(id);
			media.setName(name);
			media.setType(type);
			media.setPath(path);
			media.setDesc(desc);
			media.setMaxSize(maxSize);
			media.setVolumeId(volumeId);
			
			dao.updateMedia(media);
			System.out.println("changeMedia() successfully completed...");
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}		
	}
	
	/*
	 * 미디어 정보 삭제
	 */
	public void removeMedia(int id){
		try{
			dao.deleteMedia(id);
			System.out.println("removeMedia() successfully completed...");
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}		
	}

}
