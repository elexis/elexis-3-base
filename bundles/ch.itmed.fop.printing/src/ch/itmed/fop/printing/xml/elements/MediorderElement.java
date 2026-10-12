package ch.itmed.fop.printing.xml.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class MediorderElement {

	private MediorderElement() {
	}

	public static Element create(Document doc, String barcode) {
		Element mediorder = doc.createElement("Mediorder"); //$NON-NLS-1$
		mediorder.setAttribute("barcodeLabel", barcode); //$NON-NLS-1$
		return mediorder;
	}
}
