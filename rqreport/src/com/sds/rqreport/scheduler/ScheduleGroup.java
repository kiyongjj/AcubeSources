package com.sds.rqreport.scheduler;
import java.util.*;
import org.apache.log4j.*;

public class ScheduleGroup {
	private static Logger L = Logger.getLogger("SCHEDULER");
	Calendar startTime;
	int[] seconds;
	int[] groupsizes;
	int[] groupidxs	;
	int[] groupexists;
	Object[] groups;
	int groupsize;
	int timesection;
	int groupexistsize;
	 
	public ScheduleGroup(int groupsize, int timeinterval, int timesection)
	{
		int size;
		size = (timeinterval * 60) / timesection + 1;
		seconds = new int[size];
		groups = new Object[size];
		groupidxs = new int[size];
		groupexists = new int[size];
		this.groupsize = groupsize;
		groupexistsize = 0;
	
		this.timesection = timesection;
		// 시간 초기화 
		for(int i = 0; i < size; i++ )
		{
		  seconds[i] = i * timeinterval;
		  groups[i] = null;
		  groupidxs[i] = 0;
		}
	}
	public void setStartTime( Calendar startTime)
	{
		this.startTime = startTime;
		msgCal("ScheduleGroupStart Time:",startTime, startTime);
	}
	
	public void add(int scheduleID, Date time)
	{
		Calendar timet = Calendar.getInstance();
		timet.setTime(time);
		msgCal("ScheduleGroup Added Time:",timet, timet);
		long diff = timet.getTime().getTime() - startTime.getTime().getTime();
		int second = (int)(diff / 1000L);
		int idx = second / timesection;
		// Thread Group이 없으면 만든다.
		if(groups[idx] == null)
		{
			groups[idx] = new Vector[groupsize];
			
			for( int i = 0; i < groupsize; ++i)
			{
				((Vector[])groups[idx])[i] = new Vector(10,10);
			}
			groupexists[groupexistsize] = idx;
			groupexistsize++;
			
		}
		Vector[] threadgroup = (Vector[])groups[idx];
		threadgroup[groupidxs[idx]].add(new Integer(scheduleID));
		threadgroup = null;
		groupidxs[idx]++;
		if(groupidxs[idx] >= groupsize)
		{
			groupidxs[idx] = 0;
		}
		
	}
	
	public void clearAll()
	{
		groupexistsize = 0;
		for(int i = 0; i < groups.length; i++ )
		{
//		  seconds[i] = i * timeinterval;
		  groups[i] = null;
		  groupidxs[i] = 0;
		  groupexists[i] = 0;
		}		
	}
	
	public int getGroupSize()
	{
		return groupexistsize;
	}
	
	public Vector[] get(int num)
	{
		return getbyidx(groupexists[num]);
	}
	public int getTime(int num)
	{
		System.out.println("getTime:" + seconds[groupexists[num]]);
		return seconds[groupexists[num]];
	}
	public Vector[] getbyidx(int idx)
	{
		return (Vector[])groups[idx];
	}
	
	private void msgCal(String s, Calendar date, Calendar time)
	{
	  System.out.println(s + ":" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)+ "-" + date.get(Calendar.DAY_OF_MONTH) +" " +time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" +time.get(Calendar.SECOND));
		 L.debug(s + ":" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)+ "-" + date.get(Calendar.DAY_OF_MONTH) +" " +time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" +time.get(Calendar.SECOND));
		 return;
	}
}
