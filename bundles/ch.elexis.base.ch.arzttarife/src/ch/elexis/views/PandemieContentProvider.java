package ch.elexis.views;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;

public class PandemieContentProvider extends CommonViewerContentProvider {

	private ControlFieldProvider controlFieldProvider;

	public PandemieContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
		super(commonViewer);
		this.controlFieldProvider = controlFieldProvider;
	}

	@Override
	public Object[] getElements(Object arg0) {
		IQuery<?> query = getBaseQuery();

		java.util.Optional<IEncounter> encounter = ContextServiceHolder.get().getTyped(IEncounter.class);
		encounter.ifPresent(e -> {
			query.and("validFrom", COMPARATOR.LESS_OR_EQUAL, e.getDate());
			query.startGroup();
			query.or("validUntil", COMPARATOR.GREATER_OR_EQUAL, e.getDate());
			query.or("validUntil", COMPARATOR.EQUALS, null);
			query.andJoinGroups();
		});

		// apply filters from control field provider
		controlFieldProvider.setQuery(query);
		applyQueryFilters(query);
		query.orderBy("code", ORDER.ASC);
		List<?> elements = query.execute();

		return elements.toArray(new Object[elements.size()]);
	}

	@Override
	protected IQuery<?> getBaseQuery() {
		IQuery<IPandemieLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPandemieLeistung.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "VERSION");
		query.startGroup();
		query.or("org", COMPARATOR.EQUALS, StringUtils.EMPTY);
		query.or("org", COMPARATOR.LIKE, "%Arztpraxis%");
		query.andJoinGroups();
		return query;
	}
}
