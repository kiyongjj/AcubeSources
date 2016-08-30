package com.sds.acube.ndisc.common.exception;

public class DaoException extends Exception {
   static final long serialVersionUID = -2325822038990805631L;
    
   private int iExceptionCode;

   private String strExceptionMsg;

   public Throwable cause;

   public DaoException() {
      super();
   }

   public DaoException(String strMessage) {
      super(strMessage);
   }

   public DaoException(Throwable ex) {
      // Call base class constructor
      super(ex.getMessage());
      cause = ex;
   }
   
   public DaoException(String strMessage, Throwable ex) {
      super(strMessage);
      cause = ex;
   }

   public DaoException(String strMessage, int iErrCode) {
      super(strMessage);
      this.iExceptionCode = iErrCode;
      this.strExceptionMsg = strMessage;
   }

   public DaoException(int iErrCode) {
      super();
      this.iExceptionCode = iErrCode;
   }

   public String getMessage() {
      if (cause == null)
         return super.getMessage();
      else
         return super.getMessage() + "; nested exception is: \n\t" + cause.toString();
   }

   public int getExceptionCode() {
      return this.iExceptionCode;
   }

   public String getExceptionMsg() {
      return this.strExceptionMsg;
   }

   public void printStackTrace(java.io.PrintStream ps) {
      if (cause == null) {
         super.printStackTrace(ps);
      } else {
         synchronized (ps) {
            ps.println(this);
            cause.printStackTrace(ps);
         }
      }
   }

   public void printStackTrace() {
      printStackTrace(System.err);
   }

   public void printStackTrace(java.io.PrintWriter pw) {
      if (cause == null) {
         super.printStackTrace(pw);
      } else {
         synchronized (pw) {
            pw.println(this);
            cause.printStackTrace(pw);
         }
      }
   }

   public Throwable getCause() {
      return cause;
   }

   public void setCause(Throwable ex) {
      cause = ex;
   }
   
   
}