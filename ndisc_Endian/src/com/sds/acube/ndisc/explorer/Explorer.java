package com.sds.acube.ndisc.explorer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import com.sds.acube.ndisc.explorer.action.CreateAction;
import com.sds.acube.ndisc.explorer.action.DeleteAction;
import com.sds.acube.ndisc.explorer.action.ExitAction;
import com.sds.acube.ndisc.explorer.action.InfomationAction;
import com.sds.acube.ndisc.explorer.action.ModifyAction;
import com.sds.acube.ndisc.explorer.action.SearchAction;
import com.sds.acube.ndisc.explorer.provider.NDTableContentProvider;
import com.sds.acube.ndisc.explorer.provider.NDTableLabelProvider;
import com.sds.acube.ndisc.explorer.provider.NDTreeContentProvider;
import com.sds.acube.ndisc.explorer.provider.NDTreeLabelProvider;
import com.sds.acube.ndisc.explorer.util.ImageHandler;

public class Explorer extends ApplicationWindow {
   public static TableViewer tbv;

   public static TreeViewer tv;

   public static Object tree_selected_object;

   private InfomationAction infomation_action;

   private CreateAction create_action;

   private ModifyAction modify_action;

   private DeleteAction delete_action;

   private SearchAction search_action;

   private ExitAction exit_action;

   MenuManager menu_manager = new MenuManager();

   public Explorer() {
      super(null);

      infomation_action = new InfomationAction(this);
      create_action = new CreateAction(this);
      modify_action = new ModifyAction(this);
      delete_action = new DeleteAction(this);

      search_action = new SearchAction(this);

      exit_action = new ExitAction(this);

      addStatusLine();
      addMenuBar();
      addToolBar(SWT.FLAT | SWT.WRAP);
   }

