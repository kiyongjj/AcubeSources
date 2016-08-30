package com.sds.acube.cache;

import java.io.File;
import java.util.Properties;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sds.acube.cache.iface.ICache;

/**
 * CacheService�� ���� ȯ�漳���� �ϰ� CacheService�� �����Ѵ�
 * 
 * @author KangHun Song
 * @version $Revision: 1.1 $ $Date: 2009/12/22 05:32:16 $
 */
public class CacheConfig {

    private static final ICache cacheService;
    private static final Configuration config = ConfigurationManager.getConfiguration();
    
    // Cache Service ȯ�漳�� ����
    private static final String CACHE_CATEGORY = "cache";
    private static final String CACHE_DIR = "cache_dir";
    private static final String CAPACITY = "capacity";

	private static final String CACHE_MEMORY = "memory";
	private static final String CACHE_ALGORITHM = "algorithm";
	private static final String CACHE_UNLIMITED_DISK = "unlimited_disk";
	private static final String CACHE_BLOCKING = "blocking";
	private static final String CACHE_PERSISTENCE_CLASS = "persistence_class";
	private static final String CACHE_PERSISTENCE_DISK_HASH_ALGORITHM = "persistence_disk_hash_algorithm";
	private static final String CACHE_PERSISTENCE_OVERFLOW_ONLY = "persistence_overflow_only";
	private static final String CACHE_EVENT_LISTENERS = "event_listeners";
	private static final String CACHE_PATH = "path";
	
    static {
        try {
			if (config == null) {
				System.out.println("configuration is null");
			}
			
			/**
			 * NDISC Cache ���� ���� ���������� OOM �߻� ����<br>
			 * OSCache��ü ������ �̹� capacity Size�� ���� cacheMap ��ü�� ����ü�� �����Ǵµ�<br>
			 * CacheConfig������ ��ü ���� ���� ���Ŀ� setCacheCapacity�� �����մϴ�.<br>
			 * ���� cacheMap ��ü�� UnlimitedCache() ��ü�� �����Ǿ� ���������� Cache Object�� �����Ǵ� ���� �߻�<br>
			 * 
			 * <code>
			 * private GeneralCacheAdministrator cache = new GeneralCacheAdministrator();
			 * 			=> GeneralCacheAdministrator�� createCache() ����
			 * 			=> Cache ��ü�� ������ ȣ��
			 * 			=> Cache ��ü�� ������ �ҽ��� �Ʒ� �ڵ� ����
			 * 
			 * 		if (this.cacheMap == null) {
			 * 			if (capacity > 0) {
			 * 				this.cacheMap = new LRUCache(capacity);
			 * 			} else {
			 * 				this.cacheMap = new UnlimitedCache();
			 * 			}
			 *    }
			 * </code>
			 */

			String capacity = config.getProperty(CAPACITY, "1000", CACHE_CATEGORY);
			String cachedir = config.getProperty(CACHE_DIR, null, CACHE_CATEGORY);
			String memorycache = config.getProperty(CACHE_MEMORY, "false", CACHE_CATEGORY);
			memorycache = (memorycache.equals("")) ? "false" : memorycache;
			String algorithm = config.getProperty(CACHE_ALGORITHM, null, CACHE_CATEGORY);
			String unlimiteddiskcache = config.getProperty(CACHE_UNLIMITED_DISK, "false", CACHE_CATEGORY);
			unlimiteddiskcache = (unlimiteddiskcache.equals("")) ? "false" : unlimiteddiskcache;
			String blocking = config.getProperty(CACHE_BLOCKING, "false", CACHE_CATEGORY);
			blocking = (blocking.equals("")) ? "false" : blocking;
			String persistenceclass = config.getProperty(CACHE_PERSISTENCE_CLASS, null, CACHE_CATEGORY);
			String persistencediskhashalgorithm = config.getProperty(CACHE_PERSISTENCE_DISK_HASH_ALGORITHM, null, CACHE_CATEGORY);
			String persistenceoverflowonly = config.getProperty(CACHE_PERSISTENCE_OVERFLOW_ONLY, "false", CACHE_CATEGORY);
			persistenceoverflowonly = (persistenceoverflowonly.equals("")) ? "false" : persistenceoverflowonly;
			String eventlisteners = config.getProperty(CACHE_EVENT_LISTENERS, null, CACHE_CATEGORY);
			String path = config.getProperty(CACHE_PATH, cachedir, CACHE_CATEGORY);

			Properties props = new Properties();
			props.setProperty(getPropertiesKey(CAPACITY), capacity); // �ݵ�� �����ؾ� LRUCache ��ü�� ����
			if (!isEmpty(algorithm)) {
				props.setProperty(getPropertiesKey(CACHE_ALGORITHM), algorithm);
			}
			if (!isEmpty(persistenceclass)) {
				props.setProperty(getPropertiesKey(CACHE_PERSISTENCE_CLASS), persistenceclass);
			}
			if (!isEmpty(persistencediskhashalgorithm)) {
				props.setProperty(getPropertiesKey(CACHE_PERSISTENCE_DISK_HASH_ALGORITHM), persistencediskhashalgorithm);
			}
			if (!isEmpty(eventlisteners)) {
				props.setProperty(getPropertiesKey(CACHE_EVENT_LISTENERS), eventlisteners);
			}
			props.setProperty(getPropertiesKey(CACHE_MEMORY), memorycache);
			props.setProperty(getPropertiesKey(CACHE_UNLIMITED_DISK), unlimiteddiskcache);
			props.setProperty(getPropertiesKey(CACHE_BLOCKING), blocking);
			props.setProperty(getPropertiesKey(CACHE_PERSISTENCE_OVERFLOW_ONLY), persistenceoverflowonly);
			props.setProperty(getPropertiesKey(CACHE_PATH), path);

			cacheService = new OSCache(props);

			cacheService.setCacheCapacity(config.getIntProperty(CAPACITY, 1000, CACHE_CATEGORY));
			cacheService.setCacheDir(config.getProperty(CACHE_DIR, null, CACHE_CATEGORY));

            // cache_dir �ʱ�ȭ �۾� �ʿ�
			initDir(cachedir);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize CacheConfig.  Cause: " + e);
        }
    } 

	static String getPropertiesKey(String attr) {
		return CACHE_CATEGORY.concat(".").concat(attr.replaceAll("_", "."));
	}

	static boolean isEmpty(String val) {
		if (val == null || val.equals("")) {
			return true;
		}
		return false;
	}
	
    static void initDir(String path) {
        deleteFiles(new File(path));
    }

    static void deleteFiles(File dir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    public static ICache getService() {
        return cacheService;
    }

}
