package com.sds.acube.ndisc.mts.filter.impl;

import java.io.File;

import com.sds.acube.ndisc.mts.util.sea.EncryptorBySEA;

/**
 * Sea128용 파일 암복호화 모듈
 * @author JihoonHa
 * @since 20160722
 */
public class Sea128FltEncrypt extends DefFilterAdaptor {

   EncryptorBySEA encryptorBySEA = new EncryptorBySEA();

   public void filterFileForward(String filePath) throws Exception {
      String tmpPath = null;

      tmpPath = filePath + "__ENCRYPT_TMP__";

      encryptorBySEA.encryptFile(filePath, tmpPath);

      if (new File(filePath).exists())
    	 new File(filePath).delete();

      new File(tmpPath).renameTo(new File(filePath));
   }

   public void filterFileReverse(String filePath) throws Exception {
      String tmpPath = null;

      tmpPath = filePath + "__DECRYPT_TMP__";

      encryptorBySEA.decryptFile(filePath, tmpPath);

      if (new File(filePath).exists())
    	 new File(filePath).delete();

      new File(tmpPath).renameTo(new File(filePath));
   }
   
}
