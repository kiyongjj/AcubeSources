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
 * @file StorageIF.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 15
 * Description 	  : First Draft.
 * </pre>
 */

package com.sds.acube.ndisc.mts.storage.iface;


import java.util.List;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;

/**
 * The Interface Storage.
 */
public interface StorageIF {
   
   /**
    * 스토리지 경로 정보, 미디어 아이디 정보 등을 얻어 해당 정보를 추가한 NFile 정보를 얻어온다.
    *
    * @param nFile NFile 정보 배열
    * @param option 옵션 정보
    * @return 스토리지 경로 정보, 미디어 아이디 정보 등이 추가된 NFile 정보 배열
    * @throws Exception 예외
    */
   public NFile[] aquireStorageInfo(NFile[] nFile, String option) throws Exception;
   
   /**
    * NFile 정보를 DB에 업데이트 한다.
    *
    * @param nFile NFile 정보
    * @throws Exception 예외
    */
   public void updateNFileToDB(NFile[] nFile) throws Exception;
   
   /**
    * 파일의 미디어 경로를 얻어온다.
    *
    * @param fileID 파일아이디 문자열
    * @param createDate 등록일 문자열
    * @return 얻어진 Media 경로
    * @throws Exception 예외
    */
   public String getFileMediaPath(String fileID, String createDate) throws Exception;
   
   /**
    * 파일의 NFile 정보를 얻어온다.
    *
    * @param fileId 파일아이디 문자열
    * @return NFile 정보
    * @throws Exception 예외
    */
   public NFile selectNFileFromDB(String fileId) throws Exception;
   
   /**
    * NFile 정보를 DB에 입력한다.
    *
    * @param nFiles NFile 정보
    * @throws Exception 예외
    */
   public void registNFileToDB(NFile[] nFiles) throws Exception;
   
   /**
    * NFile 정보를 DB에서 삭제한다.
    *
    * @param nFiles NFile 정보 배열
    * @throws Exception 예외
    */
   public void deleteNFileFromDB(NFile[] nFiles) throws Exception;
   
   /**
    * Media 정보를 DB에 입력한다.
    *
    * @param media Media 정보
    * @throws Exception 예외
    */
   public void insertNewMediaToDB(Media media) throws Exception;
   
   /**
    * 새로운 Volume 정보를 DB에 입력한다.
    *
    * @param volume Volume 정보
    * @throws Exception 예외
    */
   public void insertNewVolumeToDB(Volume volume) throws Exception;   
   
   /**
    * Volume 정보 리스트를 조회한다.
    *
    * @return Volume 정보 리스트
    * @throws Exception 예외
    */
   public List<Volume> selectVolumeInfoList() throws Exception;
   
   /**
    * 해당 아이디의 볼륨 정보를 조회한다.
    *
    * @param volumeId Volume 아이디 문자열
    * @return Volume 정보
    * @throws Exception 예외
    */
   public Volume selectVolumeInfo(int volumeId) throws Exception;
   
   /**
    * Media 정보 리스트를 조회한다.
    *
    * @return Media 정보 리스트
    * @throws Exception 예외
    */
   public List<Media> selectMediaInfoList() throws Exception;
   
   /**
    * 해당 아이디의 Media 정보를 조회한다.
    *
    * @param mediaId Media 아이디 문자열
    * @return Media 정보
    * @throws Exception 예외
    */
   public Media selectMediaInfo(int mediaId) throws Exception;
   
   /**
    * NFile 정보 리스트를 조회한다.
    *
    * @return NFile 정보 리스트
    * @throws Exception 예외
    */
   public List<NFile> selectFileInfoList() throws Exception;
   
   /**
    * 해당 아이디의 NFile 정보를 조회한다.
    *
    * @param fileId 파일 아이디 문자열
    * @return NFile 정보
    * @throws Exception 예외
    */
   public NFile selectFileInfo(String fileId) throws Exception;
}
