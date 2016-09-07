package com.sds.rqreport.util;

import java.security.*;
import javax.crypto.*;
import java.io.*;

public class Encrypter {
	protected Key key = null;
	String transformation;
	protected String rqBinstr = null;
	int algorithm = 0;
	static final int DES = 0;
	static final int DES3 = 1;
	public Encrypter()
	{
	}
	public Encrypter(String IP)
	{
		genKey(IP);
	}

	public Encrypter(String IP, int algorithm)
	{
		this.algorithm = algorithm;
		genKey(IP);
	}

	public Encrypter(Key key, String transformation)
	{
		this.key = key;
		this.transformation = transformation;
	}
	public static void main(String arg[]) throws Exception
	{
//		String keystr = "rO0ABXNyACFjb20uc3VuLmNyeXB0by5wcm92aWRlci5ERVNlZGVLZXkiMda6D0P12gIAAVsAA2tl\neXQAAltCeHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAGGeea5LlWxxSlD2hT6dkN6hnnmuS5VscUg==";
//		Encrypter enc = new Encrypter(keystr,"DESede/ECB/PKCS5Padding");

/*
		StringBuffer decStr = new StringBuffer(10000000);
		try {
			FileReader f = new FileReader("c:\\dec.txt");
			BufferedReader  in = new BufferedReader (f);
			String data;
			while ((data = in.readLine()) != null)
				decStr.append(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File f = new File("C:\\out.txt");
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(Base64Encoder.encode(decStr.toString()).replaceAll("\n", "\\\\n").getBytes());
		fo.close();
		*/

		Encrypter enc = new Encrypter("test00");
		System.out.println(enc.encrypt("hello world"));
//		System.out.println(enc.encrypt(decStr));
	//	System.out.println(enc.makeEncrytedQueryStr("pdss40/HAEmpnumS.rqd","frcd|A62|pjobgrpcdfreecol3||pageid|sample202|compcd|C60|locatecd|A|compcdfreecol2|null|pjobgrpcdfreecol2||locategbncd|SA311|poscdfreecol4||frcdincd|null|compcdincd|null|frcdfreecol4|null|pjobgrpcd||eblistfreecol4||frcdView|실장(부)|seqno|0|eblistfreecol3||poscdincd||pjobgrpcdengnm||eblistincd||poscdfreecol5||pjobgrpcdnm||eblistfreecol5||poscdView||poscdnm||pjobgrpcdfreecol5||compcdengnm|SAMSUNG SDS|compcdnm|삼성SDS|compcddetailcd|C60|frcdfreecol1|null|pjobgrpcddetailcd||poscdfreecol1||pjobgrpcdincd||poscddetailcd||frcdfreecol3|null|eblistfreecol1||eblistdetailcd||eblistengnm||pjobgrpcdfreecol4||poscd||eblist||pjobgrpcdfreecol1||eblistfreecol2||compcdfreecol5|null|eblistView||frcdfreecol5|null|frcdfreecol2|null|pjobgrpcdView||compcdfreecol1|05|frcdnm|실장(부)|eblistnm||poscdfreecol3||frcdengnm|null|compcdfreecol3|null|compcdView|삼성SDS|frcddetailcd|A62|poscdfreecol2||poscdengnm||compcdfreecol4|null|authsik|and 1=1|tgtsik|af45995ea791b13137ecbb4db8ebd4dd056abc62664ccd1814920ab3154ad33cbf4b74d9518c7697b6817423f6740d0580416e62abcf0b78|accessUserid|test00|accessIdno|6805251005000|localgbn|T|eb_link|http://95.2.50.106:8001/easybaseLink.do","admin","pdss40"));
	//	CipherKey ck= new CipherKey();
	//	String data = "http://95.2.50.10/REQUBE/bin/RQISAPI.dll?view?doc=pdss40/HAMasterFile3_0R.rqd&runvar=compcd|C10|idno|'3B0DFB80896311D9B51EBB5AC69BC8AC'|&id=admin&pw=pdss40&menu=0";

//		Encrypter enc = new Encrypter("95.2.50.202");
//		System.out.println(enc.encrypt("cmd=run&doc=Demo/물품인수증.rqd&runvar=주문번호1|1000|주문번호2|1004&id=admin&pw=admin"));
//		System.out.println(enc.makeEncrytedQueryStr("pdss40/HAMasterFile3_0R.rqd","pageid|iad1110|compcd|C10|locatecd|A|locategbncd|SA311|ds|dw|seqno|1|bizunitcd|00|eb_dlevel|0|bizfgname|전사|gbltypecd|1|progbar||ym|200505|title|인당부가가치 |comstd|A|linkdocid|pdss40/ERP/ERPOvaldiag01S.rqd|check_viewer|N|eb_unit|(단위: 인당부가가치 - 백만원, 부가가치 - 억원)|bizgrpcd|0|authsik|m.compcd = 'C10' and m.idno in (select idno from sys_locate_info_SA311_v where compcd = 'C10' and locategbncd = 'SA311'","admin","pdss40"));
		//String binKey = ck.return_key("127.0.0.1");
//
//		Des ds = new Des();
//		int length = data.length();
//		StringBuffer buff = new StringBuffer(1000);
//		String zero = "00000000000000000000000000000000000000000000000000000000000000000000000";
//		for(int i = 0; i < length; ++i)
//		{
//			String temp = Integer.toBinaryString((int)data.charAt(i));
//			buff.append(zero.substring(0,16 - temp.length()) + temp);
//		}
//
//		length = buff.length();
//		int rem = length % 64;
//		String remstr;
//		if(rem > 0)
//		{
//			buff.append(zero.substring(0,64 - rem));
//		}
//
//		int pos = 0;
//		length = buff.length();
//		StringBuffer ret = new StringBuffer(1000);
//		String codeBuff = buff.toString();
//		while(length > pos)
//		{
//			ret.append(ds.Cipher(codeBuff.substring(pos, pos + 64), binKey, true));
//			pos += 64;
//		}


		//System.out.println(binKey);
		//System.out.println(ret.length());
	//	System.out.println(getGenStr(ret.toString()));




	}

