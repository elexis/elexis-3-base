package ch.elexis.importer.aeskulap.core;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.SubMonitor;

public interface IAeskulapImporter {

	final static String XID_IMPORT = "elexis.ch/aeskulap_import";
	final static String XID_IMPORT_ADDRESS = XID_IMPORT + "/AddrID";
	final static String XID_IMPORT_LABCONTACT = XID_IMPORT + "/LabContactID";
	final static String XID_IMPORT_LABITEM = XID_IMPORT + "/LabItemID";
	final static String XID_IMPORT_LABRESULT = XID_IMPORT + "/LabResultID";
	final static String XID_IMPORT_PATIENT = XID_IMPORT + "/PatID";
	final static String XID_IMPORT_GARANT = XID_IMPORT + "/garantID";
	final static String XID_IMPORT_LETTER = XID_IMPORT + "/LetterID";
	final static String XID_IMPORT_DOCUMENT = XID_IMPORT + "/DocumentID";
	final static String XID_IMPORT_FILE = XID_IMPORT + "/FileID";

	static final String PROP_KEEPPATIENTNUMBER = "aeskulap.keepPatientNumber";

	/**
	 * Set the import directory to import data from. Returns a list of
	 * {@link IAeskulapImportFile} with the found files that can be imported.
	 *
	 * @param directory
	 * @return
	 */
	public List<IAeskulapImportFile> setImportDirectory(File directory);

	/**
	 * Import all provided files. If existing content should be overwritten or
	 * skipped can be specified with the overwrite parameter. If an error occurred
	 * importing a file, that file is returned, if the returned list is empty
	 * everything was imported successful.
	 *
	 * @param files
	 * @param overwrite
	 * @param monitor
	 * @return
	 */
	List<IAeskulapImportFile> importFiles(List<IAeskulapImportFile> files, boolean overwrite, SubMonitor monitor);
}
