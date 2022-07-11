package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider.QueryFilter;

public class AtcQueryFilter implements QueryFilter {
	private String filterValue;

	public void setFilterValue(String value) {
		this.filterValue = value;
	}

	public String getFilterValue() {
		return this.filterValue;
	}

	@Override
	public void apply(IQuery<?> query) {
		if (filterValue != null && !filterValue.isEmpty()) {
			query.and("atc", COMPARATOR.LIKE, filterValue + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
