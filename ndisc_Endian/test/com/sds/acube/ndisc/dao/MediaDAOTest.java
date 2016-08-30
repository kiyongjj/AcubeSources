/**
 * 
 */
package com.sds.acube.ndisc.dao;

import junit.framework.TestCase;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.dao.config.*;

/**
 * @author SongKangHun
 *
 */
public class MediaDAOTest extends TestCase {
   /* Private Fields */
   private DaoManager daoManager = null;
   private MediaDAO dao = null;
   private Media media = null;
   // private final int test_id = 999;
   // private final int test_vol_id = 999;
   
   protected void setUp() throws Exception {
      super.setUp();
      daoManager  =  DaoConfig.getDaomanager();
      dao = (MediaDAO) daoManager.getDao(MediaDAO.class);
   }

   protected void teatDown() throws Exception {
//      if (null != dao.getMedia(test_id)) {
//         dao.deleteMedia(test_id);
//      }
   }

   public void testSaveMedia() throws Exception {      
      media = new Media();
      media.setId(202);
      media.setName("test_media");
      media.setType(10);     
      media.setPath("/media");   
      media.setCreatedDate("20050101125959");
      media.setDesc("test media");
      media.setMaxSize(999999999);
      media.setSize(10240000);
      media.setVolumeId(202);
      
      dao.saveMedia(media);
//      media = dao.getMedia(test_id);
//      assertEquals("test_media", media.getName());      
//
//      // test update
//      media.setVolumeId(test_vol_id);
//      dao.updateMedia(media);
//      assertEquals(test_vol_id, media.getVolumeId());     
   }      

//   public void testGetMedia() throws Exception {
//      media = dao.getMedia(test_id);
//      assertNotNull(media);      
//      assertEquals("test_media", media.getName());            
//   }
//
//   public void testGetAvailableMedia() throws Exception {
//      Media[] mediaList = dao.getAvailableMedia(test_vol_id);
//      assertEquals(1, mediaList.length);
//   }   
//   
//   public void testDeleteMedia() throws Exception {
//      dao.deleteMedia(test_id);
//      media = dao.getMedia(test_id);
//      assertNull(media);
//   }

}
