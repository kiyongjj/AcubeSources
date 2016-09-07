package ep;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EpTrayUtil {  
	public static String DecryptDataList(String pubkey,String seckey, String encdata){
		try{    
			Security.addProvider(new BouncyCastleProvider());
			byte[] baNewDataList = ep.Utils.base64Decode(encdata.getBytes("US-ASCII"));
			byte[] baMD5SecureKey = ep.Utils.base64Decode(seckey.getBytes("US-ASCII"));
			byte[] baPublicKey = ep.Utils.base64Decode(pubkey.getBytes("US-ASCII"));
	
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(baPublicKey);
			KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
			PublicKey publicKey = factory.generatePublic(publicKeySpec);
			
			// Cipher desCipher, rsaCipher;
			Cipher desCipher = Cipher.getInstance("DES", "BC");
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
	
			// Decrypt secrete key using public key (RSA)		
			rsaCipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] baDecryptedMD5SecretKey = rsaCipher.doFinal(baMD5SecureKey);
	
			String md5SecureKey = new String(baDecryptedMD5SecretKey, "UTF-8");
	
			//System.out.println("<<site_connect_ocx2>> decrpt 1'st md5SecureKey = "+md5SecureKey);
			
			java.util.StringTokenizer token2 = new java.util.StringTokenizer(md5SecureKey , ";");
			String strMD5 = token2.nextToken();
	
			byte[] baDecryptedSecretKey = ep.Utils.base64Decode(token2.nextToken().getBytes("US-ASCII"));
	
			SecretKeySpec secretKeySpec = new SecretKeySpec(baDecryptedSecretKey, "DES");
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES", "BC");
			SecretKey secretKey = secretKeyFactory.generateSecret(secretKeySpec);
	
			//	Decrypt user info (DES)
			desCipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] baDecryptedUserInfo = desCipher.doFinal(baNewDataList);
			String userInfo = new String(baDecryptedUserInfo, "UTF-8");
	
			/***********************************/
			/**		check MD5				   **/
			/***********************************/
	
			if(strMD5.equals(getMD5String(baDecryptedUserInfo))){
				userInfo = "EP_RETURNCODE=1;" + userInfo;
				//System.out.println("<<site_connect_ocx2>> MD5 Check OK !!");
			}
			else{
				//System.out.println("<<site_connect_ocx2>> MD5 Check Fail !!");	
				userInfo="EP_RETURNCODE=0;EP_ERRORMSG=DIGETST_CHECK_FAIL;";
			}
	  		
	  		return userInfo;
	  	}catch(Exception ex){
	  	  String userInfo = "EP_RETURNCODE=0;EP_ERRORMSG=Login Error!!;";
	  	  return userInfo;
	  	}		
	}    
	/**
	 * Gets the MD5 hash of the given byte array.
	 *
	 * @param b byte array for which an MD5 hash is desired.
	 * @return 32-character hex representation the data's MD5 hash.
	 */
	public static String getMD5String(byte[] b){
		String javaPackageMD5="";
		try{
			byte[] javaDigest = MessageDigest.getInstance("MD5").digest(b);
			javaPackageMD5 = toHex(javaDigest);
		}catch(Exception ex){
		}	
		return javaPackageMD5;
	}
  
	/**
	 * Turns array of bytes into string representing each byte as
	 * a two digit unsigned hex number.
	 *
	 * @param hash Array of bytes to convert to hex-string
	 * @return  Generated hex string
	 */
	private static String toHex(byte hash[]){
		StringBuffer buf = new StringBuffer(hash.length * 2);
		for (int i=0; i<hash.length; i++){
			int intVal = hash[i] & 0xff;
			if (intVal < 0x10){
				// append a zero before a one digit hex
				// number to make it two digits.
				buf.append("0");
			}
			buf.append(Integer.toHexString(intVal));
		}
		return buf.toString();
	}
  
}

