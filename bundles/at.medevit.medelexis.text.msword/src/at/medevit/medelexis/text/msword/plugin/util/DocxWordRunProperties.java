package at.medevit.medelexis.text.msword.plugin.util;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordRunProperties {
	Node properties;

	DocxWordRunProperties(Node node) {
		this.properties = node;
	}

	public int getFontSize() {
		List<Node> wszNodes = XMLUtil.getChildElementsByTagName(properties, "w:sz"); //$NON-NLS-1$
		if (!wszNodes.isEmpty()) {
			String strSize = XMLUtil.getAttribute((Element) wszNodes.get(0), "w:val"); //$NON-NLS-1$
			if (!strSize.isEmpty())
				return Integer.parseInt(strSize);
		}
		return -1;
	}

	public String getFont() {
		List<Node> wszNodes = XMLUtil.getChildElementsByTagName(properties, "w:rFonts"); //$NON-NLS-1$
		if (!wszNodes.isEmpty()) {
			String strFont = XMLUtil.getAttribute((Element) wszNodes.get(0), "w:cs"); //$NON-NLS-1$
			if (!strFont.isEmpty()) {
				return strFont;
			}
		}
		return StringUtils.EMPTY;
	}

	public String getColor() {
		List<Node> wszNodes = XMLUtil.getChildElementsByTagName(properties, "w:color"); //$NON-NLS-1$
		if (!wszNodes.isEmpty()) {
			String strColor = XMLUtil.getAttribute((Element) wszNodes.get(0), "w:val"); //$NON-NLS-1$
			if (!strColor.isEmpty()) {
				return strColor;
			}
		}
		return StringUtils.EMPTY;
	}

	public boolean isBold() {
		List<Node> wszNodes = XMLUtil.getChildElementsByTagName(properties, "w:b"); //$NON-NLS-1$
		return !wszNodes.isEmpty();
	}

	public void setBold(boolean value) {
		List<Node> wbNodes = XMLUtil.getChildElementsByTagName(properties, "w:b"); //$NON-NLS-1$
		if (value) {
			if (wbNodes.isEmpty()) {
				Element node = properties.getOwnerDocument().createElement("w:b"); //$NON-NLS-1$
				properties.appendChild(node);
			}
		} else {
			if (!wbNodes.isEmpty()) {
				properties.removeChild(wbNodes.get(0));
			}
		}
	}

	public DocxWordRunProperties getClone(boolean deep) {
		return new DocxWordRunProperties(properties.cloneNode(deep));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DocxWordRunProperties) {
			DocxWordRunProperties other = (DocxWordRunProperties) obj;
			if (other.getColor().equals(getColor()) && other.getFont().equals(getFont())
					&& other.getFontSize() == getFontSize() && other.isBold() == isBold()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(getColor()).append(getFont()).append(getFontSize()).append(isBold());
		return sb.toString().hashCode();
	}
}
