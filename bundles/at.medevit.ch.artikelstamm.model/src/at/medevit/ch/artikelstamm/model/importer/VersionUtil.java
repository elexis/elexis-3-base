package at.medevit.ch.artikelstamm.model.importer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import at.medevit.ch.artikelstamm.DATASOURCEType;
import ch.elexis.core.jpa.entities.ArtikelstammItem;
import ch.elexis.core.services.IElexisEntityManager;

public class VersionUtil {
	private static DateFormat df = new SimpleDateFormat("ddMMyy HH:mm"); //$NON-NLS-1$
	private static final String VERSION_ENTRY_ID = "VERSION"; //$NON-NLS-1$
	private IElexisEntityManager elexisEntityManager;

	public VersionUtil(IElexisEntityManager elexisEntityManager) {
		this.elexisEntityManager = elexisEntityManager;
	}

	public DATASOURCEType getDatasourceType() {

		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			return DATASOURCEType.fromValue(versionEntry.getAdddscr());
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public void setDataSourceType(DATASOURCEType datasource) {
		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setAdddscr(datasource.value());
			new EntityUtil(elexisEntityManager).save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public int getCurrentVersion() {
		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			String ppub = versionEntry.getPpub();
			try {
				return Integer.parseInt(((String) ppub).trim());
			} catch (NumberFormatException e) {
				// ignore return 0
			}
		}
		return 0;
	}

	public void setCurrentVersion(int newVersion) {
		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setPpub(Integer.toString(newVersion));
			new EntityUtil(elexisEntityManager).save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public void setImportSetCreationDate(Date time) {
		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setDscr(df.format(time.getTime()));
			new EntityUtil(elexisEntityManager).save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public Date getImportSetCreationDate() {
		ArtikelstammItem versionEntry = new EntityUtil(elexisEntityManager).load(VERSION_ENTRY_ID,
				ArtikelstammItem.class);
		if (versionEntry != null) {
			String value = versionEntry.getDscr();
			try {
				return df.parse((String) value);
			} catch (ParseException e) {
				return null;
			}
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}
}
