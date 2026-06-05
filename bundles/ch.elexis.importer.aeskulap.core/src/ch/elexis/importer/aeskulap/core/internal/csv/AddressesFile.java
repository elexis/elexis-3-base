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
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.core.types.Gender;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

public class AddressesFile extends AbstractCsvImportFile<IContact> implements IAeskulapImportFile {

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
	public IContact create(String[] line) {
		String firstname = line[1];
		String lastname = line[2];
		String department = line[3];
		if (StringUtils.isBlank(firstname) || StringUtils.isBlank(lastname)) {
			String bez = firstname == null ? StringUtils.EMPTY : firstname;
			if (bez.length() > 0) {
				bez += StringUtils.SPACE;
			}
			bez += lastname;
			
			IOrganization org = CoreModelServiceHolder.get().create(IOrganization.class);
			org.setDescription1(bez);
			org.setDescription2(department);
			
			CoreModelServiceHolder.get().save(org);
			return org;
		} else {
			IPerson person = CoreModelServiceHolder.get().create(IPerson.class);
			person.setLastName(lastname);
			person.setFirstName(firstname);
			person.setGender(Gender.UNKNOWN);
			
			CoreModelServiceHolder.get().save(person);
			return person;
		}
	}

	@Override
	public void setProperties(IContact contact, String[] line) {
		if (contact != null) {
			String street = StringUtils.isBlank(line[5]) ? line[4] : line[4] + ", " + line[5];
			
			contact.setStreet(street);
			contact.setZip(line[6]);
			contact.setCity(line[7]);
			
			if (StringUtils.isNotBlank(line[8])) {
				try {
					contact.setCountry(Country.valueOf(line[8].toUpperCase()));
				} catch (IllegalArgumentException e) {
					// Ignoriere Fehler, falls der String im CSV nicht dem Country-Enum entspricht
				}
			}
			
			contact.setCode(line[9]);
			contact.setPhone1(line[16]);
			contact.setPhone2(line[15]);
			contact.setMobile(line[14]);
			contact.setFax(line[17]);
			contact.setEmail(line[18]);
			
			String ean = line[19];
			if (StringUtils.isNotBlank(ean)) {
				XidServiceHolder.get().addXid(contact, XidConstants.DOMAIN_EAN, ean, false);
			}
			XidServiceHolder.get().addXid(contact, getXidDomain(), line[0], true);
			
			CoreModelServiceHolder.get().save(contact);
		}
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Adressen Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				IContact contact = getExisting(line[0]);
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
