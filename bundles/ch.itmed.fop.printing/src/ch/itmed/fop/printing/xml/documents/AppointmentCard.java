package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.AppointmentsInformationElement;
import ch.itmed.fop.printing.xml.elements.ContactElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public final class AppointmentCard {
	
	public static InputStream create() throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.APPOINTMENT_CARD);
		doc.appendChild(page);
		Element appointment = AppointmentsInformationElement.create(doc, true);
		page.appendChild(appointment);
		Element contact = ContactElement.create(doc, ContactElement.APPOINTMENT_CONTACT);
		page.appendChild(contact);
		Element patient = PatientElement.create(doc, true);
		page.appendChild(patient);

		return DomDocument.toInputStream(doc);
	}

}
