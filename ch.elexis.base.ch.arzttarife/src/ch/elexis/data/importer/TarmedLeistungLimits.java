package ch.elexis.data.importer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

public class TarmedLeistungLimits {
	
	public enum LimitsEinheit {
			TAG("21"), SEITE("10"), SITZUNG("7"), HAUPTLEISTUNG("45"), UNKNONW("");
		
		private String value;
		
		private LimitsEinheit(String value){
			this.value = value;
		}
	}
	
	private String operator;
	private float menge;
	private String anzahl;
	private String pro;
	private LimitsEinheit einheit;
	
	/**
	 * Create a limits object from the string written by the importer (see
	 * {@link TarmedReferenceDataImporter}).
	 * 
	 * @param limits
	 */
	private TarmedLeistungLimits(String limits){
		if (isValidLimitsString(limits)) {
			String[] parts;
			if (limits.endsWith("#")) {
				parts = limits.substring(0, limits.length() - 1).split(",");
			} else {
				parts = limits.split(",");
			}
			operator = parts[0];
			try {
				menge = Float.parseFloat(parts[1]);
			} catch (NumberFormatException ne) {
				// log, but do not stop
				LoggerFactory.getLogger(getClass()).warn("Could not parse amount " + parts[1], ne);
			}
			anzahl = parts[2];
			pro = parts[3];
			einheit = getLimitsEinheit(parts[4]);
		}
	}
	
	public static LimitsEinheit getLimitsEinheit(String string){
		for (LimitsEinheit le : LimitsEinheit.values()) {
			if (le.value.equals(string)) {
				return le;
			}
		}
		return LimitsEinheit.UNKNONW;
	}
	
	private static boolean isValidLimitsString(String limits){
		if(limits != null && !limits.isEmpty()) {
			return StringUtils.countMatches(limits, ",") == 4;
		}
		return false;
	}
	
	/**
	 * Create {@link TarmedLeistungLimits} objects for the limits String.
	 * 
	 * @param limits
	 * @return
	 */
	public static List<TarmedLeistungLimits> of(String limits){
		List<TarmedLeistungLimits> ret = new ArrayList<>();
		if (limits != null && !limits.isEmpty()) {
			String[] singleLimits = limits.split("#");
			for (String string : singleLimits) {
				if (isValidLimitsString(string)) {
					ret.add(new TarmedLeistungLimits(limits));
				} else {
					LoggerFactory.getLogger(TarmedLeistungLimits.class)
						.warn("Could not parse limit string [" + string + "]");
				}
			}
		}
		return ret;
	}
	
	public String getOperator(){
		return operator;
	}
	
	public float getMenge(){
		return menge;
	}
	
	public String getAnzahl(){
		return anzahl;
	}
	
	public String getPro(){
		return pro;
	}
	
	public LimitsEinheit getEinheit(){
		return einheit;
	}
}
