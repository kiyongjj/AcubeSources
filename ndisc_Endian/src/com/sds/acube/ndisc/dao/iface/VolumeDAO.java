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
	 * DB�� Volume ������ �����Ѵ�.
	 * 
	 * @param volume
	 *            ������ Volume ����
	 * @return ���� �����ϸ� true
	 */
   public boolean saveVolume(Volume volume);

   /**
	 * DB���� Volume ���̵� �̿��Ͽ� Volume ������ ���´�.
	 * 
	 * @param volumeId
	 *            Volume ���̵�
	 * @return Volume ����
	 */
   public Volume getVolume(int volumeId);

   /**
	 * DB���� Volume ���̵� �̿��Ͽ� Volume ������ �����Ѵ�.
	 * 
	 * @param volumeId
	 *            Volume ���̵�
	 * @return ���� �����ϸ� true
	 */
   public boolean deleteVolume(int volumeId);

   /**
	 * DB���� ������ Volume ������ �̿��Ͽ� Volume ������ �����Ѵ�.
	 * 
	 * @param volume
	 *            ������ Volume ����
	 * @return ���� �����ϸ� true
	 */
   public boolean updateVolume(Volume volume);   
   
   /**
	 * DB���� ���� ū Volume ���̵� ���´�.
	 * 
	 * @return ���� ū Volume ���̵�
	 */
   public int getMaxVolumeID();
   
   /**
	 * DB���� ��� Volume ���� ����� ���´�.
	 * 
	 * @return ��� Volume ���� ���
	 */
   public List<Volume> selectListAll();
}
