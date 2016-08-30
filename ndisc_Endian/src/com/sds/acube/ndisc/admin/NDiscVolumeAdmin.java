package com.sds.acube.ndisc.admin;

import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;

import java.util.ArrayList;

public class NDiscVolumeAdmin extends NDiscBaseAdmin{
	private VolumeDAO dao = null;
	
	public NDiscVolumeAdmin(){
		dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
	}
	
	/*
	 * 볼륨 생성
	 */
	public void makeVolume(String name, String accessAuth, String desc ){
		try{			
			
			Volume volume  = new Volume();
			volume.setName(name);
			volume.setAccessable(accessAuth);
			volume.setDesc(desc);			
			volume.setCreatedDate(getCreateDate());
			
			storage.insertNewVolumeToDB(volume);			
			System.out.println("makeVolue() has been succefully completed..");
					
			
		}catch(Exception ex){
			System.out.println("makeVolume() has been failed..");
			System.out.println(ex.getMessage());
			ex.printStackTrace();			
		}		
	}
	
	public void selectVolumeById(int volumeId){
		try{			
			Volume volume = storage.selectVolumeInfo(volumeId);		
			if(volume != null){
				showVolumeInfo(volume);
			}else{
				throw new Exception("volume info is null");
			}
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
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
	}

	public void selectVolumeList(){
		try{			
			ArrayList<Volume> volumelist = (ArrayList)storage.selectVolumeInfoList();	
			if(volumelist != null){
				showVolumeInfoList(volumelist);
			}else{
				throw new Exception("volumelist is null..");
			}					
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());			
		}	
	}
	
	private void showVolumeInfoList(ArrayList<Volume> volumelist){
		int size = volumelist.size();
		Volume volume = null;
		System.out.println("======================================================");
		System.out.println("Volume_ID  Volume_Name  Access  Create_Date  Desc ");		
		System.out.println("======================================================");
		
		for(int i=0;i<size;i++){
			volume = volumelist.get(i);
			System.out.println(volume.getId() + " " + volume.getName() + " " + volume.getAccessable()
					+ " " + volume.getCreatedDate() + " " + volume.getDesc());
			
			if(i < size-1){
				System.out.println("------------------------------------------------------");
			}
		}
		System.out.println("======================================================");
		System.out.println(size + " rows selected. ");
	}
	
	/*
	 * 볼륨 정보 삭제
	 */
	public void removeVolume(int volumeId){	
		try {		
			if (dao.deleteVolume(volumeId)) { // success
				System.out.println("Remove Volume ID : " + volumeId + " successfully completed !!!");				
			}else{
				System.out.println("removeVolume() failed");
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());		
			e.printStackTrace();
		}	
	}
	
	/*
	 * 볼룸 정보 수정
	 */
	public void changeVolume(int id, String name, String accessAuth, String desc){		
		try{			
			Volume volume = new Volume();
			volume.setId(id);
			volume.setName(name);
			volume.setAccessable(accessAuth);
			volume.setDesc(desc);		  		
			
	   		if(dao.updateVolume(volume)){
	   			System.out.println("changeVolume() successfully completed..");
	   		}else{
	   			System.out.println("changeVolume() failed..");
	   		}
			
		}catch(Exception ex){			
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}	
	}
}
