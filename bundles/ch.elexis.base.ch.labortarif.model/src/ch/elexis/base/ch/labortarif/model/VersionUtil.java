package ch.elexis.base.ch.labortarif.model;

import java.util.Optional;

import ch.elexis.base.ch.labortarif.ILaborLeistung;

public class VersionUtil {
	private static final String VERSION_ENTRY_ID = "1"; //$NON-NLS-1$

	public static int getCurrentVersion() {
		Optional<ILaborLeistung> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID, ILaborLeistung.class);
		if (versionEntry.isPresent()) {
			Object ppub = ModelServiceHolder.get().getEntityProperty("chapter", versionEntry.get()); //$NON-NLS-1$
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
		Optional<ILaborLeistung> versionEntry = ModelServiceHolder.get().load(VERSION_ENTRY_ID, ILaborLeistung.class);
		if (versionEntry.isPresent()) {
			ModelServiceHolder.get().setEntityProperty("chapter", Integer.toString(newVersion), versionEntry.get()); //$NON-NLS-1$
			ModelServiceHolder.get().save(versionEntry.get());
		} else {
			throw new IllegalArgumentException("No Version entry"); //$NON-NLS-1$
		}
	}
}
