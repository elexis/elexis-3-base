package ch.elexis.base.ch.arzttarife.ambulatory;

public enum AmbulantePauschalenTyp {
	PAUSCHALE("Pauschale"), TRIGGER("Trigger");

	private String code;

	private AmbulantePauschalenTyp(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static AmbulantePauschalenTyp fromCode(String name) {
		for (AmbulantePauschalenTyp value : AmbulantePauschalenTyp.values()) {
			if (value.getCode().equals(name)) {
				return value;
			}
		}
		return null;
	}
}
