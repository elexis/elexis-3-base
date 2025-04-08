package ch.elexis.estudio.clustertec;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.clustertec.estudio.schemas.order.Order;
import ch.clustertec.estudio.schemas.order.OrderResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.Unmarshaller;

public class ClustertecJaxbUtil {

	public static final String JAXB_HEADER_KEY = "org.glassfish.jaxb.xmlHeaders"; //$NON-NLS-1$
	public static final String DEFAULT_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

	private static Logger log = LoggerFactory.getLogger(ClustertecJaxbUtil.class);

	public static OrderResponse unmarshalOrderResponse(InputStream inStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(OrderResponse.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object unmarshalObj = unmarshaller.unmarshal(inStream);

			if (unmarshalObj instanceof OrderResponse) {
				OrderResponse request = (OrderResponse) unmarshalObj;
				return request;
			}
		} catch (JAXBException e) {
			log.error("Unmarshalling OrderResponse failed", e); //$NON-NLS-1$
		}
		return null;
	}

	public static boolean marshalOrder(Order clustertecOrder, OutputStream outStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Order.class);
			Marshaller marshaller = initMarshaller(jaxbContext, "http://estudio.clustertec.ch/schemas/order.xsd");
			marshaller.marshal(clustertecOrder, outStream);
			return true;
		} catch (JAXBException e) {
			log.error("Marshalling Order failed", e); //$NON-NLS-1$
			return false;
		}
	}

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
	private static Marshaller initMarshaller(JAXBContext jaxbContext, String schemaLocation) throws JAXBException {

		Marshaller marshaller = jaxbContext.createMarshaller();
		try {
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
			marshaller.setProperty(JAXB_HEADER_KEY, DEFAULT_HEADER);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			return marshaller;
		} catch (PropertyException propE) {
			log.error("Error setting marshall properties - concerns XML with schema [" + schemaLocation + "]", propE); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return marshaller;
	}
}
