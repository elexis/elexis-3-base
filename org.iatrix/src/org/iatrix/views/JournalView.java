/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.views;

import static ch.elexis.core.data.events.ElexisEvent.EVENT_CREATE;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_DELETE;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_DESELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_RELOAD;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_SELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_UPDATE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.Iatrix;
import org.iatrix.data.Problem;
import org.iatrix.util.Constants;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Helpers;
import org.iatrix.widgets.IJournalArea;
import org.iatrix.widgets.IJournalArea.KonsActions;
import org.iatrix.widgets.JournalHeader;
import org.iatrix.widgets.KonsDiagnosen;
import org.iatrix.widgets.KonsHeader;
import org.iatrix.widgets.KonsListDisplay;
import org.iatrix.widgets.KonsProblems;
import org.iatrix.widgets.KonsText;
import org.iatrix.widgets.KonsVerrechnung;
import org.iatrix.widgets.ProblemArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Anwender;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.extdoc.util.Email;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.TimeTool;
import de.kupzog.ktable.KTable;

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

public class JournalView extends ViewPart implements IActivationListener, ISaveablePart2 {

	public static final String ID = Constants.ID;

	private static Logger log = LoggerFactory.getLogger(JournalView.class);
	private static Patient actPatient = null;
	private static Konsultation actKons = null;

	private FormToolkit tk;
	private Form form;

	// container for hKonsultationDatum, hlMandant, cbFall

	// Parts (from top to bottom that make up our display
	private JournalHeader formHeader = null; // Patient name, sex, birthday, remarks, sticker, account, balance, account overview
	private KTable problemsKTable = null; // On top
	private ProblemArea problemsArea = null; // KTable with Date, nr, diagnosis, therapy, code, activ/inactiv
	private KonsProblems konsProblems = null; // left: List of Checkbox of all problems for this consultation
	private KonsText konsTextComposite; // Konsultationtext (with lock over all stations), revision info
	private KonsVerrechnung konsVerrechnung = null; // right: Items to be billed for select consultation
	private KonsDiagnosen konsDiagnosen = null; // diagnosis line
	private KonsListDisplay konsListDisplay; // bottom list of all consultations date, decreasing with date, mandant, case, text, billed items

	private ViewMenus menus;

	/* Actions */
	private IAction exportToClipboardAction;
	private IAction sendEmailAction;
	private IAction addKonsultationAction;
	private Action showAllChargesAction;
	private Action showAllConsultationsAction;

	private static List<IJournalArea> allAreas;

	private KonsHeader konsHeader;

	private Heartbeat heartbeat;

	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		heartbeat = Heartbeat.getInstance();
		tk = UiDesk.getToolkit();
		form = tk.createForm(parent);
		Composite formBody = form.getBody();

		formBody.setLayout(new GridLayout(1, true));
		formHeader = new JournalHeader(formBody);

