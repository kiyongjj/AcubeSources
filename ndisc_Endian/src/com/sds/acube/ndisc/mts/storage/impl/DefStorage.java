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
 * @file DefStorage.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 15
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.mts.storage.impl;

import java.io.File;
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

/**
 * The Class DefStorage.
 */
public class DefStorage extends DefStorageAdaptor {

	/**
	 * Instantiates a new DefStorage.
	 * 
	 * @param logger
	 *            the logger
	 */
	public DefStorage(LoggerIF logger) {
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
	public NFile[] aquireStorageInfo(NFile[] nFiles, String option) throws Exception {
		String[] storages = null;
		for (int i = 0; i < nFiles.length; i++) {
			if (NDCommon.STORAGE_PATH_REGIST.equals(option)) {
				storages = getStorage4Regist(nFiles[i]);
			} else if (NDCommon.STORAGE_PATH_ACCESS.equals(option)) {
				storages = getStorage4Access(nFiles[i]);
			}
			nFiles[i].setStoragePath(storages[0]);
			nFiles[i].setMediaId(Integer.parseInt(storages[1]));
			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getStoragePath = " + nFiles[i].getStoragePath());
			logger.log(LoggerIF.LOG_DEBUG, "nFile[" + i + "].getMediaId = " + nFiles[i].getMediaId());
		}
		return nFiles;
	}

	/**
	 * NFile 정보를 이용하여 DB에 정보 저장.
	 * 
	 * @param nFile
	 *            NFile 정보
	 * @return 파일 정보(파일경로(파일아이디), Media 아이디) 배열
	 * @throws Exception
	 *             예외
	 */
	private String[] getStorage4Regist(NFile nFile) throws Exception {
		int volumeId = -1;
		String[] storages = null;
		String strRet = null;

		try {
			storages = new String[2];

			volumeId = nFile.getVolumeId();
			Media[] media = selectAvailableMedia(volumeId);

			int nFileSize = nFile.getSize();
			String strCDate = nFile.getCreatedDate();
			String strYear = strCDate.substring(0, 4);
			String strMonth = strCDate.substring(4, 6);
			String strDay = strCDate.substring(6, 8);
			for (int i = 0; i < media.length; i++) {
				if (isAvaiableFreeSpace(nFileSize, media[i])) {
					strRet = media[i].getPath().concat(File.separator).concat(strYear).concat(File.separator).concat(strMonth).concat(File.separator).concat(strDay);
					if (new File(strRet).mkdirs()) {
						strRet = strRet.concat(File.separator).concat(cipher.encrypt(nFile.getId()));
						storages[0] = strRet;
						storages[1] = media[i].getId() + "";
					} else {
						logger.log(LoggerIF.LOG_ERROR, "can not make file media directory.");
					}
					break;
				}
			}
			if (null == strRet) {
				String msg = "no avaliable media exists - volume : " + volumeId;
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
	 *            the n file
	 * @return the storage4 access
	 * @throws Exception
	 *             the exception
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
			filePath = filePath.concat(File.separator).concat(cipher.encrypt(fileId));
		} catch (Exception e) {
			throw e;
		}
		// 2010.10.12 수정
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
	public NFile selectNFileFromDB(String fileId) throws Exception {
		NFile nFile = null;
		try {
			FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
			nFile = dao.getFile(fileId);
			if (null == nFile) {
				String msg = "file id does not exist : " + fileId;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
		} catch (Exception e) {
			throw e;
		}

		return nFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.mts.storage.impl.DefStorageAdaptor#selectVolumeInfoList()
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

	/*
	 * (non-Javadoc)
	 * 
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
	 * 해당 Volume 아이디를 통해 사용가능한 Media 정보 배열을 얻어온다.
	 * 
	 * @param volumeId
	 *            Volume 아이디
	 * @return 가용한 Media 정보 배열
	 * @throws Exception
	 *             예외
	 */
	private Media[] selectAvailableMedia(int volumeId) throws Exception {
		Media[] media = null;
		try {
			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			media = dao.getAvailableMedia(volumeId);
			if (null == media) {
				String msg = "no media exists - volume : " + volumeId;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			logger.log(LoggerIF.LOG_DEBUG, "total media counts for volume " + volumeId + " : " + media.length);
		} catch (Exception e) {
			throw e;
		}
		return media;
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
		long nMediaSize = media.getSize();
		long nMediaMaxSize = media.getMaxSize();
		logger.log(LoggerIF.LOG_DEBUG, "media max size : " + nMediaMaxSize);
		logger.log(LoggerIF.LOG_DEBUG, "current size(media size(" + nMediaSize + ") + filesize(" + nFileSize + ") = " + (nMediaSize + nFileSize));
		if ((nMediaSize + nFileSize) <= nMediaMaxSize && bExists) {
			return true;
		}
		return false;
	}
}
