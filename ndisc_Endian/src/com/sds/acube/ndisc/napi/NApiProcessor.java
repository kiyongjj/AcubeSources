package com.sds.acube.ndisc.napi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.sds.acube.cache.CacheUtil;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDConstant;

public class NApiProcessor extends NApiBase {

   protected String[] registNFile(NFile[] nFile) throws Exception {
      String[] fileID = null;

      try {
         nFile = makeRegInfo(nFile);
         sendRegInfo(nFile);
         sendStatReady();
         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {

            sendFileExNIO(nFile);

            fileID = getNDiscFileID(nFile.length);
         }
      } catch (Exception e) {
         throw e;
      } finally {
         if (IsCacheUse) {
            if (null != fileID) {
               for (int i = 0; i < nFile.length; i++) {
                  putCache(fileID[i], nFile[i].getName());
               }
            }
         }
      }

      return fileID;
   }

   protected boolean fetchNFile(NFile[] nFile) throws Exception {
      boolean bRet = false;

      try {
         if (IsCacheUse) {
            Vector vec = new Vector();
            for (int i = 0; i < nFile.length; i++) {
               String cachePath = getCache(nFile[i].getId());
               if (null == cachePath) {
                  vec.add(nFile[i]);
               } else {
                  CacheUtil.copyFile(cachePath, nFile[i].getName());
               }
            }
            nFile = new NFile[vec.size()];
            Enumeration enu = vec.elements();
            for (int i = 0; enu.hasMoreElements();) {
               nFile[i] = (NFile)enu.nextElement();
            }
         }

         nFile = makeFetchInfo(nFile);
         sendFetchInfo(nFile);
         sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {

            receiveFileExNIO(nFile);

            if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
               bRet = true;
            }
         }

      } catch (Exception e) {
         throw e;
      } finally {
         if (IsCacheUse) {
            if (bRet) {
               for (int i = 0; i < nFile.length; i++) {
                  putCache(nFile[i].getId(), nFile[i].getName());
               }
            }
         }
      }

      return bRet;
   } 

   protected boolean replaceNFile(NFile[] nFile) throws Exception {
      boolean bRet = false;

      try {
         nFile = makeRepInfo(nFile);

         sendRepInfo(nFile);
         sendStatReady();
         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {

            sendFileExNIO(nFile);

            if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
               bRet = true;
            }
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return bRet;
   }

   protected boolean deleteNFile(NFile[] nFile) throws Exception {
      boolean bRet = false;

      try {
         nFile = makeDelInfo(nFile);
         sendDelInfo(nFile);
         sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
            bRet = true;
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return bRet;
   }

   protected String[] copyNFile(NFile[] nFile) throws Exception {
      String[] fileID = null;

      try {
         nFile = makeCpyInfo(nFile);
         sendCpyInfo(nFile);
         sendStatReady();

         fileID = getNDiscFileID(nFile.length);

      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return fileID;
   }

   protected boolean moveNFile(NFile[] nFile) throws Exception {
      boolean bRet = false;

      try {
         nFile = makeMovInfo(nFile);
         sendMovInfo(nFile);
         sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
            bRet = true;
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return bRet;
   }

   protected NFile[] queryNFileInfo(NFile[] nFile) throws Exception {

      try {
         nFile = makeQueryNFileInfo(nFile);
         sendQueryNFileInfo(nFile);
         sendStatReady();

         nFile = getNDiscNFileInfo(nFile.length);

      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return nFile;
   }

   protected boolean makeVolume(Volume volume) throws Exception {
      boolean bRet = false;
      String statusCode = null;
      
      try {
    	  statusCode = NDConstant.SERVICE_STAT_MKVOLUME;
          sendVolumeInfo(volume,statusCode);
          sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
            bRet = true;
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return bRet;
   }
   
   
   protected int removeVolume(Volume volume) throws Exception {
	   int ret = 0;
	   String statusCode = null;
	   try {
		   statusCode = NDConstant.SERVICE_STAT_RMVOLUME; 
		   sendVolumeInfo(volume, statusCode);
	       sendStatReady();

	       if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
	    	   ret = 0;
	       }
	   } catch (Exception e) {
		   throw e;
	   } finally {
	         ;
	   }

	   return ret;
   }

   protected boolean makeMedia(Media media) throws Exception {
      boolean bRet = false;

      try {
         sendMediaInfo(media);
         sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
            bRet = true;
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return bRet;
   }

   protected HashMap getConfiguration() throws Exception {
      HashMap hash = null;

      try {
         sendGetConfInfo();
         sendStatReady();

         if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
            hash = receiveConfiguration();

            if (NDConstant.NO_ERROR.equals(getNDiscReply())) {
               ;
            } else {
               hash = null;
            }
         }
      } catch (Exception e) {
         throw e;
      } finally {
         ;
      }

      return hash;
   }
}
