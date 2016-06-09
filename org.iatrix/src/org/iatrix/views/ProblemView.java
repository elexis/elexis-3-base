/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *      Gerry Weirich - angepasst an neues Rezeptmodell
 *                    - angepasst an neues Eventmodell
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.views;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.Problem;
import org.iatrix.widgets.ProblemFixMediDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.codesystems.DiagnosenView;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.ExHandler;

/**
 * View for editing Problem properties.
 *
 * TODO When a Prescription is removed from the Patient's Prescriptions (e. g. in the
 * PatientDetailView), the Prescription should be removed from the Problem, too. Is there an event
 * available for this?
 *
 * @author danlutz
 */

public class ProblemView extends ViewPart implements IActivationListener, ISaveablePart2 {
	public static final String ID = "org.iatrix.views.ProblemView";

	private static Logger log = LoggerFactory.getLogger(org.iatrix.views.JournalView.class);

	private Problem actProblem;

	private FormToolkit tk;
	private ScrolledForm form;

	private ExpandableComposite dauermedikationSection;
	private ProblemFixMediDisplay dlDauerMedi;

	private Composite diagnosenComposite;
	private TableViewer diagnosenViewer;
	private TableViewer konsultationenViewer;

	/* diagnosenViewer */
	private IAction delDiagnoseAction;

	/* konsultationenViewer */
	private IAction unassignProblemAction;

	private ViewMenus menus;
	private ElexisUiEventListenerImpl eeli_problem = null;
	private ElexisUiEventListenerImpl eeli_patient = null;


	private void makeListeners() {
		eeli_problem = new ElexisUiEventListenerImpl(Episode.class,
			ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED | ElexisEvent.EVENT_UPDATE) {

			@Override
			public void runInUi(ElexisEvent ev){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					PersistentObject obj = ev.getObject();
					if (obj instanceof Episode) {
						Episode episode = (Episode) obj;
						setProblem(Problem.convertEpisodeToProblem(episode));
					} else {
						// not an episode object, silently ignore
						setProblem(null);
					}
				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					setProblem(null);
				} else if (ev.getType() == ElexisEvent.EVENT_UPDATE) {
					PersistentObject obj = ev.getObject();
					if (obj instanceof Episode) {
						Episode updatedEpisode = (Episode) obj;
						Episode actEpisode = actProblem;
						if (updatedEpisode.getId().equals(actEpisode.getId())) {
							setProblem(actProblem);
						}
					} else {
						// not an episode object, silently ignore
						setProblem(null);
					}
				}
			}
		};

