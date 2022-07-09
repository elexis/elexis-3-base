package at.medevit.ch.artikelstamm.model.importer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import at.medevit.ch.artikelstamm.DATASOURCEType;
import ch.elexis.core.jpa.entities.ArtikelstammItem;

public class VersionUtil {
	private static DateFormat df = new SimpleDateFormat("ddMMyy HH:mm"); //$NON-NLS-1$
	private static final String VERSION_ENTRY_ID = "VERSION"; //$NON-NLS-1$

	public static DATASOURCEType getDatasourceType() {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
		if (versionEntry != null) {
			return DATASOURCEType.fromValue(versionEntry.getAdddscr());
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public static void setDataSourceType(DATASOURCEType datasource) {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setAdddscr(datasource.value());
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public static int getCurrentVersion() {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
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

	public static void setCurrentVersion(int newVersion) {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setPpub(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public static void setImportSetCreationDate(Date time) {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
		if (versionEntry != null) {
			versionEntry.setDscr(df.format(time.getTime()));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
	}

	public static Date getImportSetCreationDate() {
		ArtikelstammItem versionEntry = EntityUtil.load(VERSION_ENTRY_ID, ArtikelstammItem.class);
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
