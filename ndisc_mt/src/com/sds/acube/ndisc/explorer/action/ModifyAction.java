package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.sds.acube.ndisc.explorer.Explorer;

public class ModifyAction extends ActionAdapter {

   public ModifyAction(Explorer w) {
      super(w, "Modify");
   }

   public void selectionChanged(SelectionChangedEvent event) {
      String objectType = getSelectedObjectType();
      if (null == objectType) {
         return;
      }
      
      String text = null;
      if ("VOLUME".equals(objectType)) {
         text = "Modify Volume";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else if ("MEDIA".equals(objectType)) {
         text = "Modify Media";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else {
         text = "Modify";
         setText(text);
         setToolTipText(text);
         setEnabled(false);
      }
   }
}
