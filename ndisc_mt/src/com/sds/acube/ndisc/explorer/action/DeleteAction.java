package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.sds.acube.ndisc.explorer.Explorer;

public class DeleteAction extends ActionAdapter {
   public DeleteAction(Explorer w) {
      super(w, "Delete");
   }

   public void selectionChanged(SelectionChangedEvent event) {

      String objectType = getSelectedObjectType();
      if (null == objectType) {
         return;
      }
      
      String text = null;
      if ("VOLUME".equals(objectType)) {
         text = "Delete Volume";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else if ("MEDIA".equals(objectType)) {
         text = "Delete Media";
         setText(text);
         setToolTipText(text);
         setEnabled(true);         
      } else if ("FILE".equals(objectType)) {
         text = "Delete File";
         setText(text);
         setToolTipText(text);
         setEnabled(true);           
      } else {
         text = "Delete";
         setText(text);
         setToolTipText(text);
         setEnabled(false);
      }      
   }
}
