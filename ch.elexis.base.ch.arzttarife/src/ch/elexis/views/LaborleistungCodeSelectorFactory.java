/*******************************************************************************
 * Copyright (c) 2005-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.views;

import org.eclipse.swt.SWT;

import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;
import ch.elexis.core.ui.actions.FlatDataLoader;
import ch.elexis.core.ui.actions.PersistentObjectLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.LaborLeistung;

public class LaborleistungCodeSelectorFactory extends CodeSelectorFactory {
	// private AbstractDataLoaderJob dataloader;
	private ViewerConfigurer vc;
	private PersistentObjectLoader fdl;
	
	public LaborleistungCodeSelectorFactory(){}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		fdl = new FlatDataLoader(cv, new Query<LaborLeistung>(LaborLeistung.class));
		fdl.setOrderFields("Text");
		vc =
			new ViewerConfigurer(fdl, new DefaultLabelProvider(), new DefaultControlFieldProvider(
				cv, new String[] {
					"Code", "Text"}), //$NON-NLS-1$ //$NON-NLS-2$
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		return vc;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return LaborLeistung.class;
	}
	
	@Override
	public void dispose(){
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "Analysetarif"; //$NON-NLS-1$
	}
	
	@Override
	public PersistentObject findElement(String code){
		String id = new Query<LaborLeistung>(LaborLeistung.class).findSingle("Code", "=", code);
		if (id != null) {
			return LaborLeistung.load(id);
		}
		return null;
	}
	
}
