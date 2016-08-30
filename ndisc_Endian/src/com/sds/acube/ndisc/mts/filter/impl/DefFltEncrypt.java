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

         // 20160722 filePath 의 파일이 있어서 rename 오류 -> 원 파일부터 삭제
         if (new File(filePath).exists())
        	 new File(filePath).delete();

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         // 20160722 원 파일을 삭제하고 rename 하여 본 코드 무의미
         //new File(tmpPath).delete();
      }
   }

   public void filterFileReverse(String filePath) throws Exception {
      String tmpPath = null;

      try {
         tmpPath = filePath + "__DECRYPT_TMP__";

         cipher.decryptFile(filePath, tmpPath);

         // 20160722 filePath 의 파일이 있어서 rename 오류 -> 원 파일부터 삭제
         if (new File(filePath).exists())
        	 new File(filePath).delete();

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         // 20160722 원 파일을 삭제하고 rename 하여 본 코드 무의미
         //new File(tmpPath).delete();
      }
   }
}
