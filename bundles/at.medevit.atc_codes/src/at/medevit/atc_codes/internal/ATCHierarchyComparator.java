package at.medevit.atc_codes.internal;

import java.util.Comparator;

import at.medevit.atc_codes.ATCCode;

public class ATCHierarchyComparator implements Comparator<ATCCode> {

	@Override
	public int compare(ATCCode o1, ATCCode o2){
		return o1.atcCode.compareTo(o2.atcCode);
	}
	
}
