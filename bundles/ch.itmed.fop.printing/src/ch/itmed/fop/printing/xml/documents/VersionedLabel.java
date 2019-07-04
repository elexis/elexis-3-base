package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.CaseElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public class VersionedLabel {

	/**
	 * Creates the XML file and returns it as an InputStream.
	 * 
	 * @return The generated XML as an InputStream
	 */
	public static InputStream create() throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.VERSIONED_LABEL);
		doc.appendChild(page);
		Element patient = PatientElement.create(doc, false);
		page.appendChild(patient);
		Element c = CaseElement.create(doc);
		page.appendChild(c);

		return DomDocument.toInputStream(doc);
	}

}
