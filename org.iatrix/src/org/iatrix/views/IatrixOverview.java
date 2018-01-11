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

import static ch.elexis.core.data.events.ElexisEvent.EVENT_DESELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_RELOAD;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_SELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_UPDATE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
import org.iatrix.util.Constants;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Helpers;
import org.iatrix.widgets.IJournalArea;
import org.iatrix.widgets.IJournalArea.KonsActions;
import org.iatrix.widgets.JournalHeader;
import org.iatrix.widgets.ProblemArea;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
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
import de.kupzog.ktable.KTable;

/**
 * KG-Ansicht nach Iatrix-Vorstellungen
 *
 * Oben wird die Problemliste dargestellt
 *
 * @author Niklaus Giger <niklaus.giger@member.fsf.org> Nur oberstes Drittel der Iatrix JournalView
 */

public class IatrixOverview extends ViewPart implements IActivationListener, ISaveablePart2 {
	
	/**
	 * ID of the Journal View
	 */
	public static final String ID = Constants.ID;
	
	private static Logger log = LoggerFactory.getLogger(IatrixOverview.class);
	private static Konsultation actKons = null;
	private FormToolkit tk;
	private Form form;
	
	// container for hKonsultationDatum, hlMandant, cbFall
	
	// Parts (from top to bottom that make up our display
	private JournalHeader journalHeader = null; // Patient name, sex, birthday, remarks, sticker, account, balance, account overview
	private KTable problemsKTable = null; // On top
	private ProblemArea problemsArea = null; // KTable with Date, nr, diagnosis, therapy, code, activ/inactiv
	private ViewMenus menus;
	
	/* Actions */
	private IAction exportToClipboardAction;
	private IAction sendEmailAction;
	private IAction addKonsultationAction;
	
	private static List<IJournalArea> allAreas;
	
	private Heartbeat heartbeat;
	
	@Override
	public void createPartControl(Composite parent){
		Bundle bundle = Platform.getBundle("org.iatrix");
		log.info("VERSION: " + bundle.getVersion().toString());
		parent.setLayout(new FillLayout());
		heartbeat = Heartbeat.getInstance();
		tk = UiDesk.getToolkit();
		form = tk.createForm(parent);
		Composite formBody = form.getBody();
		
		formBody.setLayout(new GridLayout(1, true));
		journalHeader = new JournalHeader(formBody);
		
		SashForm mainSash = new SashForm(form.getBody(), SWT.VERTICAL);
		mainSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite topArea = tk.createComposite(mainSash, SWT.NONE);
		topArea.setLayout(new FillLayout(SWT.VERTICAL));
		topArea.setBackground(topArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		problemsArea = new ProblemArea(topArea, IatrixOverview.this.getPartName(), getViewSite());
		problemsKTable = problemsArea.getProblemKTable();
		allAreas = new ArrayList<>();
		allAreas.add(journalHeader);
		allAreas.add(problemsArea);
		makeActions();
		menus = new ViewMenus(getViewSite());
		if (CoreHub.acl.request(AccessControlDefaults.AC_PURGE)) {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction,
				problemsArea.addProblemAction, GlobalActions.delKonsAction,
				problemsArea.delProblemAction, exportToClipboardAction,
				problemsArea.addFixmedikationAction, sendEmailAction);
		} else {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction,
				problemsArea.addProblemAction, GlobalActions.delKonsAction,
				problemsArea.delProblemAction, exportToClipboardAction,
				problemsArea.addFixmedikationAction, sendEmailAction);
		}
		
