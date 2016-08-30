package com.sds.acube.ndisc.mts.storage.iface;


import java.util.List;

import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;

public interface StorageIF {
   public NFile[] aquireStorageInfo(NFile[] nFile, String option) throws Exception;
   public void updateNFileToDB(NFile[] nFile) throws Exception;
   public String getFileMediaPath(String fileID, String createDate) throws Exception;
   public NFile selectNFileFromDB(String fileID) throws Exception;
   public void registNFileToDB(NFile[] nFile) throws Exception;
   public void deleteNFileFromDB(NFile[] nFile) throws Exception;
   public void insertNewMediaToDB(Media media) throws Exception;
   public void insertNewVolumeToDB(Volume volume) throws Exception;   
   public List selectVolumeInfoList() throws Exception;
   public Volume selectVolumeInfo(int id) throws Exception;
   public List selectMediaInfoList() throws Exception;
   public Media selectMediaInfo(int id) throws Exception;
   public List selectFileInfoList() throws Exception;
   public NFile selectFileInfo(String id) throws Exception;
   //2014.04.04 - add
   public Media[] selectAvailableMedia(int volumeID) throws Exception;
   //2014.04.04 - add
   public String getMediaPathByFile(String fileID) throws Exception;
}
