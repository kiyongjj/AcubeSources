package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.sds.acube.ndisc.explorer.Explorer;

public class InfomationAction extends ActionAdapter {

   public InfomationAction(Explorer w) {
      super(w, "Infomation");
   }

   public void selectionChanged(SelectionChangedEvent event) {
      String objectType = getSelectedObjectType();
      if (null == objectType) {
         return;
      }

      String text = null;
      if ("VOLUME".equals(objectType) || "MEDIA".equals(objectType) || "FILE".equals(objectType)) {
         text = "Get Information";
         setText(text);
         setToolTipText(text);
         setEnabled(true);
      } else {
         text = "Infomation";
         setText(text);
         setToolTipText(text);
         setEnabled(false);
      }
   }
}
