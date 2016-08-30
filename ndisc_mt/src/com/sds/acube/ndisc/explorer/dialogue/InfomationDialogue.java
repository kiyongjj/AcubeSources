package com.sds.acube.ndisc.explorer.dialogue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class InfomationDialogue extends DialogueAdapter {

   public InfomationDialogue(Composite parent, String[] element) {
      super(parent, element); // must always supply parent
   }

   public InfomationDialogue(Composite parent, int style, String[] element) {
      super(parent, style, element); // must always supply parent and style
   }
   
   public void createGui() {
      setLayout(new GridLayout(1, true));

      String objectType = element[0];
      String groupName = objectType + " : ID [" + element[1] + "]";

      Group entryGroup = new Group(this, SWT.NONE);
      entryGroup.setText(groupName);

      // use 2 columns, not same width
      GridLayout entryLayout = new GridLayout(2, false);
      entryGroup.setLayout(entryLayout);
      entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if ("VOLUME".equals(objectType)) {
         createLabelledText(entryGroup, "Identification ", 40, "");
         createLabelledText(entryGroup, "Name ", 40, "");
         createLabelledText(entryGroup, "Access Right ", 40, "");
         createLabelledText(entryGroup, "Creation Date ", 40, "");
         createLabelledText(entryGroup, "Description ", 40, "");
      } else if ("MEDIA".equals(objectType)) {
         createLabelledText(entryGroup, "Identification ", 40, "");
         createLabelledText(entryGroup, "Name ", 40, "");
         createLabelledText(entryGroup, "Type ", 40, "");
         createLabelledText(entryGroup, "Path ", 40, "");
         createLabelledText(entryGroup, "Creation Date ", 40, "");
         createLabelledText(entryGroup, "Description ", 40, "");
         createLabelledText(entryGroup, "Maximum Capacity ", 40, "");
         createLabelledText(entryGroup, "Current Size ", 40, "");
         createLabelledText(entryGroup, "Media Volume ID ", 40, "");
      } else if ("FILE".equals(objectType)) {
         createLabelledText(entryGroup, "Identification ", 40, "");
         createLabelledText(entryGroup, "Name ", 40, "");
         createLabelledText(entryGroup, "Size ", 40, "");
         createLabelledText(entryGroup, "Creation Date ", 40, "");
         createLabelledText(entryGroup, "Modification Date ", 40, "");
         createLabelledText(entryGroup, "Status ", 40, "");
         createLabelledText(entryGroup, "File Media ID ", 40, "");
      }

      for (int i = 0; i < element.length - 1; i++) {
         ((Text) (fields.get(i))).setText(element[i+1]);  
         ((Text) (fields.get(i))).setEditable(false);
      }
      
      // create the button area
      Composite buttons = new Composite(this, SWT.NONE);
      buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      // make all buttons the same size
      FillLayout buttonLayout = new FillLayout();
      buttonLayout.marginHeight = 2;
      buttonLayout.marginWidth = 2;
      buttonLayout.spacing = 5;
      buttons.setLayout(buttonLayout);      
      
      // no display button
      Button noButton1 = createButton(buttons, "", "", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            ;
         }
      });
      noButton1.setVisible(false);
      
      // OK button prints input values
      Button okButton = createButton(buttons, "&OK", "Process input", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            composite.dispose();
         }
      });
      if (null == okButton) {
         ;
      }
      
      // no display button
      Button noButton2 = createButton(buttons, "", "", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            ;
         }
      });
      noButton2.setVisible(false);    
   }
}