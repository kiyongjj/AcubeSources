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
	 * DB�� Media ������ �����Ѵ�.
	 * 
	 * @param media
	 *            ������ Media ����
	 */
   public void saveMedia(Media media);

   /**
	 * DB���� Media ���̵� �̿��Ͽ� Media ������ ���´�.
	 * 
	 * @param mediaId
	 *            Media ���̵�
	 * @return Media ����
	 */
   public Media getMedia(int mediaId);

   /**
	 * DB���� Media ���̵� �̿��Ͽ� Media ������ �����Ѵ�.
	 * 
	 * @param mediaId
	 *            Media ���̵�
	 */
   public void deleteMedia(int mediaId);

   /**
	 * DB���� ������ Media ������ �̿��Ͽ� Media ������ �����Ѵ�.
	 * 
	 * @param media
	 *            Media ���̵�
	 */
   public void updateMedia(Media media);

   /**
	 * DB���� Volume ���̵� �̿��Ͽ� �ش� ��밡���� Media ������ ���´�.
	 * 
	 * @param volumeId
	 *            Volume ���̵�
	 * @return ��밡���� Media ���� �迭
	 */
   public Media[] getAvailableMedia(int volumeId);
   
   /**
	 * DB���� ���� ū Media ���̵� ���´�.
	 * 
	 * @return ���� ū Media ���̵�
	 */
   public int getMaxMediaID();   

   /**
	 * DB���� ��� Media ���� ����� ���´�.
	 * 
	 * @return ��� Media ���� ���
	 */
   public List<Media> selectListAll();
   
   /**
	 * DB���� Volume ���̵� �̿��Ͽ� Media ������ ���´�.
	 * 
	 * @param volumeId
	 *            Volume ���̵�
	 * @return Media ���� ���
	 */
   public List<Media> selectListByVolumeId(int volumeId);
}
