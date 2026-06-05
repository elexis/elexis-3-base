package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILabOrder;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.LabOrderState;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.PathologicDescription;
import ch.elexis.core.types.PathologicDescription.Description;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.rgw.tools.TimeTool;

public class LabResultFile extends AbstractCsvImportFile<ILabResult> implements IAeskulapImportFile {

	private File file;

	private Map<IPatient, ILabOrder> importOrderMap;

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
				ILabResult labResult = getExisting(line[0]);
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
	public ILabResult create(String[] line) {
		IPatient patient = (IPatient) getWithXid(IAeskulapImporter.XID_IMPORT_PATIENT, line[3]);
		ILabItem item = (ILabItem) getWithXid(IAeskulapImporter.XID_IMPORT_LABITEM, line[4]);
		if (patient == null || item == null) {
			LoggerFactory.getLogger(getClass()).error("Could not find patient_no (Patient) [" + line[3]
					+ "] or typ_no (LabItem) [" + line[4] + "] for labresult_no [" + line[0] + "]");
			return null;
		}
		TimeTool date = new TimeTool(line[9]);

		ILabResult labResult = CoreModelServiceHolder.get().create(ILabResult.class);
		labResult.setPatient(patient);
		labResult.setDate(date.toLocalDate());
		labResult.setItem(item);
		labResult.setResult(getResult(line));
		labResult.setComment(getComment(line));

		CoreModelServiceHolder.get().save(labResult);
		String orderId = StringUtils.EMPTY;
		ILabOrder previousOrder = importOrderMap.get(patient);

		if (previousOrder == null) {
			orderId = UUID.randomUUID().toString();
		} else {
			orderId = previousOrder.getOrderId();
		}
		ILabOrder order = CoreModelServiceHolder.get().create(ILabOrder.class);
		order.setPatient(patient);
		order.setItem(item);
		order.setResult(labResult);
		order.setOrderId(orderId);
		order.setGroupName("Aeskulap Import");
		order.setTimeStamp(new TimeTool().toLocalDateTime());
		order.setState(LabOrderState.DONE_IMPORT);

		CoreModelServiceHolder.get().save(order);

		if (previousOrder == null) {
			importOrderMap.put(patient, order);
		}

		XidServiceHolder.get().addXid(labResult, getXidDomain(), line[0], true);
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
	public void setProperties(ILabResult labResult, String[] line) {
		if (labResult != null) {
			IContact laboratory = (IContact) getWithXid(IAeskulapImporter.XID_IMPORT_LABCONTACT, line[1]);
			if (laboratory != null) {
				labResult.setOrigin(laboratory);
			}
			if (!StringUtils.isBlank(line[10])) {
				TimeTool time = new TimeTool(line[10]);
				TimeTool observationTime = new TimeTool(line[9]);
				observationTime.setTime(time);
				labResult.setObservationTime(observationTime.toLocalDateTime());
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
					labResult.setReferenceMale(refValue);
				} else {
					labResult.setReferenceFemale(refValue);
				}
			}
			// must be done after setting result and reference values
			if (!StringUtils.isBlank(line[17])) {
				labResult.setPathologic(true);
				labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT, line[17]));
			} else {
				labResult.setPathologic(false);
				labResult.setPathologicDescription(new PathologicDescription(Description.PATHO_IMPORT));
			}
			CoreModelServiceHolder.get().save(labResult);
		}
	}

	private boolean isNumericRef(String refValue) {
		return refValue.matches("[\\.,<>+\\-0-9()]+");
	}
}