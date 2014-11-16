/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.views;

import static ch.elexis.core.data.events.ElexisEvent.EVENT_DELETE;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_DESELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_SELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_UPDATE;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.Iatrix;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.KonsTextLock;
import org.iatrix.data.Problem;
import org.iatrix.dialogs.ChooseKonsRevisionDialog;
import org.iatrix.widgets.KonsListDisplay;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.model.ISticker;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.actions.ICodeSelectorTarget;
import ch.elexis.core.ui.constants.ExtensionPointConstantsUi;
import ch.elexis.core.ui.contacts.views.PatientDetailView2;
import ch.elexis.core.ui.data.UiSticker;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.dialogs.MediDetailDialog;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.codesystems.DiagnosenView;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.elexis.core.ui.views.rechnung.AccountView;
import ch.elexis.core.ui.views.rechnung.BillSummary;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Anwender;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Sticker;
import ch.elexis.data.Verrechnet;
import ch.elexis.extdoc.util.Email;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionedResource;
import ch.rgw.tools.VersionedResource.ResourceItem;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickListener;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.FixedCellRenderer;

/**
 * KG-Ansicht nach Iatrix-Vorstellungen
 * 
 * Oben wird die Problemliste dargestellt, unten die aktuelle Konsultation und die bisherigen
 * Konsultationen. Hinweis: Es wird sichergestellt, dass die Problemliste und die Konsultation(en)
 * zum gleichen Patienten gehoeren.
 * 
 * TODO Definieren, wann welcher Patient und welche Konsultation gesetzt werden soll. Wie mit
 * Faellen umgehen? TODO adatpMenu as in KonsDetailView TODO check compatibility of assigned
 * problems if fall is changed
 * 
 * @author Daniel Lutz <danlutz@watz.ch>
 */

