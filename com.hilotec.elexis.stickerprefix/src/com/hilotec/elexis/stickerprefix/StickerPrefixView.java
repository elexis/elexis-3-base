package com.hilotec.elexis.stickerprefix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Sticker;

public class StickerPrefixView extends ViewPart {
	private List<Patient> patients;
	private List<Patient> selectedPatients;
	private List<Sticker> stickers;
	private List<Sticker> selectedStickers;
	
	private Table patientList;
	private Table stickerList;
	
	private Button addPrefix;
	private Button removePrefix;
	
	private final String PREFIX = "zzzz_";

	@Override
	public void createPartControl(Composite parent) {
		initializeLayout(parent);
		initializeLists();
		initializeListeners();
		
		refresh();
	}
	
	public void refresh() {
		patientList.removeAll();
		for (Patient pat : selectedPatients) {
			addPatientToList(pat);
		}
	}
	
	private void initializeLayout(Composite par) {
		patients = new ArrayList<Patient>();
		selectedPatients = new ArrayList<Patient>();
		stickers = new ArrayList<Sticker>();
		selectedStickers = new ArrayList<Sticker>();
		
		Composite parent = new Composite(par, 0);
		GridLayout gl = new GridLayout(2,true);
		parent.setLayout(gl);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		stickerList = new Table(parent, SWT.MULTI);
		stickerList.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		patientList = new Table(parent, SWT.HIDE_SELECTION);
		patientList.setLayoutData(gd);
		
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		addPrefix = new Button(parent, 0);
		addPrefix.setText("Präfix hinzufügen");
		addPrefix.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		removePrefix = new Button(parent, 0);
		removePrefix.setText("Präfix löschen");
		removePrefix.setLayoutData(gd);
	}
	
	/*
	 * Only helper class. Use class variable!
	 */
	private ArrayList<Sticker> getSelectedStickersHelper() {
		ArrayList<Sticker> selected = new ArrayList<Sticker>();
		for (TableItem item : stickerList.getSelection()) {
			if (item.getData() != null) {
				selected.add((Sticker)item.getData());
			}
		}
		return selected;
	}
	
	private void initializeListeners() {
		addPrefix.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (selectedPatients.size() == 0) {
					SWTHelper.alert("Kein Patient", "Es gibt keine Patienten mit den ausgewählten Stickern.");
				} else if (SWTHelper.askYesNo("Präfix hinzufügen", "Wollen sie die Präfixe wirklich hinzufügen?")) {
					for (Patient patient : selectedPatients) {
						if (!patient.getName().startsWith(PREFIX)) {
							patient.set(Patient.FLD_NAME, PREFIX+patient.getName());
						}
					}
					refresh();
				}
			}
		});
		
		removePrefix.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (selectedPatients.size() == 0) {
					SWTHelper.alert("Kein Patient", "Es gibt keine Patienten mit den ausgewählten Stickern.");
				} else if (SWTHelper.askYesNo("Präfix löschen", "Wollen sie die Präfixe wirklich löschen?")) {
					for (Patient patient : selectedPatients) {
						if (patient.getName().startsWith(PREFIX)) {
							patient.set(Patient.FLD_NAME, patient.getName().substring(PREFIX.length()));
						}
					}
					refresh();
				}
			}
		});
		
		stickerList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				selectedPatients.clear();
				selectedStickers = getSelectedStickersHelper();
				for (Patient pat : patients) {
					boolean pass = true;
					if (selectedStickers.size() >= 0) {
						for (Sticker sticker : selectedStickers) {
							if (!pat.getStickers().contains(sticker)) {
								pass = false;
							}
						}
					}
					
					if (!pass) {
						continue;
					}
					selectedPatients.add(pat);
				}
				refresh();
			}
		});
	}
	
	private void addPatientToList(Patient patient) {
		TableItem item = new TableItem(patientList, 0);
		item.setData(patient);
		item.setText(0, patient.getName()+" "+patient.getVorname()+" "+patient.getGeburtsdatum().toString());
	}
	
	/*
	 * Queries the database for patients and stickers
	 */
	private void initializeLists() {
		Query<Patient> pq = new Query<Patient>(Patient.class);
		patients = pq.execute();
		
		Query<Sticker> sq = new Query<Sticker>(Sticker.class);
		stickers = sq.execute();
		
		for (Patient pat : patients) {
			addPatientToList(pat);
		}
		
		TableItem item = new TableItem(stickerList, 0);
		item.setData(null);
		item.setText("Alle Sticker");
		for (Sticker sticker : stickers) {
			item = new TableItem(stickerList, 0);
			item.setData(sticker);
			item.setText(sticker.getLabel());
		}
	}

	@Override
	public void setFocus() {
		
	}
}
