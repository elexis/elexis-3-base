package ch.medshare.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class UtilXml {
	static final String JAXP_SCHEMA_LANGUAGE =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage"; //$NON-NLS-1$
	
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource"; //$NON-NLS-1$
	
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
	
	public static List<String> validateSchema(String xmlDocumentUrl){
		return validateSchema(null, xmlDocumentUrl);
	}
	
	public static List<String> validateSchema(String schemaUrl, String xmlDocumentUrl){
		MyErrorHandler errorHandler = new MyErrorHandler();
		try {
			// 1. Lookup a factory for the W3C XML Schema language
			SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA);
			
			// 2. Compile the schema.
			// Here the schema is loaded from a java.io.File, but you could use
			// a java.net.URL or a javax.xml.transform.Source instead.
			Schema schema = factory.newSchema();
			if (schemaUrl != null) {
				File schemaLocation = new File(schemaUrl);
				schema = factory.newSchema(schemaLocation);
			}
			
			// 3. Get a validator from the schema.
			Validator validator = schema.newValidator();
			
			// 4. Parse the document you want to check.
			Source source = new StreamSource(xmlDocumentUrl);
			
			// 5. Check the document
			validator.setErrorHandler(errorHandler);
			validator.validate(source);
		} catch (Exception ex) {
			errorHandler.exception(ex);
		}
		return errorHandler.getMessageList();
	}
	
	private static class MyErrorHandler implements ErrorHandler {
		public List<Exception> exceptions = new Vector<Exception>();
		
		public void error(SAXParseException exception) throws SAXException{
			exceptions.add(exception);
		}
		
		public void fatalError(SAXParseException exception) throws SAXException{
			exceptions.add(exception);
		}
		
		public void warning(SAXParseException exception) throws SAXException{
		// Nothing
		}
		
		public void exception(Exception exception){
			exceptions.add(exception);
		}
		
		public List<String> getMessageList(){
			List<String> messageList = new Vector<String>();
			for (Exception ex : exceptions) {
				String msg = ex.getMessage();
				if (msg == null || msg.length() == 0) {
					msg = ex.toString();
				}
				messageList.add(msg);
			}
			return messageList;
		}
	}
}
