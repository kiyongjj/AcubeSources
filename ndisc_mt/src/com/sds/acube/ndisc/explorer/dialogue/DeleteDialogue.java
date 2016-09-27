package com.sds.acube.ndisc.explorer.dialogue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import com.sds.acube.ndisc.explorer.util.DBHandler;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.napi.NApi;

public class DeleteDialogue extends DialogueAdapter {

   public DeleteDialogue(Composite parent, String[] element) {
      super(parent, element); // must always supply parent
   }

   public DeleteDialogue(Composite parent, int style, String[] element) {
      super(parent, style, element); // must always supply parent and style
   }
   
   public boolean execDelete() {
      boolean bRet = false;
      String id = (String)fields.get(0);

      try {
         if ("VOLUME".equals(objectType)) {  
            Volume volume = new Volume();
            volume.setId(Integer.parseInt(id));         
            
            bRet = DBHandler.deleteVolume(volume);
         } else if ("MEDIA".equals(objectType)) {   
            Media media = new Media();
            media.setId(Integer.parseInt(id));                
           
            bRet = DBHandler.deleteMedia(media);
         } else if ("FILE".equals(objectType)) {
            NApi napi = null;
            
            try {
               napi = new NApi();
               napi.NDisc_Connect(NDCommon.HOST, NDCommon.PORT);            
               NFile[] nFile = new NFile[1];
               nFile[0] = new NFile();
               nFile[0].setId(id);
   
               bRet = napi.NDISC_FileDel(nFile);
            } catch (Exception e) {
               message = e.getMessage();
               bRet = false;
            } finally {
               try {
                  napi.NDisc_Disconnect();
               } catch (Exception ex) {
                  ex.printStackTrace();
               }
            }
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

      Label L1 = new Label(entryGroup, SWT.SINGLE);
      Label L2 = new Label(entryGroup, SWT.SINGLE);
      if ("VOLUME".equals(objectType)) {
         L1.setText("Only empty volume can be deleted.");
      } else if ("MEDIA".equals(objectType)) {
         L1.setText("Only empty media can be deleted.");   
      } else if ("FILE".equals(objectType)) {
         L1.setText("File will be deleted entirely.");
      }
      L2.setText("Are you sure ?");
      // add object id
      fields.add(element[1]);
            
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
            boolean bRet = execDelete();
            if (bRet) {
               MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.ICON_INFORMATION);
               messageBox.setText("success");
               messageBox.setMessage("successfully deleted");
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
