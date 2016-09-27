package com.sds.acube.ndisc.mts.util.cipher.jce;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

public class KeyFileGenerator {
   public static Key key = null;

   public static String generate(String fileName) throws NoSuchAlgorithmException, IOException {
      KeyGenerator generator = KeyGenerator.getInstance("DES");
      generator.init(new SecureRandom());
      Key key = generator.generateKey();

      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName)); 
      out.writeObject(key);
      out.close();
      return fileName;
   }

   public static Key readKeyFile(String fileName) throws IOException, ClassNotFoundException {
      return readKeyFile(fileName, false);
   }

   public static Key readKeyFile(String fileName, boolean refresh) throws IOException, ClassNotFoundException {
      if (!refresh && key != null) {
         return key;
      }

      ObjectInputStream in = null;
      try {
         in = new ObjectInputStream(new FileInputStream(fileName));
         key = (Key) in.readObject();
      } finally {
         if (in != null)
            try {
               in.close();
            } catch (Exception e) {
            }
      }
      return key;
   }
}