   protected Control createContents(Composite parent) {
      getShell().setText("ACUBE DM Explorer Ver 0.9.4");
      getShell().setImage(ImageHandler.getImageRegistry().get("NDISC"));
      SashForm sash_form = new SashForm(parent, SWT.HORIZONTAL | SWT.NULL);

      tv = new TreeViewer(sash_form);
      tv.setContentProvider(new NDTreeContentProvider());
      tv.setLabelProvider(new NDTreeLabelProvider());

      // set root node
      String[] root = new String[1];
      root[0] = "ND-TREE-ROOT";
      tv.setInput(root);

      tbv = new TableViewer(sash_form, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
      tbv.setContentProvider(new NDTableContentProvider());
      tbv.setLabelProvider(new NDTableLabelProvider());

      // arrange table colume header : maximum 9 columns
      for (int i = 0; i < 9; i++) {
         new TableColumn(tbv.getTable(), SWT.LEFT);
      }

      tv.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();

            menu_manager.removeAll();

            tree_selected_object = selection.getFirstElement();
            // Object selected_object = selection.getFirstElement();
            if (null != tree_selected_object) {
               String[] elmt = (String[]) tree_selected_object;
               String treeObjectType = elmt[0];

               if ("STORAGE".equals(treeObjectType)) {
                  menu_manager.add(create_action);
               } else if ("VOLUME".equals(treeObjectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(create_action);
                  menu_manager.add(modify_action);
                  menu_manager.add(delete_action);
               } else if ("MEDIA".equals(treeObjectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(modify_action);
                  menu_manager.add(delete_action);
               } else if ("FILE".equals(treeObjectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(delete_action);
               }
            }

            tbv.getTable().setLinesVisible(true);

            changeTableColumeHeader(tree_selected_object, tbv.getTable().getColumns());
            tbv.setInput(tree_selected_object);

            // ////////////////////////////////////////////////////////////////////
            // tv.addSelectionChangedListener(infomation_action);
            // tv.addSelectionChangedListener(create_action);
            // tv.addSelectionChangedListener(modify_action);
            // tv.addSelectionChangedListener(delete_action);
            //
            // tv.getTree().setMenu(menu_manager.createContextMenu(tv.getTree()));
            // ////////////////////////////////////////////////////////////////////
         }
      });

      tbv.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();

            menu_manager.removeAll();

            Object selected_object = selection.getFirstElement();
            if (null != selected_object) {
               String[] elmt = (String[]) selected_object;
               String objectType = elmt[0];
               if ("STORAGE".equals(objectType)) {
                  menu_manager.add(create_action);
               } else if ("VOLUME".equals(objectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(create_action);
                  menu_manager.add(modify_action);
                  menu_manager.add(delete_action);
               } else if ("MEDIA".equals(objectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(modify_action);
                  menu_manager.add(delete_action);
               } else if ("FILE".equals(objectType)) {
                  menu_manager.add(infomation_action);
                  menu_manager.add(delete_action);
               }

            }

            setStatus("Number of items selected is " + selection.size());

            // ////////////////////////////////////////////////////////////////////
            // tbv.addSelectionChangedListener(infomation_action);
            // tbv.addSelectionChangedListener(create_action);
            // tbv.addSelectionChangedListener(modify_action);
            // tbv.addSelectionChangedListener(delete_action);
            //
            // tbv.getTable().setMenu(menu_manager.createContextMenu(tbv.getTable()));
            // ////////////////////////////////////////////////////////////////////
         }
      });

      // //////////////////////////////////////////////////////////////////
      tv.addSelectionChangedListener(infomation_action);
      tv.addSelectionChangedListener(create_action);
      tv.addSelectionChangedListener(modify_action);
      tv.addSelectionChangedListener(delete_action);

      tv.getTree().setMenu(menu_manager.createContextMenu(tv.getTree()));

      tbv.addSelectionChangedListener(infomation_action);
      tbv.addSelectionChangedListener(create_action);
      tbv.addSelectionChangedListener(modify_action);
      tbv.addSelectionChangedListener(delete_action);

      tbv.getTable().setMenu(menu_manager.createContextMenu(tbv.getTable()));
      // //////////////////////////////////////////////////////////////////

      // tv.getTree().setMenu(menu_manager.createContextMenu(tv.getControl()));
      // tv.getTree().setMenu(menu_manager.createContextMenu(tbv.getTable()));
      // do not add context menu in tree view
      // tv.getTree().setMenu(menu_manager.createContextMenu(tv.getTree()));

      sash_form.setWeights(new int[] { 3, 7 });

      return sash_form;
   }

   public static void main(String[] args) {
      Explorer w = new Explorer();
      w.setBlockOnOpen(true);
      w.open();
      Display.getCurrent().dispose();
      ImageHandler.getClipboard().dispose();
   }

   protected MenuManager createMenuManager() {
      MenuManager bar_menu = new MenuManager("");

      MenuManager explorer_menu = new MenuManager("&Explorer");
      MenuManager manage_menu = new MenuManager("&Manage");
      MenuManager util_menu = new MenuManager("&Utility");

      // # 1
      bar_menu.add(explorer_menu);
      explorer_menu.add(exit_action);

      // # 2
      bar_menu.add(manage_menu);
      manage_menu.add(infomation_action);
      manage_menu.add(create_action);
      manage_menu.add(modify_action);
      manage_menu.add(delete_action);

      // # 3
      bar_menu.add(util_menu);
      util_menu.add(search_action);

      return bar_menu;
   }

   public IStructuredSelection getTableSelection() {
      return (IStructuredSelection) (tbv.getSelection());
   }

   protected ToolBarManager createToolBarManager(int style) {

      ToolBarManager tool_bar_manager = new ToolBarManager(style);

      tool_bar_manager.add(exit_action);
      tool_bar_manager.add(search_action);

      return tool_bar_manager;
   }