		SashForm mainSash = new SashForm(form.getBody(), SWT.VERTICAL);
		mainSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite topArea = tk.createComposite(mainSash, SWT.NONE);
		topArea.setLayout(new FillLayout(SWT.VERTICAL));
		topArea.setBackground(topArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		problemsArea = new ProblemArea(topArea, JournalView.this.getPartName(), getViewSite());
		problemsKTable = problemsArea.getProblemKTable();
		Composite middleArea = tk.createComposite(mainSash, SWT.NONE);
		middleArea.setLayout(new FillLayout());
		Composite konsultationComposite = tk.createComposite(middleArea);
		konsultationComposite.setLayout(new GridLayout(1, true));

		konsHeader = new KonsHeader(konsultationComposite);

		SashForm konsultationSash = new SashForm(konsultationComposite, SWT.HORIZONTAL);
		konsultationSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite assignmentComposite = tk.createComposite(konsultationSash);
		assignmentComposite.setLayout(new GridLayout(1, true));
		konsProblems = new KonsProblems(assignmentComposite); // on the left side
		Composite konsultationTextComposite = tk.createComposite(konsultationSash);
		konsultationTextComposite.setLayout(new GridLayout(1, true));
		konsTextComposite = new KonsText(konsultationTextComposite);
		konsDiagnosen = new KonsDiagnosen(konsultationComposite);
		Composite verrechnungComposite = tk.createComposite(konsultationSash);
		konsVerrechnung = new KonsVerrechnung(verrechnungComposite, form,
			JournalView.this.getPartName(), assignmentComposite);
		if (konsultationSash.getChildren().length == 3) {
			konsultationSash.setWeights(new int[] {
				15, 65, 20
			});
		} else {
			// System.out.println("konsSash should have 3, but has " + konsultationSash.getChildren().length + " children");
		}
		Composite bottomArea = tk.createComposite(mainSash, SWT.NONE);
		bottomArea.setLayout(new FillLayout());
		bottomArea.setBackground(bottomArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		konsListDisplay = new KonsListDisplay(bottomArea);

		mainSash.setWeights(new int[] {
			20, 40, 30
		});
		allAreas = new ArrayList<>();
		allAreas.add(konsTextComposite); // Let the konsText be available for input as soon as possible
		allAreas.add(formHeader);
		allAreas.add(problemsArea);
		allAreas.add(konsHeader);
		allAreas.add(konsProblems);
		allAreas.add(konsDiagnosen);
		allAreas.add(konsVerrechnung);
		allAreas.add(konsListDisplay);
		makeActions();
		menus = new ViewMenus(getViewSite());
		if (CoreHub.acl.request(AccessControlDefaults.AC_PURGE)) {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction, problemsArea.addProblemAction,
				GlobalActions.delKonsAction, problemsArea.delProblemAction, exportToClipboardAction, sendEmailAction,
				konsTextComposite.getVersionForwardAction(), konsTextComposite.getVersionBackAction(),
				konsTextComposite.getChooseVersionAction(), konsTextComposite.getPurgeAction(),
				konsTextComposite.getSaveAction(), showAllConsultationsAction, showAllChargesAction,
				problemsArea.addFixmedikationAction);
		} else {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction, problemsArea.addProblemAction,
				GlobalActions.delKonsAction, problemsArea.delProblemAction, exportToClipboardAction, sendEmailAction,
				konsTextComposite.getVersionForwardAction(), konsTextComposite.getVersionBackAction(),
				konsTextComposite.getChooseVersionAction(), konsTextComposite.getSaveAction(),
				showAllConsultationsAction, showAllChargesAction, problemsArea.addFixmedikationAction);
		}

		menus.createToolbar(sendEmailAction, exportToClipboardAction, addKonsultationAction,
			problemsArea.getAddProblemAction(), konsTextComposite.getSaveAction());
		menus.createViewerContextMenu(konsProblems.getProblemAssignmentViewer(), konsProblems.unassignProblemAction);
		menus.createViewerContextMenu(konsVerrechnung.getVerrechnungViewer(),
			konsVerrechnung.changeVerrechnetPreisAction, konsVerrechnung.changeVerrechnetZahlAction,
			konsVerrechnung.delVerrechnetAction);

