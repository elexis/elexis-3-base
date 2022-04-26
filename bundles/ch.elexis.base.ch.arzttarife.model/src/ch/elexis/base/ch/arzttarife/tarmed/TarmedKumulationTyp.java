package ch.elexis.base.ch.arzttarife.tarmed;

public enum TarmedKumulationTyp {
	EXCLUSION("E"), INCLUSION("I"), EXCLUSIVE("X");

	private String typ;

	TarmedKumulationTyp(String typ) {
		this.typ = typ;
	}

	public String getTyp() {
		return typ;
	}

	public static TarmedKumulationTyp ofTyp(String typ) {
		if ("E".equals(typ)) {
			return EXCLUSION;
		} else if ("I".equals(typ)) {
			return INCLUSION;
		} else if ("X".equals(typ)) {
			return EXCLUSIVE;
		}
		return null;
	}

	public static String toString(TarmedKumulationTyp typ) {
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
