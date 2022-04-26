package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.core.model.IContact;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider.QueryFilter;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;

public class BlackboxViewerFilterAction extends Action {

	private CommonViewerContentProvider commonViewerContentProvider;
	private QueryFilter blackboxOnlyFilter = new BlackboxOnlyQueryFilter();
	private SelectorPanelProvider slp;

	private static final String FILTER_CFG = "BlackboxViewerFilterAction.showInactiveItems";

	public BlackboxViewerFilterAction(CommonViewerContentProvider commonViewerContentProvider,
			SelectorPanelProvider selectorPanel) {
		this.commonViewerContentProvider = commonViewerContentProvider;
		this.slp = selectorPanel;

		boolean value = false;
		Optional<IContact> activeUserContact = ContextServiceHolder.get().getActiveUserContact();
		if (activeUserContact.isPresent()) {
			value = ConfigServiceHolder.get().get(activeUserContact.get(), FILTER_CFG, false);
		}
		setChecked(value);
		addOrRemoveFilter();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ResourceManager.getPluginImageDescriptor("at.medevit.ch.artikelstamm.ui", "/rsc/icons/flag-black.png");
	}

	@Override
	public int getStyle() {
		return Action.AS_CHECK_BOX;
	}

	@Override
	public String getToolTipText() {
		return "Inaktive Items anzeigen";
	}

	private void addOrRemoveFilter() {
		if (isChecked()) {
			commonViewerContentProvider.removeQueryFilter(blackboxOnlyFilter);
		} else {
			commonViewerContentProvider.addQueryFilter(blackboxOnlyFilter);
		}
	}

	@Override
	public void run() {
		addOrRemoveFilter();
		slp.getPanel().contentsChanged(null);
		ContextServiceHolder.get().getActiveUserContact()
				.ifPresent(contact -> ConfigServiceHolder.get().set(contact, FILTER_CFG, isChecked()));
	}

	@Override
	public String getDescription() {
		return "Inkludiert inaktive Items in die Anzeige (schwarze Fahne)";
	}

	@Override
	public String getText() {
		return "Inaktive Items";
	}

	private class BlackboxOnlyQueryFilter implements QueryFilter {
		@Override
		public void apply(IQuery<?> query) {
			query.and("bb", COMPARATOR.EQUALS, "0");
		}
	}
}
