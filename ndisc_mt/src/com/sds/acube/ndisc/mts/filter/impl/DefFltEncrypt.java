package com.sds.acube.ndisc.mts.filter.impl;

import java.io.File;

import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.util.cipher.jce.SymmetryCipher;

public class DefFltEncrypt extends DefFilterAdaptor {

   SymmetryCipher cipher = new SymmetryCipher(NDCommon.NDISC_CIPHER_KEY);

   public void filterFileForward(String filePath) throws Exception {
      String tmpPath = null;

      try {
         tmpPath = filePath + "__ENCRYPT_TMP__";

         cipher.encryptFile(filePath, tmpPath);

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         new File(tmpPath).delete();
      }
   }

   public void filterFileReverse(String filePath) throws Exception {
      String tmpPath = null;

      try {
         tmpPath = filePath + "__DECRYPT_TMP__";

         cipher.decryptFile(filePath, tmpPath);

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         new File(tmpPath).delete();
      }
   }
}
