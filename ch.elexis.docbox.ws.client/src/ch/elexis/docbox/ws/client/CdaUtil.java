package ch.elexis.docbox.ws.client;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdaUtil {
	
	private static Logger logger = LoggerFactory.getLogger(CdaUtil.class);

	private static Unmarshaller unmarshaller;
	
	private static Marshaller marshaller;

	public static synchronized Unmarshaller getCdaUnmarshaller(){
		if (CdaUtil.unmarshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance("org.hl7.v3");
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				return null;
			}
		}
		return unmarshaller;
	}
	
	public static synchronized Marshaller getMarshaller(){
		if (CdaUtil.marshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance("org.hl7.v3");
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:hl7-org:v3 CDA.xsd");
				// omit the xml declaration
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, new Boolean(true));
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return marshaller;
	}

	public static POCDMT000040ClinicalDocument unmarshall(InputStream source) throws JAXBException{
		Unmarshaller unmarshaller = CdaUtil.getCdaUnmarshaller();
		
		JAXBElement<POCDMT000040ClinicalDocument> doc =
			unmarshaller.unmarshal(new StreamSource(source), POCDMT000040ClinicalDocument.class);
		return doc.getValue();
	}
	
	@SuppressWarnings({
		"rawtypes", "unchecked"
	})
	public static String marshallIntoString(POCDMT000040ClinicalDocument clinicalDocumentType){
		StringWriter writer = new StringWriter();
		try {
			Marshaller marshaller = getMarshaller();
			marshaller.marshal(new JAXBElement(new QName("urn:hl7-org:v3", "ClinicalDocument"),
				POCDMT000040ClinicalDocument.class, clinicalDocumentType), writer);
		} catch (Exception e) {
			logger.error("Could not marshall to string.", e);
			return null;
		}
		return writer.toString();
	}
}