	public static String rqEncrypt(String binKey, String data, int algorithm)
	{
		if(algorithm == Encrypter.DES3)
		{
			return rqEncrypt3(binKey, data);
		}
		if(binKey.length() != 64)
			return null;

		Des ds = new Des();
		int length = data.length();
		StringBuffer buff = new StringBuffer(1000);
		String zero = "00000000000000000000000000000000000000000000000000000000000000000000000";
		for(int i = 0; i < length; ++i)
		{
			String temp = Integer.toBinaryString((int)data.charAt(i));
			buff.append(zero.substring(0,16 - temp.length()) + temp);
		}

		length = buff.length();
		int rem = length % 64;
		String remstr;
		if(rem > 0)
		{
			buff.append(zero.substring(0,64 - rem));
		}

		int pos = 0;
		length = buff.length();
		StringBuffer ret = new StringBuffer(1000);
		String codeBuff = buff.toString();
		Create_Key Ck = new Encipher_Key(binKey);

		char[][] Ckmade = new char[16][];

		for(int i = 0; i < 16; ++i)
		{
			Ckmade[i] = new char[48];
			System.arraycopy(Ck.return_Key(), 0, Ckmade[i], 0, 48);
		}

		ds.setKey(Ckmade);
		while(length > pos)
		{
			ret.append(ds.Cipher(codeBuff.substring(pos, pos + 64), binKey, true, Ck));



			pos += 64;
		}
		return getGenStr(ret.toString());
	}

	public static String rqEncrypt3(String binKey, String data)
	{
		if(binKey.length() != 192)
			return null;
		String binKey1 = binKey.substring(0,64);
		String binKey2 = binKey.substring(64,128);
		String binKey3 = binKey.substring(128,192);
		Des ds = new Des();
		int length = data.length();
		StringBuffer buff = new StringBuffer(1000);
		String zero = "00000000000000000000000000000000000000000000000000000000000000000000000";
		for(int i = 0; i < length; ++i)
		{
			String temp = Integer.toBinaryString((int)data.charAt(i));
			buff.append(zero.substring(0,16 - temp.length()) + temp);
		}

		length = buff.length();
		int rem = length % 64;
		String remstr;
		if(rem > 0)
		{
			buff.append(zero.substring(0,64 - rem));
		}

		int pos = 0;
		length = buff.length();
		StringBuffer ret = new StringBuffer(1000);
		String codeBuff = buff.toString();
		String temp;
		Create_Key Ck1 = new Encipher_Key(binKey);
		Create_Key Ck2 = new Decipher_Key(binKey);
		Create_Key Ck3 = new Encipher_Key(binKey);

		while(length > pos)
		{

			temp = ds.Cipher(codeBuff.substring(pos, pos + 64), binKey1, true, Ck1);
			temp = ds.Cipher(temp, binKey2, false, Ck2);
			ret.append(ds.Cipher(temp, binKey3, true, Ck3));
			pos += 64;
		}
		return getGenStr(ret.toString());
	}

