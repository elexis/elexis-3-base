package ch.elexis.extdoc.util;

/**
 * Filter fuer die folgende Festlegung:
 *
 * - Die ersten 6 Zeichen des Nachnamens. Falls kuerzer, mit Leerzeichen
 * aufgefuellt
 *
 * - Der Vorname (nur der erste, falls es mehrere gibt)
 *
 */
public class FileFiltersConvention {

	private String shortName;

	public final static String BirthdayNotKnown = "1111-11-11"; //$NON-NLS-1$

	public FileFiltersConvention(String lastname, String firstname) {
		firstname = MatchPatientToPath.firstToken(firstname);

		lastname = MatchPatientToPath.cleanName(lastname);
		firstname = MatchPatientToPath.cleanName(firstname);

		String shortLastname;

		if (lastname.length() >= 6) {
			// Nachname ist lang genug
			shortLastname = lastname.substring(0, 6);
		} else {
			// Nachname ist zu kurz, mit Leerzeichen auffuellen
			StringBuilder sb = new StringBuilder();
			sb.append(lastname);
			while (sb.length() < 6) {
				sb.append(" "); //$NON-NLS-1$
			}
			shortLastname = sb.toString();
		}
		shortName = shortLastname + firstname;
	}

	public String getShortName() {
		return shortName;
	}
}