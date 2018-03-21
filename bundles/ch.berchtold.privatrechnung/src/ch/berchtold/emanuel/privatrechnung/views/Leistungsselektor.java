/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.berchtold.emanuel.privatrechnung.views;

import org.eclipse.swt.SWT;

import ch.berchtold.emanuel.privatrechnung.data.Leistung;
import ch.berchtold.emanuel.privatrechnung.model.LeistungenLoader;
import ch.elexis.core.ui.actions.TreeDataLoader;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

/**
 * This is the Composite that lets the user select codes and drag them into the billing-field. It
 * will be lined up next to the CodeSelectorFactories of all other Billing-Plugins
 * 
 * @author Gerry
 * 
 */
public class Leistungsselektor extends CodeSelectorFactory {
	
	/**
	 * Here we create the populator for the CodeSelector. We must provide a viewer widget, a content
	 * provider, a label provider, a ControlFieldProvider and a ButtonProvider Again, we simply use
	 * existing classes to keep things easy.
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		TreeDataLoader tdl =
			new LeistungenLoader(this, cv, new Query<Leistung>(Leistung.class), "parent");
		FieldDescriptor<Leistung> fdName =
			new FieldDescriptor<Leistung>("Leistungen", Leistung.FIELD_NAME, Typ.STRING, null);
		ViewerConfigurer vc =
			new ViewerConfigurer(tdl, new ViewerConfigurer.TreeLabelProvider(),
				new SelectorPanelProvider(new FieldDescriptor<?>[] {
					fdName
				}, true), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.VIRTUAL, null));
		return vc;
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return Leistung.CODESYSTEM_NAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return Leistung.class;
	}
}
