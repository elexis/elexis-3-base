/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.ContactData;

public class ContactElement {

	public static Element create(Document doc) throws Exception {
		return create(doc, false);
	}

	public static Element create(Document doc, boolean useLegalGuardian) throws Exception {
		ContactData cd = new ContactData();
		cd.load();

		Element p = doc.createElement("Contact");

		Element c = doc.createElement("Address");
		String address = cd.getAddress(useLegalGuardian);
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
