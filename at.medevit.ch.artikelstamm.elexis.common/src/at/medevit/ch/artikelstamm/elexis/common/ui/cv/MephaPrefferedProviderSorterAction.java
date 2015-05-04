/*******************************************************************************
 * Copyright (c) 2015 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.icons.ImageSize;
import ch.elexis.core.ui.icons.Images;

public class MephaPrefferedProviderSorterAction extends Action {

	private ArtikelstammFlatDataLoader afdl;

	public static final String CFG_PREFER_MEPHA = "artikelstammPreferMepha";
	
	public MephaPrefferedProviderSorterAction(ArtikelstammFlatDataLoader afdl){
		this.afdl = afdl;
	}
	
	@Override
	public String getText(){
		return "Mepha";
	}
	
	@Override
	public String getToolTipText(){
		return "Mepha Artikel bevorzugen (werden zuoberst angezeigt)";
	}
	
	@Override
	public int getStyle(){
		return Action.AS_CHECK_BOX;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(){
		return Images.lookupImageDescriptor("mepha.png", ImageSize._16x16_DefaultIconSize);
	}
	
	@Override
	public void run(){
		CoreHub.globalCfg.set(CFG_PREFER_MEPHA, isChecked());		
		afdl.setUseMephaPrefferedProviderSorter(isChecked());
	}
}
