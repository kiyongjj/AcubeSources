package com.sds.rqreport.model;

import java.io.Serializable;

public class RQDocStatTB implements Serializable{

	private static final long serialVersionUID = 1L;
	private int    RUN_TIME = 0;
	private String FILE_NM = "";
	private int    RUNCNT = 0;
	private int    SERVERTIME_ACCUMUL = 0;
	private int    SERVERTIME_AVE = 0;
	private int    TOTALTIME_ACCUMUL = 0;
	private int    TOTALTIME_AVE = 0;
	private int    MAXTIME = 0;
	private String MAXTIME_RUNVAR = "";
	private int    MINTIME = 0;
	private String MINTIME_RUNVAR = "";
	private int    ERROR_CNT = 0;
	public int getRUN_TIME() {
		return RUN_TIME;
	}
	public int getSERVERTIME_ACCUMUL() {
		return SERVERTIME_ACCUMUL;
	}
	public void setSERVERTIME_ACCUMUL(int servertime_accumul) {
		SERVERTIME_ACCUMUL = servertime_accumul;
	}
	public void setRUN_TIME(int run_time) {
		RUN_TIME = run_time;
	}
	public String getFILE_NM() {
		return FILE_NM;
	}
	public void setFILE_NM(String file_nm) {
		FILE_NM = file_nm;
	}
	public int getRUNCNT() {
		return RUNCNT;
	}
	public void setRUNCNT(int runcnt) {
		RUNCNT = runcnt;
	}
	public int getSERVERTIME_AVE() {
		return SERVERTIME_AVE;
	}
	public void setSERVERTIME_AVE(int servertime_ave) {
		SERVERTIME_AVE = servertime_ave;
	}
	public int getTOTALTIME_ACCUMUL() {
		return TOTALTIME_ACCUMUL;
	}
	public void setTOTALTIME_ACCUMUL(int totaltime_accumul) {
		TOTALTIME_ACCUMUL = totaltime_accumul;
	}
	public int getTOTALTIME_AVE() {
		return TOTALTIME_AVE;
	}
	public void setTOTALTIME_AVE(int totaltime_ave) {
		TOTALTIME_AVE = totaltime_ave;
	}
	public int getMAXTIME() {
		return MAXTIME;
	}
	public void setMAXTIME(int maxtime) {
		MAXTIME = maxtime;
	}
	public String getMAXTIME_RUNVAR() {
		return MAXTIME_RUNVAR;
	}
	public void setMAXTIME_RUNVAR(String maxtime_runvar) {
		MAXTIME_RUNVAR = maxtime_runvar;
	}
	public int getMINTIME() {
		return MINTIME;
	}
	public void setMINTIME(int mintime) {
		MINTIME = mintime;
	}
	public String getMINTIME_RUNVAR() {
		return MINTIME_RUNVAR;
	}
	public void setMINTIME_RUNVAR(String mintime_runvar) {
		MINTIME_RUNVAR = mintime_runvar;
	}
	public int getERROR_CNT() {
		return ERROR_CNT;
	}
	public void setERROR_CNT(int error_cnt) {
		ERROR_CNT = error_cnt;
	}
	
	
	
}
