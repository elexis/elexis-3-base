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

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ISticker;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.contacts.views.PatientDetailView2;
import ch.elexis.core.ui.data.UiSticker;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.rechnung.AccountView;
import ch.elexis.core.ui.views.rechnung.BillSummary;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.data.Sticker;
import ch.rgw.tools.ExHandler;

public class JournalHeader implements IJournalArea {

	private Patient patient = null;
	private FormToolkit tk;
	private static Logger log = LoggerFactory.getLogger(JournalHeader.class);
	private Hyperlink formTitel;
	private Label remarkLabel;
	private Label kontoLabel;
	private Color kontoLabelColor; // original color of kontoLabel
	Composite cEtiketten;
	public JournalHeader(Composite formBody){
		tk = UiDesk.getToolkit();
		formBody.setLayout(new GridLayout(1, true));
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
				if (patient != null) {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(PatientDetailView2.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.error("Fehler beim Öffnen von PatientDetailView: " + ex.getMessage());
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

		cEtiketten = new Composite(patInfoArea, SWT.NONE);
		cEtiketten.setLayout(new RowLayout(SWT.HORIZONTAL));
		cEtiketten.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cEtiketten.setBackground(patInfoArea.getBackground());
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
				if (patient != null) {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(AccountView.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.error("Fehler beim Öffnen von AccountView: " + ex.getMessage());
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
				if (patient != null) {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(BillSummary.ID);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.error("Fehler beim Öffnen von AccountView: " + ex.getMessage());
					}
				}
			}
		});
		openBillsHyperlink.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		formTitel.getParent().layout();
	}

	private void setPatientTitel(){
		String text = "Kein Patient ausgewählt";

		formTitel.setEnabled(patient != null);

		if (patient != null) {
			text = patient.getLabel();
		}

		formTitel.setText(PersistentObject.checkNull(text));
		formTitel.getParent().layout();
	}

	private Composite createStickerWithTooltip(Composite parent, ISticker et){
		Image img = new UiSticker((Sticker) et).getImage();
		if (img != null) {} else {
			if (patient.getGeschlecht().equals(Person.MALE)) {
				img = Images.IMG_MANN.getImage();
			} else {
				img = Images.IMG_FRAU.getImage();
			}
		}
		GridData gd1 = null;

		Composite cImg = new Composite(parent, SWT.NONE);
		if (img != null) {
			cImg.setBackgroundImage(img);
			gd1 = new GridData(img.getBounds().width, img.getBounds().height);
		} else {
			gd1 = new GridData(10, 10);
		}
		cImg.setLayoutData(gd1);
		cImg.setToolTipText(et.getLabel());
		return cImg;
	}

	private void setRemarkAndSticker(){
		String text = "";

		if (patient != null) {
			text = patient.getBemerkung();

		}
		for (Control cc : cEtiketten.getChildren()) {
			cc.dispose();
		}
		if (patient != null) {
			List<ISticker> etis = patient.getStickers();
			GridLayout stickerLayout = new GridLayout(etis.size(), false);
			// save space
			stickerLayout.horizontalSpacing = 5;
			stickerLayout.verticalSpacing = 0;
			stickerLayout.marginWidth = 0;
			stickerLayout.marginHeight = 0;
			cEtiketten.setLayout(stickerLayout);
			if (etis != null && etis.size() > 0) {
				for (ISticker et : etis) {
					if (et != null) {
						createStickerWithTooltip(cEtiketten, et);
					}
				}
			}
		}

		if (PersistentObject.checkNull(text).length() == 0)
			remarkLabel.setText("Bemerkungen");
		else
			remarkLabel.setText(PersistentObject.checkNull(text));
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
				if (patient != null) {
					text = patient.getKontostand().getAmountAsString();
					tardyPayer = isTardyPayer(patient);
				}

				kontoLabel.setText(PersistentObject.checkNull(text));
				if (kontoLabel.getText() == null || kontoLabel.getText().length() == 0) {
					kontoLabel.setText("leeres Konto");
				}
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

	private void openRemarkEditorDialog(){
		if (patient == null) {
			return;
		}

		String initialValue = PersistentObject.checkNull(patient.getBemerkung());
		InputDialog dialog =
			new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Bemerkungen", "Bemerkungen eingeben", initialValue, null);
		if (dialog.open() == Window.OK) {
			String text = dialog.getValue();
			patient.setBemerkung(text);
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

	/*
	 * Aktuellen Patienten setzen
	 */
	@Override
	public void setPatient(Patient newPatient){
		log.debug("setPatient " + (newPatient == null ? "null" : newPatient.getPersonalia()));
		if (patient != newPatient) {
			patient = newPatient;
			setPatientTitel();
			setRemarkAndSticker();
			setKontoText();
			formTitel.getParent().layout();
		}
	}

	@Override
	/**
	 * @param newKons.
	 *            Ignored, as we are only interested in patients
	 */
	public void setKons(Konsultation newKons, KonsActions op){}

	@Override
	public void visible(boolean mode){
	}

	@Override
	public void activation(boolean mode){
	}

}
