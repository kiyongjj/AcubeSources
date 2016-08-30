package com.sds.acube.ndisc.explorer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.sds.acube.ndisc.explorer.Explorer;
import com.sds.acube.ndisc.explorer.dialogue.CreateDialogue;
import com.sds.acube.ndisc.explorer.dialogue.DeleteDialogue;
import com.sds.acube.ndisc.explorer.dialogue.DialogueAdapter;
import com.sds.acube.ndisc.explorer.dialogue.InfomationDialogue;
import com.sds.acube.ndisc.explorer.dialogue.ModifyDialogue;
import com.sds.acube.ndisc.explorer.dialogue.SearchDialogue;

public abstract class ActionAdapter extends Action implements ISelectionChangedListener, IDoubleClickListener {

   public abstract void selectionChanged(SelectionChangedEvent event);

   Explorer window;

   String actionType = null;

   ActionAdapter(Explorer w, String actionType) {
      window = w;
      this.actionType = actionType;

      setText(actionType);
      setToolTipText(actionType);
      setEnabled(false);

      // below : do not show image icon
      // setImageDescriptor(ImageDescriptor.createFromURL(Util.newURL("file:icons/info.ico")));
   }

   protected String getSelectedObjectType() {
      String objectType = null;
      String[] elmt = getSelectedElement();
      if (null == elmt) {
         objectType = null;
      } else {
         objectType = elmt[0];
      }

      return objectType;
   }

   protected String[] getSelectedElement() {
      String[] elmt = null;

      IStructuredSelection selection = window.getTableSelection();
      if (1 != selection.size()) {
         Object object = null;
         if (null != (object = Explorer.tree_selected_object)) {
            elmt = (String[]) object;
         } else {
            elmt = null;
         }
      } else {
         elmt = (String[]) selection.getFirstElement();
      }
      return elmt;
   }

   public void run() {

      String[] elmt = getSelectedElement();

      Shell shell = new Shell(window.getShell(), SWT.DIALOG_TRIM);
      Display display = shell.getDisplay();
      shell.setText(actionType);

      // all children split space equally
      shell.setLayout(new FillLayout());

      DialogueAdapter dialogue = null;

      if ("Infomation".equals(actionType)) {
         dialogue = new InfomationDialogue(shell, elmt);
      } else if ("Create".equals(actionType)) {
         dialogue = new CreateDialogue(shell, elmt);
      } else if ("Modify".equals(actionType)) {
         dialogue = new ModifyDialogue(shell, elmt);
      } else if ("Delete".equals(actionType)) {
         dialogue = new DeleteDialogue(shell, elmt);
      } else if ("Search".equals(actionType)) {
         dialogue = new SearchDialogue(shell, null);
      }

      if (null == dialogue) {
         ;
      }

      shell.pack();

      // set center positon
      Rectangle bounds = (display.getActiveShell()).getBounds();
      Rectangle rect = shell.getBounds();
      int x = bounds.x + (bounds.width - rect.width) / 2;
      int y = bounds.y + (bounds.height - rect.height) / 2;
      shell.setLocation(x, y);

      shell.open();

      // process all user input events until the shell is disposed
      // (i.e., closed)
      while (!shell.isDisposed()) {

         if (!display.readAndDispatch()) { // process next message
            display.sleep(); // wait for next message
         }
      }
   }

   public void doubleClick(DoubleClickEvent event) {
      run();
   }
}
