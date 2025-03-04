package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.data.Kontakt;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;

public class CoverageGarant extends AbstractCsvImportFile<Kontakt> implements IAeskulapImportFile {

	private File file;

	private CoverageGarant garant;

	private Map<String, String[]> garantKurzBezMap;

	public CoverageGarant(File file) {
		super(file);
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line.length > 1 && line[1] != null && line[1].equalsIgnoreCase("GARANT_NO");
	}

	@Override
	public Kontakt create(String[] line) {
		throw new IllegalStateException();
	}

	private void init() {
		garantKurzBezMap = new HashMap<String, String[]>();
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				garantKurzBezMap.put(line[0], line);
			}
		} catch (IOException | CsvValidationException e) {
			LoggerFactory.getLogger(getClass()).error("Error importing file", e);
		} finally {
			close();
		}
	}

	public String[] getGuarantLine(String garKurzBez) {
		if (garantKurzBezMap == null) {
			init();
		}
		return garantKurzBezMap.get(garKurzBez);
	}

	@Override
	public void setProperties(Kontakt contact, String[] line) {
		throw new IllegalStateException();
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		return false;
	}

	@Override
	public Type getType() {
		return Type.COVERAGE;
	}

	@Override
	public String getXidDomain() {
		throw new IllegalStateException();
	}
}
