package at.medevit.elexis.inbox.model;

import org.apache.commons.lang3.StringUtils;

public enum InboxElementType {
	FILE("FILE://"), DB(StringUtils.EMPTY); //$NON-NLS-1$

	private final String prefix;

	private InboxElementType(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static InboxElementType parseType(String uri) {
		if (uri != null) {
			if (uri.contains("::")) { //$NON-NLS-1$
				return DB;
			} else if (uri.startsWith(FILE.getPrefix())) {
				return FILE;
			}
		}
		return null;
	}
}
