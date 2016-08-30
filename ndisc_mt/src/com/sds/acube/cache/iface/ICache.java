//$Id: ICache.java,v 1.1 2009/12/22 05:32:18 mrjh.com Exp $
package com.sds.acube.cache.iface;

import com.sds.acube.cache.CacheException;

/** 
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 */
public interface ICache {
	/**
	 * Get an item from the cache
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object get(Object key) throws CacheException;
	/**
	 * Add an item to the cache
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void put(Object key, Object value) throws CacheException;
    /**
     * Addd an item to the cache and manage an item in cache dir
     * @param key
     * @param path
     * @throws CacheException
     */
    public void putInCache(String key, String path) throws CacheException;
	/**
	 * Remove an item from the cache
	 */
	public void remove(Object key) throws CacheException;
    /**
     * Remove an item from the cache
     */
    public void removeFromCache(Object key) throws CacheException;
	/**
	 * Clear the cache
	 */
	public void clear() throws CacheException;
	/**
	 * Clean up
	 */
	public void destroy() throws CacheException;
	/**
	 * If this is a clustered cache, lock the item
	 */
	public void lock(Object key) throws CacheException;
	/**
	 * If this is a clustered cache, unlock the item
	 */
	public void unlock(Object key) throws CacheException;
	/**
	 * Generate a timestamp
	 */
	public long nextTimestamp();
	/**
	 * Get a reasonable "lock timeout"
	 */
	public int getTimeout();
	/**
     * Set Cache Capacity 
	 */
    public void setCacheCapacity(int cacheCapacity);
    /**
     * Set Cache Directory 
     */
    public void setCacheDir(String cacheDir);
}






