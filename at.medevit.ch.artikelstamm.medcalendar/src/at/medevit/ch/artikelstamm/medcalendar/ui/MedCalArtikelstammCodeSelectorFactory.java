package at.medevit.ch.artikelstamm.medcalendar.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.ch.artikelstamm.elexis.common.ui.ArtikelstammDetailDialog;
import at.medevit.ch.artikelstamm.elexis.common.ui.cv.ArtikelstammCodeSelectorFactory;
import at.medevit.ch.artikelstamm.elexis.common.ui.cv.VATMenuContributionItem;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.LagerhaltungArtikelstammLabelProvider;
import at.medevit.ch.artikelstamm.medcalendar.action.MephaPrefferedProviderSorterAction;
import at.medevit.ch.artikelstamm.medcalendar.ui.provider.MedCalArtikelstammFlatDataLoader;
import at.medevit.ch.artikelstamm.medcalendar.ui.provider.MedCalDecoratingLabelProvider;
import at.medevit.ch.artikelstamm.ui.IArtikelstammItem;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.data.Query;

public class MedCalArtikelstammCodeSelectorFactory extends ArtikelstammCodeSelectorFactory {
	private SelectorPanelProvider slp;
	private int eventType = SWT.KeyDown;
	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	
	private ISelectionChangedListener selChange = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event){
			TableViewer tv = (TableViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());
		}
	};
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		final CommonViewer cov = cv;
		cov.setSelectionChangedListener(selChange);
		
		FieldDescriptor<?>[] fields =
			{
				new FieldDescriptor<ArtikelstammItem>("Artikel oder Wirkstoff",
					ArtikelstammItem.FLD_DSCR, Typ.STRING, null),
			};
		
		// add keyListener to search field
		Listener keyListener = new Listener() {
			@Override
			public void handleEvent(Event event){
				if (event.type == eventType) {
					if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
						slp.fireChangedEvent();
					}
				}
			}
		};
		for (FieldDescriptor<?> fd : fields) {
			fd.setAssignedListener(eventType, keyListener);
		}
		slp = new SelectorPanelProvider(fields, true);
		
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		MedCalArtikelstammFlatDataLoader fdl = new MedCalArtikelstammFlatDataLoader(cv, qbe, slp);
		
		MephaPrefferedProviderSorterAction mppsa = new MephaPrefferedProviderSorterAction(fdl);
		mppsa.setChecked(CoreHub.globalCfg.get(MephaPrefferedProviderSorterAction.CFG_PREFER_MEPHA,
			false));
		
		List<IAction> actionList = new ArrayList<>();
		actionList.add(mppsa);
		populateSelectorPanel(slp, fdl, actionList);
		slp.addActions(actionList.toArray(new IAction[actionList.size()]));
		
		SimpleWidgetProvider swp =
			new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null);
		
		ILabelDecorator decorator =
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		MedCalDecoratingLabelProvider mcdlp =
			new MedCalDecoratingLabelProvider(new LagerhaltungArtikelstammLabelProvider(),
				decorator);
		
		ViewerConfigurer vc =
			new ViewerConfigurer(fdl, mcdlp, slp, new ViewerConfigurer.DefaultButtonProvider(),
				swp, fdl);
		
		// the dropdown menu on the viewer
		MenuManager menu = new MenuManager();
		menu.add(new Action(
			ch.elexis.core.ui.views.artikel.Messages.ArtikelContextMenu_propertiesAction) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(ch.elexis.core.ui.views.artikel.Messages.ArtikelContextMenu_propertiesTooltip);
			}
			
			@Override
			public void run(){
				StructuredSelection structuredSelection =
					new StructuredSelection(cov.getSelection());
				Object element = structuredSelection.getFirstElement();
				ArtikelstammDetailDialog dd =
					new ArtikelstammDetailDialog(UiDesk.getTopShell(), (IArtikelstammItem) element);
				dd.open();
			}
		});
		
		Image medCalIcon =
			ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.medcalendar",
				"icons/medcal.png");
		MenuManager subMenu =
			new MenuManager("MedKalender Gruppen-Selektion",
				ImageDescriptor.createFromImage(medCalIcon), null) {
				@Override
				public boolean isDynamic(){
					return true;
				}
				
				@Override
				public boolean isVisible(){
					StructuredSelection structuredSelection =
						new StructuredSelection(cov.getSelection());
					Object element = structuredSelection.getFirstElement();
					if (element instanceof ArtikelstammItem) {
						ArtikelstammItem ai = (ArtikelstammItem) element;
						return (ai.getATCCode() != null && ai.getATCCode().length() > 0);
					}
					return false;
				}
			};
		subMenu.add(new MedCalMenuContribution(cov, fdl));
		menu.add(subMenu);
		
		menu.add(tvfa);
		menu.add(new Separator());
		menu.add(new VATMenuContributionItem(cov));
		cv.setContextMenu(menu);
		
		return vc;
	}
}
