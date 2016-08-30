package com.sds.acube.ndisc.model;

public class NFile {
   private String Id;
   private String Name;
   private int Size;
   private String createdDate;
   private String modifiedDate;
   private int mediaId;
   private int volumeId;
   private String statType;
   private String TmpPath;
   private String storagePath;
   private String year;
   private String month;
   private String day;
   
   public String getStoragePath() {
      return this.storagePath;
   }

   public void setStoragePath(String storagePath) {
      this.storagePath = storagePath;
   }

   public String getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(String createdDate) {
      this.createdDate = createdDate;
   }

   public int getMediaId() {
      return mediaId;
   }

   public void setMediaId(int mediaId) {
      this.mediaId = mediaId;
   }

   public String getModifiedDate() {
      return modifiedDate;
   }

   public void setModifiedDate(String modifiedDate) {
      this.modifiedDate = modifiedDate;
   }

   public String getName() {
      return Name;
   }

   public void setName(String name) {
      Name = name;
   }

   public int getSize() {
      return Size;
   }

   public void setSize(int size) {
      Size = size;
   }

   public String getId() {
      return Id;
   }

   public void setId(String Id) {
      this.Id = Id;
   }

   public String getStatType() {
      return statType;
   }

   public void setStatType(String statType) {
      this.statType = statType;
   }

   public int getVolumeId() {
      return volumeId;
   }

   public void setVolumeId(int volumeId) {
      this.volumeId = volumeId;
   }

   public String getTmpPath() {
      return this.TmpPath;
   }

   public void setTmpPath(String tmpPath) {
      this.TmpPath = tmpPath;
   }
   public String getYear() {
	   return this.year;
   }

   public void setYear(String year) {
	   this.year = year;
   }

   public String getMonth() {
	   return this.month;
   }

   public void setMonth(String month) {
	   this.month = month;
   }

   public String getDay() {
	   return this.day;
   }


   public void setDay(String day) {
	   this.day = day;
   }   
}