		eeli_patient = new ElexisUiEventListenerImpl(Patient.class,
			ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED) {

			@Override
			public void runInUi(ElexisEvent ev){
				// make sure the current problem belongs to the newly selected patient
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					PersistentObject obj = ev.getObject();
					if (obj instanceof Patient) {
						Patient selectedPatient = (Patient) obj;
						if (actProblem != null) {
							// check whether Problem matches the currently selected patient
							if (selectedPatient != null
								&& !actProblem.getPatient().getId().equals(selectedPatient.getId())) {
								// selected patient doesn't match the current problem's patient
								setProblem(null);
							}
						} else {
							// re-select the previously selected problem
							// actually, this should never occur, but currently happens since
							// there is no responsible event manager for Episode events yet
							Problem previousProblem = IatrixEventHelper.getSelectedProblem();
							if (selectedPatient != null && previousProblem.getPatient().getId()
								.equals(selectedPatient.getId())) {
								setProblem(previousProblem);
							}
						}
					}
				}
			}
		};
	}

	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new FillLayout());

		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(main);
		form.getBody().setLayout(new GridLayout(1, true));

		SashForm mainSash = new SashForm(form.getBody(), SWT.VERTICAL);
		mainSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		dauermedikationSection = tk.createExpandableComposite(mainSash,
			ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
		dauermedikationSection.setText("Fixmedikation");

		Composite dauermedikationComposite = tk.createComposite(dauermedikationSection);
		dauermedikationSection.setClient(dauermedikationComposite);
		Composite bottomComposite = tk.createComposite(mainSash);

		mainSash.setWeights(new int[] {
			25, 75
		});

		// Dauermedikation

		dauermedikationComposite.setLayout(new GridLayout());

		// Label lDauermedikation = tk.createLabel(dauermedikationComposite, "Fixmedikation");
		// lDauermedikation.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		dlDauerMedi = new ProblemFixMediDisplay(dauermedikationComposite, getViewSite());

		bottomComposite.setLayout(new GridLayout());

		SashForm bottomSash = new SashForm(bottomComposite, SWT.HORIZONTAL);
		bottomSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		diagnosenComposite = tk.createComposite(bottomSash);
		Composite konsultationenComposite = tk.createComposite(bottomSash);

		bottomSash.setWeights(new int[] {
			25, 75
		});

		diagnosenComposite.setLayout(new GridLayout(1, true));

		Hyperlink hDiagnosen = tk.createHyperlink(diagnosenComposite, "Diagnosen", SWT.NONE);
		hDiagnosen.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		hDiagnosen.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e){
				try {
					getViewSite().getPage().showView(DiagnosenView.ID);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.error("Fehler beim Starten des Diagnosencodes " + ex.getMessage());
				}
			}
		});

		Table diagnosenTable = tk.createTable(diagnosenComposite, SWT.SINGLE);
		diagnosenTable.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		diagnosenViewer = new TableViewer(diagnosenTable);
		diagnosenViewer.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		diagnosenViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				if (actProblem != null) {
					List<IDiagnose> diagnosen = actProblem.getDiagnosen();
					return diagnosen.toArray();
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
		diagnosenViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (!(element instanceof IDiagnose)) {
					return "";
				}

				IDiagnose diagnose = (IDiagnose) element;
				return diagnose.getLabel();
			}

		});
		diagnosenViewer.setInput(this);

		konsultationenComposite.setLayout(new GridLayout(1, true));

		Label lKonsultationen = tk.createLabel(konsultationenComposite, "Konsultationen", SWT.LEFT);
		lKonsultationen.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Table konsultationenTable = tk.createTable(konsultationenComposite, SWT.SINGLE);
		konsultationenTable.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		konsultationenViewer = new TableViewer(konsultationenTable);
		konsultationenViewer.getControl()
			.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		konsultationenViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement){
				if (actProblem != null) {
					List<Konsultation> konsultationen = actProblem.getKonsultationen();
					return konsultationen.toArray();
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
		konsultationenViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				if (!(element instanceof Konsultation)) {
					return "";
				}

				Konsultation konsultation = (Konsultation) element;
				return konsultation.getLabel();
			}

		});
		konsultationenViewer.setInput(this);

		/* Implementation Drag&Drop */

		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {
			textTransfer
		};

		// diagnosenComposite
		DropTarget dtarget = new DropTarget(diagnosenComposite, DND.DROP_COPY);
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
					if (dropped instanceof IDiagnose) {
						IDiagnose diagnose = (IDiagnose) dropped;
						actProblem.addDiagnose(diagnose);

						// tell other viewers that something has changed
						IatrixEventHelper.updateProblem(actProblem);

						// update ourselves
						// TODO: implement ObjectListener
						diagnosenViewer.refresh();
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){
				/* leer */
			}
		});

		makeListeners();
		makeActions();
		menus = new ViewMenus(getViewSite());
		menus.createViewerContextMenu(diagnosenViewer, delDiagnoseAction);
		menus.createViewerContextMenu(konsultationenViewer, unassignProblemAction);

		GlobalEventDispatcher.addActivationListener(this, this);
	}

	@Override
	public void setFocus(){
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	private void makeActions(){
		// Diagnosen

		delDiagnoseAction = new Action("Diagnose entfernen") {
			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) diagnosenViewer.getSelection()).getFirstElement();
				if (sel != null && actProblem != null) {
					IDiagnose diagnose = (IDiagnose) sel;
					actProblem.removeDiagnose(diagnose);

					// TODO Diagnosen von Konsultationen entfernen
					diagnosenViewer.refresh();

					// tell other viewers that something has changed
					IatrixEventHelper.updateProblem(actProblem);
				}
			}
		};

		// Konsultationen

		unassignProblemAction = new Action("Problem entfernen") {
			{
				setToolTipText("Problem von Konsulation entfernen");
			}

			@Override
			public void run(){
				Object sel =
					((IStructuredSelection) konsultationenViewer.getSelection()).getFirstElement();
				if (sel != null && actProblem != null) {
					Konsultation konsultation = (Konsultation) sel;

					// remove problem. ask user if encounter still contains data.
					IatrixViewTool.removeProblemFromKonsultation(konsultation, actProblem);
					konsultationenViewer.refresh();

					// tell other viewers that something has changed
					IatrixEventHelper.updateProblem(actProblem);
				}
			}
		};

	}

	@Override
	public void activation(boolean mode){
		// do nothing
	}

	@Override
	public void visible(boolean mode){
		if (mode == true) {
			Problem problem = IatrixEventHelper.getSelectedProblem();
			if (problem != null) {
				// check whether Problem matches the currently selected patient
				Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
				if (selectedPatient != null
					&& problem.getPatient().getId().equals(selectedPatient.getId())) {
					setProblem(problem);
				} else {
					setProblem(null);
				}
			} else {
				setProblem(null);
			}
			ElexisEventDispatcher.getInstance().addListeners(eeli_problem, eeli_patient);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_problem, eeli_patient);
		}
	}

	/*
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

	private void setProblem(Problem problem){
		actProblem = problem;

		if (actProblem != null) {
			form.setText(
				"Problem " + problem.getLabel() + " von " + problem.getPatient().getLabel());
		} else {
			form.setText("Kein Problem ausgewählt");
		}

		diagnosenViewer.refresh();
		konsultationenViewer.refresh();

		// Fixmedikation

		// TODO work-around
		// we need to call "reload" two times to make the list expand
		// unknown why this is required
		dlDauerMedi.reload();
		dlDauerMedi.reload();
		dauermedikationSection.layout(true);

		form.reflow(true);
	}

	// FocusListener fuer Felder
	class Focusreact extends FocusAdapter {
		private final String field;

		Focusreact(String f){
			field = f;
		}

		@Override
		public void focusLost(FocusEvent e){
			if (actProblem == null) {
				return;
			}

			String oldvalue = actProblem.get(field);
			String newvalue = ((Text) e.getSource()).getText();
			if (oldvalue != null) {
				if (oldvalue.equals(newvalue)) {
					return;
				}
			}
			if (newvalue != null) {
				actProblem.set(field, newvalue);
			}
		}
	}
}
