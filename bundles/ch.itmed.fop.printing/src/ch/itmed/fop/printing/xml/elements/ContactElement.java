package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.ContactData;

public class ContactElement {

	public static Element create(Document doc) throws Exception {
		ContactData cd = new ContactData();
		cd.load();

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
