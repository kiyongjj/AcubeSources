package com.sds.acube.ndisc.dao.impl;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.QueueDAO;
import com.sds.acube.ndisc.model.Queue;

public class QueueSqlMapDAO extends BaseSqlMapDAO implements QueueDAO {

    public QueueSqlMapDAO(DaoManager daoManager) {
        super(daoManager);
    }

    public void saveQueue(Queue queue) {
        update("saveQueue", queue);
    }

    public PaginatedList getQueueList() {
        Object parameterObject = null;
        try {
            return queryForPaginatedList("getQueueLists", parameterObject, QUE_SIZE);
        } catch (Exception ex) {
            return null;
        }
    }

    public void deleteQueue(Queue queue) {
        update("deleteQueue", queue);
    }

    public void updateQueue(Queue queue) {
        update("updateQueue", queue);
    }
}
