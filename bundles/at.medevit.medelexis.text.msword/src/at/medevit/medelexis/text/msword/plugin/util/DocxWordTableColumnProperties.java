package at.medevit.medelexis.text.msword.plugin.util;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordTableColumnProperties {
	
	Node properties;

	public DocxWordTableColumnProperties(Node node){
		this.properties = node;
	}
	
	public void setWidth(int percent){
		Node width = getWidth();
		if (width == null)
			width = createWidth();
		XMLUtil.setAttribute((Element) width, "w:w", Integer.toString(percent * 50)); //$NON-NLS-1$
		XMLUtil.setAttribute((Element) width, "w:type", "pct"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private Node getWidth(){
		List<Node> widths = XMLUtil.getChildElementsByTagName(properties, "w:tblW"); //$NON-NLS-1$
		if (widths.isEmpty())
			return null;
		else
			return widths.get(0);
	}
	
	private Node createWidth(){
		Node node = properties.getOwnerDocument().createElement("w:tblW"); //$NON-NLS-1$
		properties.appendChild(node);
		return node;
	}
}