public class JournalView extends ViewPart implements IActivationListener, ISaveablePart2,
		HeartListener {

	public static final String ID = "org.iatrix.views.JournalView"; //$NON-NLS-1$

	private static final String VIEW_CONTEXT_ID = "org.iatrix.view.context"; //$NON-NLS-1$
	private static final String NEWCONS_COMMAND = "org.iatrix.commands.newcons"; //$NON-NLS-1$
	private static final String NEWPROBLEM_COMMAND = "org.iatrix.commands.newproblem"; //$NON-NLS-1$
	private static final String EXPORT_CLIPBOARD_COMMAND = "org.iatrix.commands.export_clipboard"; //$NON-NLS-1$
	private static final String EXPORT_SEND_EMAIL_COMMAND = "org.iatrix.commands.send_email"; //$NON-NLS-1$

	// org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds.SAVE
	private static final String SAVE_COMMAND = "org.eclipse.ui.file.save";

	private static final String UNKNOWN = "(unbekannt)";

	private static final DateComparator DATE_COMPARATOR = new DateComparator();
	private static final NumberComparator NUMBER_COMPARATOR = new NumberComparator();
	private static final StatusComparator STATUS_COMPARATOR = new StatusComparator();

	static Log log = Log.get(JournalView.class.getSimpleName());

	private Patient actPatient = null;
	private Konsultation actKons;
	private static boolean creatingKons = false;
	private static String savedInitialKonsText = null;

	private Hashtable<String, IKonsExtension> hXrefs;

	private final DecimalFormat df = new DecimalFormat("0.00");

	private EnhancedTextField text;
	/*
	 * private Label lKonsultation;
	 */
	private Hyperlink hlKonsultationDatum;
	private Hyperlink hlMandant;
	private Label lVersion;
	private Label lKonsLock;
	private Combo cbFall;

	// container for hKonsultationDatum, hlMandant, cbFall
	private Composite konsFallArea;

	private FormToolkit tk;
	private Form form;

	private Hyperlink formTitel;
	private Label remarkLabel;
	private Label stickerLabel;
	private Image sticker;
	private Label kontoLabel;
	private Color kontoLabelColor; // original color of kontoLabel

	// private Label lProbleme;
	private Hyperlink hVerrechnung;
	private Text tVerrechnungKuerzel;

	private Action versionBackAction;
	private Action purgeAction;
	private Action versionFwdAction;
	private Action chooseVersionAction;
	private Action saveAction;

	int displayedVersion;

	private MyKTable problemsKTable;
	private ProblemsTableModel problemsTableModel;
	private ProblemsTableColorProvider problemsTableColorProvider;

	// column indices
	private static final int DATUM = 0;
	private static final int NUMMER = 1;
	private static final int BEZEICHNUNG = 2;
	private static final int THERAPIE = 3;
	private static final int DIAGNOSEN = 4;
	/*
	 * private static final int GESETZ = 5; private static final int STATUS = 6;
	 */
	private static final int STATUS = 5;

	private static final String[] COLUMN_TEXT = {
		"Datum", // DATUM
		"Nr.", // NUMMER
		"Problem/Diagnose", // BEZEICHNUNG
		"Proc./Medic.", // THERAPIE
		"Code", // DIAGNOSEN
		/*
		 * "Fall", // GESETZ
		 */
		""
	};

	private static final String[] TOOLTIP_TEXT = {
		"Was soll niklaus hier schreiben (row 0)",
		"Datum/Dauer des Ereignis, z.B. 2013-12-30, 1999-11, 1988, 1988-2010", // DATUM
		"Nummer des Ereignis", // NUMMER
		"Geschildertes Problem, Diagnose", // BEZEICHNUNG
		"Therapie, Medikation", // THERAPIE
		"Was soll niklaus hier schreiben (row 5)", "Was soll niklaus hier schreiben (row 6)",
		"Was soll niklaus hier schreiben (row 7)",
	};

	private static final int[] DEFAULT_COLUMN_WIDTH = {
		80, // DATUM
		30, // NUMMER
		120, // BEZEICHNUNG
		120, // THERAPIE
		80, // DIAGNOSEN
		/*
		 * 40, // GESETZ
		 */
		20
	// STATUS
		};

	private static final String CFG_BASE_KEY = "org.iatrix/views/journalview/column_width";
	private static final String[] COLUMN_CFG_KEY = {
		CFG_BASE_KEY + "/" + "date", // DATUM
		CFG_BASE_KEY + "/" + "number", // NUMMER
		CFG_BASE_KEY + "/" + "description", // BEZEICHNUNG
		CFG_BASE_KEY + "/" + "therapy", // THERAPIE
		CFG_BASE_KEY + "/" + "diagnoses", // DIAGNOSEN
		/*
		 * CFG_BASE_KEY + "/" + "law", // GESETZ
		 */

		CFG_BASE_KEY + "/" + "status", // STATUS
	};

	private CheckboxTableViewer problemAssignmentViewer;
	private TableViewer verrechnungViewer;
	private Color verrechnungViewerColor; // original color of verrechnungViewer

	private CLabel lDiagnosis;

	// HistoryDisplay history;
	KonsListDisplay konsListDisplay;

	private Action showAllChargesAction;
	private Action showAllConsultationsAction;

	private ViewMenus menus;

	/* Actions */
	private IAction exportToClipboardAction;
	private IAction sendEmailAction;

	/* Konsultation */
	private IAction addKonsultationAction;

	/* problemsTableViewer */
	private IAction addProblemAction;
	private IAction delProblemAction;
	private IAction addFixmedikationAction;
	private IAction editFixmedikationAction;
	private IAction deleteFixmedikationAction;

	/* problemAssignmentViewer */
	private IAction unassignProblemAction;

	/* verrechnungViewer */
	private IAction delVerrechnetAction;
	private IAction changeVerrechnetPreisAction;
	private IAction changeVerrechnetZahlAction;

	private ICodeSelectorTarget problemDiagnosesCodeSelectorTarget;
	private ICodeSelectorTarget problemFixmedikationCodeSelectorTarget;
	private ICodeSelectorTarget konsultationVerrechnungCodeSelectorTarget;
	private Color normalColor;
	private Color highlightColor;

	/*
	 * Heartbeat activation management The heartbeat events are only processed if these variables
	 * are set to true. They may be set to false if heartbeat processing would distrub, e. g. in
	 * case of editing a problem or the consultation text.
	 */
	private boolean heartbeatProblemEnabled = true;
	private boolean konsEditorHasFocus = false;
	private KonsTextLock konsTextLock = null;

	private boolean heartbeatActive = false;

	private int konsTextSaverCount = 0;

	/**
	 * Flag indicating if there are more than one mandants. This variable is initially set in
	 * createPartControl().
	 */
	private boolean hasMultipleMandants = false;

	/**
	 * Initialize hasMultipleMandants variable
	 */
	private void initHasMultipleMandants(){
		Query<Mandant> query = new Query<Mandant>(Mandant.class);
		List<Mandant> list = query.execute();
		if (list != null && list.size() > 1) {
			hasMultipleMandants = true;
		}
	}
	
	private final ElexisUiEventListenerImpl eeli_problem = new ElexisUiEventListenerImpl(
		Episode.class, EVENT_UPDATE | EVENT_DESELECTED) {

		@Override
		public void runInUi(ElexisEvent ev){
			switch (ev.getType()) {
			case EVENT_UPDATE:
				// problem change may affect current problems list and consultation
				// TODO check if problem is part of current consultation
				// work-around: just update the current patient and consultation
				setPatient(actPatient);
				updateKonsultation(!konsEditorHasFocus, false);
				break;
			case EVENT_DESELECTED:
				problemsKTable.clearSelection();
				break;
			}

		}
	};
	
	private final ElexisUiEventListenerImpl eeli_kons = new ElexisUiEventListenerImpl(
		Konsultation.class, EVENT_DELETE | EVENT_UPDATE | EVENT_SELECTED | EVENT_DESELECTED) {

		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation k = (Konsultation) ev.getObject();
			switch (ev.getType()) {
			case EVENT_UPDATE:
				if (actKons != null && k.getId().equals(actKons.getId())) {
					updateKonsultation(!konsEditorHasFocus, false);
				}

				break;
			case EVENT_DELETE:
				if (actKons != null && k.getId().equals(actKons.getId())) {
					setKonsultation(null, false);
				}
				break;
			case EVENT_SELECTED:
				Patient patient = k.getFall().getPatient();
				setPatient(patient);
				setKonsultation(k, true);
				break;
			case EVENT_DESELECTED:
				setKonsultation(null, true);
				break;
			}

		}

	};

	private final ElexisUiEventListenerImpl eeli_fall = new ElexisUiEventListenerImpl(Fall.class,
		ElexisEvent.EVENT_SELECTED) {
		@Override
		public void runInUi(ElexisEvent ev){
			Fall fall = (Fall) ev.getObject();
			Patient patient = fall.getPatient();

			// falls aktuell ausgewaehlte Konsulation zu diesem Fall
			// gehoert,
			// diese setzen
			Konsultation konsulation =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (konsulation != null) {
				if (konsulation.getFall().getId().equals(fall.getId())) {
					// diese Konsulation gehoert zu diesem Patienten

					setPatient(patient);
					setKonsultation(konsulation, true);

					return;
				}
			}

			// sonst die aktuellste Konsulation des Falls setzen
			konsulation = getTodaysLatestKons(fall);

			setPatient(patient);
			setKonsultation(konsulation, true);

		}
	};
	private final ElexisUiEventListenerImpl eeli_pat =
		new ElexisUiEventListenerImpl(Patient.class) {
			
			@Override
			public void runInUi(ElexisEvent ev){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					Patient selectedPatient = (Patient) ev.getObject();
					
					showAllChargesAction.setChecked(false);
					showAllConsultationsAction.setChecked(false);
					
					Patient patient = null;
					Fall fall = null;
					Konsultation konsultation = null;
					
					konsultation =
						(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
					if (konsultation != null) {
						// diese Konsulation setzen, falls sie zum ausgewaehlten
						// Patienten gehoert
						fall = konsultation.getFall();
						patient = fall.getPatient();
						if (patient.getId().equals(selectedPatient.getId())) {
							setPatient(patient);
							setKonsultation(konsultation, true);
							
							return;
						}
					}
					
					// Konsulation gehoert nicht zu diesem Patienten, Fall
					// untersuchen
					fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
					if (fall != null) {
						patient = fall.getPatient();
						if (patient.getId().equals(selectedPatient.getId())) {
							// aktuellste Konsultation dieses Falls waehlen
							konsultation = getTodaysLatestKons(fall);
							
							setPatient(patient);
							setKonsultation(konsultation, true);
							
							return;
						}
					}
					
					// weder aktuell ausgewaehlte Konsulation noch aktuell
					// ausgewaehlter Fall gehoeren zu diesem Patienten
					setPatient(selectedPatient);
					
					// lezte Kons setzen, falls heutiges Datum
					Konsultation letzteKons = getTodaysLatestKons(selectedPatient);
					if (letzteKons != null) {
						TimeTool letzteKonsDate = new TimeTool(letzteKons.getDatum());
						TimeTool today = new TimeTool();
						if (!letzteKonsDate.isSameDay(today)) {
							letzteKons = null;
						}
						setKonsultation(letzteKons, true);
					} else {
						setKonsultation(null, true);
					}
					
				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					setPatient(null);
				}
			}
			
		};
	
	private final ElexisUiEventListenerImpl eeli_user = new ElexisUiEventListenerImpl(
		Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
		@Override
		public void runInUi(ElexisEvent ev){
			adaptMenus();
		}

	};

	@Override
	public void createPartControl(Composite parent){
		initHasMultipleMandants();

		// ICodeSelectorTarget for diagnoses in problems list
		problemDiagnosesCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return JournalView.this.getPartName();
			}

			@Override
			public void codeSelected(PersistentObject po){
				if (po instanceof IDiagnose) {
					IDiagnose diagnose = (IDiagnose) po;

					Problem problem = getSelectedProblem();
					if (problem != null) {
						problem.addDiagnose(diagnose);
						IatrixEventHelper.updateProblem(problem);

						if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
							Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
							// re-activate this view
							try {
								getViewSite().getPage().showView(JournalView.ID);
							} catch (Exception ex) {
								ExHandler.handle(ex);
								log.log("Fehler beim Öffnen von JournalView: " + ex.getMessage(),
									Log.ERRORS);
							}
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				highlightProblemsTable(registered);
			}
		};

		// ICodeSelectorTarget for fixmedikation in problems list
		problemFixmedikationCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return JournalView.this.getPartName();
			}

			@Override
			public void codeSelected(PersistentObject po){
				Problem problem = getSelectedProblem();
				if (problem != null) {
					if (po instanceof Artikel) {
						Artikel artikel = (Artikel) po;

						Prescription prescription =
							new Prescription(artikel, problem.getPatient(), "", "");
						problem.addPrescription(prescription);

						// Let the user set the Prescription properties

						MediDetailDialog dlg =
							new MediDetailDialog(getViewSite().getShell(), prescription);
						dlg.open();

						// tell other viewers that something has changed
						IatrixEventHelper.updateProblem(problem);

						if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
							Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
							// re-activate this view
							try {
								getViewSite().getPage().showView(JournalView.ID);
							} catch (Exception ex) {
								ExHandler.handle(ex);
								log.log("Fehler beim Öffnen von JournalView: " + ex.getMessage(),
									Log.ERRORS);
							}
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				if (registered) {
					highlightProblemsTable(true, true);
				} else {
					highlightProblemsTable(false);
				}
			}
		};

		// ICodeSelectorTarget for Verrechnung in consultation area
		konsultationVerrechnungCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return JournalView.this.getPartName();
			}

			@Override
			public void codeSelected(PersistentObject po){
				if (po instanceof IVerrechenbar) {
					if (CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN) == false) {
						SWTHelper.alert("Fehlende Rechte",
							"Sie haben nicht die Berechtigung, Leistungen zu verrechnen");
					} else {
						IVerrechenbar verrechenbar = (IVerrechenbar) po;

						if (actKons != null) {
							Result<IVerrechenbar> result = actKons.addLeistung(verrechenbar);
							if (!result.isOK()) {
								SWTHelper
									.alert("Diese Verrechnung ist ungültig", result.toString());
							} else {
								if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
									Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
									// re-activate this view
									try {
										getViewSite().getPage().showView(JournalView.ID);
									} catch (Exception ex) {
										ExHandler.handle(ex);
										log.log(
											"Fehler beim Öffnen von JournalView: "
												+ ex.getMessage(), Log.ERRORS);
									}
								}
							}
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				highlightVerrechnung(registered);
			}
		};

		parent.setLayout(new FillLayout());

		tk = UiDesk.getToolkit();
		form = tk.createForm(parent);
		Composite formBody = form.getBody();

		formBody.setLayout(new GridLayout(1, true));

		// highlighting colors for ICodeSelectorTarget
		normalColor = form.getBackground();
		highlightColor = form.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

		Composite formHeader = new Composite(formBody, SWT.NONE);
		tk.adapt(formHeader);
		formHeader.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		formHeader.setLayout(new GridLayout(3, false));

		GridData gd;

		formTitel = tk.createHyperlink(formHeader, "Iatrix KG", SWT.WRAP);

		// set font
		formTitel.setFont(JFaceResources.getHeaderFont());

		formTitel.setText("Kein Patient ausgewählt");
		formTitel.setEnabled(false);
		formTitel.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				if (actPatient != null) {
					try {
						getViewSite().getPage().showView(PatientDetailView2.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.log("Fehler beim Öffnen von PatientDetailView: " + ex.getMessage(),
							Log.ERRORS);
					}
				}
			}
		});

		Composite patInfoArea = tk.createComposite(formHeader);
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		patInfoArea.setLayoutData(gd);
		GridLayout infoLayout = new GridLayout(2, false);
		// save space
		infoLayout.horizontalSpacing = 5;
		infoLayout.verticalSpacing = 0;
		infoLayout.marginWidth = 0;
		infoLayout.marginHeight = 0;
		patInfoArea.setLayout(infoLayout);

		remarkLabel = tk.createLabel(patInfoArea, "");
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		remarkLabel.setLayoutData(gd);
		remarkLabel.setBackground(patInfoArea.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		remarkLabel.setToolTipText("Bemerkung kann via Doppelclick geändert werden");
		remarkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e){
				openRemarkEditorDialog();
			}
		});

		stickerLabel = tk.createLabel(patInfoArea, "");
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		sticker = Images.IMG_MANN.getImage();
		stickerLabel.setImage(sticker);
		stickerLabel.setLayoutData(gd);
		stickerLabel.setToolTipText("Sticker des Patienten");
		stickerLabel.setLayoutData(gd);

		Composite kontoArea = tk.createComposite(formHeader);
		gd = new GridData(SWT.END, SWT.CENTER, true, false);
		kontoArea.setLayoutData(gd);
		GridLayout gridLayout = new GridLayout(2, false);
		// save space
		gridLayout.horizontalSpacing = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		kontoArea.setLayout(gridLayout);

		Hyperlink kontoHyperlink = tk.createHyperlink(kontoArea, "Kontostand:", SWT.NONE);
		kontoHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				if (actPatient != null) {
					try {
						getViewSite().getPage().showView(AccountView.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.log("Fehler beim Öffnen von AccountView: " + ex.getMessage(),
							Log.ERRORS);
					}
				}
			}
		});
		kontoLabel = tk.createLabel(kontoArea, "", SWT.RIGHT);
		gd = SWTHelper.getFillGridData(1, true, 1, false);
		gd.verticalAlignment = GridData.END;
		kontoLabel.setLayoutData(gd);
		kontoLabelColor = kontoLabel.getForeground();

		Hyperlink openBillsHyperlink =
			tk.createHyperlink(kontoArea, "Rechnungsübersicht", SWT.NONE);
		openBillsHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				if (actPatient != null) {
					try {
						getViewSite().getPage().showView(BillSummary.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.log("Fehler beim Öffnen von AccountView: " + ex.getMessage(),
							Log.ERRORS);
					}
				}
			}
		});
		openBillsHyperlink.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		SashForm mainSash = new SashForm(form.getBody(), SWT.VERTICAL);
		mainSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite topArea = tk.createComposite(mainSash, SWT.NONE);
		topArea.setLayout(new FillLayout(SWT.VERTICAL));
		topArea.setBackground(topArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		createProblemsTable(topArea);

		Composite middleArea = tk.createComposite(mainSash, SWT.NONE);
		middleArea.setLayout(new FillLayout());

		createKonsultationArea(middleArea);

		Composite bottomArea = tk.createComposite(mainSash, SWT.NONE);
		bottomArea.setLayout(new FillLayout());
		bottomArea.setBackground(bottomArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		createHistory(bottomArea);

		mainSash.setWeights(new int[] {
			20, 40, 30
		});

		makeActions();
		menus = new ViewMenus(getViewSite());
		if (CoreHub.acl.request(AccessControlDefaults.AC_PURGE)) {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction, addProblemAction,
				GlobalActions.delKonsAction, delProblemAction, exportToClipboardAction,
				sendEmailAction, versionFwdAction, versionBackAction, chooseVersionAction,
				purgeAction, saveAction, showAllConsultationsAction, showAllChargesAction,
				addFixmedikationAction);
		} else {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction, addProblemAction,
				GlobalActions.delKonsAction, delProblemAction, exportToClipboardAction,
				sendEmailAction, versionFwdAction, versionBackAction, chooseVersionAction,
				saveAction, showAllConsultationsAction, showAllChargesAction,
				addFixmedikationAction);
		}

		menus.createToolbar(sendEmailAction, exportToClipboardAction, addKonsultationAction,
			addProblemAction, saveAction);
		menus.createControlContextMenu(problemsKTable, addFixmedikationAction,
			editFixmedikationAction, deleteFixmedikationAction);
		menus.createViewerContextMenu(problemAssignmentViewer, unassignProblemAction);
		menus.createViewerContextMenu(verrechnungViewer, changeVerrechnetPreisAction,
			changeVerrechnetZahlAction, delVerrechnetAction);

		GlobalEventDispatcher.addActivationListener(this, this);

		activateContext();
	}

	/**
	 * Activate a context that this view uses. It will be tied to this view activation events and
	 * will be removed when the view is disposed. Copied from
	 * org.eclipse.ui.examples.contributions.InfoView.java
	 */
	private void activateContext(){
		IContextService contextService =
			(IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(VIEW_CONTEXT_ID);
	}

	private void createProblemsTable(Composite parent){
		problemsKTable =
			new MyKTable(parent, SWTX.MARK_FOCUS_HEADERS | SWTX.AUTO_SCROLL
				| SWTX.FILL_WITH_DUMMYCOL | SWTX.EDIT_ON_KEY);

		tk.adapt(problemsKTable);

		problemsTableModel = new ProblemsTableModel();
		problemsTableColorProvider = new ProblemsTableColorProvider();

		problemsKTable.setModel(problemsTableModel);

		// selections
		problemsKTable.addCellSelectionListener(new KTableCellSelectionListener() {
			@Override
			public void cellSelected(int col, int row, int statemask){
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);
				if (problem != null) {
					IatrixEventHelper.fireSelectionEventProblem(problem);
				} else {
					IatrixEventHelper.clearSelectionProblem();
				}
			}

			@Override
			public void fixedCellSelected(int col, int row, int statemask){
				problemsTableModel.setComparator(col, row);

				problemsTableModel.reload();
				problemsKTable.refresh();
			}
		});

		// clear selection when ESC is pressed
		problemsKTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				if (e.keyCode == SWT.ESC) {
					problemsKTable.clearSelection();
					// work-around: KTable doesn't redraw in single selection mode
					problemsKTable.redraw();

					IatrixEventHelper.clearSelectionProblem();
				} else if ((e.character == ' ') || (e.character == '\r')) {
					// Work-around for opening the diagnosis selector on ENTER
					// or changing the status.
					// KTable supports only cell editors based on a Control.
					// So we just catch this event ourselves and assume that KTable
					// hasn't processed it in KTable.onKeyDown().

					if ((e.stateMask & SWT.CTRL) == 0) {
						// plain SPACE or ENTER

						// This is actually the same code as in the double click listener

						Point[] selection = problemsKTable.getCellSelection();
						if (selection.length == 1) {
							int col = selection[0].x;
							int row = selection[0].y;

							int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();
							int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
							Problem problem = problemsTableModel.getProblem(rowIndex);

							switch (colIndex) {
							case DIAGNOSEN:
								// open diagnosis selector

								if (problem != null) {
									try {
										getViewSite().getPage().showView(DiagnosenView.ID);
										// register as ICodeSelectorTarget
										CodeSelectorHandler.getInstance().setCodeSelectorTarget(
											problemDiagnosesCodeSelectorTarget);
									} catch (Exception ex) {
										ExHandler.handle(ex);
										log.log(
											"Fehler beim Starten des Diagnosencodes "
												+ ex.getMessage(), Log.ERRORS);
									}
								}
								break;

							case STATUS:
								// change status when status field has been double clicked

								if (problem != null) {
									if (problem.getStatus() == Episode.ACTIVE) {
										problem.setStatus(Episode.INACTIVE);
									} else {
										problem.setStatus(Episode.ACTIVE);
									}

									problemsKTable.refresh();
									if (actKons != null) {
										// only active problems are to be shown
										problemAssignmentViewer.refresh();
									}
								}
								break;
							}
						}
					} else {
						// SPACE or ENTER with CTRL

						Point[] selection = problemsKTable.getCellSelection();
						if (selection.length == 1) {
							int col = selection[0].x;
							int row = selection[0].y;

							int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();

							switch (colIndex) {
							case DIAGNOSEN:
								KTableCellEditor editor =
									problemsTableModel.getCellEditor(col, row);
								if (editor != null
									&& (editor.getActivationSignals() & KTableCellEditor.KEY_RETURN_AND_SPACE) != 0
									&& editor.isApplicable(KTableCellEditor.KEY_RETURN_AND_SPACE,
										problemsKTable, col, row, null, e.character + "",
										e.stateMask)) {

									problemsKTable.openEditorInFocus();
								}
								break;
							}
						}
					}
				}
			}
		});

		problemsKTable.addCellDoubleClickListener(new KTableCellDoubleClickListener() {
			@Override
			public void cellDoubleClicked(int col, int row, int statemask){
				int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);

				switch (colIndex) {
				case DIAGNOSEN:
					// open diagnosis selector

					if (problem != null) {
						try {
							getViewSite().getPage().showView(DiagnosenView.ID);
							// register as ICodeSelectorTarget
							CodeSelectorHandler.getInstance().setCodeSelectorTarget(
								problemDiagnosesCodeSelectorTarget);
						} catch (Exception ex) {
							ExHandler.handle(ex);
							log.log("Fehler beim Starten des Diagnosencodes " + ex.getMessage(),
								Log.ERRORS);
						}
					}
					break;

				case STATUS:
					// change status when status field has been double clicked

					if (problem != null) {
						if (problem.getStatus() == Episode.ACTIVE) {
							problem.setStatus(Episode.INACTIVE);
						} else {
							problem.setStatus(Episode.ACTIVE);
						}

						problemsKTable.refresh();
						if (actKons != null) {
							// only active problems are to be shown
							problemAssignmentViewer.refresh();
						}
					}
					break;
				}
			}

			@Override
			public void fixedCellDoubleClicked(int col, int row, int statemask){
				// nothing to do
			}
		});
		/*
		 * Sortiert die Probleme nach Datum, aktuelle zuerst
		 */
		/*
		 * problemsTableDatumComparator = new ViewerComparator() { public int compare(Viewer viewer,
		 * Object e1, Object e2) { if (!(e1 instanceof Problem)) { return 1; } if (!(e2 instanceof
		 * Problem)) { return -1; }
		 *
		 * Problem p1 = (Problem) e1; Problem p2 = (Problem) e2;
		 *
		 * String datum1 = p1.getDatum(); String datum2 = p2.getDatum();
		 *
		 * if (datum1.equals(datum2)) { // datum ist identisch, nach Nummer sortieren
		 *
		 * String nummer1 = p1.getNummer(); String nummer2 = p2.getNummer();
		 *
		 * return nummer1.compareTo(nummer2); }
		 *
		 * return datum2.compareTo(datum1); } };
		 *
		 * problemsTableNummerComparator = new ViewerComparator() { public int compare(Viewer
		 * viewer, Object e1, Object e2) { if (!(e1 instanceof Problem)) { return 1; } if (!(e2
		 * instanceof Problem)) { return -1; }
		 *
		 * Problem p1 = (Problem) e1; Problem p2 = (Problem) e2;
		 *
		 * String nummer1 = p1.getNummer(); String nummer2 = p2.getNummer();
		 *
		 * return nummer1.compareTo(nummer2); } };
		 *
		 * problemsTableViewer.setComparator(problemsTableDatumComparator);
		 */
		/*
		 * Table table = problemsTableViewer.getTable(); table.setHeaderVisible(true);
		 * table.setLinesVisible(true);
		 *
		 * problemsTableViewer.setInput(this);
		 *
		 * TableColumn[] tc = new TableColumn[COLUMN_TEXT.length]; for (int i = 0; i <
		 * COLUMN_TEXT.length; i++) { tc[i] = new TableColumn(table, SWT.NONE);
		 * tc[i].setText(COLUMN_TEXT[i]); tc[i].setWidth(COLUMN_WIDTH[i]); }
		 *
		 * tc[DATUM].addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent event) {
		 * problemsTableViewer.setComparator(problemsTableDatumComparator); } });
		 *
		 * tc[NUMMER].addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent event) { problemsTableViewer
		 * .setComparator(problemsTableNummerComparator); } });
		 */
		/*
		 * CellEditor[] cellEditors = new CellEditor[7]; cellEditors[DATUM] = new
		 * TextCellEditor(table); cellEditors[NUMMER] = new TextCellEditor(table);
		 * cellEditors[BEZEICHNUNG] = new TextCellEditor(table); cellEditors[PROCEDERE] = new
		 * TextCellEditor(table);
		 *
		 * problemsTableViewer.setColumnProperties(PROPS); problemsTableViewer.setCellModifier(new
		 * ICellModifier() { public boolean canModify(Object element, String property) { if
		 * (property.equals(DATUM_PROP) || property.equals(NUMMER_PROP) ||
		 * property.equals(BEZEICHNUNG_PROP) || property.equals(PROCEDERE_PROP)) {
		 *
		 * return true; }
		 *
		 * return false; }
		 *
		 * public Object getValue(Object element, String property) { Problem problem = (Problem)
		 * element;
		 *
		 * if (property.equals(DATUM_PROP)) { return problem.getDatum(); } else if
		 * (property.equals(NUMMER_PROP)) { return problem.getNummer(); } else if
		 * (property.equals(BEZEICHNUNG_PROP)) { return problem.getBezeichnung(); } else if
		 * (property.equals(PROCEDERE_PROP)) { return problem.getProcedere(); } else { return null;
		 * } }
		 *
		 * public void modify(Object element, String property, Object value) { if (element
		 * instanceof Item) { element = ((Item) element).getData(); } Problem problem = (Problem)
		 * element;
		 *
		 * if (property.equals(DATUM_PROP)) { problem.setDatum((String) value); } else if
		 * (property.equals(NUMMER_PROP)) { problem.setNummer((String) value); } else if
		 * (property.equals(BEZEICHNUNG_PROP)) { problem.setBezeichnung((String) value); } else if
		 * (property.equals(PROCEDERE_PROP)) { problem.setProcedere((String) value); }
		 *
		 * problemsTableViewer.refresh(); refreshProblemAssignmentViewer(); } });
		 * problemsTableViewer.setCellEditors(cellEditors);
		 */
		/*
		 * problemsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() { public
		 * void selectionChanged(SelectionChangedEvent event) { Object sel = ((IStructuredSelection)
		 * problemsTableViewer.getSelection()).getFirstElement(); if (sel != null) { Problem problem
		 * = (Problem) sel; GlobalEvents.getInstance().fireSelectionEvent(problem); } } });
		 *
		 * problemsTableViewer.addDoubleClickListener(new IDoubleClickListener() { public void
		 * doubleClick(DoubleClickEvent event) { try {
		 * getViewSite().getPage().showView(ProblemView.ID); } catch (Exception ex) {
		 * ExHandler.handle(ex); log.log("Fehler beim Öffnen von ProblemView: " + ex.getMessage(),
		 * Log.ERRORS); } } });
		 */

		// Drag'n'Drop support
		// Quelle
		DragSource ds = new DragSource(problemsKTable, DND.DROP_COPY);
		ds.setTransfer(new Transfer[] {
			TextTransfer.getInstance()
		});
		ds.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragStart(DragSourceEvent event){
				Point cell = problemsKTable.getCellForCoordinates(event.x, event.y);
				int col = cell.x;
				int row = cell.y;
				// only handle normal columns/rows, no header columns/rows
				if (col >= problemsTableModel.getFixedHeaderColumnCount()
					&& row >= problemsTableModel.getFixedHeaderRowCount()) {
					Problem problem = getSelectedProblem();
					if (problem != null) {
						event.doit = problem.isDragOK();
					} else {
						event.doit = false;
					}
				} else {
					event.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event){
				// only add single selection
				Problem problem = getSelectedProblem();
				StringBuilder sb = new StringBuilder();
				if (problem != null) {
					sb.append(problem.storeToString()).append(",");
				}
				event.data = sb.toString().replace(",$", "");
			}
		});

		// Ziel
		DropTarget dt = new DropTarget(problemsKTable, DND.DROP_COPY);
		dt.setTransfer(new Transfer[] {
			TextTransfer.getInstance()
		});
		dt.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				/* Wir machen nur Copy-Operationen */
				event.detail = DND.DROP_COPY;
			}

			/* Mausbewegungen mit gedrückter Taste sind uns egal */
			@Override
			public void dragLeave(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOver(DropTargetEvent event){
				/* leer */
			}

			/* Erst das Loslassen interessiert uns wieder */
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);

					// we don't yet support dropping to the problemsKTable
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}
		});
	}

	private void createKonsultationArea(Composite parent){
		// parent has FillLayout

		Composite konsultationComposite = tk.createComposite(parent);
		konsultationComposite.setLayout(new GridLayout(1, true));

		konsFallArea = tk.createComposite(konsultationComposite);
		konsFallArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		konsFallArea.setLayout(new GridLayout(3, false));

		/*
		 * lKonsultation = tk.createLabel(konsFallArea, "Keine Konsultation ausgewählt");
		 * //lKonsultation.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		 * lKonsultation.setFont(JFaceResources.getHeaderFont());
		 */

		hlKonsultationDatum = tk.createHyperlink(konsFallArea, "", SWT.NONE);
		hlKonsultationDatum.setFont(JFaceResources.getHeaderFont());
		hlKonsultationDatum.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				GlobalActions.redateAction.run();
			}
		});

		if (hasMultipleMandants) {
			hlMandant = tk.createHyperlink(konsFallArea, "", SWT.NONE);
			hlMandant.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e){
					KontaktSelektor ksl =
						new KontaktSelektor(getSite().getShell(), Mandant.class,
							"Mandant auswählen", "Auf wen soll diese Kons verrechnet werden?",
							new String[] {
								Mandant.FLD_SHORT_LABEL, Mandant.FLD_NAME1, Mandant.FLD_NAME2
							});
					if (ksl.open() == Dialog.OK) {
						actKons.setMandant((Mandant) ksl.getSelection());
						updateKonsultation(true, false);
					}
				}

			});
		} else {
			hlMandant = null;
			// placeholder
			tk.createLabel(konsFallArea, "");
		}

		Composite fallArea = tk.createComposite(konsFallArea);
		// GridData gd = SWTHelper.getFillGridData(1, false, 1, false);
		// gd.horizontalAlignment = SWT.RIGHT;
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		fallArea.setLayoutData(gd);

		//
		fallArea.setLayout(new GridLayout(2, false));
		tk.createLabel(fallArea, "Fall:");
		cbFall = new Combo(fallArea, SWT.SINGLE | SWT.READ_ONLY);
		cbFall.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cbFall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Fall[] faelle = (Fall[]) cbFall.getData();
				int i = cbFall.getSelectionIndex();
				Fall nFall = faelle[i];
				Fall actFall = actKons.getFall();
				if (!nFall.getId().equals(actFall.getId())) {
					MessageDialog msd =
						new MessageDialog(getViewSite().getShell(), "Fallzuordnung ändern",
							Images.IMG_LOGO.getImage(), "Möchten Sie diese Behandlung vom Fall:\n'"
								+ actFall.getLabel() + "' zum Fall:\n'" + nFall.getLabel()
								+ "' transferieren?", MessageDialog.QUESTION, new String[] {
								"Ja", "Nein"
							}, 0);
					if (msd.open() == 0) {
						// TODO check compatibility of assigned problems
						actKons.setFall(nFall);
						updateKonsultation(true, false);
					}
				}
			}
		});
		tk.adapt(cbFall);
		cbFall.setEnabled(false);

		SashForm konsultationSash = new SashForm(konsultationComposite, SWT.HORIZONTAL);
		konsultationSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite assignmentComposite = tk.createComposite(konsultationSash);
		Composite konsultationTextComposite = tk.createComposite(konsultationSash);
		Composite verrechnungComposite = tk.createComposite(konsultationSash);
		konsultationSash.setWeights(new int[] {
			15, 65, 20
		});

		assignmentComposite.setLayout(new GridLayout(1, true));
		Label lProbleme = tk.createLabel(assignmentComposite, "Probleme", SWT.LEFT);
		lProbleme.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		problemAssignmentViewer = CheckboxTableViewer.newCheckList(assignmentComposite, SWT.SINGLE);
		Table problemAssignmentTable = problemAssignmentViewer.getTable();
		tk.adapt(problemAssignmentTable);
		problemAssignmentViewer.getControl().setLayoutData(
			SWTHelper.getFillGridData(1, true, 1, true));
		problemAssignmentViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				/*
				 * if (actKons != null) { List<Problem> problems = Problem
				 * .getProblemsOfKonsultation(actKons); return problems.toArray(); } return new
				 * Problem[0];
				 */

				if (actKons != null) {
					// get all problems of the current patient
					List<Problem> patientProblems =
						Problem.getProblemsOfPatient(actKons.getFall().getPatient());
					List<Problem> konsProblems = Problem.getProblemsOfKonsultation(actKons);

					// we only show active or assigned problems
					List<Problem> problems = new ArrayList<Problem>();

					// add active problems
					for (Problem problem : patientProblems) {
						if (problem.getStatus() == Episode.ACTIVE) {
							problems.add(problem);
						}
					}

					// add already assigned problems
					for (Problem problem : konsProblems) {
						if (!problems.contains(problem)) {
							problems.add(problem);
						}
					}

					// sort by date
					Collections.sort(problems, DATE_COMPARATOR);
					return problems.toArray();
				}

				return new Problem[] {};
			}

			@Override
			public void dispose(){
				// nothing to do
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				// nothing to do
			}
		});
		problemAssignmentViewer.setLabelProvider(new ProblemAssignmentLabelProvider());

		problemAssignmentViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event){
				if (actKons == null) {
					return;
				}

				Object element = event.getElement();
				if (element instanceof Problem) {
					Problem problem = (Problem) element;
					if (event.getChecked()) {
						problem.addToKonsultation(actKons);
					} else {
						// remove problem. ask user if encounter still contains data.
						IatrixViewTool.removeProblemFromKonsultation(actKons, problem);
					}
				}

				updateProblemAssignmentViewer();
				setDiagnosenText(actKons);
			}
		});
		problemAssignmentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel != null) {
					if (sel.size() == 1) {
						Object obj = sel.getFirstElement();
						if (obj instanceof Problem) {
							Problem problem = (Problem) obj;
							IatrixEventHelper.fireSelectionEventProblem(problem);

							// select corresponding encounter. This should actually be done by the
							// ICPC plugin via GlobalEvents
							Encounter encounter = problem.getEncounter(actKons);
							if (encounter != null) {
								ElexisEventDispatcher.fireSelectionEvent(encounter);
							} else {
								ElexisEventDispatcher.clearSelection(Encounter.class);
							}
						}
					}
				}
			}
		});

		problemAssignmentViewer.setInput(this);

		konsultationTextComposite.setLayout(new GridLayout(1, true));
		text = new EnhancedTextField(konsultationTextComposite);
		hXrefs = new Hashtable<String, IKonsExtension>();
		@SuppressWarnings("unchecked")
		List<IKonsExtension> listKonsextensions =
			Extensions.getClasses(
				Extensions.getExtensions(ExtensionPointConstantsUi.KONSEXTENSION), "KonsExtension", //$NON-NLS-1$ //$NON-NLS-2$
				false);
		for (IKonsExtension x : listKonsextensions) {
			String provider = x.connect(text);
			hXrefs.put(provider, x);
		}
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		text.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e){
				konsEditorHasFocus = true;
			}

			@Override
			public void focusLost(FocusEvent e){
				updateEintrag();

				konsEditorHasFocus = false;
			}
		});
		Control control = text.getControl();
		if (control instanceof StyledText) {
			StyledText styledText = (StyledText) control;

			styledText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e){
					// create new consultation if required
					handleInitialKonsText();
				}
			});
		}

		tk.adapt(text);

		lVersion = tk.createLabel(konsultationTextComposite, "<aktuell>");
		lVersion.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		lKonsLock = tk.createLabel(konsultationTextComposite, "");
		lKonsLock.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		lKonsLock.setForeground(lKonsLock.getDisplay().getSystemColor(SWT.COLOR_RED));
		lKonsLock.setVisible(false);

		verrechnungComposite.setLayout(new GridLayout(1, true));

		Composite verrechnungHeader = tk.createComposite(verrechnungComposite);
		verrechnungHeader.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		verrechnungHeader.setLayout(new GridLayout(2, false));

		hVerrechnung = tk.createHyperlink(verrechnungHeader, "Verrechnung", SWT.NONE);
		hVerrechnung.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hVerrechnung.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				try {
					getViewSite().getPage().showView(LeistungenView.ID);
					CodeSelectorHandler.getInstance().setCodeSelectorTarget(
						konsultationVerrechnungCodeSelectorTarget);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.log("Fehler beim Starten des Leistungscodes " + ex.getMessage(), Log.ERRORS);
				}
			}
		});
		hVerrechnung.setEnabled(false);

		tVerrechnungKuerzel = tk.createText(verrechnungHeader, "", SWT.BORDER);
		gd = new GridData(SWT.END);
		gd.widthHint = 50;
		tVerrechnungKuerzel.setLayoutData(gd);
		tVerrechnungKuerzel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String mnemonic = tVerrechnungKuerzel.getText();
				if (!StringTool.isNothing(mnemonic)) {
					// TODO evaluate return value, visualize errors
					addLeistungByMnemonic(mnemonic, false, false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				widgetSelected(e);
			}
		});
		tVerrechnungKuerzel.setEnabled(false);

		Table verrechnungTable = tk.createTable(verrechnungComposite, SWT.MULTI);
		verrechnungViewer = new TableViewer(verrechnungTable);
		verrechnungViewer.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		// used by hightlightVerrechnung()
		verrechnungViewerColor = verrechnungTable.getBackground();

		verrechnungViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				if (actKons != null) {
					List<Verrechnet> lgl = actKons.getLeistungen();
					return lgl.toArray();
				}
				return new Object[0];
			}

			@Override
			public void dispose(){
				// nothing to do
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
				// nothing to do
			}
		});
		verrechnungViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (!(element instanceof Verrechnet)) {
					return "";
				}

				Verrechnet verrechnet = (Verrechnet) element;
				StringBuilder sb = new StringBuilder();
				int z = verrechnet.getZahl();
				Money preis = new Money(verrechnet.getEffPreis()).multiply(z);
				// double preis = (z * verrechnet.getEffPreisInRappen()) / 100.0;
				sb.append(z).append(" ").append(verrechnet.getCode()).append(" ")
					.append(verrechnet.getText()).append(" (").append(preis.getAmountAsString())
					.append(")");
				return sb.toString();
			}

		});
		verrechnungViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				boolean enableDel = false;
				boolean enableChange = false;

				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel != null) {
					if (sel.size() >= 1) {
						enableDel = true;
					}
					if (sel.size() == 1) {
						enableChange = true;
					}
				}

				delVerrechnetAction.setEnabled(enableDel);
				changeVerrechnetZahlAction.setEnabled(enableChange);
				changeVerrechnetPreisAction.setEnabled(enableChange);
			}
		});
		verrechnungViewer.setInput(this);

		lDiagnosis = new CLabel(konsultationComposite, SWT.LEFT);
		lDiagnosis.setText("");
		tk.adapt(lDiagnosis);
		lDiagnosis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		/* Implementation Drag&Drop */

		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {
			textTransfer
		};

		// assignmentComposite
		DropTarget dtarget = new DropTarget(assignmentComposite, DND.DROP_COPY);
		dtarget.setTransfer(types);
		dtarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				/* Wir machen nur Copy-Operationen */
				event.detail = DND.DROP_COPY;
			}

			/* Mausbewegungen mit gedrückter Taste sind uns egal */
			@Override
			public void dragLeave(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOver(DropTargetEvent event){
				/* leer */
			}

			/* Erst das Loslassen interessiert uns wieder */
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);
					if (dropped instanceof Problem) {
						Problem problem = (Problem) dropped;
						problem.addToKonsultation(actKons);

						updateProblemAssignmentViewer();
						setDiagnosenText(actKons);
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}
		});

		// verrechnungComposite
		dtarget = new DropTarget(verrechnungComposite, DND.DROP_COPY);
		dtarget.setTransfer(types);
		dtarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				/* Wir machen nur Copy-Operationen */
				event.detail = DND.DROP_COPY;
			}

			/* Mausbewegungen mit gedrückter Taste sind uns egal */
			@Override
			public void dragLeave(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event){
				/* leer */
			}

			@Override
			public void dragOver(DropTargetEvent event){
				/* leer */
			}

			/* Erst das Loslassen interessiert uns wieder */
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);
					if (dropped instanceof IVerrechenbar) {
						if (CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN) == false) {
							SWTHelper.alert("Fehlende Rechte",
								"Sie haben nicht die Berechtigung, Leistungen zu verrechnen");
						} else {
							Result<IVerrechenbar> result =
								actKons.addLeistung((IVerrechenbar) dropped);
							if (!result.isOK()) {
								SWTHelper.alert("Diese Verrechnung it ungültig", result.toString());
							}
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						}
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}
		});

	}

	private void highlightProblemsTable(boolean highlight){
		highlightProblemsTable(highlight, false);
	}

	private void highlightProblemsTable(boolean highlight, boolean full){
		problemsTableModel.setHighlightSelection(highlight, full);
		problemsKTable.redraw();
	}

	private void highlightVerrechnung(boolean highlight){
		Table table = verrechnungViewer.getTable();

		if (highlight) {
			// set highlighting color
			table.setBackground(highlightColor);
		} else {
			// set default color
			table.setBackground(verrechnungViewerColor);
		}
	}

	private void updateProblemAssignmentViewer(){
		problemAssignmentViewer.refresh();

		// set selection
		if (actKons != null) {
			List<Problem> problems = Problem.getProblemsOfKonsultation(actKons);
			problemAssignmentViewer.setCheckedElements(problems.toArray());
			problemAssignmentViewer.refresh();
		} else {
			// empty selection
			problemAssignmentViewer.setCheckedElements(new Problem[] {});
			problemAssignmentViewer.refresh();
		}
	}

	public void updateVerrechnungSum(){
		StringBuilder sb = new StringBuilder();
		sb.append("Verrechnung");

		if (actKons != null) {
			List<Verrechnet> leistungen = actKons.getLeistungen();
			Money sum = new Money(0);
			for (Verrechnet leistung : leistungen) {
				int z = leistung.getZahl();
				Money preis = leistung.getEffPreis().multiply(z);
				sum.addMoney(preis);
			}
			sb.append(" (");
			sb.append(sum.getAmountAsString());
			sb.append(")");
		}

		hVerrechnung.setText(sb.toString());
	}

	private void createHistory(Composite parent){
		// history = new HistoryDisplay(parent, getViewSite(), true);
		konsListDisplay = new KonsListDisplay(parent);
	}

	@Override
	public void dispose(){
		// make sure to unlock the kons edit field and release the lock
		removeKonsTextLock();

		// ((DefaultContentProvider)fallCf.getContentProvider()).stopListening();
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	@Override
	public void setFocus(){
		// TODO Auto-generated method stub

	}

	private Problem getSelectedProblem(){
		Point[] selection = problemsKTable.getCellSelection();
		if (selection == null || selection.length == 0) {
			return null;
		} else {
			int rowIndex = selection[0].y - problemsTableModel.getFixedHeaderRowCount();
			Problem problem = problemsTableModel.getProblem(rowIndex);
			return problem;
		}

	}

	/**
	 * used by selectionEvent(PersistentObject obj)
	 */
	private Konsultation getTodaysLatestKons(Fall fall){
		Konsultation result = null;
		TimeTool today = new TimeTool();

		Konsultation[] konsultationen = fall.getBehandlungen(true);
		if (konsultationen != null) {
			// find the latest Konsultation according to the text entry's timestamp
			long timestamp = -1;
			for (Konsultation k : konsultationen) {
				if (new TimeTool(k.getDatum()).isSameDay(today)) {
					VersionedResource vr = k.getEintrag();
					if (vr != null) {
						ResourceItem ri = vr.getVersion(vr.getHeadVersion());
						if (ri != null) {
							if (ri.timestamp > timestamp) {
								timestamp = ri.timestamp;
								result = k;
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Get the latest Konsultation of today
	 * 
	 * @return today's latest Konsultation
	 * 
	 *         Same implementation as Patient.getLetzteKons()
	 */
	public Konsultation getTodaysLatestKons(Patient patient){
		TimeTool today = new TimeTool();

		Fall[] faelle = patient.getFaelle();
		if ((faelle == null) || (faelle.length == 0)) {
			return null;
		}

		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		qbe.add("MandantID", "=", CoreHub.actMandant.getId());
		qbe.add("Datum", "=", today.toString(TimeTool.DATE_COMPACT));

		qbe.startGroup();
		boolean termInserted = false;
		for (Fall fall : faelle) {
			if (fall.isOpen()) {
				qbe.add("FallID", "=", fall.getId());
				qbe.or();
				termInserted = true;
			}
		}
		if (!termInserted) {
			return null;
		}
		qbe.endGroup();

		qbe.orderBy(true, "Datum");
		List<Konsultation> list = qbe.execute();
		if ((list == null) || list.isEmpty()) {
			return null;
		} else {
			if (list.size() == 1) {
				return list.get(0);
			} else {
				// find the latest Konsultation according to the text entry's timestamp
				long timestamp = -1;
				Konsultation result = null;

				for (Konsultation k : list) {
					VersionedResource vr = k.getEintrag();
					if (vr != null) {
						ResourceItem ri = vr.getVersion(vr.getHeadVersion());
						if (ri != null) {
							if (ri.timestamp > timestamp) {
								timestamp = ri.timestamp;
								result = k;
							}
						}
					}
				}
				if (result == null) {
					result = list.get(0);
				}

				return result;
			}
		}
	}

	public void adaptMenus(){
		verrechnungViewer.getTable().getMenu()
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN));

		// TODO this belongs to GlobalActions itself (action creator)
		GlobalActions.delKonsAction.setEnabled(CoreHub.acl
			.request(AccessControlDefaults.KONS_DELETE));
		GlobalActions.neueKonsAction.setEnabled(CoreHub.acl
			.request(AccessControlDefaults.KONS_CREATE));
	}

	private void makeActions(){
		// Konsultation

		// Replacement for GlobalActions.neueKonsAction (other image)
		addKonsultationAction = new Action(GlobalActions.neueKonsAction.getText()) {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix",
					"rsc/new_konsultation.ico"));
				setToolTipText(GlobalActions.neueKonsAction.getToolTipText());
			}

			@Override
			public void run(){
				GlobalActions.neueKonsAction.run();
			}
		};
		addKonsultationAction.setActionDefinitionId(NEWCONS_COMMAND);
		GlobalActions.registerActionHandler(this, addKonsultationAction);

		// Probleme

		delProblemAction = new Action("Problem löschen") {
			@Override
			public void run(){
				Problem problem = getSelectedProblem();
				if (problem != null) {
					String label = problem.getLabel();
					if (StringTool.isNothing(label)) {
						label = UNKNOWN;
					}
					if (MessageDialog.openConfirm(getViewSite().getShell(), "Wirklich löschen?",
						label) == true) {
						if (problem.remove(true) == false) {
							SWTHelper.alert("Konnte Problem nicht löschen",
								"Das Problem konnte nicht gelöscht werden.");
						} else {
							problemsTableModel.reload();
							problemsKTable.refresh();

							updateProblemAssignmentViewer();
						}
					}
				}
			}

		};
		addProblemAction = new Action("Neues Problem") {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix",
					"rsc/new_problem.ico"));
				setToolTipText("Neues Problem für diesen Patienten erstellen");
			}

			@Override
			public void run(){
				Problem problem = new Problem(ElexisEventDispatcher.getSelectedPatient(), "");
				String currentDate = new TimeTool().toString(TimeTool.DATE_ISO);
				problem.setStartDate(currentDate);
				IatrixEventHelper.fireSelectionEventProblem(problem);

				// neues Problem der aktuellen Konsulation hinzufuegen
				/*
				 * if (actKons != null) { MessageBox mb = new MessageBox(getViewSite().getShell(),
				 * SWT.ICON_QUESTION | SWT.YES | SWT.NO); mb.setText("Neues Problem"); mb
				 * .setMessage("Neues Problem der aktuellen Konsulation zurdnen?"); if (mb.open() ==
				 * SWT.YES) { problem.addToKonsultation(actKons); } }
				 */

				problemsTableModel.reload();
				problemsKTable.refresh();

				// select the new object
				int rowIndex = problemsTableModel.getIndexOf(problem);
				if (rowIndex > -1) {
					int col = problemsTableModel.getFixedHeaderColumnCount();
					int row = rowIndex + problemsTableModel.getFixedHeaderRowCount();
					problemsKTable.setSelection(col, row, true);
				}

				updateProblemAssignmentViewer();
			}
		};
		addProblemAction.setActionDefinitionId(NEWPROBLEM_COMMAND);
		GlobalActions.registerActionHandler(this, addProblemAction);

		addFixmedikationAction = new Action("Fixmedikation hinzufügen") {
			{
				setToolTipText("Fixmedikation hinzufügen");
			}

			@Override
			public void run(){
				Point[] selection = problemsKTable.getCellSelection();
				if (selection.length != 1) {
					// no problem selected
					SWTHelper
						.alert(
							"Fixmedikation hinzufügen",
							"Sie können eine Fixmedikation nur dann hinzufügen,"
								+ "wenn Sie in der entsprechenden Spalte der Patientenübersicht stehen.");
					return;
				}

				int row = selection[0].y;
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);
				if (problem != null) {
					try {
						getViewSite().getPage().showView(LeistungenView.ID);
						// register as ICodeSelectorTarget
						CodeSelectorHandler.getInstance().setCodeSelectorTarget(
							problemFixmedikationCodeSelectorTarget);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.log("Fehler beim Anzeigen der Artikel " + ex.getMessage(), Log.ERRORS);
					}
				}
			}
		};

		editFixmedikationAction = new Action("Fixmedikation ändern...") {
			{
				setToolTipText("Fixmedikation ändern...");
			}

			@Override
			public void run(){
				try {
					getViewSite().getPage().showView(ProblemView.ID);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.log("Fehler beim Öffnen von ProblemView: " + ex.getMessage(), Log.ERRORS);
				}
			}
		};

		deleteFixmedikationAction = new Action("Fixmedikation entfernen...") {
			{
				setToolTipText("Fixmedikation entfernen...");
			}

			@Override
			public void run(){
				try {
					getViewSite().getPage().showView(ProblemView.ID);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.log("Fehler beim Öffnen von ProblemView: " + ex.getMessage(), Log.ERRORS);
				}
			}
		};

		unassignProblemAction = new Action("Problem entfernen") {
			{
				setToolTipText("Problem von Konsulation entfernen");
			}

			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) problemAssignmentViewer.getSelection())
						.getFirstElement();
				if (sel != null) {
					Problem problem = (Problem) sel;

					problem.removeFromKonsultation(actKons);
					updateProblemAssignmentViewer();
					setDiagnosenText(actKons);
				}
			}
		};

		// Konsultationstext

		purgeAction = new Action("Alte Eintragsversionen entfernen") {
			@Override
			public void run(){
				actKons.purgeEintrag();
				ElexisEventDispatcher.fireSelectionEvent(actKons);
			}
		};
		versionBackAction = new Action("Vorherige Version") {
			@Override
			public void run(){
				if (MessageDialog
					.openConfirm(
						getViewSite().getShell(),
						"Konsultationstext ersetzen",
						"Wollen Sie wirklich den aktuellen Konsultationstext gegen eine frühere Version desselben Eintrags ersetzen?")) {
					setKonsText(actKons, displayedVersion - 1, false);
					text.setDirty(true);
				}
			}
		};
		versionFwdAction = new Action("nächste Version") {
			@Override
			public void run(){
				if (MessageDialog
					.openConfirm(
						getViewSite().getShell(),
						"Konsultationstext ersetzen",
						"Wollen Sie wirklich den aktuellen Konsultationstext gegen eine spätere Version desselben Eintrags ersetzen?")) {
					setKonsText(actKons, displayedVersion + 1, false);
					text.setDirty(true);
				}
			}
		};
		chooseVersionAction = new Action("Version wählen...") {
			@Override
			public void run(){
				ChooseKonsRevisionDialog dlg =
					new ChooseKonsRevisionDialog(getViewSite().getShell(), actKons);
				if (dlg.open() == ChooseKonsRevisionDialog.OK) {
					int selectedVersion = dlg.getSelectedVersion();

					if (MessageDialog.openConfirm(getViewSite().getShell(),
						"Konsultationstext ersetzen",
						"Wollen Sie wirklich den aktuellen Konsultationstext gegen die Version "
							+ selectedVersion + " desselben Eintrags ersetzen?")) {
						setKonsText(actKons, selectedVersion, false);
						text.setDirty(true);
					}
				}

			}
		};

		saveAction = new Action("Eintrag sichern") {
			{
				setImageDescriptor(Images.IMG_DISK.getImageDescriptor());
				setToolTipText("Text explizit speichern");
			}

			@Override
			public void run(){
				updateEintrag();
			}
		};
		saveAction.setActionDefinitionId(SAVE_COMMAND);
		GlobalActions.registerActionHandler(this, saveAction);

		// Verrechnung

		delVerrechnetAction = new Action("Leistungsposition entfernen") {
			@Override
			public void run(){
				IStructuredSelection sel = (IStructuredSelection) verrechnungViewer.getSelection();
				if (sel != null) {
					/*
					 * if (SWTHelper.askYesNo("Leistungsposition entfernen", "Sind Sie sicher, dass
					 * Sie die ausgewählten Leistungsposition entfernen wollen?") {
					 */
					for (Object obj : sel.toArray()) {
						if (obj instanceof Verrechnet) {
							Verrechnet verrechnet = (Verrechnet) obj;
							Result result = actKons.removeLeistung(verrechnet);
							if (!result.isOK()) {
								SWTHelper.alert("Leistungsposition kann nicht entfernt werden",
									result.toString());
							}
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						}
					}
					/*
					 * }
					 */
				}
			}
		};
		delVerrechnetAction.setActionDefinitionId(GlobalActions.DELETE_COMMAND);
		GlobalActions.registerActionHandler(this, delVerrechnetAction);

		changeVerrechnetPreisAction = new Action("Preis ändern") {
			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) verrechnungViewer.getSelection()).getFirstElement();
				if (sel != null) {
					Verrechnet verrechnet = (Verrechnet) sel;
					// String p=Rechnung.geldFormat.format(verrechnet.getEffPreisInRappen()/100.0);
					String p = verrechnet.getEffPreis().getAmountAsString();
					InputDialog dlg =
						new InputDialog(getViewSite().getShell(), "Preis für Leistung ändern",
							"Geben Sie bitte den neuen Preis für die Leistung ein (x.xx)", p, null);
					if (dlg.open() == Dialog.OK) {
						Money newPrice;
						try {
							newPrice = new Money(dlg.getValue());
							verrechnet.setPreis(newPrice);
							verrechnungViewer.refresh();
							updateVerrechnungSum();
						} catch (ParseException e) {
							ExHandler.handle(e);
							SWTHelper.showError("Falsche Eingabe",
								"Konnte Angabe nicht interpretieren");
						}

					}
				}
			}
		};
		changeVerrechnetZahlAction = new Action("Zahl ändern") {
			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) verrechnungViewer.getSelection()).getFirstElement();
				if (sel != null) {
					Verrechnet verrechnet = (Verrechnet) sel;
					String p = Integer.toString(verrechnet.getZahl());
					InputDialog dlg =
						new InputDialog(
							getViewSite().getShell(),
							"Zahl der Leistung ändern",
							"Geben Sie bitte die neue Anwendungszahl für die Leistung bzw. den Artikel ein",
							p, null);
					if (dlg.open() == Dialog.OK) {
						int vorher = verrechnet.getZahl();
						int neu = Integer.parseInt(dlg.getValue());
						verrechnet.setZahl(neu);
						IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
						if (verrechenbar instanceof Artikel) {
							Artikel art = (Artikel) verrechenbar;
							art.einzelRuecknahme(vorher);
							art.einzelAbgabe(neu);
						}

						verrechnungViewer.refresh();
						updateVerrechnungSum();
					}
				}
			}
		};

		exportToClipboardAction = new Action("Export (Zwischenablage)") {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis",
					"rsc/plaf/modern/icons/export.ico"));
				setToolTipText("Zusammenfassung in Zwischenablage kopieren");
			}

			@Override
			public void run(){
				exportToClipboard();
			}
		};
		exportToClipboardAction.setActionDefinitionId(EXPORT_CLIPBOARD_COMMAND);
		GlobalActions.registerActionHandler(this, exportToClipboardAction);

		sendEmailAction = new Action("E-Mail verschicken") {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis",
					"rsc/mail.png"));
				setToolTipText("E-Mail Programm öffnent (mit Medikation und allen Konsultationen)");
			}

			@Override
			public void run(){
				Email.openMailApplication("", // No default to address
					null, exportToClipboard(), null);

			}
		};
		sendEmailAction.setActionDefinitionId(EXPORT_SEND_EMAIL_COMMAND);
		GlobalActions.registerActionHandler(this, sendEmailAction);

		// history display
		showAllChargesAction = new Action("Alle Leistungen anzeigen", Action.AS_CHECK_BOX) {
			{
				setToolTipText("Leistungen aller Konsultationen anzeigen, nicht nur der ersten paar.");
			}

			@Override
			public void run(){
				konsListDisplay.setPatient(actPatient, showAllChargesAction.isChecked(),
					showAllConsultationsAction.isChecked());
			}
		};
		showAllChargesAction.setActionDefinitionId(Iatrix.SHOW_ALL_CHARGES_COMMAND);
		GlobalActions.registerActionHandler(this, showAllChargesAction);

		showAllConsultationsAction =
			new Action("Alle Konsultationen anzeigen", Action.AS_CHECK_BOX) {
				{
					setToolTipText("Alle Konsultationen anzeigen");
				}

				@Override
				public void run(){
					konsListDisplay.setPatient(actPatient, showAllChargesAction.isChecked(),
						showAllConsultationsAction.isChecked());
				}
			};
		showAllConsultationsAction.setActionDefinitionId(Iatrix.SHOW_ALL_CONSULTATIONS_COMMAND);
		GlobalActions.registerActionHandler(this, showAllConsultationsAction);
	}

	private void updateEintrag(){
		if (actKons != null) {
			log.log("updateEintrag", Log.DEBUGMSG);
			if (text.isDirty() || textChanged()) {
				if (hasKonsTextLock()) {
					actKons.updateEintrag(text.getContentsAsXML(), false);
					log.log("saved.", Log.DEBUGMSG);
					text.setDirty(false);

					// update kons version label
					// (we would get an objectChanged event, but this event isn't processed
					// in case the kons text field has the focus.)
					updateKonsVersionLabel();
				} else {
					// should never happen...
					SWTHelper
						.alert(
							"Konsultation gesperrt",
							"Der Text kann nicht gespeichert werden, weil die Konsultation durch einen anderen Benutzer gesperrt ist."
								+ " (Dieses Problem ist ein Programmfehler. Bitte informieren Sie die Entwickler.)");
				}
			}
		}
	}

	/**
	 * Check whether the text in the text field has changed compared to the database entry.
	 * 
	 * @return true, if the text changed, false else
	 */
	private boolean textChanged(){
		String dbEintrag = actKons.getEintrag().getHead();
		String textEintrag = text.getContentsAsXML();

		if (textEintrag != null) {
			if (!textEintrag.equals(dbEintrag)) {
				// text differs from db entry
				log.log("textChanged", Log.DEBUGMSG);
				return true;
			}
		}

		return false;

	}

	@Override
	public void activation(boolean mode){
		if (mode == true) {
			setKonsultation((Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class),
				false);
		} else {
			updateEintrag();
		}
	}

	@Override
	public void visible(boolean mode){
		if (mode == true) {
			showAllChargesAction.setChecked(false);
			showAllConsultationsAction.setChecked(false);

			ElexisEventDispatcher.getInstance().addListeners(eeli_kons, eeli_problem, eeli_fall,
				eeli_pat, eeli_user);

			Patient patient = ElexisEventDispatcher.getSelectedPatient();
			setPatient(patient);

			/*
			 * setPatient(Patient) setzt eine neue Konsultation, falls bereits eine gestzt ist und
			 * diese nicht zum neuen Patienten gehoert. Ansonsten sollten wir die letzte
			 * Konsultation des Paitenten setzten.
			 */
			if (actKons == null) {
				Konsultation kons =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (kons != null) {
					if (!kons.getFall().getPatient().equals(patient)) {
						kons = patient.getLetzteKons(false);
					}
				}
				setKonsultation(kons, false);
			}

			CoreHub.heart.addListener(this);
		} else {
			CoreHub.heart.removeListener(this);

			ElexisEventDispatcher.getInstance().removeListeners(eeli_kons, eeli_problem, eeli_fall,
				eeli_pat, eeli_user);

			/*
			 * setPatient(null) ruft setKonsultation(null) auf.
			 */
			setPatient(null);
		}
	};

	/**
	 * Refresh (reload) data
	 */
	@Override
	public void heartbeat(){
		// don't run while another heartbeat is currently processed
		if (heartbeatActive) {
			return;
		}

		heartbeatActive = true;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run(){
				heartbeatProblem();
				heartbeatKonsultation();
				heartbeatSaveKonsText();
			}
		});

		heartbeatActive = false;
	}

	private void heartbeatProblem(){
		if (heartbeatProblemEnabled) {
			// backup selection

			boolean isRowSelectMode = problemsKTable.isRowSelectMode();

			Problem selectedProblem = null;
			int currentColumn = -1;

			if (isRowSelectMode) {
				// full row selection
				// not supported
			} else {
				// single cell selection

				Point[] cells = problemsKTable.getCellSelection();
				if (cells != null && cells.length > 0) {
					int row = cells[0].y;
					int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
					selectedProblem = problemsTableModel.getProblem(rowIndex);
					currentColumn = cells[0].x;
				}
			}

			// reload data
			setPatient(actPatient);

			// restore selection

			if (selectedProblem != null) {
				if (isRowSelectMode) {
					// full row selection
					// not supported
				} else {
					// single cell selection
					int rowIndex = problemsTableModel.getIndexOf(selectedProblem);
					if (rowIndex >= 0) {
						// problem found, i. e. still in list

						int row = rowIndex + problemsTableModel.getFixedHeaderRowCount();
						if (currentColumn == -1) {
							currentColumn = problemsTableModel.getFixedHeaderColumnCount();
						}
						problemsKTable.setSelection(currentColumn, row, true);
					}
				}
			}
		}
	}

	private void heartbeatKonsultation(){
		// refresh kons text lock
		if (konsTextLock != null) {
			boolean success = konsTextLock.lock();
			log.log("heartbeatKonsultation: konsText locked (" + success + ")", Log.DEBUGMSG);
			// System.err.println("DEBUG: heartbeatKonsultation: konsText locked (" + success +
			// ")");

		}

		updateKonsultation(!konsEditorHasFocus, false);
	}

	/**
	 * Return the auto-save time period interval, as configured in CoreHub.userCfg
	 * 
	 * @return the calculated period interval, or 1 if there are invalid configuration values, or 0
	 *         if autos-save is disabled
	 */
	private int getKonsTextSaverPeriod(){
		int timePeriod =
			CoreHub.userCfg.get(Iatrix.CFG_AUTO_SAVE_PERIOD, Iatrix.CFG_AUTO_SAVE_PERIOD_DEFAULT);
		if (timePeriod == 0) {
			// no auto-save
			return 0;
		}

		log.log("TimePeriod: " + timePeriod, Log.DEBUGMSG);
		int heartbeatInterval =
			CoreHub.localCfg.get(ch.elexis.core.constants.Preferences.ABL_HEARTRATE, 30);
		if (heartbeatInterval > 0 && timePeriod >= heartbeatInterval) {
			int period = timePeriod / heartbeatInterval;
			if (period > 0) {
				return period;
			} else {
				// shouldn't occur...
				return 1;
			}
		} else {
			// shouldn't occur...
			return 1;
		}
	}

	/*
	 * Automatically save kons text
	 */
	private void heartbeatSaveKonsText(){
		int konsTextSaverPeriod = getKonsTextSaverPeriod();
		log.log("Period: " + konsTextSaverPeriod, Log.DEBUGMSG);
		if (!(konsTextSaverPeriod > 0)) {
			// auto-save disabled
			return;
		}

		// inv: konsTextSaverPeriod > 0

		// increment konsTextSaverCount, but stay inside period
		konsTextSaverCount++;
		konsTextSaverCount %= konsTextSaverPeriod;

		log.log("konsTextSaverCount = " + konsTextSaverCount, Log.DEBUGMSG);
		log.log("konsEditorHasFocus: " + konsEditorHasFocus, Log.DEBUGMSG);

		if (konsTextSaverCount == 0) {
			if (konsEditorHasFocus) {
				log.log("Auto Save Kons Text", Log.DEBUGMSG);

				updateEintrag();
			}
		}
	}

	private void setHeartbeatProblemEnabled(boolean value){
		heartbeatProblemEnabled = value;
	}

	/*
	 * Aktuellen Patienten setzen
	 */
	public void setPatient(Patient patient){
		if (actPatient == patient) {
			return;
		}
		actPatient = patient;

		// widgets may be disposed when application is closed
		if (form.isDisposed()) {
			return;
		}

		if (actPatient != null) {
			// Pruefe, ob Patient Probleme hat, sonst Standardproblem erstellen
			List<Problem> problems = Problem.getProblemsOfPatient(actPatient);
			if (problems.size() == 0) {
				// TODO don't yet do this
				// Problem.createStandardProblem(actPatient);
			}

			problemsTableModel.reload();
			problemsKTable.refresh();

			// Konsistenz Patient/Konsultation ueberpruefen
			if (actKons != null) {
				if (!actKons.getFall().getPatient().getId().equals(actPatient.getId())) {
					// aktuelle Konsultation gehoert nicht zum aktuellen Patienten
					setKonsultation(actPatient.getLetzteKons(false), false);
				}
			}

			// history.stop();
			// history.load(actPatient);
			// history.start();

			log.log("Patient: " + actPatient.getId(), Log.DEBUGMSG);
		} else {
			// problemsTable.setInput(null);

			problemsTableModel.reload();
			problemsKTable.refresh();

			// Kein Patient ausgewaehlt, somit auch keine Konsultation anzeigen
			setKonsultation(null, false);

			// TODO history widget may be disposed, how to recognize?
			/*
			 * history.stop(); history.load(null, true); history.start();
			 */

			log.log("Patient: null", Log.DEBUGMSG);
		}

		konsListDisplay.setPatient(actPatient, showAllChargesAction.isChecked(),
			showAllConsultationsAction.isChecked());

		setPatientTitel();
		setRemarkAndSticker();
		setKontoText();
	}

	/**
	 * Set the version label to reflect the current kons' latest version Called by: updateEintrag()
	 */
	void updateKonsVersionLabel(){
		if (actKons != null) {
			int version = actKons.getHeadVersion();

			log.log("Update Version Label: " + version, Log.DEBUGMSG);

			VersionedResource vr = actKons.getEintrag();
			ResourceItem entry = vr.getVersion(version);
			StringBuilder sb = new StringBuilder();
			sb.append("rev. ").append(version).append(" vom ")
				.append(new TimeTool(entry.timestamp).toString(TimeTool.FULL_GER)).append(" (")
				.append(entry.remark).append(")");
			lVersion.setText(sb.toString());
		} else {
			lVersion.setText("");
		}
	}

	void setKonsText(Konsultation b, int version, boolean putCaretToEnd){
		if (b != null) {
			String ntext = "";
			if ((version >= 0) && (version <= b.getHeadVersion())) {
				VersionedResource vr = b.getEintrag();
				ResourceItem entry = vr.getVersion(version);
				ntext = entry.data;
				StringBuilder sb = new StringBuilder();
				sb.append("rev. ").append(version).append(" vom ")
					.append(new TimeTool(entry.timestamp).toString(TimeTool.FULL_GER)).append(" (")
					.append(entry.remark).append(")");
				lVersion.setText(sb.toString());
			} else {
				lVersion.setText("");
			}
			text.setText(PersistentObject.checkNull(ntext));
			text.setKons(b);
			text.setEnabled(hasKonsTextLock());
			displayedVersion = version;
			versionBackAction.setEnabled(version != 0);
			versionFwdAction.setEnabled(version != b.getHeadVersion());

			if (putCaretToEnd) {
				// set focus and put caret at end of text
				text.putCaretToEnd();
			}
		} else {
			lVersion.setText("");
			text.setText("");
			text.setKons(null);

			if (actPatient == null) {
				text.setEnabled(false);
			} else {
				// enable text, in case user wants to create a new kons by
				// typing in the empty text field
				text.setEnabled(true);
			}

			displayedVersion = -1;
			versionBackAction.setEnabled(false);
			versionFwdAction.setEnabled(false);
		}
	}

	private void setDiagnosenText(Konsultation konsultation){
		String text = "";
		Image image = null;

		if (konsultation != null) {
			List<IDiagnose> diagnosen = konsultation.getDiagnosen();
			if (diagnosen != null && diagnosen.size() > 0) {
				List<String> dxList = new ArrayList<String>();
				for (IDiagnose diagnose : diagnosen) {
					dxList.add(diagnose.getLabel());
				}
				text = "Diagnosen: " + StringTool.join(dxList, ", ");
			} else {
				// no diagnosis, warn error
				text = "Keine Diagnosen";
				image = Images.IMG_ACHTUNG.getImage();
			}
		}

		lDiagnosis.setText(PersistentObject.checkNull(text));
		lDiagnosis.setImage(image);
	}

	private void setPatientTitel(){
		String text = "Kein Patient ausgewählt";

		formTitel.setEnabled(actPatient != null);

		if (actPatient != null) {
			text = actPatient.getLabel();
		}

		formTitel.setText(PersistentObject.checkNull(text));
		formTitel.getParent().layout();
	}

	private void setRemarkAndSticker(){
		String text = "";
		sticker = Images.IMG_EMPTY_TRANSPARENT.getImage();

		if (actPatient != null) {
			text = actPatient.getBemerkung();
			ISticker et = actPatient.getSticker();
			if (et != null) {
				sticker = new UiSticker((Sticker) et).getImage();
			}
		}
		if (sticker == null) {
			sticker = Images.IMG_EMPTY_TRANSPARENT.getImage();
		}

		stickerLabel.setImage(sticker);
		if (PersistentObject.checkNull(text).length() == 0)
			remarkLabel.setText("Bemerkungen");
		else
			remarkLabel.setText(PersistentObject.checkNull(text));

		formTitel.getParent().layout();
	}

	private void setKontoText(){
		// TODO common isTardyPayer method in class Patient

		// this may involve a slow query
		kontoLabel.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run(){
				boolean tardyPayer = false;

				// the widget may already be disposed when the application exits
				if (remarkLabel.isDisposed()) {
					return;
				}

				String text = "";
				if (actPatient != null) {
					text = actPatient.getKontostand().getAmountAsString();

					tardyPayer = isTardyPayer(actPatient);
				}

				kontoLabel.setText(PersistentObject.checkNull(text));
				kontoLabel.getParent().layout();

				// draw the label red if the patient is a tardy payer
				Color textColor;
				if (tardyPayer) {
					textColor = kontoLabel.getDisplay().getSystemColor(SWT.COLOR_RED);
				} else {
					textColor = kontoLabelColor;
				}
				kontoLabel.setForeground(textColor);

				formTitel.getParent().layout();
			}
		});
	}

	/**
	 * Aktuelle Konsultation setzen.
	 * 
	 * Wenn eine Konsultation gesetzt wird stellen wir sicher, dass der gesetzte Patient zu dieser
	 * Konsultation gehoert. Falls nicht, wird ein neuer Patient gesetzt.
	 * 
	 * @param putCaretToEnd
	 *            if true, activate text field ant put caret to the end
	 */
	public void setKonsultation(Konsultation k, boolean putCaretToEnd){
		// save probably not yet saved changes
		updateEintrag();

		// make sure to unlock the kons edit field and release the lock
		removeKonsTextLock();

		actKons = k;
		if (savedInitialKonsText != null && actKons != null) {
			actKons.updateEintrag(savedInitialKonsText, true);
			savedInitialKonsText = null;
		}
		creatingKons = false;

		if (actKons != null) {
			// create new konsTextLock
			createKonsTextLock();

			Patient patient = actKons.getFall().getPatient();

			// Konsistenz Patient/Konsultation ueberpruefen
			if (actPatient == null || (!actPatient.equals(patient))) {
				setPatient(patient);
			}
		}

		updateKonsultation(true, putCaretToEnd);

		// update konslock label and enable/disable save action
		updateKonsLockLabel();
		saveAction.setEnabled(konsTextLock == null || hasKonsTextLock());
	}

	private void updateKonsLockLabel(){
		if (konsTextLock == null || hasKonsTextLock()) {
			lKonsLock.setVisible(false);
			lKonsLock.setText("");
		} else {
			Anwender user = konsTextLock.getLockValue().getUser();
			String text;
			if (user != null && user.exists()) {
				text = "Konsultation wird von Benutzer \"" + user.getLabel() + "\" bearbeitet.";
			} else {
				text = "Konsultation wird von anderem Benutzer bearbeitet.";
			}

			lKonsLock.setText(text);
			lKonsLock.setVisible(true);
		}

		lKonsLock.getParent().layout();
	}

	private void updateKonsultation(boolean updateText, boolean putCaretToEnd){
		if (actKons != null) {
			cbFall.setEnabled(true);
			hVerrechnung.setEnabled(true);
			tVerrechnungKuerzel.setEnabled(true);

			StringBuilder sb = new StringBuilder();
			sb.append(actKons.getDatum());
			/*
			 * lKonsultation.setText(sb.toString());
			 */
			hlKonsultationDatum.setText(sb.toString());
			hlKonsultationDatum.setEnabled(true);

			if (hasMultipleMandants) {
				// inv: hlMandant != null

				Mandant m = actKons.getMandant();
				sb = new StringBuilder();
				if (m == null) {
					sb.append("(nicht von Ihnen)");
				} else {
					Rechnungssteller rs = m.getRechnungssteller();
					if (rs.getId().equals(m.getId())) {
						sb.append("(").append(m.getLabel()).append(")");
					} else {
						sb.append("(").append(m.getLabel()).append("/").append(rs.getLabel())
							.append(")");
					}
				}
				hlMandant.setText(sb.toString());
				hlMandant.setEnabled(CoreHub.acl.request(AccessControlDefaults.KONS_REASSIGN));
			}

			reloadFaelle(actKons);

			if (updateText) {
				setKonsText(actKons, actKons.getHeadVersion(), putCaretToEnd);
			}

			setDiagnosenText(actKons);

			log.log("Konsultation: " + actKons.getId(), Log.DEBUGMSG);
		} else {
			cbFall.setEnabled(false);
			hVerrechnung.setEnabled(false);
			tVerrechnungKuerzel.setEnabled(false);

			/*
			 * lKonsultation.setText("Keine Konsultation ausgewählt");
			 */
			hlKonsultationDatum.setText("Keine Konsultation ausgewählt");
			hlKonsultationDatum.setEnabled(false);
			if (hlMandant != null) {
				hlMandant.setText("");
				hlMandant.setEnabled(false);
			}

			reloadFaelle(null);

			setKonsText(null, 0, putCaretToEnd);
			setDiagnosenText(null);

			log.log("Konsultation: null", Log.DEBUGMSG);
		}

		konsFallArea.layout();

		updateProblemAssignmentViewer();
		verrechnungViewer.refresh();
		updateVerrechnungSum();
	}

	private void reloadFaelle(Konsultation konsultation){
		cbFall.removeAll();

		if (konsultation != null) {
			Fall fall = konsultation.getFall();
			Patient patient = fall.getPatient();

			Fall[] faelle = patient.getFaelle();
			// find current case
			int index = -1;
			for (int i = 0; i < faelle.length; i++) {
				if (faelle[i].getId().equals(fall.getId())) {
					index = i;
				}
			}
			// add cases and select current case if found
			if (index >= 0) {
				cbFall.setData(faelle);
				for (Fall f : faelle) {
					cbFall.add(f.getLabel());
				}
				// no selection event seems to be generated
				cbFall.select(index);
			}
		}
	}

	private void openRemarkEditorDialog(){
		if (actPatient == null) {
			return;
		}

		String initialValue = PersistentObject.checkNull(actPatient.getBemerkung());
		InputDialog dialog =
			new InputDialog(getViewSite().getShell(), "Bemerkungen", "Bemerkungen eingeben",
				initialValue, null);
		if (dialog.open() == Window.OK) {
			String text = dialog.getValue();
			actPatient.setBemerkung(text);
			setRemarkAndSticker();
		}
	}

	/**
	 * Is the patient a tardy payer, i. e. hasn't it paid all his bills?
	 * 
	 * @param patient
	 *            the patient to examine
	 * @return true if the patient is a tardy payer, false otherwise
	 * 
	 *         TODO this maybe makes the view slower
	 */
	private boolean isTardyPayer(Patient patient){
		// find bills with status MAHNUNG_1 to TOTALVERLUST
		// if there are such, the patient is a tardy payer

		// find all patient's bills
		Query<Rechnung> query = new Query<Rechnung>(Rechnung.class);
		Fall[] faelle = patient.getFaelle();
		if ((faelle != null) && (faelle.length > 0)) {
			query.startGroup();
			query.insertFalse();
			query.or();
			for (Fall fall : faelle) {
				if (fall.isOpen()) {
					query.add("FallID", "=", fall.getId());
				}
			}
			query.endGroup();
		} else {
			// no cases found
			return false;
		}

		query.and();

		query.startGroup();
		query.insertFalse();
		query.or();
		for (int s = RnStatus.MAHNUNG_1; s <= RnStatus.TOTALVERLUST; s++) {
			query.add("RnStatus", "=", new Integer(s).toString());
		}
		query.endGroup();

		List<Rechnung> rechnungen = query.execute();

		if (rechnungen != null && rechnungen.size() > 0) {
			// there are tardy bills
			return true;
		} else {
			// no tardy bills (or sql error)
			return false;
		}
	}

	/**
	 * Creates a new consultation if text has been entered, but no consultation is selected.
	 */
	private void handleInitialKonsText(){
		if (actPatient != null && actKons == null && creatingKons == false) {
			creatingKons = true;
			String initialText = text.getContentsAsXML();
			Konsultation.neueKons(initialText);
		} else {
			savedInitialKonsText = text.getContentsAsXML();
		}
	}

	/***********************************************************************************************
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	@Override
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}

	@Override
	public void doSave(IProgressMonitor monitor){ /* leer */}

	@Override
	public void doSaveAs(){ /* leer */}

	@Override
	public boolean isDirty(){
		return true;
	}

	@Override
	public boolean isSaveAsAllowed(){
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded(){
		return true;
	}

	private class ProblemsTableLabelProvider implements ITableLabelProvider, ITableFontProvider,
			ITableColorProvider {
		@Override
		public void addListener(ILabelProviderListener listener){
			// nothing to do
		}

		@Override
		public void removeListener(ILabelProviderListener listener){
			// nothing to do
		}

		@Override
		public void dispose(){
			// nothing to do
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex){
			if (!(element instanceof Problem)) {
				return null;
			}

			Problem problem = (Problem) element;

			switch (columnIndex) {
			case STATUS:
				if (problem.getStatus() == Episode.ACTIVE) {
					return UiDesk.getImage(Iatrix.IMG_ACTIVE);
				} else {
					return UiDesk.getImage(Iatrix.IMG_INACTIVE);
				}
			default:
				return null;
			}
		}

		@Override
		public String getColumnText(Object element, int columnIndex){
			if (!(element instanceof Problem)) {
				return "";
			}

			Problem problem = (Problem) element;

			String text;
			String lineSeparator;

			switch (columnIndex) {
			case BEZEICHNUNG:
				return problem.getTitle();
			case NUMMER:
				return problem.getNumber();
			case DATUM:
				return problem.getStartDate();
			case DIAGNOSEN:
				String diagnosen = problem.getDiagnosenAsText();
				lineSeparator = System.getProperty("line.separator");
				text = diagnosen.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);
				return text;
				/*
				 * case GESETZ: return problem.getGesetz();
				 */
				/*
				 * case RECHNUNGSDATEN: return "not yet implemented";
				 */
			case THERAPIE:
				String prescriptions = problem.getPrescriptionsAsText();
				lineSeparator = System.getProperty("line.separator");
				text = prescriptions.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);
				return text;
			case STATUS:
				return "";
			default:
				return "";
			}
		}

		@Override
		public boolean isLabelProperty(Object element, String property){
			return false;
		}

		@Override
		public Font getFont(Object element, int columnIndex){
			return null;
		}

		@Override
		public Color getForeground(Object element, int columnIndex){
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex){
			if (!(element instanceof Problem)) {
				return null;
			}

			Problem problem = (Problem) element;

			Color color;
			if (problem.getStatus() == Episode.ACTIVE) {
				color =
					problemAssignmentViewer.getTable().getDisplay().getSystemColor(SWT.COLOR_WHITE);
			} else {
				color =
					problemAssignmentViewer.getTable().getDisplay().getSystemColor(SWT.COLOR_GRAY);
			}

			return color;
		}

	}

	/**
	 * Copies the most important data to the clipboard, suitable to import into other text.
	 * Includes: - patient data (name, birthdate, address, phone numbers) - list of problems
	 * including medicals (all or selected only) - the latest consultations
	 */
	private String exportToClipboard(){
		final int NUMBER_OF_CONS = 1;

		String lineSeparator = System.getProperty("line.separator");
		String fieldSeparator = "\t";

		String clipboardText = "";

		if (actPatient != null) {
			StringBuffer output = new StringBuffer();

			// get list of selected problems
			List<Problem> problems = new ArrayList<Problem>();
			Problem p = getSelectedProblem();
			if (p != null) {
				problems.add(p);
			}

			// patient label
			StringBuffer patientLabel = new StringBuffer();
			patientLabel.append(actPatient.getName() + " " + actPatient.getVorname());
			patientLabel.append(" (");
			patientLabel.append(actPatient.getGeschlecht());
			patientLabel.append("), ");
			patientLabel.append(actPatient.getGeburtsdatum());
			patientLabel.append(", ");

			output.append(patientLabel);

			// patient address
			StringBuffer patientAddress = new StringBuffer();
			Anschrift anschrift = actPatient.getAnschrift();
			patientAddress.append(anschrift.getStrasse());
			patientAddress.append(", ");
			patientAddress.append(anschrift.getPlz() + " " + anschrift.getOrt());
			patientAddress.append(lineSeparator);

			output.append(patientAddress);

			// patient phone numbers
			boolean isFirst = true;
			StringBuffer patientPhones = new StringBuffer();
			String telefon1 = actPatient.get("Telefon1");
			String telefon2 = actPatient.get("Telefon2");
			String natel = actPatient.get("Natel");
			String eMail = actPatient.get("E-Mail");
			if (!StringTool.isNothing(telefon1)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append("T: ");
				patientPhones.append(telefon1);
				if (!StringTool.isNothing(telefon2)) {
					patientPhones.append(", ");
					patientPhones.append(telefon2);
				}
			}
			if (!StringTool.isNothing(natel)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append("M: ");
				patientPhones.append(natel);
			}
			if (!StringTool.isNothing(natel)) {
				if (isFirst) {
					isFirst = false;
				} else {
					patientPhones.append(", ");
				}
				patientPhones.append(eMail);
			}
			patientPhones.append(lineSeparator);

			output.append(patientPhones);
			output.append(lineSeparator);

			// consultations
			List<Konsultation> konsultationen = new ArrayList<Konsultation>();

			if (problems.size() > 0) {
				// get consultations of selected problems
				for (Problem problem : problems) {
					konsultationen.addAll(problem.getKonsultationen());
				}
			} else {
				// get all consultations
				for (Fall fall : actPatient.getFaelle()) {
					for (Konsultation k : fall.getBehandlungen(false)) {
						konsultationen.add(k);
					}
				}
			}

			// sort list of consultations in reverse order, get the latest ones
			Collections.sort(konsultationen, new Comparator<Konsultation>() {
				@Override
				public int compare(Konsultation k1, Konsultation k2){
					String d1 = k1.getDatum();
					String d2 = k2.getDatum();

					if (d1 == null) {
						return 1;
					}
					if (d2 == null) {
						return -1;
					}

					TimeTool date1 = new TimeTool(d1);
					TimeTool date2 = new TimeTool(d2);

					// reverse order
					return -(date1.compareTo(date2));
				}
			});

			for (int i = 0; i < NUMBER_OF_CONS && konsultationen.size() >= (i + 1); i++) {
				Konsultation konsultation = konsultationen.get(i);

				// output
				StringBuffer sb = new StringBuffer();

				sb.append(konsultation.getLabel());

				List<Problem> konsProblems = Problem.getProblemsOfKonsultation(konsultation);
				if (konsProblems != null && konsProblems.size() > 0) {
					StringBuffer problemsLabel = new StringBuffer();
					problemsLabel.append(" (");
					// first problem in list
					problemsLabel.append(konsProblems.get(0).getTitle());
					for (int j = 1; j < konsProblems.size(); j++) {
						// further problems in list
						problemsLabel.append(", ");
						problemsLabel.append(konsProblems.get(j).getTitle());
					}
					problemsLabel.append(")");

					sb.append(problemsLabel);
				}

				sb.append(lineSeparator);
				Samdas samdas = new Samdas(konsultation.getEintrag().getHead());
				sb.append(samdas.getRecordText());
				sb.append(lineSeparator);
				sb.append(lineSeparator);

				output.append(sb);
			}

			if (problems.size() == 0) {
				List<Problem> allProblems = Problem.getProblemsOfPatient(actPatient);
				if (problems != null) {
					problems.addAll(allProblems);
				}
			}

			Collections.sort(problems, DATE_COMPARATOR);

			StringBuffer problemsText = new StringBuffer();

			problemsText.append("Persönliche Anamnese");
			problemsText.append(lineSeparator);

			for (Problem problem : problems) {
				String date = problem.getStartDate();
				String text = problem.getTitle();

				List<String> therapy = new ArrayList<String>();
				String procedure = problem.getProcedere();
				if (!StringTool.isNothing(procedure)) {
					therapy.add(procedure.trim());
				}

				List<Prescription> prescriptions = problem.getPrescriptions();
				for (Prescription prescription : prescriptions) {
					String label =
						prescription.getArtikel().getLabel() + " (" + prescription.getDosis() + ")";
					therapy.add(label.trim());
				}

				StringBuffer sb = new StringBuffer();
				sb.append(date);
				sb.append(fieldSeparator);
				sb.append(text);
				sb.append(fieldSeparator);

				if (!therapy.isEmpty()) {
					// first therapy entry
					sb.append(therapy.get(0));
				}
				sb.append(lineSeparator);

				// further therapy entries
				if (therapy.size() > 1) {
					for (int i = 1; i < therapy.size(); i++) {
						sb.append(fieldSeparator);
						sb.append(fieldSeparator);
						sb.append(therapy.get(i));
						sb.append(lineSeparator);
					}
				}

				problemsText.append(sb);
			}

			output.append(problemsText);

			clipboardText = output.toString();
		}

		Clipboard clipboard = new Clipboard(UiDesk.getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] transfers = new Transfer[] {
			textTransfer
		};
		Object[] data = new Object[] {
			clipboardText
		};
		clipboard.setContents(data, transfers);
		clipboard.dispose();
		return clipboardText;
	}

	/**
	 * Leistung anhand des Kuerzels hinzufuegen
	 */
	private boolean addLeistungByMnemonic(String mnemonic, boolean approximation, boolean multi){
		boolean success = false;

		if (actKons != null && !StringTool.isNothing(mnemonic)) {
			Query<Artikel> query = new Query<Artikel>(Artikel.class);
			if (approximation) {
				query.add("Eigenname", "LIKE", mnemonic + "%");
			} else {
				query.add("Eigenname", "=", mnemonic);
			}
			List<Artikel> artikels = query.execute();

			if (artikels != null && !artikels.isEmpty()) {
				List<Artikel> selection = new ArrayList<Artikel>();
				if (multi) {
					selection.addAll(artikels);
				} else {
					selection.add(artikels.get(0));
				}

				List<Result<IVerrechenbar>> results = new ArrayList<Result<IVerrechenbar>>();
				PersistentObjectFactory factory = new PersistentObjectFactory();
				for (Artikel artikel : artikels) {
					String typ = artikel.get("Typ");
					String id = artikel.getId();

					// work-around for articles without class information (plugin
					// elexis-artikel-schweiz)
					if (typ.equals("Medikament") || typ.equals("Medical") || typ.equals("MiGeL")) {
						typ = "ch.elexis.artikel_ch.data." + typ;
					}

					PersistentObject po = factory.createFromString(typ + "::" + id);
					if (po instanceof IVerrechenbar) {
						Result<IVerrechenbar> result = actKons.addLeistung((IVerrechenbar) po);
						if (!result.isOK()) {
							results.add(result);
						}
					}
				}

				verrechnungViewer.refresh();
				updateVerrechnungSum();

				if (results.isEmpty()) {
					success = true;
				} else {
					StringBuffer sb = new StringBuffer();
					boolean first = true;
					for (Result<IVerrechenbar> result : results) {
						if (first) {
							first = false;
						} else {
							sb.append("; ");
						}
						sb.append(result.toString());
						SWTHelper.alert("Diese Verrechnung ist ungültig", sb.toString());
					}
				}
			}
		}

		return success;
	}

	/*
	 * Extendsion of KTable KTable doesn't update the scrollbar visibility if the model changes. We
	 * would require to call setModel(). As a work-around, we implement refresh(), which calls
	 * updateScrollbarVisibility() before redraw().
	 */
	class MyKTable extends KTable {
		public MyKTable(Composite parent, int style){
			super(parent, style);
		}

		public void refresh(){
			updateScrollbarVisibility();
			redraw();
		}
	}

	class ProblemsTableModel implements KTableModel {
		private Object[] problems = null;

		private final Hashtable<Integer, Integer> colWidths = new Hashtable<Integer, Integer>();
		private final Hashtable<Integer, Integer> rowHeights = new Hashtable<Integer, Integer>();

		private final KTableCellRenderer fixedRenderer = new FixedCellRenderer(
			FixedCellRenderer.STYLE_PUSH | FixedCellRenderer.INDICATION_SORT
				| FixedCellRenderer.INDICATION_FOCUS | FixedCellRenderer.INDICATION_CLICKED);

		private final KTableCellRenderer textRenderer = new ProblemsTableTextCellRenderer();

		private final KTableCellRenderer imageRenderer = new ProblemsTableImageCellRenderer();

		private final KTableCellRenderer therapyRenderer = new ProblemsTableTherapyCellRenderer();

		private Comparator comparator = DATE_COMPARATOR;

		private boolean highlightSelection = false;
		private boolean highlightRow = false;

		public Problem getProblem(int index){
			Problem problem = null;

			if (problems != null) {
				if (index >= 0 && index < problems.length) {
					Object element = problems[index];
					if (element instanceof Problem) {
						problem = (Problem) element;
					}
				}
			}

			return problem;
		}

		/**
		 * Finds the index of the given problem (array index, not row)
		 * 
		 * @param problem
		 * @return the index, or -1 if not found
		 */
		public int getIndexOf(Problem problem){
			if (problems != null) {
				for (int i = 0; i < problems.length; i++) {
					Object element = problems[i];
					if (element instanceof Problem) {
						Problem p = (Problem) element;
						if (p.getId().equals(problem.getId())) {
							return i;
						}
					}
				}
			}

			return -1;
		}

		/**
		 * Returns the KTable index corresponding to our model index (mapping)
		 * 
		 * @param rowIndex
		 *            the index of a problem
		 * @return the problem's index as a KTable index
		 */
		public int modelIndexToTableIndex(int rowIndex){
			return rowIndex + problemsTableModel.getFixedHeaderRowCount();

		}

		/**
		 * Returns the model index corresponding to the KTable index (mapping)
		 * 
		 * @param row
		 *            the KTable index of a problem
		 * @return the problem's index of the model
		 */
		public int tableIndexToRowIndex(int row){
			return row - problemsTableModel.getFixedHeaderRowCount();
		}

		@Override
		public Point belongsToCell(int col, int row){
			return new Point(col, row);
		}

		@Override
		public KTableCellEditor getCellEditor(int col, int row){
			if (row < getFixedHeaderRowCount() || col < getFixedHeaderColumnCount()) {
				return null;
			}

			int colIndex = col - getFixedHeaderColumnCount();

			if (colIndex == BEZEICHNUNG || colIndex == NUMMER || colIndex == DATUM) {
				return new MyKTableCellEditorText2();
			} else if (colIndex == THERAPIE) {
				return new KTableTherapyCellEditor();
			} else {
				return null;
			}
		}

		@Override
		public KTableCellRenderer getCellRenderer(int col, int row){
			if (row < getFixedHeaderRowCount() || col < getFixedHeaderColumnCount()) {
				return fixedRenderer;
			}

			int colIndex = col - getFixedHeaderColumnCount();

			if (colIndex == STATUS) {
				return imageRenderer;
			}

			if (colIndex == THERAPIE) {
				return therapyRenderer;
			}

			return textRenderer;
		}

		@Override
		public int getColumnCount(){
			return getFixedHeaderColumnCount() + COLUMN_TEXT.length;
		}

		@Override
		public int getRowCount(){
			loadElements();
			return getFixedHeaderRowCount() + problems.length;
		}

		@Override
		public int getFixedHeaderColumnCount(){
			return 1;
		}

		@Override
		public int getFixedSelectableColumnCount(){
			return 0;
		}

		@Override
		public int getFixedHeaderRowCount(){
			return 1;
		}

		@Override
		public int getFixedSelectableRowCount(){
			return 0;
		}

		private int getInitialColumnWidth(int col){
			if (col < getFixedHeaderColumnCount()) {
				return 20;
			}

			int colIndex = col - getFixedHeaderColumnCount();
			if (colIndex >= 0 && colIndex < COLUMN_TEXT.length) {
				int width =
					CoreHub.localCfg.get(COLUMN_CFG_KEY[colIndex], DEFAULT_COLUMN_WIDTH[colIndex]);
				return width;
			} else {
				// invalid column
				return 0;
			}
		}

		@Override
		public int getColumnWidth(int col){
			Integer width = colWidths.get(new Integer(col));
			if (width == null) {
				width = new Integer(getInitialColumnWidth(col));
				colWidths.put(new Integer(col), width);
			}

			return width.intValue();
		}

		private int getHeaderRowHeight(){
			// TODO
			return 22;
		}

		@Override
		public int getRowHeightMinimum(){
			// TODO
			return 10;
		}

		@Override
		public int getRowHeight(int row){
			Integer height = rowHeights.get(new Integer(row));
			if (height == null) {
				height = new Integer(getOptimalRowHeight(row));
				rowHeights.put(new Integer(row), height);
			}

			return height.intValue();
		}

		private int getOptimalRowHeight(int row){
			if (row < getFixedHeaderRowCount()) {
				return getHeaderRowHeight();
			} else {
				int height = 0;

				GC gc = new GC(problemsKTable);
				for (int i = 0; i < COLUMN_TEXT.length; i++) {
					int col = i + getFixedHeaderColumnCount();
					int currentHeight = 0;
					Object obj = getContentAt(col, row);
					if (obj instanceof String) {
						String text = (String) obj;
						currentHeight = gc.textExtent(text).y;
					} else if (obj instanceof Image) {
						Image image = (Image) obj;
						currentHeight = image.getBounds().height;
					} else if (obj instanceof Problem && i == THERAPIE) {
						Problem problem = (Problem) obj;
						ProblemsTableTherapyCellRenderer cellRenderer =
							(ProblemsTableTherapyCellRenderer) getCellRenderer(col, row);

						currentHeight = cellRenderer.getOptimalHeight(gc, problem);
					}

					if (currentHeight > height) {
						height = currentHeight;
					}
				}
				gc.dispose();

				return height;
			}
		}

		@Override
		public void setColumnWidth(int col, int width){
			colWidths.put(new Integer(col), new Integer(width));

			// store new column with in localCfg
			int colIndex = col - getFixedHeaderColumnCount();
			if (colIndex >= 0 && colIndex < COLUMN_TEXT.length) {
				CoreHub.localCfg.set(COLUMN_CFG_KEY[colIndex], width);
			}
		}

		@Override
		public void setRowHeight(int row, int height){
			rowHeights.put(new Integer(row), new Integer(height));
		}

		private void loadElements(){
			if (problems == null) {
				List<Object> elements = new ArrayList<Object>();

				if (actPatient != null) {
					List<Problem> problems = Problem.getProblemsOfPatient(actPatient);
					if (comparator != null) {
						Collections.sort(problems, comparator);
					}
					elements.addAll(problems);

					// add dummy element
					elements.add(new DummyProblem());
				}

				problems = elements.toArray();
			}
		}

		private void addElement(Object element){
			Object[] newProblems = new Object[problems.length + 1];
			System.arraycopy(problems, 0, newProblems, 0, problems.length);
			newProblems[newProblems.length - 1] = element;
		}

		private void reload(){
			// force elements to be reloaded
			problems = null;

			// force heights to be re-calculated
			rowHeights.clear();

		}

		private Object getHeaderContentAt(int col){
			int colIndex = col - getFixedHeaderColumnCount();

			if (colIndex >= 0 && colIndex < COLUMN_TEXT.length) {
				return COLUMN_TEXT[colIndex];
			} else {
				return "";
			}
		}

		@Override
		public Object getContentAt(int col, int row){
			if (row < getFixedHeaderRowCount()) {
				// header

				return getHeaderContentAt(col);
			}

			// rows

			// load problems if required
			loadElements();

			int colIndex = col - getFixedHeaderColumnCount();
			int rowIndex = row - getFixedHeaderRowCount(); // consider header row
			if (rowIndex >= 0 && rowIndex < problems.length) {
				Object element = problems[rowIndex];
				if (element instanceof Problem) {
					Problem problem = (Problem) element;

					String text;
					String lineSeparator;

					switch (colIndex) {
					case BEZEICHNUNG:
						return problem.getTitle();
					case NUMMER:
						return problem.getNumber();
					case DATUM:
						return problem.getStartDate();
					case DIAGNOSEN:
						String diagnosen = problem.getDiagnosenAsText();
						lineSeparator = System.getProperty("line.separator");
						text = diagnosen.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);
						return text;
						/*
						 * case GESETZ: return problem.getGesetz();
						 */
						/*
						 * case RECHNUNGSDATEN: return "not yet implemented";
						 */
					case THERAPIE:
						/*
						 * String prescriptions = problem.getPrescriptionsAsText(); lineSeparator =
						 * System.getProperty("line.separator"); text =
						 * prescriptions.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator); return
						 * text;
						 */
						return problem;
						/*
						 * case PROCEDERE: return problem.getProcedere();
						 */
					case STATUS:
						if (problem.getStatus() == Episode.ACTIVE) {
							return UiDesk.getImage(Iatrix.IMG_ACTIVE);
						} else {
							return UiDesk.getImage(Iatrix.IMG_INACTIVE);
						}
					default:
						return "";
					}
				} else {
					// DummyProblem

					if (col < getFixedHeaderColumnCount()) {
						return "*";
					} else {
						return "";
					}
				}
			} else {
				// row index out of bound
				return "";
			}
		}

		@Override
		public String getTooltipAt(int col, int row){
			if (col < TOOLTIP_TEXT.length)
				return TOOLTIP_TEXT[col];
			else
				return "Tooltip für col " + col;
		}

		@Override
		public boolean isColumnResizable(int col){
			return true;
		}

		@Override
		public boolean isRowResizable(int row){
			return true;
		}

		@Override
		public void setContentAt(int col, int row, Object value){
			// don't do anything if there are no problems
			if (problems == null) {
				return;
			}

			// only accept String values
			if (!(value instanceof String)) {
				return;
			}

			String text = (String) value;

			int colIndex = col - getFixedHeaderColumnCount();
			int rowIndex = row - getFixedHeaderRowCount();

			if (rowIndex >= 0 && rowIndex < problems.length) {
				boolean isNew = false;

				Problem problem;
				if (problems[rowIndex] instanceof Problem) {
					problem = (Problem) problems[rowIndex];
				} else {
					// replace dummy object with real object

					if (actPatient == null) {
						// shuldn't happen; silently ignore
						return;
					}

					problem = new Problem(actPatient, "");
					String currentDate = new TimeTool().toString(TimeTool.DATE_ISO);
					problem.setStartDate(currentDate);
					IatrixEventHelper.fireSelectionEventProblem(problem);

					problems[rowIndex] = problem;

					addElement(new DummyProblem());

					isNew = true;
				}

				switch (colIndex) {
				case BEZEICHNUNG:
					problem.setTitle(text);
					break;
				case NUMMER:
					problem.setNumber(text);
					break;
				case DATUM:
					problem.setStartDate(text);
					break;
				case THERAPIE:
					problem.setProcedere(text);
					break;
				}

				if (isNew) {
					reload();
					problemsKTable.refresh();
				}
			}
		}

		public void setComparator(int col, int row){
			if (row < problemsTableModel.getFixedHeaderRowCount()) {
				int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();

				switch (colIndex) {
				case DATUM:
					comparator = DATE_COMPARATOR;
					break;
				case NUMMER:
					comparator = NUMBER_COMPARATOR;
					break;
				case STATUS:
					comparator = STATUS_COMPARATOR;
					break;
				}
			}

		}

		public void setHighlightSelection(boolean highlight, boolean row){
			this.highlightSelection = highlight;
			this.highlightRow = row;
		}

		public boolean isHighlightSelection(){
			return highlightSelection;
		}

		public boolean isHighlightRow(){
			return highlightSelection && highlightRow;
		}
	}

	class ProblemsTableColorProvider {
		public Color getForegroundColor(int col, int row){
			int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
			Problem problem = problemsTableModel.getProblem(rowIndex);
			if (problem != null && problem.getStatus() == Episode.ACTIVE) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			} else {
				return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}
		}
	}

	abstract class ProblemsTableCellRendererBase implements KTableCellRenderer {
		protected Problem getSelectedProblem(){
			Point[] selection = problemsKTable.getCellSelection();
			if (selection == null || selection.length == 0) {
				return null;
			} else {
				int rowIndex = selection[0].y - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);
				return problem;
			}

		}

		protected boolean isSelected(int row){
			if (problemsKTable.isRowSelectMode()) {
				int[] selectedRows = problemsKTable.getRowSelection();
				if (selectedRows != null) {
					for (int r : selectedRows) {
						if (r == row) {
							return true;
						}
					}
				}
			} else {
				Point[] selectedCells = problemsKTable.getCellSelection();
				if (selectedCells != null) {
					for (Point cell : selectedCells) {
						if (cell.y == row) {
							return true;
						}
					}
				}
			}

			return false;
		}

	}

	class ProblemsTableTextCellRenderer extends ProblemsTableCellRendererBase {
		private final Display display;

		public ProblemsTableTextCellRenderer(){
			display = Display.getCurrent();
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof String) {
				String text = (String) content;
				return gc.textExtent(text).x + 8;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content,
			boolean focus, boolean fixed, boolean clicked, KTableModel model){
			Color textColor;
			Color backColor;
			Color borderColor;

			String text;
			if (content instanceof String) {
				text = (String) content;
			} else {
				text = "";
			}

			if (focus) {
				textColor = display.getSystemColor(SWT.COLOR_BLUE);
			} else {
				textColor = problemsTableColorProvider.getForegroundColor(col, row);
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}
			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);
			gc.setForeground(textColor);

			gc.fillRectangle(rect);
			Rectangle oldClipping = gc.getClipping();
			gc.setClipping(rect);
			gc.drawText((text), rect.x + 3, rect.y);
			gc.setClipping(oldClipping);

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	class ProblemsTableImageCellRenderer extends ProblemsTableCellRendererBase {
		private final Display display;

		public ProblemsTableImageCellRenderer(){
			display = Display.getCurrent();
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof Image) {
				Image image = (Image) content;
				return image.getBounds().width;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content,
			boolean focus, boolean fixed, boolean clicked, KTableModel model){
			Color backColor;
			Color borderColor;

			Image image = null;
			if (content instanceof Image) {
				image = (Image) content;
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}

			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);

			gc.fillRectangle(rect);

			if (image != null) {
				// center image
				Rectangle imageBounds = image.getBounds();
				int imageWidth = imageBounds.width;
				int imageHeight = imageBounds.height;
				int xOffset = (rect.width - imageWidth) / 2;
				int yOffset = (rect.height - imageHeight) / 2;

				Rectangle oldClipping = gc.getClipping();
				gc.setClipping(rect);
				gc.drawImage(image, rect.x + xOffset, rect.y + yOffset);
				gc.setClipping(oldClipping);
			}

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 * Renderer for Therapy cell. Shows the procedere of the problem. If there are prescriptions,
	 * they are shown above the procedere, separated by a line.
	 * 
	 * @author danlutz
	 */
	class ProblemsTableTherapyCellRenderer extends ProblemsTableCellRendererBase {
		private static final int MARGIN = 8;
		private static final int PADDING = 3;

		private final Display display;

		public ProblemsTableTherapyCellRenderer(){
			display = Display.getCurrent();
		}

		private boolean hasPrescriptions(Problem problem){
			List<Prescription> prescriptions = problem.getPrescriptions();
			return (prescriptions.size() > 0);
		}

		private boolean hasProcedere(Problem problem){
			if (!StringTool.isNothing(PersistentObject.checkNull(problem.getProcedere()))) {
				return true;
			} else {
				return false;
			}
		}

		private String getPrescriptionsText(Problem problem){
			String prescriptions = PersistentObject.checkNull(problem.getPrescriptionsAsText());
			String lineSeparator = System.getProperty("line.separator");
			String prescriptionsText =
				prescriptions.replaceAll(Problem.TEXT_SEPARATOR, lineSeparator);

			return prescriptionsText;
		}

		private String getProcedereText(Problem problem){
			return PersistentObject.checkNull(problem.getProcedere());
		}

		public int getOptimalHeight(GC gc, Problem problem){
			int height = 0;

			int prescriptionsHeight = 0;
			if (hasPrescriptions(problem)) {
				String prescriptionsText = getPrescriptionsText(problem);
				prescriptionsHeight = gc.textExtent(prescriptionsText).y;
			}

			int procedereHeight = 0;
			if (hasProcedere(problem)) {
				String procedereText = getProcedereText(problem);
				procedereHeight = gc.textExtent(procedereText).y;
			}

			if (prescriptionsHeight > 0 && procedereHeight > 0) {
				height = prescriptionsHeight + PADDING + procedereHeight;
			} else if (prescriptionsHeight > 0) {
				height = prescriptionsHeight;
			} else if (procedereHeight > 0) {
				height = procedereHeight;
			}

			if (height == 0) {
				// default height
				height = gc.textExtent("").y;
			}

			return height;
		}

		@Override
		public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed,
			KTableModel model){
			if (content instanceof Problem) {
				Problem problem = (Problem) content;

				String prescriptionsText = getPrescriptionsText(problem);
				String procedereText = getProcedereText(problem);

				int width1 = gc.textExtent(prescriptionsText).x;
				int width2 = gc.textExtent(procedereText).x;
				int width = Math.max(width1, width2);

				return width + MARGIN;
			} else {
				return 0;
			}
		}

		@Override
		public void drawCell(GC gc, Rectangle rect, int col, int row, Object content,
			boolean focus, boolean fixed, boolean clicked, KTableModel model){
			Color textColor;
			Color backColor;
			Color borderColor;

			String prescriptionsText = "";
			String procedereText = "";
			boolean hasPrescriptions = false;
			boolean hasProcedere = false;

			if (content instanceof Problem) {
				Problem problem = (Problem) content;

				prescriptionsText = getPrescriptionsText(problem);
				procedereText = getProcedereText(problem);
				hasPrescriptions = hasPrescriptions(problem);
				hasProcedere = hasProcedere(problem);
			}

			if (focus) {
				textColor = display.getSystemColor(SWT.COLOR_BLUE);
			} else {
				textColor = problemsTableColorProvider.getForegroundColor(col, row);
			}

			if (isSelected(row) && ((ProblemsTableModel) model).isHighlightRow()) {
				backColor = highlightColor;
			} else if (focus && ((ProblemsTableModel) model).isHighlightSelection()) {
				backColor = highlightColor;
			} else {
				backColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			}

			borderColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

			gc.setForeground(borderColor);
			gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

			gc.setBackground(backColor);
			gc.setForeground(textColor);

			gc.fillRectangle(rect);

			Rectangle oldClipping = gc.getClipping();
			gc.setClipping(rect);

			if (hasPrescriptions && hasProcedere) {
				// draw prescriptions and procedre, separated by a line
				int prescriptionsHeight = gc.textExtent(prescriptionsText).y;

				gc.setForeground(borderColor);
				gc.drawLine(rect.x, rect.y + prescriptionsHeight + 1, rect.x + rect.width, rect.y
					+ prescriptionsHeight + 1);

				gc.setBackground(backColor);
				gc.setForeground(textColor);

				gc.drawText(prescriptionsText, rect.x + 3, rect.y);
				gc.drawText(procedereText, rect.x + 3, rect.y + prescriptionsHeight + PADDING);
			} else {
				String text;
				if (hasPrescriptions) {
					// prescriptions only
					text = prescriptionsText;
				} else if (hasProcedere) {
					// procedere only
					text = procedereText;
				} else {
					// nothing
					text = "";
				}

				gc.setBackground(backColor);
				gc.setForeground(textColor);

				gc.drawText(text, rect.x + 3, rect.y);
			}

			gc.setClipping(oldClipping);

			if (focus) {
				gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	public class KTableTherapyCellEditor extends BaseCellEditor {
		private Text m_Text;

		private final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					// Do nothing
				}
			}
		};

		private final TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);

			String text = "";
			Object obj = m_Model.getContentAt(m_Col, m_Row);
			if (obj instanceof Problem) {
				Problem problem = (Problem) obj;
				text = problem.getProcedere();
			}

			m_Text.setText(PersistentObject.checkNull(text));
			m_Text.selectAll();
			m_Text.setVisible(true);
			m_Text.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
			m_Text.removeKeyListener(keyListener);
			m_Text.removeTraverseListener(travListener);
			m_Text = null;
			super.close(save);
		}

		@Override
		protected Control createControl(){
			m_Text = new Text(m_Table, SWT.MULTI | SWT.V_SCROLL);
			m_Text.addKeyListener(keyListener);
			m_Text.addTraverseListener(travListener);
			return m_Text;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 * 
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		/*
		 * overridden from superclass
		 */
		@Override
		public void setBounds(Rectangle rect){
			super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			m_Text.setText(content.toString());
			m_Text.setSelection(content.toString().length());
		}

	}

	/**
	 * Base class for our cell editors Especially, we need to take care of heartbeat management
	 */
	abstract class BaseCellEditor extends KTableCellEditor {
		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			setHeartbeatProblemEnabled(false);

			super.open(table, col, row, rect);
		}

		@Override
		public void close(boolean save){
			super.close(save);

			setHeartbeatProblemEnabled(true);
		}
	}

	/**
	 * Replacement for KTableCellEditorText2 We don't want to have the editor vertically centered
	 * 
	 * @author danlutz
	 * 
	 */
	public class MyKTableCellEditorText2 extends BaseCellEditor {
		protected Text m_Text;

		protected KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
					// Do nothing
				}
			}
		};

		protected TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);
			m_Text.setText(m_Model.getContentAt(m_Col, m_Row).toString());
			m_Text.selectAll();
			m_Text.setVisible(true);
			m_Text.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, m_Text.getText());
			m_Text.removeKeyListener(keyListener);
			m_Text.removeTraverseListener(travListener);
			super.close(save);
			m_Text = null;
		}

		@Override
		protected Control createControl(){
			m_Text = new Text(m_Table, SWT.NONE);
			m_Text.addKeyListener(keyListener);
			m_Text.addTraverseListener(travListener);
			return m_Text;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 * 
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		@Override
		protected void onKeyPressed(KeyEvent e){
			if ((e.character == '\r') && ((e.stateMask & SWT.SHIFT) == 0)) {
				close(true);
				// move one row below!
				// if (m_Row<m_Model.getRowCount())
				// m_Table.setSelection(m_Col, m_Row+1, true);
			} else
				super.onKeyPressed(e);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			m_Text.setText(content.toString());
			m_Text.setSelection(content.toString().length());
		}
	}

	public class KTableDiagnosisCellEditor extends BaseCellEditor {
		private Combo combo;

		private final KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				try {
					onKeyPressed(e);
				} catch (Exception ex) {
					// Do nothing
				}
			}
		};

		private final TraverseListener travListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e){
				onTraverse(e);
			}
		};

		@Override
		public void open(KTable table, int col, int row, Rectangle rect){
			super.open(table, col, row, rect);

			String text = "";
			Object obj = m_Model.getContentAt(m_Col, m_Row);
			if (obj instanceof Problem) {
				Problem problem = (Problem) obj;
				text = "test";
			}

			combo.setText(text);
			combo.setVisible(true);
			combo.setFocus();
		}

		@Override
		public void close(boolean save){
			if (save)
				m_Model.setContentAt(m_Col, m_Row, combo.getText());
			combo.removeKeyListener(keyListener);
			combo.removeTraverseListener(travListener);
			combo = null;
			super.close(save);
		}

		@Override
		protected Control createControl(){
			combo = new Combo(m_Table, SWT.DROP_DOWN);
			combo.addKeyListener(keyListener);
			combo.addTraverseListener(travListener);
			return combo;
		}

		/**
		 * Implement In-Textfield navigation with the keys...
		 * 
		 * @see de.kupzog.ktable.KTableCellEditor#onTraverse(org.eclipse.swt.events.TraverseEvent)
		 */
		@Override
		protected void onTraverse(TraverseEvent e){
			if (e.keyCode == SWT.ARROW_LEFT) {
				// handel the event within the text widget!
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				// handle the event within the text widget!
			} else
				super.onTraverse(e);
		}

		/*
		 * overridden from superclass
		 */
		@Override
		public void setBounds(Rectangle rect){
			super.setBounds(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.Object)
		 */
		@Override
		public void setContent(Object content){
			combo.setText(content.toString());
		}

	}

	class ProblemAssignmentLabelProvider extends LabelProvider implements ITableColorProvider {
		@Override
		public Color getForeground(Object element, int columnIndex){
			Color color = null;

			if (problemAssignmentViewer.getChecked(element)) {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			} else {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			}

			return color;
		}

		@Override
		public Color getBackground(Object element, int columnIndex){
			// we don't set the background
			return null;
		}

	}

	static class DateComparator implements Comparator<Problem> {
		@Override
		public int compare(Problem o1, Problem o2){
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (o1 == null) {
				return 1;
			}

			if (o2 == null) {
				return -1;
			}

			return PersistentObject.checkNull(o1.getStartDate()).compareTo(
				PersistentObject.checkNull(o2.getStartDate()));
		}

		@Override
		public boolean equals(Object obj){
			return super.equals(obj);
		}
	}

	static class NumberComparator implements Comparator<Problem> {
		@Override
		public int compare(Problem o1, Problem o2){
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (o1 == null) {
				return 1;
			}

			if (o2 == null) {
				return -1;
			}

			return PersistentObject.checkNull(o1.getNumber()).compareTo(
				PersistentObject.checkNull(o2.getNumber()));
		}

		@Override
		public boolean equals(Object obj){
			return super.equals(obj);
		}
	}

	/**
	 * Compare by status. ACTIVE problems are sorted before INACTIVE problems. Problems with same
	 * status are sorted by date.
	 * 
	 * @author danlutz
	 */
	static class StatusComparator implements Comparator<Problem> {
		@Override
		public int compare(Problem o1, Problem o2){
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (o1 == null) {
				return 1;
			}

			if (o2 == null) {
				return -1;
			}

			int status1 = o1.getStatus();
			int status2 = o2.getStatus();

			if (status1 == status2) {
				// same status, compare date
				return PersistentObject.checkNull(o1.getStartDate()).compareTo(
					PersistentObject.checkNull(o2.getStartDate()));
			} else if (status1 == Episode.ACTIVE) {
				return -1;
			} else {
				return 1;
			}
		}

		@Override
		public boolean equals(Object obj){
			return super.equals(obj);
		}
	}

	static class DummyProblem {}

	// helper method to create a KonsTextLock object in a save way
	// should be called when a new konsultation is set
	private void createKonsTextLock(){
		// remove old lock
		removeKonsTextLock();

		if (actKons != null && CoreHub.actUser != null) {
			konsTextLock = new KonsTextLock(actKons, CoreHub.actUser);
		} else {
			konsTextLock = null;
		}

		if (konsTextLock != null) {
			boolean success = konsTextLock.lock();
			log.log("createKonsTextLock: konsText locked (" + success + ")", Log.DEBUGMSG);
			// System.err.println("DEBUG: createKonsTextLock: konsText locked (" + success + ")");
		}
	}

	// helper method to release a KonsTextLock
	// should be called before a new konsultation is set
	// or the program/view exits
	private void removeKonsTextLock(){
		if (konsTextLock != null) {
			boolean success = konsTextLock.unlock();
			log.log("removeKonsTextLock: konsText unlocked (" + success + ")", Log.DEBUGMSG);
			// System.err.println("DEBUG: removeKonsTextLock: konsText unlocked (" + success + ")");
			konsTextLock = null;
		}
	}

	/**
	 * Check whether we own the lock
	 * 
	 * @return true, if we own the lock, false else
	 */
	private boolean hasKonsTextLock(){
		return (konsTextLock != null && konsTextLock.isLocked());
	}
}
