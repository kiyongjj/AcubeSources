package com.sds.rqreport.util;
import java.security.*;

import javax.crypto.*;
import java.io.*;

public class Decrypter {
	//Key key;
	Cipher cipher = null;
	protected String rqBinstr = null;
	int algorithm = 0;
	static final int DES = 0;
	static final int DES3 = 1;
	char[] bin= {0x0001, 0x0002, 0x0004, 0x0008,
		0x0010, 0x0020, 0x0040, 0x0080,
		0x0100, 0x0200, 0x0400, 0x0800,
		0x1000, 0x2000, 0x4000, 0x8000};
	static String[] binStr = null;
	static int[] POW26 = {1, 26,  676, 17576};
	static
	{
		int size = 0;
		binStr = new String[256];
		for(int i = 0; i < 256; ++i)
		{
			binStr[i] = Integer.toBinaryString(i);
			size = binStr[i].length();
			binStr[i] = "00000000".substring(size) + binStr[i];
		}
	}
	public Decrypter(Key key, String transformation)
	{
		//this.key = key;
		try
		{

			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, key);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Decrypter(String keystr, String transformation)
	{
		Key readKey = Encrypter.makeKey(keystr);
		try
		{

			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, readKey);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Decrypter(String keystr, int algorithm)
	{
		this.algorithm = algorithm;
		genKey(keystr);
	}

	public Decrypter(String keystr)
	{
		this.algorithm = DES;
		genKey(keystr);

	}

	public String decrypt(String str)
	{
		String output = null;
		if(rqBinstr != null && rqBinstr.length() > 0)
		{
			output = rqDecrypt(str, rqBinstr);
			return output;
		}
		String arg = replaceAll(str, "\\n", "\n");
		byte [] cipherText = Base64Decoder.decodeToBytes(arg);
		try
		{
			byte [] decryptedText = cipher.doFinal(cipherText);
		 	output =  new String(decryptedText, "UTF8");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return output;
	}

	public static void main(String[] args) {
	//	Decrypter dec = new Decrypter("rO0ABXNyACFjb20uc3VuLmNyeXB0by5wcm92aWRlci5ERVNlZGVLZXkiMda6D0P12gIAAVsAA2tl\neXQAAltCeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAGGeea5LlWxxSlD2hT6dkN6hnnmuS5VscUg==", "DESede/ECB/PKCS5Padding");
	//   System.out.println(dec.decrypt("Vq6AItiZ4OrWhuEGZkAA+Bxyh9IB3lYHe7gs2dpgEesdilHTHLLl/yBPOAOKVHnPt3Px65xes4cy\nZwYbkE5QWPrir3NSO8/C/5bWRHV3DAGfbI8AKsvcq9i+ryFvYZo9iqlWFKo0X9uvLAXpJNW2051l\nm0/YZ1/H+jIliH9+PlkU/xNLrHm9XKc76wEd4nvDBhQht2Cp/pxnvrkX74xdOEgL3VaHbBknGUUE\nN7GkEwspBEQJQA/lM1qlx0o/woFTa3HdJyXydLszBlbiZeVkxM12XWPVbouiviKOT9E7sIcelM1f\nawOZN02xYzSZQJfJqiOup+xyaFJhWCK116Q+bSSBwCr1Er0SSyWll52wNFxTq5g8xPCX4Sjb3m6w\nS/GPr3vYEgHmmKYXBysoMF73l7LePo+zl2PKdXLjX0zTf2tXZqQYLMLcATexsF4nHgZHNpFZCDQ7\nnrUl9KIlG7V6kDgxiDJKUPBXC1mri4Q56XNIIww/SOCF6ItONXgUSzylrQKOOe/4OcBY/1HzHOah\nGBGw3Q8ifie/S2cIxb48LI4tYfesymS52DxLMjjzhyDCupvtP/U4kPdDIpmbLwAyxKojrqfscmhS\nYVgitdekPm0kgcAq9RK9EkslpZedsDRcU6uYPMTwl+Eo295usEvxj6972BIB5pimFwcrKDBe95ey\n3j6Ps5djynVy419M039rV2akGCzC3AE3sbBeJx4GR1+7E+Ggq8DT+1u6/rgA4p3lmADyXcrEzDH/\nNH6bf8uwEwdGKWUahN18vZBlz+tTEcJArYAB9H/E4M81F33L+Y3bwTHMjiYLbR2qK+QEZr3fdfgM\nacG37NvKhtz++CQ3xU5idvJKBIqfHvsiV4I4ey4B6/JikrDvEfBj6wXMfuJhAGazgfExI1PnXXVG\nvo2PMHKnPJ470b8cNyEpbAvRQrmYqyX94rSD3ex5BbCWR4iVDgTWyr1g15gXS8V7Cyrvbrrth9Nr\nQAxP"));
		String encqry = "";
		String key = "reqube2006-0222";
		String data;
		try {
			FileReader f = new FileReader("c:\\test.txt");
			BufferedReader  in = new BufferedReader (f);
			while((data = in.readLine()) != null)
			{
				encqry += data;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Decrypter dec = new Decrypter(key);
		long old = System.currentTimeMillis();
		String temp = dec.decrypt(encqry);
		System.out.println(System.currentTimeMillis() - old);
		System.out.println(temp);


	}

	public static int pow(int n, int p) {
	  int ret = 1;
	  for (int i = 0; i < p; ++i)
		ret *= n;
	  return ret;
	}

	public  void genKey(String IP)
	{
		try
		{
			if( algorithm == Encrypter.DES )
			{
				CipherKey ck = new CipherKey();
				rqBinstr = ck.return_key(IP);
			}
			else if( algorithm == Encrypter.DES3 )
			{
				DES3CipherKey ck = new DES3CipherKey();
				rqBinstr = ck.DES3Return_key(IP);
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return;
	}

	public String rqDecrypt3(String str, String binKey)
	{
		int length = 0;
		int pos = 0;
		String binstr = decrypt26toBinStr(str);
		length = binstr.length();

		//String data = "";
		StringBuffer data = new StringBuffer(str.length() * 6);
		Des ds1 = new Des();
		Des ds2 = new Des();
		Des ds3 = new Des();

		if(length % 64 != 0)
		{
			return "";
		}
// binkey divided by 3
		String binKey1 = binKey.substring(0,64);
		String binKey2 = binKey.substring(64,128);
		String binKey3 = binKey.substring(128,192);
		Create_Key Ck1 = new Decipher_Key(binKey1);
		Create_Key Ck2 = new Encipher_Key(binKey2);
		Create_Key Ck3 = new Decipher_Key(binKey3);
		char[][] Ckmade1 = new char[16][];
		char[][] Ckmade2 = new char[16][];
		char[][] Ckmade3 = new char[16][];

		for(int i = 0; i < 16; ++i)
		{
			Ckmade1[i] = new char[48];
			System.arraycopy(Ck1.return_Key(), 0, Ckmade1[i], 0, 48);
		}

		for(int i = 0; i < 16; ++i)
		{
			Ckmade2[i] = new char[48];
			System.arraycopy(Ck2.return_Key(), 0, Ckmade2[i], 0, 48);
		}

		for(int i = 0; i < 16; ++i)
		{
			Ckmade3[i] = new char[48];
			System.arraycopy(Ck3.return_Key(), 0, Ckmade3[i], 0, 48);
		}

		String tempstr = null;

		ds1.setKey(Ckmade1);
		ds2.setKey(Ckmade2);
		ds3.setKey(Ckmade3);

		while(length > pos)
		{
			tempstr = ds1.Cipher(binstr.substring(pos, pos + 64), binKey3, false, Ck1);
			tempstr = ds2.Cipher(tempstr, binKey2, true, Ck2);
			tempstr = ds3.Cipher(tempstr, binKey1, false, Ck3);
			data.append(tempstr);
			pos += 64;
		}

		int wlen = data.length() / 16;
		char[] pRetBuff = new char[wlen + 1];
		pos = 0;
		int idx = 0;
		long time = System.currentTimeMillis();
		while(length > pos)
		{
			pRetBuff[idx] = (char)Integer.parseInt(data.substring(pos, pos + 16), 2);
			pos += 16;
			idx++;
		}

		pRetBuff[idx] = '\0';
		String temp = new String(pRetBuff);
		return temp;
	}
	public void copy(char[] src, char[] dest)
	{
		int len = dest.length;
		for(int i = 0; i < len; ++i)
		{
			dest[i] = src[i];
		}
	}

	public String rqDecrypt(String str, String binKey)
	{
		if(algorithm == DES3)
		{
			return rqDecrypt3(str, binKey);
		}
		int length = 0;
		int pos = 0;

		String binstr = decrypt26toBinStr(str);
		length = binstr.length();
		StringBuffer data = new StringBuffer(str.length() / 3);
		Des ds = new Des();

		if(length % 64 != 0)
		{
			return "";
		}
		long time2 = System.currentTimeMillis();

		Create_Key Ck = new Decipher_Key(binKey);
		Create_Key Ck2 = new Decipher_Key(binKey);
		char[][] Ckmade = new char[16][];

		for(int i = 0; i < 16; ++i)
		{
			Ckmade[i] = new char[48];
			System.arraycopy(Ck.return_Key(), 0, Ckmade[i], 0, 48);
		}

		ds.setKey(Ckmade);

		while(length > pos)
		{
			data.append(ds.Cipher(binstr.substring(pos, pos + 64), binKey, false, Ck2));
			pos += 64;
		}



		int wlen = data.length() / 16;
		char[] pRetBuff = new char[wlen + 1];
		pos = 0;
		int idx = 0;
		int i = 0;
		char num = 0;

		while(length > pos)
		{
			num = 0;
			for(i = 0; i < 16 ;++i)
			{
				if(data.charAt(pos + i) == '1')
				{
					num |= bin[15 - i];
				}
			}
			pRetBuff[idx] = num;;
			pos += 16;
			idx++;
		}

		pRetBuff[idx] = '\0';
		String temp = new String(pRetBuff,0, idx);
		//System.out.println("sum:" + (System.currentTimeMillis()- time2));
		return temp;
	}

	public static String decrypt26(String str)
	{
	  if(str == null)
	   return "";

	  int len = str.length();
	  if(len < 1)
	  	return str;
	  char strBuffer[] = new char[len];
	  int i = 0;
	  int n = 0;
	  int p = 0;
	  while (i < len) {
		boolean end = false;
		char wcs = '\0';
		p = 0;
		while (end == false) {
		  char s = str.charAt(i++);
		  if ('a' <= s && s <= 'z') {
			  wcs += POW26[p++] * (s - 'a');
			end = true;
		  }
		  else
			wcs += POW26[p++] * (s - 'A');
		}
		strBuffer[n++] = wcs;
	  }
	  strBuffer[n] = '\0';
	  String ret = new String(strBuffer,0,n);
	  return ret;
	}
	public static String decrypt26toBinStr(String str)
	{
	  if(str == null)
	   return "";

	  int len = str.length();
	  if(len < 1)
	  	return str;
	  char strBuffer[] = new char[len];
	  int i = 0;
	  int n = 0;
	  int p = 0;
	  while (i < len) {
		boolean end = false;
		char wcs = '\0';
		p = 0;
		while (end == false) {
		  char s = str.charAt(i++);
		  if ('a' <= s && s <= 'z') {
			wcs += POW26[p++] * (s - 'a');
			end = true;
		  }
		  else
			wcs += POW26[p++] * (s - 'A');
		}
		strBuffer[n++] = wcs;
	  }
	  strBuffer[n] = '\0';

	  StringBuffer ret = new StringBuffer(n * 6);
	  String val;
	  int size = 0;
	  for(int ii = 0; ii < n; ++ii)
	  {
		ret.append(binStr[(int)strBuffer[ii]]);
	  }
	  return ret.toString();
	}


	public static String replaceAll(String src, String from, String to)
	{
	  StringBuffer sb = new StringBuffer(src);
	  int pos = 0;
	  int num = 0;

	  int dis = to.length() - from.length();
	  while((pos = src.indexOf(from,pos)) > -1){


		  sb.replace(pos , pos + from.length(),to);
		  src = sb.toString();
		pos += dis + 1;
	  }
	  String str = sb.toString();
	  sb = null;
	  return str;
	}
}
