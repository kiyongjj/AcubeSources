package com.sds.acube.ndisc.dao.impl;

import org.apache.log4j.Logger;

import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.template.SqlMapDaoTemplate;
import com.sds.acube.ndisc.mts.common.NDCommon;

public class BaseSqlMapDAO extends SqlMapDaoTemplate {

	protected static final int PAGE_SIZE = 10;
	protected static final int QUE_SIZE = 50;

	//protected static final String dbSchemaType = System.getProperty("schemaType");

	/**
	 * ȯ��(config.xml)���� ���������� ����
	 * @since 2014.09.22
	 */
	protected static final String dbSchemaType = NDCommon.SCHEMA_TYPE;
	
	// logger append
	static Logger logger = Logger.getLogger(BaseSqlMapDAO.class);

	public BaseSqlMapDAO(DaoManager daoManager) {
		super(daoManager);
	}
}
