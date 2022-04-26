package at.medevit.elexis.outbox.model;

public enum OutboxElementType {
	FILE("FILE://"), DB(""), DOC("DOC://");

	private final String prefix;

	private OutboxElementType(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static OutboxElementType parseType(String uri) {
		if (uri != null) {
			if (uri.startsWith(FILE.getPrefix())) {
				return FILE;

			} else if (uri.startsWith(DOC.getPrefix())) {
				return DOC;
			} else if (uri.contains("::")) {
				return DB;
			}
		}
		return null;
	}
}
