package at.medevit.medelexis.text.msword.plugin.util;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordTableProperties {

	Node properties;
	Node widthNode;

	Node borderNode;
	Node borderTop;
	Node borderLeft;
	Node borderRight;
	Node borderBottom;
	Node borderInsideH;
	Node borderInsideV;
	
	public DocxWordTableProperties(Node node){
		this.properties = node;
	}
	
	public void setWidth(int percent){
		Node width = getWidthNode();
		if (width == null)
			width = createWidthNode();
		XMLUtil.setAttribute((Element) width, "w:w", Integer.toString(percent * 50)); //$NON-NLS-1$
		XMLUtil.setAttribute((Element) width, "w:type", "pct"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private Node getWidthNode(){
		if (widthNode == null) {
			List<Node> tblWNodes = XMLUtil.getChildElementsByTagName(properties, "w:tblW"); //$NON-NLS-1$
			if (!tblWNodes.isEmpty())
				widthNode = tblWNodes.get(0);
		}
		return widthNode;
	}
	
	private Node createWidthNode(){
		widthNode = properties.getOwnerDocument().createElement("w:tblW"); //$NON-NLS-1$
		properties.appendChild(widthNode);
		return widthNode;
	}
	
	private Node getBorderNode(){
		if (borderNode == null) {
			List<Node> tblBordersNodes =
				XMLUtil.getChildElementsByTagName(properties, "w:tblBorders"); //$NON-NLS-1$
			if (!tblBordersNodes.isEmpty()) {
				borderNode = tblBordersNodes.get(0);
				// get the border nodes
				List<Node> nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:top"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderTop = nodes.get(0);
				nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:left"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderLeft = nodes.get(0);
				nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:bottom"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderBottom = nodes.get(0);
				nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:right"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderRight = nodes.get(0);
				nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:insideH"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderInsideH = nodes.get(0);
				nodes = XMLUtil.getChildElementsByTagName(borderNode, "w:insideV"); //$NON-NLS-1$
				if (!nodes.isEmpty())
					borderInsideV = nodes.get(0);
			}
		}
		return borderNode;
	}

	private Node createBorderNode(){
		borderNode = properties.getOwnerDocument().createElement("w:tblBorders"); //$NON-NLS-1$
		properties.appendChild(borderNode);
		
		// create the nodes for the borders
		borderTop = properties.getOwnerDocument().createElement("w:top"); //$NON-NLS-1$
		borderNode.appendChild(borderTop);
		borderLeft = properties.getOwnerDocument().createElement("w:left"); //$NON-NLS-1$
		borderNode.appendChild(borderLeft);
		borderBottom = properties.getOwnerDocument().createElement("w:bottom"); //$NON-NLS-1$
		borderNode.appendChild(borderBottom);
		borderRight = properties.getOwnerDocument().createElement("w:right"); //$NON-NLS-1$
		borderNode.appendChild(borderRight);
		borderInsideH = properties.getOwnerDocument().createElement("w:insideH"); //$NON-NLS-1$
		borderNode.appendChild(borderInsideH);
		borderInsideV = properties.getOwnerDocument().createElement("w:insideV"); //$NON-NLS-1$
		borderNode.appendChild(borderInsideV);

		return borderNode;
	}

	public void setAllBorders(int i){
		Node borders = getBorderNode();
		if (borders == null)
			borders = createBorderNode();
		
		XMLUtil.setAttribute((Element) borderTop, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderTop, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
		
		XMLUtil.setAttribute((Element) borderLeft, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderLeft, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
		
		XMLUtil.setAttribute((Element) borderBottom, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderBottom, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
		
		XMLUtil.setAttribute((Element) borderRight, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderRight, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
		
		XMLUtil.setAttribute((Element) borderInsideH, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderInsideH, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
		
		XMLUtil.setAttribute((Element) borderInsideV, "w:val", "single"); //$NON-NLS-1$ //$NON-NLS-2$
		XMLUtil.setAttribute((Element) borderInsideV, "w:sz", Integer.toString(i * 4)); //$NON-NLS-1$
	}
}
