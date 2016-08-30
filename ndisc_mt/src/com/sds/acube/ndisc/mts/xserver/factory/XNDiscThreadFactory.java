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
 * Description 	  : 
 * </pre>
 */
package com.sds.acube.ndisc.mts.xserver.factory;

import java.util.concurrent.ThreadFactory;

import com.sds.acube.ndisc.mts.xserver.util.XNDiscConfig;

/**
 * 쓰레드 Priority 밑 쓰레드명 지정
 * 
 * @author Takkies
 *
 */
public class XNDiscThreadFactory implements ThreadFactory {

	public static final String PREFIX = "XNDISC-WORKER-";
	private int thread_num = 0;

	private boolean isdaemon = true;
	
	public XNDiscThreadFactory(boolean isdaemon) {
		this.isdaemon = isdaemon;
	}
	
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName(PREFIX + (++thread_num));
		t.setDaemon(isdaemon);
		
		if (XNDiscConfig.getString(XNDiscConfig.WORKER_POOL_PRIORITY, "NORM").equals("MAX")) {
			t.setPriority(Thread.MAX_PRIORITY);	
		} else if (XNDiscConfig.getString(XNDiscConfig.WORKER_POOL_PRIORITY, "NORM").equals("MIN")) {
			t.setPriority(Thread.MIN_PRIORITY);
		} else {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}
