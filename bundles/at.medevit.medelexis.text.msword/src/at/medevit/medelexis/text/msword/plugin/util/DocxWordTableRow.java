package at.medevit.medelexis.text.msword.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class DocxWordTableRow {
	
	Node row;
	
	public DocxWordTableRow(Node node){
		this.row = node;
	}
	
	public DocxWordTableColumn createTableColumn(){
		Node node = row.getOwnerDocument().createElement("w:tc"); //$NON-NLS-1$
		row.appendChild(node);
		return new DocxWordTableColumn(node);
	}

	public List<DocxWordTableColumn> getColumns(){
		ArrayList<DocxWordTableColumn> ret = new ArrayList<DocxWordTableColumn>();
		
		List<Node> nodes = XMLUtil.getChildElementsByTagName(row, "w:tc"); //$NON-NLS-1$
		for (Node node : nodes) {
			ret.add(new DocxWordTableColumn(node));
		}
		return ret;
	}

}
