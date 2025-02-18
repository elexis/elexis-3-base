package ch.elexis.base.befunde.findings.migrator.messwert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class MesswertFieldMapping {

	private static final String MAPPING_CONFIG = "ch.elexis.core.findins/messwert/mapping"; //$NON-NLS-1$

	private String localMesswert;
	private String localMesswertField;

	private String findingsCode;

	private static final String MAPPING_FIELD_SEPARATOR_ESCAPED = "\\|\\|"; //$NON-NLS-1$
	private static final String MAPPING_SEPARATOR = "<->"; //$NON-NLS-1$
	private static final String MAPPING_TYPE = "_MESSWERTFIELDMAPPING_"; //$NON-NLS-1$

	/**
	 * Create a BefundFieldMapping from a String representation (see
	 * exportToString).
	 *
	 * @param string
	 * @return a valid BefundFieldMapping, or null
	 */
	public static MesswertFieldMapping createFromString(String string) {
		MesswertFieldMapping mapping = new MesswertFieldMapping();
		if (string.startsWith(MAPPING_TYPE)) {
			string = string.substring(MAPPING_TYPE.length(), string.length());
			String[] mappings = string.split(MAPPING_SEPARATOR);
			if (mappings.length == 2) {
				String[] localMapping = mappings[0].split(MAPPING_FIELD_SEPARATOR_ESCAPED);
				if (localMapping.length == 2) {
					mapping.localMesswert = localMapping[0];
					mapping.localMesswertField = localMapping[1];
				}
				mapping.findingsCode = mappings[1];
			}
		}
		return mapping.isValidMapping() ? mapping : null;
	}

	private MesswertFieldMapping() {
		// is only for use with createFromString method
	}

	public boolean isLocalMatching(String befund, String field) {
		if (localMesswert != null && localMesswertField != null) {
			return localMesswert.equals(befund) && localMesswertField.equals(field);
		}
		return false;
	}

	public boolean isFindingsCodeMatching(String findingsCode) {
		if (this.findingsCode != null && findingsCode != null) {
			return this.findingsCode.equals(findingsCode);
		}
		return false;
	}

	/**
	 * Test if the mapping is valid. Valid means local and remote befund field is
	 * set, and the befund is in the local setup exists.
	 *
	 * @return
	 */
	public boolean isValidMapping() {
		return localMesswert != null && localMesswertField != null && findingsCode != null;
	}

	public String getLocalBefund() {
		return localMesswert;
	}

	public String getLocalBefundField() {
		return localMesswertField;
	}

	public String getFindingsCode() {
		return findingsCode;
	}

	/**
	 * Load all mappings from the mandant configuration.
	 *
	 * @return
	 */
	public static List<MesswertFieldMapping> getMappings() {
		List<MesswertFieldMapping> ret = new ArrayList<MesswertFieldMapping>();
		if (ContextServiceHolder.get().getActiveMandator().isPresent()) {
			String mapping = ConfigServiceHolder.getMandator(MAPPING_CONFIG, StringUtils.EMPTY);
			String[] mappings = mapping.split(Messwert.SETUP_SEPARATOR);
			for (String string : mappings) {
				MesswertFieldMapping createdMapping = MesswertFieldMapping.createFromString(string);
				if (createdMapping != null) {
					ret.add(createdMapping);
				}
			}
		} else {
			throw new IllegalStateException("No mandant config available"); //$NON-NLS-1$
		}
		return ret;
	}
}
