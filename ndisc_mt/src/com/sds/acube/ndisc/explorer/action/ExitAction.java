package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.sds.acube.ndisc.explorer.util.ImageHandler;

public class ExitAction extends Action
{
  ApplicationWindow window;

  public ExitAction(ApplicationWindow w)
  {
    window = w;
    setText("E&xit@Ctrl+W");
    setToolTipText("Exit the application");
    
    setImageDescriptor(
    ImageDescriptor.createFromURL(ImageHandler.newURL("file:icons/exit.ico")));
  }

  public void run()
  {
     MessageBox messageBox = new MessageBox(window.getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
     messageBox.setText("Exit");
     messageBox.setMessage("Exit NDISC Explorer");
     if (SWT.OK == messageBox.open()) {
        window.close();
     }
  }
}
