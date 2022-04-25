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

import ch.itmed.fop.printing.data.CaseData;

public final class CaseElement {
	public static Element create(Document doc) throws Exception {
		CaseData cd = new CaseData();
		Element p = doc.createElement("Case");

		Element c = doc.createElement("CostBearer");
		c.appendChild(doc.createTextNode(cd.getCostBearer()));
		p.appendChild(c);

		c = doc.createElement("InsurancePolicyNumber");
		c.appendChild(doc.createTextNode(cd.getInsurancePolicyNumber()));
		p.appendChild(c);

		return p;
	}
}
