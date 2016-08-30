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

import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.napi.NApi;

public class SearchDialogue extends DialogueAdapter {

   public SearchDialogue(Composite parent, String[] element) {
      super(parent, element); // must always supply parent
   }

   public SearchDialogue(Composite parent, int style, String[] element) {
      super(parent, style, element); // must always supply parent and style
   }

   public NFile execSearch() {
      NApi napi = null;
      NFile[] nFile = null;

      try {
         napi = new NApi();
         nFile = new NFile[1];
         nFile[0] = new NFile();
         
         napi.NDisc_Connect(NDCommon.HOST, NDCommon.PORT);
  
         String fileID = ((Text) (fields.get(0))).getText();
         if (null == fileID || 0 == fileID.length()) {
            throw new Exception ("file id is blank");
        }
         
         nFile[0].setId(fileID.trim());
         nFile = napi.NDISC_FileInfo(nFile);

      } catch (Exception e) {
         message = e.getMessage();
         nFile[0] = null;
      } finally {
         try {
            napi.NDisc_Disconnect();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      return nFile[0];
   }

   public void createGui() {
      setLayout(new GridLayout(1, true));

      String groupName = "Search File";

      Group entryGroup = new Group(this, SWT.NONE);
      entryGroup.setText(groupName);

      // use 2 columns, not same width
      GridLayout entryLayout = new GridLayout(2, false);
      
      entryGroup.setLayout(entryLayout);
      
      entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createLabelledText(entryGroup, "Identification ", 40, "");
      createLabelledText(entryGroup, "Name ", 40, "");
      createLabelledText(entryGroup, "Size ", 40, "");
      createLabelledText(entryGroup, "Creation Date ", 40, "");
      createLabelledText(entryGroup, "Modification Date ", 40, "");
      createLabelledText(entryGroup, "Status ", 40, "");
      createLabelledText(entryGroup, "File Media ID ", 40, "");
      createLabelledText(entryGroup, "File Path ", 40, "");

      for (int i = 1; i < 8; i++) {
         ((Text) (fields.get(i))).setText("                                                                                                    ");
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

      // OK button prints input values
      Button okButton = createButton(buttons, "&Search", "Process input", new MySelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            NFile nFile = execSearch();
            if (null != nFile) {

               ((Text) (fields.get(1))).setText(nFile.getName());
               ((Text) (fields.get(2))).setText(nFile.getSize() + "");
               ((Text) (fields.get(3))).setText(nFile.getCreatedDate());
               ((Text) (fields.get(4))).setText(nFile.getModifiedDate());
               ((Text) (fields.get(5))).setText(nFile.getStatType());
               ((Text) (fields.get(6))).setText(nFile.getMediaId() + "");
               ((Text) (fields.get(7))).setText(nFile.getStoragePath());

               MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK | SWT.ICON_INFORMATION);
               messageBox.setText("success");
               messageBox.setMessage("successfully searched");
               messageBox.open();

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