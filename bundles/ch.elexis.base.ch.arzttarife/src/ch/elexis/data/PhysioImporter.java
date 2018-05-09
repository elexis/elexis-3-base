/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import au.com.bytecode.opencsv.CSVReader;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class PhysioImporter extends ImporterPage {
	
	private TimeTool validFrom = new TimeTool();
	
	private TimeTool endOfEpoch = new TimeTool(TimeTool.END_OF_UNIX_EPOCH);
	
	public PhysioImporter(){
		// set default to start of year
		validFrom.clear();
		validFrom.set(TimeTool.getInstance().get(Calendar.YEAR), 0, 1);
	}
	
	@Override
	public Composite createPage(Composite parent){
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite validDateComposite = new Composite(fis, SWT.NONE);
		validDateComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		validDateComposite.setLayout(new FormLayout());
		
		Label lbl = new Label(validDateComposite, SWT.NONE);
		lbl.setText("Tarif ist gültig ab:");
		final DateTime validDate =
			new DateTime(validDateComposite, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(20, 5);
		validDate.setLayoutData(fd);
		
		validDate.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e){
				setValidFromDate();
			}
			
			public void widgetDefaultSelected(SelectionEvent e){
				setValidFromDate();
			}
			
			private void setValidFromDate(){
				validFrom.set(validDate.getYear(), validDate.getMonth(), validDate.getDay());
				// System.out.println("VALID FROM: " + validFrom.toString(TimeTool.DATE_COMPACT));
			}
		});
		validDate.setDate(validFrom.get(TimeTool.YEAR), validFrom.get(TimeTool.MONTH),
			validFrom.get(TimeTool.DAY_OF_MONTH));
		
		return fis;
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		CSVReader reader = new CSVReader(new FileReader(results[0]), ';');
		monitor.beginTask("Importiere Physio", 100);
		String[] line = reader.readNext();
		while ((line = reader.readNext()) != null) {
			if (line.length < 3) {
				continue;
			}
			monitor.subTask(line[1]);
			updateOrCreateFromLine(line);
		}
		closeAllOlder();
		
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private void closeAllOlder(){
		// get all entries
		TimeTool defaultValidFrom = new TimeTool();
		defaultValidFrom.set(1970, 0, 1);
		Query<PhysioLeistung> qEntries = new Query<PhysioLeistung>(PhysioLeistung.class);
		List<PhysioLeistung> entries = qEntries.execute();
		
		for (PhysioLeistung physio : entries) {
			String sVon = physio.get(PhysioLeistung.FLD_VON);
			TimeTool tVon = new TimeTool(sVon);
			String sBis = physio.get(PhysioLeistung.FLD_BIS);
			if ((sVon == null || sVon.trim().length() == 0)) {
				// old entry with no valid from
				physio.set(PhysioLeistung.FLD_VON,
					defaultValidFrom.toString(TimeTool.DATE_COMPACT));
				physio.set(PhysioLeistung.FLD_BIS,
					validFrom.toString(TimeTool.DATE_COMPACT));
			} else if (!validFrom.equals(tVon)) {
				// old entry not closed yet
				if ((sBis == null || sBis.trim().length() == 0)) {
					physio.set(PhysioLeistung.FLD_BIS, validFrom.toString(TimeTool.DATE_COMPACT));
				} else {
					TimeTool tBis = new TimeTool(sBis);
					if (tBis.isEqual(endOfEpoch)) {
						physio.set(PhysioLeistung.FLD_BIS,
							validFrom.toString(TimeTool.DATE_COMPACT));
					}
				}
			}
		}
	}
	
	private void updateOrCreateFromLine(String[] line){
		// get all entries with matching code
		Query<PhysioLeistung> qEntries = new Query<PhysioLeistung>(PhysioLeistung.class);
		qEntries.add(PhysioLeistung.FLD_ZIFFER, Query.EQUALS, line[0]);
		
		List<PhysioLeistung> entries = qEntries.execute();
		List<PhysioLeistung> openEntries = new ArrayList<PhysioLeistung>();
		// get open entries -> field FLD_GUELTIG_BIS not set
		for (PhysioLeistung physio : entries) {
			String sBis = physio.get(PhysioLeistung.FLD_BIS);
			if (sBis == null || sBis.trim().length() == 0) {
				openEntries.add(physio);
			} else {
				TimeTool tBis = new TimeTool(sBis);
				if (tBis.isEqual(endOfEpoch)) {
					openEntries.add(physio);
				}
			}
		}
		if (openEntries.isEmpty()) {
			PhysioLeistung physio = new PhysioLeistung(line[0], line[1], line[2], validFrom, null);
			if (lineHasFixPrice(line)) {
				applyFixPrice(physio, line[3]);
			}
		} else {
			// do actual import if entries with updating open entries
			for (PhysioLeistung physio : openEntries) {
				if (physio.get(PhysioLeistung.FLD_VON)
					.equals(validFrom.toString(TimeTool.DATE_COMPACT))) {
					// test if the gVon is the same -> update the values of the entry
					physio.set(new String[] {
						"Titel", "TP"
					}, line[1], line[2]);
					if (lineHasFixPrice(line)) {
						applyFixPrice(physio, line[3]);
					}
				} else {
					// close entry and create new entry
					physio.set(PhysioLeistung.FLD_BIS,
						validFrom.toString(TimeTool.DATE_COMPACT));
					
					PhysioLeistung newPhysio =
						new PhysioLeistung(line[0], line[1], line[2], validFrom, null);
					if (lineHasFixPrice(line)) {
						applyFixPrice(newPhysio, line[3]);
					}
				}
			}
		}
	}
	

	private void applyFixPrice(PhysioLeistung physio, String string){
		physio.set(PhysioLeistung.FLD_TP, string);
		StringBuilder sb = new StringBuilder();
		String existingText = physio.get(PhysioLeistung.FLD_TEXT);
		if (existingText != null) {
			sb.append(existingText);
		}
		sb.append(PhysioLeistung.FIXEDPRICE);
		physio.set(PhysioLeistung.FLD_TEXT, sb.toString());
	}
	
	private boolean lineHasFixPrice(String[] line){
		return line.length > 3 && line[3] != null && !line[3].isEmpty()
			&& Character.isDigit(line[3].charAt(0));
	}
	
	@Override
	public String getDescription(){
		return "Physiotherapie-Tarif";
	}
	
	@Override
	public String getTitle(){
		return "Physio";
	}
	
}
