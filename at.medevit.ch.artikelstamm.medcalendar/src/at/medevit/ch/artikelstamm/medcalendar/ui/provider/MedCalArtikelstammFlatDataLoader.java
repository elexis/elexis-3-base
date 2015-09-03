package at.medevit.ch.artikelstamm.medcalendar.ui.provider;

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

import at.medevit.ch.artikelstamm.elexis.common.ui.cv.MephaPrefferedProviderSorterAction;
import at.medevit.ch.artikelstamm.medcalendar.MedCalendarSection;
import at.medevit.ch.artikelstamm.medcalendar.Messages;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class MedCalArtikelstammFlatDataLoader extends FlatDataLoader implements
		IDoubleClickListener {
	
	private MedCalQueryFilter medCalQueryFilter = new MedCalQueryFilter();
	private boolean useMedCalQueryFilter = false;
	private boolean useMephaPreferredSorter = false;
	private String filterValueStore;
	
	@SuppressWarnings("rawtypes")
	private List filtered = null;
	@SuppressWarnings("rawtypes")
	private List raw = null;
	private SelectorPanelProvider slp;
	
	public MedCalArtikelstammFlatDataLoader(CommonViewer cv, Query<? extends PersistentObject> qbe,
		SelectorPanelProvider slp){
		super(cv, qbe);
		this.slp = slp;
		
		setOrderFields(ArtikelstammItem.FLD_DSCR);
		
		applyQueryFilters();
		addQueryFilter(new IncludeEANQueryFilter());
		addQueryFilter(new NoVersionQueryFilter());
		
		useMephaPreferredSorter =
			CoreHub.globalCfg.get(MephaPrefferedProviderSorterAction.CFG_PREFER_MEPHA, false);
		
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event){
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		if (selection.getFirstElement() == null)
			return;
		if (selection.getFirstElement() instanceof MedCalFilterInfoElement) {
			slp.clearValues();
			ActiveControl ac = slp.getPanel().getControls().get(0);
			ac.setText((filterValueStore != null) ? filterValueStore : "");
			
			setMedCalQueryFilterValue(null);
		}
	}
	
	/**
	 * Set the MedCalendarSection value to filter the selector
	 * 
	 * @param filterValue
	 *            a MedCalendarSection value or <code>null</code> to remove
	 */
	public void setMedCalQueryFilterValue(MedCalendarSection filterValue){
		if (filterValue == null) {
			removeQueryFilter(medCalQueryFilter);
			medCalQueryFilter.setSectionFilter(null);
		} else {
			medCalQueryFilter.setSectionFilter(filterValue);
			addQueryFilter(medCalQueryFilter);
		}
		dj.launch(0);
	}
	
	/**
	 * set the med cal query filter to active or inactive
	 * 
	 * @param useMedCalQueryFilter
	 */
	public void setUseMedCalQueryFilter(boolean useMedCalQueryFilter){
		this.useMedCalQueryFilter = useMedCalQueryFilter;
		if (!useMedCalQueryFilter) {
			removeQueryFilter(medCalQueryFilter);
			medCalQueryFilter.setSectionFilter(null);
		}
		dj.launch(0);
	}
	
	/**
	 * 
	 * @return <code>true</code> if the medCal query filter is active
	 */
	public boolean isUseMedCalQueryFilter(){
		return useMedCalQueryFilter;
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
		
		if (useMephaPreferredSorter) {
			// #3627 need to work-around 
			qbe.addToken(" 1=1 ORDER BY FIELD(COMP_GLN, '7601001001121') DESC, DSCR ASC");
		} else {
			if (orderFields != null) {
				qbe.orderBy(false, orderFields);
			}
		}
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		raw = qbe.execute();
		
		if (useMedCalQueryFilter && medCalQueryFilter.isActive()) {
			addFilterInformation();
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
	
	private void addFilterInformation(){
		MedCalendarSection sectionFilter = medCalQueryFilter.getSectionFilter();
		String label =
			Messages.MedCalFilter + sectionFilter.getCode() + " " + sectionFilter.getName();
		MedCalFilterInfoElement filterInfo = new MedCalFilterInfoElement(label);
		raw.add(0, filterInfo);
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
	
	/**
	 * should filtering prefer Mepha articles? #3627
	 * 
	 * @param doPrefer
	 *            if yes, first in list are Mepha articles A-Z then others A-Z
	 */
	public void setUseMephaPrefferedProviderSorter(boolean doPreferMepha){
		this.useMephaPreferredSorter = doPreferMepha;
		dj.launch(0);
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
			if (slp.getValues() != null) {
				String eanValue = slp.getValues()[0];
				if (eanValue.length() > 0 && StringUtils.isNumeric(eanValue)) {
					qbe.or();
					qbe.add(ArtikelstammItem.FLD_GTIN, Query.LIKE, eanValue + "%");
				}
			}
		}
	}
	
	/**
	 * This filter limits all results to a certain {@link MedCalendarSection}
	 * 
	 */
	private class MedCalQueryFilter implements QueryFilter {
		private MedCalendarSection sectionFilter;
		
		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			if (!sectionFilter.getATCCodes().isEmpty()) {
				String first = sectionFilter.getATCCodes().get(0);
				for (String atc : sectionFilter.getATCCodes()) {
					if (!atc.equals(first)) {
						qbe.or();
					}
					qbe.add(ArtikelstammItem.FLD_ATC, Query.LIKE, atc + "%");
				}
			}
		}
		
		public void setSectionFilter(MedCalendarSection sectionFilter){
			this.sectionFilter = sectionFilter;
		}
		
		public MedCalendarSection getSectionFilter(){
			return sectionFilter;
		}
		
		public boolean isActive(){
			return sectionFilter != null;
		}
	}
	
}
