/**
 * 
 */
package com.sds.acube.ndisc.dao;

import junit.framework.TestCase;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.dao.config.*;

/**
 * @author SongKangHun
 *
 */
public class VolumeDAOTest extends TestCase {
   /* Private Fields */
   private DaoManager daoManager = null;
   private VolumeDAO dao = null;
   private Volume volume = null;
   private final int test_id = 103;
   
   protected void setUp() throws Exception {
      super.setUp();
      daoManager  =  DaoConfig.getDaomanager();
      dao = (VolumeDAO) daoManager.getDao(VolumeDAO.class);
   }

   protected void teatDown() throws Exception {
//      if (null != dao.getVolume(test_id)) {
//         dao.deleteVolume(test_id);
//      }
   }

//   public void testSaveVolume() throws Exception {      
//      volume = new Volume();
//      volume.setId(201);
//      volume.setName("test_volume");
//      volume.setCreatedDate("20050101125959");
//      volume.setAccessable("RCUD");
//      volume.setDesc("test");
//      
//      dao.saveVolume(volume);
//      //volume = dao.getVolume(test_id);
//      //assertEquals("test_volume", volume.getName());      
//
//      // test update
//      //volume.setAccessable("CRUDTEST");
//      //dao.updateVolume(volume);
//      //assertEquals("CRUDTEST", volume.getAccessable());     
//   }      

   public void testGetVolume() throws Exception {
      volume = dao.getVolume(test_id);
      System.out.println("test = " + (Integer.parseInt(NDConstant.STAT_ENC) & Integer.parseInt(NDConstant.STAT_COMP)));
      System.out.println("test = " + (2 & 2));      
      assertNotNull(volume);      
      // assertEquals("test_volume", volume.getName());
   }
   
//   public void testDeleteVolume() throws Exception {
//      dao.deleteVolume(test_id);
//      volume = dao.getVolume(test_id);
//      assertNull(volume);
//   }
   
//   public void testGetMaxVolumeID() throws Exception {
//      System.out.println(dao.getMaxVolumeID());
//   }
   
   
   
}
