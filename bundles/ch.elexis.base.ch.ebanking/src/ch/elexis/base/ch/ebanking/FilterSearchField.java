package ch.elexis.base.ch.ebanking;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.core.model.IInvoice;
import ch.rgw.tools.Money;

public class FilterSearchField extends ViewerFilter {

	private static FilterSearchField instance;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // $NON-NLS-1$

	private FilterSearchField() {
	}

	public static FilterSearchField getInstance() {
		if (null == instance) {
			instance = new FilterSearchField();
		}
		return instance;
	}

	private String searchString;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() < 2)
			return true;
		IEsrRecord e = (IEsrRecord) element;

		char c = searchString.charAt(0);
		String useSearchString = searchString.substring(1);
		if (useSearchString.length() < 1)
			return false;
		switch (c) {
		case '#':
			IInvoice rn = e.getInvoice();
			if (rn != null) {
				String rgNr = rn.getNumber();
				if (rgNr.matches(".*" + useSearchString + ".*")) //$NON-NLS-1$ //$NON-NLS-2$
					return true;
			}
			return false;
		case '$':
			Money betrag = e.getAmount();
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
				if (betrag.getAmountAsString().matches(".*" + useSearchString + ".*")) //$NON-NLS-1$ //$NON-NLS-2$
					return true;
			}
			return false;
		default:
			String patLabel = (e.getPatient() != null ? e.getPatient().getLabel() : StringUtils.EMPTY).toLowerCase();
			if (patLabel.matches(".*" + useSearchString + ".*")) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			if (formatter.format(e.getImportDate()).matches(".*" + useSearchString + ".*")) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			if (formatter.format(e.getProcessingDate()).matches(".*" + useSearchString + ".*")) //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			return false;
		}
	}

	public void setSearchText(String s) {
		if (s == null || s.length() == 0)
			searchString = null;
		else
			searchString = s.toLowerCase(); // $NON-NLS-1$ //$NON-NLS-2$
		// filter "dirty" characters
		if (searchString != null)
			searchString = searchString.replaceAll("[^#<>\\.$, a-zA-Z0-9]", StringUtils.EMPTY); //$NON-NLS-1$
	}
}
