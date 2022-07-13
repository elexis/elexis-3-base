package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.MandatorData;

public class MandatorElement {
	public static Element create(Document doc) throws Exception {
		MandatorData pd = new MandatorData();
		if (pd.canLoad()) {
			pd.load();

			Element p = doc.createElement("Mandator"); //$NON-NLS-1$

			Element c = doc.createElement("FirstName"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(pd.getFirstName()));
			p.appendChild(c);

			c = doc.createElement("LastName"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(pd.getLastName()));
			p.appendChild(c);

			c = doc.createElement("Title"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(pd.getTitle()));
			p.appendChild(c);

			c = doc.createElement("Email"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(pd.getEmail()));
			p.appendChild(c);

			c = doc.createElement("Phone"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(pd.getPhone()));
			p.appendChild(c);

			return p;
		}
		return null;
	}
}
