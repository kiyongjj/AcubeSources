/**
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * @file StorServStorage.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 22
 * Description 	  : First Draft.
 * </pre>
 */
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
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import org.apache.commons.io.FileSystemUtils;

/**
 * The Class StorServStorage.
 * 
 * @author Takkies
 */
public class StorServStorage extends DefStorageAdaptor {

	/**
	 * Instantiates a new StorServStorage.
	 * 
	 * @param logger
	 *            로거
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#aquireStorageInfo(com.sds.acube.ndisc.model.NFile[], java.lang.String)
	 */
	public NFile[] aquireStorageInfo(NFile[] nFile, String option) throws Exception {
		String[] storages = null;
		for (int i = 0; i < nFile.length; i++) {
			if (NDCommon.STORAGE_PATH_REGIST.equals(option)) {
				storages = getStorage4Regist(nFile[i]);
			} else if (NDCommon.STORAGE_PATH_ACCESS.equals(option)) {
				storages = getStorage4Access(nFile[i]);
			}
			nFile[i].setStoragePath(storages[0]);
			nFile[i].setMediaId(Integer.parseInt(storages[1]));
			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getStoragePath = " + nFile[i].getStoragePath());
			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getMediaId = " + nFile[i].getMediaId());
		}

		return nFile;
	}

