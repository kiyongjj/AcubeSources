package com.sds.rqreport.service.session;

import java.util.*;

import com.sds.rqreport.common.*;

public class RQSessionManager implements RQDispatch {

	/* (non-Javadoc)
	 * @see com.sds.reqube.service.admin.RQDispatch#callByDispatch(int, java.util.Vector, java.util.Vector)
	 */
	static int sid = 0;
	protected	int count = 0;
	protected Hashtable sessionRep = null;
	RQSessionManager()
	{
		sessionRep = new Hashtable(100);
	}
	
	public int callByDispatch(int functionID, List argarray, List ret) {
		
		
		int code = -100, timeout = 0;
		String temp;
		String val,val2,val3;
		String[] retval = new String[1];
		switch(functionID)
		{
			
			case 5:
				//GetSessionCout
				count = sessionRep.size();
				System.out.println("session count:" + count);
				ret.add("" + count);
				code = 0;
			break;
			case 6:
				// Terminate session
				code = -9999;
			break;			
			case 1:
				//CreateSession
				if(argarray.size()>0)
				{
					temp = (String)argarray.get(0);
					timeout = Integer.parseInt(temp.trim());
				}
				else
				{
					timeout = 600000;
				}
				val = createSession(timeout);
				if(val == null)
					return -1;
				ret.add(val);
				code = 0;
				break;
			case 2:
				//CloseSession
				temp = (String) argarray.get(0);
				if(closeSession(temp))
				{
					code = 0;
				}
				else
				{
					code = -1;
				}
				break;
			case 3:
				//GetValues
				temp = (String) argarray.get(0);
				
				code = getValues(temp, retval);
				if(retval[0] == null || retval[0].length() < 1)
					return -1;
				ret.add(retval[0]);
				break;
			case 4:
				//SetValue
				if(argarray.size() > 2)
				{
					val = (String)argarray.get(0);
					val2 = (String)argarray.get(1);
					val3 = (String)argarray.get(2);
				}
				else
					return -101;
				code = setValue(val, val2, val3);
				break;


		}		
		
		return code;
	}
	public int getSize()
	{
		return sessionRep.size();
	}
	public static synchronized String generateID()
	{
		
		Random rd = new Random(System.currentTimeMillis());
		String ret = "RQSession" + sid + Long.toHexString(rd.nextLong())+ Long.toHexString(System.currentTimeMillis());
		if(++sid >= 9999)
			sid = 0;
		return ret;
	}
	
	public int setValue(String strID, String strKey,String strValue)
	{
		RQSession item = null;
		item = find(strID);
		if(item == null)
		{
			return -1;
		}
		item.setValue(strKey, strValue);
		return 0;
	}
	
	public int getValues(String strID, String[] val)
	{
		RQSession item = null;
		item = find(strID);
		if(item == null)
		{
			val[0] = null;
			return -1;
		}
		int res = item.getValues(val);
		return res;
	}

	public int checkAll()
	{
		Enumeration e = sessionRep.elements();
		
		while(e.hasMoreElements())
		{
			RQSession session = (RQSession)e.nextElement();
			if(!session.checkSession())
			{
				sessionRep.remove(session.getID());
				session = null;
			}
		}
		return 0;
	}
	
	boolean closeSession(String strID)
	{
		return deleteItem(strID);
		
	}
	
	boolean deleteItem(String strKey)
	{
		return sessionRep.remove(strKey) != null;
	}
	
	RQSession find(String strID)
	{
		RQSession item;
		if( strID != null && strID.length() < 1)
			return null;
		return (RQSession)sessionRep.get(strID);	
	}
	
	public boolean addItem(RQSession rqsession)
	{
		if(rqsession == null)
		  return false;
		
		//..	Hash Table에 존재할 경우 바로 빠져 나온다.
		if ( find(rqsession.getID()) != null )
			return false;		  
		sessionRep.put(rqsession.getID(), rqsession);
		return true;
	}

	String createSession(long timeout)
	{
		String id = generateID();
		long timeoutval;
		if(timeout < 36000)
		{
			timeoutval = 36000;
		}
		else
			timeoutval = timeout;
			
		RQSession item = null;
		item = new RQSession(id,timeoutval);
		if(addItem(item))
		{
			return id;
		}
		else
		{
			item = null;
			return null;
		}			
	}

	
}
