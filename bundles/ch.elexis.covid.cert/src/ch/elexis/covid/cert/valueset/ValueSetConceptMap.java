package ch.elexis.covid.cert.valueset;

import java.util.Map;

public class ValueSetConceptMap {
	public String valueSetId;
	public String valueSetDate;
	public Map<String, Map<String, String>> valueSetValues;
	
	@Override
	public String toString(){
		return "ValueSet [id=" + valueSetId + ", conceptMap=" + valueSetValues + "]";
	}
}
