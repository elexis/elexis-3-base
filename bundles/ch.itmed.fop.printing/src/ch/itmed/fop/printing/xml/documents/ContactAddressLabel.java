package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.ContactElement;

public class ContactAddressLabel {
	public static InputStream create() throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.CONTACT_ADDRESS_LABEL);
		doc.appendChild(page);
		Element contact = ContactElement.create(doc, ContactElement.SELECTED_CONTACT);
		page.appendChild(contact);

		return DomDocument.toInputStream(doc);
	}
}
