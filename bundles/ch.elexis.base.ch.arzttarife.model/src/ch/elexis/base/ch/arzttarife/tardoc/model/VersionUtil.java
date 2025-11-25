package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;

public class VersionUtil {
	public static final String VERSION_ENTRY_ID = "Version";

	private static final String DATASET_VERSION_SEPARATOR = "|";
	private static final String DATASET_LAW_SEPARATOR = ":=:";

	public static int getCurrentVersion() {
		return getCurrentVersion(StringUtils.EMPTY);
	}

	public static int getCurrentVersion(String law) {
		Optional<ITardocLeistung> versionEntry = ArzttarifeModelServiceHolder.get().load(VERSION_ENTRY_ID,
				ITardocLeistung.class);
		if (law == null || law.isEmpty()) {
			String versionVal = (String) ArzttarifeModelServiceHolder.get().getEntityProperty("code_",
					versionEntry.get());
			return getVersionAsInt(versionVal);
		} else {
			// read from text if law specified
			String versionVal = (String) ArzttarifeModelServiceHolder.get().getEntityProperty("tx255",
					versionEntry.get());
			if (versionVal != null) {
				String[] parts = versionVal.split("\\" + DATASET_VERSION_SEPARATOR);
				for (String part : parts) {
					String[] subParts = part.split(DATASET_LAW_SEPARATOR);
					if (subParts.length == 2) {
						if (law.equals(subParts[0])) {
							return getVersionAsInt(subParts[1]);
						}
					}
				}
			}
		}
		return -1;
	}

	private static int getVersionAsInt(String versionVal) {
		try {
			return Integer.parseInt(versionVal);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static Optional<ITardocLeistung> createVersionEntry() {
		ITardocLeistung versionEntry = ArzttarifeModelServiceHolder.get().create(ITardocLeistung.class);
		ArzttarifeModelServiceHolder.get().setEntityProperty("id", VERSION_ENTRY_ID, versionEntry);
		ArzttarifeModelServiceHolder.get().setEntityProperty("nickname", "1.0.0", versionEntry);
		ArzttarifeModelServiceHolder.get().save(versionEntry);
		return Optional.of(versionEntry);
	}

	public static void setCurrentVersion(String versionVal, String law) {
		Optional<ITardocLeistung> versionEntry = ArzttarifeModelServiceHolder.get().load(VERSION_ENTRY_ID,
				ITardocLeistung.class);

		// write to code if no law specified, old behavior
		if (law == null || law.isEmpty()) {
			if (!versionEntry.isPresent()) {
				versionEntry = createVersionEntry();
			}
			ArzttarifeModelServiceHolder.get().setEntityProperty("code_", versionVal, versionEntry.get());
		} else {
			boolean found = false;
			StringBuilder sb = new StringBuilder();
			// read and replace Version for specified law
			String oldVersions = (String) ArzttarifeModelServiceHolder.get().getEntityProperty("tx255",
					versionEntry.get());
			if (oldVersions == null) {
				oldVersions = StringUtils.EMPTY;
			}
			String[] parts = oldVersions.split("\\" + DATASET_VERSION_SEPARATOR);
			for (String part : parts) {
				String[] subParts = part.split(DATASET_LAW_SEPARATOR);
				if (subParts.length == 2) {
					if (sb.length() > 0) {
						sb.append(DATASET_VERSION_SEPARATOR);
					}
					sb.append(subParts[0]);
					if (law.equals(subParts[0])) {
						sb.append(DATASET_LAW_SEPARATOR).append(versionVal);
						found = true;
					} else {
						sb.append(DATASET_LAW_SEPARATOR).append(subParts[1]);
					}
				}
			}
			// if not found, add new version string
			if (!found) {
				if (sb.length() > 0) {
					sb.append(DATASET_VERSION_SEPARATOR);
				}
				sb.append(law).append(DATASET_LAW_SEPARATOR).append(versionVal);
			}
			ArzttarifeModelServiceHolder.get().setEntityProperty("tx255", sb.toString(), versionEntry.get());
		}
		ArzttarifeModelServiceHolder.get().save(versionEntry.get());
	}
}