	/**
	 * StorServ DB 스키마에는 max_size , size 컬럼이 존재하지 않음.
	 * 
	 * @param nFile
	 *            NFile 정보
	 * @return 등록을 위한 스토리지 정보 배열(파일 경로, Media 아이디)
	 * @throws Exception
	 *             예외
	 */
	private String[] getStorage4Regist(NFile nFile) throws Exception {
		int volumeID = -1;
		String[] storages = null;
		String strRet = null;

		try {
			storages = new String[2];

			volumeID = nFile.getVolumeId();
			Media[] medias = selectAvailableMedia(volumeID);

			int nFileSize = nFile.getSize();
			String strCDate = nFile.getCreatedDate();
			String strYear = strCDate.substring(0, 4);
			String strMonth = strCDate.substring(4, 6);
			String strDay = strCDate.substring(6, 8);
			for (int i = 0; i < medias.length; i++) {
				if (isAvaiableFreeSpace(nFileSize, medias[i])) {
					strRet = medias[i].getPath().concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth).concat(File.separator).concat(strDay);
					
					// 20160325 디렉토리가 이미 존재하는지 체크 추가
					if (new File(strRet).exists() || new File(strRet).mkdirs()) {
					//if (new File(strRet).mkdirs()) {
						strRet = strRet.concat(File.separator).concat(sea.EncryptFileID(nFile.getId()));
						storages[0] = strRet;
						storages[1] = medias[i].getId() + "";
					} else {
						//logger.log(LoggerIF.LOG_ERROR, "can not make file media directory.");
						logger.log(LoggerIF.LOG_ERROR, "can not find or make file media directory.");
					}
					break;
				}
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

	/**
	 * Gets the storage4 access.
	 * 
	 * @param nFile
	 *            NFile 정보
	 * @return 정보취득을 위한 스토리지 정보 배열(파일 경로, Media 아이디)
	 * @throws Exception
	 *             예외
	 */
	private String[] getStorage4Access(NFile nFile) throws Exception {
		String[] storages = null;
		try {
			NFile retFile = selectNFileFromDB(nFile.getId());
			storages = new String[2];
			storages[0] = getFileMediaPath(nFile.getId(), retFile.getCreatedDate());
			storages[1] = retFile.getMediaId() + "";
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.log(LoggerIF.LOG_ERROR, msg);
			throw e;
		}
		return storages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#getFileMediaPath(java.lang.String, java.lang.String)
	 */
	public String getFileMediaPath(String fileId, String createDate) throws Exception {
		String filePath = null;

		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			String mediaPath = dao.getMediaPathByFile(fileId);

			if (null == mediaPath) {
				String msg = "file id does not exist : " + fileId;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}

			String strYear = createDate.substring(0, 4);
			String strMonth = createDate.substring(4, 6);
			String strDay = createDate.substring(6, 8);

			filePath = mediaPath.concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth).concat(File.separator).concat(strDay);
			
			// 일기준 디렉토리가 없을 경우 월기준 디렉토리를 찾아봄
			if (!new File(filePath).exists()) {
				filePath =  mediaPath.concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth);
			}			
			filePath = filePath.concat(File.separator).concat(sea.EncryptFileID(fileId));
		} catch (Exception e) {
			throw e;
		}
		filePath = filePath.replace('\\', '/');
		return filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#updateNFileToDB(com.sds.acube.ndisc.model.NFile[])
	 */
	public void updateNFileToDB(NFile[] nFiles) throws Exception {
		int rtn = -1; // @add 2014.02.17
		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFiles.length; i++) {
				nFiles[i].setSize((int) new File(nFiles[i].getStoragePath()).length());

				rtn = dao.updateFile(nFiles[i]); // @add 2014.02.17
			}
			// @add 2014.02.17
			if (rtn < 0) {
				String msg = "update NDisc File To DB Error";
				if (rtn == NDConstant.MEDIA_SIZE_UP_FAIL) {
					msg = "increase NDisc Media size Error";
				} else if (rtn == NDConstant.MEDIA_SIZE_DOWN_FAIL) {
					msg = "decrease NDisc Media size Error";
				}
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			// @add 2014.02.17
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectNFileFromDB(java.lang.String)
	 */
	public NFile selectNFileFromDB(String fileID) throws Exception {
		NFile nFile = null;

		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
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

	/**
	 * 전체 volume 정보 리스트 반환.
	 * 
	 * @return Volume 정보 리스트
	 * @throws Exception
	 *             예외
	 */
	public List<Volume> selectVolumeInfoList() throws Exception {
		List<Volume> volumelist = null;

		try {
			VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectVolumeInfo(int)
	 */
	public Volume selectVolumeInfo(int volumeId) throws Exception {
		Volume volume = null;

		try {
			VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
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
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectMediaInfoList()
	 */
	public List<Media> selectMediaInfoList() throws Exception {
		List<Media> mediaList = null;

		try {
			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectMediaInfo(int)
	 */
	public Media selectMediaInfo(int mediaId) throws Exception {
		Media media = null;

		try {
			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectFileInfoList()
	 */
	public List<NFile> selectFileInfoList() throws Exception {
		List<NFile> fileList = null;

		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectFileInfo(java.lang.String)
	 */
	public NFile selectFileInfo(String fileId) throws Exception {
		NFile file = null;

		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#registNFileToDB(com.sds.acube.ndisc.model.NFile[])
	 */
	public void registNFileToDB(NFile[] nFiles) throws Exception {
		int rtn = -1; // @add 2014.02.17
		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFiles.length; i++) {
				nFiles[i].setSize((int) new File(nFiles[i].getStoragePath()).length());
				rtn = dao.saveFile(nFiles[i]); // @add 2014.02.17
			}
			// @add 2014.02.17
			if (rtn < 0) {
				String msg = "regist NDisc File To DB Error";
				if (rtn == NDConstant.MEDIA_SIZE_UP_FAIL) {
					msg = "increase NDisc Media size Error";
				}
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			// @add 2014.02.17
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#deleteNFileFromDB(com.sds.acube.ndisc.model.NFile[])
	 */
	public void deleteNFileFromDB(NFile[] nFiles) throws Exception {
		int rtn = -1; // @add 2014.02.17

		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			for (int i = 0; i < nFiles.length; i++) {
				rtn = dao.deleteFile(nFiles[i].getId()); // @add 2014.02.17
			}
			// @add 2014.02.17
			if (rtn < 0) {
				String msg = "delete NDisc File From DB Error";
				if (rtn == NDConstant.MEDIA_SIZE_DOWN_FAIL) {
					msg = "decrease NDisc Media size Error";
				}
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			// @add 2014.02.17
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 2009.04.30 수정됨.<br>
	 * STORServ DB 스키마 에서는 접근권한 정보가<br>
	 * R(Read):8 C(Create):4 U(Update):2 D(Delete):1 의 숫자 값의 합으로 저장 된다.<br>
	 * NDISC 에서는 접근권한 정보를 단순히 RCUD 의 값으로 입력하므로<br>
	 * 이를 대응되는 숫자 값으로 변경하여 DB에 입력하도록 수정한다.
	 * 
	 * @param volume
	 *            Volume 정보
	 * @throws Exception
	 *             예외
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#insertNewVolumeToDB(com.sds.acube.ndisc.model.Volume)
	 */
	public void insertNewVolumeToDB(Volume volume) throws Exception {
		try {
			VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
			int maxVolumeID = dao.getMaxVolumeID();
			logger.log(LoggerIF.LOG_DEBUG, "max volume id : " + maxVolumeID);
			volume.setId(maxVolumeID + 1);

			dao.saveVolume(volume);
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#insertNewMediaToDB(com.sds.acube.ndisc.model.Media)
	 */
	public void insertNewMediaToDB(Media media) throws Exception {
		try {
			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			int maxVolumeID = dao.getMaxMediaID();
			logger.log(LoggerIF.LOG_DEBUG, "max media id : " + maxVolumeID);
			media.setId(maxVolumeID + 1);
			dao.saveMedia(media);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Select available media. StorServ DB 스키마를 사용하는 경우<br>
	 * max_size, size 컬럼이 없으므로 수정 되어야 한다.
	 * 
	 * @param volumeID
	 *            the volume id
	 * @return the media[]
	 * @throws Exception
	 *             the exception
	 */
	private Media[] selectAvailableMedia(int volumeID) throws Exception {
		Media[] medias = null;
		ArrayList<Media> availableMedias = new ArrayList<Media>();

		try {
			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);

			medias = dao.getAvailableMedia(volumeID);

			if (null == medias) {
				String msg = "no media exists - volume : " + volumeID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}

			long freeSpace = 0;
			for (int i = 0; i < medias.length; i++) {
				// 20160405 여기서는 여유공간을 kbyte 단위로 등록하고 isAvaiableFreeSpace() 에서는 byte 크기로 비교하므로 여유공간이 넉넉해도 등록을 못하는 문제 발생하는 문제 바로 잡음
				//freeSpace = FileSystemUtils.freeSpaceKb(medias[i].getPath());
				//if (freeSpace > 10 * 1024 * 1024) { // 디스크 공간이 10MB 이상일 때만 해당 미디어를 사용. 
				freeSpace = FileSystemUtils.freeSpaceKb(medias[i].getPath()) * 1024;
				if (freeSpace > 10485760) { // 디스크 공간이 10MB 이상일 때만 해당 미디어를 사용. 10 * (2 exp 10)
					medias[i].setFreeSpace(freeSpace);
					availableMedias.add(medias[i]);
				}
			}

			for (int i = 0; i < availableMedias.size(); i++) {
				medias[i] = (Media) availableMedias.get(i);
			}
			logger.log(LoggerIF.LOG_DEBUG, "total media counts for volume " + volumeID + " : " + medias.length);
		} catch (IllegalArgumentException iae) { // if the path is invalid
			throw iae;
		} catch (IllegalStateException ise) { // if an error occured in initialisation
			throw ise;
		} catch (IOException ioe) { // if an error occur when finding the free space
			throw ioe;
		} catch (Exception e) {
			throw e;
		}
		return medias;
	}

	/**
	 * 파일을 저장할 여유공간이 있는지 체크하기
	 * 
	 * @param nFileSize
	 *            파일사이즈
	 * @param media
	 *            Media 정보
	 * @return 파일을 저장할 여유공간이 있으면 true
	 */
	private boolean isAvaiableFreeSpace(int nFileSize, Media media) {
		String mediaPath = media.getPath();
		boolean bExists = false;
		if (new File(mediaPath).exists()) {
			bExists = true;
		} else {
			logger.log(LoggerIF.LOG_WARNING, "no exist media path - media : " + media.getId() + ", " + mediaPath);
			bExists = false;
		}
		long nMediaFreeSpace = media.getFreeSpace();
		logger.log(LoggerIF.LOG_DEBUG, "media free space size : " + nMediaFreeSpace);
		logger.log(LoggerIF.LOG_DEBUG, "file size : " + nFileSize);		
		if (nFileSize <= nMediaFreeSpace && bExists) {
			return true;
		}
		return false;
	}
	
}
