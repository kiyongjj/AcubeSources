package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.sds.acube.ndisc.explorer.Explorer;

public class CreateAction extends ActionAdapter {
   public CreateAction(Explorer w) {
      super(w, "Create");
   }

   public void selectionChanged(SelectionChangedEvent event) {
      String objectType = getSelectedObjectType();
      if (null == objectType) {
         return;
      }

      String text = null;
      if ("STORAGE".equals(objectType)) {
         text = "Create New Volume";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else if ("VOLUME".equals(objectType)) {
         text = "Create New Media";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else {
         text = "Create";
         setText(text);
         setToolTipText(text);
         setEnabled(false); 
      }
   }
}