	private static String getGenStr(String binstr)
	{
		int pos = 0;
		int length = binstr.length();
		char strBuffer[] = new char[length * 4  + 1];

		short n_26[];
		n_26 = new short[5];
		int idx1 = 0;
		while(pos < length)
		{
		  char s = (char)Integer.parseInt(binstr.substring(pos, pos + 8),2);
		  if( s == '\0')
		  {
			  s = '\0';
		  }
		  // s를 26진수로 변환
		  transDecTo26(s, n_26);

		  // 26 진수를 문자로 변환
		  int idx2 = 0;
		  do
		  {

			if (n_26[idx2 + 1] == -1)
			  strBuffer[idx1++] = (char)('a' + n_26[idx2++]);
			else
			  strBuffer[idx1++] = (char)('A' + n_26[idx2++]);
		  }
		  while(n_26[idx2] != -1);
		  pos += 8;

		}
		strBuffer[idx1] = '\0';
		String ret = new String(strBuffer,0,idx1);
		strBuffer = null;
		return ret;
	}

	public Encrypter(String b64encodedStr, String transformation)
	{
		this.key = makeKey(b64encodedStr);
		this.transformation = transformation;
	}

	public static Key generateKey(String algorithm, int keysize)
	{
		Key key = null;
		try
		{
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
			keyGenerator.init(keysize);
			key = keyGenerator.generateKey();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return key;
	}

	public  void genKey(String IP)
	{
		Key key = null;
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


	public static String makeB64EncodedString(Key key)
	{
		String keydata = null;
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream obout = new ObjectOutputStream(out);
			obout.writeObject(key);
			byte[] data = out.toByteArray();
			keydata = Base64Encoder.encode(data);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return keydata;
	}

	public static Key makeKey(String b64encodedStr)
	{
		Key readKey = null;
		try
		{

			byte[] keyData = Base64Decoder.decodeToBytes(b64encodedStr);
			ByteArrayInputStream in = new ByteArrayInputStream(keyData);
			ObjectInputStream obin = new ObjectInputStream(in);
			readKey = (Key)obin.readObject();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return readKey;
	}
	public String encrypt(String str)
	{
		String ret = null;
		try
		{
			if(key != null && (rqBinstr == null || rqBinstr.length() < 1 ))
			{

				Cipher cipher = Cipher.getInstance(transformation);
				cipher.init(Cipher.ENCRYPT_MODE, key);
				byte [] plainText = str.getBytes("UTF8");
				byte [] cipherText = cipher.doFinal(plainText);
				ret = Base64Encoder.encode(cipherText);
			}else
			{
				ret = rqEncrypt(rqBinstr, str, algorithm);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return ret;
	}
	public String makeEncrytedQueryStr(String doc,String runvar, String id, String pw)
	{

		String qry = "cmd=view&doc=" + doc + "&runvar=" + runvar + "&id=" + id + "&pw=" + pw;
		return replaceAll(encrypt(qry), "\n","\\n");

	}

	public String makeEncryptedQueryStr(String doc,String runvar, String id, String pw, String other)
	{
		String qry = "cmd=view&doc=" + doc + "&runvar=" + runvar + "&id=" + id + "&pw=" + pw + other;
		return replaceAll(encrypt(qry), "\n","\\n");

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

	public static void transDecTo26(char s, short[] n_26)
	{
		int num = s;
		int idx1 = 0;
		n_26[0] = 0;
		n_26[1] = -1;
		while (num != 0)
		{
		  n_26[idx1++] = (short)(num % 26);
		  num /= 26;
		  n_26[idx1] = -1;
		}

	 }

	public static String encrypt26(String str)
	{
//	 Wide 캐릭터 수 구함
	  // length
	  int length = str.length();
	  char strBuffer[] = new char[length * 4  + 1];

	  short n_26[];
	  n_26 = new short[5];
	  int idx1 = 0;
	  for(int i = 0; i < length; ++i)
	  {
		char s = str.charAt(i);
		// s를 26진수로 변환
		transDecTo26(s, n_26);

		// 26 진수를 문자로 변환
		int idx2 = 0;
		do
		{
		  if (n_26[idx2 + 1] == -1)
			strBuffer[idx1++] = (char)('a' + n_26[idx2++]);
		  else
			strBuffer[idx1++] = (char)('A' + n_26[idx2++]);
		}
		while(n_26[idx2] != -1);

	  }
	  strBuffer[idx1] = '\0';
	  String ret = new String(strBuffer,0, idx1);
	  strBuffer = null;
	  return ret;
	}
}
