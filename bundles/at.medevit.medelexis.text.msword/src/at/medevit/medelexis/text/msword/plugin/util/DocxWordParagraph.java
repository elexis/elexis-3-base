package at.medevit.medelexis.text.msword.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import ch.elexis.core.data.interfaces.text.ReplaceCallback;

public class DocxWordParagraph {
	Node paragraph;
	
	DocxWordParagraph(Node paragraph){
		this.paragraph = paragraph;
		tidyRuns();
// print();
	}
	
	private void tidyRuns(){
		// get a list of all runs and join runs with same format ...
		List<DocxWordRun> runs = createDirectChildRunList();
		DocxWordRun prevRun = null;
		for (DocxWordRun docxWordRun : runs) {
			if (prevRun != null && !docxWordRun.containsLineBreak() && !prevRun.containsLineBreak()
				&& !prevRun.isFollowedBySdt()) {
				// compare properties ...
				if (!docxWordRun.containsTab() && !docxWordRun.conatinsField()
					&& !docxWordRun.containsInstrText() && prevRun.getProperties() != null
					&& docxWordRun.getProperties() != null
					&& prevRun.getProperties().equals(docxWordRun.getProperties())) {
					prevRun.setText(prevRun.getText() + docxWordRun.getText());
					// keep the is followed by sdt state
					prevRun.setIsFollowedBySdt(docxWordRun.isFollowedBySdt());
					removeRun(docxWordRun);
					continue;
				}
			}
			prevRun = docxWordRun;
		}
	}
	
	public boolean findAndReplaceAll(String regex, ReplaceCallback cb){
		DocxWordSlidingSearchAndReplace slider =
			new DocxWordSlidingSearchAndReplace(this, regex, cb);
		
		return slider.findAndReplaceAll();
	}
	
	public boolean contains(String search){
		DocxWordSlidingSearchAndReplace slider = new DocxWordSlidingSearchAndReplace(this, search);
		
		return slider.contains();
	}
	
	public boolean findAndReplaceAll(String regex, String replace){
		DocxWordSlidingSearchAndReplace slider =
			new DocxWordSlidingSearchAndReplace(this, regex, replace);
		
		return slider.findAndReplaceAll();
	}
	
	public List<DocxWordRun> getDirectChildRuns(){
		ArrayList<DocxWordRun> ret = new ArrayList<DocxWordRun>();
		// search all directly contained <w:r> with a containing <w:t> node
		List<Node> wrNodes = XMLUtil.getChildElementsByTagName(paragraph, "w:r"); //$NON-NLS-1$
		for (Node node : wrNodes) {
			ret.add(new DocxWordRun(node));
		}
		return ret;
	}
	
	private List<DocxWordRun> createDirectChildRunList(){
		ArrayList<DocxWordRun> ret = new ArrayList<DocxWordRun>();
		// search all directly contained <w:r> with a containing <w:t> node
		List<Node> nodes = XMLUtil.getChildElements(paragraph); //$NON-NLS-1$
		DocxWordRun lastRun = null;
		for (Node node : nodes) {
			if (node.getNodeName().equalsIgnoreCase("w:r")) {
				lastRun = new DocxWordRun(node);
				ret.add(lastRun);
			} else if (node.getNodeName().equalsIgnoreCase("w:sdt")) {
				if (lastRun != null) {
					lastRun.setIsFollowedBySdt(true);
				}
			}
		}
		return ret;
	}

	public void removeRun(DocxWordRun run){
		paragraph.removeChild(run.getNode());
	}
	
	protected void insertParagraphAfter(DocxWordParagraph para){
		Node parent = paragraph.getParentNode();
		parent.insertBefore(para.getNode(), paragraph.getNextSibling());
	}
	
	protected DocxWordParagraph getClone(boolean deep){
		return new DocxWordParagraph(paragraph.cloneNode(deep));
	}
	
	public DocxWordRun createRun(){
		Node node = paragraph.getOwnerDocument().createElement("w:r"); //$NON-NLS-1$
		paragraph.appendChild(node);
		return new DocxWordRun(node);
	}
	
	private void print(){
		System.out.println("PARAGRAPH TEXT:"); //$NON-NLS-1$
		System.out.println(getTextOfParagraph());
	}
	
	protected String getTextOfParagraph(){
		StringBuilder sb = new StringBuilder();
		// get all direct text elements
		List<Node> wrNodes = XMLUtil.getChildElementsByTagName(paragraph, "w:r"); //$NON-NLS-1$
		for (Node node : wrNodes) {
			List<Node> wtNodes = XMLUtil.getChildElementsByTagName(node, "w:t"); //$NON-NLS-1$
			for (Node text : wtNodes) {
				String value = text.getTextContent();
				if (value != null && !value.isEmpty()) {
					sb.append(value);
				}
			}
		}
		return sb.toString();
	}
	
	public Node getNode(){
		return paragraph;
	}
	
	public DocxWordTable replaceWithTable(){
		// create table
		Node table = paragraph.getOwnerDocument().createElement("w:tbl"); //$NON-NLS-1$
		// replace
		Node parent = paragraph.getParentNode();
		parent.insertBefore(table, paragraph);
		parent.removeChild(paragraph);
		
		DocxWordTable ret = new DocxWordTable(table);
		return ret;
	}
	
	public DocxWordParagraphProperties getProperties(){
		ArrayList<DocxWordRun> ret = new ArrayList<DocxWordRun>();
		// search all directly contained <w:r> with a containing <w:t> node
		List<Node> wpPrNodes = XMLUtil.getChildElementsByTagName(paragraph, "w:pPr"); //$NON-NLS-1$
		if (!wpPrNodes.isEmpty())
			return new DocxWordParagraphProperties(wpPrNodes.get(0));
		return null;
	}
	
	public void removeProperties(DocxWordParagraphProperties prop){
		paragraph.removeChild(prop.properties);
	}
	
	public void setProperties(DocxWordParagraphProperties prop){
		DocxWordParagraphProperties currentProp = getProperties();
		if (currentProp != null) {
			paragraph.insertBefore(prop.properties, currentProp.properties);
			removeProperties(getProperties());
		} else {
			paragraph.appendChild(prop.properties);
		}
	}
}
