package com.sds.rqreport.util;

public class DES3CipherKey{
	
	private String t = "";	// 잘려진 64비트의 나머지 부분.
	private int position;	// 문자열 시작 위치.
	
	public static void main(String[] args)
	{
		DES3CipherKey ckey = new DES3CipherKey();
		System.out.println(ckey.DES3Return_key("test12345abcdegOPQRSTU"));
	}
	
	public void DES3CipherKey()
	{
		position = 0;
	}
	
	public String DES3Return_key(String key)
	{
		return return_key(key) + return_key(key) + return_key( key);
	}
	
	public String return_key(String key)
	{
		String temp = "";
		String result = "";
		int A;
		
		if(t.length() != 0) result = t;
		
		for(int i=position;;i++)
		{
			A = (int)key.charAt(i);
			result += Integer.toBinaryString(A);
			
			if(key.length() <= (i+1)) i = -1;
			
			if(result.length() > 64)
			{
				temp = result.substring(0, 64);
				t = result.substring(64);
				result = temp;
				
				if(key.length() <= (i+1)) position = 0;
				else position = (i+1);
				
				break;
			}
			
			else if(result.length() == 64)
			{
				if(key.length() <= (i+1)) position = 0;
				else position = (i+1);
				t = "";
				break;
			}
		}
		return result;
	}		
}
