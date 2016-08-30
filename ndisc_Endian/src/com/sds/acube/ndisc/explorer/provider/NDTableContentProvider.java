package com.sds.acube.ndisc.explorer.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sds.acube.ndisc.explorer.util.ConfigHandler;
import com.sds.acube.ndisc.explorer.util.DBHandler;

public class NDTableContentProvider implements IStructuredContentProvider {
 
   public Object[] getElements(Object element) {
      Object[] kids = null;

      String[] elmt = (String[]) element;
      String objectType = elmt[0];
      if ("ND-TREE-ROOT".equals(objectType)) {
         String[][] explorer = new String[1][3];
         explorer[0][0] = "EXPLORER";
         explorer[0][1] = "1";
         explorer[0][2] = "NDISC EXPLORER";
         kids = explorer;
      } else if ("EXPLORER".equals(objectType)) {
         String[][] explorer = new String[3][3];
         explorer[0][0] = "STORAGE";
         explorer[0][1] = "STORAGE";
         explorer[0][2] = "NDISC Storage Section";

         explorer[1][0] = "CONFIGURATION";
         explorer[1][1] = "CONFIGURATION";
         explorer[1][2] = "NDISC Configuration Section";

         explorer[2][0] = "MONITORING";
         explorer[2][1] = "MONITORING";
         explorer[2][2] = "NDISC Monitoring Section";
         kids = explorer;
      } else if ("STORAGE".equals(objectType)) {
         kids = DBHandler.getVolumes();
      } else if ("CONFIGURATION".equals(objectType)) {
         String[][] explorer = new String[1][3];
         explorer[0][0] = "CONF_LOCAL";
         explorer[0][1] = "NDISC Explorer";
         explorer[0][2] = "NDISC Explorer Config Settings";

         kids = explorer;
      } else if ("CONF_LOCAL".equals(objectType)) {
         kids = ConfigHandler.getLocalCategory();
      } else if ("CONF_REMOTE".equals(objectType)) {
         kids = ConfigHandler.getRemoteCategory();         
      } else if ("CONF_LOCAL_CATEGORY".equals(objectType)) {
         kids = ConfigHandler.getLocalProperty(elmt[1]);  
      } else if ("CONF_REMOTE_CATEGORY".equals(objectType)) {
         kids = ConfigHandler.getRemoteProperty(elmt[1]);           
      } else if ("VOLUME".equals(objectType)) {
         kids = DBHandler.getMedias(Integer.parseInt(elmt[1]));
      } else if ("MEDIA".equals(objectType)) {
         kids = DBHandler.getYears(Integer.parseInt(elmt[1]));
      } else if ("YEAR_DIR".equals(objectType)) {
         kids = DBHandler.getMonths(elmt[1], Integer.parseInt(elmt[3]));
      } else if ("MONTH_DIR".equals(objectType)) {
         kids = DBHandler.getDays(elmt[1], Integer.parseInt(elmt[3]));
      } else if ("DAY_DIR".equals(objectType)) {
         kids = DBHandler.getFiles(elmt[1], Integer.parseInt(elmt[3]));
      } 

      return kids == null ? new Object[0] : kids;
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object old_object, Object new_object) {
   }
}