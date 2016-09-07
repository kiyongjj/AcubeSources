package com.sds.rqreport.util;

public final class Encipher_Key extends Create_Key
{
	public Encipher_Key(String key)
	{
		super (key);
	}

	public Encipher_Key(Create_Key Ck)
	{
		super (Ck);
	}

	void Round_Register()
	{
		String temp = "";
		
		temp = Cn.substring(0, dt.L_round[count]);
		Cn = Cn.substring(dt.L_round[count]);
		Cn = Cn + temp;
		
		temp = Dn.substring(0, dt.L_round[count]);
		Dn = Dn.substring(dt.L_round[count]);
		Dn = Dn + temp;
		
		if (count < 15) count++;
		else count = 0;
	}
}