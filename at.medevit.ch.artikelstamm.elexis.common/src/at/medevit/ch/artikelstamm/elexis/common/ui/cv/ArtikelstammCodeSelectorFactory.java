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
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import at.medevit.atc_codes.ATCCodeLanguageConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.elexis.common.preference.PreferenceConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.ArtikelstammDetailDialog;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.ATCArtikelstammDecoratingLabelProvider;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.LagerhaltungArtikelstammLabelProvider;
import at.medevit.ch.artikelstamm.ui.IArtikelstammItem;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.Messages;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ArtikelstammCodeSelectorFactory extends CodeSelectorFactory {
	
	private SelectorPanelProvider slp;
	private int eventType = SWT.KeyDown;
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		final CommonViewer cov = cv;
		
		FieldDescriptor<?>[] fields =
			{
				new FieldDescriptor<ArtikelstammItem>("Bezeichnung", ArtikelstammItem.FLD_DSCR,
					Typ.STRING, null),
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
		ArtikelstammFlatDataLoader fdl = new ArtikelstammFlatDataLoader(cv, qbe, slp);
		
		SupportedATCFilteringAction safa = new SupportedATCFilteringAction(fdl);
		
		List<IAction> actionList = new ArrayList<>();
		actionList.add(safa);
		populateSelectorPanel(slp, fdl, actionList);
		slp.addActions(actionList.toArray(new IAction[actionList.size()]));
		
		SimpleWidgetProvider swp =
			new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null);
		
		ILabelDecorator decorator =
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		String atcLang =
			CoreHub.globalCfg.get(PreferenceConstants.PREF_ATC_CODE_LANGUAGE,
				ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN);
		ATCArtikelstammDecoratingLabelProvider adlp =
			new ATCArtikelstammDecoratingLabelProvider(new LagerhaltungArtikelstammLabelProvider(),
				decorator, atcLang);
		
		ViewerConfigurer vc = new ViewerConfigurer(fdl, adlp,
		// new MedINDEXArticleControlFieldProvider(cv),
			slp, new ViewerConfigurer.DefaultButtonProvider(), swp, fdl);
		
		MenuManager menu = new MenuManager();
		menu.add(new Action(ch.elexis.core.ui.views.artikel.Messages.ArtikelContextMenu_propertiesAction) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(ch.elexis.core.ui.views.artikel.Messages.ArtikelContextMenu_propertiesTooltip);
			}
			@Override
			public void run(){
				StructuredSelection structuredSelection = new StructuredSelection(cov.getSelection());
				Object element = structuredSelection.getFirstElement();
				ArtikelstammDetailDialog dd = new ArtikelstammDetailDialog(UiDesk.getTopShell(), (IArtikelstammItem) element);
				dd.open();
			}
		});
		menu.add(new Separator());
		menu.add(new VATMenuContributionItem(cov));
		cv.setContextMenu(menu);
		
		return vc;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return ArtikelstammItem.class;
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
	public void populateSelectorPanel(SelectorPanelProvider slp, FlatDataLoader fdl,
		List<IAction> actionList){}
	
}
