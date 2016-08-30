package com.sds.acube.cache;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sds.acube.cache.iface.ICache;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * OSCache�� ���� Cache Service Class
 * @author KangHun Song
 * @version $Revision: 1.1 $ $Date: 2009/12/22 05:32:16 $
 */
public class OSCache implements ICache {

    //private GeneralCacheAdministrator cache = new GeneralCacheAdministrator();
	
	private GeneralCacheAdministrator cache = null;	//new GeneralCacheAdministrator();

    // Member field
    private int refreshPeriod;
    private String cacheDir;
 
    // logger append
    static Logger logger = Logger.getLogger(OSCache.class);
    
    // default�� Cache�� expire ���� �ʵ��� �Ѵ�
    public OSCache() {
        // this.refreshPeriod = CacheEntry.INDEFINITE_EXPIRY;
         this(CacheEntry.INDEFINITE_EXPIRY, null); // use oscache.properties
     }

    public OSCache(Properties props) {
    	this(CacheEntry.INDEFINITE_EXPIRY, props);
    }
    
    public OSCache(int refreshPeriod, Properties props) {
    	if (props == null || props.size() == 0) {
    		cache = new GeneralCacheAdministrator(); // use oscache.properties
     	} else {
      		cache = new GeneralCacheAdministrator(props);
       	}
        this.refreshPeriod = refreshPeriod;
    }

    public void setCacheCapacity(int cacheCapacity) {
        cache.setCacheCapacity(cacheCapacity);
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public Object get(Object key) throws CacheException {
        try {
            return cache.getFromCache(String.valueOf(key), refreshPeriod);
        } catch (NeedsRefreshException e) {
            cache.cancelUpdate(String.valueOf(key));
            return null;
        }
    }

    public void put(Object key, Object value) throws CacheException {
        cache.putInCache(String.valueOf(key), value);
    }

    /**
     * Cache�� �ְ�, �ش� object�� cache_dir �Ʒ� �����Ѵ�
     * 
     * @param key
     * @param path
     * @throws CacheException
     */
    public void putInCache(String Id, String path) throws CacheException {
        try {
            // ����� Cache ���ϸ� 
            // ���� : cache_dir/fle_id
            String CachePath = cacheDir + File.separator + Id;
            logger.debug("\t Path : " + path + " CachePath : " + CachePath);
            
            // ���� ��쿡��, 
            // ȯ�漳���� cache_dir �Ʒ��� transfer
            if ( !new File(CachePath).exists() ) {
                CacheUtil.copyFile( path,CachePath );
            }
            cache.putInCache(Id, CachePath);                            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CacheException(ex);
        }
    }

    /**
     * Cache���� key�� �����ϰ� object�� cache_dir���� �����Ѵ�
     * @param key
     * @throws CacheException
     */
    public void removeFromCache(Object key) throws CacheException {       
        try {
            String CachePath = cacheDir + File.separator + String.valueOf(key);            
            
            // ���� ���, cache entry������ �����Ѵ�
            if (!new File(CachePath).exists()) {
                cache.flushEntry(String.valueOf(key));
                return;
            }
            
            // cache object�� cache_dir���� �����Ѵ�                            
            new File(CachePath).delete();
        } catch (Exception e) {
            // TODO: handle exception
        }        
        cache.flushEntry(String.valueOf(key));
    }
    
    public void remove(Object key) throws CacheException {
        cache.flushEntry(String.valueOf(key));
    }

    public void clear() throws CacheException {
        cache.flushAll();
    }

    public void destroy() throws CacheException {
        cache.destroy();
    }

    public void lock(Object key) throws CacheException {
        // local cache, so we use synchronization
    }

    public void unlock(Object key) throws CacheException {
        // local cache, so we use synchronization
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public int getTimeout() {
        return Timestamper.ONE_MS * 60000; // ie. 60 seconds
    }

}