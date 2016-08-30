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
 * @file DefStorageAdaptor.java
 * Author	          : Takkies
 * Date   	          : 2014. 9. 3
 * Description 	  : First Draft.
 * </pre>
 */
package com.sds.acube.ndisc.mts.storage.impl;

import java.util.List;

import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;
import com.sds.acube.ndisc.mts.util.cipher.jce.SymmetryCipher;
import com.sds.acube.ndisc.mts.util.sea.EncryptorBySEA;

/**
 * The Class DefStorageAdaptor.
 */
public abstract class DefStorageAdaptor implements StorageIF {
   
   /** The logger. */
   LoggerIF logger = null;

   /** The cipher. */
   SymmetryCipher cipher = null;
   
   /**  The sea Encrypt. */
   EncryptorBySEA sea = null;
   
   /**
    * Instantiates a new DefStorageAdaptor.
    *
    * @param logger ·Î°Å
    */
   public DefStorageAdaptor(LoggerIF logger) {
      this.logger = logger;
      this.cipher = new SymmetryCipher(NDCommon.NDISC_CIPHER_KEY);
      this.sea = new EncryptorBySEA();      
   }   
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#aquireStorageInfo(com.sds.acube.ndisc.model.NFile[], java.lang.String)
    */
   public abstract NFile[] aquireStorageInfo(NFile[] nFiles, String option) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#updateNFileToDB(com.sds.acube.ndisc.model.NFile[])
    */
   public abstract void updateNFileToDB(NFile[] nFiles) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#getFileMediaPath(java.lang.String, java.lang.String)
    */
   public abstract String getFileMediaPath(String fileId, String createDate) throws Exception;   
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectNFileFromDB(java.lang.String)
    */
   public abstract NFile selectNFileFromDB(String fileId) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#registNFileToDB(com.sds.acube.ndisc.model.NFile[])
    */
   public abstract void registNFileToDB(NFile[] nFiles) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#deleteNFileFromDB(com.sds.acube.ndisc.model.NFile[])
    */
   public abstract void deleteNFileFromDB(NFile[] nFiles) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#insertNewMediaToDB(com.sds.acube.ndisc.model.Media)
    */
   public abstract void insertNewMediaToDB(Media media) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#insertNewVolumeToDB(com.sds.acube.ndisc.model.Volume)
    */
   public abstract void insertNewVolumeToDB(Volume volume) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectVolumeInfoList()
    */
   public abstract List<Volume> selectVolumeInfoList() throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectVolumeInfo(int)
    */
   public abstract Volume selectVolumeInfo(int volumeId) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectMediaInfoList()
    */
   public abstract List<Media> selectMediaInfoList() throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectMediaInfo(int)
    */
   public abstract Media selectMediaInfo(int mediaId) throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectFileInfoList()
    */
   public abstract List<NFile> selectFileInfoList() throws Exception;
   
   /* (non-Javadoc)
    * @see com.sds.acube.ndisc.mts.storage.iface.StorageIF#selectFileInfo(java.lang.String)
    */
   public abstract NFile selectFileInfo(String fileId) throws Exception;
   
}


