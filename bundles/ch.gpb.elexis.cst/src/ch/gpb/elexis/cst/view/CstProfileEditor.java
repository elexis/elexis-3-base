/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.gpb.elexis.cst.Activator;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstGastroColo;
import ch.gpb.elexis.cst.data.CstGroup;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.CstProimmun;
import ch.gpb.elexis.cst.dialog.CstCopyProfileDialog;
import ch.gpb.elexis.cst.dialog.CstGroupSelectionDialog;
import ch.gpb.elexis.cst.dialog.CstNewProfileDialog;
import ch.gpb.elexis.cst.dialog.ProfileDetailDialog;
import ch.gpb.elexis.cst.service.CstService;
import ch.gpb.elexis.cst.view.profileeditor.AnzeigeOptionsComposite;
import ch.gpb.elexis.cst.view.profileeditor.BefundSelectionComposite;
import ch.gpb.elexis.cst.view.profileeditor.CstDocumentsComposite;
import ch.gpb.elexis.cst.view.profileeditor.DateRangeComposite;
import ch.gpb.elexis.cst.view.profileeditor.GastroComposite;
import ch.gpb.elexis.cst.view.profileeditor.HilfeComposite;
import ch.gpb.elexis.cst.view.profileeditor.ProImmunComposite;
import ch.gpb.elexis.cst.view.profileeditor.RemindersComposite;
import ch.gpb.elexis.cst.view.profileeditor.TemplateComposite;
import ch.gpb.elexis.cst.view.profileeditor.TherapieVorschlagComposite;
//import ch.gpb.elexis.cst.widget.ResultatPartEffektiv;
//import ch.gpb.elexis.cst.widget.ResultatPartMinimax;

/**
 *
 * @author daniel created: 11.01.2015
 *
 *         GUI class for administration of CST Profiles
 *
 */
public class CstProfileEditor extends ViewPart implements IRefreshable {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ch.gpb.elexis.cst.views.cstprofileeditor";

	private CheckboxTableViewer tableViewerProfiles;
	private CheckboxTableViewer tableViewerCstGroups;
	private Action actionCreateProfile;
	private Action actionDeleteProfile;
	private Action actionRemoveCstGroup;
	private Action actionAddCstGroup;
	private Action actionCopyProfile;

	private Action doubleClickAction;

	private Table tableProfile;
	private Table tableCstGroup;
	private int sortColumn = 0;
	private boolean sortReverse = false;
	// private Color myColorRed;
	private List<CstProfile> cstProfiles;
	private List<CstGroup> cstGroups = new ArrayList<CstGroup>();
	private List<CstGroup> dialogCstGroups = new ArrayList<CstGroup>();
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();
	Patient patient;
	Label labelLeft;
	Label lblCrawlback;
	Label lblCrawlbackDate;

	DateRangeComposite dateRangeComposite;
	CstDocumentsComposite docComposite;
	ProImmunComposite proImmunComposite;
	GastroComposite gastroComposite;
	TherapieVorschlagComposite therapieComposite;
	AnzeigeOptionsComposite aoComposite;
	TemplateComposite templateComposite;
	RemindersComposite stateComposite;

	// private Map<String, String> hash;
	private CTabFolder ctabs;
	private ScrolledForm scrolledForm;
	Map<Object, Object> itemRanking = null;

	CstCopyProfileDialog dialog = null;
	private Logger log = LoggerFactory.getLogger(CstProfileEditor.class.getName());
	private boolean isRepeatedDialog;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	public enum GroupTokens {
		GASTRO_MAKRO, GASTRO_HISTO, COLO_MAKRO, COLO_HISTO
	};
	
	@Optional
	@Inject
	void activePatient(IPatient pat) {
		CoreUiUtil.runAsyncIfActive(() -> {
			if ((patient == null)
					|| (!patient.getId().equals(((Patient) NoPoUtil.loadAsPersistentObject(pat)).getId()))) {
				patient = (Patient) NoPoUtil.loadAsPersistentObject(pat);

				if (patient != null) {
					log.debug("Cst receives event with patient:" + patient.getName());
					labelLeft.setText(Messages.Cst_Text_Profile_fuer + StringUtils.SPACE + patient.getName()
							+ StringUtils.SPACE + patient.getVorname());
					labelLeft.redraw();

					loadProfileData();
					selectFirstRow();
					tableProfile.setFocus();
				}
			}
		}, tableProfile);
	}

