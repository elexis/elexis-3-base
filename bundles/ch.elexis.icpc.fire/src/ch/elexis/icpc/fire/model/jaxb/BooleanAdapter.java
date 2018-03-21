package ch.elexis.icpc.fire.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<String, Boolean> {
	@Override
	public String marshal(Boolean v) throws Exception{
		if (v != null && v.booleanValue()) {
			return "1";
		}
		return "0";
	}
	
	@Override
	public Boolean unmarshal(String v) throws Exception{
		return "1".equals(v);
	}
}
