package at.medevit.elexis.tarmed.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ch.rgw.tools.XMLTool;

public class DoubleToStringAdapter extends XmlAdapter<String, Double> {

	@Override
	public Double unmarshal(String value) throws Exception {
		return (javax.xml.bind.DatatypeConverter.parseDouble(value));
	}

	@Override
	public String marshal(Double value) throws Exception {
		if (value == null) {
			return null;
		}
		return XMLTool.doubleToXmlDouble(value, 2);

	}

}
