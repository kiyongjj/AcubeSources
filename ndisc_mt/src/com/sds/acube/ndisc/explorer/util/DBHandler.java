package com.sds.acube.ndisc.explorer.util;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.*;

import com.sds.acube.ndisc.dao.iface.FileDAO;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.model.DataBox;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.explorer.logger.LogFactory;
import com.sds.acube.ndisc.dao.config.DaoConfig;

/*
 * 1. 2009.04.30 직접 JDBC 로 데이터베이스에 접근하여 데이터를 다루던 방식에서 iBatis 를 이용한 방식으로 변경
 * 2. NDISC 의 dao 를 이용하기 위해 NDISC 의 클래스들을 ndisc.jar로 묶어서 라이브러리 형태로 포함시킴 
 * 3. 따라서 dao 관련 클래스 수정을 위해서는 NDISC 프로젝트의 dao 쪽을 수정한 후 jar로 다시 묶어서 Explorer에 포함 시켜야 함
 */
public class DBHandler {

   //static Connection conn = null;
   //static Statement stmt = null;
   static Logger logger = null;
   
   static {
      try {
         //Class.forName(NDCommon.JDBC_DRIVER);
         //conn = DriverManager.getConnection(NDCommon.JDBC_URL, NDCommon.JDBC_USERNAME, NDCommon.JDBC_PASSWORD);
    	 logger = LogFactory.getLogger("explorer");
         NDCommon.daoManager = DaoConfig.getDaomanager();         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   /*
    * "List 로 반환된 결과를 String 배열로 변경하는 함수"
    * 기존 DBHandler를 이용하는 클래스들의 수정을 최소화 하기 위해 dao를 통해 넘어온 결과를 
    * 기존 방식의 반환 값으로 변환하기 위한 함수이다. 
    */   
   private static String[][] makeReturnArray(List list, String option){
  		String[][] returnArray = null;
  		int listCount = list.size();
  		   		
  		if(option.equalsIgnoreCase("VOLUME")){
  			returnArray = new String[listCount][6];
  			for(int i=0;i<listCount;i++){   			
  				Volume volume = (Volume)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = String.valueOf(volume.getId());
  				returnArray[i][2] = volume.getName();
  				returnArray[i][3] = volume.getAccessable();
  				returnArray[i][4] = volume.getCreatedDate();
  				returnArray[i][5] = volume.getDesc();  				
  			}  			
  		}else if(option.equalsIgnoreCase("MEDIA")){
  			returnArray = new String[listCount][10];
  			for(int i=0;i<listCount;i++){   			
  				Media media = (Media)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = String.valueOf(media.getId());
  				returnArray[i][2] = media.getName();
  				returnArray[i][3] = String.valueOf(media.getType());
  				returnArray[i][4] = media.getPath();
  				returnArray[i][5] = media.getCreatedDate();
  				returnArray[i][6] = media.getDesc();
  				returnArray[i][7] = String.valueOf(media.getMaxSize());
  				returnArray[i][8] = String.valueOf(media.getSize());
  				returnArray[i][9] = String.valueOf(media.getVolumeId());
  			}  						
  			
  		}else if(option.equalsIgnoreCase("FILE")){
  			returnArray = new String[listCount][8];
  			for(int i=0;i<listCount;i++){   			
  				NFile fileInfo = (NFile)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = String.valueOf(fileInfo.getId());
  				returnArray[i][2] = fileInfo.getName();
  				returnArray[i][3] = String.valueOf(fileInfo.getSize());
  				returnArray[i][4] = fileInfo.getCreatedDate();
  				returnArray[i][5] = fileInfo.getModifiedDate();
  				returnArray[i][6] = fileInfo.getStatType();
  				returnArray[i][7] = String.valueOf(fileInfo.getMediaId());  				
  			} 			
  		}else if(option.equalsIgnoreCase("YEAR_DIR")){
  			returnArray = new String[listCount][4];
  			for(int i=0;i<listCount;i++){   			
  				DataBox dataBox = (DataBox)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = (String)dataBox.get("FIRST");
  				returnArray[i][2] = (String)dataBox.get("YEAR");
  				returnArray[i][3] = (String)dataBox.get("MEDIAID");  				 				
  			} 			  			
  		}else if(option.equalsIgnoreCase("MONTH_DIR")){
  			returnArray = new String[listCount][4];
  			for(int i=0;i<listCount;i++){   			
  				DataBox dataBox = (DataBox)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = (String)dataBox.get("FIRST");
  				returnArray[i][2] = (String)dataBox.get("MONTH");
  				returnArray[i][3] = (String)dataBox.get("MEDIAID");  				 				
  			} 			  			  			
  		}else if(option.equalsIgnoreCase("DAY_DIR")){
  			returnArray = new String[listCount][4];
  			for(int i=0;i<listCount;i++){   			
  				DataBox dataBox = (DataBox)list.get(i);   				   				
  				returnArray[i][0] = option;
  				returnArray[i][1] = (String)dataBox.get("FIRST");
  				returnArray[i][2] = (String)dataBox.get("DAY");
  				returnArray[i][3] = (String)dataBox.get("MEDIAID");  				 				
  			} 			  			
  		} 		
  		
  		
  		return returnArray;
  	}
   
   //Methods For Volume //////////////////////////////////////////////////////////////////////////////////////////////////////
   	public static String[][] getVolumes() {
   		try{
	   		// String query = "select * from tnd_volume order by vol_id asc";
		    //String[][] ret = getQueryResult(query);   		
	   		VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
	   		List volumelist = dao.selectListAll();
   		
	   		return  makeReturnArray(volumelist,"VOLUME"); 		
   		}catch(Exception ex){
   			logger.error("getVolumes() error : " + ex.getMessage()); 
   			return null;
   		}
	    //return (getObjectResult(ret, "VOLUME"));
	}   	
   	
   	public static boolean updateVolume(Volume volume) {
        /*String query = "update tnd_volume set vol_name = '" + volume.getName() + "', " + "vol_access = '"
              + volume.getAccessable() + "', " + "vol_crtdt = '" + volume.getCreatedDate() + "', " + "vol_desc = '"
              + volume.getDesc() + "' " + "where vol_id = " + volume.getId();*/
   		VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
   		  		
   		if(dao.updateVolume(volume)){
   			return true;
   		}else{
   			return false;   			
   		}   		        
     }
   	
   	public static boolean deleteVolume(Volume volume) {
        //String query = "delete tnd_volume where vol_id = " + volume.getId();
   		int Id = volume.getId();
   		VolumeDAO dao = (VolumeDAO) NDCommon.daoManager.getDao(VolumeDAO.class);
   		if(dao.deleteVolume(Id)){
   			return true;   			
   		}else{
   			return false;
   		}   		     
     }
   	
   	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   	   
   //Methods For Media///////////////////////////////////////////////////////////////////////////////////////////////////////////
   	public static String[][] getMedias(int volumeId) {
        //String query = "select * from tnd_media where md_volid = " + volID + " order by md_id asc";
        //String[][] ret = getQueryResult(query);
   		try{
	   		MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
	   		List mediaList = dao.selectListByVolumeId(volumeId);  		
	   		return makeReturnArray(mediaList,"MEDIA");
   		}catch(Exception ex){
   			logger.error("getMedias : " + ex.getMessage());
   			return null;
   		}
     }
   	
   	
   	public static boolean updateMedia(Media media) {

        /*String query = "update tnd_media set md_name = '" + media.getName() + "', " + "md_type = " + media.getType()
              + ", " + "md_crtdt = '" + media.getCreatedDate() + "', " + "md_desc = '" + media.getDesc() + "', "
              + "md_maxsize = " + media.getMaxSize() + ", " + "md_volid = " + media.getVolumeId() + " "
              + "where md_id = " + media.getId();*/
   		
   		try{
   			MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
	   		dao.updateMedia(media);  		
	   		return true;
   		}catch(Exception ex){
   			logger.error("updateMedia() : " + ex.getMessage());
   			return false;
   		}
   	}

     public static boolean deleteMedia(Media media) {

    //    String query = "delete tnd_media where md_id = " + media.getId();
    	 try{    		
 	   		MediaDAO dao = (MediaDAO) NDCommon.daoManager.getDao(MediaDAO.class);
 	   		dao.deleteMedia(media.getId());  		
 	   		return true;
    	 }catch(Exception ex){
    		logger.error("deleteMedia() : " + ex.getMessage());
    		return false;
    	 }
     
     }
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   
   //Methods For File  ////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public static String[][] getFile(String fileID) {
      /*String query = "select * from tnd_file where fle_id = '" + fileID + "' order by fle_crtdt desc";
      String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "FILE"));*/
	   
	   FileDAO dao = (FileDAO) NDCommon.daoManager.getDao(FileDAO.class);
	   NFile fileInfo = dao.getFile(fileID);
	   LinkedList list = new LinkedList();
	   list.add(fileInfo);
	   return makeReturnArray(list,"FILE");	  
   }

   public static String[][] getFiles(int mediaID) {
      /*String query = "select * from tnd_file where fle_mdid = " + mediaID + " order by fle_crtdt desc";
      String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "FILE"));*/
	  FileDAO dao = (FileDAO)NDCommon.daoManager.getDao(FileDAO.class);	
	  List list = dao.selectListByMediaId(String.valueOf(mediaID));
	  return makeReturnArray(list,"FILE");   
   }

   public static String[][] getFiles(String createDate, int mediaID) {
      /*String query = "select * from tnd_file where fle_mdid = " + mediaID + " and fle_crtdt like '" + createDate
            + "%' order by fle_crtdt desc";
          String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "FILE"));*/
	   NFile nFile = new NFile();
	   nFile.setCreatedDate(createDate);
	   nFile.setMediaId(mediaID);
	   FileDAO dao = (FileDAO)NDCommon.daoManager.getDao(FileDAO.class);
	   List list = dao.selectListByCreateDataAndMediaID(nFile);
	   return makeReturnArray(list,"FILE");	   
   }

   public static String[][] getYears(int mediaID) {
	   /*String query = "select distinct substr(fle_crtdt, 0, 4), substr(fle_crtdt, 0, 4), " + mediaID + " from tnd_file "
            + "where fle_mdid = " + mediaID;
            String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "YEAR_DIR"));*/
	   DataBox dataBox = new DataBox();
	   dataBox.put("mediaId", mediaID);
	   FileDAO dao = (FileDAO)NDCommon.daoManager.getDao(FileDAO.class);
	   List yearList = dao.getYear(dataBox);
	   return makeReturnArray(yearList,"YEAR_DIR");	   
   }

   public static String[][] getMonths(String year, int mediaID) {
      /*String query = "select distinct substr(fle_crtdt, 0, 6), substr(fle_crtdt, 5, 2), " + mediaID + " from tnd_file "
            + "where fle_mdid = " + mediaID + " and fle_crtdt like '" + year + "%'";
      String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "MONTH_DIR"));*/
	   DataBox dataBox = new DataBox();
	   dataBox.put("mediaId", mediaID);
	   dataBox.put("year", year);
	   FileDAO dao = (FileDAO)NDCommon.daoManager.getDao(FileDAO.class);
	   List monthList = dao.getMonth(dataBox);
	   return makeReturnArray(monthList,"MONTH_DIR");	    

   }

   public static String[][] getDays(String year_month, int mediaID) {
      /*String query = "select distinct substr(fle_crtdt, 0, 8), substr(fle_crtdt, 7, 2), " + mediaID + " from tnd_file "
            + "where fle_mdid = " + mediaID + " and fle_crtdt like '" + year_month + "%'";
           String[][] ret = getQueryResult(query);
      return (getObjectResult(ret, "DAY_DIR"));*/
	   DataBox dataBox = new DataBox();
	   dataBox.put("mediaId", mediaID);
	   dataBox.put("year_month", year_month);
	   FileDAO dao = (FileDAO)NDCommon.daoManager.getDao(FileDAO.class);
	   List dayList = dao.getDay(dataBox);
	   return makeReturnArray(dayList,"DAY_DIR");   
	  
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /* 
   private static String[][] getQueryResult(String query) {
      String[][] result = null;
      ResultSet rs = null;

      try {
         stmt = conn.createStatement();
         rs = stmt.executeQuery(query);
         result = getArray(rs);
         
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            stmt.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }

      return result;
   }

   private static boolean executeQuery(String query) {
      boolean bRet = false;

      try {
         stmt = conn.createStatement();
         stmt.executeUpdate(query);
         bRet = true;
      } catch (Exception e) {
         e.printStackTrace();
         bRet = false;
      } finally {
         try {
            stmt.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }

      return bRet;
   }

   private static String[][] getObjectResult(String[][] tmp, String opt) {

      String[][] ret = null;

      if (null != tmp) {
         ret = new String[tmp.length][];
         for (int i = 0; i < tmp.length; i++) {
            ret[i] = new String[tmp[i].length + 1];
            ret[i][0] = opt;
            for (int j = 0; j < tmp[i].length; j++) {
               ret[i][j + 1] = tmp[i][j];
            }
         }
      }

      return ret;
   }

   private static String[][] getArray(ResultSet rs) {
      int columns = -1;
      int rows = -1;
      String[] columnNames = null;
      ArrayList resultList = null;
      String[][] result = null;

      if (rs == null) {
         resultList = null;
         columns = 0;
         rows = 0;
      } else {
         resultList = new ArrayList();
         try {
            ResultSetMetaData rsmd = rs.getMetaData();
            columns = rsmd.getColumnCount();
            columnNames = new String[columns];
            for (int i = 0; i < columns; i++) {
               columnNames[i] = rsmd.getColumnName(i + 1);
            }
            String[] tmp = null;
            Object objTmp = null;
            while (rs.next()) {
               tmp = new String[columns];
               for (int i = 0; i < columns; i++) {
                  objTmp = rs.getObject(i + 1);
                  tmp[i] = objTmp == null ? "" : objTmp.toString();
               }
               resultList.add(tmp);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         rows = resultList.size();
      }

      if (rows == 0) {
         result = null;
      } else {
         String[][] retArr = (String[][]) resultList.toArray(new String[1][columns]);
         result = retArr;
      }

      return result;
   }
   */
}
