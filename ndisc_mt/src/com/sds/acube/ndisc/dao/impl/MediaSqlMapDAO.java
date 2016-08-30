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
 * @file MediaSqlMapDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 18
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.impl;

import java.util.Iterator;
import java.util.List;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.model.Media;

/**
 * The Class MediaSqlMapDAO.
 */
public class MediaSqlMapDAO extends BaseSqlMapDAO implements MediaDAO {

	/** The Constant LIST_SIZE. */
	private static final int LIST_SIZE = 100;

	/**
	 * Instantiates a new media sql map dao.
	 * 
	 * @param daoManager
	 *            the dao manager
	 */
	public MediaSqlMapDAO(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#saveMedia(com.sds.acube.ndisc.model.Media)
	 */
	public void saveMedia(Media media) {
		update("saveMedia", media);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#getMedia(int)
	 */
	public Media getMedia(int mediaId) {
		try {
			return (Media) queryForObject("getMedia", new Integer(mediaId));
		} catch (Exception ex) {
			logger.error("getMedia() Error : " + mediaId);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#deleteMedia(int)
	 */
	public void deleteMedia(int mediaId) {
		try {
			update("deleteMedia", new Integer(mediaId));
		} catch (Exception ex) {
			logger.error("deleteMedia() : " + ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#updateMedia(com.sds.acube.ndisc.model.Media)
	 */
	public void updateMedia(Media media) {
		try {
			update("updateMedia", media);
		} catch (Exception ex) {
			logger.error("updateMedia() error : " + ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#getAvailableMedia(int)
	 */
	@SuppressWarnings("rawtypes")
	public Media[] getAvailableMedia(int volumeId) {
		Media[] mediaList = null;
		try {
			PaginatedList mediaAvailableList = queryForPaginatedList("getAvailableMedia", new Integer(volumeId), LIST_SIZE);

			if (mediaAvailableList != null && mediaAvailableList.size() > 0) {
				mediaList = new Media[mediaAvailableList.size()];
				// 작업가능한 media 정보를 찾는다
				int i = 0;
				for (Iterator iter = mediaAvailableList.iterator(); iter.hasNext();) {
					mediaList[i++] = (Media) iter.next();
				}
			} else {
				logger.info("getAvailableMedia : NONE, Volume ID : " + volumeId);
			}
		} catch (Exception e) {
			logger.error("getAvailableMedia : " + e.getMessage());
		}
		return mediaList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#getMaxMediaID()
	 */
	public int getMaxMediaID() {
		try {
			Media media = (Media) queryForObject("getMaxMediaID", null);
			return media.getId();
		} catch (Exception ex) {
			return 100;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#selectListAll()
	 */
	@SuppressWarnings("unchecked")
	public List<Media> selectListAll() {
		try {
			Media media = new Media();
			return queryForList("selectMediaListAll", media);
		} catch (Exception ex) {
			logger.error("selectListAll Error : " + ex.getMessage());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sds.acube.ndisc.dao.iface.MediaDAO#selectListByVolumeId(int)
	 */
	@SuppressWarnings("unchecked")
	public List<Media> selectListByVolumeId(int volumeId) {
		try {
			Media media = new Media();
			media.setVolumeId(volumeId);
			return queryForList("selectListByVolumeId", media);
		} catch (Exception ex) {
			logger.error("selectListByVolumeId Error : " + ex.getMessage());
			return null;
		}
	}

}
