package at.medevit.elexis.inbox.model;

public enum InboxElementType {
	FILE("FILE://"), DB("");

	private final String prefix;

	private InboxElementType(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static InboxElementType parseType(String uri) {
		if (uri != null) {
			if (uri.contains("::")) {
				return DB;
			} else if (uri.startsWith(FILE.getPrefix())) {
				return FILE;
			}
		}
		return null;
	}
}
