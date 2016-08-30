package com.sds.acube.ndisc.mts.vo;


/**
 * @author ky.cho
 *
 */
public class JobVO
{
   private String serviceStat;
   private int objectCounts;
   private Object object = null;
       
   public String getServiceStat()
   {
      return this.serviceStat;
   }

   public void setServiceStat(String serviceStat)
   {
      this.serviceStat = serviceStat;
   }

   public int getFileCount()
   {
      return this.objectCounts;
   }
   
   public void setFileCount(int nNumOfFiles)
   {
      this.objectCounts = nNumOfFiles;
   }
   
   public Object getObject() {
      return this.object;
   }

   public void setObject(Object object) {
      this.object = object;
   }   
}
