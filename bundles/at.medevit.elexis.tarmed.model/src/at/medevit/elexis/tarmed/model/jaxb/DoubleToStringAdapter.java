package at.medevit.elexis.tarmed.model.jaxb;

import ch.rgw.tools.XMLTool;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleToStringAdapter extends XmlAdapter<String, Double> {

	@Override
	public Double unmarshal(String value) throws Exception {
		return (jakarta.xml.bind.DatatypeConverter.parseDouble(value));
	}

	@Override
	public String marshal(Double value) throws Exception {
		if (value == null) {
			return null;
		}
		return XMLTool.doubleToXmlDouble(value, 2);

	}

}
