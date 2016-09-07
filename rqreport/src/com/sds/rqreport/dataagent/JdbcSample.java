package com.sds.rqreport.dataagent;

import java.sql.*;
import java.io.*;
import java.util.*;

public class JdbcSample {

	public String sql = null;
	public Connection conn = null;
	PreparedStatement stmt = null;
	StringBuffer jsBuffer = null;
	
	public JdbcSample()
	{
		sql = "SELECT 사원.주민번호, 주문.주문번호, 제품.제품번호, 고객.상호명 FROM 사원, 주문, 제품, 고객 WHERE  사원.주민번호 = 주문.사원주민번호 AND  주문.고객번호 = 고객.고객번호 and ROWNUM <= 9000";
	}
	
	public boolean Connect()
	{
		boolean bConnected = false;
		try
		{
			String classpath = System.getProperty("java.class.path",".");
//			System.out.println(classpath);
			long starttime = System.currentTimeMillis();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@dbserver2:1521:worldav";
			conn = DriverManager.getConnection(url,"easybase","rqdev1");
			System.out.println("DB Connection Time : " + (System.currentTimeMillis() - starttime));
			
			bConnected = true;
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		return bConnected;		
	}
	
	public boolean FetchSql()
	{
		boolean bFetch = false;
		try
		{
		stmt = conn.prepareStatement(sql);
		bFetch = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return bFetch;
	}
	
	public String WriteStringDataSet()
	{
		long startTime = System.currentTimeMillis();
		ResultSet rs1 = null;
		String fileName = null;
		try{
			rs1 = stmt.executeQuery();
			System.out.println("String Time executeQuery elapsed : " + (System.currentTimeMillis() - startTime));
			
			fileName = "C:/temp/test/";
			Random ran = new Random();
			fileName = fileName + ran.nextInt(20000) + ".txt";
			
			File f = new File(fileName);
			String strContent = "Write String DataSet - fetch - fileWrite - fileRead - download\r\n\r\n\r\n";
			FileOutputStream fo = null;
	
			try
			{
				startTime = System.currentTimeMillis();
				if ( f.createNewFile() )
				{
					fo = new FileOutputStream(f);					
					fo.write(strContent.getBytes());
					while(rs1.next())
					{
						strContent = rs1.getString ("주민번호") + " " + rs1.getString("주문번호") + " " + rs1.getString("제품번호") + " " + rs1.getString("상호명") + "\r\n";
						fo.write(strContent.getBytes());
					}
					long filespan = (System.currentTimeMillis() - startTime);
					strContent = "Write String Time : " + filespan;
					fo.write(strContent.getBytes());
					System.out.println(strContent);			
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if ( fo != null)
					fo.close();
				f = null;
				rs1.close();
				stmt.close();
				conn.close();
				ran = null;
				strContent = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return fileName;
	}
	
	public String WriteStringBuffDataSet()
	{
		long startTime = System.currentTimeMillis();
		ResultSet rs1 = null;
		String fileName = null;
		try{
			rs1 = stmt.executeQuery();
			System.out.println("StringBuffer Time executeQuery elapsed : " + (System.currentTimeMillis() - startTime));
		
			fileName = "C:/temp/test/";
			Random ran = new Random();
			fileName = fileName + ran.nextInt(50000) + ".txt";
		
			File f = new File(fileName);
			String strContent = "";
			StringBuffer sf = new StringBuffer("Write StringBuffer DataSet - fetch - fileWrite - fileRead - download\r\n\r\n\r\n");
			FileOutputStream fo = null;

			try
			{
				startTime = System.currentTimeMillis();
				fo = new FileOutputStream(f);
					
				while(rs1.next())
				{
					strContent = rs1.getString ("주민번호") + " " + rs1.getString("주문번호") + " " + rs1.getString("제품번호") + " " + rs1.getString("상호명") + "\r\n";;
					sf.append(strContent);
				}
				f.createNewFile();
				fo.write(sf.toString().getBytes());
				long filespan = (System.currentTimeMillis() - startTime);
				strContent = "Write StringBuffer Time : " + filespan;
				fo.write(strContent.getBytes());

				System.out.println(strContent);			

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if ( fo != null)
					fo.close();
				f = null;
				rs1.close();
				stmt.close();
				conn.close();
				ran = null;
				strContent = null;
				sf = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		return fileName;
	}

	public byte[] WriteMemoryDataSet()
	{
		byte barray[] = null;
		long startTime = System.currentTimeMillis();
		ResultSet rs1 = null;
		try{
			rs1 = stmt.executeQuery();
			System.out.println("Memory Time executeQuery elapsed : " + (System.currentTimeMillis() - startTime));
	
			
			String strContent = "";
			jsBuffer = new StringBuffer("Write Memory DataSet - fetch - download\r\n\r\n\r\n");

			try
			{
				startTime = System.currentTimeMillis();
				
				while(rs1.next())
				{
					strContent = rs1.getString ("주민번호") + " " + rs1.getString("주문번호") + " " + rs1.getString("제품번호") + " " + rs1.getString("상호명") + "\r\n";;
					jsBuffer.append(strContent);
				}
				long filespan = (System.currentTimeMillis() - startTime);
				strContent = "Memory Time : " + filespan;
				jsBuffer.append(strContent);
				barray = jsBuffer.toString().getBytes();

				System.out.println(strContent);				

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				rs1.close();
				stmt.close();
				conn.close();
				strContent = null;
				jsBuffer = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return barray;

	}
	
	public byte[] ReadFile(String fileName)
	{
		FileInputStream fi = null;
		byte barray[] = null;
		File f = null;
		try
		{
			f = new File(fileName);
			int size = (int)f.length();
			barray = new byte[size];
			fi = new FileInputStream(f);
			int len = 0, totalReceived = 0;
			while (totalReceived < size) {
				len = fi.read(barray, totalReceived, size - totalReceived);
				totalReceived += len;
		  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
		
		
		return barray;
	}
	
	
	/**
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param string
	 */
	public void setSql(String string) {
		sql = string;
	}

}
