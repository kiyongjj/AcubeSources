package com.sds.rqreport.scheduler;

import com.sds.rqreport.common.RQInfo;
import java.util.*;
import java.sql.*;
import java.io.*;
import com.sds.rqreport.*;
import org.apache.log4j.*;

public class MailInfo
    implements RQInfo, Serializable {
  public String maillingList = "";
  public String mineType = "text/plain";
  public String subject = "Please!!";
  public String message = "test";
  public String attachFileName;
  public String[] recipients;
  public String[] attachFiles;
  public Vector userAttachFiles;
  public Vector recipientList;
  private static Logger L = Logger.getLogger("SCHEDULER");
  public MailInfo() {
// 사용자 정의만 구현
	 //makeMailForm("mail.txt");
   recipients = new String[0];
   attachFiles = new String[0]; 
  }
  
  public MailInfo(String filename)
  {

		makeMailForm(filename);
		recipients = new String[0];
		attachFiles = new String[0];  	
  }
  
  public short makeMailForm(String filename)
  {
		InputStream in = null;
		File f;
		byte[] data = null;
		int length;
		try {
		  f = new File(/*Environment.templatePath*/ ""
					   + /*System.getProperty("file.seperator")*/File.separator  + filename);
		  length = (int) f.length();
		  in = new BufferedInputStream(new FileInputStream(f));
		  data = new byte[length];
		  int len = 0;
		  len = in.read(data, 0, length);
		  if (len < length) {
			L.warn("Result length less then file size.");
		  }
		  in.close();
		  in = null;
		  String mail = new String(data, "UTF-8");//"Environment.envMime);
		  /*
		   * 제목부 입니다.[첨부파일명 입니다. 확장자는 붙이지 않습니다.]
		   * plain:
		   * 여기서부터는 본문입니다.
		   */
		
		  // 제목부에 대치할 사용자지정 첨부파일명을 넣어준다. [첨부파일명]
		  int pos1 = mail.indexOf('[', 2);
		  int pos2 = mail.indexOf(']', pos1);
		  subject = mail.substring(0, pos1);
		  attachFileName = mail.substring(pos1 + 1, pos2) + ".rqr";
		  StringBuffer ssbuf = new StringBuffer(mail);
		  ssbuf.delete(0, ssbuf.toString().indexOf("\n") + 1);
		  boolean bHtml;
		  if (ssbuf.toString().toLowerCase().indexOf("html") >= 0)
		  {
			mineType = "text/html";
			bHtml = true;
		  }
		  else
		  {
			mineType = "text/plain";
			bHtml = false;
		  }
		  ssbuf.delete(0, ssbuf.toString().indexOf("\n") + 1);
		  message = ssbuf.toString();
		}
		catch (Exception ex1) {
			return -1;
		
		}
		return 0;
  }
  
  public short setMaillingList(String[] maillist)
  {
    int size=0;
    if(maillist != null)
      size = maillist.length;
    recipients = new String[size];
    for (int i = 0; i < size; ++i)
    {
      recipients[i] = maillist[i];
    }
    return 0;
  }
  
  public short setMaillingList(ResultSet rs) throws SQLException
  {
    while(rs.next())
    {
      setTo(rs.getString(2));
    //  setFile(rs.getString(3));
    }
    if(recipientList != null)
    {
      int size = recipientList.size();
      recipients = new String[size];
    //  attachFiles = new String[size];
      for (int i = 0 ; i < size; ++i)
      {
        recipients[i] = (String)recipientList.get(i);
    //    attachFiles[i] = (String)userAttachFiles.get(i);
      }
    }
    return 0;

  }

  public void setTo(String email) {
  if (recipientList == null) {
    recipientList = new Vector();
    recipients = new String[1];
  }
  recipientList.add(email);
  recipients[0] = email;
}

  public void setFile(String file) {
  if (userAttachFiles == null) {
    userAttachFiles = new Vector();
    attachFiles = new String[1];
  }
  userAttachFiles.add(file);
  attachFiles[0] = file;
  }


  public String getParamTypes() {
    return "SSSSSS";
  }

  public int getParamInt(int index) {

    return 0;
  }

  public float getParamFloat(int index) {

    return 0F;
  }

  public String getParamString(int index) {
  	String ret = "";

    return "";
  }

  public Calendar getParamCalendar(int index) {

    return null;
  }

}
