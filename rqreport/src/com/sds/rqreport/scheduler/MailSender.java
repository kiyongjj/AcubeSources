package com.sds.rqreport.scheduler;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import com.sds.rqreport.*;
import com.sds.rqreport.util.*;
import org.apache.log4j.*;

public class MailSender {
  Vector file;
  Vector mail;
 
  public MailSender() {
    file = new Vector();
    mail = new Vector();
  
  }
  public static void main(String[] args) {
	SchedulerEnv env = SchedulerEnv.getInstance();
    MailSender mailSender1 = new MailSender();
    String list;
    String indexFile = null, formFile = null;
    if (args.length > 0)
    {
    	for(int i = 0; i < args.length; ++i)
    	{
    		
    		if(args[i].startsWith("index="))
    		{
    			indexFile = args[i].substring(6);
    		}
    		else if(args[i].startsWith("form="))
    		{
    			formFile = args[i].substring(5);
    		}
    	}
    	if(indexFile != null)
    		list = mailSender1.readFile(indexFile);
    	else
    	  list = mailSender1.readFile(env.indexFile);
    }
    else
    {
      System.out.println("No Argument! use default");
      list = mailSender1.readFile(env.indexFile);
    }

    mailSender1.getMailandFileList(list);
    int filesize = mailSender1.file.size();
    for(int i = 0; i < filesize; ++i)
    {
      MailInfo mi = new MailInfo();
      if(formFile != null)
      {
      	mi.makeMailForm(formFile);
      }
      else
      {
      	System.out.println("Templete File not found");
      	//mi.makeMailForm("mail.txt");
      	return;
      }
      
      mi.setMaillingList((String[])mailSender1.mail.get(i));
      mi.setFile((String)mailSender1.file.get(i));
      ScheduleExecution se = new ScheduleExecution(1);
      try
      {
        se.postMail(mi);
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }

  }
  protected void getMailandFileList(String data)
  {
    int pos = 0;
    while (pos >= 0)
    {
      Vector ret = new Vector(1);
      pos = getString(data,"\n",pos,ret);
      if(pos > 0)
      {
      	pos++;
      }
      if (pos > -2)
      {
        String row  = (String)ret.get(0);
        if (row == null || row.length() < 1)
        {
          break;
        }
        int pos2 = 0, idx = 0;
        Vector tempMail= new Vector(10);
        while (pos2 >= 0)
        {
          Vector ret2 = new Vector(1);

          pos2 = getString(row, "\t", pos2, ret2);
          if(pos2 > 0)
          {
          	pos2++;
          }
          if(pos2 > -2)
          {

            if (idx == 0) {
              file.add(ret2.get(0));
            }
            else {
              tempMail.add(ret2.get(0));
            }
						idx++;
          }
        }
        // make maillist;
        int mailsize = tempMail.size();
        String[] maillist = new String[mailsize];
        for(int i = 0; i < mailsize; ++i)
        {
          maillist[i] = (String) tempMail.get(i);
        }
        mail.add(maillist);
      }
    }

  }

  int getString(String data, String delim, int startpos, Vector ret)
  {
    int pos = data.indexOf(delim,startpos);

    if(pos > 0)
    {
      ret.add(data.substring(startpos,pos));
      return pos;
    }
    else if(data.length() - 1 != startpos)
    {
      ret.add(data.substring(startpos));
      return pos;

    }else return -2;

  }

  protected String readFile(String path)
  {
    InputStream in = null;
    File f;
    byte[] data = null;
    int length;
    try {
      f = new File(path);
      length = (int) f.length();
      in = new BufferedInputStream(new FileInputStream(f));
      data = new byte[length];
      int len = 0;
      len = in.read(data, 0, length);
      String readList = new String(data);
      return readList;
    }
    catch (Exception e)
    {
      return null;
    }

  }
}
