package ch.elexis.base.ch.labortarif.model.importer;

import org.apache.commons.lang3.StringUtils;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.model.ModelServiceHolder;
import ch.elexis.base.ch.labortarif.model.VersionUtil;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

@Component(service = IReferenceDataImporter.class, property = IReferenceDataImporter.REFERENCEDATAID + "=analysenliste")
public class LaborTarifReferenceDataImporter extends AbstractReferenceDataImporter {

	private static String FLD_CODE = "code"; //$NON-NLS-1$
	private static String FLD_CHAPTER = "chapter"; //$NON-NLS-1$
	private static String FLD_TP = "tp"; //$NON-NLS-1$
	private static String FLD_NAME = "name"; //$NON-NLS-1$
	private static String FLD_LIMITATIO = "limitatio"; //$NON-NLS-1$
	private static String FLD_FACHBEREICH = "fachbereich"; //$NON-NLS-1$
	private static String FLD_GUELTIGVON = "gueltigVon"; //$NON-NLS-1$
	private static String FLD_GUELTIGBIS = "gueltigBis"; //$NON-NLS-1$

	int langdef = 0;
	LocalDate validFrom;

	int row;
	// use local HashMap instead of creating new one for every tarif position
	private HashMap<String, String> importedValues = new HashMap<String, String>();

	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, @NonNull InputStream input,
			@Nullable Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		validFrom = getValidFromVersion(newVersion).toLocalDate();

