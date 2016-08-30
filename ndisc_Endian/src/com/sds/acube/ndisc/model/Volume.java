package com.sds.acube.ndisc.model;

public class Volume {
   private int Id;
   private String Name;
   private String Accessable;
   private String createdDate;
   private String Desc;
   
   public String getAccessable() {
      return Accessable;
   }
   public void setAccessable(String accessable) {
      Accessable = accessable;
   }
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
   
   
}