		GlobalEventDispatcher.addActivationListener(this, this);
		activateContext();
	}

	private void updateAllPatientAreas(Patient newPatient){
		logEvent("updateAllPatientAreas: " + newPatient);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setPatient(newPatient);
			}
		}
	}

	public static void saveActKonst(){
		if (actKons == null) {
			return;
		}
		logEvent("saveActKonst: " + actKons);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setKons(actKons, KonsActions.SAVE_KONS);
			}
		}
	}

	public static void updateAllKonsAreas(Konsultation newKons, IJournalArea.KonsActions op){
		/*
		 * Not yet sure whether comparing only the id or the whole cons is better
		 */
		actKons = newKons;
		if (newKons == null) {
			return;
		}
		// It is a bad idea to skip updating the kons, when the Id matches
		// Some changes, e.g. when date of actual kons are possible even when the compare matches.
		// Therefore we return only when we have nothing to update savedKonst == newKons?" + newId + " konsId match? " + savedKonsId.equals(newId));
		logEvent("updateAllKonsAreas: newId " + newKons.getId());
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setKons(newKons, op);
			}
		}
	}

	private void activateAllKonsAreas(boolean mode){
		logEvent("activateAllKonsAreas: " + mode);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.activation(mode);
			}
		}
	}

	private void visibleAllKonsAreas(boolean mode){
		logEvent("visibleAllKonsAreas: " + mode);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.visible(mode);
			}
		}
	}

	private final ElexisUiEventListenerImpl eeli_problem =
		new ElexisUiEventListenerImpl(Episode.class, EVENT_UPDATE | EVENT_DESELECTED) {

			@Override
			public void runInUi(ElexisEvent ev){
				switch (ev.getType()) {
				case EVENT_UPDATE:
					// problem change may affect current problems list and consultation
					// TODO check if problem is part of current consultation
					// work-around: just update the current patient and consultation
					logEvent("eeli_problem EVENT_UPDATE");
					problemsArea.reloadAndRefresh();
					break;
				case EVENT_DESELECTED:
					logEvent("eeli_problem EVENT_DESELECTED");
					problemsKTable.clearSelection();
					break;
				}

			}
		};

	private ElexisEventListener eeli_kons_filter = new ElexisEventListener() {
		private final ElexisEvent eetempl =	new ElexisEvent(null, Konsultation.class,
				EVENT_UPDATE | EVENT_RELOAD | EVENT_CREATE | EVENT_DELETE);

		@Override
		public ElexisEvent getElexisEventFilter(){
			return eetempl;
		}

		@Override
		public void catchElexisEvent(ElexisEvent ev){
			UiDesk.asyncExec(new Runnable() {
				@Override
				public void run(){
					Konsultation kons = (Konsultation) ev.getObject();
					// log.debug("catchElexisEvent " + ev.getType() + " kons " + kons.getId());
					// konsListDisplay.setKons(kons, KonsActions.ACTIVATE_KONS);
				}
			});
		}
	};

	private final ElexisUiEventListenerImpl eeli_kons =
			new ElexisUiEventListenerImpl(Konsultation.class,
				EVENT_SELECTED | EVENT_UPDATE | EVENT_RELOAD) {

		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation newKons = (Konsultation) ev.getObject();
			String msg = "unknown";
			switch (ev.getType()) {
			case EVENT_SELECTED:
				msg = "EVENT_SELECTED";
				break;
			case EVENT_UPDATE:
				msg = "EVENT_UPDATE";
				break;
			case EVENT_RELOAD:
				msg = "EVENT_RELOAD";
				break;
			}
			logEvent("eeli_kons " + msg + " " + newKons.getId());
			// when we get an update or select event the parameter is always not null
			if (newKons != actKons) {
				Patient newPatient = newKons.getFall().getPatient();
				if (newPatient != actPatient) {
					displaySelectedPatient(newPatient, "eeli_kons newPatient");
				}
				updateAllKonsAreas(newKons, KonsActions.ACTIVATE_KONS);
			}
			actKons = newKons;
		}

	};

	/**
	 * Helper to update every thing whether we got notified by opening the view
	 * or the selected patient changed
	 *
	 * @param selectedPatient patient to be displayed
	 * @param why 		Where do we come from (Only used for the logging)
	 */
	private void displaySelectedPatient(Patient selectedPatient, String why){
		if (selectedPatient == null) {
			logEvent(why + " displaySelectedPatient " + "no patient");
		} else {
			logEvent(why + " displaySelectedPatient " + selectedPatient.getId() + selectedPatient.getPersonalia());
		}

		showAllChargesAction.setChecked(false);
		showAllConsultationsAction.setChecked(false);

		// Find the most recent open konsultation for the given fall
		// If nothing found or not of today, create a new konsultation
		Konsultation konsultation = null;
		if (selectedPatient != null) {
			konsultation = selectedPatient.getLetzteKons(false);
			if (konsultation == null) {
				Fall[] faelle = selectedPatient.getFaelle();
				if (faelle.length == 0) {
					konsultation = selectedPatient.createFallUndKons();
				} else {
					for (Fall fall : faelle) {
						if (fall.isOpen()) {
							konsultation = fall.getLetzteBehandlung();
							if (konsultation == null) {
								konsultation = fall.neueKonsultation();
							} else {
								TimeTool konsDate = new TimeTool(konsultation.getDatum());
								if (!konsDate.isSameDay(new TimeTool())) {
									konsultation = konsultation.getFall().neueKonsultation();
								}
							}
							log.debug("displaySelectedPatient neue Kons fall.isOpen " +  konsultation.getId() + " " + konsultation.getLabel());
							break;
						}
					}
					if (konsultation == null) {
						konsultation = selectedPatient.createFallUndKons();
						log.debug("displaySelectedPatient neue Kons createFallUndKons " + konsultation.getId() + " " + konsultation.getLabel());
					}
				}
			}
			TimeTool konsDate = new TimeTool(konsultation.getDatum());
			if (!konsDate.isSameDay(new TimeTool())) {
				konsultation = konsultation.getFall().neueKonsultation();
			}
		}

		setPatient(selectedPatient);
		actKons = konsultation;
		updateAllKonsAreas(actKons, KonsActions.ACTIVATE_KONS);
	}

	private final ElexisUiEventListenerImpl eeli_pat =
		// Soll hier auch noch auf RELOAD und UPDATE reagiert werden
		new ElexisUiEventListenerImpl(Patient.class, ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_RELOAD | ElexisEvent.EVENT_UPDATE) {

			@Override
			public void runInUi(ElexisEvent ev){
				displaySelectedPatient((Patient) ev.getObject(), "eeli_pat " + ev.getType());
				// setPatient((Patient) ev.getObject());
			}
		};

	private final ElexisUiEventListenerImpl eeli_user =
		new ElexisUiEventListenerImpl(Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
			@Override
			public void runInUi(ElexisEvent ev){
				logEvent("runInUi eeli_user adaptMenus");
				adaptMenus();
			}
		};

	/**
	 * Activate a context that this view uses. It will be tied to this view activation events and
	 * will be removed when the view is disposed. Copied from
	 * org.eclipse.ui.examples.contributions.InfoView.java
	 */
	private void activateContext(){
		IContextService contextService =
				(IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(Constants.VIEW_CONTEXT_ID);
	}

	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_kons_filter, eeli_kons, eeli_problem,
			eeli_pat, eeli_user);
		super.dispose();
	}

	@Override
	public void setFocus(){}

	public void adaptMenus(){
		konsVerrechnung.getVerrechnungViewer().getTable().getMenu()
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN));

		// TODO this belongs to GlobalActions itself (action creator)
		GlobalActions.delKonsAction
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.KONS_DELETE));
		GlobalActions.neueKonsAction
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.KONS_CREATE));
	}

	private void makeActions(){
		// Konsultation

		// Replacement for GlobalActions.neueKonsAction (other image)
		addKonsultationAction = new Action(GlobalActions.neueKonsAction.getText()) {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix",
					"icons/new_konsultation.ico"));
				setToolTipText(GlobalActions.neueKonsAction.getToolTipText());
			}

			@Override
			public void run(){
				GlobalActions.neueKonsAction.run();
			}
		};
		addKonsultationAction.setActionDefinitionId(Constants.NEWCONS_COMMAND);
		GlobalActions.registerActionHandler(this, addKonsultationAction);

		// Probleme
		if (problemsArea != null) {
			GlobalActions.registerActionHandler(this, problemsArea.addProblemAction);
			problemsArea.addProblemAction.setActionDefinitionId(Constants.NEWPROBLEM_COMMAND);
		}

		exportToClipboardAction = new Action("Export (Zwischenablage)") {
			{
				setImageDescriptor(Images.IMG_EXPORT.getImageDescriptor());
				setToolTipText("Zusammenfassung in Zwischenablage kopieren");
			}

			@Override
			public void run(){
				Helpers.exportToClipboard(actPatient, null); // TODO: selected problem
			}
		};
		exportToClipboardAction.setActionDefinitionId(Constants.EXPORT_CLIPBOARD_COMMAND);
		GlobalActions.registerActionHandler(this, exportToClipboardAction);

		sendEmailAction = new Action("E-Mail verschicken") {
			{
				setImageDescriptor(Images.IMG_MAIL.getImageDescriptor());
				setToolTipText("E-Mail Programm öffnent (mit Medikation und allen Konsultationen)");
			}

			@Override
			public void run(){
				Email.openMailApplication("", // No default to address
					null, Helpers.exportToClipboard(actPatient, null), // TODO: selected problem
					null);

			}
		};
		sendEmailAction.setActionDefinitionId(Constants.EXPORT_SEND_EMAIL_COMMAND);
		GlobalActions.registerActionHandler(this, sendEmailAction);

		// history display
		showAllChargesAction = new Action("Alle Leistungen anzeigen", Action.AS_CHECK_BOX) {
			{
				setToolTipText(
					"Leistungen aller Konsultationen anzeigen, nicht nur der ersten paar.");
			}

			@Override
			public void run(){
				konsListDisplay.setPatient(actPatient, showAllChargesAction.isChecked(),
					showAllConsultationsAction.isChecked());
			}
		};
		showAllChargesAction.setActionDefinitionId(Iatrix.SHOW_ALL_CHARGES_COMMAND);
		GlobalActions.registerActionHandler(this, showAllChargesAction);

		showAllConsultationsAction = new Action("Alle Konsultationen anzeigen",
			Action.AS_CHECK_BOX) {
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

	@Override
	public void activation(boolean mode){
		Konsultation selected_kons = (Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		if (selected_kons != null && actKons != null && selected_kons.getId() != actKons.getId()) {
			// this should never happen
			logEvent("activation " + mode + "sel: " + selected_kons.getLabel() + " act: " + actKons.getId());
			return;
		}
		activateAllKonsAreas(mode);
		if (mode == false) {
			// text is neither dirty nor changed.
			// If it es empty and nothing has been billed, we just delete this kons.
			if (actKons == null) {
				return;
			}
			boolean noLeistungen = actKons.getLeistungen() == null || actKons.getLeistungen().isEmpty();
			log.debug("Delete the kons? " + konsTextComposite.getPlainText().length() + " noLeistungen " + noLeistungen);
			if (konsTextComposite.getPlainText().length() == 0 && (noLeistungen)) {
				Fall f = actKons.getFall();
				Konsultation[] ret = f.getBehandlungen(false);
				actKons.delete(true);
				if (ret.length == 1) {
                   /* Trying to remove the associated case got me into problems.
                    * Peter Schoenbucher argued on September, 2, 2015, that we should never
                    * delete a case, because the case holds the information which Krankenkasse is
                    * attached to this client. Therefore often the assistant opens a case before
                    * the consultation starts
                   */
				}
			}
		}
	}

	@Override
	public void visible(boolean mode){
		if (mode == true) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_kons_filter, eeli_kons, eeli_problem,
				eeli_pat, eeli_user);

			displaySelectedPatient(ElexisEventDispatcher.getSelectedPatient(), "view visible");
			heartbeat.enableListener(true);
		} else {
			heartbeat.enableListener(false);
			ElexisEventDispatcher.getInstance().removeListeners(eeli_kons_filter, eeli_kons, eeli_problem,
				eeli_pat, eeli_user);
			setPatient(null);
		}
		visibleAllKonsAreas(mode);
	};

	private static void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			Fall f = actKons.getFall();
			if (f != null) {
				Patient pat = f.getPatient();
				sb.append(actKons.getId());
				sb.append(" kons vom " + actKons.getDatum());
				sb.append(" " + pat.getId() + ": " + pat.getPersonalia());
			}
		}
		log.debug(sb.toString());
	}

	/*
	 * Aktuellen Patienten setzen
	 */
	public void setPatient(Patient newPatient){
		if (actPatient == newPatient)
			return;
		logEvent("setPatient " + (newPatient == null ? "null" : newPatient.getId()));
		actPatient = newPatient;

		// widgets may be disposed when application is closed
		if (form.isDisposed()) {
			return;
		}
		updateAllPatientAreas(actPatient);

		if (actPatient != null) {
			// Pruefe, ob Patient Probleme hat, sonst Standardproblem erstellen
			List<Problem> problems = Problem.getProblemsOfPatient(actPatient);
			if (problems.size() == 0) {
				// TODO don't yet do this
				// Problem.createStandardProblem(actPatient);
			}
		} else {
			// Kein Patient ausgewaehlt, somit auch keine Konsultation anzeigen
			updateAllKonsAreas(null, KonsActions.ACTIVATE_KONS);
		}
		if (konsListDisplay != null && actPatient != null) {
			konsListDisplay.setPatient(actPatient, showAllChargesAction.isChecked(),
				showAllConsultationsAction.isChecked());
		}
		logEvent("setPatient done");
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
}
