/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.privatrechnung.views;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import ch.elexis.actions.JobPool;
import ch.elexis.actions.LazyTreeLoader;
import ch.elexis.data.Query;
import ch.elexis.privatrechnung.data.Leistung;
import ch.elexis.util.viewers.CommonViewer;
import ch.elexis.util.viewers.DefaultControlFieldProvider;
import ch.elexis.util.viewers.SimpleWidgetProvider;
import ch.elexis.util.viewers.TreeContentProvider;
import ch.elexis.util.viewers.ViewerConfigurer;
import ch.elexis.views.codesystems.CodeSelectorFactory;

/**
 * This is the Composite that lets the user select codes and drag them into the billing-field. It
 * will be lined up next to the CodeSelectorFactories of all other Billing-Plugins
 * 
 * @author Gerry
 * 
 */
public class Leistungsselektor extends CodeSelectorFactory {
	private LazyTreeLoader<Leistung> dataloader;
	private static final String LOADER_NAME = "Privatcodes";
	
	/**
	 * On Creation we initiate a dataloader. We can simply use the existing LazyXXXLoader framework.
	 */
	@SuppressWarnings("unchecked")
	public Leistungsselektor(){
		dataloader = (LazyTreeLoader<Leistung>) JobPool.getJobPool().getJob(LOADER_NAME); //$NON-NLS-1$
		
		if (dataloader == null) {
			dataloader =
				new LazyTreeLoader<Leistung>(LOADER_NAME, new Query<Leistung>(Leistung.class),
					"parent", new String[] { "Kuerzel", "Name"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			dataloader.setParentField("Kuerzel");
			JobPool.getJobPool().addJob(dataloader);
		}
		JobPool.getJobPool().activate(LOADER_NAME, Job.SHORT); //$NON-NLS-1$
	}
	
	/**
	 * Here we create the populator for the CodeSelector. We must provide a viewer widget, a content
	 * provider, a label provider, a ControlFieldProvider and a ButtonProvider Again, we simply use
	 * existing classes to keep things easy.
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ViewerConfigurer vc =
			new ViewerConfigurer(new TreeContentProvider(cv, dataloader),
				new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(cv,
					new String[] {
						"Kuerzel", "Name"}), //$NON-NLS-1$
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
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
	public Class getElementClass(){
		return Leistung.class;
	}
	
}
