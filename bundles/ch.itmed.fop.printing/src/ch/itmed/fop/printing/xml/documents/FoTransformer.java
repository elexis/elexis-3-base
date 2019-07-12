package ch.itmed.fop.printing.xml.documents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;

public class FoTransformer {
	private static Logger logger = LoggerFactory.getLogger(FoTransformer.class);

	/**
	 * Creates an FO file and returns it as an InputStream.
	 * 
	 * @param inputStream
	 * @return
	 */
	public static InputStream transformXmlToFo(InputStream xmlInputStream, File xslFile) throws Exception {

		if (xslFile.exists() == false) {
			logger.error("XSL template " + xslFile.getAbsolutePath() + " not found");
			SWTHelper.showError("Druck fehlgeschlagen",
					"Die Vorlage " + xslFile.toString() + " konnte nicht gefunden werden.");
			return null;
		}

		if (xmlInputStream == null) {
			logger.error("Failed to create XML file");
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// Setup XSLT
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

		// Setup input for XSLT transformation
		Source src = new StreamSource(xmlInputStream);

		// Resulting SAX events (the generated FO) must be piped through to FOP
		Result res = new StreamResult(out);

		// Start XSLT transformation and FOP processing
		transformer.transform(src, res);
		return new ByteArrayInputStream(out.toByteArray());
	}

}
