package com.sds.acube.ndisc.explorer.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.acube.ndisc.explorer.util.ImageHandler;

// very very simple
public class NDTreeLabelProvider extends LabelProvider
{
  public String getText(Object element)
  {
     String[] elmt = (String[])element;
     return elmt[2];
  }

  public Image getImage(Object element)
  {
     String[] elmt = (String[])element;
     String objectType = elmt[0];
     
     return ImageHandler.getImageRegistry().get(objectType);
  }
}
