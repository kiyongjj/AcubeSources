//$Id: CacheException.java,v 1.1 2009/12/22 05:32:16 mrjh.com Exp $
package com.sds.acube.cache;

/**
 * Something went wrong in the cache
 */
//public class CacheException extends HibernateException {
public class CacheException extends Exception {
   static final long serialVersionUID = -2325822038990805636L;
   
	public CacheException(String s) {
		super(s);
	}

	public CacheException(Exception e) {
		super(e);
	}

}






 
