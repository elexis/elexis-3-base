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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
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

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
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
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * A class do receive documents by drag&drop. Documents are imported into the
 * database and linked to the selected patient. On double-click they are opened
 * with their associated application.
 *
 * 20260108js: The first column with empty header and empty rows exists to provide room for open/close-branch icons in the non-flat view.
 * 20260108js: It does however exist and remain visible in a completely empty state when the flat-view is activated.
 */

public class OmnivoreView extends ViewPart implements IRefreshable {
	private TreeViewer viewer;
	private Tree table;
	RestrictedAction editAction, deleteAction, importAction;
	public static String importAction_ID = "ch.elexis.omnivore.data.OmnivoreView.importAction"; //$NON-NLS-1$

	private Action exportAction;
	private Action doubleClickAction;
	private Action flatViewAction;
	
	//20260108js Auto formatted line breaks at non-sensical points are very unhelpful when checking column widths or sortSettings against columnNames.
	//20260108js Attempts to review and understand code for quality assurance (over adherence to auto formatting rules) will constantly be hindered by this.
	//20260108js For now, I tried to lay out the column names on one line per item.
	//20260108js I do however understand that auto formatting (or some human with similar priorities) will scramble that again and again, probably with the local save/load or the next upload to github.

	//20260108js The fact that an addition of a width was forgotton just ONE LINE DIRECTLY BELOW where a new colLabels String was added, should prove my point without a doubt.
	//20260108js That this error prevailed in a published version (for how many revisions?) proves that build time testing is cannot fix low code readability & comprehensibility & lack of comments.
	
	//TODO: 20260108js Please ensure that automatic code formatting (or humans with similar priorities) do NOT impair the quick comprehensibility (!!!) of code by inserting line breaks at fixed lengths.
	//TODO: 20260108js We all have 16:9 or 16:10 screens, my comments fit on these lines when I write them, and even I haven't used a 24-needle printer and perforated endless paper for years.
	
	//TODO: 20260108js AND IF you should EVER be re-considering auto formatting styles, please note that Pascal-like bracketing & indentation is much less error prone than Java/C/etc.-like b&i.
	//TODO: 20260108js I.e. the opening and closing curly brackets could be placed into the same column as the first character of each line at the same level within a given block.
	//TODO: 20260108js I have experienced examples where bracketing was messed up, producing erroneous code, right in this project when "freeing up space = removing comments".
		
	//20260108js: REMINDER: Whenever you add a colLabels entry here, you MUST add a colWidth and probably a sortSettings entry below.
	//20260108js: REMINDER: Or else, your omnivore view will fail to display all columns. As definitely shown in Elexis 3.13 (and possibly in other versions >= 3.8). 
	//20260108js: NOTE: That has now been mitigated; at least some degree of omissions here or in old config entries may be auto-fixed by code further below.  
	private final String[] colLabels = { StringUtils.EMPTY,
										 Messages.OmnivoreView_categoryColumn,
										 Messages.OmnivoreView_dateColumn,
										 Messages.OmnivoreView_dateOriginColumn, 
										 Messages.OmnivoreView_titleColumn,
										 Messages.OmnivoreView_keywordsColumn };
	
	//20260108js: CAVE: What follows is a highly error prone way to prepare an array of constants:
	//20260108js: CAVE: You MUST NOT add ANY SPACES or non-numeric chars except for the comma inside this String!
	//20260108js: OR you'll catch hundreds of lines on the console signaling a few "Number format exception"s after conversion to an array of (supposed) integers below.
	//20260108js: Which, of course, completely kills the construction of this view.
	//20260108js: That's Java and provocation of coding errors at its best.
	
	//TODO: 20260108js: You might consider refactoring this code, and all similar constructs, to some direct definition of arrays colWidth[] and sortSettings[] right from the start.
	//TODO: 20260108js: I see however that this might pose a large task - given that user preferences are stored in strings similar to this constant, and overlaid whenever they're flagged to be used.
	
	//20260108js: Original error inserted between 3.8 and 3.13; breaking omnivore: Inserting an additional width was forgotten when inserting OmnivoreView_dateOriginColumn above.
	//20260108js: I've inserted (!) the missing value here; AND also added code further below to harden this program against future similar errors.
	private final String colWidth = 	"20,80,80,80,150,500"; //$NON-NLS-1$
	//TODO: 20260108js: This MIGHT still fail - I observed that when a two element constant here and a three element constant from PreferencePage were used together,
	//TODO: 20260108js: even when all added hardenings did apparently work as expected. But that's a scenario so improbable in reality that I won't spend any more time on it right now.
	
