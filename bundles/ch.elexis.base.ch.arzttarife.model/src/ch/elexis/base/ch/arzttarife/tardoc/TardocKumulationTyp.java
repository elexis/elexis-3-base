package ch.elexis.base.ch.arzttarife.tardoc;

public enum TardocKumulationTyp {
	EXCLUSION("E"), INCLUSION("I"), EXCLUSIVE("X");

	private String typ;

	TardocKumulationTyp(String typ) {
		this.typ = typ;
	}

	public String getTyp() {
		return typ;
	}

	public static TardocKumulationTyp ofTyp(String typ) {
		if ("E".equals(typ)) {
			return EXCLUSION;
		} else if ("I".equals(typ)) {
			return INCLUSION;
		} else if ("X".equals(typ)) {
			return EXCLUSIVE;
		}
		return null;
	}

	public static String toString(TardocKumulationTyp typ) {
		if (typ == EXCLUSION) {
			return "Exklusion";
		} else if (typ == INCLUSION) {
			return "Inklusion";
		} else if (typ == EXCLUSIVE) {
			return "Exklusiv";
		}
		return null;
	}
}
