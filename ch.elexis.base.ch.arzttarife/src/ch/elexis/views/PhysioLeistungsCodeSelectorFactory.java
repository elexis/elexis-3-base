/*******************************************************************************
 * Copyright (c) 2009, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.SWT;

import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PhysioLeistung;
import ch.rgw.tools.IFilter;



public class PhysioLeistungsCodeSelectorFactory extends CodeSelectorFactory {
	Query<PhysioLeistung> qbe;
	
	public PhysioLeistungsCodeSelectorFactory(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		FieldDescriptor<?>[] fd =
			new FieldDescriptor<?>[] {
				new FieldDescriptor<PhysioLeistung>("Ziffer", "Ziffer", null),
				new FieldDescriptor<PhysioLeistung>("Text", "Text", null),
			};
		qbe = new Query<PhysioLeistung>(PhysioLeistung.class);
		qbe.addPostQueryFilter(new IFilter() {
			
			public boolean select(Object toTest){
				PersistentObject o = (PersistentObject) toTest;
				return !(o.getId().equals("VERSION"));
			}
		});
		PersistentObjectLoader fdl = new FlatDataLoader(cv, qbe);
		SelectorPanelProvider slp = new SelectorPanelProvider(fd, true);
		ViewerConfigurer vc =
			new ViewerConfigurer(fdl, new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc;
		
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return PhysioLeistung.CODESYSTEMNAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return PhysioLeistung.class;
	}
	
	@Override
	public PersistentObject findElement(String code){
		String id = new Query<PhysioLeistung>(PhysioLeistung.class).findSingle("Ziffer", "=", code);
		return PhysioLeistung.load(id);
	}
	
}
