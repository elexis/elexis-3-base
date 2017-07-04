package ch.elexis.icpc.fire.model.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateAdapter extends XmlAdapter<String, XMLGregorianCalendar> {
	
	
	//YYYY-MM-DDTHH:MM:SS
	private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	@Override
	public String marshal(XMLGregorianCalendar calendar) throws Exception{
		if (calendar != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			return dateFormat.format(calendar.toGregorianCalendar().getTime());
		}
		return null;
	}
	
	@Override
	public XMLGregorianCalendar unmarshal(String dateTimeString) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date dateTime = dateFormat.parse(dateTimeString);
		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(dateTime);
		XMLGregorianCalendar xmlCalendar = null;
		try {
			xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
		} catch (DatatypeConfigurationException ex) {
			return null;
		}
		return xmlCalendar;
	}
}
