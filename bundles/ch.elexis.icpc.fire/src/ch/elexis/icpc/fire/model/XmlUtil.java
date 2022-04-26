package ch.elexis.icpc.fire.model;

import java.io.OutputStream;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.rgw.tools.TimeTool;

public class XmlUtil {

	private static final String JAXB_HEADER_KEY = "com.sun.xml.bind.xmlHeaders";
	private static final String DEFAULT_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	public static XMLGregorianCalendar getXmlGregorianCalendar(TimeTool timeTool)
			throws DatatypeConfigurationException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(timeTool.getTime());
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
	}

	public static boolean marshallFireReport(Report report, OutputStream outStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);
			Marshaller marshaller = initMarshaller(jaxbContext);
			marshaller.marshal(report, outStream);
			return true;
		} catch (JAXBException e) {
			logger.error("Marshalling Report file failed", e);
			return false;
		}
	}

	public static Marshaller initMarshaller(JAXBContext jaxbContext) throws JAXBException {

		Marshaller marshaller = jaxbContext.createMarshaller();
		try {
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "/rsc/fireDbDe_20170322.xsd");
			marshaller.setProperty(JAXB_HEADER_KEY, DEFAULT_HEADER);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			return marshaller;
		} catch (PropertyException propE) {
			logger.error("Error setting marshall properties - concerns XML with schema [" + "/rsc/fireDbDe_20170322.xsd"
					+ "]", propE);
		}
		return marshaller;
	}
}
