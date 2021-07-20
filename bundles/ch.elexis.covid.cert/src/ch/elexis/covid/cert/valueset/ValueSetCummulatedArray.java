package ch.elexis.covid.cert.valueset;

import java.util.Map;

public class ValueSetCummulatedArray {
	public String Id;
	public String Date;
	public String Version;
	public Map<String, String>[] entries;
	
	@Override
	public String toString(){
		return "ValueSet [id=" + Id + ", conceptList=" + entries + "]";
	}
}
