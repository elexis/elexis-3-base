package at.medevit.medelexis.text.msword.plugin.util;

import java.util.List;

import org.w3c.dom.Node;

public class DocxWordTableColumn {
	
	Node column;

	public DocxWordTableColumn(Node node){
		this.column = node;
	}
	
	public DocxWordTableColumnProperties createProperties(){
		Node node = column.getOwnerDocument().createElement("w:tcPr"); //$NON-NLS-1$
		column.appendChild(node);
		return new DocxWordTableColumnProperties(node);
	}
	
	public DocxWordParagraph createParagraph(){
		Node node = column.getOwnerDocument().createElement("w:p"); //$NON-NLS-1$
		column.appendChild(node);
		return new DocxWordParagraph(node);
	}
	
	public DocxWordParagraph getParagraph(){
		List<Node> nodes = XMLUtil.getChildElementsByTagName(column, "w:p"); //$NON-NLS-1$
		if (!nodes.isEmpty())
			return new DocxWordParagraph(nodes.get(0));
		else
			return null;
	}
}
