package at.medevit.medelexis.text.msword.plugin.util;

import java.util.List;

import org.w3c.dom.Node;

public class DocxWordParagraphProperties {
	Node properties;
	Node runProperties;

	public DocxWordParagraphProperties(Node node) {
		properties = node;
	}

	public int getFontSize() {
		DocxWordRunProperties rProp = new DocxWordRunProperties(getRunPropertiesNode());
		return rProp.getFontSize();
	}

	private Node getRunPropertiesNode() {
		if (runProperties == null) {
			List<Node> wrPrNodes = XMLUtil.getChildElementsByTagName(properties, "w:rPr"); //$NON-NLS-1$
			if (!wrPrNodes.isEmpty()) {
				runProperties = wrPrNodes.get(0);
			}
		}
		return runProperties;
	}

	public DocxWordRunProperties getRunProperties() {
		return new DocxWordRunProperties(getRunPropertiesNode());
	}

	public DocxWordParagraphProperties getClone(boolean deep) {
		return new DocxWordParagraphProperties(properties.cloneNode(deep));
	}
}