   private void changeTableColumeHeader(Object element, TableColumn[] columns) {
      String[] elmt = (String[]) element;

      // initialize
      for (int i = 0; i < 9; i++) {
         columns[i].setText("-");
         columns[i].setWidth(0);
      }

      if (null != elmt) {
         if ("ND-TREE-ROOT".equals(elmt[0])) {
            ;
         } else if ("EXPLORER".equals(elmt[0])) {
            columns[0].setText("NAME");
            columns[0].setWidth(200);

            columns[1].setText("DESCRIPTION");
            columns[1].setWidth(500);
         } else if ("STORAGE".equals(elmt[0])) {
            // for volume info
            columns[0].setText("ID");
            columns[0].setWidth(50);

            columns[1].setText("NAME");
            columns[1].setWidth(150);

            columns[2].setText("ACCESS RIGHT");
            columns[2].setWidth(120);

            columns[3].setText("CREATION DATE");
            columns[3].setWidth(150);

            columns[4].setText("DESCRIPTION");
            columns[4].setWidth(200);
         } else if ("CONFIGURATION".equals(elmt[0])) {
            columns[0].setText("NAME");
            columns[0].setWidth(200);
            columns[1].setText("DESCRIPTION");
            columns[1].setWidth(500);
         } else if ("CONF_LOCAL".equals(elmt[0]) || "CONF_REMOTE".equals(elmt[0])) {
            columns[0].setText("CATEGORY");
            columns[0].setWidth(300);
         } else if ("CONF_LOCAL_CATEGORY".equals(elmt[0]) || "CONF_REMOTE_CATEGORY".equals(elmt[0])) {
            columns[0].setText("KEY");
            columns[0].setWidth(300);
            columns[1].setText("VALUE");
            columns[1].setWidth(600);    
         } else if ("CONF_LOCAL_PROPERTY".equals(elmt[0]) || "CONF_REMOTE_PROPERTY".equals(elmt[0])) {
            columns[0].setText("KEY");
            columns[0].setWidth(300);
            columns[1].setText("VALUE");
            columns[1].setWidth(600);              
         } else if ("VOLUME".equals(elmt[0])) {
            // for media info
            columns[0].setText("ID");
            columns[0].setWidth(50);

            columns[1].setText("NAME");
            columns[1].setWidth(150);

            columns[2].setText("TYPE");
            columns[2].setWidth(50);

            columns[3].setText("PATH");
            columns[3].setWidth(200);

            columns[4].setText("CREATION DATE");
            columns[4].setWidth(150);

            columns[5].setText("DESCRIPTION");
            columns[5].setWidth(200);

            columns[6].setText("MAX CAPACITY");
            columns[6].setWidth(150);

            columns[7].setText("CURRENT CAPACITY");
            columns[7].setWidth(150);

            columns[8].setText("MEDIA VOLUME ID");
            columns[8].setWidth(150);
         } else if ("MEDIA".equals(elmt[0])) {
            // for year_dir
            columns[0].setText("YEAR");
            columns[0].setWidth(150);
         } else if ("YEAR_DIR".equals(elmt[0])) {
            // for year_dir
            columns[0].setText("MONTH");
            columns[0].setWidth(150);
         } else if ("MONTH_DIR".equals(elmt[0])) {
            // for year_dir
            columns[0].setText("DAY");
            columns[0].setWidth(150);
         } else if ("DAY_DIR".equals(elmt[0]) || "FILE".equals(elmt[0])) {
            // for file info
            columns[0].setText("ID");
            columns[0].setWidth(300);

            columns[1].setText("NAME");
            columns[1].setWidth(150);

            columns[2].setText("SIZE");
            columns[2].setWidth(100);

            columns[3].setText("CREATION DATE");
            columns[3].setWidth(150);

            columns[4].setText("MODIFICATION DATE");
            columns[4].setWidth(150);

            columns[5].setText("STATUS");
            columns[5].setWidth(50);

            columns[6].setText("FILE MEDIA ID");
            columns[6].setWidth(150);
         } else {
            ;
         }
      }

      tbv.getTable().setHeaderVisible(true);
   }
}