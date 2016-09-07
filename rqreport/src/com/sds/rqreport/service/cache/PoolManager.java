package com.sds.rqreport.service.cache;

import java.util.*;
import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;

public class PoolManager {

	Environment env = Environment.getInstance();
	private static PoolManager pManager = null;
	private int MAX_RESOURCE = env.engine_thread;
	private long TIME_WAIT = env.engine_time_wait * 1000;
	private Hashtable poolHash = new Hashtable();
	private Stack freeStack = new Stack();
	private Logger L = Logger.getLogger("MANAGER");

	private PoolManager(){
		for(int i = 0; i< MAX_RESOURCE;i++){
			Integer index = new Integer(i);
			poolHash.put(index, new PoolObject(index));
			freeStack.push(index);
		}
	}

	/**
	 * singletom pattern
	 * @return PoolManager
	 */
	public static synchronized PoolManager getInstance(){
		if(pManager == null){
			pManager = new PoolManager();
		}
		return pManager;
	}

	/**
	 * PoolManager에서 일할 수 있는 poolObject 를 가져온다.
	 * PoolManager에서 여유가 없을 때는 기다리며 TIME_WAIT까지 대기한 후 Exception
	 * @return PoolObject
	 * @throws Exception TIME_WAIT 까지 대기한 후 Exception 처리
	 */
	public PoolObject getPoolObject() throws Exception{
		Integer index = null;
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		synchronized(this){
			while(freeStack.empty()){
				try {
					wait(1*10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				endTime = System.currentTimeMillis();
				if( freeStack.empty() && (endTime-startTime) >= TIME_WAIT){
//					index = (Integer)freeStack.pop();
					throw new Exception("getPoolObject : time out (" + TIME_WAIT + ") exceed");
				}
			}
			index = (Integer)freeStack.pop();
		}
		PoolObject obj = (PoolObject)poolHash.get(index);
		return obj;
	}

	/**
	 * Stop&Resume 을 구현하기 위한 API
	 * 특정 index 의 PoolObject 를 가져온다.
	 * @param ii(특정 index)
	 * @return PoolObject
	 */
	public PoolObject getPoolObject(int ii) {
		Integer index = new Integer(ii);

		PoolObject obj = null;
		try {
			obj = (PoolObject)poolHash.get(index);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 사용한 PoolObject 를 다시 PoolManager에 반환한다.
	 * @param obj(반환할 PoolObject)
	 */
	public void release(PoolObject obj){
		synchronized(this) {
			obj.Reset();
			freeStack.push(obj.getIndex());
			notifyAll();
			//notify();
		}
	}
}
