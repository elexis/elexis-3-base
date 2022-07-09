package at.medevit.ch.artikelstamm.elexis.common.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import at.medevit.ch.artikelstamm.DATASOURCEType;
import at.medevit.ch.artikelstamm.IArtikelstammItem;

public class VersionUtil {
	private static DateFormat df = new SimpleDateFormat("ddMMyy HH:mm"); //$NON-NLS-1$
	private static final String VERSION_ENTRY_ID = "VERSION"; //$NON-NLS-1$

	public static DATASOURCEType getDatasourceType() {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			return DATASOURCEType.fromValue(versionEntry.get().getAdditionalDescription());
		}
		throw new IllegalArgumentException("No Verison entry"); //$NON-NLS-1$
	}

	public static void setDataSourceType(DATASOURCEType datasource) {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			versionEntry.get().setAdditionalDescription(datasource.value());
			ModelServiceHolder.get().save(versionEntry.get());
		}
		throw new IllegalArgumentException("No Verison entry"); //$NON-NLS-1$
	}

	public static int getCurrentVersion() {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			Object ppub = ModelServiceHolder.get().getEntityProperty("ppub", versionEntry.get()); //$NON-NLS-1$
			if (ppub instanceof String) {
				try {
					return Integer.parseInt(((String) ppub).trim());
				} catch (NumberFormatException e) {
					// ignore return 0
				}
			}
		}
		return 0;
	}

	public static void setCurrentVersion(int newVersion) {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			ModelServiceHolder.get().setEntityProperty("ppub", Integer.toString(newVersion), versionEntry.get()); //$NON-NLS-1$
			ModelServiceHolder.get().save(versionEntry.get());
		}
		throw new IllegalArgumentException("No Verison entry"); //$NON-NLS-1$
	}

	public static void setImportSetCreationDate(Date time) {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			ModelServiceHolder.get().setEntityProperty("dscr", df.format(time.getTime()), versionEntry.get()); //$NON-NLS-1$
			ModelServiceHolder.get().save(versionEntry.get());
		}
		throw new IllegalArgumentException("No Verison entry"); //$NON-NLS-1$
	}

	public static Date getImportSetCreationDate() {
		Optional<IArtikelstammItem> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID,
				IArtikelstammItem.class);
		if (versionEntry.isPresent()) {
			Object value = ModelServiceHolder.get().getEntityProperty("dscr", versionEntry.get()); //$NON-NLS-1$
			if (value instanceof String) {
				try {
					return df.parse((String) value);
				} catch (ParseException e) {
					// ignore fall back return null
				}
			}
			return null;
		}
		throw new IllegalArgumentException("No Verison entry"); //$NON-NLS-1$
	}
}
