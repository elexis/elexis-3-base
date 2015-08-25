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
package org.iatrix.widgets;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnungssteller;

public class KonsHeader implements IJournalArea {

	private Konsultation actKons = null;
	private FormToolkit tk;
	private Composite konsFallArea;
	private Hyperlink hlKonsultationDatum;
	private Hyperlink hlMandant;
	private Combo cbFall;
	private Label cbLabel;
	private static Logger log = LoggerFactory.getLogger(KonsHeader.class);
	/**
	 * Flag indicating if there are more than one mandants. This variable is initially set in
	 * createPartControl().
	 */
	private boolean hasMultipleMandants = false;

	public KonsHeader(Composite konsultationComposite){
		tk = UiDesk.getToolkit();
		konsFallArea = tk.createComposite(konsultationComposite);
		konsFallArea.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		konsFallArea.setLayout(new GridLayout(3, false));

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
					KontaktSelektor ksl = new KontaktSelektor(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Mandant.class, "Mandant auswählen",
						"Auf wen soll diese Kons verrechnet werden?", new String[] {
							Mandant.FLD_SHORT_LABEL, Mandant.FLD_NAME1, Mandant.FLD_NAME2
					});
					if (ksl.open() == Dialog.OK) {
						actKons.setMandant((Mandant) ksl.getSelection());
						setKons(actKons, false);
					}
				}

			});
		} else {
			hlMandant = null;
			// placeholder
			tk.createLabel(konsFallArea, "nur 1 Mandant ");
		}

		Composite fallArea = tk.createComposite(konsFallArea);
		// GridData gd = SWTHelper.getFillGridData(1, false, 1, false);
		// gd.horizontalAlignment = SWT.RIGHT;
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		fallArea.setLayoutData(gd);

		//
		fallArea.setLayout(new GridLayout(2, false));
		cbLabel = tk.createLabel(fallArea, "Fall:");
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
					MessageDialog msd = new MessageDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Fallzuordnung ändern", Images.IMG_LOGO.getImage(),
						"Möchten Sie diese Behandlung vom Fall:\n'" + actFall.getLabel()
							+ "' zum Fall:\n'" + nFall.getLabel() + "' transferieren?",
						MessageDialog.QUESTION, new String[] {
							"Ja", "Nein"
					}, 0);
					if (msd.open() == 0) {
						// TODO check compatibility of assigned problems
						actKons.setFall(nFall);
						setKons(actKons, false);
					}
				}
			}
		});
		tk.adapt(cbFall);
		cbFall.setEnabled(false);

	}

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

	@Override
	public void setKons(Konsultation k, boolean putCaretToEnd){
		log.debug("setKons " + k);
		actKons = k;
		if (actKons != null) {
			cbFall.setEnabled(true);
			StringBuilder sb = new StringBuilder();
			sb.append(actKons.getDatum());
			hlKonsultationDatum.setText(sb.toString());
			hlKonsultationDatum.setEnabled(true);

			if (hasMultipleMandants) {
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

			log.debug("Konsultation: " + actKons.getId());
		} else {
			cbFall.setEnabled(false);

			/*
			 * lKonsultation.setText("Keine Konsultation ausgewählt");
			 */
			hlKonsultationDatum.setText("Keine Konsultation ausgewählt");
			hlKonsultationDatum.setEnabled(false);
			if (hlMandant != null) {
				hlMandant.setText("");
				hlMandant.setEnabled(false);
			}

			log.debug("Konsultation: null");
			reloadFaelle(null);
		}
		konsFallArea.layout();
	}

	private void reloadFaelle(Konsultation konsultation){
		cbFall.removeAll();

		if (konsultation != null) {
			Fall fall = konsultation.getFall();
			if (fall == null) {
				return;
			}
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
			String label = fall.getLabel();
			cbFall.setBackground(cbFall.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			Color color = null;
			if (label.contains("UVG")) {
				color = UiDesk.getColor(UiDesk.COL_SKYBLUE);
			} else if (label.contains("KVG")) {
				color = cbFall.getDisplay().getSystemColor(SWT.COLOR_WHITE);
			} else {
				color = cbFall.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			}
			cbLabel.setBackground(color);
			cbFall.getParent().setBackground(color);
		}
	}

	@Override
	public void setPatient(Patient newPatient){
		log.debug("setPatient " + newPatient);
		konsFallArea.layout();
	}

	@Override
	public void visible(boolean mode){
		// TODO Auto-generated method stub

	}

	@Override
	public void activation(boolean mode){
		// TODO Auto-generated method stub

	}
}
