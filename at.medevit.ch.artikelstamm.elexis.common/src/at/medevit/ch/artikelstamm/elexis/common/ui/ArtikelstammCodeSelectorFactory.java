/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.ArtikelstammDecoratingLabelProvider;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.LagerhaltungArtikelstammLabelProvider;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader.QueryFilter;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class ArtikelstammCodeSelectorFactory extends CodeSelectorFactory {
	
	private SelectorPanelProvider slp;
	private CommonViewer cv;
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		FlatDataLoader fdl = new FlatDataLoader(cv, qbe);
		fdl.setOrderFields(ArtikelstammItem.FLD_DSCR);
		fdl.addQueryFilter(new NoVersionQueryFilter());
		fdl.applyQueryFilters();
		
		FieldDescriptor<?>[] fields =
			{
				new FieldDescriptor<ArtikelstammItem>("Bezeichnung", ArtikelstammItem.FLD_DSCR,
					Typ.STRING, null)
			};
		slp = new SelectorPanelProvider(fields, true);
		
		populateSelectorPanel(slp, fdl);
		
		SimpleWidgetProvider swp =
			new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null);
		
		ILabelDecorator decorator =
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		ArtikelstammDecoratingLabelProvider adlp =
			new ArtikelstammDecoratingLabelProvider(new LagerhaltungArtikelstammLabelProvider(),
				decorator);
		
		ViewerConfigurer vc = new ViewerConfigurer(fdl, adlp,
		// new MedINDEXArticleControlFieldProvider(cv),
			slp, new ViewerConfigurer.DefaultButtonProvider(), swp);
		
// MenuManager menuManager = new MenuManager();
// menuManager.add(new CSFMedINDEXArticleMenuContribution(cv));
// cv.setContextMenu(menuManager);
		
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
	 * This filter skips all entries with ID "VERSION"
	 */
	private class NoVersionQueryFilter implements QueryFilter {
		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.add("ID", Query.NOT_EQUAL, "VERSION");
		}
	}
	
	/**
	 * Overwrite to add actions to the selector panel
	 * 
	 * @param slp2
	 */
	public void populateSelectorPanel(SelectorPanelProvider slp, FlatDataLoader fdl){}
	
}
