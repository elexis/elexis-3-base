/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   G. Weirich - initial implementation
 *     D. Lutz    - adapted from Patient to Problem
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 *
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.Problem;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.RestrictedAction;
import ch.elexis.core.ui.dialogs.MediDetailDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.ListDisplay;
import ch.elexis.core.ui.util.PersistentObjectDragSource;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.RezeptBlatt;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Rezept;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Display and let the user modify the medication of the currently selected problem. This is a
 * pop-in-Replacement for DauerMediDisplay. To calculate the daily cost wie accept the forms 1-1-1-1
 * and 1x1, 2x3 and so on
 *
 * This class is actually a specialized version fo FixMediDisplay. It acts on Problems instead of on
 * Patients.
 *
 * @author gerry
 *
 */
public class ProblemFixMediDisplay extends ListDisplay<Prescription> {
	private static final String TTCOST = Messages.getString("FixMediDisplay.DailyCost"); //$NON-NLS-1$
	private final LDListener dlisten;
	private IAction stopMedicationAction, changeMedicationAction, removeMedicationAction;
	ProblemFixMediDisplay self;
	Label lCost;
	PersistentObjectDropTarget target;
	static final String REZEPT = Messages.getString("FixMediDisplay.Prescription"); //$NON-NLS-1$
	static final String LISTE = Messages.getString("FixMediDisplay.UsageList"); //$NON-NLS-1$
	static final String HINZU = Messages.getString("FixMediDisplay.AddItem"); //$NON-NLS-1$
	static final String KOPIEREN = Messages.getString("FixMediDisplay.Copy"); //$NON-NLS-1$

	// DBUG
	public org.eclipse.swt.widgets.List getList(){
		return list;
	}

	public ProblemFixMediDisplay(Composite parent, IViewSite s){
		super(parent, SWT.NONE, null);
		lCost = new Label(this, SWT.NONE);
		lCost.setText(TTCOST);
		lCost.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		dlisten = new DauerMediListener(s);
		self = this;
		addHyperlinks(HINZU, LISTE, REZEPT);
		makeActions();
		ViewMenus menu = new ViewMenus(s);
		menu.createControlContextMenu(list, stopMedicationAction, changeMedicationAction, null,
			removeMedicationAction);
		setDLDListener(dlisten);
		target =
			new PersistentObjectDropTarget(Messages.getString("FixMediDisplay.FixMedikation"), this, //$NON-NLS-1$
				new PersistentObjectDropTarget.IReceiver() {

					@Override
					public boolean accept(PersistentObject o){
						if (o instanceof Prescription) {
							return true;
						}
						if (o instanceof Artikel) {
							return true;
						}
						return false;
					}

					@Override
					public void dropped(PersistentObject o, DropTargetEvent e){
						Problem problem = IatrixEventHelper.getSelectedProblem();
						if (problem != null) {
							if (o instanceof Artikel) {
								Artikel artikel = (Artikel) o;

								Prescription prescription = new Prescription(artikel,
									problem.getPatient(), StringTool.leer, StringTool.leer);
								prescription.set(Prescription.FLD_DATE_FROM,
									new TimeTool().toString(TimeTool.DATE_GER));
								problem.addPrescription(prescription);

								// Let the user set the Prescription properties

								MediDetailDialog dlg =
									new MediDetailDialog(getShell(), prescription);
								dlg.open();

								// tell other viewers that something has changed
								IatrixEventHelper.updateProblem(problem);

								reload();
							} else if (o instanceof Prescription) {
								Prescription pre = (Prescription) o;

								// find existing prescription
								List<Prescription> existing = problem.getPrescriptions();
								for (Prescription prescription : existing) {
									if (prescription.equals(pre)) {
										// already exists
										return;
									}
								}

								Prescription now = new Prescription(pre.getArtikel(),
									problem.getPatient(), pre.getDosis(), pre.getBemerkung());
								now.set(Prescription.FLD_DATE_FROM,
									new TimeTool().toString(TimeTool.DATE_GER));
								problem.addPrescription(now);

								// tell other viewers that something has changed
								IatrixEventHelper.updateProblem(problem);

								// self.add(now);
								reload();
							}
						} else {
							SWTHelper.alert(
								Messages
									.getString("ProblemFixMediDisplay.AlertNoProblemSelectedTitle"),
								Messages
									.getString("ProblemFixMediDisplay.AlertNoProblemSelectedText"));
						}
					}
				});
		new PersistentObjectDragSource(list, new PersistentObjectDragSource.ISelectionRenderer() {

			@Override
			public List<PersistentObject> getSelection(){
				Prescription pr = ProblemFixMediDisplay.this.getSelection();
				ArrayList<PersistentObject> ret = new ArrayList<PersistentObject>(1);
				if (pr != null) {
					ret.add(pr);
				}
				return ret;
			}
		});

	}

