package at.medevit.elexis.tarmed.model;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import ch.fd.invoice440.request.RequestType;
import ch.fd.invoice440.response.ResponseType;

public class TarmedJaxbUtil {
	private static Logger log = LoggerFactory.getLogger(TarmedJaxbUtil.class);
	
	/**
	 * creates an invoice request xml (XML4.0 or XML4.4 - depends on given RequestType)
	 * 
	 * @param request
	 *            RequestType to write to the xml
	 * @param outStream
	 *            of the file to write to
	 * @return true if success, false if an exception occurred
	 */
	public static boolean marshallInvoiceRequest(ch.fd.invoice400.request.RequestType request,
		OutputStream outStream){
		try {
			JAXBContext jaxbContext =
				JAXBContext.newInstance(ch.fd.invoice400.request.RequestType.class);
			Marshaller marshaller =
				initMarshaller(jaxbContext, Constants.INVOICE_REQUEST_400_LOCATION);
			marshaller.marshal(request, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling MDInvoiceRequest_400 file failed", e);
			return false;
		}
	}
	
	/**
	 * creates an invoice request xml (XML4.0 or XML4.4 - depends on given RequestType)
	 * 
	 * @param request
	 *            RequestType to write to the xml
	 * @param outStream
	 *            of the file to write to
	 * @return true if success, false if an exception occurred
	 */
	public static boolean marshallInvoiceRequest(RequestType request, OutputStream outStream){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RequestType.class);
			Marshaller marshaller =
				initMarshaller(jaxbContext, Constants.INVOICE_REQUEST_440_LOCATION);
			marshaller.marshal(request, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling generalInvoiceRequest_440 failed", e);
			return false;
		}
	}
	
	/**
	 * creates an invoice response xml (XML4.0 or XML4.4 - depends on given ResponseType)
	 * 
	 * @param response
	 *            ResponeType object to write to the xml
	 * @param outStream
	 *            of the file to write to
	 * @return true if success, false if an exception occurred
	 */
	public static boolean marshallInvoiceResponse(ch.fd.invoice400.response.ResponseType response,
		OutputStream outStream){
		try {
			JAXBContext jaxbContext =
				JAXBContext.newInstance(ch.fd.invoice400.response.ResponseType.class);
			Marshaller marshaller =
				initMarshaller(jaxbContext, Constants.INVOICE_RESPONSE_400_LOCATION);
			marshaller.marshal(response, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling MDInvoiceResponse_400 file failed", e);
			return false;
		}
	}
	
	/**
	 * creates an invoice response xml (XML4.0 or XML4.4 - depends on given ResponseType)
	 * 
	 * @param response
	 *            ResponeType object to write to the xml
	 * @param outStream
	 *            of the file to write to
	 * @return true if success, false if an exception occurred
	 */
	public static boolean marshallInvoiceResponse(ResponseType response, OutputStream outStream){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResponseType.class);
			Marshaller marshaller =
				initMarshaller(jaxbContext, Constants.INVOICE_RESPONSE_440_LOCATION);
			marshaller.marshal(response, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling generalInvoiceResponse_440 failed", e);
			return false;
		}
	}
	
	/**
	 * initializes a marshaller for the given {@link JAXBContext} and sets default properties
	 * 
	 * @param jaxbContext
	 * @param schemaLocation
	 *            location of schema for the XML
	 * @return {@link Marshaller} marshaller
	 * @throws JAXBException
	 *             if creating marshaller from jaxbContext failed
	 */
	private static Marshaller initMarshaller(JAXBContext jaxbContext, String schemaLocation)
		throws JAXBException{
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		try {
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
			marshaller.setProperty(Constants.JAXB_HEADER_KEY, Constants.DEFAULT_HEADER);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			return marshaller;
		} catch (PropertyException propE) {
			log.error("Error setting marshall properties - concerns XML with schema ["
				+ schemaLocation + "]", propE);
		}
		return marshaller;
	}
	
