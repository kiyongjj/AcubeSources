package com.sds.rqreport.dataagent;

import java.util.*;

import java.sql.SQLException;

import com.sap.mw.jco.JCO;

import com.sds.rqreport.common.RQDispatch;
import com.sds.rqreport.service.*;

public class DataAgent implements RQDispatch {

	RQcommController jdbc;
	JCOController	jco;
	public DataAgent() {
		super();
		jdbc = new RQcommController();
		jco = new JCOController();

	}

	public int callByDispatch(int functionID, List argarray, List ret) {
		int res = 0;
		int id;
		ArrayList ar= null;
		String result;
		int resInt[] = new int[1];
		Integer[] cstmtIdx = new Integer[1];
		try
		{
			//functionID 1001부터는 SAP JCO를 위한 함수
			switch (functionID)
			{
				case 0:
					res = 0;
					break;
				case 1:
					ret.add(GetDS((String)argarray.get(0)));
					break;

				// DB Connect check and 1 connection save.
				case 2:
					boolean[] schema = new boolean[1];
					id = jdbc.makeConnection((String)argarray.get(0),(String)argarray.get(1),(String)argarray.get(2),(String)argarray.get(3),schema);
					ret.add(new Integer(id));
					if(schema[0])
						ret.add(new Integer(1));
					else
						ret.add(new Integer(0));

					break;
				case 3:
					res = jdbc.close( ((Integer)argarray.get(0)).intValue());
					break;

				// get schema list or table list.
				case 4:
					ar = jdbc.getTables( ((Integer)argarray.get(0)).intValue(),(String)argarray.get(1), (String)argarray.get(2) );
					ret.add(ar);
					res = 0;
					break;
				case 5:
					ar = jdbc.getColumns( ((Integer)argarray.get(0)).intValue(),(String)argarray.get(1), (String)argarray.get(2) );
					ret.add(ar);
					res = 0;
					break;
				case 6:
					id = jdbc.queryPrepare( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1) );
					ret.add(new Integer(id));
					res = 0;
					break;
				case 7:
					if(!jdbc.queryExecute(((Integer)argarray.get(0)).intValue()))
					{
						res = -101;
					}
					else
					{
						res = 0;
					}
					break;
				case 8:
					result = jdbc.getFetch(((Integer)argarray.get(0)).intValue());
					ret.add(result);
					res = 0;
					break;
				case 9:
					res = jdbc.endFetch( ((Integer)argarray.get(0)).intValue());
					break;
				case 10:
					ar = jdbc.getSQLColumns(((Integer)argarray.get(0)).intValue(),(String)argarray.get(1) );
					ret.add(ar);
					res = 0;
					break;
				case 11:
					System.exit(-1);
					break;
				case 12:
					ar = jdbc.getQueryExecuteDirect( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1),  ((Integer)argarray.get(2)).intValue() == 1 , resInt);
					ret.add(new Integer(resInt[0]));
					ret.add(ar);
					res = 0;
					break;

				// get Function List
				case 13:
					ar = jdbc.getFuncList( ((Integer)argarray.get(0)).intValue(), ((Integer)argarray.get(1)).intValue() );
					ret.add(ar);
					res = 0;
					break;

				// get Procedure List
				case 14:
					ar = jdbc.getProcedureList( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1) );
					ret.add(ar);
					res = 0;
					break;
				case 15:
					ar = jdbc.getProcedureParams( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3) );
					ret.add(ar);
					res = 0;
					break;
				case 16:
					ar = jdbc.getCursorParams( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3),
											   ((Integer)argarray.get(4)).intValue(), (String)argarray.get(5), ((Integer)argarray.get(6)).intValue() );
					ret.add(ar);
					res = 0;
					break;
				case 17:
					ar = jdbc.executeProcedure( ((Integer)argarray.get(0)).intValue(), cstmtIdx, (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3),
													((Integer)argarray.get(4)).intValue(), (String)argarray.get(5), (String)argarray.get(6), resInt );
					ret.add( cstmtIdx[0] );
					ret.add(new Integer(resInt[0]));
					ret.add( ar );
					res = 0;
					break;
				case 18:
					ar = jdbc.fetchCursorProc( ((Integer)argarray.get(0)).intValue(), ((Integer)argarray.get(1)).intValue(), resInt );
					ret.add(new Integer(resInt[0]));
					ret.add(ar);
					res = 0;
					break;
				case 19:
					cstmtIdx[0] = ((Integer)argarray.get(5));
					result = jdbc.decodeExecute( ((Integer)argarray.get(0)).intValue(), cstmtIdx, (String)argarray.get(1),
												 (String)argarray.get(2), (String)argarray.get(3), (String)argarray.get(4) );
					ret.add( cstmtIdx[0] );
					ret.add( result );
					res = 0;
					break;
				case 20:
					res = jdbc.bindParameter( ((Integer)argarray.get(0)).intValue(), ((Integer)argarray.get(1)).intValue(), (String)argarray.get(2) );
				case 21:
					result = jdbc.executeQueryString( (String)argarray.get(0), (String)argarray.get(1) );
					ret.add(result);
					res = 0;
					break;
				case 1001:
					id = jco.connect((String)argarray.get(0),(String)argarray.get(1),(String)argarray.get(2),(String)argarray.get(3), (String)argarray.get(4), (String)argarray.get(5));
					ret.add(new Integer(id));

					break;
				case 1002:
					jco.disconnect( ((Integer)argarray.get(0)).intValue() );
					break;
				case 1003:
					ar = jco.rfcFunctionSearch(((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3));
					ret.add(ar);
					res = 0;
					break;
				case 1004:
					ar = jco.rfcGetFunctionInterface(((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2));
					ret.add(ar);
					res = 0;
					break;
				case 1005:
					ar = jco.rfcExecuteFunction(((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3));
					ret.add(ar);
					res = 0;
					break;
				case 1006:
					ar = jco.rfcGetFieldInfo(((Integer)argarray.get(0)).intValue(), (String)argarray.get(1), (String)argarray.get(2), (String)argarray.get(3), (String)argarray.get(4), (String)argarray.get(5));
					ret.add(ar);
					res = 0;
					break;
				case 1007:
					ar = jco.rfcGetFunctionFieldInfo( ((Integer)argarray.get(0)).intValue(), (String)argarray.get(1));
					ret.add(ar);
					res = 0;
					break;
				case 1008:
					result = jco.rfcExecuteFunction2( (String)argarray.get(0), (String)argarray.get(1) );
					ret.add(result);
					res = 0;
					break;
			}
		}catch(SQLException e)
		{
			res = -100;
			e.printStackTrace();
			ret.add(e.toString());
		}
/*
		catch(JCO.AbapException ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			ret.add(ex.getMessage());
		}
*/

		catch (JCO.Exception ex)
		{
			res = -100;
			ex.printStackTrace();
			ret.add(ex.toString());

		}


		catch(Exception ex)
		{
			res = -100;
			ex.printStackTrace();
			ret.add("Application Error");
		}
		return res;
	}

	String GetDS(String sql)
	{
		System.out.println(sql);
		JdbcSample jdbc = new JdbcSample();
		jdbc.setSql(sql);
		if(!jdbc.Connect())
		 return "Error";
		if(!jdbc.FetchSql())
		 return "Error!";
		try
		{

		String temp = new String ( jdbc.WriteMemoryDataSet() ) ;
		return temp;
		}catch(Exception ex)
		{
			return 	ex.toString();

		}

	}
}
