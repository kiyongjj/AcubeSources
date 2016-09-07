package com.sds.rqreport.scheduler;
import java.util.*;
import java.text.*;

public class ScheduleMananger {

    //TimeZone을 한국(KST)로 설정한다.
	public static void main(String[] args) {	
    final int millisPerHout = 60 * 60 * 1000;
    SimpleTimeZone timeZone = new SimpleTimeZone(9 * millisPerHout,"KST");
   
    long lTime1 = 0;
    long lTime2 = 0;
    //long lMtime1 = 0;
    //long lMtime2 = 0;
   
    String strTime = "";
    //String strOldDay = "";
    //String strNewDay = "";
   
    Date ntime;
    SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat dfTime = new SimpleDateFormat("HH");
    SimpleDateFormat dfDir = new SimpleDateFormat("yyyyMMdd");
    dfDate.setTimeZone(timeZone);
    dfTime.setTimeZone (timeZone);
    dfDir.setTimeZone(timeZone);
   
    //String BACKUP_DIR = "/data01/BackupDaily";
    //String SRC_DIR = "/var/log";
   
    //lMtime1 = 10000L; //10 second
    //lMtime2 = 20000L; //20 second
   
    try{
        if(args.length < 2){
            System.out.println("----------------------------------------------------");
            System.out.println("Set parameter to execute backup");
            System.out.println("Parameter is exection time and interval");
            System.out.println("");
            System.out.println("java SystemBackup time interval");
            System.out.println("");
            System.out.println(" 1) java SystemBackup 10 d");
            System.out.println (" 2) java SystemBackup 22 w");
            System.out.println(" 3) java SystemBackup 10 m");
            System.out.println("----------------------------------------------------");
        }
        else{
            System.out.println("----------------------------------------------------");
            System.out.println("System Backup Start Up!!");
            System.out.println("");
           
            //3600
            if(args[1].equals("d")){
                lTime1 = 3600000L; //1hour
                lTime2 =   600000L; //10min
            }else if(args[1].equals("w")){
                lTime1 = 3600000*12; //12hour
                lTime2 =   600000L;
            }else if(args[1].equals("m")){
                lTime1 = 3600000*30; //30hour
                lTime2 =   600000L;
                                   
            }
           
            //
            while (true) {
                ntime = new Date();
                strTime = String.valueOf(dfTime.format(ntime));
                //debug
                System.out.println("current time is : " + strTime);
               
                //first arguemnt is 24hour format time
                if(strTime.equals(args[0])){
	    //
                    Runtime.getRuntime().exec("/data01/backup_script_01.sh");
                    System.out.println("----------------------------------------------------");
                    System.out.println("Batch backup successed!!");
                    System.out.println("Batch Date ; " + String.valueOf(dfDate.format(ntime)));
                    System.out.println("----------------------------------------------------");
                    //원하는 시간이면 백업을 수행하고 일/주/월 단위로 정해진 시간을 sleep한다.
                    Thread.sleep(lTime1);
                }else{
	    //
                    System.out.println("sleep start");
                    //System.out.println(lMtime1);
                    //10분간격으로 시간 체크
                    Thread.sleep(lTime2);
                    //Thread.sleep(lMtime1);
                    System.out.println("sleep end");
                }
            }
           
        }
    }
    catch (Exception err){
        System.out.println("Excepitn Error :[" + err + "]");
    }
    finally{
        System.out.println ("shutdown~~");
        System.out.println("----------------------------------------------------");
    }
   
}

}