	/**
	 * loads elements of a XML4.0 invoice request file into java objects
	 * 
	 * @param inStream
	 *            of an invoice request file (base on {@link http://www.xmlData.ch/xmlInvoice/XSD
	 *            MDInvoiceRequest_400.xsd})
	 * @return {@link ch.fd.invoice400.request.RequestType} request root element containing all the
	 *         child objects or null if unable to resolve
	 */
	public static ch.fd.invoice400.request.RequestType unmarshalInvoiceRequest400(
		InputStream inStream){
		try {
			JAXBContext jaxbContext =
				JAXBContext.newInstance(ch.fd.invoice400.request.RequestType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object unmarshalObj = unmarshaller.unmarshal(inStream);
			
			if (unmarshalObj instanceof ch.fd.invoice400.request.RequestType) {
				ch.fd.invoice400.request.RequestType request =
					(ch.fd.invoice400.request.RequestType) unmarshalObj;
				return request;
			}
		} catch (JAXBException e) {
			log.error("Unmarshalling MDInvoiceRequest_400 file failed", e);
		}
		return null;
	}
	
	/**
	 * loads elements from a XML4.4 request file into java objects
	 * 
	 * @param inStream
	 *            of an inovice response file (based on {@link www.forum-datenaustausch.ch/invoice
	 *            generalInvoiceRequest_440.xsd}
	 * @return {@link RequestType} request root element containing the child objects or null if
	 *         unable to resolve
	 */
	@SuppressWarnings("unchecked")
	public static RequestType unmarshalInvoiceRequest440(InputStream inStream){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RequestType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<Object> jaxElement = (JAXBElement<Object>) unmarshaller.unmarshal(inStream);
			
			if (jaxElement.getValue() instanceof RequestType) {
				RequestType request = (RequestType) jaxElement.getValue();
				return request;
			}
		} catch (JAXBException e) {
			log.error("Unmarshalling generalInvoiceRequest_440 failed", e);
		}
		return null;
	}
	
	/**
	 * loads elements from a XML4.0 response file into java objects
	 * 
	 * @param inStream
	 *            of an invoice response file (base on {@link http://www.xmlData.ch/xmlInvoice/XSD
	 *            MDInvoiceResponse_400.xsd})
	 * @return {@link ch.fd.invoice400.response.ResponseType} response root element containing the
	 *         child objects or null if unable to resolve
	 */
	public static ch.fd.invoice400.response.ResponseType unmarshalInvoiceResponse400(
		InputStream inStream){
		try {
			JAXBContext jaxbContext =
				JAXBContext.newInstance(ch.fd.invoice400.response.ResponseType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object unmarshalObj = unmarshaller.unmarshal(inStream);
			
			if (unmarshalObj instanceof ch.fd.invoice400.response.ResponseType) {
				ch.fd.invoice400.response.ResponseType response =
					(ch.fd.invoice400.response.ResponseType) unmarshalObj;
				return response;
			}
		} catch (JAXBException e) {
			log.error("Unmarshalling MDInvoiceResponse_400 file failed", e);
		}
		return null;
	}
	
	/**
	 * loads elements from a XML4.4 response file into java objects
	 * 
	 * @param inStream
	 *            of an invoce response file (base on {@link www.forum-datenaustausch.ch/invoice
	 *            generalInvoiceResponse_440.xsd})
	 * @return {@link ResponseType} response root element containing the child objects or null if
	 *         unable to resolve
	 */
	@SuppressWarnings("unchecked")
	public static ResponseType unmarshalInvoiceResponse440(InputStream inStream){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResponseType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<Object> jaxElement = (JAXBElement<Object>) unmarshaller.unmarshal(inStream);
			
			if (jaxElement.getValue() instanceof ResponseType) {
				ResponseType response = (ResponseType) jaxElement.getValue();
				return response;
			}
		} catch (JAXBException e) {
			log.error("Unmarshalling generalInvoiceResponse_440 failed", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static RequestType unmarshalInvoiceRequest440(org.jdom.Document jdomDoc){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RequestType.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			DOMOutputter outputter = new DOMOutputter();
			Document document = outputter.output(jdomDoc);
			JAXBElement<Object> jaxElement = (JAXBElement<Object>) unmarshaller.unmarshal(document);
			
			if (jaxElement.getValue() instanceof RequestType) {
				RequestType request = (RequestType) jaxElement.getValue();
				return request;
			}
			
		} catch (JDOMException | JAXBException e) {
			log.error("Unmarshalling generalInvoiceRequest_440 from jDom document failed", e);
		}
		return null;
	}
	
	public static String getXMLVersion(org.jdom.Document jdomDoc){
		Element root = jdomDoc.getRootElement();
		String location =
			root.getAttributeValue(Constants.SCHEMA_LOCATION,
				Namespace.getNamespace(Constants.DEFAULT_NAMESPACE));
		
		if (location != null && !location.isEmpty()) {
			if (location.equalsIgnoreCase(Constants.INVOICE_REQUEST_400_LOCATION)
				|| location.equalsIgnoreCase(Constants.INVOICE_RESPONSE_400_LOCATION)) {
				return "4.0";
			} else if (location.equalsIgnoreCase(Constants.INVOICE_REQUEST_440_LOCATION)
				|| location.equalsIgnoreCase(Constants.INVOICE_RESPONSE_440_LOCATION)) {
				return "4.4";
			}
		}
		return location;
	}
}
