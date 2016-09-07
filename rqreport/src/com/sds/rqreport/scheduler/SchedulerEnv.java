package com.sds.rqreport.scheduler;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import com.sds.rqreport.Environment;
import com.sds.rqreport.util.PropertyLoader;

public class SchedulerEnv {
	private final static String PROPERTIES_NAME = "rqreport.properties";
	private static SchedulerEnv env = new SchedulerEnv();

	private SchedulerEnv() {

	}

	public static SchedulerEnv getInstance()
	{
		if( env.pl == null)
		{
			env.load(null);
		}

		return env;
	}

	public static SchedulerEnv getInstance(String configfile)
	{
		env.load(configfile);
		return env;
	}

	private PropertyLoader pl = null;
	public String scheduleTimeTableName = "RQScheduleTime";
	public String scheduleRunInfoTableName = "RQScheduleRunInfo";
	public String scheduleRunInfoStatusTableName = "RQScheduleDocStatus";
	public String scheduleNotifictionTableName = "RQNotification";
	public String scheduleDocStatus = "RQScheduleDocStatus";
	//public String repositoryJdbcDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
	public String scheduleJdbcDriver = "oracle.jdbc.driver.OracleDriver";
	//public String repositoryJdbcConnection = "jdbc:odbc:schedulerdb";
	public String scheduleJdbcConnection = "jdbc:oracle:thin:rqadmin_sun/easybase@70.7.101.214:1521:WORLDAV";
	public int schedulerInterval = 10;
	public int scheduler_thread_group = 5;
	public int scheduler_groupinterval = 5;
	public String timeFormat = "#%Y-%M-%D %H:%m:%S#";// "to_date('%Y %M %D %H %m %S','YYYY MM DD HH24 MI SS')";
	public String timeFormat2 = "%Y-%M-%D %H:%m:%S";// "to_date('%Y %M %D %H %m %S','YYYY MM DD HH24 MI SS')";
	public String timeFormat3 = "%Y/%M/%D %H:%m:%S";// "to_date('%Y %M %D %H %m %S','YYYY MM DD HH24 MI SS')";
	public String smtpServer = "";
	public String mailAuthID = "";
	public String mailAuthPw = "";
	public String mailAuthentification = "";
	public String schedulerMailSender = "";
	public String envMime = "UTF-8";
	public String indexFile = "";
	public String server = "http://127.0.0.1:8080/rqreport/RQDataset.jsp";
	public String serverRepository = "http://127.0.0.1:8080/rqreport/document/docapi.jsp";
	public String schedulerDocDir = "c:/scheduleddoc";
	public String jndiName = "";

	public String pcexelocalip = "localhost"; //pdf, hwp, xls, gul, rtf 변환을 위한 local ip(PC실행)
	public String rqsep = "`";
	public String secondarypcexeip = "";
	public String ftpinfo = "";
	
	public void load(String configFilearg)
	{
		String configfile = configFilearg;
		if(configfile == null || configfile.length() < 1)
		{
			configfile = System.getProperty(PROPERTIES_NAME);
			if (configfile == null) {

			    // 여기는 시스템 프라퍼티에 값이 설정되어 있지 않을 경우
			    // CLASSPATH에 지정된 디렉토리에서 값을 읽는다.
				URL url = SchedulerEnv.class.getClassLoader().getResource(PROPERTIES_NAME);
				if(url != null)
				{
					try {
						configfile = URLDecoder.decode(url.getFile(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						//e.printStackTrace();
						configfile = url.getFile();
					}
				}
		    }
		}

		if(configfile == null || configfile.length() < 1)
		{
			return;
		}

		//System.out.println("Property File Path:" + configfile);
		pl = new PropertyLoader(configfile);
		scheduleTimeTableName = pl.getString("rqreport.scheduler.timetable","RQScheduleTime");
		scheduleRunInfoTableName = pl.getString("rqreport.scheduler.runinfo","RQScheduleRunInfo");
		scheduleRunInfoStatusTableName = pl.getString("rqreport.scheduler.runinfostatus","RQScheduleDocStatus");
		scheduleNotifictionTableName = pl.getString("rqreport.scheduler.notification","RQNotification");
		scheduleDocStatus = pl.getString("rqreport.scheduler.scheduledocstatus","RQScheduleDocStatus");
		scheduleJdbcDriver = pl.getString("rqreport.scheduler.jdbcdriver","sun.jdbc.odbc.JdbcOdbcDriver");
		scheduleJdbcConnection = pl.getString("rqreport.scheduler.jdbcconnstr","jdbc:odbc:schedulerdb");
		schedulerInterval = pl.getInt("rqreport.scheduler.interval",5);
		scheduler_thread_group = pl.getInt("rqreport.scheduler.threadgroup",5);
		scheduler_groupinterval = pl.getInt("rqreport.scheduler.groupinterval",5);
		timeFormat = pl.getString("rqreport.scheduler.timeformat","#%Y-%M-%D %H:%m:%S#");
		server = pl.getString("rqreport.scheduler.server","http://127.0.0.1:8080/rqreport/RQDataset.jsp");
		serverRepository = pl.getString("rqreport.scheduler.repository","http://127.0.0.1:8080/rqreport/document/docapi.jsp");
		schedulerDocDir = pl.getString("rqreport.scheduler.rootdir","c:/scheduleddoc");
		smtpServer = pl.getString("rqreport.mailer.smtp", "");
		mailAuthID = pl.getString("rqreport.mailer.smtpid", "");
		mailAuthPw = pl.getString("rqreport.mailer.smtppw", "");
		mailAuthentification = pl.getString("rqreport.mailer.auth", "");
		schedulerMailSender = pl.getString("rqreport.mailer.sender", "");
		envMime = pl.getString("rqreport.mailer.envmime", "UTF-8");
		indexFile = pl.getString("rqreport.mailer.indexfile", "");

		pcexelocalip = pl.getString("rqreport.scheduler.pcexe.localip", "localhost");
		secondarypcexeip = pl.getString("rqreport.scheduler.pcexe.secondary.localip", "notavailable");
		ftpinfo = pl.getString("rqreport.scheduler.ftpinfo","");
		
		rqsep = pl.getString("rqreport.server.pcexe.rqsep", "`");


	}
}
