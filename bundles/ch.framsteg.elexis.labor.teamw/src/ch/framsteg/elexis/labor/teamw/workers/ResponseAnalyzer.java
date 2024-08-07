package ch.framsteg.elexis.labor.teamw.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ResponseAnalyzer {

	private Document doc;
	private boolean responseValid;

	Logger logger = LoggerFactory.getLogger(ResponseAnalyzer.class);

	public ResponseAnalyzer(Document doc) {
		setDoc(doc);
		checkStatus();
	}

	private void checkStatus() {
		boolean valid = false;
		NodeList urlNode = doc.getElementsByTagName("url");
		NodeList errorNode = doc.getElementsByTagName("error");

		if (urlNode.item(0) != null) {
			logger.info("URL length: " + urlNode.item(0).getTextContent().length());
			valid = urlNode.item(0).getTextContent().length() > 0 ? true : false;
			setResponseValid(valid);
		}
		if (errorNode.item(0) != null) {
			logger.info("Error length: " + errorNode.item(0).getTextContent().length());
		}
	}

	public String getResult() {
		return isResponseValid() ? getUrl() : getError();
	}

	private String getUrl() {
		return doc.getElementsByTagName("url").item(0).getTextContent();
	}

	private String getError() {
		return doc.getElementsByTagName("error").item(0).getTextContent();
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public boolean isResponseValid() {
		return responseValid;
	}

	public void setResponseValid(boolean responseValid) {
		this.responseValid = responseValid;
	}

}
