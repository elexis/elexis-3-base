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
import ch.elexis.core.types.Gender;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Person;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class AddressesFile extends AbstractCsvImportFile<Kontakt> implements IAeskulapImportFile {

	private File file;

	public AddressesFile(File file) {
		super(file);
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Adressen");
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line.length > 1 && line[1] != null && line[1].equalsIgnoreCase("vorname");
	}

	@Override
	public Kontakt create(String[] line) {
		String firstname = line[1];
		String lastname = line[2];
		String department = line[3];
		if (StringUtils.isBlank(firstname) || StringUtils.isBlank(lastname)) {
			String bez = firstname == null ? StringUtils.EMPTY : firstname;
			if (bez.length() > 0) {
				bez += StringUtils.SPACE;
			}
			bez += lastname;
			return new Organisation(bez, department);
		} else {
			return new Person(lastname, firstname, StringUtils.EMPTY, Gender.UNKNOWN.value());
		}
	}

	@Override
	public void setProperties(Kontakt contact, String[] line) {
		String street = StringUtils.isBlank(line[5]) ? line[4] : line[4] + ", " + line[5];
		Anschrift an = contact.getAnschrift();
		an.setStrasse(street);
		an.setPlz(line[6]);
		an.setOrt(line[7]);
		an.setLand(line[8]);
		contact.setAnschrift(an);
		contact.set(Kontakt.FLD_SHORT_LABEL, line[9]);
		contact.set(Kontakt.FLD_PHONE1, line[16]);
		contact.set(Kontakt.FLD_PHONE2, line[15]);
		contact.set(Kontakt.FLD_MOBILEPHONE, line[14]);
		contact.set(Kontakt.FLD_FAX, line[17]);
		contact.set(Kontakt.FLD_E_MAIL, line[18]);
		String ean = line[19];
		if (!StringUtils.isBlank(ean)) {
			contact.addXid(XidConstants.DOMAIN_EAN, ean, false);
		}
		contact.addXid(getXidDomain(), line[0], true);
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Adressen Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				Kontakt contact = getExisting(line[0]);
				if (contact == null) {
					contact = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(contact, line);
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
		return Type.ADDRESSES;
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_ADDRESS;
	}
}
