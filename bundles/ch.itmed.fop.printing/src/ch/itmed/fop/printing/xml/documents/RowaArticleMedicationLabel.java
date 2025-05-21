package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.MandatorElement;
import ch.itmed.fop.printing.xml.elements.MedicationElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;
import ch.itmed.fop.printing.xml.elements.RowaArticleMedicationElement;

public class RowaArticleMedicationLabel {
	public static InputStream create(IPrescription iPrescription, IPatient patient, String gtin) throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL);
		PageProperties.setCurrentDate(page);
		doc.appendChild(page);
		Element patientElement = PatientElement.create(doc, false, false, patient);
		page.appendChild(patientElement);
		Element medicationElement = MedicationElement.create(doc, iPrescription);
		page.appendChild(medicationElement);
		Element barcodeElement = RowaArticleMedicationElement.create(doc, iPrescription, patient, gtin);
		page.appendChild(barcodeElement);

		Element mandator = MandatorElement.create(doc, null);
		if (mandator != null) {
			page.appendChild(mandator);
		}

		return DomDocument.toInputStream(doc);
	}
}