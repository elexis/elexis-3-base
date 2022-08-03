package ch.elexis.tarmed.printer;

import java.util.Comparator;

import org.jdom2.Element;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.rgw.tools.TimeTool;

public class RnComparator implements Comparator<Element> {
	TimeTool tt0 = new TimeTool();
	TimeTool tt1 = new TimeTool();

	@Override
	public int compare(Element e0, Element e1) {
		if (!tt0.set(e0.getAttributeValue("date_begin"))) {
			return 1;
		}
		if (!tt1.set(e1.getAttributeValue("date_begin"))) {
			return -1;
		}
		int dat = tt0.compareTo(tt1);
		if (dat != 0) {
			return dat;
		}
		String t0 = e0.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
		String t1 = e1.getAttributeValue(XMLExporter.ATTR_TARIFF_TYPE);
		if (t0.equals("001")) { // tarmed-tarmed: nach code sortieren
			if (t1.equals("001")) {
				String c0 = e0.getAttributeValue(XMLExporter.ATTR_CODE);
				String c1 = e1.getAttributeValue(XMLExporter.ATTR_CODE);
				return c0.compareTo(c1);
			} else {
				return -1; // tarmed immer oberhab nicht-tarmed
			}
		} else if (t1.equals("001")) {
			return 1; // nicht-tarmed immer unterhalb tarmed
		} else { // nicht-tarmed - nicht-tarmed: alphabetisch
			int diffc = t0.compareTo(t1);
			if (diffc == 0) {
				diffc = e0.getText().compareToIgnoreCase(e1.getText());
			}
			return diffc;
		}
	}
}
