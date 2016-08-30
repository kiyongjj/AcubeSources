package com.sds.acube.ndisc.dao.config;

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoManagerBuilder;

import java.io.Reader;
import java.util.Properties;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.util.cipher.jce.*;


public class DaoConfig {

    // Constants for JDBC
    private static final String JDBC_CATEGORY = "JDBC";    
    private static final String URL = "URL";
    private static final String DRIVER = "DRIVER";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String GENERAL_CATEGORY = "general";
    private static final String ENC_FLAG = "ENC_FLAG";
    private static final String CYPER_KEY = "cipher_key";
    
	private static final String SCHEMA_DIR = "SCHEMATYPE";
	private static final String SCHEMA_TYPE = "schema_type";
	private static final String STORAGE_CATEGORY = "storage";

    private static final DaoManager daoManager;
    private static final Configuration config = ConfigurationManager.getConfiguration();
    

    static {

        try {
            String resource = "com/sds/acube/ndisc/dao/config/dao.xml";
        	//String resource = "dao.xml";
            Reader reader = Resources.getResourceAsReader(resource);
            daoManager = DaoManagerBuilder.buildDaoManager(reader, getJDBCProps());
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize DaoConfig.  Cause: " + e);
        }
    }

    static Properties getJDBCProps() {
        Properties props = new Properties();

        props.setProperty(DRIVER, config.getProperty(DRIVER, null, JDBC_CATEGORY));
        props.setProperty(USERNAME, config.getProperty(USERNAME, null, JDBC_CATEGORY));
        
      //2011.11.14 DB Password 암호화  //////////////////////////////////////////////////////////////////////////
        SymmetryCipher cipher = new SymmetryCipher(config.getProperty(CYPER_KEY, null, GENERAL_CATEGORY));
        String flag = config.getProperty(ENC_FLAG, config.getProperty(ENC_FLAG,"N",JDBC_CATEGORY));
        String Conf_Pwd = config.getProperty(PASSWORD, null, JDBC_CATEGORY);
        String password = "";
        if(flag.equalsIgnoreCase("Y")){
        	try{
        		password = cipher.decrypt(Conf_Pwd);        		
        	}catch(Exception e){        		
        		throw new RuntimeException("Could not initialize DaoConfig.  Cause: " + e);
        	}        
        }else if(flag.equalsIgnoreCase("N")){           	
        	password = Conf_Pwd;
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        props.setProperty(PASSWORD, password);
        props.setProperty(URL, config.getProperty(URL, null, JDBC_CATEGORY));

		/**
		 * 스키마 타입에 따라 sql 을 자동 지정.
		 * 
		 * @since 2014.09.22
		 */
		String schemaType = config.getProperty(SCHEMA_TYPE, NDCommon.NDISC_TYPE, STORAGE_CATEGORY).toLowerCase();
		if (schemaType.equals(NDCommon.NDISC_TYPE)) {
			schemaType = "sql";
		} else if (schemaType.equals(NDCommon.STORSERV_TYPE)) {
			schemaType = "sqlStorServ";
		} else {
			schemaType = "sql".concat(config.getProperty(SCHEMA_TYPE, NDCommon.NDISC_TYPE, STORAGE_CATEGORY));
		}

		props.setProperty(SCHEMA_DIR, schemaType);
		
        return props;
    }

    public static DaoManager getDaomanager() {
        return daoManager;
    }
}
