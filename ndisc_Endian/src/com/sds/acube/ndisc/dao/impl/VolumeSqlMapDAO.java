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
 * @file VolumeSqlMapDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 18
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.impl;

import java.util.List;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;

/**
 * The Class VolumeSqlMapDAO.
 */
public class VolumeSqlMapDAO extends BaseSqlMapDAO implements VolumeDAO {

	/**
	 * Instantiates a new volume sql map dao.
	 * 
	 * @param daoManager
	 *            the dao manager
	 */
	public VolumeSqlMapDAO(DaoManager daoManager) {
		super(daoManager);
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#saveVolume(com.sds.acube.ndisc.model.Volume)
	 */
	public boolean saveVolume(Volume volume) {
//		 2009.04.30 ������ STORServ DB ��Ű�� ������ ���ٱ��� ������ R(Read):8 C(Create):4
//		 U(Update):2 D(Delete):1 �� ���� ���� ������ ���� �ȴ�. NDISC ������ ���ٱ��� ������ �ܼ��� RCUD ��
//		 ������ �Է��ϹǷ� �̸� �����Ǵ� ���� ������ �����Ͽ� DB�� �Է��ϵ��� �����Ѵ�.
		try {
			//if (null != dbSchemaType && dbSchemaType.equalsIgnoreCase("storserv")) {
			if (dbSchemaType.equals(NDCommon.STORSERV_TYPE)) { // STOR SERVER �� ��� ���� ���� ���ϱ�
				String accessable = volume.getAccessable();
				int intAccessable = convertAccessableFromStringToInt(accessable);
				volume.setAccessable(String.valueOf(intAccessable));
			}
			update("saveVolume", volume);
			return true;
		} catch (Exception ex) {
			logger.error("saveVolume Error : " + ex.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#getVolume(int)
	 */
	public Volume getVolume(int volumeId) {
		try {
			return (Volume) queryForObject("getVolume", new Integer(volumeId));
		} catch (Exception ex) {
			logger.error("getVolume Error : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#selectListAll()
	 */
	@SuppressWarnings("unchecked")
	public List<Volume> selectListAll() {
		try {
			Volume volume = new Volume();
			return queryForList("selectVolumeListAll", volume);
		} catch (Exception ex) {
			logger.error("selectListAll() Error : " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#deleteVolume(int)
	 */
	public boolean deleteVolume(int volumeId) {
		try {
			update("deleteVolume", new Integer(volumeId));
			return true;
		} catch (Exception ex) {
			logger.error("deleteVolume() Error : " + ex.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#updateVolume(com.sds.acube.ndisc.model.Volume)
	 */
	public boolean updateVolume(Volume volume) {
//		 2009.04.30 ������ STORServ DB ��Ű�� ������ ���ٱ��� ������ R(Read):8 C(Create):4
//		 U(Update):2 D(Delete):1 �� ���� ���� ������ ���� �ȴ�. NDISC ������ ���ٱ��� ������ �ܼ��� RCUD ��
//		 ������ �Է��ϹǷ� �̸� �����Ǵ� ���� ������ �����Ͽ� DB�� �Է��ϵ��� �����Ѵ�.
		try {
			//if (null != dbSchemaType && dbSchemaType.equalsIgnoreCase("storserv")) {
			if (dbSchemaType.equals(NDCommon.STORSERV_TYPE)) { // STOR SERVER �� ��� ���� ���� ���ϱ�
				String accessable = volume.getAccessable();
				int intAccessable = convertAccessableFromStringToInt(accessable);
				volume.setAccessable(String.valueOf(intAccessable));
			}
			update("updateVolume", volume);
			return true;
		} catch (Exception ex) {
			logger.error("updateVolume() Error :" + ex.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.acube.ndisc.dao.iface.VolumeDAO#getMaxVolumeID()
	 */
	public int getMaxVolumeID() {
		try {
			Volume volume = (Volume) queryForObject("getMaxVolumeID", null);
			return volume.getId();
		} catch (Exception ex) {
			logger.error("getMaxVolumeID() Error : " + ex.getMessage());
			return 100;
		}
	}

	/**
	 * ������ ���� ���ڿ��� ���������� �����ϱ�<br>
	 * <br>
	 *  2009.04.30 �߰��� DB Schema Ÿ���� StorServ �� ��쿡�� ������ ���� ���� ���ڷ� �ԷµǾ�� �ϹǷ�<br>
	 *  ��ȯ������ �ʿ�
	 * 
	 * @param accessable
	 *            ���ڷ� ������ ������ ���� ���ڿ�
	 * @return ����� ������ ���� ���ڰ�
	 */
	private int convertAccessableFromStringToInt(String accessable) {
		int intAccessable = 0;
		char[] charAccessable = accessable.toCharArray();
		for (int i = 0; i < charAccessable.length; i++) {
			if (String.valueOf(charAccessable[i]).equalsIgnoreCase("R")) {
				intAccessable = intAccessable + 8;
			}
			if (String.valueOf(charAccessable[i]).equalsIgnoreCase("C")) {
				intAccessable = intAccessable + 4;
			}
			if (String.valueOf(charAccessable[i]).equalsIgnoreCase("U")) {
				intAccessable = intAccessable + 2;
			}
			if (String.valueOf(charAccessable[i]).equalsIgnoreCase("D")) {
				intAccessable = intAccessable + 1;
			}
		}
		if (intAccessable == 0 || intAccessable > 15) {
			intAccessable = 8;
		}
		return intAccessable;
	}
}
