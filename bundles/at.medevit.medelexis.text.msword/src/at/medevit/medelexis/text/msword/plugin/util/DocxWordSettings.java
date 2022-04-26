package at.medevit.medelexis.text.msword.plugin.util;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordSettings {
	Node settings;

	DocxWordSettings(Node settings) {
		this.settings = settings;
		// print();
	}

	// write <w:documentProtection w:edit="readOnly" w:enforcement="1" /> to
	// settings
	public boolean isReadOnly() {
		List<Node> protectionElements = XMLUtil.getChildElementsByTagName(settings, "w:documentProtection");
		if (protectionElements != null && !protectionElements.isEmpty()) {
			for (Node node : protectionElements) {
				String edit = XMLUtil.getAttribute((Element) node, "w:edit");
				String enforce = XMLUtil.getAttribute((Element) node, "w:enforcement");
				if (edit != null && enforce != null) {
					return edit.equalsIgnoreCase("readOnly") && enforce.equalsIgnoreCase("1");
				}
			}
		}
		return false;
	}

	public void setReadOnly(boolean value) {
		if (value && !isReadOnly()) {
			Node node = settings.getOwnerDocument().createElement("w:documentProtection"); //$NON-NLS-1$
			XMLUtil.setAttribute((Element) node, "w:edit", "readOnly");
			XMLUtil.setAttribute((Element) node, "w:enforcement", "1");
			settings.appendChild(node);
		} else if (!value && isReadOnly()) {
			List<Node> protectionElements = XMLUtil.getChildElementsByTagName(settings, "w:documentProtection");
			if (protectionElements != null && !protectionElements.isEmpty()) {
				for (Node node : protectionElements) {
					settings.removeChild(node);
				}
			}
		}
	}
}
