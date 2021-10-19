/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.ModelServiceHolder;
import at.medevit.ch.artikelstamm.elexis.common.ui.ArtikelstammDetailDialog;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.ATCArtikelstammDecoratingLabelProvider;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.LagerhaltungArtikelstammLabelProvider;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.ICodeSelectorTarget;
import ch.elexis.core.ui.actions.ToggleVerrechenbarFavoriteAction;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.selectors.ActiveControl;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldListener;
import ch.elexis.core.ui.views.KonsDetailView;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class ArtikelstammCodeSelectorFactory extends CodeSelectorFactory {
	
	private SelectorPanelProvider slp;
	private int eventType = SWT.KeyDown;
	private ToggleVerrechenbarFavoriteAction tvfa = new ToggleVerrechenbarFavoriteAction();
	
	private static final String DISP_NAME = "Artikel oder Wirkstoff";
	
	private ArtikelstammCommonViewerContentProvider commonViewContentProvider;
	
	private ISelectionChangedListener selChange = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event){
			TableViewer tv = (TableViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();
			tvfa.updateSelection(ss.isEmpty() ? null : ss.getFirstElement());
			if (!ss.isEmpty() && ss.getFirstElement() instanceof IArtikelstammItem) {
				IArtikelstammItem selected = (IArtikelstammItem) ss.getFirstElement();
				ContextServiceHolder.get().getRootContext()
					.setNamed("at.medevit.ch.artikelstamm.elexis.common.ui.selection", selected);
			} else {
				ContextServiceHolder.get().getRootContext()
					.setNamed("at.medevit.ch.artikelstamm.elexis.common.ui.selection", null);
			}
		}
	};
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		final CommonViewer cov = cv;
		cov.setSelectionChangedListener(selChange);
		
		FieldDescriptor<?>[] fields = {
			new FieldDescriptor<IArtikelstammItem>(DISP_NAME, "ldscr", Typ.STRING,
				null).ignoreCase(false).valueToLower(true)
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
		// delay the change event of the selector panel
		slp = new SelectorPanelProvider(fields, true);
		slp.setChangeDelay(750);
		slp.addChangeListener(new AControlFieldListener(slp));
		
		commonViewContentProvider =
			new ArtikelstammCommonViewerContentProvider(cv, slp);
		
		List<IAction> actionList = new ArrayList<>();
		populateSelectorPanel(slp, commonViewContentProvider, actionList);
		slp.addActions(actionList.toArray(new IAction[actionList.size()]));
		
		SimpleWidgetProvider swp =
			new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null);
		
		ILabelDecorator decorator =
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		String atcLang = ConfigServiceHolder.get().get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
			ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		ATCArtikelstammDecoratingLabelProvider adlp = new ATCArtikelstammDecoratingLabelProvider(
			new LagerhaltungArtikelstammLabelProvider(), decorator, atcLang);
		
		ViewerConfigurer vc = new ViewerConfigurer(commonViewContentProvider, adlp,
			slp, new ViewerConfigurer.DefaultButtonProvider(), swp);
		
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
		
		//		menu.add(new AddVerrechenbarContributionItem(cov));
		
		MenuManager subMenu =
			new MenuManager("ATC Gruppen-Selektion",
				Images.IMG_CATEGORY_GROUP.getImageDescriptor(), null) {
				@Override
				public boolean isDynamic(){
					return true;
				}
				
				@Override
				public boolean isVisible(){
					StructuredSelection structuredSelection =
						new StructuredSelection(cov.getSelection());
					Object element = structuredSelection.getFirstElement();
					if (element instanceof IArtikelstammItem) {
						IArtikelstammItem ai = (IArtikelstammItem) element;
						return (ai.getAtcCode() != null && ai.getAtcCode().length() > 0);
					}
					return false;
				}
			};
		subMenu.add(new ATCMenuContributionItem(cov, commonViewContentProvider));
		menu.add(subMenu);
		
		menu.add(tvfa);
		menu.add(new Separator());
		menu.add(new VATMenuContributionItem(cov));
		cv.setContextMenu(menu);
		vc.setContentType(ContentType.GENERICOBJECT);
		return vc;
	}
	
	@Override
	public IDoubleClickListener getDoubleClickListener(){
		return new ArtikelstammDoubleClickListener();
	}
	
	@Override
	public Class<?> getElementClass(){
		return IArtikelstammItem.class;
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getCodeSystemName(){
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}
	
	/**
	 * Overwrite to add actions to the selector panel
	 * 
	 * @param actionList
	 * 
	 * @param slp2
	 */
	public void populateSelectorPanel(SelectorPanelProvider slp,
		ArtikelstammCommonViewerContentProvider commonViewerContentProvider,
		List<IAction> actionList){
		
		MephaPrefferedProviderSorterAction mppsa =
			new MephaPrefferedProviderSorterAction(commonViewerContentProvider);
		mppsa.setChecked(ConfigServiceHolder.get()
			.get(MephaPrefferedProviderSorterAction.CFG_PREFER_MEPHA, false));
		actionList.add(mppsa);
		actionList.add(new SupportedATCFilteringAction(commonViewerContentProvider));
		actionList.add(new BlackboxViewerFilterAction(commonViewerContentProvider, slp));
	}
	
	private class ArtikelstammDoubleClickListener implements IDoubleClickListener {
		private String filterValueStore;
		
		@Override
		public void doubleClick(DoubleClickEvent event){
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (selection.getFirstElement() == null)
				return;
			if (selection.getFirstElement() instanceof ATCCode) {
				filterValueStore = slp.getValues()[0];
				slp.clearValues();
				ATCCode a = (ATCCode) selection.getFirstElement();
				
				commonViewContentProvider.removeAllQueryFilterByType(AtcQueryFilter.class);
				AtcQueryFilter queryFilter = new AtcQueryFilter();
				queryFilter.setFilterValue(a.atcCode);
				commonViewContentProvider.addQueryFilter(queryFilter);
			} else if (selection.getFirstElement() instanceof ATCFilterInfoListElement) {
				slp.clearValues();
				ActiveControl ac = slp.getPanel().getControls().get(0);
				ac.setText((filterValueStore != null) ? filterValueStore : "");
				
				commonViewContentProvider.removeAllQueryFilterByType(AtcQueryFilter.class);
			} else if (selection.getFirstElement() instanceof IArtikelstammItem) {
				ICodeSelectorTarget target =
					CodeSelectorHandler.getInstance().getCodeSelectorTarget();
				if (target != null) {
					Object obj = selection.getFirstElement();
					target.codeSelected(obj);
				}
			}
		}
	}
	
	private class AControlFieldListener implements ControlFieldListener {
		private SelectorPanelProvider slp;

		public AControlFieldListener(SelectorPanelProvider slp){
			this.slp = slp;
		}

		@Override
		public void changed(HashMap<String, String> values){
			String val = values.get(DISP_NAME);
			if (val != null && val.length() == 13 && StringUtils.isNumeric(val)) {	
				Optional<ICodeElement> result =
					((ICodeElementServiceContribution) ModelServiceHolder.get()).loadFromCode(val,
						Collections.emptyMap());
				if (result.isPresent()) {
					KonsDetailView detailView = getKonsDetailView();
					Optional<IEncounter> encounter =
						ContextServiceHolder.get().getTyped(IEncounter.class);
					// TODO use IBillingService here!?
					//					if (detailView != null && encounter.isPresent()) {
					//						detailView.addToVerechnung(result.get(0));
					//						slp.clearValues();
					//					} else {
					//						ScannerEvents.beep();
					//					}
				}
			}
		}
		
		private KonsDetailView getKonsDetailView(){
			IViewReference[] viewReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getViewReferences();
			for (IViewReference viewRef : viewReferences) {
				if (KonsDetailView.ID.equals(viewRef.getId())) {
					return (KonsDetailView) viewRef.getPart(false);
				}
			}
			return null;
		}
		
		@Override
		public void reorder(String field){}
		
		@Override
		public void selected(){}
	}
	
}
