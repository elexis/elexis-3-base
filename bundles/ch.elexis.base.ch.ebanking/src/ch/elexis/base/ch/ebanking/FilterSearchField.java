package ch.elexis.base.ch.ebanking;

import java.text.ParseException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.Money;

public class FilterSearchField extends ViewerFilter {
	
	private static FilterSearchField instance;
	
	private FilterSearchField(){}
	
	public static FilterSearchField getInstance(){
		if (null == instance) {
			instance = new FilterSearchField();
		}
		return instance;
	}
	
	private String searchString;
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		if (searchString == null || searchString.length() < 2)
			return true;
		ESRRecord e = (ESRRecord) element;
		
		char c = searchString.charAt(0);
		String useSearchString = searchString.substring(1);
		if (useSearchString.length() < 1)
			return false;
		switch (c) {
		case '#':
			Rechnung rn = e.getRechnung();
			if (rn != null) {
				String rgNr = rn.getNr();
				if (rgNr.matches(".*" + useSearchString + ".*"))
					return true;
			}
			return false;
		case '$':
			Money betrag = e.getBetrag();
			char d = useSearchString.charAt(0);
			Money moreThan;
			switch (d) {
			case '>':
				try {
					moreThan = new Money(useSearchString.substring(1));
					if (betrag.getCents() >= moreThan.getCents())
						return true;
				} catch (ParseException e1) {
					return false;
				}
				break;
			case '<':
				try {
					moreThan = new Money(useSearchString.substring(1));
					if (betrag.getCents() <= moreThan.getCents())
						return true;
				} catch (ParseException e1) {
					return false;
				}
				break;
			default:
				if (betrag.getAmountAsString().matches(".*" + useSearchString + ".*"))
					return true;
			}
			return false;
		default:
			String patLabel = e.getPatient().getLabel().toLowerCase();
			if (patLabel.matches(".*" + useSearchString + ".*"))
				return true;
			if (e.getEinlesedatatum().matches(".*" + useSearchString + ".*"))
				return true;
			if (e.getVerarbeitungsdatum().matches(".*" + useSearchString + ".*"))
				return true;
			return false;
		}
	}
	
	public void setSearchText(String s){
		if (s == null || s.length() == 0)
			searchString = null;
		else
			searchString = s.toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
		// filter "dirty" characters
		if (searchString != null)
			searchString = searchString.replaceAll("[^#<>\\.$, a-zA-Z0-9]", "");
	}
}
