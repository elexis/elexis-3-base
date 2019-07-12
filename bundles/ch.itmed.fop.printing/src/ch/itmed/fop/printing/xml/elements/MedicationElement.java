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

import ch.itmed.fop.printing.data.MedicationData;
import ch.itmed.fop.printing.resources.Messages;

public final class MedicationElement {
	public static Element create(Document doc) throws Exception {
		MedicationData md = new MedicationData();
		md.load();

		Element p = doc.createElement("Medication");

		Element c = doc.createElement("ArticleName");
		c.appendChild(doc.createTextNode(md.getArticleName()));
		p.appendChild(c);

		c = doc.createElement("ArticlePrice");
		c.appendChild(doc.createTextNode(md.getArticlePrice()));
		p.appendChild(c);

		c = doc.createElement("Dose");
		c.appendChild(doc.createTextNode(md.getDose()));
		p.appendChild(c);

		c = doc.createElement("DoseTableHeader");
		Element hi = doc.createElement("HeaderItem");
		hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Morning));
		c.appendChild(hi);
		hi = doc.createElement("HeaderItem");
		hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Midday));
		c.appendChild(hi);
		hi = doc.createElement("HeaderItem");
		hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Evening));
		c.appendChild(hi);
		hi = doc.createElement("HeaderItem");
		hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Night));
		c.appendChild(hi);
		p.appendChild(c);

		c = doc.createElement("DoseTableBody");
		for (String i : md.getDoseArray()) {
			Element di = doc.createElement("DoseItem");
			di.appendChild(doc.createTextNode(i));
			c.appendChild(di);
		}
		p.appendChild(c);

		c = doc.createElement("DosageInstructions");
		c.appendChild(doc.createTextNode(md.getDosageInstructions()));
		p.appendChild(c);

		c = doc.createElement("PrescriptionDate");
		c.appendChild(doc.createTextNode(md.getPrescriptionDate()));
		p.appendChild(c);

		c = doc.createElement("DeliveryDate");
		c.appendChild(doc.createTextNode(md.getDeliveryDate()));
		p.appendChild(c);

		c = doc.createElement("PrescriptionAuthor");
		c.appendChild(doc.createTextNode(md.getPrescriptionAuthor()));
		p.appendChild(c);

		c = doc.createElement("ResponsiblePharmacist");
		c.appendChild(doc.createTextNode(md.getResponsiblePharmacist()));
		p.appendChild(c);

		c = doc.createElement("MedicationType");
		c.appendChild(doc.createTextNode(md.getMedicationType()));
		p.appendChild(c);

		return p;
	}
}
