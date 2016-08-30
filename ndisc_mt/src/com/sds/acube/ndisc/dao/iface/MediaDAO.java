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
 * @file MediaDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 17
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.iface;

import java.util.List;

import com.sds.acube.ndisc.model.Media;


/**
 * The Interface MediaDAO.
 */
public interface MediaDAO extends Dao {

   /**
	 * DB에 Media 정보를 저장한다.
	 * 
	 * @param media
	 *            저장할 Media 정보
	 */
   public void saveMedia(Media media);

   /**
	 * DB에서 Media 아이디를 이용하여 Media 정보를 얻어온다.
	 * 
	 * @param mediaId
	 *            Media 아이디
	 * @return Media 정보
	 */
   public Media getMedia(int mediaId);

   /**
	 * DB에서 Media 아이디를 이용하여 Media 정보를 삭제한다.
	 * 
	 * @param mediaId
	 *            Media 아이디
	 */
   public void deleteMedia(int mediaId);

   /**
	 * DB에서 수정할 Media 정보를 이용하여 Media 정보를 수정한다.
	 * 
	 * @param media
	 *            Media 아이디
	 */
   public void updateMedia(Media media);

   /**
	 * DB에서 Volume 아이디를 이용하여 해당 사용가능한 Media 정보를 얻어온다.
	 * 
	 * @param volumeId
	 *            Volume 아이디
	 * @return 사용가능한 Media 정보 배열
	 */
   public Media[] getAvailableMedia(int volumeId);
   
   /**
	 * DB에서 가장 큰 Media 아이디를 얻어온다.
	 * 
	 * @return 가장 큰 Media 아이디
	 */
   public int getMaxMediaID();   

   /**
	 * DB에서 모든 Media 정보 목록을 얻어온다.
	 * 
	 * @return 모든 Media 정보 목록
	 */
   public List<Media> selectListAll();
   
   /**
	 * DB에서 Volume 아이디를 이용하여 Media 정보를 얻어온다.
	 * 
	 * @param volumeId
	 *            Volume 아이디
	 * @return Media 정보 목록
	 */
   public List<Media> selectListByVolumeId(int volumeId);
}
