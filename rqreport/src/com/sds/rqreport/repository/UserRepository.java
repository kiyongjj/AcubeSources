package com.sds.rqreport.repository;

import java.util.ArrayList;

import com.sds.rqreport.*;
import com.sds.rqreport.common.*;
import com.sds.rqreport.service.web.RQUser;
import com.sds.rqreport.util.*;

public class UserRepository {

	RQUserConnector userConnector;
	InfoConnector groupConnector;
	RepositoryEnv env = null;
	public static void main(String[] args) throws Exception
	{
		UserRepository ur = new UserRepository();
		//System.out.println(ur.verifyPassword("admin", "admin"));

	}
	public UserRepository() throws Exception
	{
		//env = RepositoryEnv.getInstance();
		env = Environment.getRepositoryEnv();
		Class c = Class.forName("com.sds.rqreport.repository.RQUserConnector");
		//userConnector = (InfoConnector)c.newInstance();
		userConnector = (RQUserConnector)c.newInstance();
		Class c2 = Class.forName("com.sds.rqreport.repository.RQGroupConnector");
		groupConnector = (InfoConnector)c2.newInstance();

	}

	public int addUser(UserInfo ui)
	{
		if(!(RequbeUtil.verifyParam(ui.id,50,"~!@#$%^&*()\\/'\"")
				//&& RequbeUtil.verifyParam(ui.email,50,"~!#$%^&*()\\/'\"")
				&& RequbeUtil.verifyParam(ui.desc,255,"~!@#$%^&*()\\/'\"")))
		{
			return -100;
		}

		int res = 0;
		RQHash hash = new RQHash();
		ui.pw = hash.calcSHA1(ui.pw);
		res = userConnector.writeInfo((RQInfo) ui);
		return res;
	}

	public int addGroup(GroupInfo gi)
	{
		groupConnector.writeInfo((RQInfo) gi);
		return 0;
	}

	public int deleteUser(String userID)
	{
		userConnector.deleteInfo(userID);
		return 0;
	}

	public int verifyPassword(String userID, String pw)
	{
		// To install
		if(env.installid != null && env.installid.length() > 0)
		{
			if(env.installid.equals(userID) && env.installpw.equals(pw))
			{
				return 0;
			}
		}

		UserInfo ui = null;
		try {
			ui = (UserInfo)userConnector.getInfo(userID);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		if(ui == null)
			return -101;
		RQHash hash = new RQHash();
		if(ui.pw.equals( hash.calcSHA1(pw))){
			RQUser.userinfo = ui;
			return 0; //
		}else{
			return -102;
		}
	}

	public int getGroups(ArrayList ret)
	{
		ret.add(groupConnector.listAll());
		return 0;
	}

	public int getUser(String userID, ArrayList ret)
	{
		try
		{
			ret.add(userConnector.getInfo(userID));
			return 0;
		}catch(Exception ex)
		{
			return -1;
		}
	}

	public int getUsers(String groupID, ArrayList ret)
	{
		try
		{
			ret.add(userConnector.getMutiInfo(groupID));
		}catch(Exception ex)
		{
			return -1;
		}
		return 0;
	}

	public int updateUser(UserInfo ui)
	{
		if(!(RequbeUtil.verifyParam(ui.id,50,"~!@#$%^&*()\\/'\"")
				&& RequbeUtil.verifyParam(ui.email,50,"~!#$%^&*()\\/'\"")
				&& RequbeUtil.verifyParam(ui.desc,255,"~!@#$%^&*()\\/'\"")))
		{
			return -100;
		}

		try
		{
			RQHash hash = new RQHash();
			ui.pw = hash.calcSHA1(ui.pw);
			userConnector.updateInfo(ui.id, (RQInfo) ui);
		}catch(Exception ex)
		{
			return -1;
		}
		return 0;
	}

	//[CDC] jia1.liu modify for [fix bug] to add res variable 2008.12.20
	public int updateUser(UserInfo ui,String changepw)
	{
		int res = -1;
		if(!(RequbeUtil.verifyParam(ui.id,50,"~!@#$%^&*()\\/'\"") 
//				&& RequbeUtil.verifyParam(ui.email,50,"~!#$%^&*()\\/'\"") 
				&& RequbeUtil.verifyParam(ui.desc,255,"~!@#$%^&*()\\/'\"")))
		{
			return -100;
		}
		
		try
		{
			RQHash hash = new RQHash();
			ui.pw = hash.calcSHA1(ui.pw);
			res = userConnector.updateInfoPw(ui.id, (RQInfo) ui);

		}catch(Exception ex)
		{
			return -1;
		}
		return res;
	}

}
