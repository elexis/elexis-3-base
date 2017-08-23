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

import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.ComplementaryLeistung;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;

public class ComplementaryCodeSelectorFactory extends CodeSelectorFactory {
	Query<ComplementaryLeistung> qbe;
	
	public ComplementaryCodeSelectorFactory(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		FieldDescriptor<?>[] fieldDescriptors =
			new FieldDescriptor<?>[] {
				new FieldDescriptor<ComplementaryLeistung>("Code", ComplementaryLeistung.FLD_CODE,
					null),
				new FieldDescriptor<ComplementaryLeistung>("Text",
					ComplementaryLeistung.FLD_CODE_TEXT, null),
			};
		qbe = new Query<ComplementaryLeistung>(ComplementaryLeistung.class, null, null,
			ComplementaryLeistung.TABLENAME,
			new String[] {
				ComplementaryLeistung.FLD_VALID_FROM, ComplementaryLeistung.FLD_VALID_TO
			});
		qbe.addPostQueryFilter(new IFilter() {
			private TimeTool now = new TimeTool();
			
			public boolean select(Object toTest){
				ComplementaryLeistung complementary = (ComplementaryLeistung) toTest;
				if (complementary.getId().equals("VERSION")) {
					return false;
				}
				if (!complementary.isValid(now)) {
					return false;
				}
				return true;
			}
		});
		PersistentObjectLoader fdl = new FlatDataLoader(cv, qbe);
		SelectorPanelProvider slp = new SelectorPanelProvider(fieldDescriptors, true);
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
		return "Komplementärmedizin";
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return ComplementaryLeistung.class;
	}
	
	@Override
	public PersistentObject findElement(String code){
		return (PersistentObject) ComplementaryLeistung.getFromCode(code, new TimeTool());
	}
	
}
