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
import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.base.ch.artikel.model.MedicalLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.artikel.ArtikelLabelProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.Artikel;

public class MedicalSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public MedicalSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("Medicals"); if(dataloader==null){ dataloader=new
	 * ListLoader("Medicals",new Query<Medical>(Medical.class),new String[]{"Name"});
	 * JobPool.getJobPool().addJob(dataloader); }
	 * JobPool.getJobPool().activate("Medicals",Job.SHORT); }
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ArtikelFactory af = new ArtikelFactory();
		Artikel artikelTemplate = (Artikel) af.createTemplate(Medical.class);
		new ArtikelContextMenu((Medical) artikelTemplate, cv);
		ViewerConfigurer vc =
			new ViewerConfigurer(
			// new LazyContentProvider(cv,dataloader,null),
				new MedicalLoader(cv), new ArtikelLabelProvider(), new MedicalControlFieldProvider(
					cv, new String[] {
						"Name"
					}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
		return vc;
	}
	
	@Override
	public Class getElementClass(){
		return Medical.class;
	}
	
	@Override
	public void dispose(){
		// TODO Automatisch erstellter Methoden-Stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return "Medicals";
	}
	
}
