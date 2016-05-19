/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz    - new version for Iatrix
 *    G. Weirich - adapted to API-Changes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.iatrix.Iatrix;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.actions.BackgroundJob;
import ch.elexis.core.ui.actions.BackgroundJob.BackgroundJobListener;
import ch.elexis.core.ui.actions.ObjectFilterRegistry;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

/**
 * Anzeige der vergangenen Konsultationen inkl. Verrechnung. Der Patient wird ueber die Methode
 * setPatient(Patient) festgelegt.
 *
 * @author Daniel Lutz
 *
 */
public class KonsListDisplay extends Composite implements BackgroundJobListener {
	private Patient patient = null;

	private final FormToolkit toolkit;
	private final ScrolledForm form;
	private final Composite formBody;

	private final KonsListComposite konsListComposite;

	private final KonsLoader dataLoader;

	// if false, only show the charges of the latest 2 consultations
	// default is true (show all charges)
	private boolean showAllCharges = true;

	// if false, only show the latest MAX_SHOWN_CONSULTATIONS
	// default is true (show all consultations)
	private boolean showAllConsultations = true;

	private final ElexisEventListenerImpl eeli_kons =
		new ElexisEventListenerImpl(Konsultation.class, ElexisEvent.EVENT_RELOAD) {
			@Override
			public void run(ElexisEvent ev){
				if (patient != null) {
					dataLoader.invalidate();
					dataLoader.schedule();
				}
			}
		};

	public KonsListDisplay(Composite parent){
		super(parent, SWT.BORDER);

		setLayout(new FillLayout());

		toolkit = new FormToolkit(getDisplay());
		form = toolkit.createScrolledForm(this);
		formBody = form.getBody();

		formBody.setLayout(new TableWrapLayout());

		konsListComposite = new KonsListComposite(formBody, toolkit);
		konsListComposite.setLayoutData(SWTHelper.getFillTableWrapData(1, true, 1, false));

		dataLoader = new KonsLoader();
		dataLoader.addListener(this);

		ElexisEventDispatcher.getInstance().addListeners(eeli_kons);
	}

	/**
	 * reload contents
	 */
	private void reload(boolean showLoading){
		if (patient != null && dataLoader.isValid()) {
			konsListComposite.setKonsultationen(dataLoader.getKonsultationen());
		} else {
			if (showLoading) {
				konsListComposite.setKonsultationen(null);
			}
		}

		refresh();
	}

	/*
	 * re-display data
	 */
	private void refresh(){
		// check for disposed widget to avoid error message at program exit
		if (form.isDisposed()) {
			dataLoader.cancel();
		} else {
			form.reflow(true);
		}
	}

	public void setPatient(Patient patient, boolean showAllCharges, boolean showAllConsultations){
		boolean patientChanged = patientChanged(patient);

		this.patient = patient;
		this.showAllCharges = showAllCharges;
		this.showAllConsultations = showAllConsultations;

		dataLoader.cancel();
		dataLoader.invalidate();

		// cause "loading" label to be displayed
		if (patientChanged) {
			reload(true);
		}

		boolean preview = (patientChanged == true);
		dataLoader.setPatient(patient, preview, showAllCharges, showAllConsultations);
		dataLoader.schedule();
	}

