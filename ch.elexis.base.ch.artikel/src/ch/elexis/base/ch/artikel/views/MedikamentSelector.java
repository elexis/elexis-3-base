/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.artikel.views;

import org.eclipse.swt.SWT;

import ch.elexis.artikel_ch.data.ArtikelFactory;
import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.base.ch.artikel.model.MedikamentLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.artikel.ArtikelLabelProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class MedikamentSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public MedikamentSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("Medikamente"); if(dataloader==null){ dataloader=new
	 * ListLoader("Medikamente",new Query<Medikament>(Medikament.class),new String[]{"Name"});
	 * JobPool.getJobPool().addJob(dataloader); }
	 * JobPool.getJobPool().activate("Medikamente",Job.SHORT); }
	 */
	// MedikamentLoader ml;
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		new ArtikelContextMenu((Medikament) new ArtikelFactory().createTemplate(Medikament.class),
			cv);
		return new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
			new MedikamentLoader(cv), new ArtikelLabelProvider(),
			new MedikamentControlFieldProvider(cv, new String[] {
				"Name"
			}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return Medikament.class;
	}
	
	@Override
	public void dispose(){
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "Medikamente";
	}
	
}
