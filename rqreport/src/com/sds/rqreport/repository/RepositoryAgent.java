package com.sds.rqreport.repository;

import java.io.*;
import java.sql.SQLException;
import java.util.*;


import com.sds.rqreport.common.RQDispatch;

public class RepositoryAgent implements RQDispatch {

	public RepositoryAgent() {
		super();
	}

	public int callByDispatch(int functionID, List argarray, List ret) {
		int res = 0;
		int id;
		ArrayList ar= null;
		String result;
		int resInt[] = new int[1];
		int objID = 0;
		DocRepository docRep = null;
		DSRepository dsRep = null;
		UserRepository userRep = null;

		try
		{
			objID = functionID / 100;
			switch (objID)
			{
			case 1:
				docRep = new DocRepository();
				break;
			case 2:
				dsRep = new DSRepository();
				break;
			case 3:
				userRep = new UserRepository();
				break;

			}
			File f = null;
			byte[] data = null;
			switch (functionID)
			{
			case 100:
				f = docRep.getFile((String)argarray.get(0));
				if(f!= null)
				{
					data = JDBCHelper.readFile(f);
					ret.add(data);
					res = 0;
				}
				else
				{
					res = -1;
				}
				break;
			case 101:
				res = docRep.getDocInfo((String)argarray.get(0), ret);
				break;
			case 102:
			//	res = docRep.addDoc( (byte[])argarray.get(0),(String)argarray.get(1),(String)argarray.get(2),(String)argarray.get(3),(String)argarray.get(4),(String)argarray.get(5),(String)argarray.get(6));
				break;
			case 103:
				res = docRep.delDoc((String)argarray.get(0));
				break;
			case 104:
				res = docRep.makeFolder((String)argarray.get(0));
				break;
			case 105:
				if(docRep.listDoc((String)argarray.get(0)))
				{
					ret.add(docRep.getList());
				}
				else
				{
					res = -104;
				}
				break;
			case 200:
				res = dsRep.getDS((String)argarray.get(0), ret);
				break;
			case 201:
				res = dsRep.getDS((String)argarray.get(0), ret);
				break;
			}





		}catch(SQLException e)
		{
			res = -100;
			e.printStackTrace();
			ret.add(e.toString());
		}
		catch(Exception ex)
		{
			res = -100;
			ex.printStackTrace();
			ret.add("Application Error");
		}
		return res;
	}

}
