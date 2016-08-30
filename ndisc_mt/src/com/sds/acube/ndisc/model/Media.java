package com.sds.acube.ndisc.model;

public class Media {
   private int Id;
   private String Name;
   private int Type;
   private String Path;
   private String createdDate;
   private String Desc;
   private long MaxSize;
   private long Size;
   private int VolumeId;
   
   //for using StorServ DB shchema
   private long freeSpace;
      
   public String getCreatedDate() {
      return createdDate;
   }
   public void setCreatedDate(String createdDate) {
      this.createdDate = createdDate;
   }
   public String getDesc() {
      return Desc;
   }
   public void setDesc(String desc) {
      Desc = desc;
   }
   public int getId() {
      return Id;
   }
   public void setId(int id) {
      Id = id;
   }
   public String getName() {
      return Name;
   }
   public void setName(String name) {
      Name = name;
   }
   public String getPath() {
      return Path;
   }
   public void setPath(String path) {
      Path = path;
   }
   public int getType() {
      return Type;
   }
   public void setType(int type) {
      Type = type;
   }
   public int getVolumeId() {
      return VolumeId;
   }
   public void setVolumeId(int volumeId) {
      VolumeId = volumeId;
   }
   public long getMaxSize() {
      return MaxSize;
   }
   public void setMaxSize(long maxSize) {
      MaxSize = maxSize;
   }
   public long getSize() {
      return Size;
   }
   public void setSize(long size) {
      Size = size;
   }

   public long getFreeSpace() {
	   return this.freeSpace;
   }

   public void setFreeSpace(long freeSpace) {
	   this.freeSpace = freeSpace;
   }
}
