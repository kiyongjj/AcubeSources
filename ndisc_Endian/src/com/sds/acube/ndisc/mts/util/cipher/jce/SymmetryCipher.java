package com.sds.acube.ndisc.mts.util.cipher.jce;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import org.apache.commons.codec.binary.Base64;

import com.sds.acube.ndisc.mts.common.NDCommon;

public class SymmetryCipher {

   private Key key = null;
   private Cipher encCipher = null;
   private Cipher decCipher = null;
   
   public SymmetryCipher()
   {
      initCipher(NDCommon.NDISC_CIPHER_KEY);
   }
   
   public SymmetryCipher(String keyFile)
   {
      initCipher(NDCommon.NDISC_CIPHER_KEY);
   }
      
   private void initCipher(String keyFile) {
      try {
         key = KeyFileGenerator.readKeyFile(keyFile);      
         
         // for enc
         encCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
         encCipher.init(Cipher.ENCRYPT_MODE, key);      
         
         // for dec
         decCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
         decCipher.init(Cipher.DECRYPT_MODE, key);  
    
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public String encrypt(String source) throws Exception {
      byte[] stringBytes = source.getBytes("UTF8");
      byte[] raw = encCipher.doFinal(stringBytes);
      Base64 base64 = new Base64();
      return rep_NOSUPPORT_CHARS(base64.encodeAsString(raw));
   }

   public String decrypt(String enc) throws Exception {
	   Base64 base64 = new Base64();
      byte[] raw = base64.decode(enc); //decoder.decodeBuffer(enc);
      byte[] stringBytes = decCipher.doFinal(raw);
      return rep_NOSUPPORT_CHARS(new String(stringBytes, "UTF8"));
   }
   
   public void encryptFile(String infile, String outfile) throws Exception {
           FileInputStream in = new FileInputStream(infile);
           FileOutputStream fileOut = new FileOutputStream(outfile);

           CipherOutputStream out = new CipherOutputStream(fileOut, encCipher);
           byte[] buffer = new byte[NDCommon.FILE_TRANS_BUFFER_SIZE];
           int length;
           while((length = in.read(buffer)) != -1)
                   out.write(buffer,0,length);
           in.close();
           out.close();
   }
 
   public void decryptFile(String infile, String outfile) throws Exception{

           FileInputStream in = new FileInputStream(infile);
           FileOutputStream fileOut = new FileOutputStream(outfile);

           CipherOutputStream out = new CipherOutputStream(fileOut, decCipher);
           byte[] buffer = new byte[NDCommon.FILE_TRANS_BUFFER_SIZE];
           int length;
           while((length = in.read(buffer)) != -1)
                   out.write(buffer,0,length);
           in.close();
           out.close();
   }   
   
   private String rep_NOSUPPORT_CHARS(String string) {
      string = string.replace('/', '_');
      string = string.replace('\\', '_');
      return string;
   }   
}

