package com.sds.acube.ndisc.mts.util.timer;

public class TimeChecker {

   private static long startTime;
   private static long endTime;
   
   public static void setStartPoint() {
      startTime = System.currentTimeMillis();
   }
   
   public static String getCurrentInterval() {
      long lGap, lMin, lSec, lMills;
      
      endTime = System.currentTimeMillis();

      lGap = endTime - startTime;
      lMin   = lGap/(60*1000);
      lSec   = (lGap - (lMin*(60*1000)))/1000;
      lMills = lGap - (lSec*1000);
      
      return lMin + " min " + lSec + " sec " + lMills + " mills";
    }
}
