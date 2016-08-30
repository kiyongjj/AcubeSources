package com.sds.acube.ndisc.explorer.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.acube.ndisc.explorer.util.ImageHandler;

public class NDTableLabelProvider implements ITableLabelProvider {
   public String getColumnText(Object element, int column_index) {
      String[] elmt = (String[]) element;

      String objectType = elmt[0];
      String ret = "";
      
      if ("ND-TREE-ROOT".equals(objectType)) {
         ;
      } else if ("EXPLORER".equals(objectType)) {
         ;
      } else if ("STORAGE".equals(objectType) || "CONFIGURATION".equals(objectType) || "MONITORING".equals(objectType)) {
         if (column_index < 2) {
            ret = elmt[column_index + 1];
         }
      } else if ("CONF_LOCAL".equals(objectType) || "CONF_REMOTE".equals(objectType)) {
         if (column_index < 2) {
            ret = elmt[column_index + 1];
         }    
      } else if ("CONF_LOCAL_CATEGORY".equals(objectType) || "CONF_REMOTE_CATEGORY".equals(objectType)) {
         if (column_index < 2) {
            ret = elmt[column_index + 1];
         }       
      } else if ("CONF_LOCAL_PROPERTY".equals(objectType) || "CONF_REMOTE_PROPERTY".equals(objectType)) {
         if (column_index < 2) {
            ret = elmt[column_index + 1];
         }            
      } else if ("VOLUME".equals(objectType)) {
         if (column_index < 5) {
            ret = elmt[column_index + 1];
         }
      } else if ("MEDIA".equals(objectType)) {
         if (column_index < 9) {
            ret = elmt[column_index + 1];
         }
      } else if ("YEAR_DIR".equals(objectType) || "MONTH_DIR".equals(objectType) || "DAY_DIR".equals(objectType)) {
         if (column_index < 2) {
            ret = elmt[column_index + 2];
         }           
      } else if ("FILE".equals(objectType)) {
         if (column_index < 7) {
            ret = elmt[column_index + 1];
         }
      }

      return ret;
   }

   public void addListener(ILabelProviderListener ilabelproviderlistener) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object obj, String s) {
      return false;
   }

   public void removeListener(ILabelProviderListener ilabelproviderlistener) {
   }

   public Image getColumnImage(Object element, int column_index) {
      String[] elmt = (String[]) element;
      String objectType = elmt[0];

      if (column_index != 0) {
         return null;
      } else {
         return ImageHandler.getImageRegistry().get(objectType);
      }

   }
}
