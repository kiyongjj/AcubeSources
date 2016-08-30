package com.sds.acube.ndisc.explorer.dialogue;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.sds.acube.ndisc.explorer.Explorer;

public abstract class DialogueAdapter extends Composite {
   
   public abstract void createGui();    
   
   ArrayList fields = new ArrayList(); // all fields
   String[] element = null;
   Composite composite = null;
   String objectType = null;
   
   String message = null;

   // reset all enabled-registered fields
   public void clearFields() {
      for (Iterator i = fields.iterator(); i.hasNext();) {
         Text text = (Text) i.next();
         if (text.isEnabled()) {
            text.setText("");
         }
      }
   }

   public DialogueAdapter(Composite parent, String[] element) {
      this(parent, SWT.NONE, element); // must always supply parent
   }

   public DialogueAdapter(Composite parent, int style, String[] element) {
      super(parent, style); // must always supply parent and style
      this.element = element;
      this.composite = parent;
      createGui();
   }

   // GUI creation helpers
   protected Text createLabelledText(Composite parent, String label) {
      return createLabelledText(parent, label, 40, null);
   }

   protected Text createLabelledText(Composite parent, String label, int limit, String tip) {
      Label l = new Label(parent, SWT.LEFT);
      l.setText(label);
      Text text = new Text(parent, SWT.BORDER);
      
      if (limit > 0) {
         text.setTextLimit(limit);
      }
      if (tip != null) {
         text.setToolTipText(tip);
      }
      text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      fields.add(text);
      return text;
   }
  
   protected Button createButton(Composite parent, String label, SelectionListener l) {
      return createButton(parent, label, l);
   }

   protected Button createButton(Composite parent, String label, String tip, SelectionListener l) {
      Button b = new Button(parent, SWT.NONE);
      b.setText(label);
      if (tip != null) {
         b.setToolTipText(tip);
      }
      if (l != null) {
         b.addSelectionListener(l);
      }
      return b;
   }

   protected boolean IS_NULL(String string) {
      boolean bReturn = false;
      
      if (null == string || 0 == string.length()) {
         bReturn = true;
      }
      
      return bReturn;
   }
   
   protected void refreshExplorer() {
      Explorer.tv.refresh(Explorer.tree_selected_object);
      Explorer.tbv.refresh();
   }
   
   // partial selection listener
   class MySelectionAdapter implements SelectionListener {
      public void widgetSelected(SelectionEvent e) {
         // default is to do nothing
      }

      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }
   };
}
