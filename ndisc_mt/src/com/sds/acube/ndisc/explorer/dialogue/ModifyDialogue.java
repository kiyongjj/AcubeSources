package com.sds.acube.ndisc.explorer.dialogue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.sds.acube.ndisc.explorer.util.DBHandler;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.Volume;

public class ModifyDialogue extends DialogueAdapter {

   public ModifyDialogue(Composite parent, String[] element) {
      super(parent, element); // must always supply parent
   }

   public ModifyDialogue(Composite parent, int style, String[] element) {
      super(parent, style, element); // must always supply parent and style
   }
   
   public boolean execModify() {
      boolean bRet = false;

      try {
         if ("VOLUME".equals(objectType)) {  
            Volume volume = new Volume();
            volume.setId(Integer.parseInt(((Text) (fields.get(0))).getText().trim()));            
            volume.setName(((Text) (fields.get(1))).getText().trim());
            volume.setAccessable(((Text) (fields.get(2))).getText().trim());
            volume.setCreatedDate(((Text) (fields.get(3))).getText().trim());            
            volume.setDesc(((Text) (fields.get(4))).getText().trim());
            
            bRet = DBHandler.updateVolume(volume);
         } else if ("MEDIA".equals(objectType)) {   
            Media media = new Media();
            media.setId(Integer.parseInt(((Text) (fields.get(0))).getText().trim()));                
            media.setName(((Text) (fields.get(1))).getText().trim());
            media.setType(Integer.parseInt(((Text) (fields.get(2))).getText().trim()));
            media.setCreatedDate(((Text) (fields.get(4))).getText().trim());            
            media.setDesc(((Text) (fields.get(5))).getText().trim());
            media.setMaxSize(Long.parseLong(((Text) (fields.get(6))).getText().trim()));
            media.setVolumeId(Integer.parseInt(((Text) (fields.get(8))).getText().trim()));            
            
            bRet = DBHandler.updateMedia(media);
         }
      } catch (Exception e) {
         message = e.getMessage();
         bRet = false;
      } finally {
         ;
      }

      return bRet;

   }

   public void createGui() {
      setLayout(new GridLayout(1, true));

      objectType = element[0];
      String groupName = objectType + " : ID [" + element[1] + "]";

      Group entryGroup = new Group(this, SWT.NONE);
      entryGroup.setText(groupName);

      // use 2 columns, not same width
      GridLayout entryLayout = new GridLayout(2, false);
      entryGroup.setLayout(entryLayout);
      entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if ("VOLUME".equals(objectType)) {
         createLabelledText(entryGroup, "Identification ", 40, "").setEditable(false);
         createLabelledText(entryGroup, "Name ", 40, "");
         createLabelledText(entryGroup, "Access Right ", 40, "");
         createLabelledText(entryGroup, "Creation Date ", 40, "");
         createLabelledText(entryGroup, "Description ", 40, "");
      } else if ("MEDIA".equals(objectType)) {
         createLabelledText(entryGroup, "Identification ", 40, "").setEditable(false);
         createLabelledText(entryGroup, "Name ", 40, "");
         createLabelledText(entryGroup, "Type ", 40, "");
         createLabelledText(entryGroup, "Path ", 40, "").setEditable(false);
         createLabelledText(entryGroup, "Creation Date ", 40, "");
         createLabelledText(entryGroup, "Description ", 40, "");
         createLabelledText(entryGroup, "Maximum Capacity ", 40, "");
         createLabelledText(entryGroup, "Current Size ", 40, "").setEditable(false);
         createLabelledText(entryGroup, "Media Volume ID ", 40, "");
      } 

      for (int i = 0; i < element.length - 1; i++) {
         ((Text) (fields.get(i))).setText(element[i+1]);         
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

      // OK button prints input values
      Button okButton = createButton(buttons, "&OK", "Process input", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            boolean bRet = execModify();
            if (bRet) { 
               MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.ICON_INFORMATION);
               messageBox.setText("success");
               messageBox.setMessage("successfully modified");
               messageBox.open();
               refreshExplorer();
            } else {
               MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.ICON_ERROR);
               messageBox.setText("error");
               messageBox.setMessage(message);
               messageBox.open();
            }
         }
      });
      if (null == okButton) {
         ;
      }

      // Clear button resets input values
      Button clearButton = createButton(buttons, "&Clear", "clear inputs", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            clearFields();
            ((Text) (fields.get(0))).forceFocus();
         }
      });
      if (null == clearButton) {
         ;
      }

      // Clear button resets input values
      Button exitButton = createButton(buttons, "&Exit", "exit", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            composite.dispose();
         }
      });
      if (null == exitButton) {
         ;
      }
   }

}