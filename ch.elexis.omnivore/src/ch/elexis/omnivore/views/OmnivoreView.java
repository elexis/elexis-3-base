/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.omnivore.data.DocHandle;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

/**
 * A class do receive documents by drag&drop. Documents are imported into the database and linked to
 * the selected patient. On double-click they are opened with their associated application.
 */

public class OmnivoreView extends ViewPart implements IActivationListener {
	private TreeViewer viewer;
	private Tree table;
	RestrictedAction editAction, deleteAction, importAction;
	public static String importAction_ID = "ch.elexis.omnivore.data.OmnivoreView.importAction";
	
	private Action exportAction;
	private Action doubleClickAction;
	private Action flatViewAction;
	private final String[] colLabels =
		{
			"", Messages.OmnivoreView_categoryColumn, Messages.OmnivoreView_dateColumn, Messages.OmnivoreView_titleColumn, //$NON-NLS-1$
			Messages.OmnivoreView_keywordsColumn
		};
	private final int[] colWidth = {
		20, 80, 80, 150, 500
	};
	private int sortMode = SORTMODE_DATE;
	private boolean bReverse = false;
	private boolean bCatReverse = false;
	private boolean bFlat = false;
	private String searchTitle = "";
	private String searchKW = "";
	static final int SORTMODE_DATE = 0;
	static final int SORTMODE_TITLE = 1;
	// ISource selectedSource = null;
	
