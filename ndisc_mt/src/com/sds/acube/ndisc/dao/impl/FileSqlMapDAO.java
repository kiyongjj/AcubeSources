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
 * @file FileSqlMapDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 17
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.FileDAO;
import com.sds.acube.ndisc.model.DataBox;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.util.RandomGUID;

/**
 * The Class FileSqlMapDAO.
 */
public class FileSqlMapDAO extends BaseSqlMapDAO implements FileDAO {

	/**
	 * Instantiates a new FileSqlMapDAO.
	 * 
	 * @param daoManager
	 *            the dao manager
	 */
	public FileSqlMapDAO(DaoManager daoManager) {
		super(daoManager);
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#getFile(java.lang.String)
	 */
	public NFile getFile(String fileId) {
		try {
			return (NFile) queryForObject("getFile", fileId);
		} catch (Exception ex) {
			logger.error("getFile Error : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#saveFile(com.sds.acube.ndisc.model.NFile)
	 */
	public int saveFile(NFile nFile) {
		int rtn = -1; //@add 2014.02.17
		try {
			if (null == nFile.getId()) {
				nFile.setId(new RandomGUID().toString());
			}
			if (null == nFile.getCreatedDate()) {
				nFile.setCreatedDate(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
			}
			rtn = update("saveFile", nFile); //@add 2014.02.17
			//if (null == dbSchemaType || !(dbSchemaType.equalsIgnoreCase("storserv"))) {
			if (dbSchemaType.equals(NDCommon.NDISC_TYPE)) { // NDISC 일 경우만 MEDIA 사이즈 변경
				rtn = update("upMediaSize", nFile); //@add 2014.02.17
				if (rtn < 0) {
					rtn = NDConstant.MEDIA_SIZE_UP_FAIL;
				}
			}
		} catch (Exception ex) {
			logger.error("saveFile() : " + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#deleteFile(java.lang.String)
	 */
	public int deleteFile(String fileId) {
		int rtn = -1; //@add 2014.02.17
		try {
			NFile formerFile = null;
			//if (null == dbSchemaType || !(dbSchemaType.equalsIgnoreCase("storserv"))) {
			if (dbSchemaType.equals(NDCommon.NDISC_TYPE)) { // NDISC 일 경우만 MEDIA 사이즈 변경위해 파일 정보 구하기
				formerFile = (NFile) queryForObject("getFile", fileId);
			}
			rtn = update("deleteFile", fileId); //@add 2014.02.17
			//if (null == dbSchemaType || !(dbSchemaType.equalsIgnoreCase("storserv"))) {
			if (dbSchemaType.equals(NDCommon.NDISC_TYPE)) { // NDISC 일 경우만 MEDIA 사이즈 변경
				rtn = update("downMediaSize", formerFile); //@add 2014.02.17
				if (rtn < 0) {
					rtn = NDConstant.MEDIA_SIZE_DOWN_FAIL;
				}
			}
		} catch (Exception ex) {
			logger.error("deleteFile : " + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#updateFile(com.sds.acube.ndisc.model.NFile)
	 */
	public int updateFile(NFile nFile) {
		int rtn = -1; //@add 2014.02.17
		try {
			if (null == nFile.getModifiedDate()) {
				nFile.setModifiedDate(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
			}
			NFile formerFile = null;
			//if (null == dbSchemaType || !(dbSchemaType.equalsIgnoreCase("storserv"))) {
			if (dbSchemaType.equals(NDCommon.NDISC_TYPE)) { // NDISC 일 경우만 MEDIA 사이즈 변경위해 파일 정보 구하기
				formerFile = (NFile) queryForObject("getFile", nFile.getId());
			}
			rtn = update("updateFile", nFile); //@add 2014.02.17
			//if (null == dbSchemaType || !(dbSchemaType.equalsIgnoreCase("storserv"))) {
			if (dbSchemaType.equals(NDCommon.NDISC_TYPE)) { // NDISC 일 경우만 MEDIA 사이즈 변경
				rtn = update("downMediaSize", formerFile); //@add 2014.02.17
				if (rtn < 0) {
					rtn = NDConstant.MEDIA_SIZE_DOWN_FAIL;
				}
				rtn = update("upMediaSize", nFile); //@add 2014.02.17
				if (rtn < 0) {
					rtn = NDConstant.MEDIA_SIZE_UP_FAIL;
				}
			}
		} catch (Exception ex) {
			logger.error("udateFile() : " + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#saveFile(com.sds.acube.ndisc.model.NFile[])
	 */
	public int saveFile(NFile[] nFiles) {
		int rtn = -1; //@add 2014.02.17
		try {
			for (int i = 0; i < nFiles.length; i++) {
				rtn = saveFile(nFiles[i]); //@add 2014.02.17 - 하나라도 실패하면 전부 실패
			}
		} catch (Exception ex) {
			logger.error("saveFile() : " + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#deleteFile(java.lang.String[])
	 */
	public int deleteFile(String[] fileIds) {
		int rtn = -1; //@add 2014.02.17
		try {
			for (int i = 0; i < fileIds.length; i++) {
				rtn = deleteFile(fileIds[i]); //@add 2014.02.17
			}
		} catch (Exception ex) {
			logger.error("deleteFile() : " + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#updateFile(com.sds.acube.ndisc.model.NFile[])
	 */
	public int updateFile(NFile[] nFiles) {
		int rtn = -1; //@add 2014.02.17
		try {
			for (int i = 0; i < nFiles.length; i++) {
				rtn = updateFile(nFiles[i]); //@add 2014.02.17 - 하나라도 실패하면 전부 실패
			}
		} catch (Exception ex) {
			logger.error("updateFile()" + ex.getMessage());
		}
		return rtn; //@add 2014.02.17
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#getMediaPathByFile(java.lang.String)
	 */
	public String getMediaPathByFile(String fileId) {
		try {
			return (String) queryForObject("getMediaPathByFile", fileId);
		} catch (Exception ex) {
			logger.error("getMediaPathByFile() : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#selectListAll()
	 */
	@SuppressWarnings("unchecked")
	public List<NFile> selectListAll() {
		try {
			NFile file = new NFile();
			return queryForList("selectFileListAll", file);
		} catch (Exception ex) {
			logger.error("selectListAll Error : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#selectListByMediaId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<NFile> selectListByMediaId(String mediaId) {
		try {
			return queryForList("selectListByMediaId", mediaId);
		} catch (Exception ex) {
			logger.error("selectListByMediaId() : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#selectListByCreateDataAndMediaID(com.sds.acube.ndisc.model.NFile)
	 */
	@SuppressWarnings("unchecked")
	public List<NFile> selectListByCreateDataAndMediaID(NFile nFile) {
		try {
			return queryForList("selectListByCreateDataAndMediaID", nFile);
		} catch (Exception ex) {
			logger.error("selectListByCreateDataAndMediaID() : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#getYear(com.sds.acube.ndisc.model.DataBox)
	 */
	@SuppressWarnings("unchecked")
	public List<DataBox> getYear(DataBox dataBox) {
		try {
			return queryForList("getYear", dataBox);
		} catch (Exception ex) {
			logger.error("getYear() : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#getMonth(com.sds.acube.ndisc.model.DataBox)
	 */
	@SuppressWarnings("unchecked")
	public List<DataBox> getMonth(DataBox dataBox) {
		try {
			return queryForList("getMonth", dataBox);
		} catch (Exception ex) {
			logger.error("getMonth() : " + ex.getMessage());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.FileDAO#getDay(com.sds.acube.ndisc.model.DataBox)
	 */
	@SuppressWarnings("unchecked")
	public List<DataBox> getDay(DataBox dataBox) {
		try {
			return queryForList("getDay", dataBox);
		} catch (Exception ex) {
			logger.error("getDay() : " + ex.getMessage());
		}
		return null;
	}

}
