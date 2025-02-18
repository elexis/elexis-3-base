package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.rgw.tools.TimeTool;

public class PatientFile extends AbstractCsvImportFile<Patient> implements IAeskulapImportFile {

	private File file;

	private int highestPatNr = 0;

	private IAeskulapImportFile mandatorFile;

	public PatientFile(File file) {
		super(file);
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line.length > 0 && line[0].equalsIgnoreCase("pat_no");
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Patienten");
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Patienten Import", getLineCount());
		try {
			mandatorFile = transientFiles.get(Type.MANDATOR);

			String[] line = null;
			while ((line = getNextLine()) != null) {
				Patient patient = getExisting(line[0]);
				if (patient == null) {
					patient = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(patient, line);
				monitor.worked(1);
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
	public Type getType() {
		return Type.PATIENT;
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_PATIENT;
	}

	@Override
	public Patient create(String[] line) {
		TimeTool tt = new TimeTool(line[12]);
		String gender = line[13].equals("1") ? "m" : "w";
		Patient patient = new Patient(line[2], line[3], tt.toString(TimeTool.DATE_GER), gender);
		if (Boolean.getBoolean(IAeskulapImporter.PROP_KEEPPATIENTNUMBER)) {
			updatePatientNumber(patient, Integer.parseInt(line[0]));
		}
		return patient;
	}

	@Override
	public void setProperties(Patient patient, String[] line) {
		Anschrift an = patient.getAnschrift();
		an.setStrasse(line[5]);
		an.setPlz(line[6]);
		an.setOrt(line[7]);
		patient.setAnschrift(an);
		if (mandatorFile != null) {
			patient.set(Patient.FLD_GROUP, (String) mandatorFile.getTransient(line[8]));
		}
		patient.set(Patient.FLD_PHONE1, line[18]);
		patient.set(Patient.FLD_PHONE2, line[17]);
		patient.set(Patient.FLD_MOBILEPHONE, line[19]);
		patient.set(Patient.FLD_E_MAIL, line[20]);

		// In elexis, we have a multi purpose comment field "Bemerkung".
		// We'll collect several fields there
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isBlank(line[21])) {
			sb.append("Verstorben: ").append(line[21]).append(StringUtils.LF);
		}
		if (!StringUtils.isBlank(line[14])) {
			sb.append("Kommentar: ").append(line[14]).append(StringUtils.LF);
		}
		if (!StringUtils.isBlank(line[15])) {
			sb.append("Warnung: ").append(line[15]).append(StringUtils.LF);
		}
		if (!StringUtils.isBlank(line[11])) {
			sb.append("Beruf: ").append(line[11]).append(StringUtils.LF);
		}
		patient.setBemerkung(sb.toString());

		if (!StringUtils.isBlank(line[22])) {
			patient.addXid(XidConstants.DOMAIN_AHV, line[22], true);
		}

		patient.addXid(getXidDomain(), line[0], true);
	}

	private void updatePatientNumber(Patient patient, Integer patNr) {
		String lockid = PersistentObject.lock("PatNummer", true);
		String pid = PersistentObject.getDefaultConnection()
				.queryString("SELECT WERT FROM CONFIG WHERE PARAM='PatientNummer'");
		int currNum = Integer.parseInt(pid);
		if (highestPatNr == 0) {
			highestPatNr = currNum;
		}
		if ((patNr + 1) > highestPatNr) {
			highestPatNr = patNr + 1;
		}
		// Patient create always increments, so we need to reset to highest number
		PersistentObject.getDefaultConnection().exec("UPDATE CONFIG set wert='" + highestPatNr + "', lastupdate="
				+ Long.toString(System.currentTimeMillis()) + " where param='PatientNummer'");

		PersistentObject.unlock("PatNummer", lockid);
		patient.set(Patient.FLD_PATID, patNr.toString());
	}
}
