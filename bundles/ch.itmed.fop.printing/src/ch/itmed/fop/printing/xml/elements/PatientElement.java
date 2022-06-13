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

import ch.itmed.fop.printing.data.PatientData;

public class PatientElement {

	public static Element create(Document doc, boolean loadFromAgenda) throws Exception {
		return create(doc, loadFromAgenda, false);
	}

	public static Element create(Document doc, boolean loadFromAgenda, boolean useLegalGuardian) throws Exception {
		PatientData pd = new PatientData(useLegalGuardian);

		if (loadFromAgenda) {
			pd.loadFromAgenda();
		} else {
			pd.load();
		}

		Element p = doc.createElement("Patient");

		Element c = doc.createElement("FirstName");
		c.appendChild(doc.createTextNode(pd.getFirstName()));
		p.appendChild(c);

		c = doc.createElement("LastName");
		c.appendChild(doc.createTextNode(pd.getLastName()));
		p.appendChild(c);

		c = doc.createElement("Birthdate");
		c.appendChild(doc.createTextNode(pd.getBirthdate()));
		p.appendChild(c);

		c = doc.createElement("Sex");
		c.appendChild(doc.createTextNode(pd.getSex()));
		p.appendChild(c);

		c = doc.createElement("PID");
		c.appendChild(doc.createTextNode(pd.getPid()));
		p.appendChild(c);

		c = doc.createElement("Salutation");
		c.appendChild(doc.createTextNode(pd.getSalutation()));
		p.appendChild(c);

		c = doc.createElement("Title");
		c.appendChild(doc.createTextNode(pd.getTitle()));
		p.appendChild(c);

		c = doc.createElement("PostalCode");
		c.appendChild(doc.createTextNode(pd.getPostalCode()));
		p.appendChild(c);

		c = doc.createElement("City");
		c.appendChild(doc.createTextNode(pd.getCity()));
		p.appendChild(c);

		c = doc.createElement("Country");
		c.appendChild(doc.createTextNode(pd.getCountry()));
		p.appendChild(c);

		c = doc.createElement("Street");
		c.appendChild(doc.createTextNode(pd.getStreet()));
		p.appendChild(c);

		c = doc.createElement("Phone1");
		c.appendChild(doc.createTextNode(pd.getPhone1()));
		p.appendChild(c);

		c = doc.createElement("Phone2");
		c.appendChild(doc.createTextNode(pd.getPhone2()));
		p.appendChild(c);

		c = doc.createElement("MobilePhone");
		c.appendChild(doc.createTextNode(pd.getMobilePhone()));
		p.appendChild(c);

		c = doc.createElement("Email");
		c.appendChild(doc.createTextNode(pd.getEmail()));
		p.appendChild(c);

		c = doc.createElement("CompleteAddress");
		String address = pd.getCompleteAddress();
		String[] addressParts = address.split("[\\r\\n]+");
		for (String addressPart : addressParts) {
			Element part = doc.createElement("Part");
			part.appendChild(doc.createTextNode(addressPart));
			c.appendChild(part);
		}
		p.appendChild(c);

		c = doc.createElement("OrderNumber");
		c.appendChild(doc.createTextNode(pd.getOrderNumber()));
		p.appendChild(c);

		return p;
	}
}
