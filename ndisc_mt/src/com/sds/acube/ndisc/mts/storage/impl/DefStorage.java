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
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;


public class DefStorage extends DefStorageAdaptor {

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

	private String[] getStorage4Regist(NFile nFile) throws Exception {
		Media[] media = null;
		int volumeID = -1;
		String[] storages = null;
		String strRet = null;

		try {
			storages = new String[2];

			volumeID = nFile.getVolumeId();
			media = selectAvailableMedia(volumeID);

			int nFileSize = nFile.getSize();
			for (int i = 0; i < media.length; i++) {

				long nMediaSize = media[i].getSize();
				long nMediaMaxSize = media[i].getMaxSize();
				String mediaPath = media[i].getPath();
				boolean bExists = false;
				if (new File(mediaPath).exists()) {
					bExists = true;
				} else {
					logger.log(LoggerIF.LOG_WARNING,
							"no exist media path - media : " + media[i].getId()
									+ ", " + mediaPath);
					bExists = false;
				}

				if ((nMediaSize + nFileSize) <= nMediaMaxSize && bExists) {

					String strCDate = nFile.getCreatedDate();
					String strYear = strCDate.substring(0, 4);
					String strMonth = strCDate.substring(4, 6);
					String strDay = strCDate.substring(6, 8);

					strRet = media[i].getPath() + File.separator + strYear
							+ File.separator + strMonth + File.separator
							+ strDay;

					
					new File(strRet).mkdirs();

					// get enc file path
					strRet = strRet + File.separator
							+ cipher.encrypt(nFile.getId());
															
					storages[0] = strRet;
					storages[1] = media[i].getId() + "";

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
			
			filePath = filePath + File.separator + cipher.encrypt(fileID);
			
			
		} catch (Exception e) {
			throw e;
		}
		
		// 2010.10.12 수정
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
	
	//��ü volume ���� ����Ʈ ��ȯ
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
	
	
	//Volume ���� ��ȯ
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
	
	//==========================================================================================
	
	//��ü Media ���� ����Ʈ ��ȯ
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
	
	
	//Media ���� ��ȯ
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
	
	//��ü File ���� ����Ʈ ��ȯ
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
	
	
	//File ���� ��ȯ
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

	public void insertNewVolumeToDB(Volume volume) throws Exception {
		VolumeDAO dao = null;

		try {
			dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
			int maxVolumeID = dao.getMaxVolumeID();
			logger.log(LoggerIF.LOG_DEBUG, "max volume id : " + maxVolumeID);
			volume.setId(maxVolumeID + 1);
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
		Media[] media = null;
		MediaDAO dao = null;
		try {
			dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
			media = dao.getAvailableMedia(volumeID);
			if (null == media) {
				String msg = "no media exists - volume : " + volumeID;
				logger.log(LoggerIF.LOG_ERROR, msg);
				throw new DaoException(msg);
			}
			logger.log(LoggerIF.LOG_DEBUG, "total media counts for volume " + volumeID + " : " + media.length);
		} catch (Exception e) {
			throw e;
		}
		return media;
	}

	/**
	 * 2014.04.04 - add
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
	
}
