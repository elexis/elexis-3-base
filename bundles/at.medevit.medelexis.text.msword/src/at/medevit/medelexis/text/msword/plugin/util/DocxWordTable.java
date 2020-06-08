package at.medevit.medelexis.text.msword.plugin.util;

import org.w3c.dom.Node;

public class DocxWordTable {
	Node table;
	
	DocxWordTable(Node node){
		this.table = node;
// print();
	}
	
	public DocxWordTableProperties createProperties(){
		Node node = table.getOwnerDocument().createElement("w:tblPr"); //$NON-NLS-1$
		table.appendChild(node);
		return new DocxWordTableProperties(node);
	}
	
	public DocxWordTableGrid createGrid(){
		Node node = table.getOwnerDocument().createElement("w:tblGrid"); //$NON-NLS-1$
		table.appendChild(node);
		return new DocxWordTableGrid(node);
	}
	
	public DocxWordTableRow createRow(){
		Node node = table.getOwnerDocument().createElement("w:tr"); //$NON-NLS-1$
		table.appendChild(node);
		return new DocxWordTableRow(node);
	}
}
