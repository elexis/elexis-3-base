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

import ch.elexis.core.model.IPrescription;
import ch.itmed.fop.printing.data.MedicationData;
import ch.itmed.fop.printing.resources.Messages;

public final class MedicationElement {

	public static Element create(Document doc) throws Exception {
		MedicationData md = new MedicationData(null);
		md.load();

		return createElement(doc, md);
	}

	public static Element create(Document doc, IPrescription iPrescription) {
		MedicationData md = new MedicationData(iPrescription);

		return createElement(doc, md);

	}

	private static Element createElement(Document doc, MedicationData md) {
		Element p = doc.createElement("Medication"); //$NON-NLS-1$

		Element c = doc.createElement("ArticleName"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getArticleName()));
		p.appendChild(c);

		c = doc.createElement("ArticlePrice"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getArticlePrice()));
		p.appendChild(c);

		c = doc.createElement("Dose"); //$NON-NLS-1$
		if (md.isFreetext()) {
			c.setAttribute("Freetext", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			c.setAttribute("Freetext", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		c.appendChild(doc.createTextNode(md.getDose()));
		p.appendChild(c);

		if (md.getDose() != null && !md.getDose().isEmpty()) {
	        c = doc.createElement("DoseTableHeader"); //$NON-NLS-1$
	        Element hi = doc.createElement("HeaderItem"); //$NON-NLS-1$
	        hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Morning));
	        c.appendChild(hi);

	        hi = doc.createElement("HeaderItem"); //$NON-NLS-1$
	        hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Midday));
	        c.appendChild(hi);

	        hi = doc.createElement("HeaderItem"); //$NON-NLS-1$
	        hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Evening));
	        c.appendChild(hi);

	        hi = doc.createElement("HeaderItem"); //$NON-NLS-1$
	        hi.appendChild(doc.createTextNode(Messages.Medication_Dose_Night));
	        c.appendChild(hi);
	        p.appendChild(c);

	        c = doc.createElement("DoseTableBody"); //$NON-NLS-1$
	        for (String i : md.getDoseArray()) {
	            Element di = doc.createElement("DoseItem"); //$NON-NLS-1$
	            di.appendChild(doc.createTextNode(i));
	            c.appendChild(di);
	        }
	        p.appendChild(c);
	    }

		String dosageInstructions = md.getDosageInstructions();
		if (dosageInstructions != null && !dosageInstructions.trim().isEmpty()) {
			c = doc.createElement("DosageInstructions"); //$NON-NLS-1$
			c.appendChild(doc.createTextNode(dosageInstructions));
			p.appendChild(c);
		}

		c = doc.createElement("PrescriptionDate"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getPrescriptionDate()));
		p.appendChild(c);

		c = doc.createElement("DeliveryDate"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getDeliveryDate()));
		p.appendChild(c);

		c = doc.createElement("PrescriptionAuthor"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getPrescriptionAuthor()));
		p.appendChild(c);

		c = doc.createElement("ResponsiblePharmacist"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getResponsiblePharmacist()));
		p.appendChild(c);

		c = doc.createElement("MedicationType"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(md.getMedicationType()));
		p.appendChild(c);

		return p;
	}
}
