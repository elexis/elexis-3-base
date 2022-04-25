package ch.elexis.base.ch.arzttarife.rfe;

import java.util.HashMap;
import java.util.Map;

public class ReasonsForEncounter {
	private static final String[][] rfe = { { "01", "01- Kontakt auf Wunsch des Patienten", "01-Wunsch" },
			{ "02", "02- Notfallkonsultation (vor 1.6.2012)", "02-NF" },
			{ "03", "03- Kontakt auf Zuweisung", "03-Zuweis." },
			{ "04", "04- Folgekontakt auf Verordnung/Empfehlung", "04-Verord." },
			{ "05", "05- Folgekontakt wegen auswärtiger Hämatologie und Chemie", "05-Labor" },
			{ "06", "06- Kontakt in Zusammenhang mit Langzeitpflege", "06-Langz." },
			{ "07", "07- Kontakt in kausalem Zusammenhang mit Eingriff / Hospitalisation", "07-Spital" },
			{ "99", "99- Kein Arztkontakt", "99-" }

	};

	private static HashMap<String, String> codeToReasonMap;
	private static HashMap<String, String> codeToShortReasonMap;

	static {
		codeToReasonMap = new HashMap<String, String>();
		for (String[] line : rfe) {
			codeToReasonMap.put(line[0], line[1]);
		}
		codeToShortReasonMap = new HashMap<String, String>();
		for (String[] line : rfe) {
			codeToShortReasonMap.put(line[0], line[2]);
		}
	}

	public static Map<String, String> getCodeToReasonMap() {
		return codeToReasonMap;
	}

	public static Map<String, String> getCodeToShortReasonMap() {
		return codeToShortReasonMap;
	}
}
