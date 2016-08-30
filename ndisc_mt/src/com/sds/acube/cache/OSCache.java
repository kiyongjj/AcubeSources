package com.sds.acube.cache;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.sds.acube.cache.iface.ICache;

/**
 * OSCache를 통한 Cache Service Class
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
    
    // default로 Cache는 expire 되지 않도록 한다
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
     * Cache에 넣고, 해당 object를 cache_dir 아래 관리한다
     * 
     * @param key
     * @param path
     * @throws CacheException
     */
    public void putInCache(String Id, String path) throws CacheException {
    	try {
            // 저장될 Cache 파일명 
            // 형태 : cache_dir/fle_id
            String CachePath = cacheDir + File.separator + Id;
            logger.debug("\t Path : " + path + " CachePath : " + CachePath);
            
            // 없을 경우에는, 
            // 환경설정의 cache_dir 아래로 transfer
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
     * Cache에서 key를 제거하고 object도 cache_dir에서 제거한다
     * @param key
     * @throws CacheException
     */
    public void removeFromCache(Object key) throws CacheException {       
        try {
            String CachePath = cacheDir + File.separator + String.valueOf(key);            
            
            // 없을 경우, cache entry에서만 제거한다
            if (!new File(CachePath).exists()) {
                cache.flushEntry(String.valueOf(key));
                return;
            }
            
            // cache object를 cache_dir에서 제거한다                            
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