	private boolean patientChanged(Patient newPatient){
		if (this.patient != null || newPatient != null) {
			if (this.patient == null || newPatient == null
				|| !this.patient.getId().equals(newPatient.getId())) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void jobFinished(BackgroundJob j){
		reload(false);
		if (dataLoader.isPreview()) {
			// load remaining consultations
			setPatient(patient, showAllCharges, showAllConsultations);
		}
	}

	class KonsLoader extends BackgroundJob {
		private final int PREVIEW_COUNT = 2;

		String name;
		Patient patient = null;
		boolean preview = false;
		private boolean showAllCharges = true;
		private boolean showAllConsultations = true;
		List<KonsListComposite.KonsData> konsDataList = new ArrayList<KonsListComposite.KonsData>();

		public KonsLoader(){
			super("KonsLoader");
		}

		public void setPatient(Patient patient, boolean preview, boolean showAllCharges,
			boolean showAllConsultations){
			this.patient = patient;
			this.preview = preview;
			this.showAllCharges = showAllCharges;
			this.showAllConsultations = showAllConsultations;

			invalidate();
		}

		public boolean isPreview(){
			return preview;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor){
			synchronized (konsDataList) {
				konsDataList.clear();

				List<Konsultation> konsList = new ArrayList<Konsultation>();

				if (patient != null) {
					Fall[] faelle = patient.getFaelle();

					if (faelle.length > 0) {
						IFilter globalFilter =
							ObjectFilterRegistry.getInstance().getFilterFor(Konsultation.class);

						/*
						 * Fall[] faelle = patient.getFaelle(); for (Fall fall : faelle) {
						 * Konsultation[] kons = fall.getBehandlungen(false); for (Konsultation k :
						 * kons) { if (globalFilter == null || globalFilter.select(k)) {
						 * konsList.add(k); } }
						 *
						 * if (monitor.isCanceled()) { monitor.done(); return Status.CANCEL_STATUS;
						 * } }
						 */

						// re-implementation using Query and conditions

						Query<Konsultation> query = new Query<Konsultation>(Konsultation.class);
						query.startGroup();
						for (Fall fall : faelle) {
							query.add("FallID", "=", fall.getId());
							query.or();
						}
						query.endGroup();
						query.orderBy(true, "Datum");
						List<Konsultation> kons = query.execute();

						if (monitor.isCanceled()) {
							monitor.done();
							return Status.CANCEL_STATUS;
						}

						if (kons != null) {
							for (Konsultation k : kons) {
								if (globalFilter == null || globalFilter.select(k)) {
									konsList.add(k);
								}
							}
						}
					}
				}
				if (monitor == null) {
					return Status.CANCEL_STATUS;
				}

				monitor.worked(1);

				if (CoreHub.globalCfg != null) {
					int maxShownConsultations =
						CoreHub.globalCfg.get(Iatrix.CFG_MAX_SHOWN_CONSULTATIONS,
							Iatrix.CFG_MAX_SHOWN_CONSULTATIONS_DEFAULT);

					if (preview && konsList.size() > PREVIEW_COUNT) {
						// don't load all entries in this step

						List<Konsultation> newList = new ArrayList<Konsultation>();
						for (int i = 0; i < PREVIEW_COUNT; i++) {
							newList.add(konsList.get(i));
						}
						konsList = newList;
					} else if (!showAllConsultations && konsList.size() > maxShownConsultations) {
						// don't load all entries

						List<Konsultation> newList = new ArrayList<Konsultation>();
						for (int i = 0; i < maxShownConsultations; i++) {
							newList.add(konsList.get(i));
						}
						konsList = newList;
					}
				}

				/*
				 * Collections.sort(konsList, new Comparator<Konsultation>() { TimeTool t1=new
				 * TimeTool(); TimeTool t2=new TimeTool(); public int compare(final Konsultation o1,
				 * final Konsultation o2) { if((o1==null) || (o2==null)){ return 0; }
				 * t1.set(o1.getDatum()); t2.set(o2.getDatum()); if(t1.isBefore(t2)){ return 1; }
				 * if(t1.isAfter(t2)){ return -1; } return 0; } });
				 */

				if (monitor.isCanceled()) {
					monitor.done();
					return Status.CANCEL_STATUS;
				}

				// convert Konsultation objects to KonsData objects

				int maxShownCharges = CoreHub.globalCfg.get(Iatrix.CFG_MAX_SHOWN_CHARGES,
					Iatrix.CFG_MAX_SHOWN_CHARGES_DEFAULT);
				int i = 0; // counter for maximally shown charges
				for (Konsultation k : konsList) {
					KonsListComposite.KonsData ks =
						new KonsListComposite.KonsData(k, showAllCharges || i < maxShownCharges);
					konsDataList.add(ks);

					i++;
				}

				monitor.worked(1);
				monitor.done();

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				} else {
					result = konsDataList;
					return Status.OK_STATUS;
				}
			}
		}

		@Override
		public int getSize(){
			/*
			 * if (konsultationen != null) { synchronized (konsultationen) { return
			 * konsultationen.size(); } } else { return 0; }
			 */

			// number of work steps in execute()
			return 2;
		}

		public List<KonsListComposite.KonsData> getKonsultationen(){
			Object data = getData();
			if (data instanceof List) {
				return (List<KonsListComposite.KonsData>) data;
			} else {
				return null;
			}
		}
	}

}
