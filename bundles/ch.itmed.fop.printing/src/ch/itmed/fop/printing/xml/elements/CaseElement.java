package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.CaseData;

public final class CaseElement {
	public static Element create(Document doc) throws Exception {
		CaseData cd = new CaseData();
		cd.load();

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