		menus.createToolbar(sendEmailAction, exportToClipboardAction, addKonsultationAction,
			problemsArea.getAddProblemAction());
		GlobalEventDispatcher.addActivationListener(this, this);
		activateContext();
	}
	
	/**
	 * First ste the global variable actKons Then updates all dependent widgets, like header,
	 * konsText konsList
	 * 
	 * @param newKons
	 * @param op
	 */
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
		logEvent(newKons, "updateAllKonsAreas: newKons op is " + op);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setKons(newKons.getFall().getPatient(), newKons, op);
			}
		}
	}
	
	private void activateAllKonsAreas(boolean mode){
		logEvent(null, "activateAllKonsAreas: " + mode);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.activation(mode, actKons == null ? null : actKons.getFall().getPatient(), actKons);
			}
		}
	}
	
	private void visibleAllKonsAreas(boolean mode){
		logEvent(null, "visibleAllKonsAreas: " + mode);
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
					logEvent(null, "eeli_problem EVENT_UPDATE");
					problemsArea.reloadAndRefresh();
					break;
				case EVENT_DESELECTED:
					logEvent(null, "eeli_problem EVENT_DESELECTED");
					problemsKTable.clearSelection();
					break;
				}
				
			}
		};
	
	private final ElexisUiEventListenerImpl eeli_kons = new ElexisUiEventListenerImpl(
		Konsultation.class) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation newKons = (Konsultation) ev.getObject();
			String msg = "";
			switch (ev.getType()) {
			case EVENT_SELECTED:
				msg = "EVENT_SELECTED";
				break;
			case EVENT_UPDATE:
				msg = "EVENT_UPDATE";
				break;
			case EVENT_DESELECTED:
				msg = "EVENT_DESELECTED";
				break;
			case EVENT_RELOAD:
				msg = "EVENT_RELOAD";
				break;
			}
			logEvent(newKons, String.format("eeli_kons type %d msg %s", ev.getType(), msg));
			// when we get an update or select event the parameter is always not null
			if (actKons == null) {
				logEvent(newKons, "eeli_kons " + msg + " SAVE_KONS");
				// updateAllKonsAreas(actKons, KonsActions.SAVE_KONS);
				Patient newPatient = newKons.getFall().getPatient();
				if (actKons != null
					&& !newPatient.getId().equals(actKons.getFall().getPatient().getId())) {
					displaySelectedPatient(newPatient, "eeli_kons newPatient");
				}
				logEvent(newKons, "eeli_kons " + msg + " ACTIVATE_KONS");
				updateAllKonsAreas(newKons, KonsActions.ACTIVATE_KONS);
			} else {
				if (ev.getType() == EVENT_RELOAD) {
					updateAllKonsAreas(newKons, KonsActions.EVENT_RELOAD);
				}
				if (ev.getType() == EVENT_UPDATE) {
					updateAllKonsAreas(newKons, KonsActions.EVENT_UPDATE);
				}
				if (ev.getType() == EVENT_SELECTED) {
					updateAllKonsAreas(newKons, KonsActions.EVENT_SELECTED);
				}
			}
			actKons = newKons;
		}
		
	};
	
	/**
	 * Helper to update every thing whether we got notified by opening the view or the selected
	 * patient changed
	 *
	 * @param selectedPatient
	 *            patient to be displayed
	 * @param why
	 *            Where do we come from (Only used for the logging)
	 */
	private void displaySelectedPatient(Patient selectedPatient, String why){
		if (selectedPatient == null) {
			logEvent(null, why + " displaySelectedPatient " + "no patient");
			updateAllKonsAreas(null, KonsActions.ACTIVATE_KONS);
			return;
			
		} else {
			logEvent(null, why + " displaySelectedPatient " + selectedPatient.getId()
				+ selectedPatient.getPersonalia());
		}
		
		/* TODO: Neue Kons erstellen wurde manuell deaktiviert
		// Find the most recent open konsultation for the given fall
		// If nothing found or not of today, create a new konsultation
		Konsultation konsultation = null;
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
		 */
	}
	
	private final ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			String msg = "";
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
			Patient newPat = (Patient) ev.getObject();
			log.debug(String.format("eeli_pat %d %s %s actKons null: %s", ev.getType(), msg,  newPat.toString(), actKons == null));
			if (actKons != null && actKons.getFall().getPatient().getId().equals(newPat.getId())) {
				log.debug(String.format("eeli_pat %d %s %s nothing todo", ev.getType(), msg,  newPat.toString()));
			} else {
				journalHeader.setPatient(newPat);
				actKons = newPat.getLetzteKons(false);
				problemsArea.setKons(newPat, actKons, KonsActions.ACTIVATE_KONS);
				updateAllKonsAreas(actKons, KonsActions.ACTIVATE_KONS);
				displaySelectedPatient(newPat, "eeli_pat " + ev.getType());
				log.debug(String.format("eeli_pat %d %s %s %s changed", ev.getType(), msg, actKons, newPat.getPersonalia()));
			}
		}
	};
	
	private final ElexisUiEventListenerImpl eeli_user =
		new ElexisUiEventListenerImpl(Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
			@Override
			public void runInUi(ElexisEvent ev){
				logEvent(null, "runInUi eeli_user adaptMenus");
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
			getSite().getService(IContextService.class);
		contextService.activateContext(Constants.VIEW_CONTEXT_ID);
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		ElexisEventDispatcher.getInstance().removeListeners(eeli_problem, eeli_kons, eeli_pat,
			eeli_user);
		super.dispose();
	}
	
	@Override
	public void setFocus(){}
	
	/**
	 * Adapt the menus (create/delete kons) according to the ACL settings
	 */
	public void adaptMenus(){
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
				if (actKons != null) {
					Helpers.exportToClipboard(actKons.getFall().getPatient(), null); // TODO: selected problem
				}
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
				if (actKons != null) {
					Email.openMailApplication("", // No default to address
						null, Helpers.exportToClipboard(actKons.getFall().getPatient(), null), // TODO: selected problem
						null);
				}
				
			}
		};
		sendEmailAction.setActionDefinitionId(Constants.EXPORT_SEND_EMAIL_COMMAND);
		GlobalActions.registerActionHandler(this, sendEmailAction);
	}
	
	@Override
	public void activation(boolean mode){
		Konsultation selected_kons =
			(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
		if (selected_kons != null && actKons != null
			&& !selected_kons.getId().equals(actKons.getId())) {
			// this should never happen
			logEvent(null, "activation " + mode + " sel: " + selected_kons.getLabel() + " act: "
				+ actKons.getId());
			return;
		}
		activateAllKonsAreas(mode);
	}
	
	@Override
	public void visible(boolean mode){
		if (mode == true) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_kons, eeli_problem, eeli_pat,
				eeli_user);
			Konsultation newKons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (newKons != null) {
				String msg = newKons.getId() + " " + newKons.getLabel() + " "
					+ newKons.getFall().getPatient().getPersonalia();
				logEvent(newKons, "visible true " + msg);
				updateAllKonsAreas(newKons, KonsActions.ACTIVATE_KONS);
			} else {
				logEvent(newKons, "visible true newKons is null");
				displaySelectedPatient(ElexisEventDispatcher.getSelectedPatient(), "view visible");
			}
			visibleAllKonsAreas(mode);
			heartbeat.enableListener(true);
		} else {
			heartbeat.enableListener(false);
			ElexisEventDispatcher.getInstance().removeListeners(eeli_kons, eeli_problem, eeli_pat,
				eeli_user);
		}
	};
	
	private static void logEvent(Konsultation kons, String msg){
		StringBuilder sb = new StringBuilder(msg);
		if (kons != null) {
			Fall f = kons.getFall();
			if (f != null) {
				Patient pat = f.getPatient();
				sb.append(" kons: " + kons.getId());
				sb.append(" vom " + kons.getDatum());
				sb.append(" " + pat.getId() + ": " + pat.getPersonalia());
			}
		}
		log.debug(sb.toString());
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
