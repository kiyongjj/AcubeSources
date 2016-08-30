package com.sds.acube.ndisc.mts.process.impl;

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;
import com.sds.acube.ndisc.mts.storage.iface.StorageIF;

public class ProcVolInfo extends DefProcessAdaptor {
   public ProcVolInfo(LoggerIF logger, StorageIF storage) {
      super(logger, storage);
   }
   
   public boolean process(Object obj1, Object obj2) {
      return false;
   }

   protected NFile[] getBasicNFileInfo(NFile[] nFile) throws Exception {
      return null;
   }

   protected NFile[] getStorageInfo(NFile[] nFile, String option) throws Exception {
      return null;
   }
}