	private static final String SORTMODE_DEF = "omnivore/sortmode"; //$NON-NLS-1$
	private final ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			viewer.refresh();
		}
		
	};
	
	private final ElexisUiEventListenerImpl eeli_user = new ElexisUiEventListenerImpl(Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		@Override
		public void runInUi(ElexisEvent ev){
			String[] defsort = CoreHub.userCfg.get(SORTMODE_DEF, "0,1").split(","); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				sortMode = Integer.parseInt(defsort[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			bReverse = defsort.length > 1 ? defsort[1].equals("1") : false; //$NON-NLS-1$
			viewer.refresh();
			importAction.reflectRight();
			editAction.reflectRight();
			deleteAction.reflectRight();
			
		}
	};
	
	private final ElexisUiEventListenerImpl eeli_dochandle = new ElexisUiEventListenerImpl(
		DocHandle.class, ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_DELETE) {
		@Override
		public void runInUi(ElexisEvent ev){
			viewer.refresh();
		}
		
	};
	
	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){}
		
		/** Add filters for search to query */
		private void addFilters(Query<DocHandle> qbe) {
			qbe.add(DocHandle.FLD_TITLE, Query.LIKE, "%"+searchTitle+"%");
			// Add every keyword
			for (String kw: searchKW.split(" ")) {
				qbe.add(DocHandle.FLD_KEYWORDS, Query.LIKE, "%"+kw+"%");
			}
		}
		
		private boolean filterMatches(String[] kws, DocHandle h) {
			if (!h.getTitle().toLowerCase().contains(searchTitle.toLowerCase()))
				return false;
			String dkw = h.getKeywords().toLowerCase();
			for (String kw: kws) {
				if (!dkw.contains(kw)) {
					return false;
				}
			}
			return true;
		}
		
		/** Filter a list of DocHandles */
		private List<DocHandle> filterList(List<DocHandle> list) {
			List<DocHandle> result = new LinkedList<DocHandle>();
			String[] kws = searchKW.toLowerCase().split(" ");
			for (DocHandle dh: list) {
				if (filterMatches(kws, dh))
					result.add(dh);
			}
			return result;
		}
		
		public Object[] getElements(Object parent){
			List<DocHandle> ret = new LinkedList<DocHandle>();
			Patient pat = ElexisEventDispatcher.getSelectedPatient();
			if (!bFlat && pat != null) {
				List<DocHandle> cats = DocHandle.getMainCategories();
				for (DocHandle dh : cats) {
					if (filterList(dh.getMembers(pat)).size() > 0) {
						ret.add(dh);
					}
				}
				Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
				qbe.add(DocHandle.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle.FLD_CAT, "", null); //$NON-NLS-1$
				addFilters(qbe);
				List<DocHandle> root = qbe.execute();
				ret.addAll(root);
			} else if (pat != null) {
				// Flat view -> all documents that
				Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
				qbe.add(DocHandle.FLD_PATID, Query.EQUALS, pat.getId());
				addFilters(qbe);
				List<DocHandle> docs = qbe.execute();
				for (DocHandle dh: docs) {
					if (!dh.isCategory())
						ret.add(dh);
				}
			}
			return ret.toArray();
		}
		
		public Object[] getChildren(Object parentElement){
			Patient pat = ElexisEventDispatcher.getSelectedPatient();
			if (!bFlat && pat != null && (parentElement instanceof DocHandle)) {
				DocHandle dhParent = (DocHandle) parentElement;
				return filterList(dhParent.getMembers(pat)).toArray();
			} else {
				return new Object[0];
			}
		}
		
		public Object getParent(Object element){
			if (!bFlat && element instanceof DocHandle) {
				DocHandle dh = (DocHandle) element;
				return dh.getCategory();
			}
			return null;
		}
		
		public boolean hasChildren(Object element){
			if (element instanceof DocHandle) {
				DocHandle dh = (DocHandle) element;
				return dh.isCategory();
			}
			return false;
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index){
			DocHandle dh = (DocHandle) obj;
			switch (index) {
			case 0:
				return ""; //$NON-NLS-1$
			case 1:
				if (bFlat)
					return dh.getCategoryName();
				return dh.isCategory() ? dh.getTitle() : ""; //$NON-NLS-1$
			case 2:
				return dh.isCategory() ? "" : dh.getDate(); //$NON-NLS-1$
			case 3:
				return dh.isCategory() ? "" : dh.getTitle(); //$NON-NLS-1$
			case 4:
				return dh.isCategory() ? "" : dh.get(DocHandle.FLD_KEYWORDS); //$NON-NLS-1$
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
		private int docCompare(DocHandle d1, DocHandle d2) {
			String c1, c2;
			if (sortMode == SORTMODE_DATE) {
				c1 = new TimeTool(d1.get(DocHandle.FLD_DATE)).toString(TimeTool.DATE_COMPACT);
				c2 = new TimeTool(d2.get(DocHandle.FLD_DATE)).toString(TimeTool.DATE_COMPACT);
			} else if (sortMode == SORTMODE_TITLE) {
				c1 = d1.get(DocHandle.FLD_TITLE).toLowerCase();
				c2 = d2.get(DocHandle.FLD_TITLE).toLowerCase();
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
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2){
			if ((e1 instanceof DocHandle) && (e2 instanceof DocHandle)) {
				DocHandle d1 = (DocHandle) e1;
				DocHandle d2 = (DocHandle) e2;
				String c1 = d1.getCategory();
				String c2 = d2.getCategory();
				if (!bFlat && !c1.equals(c2)) {
					return (!bCatReverse ? c1.compareTo(c2) : c2.compareTo(c1));
				}
				if (!c1.isEmpty()) {
					return docCompare(d1, d2);
				}
			}
			return 0;
		}
		
	}
	
	class SortListener extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e){
			TreeColumn col = (TreeColumn) e.getSource();
			if (col.getData().equals(2)) {
				if (sortMode == SORTMODE_DATE) {
					bReverse = !bReverse;
				}
				sortMode = SORTMODE_DATE;
			} else if (col.getData().equals(3)) {
				if (sortMode == SORTMODE_TITLE) {
					bReverse = !bReverse;
				}
				sortMode = SORTMODE_TITLE;
			} else if (col.getData().equals(1) && !bFlat) {
				bCatReverse = !bCatReverse;
			}
			CoreHub.userCfg.set(SORTMODE_DEF, Integer.toString(sortMode) + "," //$NON-NLS-1$
				+ (bReverse ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
			viewer.refresh();
		}
		
	}
	
	/**
	 * The constructor.
	 */
	public OmnivoreView(){
		DocHandle.load(StringConstants.ONE); // make sure the table is created
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(4, false));
		
		// Title search field
		Label lSearchTitle = new Label(parent, SWT.NONE);
		lSearchTitle.setText(Messages.OmnivoreView_searchTitleLabel);
		lSearchTitle.setLayoutData(
			new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text tSearchTitle = new Text(parent, SWT.SINGLE);
		tSearchTitle.setLayoutData(
			new GridData(SWT.FILL, SWT.TOP, true, false));
		
		// Keyword search field
		Label lSearchKW = new Label(parent, SWT.NONE);
		lSearchKW.setText(Messages.OmnivoreView_searchKeywordsLabel);
		lSearchKW.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text tSearchKW = new Text(parent, SWT.SINGLE);
		tSearchKW.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		// Add search listener
		ModifyListener searchListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				searchKW = tSearchKW.getText();
				searchTitle = tSearchTitle.getText();
				refresh();
			}
		};
		tSearchTitle.addModifyListener(searchListener);
		tSearchKW.addModifyListener(searchListener);
		
		
		// Table to display documents
		table = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		SortListener sortListener = new SortListener();
		TreeColumn[] cols = new TreeColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TreeColumn(table, SWT.NONE);
			cols[i].setWidth(colWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(sortListener);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		viewer = new TreeViewer(table);
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
		GlobalEventDispatcher.addActivationListener(this, this);
		eeli_user.catchElexisEvent(ElexisEvent.createUserEvent());
		viewer.setInput(getViewSite());
		
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
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
		MenuManager mnSources = new MenuManager(Messages.OmnivoreView_dataSources);
		manager.add(importAction);
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
		manager.add(exportAction);
		manager.add(flatViewAction);
	}
	
	private void makeActions(){
		importAction =
			new RestrictedAction(AccessControlDefaults.ACE_DOC_CREATE,
				Messages.OmnivoreView_importActionCaption) {
				{
				setToolTipText(Messages.OmnivoreView_importActionToolTip);
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
				}
				
				public void doRun(){
					if (ElexisEventDispatcher.getSelectedPatient() == null)
						return;
					FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.OPEN);
					String filename = fd.open();
					if (filename != null) {
						DocHandle.assimilate(filename);
						viewer.refresh();
					}
				}
			};
		
		deleteAction =
			new RestrictedAction(AccessControlDefaults.ACE_DOC_DELETE,
				Messages.OmnivoreView_deleteActionCaption) {
				{
				setToolTipText(Messages.OmnivoreView_deleteActionToolTip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			public void doRun(){
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					DocHandle dh = (DocHandle) obj;
					
					if (dh.isCategory()) {
						if (CoreHub.acl.request(AccessControlDefaults.ACE_DOC_CATDELETE)) {
							InputDialog id =
								new InputDialog(
									getViewSite().getShell(),
									MessageFormat.format("Kategorie {0} löschen", dh.getLabel()),
									"Geben Sie bitte an, in welche andere Kategorie die Dokumente dieser Kategorie verschoben werden sollen",
									"", null);
							if (id.open() == Dialog.OK) {
								DocHandle.removeCategory(dh.getLabel(), id.getValue());
								viewer.refresh();
							}
						} else {
							SWTHelper.showError("Insufficient Rights",
								"You have insufficient rights to delete document categories");
						}
						
					} else {
						if (SWTHelper.askYesNo(
							Messages.OmnivoreView_reallyDeleteCaption,
					MessageFormat.format(Messages.OmnivoreView_reallyDeleteContents,
						dh.get("Titel")))) { //$NON-NLS-2$
					dh.delete();
					viewer.refresh();
						}
				}
			};
		};
			
		editAction =
			new RestrictedAction(AccessControlDefaults.ACE_DOC_DELETE,
				Messages.OmnivoreView_editActionCaption) {
				{
					setToolTipText(Messages.OmnivoreView_editActionTooltip);
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			public void doRun(){
					ISelection selection = viewer.getSelection();
					DocHandle dh = (DocHandle) ((IStructuredSelection) selection).getFirstElement();
					if (dh.isCategory()) {
						if (CoreHub.acl.request(AccessControlDefaults.ACE_DOC_CATDELETE)) {
							
							InputDialog id =
								new InputDialog(getViewSite().getShell(), MessageFormat.format(
									"Kategorie '{0}' umbenennen.", dh.getLabel()),
									"Geben Sie bitte einen neuen Namen für die Kategorie ein",
									dh.getLabel(), null);
							if (id.open() == Dialog.OK) {
								String nn = id.getValue();
								DocHandle.renameCategory(dh.getLabel(), nn);
								viewer.refresh();
							}
						} else {
							SWTHelper.showError("Insufficient Rights",
								"You have insufficient rights to delete document categories");
							
						}
					} else {
						FileImportDialog fid = new FileImportDialog(dh);
						if (fid.open() == Dialog.OK) {
							viewer.refresh(true);
						}
					}
					
				}
			};
		
		doubleClickAction = new Action() {
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				DocHandle dh = (DocHandle) obj;
				if (dh.isCategory()) {
					if (viewer.getExpandedState(dh)) {
						viewer.collapseToLevel(dh, TreeViewer.ALL_LEVELS);
					} else {
						viewer.expandToLevel(dh, TreeViewer.ALL_LEVELS);
					}
				} else {
					dh.execute();
				}
				
			}
		};
		
		exportAction = new Action(Messages.OmnivoreView_exportActionCaption) {
			{
				setImageDescriptor(Images.IMG_EXPORT.getImageDescriptor());
				setToolTipText(Messages.OmnivoreView_exportActionTooltip);
			}
			
			public void run(){
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj == null)
					return;
				DocHandle dh = (DocHandle) obj;
				String mime = dh.get(DocHandle.FLD_MIMETYPE);
				FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
				fd.setFileName(mime);
				String fname = fd.open();
				if (fname != null) {
					if (!dh.storeExternal(fname)) {
						SWTHelper.showError(Messages.OmnivoreView_configErrorCaption,
							Messages.OmnivoreView_configErrorText);
					}
				}
			}
		};
		
		flatViewAction = new Action(Messages.OmnivoreView_flatActionCaption,
				Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
				setToolTipText(Messages.OmnivoreView_flatActionTooltip);
			}
			public void run() {
				bFlat = isChecked();
				refresh();
			}
		};
	};
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
	
	public void refresh(){
		viewer.refresh();
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat, eeli_user, eeli_dochandle);
			refresh();
		} else {
			ElexisEventDispatcher.getInstance()
				.removeListeners(eeli_pat, eeli_user, eeli_dochandle);
		}
		
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor(String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.omnivoredirect", path); //$NON-NLS-1$
	}
}