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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.iatrix.Iatrix;
import org.iatrix.widgets.KonsListDisplay;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Patient;

/**
 * View for showing Konsultationen
 *
 * @author danlutz
 */
public class KonsListView extends ViewPart implements IActivationListener, ISaveablePart2 {
	public static final String ID = "org.iatrix.views.KonsListView";

	private static final String VIEW_CONTEXT_ID = "org.iatrix.view.konslist.context"; //$NON-NLS-1$

	KonsListDisplay konsListDisplay;

	private Action showAllChargesAction;
	private Action showAllConsultationsAction;

	private final ElexisUiEventListenerImpl eeli_pat = new ElexisUiEventListenerImpl(Patient.class) {
		@Override
		public void runInUi(ElexisEvent ev){
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				showAllChargesAction.setChecked(false);
				showAllConsultationsAction.setChecked(false);
				Patient patient = (Patient) ev.getObject();
				konsListDisplay.setPatient(patient, showAllChargesAction.isChecked(),
					showAllConsultationsAction.isChecked());

			} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
				konsListDisplay.setPatient(null, showAllChargesAction.isChecked(),
					showAllConsultationsAction.isChecked());
			}
		}
	};

	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		konsListDisplay = new KonsListDisplay(parent);

		makeActions();

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

	private void makeActions(){
		showAllChargesAction = new Action("Alle Leistungen anzeigen", Action.AS_CHECK_BOX) {
			{
				setToolTipText("Leistungen aller Konsultationen anzeigen, nicht nur der ersten paar.");
			}

			@Override
			public void run(){
				boolean showAllCharges = this.isChecked();
				konsListDisplay.setPatient(ElexisEventDispatcher.getSelectedPatient(),
					showAllChargesAction.isChecked(), showAllConsultationsAction.isChecked());
			}
		};
		showAllChargesAction.setActionDefinitionId(Iatrix.SHOW_ALL_CHARGES_COMMAND);
		GlobalActions.registerActionHandler(this, showAllChargesAction);

		showAllConsultationsAction =
			new Action("Alle Konsultationen anzeigen", Action.AS_CHECK_BOX) {
				{
					setToolTipText("Alle Konsultationen anzeigen.");
				}

				@Override
				public void run(){
					konsListDisplay.setPatient(ElexisEventDispatcher.getSelectedPatient(),
						showAllChargesAction.isChecked(), showAllConsultationsAction.isChecked());
				}
			};
		showAllConsultationsAction.setActionDefinitionId(Iatrix.SHOW_ALL_CONSULTATIONS_COMMAND);
		GlobalActions.registerActionHandler(this, showAllConsultationsAction);
	}

	@Override
	public void setFocus(){
	// TODO Auto-generated method stub

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

	@Override
	public void activation(boolean mode){
	// do nothing
	}

	@Override
	public void visible(boolean mode){
		if (mode == true) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat);
			eeli_pat.catchElexisEvent(ElexisEvent.createPatientEvent());
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_pat);
		}
	}

	/* ******
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
