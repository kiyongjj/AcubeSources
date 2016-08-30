/**
 * 
 */
package com.sds.acube.ndisc.dao;

import java.util.Iterator;

import junit.framework.TestCase;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.QueueDAO;
import com.sds.acube.ndisc.model.Queue;
import com.sds.acube.ndisc.dao.config.*;

/**
 * @author SongKangHun
 *
 */
public class QueueDAOTest extends TestCase {
   /* Private Fields */
   private DaoManager daoManager = null;
   private QueueDAO dao = null;
   private Queue queue = null;
   private final String test_id = "test_fle_id";
   
   protected void setUp() throws Exception {
      super.setUp();
      daoManager  =  DaoConfig.getDaomanager();
      dao = (QueueDAO) daoManager.getDao(QueueDAO.class);
   }

   public void testSaveQueue() throws Exception {      
      queue = new Queue();

      queue.setFileId(test_id);
      queue.setType("PDF");
      queue.setCreatedDate("10000101125959");
      queue.setStatus("N");
      
      dao.saveQueue(queue);
      
      // get fisrt object
      for (Iterator iter = dao.getQueueList().iterator(); iter.hasNext();) {
         queue = (Queue) iter.next();
         break;
      }
      
      assertEquals("10000101125959", queue.getCreatedDate());      

      // test update
      queue.setStatus("Y");
      dao.updateQueue(queue);
      assertEquals("Y", queue.getStatus());     
   }      
   
   public void testDeleteQueue() throws Exception {
      queue = new Queue();
      queue.setFileId(test_id);
      queue.setType("PDF");
      
      dao.deleteQueue(queue);
      assertNull(null);
   }
}
