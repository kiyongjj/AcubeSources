package com.sds.rqreport.util;

public class TimeMeasure
{
	long currentTime;
	public TimeMeasure()
	{
		currentTime = System.currentTimeMillis();
	}
	
	public long getEllipsedTime()
	{
		long timeTmp = System.currentTimeMillis();
		long time = timeTmp - currentTime;
		currentTime = timeTmp;
		return time;
	}
}