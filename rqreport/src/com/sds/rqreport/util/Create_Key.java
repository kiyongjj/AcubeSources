package com.sds.rqreport.util;

abstract class Create_Key
{
	protected static Des_table dt = new Des_table();
	private String key = "", Key1 = "";
	protected String Cn="", Dn="";
	private String Key_n="";
	protected int count;
	private char[] PC_1_ret;
	private char[] PC_2_ret;
	abstract void Round_Register();

	public Create_Key(String key)
	{
		PC_1_ret  = new char[56];
		PC_2_ret = new char[48];
		this.key = key;
		count = 0;
		PC_1();
		//PC_1_ret;
		Cn = new String(PC_1_ret,0, 28);
		Dn = new String(PC_1_ret,28,28);	
	}

	public Create_Key(Create_Key obj)
	{
		PC_1_ret = obj.PC_1_ret;
		PC_2_ret = obj.PC_2_ret;
		this.key = obj.key;
		this.Cn = obj.Cn;
		this.Dn = obj.Dn;
	}
	
	private void PC_1()
	{
		
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<7; j++)
			{
				PC_1_ret[i*7 + j] = key.charAt(dt.PC1[i][j]-1);
			}			
		}

	}

	private void PC_2()
	{
		String temp = "", result="";
		temp = Cn + Dn;

		for(int i=0; i<8; i++)
		{
			for(int j=0; j<6; j++)
			{
				PC_2_ret[i*6 + j] =  temp.charAt(dt.PC2[i][j]-1);
			}			
		}
//		return result;
	}

	public char[] return_Key()
	{
		Round_Register();
		PC_2();
		
		return PC_2_ret;
	}
}