package ch.elexis.views;

import java.util.List;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;

public class AmbulatoryAllowanceContentProvider extends CommonViewerContentProvider {

	private ControlFieldProvider controlFieldProvider;

	public AmbulatoryAllowanceContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
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
			query.or("validTo", COMPARATOR.GREATER_OR_EQUAL, e.getDate());
			query.or("validTo", COMPARATOR.EQUALS, null);
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
		IQuery<IAmbulatoryAllowance> query = ArzttarifeModelServiceHolder.get().getQuery(IAmbulatoryAllowance.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "VERSION");
		query.and("typ", COMPARATOR.EQUALS, AmbulantePauschalenTyp.TRIGGER.getCode());
		return query;
	}
}
