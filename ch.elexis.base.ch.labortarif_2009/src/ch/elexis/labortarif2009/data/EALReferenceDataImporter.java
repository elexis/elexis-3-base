/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/

package ch.elexis.labortarif2009.data;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.Importer.Fachspec;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class EALReferenceDataImporter extends AbstractReferenceDataImporter {
	private static final Logger logger = LoggerFactory.getLogger(EALReferenceDataImporter.class);
	
	int langdef = 0;
	TimeTool validFrom;
	
	Fachspec[] specs;
	int row;
	// use local HashMap instead of creating new one for every tarif position
	private HashMap<String, String> importedValues = new HashMap<String, String>();

	@Override
	public @NonNull Class<?> getReferenceDataTypeResponsibleFor(){
		return Labor2009Tarif.class;
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, @NonNull InputStream input, @Nullable Integer newVersion){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		validFrom = getValidFromVersion(newVersion);

		String lang = JdbcLink.wrap(CoreHub.localCfg.get( // d,f,i
			Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}
		specs = Importer.loadFachspecs(langdef);
		if (specs != null) {
			ExcelWrapper exw = new ExcelWrapper();
			exw.setFieldTypes(new Class[] {
				String.class /* chapter */, String.class /* rev */, String.class /* code */,
				String.class /* tp */, String.class /* name */, String.class /* lim */,
				String.class /* fach (2011) comment (2012) */, String.class
			/* fach (2012) */
			});
			if (exw.load(input, langdef)) {
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
				if(newVersion != null) {
					Labor2009Tarif.setCurrentCodeVersion(newVersion);
				}
				EALBlocksCodeUpdater blocksUpdater = new EALBlocksCodeUpdater();
				String message = blocksUpdater.updateBlockCodes();
				logger.info("Updated Blocks: \n" + message); //$NON-NLS-1$
				return Status.OK_STATUS;
			}
		}
		return new Status(Status.ERROR, "ch.elexis.labotarif.ch2009", //$NON-NLS-1$
			"could not load file"); //$NON-NLS-1$
	}
	


	/**
	 * Convert version Integer in yymmdd format to date.
	 * 
	 * @param newVersion
	 * @return
	 */
	private TimeTool getValidFromVersion(Integer newVersion){
		String intString = Integer.toString(newVersion);
		if (intString.length() != 6) {
			throw new IllegalStateException("Version " + newVersion
				+ " can not be parsed to valid date.");
		}
		String year = intString.substring(0, 2);
		String month = intString.substring(2, 4);
		String day = intString.substring(4, 6);
		TimeTool ret = new TimeTool();
		ret.set(TimeTool.YEAR, Integer.parseInt(year) + 2000);
		ret.set(TimeTool.MONTH, Integer.parseInt(month) - 1);
		ret.set(TimeTool.DAY_OF_MONTH, Integer.parseInt(day));
		return ret;
	}

	public int getVersionFromValid(TimeTool validFrom){
		int year = validFrom.get(TimeTool.YEAR);
		int month = validFrom.get(TimeTool.MONTH) + 1;
		int day = validFrom.get(TimeTool.DAY_OF_MONTH);
		
		return day + (month * 100) + ((year - 2000) * 10000);
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
						},
						concatChapter(labor2009Tarif,
							importedValues.get(Labor2009Tarif.FLD_CHAPTER)),
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

	private String concatChapter(Labor2009Tarif existing, String chapter){
		String existingChapter = existing.get(Labor2009Tarif.FLD_CHAPTER);
		if (existingChapter != null && !existingChapter.isEmpty()) {
			return existingChapter + ", " + chapter;
		} else {
			return chapter;
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

	private String convertCodeString(String code){
		// split by all possible delimiters after reading for xls
		String[] parts = code.split("[\\.,']");
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

	private int getFormatYear(String[] line){
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
	public int getCurrentVersion(){
		return Labor2009Tarif.getCurrentCodeVersion();
	}
	
}
