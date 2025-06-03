package ch.elexis.estudio.clustertec;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import ch.clustertec.estudio.schemas.order.Order;
import ch.clustertec.estudio.schemas.order.OrderResponse;
import ch.clustertec.estudio.schemas.prescription.Prescription;

public class ClustertecJaxbUtil {

	public static final String JAXB_HEADER_KEY = "com.sun.xml.bind.xmlHeaders"; //$NON-NLS-1$
	public static final String DEFAULT_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

	private static Logger log = LoggerFactory.getLogger(ClustertecJaxbUtil.class);

	public static OrderResponse unmarshalOrderResponse(InputStream inStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(OrderResponse.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			// Parse the XML and remove the xsi:type attribute
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(inStream));
			Element root = document.getDocumentElement();

			// Remove the xsi:type attribute
			NamedNodeMap attributes = root.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				if (attribute.getNodeName().equals("xsi:type")) {
					attributes.removeNamedItem(attribute.getNodeName());
				}
			}
			// Remove the xmlns:xsi declaration
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				if (attribute.getNodeName().equals("xmlns:xsi")) {
					attributes.removeNamedItem(attribute.getNodeName());
				}
			}

			// Add the namespace to the order-response element
			root.setAttribute("xmlns", "http://estudio.clustertec.ch/schemas/order");

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));

			return (OrderResponse) unmarshaller.unmarshal(new StreamSource(new StringReader(writer.toString())));
		} catch (Exception e) {
			log.error("Unmarshalling OrderResponse failed", e); //$NON-NLS-1$
		}
		return null;
	}

	public static boolean marshalOrder(Order clustertecOrder, OutputStream outStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Order.class);
			Marshaller marshaller = initMarshaller(jaxbContext, "http://estudio.clustertec.ch/schemas/order.xsd", true);
			marshaller.marshal(clustertecOrder, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling Order failed", e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Use JAXB to marshal the {@link Order} to the returned String.
	 * 
	 * @param clustertecOrder
	 * @return
	 */
	public static String marshalOrder(Order clustertecOrder) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshalOrder(clustertecOrder, out);
		return new String(out.toByteArray());
	}

	/**
	 * initializes a marshaller for the given {@link JAXBContext} and sets default
	 * properties
	 *
	 * @param jaxbContext
	 * @param schemaLocation location of schema for the XML
	 * @return {@link Marshaller} marshaller
	 * @throws JAXBException if creating marshaller from jaxbContext failed
	 */
	private static Marshaller initMarshaller(JAXBContext jaxbContext, String schemaLocation, boolean addDefaultHeader)
			throws JAXBException {

		Marshaller marshaller = jaxbContext.createMarshaller();
		try {
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			if (schemaLocation != null) {
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
			}
			if (addDefaultHeader) {
				marshaller.setProperty(JAXB_HEADER_KEY, DEFAULT_HEADER);
			}
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			return marshaller;
		} catch (PropertyException propE) {
			log.error("Error setting marshall properties - concerns XML with schema [" + schemaLocation + "]", propE); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return marshaller;
	}

	/**
	 * Use JAXB to marshal the {@link Prescription} to the returned String.
	 * 
	 * @param clustertecPrescription
	 * @return
	 */
	public static String marshalPrescription(Prescription clustertecPrescription) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshalPrescription(clustertecPrescription, out);
		return new String(out.toByteArray());
	}

	public static boolean marshalPrescription(Prescription clustertecPrescription, OutputStream outStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Prescription.class);
			Marshaller marshaller = initMarshaller(jaxbContext,
					"http://estudio.clustertec.ch/schemas/prescription.xsd", false);
			marshaller.marshal(clustertecPrescription, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling Order failed", e); //$NON-NLS-1$
			return false;
		}
	}
}
