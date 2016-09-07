/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : Add XNApi
 * </pre>
 */
package com.sds.acube.jstor;

import java.util.Stack;

/**
 * JSTOR Api Factory <br>
 * Add XNApi<br>
 * 
 * @author Takkies
 *
 */
public class JSTORApiFactory {
	private final static int POOL_SIZE = 100;

	private static Stack<JSTORApi> jstorC = null;

	private static Stack<JSTORApi> jstorJ = null;

	private static Stack<JSTORApi> jstorJWin32 = null;

	private static Stack<JSTORApi> jstorNApi = null;

	private static Stack<JSTORApi> jstorXNApi = null;

	public JSTORApiFactory() {
		jstorC = new Stack<JSTORApi>(); // JNI
		jstorJ = new Stack<JSTORApi>(); // Pure Java - UNIX SERVER
		jstorJWin32 = new Stack<JSTORApi>(); // Pure Java - Win32 SERVER
		jstorNApi = new Stack<JSTORApi>(); // NDISC NAPI
		jstorXNApi = new Stack<JSTORApi>(); // XNDisc XNAPI
	}

	public JSTORApi getInstance() {
		return getInstance(null, null, null);
	}

	public JSTORApi getInstance(String apiType, String svrType, String ndiscCache) {
		String strApiType = null;
		String strSvrType = null;
		String strNDiscCache = null;
		if (apiType != null && apiType.length() > 0) {
			strApiType = apiType.toLowerCase();
		} else {
			strApiType = System.getProperty("jstor_api_type", "jni").toLowerCase();
		}
		if (svrType != null && svrType.length() > 0) {
			strSvrType = svrType.toLowerCase();
		} else {
			strSvrType = System.getProperty("jstor_svr_type", "unix").toLowerCase();
		}
		if (ndiscCache != null && ndiscCache.length() > 0) {
			strNDiscCache = ndiscCache.toLowerCase();
		} else {
			strNDiscCache = System.getProperty("ndisc_cache", "false").toLowerCase();
		}
		if (null == strApiType) { // Default jni
			strApiType = "jni";
		}
		if (null == strSvrType) { // Default unix
			strSvrType = "unix";
		}
		System.out.println("Kiyong TEST JSTORApiFactory.java getInstance strApiType ::: " + strApiType);
		System.out.println("Kiyong TEST JSTORApiFactory.java getInstance strSvrType ::: " + strSvrType);
		if ("jni".equals(strApiType)) {
			if (jstorC.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorC.push(new jstornative.JSTORNative());
			}
			return (jstornative.JSTORNative) jstorC.pop();
		} else if ("pure_java".equals(strApiType) && "unix".equals(strSvrType)) {
			if (jstorJ.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorJ.push(new com.sds.acube.jstor.JSTORNative());
			}
			return (com.sds.acube.jstor.JSTORNative) jstorJ.pop();
		} else if ("pure_java".equals(strApiType) && "win32".equals(strSvrType)) {
			if (jstorJWin32.empty()) {
				for (int i = 0; i < POOL_SIZE; i++)
					jstorJWin32.push(new com.sds.acube.jstor.JSTORNative_Win32());
			}
			return (com.sds.acube.jstor.JSTORNative_Win32) jstorJWin32.pop();
		} else if ("ndisc".equals(strApiType)) {
			boolean bCache = false;
			if (null != strNDiscCache && strNDiscCache.equals("true")) {
				bCache = true;
			}
			// if (jstorNApi.empty()) {
			// for (int i = 0; i < POOL_SIZE; i++)
			// jstorNApi.push(new com.sds.acube.jstor.NDiscNApi(bCache)); }
			// return (com.sds.acube.jstor.NDiscNApi) jstorNApi.pop();
			return new com.sds.acube.jstor.NDiscNApi(bCache);
		} else if ("xndisc".equals(strApiType.toLowerCase())) {
			boolean bCache = false;
			if (null != strNDiscCache && strNDiscCache.equals("true")) {
				bCache = true;
			}
			// if (jstorXNApi.empty()) {
			// for (int i = 0; i < POOL_SIZE; i++)
			// jstorXNApi.push(new com.sds.acube.jstor.XNDiscXNApi(bCache)); }
			// return (com.sds.acube.jstor.XNDiscXNApi) jstorXNApi.pop();			
			return new com.sds.acube.jstor.XNDiscXNApi(bCache);
		} else {// default JNI
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
	
	public int getjstorXNApiSize() {
		return jstorXNApi.size();
	}

}
