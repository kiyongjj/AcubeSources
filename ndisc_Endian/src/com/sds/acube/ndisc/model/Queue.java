package com.sds.acube.ndisc.model;

public class Queue {
   private String FileId;
   private String Type;
   private String createdDate;
   private String Status;
   
   public String getCreatedDate() {
      return createdDate;
   }
   public void setCreatedDate(String createDate) {
      this.createdDate = createDate;
   }
   public String getFileId() {
      return FileId;
   }
   public void setFileId(String fileId) {
      FileId = fileId;
   }
   public String getStatus() {
      return Status;
   }
   public void setStatus(String status) {
      Status = status;
   }
   public String getType() {
      return Type;
   }
   public void setType(String type) {
      Type = type;
   }
   
}
