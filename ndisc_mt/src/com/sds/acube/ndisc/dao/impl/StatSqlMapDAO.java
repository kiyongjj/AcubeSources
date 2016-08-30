package com.sds.acube.ndisc.dao.impl;

import com.ibatis.dao.client.DaoManager;
import com.sds.acube.ndisc.dao.iface.StatDAO;

public class StatSqlMapDAO extends BaseSqlMapDAO implements StatDAO {

   public StatSqlMapDAO(DaoManager daoManager) {
      super(daoManager);
   }

}
