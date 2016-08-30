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
	 * DB에 저장된 파일 정보 얻어오기
	 * 
	 * @param fileId
	 *            파일 아이디
	 * @return 얻어진 파일 정보
	 */
   public NFile getFile(String fileId);
   
   /**
	 * DB에 파일 정보 저장하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param nFile
	 *            파일 정보
	 */
   public int saveFile(NFile nFile);
   
   /**
	 * DB에 여러개의 파일 정보 저장하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param nFiles
	 *            파일 정보 배열
	 */
   public int saveFile(NFile[] nFiles);   
   
   /**
	 * DB에서 파일 정보 삭제하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param fileId
	 *            파일 아이디
	 */
   public int deleteFile(String fileId);
   
   /**
	 * DB에서 여러개의 파일 정보 삭제하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param fileIds
	 *            파일 아이디 배열
	 */
   public int deleteFile(String[] fileIds);   
   
   /**
	 * DB에 파일 정보 수정하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param nFile
	 *            파일 정보
	 */
   public int updateFile(NFile nFile);
   
   /**
	 * DB에 여러개의 파일 정보 수정하기<br>
	 * 2014.02.17 return 타입 지정
	 * 
	 * @param nFiles
	 *            파일 정보 배열
	 */
   public int updateFile(NFile[] nFiles);
   
   /**
	 * DB에서 파일 정보(아이디)로 부터 미디어 경로 얻어오기
	 * 
	 * @param fileId
	 *            파일 아이디
	 * @return 미디어 경로
	 */
   public String getMediaPathByFile(String fileId);   
   
   /**
	 * DB에 등록된 모든 파일 정보 얻어오기
	 * 
	 * @return 파일 정보 목록
	 */
   public List<NFile> selectListAll();   
   
   /**
	 * DB에서 미디어 아이디로 부터 파일 정보 목록 얻어오기
	 * 
	 * @param mediaId
	 *            미디어 아이디
	 * @return 파일정보 목록
	 */
   public List<NFile> selectListByMediaId(String mediaId);   
   
   /**
	 * DB에서 파일 생성일(LIKE조건) 과 Media 아이디로 부터 파일 정보 목록 얻어오기.
	 * 
	 * @param nFile
	 *            파일정보
	 * @return 파일정보 목록
	 */
   public List<NFile> selectListByCreateDataAndMediaID(NFile nFile);   
   
   /**
	 * 년도 정보 가져오기.
	 * 
	 * @param dataBox
	 *            파라미터 데이터
	 * @return 년도 정보
	 */
   public List<DataBox> getYear(DataBox dataBox);
   
   /**
	 * 달 정보 가져오기.
	 * 
	 * @param dataBox
	 *            파라미터 데이터
	 * @return 월 정보
	 */
   public List<DataBox> getMonth(DataBox dataBox);
   
   /**
	 * 일 정보 가져오기.
	 * 
	 * @param dataBox
	 *            파라미터 데이터
	 * @return 일 정보
	 */
   public List<DataBox> getDay(DataBox dataBox);
}
