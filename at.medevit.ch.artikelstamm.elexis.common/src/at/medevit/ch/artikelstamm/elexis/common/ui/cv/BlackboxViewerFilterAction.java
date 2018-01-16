package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wb.swt.ResourceManager;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader.QueryFilter;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class BlackboxViewerFilterAction extends Action {
	
	private FlatDataLoader fdl;
	private QueryFilter blackboxOnlyFilter = new BlackboxOnlyQueryFilter();
	private SelectorPanelProvider slp;
	
	private static final String FILTER_CFG = "BlackboxViewerFilterAction.showInactiveItems";
	
	public BlackboxViewerFilterAction(FlatDataLoader fdl, SelectorPanelProvider selectorPanel){
		this.fdl = fdl;
		this.slp = selectorPanel;
		
		boolean value = CoreHub.userCfg.get(FILTER_CFG, false);
		setChecked(value);
		addOrRemoveFilter();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(){
		return ResourceManager.getPluginImageDescriptor("at.medevit.ch.artikelstamm.ui",
			"/rsc/icons/flag-black.png");
	}
	
	@Override
	public int getStyle(){
		return Action.AS_CHECK_BOX;
	}
	
	@Override
	public String getToolTipText(){
		return "Inaktive Items anzeigen";
	}
	
	private void addOrRemoveFilter(){
		if (isChecked()) {
			fdl.removeQueryFilter(blackboxOnlyFilter);
		} else {
			fdl.addQueryFilter(blackboxOnlyFilter);
		}
	}
	
	@Override
	public void run(){
		addOrRemoveFilter();
		fdl.applyQueryFilters();
		slp.getPanel().contentsChanged(null);
		CoreHub.userCfg.set(FILTER_CFG, isChecked());
	}
	
	@Override
	public String getDescription(){
		return "Inkludiert inaktive Items in die Anzeige (schwarze Fahne)";
	}
	
	@Override
	public String getText(){
		return "Inaktive Items";
	}
	
	private class BlackboxOnlyQueryFilter implements QueryFilter {
		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.add(ArtikelstammItem.FLD_BLACKBOXED, Query.EQUALS, StringConstants.ZERO);
		}
	}
}
