/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.omnivore.views;

import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.Desk;
import ch.elexis.Hub;
import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.TimeTool;

/**
 * A class do receive documents by drag&drop. Documents are imported into the database and linked to
 * the selected patient. On double-click they are opened with their associated application.
 */

public class OmnivoreView extends ViewPart implements IActivationListener {
	private TableViewer viewer;
	private Table table;
	private Action importAction, editAction, deleteAction, exportAllAction;
	private Action doubleClickAction;
	private final String[] colLabels = {
		Messages.OmnivoreView_dateColumn, Messages.OmnivoreView_titleColumn,
		Messages.OmnivoreView_keywordsColumn
	};
	private final int[] colWidth = {
		80, 150, 500
	};
	private int sortMode = SORTMODE_DATE;
	private boolean bReverse = false;
	static final int SORTMODE_DATE = 0;
	static final int SORTMODE_TITLE = 1;
	
	private static final String SORTMODE_DEF = "omnivore/sortmode"; //$NON-NLS-1$
	private final ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			viewer.refresh();
		}
		
	};
	
	private final ElexisEventListenerImpl eeli_user = new ElexisEventListenerImpl(Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		@Override
		public void runInUi(ElexisEvent ev){
			String[] defsort = Hub.userCfg.get(SORTMODE_DEF, "0,1").split(","); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				sortMode = Integer.parseInt(defsort[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			bReverse = defsort.length > 1 ? defsort[1].equals("1") : false; //$NON-NLS-1$
			viewer.refresh();
		}
	};
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){}
		
		public Object[] getElements(Object parent){
			Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
			Patient pat = ElexisEventDispatcher.getSelectedPatient();
			if (pat != null) {
				qbe.add("PatID", "=", pat.getId()); //$NON-NLS-1$ //$NON-NLS-2$
				return qbe.execute().toArray();
			} else {
				return new Object[0];
			}
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index){
			switch (index) {
			case 0:
				return ((DocHandle) obj).get("Datum"); //$NON-NLS-1$
			case 1:
				return ((DocHandle) obj).get("Titel"); //$NON-NLS-1$
			case 2:
				return ((DocHandle) obj).get("Keywords"); //$NON-NLS-1$
			default:
				return "?"; //$NON-NLS-1$
			}
		}
		
		public Image getColumnImage(Object obj, int index){
			return null; // getImage(obj);
		}
		
		public Image getImage(Object obj){
			return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	class Sorter extends ViewerSorter {
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2){
			if ((e1 instanceof DocHandle) && (e2 instanceof DocHandle)) {
				DocHandle d1 = (DocHandle) e1;
				DocHandle d2 = (DocHandle) e2;
				String c1, c2;
				if (sortMode == SORTMODE_DATE) {
					c1 = new TimeTool(d1.get("Datum")) //$NON-NLS-1$
						.toString(TimeTool.DATE_COMPACT);
					c2 = new TimeTool(d2.get("Datum")) //$NON-NLS-1$
						.toString(TimeTool.DATE_COMPACT);
				} else if (sortMode == SORTMODE_TITLE) {
					c1 = d1.get("Titel").toLowerCase(); //$NON-NLS-1$
					c2 = d2.get("Titel").toLowerCase(); //$NON-NLS-1$
				} else {
					c1 = ""; //$NON-NLS-1$
					c2 = ""; //$NON-NLS-1$
				}
				if (bReverse) {
					return c1.compareTo(c2);
				} else {
					return c2.compareTo(c1);
				}
			}
			return 0;
		}
		
	}
	
	class SortListener extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e){
			TableColumn col = (TableColumn) e.getSource();
			if (col.getData().equals(0)) {
				if (sortMode == SORTMODE_DATE) {
					bReverse = !bReverse;
				}
				sortMode = SORTMODE_DATE;
			} else {
				if (sortMode == SORTMODE_TITLE) {
					bReverse = !bReverse;
				}
				sortMode = SORTMODE_TITLE;
			}
			Hub.userCfg.set(SORTMODE_DEF, Integer.toString(sortMode) + "," //$NON-NLS-1$
				+ (bReverse ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
			viewer.refresh();
		}
		
	}
	
	/**
	 * The constructor.
	 */
	public OmnivoreView(){
		DocHandle.load("1"); // make sure the table is created //$NON-NLS-1$
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent){
		
		table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		SortListener sortListener = new SortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setWidth(colWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(sortListener);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new Sorter());
		viewer.setUseHashlookup(true);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		Transfer[] transferTypes = new Transfer[] {
			FileTransfer.getInstance()
		};
		viewer.addDropSupport(DND.DROP_COPY, transferTypes, new DropTargetAdapter() {
			
			@Override
			public void dragEnter(DropTargetEvent event){
				event.detail = DND.DROP_COPY;
			}
			
			@Override
			public void drop(DropTargetEvent event){
				String[] files = (String[]) event.data;
				for (String file : files) {
					DocHandle.assimilate(file);
					viewer.refresh();
				}
				
			}
			
		});
		GlobalEventDispatcher.getInstance().addActivationListener(this, this);
		eeli_pat.catchElexisEvent(ElexisEvent.createPatientEvent());
		viewer.setInput(getViewSite());
		
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.getInstance().removeActivationListener(this, this);
		super.dispose();
	}
	
	private void hookContextMenu(){
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager){
				OmnivoreView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void contributeToActionBars(){
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager){
		manager.add(importAction);
		// manager.add(new Separator());
		// manager.add(action2);
	}
	
	private void fillContextMenu(IMenuManager manager){
		manager.add(editAction);
		manager.add(deleteAction);
		// manager.add(action2);
		// Other plug-ins can contribute there actions here
		// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager){
		manager.add(importAction);
		// manager.add(action2);
	}
	
	private void makeActions(){
		importAction = new Action(Messages.OmnivoreView_importActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_importActionToolTip);
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_IMPORT));
			}
			
			public void run(){
				FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.OPEN);
				String filename = fd.open();
				if (filename != null) {
					DocHandle.assimilate(filename);
					viewer.refresh();
				}
			}
		};
		
		deleteAction = new Action(Messages.OmnivoreView_deleteActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_deleteActionToolTip);
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_DELETE));
			}
			
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				DocHandle dh = (DocHandle) obj;
				if (SWTHelper.askYesNo(
					Messages.OmnivoreView_reallyDeleteCaption,
					MessageFormat.format(Messages.OmnivoreView_reallyDeleteContents,
						dh.get("Titel")))) { //$NON-NLS-2$
					dh.delete();
					viewer.refresh();
				}
			}
		};
		editAction = new Action(Messages.OmnivoreView_editActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_editActionTooltip);
				setImageDescriptor(Desk.getImageDescriptor(Desk.IMG_EDIT));
			}
			
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				FileImportDialog fid = new FileImportDialog((DocHandle) obj);
				if (fid.open() == Dialog.OK) {
					viewer.refresh(true);
				}
				
			}
		};
		doubleClickAction = new Action() {
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				DocHandle dh = (DocHandle) obj;
				dh.execute();
				
			}
		};
	}
	
	private void hookDoubleClickAction(){
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				doubleClickAction.run();
			}
		});
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus(){
		viewer.getControl().setFocus();
	}
	
	public void activation(boolean mode){
		// TODO Auto-generated method stub
		
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat, eeli_user);
			viewer.refresh();
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_pat, eeli_user);
		}
		
	}
	
}
