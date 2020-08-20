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

import org.eclipse.swt.SWT;

import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.privatrechnung.model.IPrivatLeistung;

/**
 * This is the Composite that lets the user select codes and drag them into the billing-field. It
 * will be lined up next to the CodeSelectorFactories of all other Billing-Plugins
 * 
 * @author Gerry
 * 
 */
public class Leistungsselektor extends CodeSelectorFactory {
	
	private CommonViewer cv;
	
	/**
	 * On Creation we initiate a dataloader. We can simply use the existing LazyXXXLoader framework.
	 */
	@SuppressWarnings("unchecked")
	public Leistungsselektor(){}
	
	/**
	 * Here we create the populator for the CodeSelector. We must provide a viewer widget, a content
	 * provider, a label provider, a ControlFieldProvider and a ButtonProvider Again, we simply use
	 * existing classes to keep things easy.
	 */
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		ViewerConfigurer vc =
			new ViewerConfigurer(new PrivatLeistungContentProvider(cv),
				new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(cv,
					new String[] {
						"Kuerzel", "Name"}), //$NON-NLS-1$
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}
	
	@Override
	public void dispose(){
		cv.dispose();
	}
	
	@Override
	public String getCodeSystemName(){
		return "Privat";
	}
	
	@Override
	public Class<?> getElementClass(){
		return IPrivatLeistung.class;
	}
}
