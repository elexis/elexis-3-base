/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.importer.div.importers.ExcelWrapper;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Importer extends ImporterPage {
	int langdef = 0;
	InputStream tarifInputStream;
	TimeTool validFrom = new TimeTool();
	
	Fachspec[] specs;
	int row;
	// use local HashMap instead of creating new one for every tarif position
	private HashMap<String, String> importedValues = new HashMap<String, String>();
	
	public Importer(){
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
		String lang = JdbcLink.wrap(CoreHub.localCfg.get( // d,f,i
			Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}
		specs = loadFachspecs(langdef);
		if (specs != null) {
			ExcelWrapper exw = new ExcelWrapper();
			exw.setFieldTypes(new Class[] {
				String.class /* chapter */, String.class /* rev */, String.class /* code */,
				String.class /* tp */, String.class /* name */, String.class /* lim */,
				String.class /* fach (2011) comment (2012) */, String.class
			/* fach (2012) */
			});
			try {
				if (tarifInputStream == null)
					tarifInputStream = new FileInputStream(results[0]);
				
				if (exw.load(tarifInputStream, langdef)) {
					int first = exw.getFirstRow();
					int last = exw.getLastRow();
					int count = last - first;
					if (monitor != null)
						monitor.beginTask(Messages.Importer_importEAL, count);
					
					String[] line = exw.getRow(1).toArray(new String[0]);
					// determine format of file according to year of tarif
					int formatYear = getFormatYear(line);
					if (formatYear != 2011 && formatYear != 2012)
						return new Status(Status.ERROR, "ch.elexis.labotarif.ch2009", //$NON-NLS-1$
							"unknown file format"); //$NON-NLS-1$
						
					for (int i = first + 1; i <= last; i++) {
						row = i;
						line = exw.getRow(i).toArray(new String[0]);
						
						if (formatYear == 2011)
							fillImportedValues2011(line);
						else if (formatYear == 2012)
							fillImportedValues2012(line);
						
						if (importedValues.size() > 0) {
							updateOrCreateFromImportedValues();
						}
						if (monitor != null) {
							monitor.worked(1);
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
						}
					}
					
					closeAllOlder();
					if (monitor != null)
						monitor.done();
					ElexisEventDispatcher.reload(Labor2009Tarif.class);
					return Status.OK_STATUS;
				}
			} finally {
				if (tarifInputStream != null)
					tarifInputStream.close();
			}
		}
		return new Status(Status.ERROR, "ch.elexis.labotarif.ch2009", //$NON-NLS-1$
			"could not load file"); //$NON-NLS-1$
	}
	
	private void updateOrCreateFromImportedValues(){
		// get all entries with matching code
		Query<Labor2009Tarif> qEntries = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		qEntries.add(Labor2009Tarif.FLD_CODE, "=", importedValues.get(Labor2009Tarif.FLD_CODE));
		
		List<Labor2009Tarif> entries = qEntries.execute();
		List<Labor2009Tarif> openEntries = new ArrayList<Labor2009Tarif>();
		// get open entries -> field FLD_GUELTIG_BIS not set
		for (Labor2009Tarif labor2009Tarif : entries) {
			String gBis = labor2009Tarif.get(Labor2009Tarif.FLD_GUELTIG_BIS);
			if (gBis == null || gBis.trim().length() == 0)
				openEntries.add(labor2009Tarif);
		}
		if (openEntries.isEmpty()) {
			// just create if there are no open entries
			Labor2009Tarif tarif =
				new Labor2009Tarif(importedValues.get(Labor2009Tarif.FLD_CHAPTER),
					importedValues.get(Labor2009Tarif.FLD_CODE),
					importedValues.get(Labor2009Tarif.FLD_TP),
					importedValues.get(Labor2009Tarif.FLD_NAME),
					importedValues.get(Labor2009Tarif.FLD_LIMITATIO),
					importedValues.get(Labor2009Tarif.FLD_FACHBEREICH), Fachspec.getFachspec(specs,
						row));
			tarif.set(Labor2009Tarif.FLD_GUELTIG_VON, validFrom.toString(TimeTool.DATE_COMPACT));
		} else {
			// do actual import if entries with updating open entries
			for (Labor2009Tarif labor2009Tarif : openEntries) {
				if (labor2009Tarif.get(Labor2009Tarif.FLD_GUELTIG_VON).equals(
					validFrom.toString(TimeTool.DATE_COMPACT))) {
					// test if the gVon is the same -> update the values of the entry
					labor2009Tarif.set(new String[] {
						Labor2009Tarif.FLD_CHAPTER, Labor2009Tarif.FLD_CODE, Labor2009Tarif.FLD_TP,
						Labor2009Tarif.FLD_NAME, Labor2009Tarif.FLD_LIMITATIO,
						Labor2009Tarif.FLD_FACHBEREICH, Labor2009Tarif.FLD_FACHSPEC
					}, importedValues.get(Labor2009Tarif.FLD_CHAPTER),
						importedValues.get(Labor2009Tarif.FLD_CODE),
						importedValues.get(Labor2009Tarif.FLD_TP),
						importedValues.get(Labor2009Tarif.FLD_NAME),
						importedValues.get(Labor2009Tarif.FLD_LIMITATIO),
						importedValues.get(Labor2009Tarif.FLD_FACHBEREICH),
						Integer.toString(Fachspec.getFachspec(specs, row)));
				} else {
					// close entry and create new entry
					labor2009Tarif.set(Labor2009Tarif.FLD_GUELTIG_BIS,
						validFrom.toString(TimeTool.DATE_COMPACT));
					
					Labor2009Tarif tarif =
						new Labor2009Tarif(importedValues.get(Labor2009Tarif.FLD_CHAPTER),
							importedValues.get(Labor2009Tarif.FLD_CODE),
							importedValues.get(Labor2009Tarif.FLD_TP),
							importedValues.get(Labor2009Tarif.FLD_NAME),
							importedValues.get(Labor2009Tarif.FLD_LIMITATIO),
							importedValues.get(Labor2009Tarif.FLD_FACHBEREICH),
							Fachspec.getFachspec(specs, row));
					tarif.set(Labor2009Tarif.FLD_GUELTIG_VON,
						validFrom.toString(TimeTool.DATE_COMPACT));
				}
			}
		}
	}
	
	private void closeAllOlder(){
		// get all entries
		TimeTool defaultValidFrom = new TimeTool();
		defaultValidFrom.set(1970, 0, 1);
		Query<Labor2009Tarif> qEntries = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		List<Labor2009Tarif> entries = qEntries.execute();
		
		for (Labor2009Tarif labor2009Tarif : entries) {
			String validFromString = labor2009Tarif.get(Labor2009Tarif.FLD_GUELTIG_VON);
			String validUntilString = labor2009Tarif.get(Labor2009Tarif.FLD_GUELTIG_BIS);
			if ((validFromString == null || validFromString.trim().length() == 0)) {
				// old entry with no valid from
				labor2009Tarif.set(Labor2009Tarif.FLD_GUELTIG_VON,
					defaultValidFrom.toString(TimeTool.DATE_COMPACT));
				labor2009Tarif.set(Labor2009Tarif.FLD_GUELTIG_BIS,
					validFrom.toString(TimeTool.DATE_COMPACT));
			} else if ((validUntilString == null || validUntilString.trim().length() == 0)
				&& !validFrom.toString(TimeTool.DATE_COMPACT).equals(validFromString)) {
				// old entry not closed yet
				labor2009Tarif.set(Labor2009Tarif.FLD_GUELTIG_BIS,
					validFrom.toString(TimeTool.DATE_COMPACT));
			}
		}
	}
	
	private void fillImportedValues2012(String[] line){
		importedValues.clear();
		importedValues.put(Labor2009Tarif.FLD_CHAPTER, StringTool.getSafe(line, 0));
		// convert code to nnnn.mm
		String code = convertCodeString(StringTool.getSafe(line, 2));
		
		importedValues.put(Labor2009Tarif.FLD_CODE, code);
		importedValues.put(Labor2009Tarif.FLD_TP,
			convertLocalizedNumericString(StringTool.getSafe(line, 3)).toString());
		importedValues.put(Labor2009Tarif.FLD_NAME,
			StringTool.limitLength(StringTool.getSafe(line, 4), 254));
		importedValues.put(Labor2009Tarif.FLD_LIMITATIO, StringTool.getSafe(line, 5));
		importedValues.put(Labor2009Tarif.FLD_FACHBEREICH, StringTool.getSafe(line, 7));
	}
	
	private void fillImportedValues2011(String[] line){
		importedValues.clear();
		importedValues.put(Labor2009Tarif.FLD_CHAPTER, StringTool.getSafe(line, 0));
		// convert code to nnnn.mm
		String code = convertCodeString(StringTool.getSafe(line, 2));
		
		importedValues.put(Labor2009Tarif.FLD_CODE, code);
		importedValues.put(Labor2009Tarif.FLD_TP,
			convertLocalizedNumericString(StringTool.getSafe(line, 3)).toString());
		importedValues.put(Labor2009Tarif.FLD_NAME,
			StringTool.limitLength(StringTool.getSafe(line, 4), 254));
		importedValues.put(Labor2009Tarif.FLD_LIMITATIO, StringTool.getSafe(line, 5));
		importedValues.put(Labor2009Tarif.FLD_FACHBEREICH, StringTool.getSafe(line, 6));
	}
	
	private String convertCodeString(String code){
		// split by all possible delimiters after reading for xls
		String[] parts = code.split("[\\.,]");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(part);
			// at one point we should reach nnnn -> then add the '.' delimiter
			if (sb.length() == 4)
				sb.append(".");
		}
		// if there was no "sub number" add "00"
		if (sb.length() == 5)
			sb.append("00");
		else if (sb.length() == 6)
			sb.append("0");
		
		return sb.toString();
	}
	
	private String convertLocalizedNumericString(String localized){
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		Number number = new Integer(0);
		try {
			number = df.parse(localized);
		} catch (ParseException pe) { /* ignore and return default 0 */}
		// cut off decimals if there are none
		if ((number.doubleValue() % 1.0) > 0) {
			return Double.toString(number.doubleValue());
		} else {
			return Integer.toString(number.intValue());
		}
	}
	
	int getFormatYear(String[] line){
		String fach2011 = StringTool.getSafe(line, 6);
		String fach2012 = StringTool.getSafe(line, 7);
		if (fach2012.equals("") && !fach2011.equals(""))
			return 2011;
		else if (fach2011.equals("") && !fach2012.equals(""))
			return 2012;
		else
			return -1;
	}
	
	@Override
	public String getDescription(){
		return Messages.Importer_selectFile;
	}
	
	@Override
	public String getTitle(){
		return "EAL 2009"; //$NON-NLS-1$
	}
	
	public static Fachspec[] loadFachspecs(int langdef){
		String specs =
			PlatformHelper.getBasePath(Constants.pluginID) + File.separator
				+ "rsc" + File.separator + "arztpraxen.xls"; //$NON-NLS-1$ //$NON-NLS-2$
		ExcelWrapper x = new ExcelWrapper();
		x.setFieldTypes(new Class[] {
			Integer.class, String.class, Integer.class, Integer.class
		});
		if (x.load(specs, langdef)) {
			int first = x.getFirstRow();
			int last = x.getLastRow();
			Fachspec[] fspecs = new Fachspec[last - first + 1];
			for (int i = first; i <= last; i++) {
				fspecs[i] = new Fachspec(x.getRow(i).toArray(new String[0]));
			}
			return fspecs;
		}
		return null;
	}
	
	public static class Fachspec {
		public int code, from, until;
		public String name;
		
		Fachspec(String[] line){
			this(Integer.parseInt(StringTool.getSafe(line, 0)), StringTool.getSafe(line, 1),
				Integer.parseInt(StringTool.getSafe(line, 2)), Integer.parseInt(StringTool.getSafe(
					line, 3)));
		}
		
		Fachspec(int code, String name, int from, int until){
			this.code = code;
			this.from = from;
			this.until = until;
			this.name = name;
		}
		
		/**
		 * Find the spec a given row belongs to
		 * 
		 * @param specs
		 *            a list of all specs
		 * @param row
		 *            the row to match
		 * @return the spec number or -1 if no spec
		 */
		public static int getFachspec(Fachspec[] specs, int row){
			for (Fachspec spec : specs) {
				if (spec.from <= row && spec.until >= row) {
					return spec.code;
				}
			}
			return -1;
		}
	}
}