	//20260108js: Inserting an additional sort setting was ALSO forgotten when inserting OmnivoreView_dateOriginColumn
	//20260108js: CAVE: I have just fixed this here; and NOT checked whether this oblivion might have caused separate undesired effects; NEITHER added ANY hardening/or handling of missing values below.   
	//20260108js: I don't understand why 0 and false were used in the same set of constants. So I chose the shorter one.
	private final String sortSettings = "0,1,1,-1,0,0"; //$NON-NLS-1$	//20260108js: Inserting an additional sort setting was probably forgotten when inserting OmnivoreView_dateOriginColumn
	//TODO: 20260108js: Check whether related corrections are required in other files.
	
	private boolean bFlat = false;
	private String searchTitle = StringUtils.EMPTY;
	private String searchKW = StringUtils.EMPTY;
	// ISource selectedSource = null;
	static Logger log = LoggerFactory.getLogger(OmnivoreView.class);

	private OmnivoreViewerComparator ovComparator;

	private IPatient actPatient;

	@Inject
	private IEventBroker eventBroker;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this) {
		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
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
	private boolean isActiveControl(Control control) {
		return control != null && !control.isDisposed() && control.isVisible();
	}

	@Inject
	void activePatient(@Optional IPatient patient) {
		Display.getDefault().asyncExec(() -> {
			if (isActiveControl(table)) {
				viewer.refresh();
				actPatient = patient;
			}
		});
	}

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (isActiveControl(table)) {
				viewer.refresh();
				importAction.reflectRight();
				editAction.reflectRight();
				deleteAction.reflectRight();
			}
		});
	}

	@Optional
	@Inject
	void updateDocHandle(@UIEventTopic(ElexisEventTopics.EVENT_UPDATE) IDocumentHandle dochandle) {
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}

	@Inject
	void createDocHandle(@Optional @UIEventTopic(ElexisEventTopics.EVENT_CREATE) IDocumentHandle dochandle) {
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}

	@Inject
	void deleteDocHandle(@Optional @UIEventTopic(ElexisEventTopics.EVENT_DELETE) IDocumentHandle dochandle) {
		if (isActiveControl(table)) {
			viewer.refresh();
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		/** Add filters for search to query */
		private void addFilters(IQuery<IDocumentHandle> qbe) {
			qbe.and("title", COMPARATOR.LIKE, "%" + searchTitle + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// Add every keyword
			for (String kw : searchKW.split(StringUtils.SPACE)) {
				if(StringUtils.isNotBlank(kw)) {
					qbe.and("keywords", COMPARATOR.LIKE, "%" + kw + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}

		private boolean filterMatches(String[] kws, IDocumentHandle h) {
			//TODO: 20260108js: Check if searches in Title and Keywords (="Titel" und "Stichwörter" in DE) fields are processed in equivalent ways.
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
		private List<IDocumentHandle> filterList(List<IDocumentHandle> list) {
			List<IDocumentHandle> result = new LinkedList<IDocumentHandle>();
			String[] kws = searchKW.toLowerCase().split(StringUtils.SPACE);
			for (IDocumentHandle dh : list) {
				if (filterMatches(kws, dh))
					result.add(dh);
			}
			return result;
		}

		@Override
		public Object[] getElements(Object parent) {
			List<IDocumentHandle> ret = new LinkedList<IDocumentHandle>();
			IPatient pat = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (!bFlat && pat != null) {
				List<IDocumentHandle> cats = CategoryUtil.getCategories();
				for (IDocumentHandle dh : cats) {
					if (filterList(Utils.getMembers(dh, pat)).size() > 0) {
						ret.add(dh);
					}
				}
				IQuery<IDocumentHandle> qbe = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
				qbe.and("kontakt", COMPARATOR.EQUALS, pat); //$NON-NLS-1$
				addFilters(qbe);
				List<IDocumentHandle> root = qbe.execute().parallelStream().filter(d -> d.isCategory())
						.collect(Collectors.toList());
				ret.addAll(root);
			} else if (pat != null) {
				// Flat view -> all documents that
				IQuery<IDocumentHandle> qbe = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
				qbe.and("kontakt", COMPARATOR.EQUALS, pat); //$NON-NLS-1$
				addFilters(qbe);
				List<IDocumentHandle> docs = qbe.execute().parallelStream().filter(d -> !d.isCategory())
						.collect(Collectors.toList());
				ret.addAll(docs);
			}
			return ret.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			IPatient pat = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (!bFlat && pat != null && (parentElement instanceof IDocumentHandle)) {
				IDocumentHandle dhParent = (IDocumentHandle) parentElement;
				return filterList(Utils.getMembers(dhParent, pat)).toArray();
			} else {
				return new Object[0];
			}
		}

		@Override
		public Object getParent(Object element) {
			if (!bFlat && element instanceof IDocumentHandle) {
				IDocumentHandle dh = (IDocumentHandle) element;
				return dh.getCategory();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof IDocumentHandle) {
				IDocumentHandle dh = (IDocumentHandle) element;
				return dh.isCategory();
			}
			return false;
		}
	}
	
	class ViewLabelProvider extends ColumnLabelProvider implements IToolTipProvider {

		private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			int columnIndex = cell.getColumnIndex();
			if (!(element instanceof IDocumentHandle)) {
				cell.setText(StringUtils.EMPTY);
				return;
			}
			IDocumentHandle dh = (IDocumentHandle) element;
			if (dh.isCategory()) {
				cell.setText(columnIndex == 1 ? dh.getCategory().getName() : StringUtils.EMPTY);
				return;
			}
			switch (columnIndex) {
			//20260108js: The first column with empty header and empty rows exists to provide room for open/close-branch icons in the non-flat view.
			//20260108js: It does however exist and remain visible in a completely empty state when the flat-view is activated.
			case 0:
				cell.setText(StringUtils.EMPTY);
				break;
			case 1:
				cell.setText(bFlat ? dh.getCategory().getName() : StringUtils.EMPTY);
				break;
			case 2:
				cell.setText(dateFormat.format(dh.getLastchanged()));
				break;
			case 3:
				cell.setText(dateFormat.format(dh.getCreated()));
				break;
			case 4:
				cell.setText(dh.getTitle());
				break;
			case 5:
				cell.setText(dh.getKeywords());
				break;
			default:
				cell.setText(StringUtils.EMPTY);
				break;
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null; // getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		@Override
		public String getToolTipText(Object element) {
			if (element instanceof IDocumentHandle) {
				IDocumentHandle docHandle = (IDocumentHandle) element;
				if (!docHandle.isCategory()) {
					String docId = docHandle.getId();
					String objectToSearch = "ch.elexis.omnivore.data.DocHandle::" + docId;
					IQuery<IInboxElement> inboxQuery = InboxServiceHolder.getModelService()
							.getQuery(IInboxElement.class);
					inboxQuery.and("object", COMPARATOR.EQUALS, objectToSearch);
					try (IQueryCursor<IInboxElement> cursor = inboxQuery.executeAsCursor()) {
						while (cursor.hasNext()) {
							IInboxElement inboxElement = cursor.next();
							String inboxElementObjectString = inboxElement.getObject().toString();
							if (inboxElementObjectString.contains(docId)) {
								State state = inboxElement.getState();
								if (state == State.SEEN) {
									String mandatorCode = inboxElement.getMandator().getCode();
									String lastUpdateFormatted = formatLastUpdate(inboxElement.getLastupdate(),
											mandatorCode);
									return "Gesehen " + lastUpdateFormatted;
								} else {
									return "Nicht gesehen";
								}
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			return null;
		}

		private String formatLastUpdate(Long timestamp, String mandatorCode) {
			Date date = new Date(timestamp);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			return sdf.format(date) + " " + mandatorCode;
		}
	}

	/**
	 * The constructor.
	 */
	public OmnivoreView() {

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
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
			@Override
			public void modifyText(ModifyEvent e) {
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
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		viewer.addSelectionChangedListener(ev -> {
			IDocumentHandle docHandle = (IDocumentHandle) ev.getStructuredSelection().getFirstElement();
			if (docHandle != null && !docHandle.isCategory()) {
				eventBroker.post(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, docHandle);
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
		final Transfer[] dropTransferTypes = new Transfer[] { FileTransfer.getInstance() };

		viewer.addDropSupport(DND.DROP_COPY, dropTransferTypes, new DropTargetAdapter() {

			@Override
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			@Override
			public void drop(DropTargetEvent event) {
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

		final Transfer[] dragTransferTypes = new Transfer[] { FileTransfer.getInstance(), TextTransfer.getInstance() };
		viewer.addDragSupport(DND.DROP_COPY, dragTransferTypes, new DragSourceAdapter() {
			@Override
			public void dragStart(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				for (Object object : selection.toList()) {
					IDocumentHandle dh = (IDocumentHandle) object;
					if (dh.isCategory()) {
						event.doit = false;
					}
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				//TODO: 20260108js: Check if some corresponding getKeywords() has been lost here; compare with improved drag/drop support from 2.1.7js and 3.7js
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
								event.dataType, sb.toString().replace(",$", StringUtils.EMPTY)); //$NON-NLS-1$
					}
					event.data = sb.toString().replace(",$", StringUtils.EMPTY); //$NON-NLS-1$
				}
			}
		});
		viewer.setInput(getViewSite());
		getSite().getPage().addPartListener(udpateOnVisible);
		getSite().setSelectionProvider(viewer);
	}

	private SelectionListener getSelectionAdapter(final TreeColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ovComparator.setColumn(index);
				ovComparator.setFlat(bFlat);
				viewer.getTree().setSortDirection(ovComparator.getDirection());
				viewer.getTree().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	private void applySortDirection() {
		String[] usrSortSettings = sortSettings.split(","); //$NON-NLS-1$

		if (ConfigServiceHolder.getUser(PreferencePage.SAVE_SORT_DIRECTION, false)) {
			String sortSet = ConfigServiceHolder.getUser(PreferencePage.USR_SORT_DIRECTION_SETTINGS, sortSettings);
			usrSortSettings = sortSet.split(","); //$NON-NLS-1$
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

	private void sortViewer(int propertyIdx, int direction) {
		TreeColumn column = viewer.getTree().getColumn(propertyIdx);
		ovComparator.setColumn(propertyIdx);
		ovComparator.setDirection(direction);
		ovComparator.setFlat(bFlat);
		viewer.getTree().setSortDirection(ovComparator.getDirection());
		viewer.getTree().setSortColumn(column);
		viewer.refresh();
	}

	//20260108js: Please DO ensure that userColWidth contains enough entries for all columns, 
	//20260108js: BOTH IF they're obtained from the constant array above, OR through ConfigServiceHolder.
	//20260108js: CAVE: AS LONG AS String[] is constructed from colWidth.split(",") instead of being directly defined as a constant of arrays: colWidth must NOT contain ANY chars but 0..9+-,  
	private void applyUsersColumnWidthSetting() {
		TreeColumn[] treeColumns = table.getColumns();
		String[] userColWidth = colWidth.split(","); //$NON-NLS-1$
		
		//20260108js: Eclipse debugging stepwise execution does not work as expected.
		//20260108js: And results aren't always as expected, either. So: Doing it the old way, time and again around here. 
		//System.out.println("String colWidth: "+colWidth);
		//System.out.println("String[] userColWidth from local constant String: "+userColWidth);
		//System.out.println("userColWidth.length from local constant String:   "+userColWidth.length);
		//System.out.println("String[] treeColumns: "+treeColumns);
		//System.out.println("treeColumns.length:   "+treeColumns.length);
		
		if (ConfigServiceHolder.getUser(PreferencePage.SAVE_COLUM_WIDTH, false)) {
			String ucw = ConfigServiceHolder.getUser(PreferencePage.USR_COLUMN_WIDTH_SETTINGS, colWidth);
			//20260108js: Ensure that the required number of additional entries with reasonable (above zero!) values are available,
			//20260108js: even when the userColWidth string from PreferencePage covers fewer columns - e.g. after an upgrade from Elexis 3.7 (or maybe more) to 3.13 ff.
			//20260108js: And don't just make additionally needed ones up - but copy them from what's defined in the local constant String colWidth.
			//System.out.println("ucw: "+ucw);
			String[] userColWidthFromPP = ucw.split(","); //$NON-NLS-1$
			if ( userColWidthFromPP.length < userColWidth.length ) {
				for (int i = userColWidthFromPP.length; i < userColWidth.length; i++ ) {
					ucw = ucw.concat(","+userColWidth[i]);
					//System.out.println("ucw: "+ucw);
				}
			}
			//System.out.println("ucw: "+ucw);
			userColWidth = ucw.split(","); //$NON-NLS-1$
		}
		
		//20260108js: While we're at it... let's make this robust against people forgetting to add local colWidth entries after adding treeColumns entries above (in the future, again).
		//20260108js: And let's do that BEFORE the final for-loop, so that the extended userColWidth will be stored in user preferences later on and work normally in the future.
		//
		//20260108js: The implementation is super clumsy, but it fits the existing variables and approaches and requires only local additions in the code. Plus, efficiency doesn't matter here at all.
		if ( userColWidth.length < treeColumns.length ) {	//20260108js: Can happen, as seen in 3.13, when treeColumns entry is added but adding a colWidth entry is forgotten.
			String availColWidths = "";
			//System.out.println("availColWidths: "+availColWidths);
			for (int i = 0; i < userColWidth.length; i++ ) {
				availColWidths = availColWidths.concat(userColWidth[i]+",");
				//System.out.println("availColWidths: "+availColWidths);
			}
			for (int i = userColWidth.length; i < treeColumns.length; i++ ) {
				availColWidths = availColWidths.concat(userColWidth[0]);	//20260108js: use the constant width intended for the first (very narrow) column to generate any forgotten widths. 
				if ( i < treeColumns.length -1 ) availColWidths = availColWidths.concat(",");
				//System.out.println("availColWidths: "+availColWidths);
			}
			//System.out.println("availColWidths: "+availColWidths);
			userColWidth = availColWidths.split(",");
		}

		//System.out.println("userColWidth.length: "+userColWidth.length);
		//20260108js: CAVE: Just limiting the following for-loop applying widths to treeColumns[i] by ... && (i < userColWidth.length) does protect from an access outside of array bounds -
		//20260108js: but it does NOT protect from a column with undefined (or zero) width, resulting in some users painfully missing the last column(s) in the display.
		//20260108js: The previous fixes may make this additional condition now technically unnecessary.
		//20260108js: But I don't remove it, just in case an overzealous person removes my preceding code-hardenings again.
		
		//20260108js: (And... up to here, I haven't even *looked* at possible consequences of possibly similarly forgotten entries to sortSettings... BUT I've already added a missing one, and cleaned all.)
		//TODO: 20260108js: The last comment should probably be moved to some place where sortSettings... is actually being put to use. And missing entries could cause trouble that's hard to debug. 

		for (int i = 0; ( (i < treeColumns.length) && (i < userColWidth.length) ); i++) {	//20260108js: Fixed missing first set of brackets. Hmm.
			//System.out.println("i: "+i);
			//System.out.println("userColWidth[i]: "+userColWidth[i]);
			//System.out.println("Integer.parseInt(userColWidth[i]): "+Integer.parseInt(userColWidth[i]));
			//System.out.println("treeColumns[i].getWidth(): "+treeColumns[i].getWidth());
			//System.out.println("About to treeColumns["+i+"].setWidth("+userColWidth[i]+")...");
			treeColumns[i].setWidth(Integer.parseInt(userColWidth[i]));
			//System.out.println("treeColumns[i].getWidth(): "+treeColumns[i].getWidth());
		}
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		saveSortSettings();
		super.dispose();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				OmnivoreView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager mnSources = new MenuManager(Messages.OmnivoreView_dataSources);
		manager.add(importAction);
	}

	private void fillContextMenu(IMenuManager manager) {

		manager.add(editAction);
		manager.add(deleteAction);

		// manager.add(action2);
		// Other plug-ins can contribute there actions here
		// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(importAction);
		manager.add(exportAction);
		manager.add(flatViewAction);
	}

	private void makeActions() {
		importAction = new RestrictedAction(EvACE.of(IDocumentHandle.class, Right.CREATE),
				Messages.OmnivoreView_importActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_importActionToolTip);
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}

			@Override
			public void doRun() {
				if (ElexisEventDispatcher.getSelectedPatient() == null)
					return;
				FileDialog fd = new FileDialog(getViewSite().getShell(), SWT.OPEN);
				String filename = fd.open();
				if (filename != null) {
					final IDocumentHandle handle = UiUtils.assimilate(filename);
					viewer.refresh();
					if (handle != null && handle.getContentLength() < 1) {
						SWTHelper.showError(Messages.OmnivoreView_importActionToolTip,
								"Beim Import ist eine Fehler aufgetreten. Bitte überprüfen sie die omnivore Einstellungen, bzw. freien Speicherplatz.");
						OmnivoreModelServiceHolder.get().remove(handle);
					}
				}
			}
		};

		deleteAction = new LockRequestingRestrictedAction<IDocumentHandle>(EvACE.of(IDocumentHandle.class, Right.DELETE),
				Messages.OmnivoreView_deleteActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_deleteActionToolTip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}

			@Override
			public IDocumentHandle getTargetedObject() {
				ISelection selection = viewer.getSelection();
				return (IDocumentHandle) ((IStructuredSelection) selection).getFirstElement();
			}

			@Override
			public void doRun(IDocumentHandle dh) {
				if (dh.isCategory()) {
					if (AccessControlServiceHolder.get().evaluate(EvACE.of(IDocumentHandle.class, Right.DELETE).and(Right.EXECUTE))) {
						ListDialog ld = new ListDialog(getViewSite().getShell());

						IQuery<IDocumentHandle> qbe = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
						qbe.and("mimetype", COMPARATOR.EQUALS, CATEGORY_MIMETYPE); //$NON-NLS-1$
						qbe.and("id", COMPARATOR.NOT_EQUALS, dh.getId()); //$NON-NLS-1$
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
					//TODO: 20260108js: Check if some corresponding getKeywords() has been lost here; compare with improved drag/drop support from 2.1.7js and 3.7js
					if (SWTHelper.askYesNo(Messages.OmnivoreView_reallyDeleteCaption,
							MessageFormat.format(Messages.OmnivoreView_reallyDeleteContents, dh.getTitle()))) {
						OmnivoreModelServiceHolder.get().delete(dh);
						viewer.refresh();
					}
				}
			};
		};

		editAction = new LockRequestingRestrictedAction<IDocumentHandle>(EvACE.of(IDocumentHandle.class, Right.UPDATE),
				Messages.OmnivoreView_editActionCaption) {
			{
				setToolTipText(Messages.OmnivoreView_editActionTooltip);
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}

			@Override
			public IDocumentHandle getTargetedObject() {
				ISelection selection = viewer.getSelection();
				return (IDocumentHandle) ((IStructuredSelection) selection).getFirstElement();
			}

			@Override
			public void doRun(IDocumentHandle dh) {
				if (dh.isCategory()) {
					//TODO: 20260108js: Check if some corresponding getKeywords() has been lost here; compare with improved drag/drop support from 2.1.7js and 3.7js
					if (AccessControlServiceHolder.get().evaluate(EvACE.of(IDocumentHandle.class, Right.UPDATE).and(Right.EXECUTE))) {

						InputDialog id = new InputDialog(getViewSite().getShell(),
								MessageFormat.format("Kategorie {0} umbenennen.", dh.getLabel()),
								"Geben Sie bitte einen neuen Namen für die Kategorie ein", dh.getLabel(), null);
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
			@Override
			public void run() {
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

			@Override
			public void run() {
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

			@Override
			public void run() {
				bFlat = isChecked();
				viewer.refresh();
			}
		};
	};

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
		refresh();
	}

	private void saveColumnWidthSettings() {
		TreeColumn[] treeColumns = viewer.getTree().getColumns();
		StringBuilder sb = new StringBuilder();
		for (TreeColumn tc : treeColumns) {
			sb.append(tc.getWidth());
			sb.append(","); //$NON-NLS-1$
		}
		ConfigServiceHolder.setUser(PreferencePage.USR_COLUMN_WIDTH_SETTINGS, sb.toString());
	}

	private void saveSortSettings() {
		int propertyIdx = ovComparator.getPropertyIndex();
		int direction = ovComparator.getDirectionDigit();
		int catDirection = ovComparator.getCategoryDirection();
		ConfigServiceHolder.setUser(PreferencePage.USR_SORT_DIRECTION_SETTINGS,
				propertyIdx + "," + direction + "," + catDirection + "," + bFlat); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.omnivoredirect", path); //$NON-NLS-1$
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}