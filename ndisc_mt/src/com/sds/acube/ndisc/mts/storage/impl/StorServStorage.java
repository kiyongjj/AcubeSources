package com.sds.acube.ndisc.mts.storage.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sds.acube.ndisc.common.exception.DaoException;
import com.sds.acube.ndisc.dao.config.DaoConfig;
import com.sds.acube.ndisc.dao.iface.FileDAO;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;

import org.apache.commons.io.FileSystemUtils;

public class StorServStorage extends DefStorageAdaptor {

	public StorServStorage(LoggerIF logger) {
		super(logger);
		try {
			if (null == NDCommon.daoManager) {
				NDCommon.daoManager = DaoConfig.getDaomanager();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NFile[] aquireStorageInfo(NFile[] nFile, String option)
			throws Exception {
		String[] storages = null;

		for (int i = 0; i < nFile.length; i++) {

			if (NDCommon.STORAGE_PATH_REGIST.equals(option)) {
				storages = getStorage4Regist(nFile[i]);
			} else if (NDCommon.STORAGE_PATH_ACCESS.equals(option)) {
				storages = getStorage4Access(nFile[i]);
			} else {
				;
			}

			nFile[i].setStoragePath(storages[0]);
			nFile[i].setMediaId(Integer.parseInt(storages[1]));

			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getStoragePath = "
					+ nFile[i].getStoragePath());
			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getMediaId = "
					+ nFile[i].getMediaId());
		}

		return nFile;
	}

	
	//占쏙옙占쏙옙 占쏙옙..StorServ DB 占쏙옙키占쏙옙占쏙옙占쏙옙 max_size , size 占시뤄옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙
	private String[] getStorage4Regist(NFile nFile) throws Exception {
		Media[] medias = null;
		int volumeID = -1;
		String[] storages = null;
		String strRet = null;

		try {
			storages = new String[2];

			volumeID = nFile.getVolumeId();
			medias = selectAvailableMedia(volumeID);

			int nFileSize = nFile.getSize();
			for (int i = 0; i < medias.length; i++) {

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
				long freeSpace = medias[i].getFreeSpace();
				String mediaPath = medias[i].getPath();
				boolean bExists = false;
				
				if (new File(mediaPath).exists()) {
					bExists = true;
				} else {
					logger.log(LoggerIF.LOG_WARNING,
							"no exist media path - media : " + medias[i].getId()
									+ ", " + mediaPath);
					bExists = false;
				}

				if (nFileSize <= freeSpace && bExists) {

					String strCDate = nFile.getCreatedDate();
					String strYear = strCDate.substring(0, 4);
					String strMonth = strCDate.substring(4, 6);
					String strDay = strCDate.substring(6, 8);

					strRet = medias[i].getPath() + File.separator + strYear
							+ File.separator + strMonth + File.separator
							+ strDay;

					new File(strRet).mkdirs();
									
					strRet = strRet + File.separator + sea.EncryptFileID(nFile.getId());			
										
					storages[0] = strRet;
					storages[1] = medias[i].getId() + "";

					break;
				}				
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}

			if (null == strRet) {
				String msg = "no avaliable media exists - volume : " + volumeID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}

		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return storages;
	}

	// return storage path only
	private String[] getStorage4Access(NFile nFile) throws Exception {
		NFile retFile = null;

		String[] storages = null;

		try {
			retFile = selectNFileFromDB(nFile.getId());

			String mediaID = retFile.getMediaId() + "";

			String createDate = retFile.getCreatedDate();

			String filePath = getFileMediaPath(nFile.getId(), createDate);

			storages = new String[2];

			storages[0] = filePath;
			storages[1] = mediaID;

		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.log(LoggerIF.LOG_ERROR, msg);
			throw e;
		}

		return storages;
	}

	public String getFileMediaPath(String fileID, String createDate)
			throws Exception {
		FileDAO dao = null;
		String filePath = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			String mediaPath = dao.getMediaPathByFile(fileID);

			if (null == mediaPath) {
				String msg = "file id does not exist : " + fileID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}

			String strYear = createDate.substring(0, 4);
			String strMonth = createDate.substring(4, 6);
			String strDay = createDate.substring(6, 8);

			filePath = mediaPath + File.separator + strYear + File.separator
					+ strMonth + File.separator + strDay;
			
			filePath = filePath + File.separator + sea.EncryptFileID(fileID);			
			
			
		} catch (Exception e) {
			throw e;
		}

		filePath = filePath.replace('\\', '/');
		return filePath;
	}

	public void updateNFileToDB(NFile[] nFile) throws Exception {
		FileDAO dao = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFile.length; i++) {
				nFile[i].setSize((int) new File(nFile[i].getStoragePath())
						.length());

				dao.updateFile(nFile[i]);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public NFile selectNFileFromDB(String fileID) throws Exception {
		FileDAO dao = null;
		NFile nFile = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			nFile = dao.getFile(fileID);
			if (null == nFile) {
				String msg = "file id does not exist : " + fileID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return nFile;
	}
	
	//===========================================================================================
	
	/* 
	 * [占쏙옙체 volume 占쏙옙占쏙옙 占쏙옙占쏙옙트 占쏙옙환]
	 */
	public List selectVolumeInfoList() throws Exception {
		VolumeDAO dao = null;
		List volumelist = null;

		try {
			dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
			volumelist = dao.selectListAll();
			if (null == volumelist) {
				String msg = "There are no Volume Info";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return volumelist;
	}
	
	
	//Volume 占쏙옙占쏙옙 占쏙옙환
	public Volume selectVolumeInfo(int volumeId) throws Exception {
		VolumeDAO dao = null;
		Volume volume = null;

		try {
			dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
			volume = dao.getVolume(volumeId);
			if (null == volume) {
				String msg = "Volume ID : " + volumeId + " does not exist";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return volume;
	}	
	
	
	
	/*
	private String convertAccessableString(int intAccessable){	
		
		return null;
	}*/
	
	//==========================================================================================
	
	//占쏙옙체 Media 占쏙옙占쏙옙 占쏙옙占쏙옙트 占쏙옙환
	public List selectMediaInfoList() throws Exception {
		MediaDAO dao = null;
		List mediaList = null;

		try {
			dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			mediaList = dao.selectListAll();
			if (null == mediaList) {
				String msg = "There are no Media Info";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return mediaList;
	}
	
	
	//Media 占쏙옙占쏙옙 占쏙옙환
	public Media selectMediaInfo(int mediaId) throws Exception {
		MediaDAO dao = null;
		Media media = null;

		try {
			dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			media = dao.getMedia(mediaId);
			if (null == media) {
				String msg = "Media ID : " + mediaId + " does not exist";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return media;
	}
	
	//======================================================================================
	
	//占쏙옙체 File 占쏙옙占쏙옙 占쏙옙占쏙옙트 占쏙옙환
	public List selectFileInfoList() throws Exception {
		FileDAO dao = null;
		List fileList = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			fileList = dao.selectListAll();
			if (null == fileList) {
				String msg = "There are no file Info";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return fileList;
	}
	
	
	//File 占쏙옙占쏙옙 占쏙옙환
	public NFile selectFileInfo(String fileId) throws Exception {
		FileDAO dao = null;
		NFile file = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			file = dao.getFile(fileId);
			if (null == file) {
				String msg = "File ID : " + fileId + " does not exist";
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return file;
	}
	
	//======================================================================================

	public void registNFileToDB(NFile[] nFile) throws Exception {
		FileDAO dao = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFile.length; i++) {
				nFile[i].setSize((int) new File(nFile[i].getStoragePath())
						.length());

				dao.saveFile(nFile[i]);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public void deleteNFileFromDB(NFile[] nFile) throws Exception {
		FileDAO dao = null;

		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFile.length; i++) {
				dao.deleteFile(nFile[i].getId());
			}

		} catch (Exception e) {
			throw e;
		}
	}

	 /*
	 * 2009.04.30 占쏙옙占쏙옙占쏙옙
	 * STORServ DB 占쏙옙키占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쌕깍옙占쏙옙  占쏙옙占쏙옙占쏙옙 R(Read):8 C(Create):4 U(Update):2 D(Delete):1 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占싫댐옙.   
	 * NDISC 占쏙옙占쏙옙占쏙옙 占쏙옙占쌕깍옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌤쇽옙占쏙옙 RCUD 占쏙옙 占쏙옙占쏙옙占쏙옙 占쌉뤄옙占싹므뤄옙 占싱몌옙 占쏙옙占쏙옙占실댐옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싹울옙 DB占쏙옙 占쌉뤄옙占싹듸옙占쏙옙 占쏙옙占쏙옙占싼댐옙.
	 */ 
	public void insertNewVolumeToDB(Volume volume) throws Exception {
		VolumeDAO dao = null;
				
		try {
			
			dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
			int maxVolumeID = dao.getMaxVolumeID();
			logger.log(LoggerIF.LOG_DEBUG, "max volume id : " + maxVolumeID);
			volume.setId(maxVolumeID + 1);
			
			/*
			int intAccessable = 0;
			char[] charAccessable = volume.getAccessable().toCharArray();
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
			if(intAccessable == 0 || intAccessable > 15){
				intAccessable = 8;
			}			
			volume.setAccessable(String.valueOf(intAccessable));
			*/
			
			dao.saveVolume(volume);
		} catch (Exception e) {
			throw e;
		}
	}

	public void insertNewMediaToDB(Media media) throws Exception {
		MediaDAO dao = null;

		try {
			dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			int maxVolumeID = dao.getMaxMediaID();
			logger.log(LoggerIF.LOG_DEBUG, "max media id : " + maxVolumeID);
			media.setId(maxVolumeID + 1);
			dao.saveMedia(media);
		} catch (Exception e) {
			throw e;
		}
	}
		
	/**
	 * 2014.04.04 - add
	 */
	public Media[] selectAvailableMedia(int volumeID) throws Exception {
		Media[] medias = null;
		ArrayList availableMedias = new ArrayList();
		MediaDAO dao = null;
		try {
			dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			medias = dao.getAvailableMedia(volumeID);
			if (null == medias) {
				String msg = "no media exists - volume : " + volumeID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}	
			long freeSpace = 0;
			for(int i=0;i<medias.length;i++){
				freeSpace = FileSystemUtils.freeSpaceKb(medias[i].getPath());
				if(freeSpace > 10240){
					medias[i].setFreeSpace(freeSpace);
					availableMedias.add(medias[i]);
				}
			}
			for(int i=0;i<availableMedias.size();i++){
				medias[i] = (Media)availableMedias.get(i);
			}		
			logger.log(LoggerIF.LOG_DEBUG, "total media counts for volume " + volumeID + " : " + medias.length);
		}catch(IllegalArgumentException iae){ //if the path is invalid
			throw iae;
		}catch(IllegalStateException ise){ //if an error occured in initialisation
			throw ise;
		}catch(IOException ioe){ //if an error occur when finding the free space 
			throw ioe;
		}catch (Exception e) {
			throw e;
		}
		return medias;
	}

	/**
	 *  2014.04.04 - add
	 */
	public String getMediaPathByFile(String fileID) throws Exception {
		FileDAO dao = null;
		String mediaPath = null;
		try {
			dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			mediaPath = dao.getMediaPathByFile(fileID);
			if (null == mediaPath) {
				String msg = "file id does not exist : " + fileID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}
		return mediaPath;
	}
	
	//private boolean check
}
