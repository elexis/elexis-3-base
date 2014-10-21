package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.ch.artikelstamm.elexis.common.internal.ATCCodeServiceConsumer;
import at.medevit.ch.artikelstamm.elexis.common.preference.PreferenceConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache.ATCCodeCache;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader;
import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ArtikelstammFlatDataLoader extends FlatDataLoader implements IDoubleClickListener {
	
	private ATCQueryFilter atcQueryFilter = new ATCQueryFilter();
	private boolean useAtcQueryFilter = false;
	
	@SuppressWarnings("rawtypes")
	private List filtered = null;
	@SuppressWarnings("rawtypes")
	private List raw = null;
	private SelectorPanelProvider slp;
	
	public ArtikelstammFlatDataLoader(CommonViewer cv, Query<? extends PersistentObject> qbe, SelectorPanelProvider slp){
		super(cv, qbe);
		this.slp = slp;
		
		setOrderFields(ArtikelstammItem.FLD_DSCR);

		applyQueryFilters();
		addQueryFilter(new IncludeEANQueryFilter());
		addQueryFilter(new NoVersionQueryFilter());
	}
	
	/**
	 * This filter skips all entries with ID "VERSION"
	 */
	private class NoVersionQueryFilter implements QueryFilter {
		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.and();
			qbe.add("ID", Query.NOT_EQUAL, "VERSION");
		}
	}
	
	private class IncludeEANQueryFilter implements QueryFilter {

		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			if(slp.getValues()!=null) {
				String eanValue = slp.getValues()[0];
				if(eanValue.length()>0 && StringUtils.isNumeric(eanValue)) {
					qbe.or();
					qbe.add(ArtikelstammItem.FLD_GTIN, Query.LIKE, eanValue+"%");
				}
			}
		}
	}
	
	/**
	 * This filter limits all results to a certain ATC code
	 * 
	 */
	private class ATCQueryFilter implements QueryFilter {
		
		private String atcFilter;
		
		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.add(ArtikelstammItem.FLD_ATC, Query.LIKE, atcFilter+"%");
		}
		
		public void setAtcFilter(String atcFilter){
			this.atcFilter = atcFilter;
		}
		
		public String getAtcFilter(){
			return atcFilter;
		}
		
		public boolean isActive(){
			return atcFilter != null;
		}
	}
	
	@Override
	public IStatus work(IProgressMonitor monitor, HashMap<String, Object> params){
		if (isSuspended()) {
			return Status.CANCEL_STATUS;
		}
		final TableViewer tv = (TableViewer) cv.getViewerWidget();
		if (filtered != null) {
			filtered.clear();
		}
		filtered = null;
		setQuery();
		applyQueryFilters();
		if (orderFields != null) {
			qbe.orderBy(false, orderFields);
		}
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		raw = qbe.execute();
		
		if(useAtcQueryFilter) {
			if (!atcQueryFilter.isActive()) {
				insertATCCodeValues(params);
			} else {
				addFilterInformation();
			}
		}
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		UiDesk.asyncExec(new Runnable() {
			public void run(){
				// Avoid access to disposed table
				if (tv != null && !tv.getTable().isDisposed()) {
					tv.setItemCount(0);
					filtered = raw;
					tv.setItemCount(raw.size());
				}
			}
		});
		
		return Status.OK_STATUS;
	}
	
	/**
	 * if {@link ATCQueryFilter} is set, we present the information to the user
	 */
	@SuppressWarnings("unchecked")
	private void addFilterInformation(){
		String atcInfo = ATCCodeServiceConsumer.getATCCodeService().getForATCCode(atcQueryFilter.atcFilter).name_german;
		String label = "ATC Filter "+atcQueryFilter.atcFilter+" ("+atcInfo+")";
		ATCFilterInfoListElement aficle = new ATCFilterInfoListElement(label);
		raw.add(0, aficle);
	}

	/**
	 * adds the atc names we find for the given description on the top of the table
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	private void insertATCCodeValues(HashMap<String, Object> params){	
		HashMap<String, String> fieldValuesHashmap =
			(HashMap<String, String>) params.get(PersistentObjectLoader.PARAM_VALUES);
		if (fieldValuesHashmap == null)
			return;
		String name = fieldValuesHashmap.get(ArtikelstammItem.FLD_DSCR);
		if (name == null || name.length() < 1)
			return;
		ATCCodeService atcCodeService = ATCCodeServiceConsumer.getATCCodeService();
		if (atcCodeService == null)
			return;
		List<ATCCode> results =
			atcCodeService.getATCCodesMatchingName(name, ATCCodeService.ATC_NAME_LANGUAGE_GERMAN, ATCCodeService.MATCH_NAME_BY_NAME_OR_ATC);

		boolean showEmptyGroups =
			CoreHub.globalCfg.get(PreferenceConstants.PREF_SHOW_ATC_GROUPS_WITHOUT_ARTICLES, true);
		if (!showEmptyGroups) {
			for (ATCCode atcCode : results) {
				if(ATCCodeCache.getAvailableArticlesByATCCode(atcCode)>0) raw.add(atcCode);
			}
		} else {
			raw.addAll(0, results);
		}
	}
	
	public void setResult(List<PersistentObject> res){
		raw = res;
	}
	
	/**
	 * prepare the query so it returns the appropriate Objects on execute(). The default
	 * implemetation lets the ControlFieldProvider set the query. Subclasses may override
	 */
	protected void setQuery(){
		qbe.clear();
		ControlFieldProvider cfp = cv.getConfigurer().getControlFieldProvider();
		if (cfp != null) {
			cfp.setQuery(qbe);
		}
	}
	
	/**
	 * copied from {@link FlatDataLoader}
	 */
	public void updateElement(int index){
		if (filtered != null) {
			if (index >= 0 && index < filtered.size()) {
				Object o = filtered.get(index);
				if (o != null) {
					TableViewer tv = (TableViewer) cv.getViewerWidget();
					tv.replace(filtered.get(index), index);
				}
			}
		}
	}
	
	private String filterValueStore;
	
	@Override
	public void doubleClick(DoubleClickEvent event){
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		if(selection.getFirstElement()==null) return;
		if (selection.getFirstElement() instanceof ATCCode) {
			filterValueStore = slp.getValues()[0];
			slp.clearValues();
			ATCCode a = (ATCCode) selection.getFirstElement();
			atcQueryFilter.setAtcFilter(a.atcCode);
			addQueryFilter(atcQueryFilter);
			dj.launch(0);
		} else if (selection.getFirstElement() instanceof ATCFilterInfoListElement) {
			slp.clearValues();
			ActiveControl ac = slp.getPanel().getControls().get(0);
			ac.setText(filterValueStore);
			removeQueryFilter(atcQueryFilter);
			atcQueryFilter.setAtcFilter(null);
			dj.launch(0);
		}
	}
	
	/**
	 * set the atc query filter to active or inactive
	 * @param useAtcQueryFilter
	 */
	public void setUseAtcQueryFilter(boolean useAtcQueryFilter){
		this.useAtcQueryFilter = useAtcQueryFilter;
		if(useAtcQueryFilter) {
			dj.launch(0);
		} else {
			removeQueryFilter(atcQueryFilter);
			atcQueryFilter.setAtcFilter(null);
			dj.launch(0);
		}
	}
	
	/**
	 * 
	 * @return <code>true</code> if the atc query filter is active
	 */
	public boolean isUseAtcQueryFilter(){
		return useAtcQueryFilter;
	}
}
