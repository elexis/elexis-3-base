package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class LabContactFile extends AbstractCsvImportFile<IContact> implements IAeskulapImportFile {

	private File file;

	public LabContactFile(File file) {
		super(file);
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Labor_LabSource");
	}

	@Override
	public Type getType() {
		return Type.LABORCONTACT;
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Labor Adressen Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				IContact laboratory = getExisting(line[0]);
				if (laboratory == null) {
					laboratory = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(laboratory, line);
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
	public boolean isHeaderLine(String[] line) {
		return line[0].equalsIgnoreCase("LabSource_no");
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_LABCONTACT;
	}

	@Override
	public IContact create(String[] line) {
		IOrganization ret = CoreModelServiceHolder.get().create(IOrganization.class);
		ret.setLaboratory(true);
		ret.setCode(line[1]);
		ret.setDescription1(line[2]);

		CoreModelServiceHolder.get().save(ret);

		XidServiceHolder.get().addXid(ret, getXidDomain(), line[0], true);
		return ret;
	}

	@Override
	public void setProperties(IContact contact, String[] line) {
		// no more properties
	}
}