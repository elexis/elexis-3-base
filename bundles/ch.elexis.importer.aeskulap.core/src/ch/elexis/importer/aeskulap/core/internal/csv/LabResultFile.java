package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.PathologicDescription;
import ch.elexis.core.types.PathologicDescription.Description;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabOrder;
import ch.elexis.data.LabOrder.State;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.rgw.tools.TimeTool;

public class LabResultFile extends AbstractCsvImportFile<LabResult> implements IAeskulapImportFile {

	private File file;

	private Map<Patient, LabOrder> importOrderMap;

	public LabResultFile(File file) {
		super(file);
		this.file = file;

		importOrderMap = new HashMap<>();
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Labor_LabWert");
	}

	@Override
	public Type getType() {
		return Type.LABORRESULT;
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Labor Resultate Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				LabResult labResult = getExisting(line[0]);
				if (labResult == null) {
					labResult = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(labResult, line);
			}
			return true;
		} catch (IOException | CsvValidationException e) {
			LoggerFactory.getLogger(getClass()).error("Error importing file", e);
		} finally {
			close();
			monitor.done();
		}
		return false;
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line[0].equalsIgnoreCase("Labwert_no");
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_LABRESULT;
	}

	@Override
	public LabResult create(String[] line) {
		Patient patient = (Patient) getWithXid(IAeskulapImporter.XID_IMPORT_PATIENT, line[3]);
		LabItem item = (LabItem) getWithXid(IAeskulapImporter.XID_IMPORT_LABITEM, line[4]);
		if (patient == null || item == null) {
			LoggerFactory.getLogger(getClass()).error("Could not find patient_no (Patient) [" + line[3]
					+ "] or typ_no (LabItem) [" + line[4] + "] for labresult_no [" + line[0] + "]");
			return null;
		}
		TimeTool date = new TimeTool(line[9]);
		LabResult labResult = new LabResult(patient, date, item, getResult(line), getComment(line));

		String orderId = StringUtils.EMPTY;
		LabOrder order = importOrderMap.get(patient);
		if (order == null) {
			orderId = LabOrder.getNextOrderId();
			order = new LabOrder(CoreHub.getLoggedInContact(), ElexisEventDispatcher.getSelectedMandator(), patient,
					item, labResult, orderId, "Aeskulap Import", new TimeTool());
			importOrderMap.put(patient, order);
		} else {
			orderId = order.get(LabOrder.FLD_ORDERID);
			order = new LabOrder(CoreHub.getLoggedInContact(), ElexisEventDispatcher.getSelectedMandator(), patient,
					item, labResult, orderId, "Aeskulap Import", new TimeTool());
		}
		if (order != null) {
			order.setState(State.DONE_IMPORT);
		}
		labResult.addXid(getXidDomain(), line[0], true);
		return labResult;
	}

	private String getComment(String[] line) {
		String resultString = getResultString(line);
		if (resultString.length() > 20) {
			return resultString + "\n\n" + normalizeTextValue(line[11]);
		}
		return normalizeTextValue(line[11]);
	}

	public String normalizeNumericValue(String value) {
		String stringValue = value.replaceAll(",", "\\.");
		// get rid of not set digit places
		try {
			Double numericValue = Double.parseDouble(stringValue);
			stringValue = Double.toString(numericValue);
		} catch (NumberFormatException e) {
			// ignore
		}
		return stringValue;
	}

	public String normalizeTextValue(String value) {
		return value.replaceAll("_x000D_", StringUtils.EMPTY);
	}

	private String getResultString(String[] line) {
		if (!StringUtils.isBlank(line[5])) {
			return normalizeTextValue(line[5]);
		} else if (!StringUtils.isBlank(line[6])) {
			return normalizeNumericValue(line[6]);
		}
		return StringUtils.EMPTY;
	}

	private String getResult(String[] line) {
		String resultString = getResultString(line);
		if (resultString.length() > 20) {
			return "text";
		}
		return resultString;
	}

	@Override
	public void setProperties(LabResult labResult, String[] line) {
		if (labResult != null) {
			Labor laboratory = (Labor) getWithXid(IAeskulapImporter.XID_IMPORT_LABCONTACT, line[1]);
			if (laboratory != null) {
				labResult.set(LabResult.ORIGIN_ID, laboratory.getId());
			}
			if (!StringUtils.isBlank(line[10])) {
				TimeTool time = new TimeTool(line[10]);
				TimeTool observationTime = new TimeTool(line[9]);
				observationTime.setTime(time);
				labResult.setObservationTime(observationTime);
			}
			if (!StringUtils.isBlank(line[20])) {
				String refValue = line[20];
				if (isNumericRef(refValue)) {
					if (refValue.startsWith("(")) {
						refValue = refValue.substring(1);
					}
					if (refValue.endsWith(")")) {
						refValue = refValue.substring(0, refValue.length() - 1);
					}
				}
				if (labResult.getPatient().getGender() == Gender.MALE) {
					labResult.setRefMale(refValue);
				} else {
					labResult.setRefFemale(refValue);
				}
			}
			// must be done after setting result and reference values
			if (!StringUtils.isBlank(line[17])) {
				labResult.setFlag(LabResultConstants.PATHOLOGIC, true);
				labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT, line[17]));
			} else {
				labResult.setFlag(LabResultConstants.PATHOLOGIC, false);
				labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT));
			}
		}
	}

	private boolean isNumericRef(String refValue) {
		return refValue.matches("[\\.,<>+\\-0-9()]+");
	}
}
