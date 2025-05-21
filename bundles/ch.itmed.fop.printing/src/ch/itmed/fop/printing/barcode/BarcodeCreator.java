package ch.itmed.fop.printing.barcode;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class BarcodeCreator {

	/**
	 * Creates a Code128 representation of a {@link Kontakt}
	 *
	 * @param pat the Kontakt subclass
	 * @return String Code128 representation
	 */
	public static String createInternalCode128fromKontakt(IContact pat) {
		return createInternalCode128FromKontaktString(StoreToStringServiceHolder.getStoreToString(pat));
	}

	public static String createInternalCode128FromKontaktPatNr(IContact pat) {
		return createInternalCode128FromKontaktPatNrString(pat);
	}

	public static String createInternalCode128FromKontaktString(String storeToString) {
		String output = null;
		if (storeToString != null) {
			if (storeToString.endsWith(","))
				storeToString = storeToString.substring(0, storeToString.length() - 1);
			String[] split = storeToString.split("::");
			output = "$KTKT$" + split[1].subSequence(0, 4) + "$" + Math.abs(split[1].hashCode());
		}
		return output;
	}

	public static String createInternalCode128FromArticleString(String article) {
		return article;
	}

	public static String createInternalCode128FromKontaktPatNrString(IContact pat) {
		String output = null;
		if (pat != null && pat.isPatient()) {
			IPatient patient = pat.asIPatient();
			output = patient.getPatientNr();
		}
		return output;
	}
}
