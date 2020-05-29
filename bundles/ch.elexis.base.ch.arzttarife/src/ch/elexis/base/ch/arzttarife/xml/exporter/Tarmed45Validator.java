package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Tarmed45Validator {
	
	private Validator validator;
	
	public List<String> validateRequest(InputStream request){
		if (validator == null) {
			try {
				validator = initValidator();
			} catch (SAXException e) {
				LoggerFactory.getLogger(getClass()).error("Error creating validator", e);
				throw new IllegalStateException("Error creating validator");
			}
		}
		return validate(new StreamSource(request));
	}
	
	private Validator initValidator() throws SAXException{
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema();
		schema = factory.newSchema(new Source[] {
			new StreamSource(Tarmed45Validator.class.getResourceAsStream("/rsc/xenc-schema.xsd")),
			new StreamSource(
				Tarmed45Validator.class.getResourceAsStream("/rsc/xmldsig-core-schema.xsd")),
			new StreamSource(
				Tarmed45Validator.class.getResourceAsStream("/rsc/generalInvoiceRequest_450.xsd"))
		});
		return schema.newValidator();
	}
	
	private List<String> validate(Source source){
		MyErrorHandler errorHandler = new MyErrorHandler();
		try {
			
			validator.setErrorHandler(errorHandler);
			validator.validate(source);
		} catch (Exception ex) {
			errorHandler.exception(ex);
		}
		return errorHandler.getMessageList();
	}
	
	private static class MyErrorHandler implements ErrorHandler {
		public List<Exception> exceptions = new ArrayList<>();
		
		@Override
		public void error(SAXParseException exception) throws SAXException{
			exceptions.add(exception);
		}
		
		@Override
		public void fatalError(SAXParseException exception) throws SAXException{
			exceptions.add(exception);
		}
		
		@Override
		public void warning(SAXParseException exception) throws SAXException{
			// Nothing
		}
		
		public void exception(Exception exception){
			// Nothing this is not an xml related error
		}
		
		public List<String> getMessageList(){
			List<String> messageList = new ArrayList<>();
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
