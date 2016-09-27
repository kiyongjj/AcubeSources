package com.sds.acube.ndisc.explorer.util;

import java.net.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;

public class ImageHandler {
   private static ImageRegistry image_registry;

   private static Clipboard clipboard;

   public static URL newURL(String url_name) {
      try {
         return new URL(url_name);
      } catch (MalformedURLException e) {
         throw new RuntimeException("Malformed URL " + url_name, e);
      } 
   }

   public static ImageRegistry getImageRegistry() {
      if (image_registry == null) {
         image_registry = new ImageRegistry();
         image_registry.put("NDISC", ImageDescriptor.createFromURL(newURL("file:icons/ndisc.ico")));             
         image_registry.put("EXPLORER", ImageDescriptor.createFromURL(newURL("file:icons/explorer.ico")));          
         image_registry.put("STORAGE", ImageDescriptor.createFromURL(newURL("file:icons/storage.ico")));  
         image_registry.put("CONFIGURATION", ImageDescriptor.createFromURL(newURL("file:icons/conf.ico"))); 
         image_registry.put("MONITORING", ImageDescriptor.createFromURL(newURL("file:icons/monitor.ico")));          
         image_registry.put("VOLUME", ImageDescriptor.createFromURL(newURL("file:icons/vol.ico")));
         image_registry.put("MEDIA", ImageDescriptor.createFromURL(newURL("file:icons/md.ico")));
         image_registry.put("FILE", ImageDescriptor.createFromURL(newURL("file:icons/file.ico")));
         image_registry.put("CONF_LOCAL", ImageDescriptor.createFromURL(newURL("file:icons/conf_local.ico")));
         image_registry.put("CONF_REMOTE", ImageDescriptor.createFromURL(newURL("file:icons/conf_remote.ico")));  
         image_registry.put("YEAR_DIR", ImageDescriptor.createFromURL(newURL("file:icons/folder.gif")));  
         image_registry.put("MONTH_DIR", ImageDescriptor.createFromURL(newURL("file:icons/folder.gif")));  
         image_registry.put("DAY_DIR", ImageDescriptor.createFromURL(newURL("file:icons/folder.gif")));  
         image_registry.put("CONF_LOCAL_CATEGORY", ImageDescriptor.createFromURL(newURL("file:icons/category.gif")));  
         image_registry.put("CONF_LOCAL_PROPERTY", ImageDescriptor.createFromURL(newURL("file:icons/property.gif")));  
         image_registry.put("CONF_REMOTE_CATEGORY", ImageDescriptor.createFromURL(newURL("file:icons/category.gif")));  
         image_registry.put("CONF_REMOTE_PROPERTY", ImageDescriptor.createFromURL(newURL("file:icons/property.gif")));          
      }
      return image_registry;
   }

   public static Clipboard getClipboard() {
      if (clipboard == null) {
         clipboard = new Clipboard(Display.getCurrent());
      }

      return clipboard;
   }
}
