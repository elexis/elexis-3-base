package at.medevit.ch.artikelstamm.medcalendar.test.internal;

import java.io.Serializable;
import java.util.Comparator;

public class SectionComparator implements Comparator<String>, Serializable {
	private static final long serialVersionUID = -6851464721841140556L;
	
	@Override
	public int compare(String key1, String key2){
		if (key1 == null || key2 == null) {
			return 0;
		}
		
		String[] k1Parts = key1.split("\\.");
		String[] k2Parts = key2.split("\\.");
		int k1Size = k1Parts.length;
		int k2Size = k2Parts.length;
		
		int compare = compareNumbers(k1Parts[0], k2Parts[0]);
		// 0 equal continue compare
		if (compare == 0) {
			if (k1Size > 1 && k2Size > 1) {
				compare = compareNumbers(k1Parts[1], k2Parts[1]);
				if (compare == 0) {
					if (k1Size > 2 && k2Size > 2) {
						return compareNumbers(k1Parts[2], k2Parts[2]);
					} else if (k1Size > 2 && k2Size == 2) {
						// k2 is a level higher
						return 1;
					} else {
						// k1 is a level higher
						return -1;
					}
				}
			} else if (k1Size > 1 && k2Size == 1) {
				// k2 is a level higher
				return 1;
			} else {
				// k1 is a level higher
				return -1;
			}
		}
		return compare;
	}
	
	private int compareNumbers(String value1, String value2){
		Integer n1 = Integer.parseInt(value1.replaceAll("[^\\d]", ""));
		Integer n2 = Integer.parseInt(value2.replaceAll("[^\\d]", ""));
		return n1.compareTo(n2);
		
	}
}
