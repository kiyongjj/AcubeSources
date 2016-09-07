package com.sds.rqreport.util;

public final class Decipher_Key extends Create_Key
{
	
	public  Decipher_Key(Create_Key Ck)
	{
		super(Ck);
	}	
	
	public  Decipher_Key(String key)
	{
		super(key);
	}

	void Round_Register()
	{
		String temp = "";
		
		temp = Cn.substring(0, 28-dt.R_round[count]);
		Cn = Cn.substring(28-dt.R_round[count]);
		Cn = Cn + temp;
		
		temp = Dn.substring(0, 28-dt.R_round[count]);
		Dn = Dn.substring(28-dt.R_round[count]);
		Dn = Dn + temp;
		
		if (count < 15) count++;
		else count = 0;		
	}
}