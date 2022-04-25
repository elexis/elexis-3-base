package ch.elexis.covid.cert.valueset;

import java.util.Map;

public class ValueSetConceptArray {
	public String valueSetId;
	public String valueSetDate;
	public Map<String, String>[] valueSetValues;

	@Override
	public String toString() {
		return "ValueSet [id=" + valueSetId + ", conceptList=" + valueSetValues + "]";
	}

}
