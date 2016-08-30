package com.sds.acube.ndisc.model;

public class Stat {
   private String ObjectId;
   private String ObjectType;
   private String createDate;
   private String Type;
   private String Value;
   
   public String getCreateDate() {
      return createDate;
   }
   public void setCreateDate(String createDate) {
      this.createDate = createDate;
   }
   public String getObjectId() {
      return ObjectId;
   }
   public void setObjectId(String objectId) {
      ObjectId = objectId;
   }
   public String getObjectType() {
      return ObjectType;
   }
   public void setObjectType(String objectType) {
      ObjectType = objectType;
   }
   public String getType() {
      return Type;
   }
   public void setType(String type) {
      Type = type;
   }
   public String getValue() {
      return Value;
   }
   public void setValue(String value) {
      Value = value;
   }
   
}
