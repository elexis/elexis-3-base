package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabMapping;
import ch.elexis.data.Labor;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class LabItemFile extends AbstractCsvImportFile<LabItem> implements IAeskulapImportFile {

	private File file;

	public LabItemFile(File file) {
		super(file);
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Labor_LabTyp");
	}

	@Override
	public Type getType() {
		return Type.LABORITEM;
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Labor Parameter Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				LabItem labItem = getExisting(line[0]);
				if (labItem == null) {
					labItem = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(labItem, line);
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
		return line[0].equalsIgnoreCase("LabTyp_no");
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_LABITEM;
	}

	@Override
	public LabItem create(String[] line) {
		String sequence = "?";
		if (!StringUtils.isBlank(line[6].trim())) {
			sequence = line[6].trim().substring(0, 1);
		}
		Labor laboratory = (Labor) getWithXid(IAeskulapImporter.XID_IMPORT_LABCONTACT, line[3]);
		LabItem labItem = new LabItem(line[7], line[6].trim(), laboratory, null, null, line[4], LabItemTyp.TEXT,
				"Import", sequence);
		if (laboratory != null) {
			String labCode = StringUtils.EMPTY;
			if (!StringUtils.isBlank(line[5])) {
				labCode = line[5];
			} else if (!StringUtils.isBlank(line[7])) {
				labCode = line[7];
			}
			if (!StringUtils.isBlank(labCode)) {
				new LabMapping(laboratory.getId(), labCode, labItem.getId(), false);
			}
		}
		labItem.addXid(getXidDomain(), line[0], true);
		return labItem;
	}

	@Override
	public void setProperties(LabItem contact, String[] line) {
		// no more properties
	}
}
