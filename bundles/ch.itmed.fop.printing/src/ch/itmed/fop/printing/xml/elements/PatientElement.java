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

import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.data.PatientData;

public class PatientElement {

	public static Element create(Document doc, boolean loadFromAgenda) throws Exception {
		return create(doc, loadFromAgenda, false, null);
	}

	/**
	 *
	 * @param doc
	 * @param loadFromAgenda
	 * @param useLegalGuardian
	 * @param patient          use the given patient, instead of loading from
	 *                         context (only considered if loadFromAgenda is false)
	 * @return
	 * @throws Exception
	 */
	public static Element create(Document doc, boolean loadFromAgenda, boolean useLegalGuardian, IPatient patient)
			throws Exception {
		PatientData pd = new PatientData(useLegalGuardian);

		if (loadFromAgenda) {
			pd.loadFromAgenda();
		} else {
			pd.load(patient);
		}

		Element p = doc.createElement("Patient"); //$NON-NLS-1$

		Element c = doc.createElement("FirstName"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getFirstName()));
		p.appendChild(c);

		c = doc.createElement("LastName"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getLastName()));
		p.appendChild(c);

		c = doc.createElement("Birthdate"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getBirthdate()));
		p.appendChild(c);

		c = doc.createElement("Sex"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getSex()));
		p.appendChild(c);

		c = doc.createElement("PID"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPid()));
		p.appendChild(c);

		c = doc.createElement("Salutation"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getSalutation()));
		p.appendChild(c);

		c = doc.createElement("Title"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getTitle()));
		p.appendChild(c);

		c = doc.createElement("PostalCode"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPostalCode()));
		p.appendChild(c);

		c = doc.createElement("City"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getCity()));
		p.appendChild(c);

		c = doc.createElement("Country"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getCountry()));
		p.appendChild(c);

		c = doc.createElement("Street"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getStreet()));
		p.appendChild(c);

		c = doc.createElement("Phone1"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPhone1()));
		p.appendChild(c);

		c = doc.createElement("Phone2"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPhone2()));
		p.appendChild(c);

		c = doc.createElement("MobilePhone"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getMobilePhone()));
		p.appendChild(c);

		c = doc.createElement("Email"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getEmail()));
		p.appendChild(c);

		c = doc.createElement("CompleteAddress"); //$NON-NLS-1$
		String address = pd.getCompleteAddress();
		String[] addressParts = address.split("[\\r\\n]+"); //$NON-NLS-1$
		for (String addressPart : addressParts) {
			Element part = doc.createElement("Part"); //$NON-NLS-1$
			part.appendChild(doc.createTextNode(addressPart));
			c.appendChild(part);
		}
		p.appendChild(c);

		c = doc.createElement("OrderNumber"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getOrderNumber()));
		p.appendChild(c);

		return p;
	}
}
