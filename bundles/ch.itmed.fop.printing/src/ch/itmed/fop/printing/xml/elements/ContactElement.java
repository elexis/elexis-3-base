package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.ContactData;

public class ContactElement {
	public static int SELECTED_CONTACT = 0;
	public static int APPOINTMENT_CONTACT = 1;
	public static int APPOINTMENTS_CONTACT = 2;

	public static Element create(Document doc, int contactType) throws Exception {
		ContactData cd = new ContactData();
		switch (contactType) {
		case 0:
			cd.load();
			break;
		case 1:
			cd.loadFromAppointment();
			break;
		case 2:
			cd.loadFromAppointments();
			break;
		}

		Element p = doc.createElement("Contact");

		Element c = doc.createElement("Address");
		String address = cd.getAddress();
		String[] addressParts = address.split("[\\r\\n]+");
		for (String addressPart : addressParts) {
			Element part = doc.createElement("Part");
			part.appendChild(doc.createTextNode(addressPart));
			c.appendChild(part);
		}
		p.appendChild(c);

		c = doc.createElement("Salutation");
		c.appendChild(doc.createTextNode(cd.getSalutaton()));
		p.appendChild(c);

		return p;
	}
}
