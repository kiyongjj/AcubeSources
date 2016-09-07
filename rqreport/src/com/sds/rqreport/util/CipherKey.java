package com.sds.rqreport.util;

final public class CipherKey
{
	private String t = "";	// 잘려진 64비트의 나머지 부분.
	private int position;	// 문자열 시작 위치.
	
	public void CipherKey()
	{
		position = 0;
	}
	
	public String return_key(String key)
	{
		String temp = "";
		StringBuffer result = new StringBuffer(49);
		int A;
		
		if(t.length() != 0) result.append(t);
		
		for(int i=position;;i++)
		{
			A = (int)key.charAt(i);
			result.append(Integer.toBinaryString(A));
			
			if(key.length() <= (i+1)) i = -1;
			
			if(result.length() > 64)
			{
				temp = result.substring(0, 64);
				t = result.substring(64);
				result.setLength(0);
				result.append(temp);
				
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
		return result.toString();
	}		
}