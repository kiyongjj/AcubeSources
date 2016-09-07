package com.sds.rqreport.scheduler;

import java.util.*;
import com.sds.rqreport.*;
import org.apache.log4j.*;

public class ScheduleArrangement extends TimerTask {
  protected Timer tm;
  protected ScheduleGroup sg;
  private static Logger L = Logger.getLogger("SCHEDULER");
  SchedulerEnv env = null;
  public ScheduleArrangement()
  {
	env = SchedulerEnv.getInstance();
  	sg = new ScheduleGroup(env.scheduler_thread_group, env.schedulerInterval, env.scheduler_groupinterval);
  }

  protected void finalize()
      throws Throwable {
    tm = null;
    super.finalize();
  }

  public void run() {
  	long currTime = System.currentTimeMillis();
    if (System.currentTimeMillis() - scheduledExecutionTime() >=
        env.schedulerInterval * 60 * 1000) {
      L.info("Too late, skip this execution.");
      return; // Too late; skip this execution.
    }

    /**@todo Implement this java.lang.Runnable abstract method*/
    // Repository Manager 에서 종료되지 않은 스케줄링 정보를 가져온다.
    // 받은 스케줄 개수만큼 쓰레드를 발생하여 실행시킨다.
    // 만일 실행할 쓰레드가 많은 경우라면? => 시작시간에 차이를 두는 방식으로..
    // 그룹핑을 하여 Serial하게 실행한다.
    Vector v = getScheduleJob();
    Vector idV = (Vector) v.get(0);
    Vector timeV = (Vector) v.get(1);

    try {
      int tsize = idV.size();
      long ttime = 0;
      Calendar job = null;
      job = Calendar.getInstance();
      job.setTime( new Date(scheduledExecutionTime()));
	  	sg.clearAll();
      sg.setStartTime(job);
      for (int i = 0; i < tsize; i++) {
      	//threadgrouping
				Date startTime = (Date) timeV.get(i);
				
				sg.add(((Integer)idV.get(i)).intValue(), startTime);
				
      }
      //      	
        tm = new Timer(false);
        
        int groupsz = sg.getGroupSize();
        for (int i = 0; i < groupsz; ++i )
        {
        	int second;
        	Vector[]  threadgroup;
        	threadgroup = sg.get(i);

        	for(int j = 0; j < threadgroup.length; ++j)
        	{
        		if(threadgroup[j].size() > 0)
        		{
        			Date startTime = new Date(scheduledExecutionTime() +  sg.getTime(i)  * 1000L);
        			ScheduleExecution se = new ScheduleExecution(threadgroup[j]);
        			if(startTime.before(new Date(System.currentTimeMillis() + 1000L)))
        			{
								L.debug("Too late, start immediately.");
        				tm.schedule(se, 0);
        			}else
        			{
									L.debug("start on time.");
								  job.setTime(startTime);
									msgCal("start time:",job,job);
									tm.schedule(se, job.getTime());
        			}
        		}
        	}
        }

        
        L.debug("Schedule Arrangement is the end.");
      
    }
    catch (Exception ex) {
      L.debug("Exception in Schedule", ex);
    }
  }

  protected Vector getScheduleJob() {
    // fetch current schedule job.
    // from scheduledExecutionTime() to scheduledExecutionTime()+ Environment.schedulerInterval * 60 * 1000
    Vector idV = null;
    Vector timeV = null;
    Vector v = new Vector();
    Vector ret = new Vector(3);
    RQScheduleAPI scheduleAPI = new RQScheduleAPI();
    long milsec = scheduledExecutionTime();
    L.debug("scheduledExecutionTime1:" + milsec);
    Date scheduledExTime = new Date(milsec);
    try
    {
    	
    	scheduleAPI.getScheduling(scheduledExTime,(long)env.schedulerInterval * 60L *  1000L, ret);
    	// GetSchedule 특정 시간 동안에 실행될 스케줄 가져오기
    }
    catch(Exception ex)
    {
    	L.error(ex);
		v.add(new Vector(1)); // schedule id vector
		v.add(new Vector(2)); // schedule time vector
		scheduleAPI = null;
		return v;
    }
    idV = (Vector)ret.get(1);
    timeV = (Vector)ret.get(2);
    v.add(idV); // schedule id vector
    v.add(timeV); // schedule time vector

    idV = null;
    timeV = null;
//	scheduleAPI = null;
    return v;
  }
  private void msgCal(String s, Calendar date, Calendar time)
  {
  	System.out.println(s + ":" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)+ "-" + date.get(Calendar.DAY_OF_MONTH) +" " +time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" +time.get(Calendar.SECOND));
		L.debug(s + ":" + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1)+ "-" + date.get(Calendar.DAY_OF_MONTH) +" " +time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" +time.get(Calendar.SECOND));
		return;
  }
}
