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
 * @file FileDAO.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 15
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.dao.iface;

import java.util.List;

import com.sds.acube.ndisc.model.DataBox;
import com.sds.acube.ndisc.model.NFile;

/**
 * The Interface FileDAO.
 */
public interface FileDAO extends Dao {

   /**
	 * DB�� ����� ���� ���� ������
	 * 
	 * @param fileId
	 *            ���� ���̵�
	 * @return ����� ���� ����
	 */
   public NFile getFile(String fileId);
   
   /**
	 * DB�� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param nFile
	 *            ���� ����
	 */
   public int saveFile(NFile nFile);
   
   /**
	 * DB�� �������� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param nFiles
	 *            ���� ���� �迭
	 */
   public int saveFile(NFile[] nFiles);   
   
   /**
	 * DB���� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param fileId
	 *            ���� ���̵�
	 */
   public int deleteFile(String fileId);
   
   /**
	 * DB���� �������� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param fileIds
	 *            ���� ���̵� �迭
	 */
   public int deleteFile(String[] fileIds);   
   
   /**
	 * DB�� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param nFile
	 *            ���� ����
	 */
   public int updateFile(NFile nFile);
   
   /**
	 * DB�� �������� ���� ���� �����ϱ�<br>
	 * 2014.02.17 return Ÿ�� ����
	 * 
	 * @param nFiles
	 *            ���� ���� �迭
	 */
   public int updateFile(NFile[] nFiles);
   
   /**
	 * DB���� ���� ����(���̵�)�� ���� �̵�� ��� ������
	 * 
	 * @param fileId
	 *            ���� ���̵�
	 * @return �̵�� ���
	 */
   public String getMediaPathByFile(String fileId);   
   
   /**
	 * DB�� ��ϵ� ��� ���� ���� ������
	 * 
	 * @return ���� ���� ���
	 */
   public List<NFile> selectListAll();   
   
   /**
	 * DB���� �̵�� ���̵�� ���� ���� ���� ��� ������
	 * 
	 * @param mediaId
	 *            �̵�� ���̵�
	 * @return �������� ���
	 */
   public List<NFile> selectListByMediaId(String mediaId);   
   
   /**
	 * DB���� ���� ������(LIKE����) �� Media ���̵�� ���� ���� ���� ��� ������.
	 * 
	 * @param nFile
	 *            ��������
	 * @return �������� ���
	 */
   public List<NFile> selectListByCreateDataAndMediaID(NFile nFile);   
   
   /**
	 * �⵵ ���� ��������.
	 * 
	 * @param dataBox
	 *            �Ķ���� ������
	 * @return �⵵ ����
	 */
   public List<DataBox> getYear(DataBox dataBox);
   
   /**
	 * �� ���� ��������.
	 * 
	 * @param dataBox
	 *            �Ķ���� ������
	 * @return �� ����
	 */
   public List<DataBox> getMonth(DataBox dataBox);
   
   /**
	 * �� ���� ��������.
	 * 
	 * @param dataBox
	 *            �Ķ���� ������
	 * @return �� ����
	 */
   public List<DataBox> getDay(DataBox dataBox);
}
