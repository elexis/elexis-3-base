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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.iatrix.Iatrix;
import org.iatrix.util.Helpers;
import org.iatrix.widgets.KonsListComposite.KonsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
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
public class KonsListDisplay extends Composite implements IJobChangeListener, IJournalArea {

	private final FormToolkit toolkit;
	private final ScrolledForm form;
	private final Composite formBody;

	private final KonsListComposite konsListComposite;

	private final KonsLoader dataLoader;
	private static Logger log = LoggerFactory.getLogger(org.iatrix.widgets.KonsListDisplay.class);

	// if false, only show the charges of the latest 2 consultations
	private boolean showAllCharges = false;

	// if false, only show the latest MAX_SHOWN_CONSULTATIONS
	private boolean showAllConsultations = false;

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
		dataLoader.actKons = actKons;
		dataLoader.addJobChangeListener(this);
	}

	/**
	 * reload contents
	 * @param object
	 */
	private void reload(boolean showLoading, List<KonsData> konsultationen){
		if (actKons!= null && konsultationen != null) {
			konsListComposite.setKonsultationen(konsultationen, actKons);
		} else {
			if (konsultationen == null) {
				konsListComposite.setKonsultationen(null, null);
			} else if (showLoading ) {
				konsListComposite.setKonsultationen(null, null);
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

	class KonsLoader extends Job {

		String name;
		Patient patient = null;
		private boolean showAllCharges = true;
		private boolean showAllConsultations = true;
		List<KonsListComposite.KonsData> konsDataList = new ArrayList<>();

		public Konsultation actKons;

		public KonsLoader(){
			super("KonsLoader");
			log.debug("loaderJob KonsLoader created");
		}

		public void setKons(Konsultation kons, boolean showAllCharges,
			boolean showAllConsultations){
			if (kons != null) {
				this.patient = kons.getFall().getPatient();
			} else {
				this.patient = null;
				dataLoader.cancel();
			}
			this.showAllCharges = showAllCharges;
			this.showAllConsultations = showAllConsultations;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (konsDataList) {
				log.debug("loaderJob started patient " + (patient == null ? "null" : patient.getPersonalia()));
				konsDataList.clear();

				List<Konsultation> konsList = new ArrayList<>();

				if (patient != null) {
					Fall[] faelle = patient.getFaelle();

					if (faelle.length > 0) {
						IFilter globalFilter =
							ObjectFilterRegistry.getInstance().getFilterFor(Konsultation.class);

						Query<Konsultation> query = new Query<>(Konsultation.class);
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
								if ( globalFilter == null || globalFilter.select(k)) {
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

					if (!showAllConsultations && konsList.size() > maxShownConsultations) {
						// don't load all entries

						List<Konsultation> newList = new ArrayList<>();
						for (int i = 0; i < maxShownConsultations; i++) {
							newList.add(konsList.get(i));
						}
						konsList = newList;
					}
				}

				if (monitor.isCanceled()) {
					monitor.done();
					return Status.CANCEL_STATUS;
				}

				if (CoreHub.globalCfg != null) {
				// convert Konsultation objects to KonsData objects
					int maxShownCharges = CoreHub.globalCfg.get(Iatrix.CFG_MAX_SHOWN_CHARGES,
						Iatrix.CFG_MAX_SHOWN_CHARGES_DEFAULT);
					int i = 0; // counter for maximally shown charges
					for (Konsultation k : konsList) {
						KonsListComposite.KonsData ks =
							new KonsListComposite.KonsData(k, showAllCharges || i < maxShownCharges);
						konsDataList.add(ks);
						i++;
						if (i > maxShownCharges) { break; }
					}
				}

				monitor.worked(1);
				monitor.done();

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				} else {
					return Status.OK_STATUS;
				}
			}
		}

		public List<KonsListComposite.KonsData> getKonsultationen(){
			return konsDataList;
		}
	}

	@Override
	public void visible(boolean mode){
	}

	@Override
	public void activation(boolean mode){
	}

	static int savedKonsVersion = -1;
	static Konsultation actKons = null;

	@Override
	public void setKons(Konsultation newKons, KonsActions op){
		if (!Helpers.twoKonsEqual(newKons, actKons)) {
			log.debug("setKons " + (newKons != null ? newKons.getId() + " " + newKons.getLabel() + " " + newKons.getLabel() : "null" ));
			actKons = newKons;
			if (newKons == null) {
				reload(false, null);
			} else {
				dataLoader.cancel();
				reload(true, null);
				dataLoader.setKons(newKons, showAllCharges, showAllConsultations);
				dataLoader.schedule();
			}
			konsListComposite.refeshHyperLinks(actKons);
		}
	}

	@Override
	public void aboutToRun(IJobChangeEvent event) {
	/* empty */}

	@Override
	public void awake(IJobChangeEvent event) {
	/* empty */}

	@Override
	public void done(IJobChangeEvent event) {
		final List<KonsData> copy = new ArrayList<>(dataLoader.getKonsultationen());
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				log.debug("loaderJob got done for " + copy.size()  + " kons.");
				reload(false, copy);
			}
		});
	}

	@Override
	public void running(IJobChangeEvent event) {
	/* empty */}

	@Override
	public void scheduled(IJobChangeEvent event) {
	/* empty */}

	@Override
	public void sleeping(IJobChangeEvent event) {
	/* empty */}

	public void setKonsultation(Konsultation newKons, boolean showCharges, boolean showConsultations){
		showAllCharges = showCharges;
		showAllConsultations = showConsultations;
		setKons(newKons, KonsActions.ACTIVATE_KONS);
	}
}