		String lang = ConfigServiceHolder.get().getLocal(Preferences.ABL_LANGUAGE, "d").toUpperCase(); //$NON-NLS-1$
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}
		ExcelWrapper exw = new ExcelWrapper();
		exw.setFieldTypes(new Class[] { String.class /* chapter */, String.class /* rev */, String.class /* code */,
				String.class /* tp */, String.class /* name */, String.class /* lim */,
				String.class /* fach (2011) comment (2012) */, String.class
				/* fach (2012) */
		});
		if (exw.load(input, langdef)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;
			if (monitor != null)
				monitor.beginTask("Import EAL", count); //$NON-NLS-1$

			String[] line = exw.getRow(1).toArray(new String[0]);
			// determine format of file according to year of tarif
			int formatYear = getFormatYear(line);
			if (formatYear != 2011 && formatYear != 2012 && formatYear != 2018)
				return new Status(Status.ERROR, "ch.elexis.base.ch.labortarif", //$NON-NLS-1$
						"unknown file format"); //$NON-NLS-1$

			for (int i = first + 1; i <= last; i++) {
				row = i;
				line = exw.getRow(i).toArray(new String[0]);

				if (formatYear == 2011)
					fillImportedValues2011(line);
				else if (formatYear == 2012)
					fillImportedValues2012(line);
				else if (formatYear == 2018)
					fillImportedValues2018(line);

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
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, ILaborLeistung.class);
			if (newVersion != null) {
				VersionUtil.setCurrentVersion(newVersion);
			}
			return Status.OK_STATUS;
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
	private TimeTool getValidFromVersion(Integer newVersion) {
		String intString = Integer.toString(newVersion);
		if (intString.length() != 6) {
			throw new IllegalStateException("Version " + newVersion + " can not be parsed to valid date."); //$NON-NLS-1$ //$NON-NLS-2$
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

	private void updateOrCreateFromImportedValues() {
		// get all entries with matching code
		INamedQuery<ILaborLeistung> query = ModelServiceHolder.get().getNamedQuery(ILaborLeistung.class, "code"); //$NON-NLS-1$
		List<ILaborLeistung> entries = query
				.executeWithParameters(query.getParameterMap("code", importedValues.get(FLD_CODE))); //$NON-NLS-1$
		List<ILaborLeistung> openEntries = new ArrayList<ILaborLeistung>();
		// get open entries -> field FLD_GUELTIG_BIS not set
		for (ILaborLeistung existing : entries) {
			LocalDate validTo = existing.getValidTo();
			if (validTo == null) {
				openEntries.add(existing);
			}
		}
		if (openEntries.isEmpty()) {
			// just create if there are no open entries
			createWithValues(importedValues);
		} else {
			// do actual import if entries with updating open entries
			for (ILaborLeistung openEntry : openEntries) {
				if (openEntry.getValidFrom().equals(validFrom)) {
					// test if the gVon is the same -> update the values of the entry
					updateWithValues(openEntry, importedValues);
				} else {
					// close entry and create new entry
					ModelServiceHolder.get().setEntityProperty(FLD_GUELTIGBIS, validFrom.minusDays(1), openEntry);
					createWithValues(importedValues);
				}
			}
		}
	}

	private void updateWithValues(ILaborLeistung existing, HashMap<String, String> values) {
		IModelService modelService = ModelServiceHolder.get();

		modelService.setEntityProperty(FLD_CHAPTER, concatChapter(existing, values.get(FLD_CHAPTER)), existing);
		modelService.setEntityProperty(FLD_CODE, values.get(FLD_CODE), existing);
		modelService.setEntityProperty(FLD_TP, Double.valueOf(values.get(FLD_TP)), existing);
		modelService.setEntityProperty(FLD_NAME, values.get(FLD_NAME), existing);
		modelService.setEntityProperty(FLD_LIMITATIO, values.get(FLD_LIMITATIO), existing);
		modelService.setEntityProperty(FLD_FACHBEREICH, values.get(FLD_FACHBEREICH), existing);

		modelService.save(existing);
	}

	private ILaborLeistung createWithValues(HashMap<String, String> values) {
		IModelService modelService = ModelServiceHolder.get();
		ILaborLeistung created = modelService.create(ILaborLeistung.class);

		modelService.setEntityProperty(FLD_CHAPTER, values.get(FLD_CHAPTER), created);
		modelService.setEntityProperty(FLD_CODE, values.get(FLD_CODE), created);
		modelService.setEntityProperty(FLD_TP, Double.valueOf(values.get(FLD_TP)), created);
		modelService.setEntityProperty(FLD_NAME, values.get(FLD_NAME), created);
		modelService.setEntityProperty(FLD_LIMITATIO, values.get(FLD_LIMITATIO), created);
		modelService.setEntityProperty(FLD_FACHBEREICH, values.get(FLD_FACHBEREICH), created);
		modelService.setEntityProperty(FLD_GUELTIGVON, validFrom, created);

		modelService.save(created);
		return created;
	}

	private String concatChapter(ILaborLeistung existing, String chapter) {
		String existingChapter = existing.getChapter();
		if (existingChapter != null && !existingChapter.isEmpty()) {
			return chaptersMakeUnique(existingChapter + ", " + chapter); //$NON-NLS-1$
		} else {
			return chaptersMakeUnique(chapter);
		}
	}

	private String chaptersMakeUnique(String chapters) {
		String[] parts = chapters.split(", "); //$NON-NLS-1$
		if (parts != null && parts.length > 1) {
			StringBuilder sb = new StringBuilder();
			HashSet<String> set = new HashSet<>();
			set.addAll(Arrays.asList(parts));
			String[] array = set.toArray(new String[set.size()]);
			Arrays.sort(array);
			for (String string : array) {
				if (sb.length() > 0) {
					sb.append(", "); //$NON-NLS-1$
				}
				sb.append(string);
			}
			return sb.toString();
		} else {
			return chapters;
		}
	}

	private void fillImportedValues2012(String[] line) {
		importedValues.clear();
		importedValues.put(FLD_CHAPTER, StringTool.getSafe(line, 0));
		// convert code to nnnn.mm
		String code = convertCodeString(StringTool.getSafe(line, 2));

		importedValues.put(FLD_CODE, code);
		importedValues.put(FLD_TP, convertLocalizedNumericString(StringTool.getSafe(line, 3)).toString());
		importedValues.put(FLD_NAME, StringTool.limitLength(StringTool.getSafe(line, 4), 254));
		importedValues.put(FLD_LIMITATIO, StringTool.getSafe(line, 5));
		importedValues.put(FLD_FACHBEREICH, StringTool.getSafe(line, 7));
	}

	private void fillImportedValues2011(String[] line) {
		importedValues.clear();
		importedValues.put(FLD_CHAPTER, StringTool.getSafe(line, 0));
		// convert code to nnnn.mm
		String code = convertCodeString(StringTool.getSafe(line, 2));

		importedValues.put(FLD_CODE, code);
		importedValues.put(FLD_TP, convertLocalizedNumericString(StringTool.getSafe(line, 3)).toString());
		importedValues.put(FLD_NAME, StringTool.limitLength(StringTool.getSafe(line, 4), 254));
		importedValues.put(FLD_LIMITATIO, StringTool.getSafe(line, 5));
		importedValues.put(FLD_FACHBEREICH, StringTool.getSafe(line, 6));
	}

	private void fillImportedValues2018(String[] line) {
		importedValues.clear();
		importedValues.put(FLD_CHAPTER, StringTool.getSafe(line, 0));
		if (StringTool.getSafe(line, 6).equals("1") || StringTool.getSafe(line, 6).equalsIgnoreCase("true")) { //$NON-NLS-1$ //$NON-NLS-2$
			importedValues.put(FLD_CHAPTER, StringTool.getSafe(line, 0) + ", 5.1.2.2.1"); //$NON-NLS-1$
		}
		// convert code to nnnn.mm
		String code = convertCodeString(StringTool.getSafe(line, 1));

		importedValues.put(FLD_CODE, code);
		importedValues.put(FLD_TP, convertLocalizedNumericString(StringTool.getSafe(line, 2)).toString());
		importedValues.put(FLD_NAME, StringTool.limitLength(StringTool.getSafe(line, 3), 254));
		importedValues.put(FLD_LIMITATIO, StringTool.getSafe(line, 4));
		importedValues.put(FLD_FACHBEREICH, StringTool.getSafe(line, 5));
		if ("unknown cell type".equalsIgnoreCase(importedValues.get(FLD_FACHBEREICH))) {
			importedValues.put(FLD_FACHBEREICH, StringUtils.EMPTY);
		}
	}

	private void closeAllOlder() {
		IModelService modelService = ModelServiceHolder.get();
		// get all entries
		LocalDate defaultValidFrom = LocalDate.of(1970, 1, 1);
		List<ILaborLeistung> entries = modelService.getQuery(ILaborLeistung.class).execute();

		List<ILaborLeistung> oldEntriesToSave = new ArrayList<ILaborLeistung>();
		for (ILaborLeistung entry : entries) {
			if (entry.getValidFrom() == null) {
				// old entry with no valid from
				modelService.setEntityProperty(FLD_GUELTIGVON, defaultValidFrom, entry);
				modelService.setEntityProperty(FLD_GUELTIGBIS, validFrom.minusDays(1), entry);
			} else if (entry.getValidTo() == null && !entry.getValidFrom().equals(validFrom)) {
				// old entry not closed yet
				modelService.setEntityProperty(FLD_GUELTIGBIS, validFrom.minusDays(1), entry);
				oldEntriesToSave.add(entry);
			}
		}
		modelService.save(oldEntriesToSave);
	}

	private String convertCodeString(String code) {
		// split by all possible delimiters after reading for xls
		String[] parts = code.split("[\\.,']"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(part);
			// at one point we should reach nnnn -> then add the '.' delimiter
			if (sb.length() == 4)
				sb.append("."); //$NON-NLS-1$
		}
		// if there was no "sub number" add "00"
		if (sb.length() == 5)
			sb.append("00"); //$NON-NLS-1$
		else if (sb.length() == 6)
			sb.append("0"); //$NON-NLS-1$

		return sb.toString();
	}

	private String convertLocalizedNumericString(String localized) {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		Number number = new Integer(0);
		try {
			number = df.parse(localized);
		} catch (ParseException pe) {
			/* ignore and return default 0 */}
		// cut off decimals if there are none
		if ((number.doubleValue() % 1.0) > 0) {
			return Double.toString(number.doubleValue());
		} else {
			return Integer.toString(number.intValue());
		}
	}

	private int getFormatYear(String[] line) {
		String fach2011 = StringTool.getSafe(line, 6);
		String fach2012 = StringTool.getSafe(line, 7);
		String code2018 = StringTool.getSafe(line, 1);
		if (fach2012.equals(StringUtils.EMPTY) && !fach2011.equals(StringUtils.EMPTY)) {
			if (isCode(code2018)) {
				return 2018;
			} else {
				return 2011;
			}
		} else if (fach2011.equals(StringUtils.EMPTY) && !fach2012.equals(StringUtils.EMPTY)) {
			return 2012;
		}
		return -1;
	}

	private boolean isCode(String code2018) {
		code2018 = code2018.replaceAll("[\\.,']", StringUtils.EMPTY); //$NON-NLS-1$
		Integer value = Integer.valueOf(code2018);
		return value >= 1000;
	}

	@Override
	public int getCurrentVersion() {
		return VersionUtil.getCurrentVersion();
	}
}
