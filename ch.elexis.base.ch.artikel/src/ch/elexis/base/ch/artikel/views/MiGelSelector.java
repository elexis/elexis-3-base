/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
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
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.base.ch.artikel.model.MigelLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class MiGelSelector extends CodeSelectorFactory {
	/*
	 * AbstractDataLoaderJob dataloader;
	 * 
	 * public MiGelSelector() { dataloader=(AbstractDataLoaderJob)
	 * JobPool.getJobPool().getJob("MiGeL"); if(dataloader==null){ dataloader=new
	 * ListLoader("MiGeL",new Query<MiGelArtikel>(MiGelArtikel.class),new String[]{"SubID","Name"});
	 * JobPool.getJobPool().addJob(dataloader); JobPool.getJobPool().activate("MiGeL",Job.SHORT); }
	 * }
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		new ArtikelContextMenu(
			(MiGelArtikel) new ArtikelFactory().createTemplate(MiGelArtikel.class), cv);
		return new ViewerConfigurer(
		// new LazyContentProvider(cv,dataloader,null),
			new MigelLoader(cv), new DefaultLabelProvider(), new DefaultControlFieldProvider(cv,
				new String[] {
					"SubID=Code", "Name" //$NON-NLS-1$ //$NON-NLS-2$
				}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
	}
	
	@Override
	public Class getElementClass(){
		return MiGelArtikel.class;
	}
	
	@Override
	public void dispose(){
		// TODO Automatisch erstellter Methoden-Stub
		
	}
	
	@Override
	public String getCodeSystemName(){
		return MiGelArtikel.MIGEL_NAME; //$NON-NLS-1$
	}
	
}
