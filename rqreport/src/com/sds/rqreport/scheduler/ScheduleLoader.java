package com.sds.rqreport.scheduler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.sds.rqreport.*;
import org.apache.log4j.*;

public class ScheduleLoader {
  static {
    NDC.push("Scheduler");
  }
  private static Timer mainTimer;
  private static Logger L = Logger.getLogger("SCHEDULER");

  static {
    MDC.put("user_id", "admin");
  //  MDC.put("user_ip", (String)Environment.repositoryServer_NameArray.get(0));
    MDC.put("conn_id", "schedule");
  }

  public static void main(String[] args) {
    try {
      if(args.length > 0)
      {
    	  SchedulerEnv env = SchedulerEnv.getInstance(args[0]);
      }
      start();
    }
    catch (Exception ex) {
    }
  }

  public static void start()
      throws Exception {
	SchedulerEnv env = SchedulerEnv.getInstance();
    if (mainTimer == null)
      mainTimer = new Timer(false);
    mainTimer.schedule(new ScheduleArrangement(), 0, env.schedulerInterval * 60 * 1000);
    L.info("Scheduler Start at" + new Date());
  }

  public static void restart()
      throws Exception {
    L.info("Scheduler Restarting at" + new Date());
    stop();
    start();
  }

  public static void stop()
      throws Exception {
    if (mainTimer == null)
      return;
    L.info("Scheduler Thread Stoped at" + new Date());
    mainTimer.cancel();
    mainTimer = null;
  }
}