package com.sds.acube.jstor;

import java.util.Stack;


public class JSTORApiFactory {
	private final static int POOL_SIZE = 100;

	private static Stack jstorC = null;

	private static Stack jstorJ = null;

	private static Stack jstorJWin32 = null;

	private static Stack jstorNApi = null;

	public JSTORApiFactory() {
		// JNI
		jstorC = new Stack();
		// Pure Java - UNIX SERVER
		jstorJ = new Stack();
		// Pure Java - Win32 SERVER
		jstorJWin32 = new Stack();
		// NDISC NAPI
		jstorNApi = new Stack();
	}
	
	public JSTORApi getInstance() {
		return getInstance(null, null, null);
	}

	public JSTORApi getInstance(String apiType, String svrType, String ndiscCache) {
		String strApiType = null;
		String strSvrType = null;
		String strNDiscCache = null;

		if(apiType != null && apiType.length() > 0)
			strApiType = apiType;
		else
			strApiType = System.getProperty("jstor_api_type");
		
		if(svrType != null && svrType.length() > 0)
			strSvrType = svrType;
		else
			strSvrType = System.getProperty("jstor_svr_type");
		
		if(ndiscCache != null && ndiscCache.length() > 0)
			strNDiscCache = ndiscCache;
		else
			strNDiscCache = System.getProperty("ndisc_cache");

		if (null == strApiType) {
			// Default jni
			strApiType = "jni";
		}

		if (null == strSvrType) {
			// Default unix
			strSvrType = "unix";
		}

		if ("jni".equals(strApiType.toLowerCase())) {
			if (jstorC.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorC.push(new jstornative.JSTORNative());
			}
			return (jstornative.JSTORNative) jstorC.pop();
		} else if ("pure_java".equals(strApiType.toLowerCase())
				&& "unix".equals(strSvrType.toLowerCase())) {
			if (jstorJ.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorJ.push(new com.sds.acube.jstor.JSTORNative());
			}
			return (com.sds.acube.jstor.JSTORNative) jstorJ.pop();
		} else if ("pure_java".equals(strApiType.toLowerCase())
				&& "win32".equals(strSvrType.toLowerCase())) {
			if (jstorJWin32.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorJWin32
							.push(new com.sds.acube.jstor.JSTORNative_Win32());
			}
			return (com.sds.acube.jstor.JSTORNative_Win32) jstorJWin32.pop();
		} else if ("ndisc".equals(strApiType.toLowerCase())) {
			boolean bCache = false;
			if (null != strNDiscCache) {
				if ("true".equals(strNDiscCache.toLowerCase())) {
					bCache = true;
				}
			}

			/*
			if (jstorNApi.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorNApi.push(new com.sds.acube.jstor.NDiscNApi(bCache));
			}
			return (com.sds.acube.jstor.NDiscNApi) jstorNApi.pop();
			*/

			return new com.sds.acube.jstor.NDiscNApi(bCache);
		}
		// default JNI
		else {
			if (jstorC.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorC.push(new jstornative.JSTORNative());
			}
			return (jstornative.JSTORNative) jstorC.pop();
		}
	}

	public int getjstorJSize() {
		return jstorJ.size();
	}

	public int getjstorJWin32Size() {
		return jstorJWin32.size();
	}

	public int getjstorCSize() {
		return jstorC.size();
	}

	public int getjstorNApiSize() {
		return jstorNApi.size();
	}
}
