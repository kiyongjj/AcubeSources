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
 * @file VolumeDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 17
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.iface;

import java.util.List;

import com.sds.acube.ndisc.model.Volume;

/**
 * The Interface VolumeDAO.
 */
public interface VolumeDAO extends Dao {

   /**
	 * DB에 Volume 정보를 저장한다.
	 * 
	 * @param volume
	 *            저장할 Volume 정보
	 * @return 저장 성공하면 true
	 */
   public boolean saveVolume(Volume volume);

   /**
	 * DB에서 Volume 아이디를 이용하여 Volume 정보를 얻어온다.
	 * 
	 * @param volumeId
	 *            Volume 아이디
	 * @return Volume 정보
	 */
   public Volume getVolume(int volumeId);

   /**
	 * DB에서 Volume 아이디를 이용하여 Volume 정보를 삭제한다.
	 * 
	 * @param volumeId
	 *            Volume 아이디
	 * @return 삭제 성공하면 true
	 */
   public boolean deleteVolume(int volumeId);

   /**
	 * DB에서 수정할 Volume 정보를 이용하여 Volume 정보를 수정한다.
	 * 
	 * @param volume
	 *            수정할 Volume 정보
	 * @return 수정 성공하면 true
	 */
   public boolean updateVolume(Volume volume);   
   
   /**
	 * DB에서 가장 큰 Volume 아이디를 얻어온다.
	 * 
	 * @return 가장 큰 Volume 아이디
	 */
   public int getMaxVolumeID();
   
   /**
	 * DB에서 모든 Volume 정보 목록을 얻어온다.
	 * 
	 * @return 모든 Volume 정보 목록
	 */
   public List<Volume> selectListAll();
}
