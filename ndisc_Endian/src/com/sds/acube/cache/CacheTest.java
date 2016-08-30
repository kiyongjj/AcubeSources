package com.sds.acube.cache;

import com.sds.acube.cache.iface.ICache;

import junit.framework.TestCase;


public class CacheTest extends TestCase {

    // CacheConfig를 통해 CacheService를 얻는
    ICache cache = CacheConfig.getService();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPutInCache() { 
        String key = "cache_key";
        String path = "D:\\temp\\test.zip";
        
        try {
            cache.putInCache(key, path);     
            assertNotNull( cache.get(key) );
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public void testRemoveCache() {
        String key = "cache_key";
        String path = "D:\\temp\\test.zip";
        
        try {
            cache.putInCache(key, path);     
            assertNotNull( cache.get(key) );
            cache.removeFromCache(key);
            assertNull( cache.get(key) );
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
