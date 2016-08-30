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

import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.napi.NApi;

public class CreateDialogue extends DialogueAdapter {

   public CreateDialogue(Composite parent, String[] element) {
      super(parent, element); // must always supply parent
   }

   public CreateDialogue(Composite parent, int style, String[] element) {
      super(parent, style, element); // must always supply parent and style
   }
   
   public boolean execCreate() {
      boolean bRet = false;
      NApi napi = null;

      try {
         napi = new NApi();
         napi.NDisc_Connect(NDCommon.HOST, NDCommon.PORT);

         if ("STORAGE".equals(objectType)) { // create volume for STORAGE
            Volume volume = new Volume();
            volume.setName(((Text) (fields.get(0))).getText().trim());
            volume.setAccessable(((Text) (fields.get(1))).getText().trim());
            volume.setDesc(((Text) (fields.get(2))).getText().trim());
      
            if (IS_NULL(volume.getName()) || IS_NULL(volume.getAccessable()) || IS_NULL(volume.getDesc())) {
               throw new Exception ("every fields must be assigned");
            }
  
            bRet = napi.NDISC_MakeVolume(volume);
         } else if ("VOLUME".equals(objectType)) { // create media for VOLUME
            Media media = new Media();
            media.setName(((Text) (fields.get(0))).getText().trim());
            media.setType(Integer.parseInt(((Text) (fields.get(1))).getText().trim()));
            media.setPath(((Text) (fields.get(2))).getText().trim());
            media.setDesc(((Text) (fields.get(3))).getText().trim());
            media.setMaxSize(Long.parseLong(((Text) (fields.get(4))).getText().trim()));
            media.setVolumeId(Integer.parseInt(((Text) (fields.get(5))).getText().trim()));

            if (IS_NULL(media.getName()) ||
                IS_NULL(media.getPath()) ||
                IS_NULL(media.getDesc())) {
               throw new Exception ("every fields must be assigned");               
            }
 
            bRet = napi.NDISC_MakeMedia(media);
         }
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

      return bRet;

   }

   public void createGui() {
      setLayout(new GridLayout(1, true));

      objectType = element[0];
      // String groupName = objectType + " : ID [" + element[1] + "]";

      Group entryGroup = new Group(this, SWT.NONE);
      // entryGroup.setText(groupName);

      // use 2 columns, not same width
      GridLayout entryLayout = new GridLayout(2, false);
      entryGroup.setLayout(entryLayout);
      entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if ("STORAGE".equals(objectType)) {
         createLabelledText(entryGroup, "Name ", 40, "Input Volume Name");
         createLabelledText(entryGroup, "Access Right ", 40, "Input Volume Access Right");
         createLabelledText(entryGroup, "Description ", 40, "Input Volume Description");
      } else if ("VOLUME".equals(objectType)) {
         createLabelledText(entryGroup, "Name ", 40, "Input Media Name");
         createLabelledText(entryGroup, "Type ", 40, "Input Media Type");
         createLabelledText(entryGroup, "Path ", 40, "Input Media Storage Path");
         createLabelledText(entryGroup, "Description ", 40, "Input Media Description");
         createLabelledText(entryGroup, "Maximum Capacity ", 40, "Input Manimum Media Capacity");
         createLabelledText(entryGroup, "Media Volume ID ", 40, "Input Media Volume ID");
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
            boolean bRet = execCreate();
            if (bRet) {
               MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.ICON_INFORMATION);
               messageBox.setText("success");
               messageBox.setMessage("successfully created");
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