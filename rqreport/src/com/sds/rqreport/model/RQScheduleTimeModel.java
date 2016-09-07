package com.sds.rqreport.model;

import java.util.*;

import com.sds.rqreport.util.Encoding;

public class RQScheduleTimeModel {
	
	private int schedule;
	private Calendar m_cal_startDate;

	private String startDate; // <---cal
	
	private int repeatType;
	
	private int dayOfMonth = 27;
	private int dayOfWeek; // <---cal
	
	private int periodOfDay;
	private int periodOfWeek;
	private int periodOfMonth = 1; // 1
	
	private int ordinalOfDayType = 2; // 2
	private int dayType = 3;	// 3
	private int notification = 0; // 0
	private int repeatFreq = 1; // 1
	private int repeatBoundType = 1; // 1
	private Calendar repeatEndDate;
	private int Status = 0; // 0 : default , 1: complete
	
	private String lm_serverCharset;
	private String lm_RQCharset;
	Calendar cal = null;
	
	public RQScheduleTimeModel() {
		Encoding enc = new Encoding();
		lm_serverCharset = enc.getServerCharset();
		lm_RQCharset = enc.getRQCharset();
		
		repeatEndDate = Calendar.getInstance();
		repeatEndDate.set(4000,1,1);
	}
	
	public Calendar getM_cal_startDate() {
		return m_cal_startDate;
	}
	public void setM_cal_startDate(Calendar date) {
		m_cal_startDate = date;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		
		String[] lm_startDateArr = startDate.split(":");
		cal = Calendar.getInstance();
		cal.set(
				Integer.parseInt(lm_startDateArr[0]), 
				Integer.parseInt(lm_startDateArr[1]) - 1, 
				Integer.parseInt(lm_startDateArr[2]),
				Integer.parseInt(lm_startDateArr[3]),
				Integer.parseInt(lm_startDateArr[4]),
				Integer.parseInt(lm_startDateArr[5])
				);
		setM_cal_startDate(cal);
		this.startDate = startDate;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public int getDayType() {
		return dayType;
	}
	public void setDayType(int dayType) {
		this.dayType = dayType;
	}
	public String getLm_RQCharset() {
		return lm_RQCharset;
	}
	public void setLm_RQCharset(String lm_RQCharset) {
		this.lm_RQCharset = lm_RQCharset;
	}
	public String getLm_serverCharset() {
		return lm_serverCharset;
	}
	public void setLm_serverCharset(String lm_serverCharset) {
		this.lm_serverCharset = lm_serverCharset;
	}
	public int getNotification() {
		return notification;
	}
	public void setNotification(int notification) {
		this.notification = notification;
	}
	public int getOrdinalOfDayType() {
		return ordinalOfDayType;
	}
	public void setOrdinalOfDayType(int ordinalOfDayType) {
		this.ordinalOfDayType = ordinalOfDayType;
	}
	public int getPeriodOfDay() {
		return periodOfDay;
	}
	public void setPeriodOfDay(int periodOfDay) {
		this.periodOfDay = periodOfDay;
	}
	public int getPeriodOfMonth() {
		return periodOfMonth;
	}
	public void setPeriodOfMonth(int periodOfMonth) {
		this.periodOfMonth = periodOfMonth;
	}
	public int getPeriodOfWeek() {
		return periodOfWeek;
	}
	public void setPeriodOfWeek(int periodOfWeek) {
		this.periodOfWeek = periodOfWeek;
	}
	public int getRepeatBoundType() {
		return repeatBoundType;
	}
	public void setRepeatBoundType(int repeatBoundType) {
		this.repeatBoundType = repeatBoundType;
	}
	public Calendar getRepeatEndDate() {
		return repeatEndDate;
	}
	public void setRepeatEndDate(Calendar repeatEndDate) {
		this.repeatEndDate = repeatEndDate;
	}
	public int getRepeatFreq() {
		return repeatFreq;
	}
	public void setRepeatFreq(int repeatFreq) {
		this.repeatFreq = repeatFreq;
	}
	public int getRepeatType() {
		return repeatType;
	}
	public void setRepeatType(int repeatType) {
		this.repeatType = repeatType;
	}
	public int getSchedule() {
		return schedule;
	}
	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}
	
}
