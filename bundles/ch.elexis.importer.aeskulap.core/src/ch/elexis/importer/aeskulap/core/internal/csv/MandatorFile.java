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

import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class MandatorFile extends AbstractCsvImportFile<String> implements IAeskulapImportFile {

	private File file;

	private Map<String, String> values;

	public MandatorFile(File file) {
		super(file);
		this.file = file;
		this.values = new HashMap<>();
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Mandanten");
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line[0] != null && line[0].equalsIgnoreCase("Arzt_No");
	}

	@Override
	public String create(String[] line) {
		values.put(line[0], StringUtils.abbreviate(line[1], 10));
		return line[1];
	}

	@Override
	public void setProperties(String map, String[] line) {
		// no additional properties
	}

	@Override
	public String getExisting(String id) {
		return values.get(id);
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				String manator = getExisting(line[0]);
				if (manator == null) {
					manator = create(line);
				}
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
		return Type.MANDATOR;
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_ADDRESS;
	}

	@Override
	public boolean isTransient() {
		return true;
	}

	@Override
	public Object getTransient(String id) {
		return values.get(id);
	}
}
