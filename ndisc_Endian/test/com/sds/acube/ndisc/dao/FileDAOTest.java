/**
 * 
 */
package com.sds.acube.ndisc.dao;

import junit.framework.TestCase;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.FileDAO;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.dao.config.*;

/**
 * @author SongKangHun
 *
 */
public class FileDAOTest extends TestCase {
   /* Private Fields */
   private DaoManager daoManager = null;
   private FileDAO dao = null;
   private MediaDAO mediaDao = null;
   private NFile nFile = null;
   private Media media = null;
   private final String test_id = "test_fle_id";
   private final int test_media_id = 9999;
   
   protected void setUp() throws Exception {
      super.setUp();
      daoManager  =  DaoConfig.getDaomanager();
      dao = (FileDAO) daoManager.getDao(FileDAO.class);
      mediaDao = (MediaDAO) daoManager.getDao(MediaDAO.class);     
   }

   protected void teatDown() throws Exception {
      if (null != dao.getFile(test_id)) {
         dao.deleteFile(test_id);
      }           
   }

   public void testSaveFile() throws Exception {            
      // create media
      media = new Media();
      media.setId(test_media_id);
      media.setName("test_media");
      media.setMaxSize(999999999);
      media.setSize(0);
      media.setPath("/media");
      media.setType(10);
      media.setVolumeId(101);
      
      mediaDao.saveMedia(media);
      
      // create file
      nFile = new NFile();
      nFile.setId(test_id);
      nFile.setName("test");
      nFile.setStatType("1");
      nFile.setMediaId(test_media_id);
      nFile.setSize(1000);

      dao.saveFile(nFile);
      nFile = dao.getFile(nFile.getId());
      assertEquals("test", nFile.getName());      
      assertEquals("1", nFile.getStatType());      
      
      // check get media path
      assertEquals("/media", dao.getMediaPathByFile(nFile.getId()));   
      
      // check media size
      media = mediaDao.getMedia(test_media_id);
      assertEquals(1000, media.getSize());
      
      // test update
      nFile.setSize(1500);
      dao.updateFile(nFile);
      media = mediaDao.getMedia(test_media_id);
      assertEquals(1500, media.getSize());
      nFile = dao.getFile(nFile.getId());
      assertEquals(1500, nFile.getSize());
      mediaDao.deleteMedia(test_media_id);         
   }      

   public void testGetFile() throws Exception {
      nFile = dao.getFile(test_id);
      assertNotNull(nFile);      
      assertEquals("test", nFile.getName());            
   }
   
   public void testDeleteFile() throws Exception {
      dao.deleteFile(test_id);
      nFile = dao.getFile(test_id);
      assertNull(nFile);
   }
}
