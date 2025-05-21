package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.itmed.fop.printing.barcode.BarcodeCreator;
import ch.itmed.fop.printing.data.MedicationData;

public class RowaArticleMedicationElement {

	private static final String BARCODE_SEPARATOR = ":";

	public static Element create(Document doc, IPrescription iPrescription, IPatient patient, String gtin) {
		MedicationData md = new MedicationData(iPrescription);
		return createElement(doc, md, patient, gtin);
	}

	private static Element createElement(Document doc, MedicationData md, IPatient patient, String gtin) {
		Element barcodeElement = doc.createElement("Barcode"); //$NON-NLS-1$
		Element etiketteElement = doc.createElement("Etikette"); //$NON-NLS-1$
		etiketteElement.setAttribute("barcodeLabel", BarcodeCreator //$NON-NLS-1$
				.createInternalCode128FromArticleString(patient.getPatientNr() + BARCODE_SEPARATOR + gtin));
		System.out.println(patient.getPatientNr() + BARCODE_SEPARATOR + gtin);
		barcodeElement.appendChild(etiketteElement);

		return barcodeElement;
	}
}