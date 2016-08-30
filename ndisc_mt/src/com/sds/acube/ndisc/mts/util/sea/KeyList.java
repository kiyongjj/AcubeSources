/**
 * 
 */
package com.sds.acube.ndisc.mts.util.sea;

/**
 * @author 윤병준
 *
 */
public class KeyList {
	
	private long[] ek = null;
	private long[] dk = null;
	
	public KeyList(int ROUND){
		ek = new long[ROUND + 2];
		dk = new long[ROUND + 2];
	}

	/**
	 * @return the ek
	 */
	public long[] getEk() {
		return this.ek;
	}

	/**
	 * @param ek the ek to set
	 */
	public void setEk(long[] ek) {
		this.ek = ek;
	}

	/**
	 * @return the dk
	 */
	public long[] getDk() {
		return this.dk;
	}

	/**
	 * @param dk the dk to set
	 */
	public void setDk(long[] dk) {
		this.dk = dk;
	}	
}
