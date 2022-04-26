package at.medevit.medelexis.text.msword.plugin.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocxWordText {

	Node text;

	public DocxWordText(Node textNode) {
		this.text = textNode;
	}

	public DocxWordRun setText(String string) {
		DocxWordRun ret = getParentRun();
		if (string == null)
			return ret;
		if (string.indexOf('\t') != -1) {
			ret = setTextWithTabs(string);
		} else {
			((Element) text).setTextContent(string);
		}
		return ret;
	}

	private DocxWordRun setTextWithTabs(String string) {
		DocxWordRun templateRun = getParentRun().getCloneOfRun(true);
		DocxWordRun currentRun = getParentRun();
		StringBuilder currentString = new StringBuilder();
		boolean tabInserted = false;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c != '\t') {
				// create new run for text after tab
				if (tabInserted) {
					DocxWordRun newRun = templateRun.getCloneOfRun(true);
					newRun.clear();
					newRun.createText();
					currentRun.insertRunAfter(newRun);
					currentRun = newRun;
					tabInserted = false;
				}
				currentString.append(c);
			} else {
				// insert text to current text
				currentRun.setText(currentString.toString());
				// insert a run with a tab inside
				DocxWordRun newRun = templateRun.getCloneOfRun(true);
				newRun.clear();
				newRun.insertTab();
				currentRun.insertRunAfter(newRun);
				currentRun = newRun;
				// set current text to null and clear the stringbuilder
				tabInserted = true;
				currentString.delete(0, currentString.length());
			}
		}
		String newText = currentString.toString();
		if (!newText.isEmpty() && !currentRun.containsTab()) {
			currentRun.setText(currentString.toString());
		}
		return currentRun;
	}

	public DocxWordRun getParentRun() {
		Node parent = text.getParentNode();
		while (parent != null && !parent.getNodeName().equals("w:r")) { //$NON-NLS-1$
			parent = parent.getParentNode();
		}
		if (parent != null)
			return new DocxWordRun(parent);
		else
			return null;
	}

	public Node getNode() {
		return text;
	}

	public Object getText() {
		return ((Element) text).getTextContent();
	}
}
