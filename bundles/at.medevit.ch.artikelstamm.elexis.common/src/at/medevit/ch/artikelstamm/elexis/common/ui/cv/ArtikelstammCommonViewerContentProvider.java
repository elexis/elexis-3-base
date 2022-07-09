package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.HashMap;
import java.util.List;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.ATCCodeCacheServiceHolder;
import at.medevit.ch.artikelstamm.elexis.common.service.ATCCodeServiceHolder;
import at.medevit.ch.artikelstamm.elexis.common.service.ModelServiceHolder;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.Message;
import ch.elexis.core.ui.util.viewers.LazyCommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;

public class ArtikelstammCommonViewerContentProvider extends LazyCommonViewerContentProvider {

	private static final int QUERY_LIMIT = 500;

	private ControlFieldProvider controlFieldProvider;
	private boolean addAtcElements;

	public ArtikelstammCommonViewerContentProvider(CommonViewer commonViewer,
			ControlFieldProvider controlFieldProvider) {
		super(commonViewer);
		this.controlFieldProvider = controlFieldProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object arg0) {
		IQuery<?> query = getBaseQuery();
		query.startGroup();
		// apply filters from control field provider
		controlFieldProvider.setQuery(query);
		// or match the gtin
		if (controlFieldProvider.getValues() != null && controlFieldProvider.getValues().length > 0) {
			query.or("gtin", COMPARATOR.LIKE, controlFieldProvider.getValues()[0] + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		query.startGroup();
		// apply additional filters like atc, mepha, ...
		applyQueryFilters(query);
		query.andJoinGroups();

		query.orderBy("ldscr", ORDER.ASC); //$NON-NLS-1$
		List<?> elements = query.execute();
		commonViewer.setLimitReached(elements.size() == QUERY_LIMIT, QUERY_LIMIT);
		if (addAtcElements) {
			if (!isQueryFilterByType(AtcQueryFilter.class)) {
				insertATCCodeValues((List<Object>) elements);
			}
		}
		if (isQueryFilterByType(AtcQueryFilter.class)) {
			getQueryFilterByType(AtcQueryFilter.class).ifPresent(qf -> {
				addFilterInformation(qf, (List<Object>) elements);
			});
		}
		return elements.toArray(new Object[elements.size()]);
	}

	@Override
	protected String getJobName() {
		return "Artikelstamm load";
	}

	@Override
	protected IQuery<?> getBaseQuery() {
		IQuery<IArtikelstammItem> ret = ModelServiceHolder.get().getQuery(IArtikelstammItem.class);
		if (!ignoreLimit) {
			ret.limit(QUERY_LIMIT);
		}
		return ret;
	}

	@Override
	public void changed(HashMap<String, String> values) {
		super.setIgnoreLimit(false);
		super.changed(values);
	}

	@Override
	protected void setIgnoreLimit(boolean value) {
		super.setIgnoreLimit(value);
		if (true) {
			// trigger loading
			asyncReload();
		}
	}

	public void setAddAtcElements(boolean checked) {
		addAtcElements = checked;
		commonViewer.notify(Message.update);
	}

	/**
	 * if {@link ATCQueryFilter} is set, we present the information to the user
	 *
	 * @param atcQueryFilter
	 */
	private void addFilterInformation(AtcQueryFilter atcQueryFilter, List<Object> elements) {
		String atcFilterValue = atcQueryFilter.getFilterValue();
		String atcInfo = ATCCodeServiceHolder.get().get().getForATCCode(atcFilterValue).name_german;
		String label = "ATC Filter " + atcFilterValue + " (" + atcInfo + ")"; //$NON-NLS-2$ //$NON-NLS-3$
		ATCFilterInfoListElement aficle = new ATCFilterInfoListElement(label);
		elements.add(0, aficle);
	}

	private void insertATCCodeValues(List<Object> elements) {
		if (fieldFilterValues != null) {
			String name = fieldFilterValues.get("ldscr"); //$NON-NLS-1$

			if (name == null || name.length() < 1)
				return;
			ATCCodeService atcCodeService = ATCCodeServiceHolder.get().get();
			if (atcCodeService == null)
				return;
			List<ATCCode> results = atcCodeService.getATCCodesMatchingName(name,
					ATCCodeService.ATC_NAME_LANGUAGE_GERMAN, ATCCodeService.MATCH_NAME_BY_NAME_OR_ATC);

			boolean showEmptyGroups = ConfigServiceHolder.get()
					.get(PreferenceConstants.PREF_SHOW_ATC_GROUPS_WITHOUT_ARTICLES, true);
			if (!showEmptyGroups) {
				for (ATCCode atcCode : results) {
					if (ATCCodeCacheServiceHolder.getAvailableArticlesByATCCode(atcCode) > 0)
						elements.add(atcCode);
				}
			} else {
				elements.addAll(0, results);
			}
		}
	}
}
