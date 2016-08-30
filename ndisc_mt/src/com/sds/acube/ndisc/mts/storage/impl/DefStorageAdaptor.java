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

public abstract class DefStorageAdaptor implements StorageIF {
   LoggerIF logger = null;
   // ndisc cipher class for file_id encoding & decoding   
   SymmetryCipher cipher = null;
   EncryptorBySEA sea = null;
   
   public DefStorageAdaptor(LoggerIF logger) {
      this.logger = logger;
      this.cipher = new SymmetryCipher(NDCommon.NDISC_CIPHER_KEY);
      this.sea = new EncryptorBySEA();      
   }   
   
   public abstract NFile[] aquireStorageInfo(NFile[] nFile, String option) throws Exception;
   public abstract void updateNFileToDB(NFile[] nFile) throws Exception;
   public abstract String getFileMediaPath(String fileID, String createDate) throws Exception;   
   public abstract NFile selectNFileFromDB(String fileID) throws Exception;
   public abstract void registNFileToDB(NFile[] nFile) throws Exception;
   public abstract void deleteNFileFromDB(NFile[] nFile) throws Exception;
   public abstract void insertNewMediaToDB(Media media) throws Exception;
   public abstract void insertNewVolumeToDB(Volume volume) throws Exception;
   public abstract List selectVolumeInfoList() throws Exception;
   public abstract Volume selectVolumeInfo(int id) throws Exception;
   public abstract List selectMediaInfoList() throws Exception;
   public abstract Media selectMediaInfo(int id) throws Exception;
   public abstract List selectFileInfoList() throws Exception;
   public abstract NFile selectFileInfo(String id) throws Exception;
   
}


