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
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.data.PersistentObject;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.rgw.tools.TimeTool;

public class PatientFile extends AbstractCsvImportFile<IPatient> implements IAeskulapImportFile {

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
				IPatient patient = getExisting(line[0]);
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
	public IPatient create(String[] line) {
		TimeTool tt = new TimeTool(line[12]);
		Gender gender = line[13].equals("1") ? Gender.MALE : Gender.FEMALE;

		IPatient patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), line[3], line[2],
				tt.toLocalDateTime().toLocalDate(), gender).buildAndSave();

		if (Boolean.getBoolean(IAeskulapImporter.PROP_KEEPPATIENTNUMBER)) {
			updatePatientNumber(patient, Integer.parseInt(line[0]));
		}
		return patient;
	}

	@Override
	public void setProperties(IPatient patient, String[] line) {
		if (patient != null) {
			patient.setStreet(line[5]);
			patient.setZip(line[6]);
			patient.setCity(line[7]);

			if (mandatorFile != null) {
				patient.setGroup((String) mandatorFile.getTransient(line[8]));
			}

			patient.setPhone1(line[18]);
			patient.setPhone2(line[17]);
			patient.setMobile(line[19]);
			patient.setEmail(line[20]);

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
			patient.setComment(sb.toString());

			if (!StringUtils.isBlank(line[22])) {
				XidServiceHolder.get().addXid(patient, XidConstants.DOMAIN_AHV, line[22], true);
			}
			XidServiceHolder.get().addXid(patient, getXidDomain(), line[0], true);

			CoreModelServiceHolder.get().save(patient);
		}
	}

	private void updatePatientNumber(IPatient patient, Integer patNr) {
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
		patient.setPatientNr(patNr.toString());
	}
}