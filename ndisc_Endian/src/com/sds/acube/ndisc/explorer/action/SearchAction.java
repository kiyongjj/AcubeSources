package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.sds.acube.ndisc.explorer.Explorer;
import com.sds.acube.ndisc.explorer.util.ImageHandler;

public class SearchAction extends ActionAdapter {

   public SearchAction(Explorer w) {
      super(w, "Search");

      setEnabled(true);      
      
      setImageDescriptor(
      ImageDescriptor.createFromURL(ImageHandler.newURL("file:icons/search.gif")));      
   }

   public void selectionChanged(SelectionChangedEvent event) {
      ;
   }
}
