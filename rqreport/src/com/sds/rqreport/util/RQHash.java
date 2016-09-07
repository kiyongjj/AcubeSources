package com.sds.rqreport.util;

public class RQHash {

	public static void main(String[] args) {
		if(args.length > 0)
		{
			RQHash rqhash = new RQHash();
			System.out.println(rqhash.calcSHA1(args[0]));
		}
		else
		{
			RQHash rqhash = new RQHash();
			System.out.println(rqhash.calcSHA1("HelloWorld!"));			
		}

	}

	char[] str2blks_SHA1(String str)
	{
	  	
	  int nblk = ((str.length() + 8) >> 6) + 1;
	  char[] blks = new char[nblk * 16];
	  int i = 0;
	  for(i = 0; i < nblk * 16; i++) blks[i] = 0;
	  for(i = 0; i < str.length(); i++)
	    blks[i >> 2] |= str.charAt(i) << (24 - (i % 4) * 8);
	  blks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
	  blks[nblk * 16 - 1] = (char)(str.length() * 8);
	  return blks;
	}

	int safe_add(int x, int y)
	{
	  int lsw = (x & 0xFFFF) + (y & 0xFFFF);
	  int msw = (x >> 16) + (y >> 16) + (lsw >> 16);
	  return (msw << 16) | (lsw & 0xFFFF);
	}
	
	int rol(int num,int cnt)
	{
	 // return (num << cnt) | (num >>> (32 - cnt));
	  int uns = 0x7FFFFFFF;
	  if(32 - cnt > 0)
		return (num << cnt) | ((num >> (32 - cnt)) & (uns >> (31 - cnt)));
	  else
		return (num << cnt) | (num >> (32 - cnt));		
	}
	
	int ft(int t,int b,int c,int d)
	{
	  if(t < 20) return (b & c) | ((~b) & d);
	  if(t < 40) return b ^ c ^ d;
	  if(t < 60) return (b & c) | (b & d) | (c & d);
	  return b ^ c ^ d;
	}
	
	int kt(int t)
	{
	  return (t < 20) ?  1518500249 : (t < 40) ?  1859775393 :
	         (t < 60) ? -1894007588 : -899497514;
	}
	
	public String calcSHA1(String str)
	{
	  char[] x = str2blks_SHA1(str);
	  int[] w = new int[80];

	  int a =  1732584193;
	  int b = -271733879;
	  int c = -1732584194;
	  int d =  271733878;
	  int e = -1009589776;
	  int t = 0;

	  for(int i = 0; i < x.length; i += 16)
	  {
		  int olda = a;
		  int oldb = b;
		  int oldc = c;
		  int oldd = d;
		  int olde = e;

	    for(int j = 0; j < 80; j++)
	    {
	      if(j < 16) w[j] = x[i + j];
	      else w[j] = rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
	      t = safe_add(safe_add(rol(a, 5), ft(j, b, c, d)), safe_add(safe_add(e, w[j]), kt(j)));
	      e = d;
	      d = c;
	      c = rol(b, 30);
	      b = a;
	      a = t;
	    }

	    a = safe_add(a, olda);
	    b = safe_add(b, oldb);
	    c = safe_add(c, oldc);
	    d = safe_add(d, oldd);
	    e = safe_add(e, olde);
	  }
	   return Integer.toHexString (a) +  Integer.toHexString(b) + Integer.toHexString(c) + Integer.toHexString(d) + Integer.toHexString(e);

//	  return hex(a) + hex(b) + hex(c) + hex(d) + hex(e);
	}
}
