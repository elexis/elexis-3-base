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

package ch.elexis.omnivore.ui.views;

import static ch.elexis.omnivore.Constants.CATEGORY_MIMETYPE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.e4.events.ElexisUiEventTopics;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.omnivore.data.AutomaticBilling;
import ch.elexis.omnivore.data.Utils;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.Messages;
import ch.elexis.omnivore.ui.preferences.PreferencePage;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;
import ch.elexis.omnivore.ui.util.UiUtils;

/**
 * A class do receive documents by drag&drop. Documents are imported into the database and linked to
 * the selected patient. On double-click they are opened with their associated application.
 */

public class OmnivoreView extends ViewPart implements IRefreshable {
	private TreeViewer viewer;
	private Tree table;
	RestrictedAction editAction, deleteAction, importAction;
	public static String importAction_ID = "ch.elexis.omnivore.data.OmnivoreView.importAction";
	
	private Action exportAction;
	private Action doubleClickAction;
	private Action flatViewAction;
	private final String[] colLabels = {
		"", Messages.OmnivoreView_categoryColumn, Messages.OmnivoreView_dateColumn, //$NON-NLS-1$
		Messages.OmnivoreView_dateOriginColumn, Messages.OmnivoreView_titleColumn,
		Messages.OmnivoreView_keywordsColumn
	};
	private final String colWidth = "20,80,80,150,500";
	private final String sortSettings = "0,1,-1,false";
	private boolean bFlat = false;
	private String searchTitle = "";
	private String searchKW = "";
	// ISource selectedSource = null;
	static Logger log = LoggerFactory.getLogger(OmnivoreView.class);
	
	private OmnivoreViewerComparator ovComparator;
	
	private IPatient actPatient;
	
