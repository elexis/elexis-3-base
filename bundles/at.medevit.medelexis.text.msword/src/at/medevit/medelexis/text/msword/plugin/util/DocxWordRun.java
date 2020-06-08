package at.medevit.medelexis.text.msword.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordRun {
	private Node run;
	private boolean isFollowedBySdt;
	
	public DocxWordRun(Node runNode){
		this.run = runNode;
	}
	
	public List<DocxWordText> getDirectChildText(){
		ArrayList<DocxWordText> ret = new ArrayList<DocxWordText>();
		List<Node> wtNodes = XMLUtil.getChildElementsByTagName(run, "w:t"); //$NON-NLS-1$
		for (Node node : wtNodes) {
			ret.add(new DocxWordText(node));
		}
		return ret;
	}
	
	public void insertRunAfter(DocxWordRun insert){
		run.getParentNode().insertBefore(insert.getNode(), run.getNextSibling());
	}
	
	public void insertRunBefore(DocxWordRun insert){
		run.getParentNode().insertBefore(insert.getNode(), run);
	}
	
	public boolean isContainingText(){
		List<Node> wtNodes = XMLUtil.getChildElementsByTagName(run, "w:t"); //$NON-NLS-1$
		return !wtNodes.isEmpty();
	}
	
	public Node getNode(){
		return run;
	}
	
	public String getText(){
		StringBuilder sb = new StringBuilder();
		List<DocxWordText> texts = getDirectChildText();
		for (DocxWordText text : texts) {
			sb.append(text.getText());
		}
		return sb.toString();
	}
	
	public DocxWordRun setText(String newText){
		DocxWordRun ret = this;
		List<DocxWordText> texts = getDirectChildText();
		if (texts.isEmpty()) {
			texts.add(createText());
		}
		for (int i = 0; i < texts.size(); i++) {
			if (i == 0) {
				if (newText.indexOf('\r') != -1) {
					ret = setTextWithLineBreaks(newText);
				} else {
					ret = texts.get(i).setText(newText);
				}
			} else {
				removeText(texts.get(i));
			}
		}
		return ret;
	}
	
	public DocxWordText createText(){
		Node node = run.getOwnerDocument().createElement("w:t"); //$NON-NLS-1$
		run.appendChild(node);
		return new DocxWordText(node);
	}
	
	public DocxWordParagraph getParentParagraph(){
		Node parent = run.getParentNode();
		while (parent != null && !parent.getNodeName().equals("w:p")) { //$NON-NLS-1$
			parent = parent.getParentNode();
		}
		if (parent != null)
			return new DocxWordParagraph(parent);
		else
			return null;
	}
	
	protected DocxWordRun getCloneOfRun(boolean deep){
		return new DocxWordRun(run.cloneNode(deep));
	}
	
	private DocxWordRun setTextWithLineBreaks(String newText){
		DocxWordRun currentRun = this;
		String[] lines = newText.split("\\r"); //$NON-NLS-1$
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				// write text to current run
				currentRun = currentRun.setText(lines[i]);
			} else {
				// create new run for the next text line
				DocxWordRun breakRun = getCloneOfRun(true);
				breakRun.clear();
				breakRun.insertLineBreak();
				currentRun.insertRunAfter(breakRun);
				
				DocxWordRun newRun = getCloneOfRun(true);
				newRun.clear();
				newRun.createText();
				breakRun.insertRunAfter(newRun);
				currentRun = newRun.setText(lines[i]);
			}
		}
		return currentRun;
	}
	
	public void removeText(DocxWordText text){
		run.removeChild(text.getNode());
	}
	
	public void removeAllText(){
		List<DocxWordText> texts = getDirectChildText();
		for (DocxWordText docxWordText : texts) {
			removeText(docxWordText);
		}
	}
	
	public void insertTab(){
		Document document = run.getOwnerDocument();
		Element tab = document.createElement("w:tab"); //$NON-NLS-1$
		run.appendChild(tab);
	}
	
	public void removeAllTab(){
		List<Node> wrTabNodes = XMLUtil.getChildElementsByTagName(run, "w:tab"); //$NON-NLS-1$
		for (Node node : wrTabNodes) {
			run.removeChild(node);
		}
	}
	
	public void removeAllLineBreak(){
		List<Node> wrBrNodes = XMLUtil.getChildElementsByTagName(run, "w:br"); //$NON-NLS-1$
		for (Node node : wrBrNodes) {
			run.removeChild(node);
		}
	}
	
	public void insertLineBreak(){
		Document document = run.getOwnerDocument();
		Element lineBr = document.createElement("w:br"); //$NON-NLS-1$
		run.appendChild(lineBr);
	}
	
	public void clear(){
		removeAllText();
		removeAllLineBreak();
		removeAllTab();
	}
	
	public DocxWordRunProperties getProperties(){
		List<Node> wrPrNodes = XMLUtil.getChildElementsByTagName(run, "w:rPr"); //$NON-NLS-1$
		if (!wrPrNodes.isEmpty())
			return new DocxWordRunProperties(wrPrNodes.get(0));
		return null;
	}
	
	public void removeProperties(DocxWordRunProperties prop){
		run.removeChild(prop.properties);
	}
	
	public void setProperties(DocxWordRunProperties prop){
		DocxWordRunProperties currentProp = getProperties();
		if (currentProp != null) {
			run.insertBefore(prop.properties, currentProp.properties);
			removeProperties(getProperties());
		} else {
			run.appendChild(prop.properties);
		}
	}
	
	public DocxWordRunProperties createProperties(){
		Node node = run.getOwnerDocument().createElement("w:rPr"); //$NON-NLS-1$
		run.appendChild(node);
		return new DocxWordRunProperties(node);
	}
	
	public boolean containsTab(){
		List<Node> wTab = XMLUtil.getChildElementsByTagName(run, "w:tab"); //$NON-NLS-1$
		return !wTab.isEmpty();
	}
	
	public boolean conatinsField(){
		List<Node> wField = XMLUtil.getChildElementsByTagName(run, "w:fldChar"); //$NON-NLS-1$
		return !wField.isEmpty();
	}
	
	public boolean containsLineBreak(){
		List<Node> wTab = XMLUtil.getChildElementsByTagName(run, "w:br"); //$NON-NLS-1$
		return !wTab.isEmpty();
	}
	
	public boolean containsInstrText(){
		List<Node> wInstr = XMLUtil.getChildElementsByTagName(run, "w:instrText"); //$NON-NLS-1$
		return !wInstr.isEmpty();
	}
	
	public void setIsFollowedBySdt(boolean value){
		isFollowedBySdt = value;
	}
	
	public boolean isFollowedBySdt(){
		return isFollowedBySdt;
	}
}