	public void reload(){
		clear();
		Problem problem = IatrixEventHelper.getSelectedProblem();
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (problem != null && patient != null) {
			if (!problem.getPatient().getId().equals(patient.getId())) {
				// TODO work-around:
				// currently selected patient doesn't match the problem,
				// i. e. the problem should actually have been deselected, but has not been.
				problem = null;
			}
		}

		double cost = 0.0;
		boolean canCalculate = true;
		if (problem != null) {
			List<Prescription> pre = problem.getPrescriptions();
			for (Prescription pr : pre) {
				float num = 0;
				try {
					String dosis = pr.getDosis();
					if (dosis != null) {
						if (dosis.matches("[0-9]+[xX][0-9]+(/[0-9]+)?")) { //$NON-NLS-1$
							String[] dose = dosis.split("[xX]"); //$NON-NLS-1$
							int count = Integer.parseInt(dose[0]);
							num = getNum(dose[1]) * count;
						} else if (dosis.indexOf('-') != -1) {
							String[] dos = dosis.split("-"); //$NON-NLS-1$
							if (dos.length > 2) {
								for (String d : dos) {
									num += getNum(d);
								}
							} else {
								num = getNum(dos[1]);
							}
						} else {
							canCalculate = false;
						}
					} else {
						canCalculate = false;
					}
					Artikel art = pr.getArtikel();
					if (art != null) {
						int ve = art.guessVE();
						if (ve != 0) {
							Money price = pr.getArtikel().getVKPreis();
							cost += num * price.getAmount() / ve;
						} else {
							canCalculate = false;
						}
					} else {
						canCalculate = false;
					}
				} catch (Exception ex) {
					ExHandler.handle(ex);
					canCalculate = false;
				}
				add(pr);
			}
			double rounded = Math.round(100.0 * cost) / 100.0;
			if (canCalculate) {
				lCost.setText(TTCOST + Double.toString(rounded));
			} else {
				if (rounded == 0.0) {
					lCost.setText(TTCOST + "?"); //$NON-NLS-1$
				} else {
					lCost.setText(TTCOST + ">" + Double.toString(rounded)); //$NON-NLS-1$
				}
			}
		}
	}

	private float getNum(String n){
		if (n.indexOf('/') != -1) {
			String[] bruch = n.split(StringConstants.SLASH);
			float zaehler = Float.parseFloat(bruch[0]);
			float nenner = Float.parseFloat(bruch[1]);
			return zaehler / nenner;
		} else {
			return Float.parseFloat(n);
		}
	}

	class DauerMediListener implements LDListener {
		IViewSite site;

		DauerMediListener(IViewSite s){
			site = s;
		}

		@Override
		public void hyperlinkActivated(String l){
			try {
				if (l.equals(HINZU)) {
					site.getPage().showView(LeistungenView.ID);
					CodeSelectorHandler.getInstance().setCodeSelectorTarget(target);
				} else if (l.equals(LISTE)) {

					RezeptBlatt rpb = (RezeptBlatt) site.getPage().showView(RezeptBlatt.ID);
					rpb.createEinnahmeliste(ElexisEventDispatcher.getSelectedPatient(),
						getAll().toArray(new Prescription[0]));
				} else if (l.equals(REZEPT)) {
					Rezept rp = new Rezept(ElexisEventDispatcher.getSelectedPatient());
					for (Prescription p : getAll().toArray(new Prescription[0])) {
						/*
						 * rp.addLine(new RpZeile("1",p.getArtikel().getLabel(),"",
						 * p.getDosis(),p.getBemerkung()));
						 */
						rp.addPrescription(new Prescription(p));
					}
					RezeptBlatt rpb = (RezeptBlatt) site.getPage().showView(RezeptBlatt.ID);
					rpb.createRezept(rp);
				} else if (l.equals(KOPIEREN)) {
					toClipBoard(true);
				}
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}

		}

		@Override
		public String getLabel(Object o){
			if (o instanceof Prescription) {
				return ((Prescription) o).getLabel();
			}
			return o.toString();
		}
	}

	private void makeActions(){

		changeMedicationAction = new RestrictedAction(AccessControlDefaults.MEDICATION_MODIFY,
			Messages.getString("FixMediDisplay.Change")) { //$NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.getString("FixMediDisplay.Modify")); //$NON-NLS-1$
			}

			@Override
			public void doRun(){
				Prescription pr = getSelection();
				if (pr != null) {
					new MediDetailDialog(getShell(), pr).open();
					reload();
					redraw();
					// tell other viewers that something has changed
					IatrixEventHelper.updateProblem(IatrixEventHelper.getSelectedProblem());
				}
			}
		};

		stopMedicationAction = new RestrictedAction(AccessControlDefaults.MEDICATION_MODIFY,
			Messages.getString("FixMediDisplay.Stop")) { //$NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_REMOVEITEM.getImageDescriptor());
				setToolTipText(Messages.getString("FixMediDisplay.StopThisMedicament")); //$NON-NLS-1$
			}

			@Override
			public void doRun(){
				Prescription pr = getSelection();
				if (pr != null) {
					remove(pr);
					pr.delete(); // this does not delete but stop the Medication. Sorry for
					// that
					reload();
					// tell other viewers that something has changed
					IatrixEventHelper.updateProblem(IatrixEventHelper.getSelectedProblem());
				}
			}
		};

		removeMedicationAction = new RestrictedAction(AccessControlDefaults.DELETE_MEDICATION,
			Messages.getString("FixMediDisplay.Delete")) { //$NON-NLS-1$
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.getString("FixMediDisplay.DeleteUnrecoverable")); //$NON-NLS-1$
			}

			@Override
			public void doRun(){
				Prescription pr = getSelection();
				if (pr != null) {
					// remove prescription from problem
					Problem problem = IatrixEventHelper.getSelectedProblem();
					if (problem != null) {
						problem.removePrescription(pr);
					}

					remove(pr);
					pr.remove(); // this does, in fact, remove the medication from the
					// database

					// tell other viewers that something has changed
					IatrixEventHelper.updateProblem(problem);

					reload();
				}
			}
		};

	}

}
