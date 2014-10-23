/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
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

import ch.elexis.core.ui.icons.Images;

public class SupportedATCFilteringAction extends Action {
	
	private ArtikelstammFlatDataLoader afdl;
	
	public SupportedATCFilteringAction(ArtikelstammFlatDataLoader afdl){
		this.afdl = afdl;
	}
	
	@Override
	public String getText(){
		return "ATC Filter";
	}
	
	@Override
	public int getStyle(){
		return Action.AS_CHECK_BOX;
	}
	
	@Override
	public String getToolTipText(){
		return "ATC basierten Filter de-/aktivieren";
	}
	
	@Override
	public String getDescription(){
		return "De-/aktiviert die Einbeziehung von ATC namen in die Suche";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(){
		return Images.IMG_CATEGORY_GROUP.getImageDescriptor();
	}
	
	@Override
	public void run(){
		afdl.setUseAtcQueryFilter(isChecked());
	}
}
