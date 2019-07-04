package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.ArticlesElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public class ArticleLabel {

	/**
	 * Creates the XML file and returns it as an InputStream.
	 * 
	 * @return The generated XML as an InputStream
	 */
	public static InputStream create() throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.ARTICLE_LABEL);
		doc.appendChild(page);
		Element patient = PatientElement.create(doc, false);
		page.appendChild(patient);
		Element articles = ArticlesElement.create(doc);
		page.appendChild(articles);

		return DomDocument.toInputStream(doc);
	}

}
