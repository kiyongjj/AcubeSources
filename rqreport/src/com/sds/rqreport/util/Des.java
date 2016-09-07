package com.sds.rqreport.util;

public final class Des
{

	   
	private String Encode_Data="";
	private char[] Ln= new char[32];
	private char[] Rn= new char[32];
	private char[] Tmp= new char[32];
//	private String Key="";
	private Create_Key Ck;
	private char[][] CkMade = null;
	private static Des_table dt = new Des_table();
	private char[] IP_ret = new char[64];
	private char[] P_ret = new char[32];
	private char[] S_Box_ret = new char[32];
	private char[] ExOR_ret = new char[50];
	private char[] E_ret = new char[48];
	private char[] IP_1_ret = new char[64];
	private char S[][] = new char[8][6];
	StringBuffer IP_1temp = new StringBuffer(65);
	public Des()
	{
	}
	
	void setKey(char[][] ck)
	{
		CkMade = ck;
	}
	
	public String Cipher(String Encode_Data, String Key, boolean DeOrEn, Create_Key CkOrg)
	{
		
		String temp = "";
		this.Encode_Data = Encode_Data;
		
		// 1. 초기치환 (IP 적용).
		long time;
		IP();
		
		// 2. L0와 R0를 생성하여 Ln과 Rn에 저장.
		System.arraycopy(IP_ret, 0, Ln, 0, 32);
		System.arraycopy(IP_ret, 32, Rn, 0, 32);
		
		// 3. 키 생성 객체 생성. 암호화는 DeOrEn 이 true, 복호화는 DeOrEn 이 false.
//		if (DeOrEn)  Ck = new Encipher_Key(CkOrg);
//		else Ck = new Decipher_Key(CkOrg);

		// 4. Des 라운드. 각 연산 법칙에 따라 Ln과 Rn을 생성.
		for(int i = 0; i<16; i++)
		{
			Make_Ln_Rn(i);
		}
		IP_1();
		return new String(IP_1_ret);
	}

	// 초기치환.
	private void IP()
	{
		
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<8; j++)
			{
				IP_ret[i * 8 + j] = Encode_Data.charAt(dt.IP[i][j]-1);
			}			
		}

	}

	// 이 부분이 16회 반복되어야 한다.
	public void copy(char[] src, char[] dest)
	{
		int len = dest.length;
		for(int i = 0; i < len; ++i)
		{
			dest[i] = src[i];
		}
	}
	
	private void Make_Ln_Rn(int i)
	{
		
		//copy(Ln, Tmp);
		System.arraycopy(Ln, 0, Tmp, 0, 32);
		Exclusive_OR(Tmp, F(Rn, CkMade[i]));
		//Exclusive_OR(Tmp, F(Rn, Ck.return_Key()));
		//copy(Rn, Ln);
		System.arraycopy(Rn, 0, Ln, 0, 32);
		System.arraycopy(ExOR_ret,0 ,Rn, 0, 32);

	}
	
	// XOR은 두개의 입력값이 다를때만 1이 출력된다.	
	private void Exclusive_OR(char[] A, char[] B)
	{
		int Len = 0;
		Len = A.length;
		for (int i=0; i<Len; i++)
		{
			if (A[i] != B[i])
				ExOR_ret[i] = '1';
			else
				ExOR_ret[i] = '0';
		}
	}

	// 암호화 함수.
	private char[] F(char[] Rn_1, char[] Key)
	{
		/* 	1. Rn_1에 E를 적용
		 	2. Rn_1과 Key를 Exclusive_OR
		 	3. 2의 값에 s_box를 적용
		 	4. 3의 값에 P를 적용. P(S_Box(Exclusive_OR(Key, E(Rn_1))))
			5. return (4의 값);		
						*/

		E(Rn_1);

		Exclusive_OR(Key, E_ret);

		S_Box(ExOR_ret);

		P(S_Box_ret);

		return P_ret;		
	}
	
	private void E(char[] Rn_1)
	{
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<6; j++)
			{
				E_ret[i*6 + j] = Rn_1[dt.Pe[i][j]-1];
			}			
		}
	}

	private void S_Box(char[] RnK)
	{
				
		int i=0, j=0, k=0, row=0, col=0, temp = 0, num = 0, dec = 16;
		
		for(i=0; i<8; i++)
		{
			System.arraycopy(RnK, i*6, S[i], 0, 6);
		}
		j = 0;
		for(i = 0; i<8; i++)
		{
			col += (S[i][j++] == '1') ? 2 : 0;
			row += (S[i][j++] == '1') ? 8 : 0;
			row += (S[i][j++] == '1') ? 4 : 0;
			row += (S[i][j++] == '1') ? 2 : 0;
			row += (S[i][j++] == '1') ? 1 : 0;
			col += (S[i][j] == '1') ? 1 : 0;
						
			temp = dt.S_Box[i][col][row];
			dec = 16;
			for(k = 0; k < 4; ++k)
			{
				dec >>= 1;
				if((dec & temp) == 0)
					S_Box_ret[num++] = '0';
				else
					S_Box_ret[num++] = '1';
			}
				col = row = j = 0;
		}
		

	}	

	private void P(char[] A)
	{
		
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<4; j++)
			{
				P_ret[i*4 + j] = A[dt.P[i][j]-1];
			}			
		}

	}	
	
	private void IP_1()
	{

		
		IP_1temp.setLength(0);
		IP_1temp.append(Rn);
		IP_1temp.append(Ln);
		
		for(int i=0; i<8; i++)
		{
			for(int j=0; j<8; j++)
			{
				IP_1_ret[i*8 + j] = IP_1temp.charAt(dt.IP_Inver[i][j]-1);
			}			
		}
		
	}
}