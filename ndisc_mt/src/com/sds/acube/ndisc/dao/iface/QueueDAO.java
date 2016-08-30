package com.sds.acube.ndisc.dao.iface;

import com.ibatis.common.util.PaginatedList;
import com.sds.acube.ndisc.model.Queue;

public interface QueueDAO extends Dao {

   public void saveQueue(Queue queue);

   public PaginatedList getQueueList();

   public void updateQueue(Queue queue);

   public void deleteQueue(Queue queue);
}