	@Inject
	private IEventBroker eventBroker;
	
	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this) {
		@Override
		public void partDeactivated(IWorkbenchPartReference partRef){
			if (isMatchingPart(partRef)) {
				saveColumnWidthSettings();
				saveSortSettings();
			}
		}
	};
	
	/**
	 * Test if the control is not disposed and visible.
	 * 
	 * @param control
	 * @return
	 */
	private boolean isActiveControl(Control control){
		return control != null && !control.isDisposed() && control.isVisible();
	}
	
	@Inject
	void activePatient(@Optional IPatient patient){
		Display.getDefault().asyncExec(() -> {
			if (isActiveControl(table)) {
				if (actPatient != patient) {
					viewer.refresh();
					actPatient = patient;
				}
			}
		});
	}
	
	@Inject
	void changedMandator(
		@Optional @UIEventTopic(ElexisEventTopics.EVENT_USER_CHANGED) IContact mandator){
		if (isActiveControl(table)) {
			viewer.refresh();
			importAction.reflectRight();
			editAction.reflectRight();
			deleteAction.reflectRight();
		}
	}
	
	@Optional
	@Inject
	void updateDocHandle(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IDocumentHandle dochandle){
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}
	
	@Inject
	void createDocHandle(
		@Optional @UIEventTopic(ElexisEventTopics.EVENT_CREATE) IDocumentHandle dochandle){
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}
	
	@Inject
	void deleteDocHandle(
		@Optional @UIEventTopic(ElexisEventTopics.EVENT_DELETE) IDocumentHandle dochandle){
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}
	
	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){}
		
		/** Add filters for search to query */
		private void addFilters(IQuery<IDocumentHandle> qbe){
			qbe.and("title", COMPARATOR.LIKE, "%" + searchTitle + "%");
			// Add every keyword
			for (String kw : searchKW.split(" ")) {
				qbe.and("keywords", COMPARATOR.LIKE, "%" + kw + "%");
			}
		}
		
		private boolean filterMatches(String[] kws, IDocumentHandle h){
			if (!h.getTitle().toLowerCase().contains(searchTitle.toLowerCase()))
				return false;
			String dkw = h.getKeywords().toLowerCase();
			for (String kw : kws) {
				if (!dkw.contains(kw)) {
					return false;
				}
			}
			return true;
		}
		
		/** Filter a list of DocHandles */
		private List<IDocumentHandle> filterList(List<IDocumentHandle> list){
			List<IDocumentHandle> result = new LinkedList<IDocumentHandle>();
			String[] kws = searchKW.toLowerCase().split(" ");
			for (IDocumentHandle dh : list) {
				if (filterMatches(kws, dh))
					result.add(dh);
			}
			return result;
		}
		
		public Object[] getElements(Object parent){
			List<IDocumentHandle> ret = new LinkedList<IDocumentHandle>();
			IPatient pat = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (!bFlat && pat != null) {
				List<IDocumentHandle> cats = CategoryUtil.getCategories();
				for (IDocumentHandle dh : cats) {
					if (filterList(Utils.getMembers(dh, pat)).size() > 0) {
						ret.add(dh);
					}
				}
				IQuery<IDocumentHandle> qbe =
					OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
				qbe.and("kontakt", COMPARATOR.EQUALS, pat);
				addFilters(qbe);
				List<IDocumentHandle> root = qbe.execute().parallelStream()
					.filter(d -> d.isCategory()).collect(Collectors.toList());
				ret.addAll(root);
			} else if (pat != null) {
				// Flat view -> all documents that
				IQuery<IDocumentHandle> qbe =
					OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
				qbe.and("kontakt", COMPARATOR.EQUALS, pat);
				addFilters(qbe);
				List<IDocumentHandle> docs = qbe.execute().parallelStream()
					.filter(d -> !d.isCategory()).collect(Collectors.toList());
				ret.addAll(docs);
			}
			return ret.toArray();
		}
		
		public Object[] getChildren(Object parentElement){
			IPatient pat = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (!bFlat && pat != null && (parentElement instanceof IDocumentHandle)) {
				IDocumentHandle dhParent = (IDocumentHandle) parentElement;
				return filterList(Utils.getMembers(dhParent, pat)).toArray();
			} else {
				return new Object[0];
			}
		}
		
		public Object getParent(Object element){
			if (!bFlat && element instanceof IDocumentHandle) {
				IDocumentHandle dh = (IDocumentHandle) element;
				return dh.getCategory();
			}
			return null;
		}
		
		public boolean hasChildren(Object element){
			if (element instanceof IDocumentHandle) {
				IDocumentHandle dh = (IDocumentHandle) element;
				return dh.isCategory();
			}
			return false;
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		
		public String getColumnText(Object obj, int index){
			IDocumentHandle dh = (IDocumentHandle) obj;
			switch (index) {
			case 0:
				return ""; //$NON-NLS-1$
			case 1:
				if (bFlat)
					return dh.getCategory().getName();
				return dh.isCategory() ? dh.getTitle() : ""; //$NON-NLS-1$
			case 2:
				return dh.isCategory() ? "" : dateFormat.format(dh.getLastchanged()); //$NON-NLS-1$
			case 3:
				return dh.isCategory() ? "" : dateFormat.format(dh.getCreated()); //$NON-NLS-1$
			case 4:
				return dh.isCategory() ? "" : dh.getTitle(); //$NON-NLS-1$
			case 5:
				return dh.isCategory() ? "" : dh.getKeywords(); //$NON-NLS-1$
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
	
	/**
	 * The constructor.
	 */
	public OmnivoreView(){
		
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout(4, false));
		
		// Title search field
		Label lSearchTitle = new Label(parent, SWT.NONE);
		lSearchTitle.setText(Messages.OmnivoreView_searchTitleLabel);
		lSearchTitle.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text tSearchTitle = new Text(parent, SWT.SINGLE);
		tSearchTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		// Keyword search field
		Label lSearchKW = new Label(parent, SWT.NONE);
		lSearchKW.setText(Messages.OmnivoreView_searchKeywordsLabel);
		lSearchKW.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text tSearchKW = new Text(parent, SWT.SINGLE);
		tSearchKW.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		// Add search listener
		ModifyListener searchListener = new ModifyListener() {
			public void modifyText(ModifyEvent e){
				searchKW = tSearchKW.getText();
				searchTitle = tSearchTitle.getText();
				viewer.refresh();
			}
		};
		tSearchTitle.addModifyListener(searchListener);
		tSearchKW.addModifyListener(searchListener);
		
		// Table to display documents
		table = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		TreeColumn[] cols = new TreeColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TreeColumn(table, SWT.NONE);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
		}
		applyUsersColumnWidthSetting();
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		viewer = new TreeViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.addSelectionChangedListener(ev -> {
			IDocumentHandle docHandle =
				(IDocumentHandle) ev.getStructuredSelection().getFirstElement();
			if (docHandle != null && !docHandle.isCategory()) {
				if (StringUtils.containsIgnoreCase(docHandle.getMimeType(), "pdf")) {
					try (InputStream content = docHandle.getContent()) {
						eventBroker.post(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, content);
					} catch (IOException e1) {
						LoggerFactory.getLogger(getClass()).warn("Exception", e1);
					}
				}
			}
		});
		makeActions();
		
		ovComparator = new OmnivoreViewerComparator();
		viewer.setComparator(ovComparator);
		TreeColumn[] treeCols = viewer.getTree().getColumns();
		for (int i = 0; i < treeCols.length; i++) {
			TreeColumn tc = treeCols[i];
			tc.addSelectionListener(getSelectionAdapter(tc, i));
		}
		applySortDirection();
		
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		final Transfer[] dropTransferTypes = new Transfer[] {
			FileTransfer.getInstance()
		};
		
		viewer.addDropSupport(DND.DROP_COPY, dropTransferTypes, new DropTargetAdapter() {
			
			@Override
			public void dragEnter(DropTargetEvent event){
				event.detail = DND.DROP_COPY;
			}
			
			@Override
			public void drop(DropTargetEvent event){
				if (dropTransferTypes[0].isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					String category = null;
					if (event.item != null && event.item.getData() instanceof IDocumentHandle) {
						IDocumentHandle dh = (IDocumentHandle) event.item.getData();
						category = dh.getCategory().getName();
					}
					for (String file : files) {
						final IDocumentHandle handle = UiUtils.assimilate(file, category);
						// do automatic billing if configured
						if (AutomaticBilling.isEnabled() && handle != null) {
							AutomaticBilling billing = new AutomaticBilling(handle);
							billing.bill();
						}
						viewer.refresh();
					}
				}
			}
			
		});
		
		final Transfer[] dragTransferTypes = new Transfer[] {
			FileTransfer.getInstance(), TextTransfer.getInstance()
		};
		viewer.addDragSupport(DND.DROP_COPY, dragTransferTypes, new DragSourceAdapter() {
			@Override
			public void dragStart(DragSourceEvent event){
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				for (Object object : selection.toList()) {
					IDocumentHandle dh = (IDocumentHandle) object;
					if (dh.isCategory()) {
						event.doit = false;
					}
				}
			}
			
			@Override
			public void dragSetData(DragSourceEvent event){
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				
				if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
					String[] files = new String[selection.size()];
					for (int index = 0; index < selection.size(); index++) {
						IDocumentHandle dh = (IDocumentHandle) selection.toList().get(index);
						File file = Utils.createTemporaryFile(dh, dh.getTitle());
						files[index] = file.getAbsolutePath();
						log.debug("dragSetData; isSupportedType {} data {}", file.getAbsolutePath(), //$NON-NLS-1$
							event.data);
					}
					event.data = files;
				} else {
					StringBuilder sb = new StringBuilder();
					for (int index = 0; index < selection.size(); index++) {
						IDocumentHandle dh = (IDocumentHandle) selection.toList().get(index);
						sb.append(StoreToStringServiceHolder.getStoreToString(dh)).append(","); //$NON-NLS-1$
						log.debug("dragSetData; unsupported dataType {} returning {}", //$NON-NLS-1$
							event.dataType, sb.toString().replace(",$", ""));
					}
					event.data = sb.toString().replace(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
		viewer.setInput(getViewSite());
		getSite().getPage().addPartListener(udpateOnVisible);
		getSite().setSelectionProvider(viewer);
	}
	
	private SelectionListener getSelectionAdapter(final TreeColumn column, final int index){
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				ovComparator.setColumn(index);
				ovComparator.setFlat(bFlat);
				viewer.getTree().setSortDirection(ovComparator.getDirection());
				viewer.getTree().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}
	
	private void applySortDirection(){
		String[] usrSortSettings = sortSettings.split(",");
		
		if (ConfigServiceHolder.getUser(PreferencePage.SAVE_SORT_DIRECTION, false)) {
			String sortSet = ConfigServiceHolder.getUser(PreferencePage.USR_SORT_DIRECTION_SETTINGS,
				sortSettings);
			usrSortSettings = sortSet.split(",");
		}
		
		int propertyIdx = Integer.parseInt(usrSortSettings[0]);
		int direction = Integer.parseInt(usrSortSettings[1]);
		int catDirection = Integer.parseInt(usrSortSettings[2]);
		bFlat = Boolean.valueOf(usrSortSettings[3]);
		
		flatViewAction.setChecked(bFlat);
		if (!bFlat) {
			if (catDirection != -1) {
				sortViewer(1, catDirection);
				ovComparator.setCategoryDirection(catDirection);
			}
		}
		
		if (propertyIdx != 0) {
			sortViewer(propertyIdx, direction);
		}
		
	}
	
	private void sortViewer(int propertyIdx, int direction){
		TreeColumn column = viewer.getTree().getColumn(propertyIdx);
		ovComparator.setColumn(propertyIdx);
		ovComparator.setDirection(direction);
		ovComparator.setFlat(bFlat);
		viewer.getTree().setSortDirection(ovComparator.getDirection());
		viewer.getTree().setSortColumn(column);
		viewer.refresh();
	}
	
	private void applyUsersColumnWidthSetting(){
		TreeColumn[] treeColumns = table.getColumns();
		String[] userColWidth = colWidth.split(",");
		if (ConfigServiceHolder.getUser(PreferencePage.SAVE_COLUM_WIDTH, false)) {
			String ucw =
				ConfigServiceHolder.getUser(PreferencePage.USR_COLUMN_WIDTH_SETTINGS, colWidth);
			userColWidth = ucw.split(",");
		}
		
		for (int i = 0; i < treeColumns.length && (i < userColWidth.length); i++) {
			treeColumns[i].setWidth(Integer.parseInt(userColWidth[i]));
		}
	}
	
	@Override
	public void dispose(){
		getSite().getPage().removePartListener(udpateOnVisible);
		saveSortSettings();
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
		importAction = new RestrictedAction(AccessControlDefaults.DOCUMENT_CREATE,
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
					final IDocumentHandle handle = UiUtils.assimilate(filename);
					viewer.refresh();
				}
			}
		};
		
		deleteAction = new LockRequestingRestrictedAction<IDocumentHandle>(
			AccessControlDefaults.DOCUMENT_DELETE, Messages.OmnivoreView_deleteActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_deleteActionToolTip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			@Override
			public IDocumentHandle getTargetedObject(){
				ISelection selection = viewer.getSelection();
				return (IDocumentHandle) ((IStructuredSelection) selection).getFirstElement();
			}
			
			@Override
			public void doRun(IDocumentHandle dh){
				if (dh.isCategory()) {
					if (CoreHub.acl.request(AccessControlDefaults.DOCUMENT_CATDELETE)) {
						ListDialog ld = new ListDialog(getViewSite().getShell());
						
						IQuery<IDocumentHandle> qbe =
							OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
						qbe.and("mimetype", COMPARATOR.EQUALS, CATEGORY_MIMETYPE);
						qbe.and("id", COMPARATOR.NOT_EQUALS, dh.getId());
						List<IDocumentHandle> mainCategories = qbe.execute();
						
						ld.setInput(mainCategories);
						ld.setContentProvider(ArrayContentProvider.getInstance());
						ld.setLabelProvider(new DefaultLabelProvider());
						ld.setTitle(MessageFormat.format("Kategorie {0} löschen", dh.getLabel()));
						ld.setMessage(
							"Geben Sie bitte an, in welche andere Kategorie die Dokumente dieser Kategorie verschoben werden sollen");
						int open = ld.open();
						if (open == Dialog.OK) {
							Object[] selection = ld.getResult();
							if (selection != null && selection.length > 0) {
								String label = ((IDocumentHandle) selection[0]).getLabel();
								CategoryUtil.removeCategory(dh.getLabel(), label);
							}
							viewer.refresh();
						}
					} else {
						SWTHelper.showError("Insufficient Rights",
							"You have insufficient rights to delete document categories");
					}
				} else {
					if (SWTHelper.askYesNo(Messages.OmnivoreView_reallyDeleteCaption, MessageFormat
						.format(Messages.OmnivoreView_reallyDeleteContents, dh.getTitle()))) {
						OmnivoreModelServiceHolder.get().delete(dh);
						viewer.refresh();
					}
				}
			};
		};
		
		editAction = new LockRequestingRestrictedAction<IDocumentHandle>(
			AccessControlDefaults.DOCUMENT_DELETE, Messages.OmnivoreView_editActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_editActionTooltip);
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			@Override
			public IDocumentHandle getTargetedObject(){
				ISelection selection = viewer.getSelection();
				return (IDocumentHandle) ((IStructuredSelection) selection).getFirstElement();
			}
			
			@Override
			public void doRun(IDocumentHandle dh){
				if (dh.isCategory()) {
					if (CoreHub.acl.request(AccessControlDefaults.DOCUMENT_CATDELETE)) {
						
						InputDialog id = new InputDialog(getViewSite().getShell(),
							MessageFormat.format("Kategorie {0} umbenennen.", dh.getLabel()),
							"Geben Sie bitte einen neuen Namen für die Kategorie ein",
							dh.getLabel(), null);
						if (id.open() == Dialog.OK) {
							String nn = id.getValue();
							CategoryUtil.renameCategory(dh.getTitle(), nn);
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
				IDocumentHandle dh = (IDocumentHandle) obj;
				if (dh.isCategory()) {
					if (viewer.getExpandedState(dh)) {
						viewer.collapseToLevel(dh, TreeViewer.ALL_LEVELS);
					} else {
						viewer.expandToLevel(dh, TreeViewer.ALL_LEVELS);
					}
				} else {
					UiUtils.open(dh);
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
				IDocumentHandle dh = (IDocumentHandle) obj;
				String mime = dh.getMimeType();
				FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
				fd.setFileName(mime);
				String fname = fd.open();
				if (fname != null) {
					if (!Utils.storeExternal(dh, fname)) {
						SWTHelper.showError(Messages.OmnivoreView_configErrorCaption,
							Messages.OmnivoreView_configErrorText);
					}
				}
			}
		};
		
		flatViewAction = new Action(Messages.OmnivoreView_flatActionCaption, Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
				setToolTipText(Messages.OmnivoreView_flatActionTooltip);
			}
			
			public void run(){
				bFlat = isChecked();
				viewer.refresh();
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
		refresh();
	}
	
	private void saveColumnWidthSettings(){
		TreeColumn[] treeColumns = viewer.getTree().getColumns();
		StringBuilder sb = new StringBuilder();
		for (TreeColumn tc : treeColumns) {
			sb.append(tc.getWidth());
			sb.append(",");
		}
		ConfigServiceHolder.setUser(PreferencePage.USR_COLUMN_WIDTH_SETTINGS, sb.toString());
	}
	
	private void saveSortSettings(){
		int propertyIdx = ovComparator.getPropertyIndex();
		int direction = ovComparator.getDirectionDigit();
		int catDirection = ovComparator.getCategoryDirection();
		ConfigServiceHolder.setUser(PreferencePage.USR_SORT_DIRECTION_SETTINGS,
			propertyIdx + "," + direction + "," + catDirection + "," + bFlat);
	}
	
	public void refresh(){
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
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
	
	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState){
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}