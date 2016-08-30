package com.sds.acube.ndisc.mts.filter.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.sds.acube.ndisc.mts.common.NDConstant;

public class DefFltCompress extends DefFilterAdaptor {

   public void filterFileForward(String filePath) throws Exception {
      FileInputStream in = null;
      GZIPOutputStream out = null;
      int read = -1;
      byte[] buffer = null;

      String tmpPath = null;

      try {
         tmpPath = filePath + "__COMP_TMP__";
         buffer = new byte[NDConstant.FILE_TRANS_BUFFER_SIZE];

         in = new FileInputStream(filePath);
         out = new GZIPOutputStream(new FileOutputStream(tmpPath));

         while (-1 != (read = in.read(buffer))) {
            out.write(buffer, 0, read);
         }
         
         // 20160722 filePath 의 파일이 있어서 rename 오류 -> 원 파일부터 삭제
         if (new File(filePath).exists())
        	 new File(filePath).delete();

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         in.close();
         out.close();

         // 20160722 원 파일을 삭제하고 rename 하여 본 코드 무의미
         //new File(tmpPath).delete();
      }

   }

   public void filterFileReverse(String filePath) throws Exception {
      GZIPInputStream in = null;
      FileOutputStream out = null;

      int read = -1;
      byte[] buffer = null;

      String tmpPath = null;

      try {
         tmpPath = filePath + "__DECOMP_TMP__";
         buffer = new byte[NDConstant.FILE_TRANS_BUFFER_SIZE];

         in = new GZIPInputStream(new FileInputStream(filePath));
         out = new FileOutputStream(tmpPath);

         while (-1 != (read = in.read(buffer))) {
            out.write(buffer, 0, read);
         }

         // 20160722 filePath 의 파일이 있어서 rename 오류 -> 원 파일부터 삭제
         if (new File(filePath).exists())
        	 new File(filePath).delete();

         new File(tmpPath).renameTo(new File(filePath));

      } catch (Exception e) {
         throw e;
      } finally {
         in.close();
         out.close();

         // 20160722 원 파일을 삭제하고 rename 하여 본 코드 무의미
         //new File(tmpPath).delete();
      }
   }
}