	/**
	 * The constructor.
	 */
	public CstProfileEditor() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {

		patient = ElexisEventDispatcher.getSelectedPatient();

		Canvas baseCanvas = new Canvas(parent, SWT.FILL);
		baseCanvas.setBackground(new Color(Display.getDefault(), 239, 239, 239));

		FillLayout glBase = new FillLayout();
		glBase.type = SWT.VERTICAL;

		GridData gdBase = new GridData();
		gdBase.horizontalAlignment = SWT.FILL;
		gdBase.grabExcessHorizontalSpace = true;
		baseCanvas.setLayout(glBase);

		Canvas profileParameterCanvas = new Canvas(baseCanvas, SWT.FILL);
		GridLayout profileGridLayout = new GridLayout(1, true);
		profileParameterCanvas.setBackground(new Color(Display.getDefault(), 239, 239, 239));
		profileParameterCanvas.setLayout(profileGridLayout);

		GridData gd = new GridData();
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;
		profileParameterCanvas.setLayoutData(gd);

		profileParameterCanvas.setSize(400, 400);

		scrolledForm = UiDesk.getToolkit().createScrolledForm(profileParameterCanvas);
		scrolledForm.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = scrolledForm.getBody();
		body.setLayout(new FillLayout());

		ctabs = new CTabFolder(body, SWT.NONE);
		ctabs.setLayout(new FillLayout());

		Canvas buttonCanvas = new Canvas(profileParameterCanvas, SWT.NONE);
		GridLayout glButtonCanvas = new GridLayout();
		GridData gdButtonCanvas = new GridData();
		gdButtonCanvas.horizontalAlignment = SWT.CENTER;
		gdButtonCanvas.verticalAlignment = SWT.BEGINNING;
		glButtonCanvas.numColumns = 3;
		buttonCanvas.setSize(SWT.DEFAULT, 40);

		buttonCanvas.setLayoutData(gdButtonCanvas);
		buttonCanvas.setLayout(glButtonCanvas);
		Button btnSaveCstProfile = new Button(buttonCanvas, SWT.BORDER);

		btnSaveCstProfile.setText(Messages.CstProfileEditor_SaveProfile);
		btnSaveCstProfile.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					saveCstProfile();
					break;
				}
			}
		});

		Button btnShowResult = new Button(buttonCanvas, SWT.BORDER);
		btnShowResult.setText(Messages.CstProfileEditor_AuswertungAnzeigen);
		btnShowResult.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:

					showResultPage();

					break;
				}
			}
		});

		CTabItem ciTmp = new CTabItem(ctabs, SWT.NONE);
		ciTmp.setText(Messages.CstProfileEditor_Anzeige);
		aoComposite = new AnzeigeOptionsComposite(ctabs);
		ciTmp.setControl(aoComposite);

		// Tab Auswahl Befunde
		BefundSelectionComposite befundSelectionComposite = new BefundSelectionComposite(ctabs);
		CTabItem ciBsc = new CTabItem(ctabs, SWT.NONE);
		ciBsc.setText(Messages.CstProfileEditor_Auswahlbefunde);

		ciBsc.setControl(befundSelectionComposite);

		// Tab Therapievorschlag
		CTabItem ci4 = new CTabItem(ctabs, SWT.NONE);
		ci4.setText(Messages.CstProfileEditor_Therapievorschlag);
		therapieComposite = new TherapieVorschlagComposite(ctabs);
		ci4.setControl(therapieComposite);

		// Tabitem Cst Documents
		CTabItem ci8 = new CTabItem(ctabs, SWT.NONE);
		ci8.setText(Messages.Cst_Text_cst_documents);
		docComposite = new CstDocumentsComposite(ctabs, getViewSite());
		ci8.setControl(docComposite);

		// Tabitem Pro Immun
		CTabItem ci9 = new CTabItem(ctabs, SWT.NONE);
		ci9.setText(Messages.CstProfileEditor_Proimmun);
		proImmunComposite = new ProImmunComposite(ctabs);
		ci9.setControl(proImmunComposite);

		// Tabitem Gastro
		CTabItem ci10 = new CTabItem(ctabs, SWT.NONE);
		ci10.setText(Messages.CstProfileEditor_GastroColo);
		gastroComposite = new GastroComposite(ctabs);
		ci10.setControl(gastroComposite);

		// Tabitem TEmplate
		CTabItem ci12 = new CTabItem(ctabs, SWT.NONE);
		ci12.setText(Messages.TemplateComposite_template_title);
		TemplateComposite templateComposite = new TemplateComposite(ctabs);
		ci12.setControl(templateComposite);

		// Tabitem REminders
		/*
		 * CTabItem ci13 = new CTabItem(ctabs, SWT.NONE); ci13.setText("Reminders");
		 * stateComposite = new RemindersComposite(ctabs);
		 * ci13.setControl(stateComposite);
		 */

		// Tabitem Hilfe
		CTabItem ci11 = new CTabItem(ctabs, SWT.NONE);
		ci11.setText(Messages.HilfeComposite_hilfe_text);
		HilfeComposite hilfeComposite = new HilfeComposite(ctabs);
		ci11.setControl(hilfeComposite);

		// **** Tables with Profiles and Groups *********
		//
		SashForm sashform = new SashForm(baseCanvas, SWT.FILL);
		GridData gdSash = new GridData();
		gdSash.heightHint = 400;
		gdSash.horizontalAlignment = SWT.FILL;
		sashform.setLayoutData(gdSash);

		sashform.setLayout(new GridLayout());
		sashform.setSashWidth(2);

		// Composite Left Side
		Composite child1 = new Composite(sashform, SWT.FILL);
		GridLayout gridLayoutLeft = new GridLayout();
		gridLayoutLeft.numColumns = 1;
		child1.setLayout(gridLayoutLeft);

		// Composite Right Side
		Composite child2 = new Composite(sashform, SWT.FILL);
		GridLayout gridLayoutRight = new GridLayout();
		gridLayoutRight.numColumns = 1;
		child2.setLayout(gridLayoutRight);

		sashform.setWeights(new int[] { 200, 300 });

		// Label and Table Left Side
		labelLeft = new Label(child1, SWT.BORDER | SWT.CENTER);
		if (patient == null) {
			labelLeft.setText(Messages.CstCategory_nopatientselected);
		} else {
			labelLeft.setText(Messages.Cst_Text_Profile_fuer + StringUtils.SPACE + patient.getName() + StringUtils.SPACE
					+ patient.getVorname());
		}

		labelLeft.setSize(100, 20);
		labelLeft.setFont(createBoldFont(labelLeft.getFont()));
		labelLeft.setForeground(UiDesk.getColor(UiDesk.COL_BLUE));
		labelLeft.setBackground(new Color(Display.getDefault(), 251, 247, 247));

		GridData gridDataLabelLeft = new GridData();
		gridDataLabelLeft.horizontalAlignment = GridData.FILL;
		gridDataLabelLeft.grabExcessHorizontalSpace = true;
		labelLeft.setLayoutData(gridDataLabelLeft);

		tableProfile = new Table(child1, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridDataTableLeft = new GridData();
		gridDataTableLeft.horizontalAlignment = GridData.FILL;
		gridDataTableLeft.verticalAlignment = GridData.FILL;
		gridDataTableLeft.grabExcessHorizontalSpace = true;
		gridDataTableLeft.grabExcessVerticalSpace = true;
		gridDataTableLeft.minimumHeight = 200;
		gridDataTableLeft.heightHint = 200;
		tableProfile.setLayoutData(gridDataTableLeft);

		// Label and Table Right Side
		Label labelRight = new Label(child2, SWT.BORDER | SWT.CENTER);
		labelRight.setText("Profile Items (CST Groups)");
		labelRight.setSize(100, 20);
		labelRight.setFont(createBoldFont(labelRight.getFont()));
		labelRight.setBackground(new Color(Display.getDefault(), 251, 247, 247));

		GridData gridDataLabelRight = new GridData();
		gridDataLabelRight.horizontalAlignment = GridData.FILL;
		gridDataLabelRight.grabExcessHorizontalSpace = true;
		labelRight.setLayoutData(gridDataLabelRight);

		tableCstGroup = new Table(child2, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridDataTableRight = new GridData();
		gridDataTableRight.horizontalAlignment = GridData.FILL;
		gridDataTableRight.verticalAlignment = GridData.FILL;
		gridDataTableRight.grabExcessHorizontalSpace = true;
		gridDataTableRight.grabExcessVerticalSpace = true;
		gridDataTableRight.minimumHeight = 200;
		gridDataTableRight.heightHint = 200;
		tableCstGroup.setLayoutData(gridDataTableRight);

		Composite movebuttonDummyCompo = new Composite(child1, SWT.NONE);
		GridData gdDummyMovebutton = new GridData();
		movebuttonDummyCompo.setLayoutData(gdDummyMovebutton);
		gdDummyMovebutton.heightHint = 30;

		Composite movebuttonCompo = new Composite(child2, SWT.NONE);
		GridLayout movebuttonGridLayout = new GridLayout(2, true);

		movebuttonCompo.setLayout(movebuttonGridLayout);
		movebuttonCompo.setSize(400, 30);
		GridData gdButtonCompo = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gdButtonCompo.minimumHeight = 30;
		gdButtonCompo.heightHint = 30;
		gdButtonCompo.horizontalAlignment = SWT.CENTER;
		gdButtonCompo.verticalAlignment = SWT.BEGINNING;
		movebuttonCompo.setLayoutData(gdButtonCompo);

		Image imgArrowUp = UiDesk.getImage(Activator.IMG_ARROW_UP_NAME);
		Image imgArrowDown = UiDesk.getImage(Activator.IMG_ARROW_DOWN_NAME);

		Button btnArrowUp = new Button(movebuttonCompo, SWT.BORDER);
		Button btnArrowDown = new Button(movebuttonCompo, SWT.BORDER);

		btnArrowDown.setText(Messages.Button_MoveDown);
		btnArrowUp.setText(Messages.Button_MoveUp);

		btnArrowUp.setImage(imgArrowUp);
		btnArrowDown.setImage(imgArrowDown);

		GridData gdArrowUp = new GridData(GridData.HORIZONTAL_ALIGN_END);
		GridData gdArrowDown = new GridData(GridData.HORIZONTAL_ALIGN_END);

		btnArrowUp.setLayoutData(gdArrowUp);
		btnArrowDown.setLayoutData(gdArrowDown);

		btnArrowUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItemUp();
				;
			}
		});

		btnArrowDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItemDown();
				;
			}
		});

		cstProfiles = new ArrayList<CstProfile>();

		String[] colLabels = getCategoryColumnLabels();
		int columnWidth[] = getProfileColumnWidth();
		ProfileSortListener categorySortListener = new ProfileSortListener();
		TableColumn[] cols = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels.length; i++) {
			cols[i] = new TableColumn(tableProfile, SWT.NONE);
			cols[i].setWidth(columnWidth[i]);
			cols[i].setText(colLabels[i]);
			cols[i].setData(new Integer(i));
			cols[i].addSelectionListener(categorySortListener);
		}
		tableProfile.setHeaderVisible(true);
		tableProfile.setLinesVisible(true);

		String[] colLabels2 = getCstGroupColumnLabels();
		int columnWidth2[] = getCstGroupColumnWidth();

		TableColumn[] cols2 = new TableColumn[colLabels.length];
		for (int i = 0; i < colLabels2.length; i++) {
			cols2[i] = new TableColumn(tableCstGroup, SWT.NONE);
			cols2[i].setWidth(columnWidth2[i]);
			cols2[i].setText(colLabels2[i]);
			cols2[i].setData(new Integer(i));
		}
		tableCstGroup.setHeaderVisible(true);
		tableCstGroup.setLinesVisible(true);

		tableViewerProfiles = new CheckboxTableViewer(tableProfile);
		tableViewerProfiles.setContentProvider(new ProfileContentProvider());
		tableViewerProfiles.setLabelProvider(new ProfileLabelProvider());
		tableViewerProfiles.setSorter(new ProfileSorter());

		tableViewerProfiles.setInput(getViewSite());
		if (tableProfile.getItems().length > 0) {
			tableProfile.select(0);
		}

		tableViewerProfiles.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerProfiles.getSelection();
				// on selecting a new Category, fetch its LabItems
				CstProfile selProfile = (CstProfile) selection.getFirstElement();
				if (selProfile != null) {
					itemRanking = selProfile.getMap(CstGroup.ITEMRANKING);
					cstGroups = selProfile.getCstGroups();

					// if null, initialize the ranking with the current sequence
					if (itemRanking == null || itemRanking.size() == 0) {
						Hashtable<Object, Object> ranking = new Hashtable<Object, Object>();
						int i = 1;
						for (CstGroup item : cstGroups) {
							ranking.put(item.getId(), i++);

						}
						itemRanking = (Map<Object, Object>) ranking.clone();
						selProfile.setMap(CstGroup.ITEMRANKING, ranking);
					}

					loadCstProfile(selProfile);
					tableViewerCstGroups.refresh();
				}
			}
		});

		tableViewerCstGroups = new CheckboxTableViewer(tableCstGroup);
		tableViewerCstGroups.setContentProvider(new CstGroupContentProvider());
		tableViewerCstGroups.setLabelProvider(new CstGroupLabelProvider());
		tableViewerCstGroups.setSorter(new CstGroupSorter());

		tableViewerCstGroups.setInput(getViewSite());

		// Create the help context id for the viewer's control
		ctabs.setSelection(ctabs.getItem(0));

		PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewerProfiles.getControl(), "ch.gpb.elexis.cst.viewer");
		makeActions();
		hookContextMenuCategory();
		hookContextMenuLabItem();
		hookDoubleClickAction();
		contributeToActionBars();
		getSite().getPage().addPartListener(udpateOnVisible);

	}

	private void moveItemUp() {

		IStructuredSelection selection2 = (IStructuredSelection) tableViewerProfiles.getSelection();
		CstProfile selProfile = (CstProfile) selection2.getFirstElement();

		try {
			IStructuredSelection selection = (IStructuredSelection) tableViewerCstGroups.getSelection();
			CstGroup selItem = (CstGroup) selection.getFirstElement();
			if (selItem == null) {
				return;
			}
			int selIndex = tableViewerCstGroups.getTable().getSelectionIndex();

			if (selIndex < 1) {
				return;
			}

			TableItem tableItem = tableViewerCstGroups.getTable().getItem(selIndex - 1);
			CstGroup aboveItem = (CstGroup) tableItem.getData();

			int rank1 = (int) itemRanking.get(selItem.getId());
			int rank2 = (int) itemRanking.get(aboveItem.getId());
			itemRanking.put(selItem.getId(), rank1 - 1);
			itemRanking.put(aboveItem.getId(), rank2 + 1);

			selProfile.setMap(CstProfile.ITEMRANKING, itemRanking);

			tableViewerCstGroups.refresh();
		} catch (Exception e) {
			log.error(e.toString());
			showMessage("Fehler: die Reihenfolge der CST Gruppen muss neu initialisiert werden.");
			reinitRanking(selProfile);
		}
	}

	private void moveItemDown() {

		IStructuredSelection selection2 = (IStructuredSelection) tableViewerProfiles.getSelection();
		CstProfile selProfile = (CstProfile) selection2.getFirstElement();

		try {
			IStructuredSelection selection = (IStructuredSelection) tableViewerCstGroups.getSelection();
			CstGroup selItem = (CstGroup) selection.getFirstElement();
			if (selItem == null) {
				return;
			}
			int selIndex = tableViewerCstGroups.getTable().getSelectionIndex();
			if (selIndex + 1 >= tableViewerCstGroups.getTable().getItemCount()) {
				return;
			}

			TableItem tableItem = tableViewerCstGroups.getTable().getItem(selIndex + 1);
			CstGroup belowItem = (CstGroup) tableItem.getData();

			int rank1 = (int) itemRanking.get(selItem.getId());
			int rank2 = (int) itemRanking.get(belowItem.getId());
			itemRanking.put(selItem.getId(), rank1 + 1);
			itemRanking.put(belowItem.getId(), rank2 - 1);

			selProfile.setMap(CstProfile.ITEMRANKING, itemRanking);

			tableViewerCstGroups.refresh();
		} catch (Exception e) {
			log.error(e.toString());
			showMessage("Fehler: die Reihenfolge der CST Gruppen muss neu initialisiert werden.");
			reinitRanking(selProfile);
		}

	}

	/*
	 * private void showResultPageNew() {
	 *
	 * // TODO create Constants for the view IDs
	 *
	 * TableItem[] selItemC = tableProfile.getSelection();
	 *
	 * if (selItemC.length == 0) {
	 * showMessage(Messages.Cst_Text_Bitte_Profil_auswaehlen); return; }
	 *
	 * CstProfile selProfile = (CstProfile) selItemC[0].getData();
	 *
	 * if (selProfile.getCstGroups().size() == 0) { MessageBox dialog = new
	 * MessageBox(UiDesk.getTopShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
	 * dialog.setText(Messages.Cst_Text_profil_unvollstaendig);
	 * dialog.setMessage(Messages.Cst_Text_profil_hat_keine_gruppen);
	 *
	 * // open dialog and await user selection int returnCode = dialog.open(); if
	 * (returnCode == SWT.CANCEL) { return; }
	 *
	 * }
	 *
	 * try { if (selProfile.getAnzeigeTyp().equals(CstProfile.ANZEIGETYP_EFFEKTIV))
	 * { ResultatPartEffektiv viewer = (ResultatPartEffektiv)
	 * PlatformUI.getWorkbench() .getActiveWorkbenchWindow()
	 * .getActivePage().findView("ch.gpb.elexis.cst.resultateffektiv"); if (viewer
	 * == null) { viewer = (ResultatPartEffektiv)
	 * PlatformUI.getWorkbench().getActiveWorkbenchWindow() .getActivePage()
	 * .showView("ch.gpb.elexis.cst.resultateffektiv"); }
	 * viewer.setProfile(selProfile); viewer = (ResultatPartEffektiv)
	 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
	 * .showView("ch.gpb.elexis.cst.resultateffektiv");
	 *
	 * }
	 *
	 * if (selProfile.getAnzeigeTyp().equals(CstProfile.ANZEIGETYP_MINIMAX)) {
	 * ResultatPartMinimax viewer = (ResultatPartMinimax) PlatformUI.getWorkbench()
	 * .getActiveWorkbenchWindow()
	 * .getActivePage().findView("ch.gpb.elexis.cst.resultatminimax"); if (viewer ==
	 * null) { viewer = (ResultatPartMinimax)
	 * PlatformUI.getWorkbench().getActiveWorkbenchWindow() .getActivePage()
	 * .showView("ch.gpb.elexis.cst.resultatminimax"); }
	 * viewer.setProfile(selProfile); viewer = (ResultatPartMinimax)
	 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
	 * .showView("ch.gpb.elexis.cst.resultatminimax");
	 *
	 * }
	 *
	 * } catch (PartInitException e) { log.info("Error opening result view: " +
	 * e.getMessage(), Log.INFOS); }
	 *
	 * }
	 */

	private void showResultPage() {

		// TODO create Constants for the view IDs

		TableItem[] selItemC = tableProfile.getSelection();

		if (selItemC.length == 0) {
			showMessage(Messages.Cst_Text_Bitte_Profil_auswaehlen);
			return;
		}

		CstProfile selProfile = (CstProfile) selItemC[0].getData();

		if (selProfile.getCstGroups().size() == 0) {
			MessageBox dialog = new MessageBox(UiDesk.getTopShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			dialog.setText(Messages.Cst_Text_profil_unvollstaendig);
			dialog.setMessage(Messages.Cst_Text_profil_hat_keine_gruppen);

			// open dialog and await user selection
			int returnCode = dialog.open();
			if (returnCode == SWT.CANCEL) {
				return;
			}

		}

		try {
			if (selProfile.getAnzeigeTyp().equals(CstProfile.ANZEIGETYP_EFFEKTIV)) {
				CstResultEffektiv viewer = (CstResultEffektiv) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView("ch.gpb.elexis.cst.cstresulteffektiv");
				if (viewer == null) {
					viewer = (CstResultEffektiv) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("ch.gpb.elexis.cst.cstresulteffektiv");
				}
				viewer.setProfile(selProfile);
				viewer = (CstResultEffektiv) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("ch.gpb.elexis.cst.cstresulteffektiv");

			}

			if (selProfile.getAnzeigeTyp().equals(CstProfile.ANZEIGETYP_MINIMAX)) {
				CstResultMiniMax viewer = (CstResultMiniMax) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView("ch.gpb.elexis.cst.cstresultminimax");
				if (viewer == null) {
					viewer = (CstResultMiniMax) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("ch.gpb.elexis.cst.cstresultminimax");
				}
				viewer.setProfile(selProfile);
				viewer = (CstResultMiniMax) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("ch.gpb.elexis.cst.cstresultminimax");

			}

		} catch (PartInitException e) {
			log.info("Error opening result view: " + e.getMessage(), Log.INFOS);
		}

	}

	private CTabItem getTabitemByName(CTabFolder cTabFolder, String title) {
		CTabItem[] items = cTabFolder.getItems();

		for (CTabItem item : items) {
			if (item.getText().equals(title)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * pick up all values from the gui to save them in DB
	 */
	@SuppressWarnings("unchecked")
	private void saveCstProfile() {
		int saveSelIdx = tableProfile.getSelectionIndex();
		TableItem[] selItemC = tableProfile.getSelection();
		CstProfile selProfile = (CstProfile) selItemC[0].getData();

		Map<Object, Object> mAuswahl = selProfile.getMap(CstProfile.KEY_AUSWAHLBEFUNDE);
		if (mAuswahl == null) {
			showMessage("Map Auswahl lacks in profile. Create?");
			Hashtable<Object, Object> map = new Hashtable<Object, Object>();
			selProfile.setMap(CstProfile.KEY_AUSWAHLBEFUNDE, map);
		}

		CTabItem ciBefundauswahl = getTabitemByName(ctabs, Messages.CstProfileEditor_Auswahlbefunde);
		BefundSelectionComposite befundSelectionComposite = (BefundSelectionComposite) ciBefundauswahl.getControl();

		selProfile.setMap(CstProfile.KEY_AUSWAHLBEFUNDE, befundSelectionComposite.getSelection(mAuswahl));

		CTabItem ci = getTabitemByName(ctabs, Messages.CstProfileEditor_Therapievorschlag);
		TherapieVorschlagComposite ca2 = (TherapieVorschlagComposite) ci.getControl();
		selProfile.setTherapievorschlag(ca2.getTextTherapie());
		selProfile.setDiagnose(ca2.getTextDiagnose());

		ci = getTabitemByName(ctabs, Messages.TemplateComposite_template_title);
		TemplateComposite templateComposite = (TemplateComposite) ci.getControl();
		selProfile.setTemplate(templateComposite.isTemplate() ? "1" : "0");
		selProfile.setOutputHeader(templateComposite.getOutputHeader());

		ci = getTabitemByName(ctabs, Messages.CstProfileEditor_Anzeige);

		AnzeigeOptionsComposite aoComposite = (AnzeigeOptionsComposite) ci.getControl();
		selProfile.setPeriod1DateStart(aoComposite.getPeriod1StartDate());
		selProfile.setPeriod1DateEnd(aoComposite.getPeriod1EndDate());

		selProfile.setPeriod2DateStart(aoComposite.getPeriod2StartDate());
		selProfile.setPeriod2DateEnd(aoComposite.getPeriod1StartDate());

		selProfile.setPeriod3DateStart(aoComposite.getPeriod3StartDate());
		selProfile.setPeriod3DateEnd(aoComposite.getPeriod3EndDate());
		selProfile.setCrawlBack(aoComposite.getCrawlback());
		selProfile.setAnzeigeTyp(aoComposite.getAnzeigeTyp());
		selProfile.setAusgabeRichtung(aoComposite.getAusgabeRichtung());

		CstProimmun cstProImmun = CstProimmun.getByProfileId(selProfile.getId());
		if (cstProImmun == null) {
			cstProImmun = new CstProimmun();
		}

		cstProImmun.setDatum(proImmunComposite.getDate());
		cstProImmun.setText1(proImmunComposite.getReaktionsStaerke1());
		cstProImmun.setText2(proImmunComposite.getReaktionsStaerke2());
		cstProImmun.setText3(proImmunComposite.getReaktionsStaerke3());
		cstProImmun.setText4(proImmunComposite.getReaktionsStaerke4());
		cstProImmun.setTested(proImmunComposite.getTested());
		cstProImmun.setToBeTested(proImmunComposite.getToBeTested());

		ci = getTabitemByName(ctabs, Messages.CstProfileEditor_GastroColo);
		GastroComposite gastroComposite = (GastroComposite) ci.getControl();
		CstGastroColo dbObjGastro = CstGastroColo.getByProfileId(selProfile.getId());
		if (dbObjGastro == null) {
			dbObjGastro = new CstGastroColo();
			dbObjGastro.setDatumColo(CstService.getCompactFromDate(new Date()));
			dbObjGastro.setDatumGastro(CstService.getCompactFromDate(new Date()));

		}
		dbObjGastro.setDatumGastro(gastroComposite.getGastroDatum());
		dbObjGastro.setDatumColo(gastroComposite.getColoDatum());
		dbObjGastro.setText1(gastroComposite.getTxtGastroMakro());
		dbObjGastro.setText2(gastroComposite.getTxtGastroHisto());
		dbObjGastro.setText3(gastroComposite.getTxtColoMakro());
		dbObjGastro.setText4(gastroComposite.getTxtColoHisto());

		dbObjGastro.setGastroMakroBefund(gastroComposite.getBefundGastroMakro());
		dbObjGastro.setGastroHistoBefund(gastroComposite.getBefundGastroHisto());
		dbObjGastro.setColoMakroBefund(gastroComposite.getBefundColoMakro());
		dbObjGastro.setColoHistoBefund(gastroComposite.getBefundColoHisto());

		loadProfileData();
		loadCstProfile(selProfile);
		tableProfile.setSelection(saveSelIdx);
	}

	/**
	 * populate the GUI with profile data from DB
	 *
	 * @param selProfile the profile to get data from
	 */
	@SuppressWarnings("unchecked")
	private void loadCstProfile(CstProfile selProfile) {
		// Befundparaameter auswahl
		Map<String, Object> mapAuswahl = selProfile.getMap(CstProfile.KEY_AUSWAHLBEFUNDE);

		for (CTabItem item : this.ctabs.getItems()) {
			if (item.getText().startsWith(Messages.CstProfileEditor_Auswahlbefunde)) {
				BefundSelectionComposite befundSelectionComposite = (BefundSelectionComposite) item.getControl();

				befundSelectionComposite.setSelection(mapAuswahl);
			}

			if (item.getText().startsWith(Messages.CstProfileEditor_Anzeige)) {

				AnzeigeOptionsComposite aoComposite = (AnzeigeOptionsComposite) item.getControl();
				aoComposite.setCrawlback(selProfile.getCrawlBack());
				aoComposite.setPeriod1StartDate(selProfile.getPeriod1DateStart());
				aoComposite.setPeriod1EndDate(selProfile.getPeriod1DateEnd());

				aoComposite.setPeriod2StartDate(selProfile.getPeriod2DateStart());
				aoComposite.setPeriod2EndDate(selProfile.getPeriod2DateEnd());

				aoComposite.setPeriod3StartDate(selProfile.getPeriod3DateStart());
				aoComposite.setPeriod3EndDate(selProfile.getPeriod3DateEnd());
				aoComposite.setAnzeigeTyp(selProfile.getAnzeigeTyp());

				aoComposite.setAusgabeRichtung(selProfile.getAusgabeRichtung());

			}

			if (item.getText().startsWith(Messages.Cst_Text_cst_documents)) {
				CstDocumentsComposite docComposite = (CstDocumentsComposite) item.getControl();
				docComposite.clear();

			}
			if (item.getText().startsWith("Reminders")) {
				RemindersComposite composite = (RemindersComposite) item.getControl();
				composite.setProfile(selProfile);

			}

			if (item.getText().startsWith(Messages.TemplateComposite_template_title)) {
				TemplateComposite templateComposite = (TemplateComposite) item.getControl();
				templateComposite.setTemplate(selProfile.getTemplate().equals("1") ? true : false);
				templateComposite.setOutputHeader(selProfile.getOutputHeader());
				// templateComposite.clear();

			}
			if (item.getText().startsWith(Messages.CstProfileEditor_Therapievorschlag)) {
				TherapieVorschlagComposite therapieComposite = (TherapieVorschlagComposite) item.getControl();
				therapieComposite.setTextTherapie(selProfile.getTherapievorschlag());
				therapieComposite.setTextDiagnose(selProfile.getDiagnose());
			}

			if (item.getText().startsWith(Messages.CstProfileEditor_Proimmun)) {
				ProImmunComposite proimmunCompo = (ProImmunComposite) item.getControl();

				CstProimmun cstProImmun = CstProimmun.getByProfileId(selProfile.getId());

				if (cstProImmun == null) {
					cstProImmun = new CstProimmun(selProfile.getId(), CstService.getCompactFromDate(new Date()));
				}
				proimmunCompo.setDate(cstProImmun.getDatum());
				proimmunCompo.setTested(cstProImmun.getTested());
				// TODO: new DB field
				proimmunCompo.setToBeTested(cstProImmun.getToBeTested());
				proimmunCompo.setReaktionsStaerke1(cstProImmun.getText1());
				proimmunCompo.setReaktionsStaerke2(cstProImmun.getText2());
				proimmunCompo.setReaktionsStaerke3(cstProImmun.getText3());
				proimmunCompo.setReaktionsStaerke4(cstProImmun.getText4());

			}
			if (item.getText().startsWith(Messages.CstProfileEditor_GastroColo)) {
				GastroComposite gastroCompo = (GastroComposite) item.getControl();
				gastroCompo.clear();

				CstGastroColo dbObj = CstGastroColo.getByProfileId(selProfile.getId());

				if (dbObj == null) {
					dbObj = new CstGastroColo(selProfile.getId(), CstService.getCompactFromDate(new Date()),
							CstService.getCompactFromDate(new Date()));
				}

				gastroCompo.setGastroDatum(dbObj.getDatumGastro());
				gastroCompo.setColoDatum(dbObj.getDatumColo());

				gastroCompo.setBefundColoHisto(dbObj.getColoHistoBefund());
				gastroCompo.setBefundColoMakro(dbObj.getColoMakroBefund());
				gastroCompo.setBefundGastroMakro(dbObj.getGastroMakroBefund());
				gastroCompo.setBefundGastroHisto(dbObj.getGastroHistoBefund());
				gastroCompo.setTxtGastroMakro(dbObj.getText1());
				gastroCompo.setTxtGastroHisto(dbObj.getText2());
				gastroCompo.setTxtColoMakro(dbObj.getText3());
				gastroCompo.setTxtColoHisto(dbObj.getText4());
			}

		}

	}

	private void loadProfileData() {
		Mandant m = CoreHub.actMandant;
		log.info("load CST Profiles for mandant: " + m.getId() + StringUtils.SPACE + m.getName(), Log.INFOS);
		log.info("and patient: " + patient.getId() + StringUtils.SPACE + patient.getName(), Log.INFOS);

		cstProfiles = CstProfile.getCstGroups(patient, m.getId());

		tableViewerProfiles.refresh();

		if (!cstProfiles.isEmpty()) {
			tableViewerProfiles.setSelection(new StructuredSelection(tableViewerProfiles.getElementAt(0)), true);

		} else {
			therapieComposite.clear();
			docComposite.clear();
			cstGroups.clear();
			gastroComposite.clear();
			aoComposite.initDates();
		}

		if (tableViewerProfiles != null) {

			tableViewerProfiles.refresh();
			tableProfile.setFocus();
		}
		if (tableViewerCstGroups != null) {
			tableViewerCstGroups.refresh();
		}
	}

	private void selectFirstRow() {
		if (tableViewerProfiles != null) {
			Object obj = tableViewerProfiles.getElementAt(0);
			if (!cstProfiles.isEmpty() && obj != null) {
				tableViewerProfiles.setSelection(new StructuredSelection(tableViewerProfiles.getElementAt(0)), true);
			}
		}
	}

	private void selectRow(int row) {
		if (tableViewerProfiles != null) {
			Object obj = tableViewerProfiles.getElementAt(row);
			if (!cstProfiles.isEmpty() && obj != null) {
				tableViewerProfiles.setSelection(new StructuredSelection(tableViewerProfiles.getElementAt(row)), true);
			}
		}
	}

	private Font createBoldFont(Font baseFont) {
		FontData fd = baseFont.getFontData()[0];
		Font font = new Font(baseFont.getDevice(), fd.getName(), 10, fd.getStyle() | SWT.BOLD);
		return font;
	}

	private String[] getCategoryColumnLabels() {
		String columnLabels[] = { Messages.CstCategory_name, Messages.CstCategory_description,
				Messages.Cst_Text_anzeigen_ab };
		return columnLabels;
	}

	private String[] getCstGroupColumnLabels() {

		String columnLabels[] = { Messages.CstLaborPrefs_type, Messages.CstCategory_description,
				Messages.CstProfile_Ranking };

		return columnLabels;
	}

	private int[] getProfileColumnWidth() {
		int columnWidth[] = { 120, 50, 100 };
		return columnWidth;
	}

	private int[] getCstGroupColumnWidth() {
		int columnWidth[] = { 120, 150, 200 };
		return columnWidth;
	}

	private void hookContextMenuCategory() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CstProfileEditor.this.fillContextMenuCategory(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewerProfiles.getControl());
		tableViewerProfiles.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewerProfiles);
	}

	private void hookContextMenuLabItem() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CstProfileEditor.this.fillContextMenuLabItem(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewerCstGroups.getControl());
		tableViewerCstGroups.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewerCstGroups);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionCreateProfile);
		manager.add(new Separator());
		manager.add(actionDeleteProfile);
	}

	private void fillContextMenuCategory(IMenuManager manager) {
		manager.add(actionCreateProfile);
		manager.add(actionDeleteProfile);
		manager.add(actionCopyProfile);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenuLabItem(IMenuManager manager) {
		manager.add(actionRemoveCstGroup);
		manager.add(actionAddCstGroup);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionCreateProfile);
		manager.add(actionDeleteProfile);
		manager.add(actionCopyProfile);
	}

	private void makeActions() {

		actionCreateProfile = new Action() {
			public void run() {
				CstNewProfileDialog dialog = new CstNewProfileDialog(tableViewerProfiles.getControl().getShell(),
						CoreHub.actMandant);

				dialog.create();

				// flag f�r den rekursiven Dialog Aufruf
				if (isRepeatedDialog) {
					dialog.setErrorMessage(Messages.Cst_Text_cstprofile_exists);
				}
				isRepeatedDialog = false;

				if (dialog.open() == Window.OK) {
					if (dialog.getGroupName().length() < 1) {
						return;
					}
				} else {
					return;
				}

				try {

					Mandant m = CoreHub.actMandant;
					if (m != null) {
						if (patient != null) {

							Calendar now = Calendar.getInstance();
							now.add(Calendar.YEAR, -2);

							CstProfile target = new CstProfile(dialog.getGroupName(), dialog.getGroupDescription(),
									null, patient.getId(), m.getId(), CstService.getCompactFromDate(now.getTime()),
									CstService.getCompactFromDate(new Date()), "1");

							CstProfile profileTemplate = dialog.getProfileToCopyFrom();
							// was a profile selected as template to copy from?
							if (profileTemplate != null) {
								log.debug("Selected a profile to copy from: " + profileTemplate.getName());
								CstService.copyProfile(profileTemplate, target);

							} else {

								// initialize Auswahl Befunde
								Hashtable<Object, Object> auswahlBefunde = new Hashtable<Object, Object>();
								target.setMap(CstProfile.KEY_AUSWAHLBEFUNDE, auswahlBefunde);
								target.setAnzeigeTyp(CstProfile.ANZEIGETYP_EFFEKTIV);

								target.setPeriod1DateStart(CstService
										.getCompactFromDate(CstService.getDateByAddingDays(now.getTime(), -365)));
								target.setPeriod1DateEnd(CstService.getCompactFromDate(now.getTime()));

								target.setPeriod2DateStart(CstService
										.getCompactFromDate(CstService.getDateByAddingDays(now.getTime(), -365)));
								target.setPeriod2DateEnd(CstService
										.getCompactFromDate(CstService.getDateByAddingDays(now.getTime(), -730)));

								target.setPeriod3DateStart(CstService
										.getCompactFromDate(CstService.getDateByAddingDays(now.getTime(), -730)));
								target.setPeriod3DateEnd(CstService
										.getCompactFromDate(CstService.getDateByAddingDays(now.getTime(), -1095)));

								TableItem[] selItem = tableProfile.getSelection();
								if (selItem.length != 0) {

									CstProfile selProfile = (CstProfile) selItem[0].getData();
									CstProimmun cstProImmun = CstProimmun.getByProfileId(selProfile.getId());

									if (cstProImmun != null) {
										CstProimmun newCstProImmun = new CstProimmun(target.getId(),
												cstProImmun.getDatum());
										newCstProImmun.setProfileId(target.getId());
										newCstProImmun.setTested(cstProImmun.getTested());
										newCstProImmun.setToBeTested(cstProImmun.getToBeTested());
										newCstProImmun.setText1(cstProImmun.getText1());
										newCstProImmun.setText2(cstProImmun.getText2());
										newCstProImmun.setText3(cstProImmun.getText3());
										newCstProImmun.setText4(cstProImmun.getText4());

									}
								}

							}
							loadProfileData();

							// select newly created Item
							TableItem[] items = tableProfile.getItems();
							for (int i = 0; i < items.length; i++) {
								TableItem item = items[i];
								CstProfile g = (CstProfile) item.getData();
								if (g.getId().equals(target.getId())) {
									selectRow(i);
									break;
								}
							}
							tableViewerProfiles.refresh(true);
							tableProfile.setFocus();

						}
					} else {
						log.info("Error no mandant available ", Log.INFOS);
					}
				} catch (Exception e) {
					log.info("CST Category already exists: " + e.getMessage(), Log.INFOS);
					isRepeatedDialog = true;
					actionCreateProfile.run();
				}
			}
		};
		actionCreateProfile.setText(Messages.Cst_Text_create_cstprofile);
		actionCreateProfile.setToolTipText(Messages.Cst_Text_create_cstprofile_tooltip);
		actionCreateProfile.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actionDeleteProfile = new Action() {
			public void run() {
				TableItem[] selItem = tableProfile.getSelection();
				if (selItem.length == 0) {
					return;
				}

				CstProfile selProfile = (CstProfile) selItem[0].getData();

				String sMsg = String.format(Messages.Cst_Text_confirm_delete_profile, selProfile.getName());
				// showMessage(sMsg);
				MessageBox dialog = new MessageBox(UiDesk.getTopShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
				dialog.setText("Delete Profile");
				dialog.setMessage(sMsg);

				// open dialog and await user selection
				int returnCode = dialog.open();
				if (returnCode == SWT.CANCEL) {
					return;
				}
				// delete the dependent records in other tables
				CstGastroColo.getByProfileId(selProfile.getId()).delete();
				CstProimmun.getByProfileId(selProfile.getId()).delete();
				// CstGroup.getCstGroups(patient, mandantId)

				// Deletes also the entries in the N:N table to CstGroups
				// (cstgroup_profile_joint)
				selProfile.delete();
				loadProfileData();

				tableViewerProfiles.refresh();
				selectFirstRow();
				tableProfile.setFocus();

			}
		};
		actionDeleteProfile.setText(Messages.Cst_Text_delete_profile);
		actionDeleteProfile.setToolTipText(Messages.Cst_Text_delete_profile_tooltip);
		actionDeleteProfile.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		actionCopyProfile = new Action() {
			public void run() {

				/*
				 * TODO: Denkfehler: asyncExec bringt gar nix mit einem blockenden Dialog!!
				 *
				 */

				UiDesk.asyncExec(new Runnable() {
					public void run() {
						IProgressMonitor monitor = new NullProgressMonitor();

						String msg = "Patienten-Liste erstellen";

						showBusy(true);

						monitor.beginTask(msg, 7);
						monitor.subTask("Einlesen der Aktualisierungsdaten");

						monitor.worked(1);

						TableItem[] selItem = tableProfile.getSelection();
						if (selItem.length == 0) {
							return;
						}

						CstProfile selProfile = (CstProfile) selItem[0].getData();

						dialog = new CstCopyProfileDialog(tableViewerProfiles.getControl().getShell());
						dialog.create();

						monitor.worked(1);

						if (dialog.open() == Window.OK) {
							List<Patient> selPatient = dialog.getSelItems();
							if (selPatient.size() == 0) {
								return;
							}

							Mandant m = CoreHub.actMandant;
							if (m != null) {
								CstService service = new CstService();
								service.copyProfile(selProfile, selPatient, m);
							}
						}

						loadProfileData();

						tableViewerProfiles.refresh();
						selectFirstRow();
						tableProfile.setFocus();

						showBusy(false);
						monitor.worked(1);
						monitor.done();

					}
				});

			}
		};
		actionCopyProfile.setText(Messages.Cst_Text_copy_profile);
		actionCopyProfile.setToolTipText(Messages.Cst_Text_copy_profile_tooltip);
		actionCopyProfile.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		actionRemoveCstGroup = new Action() {
			public void run() {
				TableItem[] selItems = tableProfile.getSelection();

				TableItem[] selItemC = tableProfile.getSelection();
				CstProfile selGroup = (CstProfile) selItemC[0].getData();

				TableItem[] selItem = tableCstGroup.getSelection();
				if (selItem.length == 0) {
					return;
				}
				CstGroup labItem = (CstGroup) selItem[0].getData();

				selGroup.removeCstGroup(labItem);

				loadProfileData();
				tableProfile.setSelection(selItems[0]);

				tableViewerProfiles.refresh();
				tableViewerCstGroups.refresh();

				reinitRanking(selGroup);

				tableProfile.setFocus();

			}
		};
		actionRemoveCstGroup.setText(Messages.Cst_Text_delete_cstgroup_from_profile);
		actionRemoveCstGroup.setToolTipText(Messages.Cst_Text_delete_cstgroup_from_profile_tooltip);
		actionRemoveCstGroup.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		actionAddCstGroup = new Action() {
			public void run() {
				TableItem[] selItems = tableProfile.getSelection();

				List<CstGroup> itemsToAdd;

				dialogCstGroups = CstGroup.getCstGroups();

				CstGroupSelectionDialog dialog = new CstGroupSelectionDialog(
						tableViewerCstGroups.getControl().getShell(), dialogCstGroups);
				dialog.create();
				if (dialog.open() == Window.OK) {
					itemsToAdd = dialog.getSelItems();
				} else {
					return;
				}

				TableItem[] selItemC = tableProfile.getSelection();
				if (selItemC == null || selItemC.length < 1) {
					return;
				}

				CstProfile selProfile = (CstProfile) selItemC[0].getData();
				if (selProfile == null) {
					return;
				}

				try {
					selProfile.addItems(itemsToAdd);
				} catch (Exception e) {
					showMessage(Messages.Cst_Text_cstgroup_exists_in_profile);
				}

				loadProfileData();
				tableProfile.setSelection(selItems[0]);

				tableViewerProfiles.refresh();
				tableViewerCstGroups.refresh();

				reinitRanking(selProfile);

				tableProfile.setFocus();
			}
		};
		actionAddCstGroup.setText(Messages.Cst_Text_add_cstgroup_to_profile);
		actionAddCstGroup.setToolTipText(Messages.Cst_Text_add_cstgroup_to_profile);
		actionAddCstGroup.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = tableViewerProfiles.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				CstProfile profile = (CstProfile) obj;

				ProfileDetailDialog dialog = new ProfileDetailDialog(tableViewerProfiles.getControl().getShell());
				dialog.create();
				dialog.setName(profile.getName());
				dialog.setDescription(profile.getDescription());
				dialog.setValidFrom(profile.getValidFrom());
				// dialog.setValidTo(profile.getValidTo());

				if (dialog.open() == Window.OK) {

					// CstProfile.getByNameAndPatientAndMandant(dialog.getName(), kontaktId,
					// mandantId)
					/*
					 * CstProfile existProf =
					 * CstProfile.getByNameAndPatientAndMandant(dialog.getName(), patient.getId(),
					 * CoreHub.actMandant.getId());
					 *
					 * //if (existProf != null && !profile.getId().equals(existProf.getId())) { if
					 * (existProf != null) {
					 *
					 * showMessage(Messages.Cst_Text_cstprofile_exists); return; }
					 */
					profile.setName(dialog.getName());
					profile.setDescription(dialog.getDescription());
					profile.setValidFrom(dialog.getValidFrom());
					// profile.setValidTo(dialog.getValidTo());
					loadProfileData();
					tableViewerProfiles.setSelection(selection);
				}
			}
		};
	}

	private CstProfile getSelectedProfile() {
		TableItem[] selItem = tableProfile.getSelection();
		if (selItem.length == 0) {
			return null;
		} else {
			CstProfile selProf = (CstProfile) selItem[0].getData();
			return selProf;
		}
	}

	/**
	 *
	 * TODO: does not work all the time!!! there are sometimes null values in the
	 * table
	 */
	@SuppressWarnings("unchecked")
	private void reinitRanking(CstProfile selGroup) {
		cstGroups = selGroup.getCstGroups();
		Hashtable<Object, Object> ranking = new Hashtable<Object, Object>();

		int x = 1;
		for (CstGroup item : cstGroups) {
			ranking.put(item.getId(), x++);
		}

		itemRanking = (Map<Object, Object>) ranking.clone();
		selGroup.setMap(CstProfile.ITEMRANKING, ranking);

		// tableViewerCstGroups.refresh();
	}

	private void hookDoubleClickAction() {
		tableViewerProfiles.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(tableViewerProfiles.getControl().getShell(), "CST View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewerProfiles.getControl().setFocus();
		Patient prevPatient = patient;

		patient = ElexisEventDispatcher.getSelectedPatient();

		if (patient == null) {
			log.info("kein patient ausgew�hlt", Log.INFOS);
			/*
			 * MessageEvent.fireError("Kein Patient ausgew�hlt",
			 * "Bitte w�hlen Sie zuerst einen Patienten aus");
			 */
		} else {
			log.info("patient ausgew�hlt" + patient.getName(), Log.INFOS);
			labelLeft.setText(Messages.Cst_Text_Profile_fuer + StringUtils.SPACE + patient.getName() + StringUtils.SPACE
					+ patient.getVorname());

			if (prevPatient != null && !prevPatient.getId().toString().equals(patient.getId().toString())) {
				loadProfileData();
			}
		}

	}

	/*
	 * The content provider class is responsible for providing objects to the view.
	 * It can wrap existing objects in adapters or simply return objects as-is.
	 * These objects may be sensitive to the current input of the view, or ignore it
	 * and always show the same content (like Task List, for example).
	 */

	class ProfileContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {

			return cstProfiles.toArray();
		}
	}

	class ProfileLabelProvider extends LabelProvider
			implements ITableLabelProvider, ITableFontProvider, IColorProvider {

		public String getColumnText(Object obj, int index) {
			CstProfile cstProfile = (CstProfile) obj;
			switch (index) {
			case 0:
				return cstProfile.getName();
			case 1:
				return cstProfile.getDescription();
			case 2:
				return CstService.parseCompactDate(cstProfile.getValidFrom());
			case 3:
				return CstService.parseCompactDate(cstProfile.getValidTo());
			default:
				return StringUtils.EMPTY;
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Font getFont(Object element, int columnIndex) {
			Font font = null;
			return font;
		}

		@Override
		public Color getForeground(Object element) {
			CstProfile cstProfile = (CstProfile) element;
			if (cstProfile.getTemplate().equals("1")) {
				return UiDesk.getColorFromRGB("ff0000");
			}
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}
	}

	class ProfileSortListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			TableColumn col = (TableColumn) e.getSource();

			Integer colNo = (Integer) col.getData();

			if (colNo != null) {
				if (colNo == sortColumn) {
					sortReverse = !sortReverse;
				} else {
					sortReverse = false;
					sortColumn = colNo;
				}
				tableViewerProfiles.refresh();
			}
		}

	}

	class ProfileSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof CstProfile) && (e2 instanceof CstProfile)) {
				CstProfile d1 = (CstProfile) e1;
				CstProfile d2 = (CstProfile) e2;
				String c1 = StringUtils.EMPTY;
				String c2 = StringUtils.EMPTY;
				switch (sortColumn) {
				case 0:
					c1 = d1.getName();
					c2 = d2.getName();
					break;
				case 1:
					c1 = d1.getDescription();
					c2 = d2.getDescription();
					break;
				case 2:
					c1 = d1.getValidFrom();
					c2 = d2.getValidFrom();
					break;
				}
				if (sortReverse) {
					return c1.compareTo(c2);
				} else {
					return c2.compareTo(c1);
				}
			}
			return 0;
		}

	}

	class CstGroupContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {

			TableItem[] selItem = tableProfile.getSelection();
			if (selItem.length == 0) {
				return cstGroups.toArray();
			} else {
				CstProfile selProf = (CstProfile) selItem[0].getData();
				return selProf.getCstGroups().toArray();
			}

		}
	}

	class CstGroupLabelProvider extends LabelProvider
			implements ITableLabelProvider, ITableFontProvider, IColorProvider {
		public String getColumnText(Object obj, int index) {
			CstGroup labItem = (CstGroup) obj;
			switch (index) {
			case 0:
				return labItem.getName();
			case 1:
				return labItem.getDescription();
			case 2:

				Object ranking = itemRanking.get(labItem.getId());
				if (ranking == null) {
					// showMessage("Error with Ranking. Reinitializing...");
					reinitRanking(getSelectedProfile());
				}

				return String.valueOf(itemRanking.get(labItem.getId()));
			default:
				return "?";
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

		public Font getFont(Object element, int columnIndex) {
			Font font = null;
			return font;
		}

		@Override
		public Color getForeground(Object element) {
			return null;
		}

		@Override
		public Color getBackground(Object element) {
			return null;
		}
	}

	class CstGroupSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 instanceof CstGroup) && (e2 instanceof CstGroup)) {
				CstGroup d1 = (CstGroup) e1;
				CstGroup d2 = (CstGroup) e2;

				/*
				 * for debugging, there were records that did not point to an existing cstgroup
				 * in cstgroup_profile_joint
				 *
				 *
				 * if (d1.getName() == null || d2.getName() == null) { System.out.println("d1: "
				 * + d1.getId()); System.out.println("d2: " + d2.getId());
				 *
				 * }
				 */

				Integer r1 = (Integer) itemRanking.get(d1.getId());
				Integer r2 = (Integer) itemRanking.get(d2.getId());
				if (r1 == null || r2 == null) {
					return 0;
				}

				return r1.compareTo(r2);

			}
			return 0;
		}

	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}