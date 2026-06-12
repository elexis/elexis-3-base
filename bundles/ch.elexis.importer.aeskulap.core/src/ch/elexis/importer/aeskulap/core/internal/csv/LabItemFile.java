package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.LabMapping;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class LabItemFile extends AbstractCsvImportFile<ILabItem> implements IAeskulapImportFile {

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
				ILabItem labItem = getExisting(line[0]);
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
	public ILabItem create(String[] line) {
		String sequence = "?";
		if (!StringUtils.isBlank(line[6].trim())) {
			sequence = line[6].trim().substring(0, 1);
		}
		IContact laboratory = (IContact) getWithXid(IAeskulapImporter.XID_IMPORT_LABCONTACT, line[3]);
		ILabItem labItem = CoreModelServiceHolder.get().create(ILabItem.class);
		labItem.setCode(line[7]);
		labItem.setName(line[6].trim());
		labItem.setUnit(line[4]);
		labItem.setTyp(LabItemTyp.TEXT);
		labItem.setGroup("Import");
		labItem.setPriority(sequence);

		CoreModelServiceHolder.get().save(labItem);
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
		XidServiceHolder.get().addXid(labItem, getXidDomain(), line[0], true);
		return labItem;
	}

	@Override
	public void setProperties(ILabItem contact, String[] line) {
		// no more properties
	}
}