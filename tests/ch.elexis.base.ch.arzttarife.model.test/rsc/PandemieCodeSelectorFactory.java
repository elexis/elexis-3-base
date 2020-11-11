/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.SWT;

import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class PandemieCodeSelectorFactory extends CodeSelectorFactory {
	
	private Query<PandemieLeistung> qbe;
	
	public PandemieCodeSelectorFactory(){
		
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ViewerConfigurer vc =
			new ViewerConfigurer(new PandemicContentProvider(), new DefaultLabelProvider(),
				new PandemicControlFieldProvider(cv),
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
		return "Pandemie";
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return PandemieLeistung.class;
	}
	
	@Override
	public PersistentObject findElement(String code){
		return (PersistentObject) PandemieLeistung.getFromCode(code, new TimeTool());
	}
}
